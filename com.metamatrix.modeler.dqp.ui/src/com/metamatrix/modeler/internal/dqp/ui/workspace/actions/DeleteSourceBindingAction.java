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

import com.metamatrix.modeler.dqp.internal.workspace.SourceModelInfo;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;


/** 
 * @since 5.0
 */
public class DeleteSourceBindingAction extends ConfigurationManagerAction {

    /** 
     * 
     * @since 5.0
     */
    public DeleteSourceBindingAction() {
        super(DqpUiConstants.UTIL.getString("DeleteSourceBindingAction.label")); //$NON-NLS-1$
    }

    /**
     *  
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        // Get Selection
        SourceModelInfo theModelInfo = (SourceModelInfo)getSelectedObject();
        if( theModelInfo != null ) {
            getWorkspaceConfig().removeSourceBinding(theModelInfo);
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
            if( selectedObject instanceof SourceModelInfo) {
                result = true;
            }
        }
        
        setEnabled(result);
    }
}
