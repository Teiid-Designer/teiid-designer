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
 * OffsetArrowDecoration
 * A decorative Figure intended to be placed on a 
 * {@link Polyline}.
 * It has the default shape of diamond that is wider than it is tall.
 */

public class OffsetArrowDecoration extends PolygonDecoration {
    
    public static final PointList V_TIP = new PointList();

    static {
        V_TIP.addPoint(-2, 1);
        V_TIP.addPoint(-1, 0);
        V_TIP.addPoint(-2, -1);
    }

    /**
     * Constructs a RectangleDecoration. 
     * Defaults the RectangleDecoration to fill its region with black.
     * 
     * @since 2.0 
     */
    public OffsetArrowDecoration() {
        setBackgroundColor(ColorConstants.green);
        setForegroundColor(ColorConstants.green);
        setTemplate(V_TIP);
        setPoints(V_TIP);
        setScale(8, 4);
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

