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

package com.metamatrix.modeler.diagram.ui.connection;

import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;

/**
 * AnchorManager
 */
public interface AnchorManager {
    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    
    

    /**
     * Add NodeConnectionAnchor to anchorManager cache
     * The cache implementation may be different for each manager.
     * @param targetAnchor
     * @return success value
     */
    public boolean add(NodeConnectionAnchor targetAnchor);
    
    /**
     * move NodeConnectionAnchor from one direction to another
     * @param targetAnchor
     * @return success value
     */
    public boolean move(NodeConnectionAnchor targetAnchor);
    
    /**
     * Remove NodeConnectionAnchor to anchorManager cache
     * The cache implementation may be different for each manager.
     * @param targetAnchor
     * @return success value
     */
    public boolean remove(NodeConnectionAnchor targetAnchor);

    /**
     * Method used to reset source anchor positions from the edit part.
     * updateTargetEnd indicates whether the other end (target) anchor should be
     * repositioned as well.
     * @param updateTargetEnd
     */
    public void resetSourceAnchors(boolean updateTargetEnd);
    
    /**
     * Method used to reset target anchor positions from the edit part.
     * updateSourceEnd indicates whether the other end (source) anchor should be
     * repositioned as well.
     * @param updateSourceEnd
     */
    public void resetTargetAnchors(boolean updateSourceEnd);
    
    /**
     * Method used to reorder all anchors so there is minized overlapping of links
     */
    public void reorderAllAnchors(boolean updateBothEnds);
    
    /**
     * Returns a List of source connection edit parts for the edit part owning this anchor manager.
     * @return List 
     */
    public List getSourceConnections();

    /**
     * Returns a List of target connection edit parts for the edit part owning this anchor manager.
     * @return List 
     */
    public List getTargetConnections();
    
    /**
     * Returns the source anchor model object for a given connect edit part.
     * @param connection
     * @return NodeConnectionAnchor 
     */
    public ConnectionAnchor getSourceAnchor(NodeConnectionEditPart connection) ;

    /**
     * Returns the target anchor model object for a given connect edit part.
     * @param connection
     * @return NodeConnectionAnchor 
     */
    public ConnectionAnchor getTargetAnchor(NodeConnectionEditPart connection);
    
    /**
     * Indicates if current edit part contains source anchors.
     * @return hasSourceAnchors
     */
    public boolean hasSourceAnchors();
    
    /**
     * Indicates if current edit part contains target anchors.
     * @return hasTargetAnchors
     */
    public boolean hasTargetAnchors();
    
    /**
     * Method used to set the position of any input anchor.
     * Implementation is left to the concrete class.
     * @param theAnchor
     * @param direction
     */
    public void setAnchorPosition(NodeConnectionAnchor theAnchor, int direction );
    
  
}
