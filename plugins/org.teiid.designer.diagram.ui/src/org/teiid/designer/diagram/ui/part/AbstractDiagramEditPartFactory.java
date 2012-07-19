/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.part;

import org.eclipse.gef.EditPart;
import org.teiid.designer.diagram.ui.connection.NodeConnectionEditPart;
import org.teiid.designer.diagram.ui.editor.IDiagramSelectionHandler;

/**
 * @author blafond AbstractDiagramEditPartFactory provides a base class for all Diagram Edit Part Factories This class handles the
 *         notation ID as well as implements the default method for creating Edit Parts for the EditPartFactory interface method.
 *
 * @since 8.0
 */
public abstract class AbstractDiagramEditPartFactory implements DiagramEditPartFactory {

    private String sNotationId;
    private IDiagramSelectionHandler selectionHandler;

    /**
     * @see org.eclipse.gef.EditPartFactory;#createEditPart()
     */
    @Override
	public EditPart createEditPart( EditPart context,
                                    Object model ) {
        return null;
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#getNotationId()
     */
    @Override
	public String getNotationId() {
        return sNotationId;
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#setNotationId(java.lang.String)
     */
    @Override
	public void setNotationId( String sNotationId ) {
        this.sNotationId = sNotationId;
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#getSelectionHandler()
     */
    @Override
	public IDiagramSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#setSelectionHandler(org.teiid.designer.diagram.ui.editor.DiagramSelectionHandler)
     */
    @Override
	public void setSelectionHandler( IDiagramSelectionHandler selectionHandler ) {
        this.selectionHandler = selectionHandler;
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#getConnectionEditPart()
     */
    @Override
	public NodeConnectionEditPart getConnectionEditPart() {
        return new NodeConnectionEditPart();
    }

}
