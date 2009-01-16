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

package com.metamatrix.modeler.mapping.ui;

import java.util.ResourceBundle;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * DiagramUiConstants
 * @since 4.0
 */
public interface UiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = "com.metamatrix.modeler.mapping.ui"; //$NON-NLS-1$
     
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
    // Extension constants
    
    /**
     * Constants related to extensions, including all extension ID's.
     * @since 4.0
     */
    interface Extensions {
//        String DIAGRAM_EDITOR           = "diagramEditorPage"; //$NON-NLS-1$
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
        Color GROUP_HEADER              = DiagramUiConstants.Colors.GROUP_HEADER;
        Color GROUP_BKGRND              = DiagramUiConstants.Colors.GROUP_BKGRND;
        Color HILITE                    = ColorConstants.lightGreen;
        Color SELECTION                 = ColorConstants.lightBlue;
        Color OUTLINE                   = GROUP_BKGRND;
        Color VIRTUAL_GROUP_HEADER      = DiagramUiConstants.Colors.VIRTUAL_GROUP_HEADER;
        Color VIRTUAL_RS_GROUP_HEADER   = DiagramUiConstants.Colors.VIRTUAL_RS_GROUP_HEADER;
        Color VIRTUAL_GROUP_BKGRND      = DiagramUiConstants.Colors.VIRTUAL_GROUP_BKGRND;
        Color INPUT_SET_HEADER          = GlobalUiColorManager.getColor(new RGB(250, 160, 210));
        Color INPUT_SET_BKGRND          = GlobalUiColorManager.getColor(new RGB(250, 240, 240));
        Color TEMP_TABLE_HEADER         = GlobalUiColorManager.getColor(new RGB(255, 160, 140));
        Color DEPENDENCY                = DiagramUiConstants.Colors.DEPENDENCY;
        Color UNMAPPED					= ColorConstants.yellow;
        Color REQUIRES_MAPPING			= ColorConstants.red;
    } 
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images {
        class PC {
            private static final String FULL  = "icons/full/"; //$NON-NLS-1$
            private static final String CVIEW16 = FULL + "cview16/"; //$NON-NLS-1$
            private static final String COBJ  = FULL + "obj16/"; //$NON-NLS-1$
        }
        
        String SHOW_COARSE_MAPPING      = PC.CVIEW16 + "tb_show_coarse_mapping.gif";      //$NON-NLS-1$
        String SHOW_DETAILED_MAPPING    = PC.CVIEW16 + "tb_show_detailed_mapping.gif";    //$NON-NLS-1$
        String GENERATE_MAPPING_CLASSES = PC.CVIEW16 + "tb_gen_mapping_classes.gif";      //$NON-NLS-1$
        String NEW_MAPPING_CLASS        = PC.CVIEW16 + "tb_new_mapping_class.gif";        //$NON-NLS-1$
        String NEW_MAPPING_LINK         = PC.CVIEW16 + "tb_new_mapping_link.gif";         //$NON-NLS-1$
        String NEW_TEMP_TABLE           = PC.CVIEW16 + "tb_new_temp_table.gif";           //$NON-NLS-1$
        String DELETE_MAPPING_LINK      = PC.CVIEW16 + "tb_delete_mapping_link.gif";      //$NON-NLS-1$
        String MERGE_MAPPING_CLASSES    = PC.CVIEW16 + "tb_merge_mapping_classes.gif";    //$NON-NLS-1$
        String SPLIT_MAPPING_CLASS      = PC.CVIEW16 + "tb_split_mapping_classes.gif";    //$NON-NLS-1$
        String LOCK_MAPPING_CLASS       = PC.CVIEW16 + "lock_mapping_class.gif";          //$NON-NLS-1$
        String UNLOCK_MAPPING_CLASS     = PC.CVIEW16 + "unlock_mapping_class.gif";        //$NON-NLS-1$
        String FIND_XSD_COMPONENT       = PC.CVIEW16 + "tb_find_xsd_component.gif";        //$NON-NLS-1$
        String RECURSION_IMAGE          = PC.COBJ  + "recursion.gif";                   //$NON-NLS-1$
        String EDIT_OBJECT_ICON         = PC.COBJ  + "edit_model_object.gif";           //$NON-NLS-1$
        String SHOW_ALL_MAPPING_CLASSES = PC.CVIEW16 + "show_exposed_mapping_classes.gif";    //$NON-NLS-1$
        String SHOW_EXPOSED_MAPPING_CLASSES = PC.CVIEW16 + "show_exposed_mapping_classes.gif";    //$NON-NLS-1$
        String COLLAPSE_MAPPING_CLASS_COLUMNS = PC.CVIEW16 + "collapse_mapping_classes.gif";    //$NON-NLS-1$
        String EXPAND_MAPPING_CLASS_COLUMNS = PC.CVIEW16 + "expand_mapping_classes.gif";    //$NON-NLS-1$
        String SYNC_TREE_AND_DIAGRAM_WHEN_EXPANDING = PC.CVIEW16 + "synced.gif";    //$NON-NLS-1$
        String POPULATE_DIAGRAM_FROM_TREE_SELECTION = PC.CVIEW16 + "filter_change.gif";    //$NON-NLS-1$
        String COLUMN_FOR_SUMMARY_EXTENT = PC.COBJ + "Column.gif";    //$NON-NLS-1$

//        String UP_PACKAGE_DIAGRAM       = PC.CVIEW16 + "tb_up_package_diagram.gif";       //$NON-NLS-1$
    }
}


