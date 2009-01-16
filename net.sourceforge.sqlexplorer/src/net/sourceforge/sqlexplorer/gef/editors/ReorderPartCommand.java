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



import net.sourceforge.sqlexplorer.gef.model.AbstractModelObject;

import org.eclipse.gef.commands.Command;


public class ReorderPartCommand extends Command {
	
//private boolean valuesInitialized;
private int oldIndex, newIndex;
private AbstractModelObject child;
private AbstractModelObject parent;

public ReorderPartCommand(AbstractModelObject child, AbstractModelObject parent, int oldIndex, int newIndex ) {
	
	this.child = child;
	this.parent = parent;
	this.oldIndex = oldIndex;
	this.newIndex = newIndex;
}

@Override
public void execute() {
	parent.removeChild(child);
	parent.addChild( newIndex,child);
}

@Override
public void undo() {
	parent.removeChild(child);
	parent.addChild( oldIndex,child);
}

}