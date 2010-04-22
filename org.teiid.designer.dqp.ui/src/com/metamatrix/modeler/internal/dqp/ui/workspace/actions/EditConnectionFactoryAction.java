/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.util.Properties;
import org.eclipse.jface.window.Window;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ui.EditConnectionFactoryDialog;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * @since 5.0
 */
public class EditConnectionFactoryAction extends RuntimeAction {

    /**
     * @since 5.0
     */
    public EditConnectionFactoryAction() {
        super(UTIL.getString(I18nUtil.getPropertyPrefix(EditConnectionFactoryAction.class) + "label")); //$NON-NLS-1$
        setToolTipText(UTIL.getString(I18nUtil.getPropertyPrefix(EditConnectionFactoryAction.class) + "tooltip")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        Connector connector = (Connector)getSelectedObject();
        assert (connector != null); // action should not be enabled if there isn't one and only one connector selected

        EditConnectionFactoryDialog dialog = new EditConnectionFactoryDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), connector);
        dialog.open();

        if (dialog.getReturnCode() == Window.OK) {
            Properties changedProperties = dialog.getConnector().getChangedProperties();

            try {
                getAdmin().setProperties(connector, changedProperties);
            } catch (Exception e) {
                UTIL.log(e);
                WidgetUtil.showError(UTIL.getString(I18nUtil.getPropertyPrefix(EditConnectionFactoryAction.class) + "errorMsg")); //$NON-NLS-1$
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
        Object selectedObject = getSelectedObject();

        if (selectedObject instanceof Connector) {
            result = true;
        }

        setEnabled(result);
    }

}
