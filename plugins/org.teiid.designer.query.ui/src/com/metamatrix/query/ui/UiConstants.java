/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
    String PLUGIN_ID = "org.teiid.designer.query.ui"; //$NON-NLS-1$ 

    
    String PACKAGE_ID = UiConstants.class.getPackage().getName();
     
    /**
     * Contains private constants used by other constants within this class.
     * @since 4.0
     */  
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    }
    
    /**
     * Provides access to the plugin's log and to it's resources.
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

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
//           private static final String CVIEW16 = FULL + "cview16/"; //$NON-NLS-1$
            private static final String OBJ  = FULL + "obj16/"; //$NON-NLS-1$
        }
        
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

        String START_CLAUSES_ON_NEW_LINE = "startClausesOnNewLine"; //$NON-NLS-1$
        String INDENT_CLAUSE_CONTENT = "indentClauseContent"; //$NON-NLS-1$
        String SQL_OPTIMIZATION_ON = "sqlOptimizationOn"; //$NON-NLS-1$
        String TREE_DIAGRAM_LAYOUT = "treeDiagramLayout"; //$NON-NLS-1$
        String AUTO_EXPAND_SELECT = "autoExpandSelect"; //$NON-NLS-1$

        public static final PreferenceKeyAndDefaultValue[] PREFERENCES = new PreferenceKeyAndDefaultValue[] {
            new PreferenceKeyAndDefaultValue(START_CLAUSES_ON_NEW_LINE, new Boolean(true)),
            new PreferenceKeyAndDefaultValue(INDENT_CLAUSE_CONTENT, new Boolean(true)),
            new PreferenceKeyAndDefaultValue(SQL_OPTIMIZATION_ON, new Boolean(true)),
            new PreferenceKeyAndDefaultValue(TREE_DIAGRAM_LAYOUT, new Boolean(false)),
            new PreferenceKeyAndDefaultValue(AUTO_EXPAND_SELECT, new Boolean(false))};
    }
}
