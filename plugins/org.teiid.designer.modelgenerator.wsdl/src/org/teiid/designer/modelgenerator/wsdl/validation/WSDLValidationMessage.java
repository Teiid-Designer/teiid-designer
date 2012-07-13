/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.validation;

import org.eclipse.core.runtime.IStatus;

public class WSDLValidationMessage implements IStatus {

    private String m_message;
    private int m_severity;

    public WSDLValidationMessage( String message,
                                  int severity ) {
        m_message = message;
        m_severity = severity;
    }

    @Override
	public IStatus[] getChildren() {
        return new IStatus[0];
    }

    @Override
	public int getCode() {
        return 0;
    }

    @Override
	public Throwable getException() {
        return null;
    }

    @Override
	public String getMessage() {
        return m_message;
    }

    @Override
	public String getPlugin() {
        return null;
    }

    @Override
	public int getSeverity() {
        return m_severity;
    }

    @Override
	public boolean isMultiStatus() {
        return false;
    }

    @Override
	public boolean isOK() {
        if (m_severity == OK) return true;
        return false;
    }

    @Override
	public boolean matches( int severityMask ) {
        return (m_severity & severityMask) != 0;
    }
}
