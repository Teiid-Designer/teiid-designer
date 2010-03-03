/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import org.eclipse.swt.widgets.Display;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * Deletes the connector binding through the workspace configuration manager. This insures all source bindings which reference the
 * deleted binding will also be removed from WorkspaceConfig.def
 * 
 * @since 5.0
 */
public class DeleteConnectorBindingAction extends ConfigurationManagerAction {

    /**
     * @since 5.0
     */
    public DeleteConnectorBindingAction() {
        super(DqpUiConstants.UTIL.getString("DeleteConnectorBindingAction.label")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        Object[] selectedObjects = getSelectedObjects().toArray();

        if (selectedObjects != null) {
            ExecutionAdmin admin = getAdmin();
            assert (admin != null);

            for (int i = 0; i < selectedObjects.length; ++i) {
                assert (selectedObjects[i] instanceof Connector); // should never be enabled if not a connector

                Connector theBinding = (Connector)selectedObjects[i];

                try {
                    admin.removeConnector(theBinding);
                } catch (final Exception error) {
                    UiUtil.runInSwtThread(new Runnable() {

                        public void run() {
                            DqpUiPlugin.showErrorDialog(Display.getCurrent().getActiveShell(), error);
                        }
                    }, false);
                }
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
        if (!isEmptySelection()) {
            Object[] selectedObjects = getSelectedObjects().toArray();
            if (selectedObjects != null) {
                result = true;
                for (int i = 0; i < selectedObjects.length; i++) {
                    if (!(selectedObjects[i] instanceof Connector)) {
                        result = false;
                    }
                }
            }
        }

        setEnabled(result);
    }

}
