/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relational.ui;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

public interface UiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.designer.relational.ui"; //$NON-NLS-1$

    String PACKAGE_ID = UiConstants.class.getPackage().getName();

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    }

    // ============================================================================================================================
    // Image constants

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 4.0
     */
    interface Images extends com.metamatrix.ui.UiConstants.Images {
        String MANAGE_EXTENDED_PROPERTIES_ICON = WIZBAN + "manage_properties.gif"; //$NON-NLS-1$
        String MANAGE_EXTENDED_PROPERTIES_ACTION_ICON = CTOOL16 + "edit_extension_properties.png"; //$NON-NLS-1$
        
        String COLUMN_ICON = OBJ16 + "column.png"; //$NON-NLS-1$
        String COLUMN_ERROR_ICON = OBJ16 + "column-error.png"; //$NON-NLS-1$
        String COLUMN_WARNING_ICON = OBJ16 + "column-warning.png"; //$NON-NLS-1$
        String PARAMETER_ICON = OBJ16 + "parameter.png"; //$NON-NLS-1$
        String PARAMETER_ERROR_ICON = OBJ16 + "parameter-error.png"; //$NON-NLS-1$
        String PARAMETER_WARNING_ICON = OBJ16 + "parameter-warning.png"; //$NON-NLS-1$
        String FK_ICON = OBJ16 + "foreign-key.png"; //$NON-NLS-1$
        String FK_ERROR_ICON = OBJ16 + "foreign-key-error.png"; //$NON-NLS-1$
        String FK_WARNING_ICON = OBJ16 + "foreign-key-warning.png"; //$NON-NLS-1$
        String PK_ICON = OBJ16 + "primary-key.png"; //$NON-NLS-1$
        String PK_ERROR_ICON = OBJ16 + "primary-key-error.png"; //$NON-NLS-1$
        String PK_WARNING_ICON = OBJ16 + "primary-key-warning.png"; //$NON-NLS-1$
        String UC_ICON = OBJ16 + "unique-constraint.png"; //$NON-NLS-1$
        String UC_ERROR_ICON = OBJ16 + "unique-constraint-error.png"; //$NON-NLS-1$
        String UC_WARNING_ICON = OBJ16 + "unique-constraint-warning.png"; //$NON-NLS-1$
        String AP_ICON = OBJ16 + "access-pattern.png"; //$NON-NLS-1$
        String AP_ERROR_ICON = OBJ16 + "access-pattern-error.png"; //$NON-NLS-1$
        String AP_WARNING_ICON = OBJ16 + "access-pattern-warning.png"; //$NON-NLS-1$
        String TABLE_ICON = OBJ16 + "relational-table.png"; //$NON-NLS-1$
        String TABLE_ERROR_ICON = OBJ16 + "relational-table-error.png"; //$NON-NLS-1$
        String TABLE_WARNING_ICON = OBJ16 + "relational-table-warning.png"; //$NON-NLS-1$
        String NEW_TABLE_ICON = OBJ16 + "new-relational-table.png"; //$NON-NLS-1$
        String VIRTUAL_TABLE_ICON = OBJ16 + "virtual-relational-table.png"; //$NON-NLS-1$
        String NEW_VIRTUAL_TABLE_ICON = OBJ16 + "new-view-table.png"; //$NON-NLS-1$
        String VIRTUAL_TABLE_ERROR_ICON = OBJ16 + "virtual-relational-table-error.png"; //$NON-NLS-1$
        String VIRTUAL_TABLE_WARNING_ICON = OBJ16 + "virtual-relational-table-warning.png"; //$NON-NLS-1$
        String PROCEDURE_ICON = OBJ16 + "relational-procedure.png"; //$NON-NLS-1$
        String PROCEDURE_ERROR_ICON = OBJ16 + "relational-procedure-error.png"; //$NON-NLS-1$
        String PROCEDURE_WARNING_ICON = OBJ16 + "relational-procedure-warning.png"; //$NON-NLS-1$
        String VIRTUAL_PROCEDURE_ICON = OBJ16 + "virtual-relational-procedure.png"; //$NON-NLS-1$
        String NEW_VIRTUAL_PROCEDURE_ICON = OBJ16 + "new-view-procedure.png"; //$NON-NLS-1$
        String VIRTUAL_PROCEDURE_ERROR_ICON = OBJ16 + "virtual-relational-procedure-error.png"; //$NON-NLS-1$
        String VIRTUAL_PROCEDURE_WARNING_ICON = OBJ16 + "virtual-relational-procedure-warning.png"; //$NON-NLS-1$
        String RESULT_SET_ERROR_ICON = OBJ16 + "result-set-error.png"; //$NON-NLS-1$
        String RESULT_SET_WARNING_ICON = OBJ16 + "result-set-warning.png"; //$NON-NLS-1$
        String VIRTUAL_RESULT_SET_ICON = OBJ16 + "virtual-result-set.png"; //$NON-NLS-1$
        String VIRTUAL_RESULT_SET_ERROR_ICON = OBJ16 + "virtual-result-set-error.png"; //$NON-NLS-1$
        String VIRTUAL_RESULT_SET_WARNING_ICON = OBJ16 + "virtual-result-set-warning.png"; //$NON-NLS-1$
        String INDEX_ICON = OBJ16 + "index.png"; //$NON-NLS-1$
        String INDEX_ERROR_ICON = OBJ16 + "index-error.png"; //$NON-NLS-1$
        String INDEX_WARNING_ICON = OBJ16 + "index-warning.png"; //$NON-NLS-1$
        
        
    }

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));
}
