/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.part;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;

/**
 * Layout policy for our Hello Gef Editor.
 */
public class NavigationDiagramXYLayoutPolicy extends XYLayoutEditPolicy {
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
		SetConstraintCommand locationCommand = new SetConstraintCommand();

		locationCommand.setModel((NavigationModelNode)child.getModel());
		locationCommand.setLocation((Rectangle)constraint);

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
		return new NonResizableEditPolicy();
	}

	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(Request)
	**/
	@Override
    protected Command getDeleteDependantCommand(Request request) {
		return null;
	}
}
