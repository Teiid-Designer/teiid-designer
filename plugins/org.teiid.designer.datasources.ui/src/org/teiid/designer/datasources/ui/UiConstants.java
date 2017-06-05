/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui;

import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.LoggingUtil;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;


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
    String CONNECTION_URL_DISPLAYNAME = "connection-url";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String CONNECTION_ENDPOINT_DISPLAYNAME = "EndPoint";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String USERNAME_PROP_DISPLAYNAME = "user-name";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String PASSWORD_PROP_DISPLAYNAME = "password";  //$NON-NLS-1$

    @SuppressWarnings( "javadoc" )
    String PASSWORD_PROP_NAME = "password";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String AUTH_PASSWORD_PROP_NAME = "AuthPassword";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String RECOVERY_PASSWORD_PROP_NAME = "recovery-password";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String LDAP_PASSWORD_PROP_NAME = "LdapAdminUserPassword";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String DATABASE_CONNECTIONS = "Database Connections";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String ODA_CONNECTIONS = "ODA Data Sources";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String TEIID_CONNECTIONS = "Teiid Importer Connections";  //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String FLAT_FILE_DATA_SOURCE = "Flat File Data Source"; //$NON-NLS-1$
    @SuppressWarnings( "javadoc" )
    String CONNECTION_PROFILE_REPOSITORIES = "Connection Profile Repositories";  //$NON-NLS-1$
    
    String JDBC_PROFILE_CATEGORY_ID = "org.eclipse.datatools.connectivity.db.category";  //$NON-NLS-1$
    String JDBC_PROFILE_CATEGORY_ID_PREFIX = "org.eclipse.datatools.connectivity.db";  //$NON-NLS-1$
    String ODA_PROFILE_CATEGORY_ID = "org.eclipse.datatools.connectivity.oda.profileCategory";  //$NON-NLS-1$
    String ODA_PROFILE_CATEGORY_ID_PREFIX = "org.eclipse.datatools.connectivity.oda";  //$NON-NLS-1$
    String TEIID_PROFILE_CATEGORY_ID = "org.teiid.designer.import.category";  //$NON-NLS-1$
    String TEIID_PROFILE_CATEGORY_ID_PREFIX = "org.teiid.designer.import";  //$NON-NLS-1$
    	
    
	/**
	 * Private constants used by other constants within this class.
	 * 
	 * @since 4.3
	 */
	interface PC {
	    String I18N_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
	
	    String ICON_PATH = "icons/full/"; //$NON-NLS-1$
	
	    String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$
	
	    String CTOOL16 = ICON_PATH + "ctool16/"; //$NON-NLS-1$
	
	    String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$
	
	    String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
	}
	
    @SuppressWarnings( "javadoc" )
    interface IMAGES {
        
        String RESET_PROPERTY = PC.CVIEW16 + "restore-default-value.png";  //$NON-NLS-1$
        String ADD_PROPERTY = PC.CVIEW16 + "add_property.png";  //$NON-NLS-1$
        String REMOVE_PROPERTY = PC.CVIEW16 + "remove_property.png";  //$NON-NLS-1$
        
        String JDBC_SOURCE = PC.CVIEW16 + "jdbc_16.gif"; //$NON-NLS-1$
        String TEIID_JDBC_SOURCE = PC.CVIEW16 + "jdbc-source.gif"; //$NON-NLS-1$
        String TEIID_SERVER = PC.CVIEW16 + "teiid-server.png"; //$NON-NLS-1$
        String CONNECTION = PC.CVIEW16 + "connection.gif"; //$NON-NLS-1$
        String PROFILES = PC.CVIEW16 + "profiles.gif"; //$NON-NLS-1$
        
        String ADD_CONNECTION = PC.CVIEW16 + "add-connection.png";  //$NON-NLS-1$
        String EDIT_CONNECTION = PC.CVIEW16 + "edit-connection.png";  //$NON-NLS-1$
        String REMOVE_CONNECTION = PC.CVIEW16 + "remove-connection.png";  //$NON-NLS-1$
        String REFRESH = PC.CVIEW16 + "refresh.png";  //$NON-NLS-1$
        String GENERATE_SOURCE_MODEL = PC.CVIEW16 + "generate-source-model.png";  //$NON-NLS-1$
        String CREATE_DATA_SOURCE = PC.CVIEW16 + "create-data-source.png";  //$NON-NLS-1$
        String LAUNCH_RUN = PC.CVIEW16 + "launch_run.gif";  //$NON-NLS-1$
        String LAUNCH_RUN_DISABLED = PC.CVIEW16 + "launch_run_disabled.gif";  //$NON-NLS-1$
        String LAUNCH_STOP = PC.CVIEW16 + "launch_stop.gif";  //$NON-NLS-1$
        String LAUNCH_STOP_DISABLED = PC.CVIEW16 + "launch_stop_disabled.gif";  //$NON-NLS-1$
    }
    
    interface JdbcTranslatorKeys {
    	String TABLE_TYPES = "importer.TableTypes";
    	String USE_QUALIFIED_NAME = "importer.UseQualifiedName";
    }
    
    interface JdbcTranslatorDefaults {
    	String TABLE_TYPES = "TABLE";
    	String USE_QUALIFIED_NAME = "false";
    }
    
	/**
	 * Constants related to extensions, including all extension ID's.
	 * s
	 * @since 4.0
	 */
	interface Extensions {
	    // Perspectives
	
	    // content supplier's IDs must match their class name
	    String DATASOURCES_VIEW_ID = PLUGIN_ID + ".sources.DataSourcesViewID"; //$NON-NLS-1$
	}
}
