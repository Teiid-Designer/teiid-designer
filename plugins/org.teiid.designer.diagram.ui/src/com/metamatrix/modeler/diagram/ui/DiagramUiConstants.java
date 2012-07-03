/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui;

import java.util.ResourceBundle;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * DiagramUiConstants
 * 
 * @since 4.0
 */
public interface DiagramUiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.designer.diagram.ui"; //$NON-NLS-1$

    String PACKAGE_ID = DiagramUiConstants.class.getPackage().getName();

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    }

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

    public static final int NO_ERRORS = 0;
    public static final int HAS_WARNING = 1;
    public static final int HAS_ERROR = 2;

    // ============================================================================================================================
    // Extension constants

    /**
     * Constants related to extensions, including all extension ID's.
     * 
     * @since 4.0
     */
    interface Extensions {
        String DIAGRAM_EDITOR = "diagramEditorPage"; //$NON-NLS-1$
        String UML_NOTATION = "umlDiagramNotation"; //$NON-NLS-1$
        String TRANSFORMATION_DIAGRAM = "transformDiagramType"; //$NON-NLS-1$
        String PACKAGE_DIAGRAM = "packageDiagramType"; //$NON-NLS-1$
        String MAPPING_DIAGRAM = "mappingDiagramType"; //$NON-NLS-1$
        String DEPENDENCY_DIAGRAM = "dependencyDiagramType"; //$NON-NLS-1$
    }

    /**
     * Constants related to extension points, including all extension point ID's and extension point schema component names.
     * 
     * @since 4.0
     */
    interface ExtensionPoints {

        interface DiagramNotation {
            String ID = "diagramNotation"; //$NON-NLS-1$
            String EDIT_PART_GENERATOR_ELEMENT = "editPartGenerator"; //$NON-NLS-1$
            String DIAGRAM_MODEL_GENERATOR_ELEMENT = "diagramModelGenerator"; //$NON-NLS-1$
            String FIGURE_GENERATOR_ELEMENT = "figureGenerator"; //$NON-NLS-1$
            String CLASS_NAME = "class"; //$NON-NLS-1$
            String NOTATION_PREFERENCES = "notationPreferences"; //$NON-NLS-1$
            String DISPLAY_NAME = "displayName"; //$NON-NLS-1$
        }

        interface DiagramType {
            String ID = "diagramType"; //$NON-NLS-1$
            String DIAGRAM_TYPE_ELEMENT = "diagram"; //$NON-NLS-1$
            String CLASS_NAME = "class"; //$NON-NLS-1$
            String NAME = "names"; //$NON-NLS-1$
            String ID_ATTR = "id"; //$NON-NLS-1$
            String SIMPLE_DIAGRAM = "simpleDiagram"; //$NON-NLS-1$
            String DEPRECATED_DIAGRAM = "deprecated"; //$NON-NLS-1$
        }
    }

    /**
     * Constants related to properties on a Diagram Model Node
     * 
     * @since 4.0
     */
    interface DiagramNodeProperties {

        String LOCATION = "location"; //$NON-NLS-1$
        String SIZE = "size"; //$NON-NLS-1$
        String CHILDREN = "children"; //$NON-NLS-1$
        String PROPERTIES = "prop"; //$NON-NLS-1$
        String NAME = "name"; //$NON-NLS-1$
        String CONNECTION = "connection"; //$NON-NLS-1$
        String IMAGES = "image"; //$NON-NLS-1$
        String ERRORS = "errors"; //$NON-NLS-1$
        String BUTTONS = "buttons"; //$NON-NLS-1$
        String LAYOUT = "layout"; //$NON-NLS-1$
        String BENDPOINT = "bendpoint"; //$NON-NLS-1$
        String ROUTER = "router"; //$NON-NLS-1$
        String SUBSCRIPT = "subscript"; //$NON-NLS-1$
        String PATH = "path"; //$NON-NLS-1$
        String RENAME = "rename"; //$NON-NLS-1$
        String EXPAND = "expand"; //$NON-NLS-1$
        String COLLAPSE = "collapse"; //$NON-NLS-1$
    }

    /**
     * Constants related to properties on a Diagram Model Node
     * 
     * @since 4.0
     */
    interface Errors {

        String MODEL_NODE_FAILURE = "DiagramErrors.modelNodeFailure"; //$NON-NLS-1$
        String EDIT_PART_FAILURE = "DiagramErrors.editPartFailure"; //$NON-NLS-1$
        String FIGURE_GENERATOR_FAILURE = "DiagramErrors.figureGeneratorFailure"; //$NON-NLS-1$
        String PART_GENERATOR_FAILURE = "DiagramErrors.partGeneratorFailure"; //$NON-NLS-1$
        String MODEL_GENERATOR_FAILURE = "DiagramErrors.modelGeneratorFailure"; //$NON-NLS-1$

    }

    /**
     * Constants related to styles of drawing diagram connections ('routers')
     * 
     * @since 4.0
     */
    interface DiagramRouterStyles {

        String MANHATTAN_ROUTER = "DiagramRouterStyles.manhattan.textid"; //$NON-NLS-1$
        String FAN_ROUTER = "DiagramRouterStyles.fan.textid"; //$NON-NLS-1$
    }

    /**
     * Constants related to diagram colors
     * 
     * @since 4.0
     */
    interface Colors {
        Color GROUP_HEADER = GlobalUiColorManager.getColor(new RGB(0, 100, 246));
        Color GROUP_BKGRND = GlobalUiColorManager.getColor(new RGB(150, 191, 255));
        Color OUTLINE = GROUP_BKGRND;
        Color VIRTUAL_GROUP_HEADER = GlobalUiColorManager.getColor(new RGB(255, 153, 0));
        Color VIRTUAL_RS_GROUP_HEADER = GlobalUiColorManager.getColor(new RGB(0, 100, 225));
        Color VIRTUAL_GROUP_BKGRND = GlobalUiColorManager.getColor(new RGB(255, 204, 102));
        Color TEMP_GROUP_HEADER = GlobalUiColorManager.getColor(new RGB(255, 230, 225));
        Color TEMP_GROUP_BKGRND = GlobalUiColorManager.getColor(new RGB(255, 240, 225));
        // Color INPUT_SET_HEADER = new Color(null, 250, 160, 210);
        // Color INPUT_SET_BKGRND = new Color(null, 250, 240, 240);
        // Color TEMP_TABLE_HEADER = new Color(null, 255, 160, 140);
        Color DEPENDENCY = GlobalUiColorManager.getColor(new RGB(0, 255, 200));
        Color LOGICAL_GROUP_HEADER = GlobalUiColorManager.getColor(new RGB(0, 160, 140));
        Color LOGICAL_GROUP_BKGRND = GlobalUiColorManager.getColor(new RGB(0, 220, 152));
    }

    /**
     * Constants related to styles of drawing diagram connections ('routers')
     * 
     * @since 4.0
     */
    interface Position {
        int UPPER_LEFT = 0;
        int UPPER_CENTER = 1;
        int UPPER_RIGHT = 2;
        int CENTER_LEFT = 3;
        int CENTER_CENTER = 4;
        int CENTER_RIGHT = 5;
        int LOWER_LEFT = 6;
        int LOWER_CENTER = 7;
        int LOWER_RIGHT = 8;
    }

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 4.0
     */
    interface Images {
        class PC {
            private static final String FULL = "icons/full/"; //$NON-NLS-1$
            private static final String CVIEW = FULL + "cview20/"; //$NON-NLS-1$
            private static final String CVIEW16 = FULL + "cview16/"; //$NON-NLS-1$
        }

        String ADD_TO_DIAGRAM = PC.CVIEW16 + "tb_add_to_diagram.gif"; //$NON-NLS-1$
        String REMOVE_FROM_DIAGRAM = PC.CVIEW16 + "tb_remove_from_diagram.gif"; //$NON-NLS-1$
        String CLEAR_DIAGRAM = PC.CVIEW16 + "tb_clear_diagram.gif"; //$NON-NLS-1$
        String ADD_ASSOCIATED_OBJECTS = PC.CVIEW + "tb_add_to_diagram.gif"; //$NON-NLS-1$
        String NEW_ELLIPSE = PC.CVIEW + "tb_new_circle.gif"; //$NON-NLS-1$
        String NEW_RECTANGLE = PC.CVIEW + "tb_new_rect.gif"; //$NON-NLS-1$
        String NEW_TEXT = PC.CVIEW + "tb_new_text.gif"; //$NON-NLS-1$
        String NEW_NOTE = PC.CVIEW + "tb_new_note.gif"; //$NON-NLS-1$
        String SAVE_DIAGRAM = PC.CVIEW16 + "tb_save_diagram.gif"; //$NON-NLS-1$
        String REFRESH_DIAGRAM = PC.CVIEW16 + "tb_refresh_diagram.gif"; //$NON-NLS-1$
        String UP_PACKAGE_DIAGRAM = PC.CVIEW16 + "tb_show_parent_diagram.gif"; //$NON-NLS-1$
        String NEW_ASSOCIATION = PC.CVIEW + "tb_new_assoc.gif"; //$NON-NLS-1$
        String SHOW_PAGE_GRID = PC.CVIEW16 + "tb_grid_on.gif"; //$NON-NLS-1$
        String HIDE_PAGE_GRID = PC.CVIEW16 + "tb_grid_off.gif"; //$NON-NLS-1$

    }

    interface Zoom {
        double[] zoomValues = {0.1, 0.25, 0.50, 0.75, 0.9, 1.0, 1.10, 1.25, 1.50, 2.0, 3.0, 4.0, 10.0};
        String[] zoomStrings = {"10%", //$NON-NLS-1$
            "25%", //$NON-NLS-1$
            "50%", //$NON-NLS-1$
            "75%", //$NON-NLS-1$
            "90%", //$NON-NLS-1$
            "100%", //$NON-NLS-1$
            "110%", //$NON-NLS-1$
            "125%", //$NON-NLS-1$
            "150%", //$NON-NLS-1$
            "200%", //$NON-NLS-1$
            "300%", //$NON-NLS-1$
            "400%", //$NON-NLS-1$
            "1000%"}; //$NON-NLS-1$
    }

    /**
     * Constants related to styles of drawing diagram connections ('routers')
     * 
     * @since 4.0
     */
    interface LinkRouter {
        int DIRECT = 1;
        int ORTHOGONAL = 0;
        int MANUAL = 2;
        String[] types = {"Orthogonal", //$NON-NLS-1$
            "Directed", //$NON-NLS-1$
            "Manual"}; //$NON-NLS-1$
    }
}
