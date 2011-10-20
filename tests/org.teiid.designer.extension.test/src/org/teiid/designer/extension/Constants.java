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
    String SALESFORCE_MED = TESTDATA + File.separatorChar + "salesforce.mxd"; //$NON-NLS-1$

    String SALESFORCE_MED_PREFIX = "salesforce"; //$NON-NLS-1$
    String SALESFORCE_MED_URI = "org.teiid.designer.extension.salesforce"; //$NON-NLS-1$

    String DEFAULT_MED_DESCRIPTION = "This is a MED description"; //$NON-NLS-1$
    String DEFAULT_NAMESPACE_PREFIX = SALESFORCE_MED_PREFIX;
    String DEFAULT_NAMESPACE_URI = SALESFORCE_MED_URI;
    String DEFAULT_METAMODEL_URI = RELATIONAL_METAMODEL;
    String DEFAULT_VERSION = String.valueOf(ModelExtensionDefinitionHeader.DEFAULT_VERSION);

    public class Utils {
        static public Set<String> getExtendableMetamodelUris() {
            Set<String> metamodelUris = new HashSet<String>();
            metamodelUris.add(FUNCTION_METAMODEL);
            metamodelUris.add(RELATIONAL_METAMODEL);
            metamodelUris.add(WEB_SERVICE_METAMODEL);
            metamodelUris.add(XML_METAMODEL);
            return metamodelUris;
        }
    }

}
