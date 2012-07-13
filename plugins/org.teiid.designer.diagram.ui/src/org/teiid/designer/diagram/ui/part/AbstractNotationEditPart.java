/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.part;

import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.editor.IDiagramSelectionHandler;
import org.teiid.designer.diagram.ui.notation.NotationFigureGenerator;

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
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#getSelectionHandler()
     */
    @Override
	public IDiagramSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#setSelectionHandler(org.teiid.designer.diagram.ui.editor.DiagramSelectionHandler)
     */
    @Override
	public void setSelectionHandler(IDiagramSelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

}
