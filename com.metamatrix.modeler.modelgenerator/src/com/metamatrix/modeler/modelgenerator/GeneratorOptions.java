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

package com.metamatrix.modeler.modelgenerator;

import org.eclipse.core.runtime.IStatus;

/**
 * GeneratorOptions
 */
public interface GeneratorOptions {

    /**
     * Validate the current option settings, and return an {@link IStatus} denoting whether
     * additional options must be set and whether any current settings are invalid.
     * <p>
     * The resulting IStatus should return true for {@link IStatus#isOK()} with {@link IStatus#getSeverity() severity} 
     * of {@link IStatus#INFO INFO} if the current option settings are considered valid.  
     * Otherwise, the {@link IStatus#getSeverity() severity} should be:
     * <ul>
     *  <li>{@link IStatus#WARNING WARNING} if the current settings may be used, but also may
     *      cause unexpected or unanticipated results.</li>
     *  <li>{@link IStatus#WARNING ERROR} if the current settings may not be used as is and must
     *      be corrected.</li>
     * </ul> 
     * </p>
     * @return an IStatus denoting whether the current option settings are considered valid.
     */
    IStatus validate();

}
