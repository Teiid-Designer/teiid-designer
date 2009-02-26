/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards;

import java.util.Collections;
import java.util.Map;

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
import com.metamatrix.modeler.internal.transformation.util.TransformationNewModelObjectHelper;
import com.metamatrix.modeler.internal.ui.wizards.IStructuralCopyTreePopulator;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.ui.wizards.INewModelWizardContributor;

/**
 * TransformationLinkContributor is the NewModelWizard builder contributor for generating
 * a virtual model that is transformed from an existing model.
 */
public class TransformationLinkContributor implements INewModelWizardContributor, UiConstants {
    //
    // Class constants:
    //
    private static final Map MAP_CLEAR_SUPPORTS_UPDATES = Collections.singletonMap(TransformationNewModelObjectHelper.VIRTUAL_TABLE_CLEAR_SUPPORTS_UPDATE, Boolean.TRUE);
    private static final Map MAP_KEEP_SUPPORTS_UPDATES = Collections.singletonMap(TransformationNewModelObjectHelper.VIRTUAL_TABLE_CLEAR_SUPPORTS_UPDATE, Boolean.FALSE);

    //
    // Instance variables:
    //
    private IWizardPage[] pages;
    private TransformationLinkWizardPage transformationLinkPage;

    /**
     * Construct an instance of TransformationLinkContributor.
     */
    public TransformationLinkContributor() {
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
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#createWizardPages(org.eclipse.jface.viewers.ISelection, org.eclipse.core.resources.IProject, com.metamatrix.modeler.core.MetamodelDescriptor, boolean)
     */
    public void createWizardPages(
        	ISelection selection,
        	IResource targetResource,
            IPath targetFilePath,
        	MetamodelDescriptor descriptor,
        	boolean isVirtual) {

        pages = new IWizardPage[1];
        transformationLinkPage = new TransformationLinkWizardPage(
				"transformationLinkWizardPage", //$NON-NLS-1$
        		selection, descriptor, isVirtual);
        pages[0] = transformationLinkPage;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#getWizardPages()
     */
    public IWizardPage[] getWizardPages() {
        return pages;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#inputChanged(org.eclipse.jface.viewers.ISelection, org.eclipse.core.resources.IProject, com.metamatrix.modeler.core.MetamodelDescriptor, boolean)
     */
    public void inputChanged(
        ISelection selection,
        IResource targetResource,
        MetamodelDescriptor descriptor,
        boolean isVirtual) {

    }
    
    /** 
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#doCancel()
     * @since 4.2
     */
    public void doCancel() {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.wizards.INewModelWizardContributor#doFinish(com.metamatrix.modeler.core.workspace.ModelResource, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doFinish(ModelResource modelResource, IProgressMonitor monitor) {
		String transactionName = Util.getString("TransformationLinkContributor.transactionName"); //$NON-NLS-1$
		boolean started = ModelerCore.startTxn(transactionName, this);
        boolean succeeded = false;
		try {
			IStructuralCopyTreePopulator populator = transformationLinkPage.getTreePopulator();
			TreeViewer viewer = transformationLinkPage.getViewer();
            Map extraProperties = transformationLinkPage.isClearSupportsUpdate() ? MAP_CLEAR_SUPPORTS_UPDATES : MAP_KEEP_SUPPORTS_UPDATES;
			if ((populator != null) && (viewer != null)) {

                // all nodes selected, copy all:
                populator.copyModel((ModelResource) viewer.getInput(), modelResource, extraProperties, transformationLinkPage.isCopyAllDescriptions(), monitor);
			}
            succeeded = true;
        } catch (Exception ex) {
            String message = UiConstants.Util.getString("TransformationLinkContributor.doFinishError",     //$NON-NLS-1$
                                                      modelResource.getItemName()); 
            UiConstants.Util.log(IStatus.ERROR, ex, message); 
		} finally {
			if (started) {
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
			}
		}
    }

    public void currentPageChanged(IWizardPage page) {
        // unneeded, for now
    }
}
