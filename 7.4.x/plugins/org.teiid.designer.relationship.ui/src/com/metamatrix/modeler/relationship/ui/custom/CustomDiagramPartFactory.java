/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.custom;

import org.eclipse.gef.EditPart;
import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.BlockAnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.part.RelationshipDiagramPartFactory;

/**
 * PackageDiagramPartFactory
 */
public class CustomDiagramPartFactory extends RelationshipDiagramPartFactory {

    private DiagramFigureFactory figureFactory;
    private static final String diagramTypeId = PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID;

    @Override
    public EditPart createEditPart( EditPart iContext,
                                    Object iModel ) {
        EditPart editPart = null;

        if (figureFactory == null) figureFactory = new CustomDiagramFigureFactory();

        if (iModel instanceof CustomDiagramNode) {
            editPart = new CustomDiagramEditPart();
            ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
        } else {
            editPart = super.createEditPart(iContext, iModel);
            if (editPart instanceof AbstractDiagramEditPart) ((AbstractDiagramEditPart)editPart).setFigureFactory(figureFactory);
        }

        if (editPart != null) {
            if (editPart instanceof DiagramEditPart) {
                editPart.setModel(iModel);
                ((DiagramEditPart)editPart).setNotationId(getNotationId());
                ((DiagramEditPart)editPart).setSelectionHandler(getSelectionHandler());
                ((DiagramEditPart)editPart).setDiagramTypeId(diagramTypeId);
            }
        } else {
            // ModelerCore.Util.log( IStatus.ERROR, Util.getString(Errors.EDIT_PART_FAILURE));
        }

        return editPart;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getConnectionEditPart()
     */
    @Override
    public NodeConnectionEditPart getConnectionEditPart() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getAnchorManager(com.metamatrix.modeler.diagram.ui.part.DiagramEditPart)
     */
    @Override
    public AnchorManager getAnchorManager( DiagramEditPart editPart ) {
        return new BlockAnchorManager(editPart);
    }
}
