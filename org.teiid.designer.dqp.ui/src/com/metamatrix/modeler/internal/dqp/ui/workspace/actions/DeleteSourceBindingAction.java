/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import com.metamatrix.modeler.dqp.internal.workspace.SourceModelInfo;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;


/** 
 * @since 5.0
 */
public final class DeleteSourceBindingAction extends ConfigurationManagerAction {

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
