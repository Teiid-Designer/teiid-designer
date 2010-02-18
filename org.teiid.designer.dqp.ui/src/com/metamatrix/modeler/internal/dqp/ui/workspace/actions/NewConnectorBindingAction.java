/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import org.eclipse.jface.window.Window;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.designer.runtime.ConnectorType;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.NewConnectorBindingDialog;
import com.metamatrix.ui.internal.util.UiUtil;


/** 
 * @since 5.0
 */
public class NewConnectorBindingAction extends ConfigurationManagerAction {
    private static final String PREFIX = I18nUtil.getPropertyPrefix(NewConnectorBindingAction.class);
    
    /** 
     * 
     * @since 5.0
     */
    public NewConnectorBindingAction() {
        super(DqpUiConstants.UTIL.getString(PREFIX + "label")); //$NON-NLS-1$
        this.setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.NEW_BINDING_ICON));
        this.setToolTipText(DqpUiConstants.UTIL.getString(PREFIX + "tooltip")); //$NON-NLS-1$
        setEnabled(true);
    }

    /**
     *  
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        //System.out.println("  NewConnectorBindingAction.run() ====>>> ");
        // Get Selection

        NewConnectorBindingDialog dialog = new NewConnectorBindingDialog(UiUtil.getWorkbenchShellOnlyIfUiThread());
        Object selectedObject = getSelectedObject();
        if( selectedObject instanceof ConnectorType ) {
            dialog.setConnectorType((ConnectorType)selectedObject);
        }
        dialog.open();
        
        if (dialog.getReturnCode() == Window.OK) {
            // NOTE:  the result of this call is a clone of the dialog's connector binding and the addition of the binding to 
            // the Configuration Manager
            dialog.getNewConnectorBinding();
        }
    }

    /**
     *  
     * @see com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ConfigurationManagerAction#setEnablement()
     * @since 5.0
     */
    @Override
    protected void setEnablement() {
        boolean result = getAdmin().getConnectorTypes().size() > 0;
        if( !isMultiSelection() && !isEmptySelection() ) {
            Object selectedObject = getSelectedObject();
            result = false;
            if( selectedObject instanceof ConnectorBinding 
                || selectedObject instanceof ConnectorType) {
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
