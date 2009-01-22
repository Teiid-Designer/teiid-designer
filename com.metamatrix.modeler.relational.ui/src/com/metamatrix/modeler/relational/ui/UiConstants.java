/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relational.ui;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

public interface UiConstants {
	/**
	 * The ID of the plug-in containing this constants class.
	 * @since 4.0
	 */
	String PLUGIN_ID = "com.metamatrix.modeler.relational.ui"; //$NON-NLS-1$
     
	/**
	 * Contains private constants used by other constants within this class.
	 * @since 4.0
	 */  
	class PC {
		private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
	}
    
	/**
	 * Provides access to the plugin's log and to it's resources.
	 * @since 4.0
	 */
	PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));
}
