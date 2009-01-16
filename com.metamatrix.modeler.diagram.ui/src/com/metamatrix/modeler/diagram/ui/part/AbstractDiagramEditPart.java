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

package com.metamatrix.modeler.diagram.ui.part;

import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigureFactory;

/**
 * AbstractDiagramEditPart provides a base class for all Metamatrix EditParts.
 * These classes are specialized to provide a standard set of methods coordinated with
 * DiagramFigure and DiagramModelNode interface methods to simplify coordinate of selection,
 * resizing, updates and other control-type functions.
 */
public abstract class AbstractDiagramEditPart extends AbstractDefaultEditPart {
	private boolean supportsExpanding = false;
    private DiagramFigureFactory figureFactory;
    private IDiagramSelectionHandler selectionHandler;

    /* 
     * 
     */
    public DiagramFigureFactory getFigureFactory() {
        if( this.figureFactory == null )
            this.figureFactory = DiagramUiPlugin.getDiagramTypeManager().getDiagram(getDiagramTypeId()).getFigureFactory();
            
        return this.figureFactory;
    }
    
    public void setFigureFactory(DiagramFigureFactory ff) {
        this.figureFactory = ff;
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

	public boolean supportsExpanding() {
		return supportsExpanding;
	}

	public void setSupportsExpanding(boolean supportsExpanding) {
		this.supportsExpanding = supportsExpanding;
	}

}
