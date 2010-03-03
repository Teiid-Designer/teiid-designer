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
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.NewConnectorBindingDialog;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.0
 */
public class NewConnectorBindingAction extends ConfigurationManagerAction {
    private static final String PREFIX = I18nUtil.getPropertyPrefix(NewConnectorBindingAction.class);

    /**
     * @since 5.0
     */
    public NewConnectorBindingAction() {
        super(DqpUiConstants.UTIL.getString(PREFIX + "label")); //$NON-NLS-1$
        this.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.NEW_BINDING_ICON));
        this.setToolTipText(DqpUiConstants.UTIL.getString(PREFIX + "tooltip")); //$NON-NLS-1$
        setEnabled(true);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        Object selectedObject = getSelectedObject();
        ExecutionAdmin admin = null;

        if (selectedObject instanceof ConnectorType) {
            admin = ((ConnectorType)selectedObject).getAdmin();
        } else {
            admin = ((Connector)selectedObject).getType().getAdmin();
        }
        
        NewConnectorBindingDialog dialog = new NewConnectorBindingDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), admin);

        if (selectedObject instanceof ConnectorType) {
            dialog.setConnectorType((ConnectorType)selectedObject);
        } else {
            dialog.setConnectorType(((Connector)selectedObject).getType());
        }

        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            Connector newConnector = dialog.getNewConnector();
            admin.addConnector(newConnector.getName(), newConnector.getType(), dialog.getProperties());
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ConfigurationManagerAction#setEnablement()
     * @since 5.0
     */
    @Override
    protected void setEnablement() {
        boolean result = false;

        if (SelectionUtilities.isSingleSelection(getSelection())) {
            result = false;
            Object selectedObject = getSelectedObject();

            if (selectedObject instanceof Connector || selectedObject instanceof ConnectorType) {
                result = true;
            }
        }

        setEnabled(result);
    }

    /**
     * Needed to check/reset enablement from ConnectorsView if NO selection
     * 
     * @since 5.0
     */
    public void checkEnablement() {
        setEnablement();
    }
}
