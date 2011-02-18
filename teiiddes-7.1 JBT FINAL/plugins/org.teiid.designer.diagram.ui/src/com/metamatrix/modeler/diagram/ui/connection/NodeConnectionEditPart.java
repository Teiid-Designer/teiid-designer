/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.figure.DiagramPolylineConnection;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;

/**
 * NodeConnectionEditPart
 */
public class NodeConnectionEditPart extends AbstractConnectionEditPart 
                                 implements PropertyChangeListener, FigureListener {
                                     
                                     
     private ConnectionAnchor sourceAnchor;
     private ConnectionAnchor targetAnchor;
     private DiagramViewer diagramViewer;
     private boolean allowBendpoints = false;
     

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {
        IFigure connectionFigure = super.createFigure();

        ((PolylineConnection)connectionFigure).setLineStyle( Graphics.LINE_DOT );
        int iStandardWidth = ((PolylineConnection)connectionFigure).getLineWidth();
        iStandardWidth = 1;
        ((PolylineConnection)connectionFigure).setLineWidth( iStandardWidth );
        ((PolylineConnection)connectionFigure).setForegroundColor(ColorConstants.blue);
        
        return connectionFigure;
    }
    
    /**
     * Returns the model of this represented as a Wire.
     * 
     * @return  Model of this as <code>Wire</code>
     */
    protected NodeConnectionModel getConnectionModel() {
        return (NodeConnectionModel)getModel();
    }

    /**
     * Adds extra EditPolicies as required. 
     */
    
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, null);
        if( allowBendpoints )
            installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new LinkBendpointEditPolicy());
