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

package com.metamatrix.query.ui;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.PreferenceKeyAndDefaultValue;

/**
 * UiConstants
 * @since 4.0
 */
public interface UiConstants {
    //============================================================================================================================
    // Constants
    
    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = "com.metamatrix.query.ui"; //$NON-NLS-1$ 

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

    //============================================================================================================================
    // Image constants
        
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images {
        class PC {
            private static final String FULL  = "icons/full/"; //$NON-NLS-1$
//            private static final String CVIEW20 = FULL + "cview20/"; //$NON-NLS-1$
            private static final String CVIEW16 = FULL + "cview16/"; //$NON-NLS-1$
            private static final String OBJ  = FULL + "obj16/"; //$NON-NLS-1$
        }
        
        String CRITERIA_BUILDER  = PC.CVIEW16 + "launch_criteria_builder.gif"; //$NON-NLS-1$
        String EXPRESSION_BUILDER= PC.CVIEW16 + "launch_expression_builder.gif"; //$NON-NLS-1$
        String EXPAND_SELECT     = PC.CVIEW16 + "expand.gif"; //$NON-NLS-1$
        String UP_FONT           = PC.CVIEW16 + "inc_font.gif"; //$NON-NLS-1$
        String DOWN_FONT         = PC.CVIEW16 + "dec_font.gif"; //$NON-NLS-1$
        String VALIDATE          = PC.CVIEW16 + "validate.gif"; //$NON-NLS-1$
        String SHOW_MESSAGES     = PC.CVIEW16 + "show_message_off.gif"; //$NON-NLS-1$
        String SHOW_PREFERENCES  = PC.CVIEW16 + "show_preferences.gif"; //$NON-NLS-1$
        String SHORT_NAMES       = PC.CVIEW16 + "short_names.gif"; //$NON-NLS-1$
        String IMPORT_FROM_FILE  = PC.CVIEW16 + "import_from_file.gif"; //$NON-NLS-1$
        String EXPORT_TO_FILE    = PC.CVIEW16 + "export_to_file.gif"; //$NON-NLS-1$

        // builder icons
        String CONSTANT_LANG_OBJ = PC.OBJ + "constantLangObj.gif"; //$NON-NLS-1$        
        String COMPOUND_CRITERIA_LANG_OBJ = PC.OBJ + "compoundCriteriaLangObj.gif"; //$NON-NLS-1$        
        String FUNCTION_LANG_OBJ = PC.OBJ + "functionLangObj.gif"; //$NON-NLS-1$        
        String PREDICATE_LANG_OBJ = PC.OBJ + "predicateCriteriaLangObj.gif"; //$NON-NLS-1$        
        String REFERENCE_LANG_OBJ = PC.OBJ + "referenceLangObj.gif"; //$NON-NLS-1$        
        String UNDEFINED_LANG_OBJ = PC.OBJ + "undefinedLangObj.gif"; //$NON-NLS-1$
        
        // language object tree provider icons
        String SELECT_ICON = PC.OBJ + "select.gif"; //$NON-NLS-1$  
        String FROM_ICON =   PC.OBJ + "from.gif"; //$NON-NLS-1$  
        String UNION_ICON =  PC.OBJ + "union.gif"; //$NON-NLS-1$  
        String JOIN_ICON =  PC.OBJ + "join.gif"; //$NON-NLS-1$  
        String WHERE_ICON =  PC.OBJ + "where.gif"; //$NON-NLS-1$
        String EXPRESSION_ICON = FUNCTION_LANG_OBJ;
    }
    
    interface Prefs {
        // Preferences private constants
        class PC {
            private static final String PREFIX = "query.ui.preference."; //$NON-NLS-1$
        }
    	
    	public static final String START_CLAUSES_ON_NEW_LINE = PC.PREFIX + 
    			"startClausesOnNewLine"; //$NON-NLS-1$
    	public static final String INDENT_CLAUSE_CONTENT = PC.PREFIX + 
    			"indentClauseContent"; //$NON-NLS-1$
        public static final String SQL_OPTIMIZATION_ON = PC.PREFIX +
                "sqlOptimizationOn"; //$NON-NLS-1$
        public static final String TREE_DIAGRAM_LAYOUT = PC.PREFIX +
                "treeDiagramLayout"; //$NON-NLS-1$
    	
        public static final PreferenceKeyAndDefaultValue[] PREFERENCES = 
        		new PreferenceKeyAndDefaultValue[] {
					new PreferenceKeyAndDefaultValue(START_CLAUSES_ON_NEW_LINE,
        					new Boolean(true)),
					new PreferenceKeyAndDefaultValue(INDENT_CLAUSE_CONTENT,
        					new Boolean(true)),
                    new PreferenceKeyAndDefaultValue(SQL_OPTIMIZATION_ON,
                            new Boolean(true)),
                    new PreferenceKeyAndDefaultValue(TREE_DIAGRAM_LAYOUT,
                            new Boolean(false))
        		};
    }
}
