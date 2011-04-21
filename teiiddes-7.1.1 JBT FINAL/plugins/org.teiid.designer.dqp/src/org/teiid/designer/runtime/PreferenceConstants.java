/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.PLUGIN_ID;

/**
 * Designer runtime preference names and default values.
 */
public interface PreferenceConstants {

    /**
     * The name of the preference indicating if preview is enabled.
     */
    String PREVIEW_ENABLED = PLUGIN_ID + ".preferences.PreviewEnabled"; //$NON-NLS-1$

    /**
     * The default value for the {@link #PREVIEW_ENABLED} preference. Default value is {@value} .
     */
    boolean PREVIEW_ENABLED_DEFAULT = true;

    /**
     * The name of the preference indicating if Preview VDBs and their associated data sources will be deleted from Teiid
     * instances.
     */
    String PREVIEW_TEIID_CLEANUP_ENABLED = PLUGIN_ID + ".preferences.PreviewTeiidCleanupEnabled"; //$NON-NLS-1$

    /**
     * The default value for the {@link #PREVIEW_TEIID_CLEANUP_ENABLED} preference. Default value is {@value} .
     */
    boolean PREVIEW_TEIID_CLEANUP_ENABLED_DEFAULT = true;

}
