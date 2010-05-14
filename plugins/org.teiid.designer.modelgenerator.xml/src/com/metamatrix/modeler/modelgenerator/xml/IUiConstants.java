/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml;

import com.metamatrix.core.util.I18nUtil;

/**
 * The internal UI constants.
 * 
 * @since 4.2
 */
public interface IUiConstants {
    /**
     * The plug-in ID where this interface is located.
     * 
     * @since 4.2
     */
    public final static String PLUGIN_ID = "org.teiid.designer.modelgenerator.xml"; //$NON-NLS-1$
    
    final static String PACKAGE_ID = IUiConstants.class.getPackage().getName();
    /**
     * The resource bundle path/filename.
     * 
     * @since 4.2
     */
    String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$

    /** The dialog settings section to use for any settings saved. */
    String DIALOG_SETTINGS_SECTION = "XmlAsRelationalImporter"; //$NON-NLS-1$

    /**
     * Contains constants for accessing images.
     * 
     * @since 4.2
     */
    interface Images {
        String ICON_PATH = "icons/full/"; //$NON-NLS-1$
        String CTOOL16 = ICON_PATH + "ctool16/"; //$NON-NLS-1$
        String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$
        String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$

        String RESOLUTION_STATUS = CTOOL16 + "resolution_status.gif"; //$NON-NLS-1$
        String SHOW_DEPENDENCIES = CTOOL16 + "show_dependencies.gif"; //$NON-NLS-1$
        String UNRESOLVE_NAMESPACE = CTOOL16 + "unresolve_namespace.gif"; //$NON-NLS-1$

        String SCHEMA_EDITOR = CTOOL16 + "schemaEditor.gif"; //$NON-NLS-1$
        String CLOSE_EDITOR = CTOOL16 + "closeEditor.gif"; //$NON-NLS-1$
        String PROBLEM_INDICATOR_ICON = CTOOL16 + "problem_indicator.gif"; //$NON-NLS-1$

        String NEW_MODEL_BANNER = WIZBAN + "WebService.gif"; //$NON-NLS-1$
        String DATABASE = OBJ16 + "database.gif"; //$NON-NLS-1$
        String IMPORT_DATABASE_ICON = WIZBAN + "importDatabase.gif"; //$NON-NLS-1$ 
    }

    /**
     * Contains constants for the available context help identifiers found in the helpContexts.xml file.
     * 
     * @since 4.2
     */
    interface HelpContexts {
        String PREFIX = PLUGIN_ID + '.';
        String NAMESPACE_RESOLUTION_PAGE = PREFIX + "namespaceResolutionPage"; //$NON-NLS-1$
        String SCHEMA_LOCATION_PAGE = PREFIX + "schemaLocationPage"; //$NON-NLS-1$
        String SCHEMA_LOCATION_EDITOR = PREFIX + "schemaLocationPage_schemaLocationEditor"; //$NON-NLS-1$
        String XSD_SELECTION_PAGE = PREFIX + "responseSelectionPage"; //$NON-NLS-1$
        String XML_MODEL_SELECTION_PAGE = PREFIX + "xmlModelSelectionPage"; //$NON-NLS-1$
    }

    /**
     * Contains widget constants.
     * 
     * @since 4.0
     */
    interface Widgets {
        class PC {
            private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(Widgets.class);

            static String getString( final String id ) {
                return XmlImporterUiPlugin.getDefault().getPluginUtil().getString(I18N_PREFIX + id);
            }
        }

        String CLASS_NAME_LABEL = PC.getString("classNameLabel"); //$NON-NLS-1$
        String DRIVER_LABEL = PC.getString("driverLabel"); //$NON-NLS-1$
        String NAME_LABEL = PC.getString("nameLabel"); //$NON-NLS-1$
        String URL_LABEL = PC.getString("urlLabel"); //$NON-NLS-1$
        String URL_SYNTAX_LABEL = PC.getString("urlSyntaxLabel"); //$NON-NLS-1$
        String USER_NAME_LABEL = PC.getString("userNameLabel"); //$NON-NLS-1$
        String SELECT_DRIVER_ITEM = "<Select Driver>"; //$NON-NLS-1$
    }

    interface ProductInfo {
        String PRODUCT = "MetaBase Modeler"; //$NON-NLS-1$
        String VERSION = "4.2"; //$NON-NLS-1$
        String DELIMETER = "/"; //$NON-NLS-1$

        interface Capabilities {
            String VIRTUAL_MODELING = "Virtual Modeling"; //$NON-NLS-1$
            String RELATIONAL_VIRTUAL_MODELING = VIRTUAL_MODELING + DELIMETER + "Relational"; //$NON-NLS-1$
            String XML_VIRTUAL_MODELING = VIRTUAL_MODELING + DELIMETER + "XML"; //$NON-NLS-1$

            String IMPORT = "Importer"; //$NON-NLS-1$
            String IMPORT_43 = "Import"; //$NON-NLS-1$
            String JDBC_IMPORT = IMPORT + DELIMETER + "JDBC"; //$NON-NLS-1$
            String JDBC_IMPORT_43 = PRODUCT + DELIMETER + IMPORT_43 + DELIMETER + "JDBC"; //$NON-NLS-1$
            String RATIONAL_ROSE_IMPORT = IMPORT + DELIMETER + "Rational Rose"; //$NON-NLS-1$
            String ERWIN_IMPORT = IMPORT + DELIMETER + "ERwin"; //$NON-NLS-1$

            String EXPORT = "Export"; //$NON-NLS-1$
            String RDBMS_EXPORT = EXPORT + DELIMETER + "RDBMS"; //$NON-NLS-1$

            String MODELGEN = "Model Generation"; //$NON-NLS-1$
            String RELATIONAL_MODELGEN = MODELGEN + DELIMETER + "Relational"; //$NON-NLS-1$
            String RELATIONAL_FROM_UML_MODELGEN = RELATIONAL_MODELGEN + DELIMETER + "From UML"; //$NON-NLS-1$

            String VDB_MGMT = "VDB Management"; //$NON-NLS-1$

            String REPOSITORY = "Repository Manager"; //$NON-NLS-1$
        }
    }
}
