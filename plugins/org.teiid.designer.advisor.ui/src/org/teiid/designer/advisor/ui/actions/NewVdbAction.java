/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;

import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;

/**
 * @since 4.3
 */
public final class NewVdbAction extends Action implements IWorkbenchWindowActionDelegate, AdvisorUiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public NewVdbAction() {
        //String keyPrefix = I18nUtil.getPropertyPrefix(NewVdbAction.class);

        this.setText("New VDB Action"); //$NON-NLS-1$
        this.setToolTipText("New VDB Action tooltip"); //$NON-NLS-1$
        this.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.NEW_VDB));
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     * @since 4.3
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     * @since 4.3
     */
    public void init( IWorkbenchWindow theWindow ) {
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     * @since 4.3
     */
    public void selectionChanged( IAction theAction,
                                  ISelection theSelection ) {
        // always enabled
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.3
     */
    public void run( IAction theAction ) {
        run();
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.4
     */
    public void run() {
        ModelerUiViewUtils.launchWizard("newVdbWizard", new StructuredSelection(), true); //$NON-NLS-1$
    }

}
