/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
