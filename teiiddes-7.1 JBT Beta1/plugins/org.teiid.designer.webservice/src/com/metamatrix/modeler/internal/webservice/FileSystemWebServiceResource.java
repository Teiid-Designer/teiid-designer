/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import com.metamatrix.core.util.CoreArgCheck;


/** 
 * @since 4.2
 */
public class FileSystemWebServiceResource extends AbstractWebServiceResource {

    private final File file;
    
    /** 
     * 
     * @since 4.2
     */
    public FileSystemWebServiceResource( final String namespace, final File file ) {
        super(namespace,file == null ? null : file.getAbsolutePath() );
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
        return new FileInputStream(this.file);
    }
    
    
    /** 
     * @see com.metamatrix.modeler.internal.webservice.AbstractWebServiceResource#doGetFile()
     * @since 4.2
     */
    @Override
    protected File doGetFile() {
        return this.file;
    }

}
