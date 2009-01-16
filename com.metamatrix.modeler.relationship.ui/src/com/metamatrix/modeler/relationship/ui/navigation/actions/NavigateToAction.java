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

package com.metamatrix.modeler.relationship.ui.navigation.actions;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.relationship.NavigationLink;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.UiPlugin;
import com.metamatrix.modeler.ui.UiConstants.Extensions;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * NavigateToAction
 */
public class NavigateToAction extends Action implements ISelectionListener, UiConstants.Images {

    private static final String LABEL = UiConstants.Util.getString("NavigateToAction.label"); //$NON-NLS-1$
    private static final String TOOLTIP = UiConstants.Util.getString("NavigateToAction.tooltip"); //$NON-NLS-1$

    private ISelectionProvider selectionProvider;
    private URI selectedUri;

    /**
     * Construct an instance of NavigateToAction.
     */
    public NavigateToAction(ISelectionProvider selectionProvider) {
        super();
        this.selectionProvider = selectionProvider;
        setText(LABEL);
        setToolTipText(TOOLTIP);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(FIND));
        setEnabled(false); 
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        try {
            // resolve the object
            EObject obj = ModelerCore.getModelContainer().getEObject(selectedUri, true);
            // send the selected EObject as a new selection in the workbench
            final ISelection selection = new StructuredSelection(obj);
            selectionProvider.setSelection(selection);
            // activate the Model Explorer view (must do this last)
            Display.getCurrent().asyncExec(new Runnable() {
                public void run() {
                    try {
                        ModelExplorerResourceNavigator view = 
                            (ModelExplorerResourceNavigator) UiUtil.getWorkbenchPage().showView(Extensions.Explorer.VIEW);
                        view.getTreeViewer().setSelection(selection);
                    } catch (PartInitException err) {
                        UiConstants.Util.log(err);
                        WidgetUtil.showError(err.getLocalizedMessage());
                    }
                }
            });
        } catch (Exception err) {
            UiConstants.Util.log(err);
            WidgetUtil.showError(err.getLocalizedMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        setEnabled(false);
        if ( SelectionUtilities.isSingleSelection(selection) ) {
            Object selectedObject = SelectionUtilities.getSelectedObject(selection);
            if ( selectedObject instanceof NavigationNode ) {
                this.selectedUri = ((NavigationNode) selectedObject).getModelObjectUri();
                setEnabled(selectedUri != null);
            } else if ( selectedObject instanceof NavigationLink ) {
                this.selectedUri = ((NavigationLink) selectedObject).getModelObjectUri();
                // there may not be a model object representing this link, so check
                setEnabled(this.selectedUri != null);
            }
        }
    }

}
