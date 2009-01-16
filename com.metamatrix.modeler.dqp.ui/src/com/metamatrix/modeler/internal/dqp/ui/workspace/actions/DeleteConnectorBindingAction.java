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

import org.eclipse.swt.widgets.Display;

import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;


/** 
 * Deletes the connector binding through the workspace configuration manager. This insures all source bindings which reference
 * the deleted binding will also be removed from WorkspaceConfig.def
 * @since 5.0
 */
public class DeleteConnectorBindingAction extends ConfigurationManagerAction {

    /** 
     * 
     * @since 5.0
     */
    public DeleteConnectorBindingAction() {
        super(DqpUiConstants.UTIL.getString("DeleteConnectorBindingAction.label")); //$NON-NLS-1$
    }

    /**
     *  
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        //System.out.println("  DeleteConnectorBindingAction.run() ====>>> ");
        // Get Selection
        Object[] selectedObjects = getSelectedObjects().toArray();
        if( selectedObjects != null ) {
            for( int i=0; i<selectedObjects.length; i++ ) {
                if( selectedObjects[i] instanceof ConnectorBinding ) {
                    ConnectorBinding theBinding = (ConnectorBinding)selectedObjects[i];
                    if( theBinding != null ) {
                        try {
                            getConfigurationManager().removeBinding(theBinding);
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
        if(  !isEmptySelection() ) {
            Object[] selectedObjects = getSelectedObjects().toArray();
            if( selectedObjects != null ) {
                result = true;
                for( int i=0; i<selectedObjects.length; i++ ) {
                    if( ! (selectedObjects[i] instanceof ConnectorBinding) ) {
                        result = false;
                    }
                }
            }
        }
        
        setEnabled(result);
    }

}
