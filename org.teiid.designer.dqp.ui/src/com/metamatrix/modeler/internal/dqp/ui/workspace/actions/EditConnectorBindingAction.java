/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import org.eclipse.jface.window.Window;
import org.teiid.designer.runtime.Connector;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.EditConnectorBindingDialog;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.0
 */
public class EditConnectorBindingAction extends ConfigurationManagerAction {

    /**
     * @since 5.0
     */
    public EditConnectorBindingAction() {
        super(DqpUiConstants.UTIL.getString("EditConnectorBindingAction.label")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        Connector connector = (Connector)getSelectedObject();
        assert (connector != null); // action should not be enabled if there isn't one and only one connector selected

        EditConnectorBindingDialog dialog = new EditConnectorBindingDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), connector);

        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            getAdmin().modifyConnector(); // TODO need this method in admin
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ConfigurationManagerAction#setEnablement()
     * @since 5.0
     */
    @Override
    protected void setEnablement() {
        boolean result = false;
        Object selectedObject = getSelectedObject();

        if (selectedObject instanceof Connector) {
            result = true;
        }

        setEnabled(result);
    }
}
