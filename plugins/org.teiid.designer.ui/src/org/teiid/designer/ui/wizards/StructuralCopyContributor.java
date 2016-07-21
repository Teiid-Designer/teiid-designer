/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.UiConstants;


/**
 * StructuralCopyContributor
 *
 * @since 8.0
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
     * @see org.teiid.designer.ui.wizards.INewModelWizardContributor#canFinishEarly(org.eclipse.jface.wizard.IWizardPage)
     * @since 4.2
     */
    @Override
	public boolean canFinishEarly(IWizardPage theCurrentPage) {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.wizards.INewModelWizardContributor#createWizardPages(org.eclipse.swt.widgets.Composite, org.eclipse.core.resources.IProject, org.teiid.designer.core.MetamodelDescriptor, boolean)
     */
    @Override
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
	public void doFinish(ModelResource modelResource, IProgressMonitor monitor) {
        Map<String, Boolean> extraProps = new HashMap<String, Boolean>();
        if( structuralCopyPage.doGenerateDefaultSQL() ) {
        	extraProps.put("generateDefaultSQL", true); //$NON-NLS-1$
        	extraProps.put("validate", true); //$NON-NLS-1$
        }

    	String transactionName = Util.getString("StructuralCopyContributor.copyExistingModelTransactionName"); //$NON-NLS-1$
    	boolean started = ModelerCore.startTxn(transactionName,this);
        boolean succeeded = false;
    	try {
			IStructuralCopyTreePopulator populator = structuralCopyPage.getTreePopulator();
			TreeViewer viewer = structuralCopyPage.getViewer();
			if ((populator != null) && (viewer != null)) {
				try {
                    // tree was disabled; copy everything:
                    populator.copyModel((ModelResource) viewer.getInput(), modelResource, extraProps, structuralCopyPage.isCopyAllDescriptions(), monitor);
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
     * @See org.teiid.designer.ui.wizards.INewModelWizardContributor#getWizardPages()
     */
    @Override
	public IWizardPage[] getWizardPages() {
        return pages;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.wizards.INewModelWizardContributor#inputChanged(org.eclipse.core.resources.IProject, org.teiid.designer.core.MetamodelDescriptor, boolean)
     */
    @Override
	public void inputChanged(
        ISelection selection,
        IResource targetResource,
        MetamodelDescriptor descriptor,
        boolean isVirtual) {
    	structuralCopyPage.setMetamodelDescriptor(descriptor);
    	structuralCopyPage.setTargetIsVirtual(isVirtual);
    }

    @Override
	public void currentPageChanged(IWizardPage page) {
        // unneeded, for now
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
