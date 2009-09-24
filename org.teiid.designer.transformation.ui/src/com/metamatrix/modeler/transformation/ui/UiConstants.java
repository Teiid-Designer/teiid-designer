/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui;

import java.util.ResourceBundle;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;

/**
 * DiagramUiConstants
 * @since 4.0
 */
public interface UiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.designer.transformation.ui"; //$NON-NLS-1$
     
    String PACKAGE_ID = UiConstants.class.getPackage().getName();
    
    String EXT_ID_PREFIX = "com.metamatrix.modeler.transformation.ui"; //$NON-NLS-1$
     
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
    // Extension constants
    
    /**
     * Constants related to extensions, including all extension ID's.
     * @since 4.0
     */
    interface Extensions {
        //interface SearchPage {
        //    String ID = PLUGIN_ID + ".search.RelationshipSearchPage"; //$NON-NLS-1$
        //}
        
        interface SearchActionSet {
            String ID = EXT_ID_PREFIX + "transformationSearchActionSet"; //$NON-NLS-1$
            String ACTION_ID = "OpenTransformationSearchPageAction"; //$NON-NLS-1$
        }
    }
   
    
    /**
     * Constants related to properties on a Diagram Model Node
     * @since 4.0
     */
    interface Errors {

        String SOME_TRANSFORMATION_FAILURE = "TransformationErrors.someError"; //$NON-NLS-1$
     }
    
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images {
        class PC {
            private static final String FULL  = "icons/full/"; //$NON-NLS-1$
//            private static final String CVIEW = FULL + "cview20/"; //$NON-NLS-1$
            private static final String CVIEW16 = FULL + "cview16/"; //$NON-NLS-1$
            private static final String DVIEW16 = FULL + "dview16/"; //$NON-NLS-1$
//            private static final String COBJ  = FULL + "cobj16/"; //$NON-NLS-1$
            private static final String OVR16= FULL + "ovr16/"; //$NON-NLS-1$
        }
        
        String ADD_SOURCES              = PC.CVIEW16 + "tb_add_to_diagram.gif"; //$NON-NLS-1$
        String ADD_UNION_SOURCES        = PC.CVIEW16 + "tb_add_union_to_diagram.gif"; //$NON-NLS-1$
        String REMOVE_SOURCES           = PC.CVIEW16 + "tb_remove_from_diagram.gif"; //$NON-NLS-1$
        String CLEAR_TRANSFORMATION     = PC.CVIEW16 + "tb_clear_transformation.gif"; //$NON-NLS-1$
        String LOCK_VIRTUAL_GROUP       = PC.OVR16   + "lock_vg.gif"; //$NON-NLS-1$
        String LOCK_MAPPING_CLASS       = PC.CVIEW16 + "lock_mapping_class.gif";          //$NON-NLS-1$
        String UNLOCK_MAPPING_CLASS     = PC.CVIEW16 + "unlock_mapping_class.gif";        //$NON-NLS-1$
        
        String SAVE                     = PC.CVIEW16 + "save.gif";        //$NON-NLS-1$
        String SAVE_DISABLED            = PC.DVIEW16 + "save.gif";        //$NON-NLS-1$

    }
    /**
     * Constants related to color of diagram objects
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
        Color DEPENDENCY                = DiagramUiConstants.Colors.DEPENDENCY;
    } 
}


