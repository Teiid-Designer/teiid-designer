/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing.part;

import org.eclipse.draw2d.IFigure;

import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * EllipseEditPart
 */
public class EllipseEditPart extends DrawingEditPart {

    /**
     * Construct an instance of DrawingEditPart.
     * 
     */
    public EllipseEditPart() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        return getFigureFactory().createFigure((DiagramModelNode)getModel());
    }

}

