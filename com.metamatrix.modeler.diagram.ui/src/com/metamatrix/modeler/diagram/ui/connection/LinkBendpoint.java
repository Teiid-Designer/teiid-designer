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

package com.metamatrix.modeler.diagram.ui.connection;

import org.eclipse.draw2d.AbsoluteBendpoint;
//import org.eclipse.draw2d.Bendpoint;
//import org.eclipse.draw2d.geometry.Dimension;
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
