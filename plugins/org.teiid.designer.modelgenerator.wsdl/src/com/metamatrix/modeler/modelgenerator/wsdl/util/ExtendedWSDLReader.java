/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.ibm.wsdl.util.StringUtils;
import com.ibm.wsdl.xml.WSDLReaderImpl;
import com.metamatrix.common.protocol.URLHelper;

/**
 * The {@link WSDLReaderImpl} is not able to read WSDL files secured by
 * authentication credentials. This class extends the implementation to set the
 * credentials using the request property of the HTTPURLConnection.
 */
public class ExtendedWSDLReader extends WSDLReaderImpl {

    /**
     * @param wsdlURI
     * @param userName
     * @param password
     * @return
     */
    public Definition readWSDL(String wsdlURI, String userName, String password)
            throws WSDLException {
        return readWSDL(null, wsdlURI, userName, password);
    }

    /**
     * 
     * @param contextURI
     * @param wsdlURI
     * @param userName
     * @param password
     * @return
     * @throws WSDLException
     */
    public Definition readWSDL(String contextURI, String wsdlURI, String userName, String password)
            throws WSDLException {
        try {
            if (verbose) {
                System.out.println("Retrieving document at '" + wsdlURI + "'" //$NON-NLS-1$ //$NON-NLS-2$
                        + (contextURI == null ? "." : ", relative to '" + contextURI + "'.")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            }

            URL contextURL = (contextURI != null) ? StringUtils.getURL(null, contextURI) : null;
            URL url = StringUtils.getURL(contextURL, wsdlURI);
            URLConnection urlConn = url.openConnection();
            URLHelper.setCredentials(urlConn, userName, password);

            InputStream inputStream = urlConn.getInputStream();
            InputSource inputSource = new InputSource(urlConn.getInputStream());
            inputSource.setSystemId(url.toString());
            Document doc = getDocument(inputSource, url.toString());

            inputStream.close();

            Definition def = readWSDL(url.toString(), doc);

            return def;
        } catch (WSDLException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new WSDLException(WSDLException.OTHER_ERROR,
                    "Unable to resolve imported document at '" + wsdlURI //$NON-NLS-1$
                            + (contextURI == null ? "'." : "', relative to '" + contextURI + "'."), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    e);
        }
    }

    private static Document getDocument(InputSource inputSource, String desc) throws WSDLException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);
        factory.setValidating(false);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputSource);

            return doc;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new WSDLException(WSDLException.PARSER_ERROR, "Problem parsing '" + desc + "'.", //$NON-NLS-1$ //$NON-NLS-2$
                    e);
        }
    }

}
