/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetUtil;


/**<p>
 * </p>
 * @since 4.0
 */
public final class ShowDatatypeHierarchyAction implements IWorkbenchWindowActionDelegate,
                                                          UiConstants,
                                                          UiConstants.Extensions {
    //============================================================================================================================
    // Implemented Methods
    
    /**<p>
     * </p>
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     * @since 4.0
     */
    public void dispose() {
    }

    /**<p>
     * </p>
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     * @since 4.0
     */
    public void init(final IWorkbenchWindow window) {
    }

    /**<p>
     * </p>
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.0
     */
    public void run(final IAction action) {
        try {
            UiUtil.getWorkbenchPage().showView(DATATYPE_HIERARCHY_VIEW);
        } catch (final PartInitException err) {
            Util.log(err);
            WidgetUtil.showError(err.getLocalizedMessage());
        }
    }

    /**<p>
     * </p>
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     * @since 4.0
     */
    public void selectionChanged(final IAction action,
                                 final ISelection selection) {
    }
}
