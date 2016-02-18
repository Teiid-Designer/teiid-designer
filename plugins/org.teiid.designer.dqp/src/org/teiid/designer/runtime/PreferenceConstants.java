/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;

/**
 * Designer runtime preference names and default values.
 *
 * @since 8.0
 */
public interface PreferenceConstants {

    /**
     * The name of the preference indicating if, during VDB execution, if a data source should be auto-created if it doesn't exist
     * on the current Teiid Instance. This will only happen if the data source name matches the default name.
     */
    String AUTO_CREATE_DATA_SOURCE = PLUGIN_ID + ".preferences.AutoCreateDataSource"; //$NON-NLS-1$

    /**
     * The default value for the {@link #AUTO_CREATE_DATA_SOURCE} preference. Default value is {@value} .
     */
    boolean AUTO_CREATE_DATA_SOURCE_DEFAULT = true;

    /**
     * The name of the preference indicating if preview is enabled.
     */
    String PREVIEW_ENABLED = PLUGIN_ID + ".preferences.PreviewEnabled"; //$NON-NLS-1$

    /**
     * The default value for the {@link #PREVIEW_ENABLED} preference. Default value is {@value} .
     */
    boolean PREVIEW_ENABLED_DEFAULT = true;

    /**
     * The name of the preference indicating Teiid Importer timeout value (in Secs)
     */
    String TEIID_IMPORTER_TIMEOUT_SEC = PLUGIN_ID + ".preferences.TeiidImporterTimeoutSec"; //$NON-NLS-1$

    /**
     * The default value for the {@link #TEIID_IMPORTER_TIMEOUT_SEC} preference. Default value is {@value} .
     */
    int TEIID_IMPORTER_TIMEOUT_SEC_DEFAULT = 120;
    
    /**
     * The Min value for the {@link #TEIID_IMPORTER_TIMEOUT_SEC} preference.
     */
    int TEIID_IMPORTER_TIMEOUT_SEC_MIN = 0;

    /**
     * The Max value for the {@link #TEIID_IMPORTER_TIMEOUT_SEC} preference.
     */
    int TEIID_IMPORTER_TIMEOUT_SEC_MAX = 999;

    /**
     * The name of the preference indicating if Preview VDBs and their associated data sources will be deleted from Teiid
     * instances.
     */
    String PREVIEW_TEIID_CLEANUP_ENABLED = PLUGIN_ID + ".preferences.PreviewTeiidCleanupEnabled"; //$NON-NLS-1$

    /**
     * The default value for the {@link #PREVIEW_TEIID_CLEANUP_ENABLED} preference. Default value is {@value} .
     */
    boolean PREVIEW_TEIID_CLEANUP_ENABLED_DEFAULT = true;

    /**
     * The name of the preference for the timeout value for jboss server requests
     */
    String JBOSS_REQUEST_EXECUTION_TIMEOUT = ".preferences.JbossRequestExecutionTimeout"; //$NON-NLS-1$

    /**
     * The default value for the {@link #JBOSS_REQUEST_EXECUTION_TIMEOUT} preference. Default value is {@value} .
     */
    int JBOSS_REQUEST_EXECUTION_TIMEOUT_SEC_DEFAULT = 10;
    
    /**
     * The name of the preference indicating Teiid Importer timeout value (in Secs)
     */
    String TEIID_SERVER_STARTUP_TIMEOUT_SEC = PLUGIN_ID + ".preferences.TeiidServerStartupTimeoutSec"; //$NON-NLS-1$
    
    /**
     * The default value for the {@link #TEIID_SERVER_STARTUP_TIMEOUT_SEC} preference. Default value is {@value} .
     */
    int TEIID_SERVER_STARTUP_TIMEOUT_SEC_DEFAULT = 120;
    
    /**
     * The Min value for the {@link #TEIID_IMPORTER_TIMEOUT_SEC} preference.
     */
    int TEIID_SERVER_STARTUP_TIMEOUT_SEC_MIN = 0;

    /**
     * The Max value for the {@link #TEIID_IMPORTER_TIMEOUT_SEC} preference.
     */
    int TEIID_SERVER_STARTUP_TIMEOUT_SEC_MAX = 999;
}