//        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new HiliteDndNodeSelectionEditPolicy());
//        installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
//        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new LinkEndpointEditPolicy());
        //        //Note that the Connection is already added to the diagram and knows its Router.
        //        refreshBendpointEditPolicy();
        //        installEditPolicy(EditPolicy.CONNECTION_ROLE,new WireEditPolicy());
    }

    /* (non-JavaDoc)
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CHILDREN)) {
//            refreshChildren();
            refreshVisuals();
        } 

        else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.PROPERTIES)) {
            refreshVisuals();
        } else if( prop.equals(DiagramUiConstants.DiagramNodeProperties.BENDPOINT)) {
            refreshBendpoints();   
        }
    }
    
    /**
     * Updates the bendpoints, based on the model.
     */
    protected void refreshBendpoints() {
        if (((NodeConnectionModel)getModel()).getRouterStyle() != DiagramUiConstants.LinkRouter.MANUAL )
            return;
        List modelConstraint = getConnectionModel().getBendpoints();
        getConnectionFigure().setRoutingConstraint(modelConstraint);
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
        ((NodeConnectionModel)getModel()).addPropertyChangeListener(this);
        this.getFigure().addFigureListener( this );
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
        ((NodeConnectionModel)getModel()).removePropertyChangeListener(this);
        this.getFigure().removeFigureListener( this );
    }
    
    /* (non-JavaDoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
     * Relays out the 'Assocation' labels 
     *  
     */
    @Override
    public void refreshVisuals() {

    }   
    
    public void figureMoved( IFigure fig ) {
//        System.out.println("[NodeConnectionEditPart.figureMoved] TOP"); //$NON-NLS-1$        

        // when our polyline figure changes, we must re-layout the Assocation's labels
        NodeConnectionModel daAssociation = (NodeConnectionModel)getModel();
        
        if( getSource() instanceof DiagramEditPart && getTarget() instanceof DiagramEditPart ) {
	        DiagramEditPart depSourceEditPart = (DiagramEditPart)this.getSource();
	        DiagramEditPart depTargetEditPart = (DiagramEditPart)this.getTarget();
	            
	        if ( depSourceEditPart != null && depTargetEditPart != null ) {            
	            ConnectionAnchor ncaSourceAnchor = depSourceEditPart.getSourceConnectionAnchor( this );
	            ConnectionAnchor ncaTargetAnchor = depTargetEditPart.getTargetConnectionAnchor( this );
	            
	            if( ncaSourceAnchor != null && ncaTargetAnchor != null && 
	            	ncaSourceAnchor instanceof NodeConnectionAnchor &&
					ncaTargetAnchor instanceof NodeConnectionAnchor ) {
	                daAssociation.placeStereotypeAndName( ((NodeConnectionAnchor)ncaSourceAnchor).getDirection(),
	                                                      ((NodeConnectionAnchor)ncaTargetAnchor).getDirection(), 
	                                                      getConnectionFigure().getPoints() );                                                            
	            }
	        }  
        }
    }
    
    public void setSourceAndTarget(DiagramEditPart someEditPart) {
        NodeConnectionModel daAssociation = (NodeConnectionModel)getModel();
        if( this.getSource() == null ) {
            if( daAssociation.getSourceNode().equals(someEditPart.getModel()))
                setSource(someEditPart);
            else {
                DiagramEditPart rootEditPart = (DiagramEditPart)someEditPart.getViewer().getContents();
                DiagramEditPart sourceEditPart = rootEditPart.getEditPart((DiagramModelNode)daAssociation.getSourceNode());
                setSource(sourceEditPart);
            }
        }
        if( this.getTarget() == null ) {
            if( daAssociation.getTargetNode().equals(someEditPart.getModel())) {
                setTarget(someEditPart);
            } else {
                DiagramEditPart rootEditPart = (DiagramEditPart)someEditPart.getViewer().getContents();
                DiagramEditPart targetEditPart = rootEditPart.getEditPart((DiagramModelNode)daAssociation.getTargetNode());
                setTarget(targetEditPart);
            }
        }
    }
    
    
    public void setSourceAndTarget(EditPart someEditPart) {
        NodeConnectionModel daAssociation = (NodeConnectionModel)getModel();
        if( this.getSource() == null ) {
            if( daAssociation.getSourceNode().equals(someEditPart.getModel()))
                setSource(someEditPart);
            else if( someEditPart instanceof DiagramEditPart) {
                DiagramEditPart rootEditPart = (DiagramEditPart)someEditPart.getViewer().getContents();
                DiagramEditPart sourceEditPart = rootEditPart.getEditPart((DiagramModelNode)daAssociation.getSourceNode());
                setSource(sourceEditPart);
            } else {
				// using target node (model) find edit part
				EditPart rootEditPart = someEditPart.getViewer().getContents();
				Iterator iter = rootEditPart.getChildren().iterator();
				EditPart sourcePart = null;
				EditPart nextChild = null;
				Object sourceNode = daAssociation.getSourceNode();
				while( iter.hasNext() && sourcePart == null) {
					nextChild = (EditPart)iter.next();
					if( nextChild.getModel() != null && nextChild.getModel().equals(sourceNode))
						sourcePart = nextChild;
				}
				setSource(sourcePart);
			}
        }
        if( this.getTarget() == null ) {
            if( daAssociation.getTargetNode().equals(someEditPart.getModel())) {
                setTarget(someEditPart);
            } else if( someEditPart instanceof DiagramEditPart ) {
                DiagramEditPart rootEditPart = (DiagramEditPart)someEditPart.getViewer().getContents();
                DiagramEditPart targetEditPart = rootEditPart.getEditPart((DiagramModelNode)daAssociation.getTargetNode());
                setTarget(targetEditPart);
            } else {
            	// using target node (model) find edit part
				EditPart rootEditPart = someEditPart.getViewer().getContents();
				Iterator iter = rootEditPart.getChildren().iterator();
				EditPart targetPart = null;
				EditPart nextChild = null;
				Object targetNode = daAssociation.getTargetNode();
				while( iter.hasNext() && targetPart == null) {
					nextChild = (EditPart)iter.next();
					if( nextChild.getModel() != null && nextChild.getModel().equals(targetNode))
						targetPart = nextChild;
				}
				setTarget(targetPart);
            }
        }
    }
    
                     
    /**
     * @return
     */
    public ConnectionAnchor getSourceAnchor() {
        return sourceAnchor;
    }

    /**
     * @return
     */
    public ConnectionAnchor getTargetAnchor() {
        return targetAnchor;
    }

    /**
     * @param anchor
     */
    public void setSourceAnchor(ConnectionAnchor anchor) {
        sourceAnchor = anchor;
    }

    /**
     * @param anchor
     */
    public void setTargetAnchor(ConnectionAnchor anchor) {
        targetAnchor = anchor;
    }

	/**
	 * @return
	 */
	public DiagramViewer getDiagramViewer() {
		return diagramViewer;
	}

	/**
	 * @param viewer
	 */
	public void setDiagramViewer(DiagramViewer viewer) {
		diagramViewer = viewer;
	}

	public void hilite(boolean value) {
		if( getFigure() instanceof DiagramPolylineConnection) {
			((DiagramPolylineConnection)getFigure()).hilite(value);
		}
	}

    /** 
     * @return Returns the allowBendpoints.
     * @since 4.2
     */
    public boolean isAllowBendpoints() {
        return this.allowBendpoints;
    }
    /** 
     * @param allowBendpoints The allowBendpoints to set.
     * @since 4.2
     */
    public void setAllowBendpoints(boolean allowBendpoints) {
        this.allowBendpoints = allowBendpoints;
    }
}
