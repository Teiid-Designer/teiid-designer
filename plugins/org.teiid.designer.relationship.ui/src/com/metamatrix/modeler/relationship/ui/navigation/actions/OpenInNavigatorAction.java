/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionDelegate;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationView;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * OpenInNavigatorAction is the global action for opening the selected EObject in the
 * Navigator View.
 */
public class OpenInNavigatorAction extends ActionDelegate implements IWorkbenchWindowActionDelegate,
                                                                    IViewActionDelegate {

    private ISelection selection;


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(IAction action) {
        EObject obj = SelectionUtilities.getSelectedEObject(selection);
        try {
            IViewPart viewPart = UiUtil.getWorkbenchPage().showView(UiConstants.Extensions.Navigator.VIEW_ID);
            if ( viewPart instanceof NavigationView ) {
                ((NavigationView) viewPart).setCurrentObject(obj);
            }
        } catch (final PartInitException err) {
            UiConstants.Util.log(err);
            WidgetUtil.showError(err.getLocalizedMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        boolean enable = false;
        this.selection = selection;
        if ( SelectionUtilities.isSingleSelection(selection) ) {
            if ( SelectionUtilities.getSelectedEObject(selection) != null ) {
                enable = true;
            }
        }
        action.setEnabled(enable);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {    }

}
