/**
 * $Id: colon.java 245 2009-05-29 11:44:01Z daldei $
 * $Date: 2009-05-29 07:44:01 -0400 (Fri, 29 May 2009) $
 *
 */

package org.xmlsh.commands.builtin;

import java.util.List;

import org.xmlsh.core.BuiltinCommand;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.XValue;

/*
 * Ties variables to an expression
 */
public class tie extends BuiltinCommand {

	
	public int run( List<XValue> args ) throws Exception {
			
			if( args.size() < 1 )
				usage();
			String var = args.get(0).toString();
			String expr = args.size() > 1 ? args.get(1).toString() : null ;
			mShell.getEnv().tie( mShell , var , expr );
			return 0;
				
	}
	
	private void usage() throws InvalidArgumentException
	{
		this.mShell.printErr("Usage: tie variable ['expression']"); 
		
		throw new InvalidArgumentException("Usage: tie variable 'expression'");
		
		
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
