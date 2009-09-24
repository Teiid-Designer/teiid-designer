/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
