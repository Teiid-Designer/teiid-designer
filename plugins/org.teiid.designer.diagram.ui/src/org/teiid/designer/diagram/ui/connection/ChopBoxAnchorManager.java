/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.connection;

import java.util.List;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.teiid.designer.diagram.ui.part.DiagramEditPart;


/** 
 * @since 8.0
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
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#add(org.teiid.designer.diagram.ui.connection.NodeConnectionAnchor)
     * @since 4.3
     */
    @Override
	public boolean add(NodeConnectionAnchor targetAnchor) {
        return false;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#move(org.teiid.designer.diagram.ui.connection.NodeConnectionAnchor)
     * @since 4.3
     */
    @Override
	public boolean move(NodeConnectionAnchor targetAnchor) {
        return false;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#remove(org.teiid.designer.diagram.ui.connection.NodeConnectionAnchor)
     * @since 4.3
     */
    @Override
	public boolean remove(NodeConnectionAnchor targetAnchor) {
        return false;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#resetSourceAnchors(boolean)
     * @since 4.3
     */
    @Override
	public void resetSourceAnchors(boolean updateTargetEnd) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#resetTargetAnchors(boolean)
     * @since 4.3
     */
    @Override
	public void resetTargetAnchors(boolean updateSourceEnd) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#reorderAllAnchors(boolean)
     * @since 4.3
     */
    @Override
	public void reorderAllAnchors(boolean updateBothEnds) {
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#getSourceConnections()
     * @since 4.3
     */
    @Override
	public List getSourceConnections() {
        return null;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#getTargetConnections()
     * @since 4.3
     */
    @Override
	public List getTargetConnections() {
        return null;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#getSourceAnchor(org.teiid.designer.diagram.ui.connection.NodeConnectionEditPart)
     * @since 4.3
     */
    @Override
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
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#getTargetAnchor(org.teiid.designer.diagram.ui.connection.NodeConnectionEditPart)
     * @since 4.3
     */
    @Override
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
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#hasSourceAnchors()
     * @since 4.3
     */
    @Override
	public boolean hasSourceAnchors() {
        return true;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#hasTargetAnchors()
     * @since 4.3
     */
    @Override
	public boolean hasTargetAnchors() {
        return true;
    }

    /** 
     * @see org.teiid.designer.diagram.ui.connection.AnchorManager#setAnchorPosition(org.teiid.designer.diagram.ui.connection.NodeConnectionAnchor, int)
     * @since 4.3
     */
    @Override
	public void setAnchorPosition(NodeConnectionAnchor theAnchor,
                                  int direction) {
    }

}
