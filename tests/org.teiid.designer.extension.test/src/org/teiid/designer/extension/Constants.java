/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.teiid.designer.extension.definition.ModelExtensionDefinitionHeader;
import org.teiid.designer.extension.properties.Translation;

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

    String SALESFORCE_MED_FILE_NAME = TESTDATA + File.separatorChar + "salesforce." + ExtensionConstants.MED_EXTENSION; //$NON-NLS-1$
    String SALESFORCE_MED_PREFIX = "salesforce"; //$NON-NLS-1$
    String SALESFORCE_MED_URI = "org.teiid.designer.extension.salesforce"; //$NON-NLS-1$
    String[] SALESFORCE_MED_METACLASSES = new String[] { TABLE_METACLASS_NAME, COLUMN_METACLASS_NAME };
    String[] SALESFORCE_METACLASS_0_PROP_IDS = new String[] { "custom", "supportsCreate", "supportsDelete", "supportsIdLookup", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "supportsMerge", "supportsQuery", "supportsReplicate", "supportsRetrieve", "supportsSearch" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    String[] SALESFORCE_METACLASS_1_PROP_IDS = new String[] { "custom", "calculated", "defaultedOnCreate", "picklistValues" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    String REST_MED_FILE_NAME = TESTDATA + File.separatorChar + "rest." + ExtensionConstants.MED_EXTENSION; //$NON-NLS-1$
    String REST_MED_PREFIX = "rest"; //$NON-NLS-1$
    String REST_MED_URI = "org.teiid.designer.extension.rest"; //$NON-NLS-1$
    String[] REST_MED_METACLASSES = new String[] { PROCEDURE_METACLASS_NAME };
    String[] REST_METACLASS_0_PROP_IDS = new String[] { "restMethod", "uri" }; //$NON-NLS-1$ //$NON-NLS-2$

    String SOURCE_FUNCTION_MED_FILE_NAME = TESTDATA + File.separatorChar + "sourcefunction." + ExtensionConstants.MED_EXTENSION; //$NON-NLS-1$
    String SOURCE_FUNCTION_MED_PREFIX = "sourcefunction"; //$NON-NLS-1$
    String SOURCE_FUNCTION_MED_URI = "org.teiid.designer.extension.sourcefunction"; //$NON-NLS-1$
    String[] SOURCE_FUNCTION_MED_METACLASSES = new String[] { PROCEDURE_METACLASS_NAME };
    String[] SOURCE_FUNCTION_METACLASS_0_PROP_IDS = new String[] { "deterministic" }; //$NON-NLS-1$

    String DEPRECATED_MED_FILE_NAME = TESTDATA + File.separatorChar + "deprecated." + ExtensionConstants.MED_EXTENSION; //$NON-NLS-1$
    String DEPRECATED_MED_PREFIX = "ext-custom"; //$NON-NLS-1$
    String DEPRECATED_MED_URI = "org.teiid.designer.extension.deprecated"; //$NON-NLS-1$
    String[] DEPRECATED_MED_METACLASSES = new String[] { PROCEDURE_METACLASS_NAME };
    String[] DEPRECATED_METACLASS_0_PROP_IDS = new String[] { "deterministic", "REST-METHOD", "URI" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    String[] BUILT_IN_MEDS = new String[] { SALESFORCE_MED_FILE_NAME, REST_MED_FILE_NAME, SOURCE_FUNCTION_MED_FILE_NAME,
            DEPRECATED_MED_FILE_NAME };
    String EMPTY_MED_FILE_NAME = TESTDATA + File.separatorChar + "emptyMed." + ExtensionConstants.MED_EXTENSION; //$NON-NLS-1$
    String TEMP_MED_FILE_NAME = TESTDATA + File.separatorChar + "tempMed." + ExtensionConstants.MED_EXTENSION; //$NON-NLS-1$

    String DEFAULT_MED_DESCRIPTION = "This is a MED description"; //$NON-NLS-1$
    String DEFAULT_NAMESPACE_PREFIX = SALESFORCE_MED_PREFIX;
    String DEFAULT_NAMESPACE_URI = SALESFORCE_MED_URI;
    String DEFAULT_METAMODEL_URI = RELATIONAL_METAMODEL;
    String DEFAULT_VERSION = String.valueOf(ModelExtensionDefinitionHeader.DEFAULT_VERSION);
    String DEFAULT_METACLASS = TABLE_METACLASS_NAME;

    Locale[] DEFAULT_LOCALES = new Locale[] { Locale.CANADA, Locale.CHINA, Locale.ENGLISH, Locale.FRANCE, Locale.GERMAN };
    String[] DEFAULT_TRANSLATION_TEXTS = new String[] { DEFAULT_LOCALES[0].toString(), DEFAULT_LOCALES[1].toString(),
            DEFAULT_LOCALES[2].toString(), DEFAULT_LOCALES[3].toString(), DEFAULT_LOCALES[4].toString() };
    Translation[] DEFAULT_TRANSLATIONS = new Translation[] { new Translation(DEFAULT_LOCALES[0], DEFAULT_TRANSLATION_TEXTS[0]),
            new Translation(DEFAULT_LOCALES[1], DEFAULT_TRANSLATION_TEXTS[1]),
            new Translation(DEFAULT_LOCALES[2], DEFAULT_TRANSLATION_TEXTS[2]),
            new Translation(DEFAULT_LOCALES[3], DEFAULT_TRANSLATION_TEXTS[3]),
            new Translation(DEFAULT_LOCALES[4], DEFAULT_TRANSLATION_TEXTS[4]) };
    Locale NON_DEFAULT_LOCALE = Locale.US;
    Translation NON_DEFAULT_TRANSLATION = new Translation(NON_DEFAULT_LOCALE, NON_DEFAULT_LOCALE.toString());

    String[] DEFAULT_STRING_ALLOWED_VALUES = new String[] { "value-0", "value-1", "value-2", "value-3", "value-4" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    public class Utils {
        public static Set<String> getExtendableMetamodelUris() {
            Set<String> metamodelUris = new HashSet<String>();
            metamodelUris.add(FUNCTION_METAMODEL);
            metamodelUris.add(RELATIONAL_METAMODEL);
            metamodelUris.add(WEB_SERVICE_METAMODEL);
            metamodelUris.add(XML_METAMODEL);
            return metamodelUris;
        }

        public static Set<String> getStringAllowedValues() {
            return new HashSet<String>(Factory.createDefaultStringAllowedValues());
        }

        public static Collection<String> getStringAllowedValuesWithDuplicates() {
            List<String> allowedValues = new ArrayList<String>(Factory.createDefaultStringAllowedValues());
            allowedValues.add(0, DEFAULT_STRING_ALLOWED_VALUES[0]);
            allowedValues.add(DEFAULT_STRING_ALLOWED_VALUES[0]);
            return allowedValues;
        }

        public static Set<Translation> getTranslations() {
            return new HashSet<Translation>(Factory.createDefaultTranslations());
        }

        public static Collection<Translation> getTranslationsWithDuplicates() {
            List<Translation> translations = new ArrayList<Translation>(Factory.createDefaultTranslations());
            translations.add(0, DEFAULT_TRANSLATIONS[0]);
            translations.add(DEFAULT_TRANSLATIONS[0]);
            return translations;
        }
    }

}
