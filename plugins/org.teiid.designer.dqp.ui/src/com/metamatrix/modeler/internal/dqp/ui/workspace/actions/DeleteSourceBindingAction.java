/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import com.metamatrix.modeler.dqp.internal.workspace.SourceBinding;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;


/** 
 * @since 5.0
 */
public final class DeleteSourceBindingAction extends RuntimeAction {

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
        SourceBinding binding = (SourceBinding)getSelectedObject();

        if( binding != null ) {
            getSourceBindingsManager().removeSourceBinding(binding);
        }
    }

    /**
     *  
     * @see com.metamatrix.modeler.internal.dqp.ui.workspace.actions.RuntimeAction#setEnablement()
     * @since 5.0
     */
    @Override
    protected void setEnablement() {
        boolean result = false;
        if( !isMultiSelection() && !isEmptySelection() ) {
            Object selectedObject = getSelectedObject();

            if( selectedObject instanceof SourceBinding) {
                result = true;
            }
        }
        
        setEnabled(result);
    }
}
