/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.commands.internal;

import java.util.List;

import javanet.staxutils.ContentHandlerToXMLStreamWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.InputSource;
import org.xmlsh.core.InputPort;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.shell.SerializeOpts;

public class xinclude extends XCommand {

	
	
	
	

	@Override
	public int run(List<XValue> args) throws Exception {
		
		

		Options opts = new Options( SerializeOpts.getOptionDefs() );
		opts.parse(args);
		args = opts.getRemainingArgs();
		
		
		InputPort stdin = null;
		if( args.size() > 0 )
			stdin = getInput( args.get(0));
		else
			stdin = getStdin();
		if( stdin == null )
			throw new InvalidArgumentException("Cannot open input");
		try {
			
			SerializeOpts sopts = getSerializeOpts(opts);
			
			

			SAXParserFactory f = SAXParserFactory.newInstance();
			
			f.setValidating(false);
			f.setNamespaceAware(true);
			f.setXIncludeAware(true);
			
	
			SAXParser parser = f.newSAXParser();
			
			OutputPort stdout = getStdout();
			XMLStreamWriter w = stdout.asXMLStreamWriter(sopts);
			
			ContentHandlerToXMLStreamWriter	handler = new ContentHandlerToXMLStreamWriter(w);
			
			
			InputSource	source = stdin.asInputSource(sopts);

			
			parser.parse( source ,  handler );
			
			
			
		
	
			stdout.writeSequenceTerminator(sopts);
		} 
		finally {
			
			stdin.close();
		}
		return 0;
		
		
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