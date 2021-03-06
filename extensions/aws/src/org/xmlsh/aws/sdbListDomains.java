package org.xmlsh.aws;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import net.sf.saxon.s9api.SaxonApiException;

import org.xmlsh.aws.util.AWSSDBCommand;
import org.xmlsh.core.CoreException;
import org.xmlsh.core.Options;
import org.xmlsh.core.OutputPort;
import org.xmlsh.core.UnexpectedException;
import org.xmlsh.core.XValue;

import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;


public class sdbListDomains	 extends  AWSSDBCommand {



    /**
     * @param args
     * @throws IOException 
     */
    @Override
    public int run(List<XValue> args) throws Exception {


        Options opts = getOptions();
        parseOptions(opts, args);

        setSerializeOpts(this.getSerializeOpts(opts));

        args = opts.getRemainingArgs();

        if( args.size() != 0 ){
            usage();
            return 1;
        }

        try {
            getSDBClient(opts);
        } catch (UnexpectedException e) {
            usage( e.getLocalizedMessage() );
            return 1;

        }


        int ret = -1;
        ret = list();



        return ret;


    }


    private int list() throws IOException, XMLStreamException, SaxonApiException, CoreException 
    {

        OutputPort stdout = getStdout();
        mWriter = stdout.asXMLStreamWriter(getSerializeOpts());

        startDocument();
        startElement(getName());

        String token = null ;

        do {
            ListDomainsRequest listDomainsRequest = new ListDomainsRequest();
            if( token != null )
                listDomainsRequest.setNextToken(token);

            traceCall("listDomains");

            ListDomainsResult result = mAmazon.listDomains(listDomainsRequest);
            writeStringList(  null , "domain" , "name" ,  result.getDomainNames() );
            token = result.getNextToken();
        } while( token != null );
        endElement();
        endDocument();








        closeWriter();
        stdout.writeSequenceTerminator(getSerializeOpts());
        stdout.release();




        return 0;




    }


    @Override
    public void usage() {
        super.usage();
    }





}
