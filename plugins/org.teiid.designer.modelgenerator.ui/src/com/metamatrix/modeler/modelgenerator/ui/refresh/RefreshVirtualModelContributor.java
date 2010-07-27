/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.ui.refresh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.uml2.uml.UMLPackage;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.modelgenerator.ui.ModelGeneratorUiConstants;
import com.metamatrix.modeler.modelgenerator.ui.wizards.RefreshVirtualModelWizard;
import com.metamatrix.modeler.ui.actions.IRefreshContributor;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * RefreshVirtualModelContributor is an IRefreshContributor for refreshing virtual relational models that were built from UML2
 * models.
 */
public class RefreshVirtualModelContributor implements IRefreshContributor, ModelGeneratorUiConstants {

    private boolean enable = false;
    private IWorkbenchWindow window;
    private ModelResource modelResource;
    private IStructuredSelection selection;

    /**
     * Construct an instance of RefreshVirtualModelContributor.
     */
    public RefreshVirtualModelContributor() {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.actions.IRefreshContributor#canRefresh()
     */
    public boolean canRefresh() {
        return enable;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window ) {
        this.window = window;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action ) {
        try {
            RefreshVirtualModelWizard wizard = new RefreshVirtualModelWizard();
            wizard.init(window.getWorkbench(), selection);
            WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
            dialog.open();
        } catch (Exception e) {
            ModelGeneratorUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection)selection;
        }
        determineEnablement(selection);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection)selection;
        }
        determineEnablement(selection);
    }

    private void determineEnablement( ISelection selection ) {
        enable = false;

        modelResource = null;

        // Make sure selection contains one virtual, relational model
        Object obj = SelectionUtilities.getSelectedObject(selection);
        if ((obj instanceof IFile) && ModelUtilities.isModelFile((IFile)obj) && ((IFile)obj).exists()) {
            try {
                modelResource = ModelUtil.getModelResource((IFile)obj, false);

                // Can refresh a physical or virtual relational model if there is a relationship model in the
                // workspace and references this relational model and a UML model. We need the relationship
                // model to define the relational to UML mappings for the refresh operation. (Fix for defect 14401)
                if (modelResource != null) {
                    // defect 19085 - get rid of some alarming errors when there is a null primary metamodel descriptor
                    MetamodelDescriptor primary = modelResource.getPrimaryMetamodelDescriptor();
                    if (primary != null && RelationalPackage.eNS_URI.equals(primary.getNamespaceURI())) {
                        // Get the collection of models in the workspace that depend upon this relational model
                        Collection dependentModels = ModelUtilities.getResourcesThatUse(modelResource);
                        if (dependentModels.isEmpty()) {
                            return;
                        }

                        // For the models in the workspace that depend upon this relational model, get the
                        // subset of those models that are relationship models
                        Collection relationshipModels = getResourcesMatchingMetamodelURI(dependentModels,
                                                                                         ModelIdentifier.RELATIONSHIP_MODEL_URI);
                        if (relationshipModels.isEmpty()) {
                            return;
                        }

                        // Iterate over all relationship models to see if any of them import a UML model - this is
                        // the final check for enablement that we find a relationship model that imports both the
                        // selected relational model and a UML model
                        for (Iterator iter = relationshipModels.iterator(); iter.hasNext();) {
                            ModelResource mr = (ModelResource)iter.next();
                            if (mr != null) {
                                Collection importedResources = ModelUtilities.getDependentResources(mr);
                                if (getResourcesMatchingMetamodelURI(importedResources, UMLPackage.eNS_URI).size() > 0) {
                                    enable = true;
                                    break;
                                }
                            }
                        }
                    } // endif
                }
            } catch (ModelWorkspaceException e) {
                ModelGeneratorUiConstants.Util.log(e);
            }
        }
    }

    private static Collection getResourcesMatchingMetamodelURI( final Collection modelResources,
                                                                final String primaryMetamodelUri ) {
        final Collection result = new ArrayList(modelResources.size());
        if (primaryMetamodelUri != null) {
            for (Iterator iter = modelResources.iterator(); iter.hasNext();) {
                ModelResource mr = (ModelResource)iter.next();
                try {
                    if (mr != null && primaryMetamodelUri.equals(mr.getPrimaryMetamodelDescriptor().getNamespaceURI())) {
                        result.add(mr);
                    }
                } catch (ModelWorkspaceException e) {
                    ModelGeneratorUiConstants.Util.log(e);
                }
            }
        }
        return result;
    }

}
