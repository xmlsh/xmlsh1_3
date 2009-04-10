/**
 * $Id: xpwd.java 21 2008-07-04 08:33:47Z daldei $
 * $Date: 2008-07-04 04:33:47 -0400 (Fri, 04 Jul 2008) $
 *
 */

package org.xmlsh.commands;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.List;

import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.util.Util;

/*
 * 
 * Command to call into any java class's main method in the same JVM
 * 
 * jcall class args
 * 
 */

public class jcall extends XCommand
{
	@SuppressWarnings("serial")
	private static class ExitException extends SecurityException
	{
		int	mExitCode ;
		ExitException( int code )
		{
			mExitCode = code ;
		}
		
	
	}
	private static class NoExitSecurityManager extends SecurityManager
	{
		SecurityManager mParent;
		 @Override
	        public void checkPermission(Permission perm) 
	        {
			 if( mParent != null )
				 mParent.checkPermission(perm);
			 
	        }
	        @Override
	        public void checkPermission(Permission perm, Object context) 
	        {

				 if( mParent != null )
					 mParent.checkPermission(perm, context);
	        
	        }
	        
	        
	    NoExitSecurityManager( SecurityManager parent )
	    {
	    	mParent = parent ;
	    }
		/* (non-Javadoc)
		 * @see java.lang.SecurityManager#checkExit(int)
		 */
		@Override
		public void checkExit(int status) {
			throw new ExitException(status);
		}
		
		
	}

	
	
	public synchronized int run(  List<XValue> args )	throws Exception
	{
		SecurityManager oldManager = null;
		if( args.size() < 1 )
			throw new InvalidArgumentException( "usage: jcall class [args]");
		
		PrintStream	stdout = System.out;

		// DAL: Resesting stderr causes stderr to be lost after this call
		// dont know why. Dont reset stderr and all is well.
		// PrintStream	stderr = System.err;
		InputStream	stdin  = System.in;
		
		PrintStream newStdout = null;
		// PrintStream newStderr = null;
		
		try {
			oldManager = System.getSecurityManager();
			System.setSecurityManager(new NoExitSecurityManager(oldManager));
			
			
			System.setOut(newStdout = getStdout().asPrintStream());

			System.setIn(getStdin().asInputStream(getSerializeOpts())) ;
		
			String className = args.remove(0).toString();
			Class<?> cls = Class.forName(className);
			
			Method method = cls.getMethod("main", String[].class );
			method.invoke(null, new Object[] { Util.toStringArray(args)} );
		
		}
		catch( InvocationTargetException e )
		{
			Throwable e2 = e.getTargetException() ;
			if( e2 instanceof ExitException )
				return ((ExitException )e2).mExitCode ;
			else
				throw e ;
		}
		catch ( ExitException e ){
			
			return e.mExitCode ;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		
		finally {
			System.setSecurityManager(oldManager);
			
			
			System.setOut(stdout);
			// System.setErr(stderr);
			System.setIn(stdin);
			
			newStdout.flush();
			// newStderr.flush();
			
			
			
		}
		
		return 0;
		
	}
	
	/*
	 * Test for calling jcall or exiting 
	 */
	public static void main( String[] args )
	{
		
		System.out.println(args[0]);
		if( args.length == 1 && args[0].equals("exit"))
			System.exit(1);
		
		
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
