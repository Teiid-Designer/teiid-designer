/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import java.io.File;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import com.metamatrix.core.util.CoreArgCheck;


/** 
 * @since 4.2
 */
public class WorkspaceFileWebServiceResource extends AbstractWebServiceResource {

    private final IFile file;

    /** 
     * 
     * @since 4.2
     */
    public WorkspaceFileWebServiceResource( final String namespace, final IFile file ) {
        super(namespace,file == null ? null : file.getFullPath().toString() );
        CoreArgCheck.isNotNull(file);
        this.file = file;
    }

    
    /** 
     * @see com.metamatrix.modeler.internal.webservice.AbstractWebServiceResource#exists()
     * @since 4.2
     */
    @Override
    protected boolean exists() {
        return this.file.exists();
    }

    /** 
     * @see com.metamatrix.modeler.internal.webservice.AbstractWebServiceResource#doGetRawInputStream()
     * @since 4.2
     */
    @Override
    protected InputStream doGetRawInputStream() throws Exception {
        return this.file.getContents(true);
    }
    
    
    /** 
     * @see com.metamatrix.modeler.internal.webservice.AbstractWebServiceResource#doGetFile()
     * @since 4.2
     */
    @Override
    protected File doGetFile() {
        final IPath location = this.file.getLocation();
        return location != null ? location.toFile() : null;
    }

}
