/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
        final String BUNDLE_NAME = "com.metamatrix.modeler.ui.actions.modelerUiActionsI18n";//$NON-NLS-1$
        
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, ModelerUiActionsI18n.class);
    }

}
