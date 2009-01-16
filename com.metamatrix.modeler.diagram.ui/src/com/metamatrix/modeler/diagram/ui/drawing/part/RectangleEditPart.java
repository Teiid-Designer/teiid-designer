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

package com.metamatrix.modeler.diagram.ui.drawing.part;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * RectangleEditPart
 */
public class RectangleEditPart  extends DrawingEditPart {

    /**
     * Construct an instance of DrawingEditPart.
     * 
     */
    public RectangleEditPart() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        Point posn = new Point( ((DiagramModelNode)getModel()).getX(), ((DiagramModelNode)getModel()).getY());
        Figure newFigure = getFigureFactory().createFigure((DiagramModelNode)getModel());
        newFigure.setLocation(posn);
        return newFigure;
    }
}

