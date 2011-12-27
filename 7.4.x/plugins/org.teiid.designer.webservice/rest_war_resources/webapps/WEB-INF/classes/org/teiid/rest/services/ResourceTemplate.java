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
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.teiid.rest.RestPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

${path}
public class ${className}{

    org.teiid.rest.services.TeiidRSProvider teiidProvider = new org.teiid.rest.services.TeiidRSProvider();
    Map<String, String> parameterMap = new LinkedHashMap<String, String>();
    private static Properties properties = new Properties();
    private static Logger logger = Logger.getLogger("org.teiid.rest"); //$NON-NLS-1$

    public ${className}() {
        loadProperties();
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

    protected Map<String, String> getJSONInputs( InputStream is ) {
        Map parameters;
        try {
            String jsonString = convertStreamToString(is);

            // Do this to validate the JSON string. If we don't blow up, then we are good.
            new JSONObject(jsonString);

            parameters = convertJSONStringToMap(jsonString);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return parameters;
    }

    public String convertStreamToString( InputStream is ) throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. 
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); //$NON-NLS-1$
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        }

        return ""; //$NON-NLS-1$
    }

    public Map<String, String> convertJSONStringToMap( String jsonString ) {

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
                parameterMap.put(fieldname, value);
            }
        } catch (JsonParseException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        } catch (IOException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        return parameterMap;
    }

    private void loadProperties() {
        try {
            // Get the inputStream
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("teiidrest.properties"); //$NON-NLS-1$

            properties = new Properties();

            // load the inputStream using the Properties
            properties.load(inputStream);

        } catch (IOException e) {
            String msg = RestPlugin.Util.getString("TeiidRSProvider.1"); //$NON-NLS-1$
            logger.logrb(Level.SEVERE, "TeiidWSProvider", "loadProperties", RestPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
            throw new RuntimeException(e);
        }
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
}

