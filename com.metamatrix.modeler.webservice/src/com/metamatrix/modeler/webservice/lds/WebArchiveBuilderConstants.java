/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.lds;


/**
 * This class contains constants for working with a WebArchiveBuilder.
 * 
 * @since 4.4
 */
public class WebArchiveBuilderConstants {
    
    /********** PROPERTIES **********/ 

    /**
     * Property for setting the WAR file save location. This property can be added to the properties map parameter to the
     * WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_WAR_FILE_SAVE_LOCATION = "WARFileSaveLocation"; //$NON-NLS-1$

    /**
     * Property for setting the license file path. This property can be added to the properties map parameter to the
     * WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_LICENSE_FILE_PATH = "LicenseFilePath"; //$NON-NLS-1$

    /**
     * Property for setting the context name. This property can be added to the properties map parameter to the
     * WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_CONTEXT_NAME = "ContextName"; //$NON-NLS-1$

    /**
     * Property for setting the log file path. This property can be added to the properties map parameter to the
     * WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_LOG_FILE_PATH = "LogFilePath"; //$NON-NLS-1$
    
    /**
     * Property indicating the VDB file to include in the WAR file.
     */
    public static final String PROPERTY_VDB_FILE_NAME = "VdbFileName"; //$NON-NLS-1$
    
    
    /**
     * Property whose value is a Properties object (or null) that contains all additional properties to be put on the connection
     * URL for any connections to the Query engine made from the WAR file.  This is specifically used to set the txnAutoWrap
     * property to disable transactions in WAR files that allow updates.   This property can be added to the properties map
     * parameter to the WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_ADDITIONAL_PROPERTIES = "AdditionalProperties"; //$NON-NLS-1$

    
    /********** STATUS CODES **********/ 
    
    /**
     * Context name valdiation failed status code.
     */
    public static final int STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED = 1;
    
    /**
     * Context name valdiation succeeded status code.
     */    
    public static final int STATUS_CODE_CONTEXT_NAME_VALIDATION_SUCCEEDED = 2;
    
    /**
     * License file valdiation failed status code.
     */    
    public static final int STATUS_CODE_LICENSE_FILE_VALIDATION_FAILED = 3;
    
    /**
     * License file valdiation succeeded status code.
     */        
    public static final int STATUS_CODE_LICENSE_FILE_VALIDATION_SUCCEEDED = 4;
    
    /**
     * WAR file creation failed status code.
     */        
    public static final int STATUS_CODE_WAR_FILE_CREATION_FAILED = 5;
    
    /**
     * WAR file creation succeeded status code.
     */      
    public static final int STATUS_CODE_WAR_FILE_CREATION_SUCCEEDED = 6;  
    
    /********** TEMPLATE INFO **********/ 

    /**
     * Location where the WAR template is stored.
     */
    public static final String BUILD_DIR = "lds_war_build"; //$NON-NLS-1$
    
    /**
     * Name of the template work directory.
     */
    public static final String WORK_DIR = "work"; //$NON-NLS-1$
    
    /********** LICENSE INFO **********/ 
    
    /**
     * Name of MetaMatrix license file.
     */
    public static final String LICENSE_NAME = "MetaMatrixLicense.xml"; //$NON-NLS-1$
    
    /**
     * Name of the MetaMatrix License certificate file to be included in the WAR file.
     */
    public static final String LICENSE_CERT_NAME = "metamatrix.cert"; //$NON-NLS-1$
    
    /**
     * Name of MetaMatrix license JAR file.
     */
    public static final String LICENSE_JAR_NAME = "license.jar"; //$NON-NLS-1$
    
    /********** WAR Info **********/
    
    /**
     * Location of WAR file lib directory.
     */
    public static final String WAR_LIB_DIR = "/WEB-INF/lib"; //$NON-NLS-1$
    
    /********** Embedded Info **********/
    
    /**
     * Location of Embedded directory.
     */
    public static final String EMBEDDED_DIR = "/embedded"; //$NON-NLS-1$
    
    /**
     * Location of Embedded config directory.
     */    
    public static final String EMBEDDED_CONFIG_DIR = "/config"; //$NON-NLS-1$
    
    /**
     * Location of Embedded extensions directory.
     */
    public static final String EMBEDDED_EXTENSIONS_DIR = "/extensions"; //$NON-NLS-1$
    
    /**
     * Location of Embedded JDBC directory.
     */
    public static final String EMBEDDED_JDBC_DIR = "/jdbc"; //$NON-NLS-1$
    
    /**
     * Location of Embedded lib directory.
     */
    public static final String EMBEDDED_LIB_DIR = "/lib"; //$NON-NLS-1$
    
    /**
     * Location of Embedded license directory.
     */
    public static final String EMBEDDED_LICENSE_DIR = "/license"; //$NON-NLS-1$
    
    /**
     * Location of Embedded logs directory.
     */
    public static final String EMBEDDED_LOGS_DIR = "/logs"; //$NON-NLS-1$
    
    /**
     * Name of Embedded JAR file.
     */
    public static final String EMBEDDED_JAR_FILE_NAME = "embedded.jar"; //$NON-NLS-1$  
    
    /**
     * Name of the Embedded properties file.
     */
    public static final String EMBEDDED_PROPERTIES_FILE_NAME = "embedded.properties"; //$NON-NLS-1$  
    
    /********** External Info **********/
    public static final String EXTERNAL_JAR_FILE_NAME = "external.jar"; //$NON-NLS-1$ 
    
    public static final String EXTERNAL_JARS_DIR_NAME = "externalJars"; //$NON-NLS-1$ 

}
