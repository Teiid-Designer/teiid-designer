/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war;

/**
 * This class contains constants for working with a WebArchiveBuilder.
 * 
 * @since 7.1
 */
public class WebArchiveBuilderConstants {

    /********** PROPERTIES **********/

    /**
     * Property for setting the WAR file save location. This property can be added to the properties map parameter to the
     * WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_WAR_HOST = "host"; //$NON-NLS-1$

    /**
     * Property for setting the WAR port number. This property can be added to the properties map parameter to the
     * WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_WAR_PORT = "port"; //$NON-NLS-1$

    /**
     * Property for setting the WSDL target namespace. This property can be added to the properties map parameter to the
     * WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_WSDL_TNS = "tns"; //$NON-NLS-1$

    /**
     * Property for setting the WAR host location. This property can be added to the properties map parameter to the
     * WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_WAR_FILE_SAVE_LOCATION = "WARFileSaveLocation"; //$NON-NLS-1$

    /**
     * Property for setting the context name. This property can be added to the properties map parameter to the
     * WebArchiveBuilder.createWebArchive() method.
     */
    public static final String PROPERTY_CONTEXT_NAME = "ContextName"; //$NON-NLS-1$

    /**
     * Property indicating the JNDI name to use for the Teiid web service connection.
     */
    public static final String PROPERTY_JNDI_NAME = "jndiName"; //$NON-NLS-1$

    /**
     * Property indicating the security type to use for the Teiid web service connection.
     */
    public static final String PROPERTY_SECURITY_TYPE = "securityType"; //$NON-NLS-1$

    /**
     * Property indicating the security realm to use for the Teiid web service connection.
     */
    public static final String PROPERTY_SECURITY_REALM = "securityRealm"; //$NON-NLS-1$

    /**
     * Property indicating the security role to use for the Teiid web service connection.
     */
    public static final String PROPERTY_SECURITY_ROLE = "securityRole"; //$NON-NLS-1$

    /**
     * Property indicating the test security username to use for the Teiid web service connection when using WS-Security
     */
    public static final String PROPERTY_SECURITY_USERNAME = "securityUsername"; //$NON-NLS-1$

    /**
     * Property indicating the test security password to use for the Teiid web service connection when using WS-Security
     */
    public static final String PROPERTY_SECURITY_PASSWORD = "securityPassword"; //$NON-NLS-1$

    /**
     * Property indicating the VDB file to include in the WAR file.
     */
    public static final String PROPERTY_VDB_FILE_NAME = "VdbFileName"; //$NON-NLS-1$

    /**
     * Property indicating the web service models contained in the VDB
     */
    public static final String PROPERTY_VDB_WS_MODELS = "WebServiceModels"; //$NON-NLS-1$

    /**
     * Property indicating the REST procedures contained in virtual models of the VDB
     */
    public static final String PROPERTY_VDB_REST_PROCEDURES = "RESTProcedures"; //$NON-NLS-1$

    /********** STATUS CODES **********/

    /**
     * Context name validation failed status code.
     */
    public static final int STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED = 1;

    /**
     * Context name validation succeeded status code.
     */
    public static final int STATUS_CODE_CONTEXT_NAME_VALIDATION_SUCCEEDED = 2;

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
    public static final String BUILD_DIR = "war_build"; //$NON-NLS-1$

    /**
     * Location where the REST WAR template is stored.
     */
    public static final String REST_BUILD_DIR = "rest_war_build"; //$NON-NLS-1$

    /**
     * Name of the template work directory.
     */
    public static final String WORK_DIR = "work"; //$NON-NLS-1$

    /********** WAR Info **********/

    /**
     * Location of WAR file lib directory.
     */
    public static final String WAR_LIB_DIR = "/WEB-INF/lib"; //$NON-NLS-1$

}
