/**
 * $Id: colon.java 245 2009-05-29 11:44:01Z daldei $
 * $Date: 2009-05-29 07:44:01 -0400 (Fri, 29 May 2009) $
 *
 */

package org.xmlsh.commands.builtin;

import java.util.List;

import org.xmlsh.core.BuiltinCommand;
import org.xmlsh.core.CommandFactory;
import org.xmlsh.core.ICommand;
import org.xmlsh.core.Options;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.core.Command;
import org.xmlsh.sh.core.EvalScriptCommand;
import org.xmlsh.sh.core.SourceLocation;
import org.xmlsh.sh.shell.Shell;

public class xmlsh extends BuiltinCommand {

	
	boolean mTopShell = false ;
	
	
	public xmlsh()
	{
		
	}
	
	
	/*
	 * Special constructor for a top level shell which doesnt clone
	 */
	public xmlsh( boolean bTopShell )
	{
		mTopShell = bTopShell ;
	}
	
	
	
	public int run( List<XValue> args ) throws Exception {
			
		Options opts = new Options( "+n,+x,+v,+e,c:,rcfile:,e,norc"  );
		opts.parse(args);
		Shell shell = getShell();
		
		if( ! mTopShell )
			shell = shell.clone();
		
		
	    int ret = 0;
		try {
		    mShell.setOptions( opts );
	    	
	    	String command  = null ;
	    	if( opts.hasOpt("c"))
	    		command = opts.getOptStringRequired("c").toString();
		    
	    	boolean bNoRc = opts.hasOpt("norc");
		    args = opts.getRemainingArgs();
		    
		    String rcfile =  opts.getOptString("rcfile", null );
		    if( rcfile == null ){
		    	XValue xrc = shell.getEnv().getVarValue("XMLSHRC");
		        if( xrc != null )
		        	rcfile = xrc.toString();
		    	if( rcfile == null ){
			    	XValue home = shell.getEnv().getVarValue("HOME");
			    	if( home != null ){
			    		rcfile = home.toString() + "/.xmlshrc" ;
			    	}
		    	}
		    }	
		    
		    
		    if( ! bNoRc && rcfile != null )
		    	shell.runRC(rcfile);
			
			
			
		    
		    if(  args.size() == 0 && command == null ){
			   
			    		
		    	ret = shell.interactive();
		    	
		    } else {
	
		     	
			    // Run command
			    if(command != null)
			    {
	
	
			    	
			    	Command cmd = new EvalScriptCommand( command );
		    		ret = shell.exec(cmd);
			    	
	
			    }
			    else // Run script 
			    {
			    	
			    	String scmd = args.remove(0).toString();
			    	ICommand cmd = CommandFactory.getInstance().getScript( shell , scmd, true,getLocation() );
			    	if( cmd == null ){
			    		SourceLocation loc = getLocation();
						if( loc != null )
							shell.printErr(loc.toString());
						
			    		shell.printErr( scmd + ": not found");
			    	}
			    	else {
			    		
			    			// Run as sourced mode, in this shell ...
			    		// must set args ourselves
			    		
			    		shell.setArg0( scmd);
			    		shell.setArgs(args );
			    		ret = cmd.run( shell , scmd , null );
			    	}
			    	
			    	
			    }
		    	
		    }
		} finally {
			if( ! mTopShell )
				shell.close();
			
		}
	    return ret ;
				
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
