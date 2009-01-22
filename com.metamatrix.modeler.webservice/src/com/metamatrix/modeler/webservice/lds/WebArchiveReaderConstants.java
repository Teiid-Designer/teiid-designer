/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.lds;

/**
 * This class contains constants for working with a WebArchiveReader.
 * 
 * @since 4.4
 */
public class WebArchiveReaderConstants {
    
    /********** STATUS CODES **********/
    
    /**
     * WAR file creation failed status code.
     */        
    public static final int STATUS_CODE_WAR_FILE_CLEAN_FAILED = 5;
    
    /**
     * WAR file creation succeeded status code.
     */      
    public static final int STATUS_CODE_WAR_FILE_CLEAN_SUCCEEDED = 6;  
    
    /********** LDS INFO **********/

    /**
     * This is the main directory name used for reading a WAR file.
     */
    public static final String LDS_WAR_READER_DIR = "lds_war_reader"; //$NON-NLS-1$
    
    /**
     * This is the work directory name used for reading a WAR file.
     */    
    public static final String LDS_WAR_READER_WORK_DIR = "work"; //$NON-NLS-1$

    /**
     * This the name of the web.xml file in the expanded LDS directory erlative to the root.
     */
    public static final String LDS_WEB_XML_FILE_NAME = "/WEB-INF/web.xml"; //$NON-NLS-1$
    
    /********** DQP INFO **********/

    /**
     * This the name of the DQP JAR file, relative to the root of the WAR file.
     */
    public static final String DQP_JAR_FILE_NAME = "/WEB-INF/lib/dqp.jar"; //$NON-NLS-1$

    /**
     * This is the name of the work driectory that will be used for expanding the DQP JAR file.
     */
    public static final String DQP_JAR_FILE_WORK_DIR = "dqp_jar"; //$NON-NLS-1$
    
    /********** CONTEXT PARAMETERS **********/
    
    /**
     * The name of the VDB Name context parameter in the LDS web.xml file.
     */
    public static final String CONTEXT_PARAM_VDB_NAME = "vdbName"; //$NON-NLS-1$   
}
