/**
 * $Id$
 * $Date$
 *
 */

package org.xmlsh.sh.core;

import java.io.PrintWriter;

import org.xmlsh.sh.shell.ControlLoop;
import org.xmlsh.sh.shell.Shell;

public class WhileClause extends CompoundCommand {
	private		Command		mWhile;
	private		Command		mDo;
	public WhileClause(Command while1, Command do1) {
		super();
		mWhile = while1;
		mDo = do1;
		setLocation(do1);
	}
	
	public void print(PrintWriter out, boolean bExec)
	{
		out.print("while ");
		mWhile.print(out, bExec);
		out.println(" do");
		mDo.print(out, bExec);
		out.println("done");
	}
	

	public int exec(Shell shell) throws Exception {
		
		shell.getEnv().saveIO();
		ControlLoop loop = shell.pushLoop( getLocation() );
		try {

			applyRedirect(shell);	

			while( shell.keepRunning() && Shell.toBool( shell.execCondition( mWhile ))   ){
					
					shell.exec( mDo );
					
					
					if( loop.mContinue ) // continue clause - clear out  continue & keep going
						loop.mContinue = false ;
			}
		} 
		finally {
			shell.popLoop(  loop );
			shell.getEnv().restoreIO();
		}
		
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

