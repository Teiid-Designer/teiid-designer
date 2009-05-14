/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

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

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.EditableEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.relationship.ui.figure.FocusNodeFigure;
import com.metamatrix.modeler.relationship.ui.model.FocusModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FocusNodeEditPart extends AbstractDiagramEditPart implements EditableEditPart {
	///////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	///////////////////////////////////////////////////////////////////////////////////////////////
	private DragTracker myDragTracker = null;
    
//	private static final String THIS_CLASS = "RelationshipNodeEditPart"; //$NON-NLS-1$
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public FocusNodeEditPart() {
		super();
	}
    
	public FocusNodeEditPart(String diagramTypeId) {
		super();
		setDiagramTypeId(diagramTypeId);
		init();
	}
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public void init() {
		if( getAnchorManager() == null ) {
			setAnchorManager(getEditPartFactory().getAnchorManager(this));
		}
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {
        
		Point location = new Point(100, 100);
		FocusNodeFigure nodeFigure = (FocusNodeFigure)getFigureFactory().createFigure(getModel());
		nodeFigure.setLocation(location);
		
		List toolTips = ((FocusModelNode)getModel()).getToolTipStrings();
		if( toolTips != null && !toolTips.isEmpty() )
			nodeFigure.setToolTip(nodeFigure.createToolTip(toolTips));
		
		return nodeFigure;
	}


	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	**/
	@Override
    protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new DiagramNodeSelectionEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPartEditPolicy());
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
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT && getSelectionHandler() != null) {
			getSelectionHandler().handleDoubleClick(this.getModelObject());
		}
	}
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 * You must implement this method if you want you root model to have 
	 * children!
	**/
	@Override
    protected List getModelChildren() {

		List children = ((FocusModelNode) getModel()).getChildren();
 
		return children;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	**/
	@Override
    protected void refreshVisuals() {

		Point loc = ((DiagramModelNode) getModel()).getPosition();
		Dimension size = ((DiagramModelNode) getModel()).getSize();
		Rectangle r = new Rectangle(loc, new Dimension(size.width, size.height));
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
		getFigure().repaint();
	 }

	@Override
    public void propertyChange(PropertyChangeEvent evt) {
		// 
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
			resizeChildren();
			setLabelLocation();
		} else if( prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
			setLabelLocation();
			createOrUpdateAnchorsLocations(true);
			refreshAllLabels();
		}  else if(prop.equals(DiagramUiConstants.DiagramNodeProperties.CONNECTION)) {
			refresh();
			createOrUpdateAnchorsLocations(true);
		}
	}
	
	private void setLabelLocation() {
		DiagramModelNode labelNode = ((FocusModelNode)getModel()).getLabelNode();
		if( labelNode != null ) {
			int newX = ((FocusModelNode)getModel()).getX() + ((FocusModelNode)getModel()).getWidth()-5;
			int newY = ((FocusModelNode)getModel()).getY() + ((FocusModelNode)getModel()).getHeight() - 6;
	
			labelNode.setPosition(new Point(newX, newY));
		}
	}
    
	@Override
    public void resizeChildren() {
		// call header.resize();
		getDiagramFigure().updateForSize(((DiagramModelNode) getModel()).getSize());
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
			myDragTracker = new SelectionTracker(this);
		}
		return myDragTracker;
	}

}
