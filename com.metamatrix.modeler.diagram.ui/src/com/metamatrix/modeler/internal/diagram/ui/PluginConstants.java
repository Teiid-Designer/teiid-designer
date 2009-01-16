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

package com.metamatrix.modeler.internal.diagram.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.ui.PreferenceKeyAndDefaultValue;

/**
 * This class is intended for use within this plugin only.
 * @since 4.0
 */
public interface PluginConstants {
    //============================================================================================================================
    // Constants

    public static final String PACKAGE_DIAGRAM_TYPE_ID                  = "packageDiagramType";         //$NON-NLS-1$
    public static final String CUSTOM_DIAGRAM_TYPE_ID                   = "customDiagramType";          //$NON-NLS-1$
    public static final String DEFAULT_DIAGRAM_NOTATION_ID              = "umlDiagramNotation";         //$NON-NLS-1$
    
    interface Prefs {
        // Preferences private constants
        class PC {
            private static final String PREFIX = "modeler.preference.diagram."; //$NON-NLS-1$
        }
        
//        public static final String LINK_ORTHOGONAL = PC.PREFIX + "orthogonalLinks"; //$NON-NLS-1$
        public static final String DIAGRAM_NOTATION = PC.PREFIX + "diagramNotation"; //$NON-NLS-1$
        public static final String DIAGRAM_ROUTER_STYLE = PC.PREFIX + "diagramRouterStyle"; //$NON-NLS-1$
    	public static final String LARGE_MODEL_SIZE = "largeModelSize"; //$NON-NLS-1$
        public static final String SHOW_FK_NAME = "showFkName"; //$NON-NLS-1$
        public static final String SHOW_FK_MULTIPLICITY = "showFkMultiplicity"; //$NON-NLS-1$

        
        public static final PreferenceKeyAndDefaultValue[] PREFERENCES = 
        		new PreferenceKeyAndDefaultValue[] {
//        			new PreferenceKeyAndDefaultValue(LINK_ORTHOGONAL, new Boolean(true)),
        			new PreferenceKeyAndDefaultValue(DIAGRAM_NOTATION,
        					PluginConstants.DEFAULT_DIAGRAM_NOTATION_ID),
                    new PreferenceKeyAndDefaultValue(DIAGRAM_ROUTER_STYLE,
                                                    DiagramUiConstants.LinkRouter.types[DiagramLinkType.ORTHOGONAL]),
                    new PreferenceKeyAndDefaultValue(LARGE_MODEL_SIZE, "20000"), //$NON-NLS-1$
                    new PreferenceKeyAndDefaultValue(SHOW_FK_NAME, new Boolean(true)),
                    new PreferenceKeyAndDefaultValue(SHOW_FK_MULTIPLICITY, new Boolean(true))
        		};
        		
        // Appearance Preferences
        interface Appearance {
            class PC {
                private static final String PREFIX = "modeler.preference.diagram."; //$NON-NLS-1$
            }
            
//            // Grid Preferences
//            public static final String GRID_SPACING = PC.PREFIX + "grid.spacing"; //$NON-NLS-1$
//            public static final String GRID_COLOR = PC.PREFIX + "grid.color"; //$NON-NLS-1$
//            public static final String GRID_VISIBLE = PC.PREFIX + "grid.visible"; //$NON-NLS-1$
//            public static final String GRID_ACTIVATED = PC.PREFIX + "grid.activated"; //$NON-NLS-1$
//            public static final String GRID_TYPE = PC.PREFIX + "grid.type"; //$NON-NLS-1$

            // Font
            public static final String FONT = PC.PREFIX + "font"; //$NON-NLS-1$
            
