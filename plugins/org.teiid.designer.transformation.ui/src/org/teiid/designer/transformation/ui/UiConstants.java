/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui;

import java.util.ResourceBundle;
import org.eclipse.swt.graphics.Color;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.diagram.ui.DiagramUiConstants;


/**
 * DiagramUiConstants
 * 
 * @since 8.0
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
	interface Images extends org.teiid.designer.ui.common.UiConstants.Images{
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
		String COLUMN_ICON = OBJ16 + "column.png"; //$NON-NLS-1$

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

		String VIRTUAL_RELATIONAL_TABLE = PC.OBJ16 + "virtual-relational-table.png"; //$NON-NLS-1$
		String RELATIONAL_TABLE = PC.OBJ16 + "relational-table.png"; //$NON-NLS-1$
		String SOURCE_MODEL = PC.OBJ16 + "source-model.gif"; //$NON-NLS-1$
		String VIEW_MODEL = PC.OBJ16 + "view-model.gif"; //$NON-NLS-1$
		String FOLDER = PC.OBJ16 + "folder.png"; //$NON-NLS-1$
		String OPEN_FOLDER = PC.OBJ16 + "open-folder.png"; //$NON-NLS-1$
		String MODEL_PROJECT = PC.OBJ16 + "ModelProject.gif"; //$NON-NLS-1$
		
		String UP_ICON = PC.CVIEW16 + "up.gif"; //$NON-NLS-1$
		String DOWN_ICON = PC.CVIEW16 + "down.gif"; //$NON-NLS-1$
		String ADD_ICON = PC.CVIEW16 + "add.gif"; //$NON-NLS-1$
		String REMOVE_ICON = PC.CVIEW16 + "remove.gif"; //$NON-NLS-1$
		String CLEAR_ICON = PC.CVIEW16 + "clear.png"; //$NON-NLS-1$
		String DELETE_ICON = PC.CVIEW16 + "delete.gif"; //$NON-NLS-1$
		String TOP_ICON = PC.CVIEW16 + "top.gif"; //$NON-NLS-1$
		String BOTTOM_ICON = PC.CVIEW16 + "bottom.gif"; //$NON-NLS-1$
		String SWAP_ICON = PC.CVIEW16 + "swap.png"; //$NON-NLS-1$
		
		String NEW_TABLE_ICON = PC.CVIEW16 + "new-relational-table.png"; //$NON-NLS-1$
		String NEW_VIRTUAL_TABLE_ICON = PC.CVIEW16 + "new-view-table.png"; //$NON-NLS-1$
		String NEW_PROCECDURE_ICON = PC.CVIEW16 + "new-relational-procedure.png"; //$NON-NLS-1$
		String NEW_VIRTUAL_PROCEDURE_ICON = PC.CVIEW16 + "new-view-procedure.png"; //$NON-NLS-1$
		String NEW_INDEX_ICON = PC.CVIEW16 + "new-relational-index.png"; //$NON-NLS-1$
		
		String CLONE_ICON = PC.CVIEW16 + "clone.png"; //$NON-NLS-1$
		String CLONE_DISABLED_ICON = PC.CVIEW16 + "clone-disabled.png"; //$NON-NLS-1$
		
		String PREVIEW_DATA_ICON = PC.CVIEW16 + "previewData.gif"; //$NON-NLS-1$
	}
    
    interface SQLPanels {
    	int SELECT = 0;
    	int UPDATE_SELECT = 1;
    	int UPDATE_INSERT = 2;
    	int UPDATE_UPDATE = 3;
    	int UPDATE_DELETE = 4;
    	
    }

    /**
     * Constants related to color of diagram objects
     * 
     * @since 4.0
     */
    interface Colors {
        Color DEPENDENCY = DiagramUiConstants.Colors.DEPENDENCY;
    }
}
