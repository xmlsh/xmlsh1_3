/**
 * $Id: $
 * $Date: $
 *
 */

package org.xmlsh.xproc.compiler;

import java.util.ArrayList;
import java.util.List;

import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmNodeKind;
import net.sf.saxon.s9api.XdmSequenceIterator;
import org.xmlsh.xproc.util.XProcException;

class Otherwise {
	List<OutputOrLog>	outputs = new ArrayList<OutputOrLog>();
	SubPipeline 	subpipeline = new SubPipeline();
	
	static Otherwise create(XdmNode node){
		Otherwise o = new Otherwise();
		o.parse(node);
		return o;
	}

	protected void parse(XdmNode node) {

		parseChildren(node);
		
	}

	private void parseChildren(XdmNode parent) {

		Output			output = null;
		Log				log = null ;
		
		XdmSequenceIterator children = parent.axisIterator(Axis.CHILD);
		while( children.hasNext() ){
			XdmItem item=children.next();
			if( item instanceof XdmNode ){
				XdmNode child = (XdmNode) item ;
				if( child.getNodeKind() != XdmNodeKind.ELEMENT )
					continue ;
				QName name = child.getNodeName();
				
				if( name.equals(Names.kOUTPUT))
					output = Output.create(child);
				else
				if( name.equals(Names.kLOG))
					log = Log.create(child);
				
				if(output != null || log != null ){
					
					outputs.add( new OutputOrLog(output,log));

					output = null;
					log = null;
					continue ;
				}
				
				// Sub pipeline 
				subpipeline.parse(child);
			}
		}
	}

	public void serialize(OutputContext c) throws XProcException {
		c.addBodyLine("else");
		subpipeline.serialize(c);
		c.addBodyLine("");
		
		
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
