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


import net.sourceforge.sqlexplorer.gef.model.WireBendpoint;

import org.eclipse.draw2d.Bendpoint;

public class MoveBendpointCommand
	extends BendpointCommand 
{

private Bendpoint oldBendpoint;

@Override
public void execute() {
	WireBendpoint bp = new WireBendpoint();
	bp.setRelativeDimensions(getFirstRelativeDimension(), 
					getSecondRelativeDimension());
	setOldBendpoint((Bendpoint)getLink().getBendpoints().get(getIndex()));
	getLink().setBendpoint(getIndex(), bp);
	super.execute();
}

protected Bendpoint getOldBendpoint() {
	return oldBendpoint;
}

public void setOldBendpoint(Bendpoint bp) {
	oldBendpoint = bp;
}

@Override
public void undo() {
	super.undo();
	getLink().setBendpoint(getIndex(), getOldBendpoint());
}

}


