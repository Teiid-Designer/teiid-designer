/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.diagram.ui.connection.decorator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.*;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ClosedArrowDecoration extends PolygonDecoration {

	public static final PointList ARROW_TIP = new PointList();

	static {
		ARROW_TIP.addPoint(0, 0);
		ARROW_TIP.addPoint(-1, 1);
		ARROW_TIP.addPoint(-1, -1);
	}

	/**
	 * Constructs a RectangleDecoration. 
	 * Defaults the RectangleDecoration to fill its region with black.
	 * 
	 * @since 2.0 
	 */
	public ClosedArrowDecoration() {
		setBackgroundColor(ColorConstants.black);
		setForegroundColor(ColorConstants.black);
		setTemplate(ARROW_TIP);
		setPoints(ARROW_TIP);
		setScale(4, 4);
	}

	/**
	 * Returns the points in the PolylineDecoration as a PointList.
	 * 
	 * @since 2.0
	 */
	@Override
    public PointList getPoints() {
		return super.getPoints();
	}

	/**
	 * Sets the amount of scaling to be done along X and Y
	 * axes on the PolylineDecoration's template.
	 *
	 * @param x X scaling
	 * @param y Y scaling
	 * @since 2.0
	 */
	@Override
    public void setScale(double x, double y) {
		super.setScale(x, y);
	}

	@Override
    public void setReferencePoint(Point ref) {
		super.setReferencePoint(ref);
	}

	/**
	 * Sets the angle by which rotation is to be done on the 
	 * PolylineDecoration.
	 * 
	 * @param angle Angle of rotation.
	 * @since 2.0
	 */
	@Override
    public void setRotation(double angle) {
		super.setRotation(angle);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	@Override
    protected boolean useLocalCoordinates() {
		return true;
	}
}
