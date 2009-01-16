/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
