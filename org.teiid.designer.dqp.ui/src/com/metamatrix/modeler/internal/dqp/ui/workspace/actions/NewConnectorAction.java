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
import org.teiid.designer.runtime.Server;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.NewConnectorDialog;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 5.0
 */
public class NewConnectorAction extends ConfigurationManagerAction {
    private static final String PREFIX = I18nUtil.getPropertyPrefix(NewConnectorAction.class);

    /**
     * @since 5.0
     */
    public NewConnectorAction() {
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
        ExecutionAdmin admin = getAdmin();
        ConnectorType type = null;
        Object selectedObject = getSelectedObject();

        assert (selectedObject != null);
        assert ((selectedObject instanceof Connector) || (selectedObject instanceof ConnectorType) || (selectedObject instanceof Server));
        assert (admin != null);

        if (selectedObject instanceof ConnectorType) {
            type = (ConnectorType)selectedObject;
        } else if (selectedObject instanceof Connector) {
            type = ((Connector)selectedObject).getType();
        }

        // show dialog
        NewConnectorDialog dialog = new NewConnectorDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), admin, type);

        if (dialog.open() == Window.OK) {
            try {
                admin.addConnector(dialog.getConnectorName(), dialog.getConnectorType(), dialog.getConnectorProperties());
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

        if (SelectionUtilities.isSingleSelection(getSelection())) {
            Object selectedObject = getSelectedObject();

            if ((selectedObject instanceof Connector) || (selectedObject instanceof ConnectorType)
                || (selectedObject instanceof Server)) {
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
