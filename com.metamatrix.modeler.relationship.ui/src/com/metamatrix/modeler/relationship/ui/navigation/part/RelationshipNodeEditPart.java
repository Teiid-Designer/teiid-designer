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

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationGraphicalViewer;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.navigation.figure.RelationshipNodeFigure;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.RelationshipModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.selection.NavigationSelectionHandler;


/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipNodeEditPart extends AbstractNavigationEditPart {
	///////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	///////////////////////////////////////////////////////////////////////////////////////////////
//	private DragTracker myDragTracker = null;
	private static final int CIRCULAR_LAYOUT = 2;
    
//	private static final String THIS_CLASS = "RelationshipNodeEditPart"; //$NON-NLS-1$
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public RelationshipNodeEditPart(NavigationDiagramFigureFactory figureFactory) {
		super(figureFactory);
		init();
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
        
		Point location = new Point(100, 100);
		Figure nodeFigure = getFigureFactory().createFigure(getModel());
		nodeFigure.setLocation(location);
        
		return nodeFigure;
	}
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 * You must implement this method if you want you root model to have 
	 * children!
	**/
	@Override
    protected List getModelChildren() {
		Object model = getModel();
		if( model instanceof RelationshipModelNode ) {
			return ((RelationshipModelNode) getModel()).getChildren();
		}
 
		return Collections.EMPTY_LIST;
	}
	
	@Override
    public void layout() {
		// Layout children here...
		int parentDiameter = ((RelationshipModelNode)this.getModel()).getWidth();
		
		runCircularLayout(parentDiameter);
		
//		Iterator iter = getChildren().iterator();
//		AbstractNavigationEditPart nextEP = null;
//		Object nextObject = null;
//		int nChildren = getChildren().size();
//		
//		while( iter.hasNext() ) {
//			nextObject = iter.next();
//			if( nextObject instanceof AbstractNavigationEditPart) {
//				nextEP = (AbstractNavigationEditPart)nextObject;
//				
//			}
//		}
	}
	protected void runCircularLayout(int parentDiameter) {
		int outerPad = 10;
		List navigationParts = getChildren();
		
		if( navigationParts != null && !navigationParts.isEmpty() ) {
			// Locate the Focus Part in the center
			
//			outerNodeDiam = parentDiameter/(1 - );
			
//			setLabelPosition(focusPart);
			
			if( !navigationParts.isEmpty() ) {
				Iterator iter = navigationParts.iterator();
				NavigationNodeEditPart nextPart = null;
				int iPart = 0;
				int nNodes = navigationParts.size();
				Object nextObject = null;
				while( iter.hasNext() ) {
					nextObject = iter.next();
					if( nextObject instanceof NavigationNodeEditPart) {
						nextPart = (NavigationNodeEditPart)iter.next();
						setNextNodePoint(CIRCULAR_LAYOUT ,nextPart, nNodes, iPart, parentDiameter, outerPad);
	//					setLabelPosition(nextPart);	
						iPart++;
					}
				}
			}
		}
	}
	
	/*
	 * This method provides a method to place any navigation node (not focus) around the perimeter.
	 */
	private void setNextNodePoint(
		int layoutType, 
		NavigationNodeEditPart thePart, 
		int nNodes, 
		int iNode, 
		int parentDiameter,
		int outerPad ) {
		
		Rectangle sizeAndLocation = NavigationLayoutUtil.getNextCircularNodePoint(nNodes, iNode, parentDiameter, 0, outerPad);
		((NavigationModelNode)thePart.getModel()).setSize(sizeAndLocation.getSize());
		((NavigationModelNode)thePart.getModel()).setPosition(sizeAndLocation.getLocation());
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
    protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new DiagramNodeSelectionEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPartEditPolicy());
	}
	
	/**
	 * This method is not mandatory to implement, but if you do not implement
	 * it, you will not have the ability to rectangle-selects several figures...
	**/
	@Override
    public DragTracker getDragTracker(Request req) {
		// Unlike in Logical Diagram Editor example, I use a singleton because this 
		// method is Entered  >>  several time, so I prefer to save memory ; and it works!
		if (myDragTracker == null) {
			myDragTracker = new SelectionTracker(this); //(this, getSelectionHandler());
		}
		return myDragTracker;
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
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	**/
	@Override
    protected void refreshVisuals() {

		Point loc = ((NavigationModelNode) getModel()).getPosition();
		Dimension size = ((NavigationModelNode) getModel()).getSize();
		Rectangle r = new Rectangle(loc, new Dimension(size.width + 2, size.height + 2));
//System.out.println("  -->>> RelationshipNodeEditPart.refreshVisuals() Rect = " + r);
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
		getFigure().setSize(size);
		getFigure().setLocation(loc);
		((RelationshipNodeFigure)getFigure()).updateForSize(size);
//		((AbstractNavigationNodeFigure)getFigure()).updateForLocation(loc);
		getFigure().repaint();
	 }

    

	@Override
    public void propertyChange(PropertyChangeEvent evt) {
 
		super.propertyChange(evt);
	}
    
	public void resizeChildren() {
		// call header.resize();
		if( getNavigationNodeFigure() != null )
			getNavigationNodeFigure().updateForSize(((NavigationModelNode) getModel()).getSize());
	}
}
