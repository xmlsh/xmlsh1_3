/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.marklogic.ui;

import org.xmlsh.marklogic.util.MLUtil;

import com.marklogic.xcc.ResultSequence;
import com.marklogic.xcc.types.XdmVariable;

public class MLDeleteRequest extends MLQueryRequest {

	
	
	public MLDeleteRequest(String url) throws InterruptedException {
		super("Deleting ... ", QueryCache.getInstance().getQuery("documentDelete.xquery") , 
				  new XdmVariable[] { 	MLUtil.newVariable("url", url ) }, null );
	}

	@Override
	void onComplete(ResultSequence rs) throws Exception {
		
	}


}



/*
 * Copyright (C) 2008-2014 David A. Lee.
 * 
 * The contents of this file are subject to the "Simplified BSD License" (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.opensource.org/licenses/bsd-license.php 

 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights and limitations under the License.
 *
 * The Original Code is: all this file.
 *
 * The Initial Developer of the Original Code is David A. Lee
 *
 * Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
 *
 * Contributor(s): David A. Lee
 * 
 */