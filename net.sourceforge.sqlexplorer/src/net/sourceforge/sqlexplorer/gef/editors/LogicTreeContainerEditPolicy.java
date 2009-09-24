/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.sourceforge.sqlexplorer.gef.editors;


import java.util.List;


import net.sourceforge.sqlexplorer.gef.model.AbstractModelObject;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.*;
import org.eclipse.gef.editpolicies.TreeContainerEditPolicy;


import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

public class LogicTreeContainerEditPolicy 
	extends TreeContainerEditPolicy 
{
	

@Override
protected Command getAddCommand(ChangeBoundsRequest request){
	//System.out.println("LogicTreeContainerEditPolicy getAddCommand");
	return new Command(){};
}

@Override
protected Command getCreateCommand(CreateRequest request){
	//System.out.println("LogicTreeContainerEditPolicy getCreateCommand");
	return new Command(){};
}


@Override
protected Command getMoveChildrenCommand(ChangeBoundsRequest request){
	CompoundCommand command = new CompoundCommand();
	List editparts = request.getEditParts();
	List children = getHost().getChildren();
	int newIndex = findIndexOfTreeItemAt(request.getLocation());
		
	for(int i = 0; i < editparts.size(); i++){
		EditPart child = (EditPart)editparts.get(i);
		int tempIndex = newIndex;
		int oldIndex = children.indexOf(child);
		if(oldIndex == tempIndex || oldIndex + 1 == tempIndex){
			command.add(UnexecutableCommand.INSTANCE);
			return command;
		} else if(oldIndex < tempIndex){
			tempIndex--;
		}
		command.add(new ReorderPartCommand(
					(AbstractModelObject)child.getModel(), 
					(AbstractModelObject)getHost().getModel(), 
					oldIndex, tempIndex)); 
	}
	return command;
}

protected boolean isAncestor(EditPart source, EditPart target){
	if(source == target)
		return true;
	if(target.getParent() != null)
		return isAncestor(source, target.getParent());
	return false;
}

}