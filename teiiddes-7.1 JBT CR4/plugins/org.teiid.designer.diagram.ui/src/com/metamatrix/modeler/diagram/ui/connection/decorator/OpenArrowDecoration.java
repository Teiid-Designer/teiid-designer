/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection.decorator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OpenArrowDecoration extends PolygonDecoration {

	public static final PointList ARROW_TIP = new PointList();

	//jh: this produced a nice diamond, but the classifier 'wall' bisected it:
	//	solution: move all x's to the left 2 points
	// static {
	//	   DIAMOND_TIP.addPoint(-2, 0);
	//	   DIAMOND_TIP.addPoint(0, 1);
	//	   DIAMOND_TIP.addPoint(2, 0);
	//	   DIAMOND_TIP.addPoint(0, -1);        
	// }

	static {
		ARROW_TIP.addPoint(0, 0);
		ARROW_TIP.addPoint(-1, 1);
		ARROW_TIP.addPoint(0, 0);
		ARROW_TIP.addPoint(-1, -1);
	}

	/**
	 * Constructs a RectangleDecoration. 
	 * Defaults the RectangleDecoration to fill its region with black.
	 * 
	 * @since 2.0 
	 */
	public OpenArrowDecoration() {
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
