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
import com.metamatrix.ui.UiConstants;

public interface ExtensionUiConstants {
    /**
     * The identifier of the containing plug-in.
     * 
     * @since 4.3
     */

    String PLUGIN_ID = "org.teiid.designer.extension.ui"; //$NON-NLS-1$

    String PACKAGE_ID = ExtensionUiConstants.class.getPackage().getName();

    /**
     * Provides access to the plug-in's log
     * 
     * @since 4.3
     */
    PluginUtil UTIL = new LoggingUtil(PLUGIN_ID);
    
    /**
     * Private constants used by other constants within this class.
     * 
     * @since 4.3
     */
    class PC {

        public static final String ICON_PATH = "icons/full/"; //$NON-NLS-1$

        public static final String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$

        public static final String CTOOL16 = ICON_PATH + "ctool16/"; //$NON-NLS-1$

        public static final String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$

        public static final String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
    }
    
    interface Images extends UiConstants.Images {
    	String EDIT_EXTENSION_PROPERTIES_ICON = CVIEW16 + "edit-extension-properties.png"; //$NON-NLS-1$
    }
}
