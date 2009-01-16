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

package com.metamatrix.modeler.relationship.ui.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
//import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;

import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartManager;
import com.metamatrix.modeler.diagram.ui.util.directedit.LabelCellEditorLocator;
import com.metamatrix.modeler.relationship.ui.figure.RelationshipTypeFigure;
import com.metamatrix.modeler.relationship.ui.model.RelationshipTypeModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipTypeNodeEditPart extends AbstractDiagramEditPart implements DirectEditPart {
	///////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	///////////////////////////////////////////////////////////////////////////////////////////////
	private DragTracker myDragTracker = null;
	private DirectEditManager manager;
	
//	private static final String THIS_CLASS = "RelationshipNodeEditPart"; //$NON-NLS-1$
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public RelationshipTypeNodeEditPart() {
		super();
	}
    
	public RelationshipTypeNodeEditPart(String diagramTypeId) {
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
		Figure nodeFigure = getFigureFactory().createFigure(getModel());
		nodeFigure.setLocation(location);
        
		// let's check to see if the name is italisized?
		int nameFontStyle = ScaledFont.BOLD_STYLE;
		RelationshipTypeModelNode relNode = (RelationshipTypeModelNode)getModel();
		RelationshipType relType = (RelationshipType)relNode.getModelObject();
		if( relType.isAbstract() ) {
			nameFontStyle = ScaledFont.BOLD_ITALICS_STYLE;
		} 
        
		((RelationshipTypeFigure)nodeFigure).setNameFontStyle(nameFontStyle);
        
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
		if( ModelerCore.getModelEditor().hasName(getModelObject()) )
			performDirectEdit();
	}
    
	public void performDirectEdit(){
		if(manager == null)
			manager = new DirectEditPartManager(this, 
				TextCellEditor.class, new LabelCellEditorLocator(getLabel()));
		manager.show();
	}

	@Override
    public void performRequest(Request request){
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			if( ! (getSelectionHandler().handleDoubleClick(this.getModelObject())) ){
				if( ModelerCore.getModelEditor().hasName(getModelObject()) )
					performDirectEdit();
			}
		}
	}
	// ----------------------------------
	// DirectEditPart interface methods
	//	----------------------------------
	public String getText() {
		return ((DiagramModelNode)getModel()).getName();
	}

	public String getEditString(){
		return ((DiagramModelNode)getModel()).getName();
	}

	public void setText(String newName) {
		((DiagramModelNode)getModel()).setName(newName);
	}
	private Label getLabel() {
		Label label = null;
		if( getFigure() instanceof DirectEditFigure ) {
			label = ((DirectEditFigure)getFigure()).getLabelFigure();
		}
		return label;
	}
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 * You must implement this method if you want you root model to have 
	 * children!
	**/
	@Override
    protected List getModelChildren() {

		List children = ((RelationshipTypeModelNode) getModel()).getChildren();
 
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
		String prop = evt.getPropertyName();
		if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
			resizeChildren();
		}  else if(prop.equals(DiagramUiConstants.DiagramNodeProperties.CONNECTION)) {
			refresh();
			createOrUpdateAnchorsLocations(true);
		}
        
		super.propertyChange(evt);
		
		if (prop.equals(DiagramUiConstants.DiagramNodeProperties.NAME)) {
			layout();
			refreshVisuals();
		} else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
			createOrUpdateAnchorsLocations(true);
			refreshAllLabels();
		}
//		if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CONNECTION)) {
////			if ( UiConstants.Util.isDebugEnabled(DebugConstants.PROPERTIES) && 
////				 UiConstants.Util.isDebugEnabled(DebugConstants.TRACE)) { //$NON-NLS-1$
////				UiConstants.Util.debug(DebugConstants.PROPERTIES, //$NON-NLS-1$
////					THIS_CLASS + "propertyChange() Connection Changed for EditPart Model = " + ((DiagramModelNode)getModel()).getName()); //$NON-NLS-1$
////			}
//			refresh();
//			createOrUpdateAnchorsLocations(true);
//			refreshAllLabels();
//		}
//		if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
//			((DiagramModelNode)getModel()).updateAssociations();
//		}

	}
    
	@Override
    public void resizeChildren() {
		// call header.resize();
		getDiagramFigure().updateForSize(((DiagramModelNode) getModel()).getSize());
	}
	
	/* (non-JavaDoc)
	 * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#resizeChildren()
	**/
	@Override
    public void refreshName() {
		super.refreshName();
		RelationshipTypeModelNode rmn = (RelationshipTypeModelNode)getModel();
		// Need to get the figure and update the type also
		((RelationshipTypeFigure)getDiagramFigure()).updateForChange(rmn.getRoleString());
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
	

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart#getEditManager()
	 */
	public DirectEditPartManager getEditManager() {
		return (DirectEditPartManager)manager;
	}

}
