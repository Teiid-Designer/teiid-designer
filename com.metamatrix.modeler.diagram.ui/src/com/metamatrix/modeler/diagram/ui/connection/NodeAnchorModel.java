/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import org.eclipse.draw2d.geometry.Point;

/**
 * NodeAnchorModel
 */
public class NodeAnchorModel {

    private Point m_location;
    
    public void setLocation(Point iLocation) {
        m_location = iLocation;
    }

    public Point getLocation() {
        return m_location;
    }

}
