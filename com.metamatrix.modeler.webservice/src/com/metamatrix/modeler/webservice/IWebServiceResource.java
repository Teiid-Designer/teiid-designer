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

package com.metamatrix.modeler.webservice;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;


/** 
 * This interface represents an input resource to the {@link IWebServiceModelBuilder}.
 * @since 4.2
 */
public interface IWebServiceResource {
    
    static final String EXTENSION_WSDL = "wsdl"; //$NON-NLS-1$
    static final String EXTENSION_XSD  = "xsd";  //$NON-NLS-1$

    static final int STATUS_RESOLVED_TO_SELF                = 1;
    static final int STATUS_RESOLVED                        = 2;
    static final int STATUS_RESOLVED_PATH_DOESNT_EXIST      = 3;
    static final int STATUS_UNRESOLVED                      = 4;

    /**
     * Get the namespace of this resource. 
     * @return the namespace URI of this resource.
     * @since 4.2
     */
    String getNamespace();
    
    /**
     * Get the full path of this resource. 
     * @return
     * @since 4.2
     */
    String getFullPath();
    
    /**
     * Obtain the file the corresponds to this resource. 
     * @return the File object, or null if this resource is unresolved
     * @since 4.2
     */
    File getFile();
    
    /**
     * Obtain an input stream to this resource.  This method will return null if
     * it is not {@link #isResolved() resolved}. 
     * @return the input stream to this resource, or null if this resource is not resolved.
     * @throws Exception if there is an error obtaining an input stream when this is resolved
     * @since 4.2
     */
    InputStream getInputStream() throws Exception;
    
    /**
     * Return whether this resource is a valid WSDL file. 
     * @return true if it is a valid WSDL file, or false otherwise.
     * @since 4.2
     */
    boolean isWsdl();
    
    /**
     * Return whether this resource is a valid XML Schema file. 
     * @return true if it is a valid XML Schema file, or false otherwise.
     * @since 4.2
     */
    boolean isXsd();
    
    /**
     * Return the {@link IWebServiceResource resources} that directly reference this resource. 
     * @return the collection of {@link IWebServiceResource} instances; never null but may be empty
     * @see #getReferencedResources()
     * @since 4.2
     */
    Collection getReferencingResources();
    
    /**
     * Return the {@link IWebServiceResource resources} that this resource directly references. 
     * @return the collection of {@link IWebServiceResource} instances; never null but may be empty
     * @see #getReferencingResources()
     * @since 4.2
     */
    Collection getReferencedResources();
    
    /**
     * Get the status describing the resolved state of this resource. 
     * @return the resolved status
     * @see #isResolved()
     * @since 4.2
     */
    IStatus getStatus();
    
    /**
     * Return whether this resource is resolved.  This method returns true if
     * {@link #getStatus()} returns an {@link IStatus} that is {@link IStatus#isOK() OK}.
     * @return true if this resource is resolved, or false otherwise
     * @see #getStatus()
     * @since 4.2
     */
    boolean isResolved();
    
    /**
     * Return whether this resource is resolved to itself, meaning the underlying resource is
     * valid and directly accessible.
     * @return true if this resource is resolved to itself, or false otherwise
     * @see #getStatus()
     * @see #getResolvedResource()
     * @since 4.2
     */
    boolean isResolvedToSelf();
    
    /**
     * Obtain the {@link IWebServiceResource} that represents the resource to which this is resolved.
     * This method returns itself if this resource is resolvable by itself (see {@link #isResolvedToSelf()}).
     * @return the resource to which this is resolved, or null if this is not resolved.
     * @see #isResolved()
     * @see #isResolvedToSelf()
     * @since 4.2
     */
    IWebServiceResource getResolvedResource();
    
    /**
     * Obtain the final {@link IWebServiceResource} that represents the ultimate resource to which this is resolved.
     * This method returns itself if this resource is resolvable by itself (see {@link #isResolvedToSelf()}).
     * @return the resource to which this is resolved, or null if this is not resolved.
     * @see #isResolved()
     * @see #isResolvedToSelf()
     * @since 4.2
     */
    IWebServiceResource getLastResolvedResource();
    
    /**
     * Return the {@link IWebServiceResource} that this object resolves.
     * @return the list of IWebServiceResource instances that are {@link #getResolvedResource() resolved}
     * by this resource
     * @since 4.2
     */
    Collection getResourcesResolved();
    
    /**
     * Sets the resource this is resolved to.
     * @param resource the resource this is being resolved to or <code>null</code> if clearing
     * @return <code>true</code>if this resource is now resolved; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean setResolvedResource(IWebServiceResource resource);
}
