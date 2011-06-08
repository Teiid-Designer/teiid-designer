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

import java.io.IOException;
import java.io.InputStream;
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
import org.teiid.rest.RestPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
                Element element = (Element)nodes.item(i);
                parameters.put(element.getNodeName(), element.getTextContent());
            }
            return parameters;
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

    private void loadProperties() {
        try {
            // Get the inputStream
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("teiidrest.properties"); //$NON-NLS-1$

            properties = new Properties();

            // load the inputStream using the Properties
            properties.load(inputStream);

        } catch (IOException e) {
            String msg = RestPlugin.Util.getString("TeiidWSProvider.1"); //$NON-NLS-1$
            logger.logrb(Level.SEVERE, "TeiidWSProvider", "loadProperties", RestPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
            throw new RuntimeException(e);
        }
    }

}

