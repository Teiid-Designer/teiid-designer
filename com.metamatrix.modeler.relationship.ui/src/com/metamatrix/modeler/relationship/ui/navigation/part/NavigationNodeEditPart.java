/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.part;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.swt.events.MouseEvent;


import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure;
import com.metamatrix.modeler.relationship.ui.navigation.selection.NavigationSelectionHandler;

/**
 * DiagramEditPart provides an interface for all Metamatrix EditParts.
 * This interface is specialized to provide a standard set of methods coordinated with
 * DiagramFigure and DiagramModelNode interface methods to simplify coordinate of selection,
 * resizing, updates and other control-type functions.
 */
public interface NavigationNodeEditPart extends NodeEditPart {
    
	/**
	 * This method is provided to allow the edit part to be the manager of the layout of it's children
	 * and not affect the children (see layout(layoutChildren) method.
	 */
	void layout();
    
	/**
	 * This method is provided to allow the edit part to be the manager of the layout of it's children
	 * and not affect the children (see layout(layoutChildren) method.
	 * @param layoutChildren
	 */
	void layout(boolean layoutChildren);
    
	/**
	 * Used by the edit part to update the model location with current location of "figure"
	 *
	 */
	void updateModelPosition();
    
	/**
	 * Used by the edit part to update the model size with current size of "figure"
	 *
	 */
	void updateModelSize();
    
	/**
	 * Used by the edit part to access the underlying model object that the edit part, model, and figure
	 * are based on or referenced to. This is the hook method to send back the 'real' selected item in a diagram.
	 * @return modelObject;
	 */
	Object getModelObject();
    
	/**
	 * A convenience method which uses the Root to obtain the EditPartViewer.
	 * @throws NullPointerException if the root is not found
	 * @return the EditPartViewer
	 */
	EditPartViewer getViewer();


	/** Method used to return the EditPart's diagram selection handlerD
	 */
	NavigationSelectionHandler getSelectionHandler();
    
	/** Method used to set the EditPart's diagram selection handler
	 * @param sNotationId
	 */
	void setSelectionHandler(NavigationSelectionHandler selectionHandler);
	
	IFigure getFigure();
   
	NavigationNode getSelectedNavigationNode(MouseEvent me);
	NavigationNode getSelectedNavigationNode(Point lastMousePoint);
	
	NavigationNodeFigure getNavigationNodeFigure();
}
