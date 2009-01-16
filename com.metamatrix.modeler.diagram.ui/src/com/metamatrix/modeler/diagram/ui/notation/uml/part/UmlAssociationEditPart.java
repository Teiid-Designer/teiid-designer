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

package com.metamatrix.modeler.diagram.ui.notation.uml.part;


import java.beans.PropertyChangeEvent;
import java.util.Collections;
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
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlAssociationNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractNotationEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.*;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartManager;
import com.metamatrix.modeler.diagram.ui.util.directedit.LabelCellEditorLocator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;


/**
 * UmlAssociationEditPart
 */
public class UmlAssociationEditPart extends AbstractNotationEditPart implements DirectEditPart  {
    private DirectEditManager manager;
    private DragTracker myDragTracker = null;
    
    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {

        Point location = new Point(100, 100);
        Figure newFigure = getFigureGenerator().createFigure(getModel()); //);
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
				&& !ModelObjectUtilities.isReadOnly(getModelObject()) )
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
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        String prop = evt.getPropertyName();
        
        if( prop.equals(DiagramUiConstants.DiagramNodeProperties.NAME)) {
            refreshName();
            ((DiagramEditPart)getParent().getParent()).layout(LAYOUT_CHILDREN);
            refreshVisuals();
        }
    }
    
    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#resizeChildren()
     * Overrides AbstractDefaultEditPart so it can put the "data type" in the signature
    **/
    @Override
    public void refreshName() {
        // Need to get the figure and update the name
        getDiagramFigure().updateForName(((UmlAssociationNode)getModel()).getSignature());
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
        }
        refreshVisuals();
        refreshAllLabels();
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
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getDependencies()
     */
    @Override
    public List getDependencies() {
        DiagramModelNode modelNode = (DiagramModelNode)getModel();
        if( modelNode != null )
            return modelNode.getDependencies();
            
        return Collections.EMPTY_LIST;
    }

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart#getEditManager()
	 */
	public DirectEditPartManager getEditManager() {
		return (DirectEditPartManager)manager;
	}
}

