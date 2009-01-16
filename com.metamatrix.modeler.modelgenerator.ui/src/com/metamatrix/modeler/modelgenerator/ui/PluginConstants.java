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

package com.metamatrix.modeler.modelgenerator.ui;

import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalOptions;
import com.metamatrix.ui.PreferenceKeyAndDefaultValue;


/**
 * This class is intended for use within this plugin only.
 * @since 4.0
 */
public interface PluginConstants {

    //============================================================================================================================
    // Constants

    public static final String ZERO_LENGTH_STRING = "";      //$NON-NLS-1$
    
    interface Prefs {
        // Model Generation Preferences
        interface ModelGenerator {
            class PC {
                private static final String PREFIX = "modelgenerator.ui.preference."; //$NON-NLS-1$
            }
    
            public static final String RELATIONAL_COLUMN_TYPE    = PC.PREFIX + "relationalColumnType"; //$NON-NLS-1$
            public static final String CLASS_IGNORED_STEREOTYPES = PC.PREFIX + "classIgnoredStereotypes"; //$NON-NLS-1$
            public static final String PACKAGE_USAGE             = PC.PREFIX + "packageUsage"; //$NON-NLS-1$
            public static final String REACHABILITY_CONTRAINT    = PC.PREFIX + "reachabilityConstraint"; //$NON-NLS-1$
            public static final String KEY_COLUMN_BASE_NAME      = PC.PREFIX + "keyColumnBaseName"; //$NON-NLS-1$
            public static final String NUMBER_KEY_COLUMNS        = PC.PREFIX + "numberKeyColumns"; //$NON-NLS-1$
            public static final String PRIMARY_KEY_STEREOTYPES   = PC.PREFIX + "primaryKeyStereotypes"; //$NON-NLS-1$
            public static final String KEY_COLUMN_TYPE           = PC.PREFIX + "keyColumnType"; //$NON-NLS-1$
            public static final String KEY_COLUMN_LENGTH           = PC.PREFIX + "keyColumnLength"; //$NON-NLS-1$
            public static final String CLASS_READONLY_STEREOTYPES = PC.PREFIX + "classReadOnlyStereotypes"; //$NON-NLS-1$
            public static final String DEFAULT_STRING_LENGTH     = PC.PREFIX + "defaultStringLength"; //$NON-NLS-1$
        
            public static final PreferenceKeyAndDefaultValue[] PREFERENCES = 
                    new PreferenceKeyAndDefaultValue[] {
                        new PreferenceKeyAndDefaultValue(RELATIONAL_COLUMN_TYPE,
                                DataTypeManager.DefaultDataTypes.STRING),                                
                        
                        new PreferenceKeyAndDefaultValue(CLASS_IGNORED_STEREOTYPES,
                                ZERO_LENGTH_STRING),
                                
                        new PreferenceKeyAndDefaultValue(PACKAGE_USAGE,
                                new Integer(Uml2RelationalOptions.DEFAULT_PACKAGE_USAGE.getValue())),
                        
                        new PreferenceKeyAndDefaultValue(REACHABILITY_CONTRAINT,
                                new Integer(1)),
                        
                        new PreferenceKeyAndDefaultValue(KEY_COLUMN_BASE_NAME,
                                new String("KeyColumn")), //$NON-NLS-1$
                        
                        new PreferenceKeyAndDefaultValue(NUMBER_KEY_COLUMNS,
                                new Integer(1)),
                                
                        new PreferenceKeyAndDefaultValue(PRIMARY_KEY_STEREOTYPES,
                                ZERO_LENGTH_STRING),
                                
                        new PreferenceKeyAndDefaultValue(KEY_COLUMN_TYPE,
                                DataTypeManager.DefaultDataTypes.LONG),
                                        
                        new PreferenceKeyAndDefaultValue(KEY_COLUMN_LENGTH,
                        		new Integer(1)),
                                                
                        new PreferenceKeyAndDefaultValue(CLASS_READONLY_STEREOTYPES,
                                ZERO_LENGTH_STRING),
                                
                        new PreferenceKeyAndDefaultValue(DEFAULT_STRING_LENGTH,
                                new Integer(1)),
                    };
            
        }
    }
        
}
