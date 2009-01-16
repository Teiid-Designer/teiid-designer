/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
