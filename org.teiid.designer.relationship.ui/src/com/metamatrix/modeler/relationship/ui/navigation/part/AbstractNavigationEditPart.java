/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.events.MouseEvent;
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationGraphicalViewer;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.selection.NavigationSelectionHandler;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AbstractNavigationEditPart
	extends AbstractGraphicalEditPart
	implements NavigationNodeEditPart, PropertyChangeListener{
		
	///////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	///////////////////////////////////////////////////////////////////////////////////////////////
	protected DragTracker myDragTracker = null;
	
	private NavigationDiagramFigureFactory figureFactory;
	
	/**
	 * 
	 */
	public AbstractNavigationEditPart(NavigationDiagramFigureFactory figureFactory) {
		super();
		this.figureFactory = figureFactory;
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();

		if (prop.equals(UiConstants.NavigationModelNodeProperties.SIZE)) {
//			createOrUpdateAnchorsLocations(true);
//			refreshAllLabels();
//			refreshVisuals();
		} else if (prop.equals(UiConstants.NavigationModelNodeProperties.LOCATION)) {
//			createOrUpdateAnchorsLocations(true);
//			refreshAllLabels();
//			animateMove(this);
			refreshVisuals();
		} else if (prop.equals(UiConstants.NavigationModelNodeProperties.CHILDREN)) {
			refreshChildren();
		}  else if (prop.equals(UiConstants.NavigationModelNodeProperties.CONNECTION)) {
			refresh();
		}
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	**/
	@Override
    protected void refreshVisuals() {
//		((NavigationModelNode) getModel()).printBounds(" --START >> AbstractNavEP.refreshVisuals()");
		Point loc = ((NavigationModelNode) getModel()).getPosition();
		Dimension size = ((NavigationModelNode) getModel()).getSize();
		Rectangle r = new Rectangle(loc, new Dimension(size.width, size.height));

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
		getFigure().setSize(size);
		getFigure().setLocation(loc);
//		((NavigationNodeFigure)getFigure()).layoutFigure();
//		((NavigationNodeFigure)getFigure()).updateForSize(size);
//		((NavigationNodeFigure)getFigure()).updateForLocation(loc);
		getFigure().repaint();
	 }

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
    protected IFigure createFigure() {
		return null;
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
			myDragTracker = new NavigationSelectionTracker(this, getSelectionHandler()); //(this, getSelectionHandler());
		}
		return myDragTracker;
	}
	
	@Override
    public void performRequest(Request request){
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT ) {
//			System.out.println("  -->> AbstractNavigationEditPart.performRequest():  Double-CLICK!!!");
//			getSelectionHandler().handleDoubleClick(this.getModelObject());
		}
	}
	
	/* (non-JavaDoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#activate()
	 * Makes the EditPart aware to changes in the model
	 * by adding itself to the model's list of listeners.
	 */
	@Override
    public void activate() {
		if (isActive())
			return;
		super.activate();
		((NavigationModelNode)getModel()).addPropertyChangeListener(this);
		if( getFigure() instanceof NavigationNodeFigure) {	
			if (getNavigationNodeFigure() != null)
				getNavigationNodeFigure().activate();
		}
	}

	/* (non-JavaDoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#deactivate()
	 * Makes the EditPart insensible to changes in the model
	 * by removing itself from the model's list of listeners.
	 */
	@Override
    public void deactivate() {
		if (!isActive())
			return;
		super.deactivate();
		((NavigationModelNode)getModel()).removePropertyChangeListener(this);
		if( getFigure() instanceof NavigationNodeFigure) {	
			if (getNavigationNodeFigure() != null)
				getNavigationNodeFigure().deactivate();
		}
	}

	/* (non-JavaDoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 * You must implement this method if you want you root model to have 
	 * children!
	**/
	@Override
    protected List getModelChildren() {
		List children = ((NavigationModelNode)getModel()).getChildren();
		return children;
	}
	
	public NavigationNodeFigure getNavigationNodeFigure() {
		if( getFigure() instanceof NavigationNodeFigure) {	
			return (NavigationNodeFigure)super.getFigure();
		}
		
		return null;
	}
	
	public NavigationDiagramFigureFactory getFigureFactory() {
		return figureFactory;
	}
	
	/* (non-JavaDoc)
	 * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#updateModelLocation()
	**/
	public void updateModelPosition() {
		((NavigationModelNode)getModel()).setPosition(
			new Point(getFigure().getBounds().x, getFigure().getBounds().y));
	}
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#getModelObject()
	 */
	public Object getModelObject() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#getSelectionHandler()
	 */
	public NavigationSelectionHandler getSelectionHandler() {
		return ((NavigationGraphicalViewer)getViewer()).getSelectionHandler();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#layout()
	 */
	public void layout() {
	// Layout out it's children first
		List editPartChildren = getChildren();
		Iterator iter = editPartChildren.iterator();
		EditPart nextEP = null;
		while (iter.hasNext()) {
			nextEP = (EditPart)iter.next();
			if (nextEP instanceof NavigationNodeEditPart)
				 ((NavigationNodeEditPart)nextEP).layout();
		}
		// Then do a getFigure().layout here.
		if (getNavigationNodeFigure() != null)
			getNavigationNodeFigure().layoutFigure();

		updateModelSize();
		
		// get it's children and update their model positions.
		List containerChildren = getChildren();
		iter = containerChildren.iterator();
		while (iter.hasNext()) {
			Object nextObj = iter.next();
			if (nextObj instanceof NavigationNodeEditPart) {
				((NavigationNodeEditPart)nextObj).updateModelPosition();
			}
		}
	}
	/*
	 *  (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.nabigation.part.NavigationNodeEditPart#layout(boolean)
	 */
	public void layout(boolean layoutChildren) {
		if( layoutChildren ) {
			// Layout out it's children first
			List editPartChildren = getChildren();
			Iterator iter = editPartChildren.iterator();
			EditPart nextEP = null;
			while (iter.hasNext()) {
				nextEP = (EditPart)iter.next();
				if (nextEP instanceof NavigationNodeEditPart)
					 ((NavigationNodeEditPart)nextEP).layout();
			}
		}
		// Then do a getFigure().layout here.
		if (getNavigationNodeFigure() != null)
			getNavigationNodeFigure().layoutFigure();

		updateModelSize();
		
		// get it's children and update their model positions.
		List containerChildren = getChildren();
		Iterator iter = containerChildren.iterator();
		while (iter.hasNext()) {
			Object nextObj = iter.next();
			if (nextObj instanceof NavigationNodeEditPart) {
				((NavigationNodeEditPart)nextObj).updateModelPosition();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#setSelectionHandler(com.metamatrix.modeler.relationship.ui.navigation.NavigationSelectionHandler)
	 */
	public void setSelectionHandler(NavigationSelectionHandler selectionHandler) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#updateModelSize()
	 */
	public void updateModelSize() {
		((NavigationModelNode)getModel()).setSize(getFigure().getSize());

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.GraphicalEditPart#getFigure()
	 */
	@Override
    public IFigure getFigure() {
		return super.getFigure();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#getSelectedNavigationNode(org.eclipse.swt.events.MouseEvent)
	 */
	public NavigationNode getSelectedNavigationNode(MouseEvent me) {
		// XXX Auto-generated method stub
		return (NavigationNode)((NavigationModelNode)getModel()).getModelObject();
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#getSelectedNavigationNode(org.eclipse.swt.events.MouseEvent)
	 */
	public NavigationNode getSelectedNavigationNode(Point lastMousePoint) {
		// XXX Auto-generated method stub
		return null;
	}

	public ConnectionAnchor getAnchor() {
		return new ChopboxAnchor(this.getFigure());
	}
	
	// =================================================================================================
	// =================================================================================================
	// Connection Methods from NodeEditPart interface and Overriding AbstractGraphicalEditPart
	// =================================================================================================
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	**/
	@Override
    protected List getModelSourceConnections() {
		return ((NavigationModelNode)getModel()).getSourceConnections();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	**/
	@Override
    protected List getModelTargetConnections() {
		return ((NavigationModelNode)getModel()).getTargetConnections();
	}
	/**
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(ConnectionEditPart)
	**/
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return getAnchor();
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(Request)
	**/
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getAnchor();
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(ConnectionEditPart)
	**/
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return getAnchor();
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(Request)
	**/
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getAnchor();
	}
	/**
	 * 
	 */
	public void animateMove(GraphicalEditPart gep) {
		int deltaX = 8;
		int deltaY = 8;
		
		Dimension size = ((NavigationModelNode) gep.getModel()).getSize();
		
		// we need to establish a path fromt he current location and the new location

		Point oldLoc = new Point(gep.getFigure().getBounds().x, gep.getFigure().getBounds().y);
		Point newLoc = ((NavigationModelNode) gep.getModel()).getPosition();
		Point tempLoc = new Point(oldLoc);
		
		boolean isRight = newLoc.x >= oldLoc.x;
		boolean isDown = newLoc.y >= oldLoc.y;
		int dx = deltaX;
		int dy = deltaY;
		if( !isRight )
			dx = dx*(-1);
		if( !isDown )
			dy = dy*(-1);
			
		boolean xArrived = false;
		boolean yArrived = false;
		while( !xArrived || !yArrived ) {
			if( Math.abs(tempLoc.x - newLoc.x) > deltaX ) {
				tempLoc.x += dx;
			} else {
				xArrived = true;
			}

			if( Math.abs(tempLoc.y - newLoc.y) > deltaY )  {
				tempLoc.y += dy;
			} else  {
				yArrived = true;
			}
			if( !xArrived || !yArrived ) {
				Rectangle r = new Rectangle(tempLoc, size);
				((GraphicalEditPart)gep.getParent()).setLayoutConstraint(gep, gep.getFigure(), r);
				gep.getContentPane().getUpdateManager().performUpdate(r);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
			}
		}
		Rectangle r = new Rectangle(newLoc, size);
		((GraphicalEditPart)gep.getParent()).setLayoutConstraint(gep, gep.getFigure(), r);
		gep.getContentPane().getUpdateManager().performUpdate(r);
	}
	
	public void animateMove(Point newLoc, Point oldLoc) {
		int deltaX = 4;
		int deltaY = 4;
		
		Dimension size = ((NavigationModelNode) getModel()).getSize();
		
		// we need to establish a path fromt he current location and the new location

		Point tempLoc = new Point(oldLoc);
		
		boolean isRight = newLoc.x >= oldLoc.x;
		boolean isDown = newLoc.y >= oldLoc.y;
		int dx = deltaX;
		int dy = deltaY;
		if( !isRight )
			dx = dx*(-1);
		if( !isDown )
			dy = dy*(-1);
			
		boolean xArrived = false;
		boolean yArrived = false;
		while( !xArrived || !yArrived ) {
			if( Math.abs(tempLoc.x - newLoc.x) > deltaX ) {
				tempLoc.x += dx;
			} else {
				xArrived = true;
			}

			if( Math.abs(tempLoc.y - newLoc.y) > deltaY )  {
				tempLoc.y += dy;
			} else  {
				yArrived = true;
			}
			if( !xArrived || !yArrived ) {
				Rectangle r = new Rectangle(tempLoc, size);
				((GraphicalEditPart)getParent()).setLayoutConstraint(this, this.getFigure(), r);
				getContentPane().getUpdateManager().performUpdate(r);
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
	}
	
	public void animateMove(Point startLoc, Point endLoc, int startingSize, int endingSize, boolean grow) {
		int deltaX = 4;
		int deltaY = 4;
		double deltaSize = 1.0;
		
		Dimension size = new Dimension(startingSize, startingSize);
		int nSteps = 1;
		
		// we need to establish a path fromt he current location and the new location

		Point tempLoc = new Point(endLoc);
		
		boolean isRight = startLoc.x >= endLoc.x;
		boolean isDown = startLoc.y >= endLoc.y;
		int nXSteps = Math.abs( (startLoc.x - endLoc.x)/deltaX );
		int nYSteps = Math.abs( (startLoc.y - endLoc.y)/deltaY );
		nSteps = Math.max(nXSteps, nYSteps);
		if( nSteps < 2 )
			nSteps = 2;
		
		int realDeltaSize = Math.abs(endingSize - startingSize);
		deltaSize = ((double)endingSize - (double)startingSize)/nSteps;
		int startStep = 0;
		if( deltaSize*nSteps < realDeltaSize ) {
			startStep = realDeltaSize - (int)(deltaSize*nSteps);
		}
		
		if( deltaSize <= 0 )
			deltaSize = 1;
		if( !grow )
			deltaSize *= -1;
		
		int dx = deltaX;
		int dy = deltaY;
		if( !isRight )
			dx = dx*(-1);
		if( !isDown )
			dy = dy*(-1);
			
		boolean xArrived = false;
		boolean yArrived = false;
		int iStep = 0;
		Point finalPoint = new Point(tempLoc);
		double realSizeW = size.width;
		double realSizeH = size.height;
		while( !xArrived || !yArrived ) {
			if( iStep > startStep ) {
				realSizeW += deltaSize;
				realSizeH += deltaSize;
			}
			size.width = (int)realSizeW;
			size.height = (int)realSizeH;
			if( Math.abs(tempLoc.x - startLoc.x) > deltaX ) {
				tempLoc.x += dx;
			} else {
				xArrived = true;
			}

			if( Math.abs(tempLoc.y - startLoc.y) > deltaY )  {
				tempLoc.y += dy;
			} else  {
				yArrived = true;
			}
			finalPoint.x = tempLoc.x - size.width/2;
			finalPoint.y = tempLoc.y - size.height/2;
			if( !xArrived || !yArrived ) {
				Rectangle r = new Rectangle(finalPoint, size);
				((GraphicalEditPart)getParent()).setLayoutConstraint(this, this.getFigure(), r);
				getContentPane().getUpdateManager().performUpdate(r);
			}
			iStep++;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
			}
		}
	}
}
