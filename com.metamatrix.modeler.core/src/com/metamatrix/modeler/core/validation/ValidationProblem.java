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
