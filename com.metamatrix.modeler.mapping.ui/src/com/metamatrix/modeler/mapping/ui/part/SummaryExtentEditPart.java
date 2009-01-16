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

package com.metamatrix.modeler.mapping.ui.part;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
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
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.mapping.ui.figure.SummaryExtentFigure;
import com.metamatrix.modeler.mapping.ui.model.SummaryExtentNode;


/**
 * @author jhelbling
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SummaryExtentEditPart extends MappingExtentEditPart /*AbstractDiagramEditPart*/ {
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
    
    public SummaryExtentEditPart() {
        super();
//        System.out.println("[SummaryExtentEditPart.ctor] BOT");
    }
    
    public SummaryExtentEditPart(String diagramTypeId) {
        super();
        setDiagramTypeId(diagramTypeId);
        init();
//        thisExtentID = nExtents;
        nExtents++;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void init() {
        if( getAnchorManager() == null )
            setAnchorManager(getEditPartFactory().getAnchorManager(this));
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {
//        System.out.println("[SummaryExtentEditPart.createFigure] TOP");
        
        Point location = new Point(100, 100);
        SummaryExtentFigure extentFigure = (SummaryExtentFigure)getFigureFactory().createFigure(getModel());
		extentFigure.setLocation(location);
        
		List toolTips = ((SummaryExtentNode)getModel()).getToolTipStrings();
		if( toolTips != null && !toolTips.isEmpty() )
			extentFigure.setToolTip(extentFigure.createToolTip(toolTips));
		
        boolean isRequired = ((SummaryExtentNode)getModel()).getExtent().isMappingRequired();
        if( isRequired ) {
           extentFigure.setOutlineColor(ColorConstants.red);
           extentFigure.setOutlineWidth(3);
        }

//        System.out.println("[SummaryExtentEditPart.createFigure] BOT");        
        
        
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
//        System.out.println(" -->> MappingExtentEditPart.propertyChange():  Type = " + prop + "  CurrentSize = " + ((DiagramModelNode) getModel()).getSize());
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            ((SummaryExtentFigure)getFigure()).updateForSize(((DiagramModelNode) getModel()).getSize());
            createOrUpdateAnchorsLocations(true);
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CONNECTION)) {
            refresh();
            createOrUpdateAnchorsLocations(true);
        }   else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            createOrUpdateAnchorsLocations(true);
            refreshVisuals();
        }
    }
    
    @Override
    public void resizeChildren() {
        // call header.resize();
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
			DiagramModelNode nextNode = (SummaryExtentNode)getModel();
			if( !(nextNode.getModelObject() instanceof StagingTable ) ) {
				getDiagramFigure().hiliteBackground(null);
			} else
				getDiagramFigure().hiliteBackground(hiliteColor);
    	}
        	
    }
    
    private boolean hasConnections() {
		DiagramModelNode nextNode = (SummaryExtentNode)getModel();
		
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
//        jh: Defect 20609 - call the SummaryExtentNode.updateModelForExtent unconditionally
//		if( ((SummaryExtentNode)getModel()).getExtent().getMappingReference() instanceof StagingTable)
			((SummaryExtentNode)getModel()).updateModelForExtent();
//		else
//			super.updateModelSize();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	@Override
    public void activate() {
		// XXX Auto-generated method stub
		super.activate();
			
		if( ((SummaryExtentNode)getModel()).getExtent().getMappingReference() instanceof StagingTable)
			((SummaryExtentNode)getModel()).updateModelForExtent();
	}

}




