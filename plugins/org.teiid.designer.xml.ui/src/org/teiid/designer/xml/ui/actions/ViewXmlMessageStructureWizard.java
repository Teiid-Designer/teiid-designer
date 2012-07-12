/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.xsd.XSDElementDeclaration;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.wizards.AbstractNewModelContributorWizard;
import org.teiid.designer.ui.wizards.INewModelWizardContributor;
import org.teiid.designer.xml.ui.ModelerXmlUiConstants;
import org.teiid.designer.xml.ui.ModelerXmlUiPlugin;
import org.teiid.designer.xml.ui.wizards.VirtualDocumentWizardContributor;



/** 
 * @since 5.0.2
 */
public class ViewXmlMessageStructureWizard extends AbstractNewModelContributorWizard {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private VirtualDocumentWizardContributor contributor;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ViewXmlMessageStructureWizard(ModelResource theModelResource,
                                         ISelection theSchemaFileSelection) {
        super(ModelerXmlUiPlugin.getDefault(),
              ModelerXmlUiConstants.Util.getString(I18nUtil.getPropertyPrefix(ViewXmlMessageStructureWizard.class) + "title"), //$NON-NLS-1$
              null,
              theModelResource,
              theSchemaFileSelection);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private VirtualDocumentWizardContributor accessContributor() {
        return (VirtualDocumentWizardContributor)getContributor();
    }
    
    /** 
     * @see org.teiid.designer.ui.wizards.AbstractNewModelContributorWizard#getContributor()
     * @since 5.0.2
     */
    @Override
    protected INewModelWizardContributor getContributor() {
        if (this.contributor == null) {
            this.contributor = new VirtualDocumentWizardContributor();
        }
        
        return this.contributor;
    }
    
    /** 
     * @see org.teiid.designer.ui.wizards.AbstractNewModelContributorWizard#getMetamodelDescriptor()
     * @since 5.0.2
     */
    @Override
    protected MetamodelDescriptor getMetamodelDescriptor() {
        return null;
    }
    
    /** 
     * @see org.teiid.designer.ui.wizards.AbstractNewModelContributorWizard#getSelection()
     * @since 5.0.2
     */
    @Override
    protected ISelection getSelection() {
        if (this.selection == null) {
            this.selection = new StructuredSelection(getFile());
        }
        
        return super.getSelection();
    }

    /** 
     * @see org.teiid.designer.ui.wizards.AbstractNewModelContributorWizard#isVirtual()
     * @since 5.0.2
     */
    @Override
    protected boolean isVirtual() {
        return true;
    }

    /**
     * Sets the document roots for the new model. 
     * @param theRoots the XSD root elements
     * @since 5.0.2
     */
    public void setDocumentRoots(XSDElementDeclaration[] theRoots) {
        accessContributor().setDocumentRoots(theRoots);
    }
    
}
