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
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.figure.LabeledRectangleFigure;
import com.metamatrix.modeler.relationship.ui.navigation.model.FreeNavigationModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.LabelModelNode;

/**
 * LabelEditPart
 */
public class LabelEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {
	private static int labelCount = 0;
	/**
	 * Construct an instance of LabelEditPart.
	 * 
	 */
	public LabelEditPart(LabelModelNode node) {
		super();
		setModel(node);
	}
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {

		Point posn = new Point( 10, 10); //new Point(100 + labelCount*30, 100 + labelCount*30);
		Figure newFigure = new LabeledRectangleFigure(((LabelModelNode)getModel()).getName(), false, null );
//		  NoteFigure noteFigure = new NoteFigure((Composite)getRoot().getViewer().getControl());
//		  noteFigure.setText("SOME NOTE FIGURE WITH LOTS OF TEXT HERE.");
//		  IFigure newFigure = noteFigure.getViewport().getContents();
//		  newFigure.setBackgroundColor(ColorConstants.cyan);
//		  newFigure.setBorder(new LineBorder(2));
		newFigure.setLocation(posn);
		((LabelModelNode) getModel()).setPosition(posn);
		((LabelModelNode) getModel()).setSize(newFigure.getSize());
		labelCount ++;
        
		return newFigure;
	}
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 * You need to tell how children nodes will be layed out...
	**/
	@Override
    protected void createEditPolicies() {
	}

    
	/* (non-JavaDoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	**/
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
        
		if( prop.equals(DiagramUiConstants.DiagramNodeProperties.NAME)) {
//			refreshName();
			((LabeledRectangleFigure)getFigure()).resize();
			((LabelModelNode)getModel()).setSize(getFigure().getSize());
//			  refreshVisuals();
		} else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
			refreshVisuals();
		} else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
			refreshVisuals();
		} else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.PROPERTIES)) {
			refreshVisuals();
			//            refreshChildren();
			//            layout(DiagramEditPart.LAYOUT_CHILDREN);
		}
	}
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	**/
	@Override
    protected void refreshVisuals() {
		Point loc = ((LabelModelNode) getModel()).getPosition();
		Dimension size = ((LabelModelNode) getModel()).getSize();
		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
		getFigure().repaint();
	}
    
    
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	@Override
    public DragTracker getDragTracker(Request request) {
		// We wanted to remove selection capability to the Labeled objects in the diagram.
		// This was the only way I could figure out how to do it.  Don't let it play
		// in selection...
		return null;
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
		((FreeNavigationModelNode)getModel()).addPropertyChangeListener(this);

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
		((FreeNavigationModelNode)getModel()).removePropertyChangeListener(this);

	}
	

	public void resetSize() {
		// tell the figure to update
		LabeledRectangleFigure figure = (LabeledRectangleFigure)getFigure();
		figure.resize();
		LabelModelNode node = (LabelModelNode)getModel();
		node.setSize(figure.getSize());
	}

}
