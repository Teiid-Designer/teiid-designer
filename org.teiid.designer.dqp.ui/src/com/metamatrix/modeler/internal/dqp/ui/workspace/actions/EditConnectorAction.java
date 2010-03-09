/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import java.util.Properties;
import org.eclipse.jface.window.Window;
import org.teiid.designer.runtime.Connector;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.EditConnectorDialog;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 5.0
 */
public class EditConnectorAction extends ConfigurationManagerAction {

    /**
     * @since 5.0
     */
    public EditConnectorAction() {
        super(DqpUiConstants.UTIL.getString("EditConnectorAction.label")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        Connector connector = (Connector)getSelectedObject();
        assert (connector != null); // action should not be enabled if there isn't one and only one connector selected

        EditConnectorDialog dialog = new EditConnectorDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), connector);
        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            Properties changedProperties = dialog.getPropertyChanges();
            try {
                getAdmin().setProperties(connector, changedProperties);
            } catch (Exception e) {
                // TODO might need a better error message here
                WidgetUtil.showError(e);
            }
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
