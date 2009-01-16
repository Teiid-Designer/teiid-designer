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

package com.metamatrix.modeler.internal.core.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ValidationProblem;

/**
 * ValidationProblemImpl
 */
public class ValidationProblemImpl implements ValidationProblem {
    
    private final int code;
    private final int severity;
    private final String message;
    private boolean hasPreference;
    private final String uri;
    private final String location;
    
    /**
     * Construct an instance of ValidationProblemImpl.
     * @param status - The IStatus of the problem 
     */
    public ValidationProblemImpl(final IStatus status) {
        this(status.getCode(), status.getSeverity(), status.getMessage());
    }

    /**
     * Construct an instance of ValidationProblemImpl.
     * @param code - code for this exception 
     * @param severity - Severity using IStatus constants
     * @param msg - Message to report to the user
     */
    public ValidationProblemImpl(final int code, final int severity, final String message) {
        this(code, severity, message, null, null);
    }

    /**
     * 
     * @param code - code for this exception 
     * @param severity - Severity using IStatus constants
     * @param message - Message to report to the user
     * @param location - the location (generally, the parent) of the issue.  May be null.
     * @param uri - the URI of the EObject causing the issue.  May be null.
     */
    public ValidationProblemImpl(final int code, final int severity, final String message, String location, String uri) {
        validateSeverity(severity);

        this.code = code;
        this.severity = severity;
        this.message = message;
        this.uri = uri;
        this.location = location;
    }

    /**
     * @param severity
     */
    private void validateSeverity(final int severity) {
        if(severity == IStatus.ERROR || severity == IStatus.INFO || severity == IStatus.OK || severity == IStatus.WARNING){
            return;
        }
        throw new IllegalArgumentException(ModelerCore.Util.getString("ValidationProblemImpl.Invalid_severity.__Value_must_be_one_of_valid_status_constants_from_IStatus_class_1")); //$NON-NLS-1$
    }

    /**
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * @return msg
     */
    public String getMessage() {
        if(!hasPreference) {
            return message;
        }
        return message+ModelerCore.Util.getString("ValidationProblemImpl.option_preference"); //$NON-NLS-1$
    }

    /**
     * @return severity
     */
    public int getSeverity() {
        return severity;
    }
    
    /** 
     * @param hasPreference The hasPreference to set.
     * @since 4.2
     */
    public void setHasPreference(boolean hasPreference) {
        this.hasPreference = hasPreference;
    }

    @Override
    public String toString(){
        final StringBuffer buffer = new StringBuffer();
        
        buffer.append(getSeverityString() );
        buffer.append(" - "); //$NON-NLS-1$
        buffer.append(getMessage() );

        return buffer.toString();
    }
    
    private String getSeverityString(){
        switch (this.severity) {
            case IStatus.ERROR : return ModelerCore.Util.getString("ValidationProblemImpl.Error_1"); //$NON-NLS-1$
            case IStatus.INFO : return ModelerCore.Util.getString("ValidationProblemImpl.Info_2"); //$NON-NLS-1$
            case IStatus.OK : return ModelerCore.Util.getString("ValidationProblemImpl.OK_3"); //$NON-NLS-1$
            case IStatus.WARNING : return ModelerCore.Util.getString("ValidationProblemImpl.Warning_4"); //$NON-NLS-1$

            default :
                return(ModelerCore.Util.getString("ValidationProblemImpl.Unknown_Severity_5")); //$NON-NLS-1$
        }
    }

    /** 
     * @see com.metamatrix.modeler.core.validation.ValidationProblem#getStatus()
     * @since 4.2
     */
    public IStatus getStatus() {
        return new Status(severity, ModelerCore.PLUGIN_ID, code, message, null);
    }

    public String getURI() {
        return uri;
    }

    public String getLocation() {
        return location;
    }
}
