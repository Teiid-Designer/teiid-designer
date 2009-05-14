/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.war.wizards;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.webservice.ui.IUiConstants;

/**
 * @since 4.0
 */
public interface InternalModelerLdsUiConstants{
    //==================
    // Constants
    //==================
    
    /**
     * The identifier of the containing plugin.
     * @since 4.3
     */
	
	String PLUGIN_ID = "org.teiid.designer.webservice.ui"; //$NON-NLS-1$
	
    String PACKAGE_ID = IUiConstants.class.getPackage().getName();
    
    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * @since 4.3
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));
    
    /**
     * Contains private constants used by other constants within this class.
     * @since 4.0
     */
    class PC {
        public static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
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
     * @since 4.0
     */
    interface WebServicesImages {                       
        String WAR_FILE_ICON = "icons/full/wizban/warFile.jpg"; //$NON-NLS-1$
    }
    
    /**
     * status code for opening connector binding editor
     */
    public static int OPEN_BINDING_EDITOR = 55;
    
    public static int LEAVE_EDITOR_ALONE = 5;
    
    
    public static int VALIDATEVDBEDITINGCONTEXT = 0;
    public static int VALIDATECONTEXT           = 1;
    public static int VALIDATELOGFILE           = 2;
    public static int VALIDATEWARFILE           = 3;
    public static int VALIDATELLICENSEFILE      = 4;                           
    public static int VALIDATEVDBFILENAME       = 5;
}
