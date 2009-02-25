/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.sh.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.grammar.ParseException;
import org.xmlsh.sh.shell.Shell;
import org.xmlsh.sh.shell.ShellThread;
import org.xmlsh.util.NullInputStream;
import org.xmlsh.util.PipedStream;
import org.xmlsh.util.Util;
import org.xmlsh.util.XMLException;

/*
 * A Value that evaulates to a "cmd_word" which is either a simple string,
 * or a subprocess expression 
 * 
 */
public class CommandWord extends Word {
	String		mType;	// $( $(< $<( $<(< 
	Command		mCommand;
	
	public CommandWord( String type , Command c){
		mType = type;
		mCommand =  c;
	}
	
	public void print( PrintWriter out )
	{
		out.print( mType );
		mCommand.print(out,false);
		out.print(")");
	}
	

	private String expandSubproc(Shell shell , Command c ) throws InvalidArgumentException, IOException 
	{
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		shell = shell.clone();
		try {
		
			shell.getEnv().setStdout( out );
			shell.getEnv().setStdin( new NullInputStream(),"" );
			shell.exec(c);
			
			
			return out.toString().trim();
	
			
			
		} 
		finally {
			shell.close();

			
		}

		
	}


	

	/*
	 * Parse an XML command expression and build the XdmValue by running 
	 * a sub shell and parsing the XML through a pipe 
	 * NOTE: Not entirely sure this is better then simply outputting the XML as a string
	 * then parsing the result text.
	 */
	
	

	private XdmValue parseXCmd(Shell shell , Command cmd) throws IOException, CoreException
	{

		
	
		ShellThread sht = null;


		try {

			PipedStream pipe = new PipedStream();

			shell = shell.clone();
			shell.getEnv().setStdout( pipe.getOutput() );
			
			
			shell.getEnv().setStdin( new NullInputStream() ,"");
			

			
			sht = new ShellThread( shell , null ,  cmd );


			 sht.start();
			
			 DocumentBuilder builder = Shell.getProcessor().newDocumentBuilder();
			 XdmNode node = builder.build(new StreamSource(pipe.getInput()));
			 
			if( sht != null )
				sht.join();
				
			return node ;
			 
		} catch ( Exception e )
		{
			throw new XMLException("Exception parsing XML command: " + cmd , e );


			
		} 
		
		
		
	}
		

	public XValue expand(Shell shell,boolean bExpandWild , boolean bExpandWords ) throws IOException, CoreException {
		
		
		if( mType.equals("$(")){
			String 	value = expandSubproc( shell , mCommand);
			return new XValue(value);
		} else 
		if( mType.equals("$<(")){
			
			XdmValue v = parseXCmd( shell , mCommand );
			return new XValue(v);
		}

		else 
			return null;
		
	}

	
	public boolean isEmpty() {
		return mType == null || mCommand == null ;
	}
	
	public String toString()
	{
		return mType +  mCommand.toString(false) + ")";
	}
}



//
//
//Copyright (C) 2008, David A. Lee.
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