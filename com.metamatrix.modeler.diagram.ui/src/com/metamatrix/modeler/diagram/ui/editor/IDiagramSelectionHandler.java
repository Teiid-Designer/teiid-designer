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

package com.metamatrix.modeler.diagram.ui.editor;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;

/**
 * IDiagramSelectionHandler
 * This interface defines common methods for Diagram Editor, Diagram Viewer, and Edit Parts
 * to communicate based on their specific diagram type.  Each diagram type plugin may provide their
 * own handler for selection, hiliting and double-clicking (editing).
 */
public interface IDiagramSelectionHandler {
    
    DiagramViewer getViewer();
    
    void deselectAll();
    
    void select(EObject selectedObject );
    
    List getSelectedEObjects();
    
    void select(ISelection selection );
    
    void clearDependencyHilites();
    
    void hiliteDependencies(Object selectedObject );
    
	void hiliteConnection(NodeConnectionEditPart connectionEditPart);
	
	void clearConnectionHilites();

    void selectAndReveal(EObject selectedObject );
    
    EditPart findEditPart(EObject selectedObject, boolean linksAllowed);
    
    EditPart findDiagramChildEditPart(EObject selectedObject, boolean linksAllowed);
    
    /**
     * This provides the diagram type plugin the chance to customize the double-click of an edit part's
     * DirectEdit request.  If it does intercede, then the return value should be TRUE, else false, and 
     * the edit policy is executed on the edit part.
     * @param selectedObject
     * @return
     */
    boolean handleDoubleClick( EObject selectedObject );
    
    boolean shouldRename(EObject dClickedEObject);
    
    /**
     * Method force in-line renaming of EObjects 
     * @param selectedObject
     * @since 5.0.2
     */
    void renameInline(EObject selectedObject);
    
    String getDiagramType();
    
    boolean shouldReveal(EObject selectedObject);
    
	public void setClearHilites(boolean clear);
	
	public void fireMouseExit();
}
