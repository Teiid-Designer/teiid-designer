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

import org.eclipse.gef.EditPart;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;

/**
 * @author blafond
 *
 * AbstractDiagramEditPartFactory provides a base class for all Metamatrix Diagram Edit Part Factories
 * This class handles the notation ID as well as implements the default method for creating Edit Parts
 * for the EditPartFactory interface method.
 */
public abstract class AbstractDiagramEditPartFactory implements DiagramEditPartFactory {
    
    // ===============================================
    // FIELDS
    // ===============================================
    
    private String sNotationId;
    private IDiagramSelectionHandler selectionHandler;
    // ===============================================
    // Methods
    // ===============================================
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPartFactory;#createEditPart()
     */
    public EditPart createEditPart(EditPart context, Object model) {
        return null;
    }    
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getNotationId()
     */
    public String getNotationId() {
        return sNotationId;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#setNotationId(java.lang.String)
     */
    public void setNotationId(String sNotationId) {
        this.sNotationId = sNotationId;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getSelectionHandler()
     */
    public IDiagramSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#setSelectionHandler(com.metamatrix.modeler.diagram.ui.editor.DiagramSelectionHandler)
     */
    public void setSelectionHandler(IDiagramSelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getConnectionEditPart()
     */
    public NodeConnectionEditPart getConnectionEditPart() {
        return new NodeConnectionEditPart();
    }

}
