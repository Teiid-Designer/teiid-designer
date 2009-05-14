/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.part;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.swt.graphics.Color;

import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.PropertyChangeManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.mapping.ui.figure.MappingExtentFigure;
import com.metamatrix.modeler.mapping.ui.model.MappingExtentNode;


/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MappingExtentEditPart extends AbstractDiagramEditPart {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static int nExtents = 0;
//    private int thisExtentID = 0;
    
    /** Singleton instance of MarqueeDragTracker. */
    private DragTracker myDragTracker = null;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public MappingExtentEditPart() {
        super();
    }
    
    public MappingExtentEditPart(String diagramTypeId) {
        super();
        setDiagramTypeId(diagramTypeId);
        init();
//        thisExtentID = nExtents;
        nExtents++;
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
        MappingExtentFigure extentFigure = (MappingExtentFigure)getFigureFactory().createFigure(getModel());
		extentFigure.setLocation(location);
        
		List toolTips = ((MappingExtentNode)getModel()).getToolTipStrings();
		if( toolTips != null && !toolTips.isEmpty() )
			extentFigure.setToolTip(extentFigure.createToolTip(toolTips));
		
//        boolean isRequired = ((MappingExtentNode)getModel()).getExtent().isMappingRequired();
//        if( isRequired ) {
////           extentFigure.setOutlineColor(ColorConstants.red);
//        }
        extentFigure.setOutlineWidth(3);
        return extentFigure;
    }


    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
    **/
    @Override
    protected void createEditPolicies() {

        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new DiagramNodeSelectionEditPolicy());
        installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, null);
    }
    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     * You must implement this method if you want you root model to have 
     * children!
    **/
    @Override
    protected List getModelChildren() {

        List children = ((DiagramModelNode) getModel()).getChildren();
 
        return children;
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

    

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            ((MappingExtentFigure)getFigure()).updateForSize(((DiagramModelNode) getModel()).getSize());
            getChangeManager().refresh(PropertyChangeManager.ANCHORS, false);
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CONNECTION)) {
            getChangeManager().refresh(PropertyChangeManager.GENERAL, false);
            getChangeManager().refresh(PropertyChangeManager.ANCHORS, false);
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        }   else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            getChangeManager().refresh(PropertyChangeManager.ANCHORS, false);
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        }
    }
    
    @Override
    public void resizeChildren() {
        getDiagramFigure().updateForSize(((DiagramModelNode) getModel()).getSize());
    }
    
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    @Override
    public void hiliteBackground(Color hiliteColor) {
    	if( hasConnections() ) {
			getDiagramFigure().hiliteBackground(hiliteColor);
    	} else {
			DiagramModelNode nextNode = (MappingExtentNode)getModel();
			if( !(nextNode.getModelObject() instanceof StagingTable ) ) {
				getDiagramFigure().hiliteBackground(null);
			} else
				getDiagramFigure().hiliteBackground(hiliteColor);
    	}
        	
    }
    
    private boolean hasConnections() {
		DiagramModelNode nextNode = (MappingExtentNode)getModel();
		
		if( (nextNode.getSourceConnections() != null &&
			 !nextNode.getSourceConnections().isEmpty() ) ||
			(nextNode.getTargetConnections() != null &&
			 !nextNode.getTargetConnections().isEmpty() ) ) {
			return true;
		}
		return false;
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
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#layout(boolean)
     */
    @Override
    public void layout(boolean layoutChildren) {
        // Then do a getFigure().layout here.
        if (getDiagramFigure() != null) {
            getDiagramFigure().layoutFigure();
        }
        
        updateModelSize();
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
            myDragTracker = new SelectEditPartTracker(this); //(this, getSelectionHandler());
        }
        return myDragTracker;
    }
    
    

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#shouldReveal()
	 */
	@Override
    public boolean shouldReveal() {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#updateModelSize()
	 */
	@Override
    public void updateModelSize() {
		if( ((MappingExtentNode)getModel()).getExtent().getMappingReference() instanceof StagingTable)
			((MappingExtentNode)getModel()).updateModelForExtent();
		else
			super.updateModelSize();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	@Override
    public void activate() {
		// XXX Auto-generated method stub
		super.activate();
			
		if( ((MappingExtentNode)getModel()).getExtent().getMappingReference() instanceof StagingTable)
			((MappingExtentNode)getModel()).updateModelForExtent();
	}
    /**
     *  
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#constructionCompleted()
     * @since 5.0
     */
    @Override
    public void constructionCompleted(boolean updateLinkedParts) {
        if( isUnderConstruction() ) {
            getChangeManager().executeRefresh(updateLinkedParts);
            super.constructionCompleted(updateLinkedParts);
        }   
    }
}




