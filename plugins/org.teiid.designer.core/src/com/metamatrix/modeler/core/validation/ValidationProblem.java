/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation;

import org.eclipse.core.runtime.IStatus;

/**
 * ValidationProblem
 */
public interface ValidationProblem {
    /**
     * @return code for this problem
     */
    int getCode();

    /**
     * @return message for this problem. Never null.
     */
    String getMessage();

    /**
     * @return severity for this problem
     */
    int getSeverity();

    /**
     * @return IStatus for this problem
     */
    IStatus getStatus();
    
    /**
     * Set boolean indiacating if this problem can be fixed by changing a preference. 
     * @param hasPreference
     * @since 4.2
     */
    void setHasPreference(boolean hasPreference);
    
    /**
     * get the URI for this problem, if defined
     * @return
     */
    String getURI();
    
    /**
     * get the Location for this problem, if defined
     * @return
     */
    String getLocation();
}
