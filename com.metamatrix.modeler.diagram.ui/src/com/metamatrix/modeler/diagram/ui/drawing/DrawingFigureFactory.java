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

package com.metamatrix.modeler.diagram.ui.drawing;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.IDiagramType;
import com.metamatrix.modeler.diagram.ui.drawing.figure.NoteFigure;
import com.metamatrix.modeler.diagram.ui.drawing.model.EllipseModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.model.NoteModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.model.RectangleModelNode;
import com.metamatrix.modeler.diagram.ui.drawing.model.TextModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;

/**
 * DrawingFigureFactory
 */
public class DrawingFigureFactory implements DrawingConstants {

    /**
     * Construct an instance of DrawingFigureFactory.
     * 
     */
    public DrawingFigureFactory() {
        super();
    }

    public Figure createFigure(DiagramModelNode dmn) {
        Diagram diagram = (Diagram)dmn.getParent().getModelObject();
        String diagramType = diagram.getType();
        ColorPalette colorPalette = null;
        if( diagramType != null) {
            IDiagramType myDiagramType = DiagramUiPlugin.getDiagramTypeManager().getDiagram(diagramType);
            if( myDiagramType != null ) {
                colorPalette = myDiagramType.getColorPaletteManager().getColorPalette(null);
            }
        }

        Figure newFigure = null;
        switch( getObjectType(dmn) ) {

            case TypeId.NOTE: {
                newFigure = new NoteFigure(dmn.getDiagramModelObject().getUserString(), colorPalette);
            } break;
            
            case TypeId.TEXT: {
                newFigure = new Label(dmn.getDiagramModelObject().getUserString());
            } break;
            
            case TypeId.RECTANGLE: {
                newFigure = new RectangleFigure();
                ((RectangleFigure)newFigure).setFill(false);
            } break;
            
            case TypeId.ELLIPSE: {
                newFigure = new Ellipse();
                ((Ellipse)newFigure).setFill(false);
            } break;
            
            default: {
                // Here's where we get the notation manager and tell it to create a figure
                // for this modelObject.  So it'll come back in whatever "Notation" it desires.
                ModelerCore.Util.log( IStatus.ERROR, DiagramUiConstants.Util.getString(DiagramUiConstants.Errors.FIGURE_GENERATOR_FAILURE));
            } break;
        }
        
        return newFigure;

    }
    
    protected int getObjectType( DiagramModelNode dmn ) {
        int objectType = -1;
        
        if( dmn != null ) {
            if (dmn instanceof NoteModelNode) {
                return TypeId.NOTE;
            } else if (dmn instanceof TextModelNode) {
                return TypeId.TEXT;
            } else if (dmn instanceof RectangleModelNode) {
                return TypeId.RECTANGLE;
            } else if (dmn instanceof EllipseModelNode) {
                return TypeId.ELLIPSE;
            }
        }
        return objectType;
    }
}
