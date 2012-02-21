/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;

import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;

/**
 * @since 5.0.1
 */
public class ImportMetadataAction extends Action implements AdvisorUiConstants {
	
	public static final String TEIID_FLAT_FILE = "teiidMetadataImportWizard"; //$NON-NLS-1$
	public static final String TEIID_XML_FILE = "teiidXmlImportWizard";  //$NON-NLS-1$
	public static final String JDBC = "jdbcImportWizard"; //$NON-NLS-1$
	public static final String WSDL_TO_WEB_SERVICE = "wsdlFileSystemImportWizard"; //$NON-NLS-1$
	public static final String XML_SCHEMA = "xsdFileSystemImportWizard"; //$NON-NLS-1$
	public static final String WSDL_TO_RELATIONAL = "RelationalFromWSDLImportWizard"; //$NON-NLS-1$
	public static final String SALESFORCE_TO_RELATIONAL = "SalesforceToRelationalImportWizard"; //$NON-NLS-1$
	public static final String DDL_TO_RELATIONAL = "org.teiid.designer.ddl.importer.ui.ddlImportWizard"; //$NON-NLS-1$
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public ImportMetadataAction() {
        setText("Import Action"); //$NON-NLS-1$
        setToolTipText("Import Action Tooltip"); //$NON-NLS-1$
        //setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(Images.IMPORT));

    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public void launchWizard(String wizardID) {
        ModelerUiViewUtils.launchWizard(wizardID, new StructuredSelection(), true);
    }

}
