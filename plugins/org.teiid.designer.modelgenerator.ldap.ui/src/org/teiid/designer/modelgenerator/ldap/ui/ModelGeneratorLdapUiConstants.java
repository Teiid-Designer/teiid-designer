/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.ldap.ui;

import java.util.ResourceBundle;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;


/**
 * DiagramUiConstants
 * @since 8.0
 */
public interface ModelGeneratorLdapUiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = ModelGeneratorLdapUiConstants.class.getPackage().getName();

    /**
     * The package containing this constants class.
     */
    String PACKAGE_ID = ModelGeneratorLdapUiConstants.class.getPackage().getName();

    /** The dialog settings section to use for any settings saved. */
    String DIALOG_SETTINGS_SECTION = "ModelGeneratorLdapUi"; //$NON-NLS-1$

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
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

    //============================================================================================================================

    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images {
        String ICON_PATH = "icons/full/"; //$NON-NLS-1$
        String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$
        String CTOOL16 = ICON_PATH + "ctool16/"; //$NON-NLS-1$
        String DTOOL16 = ICON_PATH + "dtool16/"; //$NON-NLS-1$
        String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$
        String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$

        String IMPORT_LDAP_ICON = CTOOL16 + "import_ldap_wiz.gif"; //$NON-NLS-1$
        String LDAP_OBJECTS_ICON = CTOOL16 + "ldap_banner_icon.png"; //$NON-NLS-1$
        String WIZARD_BANNER = WIZBAN + "ldap_banner_icon.png"; //$NON-NLS-1$
        String LDAP_REFRESH_ICON = CTOOL16 + "refresh.gif"; //$NON-NLS-1$
        String LDAP_DELETE_ICON = CTOOL16 + "delete.gif"; //$NON-NLS-1$
        String LDAP_ADD_ICON = CTOOL16 + "add.gif"; //$NON-NLS-1$
        String LDAP_TABLE_ICON = CTOOL16 + "table.png"; //$NON-NLS-1$
        String LDAP_COLUMN_ICON = CTOOL16 + "column.png"; //$NON-NLS-1$
    }

    /**
     * Contains constants for the available context help identifiers found in the helpContexts.xml file. 
     * @since 4.2
     */
    interface HelpContexts {
        String PREFIX = PLUGIN_ID + '.';
        String LDAP_SELECTION_PAGE = PREFIX + "ldapSelectionPage"; //$NON-NLS-1$
    }
}
