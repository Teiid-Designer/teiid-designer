/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.layout.spring;

import java.awt.Rectangle;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * SpringNode
 */
//=================================================================================================
// Inner Class - SpringNode
//=================================================================================================

public class SpringNode {
    
    private DiagramModelNode modelNode;
    private double thisX;
    private double thisY;
    private double centerX;
    private double centerY;
    private Rectangle bounds;

    public SpringNode(DiagramModelNode modelNode) {

        this.modelNode = modelNode;
        centerX = modelNode.getCenterX();
        centerY = modelNode.getCenterY();
        bounds = new Rectangle(modelNode.getWidth(), modelNode.getHeight());
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
    }
    
    public void setCenterX(double x) {
        centerX = x;
    }
    
    public void setCenterY(double y) {
        centerY = y;
    }
    
    public void setX(double x) {
        thisX = x;
    }
    
    public void setY(double y) {
        thisY = y;
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
    /**
     * @return
     */
    public double getCenterX() {
        return centerX;
    }

    /**
     * @return
     */
    public double getCenterY() {
        return centerY;
    }

    public void setFinalPosition() {
        getModelNode().setCenterXY((int)getCenterX(), (int)getCenterY());    
    }
}
