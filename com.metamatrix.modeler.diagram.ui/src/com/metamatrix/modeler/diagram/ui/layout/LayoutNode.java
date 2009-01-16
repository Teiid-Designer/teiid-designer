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
