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
package net.sourceforge.sqlexplorer.gef.figures;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public class FixedConnectionAnchor 
	extends AbstractConnectionAnchor
{

public boolean leftToRight = true;
public int offsetH;
public int offsetV;
public boolean topDown = true;

public FixedConnectionAnchor(IFigure owner) {
	super(owner);
}

/**
 * @see org.eclipse.draw2d.AbstractConnectionAnchor#ancestorMoved(IFigure)
 */
@Override
public void ancestorMoved(IFigure figure) {
	if (figure instanceof ScalableFreeformLayeredPane)
		return;
	super.ancestorMoved(figure);
}

public Point getLocation(Point reference) {
	Rectangle r = getOwner().getBounds();
	int x,y;
	if (topDown)
		y = r.y + offsetV;
	else
		y = r.bottom() - 1 - offsetV;

	if (leftToRight)
		x = r.x + offsetH;
	else
		x = r.right() - 1 - offsetH;
	
	Point p = new PrecisionPoint(x,y);
	getOwner().translateToAbsolute(p);
	return p;
}

@Override
public Point getReferencePoint(){
	return getLocation(null);
}
	
}