/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.LoggingUtil;

/**
 * Common constants used by this plugin.
 */
public interface UiConstants {

    String PLUGIN_ID = UiConstants.class.getPackage().getName();

    PluginUtil UTIL = new LoggingUtil(PLUGIN_ID);

    interface Images {
        String FOLDER = "icons/"; //$NON-NLS-1$
        String CHECK_MARK = FOLDER + "checkmark.gif"; //$NON-NLS-1$
        String REGISTRY_VIEW = FOLDER + "model-extension-registry-view.png"; //$NON-NLS-1$
    }

}
