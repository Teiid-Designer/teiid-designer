/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.part;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.events.MouseEvent;

import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.navigation.figure.FocusNodeFigure;
import com.metamatrix.modeler.relationship.ui.navigation.figure.NavigationDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.navigation.model.FocusModelNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FocusNodeEditPart extends NonFocusNodeEditPart {
	///////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
//	private static final String THIS_CLASS = "RelationshipNodeEditPart"; //$NON-NLS-1$
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public FocusNodeEditPart(NavigationDiagramFigureFactory figureFactory) {
		super(figureFactory);
		init();
	}
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	@Override
    public void init() {
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {
        
		Point location = new Point(100, 100);
		FocusNodeFigure nodeFigure = (FocusNodeFigure)getFigureFactory().createFigure(getModel());
		nodeFigure.setLocation(location);
        
		return nodeFigure;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.EditableEditPart#edit()
	 */
	@Override
    public void edit() {
        
//		if( ModelEditorManager.canEdit( ((DiagramModelNode)getModel()).getModelObject() ) )
//			ModelEditorManager.edit(((DiagramModelNode)getModel()).getModelObject());
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
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.part.NavigationNodeEditPart#getSelectedNavigationNode(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
    public NavigationNode getSelectedNavigationNode(MouseEvent me) {
		return (NavigationNode)((NavigationModelNode)getModel()).getModelObject();
	}

}
