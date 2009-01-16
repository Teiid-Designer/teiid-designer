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

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlPackageFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlPackageNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractNotationEditPart;
import com.metamatrix.modeler.diagram.ui.part.EditableEditPart;
import com.metamatrix.modeler.diagram.ui.part.PropertyChangeManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartManager;
import com.metamatrix.modeler.diagram.ui.util.directedit.LabelCellEditorLocator;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;


/**
 * UmlPackageEditPart
 */
public class UmlPackageEditPart extends AbstractNotationEditPart implements DirectEditPart, EditableEditPart {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Singleton instance of MarqueeDragTracker. */
    private DragTracker myDragTracker = null;
	private DirectEditManager manager;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public UmlPackageEditPart() {
        super();
        init();
        
    }
    
    public UmlPackageEditPart(String diagramTypeId) {
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
        Figure newFigure = getFigureGenerator().createFigure(getModel());
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

        if(  doubleClickedName() ) {
            if(ModelerCore.getModelEditor().hasName(getModelObject()) 
               && !ModelObjectUtilities.isReadOnly(getModelObject()) )
                            performDirectEdit();
        } else {
            // Let's just open up the package diagram for this thing
            ModelEditorManager.open(((DiagramModelNode)getModel()).getModelObject(), true);       
//            ((DiagramViewer)getViewer()).setInput( ((DiagramModelNode)getModel()).getModelObject() );
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
            edit();
        }
    }
    
    
    private boolean doubleClickedName() {
        SelectionTracker tracker = (SelectionTracker)getDragTracker(null);
        if( tracker != null && tracker.getLastMouseLocation() != null ) {
            Point point = new Point(tracker.getLastMouseLocation());
            Point viewportLoc = ((DiagramViewer)getViewer()).getViewportLocation();
            // Let's get the rectangle for the figure for the name in the header...
            UmlPackageFigure cFigure = (UmlPackageFigure)getDiagramFigure();
            IFigure nameFigure = cFigure.getNameFigure();
            if( nameFigure != null ) {
                // Let's add a coupleof pixels on the bounds
                Rectangle rect = new Rectangle(nameFigure.getBounds());
                rect.x = rect.x + cFigure.getBounds().x - 3 - viewportLoc.x;
                rect.y = rect.y + cFigure.getBounds().y - 3 - viewportLoc.y;
                rect.width += 6;
                rect.height += 6;
                if( rect.contains(point) ) {
                    return true;
                }
            }
        }
        
        return false;
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
            getChangeManager().refresh(PropertyChangeManager.RESIZE_CHILDREN, true);
        }
        
        super.propertyChange(evt);
        
		if (prop.equals(DiagramUiConstants.DiagramNodeProperties.NAME)) {
			layout();
		} else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            getChangeManager().refresh(PropertyChangeManager.ANCHORS, true);
            getChangeManager().refresh(PropertyChangeManager.LABELS, true);
		}

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.PATH)) {
            refreshPath();
            layout();
        }
    }
    
    @Override
    public void resizeChildren() {
        // call header.resize();
        getDiagramFigure().updateForSize(((DiagramModelNode) getModel()).getSize());
    }
    
//    /**
//     * This method is not mandatory to implement, but if you do not implement
//     * it, you will not have the ability to rectangle-selects several figures...
//    **/
//    public DragTracker getDragTracker(Request req) {       
//        // Unlike in Logical Diagram Editor example, I use a singleton because this 
//        // method is Entered  >>  several time, so I prefer to save memory ; and it works!
//        if (m_dragTracker == null && ((DiagramViewer)getViewer()).getSelectionHandler() != null ) {
//            m_dragTracker = new SelectionTracker(this, ((DiagramViewer)getViewer()).getSelectionHandler());
//        }
//        return m_dragTracker;
//    }
    
    
    public void refreshPath() {
        ((UmlPackageFigure)getDiagramFigure()).updateForPath(((UmlPackageNode)getModel()).getPath());
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
    
//    private Label getLabel() {
//        Label label = null;
//        if( getFigure() instanceof DirectEditFigure ) {
//            label = ((DirectEditFigure)getFigure()).getLabelFigure();
//        }
//        return label;
//    }


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

