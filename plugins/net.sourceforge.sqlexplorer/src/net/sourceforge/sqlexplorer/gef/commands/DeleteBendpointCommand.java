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
package net.sourceforge.sqlexplorer.gef.commands;

import org.eclipse.draw2d.Bendpoint;

public class DeleteBendpointCommand 
	extends BendpointCommand 
{

private Bendpoint bendpoint;

@Override
public void execute() {
	bendpoint = (Bendpoint)getLink().getBendpoints().get(getIndex());
	getLink().removeBendpoint(getIndex());
	super.execute();
}

@Override
public void undo() {
	super.undo();
	getLink().insertBendpoint(getIndex(), bendpoint);
}

}


