/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.favorites.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionDelegate;

import com.metamatrix.modeler.internal.ui.favorites.FavoritesView;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 4.2
 */
public class AddToMetadataFavoritesAction extends ActionDelegate implements
                                                              IWorkbenchWindowActionDelegate,
                                                              IViewActionDelegate {

    private ISelection selection;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(IAction action) {
        Collection objs = new ArrayList(SelectionUtilities.getSelectedEObjects(selection));
        try {
            IViewPart viewPart = UiUtil.getWorkbenchPage().showView(UiConstants.Extensions.FAVORITES_VIEW_ID);
            if (viewPart instanceof FavoritesView) {
                UiPlugin.getDefault().getEObjectCache().addAll(objs);
//                ((FavoritesView)viewPart).addToClipboard(objs);
            }
        } catch (final PartInitException err) {
            UiConstants.Util.log(err);
            WidgetUtil.showError(err.getLocalizedMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IAction action,
                                 ISelection selection) {
        boolean enable = false;
        this.selection = selection;
        
        // check to make sure the selection is good (should be, the plugin
        //  settings pretty much mandate it:
        if (!SelectionUtilities.getSelectedEObjects(selection).isEmpty() ) {
            // yes, the selection has an EObject.
            // defect 15111 - disable this menu in favorites view
            // check what view we came from:
            IWorkbenchPart iwp = getActiveWorkbenchPart();

            if (!(iwp instanceof FavoritesView)) {
                // was not the favorites, enable:
                enable = true;
            } // endif -- not from FavoritesView
        } // endif -- eobject selected
        

        action.setEnabled(enable);
    }

    /**
     * @return the currently active IWorkbenchPart, presumably the view
     *   triggering the popup.
     */
    private static IWorkbenchPart getActiveWorkbenchPart() {
        IWorkbench        iwb = UiPlugin.getDefault().getWorkbench();
        IWorkbenchWindow iwbw = iwb.getActiveWorkbenchWindow();
        return iwbw.getActivePage().getActivePart();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {
    }

}
