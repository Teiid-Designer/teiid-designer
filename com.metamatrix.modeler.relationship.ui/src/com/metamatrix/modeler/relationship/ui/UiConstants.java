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

package com.metamatrix.modeler.relationship.ui;

import java.util.ResourceBundle;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface UiConstants {
	/**
	 * The ID of the plug-in containing this constants class.
	 * @since 4.0
	 */
	String PLUGIN_ID = "com.metamatrix.modeler.relationship.ui"; //$NON-NLS-1$
     
	/**
	 * Contains private constants used by other constants within this class.
	 * @since 4.0
	 */  
	class PC {
		private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
        private static final String SEARCH_ATTRIBUTE_PREFIX = PLUGIN_ID + ".relationshipSearch."; //$NON-NLS-1$ 
	}
    
	/**
	 * Provides access to the plugin's log and to it's resources.
	 * @since 4.0
	 */
	PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));
  
	//============================================================================================================================
	// Extension constants
    
	/**
	 * Constants related to extensions, including all extension ID's.
	 * @since 4.0
	 */
	interface Extensions {
        interface Navigator {
            String VIEW_ID = PLUGIN_ID + ".navigation.navigationView"; //$NON-NLS-1$
            String CONTEXT_MENU_GROUP_0 = "NavigatorViewContextMenu.Group0"; //$NON-NLS-1$
            String CONTEXT_MENU_GROUP_1 = "NavigatorViewContextMenu.Group1"; //$NON-NLS-1$
            String CONTEXT_MENU_GROUP_2 = "NavigatorViewContextMenu.Group2"; //$NON-NLS-1$
        }

        interface SearchPage {
            String ID = PLUGIN_ID + ".search.RelationshipSearchPage"; //$NON-NLS-1$
        }
        
        interface SearchActionSet {
            String ID = PLUGIN_ID + ".search.relationshipSearchActionSet"; //$NON-NLS-1$
            String ACTION_ID = "com.metamatrix.modeler.internal.relationship.ui.search.OpenRelationshipSearchPageAction"; //$NON-NLS-1$
        }

	}
    
	/**
	 * Constants related to extension points, including all extension point ID's and extension point schema component names.
	 * @since 4.0
	 */
	interface ExtensionPoints {

	}
        
	/**
	 * Constants related to properties on a Diagram Model Node
	 * @since 4.0
	 */
	interface Errors {

		String SOME_MAPPING_FAILURE = "MappingErrors.someError"; //$NON-NLS-1$
	 }
    
	/**
	 * Constants related to styles of drawing diagram connections ('routers')
	 * @since 4.0
	 */
	interface Colors {
		Color FOCUS_NODE_BKGD           = GlobalUiColorManager.getColor(new RGB(225, 220, 210));
		Color NON_FOCUS_NODE_BKGD       = GlobalUiColorManager.getColor(new RGB(200, 230, 255));
		Color HILITE                    = ColorConstants.lightGreen;
		Color SELECTION                 = ColorConstants.lightBlue;
		Color OUTLINE                   = ColorConstants.black;
		Color RELATIONSHIP_BKGD         = DiagramUiConstants.Colors.LOGICAL_GROUP_BKGRND; 
		Color RELATIONSHIP_HEADER_BKGD  = DiagramUiConstants.Colors.LOGICAL_GROUP_HEADER;
		Color RELATIONISHIP_TYPE_BKGD   = DiagramUiConstants.Colors.LOGICAL_GROUP_BKGRND;
//		Color RELATIONISHIP_TYPE_BKGD   = new Color(null, 92, 160, 192);
		Color LAST_FOCUS_NODE_BKGD       = ColorConstants.orange;
		Color NEXT_FOCUS_NODE_BKGD       = ColorConstants.yellow;
		
	} 
	/**
	 * Keys for images and image descriptors stored in the image registry.
	 * @since 4.0
	 */
	interface Images {
		class PC {
			private static final String FULL  = "icons/full/"; //$NON-NLS-1$
			private static final String CVIEW = FULL + "cview20/"; //$NON-NLS-1$
			private static final String COBJ  = FULL + "obj16/"; //$NON-NLS-1$
		}
        
		String SHOW_RELATIONSHIP_DIAGRAM     = PC.CVIEW + "tb_show_relationship_diagram.gif"; //$NON-NLS-1$
		String REMOVE_FROM_RELATIONSHIP      = PC.CVIEW + "tb_remove_from_relationship.gif"; //$NON-NLS-1$
		String RESTORE_RELATIONSHIP          = PC.CVIEW + "tb_restore_relationship.gif"; //$NON-NLS-1$
		String SHOW_SUBTYPES	             = PC.CVIEW + "tb_show_subtypes.gif"; //$NON-NLS-1$
		String SHOW_SUPERTYPE          		 = PC.CVIEW + "tb_show_supertype.gif"; //$NON-NLS-1$
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
	 * @since 4.0
	 */
	interface NavigationModelNodeProperties {

		String LOCATION     = "location";     //$NON-NLS-1$
		String SIZE         = "size";         //$NON-NLS-1$
		String CHILDREN     = "children";     //$NON-NLS-1$
		String PROPERTIES   = "prop";         //$NON-NLS-1$
		String NAME         = "name";         //$NON-NLS-1$
		String CONNECTION   = "connection";   //$NON-NLS-1$
		String IMAGES       = "image";        //$NON-NLS-1$
		String ERRORS       = "errors";       //$NON-NLS-1$
        String LAYOUT       = "layout";       //$NON-NLS-1$
	}
	
	/**
	 * Constants related to properties on a Diagram Model Node
	 * @since 4.0
	 */
	interface RelationshipModelTypes {
		int DIAGRAM 		= 100;
		int LABEL			= 99;
		int DRAWING			= 98;
		int RELATIONSHIP    = 0;
		int TYPE			= 1;
		int ROLE			= 2;
		int FOCUS_NODE		= 3;
		int FOLDER			= 4;
		int OTHER 			= 9;
		int LINK			= 10;
		int TYPE_LINK		= 11;
	}
    
    /**
     * Additional search marker attributes specific to relationship searches. 
     * @since 4.2
     */
    interface SearchAttributes extends com.metamatrix.modeler.ui.UiConstants.SearchAttributes {
        String SEARCH_RECORD_TYPE = PC.SEARCH_ATTRIBUTE_PREFIX + "searchRecord"; //$NON-NLS-1$
        String RELATIONSHIP_TYPE_NAME = PC.SEARCH_ATTRIBUTE_PREFIX + "relationshipTypeName"; //$NON-NLS-1$
        String RELATIONSHIP_TYPE_UUID = PC.SEARCH_ATTRIBUTE_PREFIX + "relationshipTypeUuid"; //$NON-NLS-1$
    }
}
