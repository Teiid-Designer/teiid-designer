/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing;

import org.eclipse.gef.EditPart;
import com.metamatrix.modeler.diagram.ui.drawing.model.EllipseModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.model.NoteModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.model.RectangleModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.model.TextModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.part.EllipseEditPart;
import com.metamatrix.modeler.diagram.ui.drawing.part.NoteEditPart;
import com.metamatrix.modeler.diagram.ui.drawing.part.RectangleEditPart;
import com.metamatrix.modeler.diagram.ui.drawing.part.TextEditPart;

/**
 * DrawingPartFactory
 */
public class DrawingPartFactory {
    
    private DrawingFigureFactory figureFactory;
    
    /**
     * Construct an instance of DrawingPartFactory.
     * 
     */
    public DrawingPartFactory() {
        super();
    }

    
    public EditPart createEditPart(EditPart iContext, Object iModel) {
        EditPart editPart = null;

        if( figureFactory == null )
            figureFactory = new DrawingFigureFactory();

        if( iModel instanceof NoteModelNode ) {
            editPart = new NoteEditPart();
        } else if( iModel instanceof TextModelNode ) {
            editPart = new TextEditPart();
        } else  if( iModel instanceof RectangleModelNode ) {
            editPart = new RectangleEditPart();
        } else if( iModel instanceof EllipseModelNode ) {
            editPart = new EllipseEditPart();
        }
        
        if (editPart != null) {
            editPart.setModel(iModel);
        }

        return editPart;
    }
}
