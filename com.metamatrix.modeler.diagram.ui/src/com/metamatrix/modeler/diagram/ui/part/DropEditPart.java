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

package com.metamatrix.modeler.diagram.ui.part;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;


/** 
 * This interface provides EditParts a mechanism to allow dropping objects into
 * itself.  Initially created for adding relationship role objects to relationship
 * role containers.  But could be used for others.
 */
public interface DropEditPart {

    /**
     * Method provides drop point and drop object list for edit part to determine what and where
     * to drop the objects. 
     * @param dropPoint
     * @param dropList
     */
    void drop(Point dropPoint, List dropList);
    
    /**
     * Method provides generic method for DropAdapter and hilite edit policy to tell the edit part to hilite
     * itself for dragOver() or any parth within the edit part. 
     * @param hilite
     * @since 4.2
     */
    void hilite(boolean hilite);
    
    /**
     * Method to access the last hover point on an edit part.  This is required to maintain the mouse information
     * defined in the DropAdapter's DropTargetEvent.
     * This point can later be used by the drop method
     * @return point
     * @since 4.2
     */
    public Point getLastHoverPoint();
    /**
     * Setter for the last hover point on an edit part.  This is required to maintain the mouse information
     * defined in the DropAdapter's DropTargetEvent.
     * This point can later be used by the drop method
     * @param lastHoverPoint
     * @since 4.2
     */
    public void setLastHoverPoint(Point lastHoverPoint);
    
    
    /**
     * Method provides drop point and drop object list for edit part to determine what and where
     * to drop the objects. 
     * @param dropPoint
     * @param dropList
     */
    public boolean allowsDrop(Object target, List dropList);
}
