/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.builder.execution;

import org.eclipse.emf.ecore.EcorePackage;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.relational.RelationalPackage;

/**
 * Defines the constants used in the MetaModel entity builder process.
 */
public interface MetamodelBuilderConstants {
    // Plug-in constants
    final String pluginID = MetamodelBuilderExecutionPlugin.PLUGIN_ID;
    final PluginUtil UTIL = MetamodelBuilderExecutionPlugin.Util;

    // Structural feature keys
    final String DESCRIPTION = "description"; //$NON-NLS-1$
    static final String NAME = "name"; //$NON-NLS-1$

    // URI Constants
    final String MM_PREFIX = "MM:"; //$NON-NLS-1$
    final String XSD_PREFIX = "XSD:"; //$NON-NLS-1$
    final String POUND = "#"; //$NON-NLS-1$
    final String MM_URI = "http://www.metamatrix.com/metamodels/"; //$NON-NLS-1$
    final String ECORE_URI = EcorePackage.eNS_URI + "#//"; //$NON-NLS-1$
    final String XSD_RESOURCE_URI = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
    final String XSD_DT_URI = XSD_RESOURCE_URI + POUND;
    final String MM_DT_RESOURCE_URI = "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"; //$NON-NLS-1$
    final String MM_DT_URI = MM_DT_RESOURCE_URI + POUND;
    final String RELATIONAL_URI = RelationalPackage.eNS_URI + "#//"; //$NON-NLS-1$
    final String EXTENSION_URI = ExtensionPackage.eNS_URI + "#//"; //$NON-NLS-1$

    // Primary MetaModel Types
    final int UNKNOWN_MODEL = -1;
    final int RELATIONAL_MODEL = 1;
    final int EXTENSION_MODEL = 2;
    final int RELATIONSHIP_MODEL = 3;
    final int XSD_MODEL = 4;
    final int UML_MODEL = 5;

    final String RELATIONAL_STR = "Relational"; //$NON-NLS-1$
    final String EXTENSION_STR = "Extension"; //$NON-NLS-1$
    final String RELATIONSHIP_STR = "Relationship"; //$NON-NLS-1$
    final String XSD_STR = "XSD"; //$NON-NLS-1$
    final String UML_STR = "UML"; //$NON-NLS-1$
    final String VIRTUAL_STR = "Virtual"; //$NON-NLS-1$
    final String PHYSICAL_STR = "Physical"; //$NON-NLS-1$
    final String MODEL_EXT = ".xmi"; //$NON-NLS-1$
    final String PATH_SEPARATOR = "\\"; //$NON-NLS-1$

    // Extensions Schema Tables
    final String XCLASS_TABLE = "XClass"; //$NON-NLS-1$
    final String XPACKAGE_TABLE = "XPackage"; //$NON-NLS-1$
    final String XATTRIBUTE_TABLE = "XAttribute"; //$NON-NLS-1$
    final String XENUM_TABLE = "XEnum"; //$NON-NLS-1$
    final String XENUM_LITERAL_TABLE = "XEnumLiteral"; //$NON-NLS-1$

    // Processing order for the Extensions Schema Tables
    final String[] EXTENSIONS_PROCESSING_ORDER = new String[] {XPACKAGE_TABLE, XCLASS_TABLE, XENUM_TABLE, XATTRIBUTE_TABLE,
        XENUM_LITERAL_TABLE};

    // Relational Schema Tables
    final String CATALOG_TABLE = "Catalog"; //$NON-NLS-1$
    final String SCHEMA_TABLE = "Schema"; //$NON-NLS-1$
    final String BASETABLE_TABLE = "BaseTable"; //$NON-NLS-1$
    final String COLUMN_TABLE = "Column"; //$NON-NLS-1$
    final String INDEX_TABLE = "Index"; //$NON-NLS-1$
    final String FOREIGN_KEY_TABLE = "ForeignKey"; //$NON-NLS-1$
    final String PRIMARY_KEY_TABLE = "PrimaryKey"; //$NON-NLS-1$
    final String VIEW_TABLE = "View"; //$NON-NLS-1$
    final String PROCEDURE_TABLE = "Procedure"; //$NON-NLS-1$
    final String PROCEDURE_PARAMETER_TABLE = "ProcedureParameter"; //$NON-NLS-1$
    final String PROCEDURE_RESULT_TABLE = "ProcedureResult"; //$NON-NLS-1$
    final String UNIQUE_CONSTRAINT_TABLE = "UniqueConstraint"; //$NON-NLS-1$

    // Processing order for the Relational Schema Tables
    final String[] RELATIONAL_PROCESSING_ORDER = new String[] {CATALOG_TABLE, SCHEMA_TABLE, BASETABLE_TABLE, VIEW_TABLE,
        PROCEDURE_TABLE, COLUMN_TABLE, PROCEDURE_RESULT_TABLE, PROCEDURE_PARAMETER_TABLE, INDEX_TABLE, PRIMARY_KEY_TABLE,
        FOREIGN_KEY_TABLE, UNIQUE_CONSTRAINT_TABLE};

}