            // Layout Padding
//            public static final String USER_PREFS_DIAGRAM_LAYOUT_PADDING        = "modeler.preference.diagram.layout.padding";
//            public static final PropertyDefinition USER_PREFS_DIAGRAM_LAYOUT_PADDING_DEFN =
//                    new PropertyDefinitionImpl(USER_PREFS_DIAGRAM_LAYOUT_PADDING,"Layout Padding",PropertyType.INTEGER,MultiplicityPool.ZERO_OR_ONE,"Layout Padding","20",null,null,false,false,false);
    
            // Background Colors
            public static final String CUSTOM_BKGD_COLOR = PC.PREFIX + "custom.backgroundcolor"; //$NON-NLS-1$
            public static final String TRANSFORM_BKGD_COLOR = PC.PREFIX + "transform.backgroundcolor"; //$NON-NLS-1$
            public static final String PACKAGE_BKGD_COLOR = PC.PREFIX + "package.backgroundcolor"; //$NON-NLS-1$
    
    		public static final PreferenceKeyAndDefaultValue[] PREFERENCES = 
    				new PreferenceKeyAndDefaultValue[] {
    					new PreferenceKeyAndDefaultValue(FONT,
    							new Font(null, "Courier New", 8, SWT.NORMAL)),  //$NON-NLS-1$
    					new PreferenceKeyAndDefaultValue(CUSTOM_BKGD_COLOR,
    							new RGB(175, 220, 250)),
    					new PreferenceKeyAndDefaultValue(TRANSFORM_BKGD_COLOR,
    							new RGB(175, 220, 235)),
    					new PreferenceKeyAndDefaultValue(PACKAGE_BKGD_COLOR,
    							new RGB(175, 220, 220))
    				};
        }
        
        // Filter Preferences
        interface Filter {
            class PC {
                private static final String PREFIX = "modeler.diagram.filter."; //$NON-NLS-1$
            }
            
//            public static final String DIAGRAM_FILTER_TOGGLESTATE = PC.PREFIX + "toggleState"; //$NON-NLS-1$

            // Diagram Filter Property Names and Definitions
            public static final String DIAGRAM_HIDE_ALL = PC.PREFIX + "diagram.all"; //$NON-NLS-1$
            public static final String DIAGRAM_HIDE_DEPENDENCIES = PC.PREFIX + "diagram.dependencies"; //$NON-NLS-1$
            public static final String DIAGRAM_HIDE_TRANSFORMATIONS = PC.PREFIX + "diagram.transformations"; //$NON-NLS-1$
            public static final String DIAGRAM_HIDE_NOTES = PC.PREFIX + "diagram.notes"; //$NON-NLS-1$

            // Diagram Group Filter Preference Names
            public static final String GROUP_HIDE_GROUPS = PC.PREFIX + "group.groups"; //$NON-NLS-1$
            public static final String GROUP_HIDE_ATTRIBUTES = PC.PREFIX + "group.attributes"; //$NON-NLS-1$
            public static final String GROUP_HIDE_OPERATIONS = PC.PREFIX + "group.operations"; //$NON-NLS-1$
            public static final String GROUP_HIDE_KEYS = PC.PREFIX + "group.keys"; //$NON-NLS-1$
            public static final String GROUP_HIDE_INDEXES = PC.PREFIX + "group.indexes"; //$NON-NLS-1$
            public static final String GROUP_HIDE_LOCATION = PC.PREFIX + "group.location"; //$NON-NLS-1$
            public static final String GROUP_HIDE_STEREOTYPE = PC.PREFIX + "group.stereotype"; //$NON-NLS-1$
            
            // Diagram Package Filter Property Names 
            public static final String PACKAGE_HIDE_LOCATION = PC.PREFIX + "package.location"; //$NON-NLS-1$
            public static final String PACKAGE_HIDE_STEREOTYPE = PC.PREFIX + "package.stereotype"; //$NON-NLS-1$

            // Diagram Attribute Filter Property Names 
            public static final String ATTRIBUTE_HIDE_RETURNTYPE = PC.PREFIX + "attribute.returnType"; //$NON-NLS-1$
            public static final String ATTRIBUTE_HIDE_VISIBILITY = PC.PREFIX + "attribute.visibility"; //$NON-NLS-1$

