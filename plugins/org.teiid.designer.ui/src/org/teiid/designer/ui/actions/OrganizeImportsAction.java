/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.actions.ActionDelegate;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ImportContainer;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * OrganizeImportsAction
 *
 * @since 8.0
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
    public void run( final IAction action ) {
        final ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(resource);
        if (modelResource != null) {

            // Defect 23823 - switched to use a new Modeler Core utility.
            try {
                ModelBuildUtil.rebuildImports(modelResource.getEmfResource(), true);
            } catch (final ModelWorkspaceException theException) {
                UiConstants.Util.log(IStatus.ERROR, theException, theException.getMessage());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( final IAction action,
                                  final ISelection selection ) {
        boolean enable = false;
        final Object o = SelectionUtilities.getSelectedObject(selection);
        if (o instanceof ImportContainer) {
            resource = ((ImportContainer)o).getResource();
            final ModelResource mResource = ModelUtilities.getModelResource(resource, false);
            enable = !mResource.isReadOnly();
        }
        action.setEnabled(enable);
        // BML 9/13/03 - I added this line (and accompanying text property) because I couldn't
        // figure out why the plugin.xml label ID wasn't being set correctly. It acted like it couldn't find
        // it, so I brute forced it here to get it working...
        action.setText(UiConstants.Util.getString("OrganizeImportsAction.label")); //$NON-NLS-1$
    }

}
