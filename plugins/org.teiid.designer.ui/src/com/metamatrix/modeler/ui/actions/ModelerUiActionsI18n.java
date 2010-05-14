/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;

import org.eclipse.osgi.util.NLS;


/** 
 * @since 4.4
 */
public final class ModelerUiActionsI18n extends NLS {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    //
    // Main Menu
    //
    
    public static String MainMenu_File;
    public static String MainMenu_Edit;
    public static String MainMenu_Search;
    public static String MainMenu_Validate;
    public static String MainMenu_Views;
    public static String MainMenu_Help;
    
    //
    // File Menu
    //
    
    public static String NewMenu;
    public static String CloseAction;
    public static String CloseAllAction;
    public static String PropertiesAction;
    public static String RefreshAction;
    
    //
    // Validate Menu
    //
    
    public static String ValidateAll;
    public static String ValidateChanges;
    public static String ToggleAutoBuild;
    
    //
    // Views Menu
    //
    
    public static String ShowViewMenu;
    
    //
    // Help Menu
    //
    
    public static String SoftwareUpdates;
    
    public static String Diagnostics;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    static {
        final String I18N_NAME = "com.metamatrix.modeler.ui.actions.modelerUiActionsI18n";//$NON-NLS-1$
        
        // load message values from bundle file
        NLS.initializeMessages(I18N_NAME, ModelerUiActionsI18n.class);
    }

}
