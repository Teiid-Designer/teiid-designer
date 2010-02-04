/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
