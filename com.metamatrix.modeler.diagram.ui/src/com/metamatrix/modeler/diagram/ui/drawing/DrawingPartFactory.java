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
