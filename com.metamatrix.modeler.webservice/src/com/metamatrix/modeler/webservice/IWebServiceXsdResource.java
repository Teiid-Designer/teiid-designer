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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDSchema;


/** 
 * XML Schemas referenced by WSDL files and other XSDs may have to be copied
 * into the workspace during the {@link IWebServiceModelBuilder building of Web Service models}.
 * This interface represents a single resource that is to be copied, and captures
 * where in the workspace the XSD is to be placed when the
 * {@link com.metamatrix.modeler.webservice.IWebServiceModelBuilder model builder} completes.
 * @since 4.2
 */
public interface IWebServiceXsdResource {
    
    /**
     * Return the resolved schema. 
     * @return the schema; should not be null
     * @since 4.2
     */
    XSDSchema getSchema();
    
    /**
     * Get the target namespace of the XML Schema resource. 
     * @return the target namespace; may be null or empty if the XSD has
     * no target namespace.
     * @since 4.2
     */
    String getTargetNamespace();
    
    /**
     * Get the full path to the file where this XSD originally came from. 
     * @return
     * @since 4.2
     */
    String getOriginalPath();
    
    /**
     * Get the path to the workspace location where this XSD is to be saved.
     * @return the IPath with the workspace location; may be null if
     * no destination has yet been set
     * @since 4.2
     */
    IPath getDestinationPath();
    
    /**
     * Validate the {@link #getDestinationPath() destination path} for this XSD
     * resource. 
     * @return the IStatus containing the validation results; never null
     * @see #isValid(String)
     * @since 4.2
     */
    IStatus isValid();
    
    /**
     * Determine whether the proposed {@link #getDestinationPath() destination path} would make this XSD
     * resource valid.
     * @param proposedDestination the proposed destination path to be validated; may be null
     * @return the IStatus containing the validation results; never null
     * @see #isValid()
     * @since 4.2
     */
    IStatus isValid( final IPath proposedDestination );
}
