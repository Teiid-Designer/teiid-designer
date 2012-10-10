/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.teiid.core.designer.util.CoreArgCheck;


/** 
 * @since 8.0
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
     * @see org.teiid.designer.webservice.AbstractWebServiceResource#exists()
     * @since 4.2
     */
    @Override
    protected boolean exists() {
        return this.file.exists();
    }

    /** 
     * @see org.teiid.designer.webservice.AbstractWebServiceResource#doGetRawInputStream()
     * @since 4.2
     */
    @Override
    protected InputStream doGetRawInputStream() throws Exception {
        return new FileInputStream(this.file);
    }
    
    
    /** 
     * @see org.teiid.designer.webservice.AbstractWebServiceResource#doGetFile()
     * @since 4.2
     */
    @Override
    protected File doGetFile() {
        return this.file;
    }

}
