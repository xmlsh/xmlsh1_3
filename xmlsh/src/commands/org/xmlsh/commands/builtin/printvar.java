/**
 * $Id: echo.java 88 2008-11-27 17:06:00Z daldei $
 * $Date: 2008-11-27 12:06:00 -0500 (Thu, 27 Nov 2008) $
 *
 */

package org.xmlsh.commands.builtin;

import java.io.OutputStream;
import java.util.List;

import org.xmlsh.core.BuiltinCommand;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.shell.SerializeOpts;
import org.xmlsh.util.Util;

public class printvar extends BuiltinCommand {
	
	public int run( List<XValue> args ) throws Exception {

		Options opts = new Options( "n,p=port:" ,  SerializeOpts.getOptionDefs());
		opts.parse(args);
		
		boolean nolf = opts.hasOpt("n");
		String port = opts.getOptString("p", null);
		
		
		OutputPort stdout = 
			port != null ? mShell.getEnv().getOutputPort(port) : 
			mShell.getEnv().getStdout();
			
		if( stdout == null )
			throw new InvalidArgumentException("Output port not found: " + port );
			
		SerializeOpts serializeOpts = getSerializeOpts(opts);
			
		OutputStream out = stdout.asOutputStream(serializeOpts);
	
		
		args = opts.getRemainingArgs();
	
		args = Util.expandSequences( args);
		boolean bFirst = true;
		for ( XValue arg : args ){
			    if( ! bFirst  && !nolf )  
			           stdout.writeSequenceSeperator(serializeOpts);
				if( arg.isNull() )
				    continue;
			    String varname = arg.toString();
                XValue value = mShell.getEnv().getVarValue(varname);
				if( value == null )
				    mShell.printErr("not found: " + varname);
				else {
				  bFirst = false;
				  value.serialize( out , serializeOpts );
				}
		}
		if( !nolf )
		stdout.writeSequenceTerminator(serializeOpts);
		return 0;
	}
}
//
//
//Copyright (C) 2008-2014    David A. Lee.
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
