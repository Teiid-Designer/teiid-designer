/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.part;

import java.util.Iterator;
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
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Color;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlOperationNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractNotationEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.HiliteDndNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartManager;
import com.metamatrix.modeler.diagram.ui.util.directedit.LabelCellEditorLocator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;


/**
 * UmlOperationEditPart
 */
public class UmlOperationEditPart extends AbstractNotationEditPart implements DirectEditPart {
	private DirectEditManager manager;
	private DragTracker myDragTracker = null;
    
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {

		Point location = new Point(100, 100);
		Figure newFigure = getFigureGenerator().createFigure(getModel()); 
		newFigure.setLocation(location);
		((DiagramModelNode) getModel()).setPosition(location);
		((DiagramModelNode) getModel()).setSize(newFigure.getSize());
                
		return newFigure;
	}
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 * You need to tell how children nodes will be layed out...
	**/
	@Override
    protected void createEditPolicies() {
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new HiliteDndNodeSelectionEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPartEditPolicy());
	}
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.EditableEditPart#edit()
	 */
	public void edit() {
		if( getSelectionHandler().shouldRename(getModelObject()) ) {
			if( ModelerCore.getModelEditor().hasName(getModelObject())  
				&& !ModelObjectUtilities.isReadOnly(getModelObject()))
				performDirectEdit();
		}
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
    
	public String getText() {
		return ((DiagramModelNode)getModel()).getName();
	}

	public void setText(String newName) {
		((DiagramModelNode)getModel()).setName(newName);
	}
    
	public String getEditString(){
		return ((DiagramModelNode)getModel()).getName();
	}
    
	private Label getLabel() {
		Label label = null;
		if( getFigure() instanceof DirectEditFigure ) {
			label = ((DirectEditFigure)getFigure()).getLabelFigure();
		}
		return label;
	}
    

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#hiliteBackground(org.eclipse.swt.graphics.Color)
	 */
	@Override
    public void hiliteBackground(Color hiliteColor) {
		getDiagramFigure().hiliteBackground(hiliteColor);
	}
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#shouldHiliteBackground()
	 */
	@Override
    public boolean shouldHiliteBackground(List sourceEditParts) {
		// We'll start off by checking to see that list of sourceEditParts do not have the same parent as this.
		Iterator iter = sourceEditParts.iterator();
		DiagramEditPart nextEP = null;
		while( iter.hasNext()) {
			nextEP = (DiagramEditPart)iter.next();
			if( nextEP.getParent().equals(this.getParent()))
				return false;
		}
        
		return true;
	}
    
	/* (non-JavaDoc)
	 * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#resizeChildren()
	 * Overrides AbstractDefaultEditPart so it can put the "data type" in the signature
	**/
	@Override
    public void refreshName() {
		// Need to get the figure and update the name
		getDiagramFigure().updateForName(((UmlOperationNode)getModel()).getSignature());
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
	 * Update the positions of the association's labels
	**/
	@Override
    public void refreshFont(boolean refreshChildren) {
		// Then do a getFigure().layout here.
		if (getDiagramFigure() != null) {
			getDiagramFigure().refreshFont();
			getDiagramFigure().layoutFigure();
			// Let's get children of container and set their model 
			((DiagramModelNode)getModel()).setSize(getFigure().getSize());
			((DiagramModelNode)getModel()).setPosition(new Point(getFigure().getBounds().x, getFigure().getBounds().y));
//			  System.out.println(" ==>> UmlAttributeEditPart.refreshFont()  new model  Location = " + 
//								  ((DiagramModelNode)getModel()).getPosition() + "  Size = " + ((DiagramModelNode)getModel()).getSize());
		}
		refreshVisuals();
		refreshAllLabels();
	}
	

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart#getEditManager()
	 */
	public DirectEditPartManager getEditManager() {
		return (DirectEditPartManager)manager;
	}
}
