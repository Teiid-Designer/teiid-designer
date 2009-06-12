/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.explorer;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * Helper class designed to handle transform concrete selections into other selection types Use case is the Import/Export wizards
 * may be enabled with "mixed" selections (i.e. EObjects & IResources). We need to interpret this and provide a single container
 * resource for the wizard initialization
 * 
 * @author BLaFond
 */
public class ModelExplorerSelectionHelper {

    private TreeViewer viewer;

    public ModelExplorerSelectionHelper(TreeViewer viewer) {
        super();
        this.viewer = viewer;
    }

    public IStructuredSelection getBestSelection() {
        return getBestSelection(viewer.getSelection());
    }

    public IStructuredSelection getBestSelection(ISelection theSelection) {
        IResource resrc = null;
        if (SelectionUtilities.isAllEObjects(theSelection)) {
            EObject eObj = (EObject)SelectionUtilities.getSelectedEObjects(theSelection).get(0);
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObj);
            if (mr != null) {
                try {
                    resrc = mr.getUnderlyingResource();
                } catch (ModelWorkspaceException e) {
                    UiConstants.Util.log(e);
                }
            }
            // Grab first one and get it's model resource's folder
        } else if (!SelectionUtilities.isEmptySelection(theSelection)) {
            Object obj = SelectionUtilities.getSelectedObjects(theSelection).get(0);
            if (obj instanceof EObject) {
                // get it's model resource's folder
                ModelResource mr = ModelUtilities.getModelResourceForModelObject((EObject)obj);
                if (mr != null) {
                    try {
                        resrc = mr.getUnderlyingResource();
                    } catch (ModelWorkspaceException e) {
                        UiConstants.Util.log(e);
                    }
                }
            } else if (obj instanceof IResource) {
                resrc = (IResource)obj;
            }
        }
        if (resrc == null) {
            return new StructuredSelection();
        }
        return new StructuredSelection(resrc);
    }

}
