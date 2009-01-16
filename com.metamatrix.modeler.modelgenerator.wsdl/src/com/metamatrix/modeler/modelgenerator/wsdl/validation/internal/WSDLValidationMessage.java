/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
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
