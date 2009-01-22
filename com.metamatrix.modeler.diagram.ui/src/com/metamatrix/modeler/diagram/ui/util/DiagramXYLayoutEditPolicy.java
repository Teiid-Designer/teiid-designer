/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;

/**
 * Layout policy for our Hello Gef Editor.
 */
public class DiagramXYLayoutEditPolicy extends XYLayoutEditPolicy {
    boolean logging = true;
    
    /**
     * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(EditPart, Object)
    **/
    @Override
    protected Command createAddCommand(EditPart child, Object constraint) {
        return null;
    }

    /**
     * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(EditPart, Object)
    **/
    @Override
    protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
        SetConstraintCommand locationCommand = new SetConstraintCommand(child);

        locationCommand.setModel((DiagramModelNode) child.getModel());
        locationCommand.setLocation((Rectangle) constraint);

        return locationCommand;
    }

    /**
     * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(CreateRequest)
    **/
    @Override
    protected Command getCreateCommand(CreateRequest request) {
        return null;
    }

    /**
     * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#createChildEditPolicy(EditPart)
    **/
    @Override
    protected EditPolicy createChildEditPolicy(EditPart child) {
        EditPolicy ep = null;
        
        if( child instanceof DiagramEditPart ) {
            if( ((DiagramEditPart)child).isResizable())
                ep = new ResizableEditPolicy();
            else
                ep = new NonResizableEditPolicy();
        } else {
            ep = new NonResizableEditPolicy();
        }
        return ep;
    }

    /**
     * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(Request)
    **/
    @Override
    protected Command getDeleteDependantCommand(Request request) {
        return null;
    }
}
