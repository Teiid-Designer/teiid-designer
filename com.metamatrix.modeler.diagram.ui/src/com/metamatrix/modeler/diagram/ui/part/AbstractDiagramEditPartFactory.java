/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.part;

import org.eclipse.gef.EditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;

/**
 * @author blafond AbstractDiagramEditPartFactory provides a base class for all Diagram Edit Part Factories This class handles the
 *         notation ID as well as implements the default method for creating Edit Parts for the EditPartFactory interface method.
 */
public abstract class AbstractDiagramEditPartFactory implements DiagramEditPartFactory {

    private String sNotationId;
    private IDiagramSelectionHandler selectionHandler;

    /**
     * @see org.eclipse.gef.EditPartFactory;#createEditPart()
     */
    public EditPart createEditPart( EditPart context,
                                    Object model ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getNotationId()
     */
    public String getNotationId() {
        return sNotationId;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#setNotationId(java.lang.String)
     */
    public void setNotationId( String sNotationId ) {
        this.sNotationId = sNotationId;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getSelectionHandler()
     */
    public IDiagramSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#setSelectionHandler(com.metamatrix.modeler.diagram.ui.editor.DiagramSelectionHandler)
     */
    public void setSelectionHandler( IDiagramSelectionHandler selectionHandler ) {
        this.selectionHandler = selectionHandler;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getConnectionEditPart()
     */
    public NodeConnectionEditPart getConnectionEditPart() {
        return new NodeConnectionEditPart();
    }

}
