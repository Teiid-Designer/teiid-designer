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

package com.metamatrix.modeler.internal.webservice;

import org.eclipse.core.runtime.IPath;

import com.metamatrix.modeler.webservice.IWebServiceXsdResource;


/** 
 * @since 4.2
 */
public interface IInternalWebServiceXsdResource extends IWebServiceXsdResource {

    /**
     * Set the path to the workspace location where this XSD is to be saved. 
     * @param workspacePathForXsd the IPath with the workspace location;
     * may be null
     * @since 4.2
     */
    void setDestinationPath( final IPath workspacePathForXsd );
}
