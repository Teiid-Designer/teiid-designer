/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.part;

import org.eclipse.gef.EditPartFactory;
import org.teiid.designer.diagram.ui.connection.AnchorManager;
import org.teiid.designer.diagram.ui.connection.NodeConnectionEditPart;
import org.teiid.designer.diagram.ui.editor.IDiagramSelectionHandler;

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
