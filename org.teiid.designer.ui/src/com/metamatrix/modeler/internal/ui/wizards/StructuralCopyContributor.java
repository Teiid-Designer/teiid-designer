/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.ui.wizards.INewModelWizardContributor;
import com.metamatrix.ui.UiConstants;

/**
 * StructuralCopyContributor
 */
public class StructuralCopyContributor implements INewModelWizardContributor, UiConstants {

    private IWizardPage[] pages;
    private StructuralCopyWizardPage structuralCopyPage;
    
    /**
     * Construct an instance of StructuralCopyContributor.
     * 
     */
    public StructuralCopyContributor() {
        super();
    }
    
    /** 
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#canFinishEarly(org.eclipse.jface.wizard.IWizardPage)
     * @since 4.2
     */
    public boolean canFinishEarly(IWizardPage theCurrentPage) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#createWizardPages(org.eclipse.swt.widgets.Composite, org.eclipse.core.resources.IProject, com.metamatrix.modeler.core.MetamodelDescriptor, boolean)
     */
    public void createWizardPages(
        ISelection selection,
        IResource targetResource,
        IPath targetFilePath,
        MetamodelDescriptor descriptor,
        boolean isVirtual) {
        
        pages = new IWizardPage[1];
        
        structuralCopyPage = new StructuralCopyWizardPage(selection, descriptor,
        		isVirtual);
        pages[0] = structuralCopyPage;
    }
    
    /** 
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#doCancel()
     * @since 4.2
     */
    public void doCancel() {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#doFinish(com.metamatrix.modeler.core.workspace.ModelResource)
     */
    public void doFinish(ModelResource modelResource, IProgressMonitor monitor) {
    	String transactionName = Util.getString("StructuralCopyContributor.copyExistingModelTransactionName"); //$NON-NLS-1$
    	boolean started = ModelerCore.startTxn(transactionName,this);
        boolean succeeded = false;
    	try {
			IStructuralCopyTreePopulator populator = structuralCopyPage.getTreePopulator();
			TreeViewer viewer = structuralCopyPage.getViewer();
			if ((populator != null) && (viewer != null)) {
				try {
                    // tree was disabled; copy everything:
                    populator.copyModel((ModelResource) viewer.getInput(), modelResource, null, structuralCopyPage.isCopyAllDescriptions(), monitor);
				} catch (Exception ex) {
                    UiConstants.Util.log(IStatus.ERROR, ex, ex.getMessage());
				}
			}
            succeeded = true;
    	} finally {
    		if (started) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
    		}
    	}
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#getWizardPages()
     */
    public IWizardPage[] getWizardPages() {
        return pages;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#inputChanged(org.eclipse.core.resources.IProject, com.metamatrix.modeler.core.MetamodelDescriptor, boolean)
     */
    public void inputChanged(
        ISelection selection,
        IResource targetResource,
        MetamodelDescriptor descriptor,
        boolean isVirtual) {
    }

    public void currentPageChanged(IWizardPage page) {
        // unneeded, for now
    }
}
