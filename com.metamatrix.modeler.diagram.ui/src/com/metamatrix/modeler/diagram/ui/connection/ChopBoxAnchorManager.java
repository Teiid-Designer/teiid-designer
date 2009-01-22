/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;

import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;


/** 
 * @since 4.3
 */
public class ChopBoxAnchorManager implements AnchorManager {
//    private ConnectionAnchor sourceAnchor;
//    private ConnectionAnchor targetAnchor;
    private DiagramEditPart diagramEditPart;
    
    /**
     * Construct an instance of AnchorManager.
     */
    public ChopBoxAnchorManager(DiagramEditPart diagramEditPart) {
        this.diagramEditPart = diagramEditPart;
    }
    
    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#add(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionAnchor)
     * @since 4.3
     */
    public boolean add(NodeConnectionAnchor targetAnchor) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#move(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionAnchor)
     * @since 4.3
     */
    public boolean move(NodeConnectionAnchor targetAnchor) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#remove(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionAnchor)
     * @since 4.3
     */
    public boolean remove(NodeConnectionAnchor targetAnchor) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#resetSourceAnchors(boolean)
     * @since 4.3
     */
    public void resetSourceAnchors(boolean updateTargetEnd) {
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#resetTargetAnchors(boolean)
     * @since 4.3
     */
    public void resetTargetAnchors(boolean updateSourceEnd) {
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#reorderAllAnchors(boolean)
     * @since 4.3
     */
    public void reorderAllAnchors(boolean updateBothEnds) {
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#getSourceConnections()
     * @since 4.3
     */
    public List getSourceConnections() {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#getTargetConnections()
     * @since 4.3
     */
    public List getTargetConnections() {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#getSourceAnchor(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart)
     * @since 4.3
     */
    public ConnectionAnchor getSourceAnchor(NodeConnectionEditPart connection) {
        // This anchor manager belongs to the edit part.
        // This edit part knows about all it's target connections
        // An anchor is either target or source
        // 

        if (connection.getSourceAnchor() == null) {
            // Create a anchor for it.
            ConnectionAnchor newAnchor = createSourceAnchor();
            connection.setSourceAnchor(newAnchor);
            return newAnchor;
        }
        return connection.getSourceAnchor();
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#getTargetAnchor(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart)
     * @since 4.3
     */
    public ConnectionAnchor getTargetAnchor(NodeConnectionEditPart connection) {
        // This anchor manager belongs to the edit part.
        // This edit part knows about all it's target connections
        // An anchor is either target or source
        // 
        if (connection.getTargetAnchor() == null) {
            // Create a anchor for it.
            ConnectionAnchor newAnchor = createTargetAnchor();
            connection.setTargetAnchor(newAnchor);
            return newAnchor;
        }
        return connection.getTargetAnchor();
    }
    
    private ConnectionAnchor createTargetAnchor() {
        // Now there might be two here.
        ConnectionAnchor newAnchor = new ChopboxAnchor(diagramEditPart.getFigure());
//        targetAnchor = newAnchor;
        return newAnchor;
    }

    private ConnectionAnchor createSourceAnchor() {
        ConnectionAnchor newAnchor = new ChopboxAnchor(diagramEditPart.getFigure());
//        sourceAnchor = newAnchor;
        return newAnchor;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#hasSourceAnchors()
     * @since 4.3
     */
    public boolean hasSourceAnchors() {
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#hasTargetAnchors()
     * @since 4.3
     */
    public boolean hasTargetAnchors() {
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#setAnchorPosition(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionAnchor, int)
     * @since 4.3
     */
    public void setAnchorPosition(NodeConnectionAnchor theAnchor,
                                  int direction) {
    }

}
