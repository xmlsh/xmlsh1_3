/**
 * $Id: xls.java 357 2010-01-01 15:56:38Z daldei $
 * $Date: 2010-01-01 10:56:38 -0500 (Fri, 01 Jan 2010) $
 *
 */

package org.xmlsh.commands.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.types.XFile;
import org.xmlsh.util.Util;

public class xls extends XCommand {

	
	private boolean opt_a = false ;
	private boolean opt_R = false ;
	private boolean opt_l = false ;
	public int run(  List<XValue> args  )	throws Exception
	{
		Options opts = new Options("a=all,l=long,R=recurse", SerializeOpts.getOptionDefs() );
		opts.parse(args);
		args = opts.getRemainingArgs();
		
		

	      
		OutputPort stdout = getStdout();
		SerializeOpts serializeOpts = getSerializeOpts(opts);
		XMLStreamWriter writer = stdout.asXMLStreamWriter(serializeOpts);
		writer.writeStartDocument();
		
		String sDocRoot = "dir";
		writer.writeStartElement(sDocRoot);
		
		
		if( args == null )
			args = new ArrayList<XValue>();
		if( args.size() == 0 )
			args.add(new XValue(""));
		
		opt_l = opts.hasOpt("l");
		opt_a = opts.hasOpt("a");
		opt_R = opts.hasOpt("R");
		int ret = 0;
		for( XValue arg : args ){
			
			// Must go to Shell API to get raw files
			String sArg = arg.toString();
			File dir = getEnv().getShell().getFile(sArg);
			if( ! dir.exists() ){
				this.printErr("ls: cannot access " + sArg + " : No such file or directory" );
				ret++;
				continue;
			}
			list(writer, dir , true);
		}
		writer.writeEndElement();
		writer.writeEndDocument();
		stdout.writeSequenceTerminator(serializeOpts);
		
		
		return ret;
	}

	private void list(XMLStreamWriter writer, File dir, boolean top ) throws XMLStreamException {
		if( !dir.isDirectory() ){

			new XFile(dir).serialize(writer, opt_l,true);
		} else {
			
			if( ! top )
				new XFile(dir).serialize(writer, opt_l,false);
				
			
			if( top || opt_R ){
				File [] files =  dir.listFiles();
				
				
				Util.sortFiles(files);
				
				for( File f : files ){
					
					if( ! opt_a && f.getName().startsWith("."))
						continue;
					
					list( writer  , f , false  );
	
				}
			}
			if( ! top )
				writer.writeEndElement();
		}
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