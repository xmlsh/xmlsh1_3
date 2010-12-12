/**
 * $Id: xpwd.java 21 2008-07-04 08:33:47Z daldei $
 * $Date: 2008-07-04 04:33:47 -0400 (Fri, 04 Jul 2008) $
 *
 */

package org.xmlsh.commands.internal;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;
import org.xmlsh.core.InputPort;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.XCommand;
import org.xmlsh.core.XValue;
import org.xmlsh.sh.shell.SerializeOpts;

/*
 * 
 * Convert XML files to an CSV file
 * 
 * Arguments
 * 
 * -header		Add a header row
 * 
 * 
 */

public class json2xml extends XCommand {

	public int run(List<XValue> args) throws Exception {
		Options opts = new Options(SerializeOpts.getOptionDefs());
		opts.parse(args);

		args = opts.getRemainingArgs();

		OutputPort stdout = getStdout();

		SerializeOpts serializeOpts = getSerializeOpts(opts);

		InputPort in = args.isEmpty() ? this.getStdin() : this.getInput(args.get(0));
		Reader inr = new InputStreamReader(in.asInputStream(serializeOpts), serializeOpts
				.getText_encoding());
		;

		JSONTokener tokenizer = new JSONTokener(inr);

		/*
		 * Assume JSON file is wrapped by an Object
		 */
		JSONObject obj = new JSONObject(tokenizer);

		XMLStreamWriter sw = stdout.asXMLStreamWriter(serializeOpts);
		sw.writeStartDocument();

		write(obj, sw);
		sw.writeEndDocument();
		sw.flush();
		sw.close();

		inr.close();

		return 0;

	}

	private void write(JSONObject obj, XMLStreamWriter writer) throws XMLStreamException,
			InvalidArgumentException, JSONException {
		writer.writeStartElement("OBJECT");


		Iterator<?> keys = obj.keys();

		while (keys.hasNext()) {

			Object k = keys.next();
			writer.writeStartElement("MEMBER");

			String name = k.toString();
			writer.writeAttribute("name", name);
			Object v = obj.get(name);
			write(v,writer);
			writer.writeEndElement();

		}
		writer.writeEndElement();

	}

	private void writeBoolean(Boolean b, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("BOOLEAN");
		// writer.writeAttribute("value", b.booleanValue() ? "true" : "false");
		writer.writeCharacters(b.booleanValue() ? "true" : "false");
		writer.writeEndElement();

	}

	
	private void writeNull( XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("NULL");
		writer.writeEndElement();

	}
	private void write(Number v, XMLStreamWriter writer) throws XMLStreamException, JSONException {
		writer.writeStartElement("NUMBER");
		// writer.writeAttribute("value", JSONObject.numberToString(v));
		writer.writeCharacters( JSONObject.numberToString(v));
		writer.writeEndElement();

	}

	private void write(String s, XMLStreamWriter writer) throws XMLStreamException {

		writer.writeStartElement("STRING");
		// writer.writeAttribute("value", s);
		writer.writeCharacters(s);
		writer.writeEndElement();

	}

	private void write(JSONArray array, XMLStreamWriter writer) throws XMLStreamException,
			JSONException, InvalidArgumentException {

		boolean b = false;
		int len = array.length();

		writer.writeStartElement("ARRAY");

		for (int i = 0; i < len; i += 1) {

			Object v = array.get(i);
			write(v, writer);
		}
		writer.writeEndElement();

	}

	private void write(Object object, XMLStreamWriter writer) throws XMLStreamException,
			InvalidArgumentException, JSONException {
		if (object instanceof JSONObject) {
			write((JSONObject) object, writer);

		} else if (object instanceof JSONArray) {
			write((JSONArray) object, writer);
		} else if (object instanceof JSONString) {

			write(((JSONString) object).toJSONString(), writer);
		} else if (object instanceof Number) {
			write((Number) object, writer);
		} else if (object instanceof Boolean) {
			writeBoolean(((Boolean) object), writer);
		} else if( object == JSONObject.NULL )
			writeNull(  writer );
		else if( object instanceof String )
			write( (String) object , writer );
		else
			throw new InvalidArgumentException("Unknown type for JSON value: "
					+ object.getClass().toString());

	}

}

//
//
// Copyright (C) 2008,2009,2010 , David A. Lee.
//
// The contents of this file are subject to the "Simplified BSD License" (the
// "License");
// you may not use this file except in compliance with the License. You may
// obtain a copy of the
// License at http://www.opensource.org/licenses/bsd-license.php
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations
// under the License.
//
// The Original Code is: all this file.
//
// The Initial Developer of the Original Code is David A. Lee
//
// Portions created by (your name) are Copyright (C) (your legal entity). All
// Rights Reserved.
//
// Contributor(s): none.
//