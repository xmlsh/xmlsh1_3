/**
 * $Id: xsplit.java 364 2010-01-07 20:43:13Z daldei $
 * $Date: 2010-01-07 15:43:13 -0500 (Thu, 07 Jan 2010) $
 *
 */

package org.xmlsh.commands.internal;

/**
 * Command: xsplit
 * Usage: xsplit [options] [input]
 * Options:
 * 		-p	prefix
 * 		-s	suffix
 * 		-e	extension
 * 		-c	num
 * 			Number of child XML elements to output (def 1)
 * 		-n  
 * 			Do not wrap in root element (requires -c=1)
 * 		-w  elem
 * 			Wrap in element instead of root element
 * 
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.xmlsh.core.InputPort;
import org.xmlsh.core.Options;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.core.Options.OptionValue;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.util.Util;

public class xsplit extends XCommand {

	
	private 	XMLOutputFactory mOutputFactory = XMLOutputFactory.newInstance();
	private		XMLEventFactory  mEventFactory  = XMLEventFactory.newInstance();

	/*
	 * Runtime data
	 */
	
	private		XMLEvent		mRootNode = null;
	private		String 			mPrefix = "x";
	private		int				mSeq 	= 0;
	private		String			mSuffix = "";
	private		String			mExt 	= ".xml";
	private		File 			mOutputDir = null ;
	
    private		boolean			mNoRoot = false ;
	private		int				mNumChildren = 1;
	private		boolean			mNoDTD = false ;
	private		boolean			mNoPI  = false ;
	
	private		List<XMLEvent>	mHeader = new ArrayList<XMLEvent>();


	public int run( List<XValue> args)	throws Exception
	{


		Options opts = new Options( "c=children:,w=wrap:,n,p=prefix:,e=ext:,s=suffix:,n=nowrap,o=output:,nopi,nodtd" ,SerializeOpts.getOptionDefs() );
		opts.parse(args);
		
		// root node
		OptionValue ow = opts.getOpt("w");
		XValue wrapper = null;
		if( ow != null ){
			wrapper = ow.getValue();
			mRootNode = mEventFactory.createStartElement( new QName(null,wrapper.toString()), null, null);
		}
		
		
		
		mNumChildren = Util.parseInt(opts.getOptString("c","1"),1);
		
		mExt 	 = opts.getOptString("e",mExt);
		mSuffix  = opts.getOptString("s",mSuffix);
		mPrefix  = opts.getOptString("p",mPrefix);
		if( opts.hasOpt("o"))
			mOutputDir = getFile(opts.getOptValue("o"));
		mNoDTD = opts.hasOpt("nodtd");
		mNoPI  = opts.hasOpt("nopi");
		
		
		
		/*
		 * If not adding a root then must add only 1 child 
		 */
		if( opts.hasOpt("n") ){ 
			mNumChildren = 1;
			mNoRoot = true ;
		}

		
		
		List<XValue> xvargs = opts.getRemainingArgs();
		if( xvargs.size() > 1 ){
			usage();
			return 1;
		}
		
		InputPort in = 
			xvargs.size() == 1 ? 
					getInput(xvargs.get(0)): 
					getStdin();
			
		
		SerializeOpts serializeOpts = getSerializeOpts(opts);
		InputStream is = in.asInputStream(serializeOpts) ;
		split(in.getSystemId() , is );
		
		is.close();
		in.close();
		
		
		
		
		
		return 0;


	}






	private void split(String systemId , InputStream is) throws XMLStreamException, IOException {
	

		
		
		XMLInputFactory inputFactory=XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.valueOf(true));
		XMLEventReader  xmlreader  =inputFactory.createXMLEventReader(systemId, is);
		
		
		/* 
		 * Read up to root elem collecting events to repeat on each file
		 * 
		 */
		
		List<Namespace>  ns = null;
		
		while( xmlreader.hasNext()   ){
			XMLEvent e = xmlreader.nextEvent();
			if( e.getEventType() != XMLStreamConstants.START_ELEMENT ){
				if( mNoDTD && e.getEventType() == XMLStreamConstants.DTD )
					continue;
				if( mNoPI && e.getEventType() == XMLStreamConstants.PROCESSING_INSTRUCTION )
					continue;
				
				
				mHeader.add(e);
				continue;
			}
			
			
			/* 
			 * If no root then dont add elements to root, but do collect namespaces
			 */
			if( mNoRoot ){
				
				
				StartElement se = e.asStartElement();
				
				Iterator<?> nsi = se.getNamespaces();
				ns = new ArrayList<Namespace>();
				while( nsi.hasNext())
					ns.add((Namespace) nsi.next());

			}
			
			if( ! mNoRoot ){
				// Found document root
				if( mRootNode == null )
					mHeader.add(e);
				else
					mHeader.add( mRootNode );

			}
				
			break;
			
			
		}
		
		/*
		 * For each element write it to a new file
		 */
		
		while( xmlreader.hasNext() ){
			XMLEvent e = xmlreader.nextEvent();
			
			int type = e.getEventType() ;
			
			if( type == XMLStreamConstants.START_ELEMENT ){
				write( xmlreader , e ,ns );
				
				
			} else
			if( type == XMLStreamConstants.END_ELEMENT ||
				type == XMLStreamConstants.END_DOCUMENT 
				
			)
			{
				// ignore
			}
			
			else
			{
				if( type == XMLStreamConstants.CHARACTERS && e.asCharacters().isWhiteSpace())
					continue ;
				printErr("Skipping XML node: " + e.toString());
			}
		}
		
		xmlreader.close();
				
			
		
	}


	/* 
	 * Write out a single element and all its children to the next file
	 * 
	 */



	private void write(XMLEventReader xmlreader, XMLEvent first, List<Namespace> ns) throws XMLStreamException, IOException {
		File fout = nextFile();
		OutputStream fo = new FileOutputStream(fout); // need to close seperately
		XMLEventWriter w = mOutputFactory.createXMLEventWriter( fo );
		
		
		/*
		 * Write the common header elements
		 */
		for( XMLEvent e : mHeader )
			w.add(e);
		
		
		
		w.add(first);
		/*
		 * Add namespaces if needed
		 */
		
		if( ns != null )
			for( Namespace n : ns )
				w.add(n);
		
		int depth = 0;
		int nchild = 0;
		
		while( xmlreader.hasNext() ){
			XMLEvent e = xmlreader.nextEvent();
			w.add(e);

			if( e.getEventType() == XMLStreamConstants.START_ELEMENT )
				depth++;
			else
			if( e.getEventType() == XMLStreamConstants.END_ELEMENT ){
				if( depth-- <= 0 ){
					if( ++nchild == mNumChildren )
						break;
				}

			}
			
		}
		
		/*
		 * End with end element and end document
		 */
		// w.add( mEventFactory.createEndElement( first.asStartElement().getName(), null));
		w.add( mEventFactory.createEndDocument());
		w.close();
		fo.close();
		
		
	}





	private File nextFile() throws IOException {
		File f = getEnv().getShell().getFile( mOutputDir , mPrefix + mSeq++ + mSuffix + mExt );
		return f;
	}





	private int usage( ) {
		printErr("Usage: xsplit [-w wrap] [-c children] [-n]  [-p prefix] [file]");
		return 1;
	}


}

//
// 
//Copyright (C) 2008,2009 , David A. Lee.
// 
//The contents of this file are subject to the "Simplified BSD License" (the "License");
//you may not use this file except in compliance with the License. You may obtain a copy of the
//License at http://www.opensource.org/licenses/bsd-license.php 
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied.
//See the License for the specific language governing rights and limitations under the License.
//
//The Original Code is: all this file.
//
//The Initial Developer of the Original Code is David A. Lee
//
//Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
//
//Contributor(s): none.
//