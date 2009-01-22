/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.validation.internal;

import org.eclipse.core.runtime.IStatus;

public class WSDLValidationMessage implements IStatus {

    private String m_message;
    private int m_severity;

    public WSDLValidationMessage( String message,
                                  int severity ) {
        m_message = message;
        m_severity = severity;
    }

    public IStatus[] getChildren() {
        return new IStatus[0];
    }

    public int getCode() {
        return 0;
    }

    public Throwable getException() {
        return null;
    }

    public String getMessage() {
        return m_message;
    }

    public String getPlugin() {
        return null;
    }

    public int getSeverity() {
        return m_severity;
    }

    public boolean isMultiStatus() {
        return false;
    }

    public boolean isOK() {
        if (m_severity == OK) return true;
        return false;
    }

    public boolean matches( int severityMask ) {
        return (m_severity & severityMask) != 0;
    }
}
