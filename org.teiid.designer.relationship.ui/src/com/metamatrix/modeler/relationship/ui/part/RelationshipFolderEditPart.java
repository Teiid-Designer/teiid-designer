/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.part;

import java.beans.PropertyChangeEvent;

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
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Display;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartManager;
import com.metamatrix.modeler.diagram.ui.util.directedit.LabelCellEditorLocator;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;


/**
 * UmlPackageEditPart
 */
public class RelationshipFolderEditPart extends AbstractDiagramEditPart implements DirectEditPart {
	///////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	/** Singleton instance of MarqueeDragTracker. */
	private DragTracker myDragTracker = null;
	private DirectEditManager manager;
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public RelationshipFolderEditPart() {
		super();
		init();
        
	}
    
	public RelationshipFolderEditPart(String diagramTypeId) {
		super();
		setDiagramTypeId(diagramTypeId);
		init();
	}
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	public void init() {
		if( getAnchorManager() == null )
			setAnchorManager(getEditPartFactory().getAnchorManager(this));
	}
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {

		Point location = new Point(100, 100);
		Figure newFigure = getFigureFactory().createFigure(getModel());
		newFigure.setLocation(location);
                
		return newFigure;
	}
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 * You need to tell how children nodes will be layed out...
	**/
	@Override
    protected void createEditPolicies() {
		setPrimaryParent(true);
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new DiagramNodeSelectionEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPartEditPolicy());
	}
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.EditableEditPart#edit()
	 */
	public void edit() {
		// Here's where we open it's package diagram.
		((DiagramViewer)getViewer()).setInput( ((DiagramModelNode)getModel()).getModelObject() );
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
			if( getViewer() != null ) {
				UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
					public void run() {
						((DiagramViewer)getViewer()).getEditor().openContext( getModelObject() );
					}
				});
                
			}
		}
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
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	**/
	@Override
    protected void refreshVisuals() {
		Point loc = ((DiagramModelNode) getModel()).getPosition();
		Dimension size = ((DiagramModelNode) getModel()).getSize();
		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
		getFigure().repaint();
	}
    
	/* (non-JavaDoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	**/
	@Override
    public void propertyChange(PropertyChangeEvent evt) {
		// 
		String prop = evt.getPropertyName();
		if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
			resizeChildren();
		} else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.NAME)) {
			layout();
		}
        
		super.propertyChange(evt);

	}
    
	@Override
    public void resizeChildren() {
		// call header.resize();
		getDiagramFigure().updateForSize(((DiagramModelNode) getModel()).getSize());
	}
    
	public String getText() {
		return ((DiagramModelNode)getModel()).getName();
	}

	public void setText(String newName) {
		((DiagramModelNode)getModel()).setName(newName);
	}

	public String getEditString(){
		return ((DiagramModelNode)getModel()).getName();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart#getEditManager()
	 */
	public DirectEditPartManager getEditManager() {
		return (DirectEditPartManager)manager;
	}
	
	private Label getLabel() {
		Label label = null;
		if( getFigure() instanceof DirectEditFigure ) {
			label = ((DirectEditFigure)getFigure()).getLabelFigure();
		}
		return label;
	}
}
