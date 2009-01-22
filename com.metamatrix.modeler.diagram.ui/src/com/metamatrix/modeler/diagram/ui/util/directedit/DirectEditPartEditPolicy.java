/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util.directedit;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;

import org.eclipse.gef.requests.DirectEditRequest;


public class DirectEditPartEditPolicy extends DirectEditPolicy {

    /**
     * @see DirectEditPolicy#getDirectEditCommand(DirectEditRequest)
     */
    @Override
    protected Command getDirectEditCommand(DirectEditRequest edit) {
        DirectEditPartCommand command = null;
        
        String labelText = (String)edit.getCellEditor().getValue();
        if( getHost() instanceof DirectEditPart ) {
    
            DirectEditPart dep = (DirectEditPart)getHost();
            command = new DirectEditPartCommand(dep, labelText);
        }
        
        return command;
    }

    /**
     * @see DirectEditPolicy#showCurrentEditValue(DirectEditRequest)
     */
    @Override
    protected void showCurrentEditValue(DirectEditRequest request) {

//        if( getHost() instanceof DirectEditPart ) {
//            String value = (String)request.getCellEditor().getValue();
//            ((DirectEditPart)getHost()).setText(value);
//            // - hack to prevent async layout from placing the cell editor twice.
//            getHostFigure().getUpdateManager().performUpdate();
//        }
    }

}