            // Diagram Operation Filter Property Names 
            public static final String OPERATION_HIDE_RETURNTYPE = PC.PREFIX + "operation.returnType"; //$NON-NLS-1$
            public static final String OPERATION_HIDE_PARAMETERS = PC.PREFIX + "operation.parameterList"; //$NON-NLS-1$
            public static final String OPERATION_HIDE_VISIBILITY = PC.PREFIX + "operation.visibility"; //$NON-NLS-1$

            // Diagram Association Filter Property Names
            public static final String ASSOCIATION_HIDE_LABEL = PC.PREFIX + "association.label"; //$NON-NLS-1$
            public static final String ASSOCIATION_HIDE_ROLENAMES = PC.PREFIX + "association.roleNames"; //$NON-NLS-1$
            public static final String ASSOCIATION_HIDE_MULTIPLICITY = PC.PREFIX + "association.multiplicity"; //$NON-NLS-1$
            
            public static final PreferenceKeyAndDefaultValue[] PREFERENCES =
            		new PreferenceKeyAndDefaultValue[] {
            			new PreferenceKeyAndDefaultValue(DIAGRAM_HIDE_ALL,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(DIAGRAM_HIDE_DEPENDENCIES,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(DIAGRAM_HIDE_TRANSFORMATIONS,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(DIAGRAM_HIDE_NOTES,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(GROUP_HIDE_GROUPS,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(GROUP_HIDE_ATTRIBUTES,
            				new Boolean(true)),
            			new PreferenceKeyAndDefaultValue(GROUP_HIDE_OPERATIONS,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(GROUP_HIDE_KEYS,
            				new Boolean(true)),
            			new PreferenceKeyAndDefaultValue(GROUP_HIDE_INDEXES,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(GROUP_HIDE_LOCATION,
            				new Boolean(true)),
            			new PreferenceKeyAndDefaultValue(GROUP_HIDE_STEREOTYPE,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(PACKAGE_HIDE_LOCATION,
            				new Boolean(true)),
            			new PreferenceKeyAndDefaultValue(PACKAGE_HIDE_STEREOTYPE,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(ATTRIBUTE_HIDE_RETURNTYPE,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(ATTRIBUTE_HIDE_VISIBILITY,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(OPERATION_HIDE_RETURNTYPE,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(OPERATION_HIDE_PARAMETERS,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(OPERATION_HIDE_VISIBILITY,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(ASSOCIATION_HIDE_LABEL,
            				new Boolean(false)),
            			new PreferenceKeyAndDefaultValue(ASSOCIATION_HIDE_ROLENAMES,
            				new Boolean(true)),
            			new PreferenceKeyAndDefaultValue(ASSOCIATION_HIDE_MULTIPLICITY,
            				new Boolean(false))
            		};
        }
        
        // Print Preferences
        interface Print {
            class PC {
                private static final String PREFIX = "modeler.diagram.print."; //$NON-NLS-1$
            }

            // Diagram Printing Property Names and Definitions
            public static final String PORTRAIT = PC.PREFIX + "diagram.print.portrait"; //$NON-NLS-1$
            public static final String LANDSCAPE = PC.PREFIX + "diagram.print.landscape"; //$NON-NLS-1$
            public static final String FIT_TO_ONE_PAGE = PC.PREFIX + "diagram.print.fitToOnePage"; //$NON-NLS-1$
            public static final String FIT_TO_ONE_PAGE_HIGH = PC.PREFIX + "diagram.print.fitToOnePageHigh"; //$NON-NLS-1$
            public static final String FIT_TO_ONE_PAGE_WIDE = PC.PREFIX + "diagram.print.fitToOnePageWide"; //$NON-NLS-1$
            public static final String ADJUST_TO_PERCENT = PC.PREFIX + "diagram.print.adjustToPercent"; //$NON-NLS-1$
            public static final String SCALING_PERCENTAGE = PC.PREFIX + "diagram.print.scalingPercent"; //$NON-NLS-1$
            public static final String TOP_MARGIN = PC.PREFIX + "diagram.print.top"; //$NON-NLS-1$
            public static final String RIGHT_MARGIN = PC.PREFIX + "diagram.print.right"; //$NON-NLS-1$
            public static final String BOTTOM_MARGIN = PC.PREFIX + "diagram.print.bottom"; //$NON-NLS-1$
            public static final String LEFT_MARGIN = PC.PREFIX + "diagram.print.left"; //$NON-NLS-1$
            public static final String OVER_THEN_DOWN = PC.PREFIX + "diagram.print.overThenDown"; //$NON-NLS-1$
            public static final String DOWN_THEN_OVER = PC.PREFIX + "diagram.print.downThenOver"; //$NON-NLS-1$

            
            public static final PreferenceKeyAndDefaultValue[] PREFERENCES =
                    new PreferenceKeyAndDefaultValue[] {
                        new PreferenceKeyAndDefaultValue(PORTRAIT,
                            new Boolean(false)),
                        new PreferenceKeyAndDefaultValue(LANDSCAPE,
                            new Boolean(true)),
                        new PreferenceKeyAndDefaultValue(ADJUST_TO_PERCENT,
                            new Boolean(true)),
                        new PreferenceKeyAndDefaultValue(SCALING_PERCENTAGE,
                            new Integer( 100 )),
                        new PreferenceKeyAndDefaultValue(FIT_TO_ONE_PAGE,
                            new Boolean(false)),
                        new PreferenceKeyAndDefaultValue(FIT_TO_ONE_PAGE_HIGH,
                            new Boolean(false)),
                        new PreferenceKeyAndDefaultValue(FIT_TO_ONE_PAGE_WIDE,
                            new Boolean(false)),
                        new PreferenceKeyAndDefaultValue(TOP_MARGIN,
                            new Double( 0.0 )),
                        new PreferenceKeyAndDefaultValue(RIGHT_MARGIN,
                            new Double( 0.0 )),
                        new PreferenceKeyAndDefaultValue(BOTTOM_MARGIN,
                            new Double( 0.0 )),
                        new PreferenceKeyAndDefaultValue(LEFT_MARGIN,
                            new Double( 0.0 )),
                        new PreferenceKeyAndDefaultValue(OVER_THEN_DOWN,
                            new Boolean(true)),
                        new PreferenceKeyAndDefaultValue(DOWN_THEN_OVER,
                            new Boolean(false))                         
                    };
        }        
        
        
    }
    
    //============================================================================================================================
    // Image constants
    
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images { 
        String OVERVIEW_ICON = "icons/full/cview16/Overview.gif"; //$NON-NLS-1$
        String EDITOR_ICON = "icons/full/cview16/Diagram.gif"; //$NON-NLS-1$
        String PRINT_ACTION_ICON = "icons/full/cview16/print_edit.gif"; //$NON-NLS-1$
        String WARNING_ICON = "icons/full/ovr16/warning_co.gif"; //$NON-NLS-1$
        String ERROR_ICON = "icons/full/ovr16/error_co.gif"; //$NON-NLS-1$
        String PACKAGE_DIAGRAM_ICON = "icons/full/cview16/Diagram.gif"; //$NON-NLS-1$
        String CUSTOM_DIAGRAM_ICON = "icons/full/cview16/custom_diagram.gif"; //$NON-NLS-1$
        String EDIT_MODEL_OBJECT_ICON = "icons/full/cview16/edit_model_object.gif"; //$NON-NLS-1$
        String ENUM_ICON = "icons/full/ovr16/enum_co.gif"; //$NON-NLS-1$
    }
}
