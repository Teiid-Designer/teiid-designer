/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.connection;

import java.util.List;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.teiid.designer.diagram.ui.connection.AnchorManager;
import org.teiid.designer.diagram.ui.connection.DiagramUmlDependency;
import org.teiid.designer.diagram.ui.connection.NodeConnectionAnchor;
import org.teiid.designer.diagram.ui.connection.NodeConnectionEditPart;
import org.teiid.designer.diagram.ui.connection.NodeConnectionModel;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;
import org.teiid.designer.relationship.ui.model.FocusModelNode;


/**
 * AnchorManager
 */
public class RelationshipAnchorManager implements AnchorManager {
	
	private NodeConnectionAnchor relationshipAnchor;
	private NodeConnectionAnchor sourceAnchor;
	private NodeConnectionAnchor targetAnchor;
	private ChopboxAnchor centerAnchor;
    
	private DiagramEditPart diagramEditPart;
    
	/**
	 * Construct an instance of AnchorManager.
	 * 
	 */
	public RelationshipAnchorManager(DiagramEditPart diagramEditPart) {
		this.diagramEditPart = diagramEditPart;
		init();
	}
    
	private void init() {
	}
   
	@Override
	public boolean add(NodeConnectionAnchor targetAnchor) {
		return false;
	}
    
	@Override
	public boolean move(NodeConnectionAnchor targetAnchor) {
		return false;
	}
    
	@Override
	public boolean remove(NodeConnectionAnchor targetAnchor) {
		return false;
	}
    
	@Override
	public void resetSourceAnchors(boolean updateTargetEnd) {
		setAnchorPositions();
	}
    
	@Override
	public void resetTargetAnchors(boolean updateSourceEnd) {
		setAnchorPositions();   
	}
    
	/**
	 * @return
	 */
	@Override
	public List getSourceConnections() {
		return diagramEditPart.getSourceConnections();
	}

	/**
	 * @return
	 */
	@Override
	public List getTargetConnections() {
		return diagramEditPart.getTargetConnections();
	}
	/**
	 * @return
	 */
	@Override
	public ConnectionAnchor getSourceAnchor(NodeConnectionEditPart connection) {
		// This anchor manager  belongs to the edit part.
		// This edit part knows about all it's target connections
		// An anchor is either target or source

		if( connection.getSourceAnchor() == null ) {
			createAnchors();
			// Create a anchor for it.
			// Let's see if this is a focus node or not.
			DiagramModelNode dmn = (DiagramModelNode)((NodeConnectionModel)connection.getModel()).getSourceNode();
			if( dmn instanceof FocusModelNode ) {
				connection.setSourceAnchor(centerAnchor);
				return centerAnchor;
			}
			if( connection.getModel() instanceof RelationshipLink) {
				if( ((RelationshipLink)connection.getModel()).sourceUsesCenterAnchor()) {
					connection.setSourceAnchor(relationshipAnchor);
					return relationshipAnchor;
				}
				connection.setSourceAnchor(targetAnchor);
				return targetAnchor;
			} else if(connection.getModel() instanceof DiagramUmlDependency ) {
			    return centerAnchor;
		    }
			return centerAnchor;
		}
		return connection.getSourceAnchor();
	}

	/**
	 * @return
	 */
	@Override
	public ConnectionAnchor getTargetAnchor(NodeConnectionEditPart connection) {
		// This anchor manager  belongs to the edit part.
		// This edit part knows about all it's target connections
		// An anchor is either target or source
		
		if( connection.getTargetAnchor() == null ) {
			createAnchors();
			// Create a anchor for it.
			// Let's see if this is a focus node or not.
			DiagramModelNode dmn = (DiagramModelNode)((NodeConnectionModel)connection.getModel()).getTargetNode();
			if( dmn instanceof FocusModelNode ) {
				connection.setTargetAnchor(centerAnchor);
				return centerAnchor;
			}
			if( connection.getModel() instanceof RelationshipLink) {
				if( ((RelationshipLink)connection.getModel()).targetUsesCenterAnchor()) {
					connection.setTargetAnchor(relationshipAnchor);
					return relationshipAnchor;
				}
				connection.setTargetAnchor(sourceAnchor);
				return sourceAnchor;
			} else if(connection.getModel() instanceof DiagramUmlDependency ) {
				return centerAnchor;
			}
			return centerAnchor;
		}
		return connection.getTargetAnchor();
	} 
    
	private void createAnchors() {
		if( sourceAnchor == null ) {
			sourceAnchor = new NodeConnectionAnchor(diagramEditPart.getFigure(), NodeConnectionAnchor.IS_SOURCE);
			sourceAnchor.setDirection(EAST);
	
			targetAnchor = new NodeConnectionAnchor(diagramEditPart.getFigure(), NodeConnectionAnchor.IS_SOURCE);
			targetAnchor.setDirection(WEST);
	
			relationshipAnchor = new NodeConnectionAnchor(diagramEditPart.getFigure(), NodeConnectionAnchor.IS_SOURCE);
			relationshipAnchor.setDirection(NORTH);
	
			centerAnchor = new ChopboxAnchor(diagramEditPart.getFigure());
			setAnchorPositions();
		}
	}
    
	@Override
	public boolean hasSourceAnchors() {
		return (sourceAnchor != null);
	}
    
    
	@Override
	public boolean hasTargetAnchors() {
		return (targetAnchor != null);
	}
    
	@Override
	public void setAnchorPosition(NodeConnectionAnchor theAnchor, int direction ) {

	}
    
	private void setAnchorPositions( ) {
          
		Dimension partSize = ((DiagramModelNode)diagramEditPart.getModel()).getSize();
		if( sourceAnchor == null )
			createAnchors();
			
		sourceAnchor.setOffsetH(0);
		sourceAnchor.setOffsetV(partSize.height);
		targetAnchor.setOffsetH(partSize.width);
		targetAnchor.setOffsetV(partSize.height);
		relationshipAnchor.setOffsetH(partSize.width/2);
		relationshipAnchor.setOffsetV(0);
	}
    
	/**
     *  
	 * @see org.teiid.designer.diagram.ui.connection.AnchorManager#reorderAllAnchors()
	 * @since 4.2
	 */
    @Override
	public void reorderAllAnchors(boolean updateBothEnds) {
        resetSourceAnchors(updateBothEnds);
        resetTargetAnchors(updateBothEnds);
    }
}
