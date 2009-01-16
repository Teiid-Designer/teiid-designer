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

package com.metamatrix.modeler.relationship.ui.navigation.part;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

import com.metamatrix.modeler.relationship.ui.navigation.NavigationGraphicalViewer;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.selection.NavigationSelectionHandler;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NonFocusNodeEditPart extends AbstractNavigationEditPart {
	private static int currentNodeID = 0;
//	private int thisNodeID = 0;
	///////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	///////////////////////////////////////////////////////////////////////////////////////////////
//	private DragTracker myDragTracker = null;
    
//	private static final String THIS_CLASS = "RelationshipNodeEditPart"; //$NON-NLS-1$
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public NonFocusNodeEditPart(NavigationDiagramFigureFactory figureFactory) {
		super(figureFactory);
//		thisNodeID = currentNodeID;
		init();
		currentNodeID++;
	}
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public void init() {
//		if( getAnchorManager() == null ) {
//			setAnchorManager(getEditPartFactory().getAnchorManager(this));
//		}
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {
        
		Point location = new Point(50, 80);
		Figure nodeFigure = getFigureFactory().createFigure(getModel());
		nodeFigure.setLocation(location);
        
		return nodeFigure;
	}
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.EditableEditPart#edit()
	 */
	public void edit() {
        
//		if( ModelEditorManager.canEdit( ((DiagramModelNode)getModel()).getModelObject() ) )
//			ModelEditorManager.edit(((DiagramModelNode)getModel()).getModelObject());
	}
    
	@Override
    public void performRequest(Request request){
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT ) {
//			getSelectionHandler().handleDoubleClick(this.getModelObject());
		}
	}
	
	@Override
    public NavigationSelectionHandler getSelectionHandler() {
		return ((NavigationGraphicalViewer)getViewer()).getSelectionHandler();
	}
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 * You must implement this method if you want you root model to have 
	 * children!
	**/
	@Override
    protected List getModelChildren() {

		List children = ((NavigationModelNode) getModel()).getChildren();
 
		return children;
	}

    
	public void resizeChildren() {
		// call header.resize();
		if( getNavigationNodeFigure() != null )
			getNavigationNodeFigure().updateForSize(((NavigationModelNode) getModel()).getSize());
	}

}
