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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.metamatrix.core.util.ArgCheck;


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
        ArgCheck.isNotNull(file);
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
