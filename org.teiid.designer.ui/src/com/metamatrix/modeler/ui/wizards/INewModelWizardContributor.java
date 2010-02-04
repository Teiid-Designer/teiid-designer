/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * INewModelWizardContributor is an interface for the newModelWizardContributor extension point.
 * It allows a plugin to contribute pages to the NewModelWizard that will do additional work
 * to populate a model of a given MetamodelDescriptor.
 */
public interface INewModelWizardContributor {

    /**
     * Indicates if the wizard can finish on the specified page. The specified page should never be
     * the the last page of the wizard.
     * @param theCurrentPage the current wizard page
     * @return <code>true</code>if can finish; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean canFinishEarly(IWizardPage theCurrentPage);
    
    /**
     * Construct the set of IWizardPage instances that this builder will provide to the NewModelWizard.
     * @param pageContainer
     * @param targetResource the IResource within which the new model will be created.
     * @param targetFilePath the IPath of the target Model to be created.
     * @param descriptor the MetamodelDescriptor that will be the primary metamodel for the
     * resulting model file.
     * @param isVirtual true if resulting model will be made virtual.
     */
    void createWizardPages(
        ISelection selection, 
        IResource targetResource,
        IPath targetFilePath,
        MetamodelDescriptor descriptor,
        boolean isVirtual);
    
    /**
     * Obtain the array of IWizardPage instances for display in the NewModelWizard.  This method will be
     * called after createWizardPages and inputChanged.
     * @return
     */
    IWizardPage[] getWizardPages();
    
    /**
     * Notifies this contributor that the user has modified the inputs that were specified during
     * the call to createWizardPages.  It is up to the implementation to decide how to respond to
     * the changed input and if new wizard pages need to be constructed.  This call will be followed
     * by a call to getWizardPages.
     * @param pageContainer
     * @param targetResource
     * @param descriptor
     * @param isVirtual
     */
    void inputChanged( 
        ISelection selection, 
        IResource targetResource,
        MetamodelDescriptor descriptor,
        boolean isVirtual);
    
    /**
     * Provides this contributor with a constructed, empty model resource.  Implementation should respond
     * by populating the model based on the control settings on the wizard pages.  This method will
     * be called within the same transaction that created the model.
     * @param modelResource the model resource that was created by the NewModelWizard.
     * @param an IProgressMonitor to be used by the builder.
     */
    void doFinish(ModelResource modelResource, IProgressMonitor monitor);
    
    /**
     * Informs the contributor the new model wizard was cancelled by the user. 
     * @since 4.2
     */
    void doCancel();
    
    /** Informs the contributor that, to the best of the caller's knowledge, the current page has changed
      *  to the specified page.
      * @param page the new current page
      * @author PForhan
      */
    void currentPageChanged(IWizardPage page);
}
