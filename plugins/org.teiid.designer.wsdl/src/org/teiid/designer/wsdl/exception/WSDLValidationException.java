/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.wsdl.exception;

import java.util.ArrayList;

import javax.wsdl.WSDLException;

/**
 * This class represents any errors that might occur while reading or validating a WSDL
 */
public class WSDLValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    private ArrayList m_messages;

    public WSDLValidationException() {
        super();
    }

    public WSDLValidationException( ArrayList validationMessages ) {
        m_messages = validationMessages;
    }

    public WSDLValidationException( WSDLException wx ) {
        super(wx);
    }

    /**
     * If a validation error has occurred, calling this method will return a list of validation error messages
     * 
     * @return an array of validation errors or null if there are none
     */
    public ArrayList getValidationMessages() {
        return m_messages;
    }

}
