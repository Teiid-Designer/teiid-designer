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

package com.metamatrix.modeler.webservice.lds;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * This class defines the interfaces for building a WAR file and validating the input properties.
 */
public interface WebArchiveBuilder {

    /**
     * Validate that a context name is valid.
     * 
     * @param contextName
     * @return
     */
    public IStatus validateContextName(String contextName);

    /**
     * Creates a new WAR file using the input VDB and inputproperties.
     * 
     * @param properties
     * @return
     */
    public IStatus createWebArchive(Map properties, IProgressMonitor monitor);

    /**
     * Checks the target WAR fileName (constructed from properties)
     *  to see if it already exists on the file system
     * @param properties
     * @return 'true' if the file already exists, 'false' if not.
     */
    public boolean targetWarFileExists(Map properties);

}
