/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.geometry.Point;

/**
 * @since 4.2
 */
public class LinkBendpoint extends AbsoluteBendpoint {
   
    /**
     */
    private static final long serialVersionUID = 1L;
    /** 
     * @param x
     * @param y
     * @since 4.2
     */
    public LinkBendpoint(int x,
                         int y) {
        super(x, y);
    }
    /** 
     * @param p
     * @since 4.2
     */
    public LinkBendpoint(Point p) {
        super(p);
    }
//implements
//                          java.io.Serializable,
//                          Bendpoint {

//    private float weight = 0.5f;
//    private Dimension d1, d2;
//
//    public LinkBendpoint() {
//    }
//
//    public Dimension getFirstRelativeDimension() {
//        return d1;
//    }
//
//    public Point getLocation() {
//        return null;
//    }
//
//    public Dimension getSecondRelativeDimension() {
//        return d2;
//    }
//
//    public float getWeight() {
//        return weight;
//    }
//
//    public void setRelativeDimensions(Dimension dim1,
//                                      Dimension dim2) {
//        d1 = dim1;
//        d2 = dim2;
//    }
//
//    public void setWeight(float w) {
//        weight = w;
//    }

}
