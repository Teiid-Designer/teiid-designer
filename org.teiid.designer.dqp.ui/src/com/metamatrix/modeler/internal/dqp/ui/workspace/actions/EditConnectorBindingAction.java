/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import org.eclipse.jface.window.Window;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.EditConnectorBindingDialog;
import com.metamatrix.ui.internal.util.UiUtil;


/** 
 * @since 5.0
 */
public class EditConnectorBindingAction extends ConfigurationManagerAction {

    /** 
     * 
     * @since 5.0
     */
    public EditConnectorBindingAction() {
        super(DqpUiConstants.UTIL.getString("EditConnectorBindingAction.label")); //$NON-NLS-1$
    }

    /**
     *  
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        //System.out.println("  EditConnectorBindingAction.run() ====>>> ");
        // Get Selection
        ConnectorBinding theBinding = (ConnectorBinding)getSelectedObject();
        if( theBinding != null ) {
            EditConnectorBindingDialog dialog = new EditConnectorBindingDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), theBinding);

            dialog.open();
            
            if (dialog.getReturnCode() == Window.OK) {
                save();
            }
        }
    }

    /**
     *  
     * @see com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ConfigurationManagerAction#setEnablement()
     * @since 5.0
     */
    @Override
    protected void setEnablement() {
        boolean result = false;
        if( !isMultiSelection() && !isEmptySelection() ) {
            Object selectedObject = getSelectedObject();
            if( selectedObject instanceof ConnectorBinding) {
                result = true;
            }
        }
        
        setEnabled(result);
    }
}
