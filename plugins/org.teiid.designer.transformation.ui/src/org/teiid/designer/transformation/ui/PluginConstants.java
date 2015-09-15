/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui;

import org.eclipse.swt.graphics.RGB;
import org.teiid.designer.ui.common.PreferenceKeyAndDefaultValue;

/**
 * This class is intended for use within this plugin only.
 * 
 * @since 8.0
 */
public interface PluginConstants {
    // ============================================================================================================================
    // Constants

    /**
     * 
     */
    public static final String TRANSFORMATION_DIAGRAM_TYPE_ID = "transformationDiagramType"; //$NON-NLS-1$
    /**
     * 
     */
    public static final String DEPENDENCY_DIAGRAM_TYPE_ID = "dependencyDiagramType"; //$NON-NLS-1$
    /**
     * 
     */
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$
    /**
     * 
     */
    public static final String TRANSFORMATION_EDITOR_ID = "org.teiid.designer.transformation.ui.editors.TransformationObjectEditorPage"; //$NON-NLS-1$

    // ============================================================================================================================
    // Image constants

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 4.0
     */
    interface Images {
        String WARNING_ICON = "icons/full/ovr16/warning_co.gif"; //$NON-NLS-1$
        String ERROR_ICON = "icons/full/ovr16/error_co.gif"; //$NON-NLS-1$
        String TRANSFORMATION_DIAGRAM_ICON = "icons/full/obj16/TransformationDiagram.gif"; //$NON-NLS-1$
        String SYMBOL_ICON = "icons/full/obj16/tn_symbol.gif"; //$NON-NLS-1$
        String FUNCTION_ICON = "icons/full/obj16/tn_function.gif"; //$NON-NLS-1$
        String CONSTANT_ICON = "icons/full/obj16/tn_constant.gif"; //$NON-NLS-1$
        String UNDEFINED_ICON = "icons/full/obj16/tn_undefined.gif"; //$NON-NLS-1$
        String EXPRESSION_BUILDER_ICON = "icons/full/cview20/launch_expression_builder.gif"; //$NON-NLS-1$
        String RECONCILER_ICON = "icons/full/cview16/tb_reconcile_sql.gif"; //$NON-NLS-1$; 
        String TRANSFORMATION_NODE_ICON = "icons/full/cview20/transformation_node.gif"; //$NON-NLS-1$
        String TRANSFORMATION_NODE_ICON_ERROR = "icons/full/cview20/transformation_node_red.gif"; //$NON-NLS-1$
        String ARROW_TRANSFORMATION_NODE_ICON = "icons/full/cview20/arrow_transformation_node.gif"; //$NON-NLS-1$
        String ARROW_TRANSFORMATION_NODE_ICON_ERROR = "icons/full/cview20/arrow_transformation_node_red.gif"; //$NON-NLS-1$
        String XTRANSFORMATION_NODE_ICON = "icons/full/cview20/Xtransformation_node.gif"; //$NON-NLS-1$
        String ARROW_XTRANSFORMATION_NODE_ICON = "icons/full/cview20/arrow_xtransformation_node.gif"; //$NON-NLS-1$
        String REVALIDATE_TRANSFORMATION_ICON = "icons/full/cview16/validate.gif"; //$NON-NLS-1$
        String EDIT_TRANSFORMATION_ICON = "icons/full/cview16/tb_edit_transformation.gif"; //$NON-NLS-1$
        String SEARCH_TRANSFORMATION_ICON = "icons/full/cview16/search_transformation.gif"; //$NON-NLS-1$
        String PREVIEW_VIRTUAL_DATA_ICON = "icons/full/cview16/previewVirtualData.png"; //$NON-NLS-1$
        String CREATE_MATERIALIZED_VIEWS_ICON = "icons/full/cview16/materialized_views.png"; //$NON-NLS-1$
        
        String CHECKED_BOX_ICON = "icons/full/cview16/checked_box.png"; //$NON-NLS-1$
        String UNCHECKED_BOX_ICON = "icons/full/cview16/unchecked_box.png"; //$NON-NLS-1$
        
        String CHANGED_BUTTON_ICON = "icons/full/wizban/change-button.png"; //$NON-NLS-1$
        String CHANGED_BUTTON_DISABLED_ICON = "icons/full/wizban/change-button-disabled.png"; //$NON-NLS-1$
        String CONVERT_BUTTON_ICON = "icons/full/wizban/convert-button.png"; //$NON-NLS-1$
        String CONVERT_BUTTON_DISABLED_ICON = "icons/full/wizban/convert-button-disabled.png"; //$NON-NLS-1$
        String ELIPSIS_BUTTON_ICON = "icons/full/wizban/elipsis-button.png"; //$NON-NLS-1$
        String ELIPSIS_LONG_BUTTON_ICON = "icons/full/wizban/elipsis-long-button.png"; //$NON-NLS-1$
        
