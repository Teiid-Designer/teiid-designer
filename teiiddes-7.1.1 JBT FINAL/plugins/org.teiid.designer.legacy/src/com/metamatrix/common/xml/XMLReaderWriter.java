/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.xml;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jdom.Document;
import org.jdom.JDOMException;


/** 
* This interface is used to read and write JDOM compliant XML files.
*/
public interface XMLReaderWriter {

    /**
    * This method will write a JDOM Document to an OutputStream.
    *
    * @param doc the JDOM document to be written to the OutputStream
    * @param stream the output stream to be written to.
    * @throws IOException if there is a problem writing to the OutputStream
    */
    public void writeDocument(Document doc, OutputStream stream) throws IOException;
    
    /**
    * This method will write a JDOM Document to an OutputStream.
    *
    * @param stream the input stream to read the XML document from.
    * @return the JDOM document reference that represents the XML text in the
    * InputStream.
    * @throws IOException if there is a problem reading from the InputStream
    * @throws JDOMException if the InputStream does not represent a JDOM 
    * compliant XML document.
    */
    public Document readDocument(InputStream stream) throws JDOMException, IOException;
    
}
