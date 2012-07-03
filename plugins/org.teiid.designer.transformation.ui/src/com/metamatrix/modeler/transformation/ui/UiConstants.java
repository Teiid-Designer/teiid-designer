/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui;

import java.util.ResourceBundle;
import org.eclipse.swt.graphics.Color;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;

/**
 * DiagramUiConstants
 * 
 * @since 4.0
 */
public interface UiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.designer.transformation.ui"; //$NON-NLS-1$

    String PACKAGE_ID = UiConstants.class.getPackage().getName();

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

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 4.0
     */
	interface Images {
		class PC {
			private static final String FULL = "icons/full/"; //$NON-NLS-1$
			//            private static final String CVIEW = FULL + "cview20/"; //$NON-NLS-1$
			private static final String CVIEW16 = FULL + "cview16/"; //$NON-NLS-1$
			private static final String DVIEW16 = FULL + "dview16/"; //$NON-NLS-1$
			//            private static final String COBJ  = FULL + "cobj16/"; //$NON-NLS-1$
			private static final String OVR16 = FULL + "ovr16/"; //$NON-NLS-1$
			private static final String OBJ16 = FULL + "obj16/"; //$NON-NLS-1$
			private static final String WIZBAN = FULL + "wizban/"; //$NON-NLS-1$
		}

		String ADD_SOURCES = PC.CVIEW16 + "tb_add_to_diagram.gif"; //$NON-NLS-1$
		String ADD_UNION_SOURCES = PC.CVIEW16 + "tb_add_union_to_diagram.gif"; //$NON-NLS-1$
		String REMOVE_SOURCES = PC.CVIEW16 + "tb_remove_from_diagram.gif"; //$NON-NLS-1$
		String CLEAR_TRANSFORMATION = PC.CVIEW16
				+ "tb_clear_transformation.gif"; //$NON-NLS-1$
		String LOCK_VIRTUAL_GROUP = PC.OVR16 + "lock_vg.gif"; //$NON-NLS-1$
		String LOCK_MAPPING_CLASS = PC.CVIEW16 + "lock_mapping_class.gif"; //$NON-NLS-1$
		String UNLOCK_MAPPING_CLASS = PC.CVIEW16 + "unlock_mapping_class.gif"; //$NON-NLS-1$
		String NOT_ALLOWED = PC.CVIEW16 + "not-allowed.png"; //$NON-NLS-1$
		String WARNING = PC.OVR16 + "warning_co.gif"; //$NON-NLS-1$
		String ERROR = PC.OVR16 + "error_co.gif"; //$NON-NLS-1$

		String SAVE = PC.CVIEW16 + "save.gif"; //$NON-NLS-1$
		String SAVE_DISABLED = PC.DVIEW16 + "save.gif"; //$NON-NLS-1$

		String IMPORT_TEIID_METADATA = PC.WIZBAN + "importTeiidMetadataWiz.gif"; //$NON-NLS-1$
		String COLUMN_ICON = PC.OBJ16 + "Column.gif"; //$NON-NLS-1$

		String CRITERIA_BUILDER = PC.CVIEW16 + "launch_criteria_builder.gif"; //$NON-NLS-1$
		String EXPRESSION_BUILDER = PC.CVIEW16
				+ "launch_expression_builder.gif"; //$NON-NLS-1$
		String EXPAND_SELECT = PC.CVIEW16 + "expand.gif"; //$NON-NLS-1$
		String UP_FONT = PC.CVIEW16 + "inc_font.gif"; //$NON-NLS-1$
		String DOWN_FONT = PC.CVIEW16 + "dec_font.gif"; //$NON-NLS-1$
		String VALIDATE = PC.CVIEW16 + "validate.gif"; //$NON-NLS-1$
		String SHOW_MESSAGES = PC.CVIEW16 + "show_message_off.gif"; //$NON-NLS-1$
		String SHOW_PREFERENCES = PC.CVIEW16 + "show_preferences.gif"; //$NON-NLS-1$
		String SHORT_NAMES = PC.CVIEW16 + "short_names.gif"; //$NON-NLS-1$
		String IMPORT_FROM_FILE = PC.CVIEW16 + "import_from_file.gif"; //$NON-NLS-1$
		String EXPORT_TO_FILE = PC.CVIEW16 + "export_to_file.gif"; //$NON-NLS-1$
		
		String SCHEMA_ELEMENT = PC.OBJ16 + "XSDElementDeclaration.gif"; //$NON-NLS-1$
		String SCHEMA_ATTRIBUTE = PC.OBJ16 + "XSDAttributeDeclaration.gif"; //$NON-NLS-1$

	}
    
    interface SQLPanels {
    	int SELECT = 0;
    	int UPDATE_SELECT = 1;
    	int UPDATE_INSERT = 2;
    	int UPDATE_UPDATE = 3;
    	int UPDATE_DELETE = 4;
    	
    }
    
    interface METHODS {
    	String GET = "GET"; //$NON-NLS-1$
	    String PUT = "PUT"; //$NON-NLS-1$
	    String POST = "POST"; //$NON-NLS-1$
	    String DELETE = "DELETE"; //$NON-NLS-1$
    }
    
    String[] METHODS_ARRAY = {METHODS.GET, METHODS.PUT, METHODS.POST, METHODS.DELETE };

    /**
     * Constants related to color of diagram objects
     * 
     * @since 4.0
     */
    interface Colors {
        Color DEPENDENCY = DiagramUiConstants.Colors.DEPENDENCY;
    }
}
