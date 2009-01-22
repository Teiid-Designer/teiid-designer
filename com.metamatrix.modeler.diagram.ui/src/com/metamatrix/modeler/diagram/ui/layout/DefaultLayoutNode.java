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
 * DefaultLayoutNode
 */
public class DefaultLayoutNode implements LayoutNode {
    
    private DiagramModelNode modelNode;
    private double thisX;
    private double thisY;
    private double centerX;
    private double centerY;
    private Rectangle bounds;

    public DefaultLayoutNode(DiagramModelNode modelNode) {
        if( modelNode != null ) {
            this.modelNode = modelNode;
            centerX = modelNode.getCenterX();
            centerY = modelNode.getCenterY();
            bounds = new Rectangle(0, 0, modelNode.getWidth(), modelNode.getHeight());
        }
    }
    
    public DiagramModelNode getModelNode() {
        return modelNode;
    }
        
    public Rectangle getBounds() {
        return bounds;
    }
    
    public void setCenterXY(double x, double y) {
        centerX = x;
        centerY = y;
        thisX = centerX - getWidth()/2;
        thisY = centerY - getHeight()/2;
		bounds.setLocation((int)thisX, (int)thisY);
//        System.out.println(" -->> DefaultLayoutNode.setCenterXY():  New XY Point = " + getPosition());
    }
    
    public void setCenterX(double x) {
        centerX = x;
        thisX = centerX - getWidth()/2;
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    public void setCenterY(double y) {
        centerY = y;
        thisY = centerY - getHeight()/2;
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    public Point getPosition() {
        return new Point(thisX, thisY);
    }
    
    public void setPosition(Point point ) {
        thisX = point.x;
        thisY = point.y;
        setCenterX(thisX + getWidth()/2);
        setCenterY(thisY + getHeight()/2);
		bounds.setLocation(point);
//        System.out.println(" -->> DefaultLayoutNode.setPosition():  New XY Point = " + point);
    }
    
    public void setPosition(double x, double y) {
        thisX = x;
        thisY = y;
        setCenterX(thisX + getWidth()/2);
        setCenterY(thisY + getHeight()/2);
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    public void setX(double x) {
        thisX = x;
        setCenterX(thisX + getWidth()/2);
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    public void setY(double y) {
        thisY = y;
        setCenterY(thisY + getHeight()/2);
		bounds.setLocation((int)thisX, (int)thisY);
    }
    
    public double getX() {
        return thisX;
    }
    
    public double getY() {
        return thisY;
    }
    
    public double getWidth() {
        return bounds.width;
    }
    
    public double getHeight() {
        return bounds.height;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setFinalPosition() {
        getModelNode().setCenterXY((int)getCenterX(), (int)getCenterY());    
    }    /**
     * @param rectangle
     */
    public void setBounds(Rectangle rectangle) {
        bounds = rectangle;
    }

}
