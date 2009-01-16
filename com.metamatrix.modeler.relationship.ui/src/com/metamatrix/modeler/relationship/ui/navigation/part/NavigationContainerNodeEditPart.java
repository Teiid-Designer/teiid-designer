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


import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.events.MouseEvent;

import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationContainerNodeFigure;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;


/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationContainerNodeEditPart extends AbstractNavigationEditPart {
	///////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	///////////////////////////////////////////////////////////////////////////////////////////////
//	private DragTracker myDragTracker = null;
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public NavigationContainerNodeEditPart(NavigationDiagramFigureFactory figureFactory) {
		super(figureFactory);
	}
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {
        
		Figure nodeFigure = getFigureFactory().createFigure(getModel());
		nodeFigure.setLocation(((NavigationModelNode)getModel()).getPosition());
		nodeFigure.setSize(((NavigationModelNode)getModel()).getSize());
        
		return nodeFigure;
	}

	
	@Override
    public void layout() {
		getNavigationNodeFigure().layoutFigure();
		// Layout children here...
//		int parentDiameter = ((NavigationContainerModelNode)this.getModel()).getWidth();
//		
//		runCircularLayout(parentDiameter);
		updateModelSize();
//		
//		// get it's children and update their model positions.
//		List containerChildren = ((NavigationContainerModelNode)getModel()).getContainerChildren();
//		
//		Iterator iter = containerChildren.iterator();
//		while (iter.hasNext()) {
//			Object nextObj = iter.next();
//			if (nextObj instanceof NavigationNodeEditPart) {
//				((NavigationNodeEditPart)nextObj).updateModelSize();
//				((NavigationNodeEditPart)nextObj).updateModelPosition();
//			}
//		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
    protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, null); // new NonResizableEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new DiagramNodeSelectionEditPolicy());
//		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPartEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new NavigationDiagramXYLayoutPolicy());
	}
	

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#getSelectedNavigationNode(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
    public NavigationNode getSelectedNavigationNode(MouseEvent me) {
		Point selectedPt = new Point(me.x, me.y);
		
		return getSelectedNavigationNode(selectedPt);
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#getSelectedNavigationNode(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
    public NavigationNode getSelectedNavigationNode(Point selectedPt) {
		// Ask the figure for the NavigationModelNode for a given mouse x,y value;
		NavigationModelNode modelNode = ((NavigationContainerNodeFigure)getNavigationNodeFigure()).getSelectedModelNode(selectedPt);
		if( modelNode != null ) {
			return (NavigationNode)modelNode.getModelObject();
		}
		else if( getModel() instanceof NavigationNode) {
			return (NavigationNode)getModel();
		}
		return null;
	}

}
