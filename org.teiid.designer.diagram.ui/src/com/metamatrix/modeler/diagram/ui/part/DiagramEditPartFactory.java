/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.part;

import org.eclipse.gef.EditPartFactory;

import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;

/**
 * DiagramEditPartFactory
 */
public interface DiagramEditPartFactory extends EditPartFactory {

    void setNotationId( String sNotationId );
    String getNotationId();
    
    void setSelectionHandler(IDiagramSelectionHandler selectionHandler);
    
    IDiagramSelectionHandler getSelectionHandler();
    
    NodeConnectionEditPart getConnectionEditPart();
    
    AnchorManager getAnchorManager(DiagramEditPart editPart);

}
