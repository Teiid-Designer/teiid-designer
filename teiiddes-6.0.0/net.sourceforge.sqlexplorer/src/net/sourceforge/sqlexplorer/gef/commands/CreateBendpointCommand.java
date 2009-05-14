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





public class CreateBendpointCommand 
	extends BendpointCommand 
{

@Override
public void execute() {
	WireBendpoint wbp = new WireBendpoint();
	wbp.setRelativeDimensions(getFirstRelativeDimension(), 
					getSecondRelativeDimension());
	getLink().insertBendpoint(getIndex(), wbp);
	super.execute();
}

@Override
public void undo() {
	super.undo();
	getLink().removeBendpoint(getIndex());
}

}


