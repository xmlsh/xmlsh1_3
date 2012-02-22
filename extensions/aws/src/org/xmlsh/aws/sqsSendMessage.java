package org.xmlsh.aws;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import net.sf.saxon.s9api.SaxonApiException;
import org.xmlsh.aws.util.AWSSQSCommand;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.InputPort;
import org.xmlsh.core.InvalidArgumentException;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.UnexpectedException;
import org.xmlsh.core.XValue;
import org.xmlsh.util.Util;

import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;


public class sqsSendMessage extends AWSSQSCommand {

	



	/**
	 * @param args
	 * @throws IOException 
	 */
	@Override
	public int run(List<XValue> args) throws Exception {

		
		Options opts = getOptions("f=file:");
		opts.parse(args);

		args = opts.getRemainingArgs();
		
		if( args.size() < 1 ){
			usage();
			return 1;
		}
		

		
		mSerializeOpts = this.getSerializeOpts(opts);
		
		
		String url = args.get(0).toString();
		String body;
		// Get message from file 
		if( opts.hasOpt("f")){
			body = readMessage( mShell.getEnv().getInput(opts.getOptValue("f")));
			
		} else
		switch( args.size() ){
		case	1:
		// Read from stdin 
			body = readMessage( getStdin() );
			break ;
		case	2:
			body = args.get(1).toString();
			break ;
		default :
		{
			usage();
			return 1 ;
		}
		}
		
		
			
		
		try {
			mAmazon = getSQSClient(opts);
		} catch (UnexpectedException e) {
			usage( e.getLocalizedMessage() );
			return 1;
			
		}
		
		int ret;
		
		ret = send(url , body  );
		
		
		return ret;
		
		
	}


	private String readMessage(InputPort input) throws CoreException, IOException {
		
		InputStream is = input.asInputStream(mSerializeOpts);
		String body = Util.readString(is, mSerializeOpts.getInputTextEncoding() );
		is.close();
		input.release();
		return body ;
		
		
		
	}


	private int send(String url, String body ) throws IOException, InvalidArgumentException, XMLStreamException, SaxonApiException  {
		

		SendMessageRequest request = new SendMessageRequest(url, body);
		
		SendMessageResult result = mAmazon.sendMessage(request);
		
		OutputPort stdout = this.getStdout();
		mWriter = stdout.asXMLStreamWriter(mSerializeOpts);
		
		
		startDocument();
		startElement(getName());
		
		
			startElement("message");
			attribute("md5", result.getMD5OfMessageBody());
			attribute("id" , result.getMessageId());

			endElement();
			
		
		
		endElement();
		endDocument();
		closeWriter();
		stdout.writeSequenceTerminator(mSerializeOpts);
		stdout.release();
		

		return 0;
		
		
		
		
	}


	

}