        String ARROW_RIGHT_BUTTON_ICON = "icons/full/cview20/arrow-20x20.png"; //$NON-NLS-1$
        String ARROW_RIGHT_BUTTON_DISABLED_ICON = "icons/full/cview20/arrow-20x20-disabled.png"; //$NON-NLS-1$
        String ARROW_LEFT_BUTTON_ICON = "icons/full/cview20/arrow-left-20x20.png"; //$NON-NLS-1$
        String ARROW_LEFT_BUTTON_DISABLED_ICON = "icons/full/cview20/arrow-left-20x20-disabled.png"; //$NON-NLS-1$
        String WARNING_BUTTON_ICON = "icons/full/cview20/warning-20x20.png"; //$NON-NLS-1$
        String ARROW_LEFT_ICON = "icons/full/cview20/arrow-only-left-20x20.png"; //$NON-NLS-1$
        String ARROW_RIGHT_ICON = "icons/full/cview20/arrow-only-20x20.png"; //$NON-NLS-1$
        
        String EXPORT_DDL_ICON = "icons/full/ctool16/exportddl_wiz.gif"; //$NON-NLS-1$
    }

    /**
     *
     */
    interface Prefs {

        // Appearance Preferences
        interface Appearance {
            class PC {
                private static final String PREFIX = "modeler.preference.diagram."; //$NON-NLS-1$
            }

            public static final String TRANSFORM_BKGD_COLOR = PC.PREFIX + "transformation.backgroundcolor"; //$NON-NLS-1$
            public static final String DEPENDENCY_BKGD_COLOR = PC.PREFIX + "dependency.backgroundcolor"; //$NON-NLS-1$

            public static final PreferenceKeyAndDefaultValue[] PREFERENCES = new PreferenceKeyAndDefaultValue[] {
                new PreferenceKeyAndDefaultValue(TRANSFORM_BKGD_COLOR, new RGB(175, 220, 250)),
                new PreferenceKeyAndDefaultValue(DEPENDENCY_BKGD_COLOR, new RGB(175, 220, 235)),};
        }

        // Reconciler Preference
        interface Reconciler {
            class PC {
                private static final String PREFIX = "transformation.ui.preference."; //$NON-NLS-1$
            }

            public static final String SHOW_SQL_DISPLAY = PC.PREFIX + "showSqlDisplay"; //$NON-NLS-1$

            public static final PreferenceKeyAndDefaultValue[] PREFERENCES = new PreferenceKeyAndDefaultValue[] {new PreferenceKeyAndDefaultValue(
                                                                                                                                                  SHOW_SQL_DISPLAY,
                                                                                                                                                  new Boolean(
                                                                                                                                                              true)),};
        }

        // Callback Preference
        interface Callbacks {
            class PC {
                private static final String PREFIX = "transformation.ui.preference."; //$NON-NLS-1$
            }

            public static final String DISABLE_CALLBACKS = PC.PREFIX + "disableCallbacks"; //$NON-NLS-1$

            public static final PreferenceKeyAndDefaultValue[] PREFERENCES = new PreferenceKeyAndDefaultValue[] {new PreferenceKeyAndDefaultValue(
                                                                                                                                                  DISABLE_CALLBACKS,
                                                                                                                                                  new Boolean(
                                                                                                                                                              false)),};
        }

        // TableSupportsUpdateChange Preference
        interface TableSupportsUpdateChange {
            class PC {
                private static final String PREFIX = "transformation.ui.preference."; //$NON-NLS-1$
            }

            public static final String IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_FALSE = PC.PREFIX
                                                                                   + "ignoreTableSupportsUpdateChangedFalse"; //$NON-NLS-1$
            public static final String IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_TRUE = PC.PREFIX
                                                                                  + "ignoreTableSupportsUpdateChangedTrue"; //$NON-NLS-1$

            public static final PreferenceKeyAndDefaultValue[] PREFERENCES = new PreferenceKeyAndDefaultValue[] {
                new PreferenceKeyAndDefaultValue(IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_FALSE, new Boolean(false)),
                new PreferenceKeyAndDefaultValue(IGNORE_TABLE_SUPPORTSUPDATE_CHANGED_TRUE, new Boolean(false)),};
        }
    }

}
