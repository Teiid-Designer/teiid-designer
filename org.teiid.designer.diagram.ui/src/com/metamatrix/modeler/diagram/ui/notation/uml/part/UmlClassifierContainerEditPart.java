/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.part;

//import java.util.Iterator;
//import java.util.List;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.figure.ContainerFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractNotationEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.PropertyChangeManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;

/**
 * UmlClassifierContainerEditPart
 */
public class UmlClassifierContainerEditPart extends AbstractNotationEditPart {
    
    private DragTracker myDragTracker = null;
    
    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {

        Point posn = new Point(100, 100);
        Figure newFigure = getFigureGenerator().createFigure(getModel());
        newFigure.setLocation(posn);
        ((DiagramModelNode) getModel()).setPosition(posn);
        ((DiagramModelNode) getModel()).setSize(newFigure.getSize());
                
        return newFigure;
    }
    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     * You need to tell how children nodes will be layed out...
    **/
    @Override
    protected void createEditPolicies() {
        setSelectablePart(false);
        installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new DiagramNodeSelectionEditPolicy());
    }
    
    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
    **/
    @Override
    protected void refreshVisuals() {
        Point posn = ((DiagramModelNode) getModel()).getPosition();
        Dimension size = ((DiagramModelNode) getModel()).getSize();
        Rectangle r = new Rectangle(posn, size);

        ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
        getFigure().repaint();
    }

    @Override
    public IFigure getContentPane() {
        if( this.getFigure() instanceof ContainerFigure )
            return ((ContainerFigure)getFigure()).getContentsPane();
        
        return null;
    }
    
    /* (non-JavaDoc)
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        String prop = evt.getPropertyName();
        
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CHILDREN)) {
            getChangeManager().refresh(PropertyChangeManager.CHILDREN, true);

            layout(DiagramEditPart.LAYOUT_CHILDREN);

            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
            getChangeManager().refresh(PropertyChangeManager.ANCHORS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            getChangeManager().refresh(PropertyChangeManager.ANCHORS, false);
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {

            getChangeManager().refresh(PropertyChangeManager.ANCHORS, false);
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.PROPERTIES)) {
            refreshVisuals();
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        }
    }
    
    /** 
     * Update the positions of the association's labels
    **/
    @Override
    public void refreshFont(boolean refreshChildren) {
    // Walk the children and tell them to refresh font.
    // Layout out it's children first
        if( refreshChildren ) {
            List editPartChildren = getChildren();
            Iterator iter = editPartChildren.iterator();
            EditPart nextEP = null;
            while (iter.hasNext()) {
                nextEP = (EditPart)iter.next();
                if (nextEP instanceof DiagramEditPart)
                     ((DiagramEditPart)nextEP).refreshFont(refreshChildren);
            }
        }
        // Then do a getFigure().layout here.
        if (getDiagramFigure() != null) {
            getDiagramFigure().refreshFont();
            getDiagramFigure().layoutFigure();
            // Let's get children of container and set their model 
            ((DiagramModelNode)getModel()).setSize(getFigure().getSize());
            ((DiagramModelNode)getModel()).setPosition(new Point(getFigure().getBounds().x, getFigure().getBounds().y));
//            System.out.println(" ==>> UmlClassifierEditPart.refreshFont()  new model  Location = " + 
//                                ((DiagramModelNode)getModel()).getPosition() + "  Size = " + ((DiagramModelNode)getModel()).getSize());
        }
        refreshAllLabels();
    }
    
    @Override
    public void performRequest(Request request) {
        if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
            getSelectionHandler().handleDoubleClick(((DiagramEditPart)this.getParent()).getModelObject());
        }
    }
    
    /**
     * This method is not mandatory to implement, but if you do not implement it, you will not have the ability to
     * rectangle-selects several figures...
     */
    @Override
    public DragTracker getDragTracker(Request req) {
        // Unlike in Logical Diagram Editor example, I use a singleton because this
        // method is Entered >> several time, so I prefer to save memory ; and it works!
        if (myDragTracker == null) {
            myDragTracker = new SelectionTracker(this);
        }
        return myDragTracker;
    }
}

