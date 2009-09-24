/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.part;

import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.notation.NotationFigureGenerator;

/**
 * AbstractNotationEditPart
 */
public abstract class AbstractNotationEditPart extends AbstractDefaultEditPart {


    private NotationFigureGenerator figureGenerator;
    private IDiagramSelectionHandler selectionHandler;

    /**
     * 
     */
    public NotationFigureGenerator getFigureGenerator() {
        if( this.figureGenerator == null )
            figureGenerator = DiagramUiPlugin.getDiagramNotationManager().getFigureGenerator(getNotationId());

        return figureGenerator;
    }
    
    public void setFigureFactory(NotationFigureGenerator ff) {
        this.figureGenerator = ff;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getSelectionHandler()
     */
    public IDiagramSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setSelectionHandler(com.metamatrix.modeler.diagram.ui.editor.DiagramSelectionHandler)
     */
    public void setSelectionHandler(IDiagramSelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

}
