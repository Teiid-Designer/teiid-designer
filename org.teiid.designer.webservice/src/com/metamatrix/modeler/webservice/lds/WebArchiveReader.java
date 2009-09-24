/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
