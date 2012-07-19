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
import org.teiid.designer.diagram.ui.figure.DiagramFigureFactory;

/**
 * AbstractDiagramEditPart provides a base class for all EditParts. These classes are specialized to provide a standard set of
 * methods coordinated with DiagramFigure and DiagramModelNode interface methods to simplify coordinate of selection, resizing,
 * updates and other control-type functions.
 *
 * @since 8.0
 */
public abstract class AbstractDiagramEditPart extends AbstractDefaultEditPart {
    private boolean supportsExpanding = false;
    private DiagramFigureFactory figureFactory;
    private IDiagramSelectionHandler selectionHandler;

    public DiagramFigureFactory getFigureFactory() {
        if (this.figureFactory == null) this.figureFactory = DiagramUiPlugin.getDiagramTypeManager().getDiagram(getDiagramTypeId()).getFigureFactory();

        return this.figureFactory;
    }

    public void setFigureFactory( DiagramFigureFactory ff ) {
        this.figureFactory = ff;
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#getSelectionHandler()
     */
    @Override
	public IDiagramSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#setSelectionHandler(org.teiid.designer.diagram.ui.editor.DiagramSelectionHandler)
     */
    @Override
	public void setSelectionHandler( IDiagramSelectionHandler selectionHandler ) {
        this.selectionHandler = selectionHandler;
    }

    public boolean supportsExpanding() {
        return supportsExpanding;
    }

    public void setSupportsExpanding( boolean supportsExpanding ) {
        this.supportsExpanding = supportsExpanding;
    }

}
