/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.datatools.connectivity.ui;

import static org.teiid.datatools.connectivity.ui.Activator.PLUGIN_ID;

/**
 * Designer runtime preference names and default values.
 *
 * @since 8.2
 */
public interface PreferenceConstants {

    /**
     * The name of the preference indicating if QueryPlans are to be generated.
     */
    String TEIID_QUERYPLANS_ENABLED = PLUGIN_ID + ".preferences.TeiidQueryPlansEnabled"; //$NON-NLS-1$

    /**
     * The default value for the {@link #TEIID_QUERYPLANS_ENABLED} preference. Default value is {@value} .
     */
    boolean TEIID_QUERYPLANS_ENABLED_DEFAULT = true;

}
