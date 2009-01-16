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
