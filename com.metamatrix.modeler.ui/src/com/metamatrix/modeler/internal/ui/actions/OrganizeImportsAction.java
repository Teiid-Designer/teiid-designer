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

package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.actions.ActionDelegate;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ImportContainer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * OrganizeImportsAction
 */
public class OrganizeImportsAction extends ActionDelegate {

    private Resource resource;

    /**
     * Construct an instance of OrganizeImportsAction.
     */
    public OrganizeImportsAction() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(IAction action) {
        ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(resource);
        if ( modelResource != null ) { 
            
            // Defect 23823 - switched to use a new Modeler Core utility.
            try {
                ModelBuildUtil.rebuildImports(modelResource.getEmfResource(), this, true);
            } catch (ModelWorkspaceException theException) {
                UiConstants.Util.log(IStatus.ERROR, theException, theException.getMessage());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        boolean enable = false;
        Object o = SelectionUtilities.getSelectedObject(selection);
        if ( o instanceof ImportContainer) {
            resource = ((ImportContainer) o).getResource();
                ModelResource mResource = ModelUtilities.getModelResource(resource, false);
                enable = !mResource.isReadOnly();
        }
        action.setEnabled(enable);
        // BML 9/13/03 - I added this line (and accompanying text property) because I couldn't
        // figure out why the plugin.xml label ID wasn't being set correctly. It acted like it couldn't find
        // it, so I brute forced it here to get it working...
        action.setText(UiConstants.Util.getString("OrganizeImportsAction.label")); //$NON-NLS-1$
    }

}
