/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.Images.NEW_BINDING_ICON;
import org.eclipse.jface.window.Window;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorTemplate;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ui.NewConnectionFactoryDialog;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 5.0
 */
public class NewConnectionFactoryAction extends RuntimeAction {
    /**
     * @since 5.0
     */
    public NewConnectionFactoryAction() {
        super(UTIL.getString(I18nUtil.getPropertyPrefix(NewConnectionFactoryAction.class) + "label")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(NEW_BINDING_ICON));
        setToolTipText(UTIL.getString(I18nUtil.getPropertyPrefix(NewConnectionFactoryAction.class) + "tooltip")); //$NON-NLS-1$
        setEnabled(true);
    }

    /**
     * Needed to check/reset enablement from ConnectorsView if NO selection
     * 
     * @since 5.0
     */
    public void checkEnablement() {
        setEnablement();
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
        NewConnectionFactoryDialog dialog = new NewConnectionFactoryDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), admin, type);

        if (dialog.open() == Window.OK) {
            try {
                ConnectorTemplate newConnector = dialog.getConnector();
                admin.addConnector(newConnector.getName(), newConnector.getType(), newConnector.getChangedProperties());
            } catch (Exception e) {
                UTIL.log(e);
                WidgetUtil.showError(UTIL.getString(I18nUtil.getPropertyPrefix(NewConnectionFactoryAction.class) + "errorMsg")); //$NON-NLS-1$
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.dqp.ui.workspace.actions.RuntimeAction#setEnablement()
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

}
