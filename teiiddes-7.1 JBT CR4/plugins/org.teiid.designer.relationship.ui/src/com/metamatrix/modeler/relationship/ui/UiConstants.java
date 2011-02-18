/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui;

import java.util.ResourceBundle;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * @author BLaFond To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public interface UiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.designer.relationship.ui"; //$NON-NLS-1$

    String PACKAGE_ID = UiConstants.class.getPackage().getName();

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
        static final String SEARCH_ATTRIBUTE_PREFIX = PLUGIN_ID + ".relationshipSearch."; //$NON-NLS-1$ 
    }

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

    // ============================================================================================================================
    // Extension constants

    /**
     * Constants related to extensions, including all extension ID's.
     * 
     * @since 4.0
     */
    interface Extensions {
        interface Navigator {
            String VIEW_ID = "relationship.ui.navigation.navigationView"; //$NON-NLS-1$
            String CONTEXT_MENU_GROUP_0 = "NavigatorViewContextMenu.Group0"; //$NON-NLS-1$
            String CONTEXT_MENU_GROUP_1 = "NavigatorViewContextMenu.Group1"; //$NON-NLS-1$
            String CONTEXT_MENU_GROUP_2 = "NavigatorViewContextMenu.Group2"; //$NON-NLS-1$
        }

        interface SearchPage {
            String ID = "relationshipSearchPage"; //$NON-NLS-1$
        }

    }

    /**
     * Constants related to styles of drawing diagram connections ('routers')
     * 
     * @since 4.0
     */
    interface Colors {
        Color FOCUS_NODE_BKGD = GlobalUiColorManager.getColor(new RGB(225, 220, 210));
        Color NON_FOCUS_NODE_BKGD = GlobalUiColorManager.getColor(new RGB(200, 230, 255));
        Color RELATIONSHIP_BKGD = DiagramUiConstants.Colors.LOGICAL_GROUP_BKGRND;
        Color RELATIONSHIP_HEADER_BKGD = DiagramUiConstants.Colors.LOGICAL_GROUP_HEADER;
        Color RELATIONISHIP_TYPE_BKGD = DiagramUiConstants.Colors.LOGICAL_GROUP_BKGRND;
        // Color RELATIONISHIP_TYPE_BKGD = new Color(null, 92, 160, 192);

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
            private static final String COBJ = FULL + "obj16/"; //$NON-NLS-1$
        }

        String SHOW_RELATIONSHIP_DIAGRAM = PC.CVIEW + "tb_show_relationship_diagram.gif"; //$NON-NLS-1$
        String REMOVE_FROM_RELATIONSHIP = PC.CVIEW + "tb_remove_from_relationship.gif"; //$NON-NLS-1$
        String RESTORE_RELATIONSHIP = PC.CVIEW + "tb_restore_relationship.gif"; //$NON-NLS-1$
        String SHOW_SUBTYPES = PC.CVIEW + "tb_show_subtypes.gif"; //$NON-NLS-1$
        String SHOW_SUPERTYPE = PC.CVIEW + "tb_show_supertype.gif"; //$NON-NLS-1$
        String BACK_C = "icons/full/clcl16/backward_nav.gif"; //$NON-NLS-1$
        String BACK_D = "icons/full/dlcl16/backward_nav.gif"; //$NON-NLS-1$
        String BACK_E = "icons/full/elcl16/backward_nav.gif"; //$NON-NLS-1$
        String FORWARD_C = "icons/full/clcl16/forward_nav.gif"; //$NON-NLS-1$
        String FORWARD_D = "icons/full/dlcl16/forward_nav.gif"; //$NON-NLS-1$
        String FORWARD_E = "icons/full/elcl16/forward_nav.gif"; //$NON-NLS-1$
        String CLEAR_C = "icons/full/clcl16/delete.gif"; //$NON-NLS-1$
        String CLEAR_D = "icons/full/dlcl16/delete.gif"; //$NON-NLS-1$
        String CLEAR_E = "icons/full/elcl16/delete.gif"; //$NON-NLS-1$
        String FOCUS_C = "icons/full/clcl16/set_focus.gif"; //$NON-NLS-1$
        String FOCUS_D = "icons/full/dlcl16/set_focus.gif"; //$NON-NLS-1$
        String FOCUS_E = "icons/full/elcl16/set_focus.gif"; //$NON-NLS-1$
        String PROPERTIES_C = "icons/full/clcl16/properties.gif"; //$NON-NLS-1$
        String PROPERTIES_D = "icons/full/dlcl16/properties.gif"; //$NON-NLS-1$
        String PROPERTIES_E = "icons/full/elcl16/properties.gif"; //$NON-NLS-1$
        String REFRESH_C = "icons/full/clcl16/refresh.gif"; //$NON-NLS-1$
        String REFRESH_D = "icons/full/dlcl16/refresh.gif"; //$NON-NLS-1$
        String REFRESH_E = "icons/full/elcl16/refresh.gif"; //$NON-NLS-1$

        String FIND = "icons/full/cview16/find.gif"; //$NON-NLS-1$

        String RELATIONSHIP_SEARCH = PC.COBJ + "relationshipSearchPage.gif"; //$NON-NLS-1$

        String TYPE_FOLDER = PC.COBJ + "TypeFolder.gif"; //$NON-NLS-1$
    }

    /**
     * Constants related to properties on a Diagram Model Node
     * 
     * @since 4.0
     */
    interface NavigationModelNodeProperties {

        String LOCATION = "location"; //$NON-NLS-1$
        String SIZE = "size"; //$NON-NLS-1$
        String CHILDREN = "children"; //$NON-NLS-1$
        String PROPERTIES = "prop"; //$NON-NLS-1$
        String NAME = "name"; //$NON-NLS-1$
        String CONNECTION = "connection"; //$NON-NLS-1$
        String IMAGES = "image"; //$NON-NLS-1$
        String ERRORS = "errors"; //$NON-NLS-1$
        String LAYOUT = "layout"; //$NON-NLS-1$
    }

    /**
     * Constants related to properties on a Diagram Model Node
     * 
     * @since 4.0
     */
    interface RelationshipModelTypes {
        int DIAGRAM = 100;
        int LABEL = 99;
        int DRAWING = 98;
        int RELATIONSHIP = 0;
        int TYPE = 1;
        int ROLE = 2;
        int FOCUS_NODE = 3;
        int FOLDER = 4;
        int OTHER = 9;
        int LINK = 10;
        int TYPE_LINK = 11;
    }

}
