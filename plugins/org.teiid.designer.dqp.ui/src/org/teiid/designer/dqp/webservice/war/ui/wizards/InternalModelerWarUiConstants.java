/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war.ui.wizards;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * @since 7.1
 */
public interface InternalModelerWarUiConstants {
    // ==================
    // Constants
    // ==================

    /**
     * The identifier of the containing plugin.
     * 
     * @since 7.1
     */
    String PLUGIN_ID = InternalModelerWarUiConstants.class.getPackage().getName();

    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * 
     * @since 7.1
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 7.1
     */
    class PC {
        public static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    }

    public final static int NONE = 0;

    /**
     * Constant for an info message (value 1).
     */
    public final static int INFORMATION = 1;

    /**
     * Constant for a warning message (value 2).
     */
    public final static int WARNING = 2;

    /**
     * Constant for an error message (value 3).
     */
    public final static int ERROR = 3;

    public static final String CLOSE = "Close"; //$NON-NLS-1$

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 7.1
     */
    interface WebServicesImages {
        String WAR_FILE_ICON = "icons/full/wizban/warFile.jpg"; //$NON-NLS-1$
    }

    /**
     * status code for opening connector binding editor
     */
    public static int OPEN_BINDING_EDITOR = 55;

    public static int LEAVE_EDITOR_ALONE = 5;

    public static int VALIDATETNS = 0;
    public static int VALIDATECONTEXT = 1;
    public static int VALIDATEWARFILE = 2;
    public static int VALIDATEHOST = 3;
    public static int VALIDATEPORT = 4;
    public static int VALIDATEJNDI = 5;
}
