/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.teiid.designer.extension.definition.ModelExtensionDefinitionHeader;

/**
 * 
 */
public interface Constants {

    String TESTDATA = "testdata"; //$NON-NLS-1$

    String FUNCTION_METAMODEL = "http://www.metamatrix.com/metamodels/MetaMatrixFunction"; //$NON-NLS-1$
    String RELATIONAL_METAMODEL = "http://www.metamatrix.com/metamodels/Relational"; //$NON-NLS-1$
    String WEB_SERVICE_METAMODEL = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$
    String XML_METAMODEL = "http://www.metamatrix.com/metamodels/XmlDocument"; //$NON-NLS-1$

    String MED_SCHEMA = TESTDATA + File.separatorChar + ExtensionConstants.SCHEMA_FILENAME;

    String TABLE_METACLASS_NAME = "com.metamatrix.metamodels.relational.impl.BaseTableImpl"; //$NON-NLS-1$
    String COLUMN_METACLASS_NAME = "com.metamatrix.metamodels.relational.impl.ColumnImpl"; //$NON-NLS-1$
    String PROCEDURE_METACLASS_NAME = "com.metamatrix.metamodels.relational.impl.ProcedureImpl"; //$NON-NLS-1$

    String SALESFORCE_MED_FILE_NAME = TESTDATA + File.separatorChar + "salesforce.mxd"; //$NON-NLS-1$
    String SALESFORCE_MED_PREFIX = "salesforce"; //$NON-NLS-1$
    String SALESFORCE_MED_URI = "org.teiid.designer.extension.salesforce"; //$NON-NLS-1$
    String[] SALESFORCE_MED_METACLASSES = new String[] { TABLE_METACLASS_NAME, COLUMN_METACLASS_NAME };
    String[] SALESFORCE_METACLASS_0_PROP_IDS = new String[] { "custom", "supportsCreate", "supportsDelete", "supportsIdLookup", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "supportsMerge", "supportsQuery", "supportsReplicate", "supportsRetrieve", "supportsSearch" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    String[] SALESFORCE_METACLASS_1_PROP_IDS = new String[] { "custom", "calculated", "defaultedOnCreate", "picklistValues" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    String REST_MED_FILE_NAME = TESTDATA + File.separatorChar + "rest.mxd"; //$NON-NLS-1$
    String REST_MED_PREFIX = "rest"; //$NON-NLS-1$
    String REST_MED_URI = "org.teiid.designer.extension.rest"; //$NON-NLS-1$
    String[] REST_MED_METACLASSES = new String[] { PROCEDURE_METACLASS_NAME };
    String[] REST_METACLASS_0_PROP_IDS = new String[] { "restMethod", "uri" }; //$NON-NLS-1$ //$NON-NLS-2$

    String SOURCE_FUNCTION_MED_FILE_NAME = TESTDATA + File.separatorChar + "sourcefunction.mxd"; //$NON-NLS-1$
    String SOURCE_FUNCTION_MED_PREFIX = "sourcefunction"; //$NON-NLS-1$
    String SOURCE_FUNCTION_MED_URI = "org.teiid.designer.extension.sourcefunction"; //$NON-NLS-1$
    String[] SOURCE_FUNCTION_MED_METACLASSES = new String[] { PROCEDURE_METACLASS_NAME };
    String[] SOURCE_FUNCTION_METACLASS_0_PROP_IDS = new String[] { "deterministic" }; //$NON-NLS-1$

    String DEPRECATED_MED_FILE_NAME = TESTDATA + File.separatorChar + "deprecated.mxd"; //$NON-NLS-1$
    String DEPRECATED_MED_PREFIX = "ext-custom"; //$NON-NLS-1$
    String DEPRECATED_MED_URI = "org.teiid.designer.extension.deprecated"; //$NON-NLS-1$
    String[] DEPRECATED_MED_METACLASSES = new String[] { PROCEDURE_METACLASS_NAME };
    String[] DEPRECATED_METACLASS_0_PROP_IDS = new String[] { "deterministic", "REST-METHOD", "URI" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    String[] BUILT_IN_MEDS = new String[] { SALESFORCE_MED_FILE_NAME, REST_MED_FILE_NAME, SOURCE_FUNCTION_MED_FILE_NAME,
            DEPRECATED_MED_FILE_NAME };
    String EMPTY_MED_FILE_NAME = TESTDATA + File.separatorChar + "emptyMed.mxd"; //$NON-NLS-1$

    String DEFAULT_MED_DESCRIPTION = "This is a MED description"; //$NON-NLS-1$
    String DEFAULT_NAMESPACE_PREFIX = SALESFORCE_MED_PREFIX;
    String DEFAULT_NAMESPACE_URI = SALESFORCE_MED_URI;
    String DEFAULT_METAMODEL_URI = RELATIONAL_METAMODEL;
    String DEFAULT_VERSION = String.valueOf(ModelExtensionDefinitionHeader.DEFAULT_VERSION);
    String DEFAULT_METACLASS = TABLE_METACLASS_NAME;

    public class Utils {
        public static Set<String> getExtendableMetamodelUris() {
            Set<String> metamodelUris = new HashSet<String>();
            metamodelUris.add(FUNCTION_METAMODEL);
            metamodelUris.add(RELATIONAL_METAMODEL);
            metamodelUris.add(WEB_SERVICE_METAMODEL);
            metamodelUris.add(XML_METAMODEL);
            return metamodelUris;
        }
    }

}
