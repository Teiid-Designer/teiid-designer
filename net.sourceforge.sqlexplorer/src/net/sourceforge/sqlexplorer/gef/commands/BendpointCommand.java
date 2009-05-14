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


import net.sourceforge.sqlexplorer.gef.model.Link;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

public class BendpointCommand 
	extends Command 
{

protected int index;
protected Point location;
protected Link wire;
private Dimension d1, d2;

protected Dimension getFirstRelativeDimension() {
	return d1;
}

protected Dimension getSecondRelativeDimension() {
	return d2;
}

protected int getIndex() {
	return index;
}

protected Point getLocation() {
	return location;
}

protected Link getLink() {
	return wire;
}

@Override
public void redo() {
	execute();
}

public void setRelativeDimensions(Dimension dim1, Dimension dim2) {
	d1 = dim1;
	d2 = dim2;
}

public void setIndex(int i) {
	index = i;
}

public void setLocation(Point p) {
	location = p;
}

public void setLink(Link w) {
	wire = w;
}

}


