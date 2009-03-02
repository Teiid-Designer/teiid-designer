/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * DrawingEditPart
 */
public class NoteEditPart extends DrawingEditPart {

    /**
     * Construct an instance of DrawingEditPart.
     * 
     */
    public NoteEditPart() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        return getFigureFactory().createFigure((DiagramModelNode)getModel());
    }
    
    /* (non-JavaDoc)
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // 
        String prop = evt.getPropertyName();
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            resizeChildren();
        }
        
        super.propertyChange(evt);

    }

    
    @Override
    public void resizeChildren() {
        // call header.resize();
        getDiagramFigure().updateForSize(((DiagramModelNode) getModel()).getSize());
    }

}
