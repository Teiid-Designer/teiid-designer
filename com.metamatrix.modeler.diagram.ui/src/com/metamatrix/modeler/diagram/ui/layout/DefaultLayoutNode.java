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
