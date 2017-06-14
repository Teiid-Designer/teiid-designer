/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dataservices.ui;

import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.LoggingUtil;


/**
 * UiConstants
 * 
 * @since 8.1
 */
public interface UiConstants {

    @SuppressWarnings( "javadoc" )
    String PLUGIN_ID = UiConstants.class.getPackage().getName();

    @SuppressWarnings( "javadoc" )
    PluginUtil UTIL = new LoggingUtil(PLUGIN_ID);

    @SuppressWarnings( "javadoc" )
    public static final String CONNECTION_URL_DISPLAYNAME = "connection-url";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String CONNECTION_ENDPOINT_DISPLAYNAME = "EndPoint";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String USERNAME_PROP_DISPLAYNAME = "user-name";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String PASSWORD_PROP_DISPLAYNAME = "password";  //$NON-NLS-1$

    @SuppressWarnings( "javadoc" )
    public static final String PASSWORD_PROP_NAME = "password";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String AUTH_PASSWORD_PROP_NAME = "AuthPassword";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String RECOVERY_PASSWORD_PROP_NAME = "recovery-password";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String LDAP_PASSWORD_PROP_NAME = "LdapAdminUserPassword";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String DATABASE_CONNECTIONS = "Database Connections";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String ODA_CONNECTIONS = "ODA Data Sources";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    public static final String TEIID_CONNECTIONS = "Teiid Importer Connections";  //$NON-NLS-1$
    
    public static final String DS_EDITOR_ID = "org.teiid.designer.dataservices.ui.editor.DataServiceProjectEditorID"; //$NON-NLS-1$
    
    @SuppressWarnings( "javadoc" )
    interface ImageIds {
        String FOLDER = "icons/"; //$NON-NLS-1$
        
        String RESET_PROPERTY = FOLDER + "restore-default-value.png";  //$NON-NLS-1$
        String ADD_PROPERTY = FOLDER + "add_property.png";  //$NON-NLS-1$
        String REMOVE_PROPERTY = FOLDER + "remove_property.png";  //$NON-NLS-1$
        
        String JDBC_SOURCE = FOLDER + "jdbc_16.gif"; //$NON-NLS-1$
        String TEIID_JDBC_SOURCE = FOLDER + "jdbc-source.gif"; //$NON-NLS-1$
        String TEIID_SERVER = FOLDER + "teiid-server.png"; //$NON-NLS-1$
        String CONNECTION = FOLDER + "connection.gif"; //$NON-NLS-1$
        String PROFILES = FOLDER + "profiles.gif"; //$NON-NLS-1$
    }
    
    interface JdbcTranslatorKeys {
    	String TABLE_TYPES = "importer.TableTypes";
    	String USE_QUALIFIED_NAME = "importer.UseQualifiedName";
    }
    
    interface JdbcTranslatorDefaults {
    	String TABLE_TYPES = "TABLE";
    	String USE_QUALIFIED_NAME = "false";
    }
}
