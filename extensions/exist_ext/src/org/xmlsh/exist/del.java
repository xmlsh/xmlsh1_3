package org.xmlsh.exist;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.xmlsh.core.Options;
import org.xmlsh.core.XValue;
import org.xmlsh.exist.util.ExistCommand;
import org.xmlsh.exist.util.ExistConnection;
import org.xmlsh.util.Util;

public class del extends ExistCommand {


	@Override
	public int run(List<XValue> args) throws Exception {
		
		
		
		Options opts = getOptions();
		opts.parse(args);
		args = opts.getRemainingArgs();
		


		int ret = 0;
		

		if( args.size() == 0 ){
			usage();
			return 1;
		}
		
		for( String uri : Util.toStringList(args)){
				
			ExistConnection conn = getConnection(opts, uri);
				
			ret += conn.del();
			conn.close();
				
		}
			
			
		
		
		return ret;
	}

	private String getOutputFile(File outDir, String uri) throws IOException {
		while( uri.startsWith("/"))
			uri = uri.substring(1);
			
		return mShell.getFile(outDir,uri).getAbsolutePath();
	}

	
	

}



//
//
//Copyright (C) 2008-2014  David A. Lee.
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
