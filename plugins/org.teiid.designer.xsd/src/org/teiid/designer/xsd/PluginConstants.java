/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd;

import java.util.ResourceBundle;

import org.teiid.core.PluginUtil;
import org.teiid.core.util.PluginUtilImpl;


/**
 * @since 8.0
 */
public interface PluginConstants {

    /**
     * The ID of the plug-in containing this constants class.
     * @since 5.0.2
     */
    String PLUGIN_ID = "org.teiid.designer.xsd"; //$NON-NLS-1$
    
    String PACKAGE_ID = PluginConstants.class.getPackage().getName();
     
    /**
     * Contains private constants used by other constants within this class.
     * @since 5.0.2
     */  
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    }
    
    /**
     * Provides access to the plugin's log and to it's resources.
     * @since 5.0.2
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));
   
}
