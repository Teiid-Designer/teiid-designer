/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.rest.services;

import javax.xml.transform.Source;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.teiid.rest.RestPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

${path}
${api}
public class ${className}{

    ${TeiidRSProvider} teiidProvider = new ${TeiidRSProvider}();
    private final static Logger logger = Logger.getLogger("org.teiid.rest"); //$NON-NLS-1$
    private final static Properties properties;

    static {
    	try {
            // Get the inputStream
            InputStream inputStream = ${className}.class.getClassLoader().getResourceAsStream("teiidrest.properties"); //$NON-NLS-1$

            properties = new Properties();

            // load the inputStream using the Properties
            properties.load(inputStream);

        } catch (IOException e) {
            String msg = RestPlugin.Util.getString("TeiidRSProvider.1"); //$NON-NLS-1$
            logger.logrb(Level.SEVERE, "TeiidWSProvider", "loadProperties", RestPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
            throw new RuntimeException(e);
        }
    }

    public ${className}() {
    }

    ${httpMethods}
    
    protected Map<String, String> getInputs( InputStream is ) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            Element root = doc.getDocumentElement(); // input
            Map<String, String> parameters = new LinkedHashMap<String, String>();
            if (!root.getNodeName().equals("input")) throw new WebApplicationException(Response.Status.BAD_REQUEST);
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = findElement(nodes.item(i));
                if (element != null){
                    parameters.put(element.getNodeName(), element.getTextContent());
                }
            }
            return parameters;
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    private Element findElement( Node node ) {
        while (node != null && node.getNodeType() != Node.ELEMENT_NODE)
            node = node.getNextSibling();
        return (Element)node;
    }

    protected Map<String, String> getJSONInputs( InputStream is, String charset ) {
    	Map<String, String> parameters = getParameterMap();
    	 
    	try {
            String jsonString = convertInputStreamToString(is, charset);

            // Do this to validate the JSON string. If we don't blow up, then we are good.
            new JSONObject(jsonString);
            parameters = convertJSONStringToMap(jsonString);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return parameters;
    }
    
    public String convertInputStreamToString( InputStream is, String charset) {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. 
         */
    	
    	if (is != null) {
    	
    	   ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    	   try {
	    	    int nRead;
	    	    byte[] data = new byte[1024];
	    	    while ((nRead = is.read(data, 0, data.length)) != -1) {
	    	        buffer.write(data, 0, nRead);
	    	    }
	    	 
	    	    buffer.flush();
	    	    byte[] byteArray = buffer.toByteArray();
	    	         
	    	    return new String(byteArray, charset);
	    	    
    	   }catch(Exception ioe){
    		   throw new WebApplicationException(ioe, Response.Status.INTERNAL_SERVER_ERROR);	
           }
    	    
    	}
    	    
    	return ""; //$NON-NLS-1$
    	
    }

    public String convertStreamToString( StreamingOutput is, String charset) {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. 
         */
        if (is != null) {

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
            	
            	is.write(output);
            	//String string = new String(output.toByteArray(), charset);
                
            }catch(Exception ioe){
            	throw new WebApplicationException(ioe, Response.Status.INTERNAL_SERVER_ERROR);	
            }
            
            return output.toString();
        }

        return ""; //$NON-NLS-1$
    }

    public Map<String, String> convertJSONStringToMap( String jsonString ) {

    	Map<String, String> parameters = getParameterMap();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        JsonParser jp = null;
        try {
            jp = factory.createJsonParser(jsonString);
        } catch (JsonParseException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        } catch (IOException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        try {
            jp.nextToken();
        } catch (JsonParseException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        } catch (IOException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        try {
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = jp.getCurrentName();
                jp.nextToken(); // move to value, or START_OBJECT/START_ARRAY
                String value = jp.getText();
                parameters.put(fieldname, value);
            }
        } catch (JsonParseException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        } catch (IOException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        return parameters;
    }

    /**
     * Converts XML to JSON
     */
    public static String convertXMLToJSON( String XMLfile ) {
        // obj that will convert xml string to json obj
        JSONObject jsonObj;
        String jsonString = ""; //$NON-NLS-1$

        // convert xml to json obj
        try {
            jsonObj = XML.toJSONObject(XMLfile);
            jsonString = jsonObj.toString(1);
        } catch (JSONException je) {
            String msg = RestPlugin.Util.getString("TeiidRSProvider.1"); //$NON-NLS-1$
            logger.logrb(Level.SEVERE, "TeiidRSProvider", "convertXMLToJSON", RestPlugin.PLUGIN_ID, msg, new Throwable(je)); //$NON-NLS-1$ //$NON-NLS-2$
            throw new RuntimeException(je);
        }

        return jsonString;
    }
    
    private Map<String, String> getParameterMap( ) {
	
    	return new LinkedHashMap<String, String>();
	 
    }
	
}
