/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.layout;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * LayoutNode
 */
public interface LayoutNode {


    public DiagramModelNode getModelNode();
        
    public Rectangle getBounds();
    
    public void setCenterXY(double x, double y);
    
    public void setCenterX(double x);
    
    public void setCenterY(double y);
    
    public Point getPosition();
    
    public void setPosition(Point point );
    
    public void setPosition(double x, double y);
    
    public void setX(double x);
    
    public void setY(double y);
    
    public double getX();
    
    public double getY();
    
    public double getWidth();
    
    public double getHeight();

    public double getCenterX();

    public double getCenterY();

    public void setFinalPosition();

    public void setBounds(Rectangle rectangle);
}
