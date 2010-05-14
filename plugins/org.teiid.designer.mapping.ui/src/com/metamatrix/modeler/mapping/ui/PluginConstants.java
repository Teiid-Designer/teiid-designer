/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui;

import org.eclipse.swt.graphics.RGB;
import com.metamatrix.ui.PreferenceKeyAndDefaultValue;

/**
 * PluginConstants
 * This class is intended for use within this plugin only.
 * @since 4.0
 */
public interface PluginConstants {
    //============================================================================================================================
    // Constants

    public static final String MAPPING_DIAGRAM_TYPE_ID                  = "mappingDiagramType";     //$NON-NLS-1$
    public static final String MAPPING_TRANSFORMATION_DIAGRAM_TYPE_ID   = "mappingTransformationDiagramType";         //$NON-NLS-1$
    public static final String RECURSION_EDITOR_ID = "com.metamatrix.modeler.mapping.ui.recursion.RecursionObjectEditorPage"; //$NON-NLS-1$
    public static final String INPUT_SET_EDITOR_ID = "com.metamatrix.modeler.mapping.ui.editor.InputSetObjectEditorPage"; //$NON-NLS-1$

    public static final int COARSE_MAPPING = 0;
    public static final int DETAILED_MAPPING = 1;

    //============================================================================================================================
    // Image constants
    
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images { 
        String WARNING_ICON = "icons/full/ovr16/warning_co.gif"; //$NON-NLS-1$
        String ERROR_ICON = "icons/full/ovr16/error_co.gif"; //$NON-NLS-1$
        String MAPPING_TRANSFORMATION_DIAGRAM_ICON = "icons/full/obj16/TransformationDiagram.gif"; //$NON-NLS-1$
        String MAPPING_DIAGRAM_ICON = "icons/full/obj16/MappingDiagram.gif"; //$NON-NLS-1$
        String STRUCTURE_DIAGRAM_ICON = "icons/full/obj16/XmlStructureDiagram.gif"; //$NON-NLS-1$
        String CLEAR_ICON = "icons/full/clcl16/clear.gif"; //$NON-NLS-1$
        String FILTER_ICON = "icons/full/clcl16/filter.gif"; //$NON-NLS-1$
        String UP_ICON = "icons/full/clcl16/up.gif"; //$NON-NLS-1$
        String DOWN_ICON = "icons/full/clcl16/down.gif"; //$NON-NLS-1$

    }
    
    interface Prefs {
//        class PC {
//            private static final String PREFIX = "modeler.preference.diagram."; //$NON-NLS-1$
//        }

        public static final String AUTO_EXPAND_MAX_MAPPING_CLASSES = "autoExpandMaxMappingClasses"; //$NON-NLS-1$
        public static final String AUTO_EXPAND_TARGET_LEVEL = "autoExpandTargetLevel"; //$NON-NLS-1$
        public static final String FOLD_MAPPING_CLASSES_BY_DEFAULT = "foldMappingClassesByDefault"; //$NON-NLS-1$

        public static final PreferenceKeyAndDefaultValue[] PREFERENCES = 
                new PreferenceKeyAndDefaultValue[] {
                    new PreferenceKeyAndDefaultValue(AUTO_EXPAND_MAX_MAPPING_CLASSES, "21"), //$NON-NLS-1$
                    new PreferenceKeyAndDefaultValue(AUTO_EXPAND_TARGET_LEVEL, "1"), //$NON-NLS-1$

                    // Defect 20604: changing 'fold mapping classes' default setting to false rather than true.
                    //               Also retargeting this defect to 5.0 SP1, so this may change then.
                    new PreferenceKeyAndDefaultValue(FOLD_MAPPING_CLASSES_BY_DEFAULT, new Boolean( false ) ) 
                };
        
        // Appearance Preferences
        interface Appearance {
            class PC {
                private static final String PREFIX = "modeler.preference.diagram."; //$NON-NLS-1$
            }

            public static final String MAPPING_BKGD_COLOR = PC.PREFIX + "mapping.backgroundcolor"; //$NON-NLS-1$
    
            public static final PreferenceKeyAndDefaultValue[] PREFERENCES = 
                    new PreferenceKeyAndDefaultValue[] {
                        new PreferenceKeyAndDefaultValue(MAPPING_BKGD_COLOR,
                                new RGB(175, 220, 250)),
                    };
        }
        
    }
}
