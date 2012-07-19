/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.custom;

//import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef.EditPart;
import org.teiid.designer.diagram.ui.PluginConstants;
import org.teiid.designer.diagram.ui.figure.DiagramFigureFactory;
import org.teiid.designer.diagram.ui.pakkage.PackageDiagramPartFactory;
import org.teiid.designer.diagram.ui.part.AbstractDiagramEditPart;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;

/**
 * PackageDiagramPartFactory
 *
 * @since 8.0
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
