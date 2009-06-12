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
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationNodeFigure;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationEditPart
	extends AbstractGraphicalEditPart
	implements PropertyChangeListener{
	private NavigationDiagramFigureFactory figureFactory;
	/**
	 * 
	 */
	public NavigationEditPart(NavigationDiagramFigureFactory figureFactory) {
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
			refreshVisuals();
		} else if (prop.equals(UiConstants.NavigationModelNodeProperties.LOCATION)) {
//			createOrUpdateAnchorsLocations(true);
//			refreshAllLabels();
			refreshVisuals();
		} else if (prop.equals(UiConstants.NavigationModelNodeProperties.CHILDREN)) {
			refreshChildren();
		}
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
		// XXX Auto-generated method stub

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
}
