/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.xsd.XSDElementDeclaration;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.mapping.factory.MappingClassBuilderStrategy;
import org.teiid.designer.ui.wizards.INewModelWizardContributor;
import org.teiid.designer.xml.ui.ModelerXmlUiConstants;
import org.teiid.designer.xml.ui.wizards.XMLDocumentWizard.DocSrcUpdater;


/**
 * VirtualDocumentWizardContributor
 *
 * @since 8.0
 */
public class VirtualDocumentWizardContributor implements INewModelWizardContributor,
		ModelerXmlUiConstants {

    private IWizardPage[] pages;
    private NewVirtualDocumentWizardPage newVirtualDocumentPage;
    private VirtualDocumentStatisticsWizardPage statsPage;
    private PreviewVirtualDocumentWizardPage previewVDocPage;
    private IWizardPage priorPage;
    private NewDocumentWizardModel model;
    private XSDElementDeclaration[] docRoots;
    private MappingClassBuilderStrategy strategy;

    /**
     * Construct an instance of VirtualDocumentWizardContributor.
     * 
     */
    public VirtualDocumentWizardContributor() {
        super();
    }
    
    /** 
     * @see org.teiid.designer.ui.wizards.INewModelWizardContributor#canFinishEarly(org.eclipse.jface.wizard.IWizardPage)
     * @since 4.2
     */
    @Override
	public boolean canFinishEarly(IWizardPage theCurrentPage) {
        return true; //theCurrentPage == newVirtualDocumentPage;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.wizards.INewModelWizardContributor#createWizardPages(org.eclipse.swt.widgets.Composite, org.eclipse.core.resources.IProject, org.teiid.designer.core.MetamodelDescriptor, boolean)
     */
    @Override
	public void createWizardPages(ISelection selection,
                                  IResource targetResource,
                                  IPath targetFilePath,
                                  MetamodelDescriptor descriptor,
                                  boolean isVirtual) {
        model = new NewDocumentWizardModel();
        newVirtualDocumentPage = new NewVirtualDocumentWizardPage(model, selection);
        model.setSource(newVirtualDocumentPage);
        statsPage = new VirtualDocumentStatisticsWizardPage(model);
        previewVDocPage = new PreviewVirtualDocumentWizardPage(model);
            
        pages = new IWizardPage[] { newVirtualDocumentPage, statsPage, previewVDocPage };
        
        // if document roots have been identified set them on the document wizard page
        if (this.docRoots != null) {
            this.newVirtualDocumentPage.setXsdRoots(this.docRoots);
        }
        
        // if strategy exists set it on the wizard page
        if (this.strategy != null) {
            this.newVirtualDocumentPage.setMappingClassBuilderStrategy(this.strategy);
        }
    }
    
    /** 
     * @see org.teiid.designer.ui.wizards.INewModelWizardContributor#doCancel()
     * @since 4.2
     */
    @Override
	public void doCancel() {
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.wizards.INewModelWizardContributor#doFinish(org.teiid.designer.core.workspace.ModelResource)
     */
    @Override
	public void doFinish(final ModelResource modelResource, final IProgressMonitor monitor) {
        IWizardContainer container = previewVDocPage.getWizard().getContainer();
        XMLDocumentWizard.finishWizard(newVirtualDocumentPage, previewVDocPage, modelResource, container, model);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.wizards.INewModelWizardContributor#getWizardPages()
     */
    @Override
	public IWizardPage[] getWizardPages() {
        return pages;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.wizards.INewModelWizardContributor#inputChanged(org.eclipse.swt.widgets.Composite, org.eclipse.core.resources.IProject, org.teiid.designer.core.MetamodelDescriptor, boolean)
     */
    @Override
	public void inputChanged(
        ISelection selection,
        IResource targetResource,
        MetamodelDescriptor descriptor,
        boolean isVirtual) {
    }

    @Override
	public void currentPageChanged(IWizardPage page) {
        // set up progress monitor:
        IWizard wizard = page.getWizard(); // note that wizard can change through subsequent calls to this method
        if (wizard instanceof Wizard) {
            Wizard wiz = (Wizard) wizard;
            wiz.setNeedsProgressMonitor(true);
        } //endif
        // make sure this is for the right page, and is a consequence
        //  of hitting 'next', not 'back'
        try {
            DocSrcUpdater dsi;

            if (priorPage == newVirtualDocumentPage
             && page == statsPage) {
                // current is stats, tell it to update:
                dsi = new DocSrcUpdater(statsPage, true, model.getSelectedFragmentCount());
            } else if (page == previewVDocPage) {
                // current is preview, tell it to update:
                dsi = new DocSrcUpdater(previewVDocPage, true, model.getEstimatedNodeCount());
            } else {
                // no updates needed.
                dsi = null;
            } // endif

            if (dsi != null) {
                wizard.getContainer().run(true, true, dsi);
            } // endif
        } catch (Exception ex) {
            Util.log(ex);
        } // endtry

        priorPage = page;
    }
    
    /**
     * Sets the XSD elements to use as document root elements in the new model. 
     * @param theRoots the roots
     * @since 5.0.2
     */
    public void setDocumentRoots(XSDElementDeclaration[] theRoots) {
        this.docRoots = theRoots;
        
        if (this.newVirtualDocumentPage != null) {
            this.newVirtualDocumentPage.setXsdRoots(theRoots);
        }
    }
    
    /**
     * Sets the mapping strategy for the new model. 
     * @param theStrategy the strategy
     * @since 5.0.2
     */
    public void setMappingClassBuilderStrategy(MappingClassBuilderStrategy theStrategy) {
        this.strategy = theStrategy;
        
        if (this.newVirtualDocumentPage != null) {
            this.newVirtualDocumentPage.setMappingClassBuilderStrategy(theStrategy);
        }
    }

	@Override
	public ModelResource getSelectedModelResource() {
		return null;
	}
	
	@Override
    public boolean copyAllDescriptions() { 
		return false;
	}
}
