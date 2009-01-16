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

import java.io.File;
import java.io.InputStream;

import org.eclipse.core.runtime.IStatus;


/** 
 * This interface is responsible for reading a LDS WAR file and returning the VDB embedded 
 * inside of it.
 * 
 * @since 4.4
 */
public interface WebArchiveReader {

    /**
     * Takes an input web archive and returns the embedded VDB as an input stream.
     *  
     * @param webArchiveFileName
     * @return
     * @throws Exception
     * @since 4.4
     */
    public InputStream getVdb(String webArchiveFileName) throws Exception;
    
    /**
     * Takes an input web archive and returns the embedded VDB as a file.
     *  
     * @param webArchiveFileName
     * @return
     * @throws Exception
     * @since 4.4
     */
    public File getVdbFile(String webArchiveFileName) throws Exception;    
    
    /**
     * Cleans up the work area used for reading a web archive. 
     * @throws Exception
     * @since 4.4
     */
    public IStatus clean() throws Exception;
}
