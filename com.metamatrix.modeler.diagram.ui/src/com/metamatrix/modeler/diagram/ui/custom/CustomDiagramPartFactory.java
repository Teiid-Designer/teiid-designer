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

package com.metamatrix.modeler.diagram.ui.custom;

//import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;

import com.metamatrix.modeler.diagram.ui.figure.DiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.pakkage.PackageDiagramPartFactory;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;

/**
 * PackageDiagramPartFactory
 */
public class CustomDiagramPartFactory extends PackageDiagramPartFactory  {
    private DiagramFigureFactory figureFactory;
    private static final String diagramTypeId = PluginConstants.CUSTOM_DIAGRAM_TYPE_ID;
    
    @Override
    public EditPart createEditPart(EditPart iContext, Object iModel) {
        EditPart editPart = null;
		if( figureFactory == null )
			figureFactory = new CustomDiagramFigureFactory();
			
		editPart = super.createEditPart(iContext, iModel, diagramTypeId);

        if( iModel instanceof CustomDiagramNode ) {
            editPart = new CustomDiagramEditPart();
            ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
			editPart.setModel(iModel);
			((DiagramEditPart)editPart).setNotationId( getNotationId());
			((DiagramEditPart)editPart).setSelectionHandler(getSelectionHandler());
			((DiagramEditPart)editPart).setDiagramTypeId(diagramTypeId);
            ((CustomDiagramEditPart)editPart).setDropHelper(new CustomDiagramDropEditPartHelper((CustomDiagramEditPart)editPart));
    	}

        if( editPart instanceof DiagramEditPart ) {
            ((DiagramEditPart)editPart).setUnderConstruction(true);
        }
        
        return editPart;
    }
}
