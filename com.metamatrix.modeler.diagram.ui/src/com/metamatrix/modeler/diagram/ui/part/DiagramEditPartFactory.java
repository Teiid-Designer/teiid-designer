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
