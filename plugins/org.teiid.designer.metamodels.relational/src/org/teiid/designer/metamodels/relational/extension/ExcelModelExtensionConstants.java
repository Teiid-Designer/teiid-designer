package org.teiid.designer.metamodels.relational.extension;

import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.NamespaceProvider;

public interface ExcelModelExtensionConstants{
	String MED_PROBLEM_MARKER_ID = "org.teiid.designer.extension.ui.medMarker";  //$NON-NLS-1$

    /**
     * 
     */
    NamespaceProvider NAMESPACE_PROVIDER = new NamespaceProvider() {

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespacePrefix()
         */
        @Override
        public String getNamespacePrefix() {
            return "excel"; //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespaceUri()
         */
        @Override
        public String getNamespaceUri() {
            return "http://www.jboss.org/teiiddesigner/ext/rest/2012"; //$NON-NLS-1$
        }
    };

    /**
     * The fully qualified extension property definition identifiers.
     */
    interface PropertyIds {

        /**
         * The property definition identifer for the excel table cell number. Applies to column
         */
        String CELL_NUMBER = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "CELL_NUMBER"); //$NON-NLS-1$

        /**
         * The property definition identifer for the excel table cell number.
         */
        String FILE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "FILE"); //$NON-NLS-1$

        /**
         * The property definition identifer for the excel table cell number.
         */
        String FIRST_DATA_ROW_NUMBER = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "FIRST_DATA_ROW_NUMBER"); //$NON-NLS-1$

    }

}
