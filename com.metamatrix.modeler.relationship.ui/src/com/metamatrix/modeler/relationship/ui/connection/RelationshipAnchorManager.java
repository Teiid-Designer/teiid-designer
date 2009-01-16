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

package com.metamatrix.modeler.relationship.ui.connection;

import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;

import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.DiagramUmlDependency;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionAnchor;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.relationship.ui.model.FocusModelNode;

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
   
	public boolean add(NodeConnectionAnchor targetAnchor) {
		return false;
	}
    
	public boolean move(NodeConnectionAnchor targetAnchor) {
		return false;
	}
    
	public boolean remove(NodeConnectionAnchor targetAnchor) {
		return false;
	}
    
	public void resetSourceAnchors(boolean updateTargetEnd) {
		setAnchorPositions();
	}
    
	public void resetTargetAnchors(boolean updateSourceEnd) {
		setAnchorPositions();   
	}
    
	/**
	 * @return
	 */
	public List getSourceConnections() {
		return diagramEditPart.getSourceConnections();
	}

	/**
	 * @return
	 */
	public List getTargetConnections() {
		return diagramEditPart.getTargetConnections();
	}
	/**
	 * @return
	 */
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
    
	public boolean hasSourceAnchors() {
		return (sourceAnchor != null);
	}
    
    
	public boolean hasTargetAnchors() {
		return (targetAnchor != null);
	}
    
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
	 * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#reorderAllAnchors()
	 * @since 4.2
	 */
    public void reorderAllAnchors(boolean updateBothEnds) {
        resetSourceAnchors(updateBothEnds);
        resetTargetAnchors(updateBothEnds);
    }
}
