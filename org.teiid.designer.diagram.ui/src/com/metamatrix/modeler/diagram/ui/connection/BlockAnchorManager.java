/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;

/**
 * AnchorManager
 */
public class BlockAnchorManager implements AnchorManager {

    public static final int QA = 0;
    public static final int QB = 1;
    public static final int QC = 2;
    public static final int QD = 3;
    public static final int QE = 4;
    public static final int QF = 5;
    public static final int QG = 6;
    public static final int QH = 7;
    public static final int QZ = 8;
    public static final int C1 = 9;
    public static final int C2 = 10;
    public static final int C3 = 11;
    public static final int C4 = 12;
    public static final int C5 = 13;
    public static final int C6 = 14;
    public static final int C7 = 15;
    public static final int C8 = 16;
    public static final int C9 = 17;
    public static final int C10 = 18;
    public static final int C11 = 19;
    public static final int C12 = 20;
    public static final int C13 = 21;
    public static final int C14 = 22;
    public static final int C15 = 23;
    public static final int C16 = 24;
    public static final int C17 = 25;
    public static final int C18 = 26;
    public static final int C19 = 27;
    public static final int C20 = 28;
    public static final int C21 = 29;
    public static final int C22 = 30;
    public static final int C23 = 31;
    public static final int C24 = 32;

    private List northAnchors;
    private List southAnchors;
    private List eastAnchors;
    private List westAnchors;

    // private NodeConnectionAnchor sourceAnchor;
    // private NodeConnectionAnchor targetAnchor;

    private DiagramEditPart diagramEditPart;

    /**
     * Construct an instance of AnchorManager.
     */
    public BlockAnchorManager( DiagramEditPart diagramEditPart ) {
        this.diagramEditPart = diagramEditPart;
        init();
    }

    private void init() {

    }

    /**
     * @return
     */
    private List getEastAnchors() {
        return eastAnchors;
    }

    /**
     * @return
     */
    private List getNorthAnchors() {
        return northAnchors;
    }

    /**
     * @return
     */
    private List getSouthAnchors() {
        return southAnchors;
    }

    /**
     * @return
     */
    private List getWestAnchors() {
        return westAnchors;
    }

    /**
     * @param newAnchor
     */
    private boolean addEastAnchor( NodeConnectionAnchor newAnchor ) {
        if (eastAnchors == null) eastAnchors = new ArrayList(1);

        if (!eastAnchors.contains(newAnchor)) {
            eastAnchors.add(newAnchor);
            return true;
        }

        return false;
    }

    /**
     * @param newAnchor
     */
    private boolean addNorthAnchor( NodeConnectionAnchor newAnchor ) {
        if (northAnchors == null) northAnchors = new ArrayList(1);

        if (!northAnchors.contains(newAnchor)) {
            northAnchors.add(newAnchor);
            return true;
        }

        return false;
    }

    /**
     * @param newAnchor
     */
    private boolean addSouthAnchor( NodeConnectionAnchor newAnchor ) {
        if (southAnchors == null) southAnchors = new ArrayList(1);

        if (!southAnchors.contains(newAnchor)) {
            southAnchors.add(newAnchor);
            return true;
        }

        return false;

    }

    /**
     * @param newAnchor
     */
    private boolean addWestAnchor( NodeConnectionAnchor newAnchor ) {
        if (westAnchors == null) westAnchors = new ArrayList(1);

        if (!westAnchors.contains(newAnchor)) {
            westAnchors.add(newAnchor);
            return true;
        }

        return false;
    }

    public boolean add( NodeConnectionAnchor targetAnchor ) {
        boolean added = false;

        if (targetAnchor.getDirection() == NORTH) {
            added = addNorthAnchor(targetAnchor);
        } else if (targetAnchor.getDirection() == SOUTH) {
            added = addSouthAnchor(targetAnchor);
        } else if (targetAnchor.getDirection() == EAST) {
            added = addEastAnchor(targetAnchor);
        } else if (targetAnchor.getDirection() == WEST) {
            added = addWestAnchor(targetAnchor);
        }

        return added;
    }

    public boolean move( NodeConnectionAnchor targetAnchor ) {
        // Need to move the target anchor from one anchor list to another.

        // remove from any list.
        boolean removed = remove(targetAnchor);

        // Now add to direction's list
        boolean added = add(targetAnchor);

        if (removed && added) return true;

        return false;
    }

    public boolean remove( NodeConnectionAnchor targetAnchor ) {
        boolean removed = false;
        if (targetAnchor.getDirection() != NORTH && getNorthAnchors() != null && getNorthAnchors().contains(targetAnchor)) {
            getNorthAnchors().remove(targetAnchor);
            removed = true;
        }

        if (targetAnchor.getDirection() != SOUTH && getSouthAnchors() != null && getSouthAnchors().contains(targetAnchor)) {
            getSouthAnchors().remove(targetAnchor);
            removed = true;
        }

        if (targetAnchor.getDirection() != EAST && getEastAnchors() != null && getEastAnchors().contains(targetAnchor)) {
            getEastAnchors().remove(targetAnchor);
            removed = true;
        }

        if (targetAnchor.getDirection() != WEST && getWestAnchors() != null && getWestAnchors().contains(targetAnchor)) {
            getWestAnchors().remove(targetAnchor);
            removed = true;
        }

        return removed;
    }

    public void resetSourceAnchors( boolean updateTargetEnd ) {
        List sConnections = getSourceConnections();
        if (sConnections.isEmpty()) return;

        NodeConnectionEditPart nextConnection = null;
        Iterator iter = sConnections.iterator();
        DiagramEditPart targetEditPart = null;
        NodeConnectionAnchor sourceAnchor = null;

        while (iter.hasNext()) {
            nextConnection = (NodeConnectionEditPart)iter.next();
            sourceAnchor = (NodeConnectionAnchor)nextConnection.getSourceAnchor();

            targetEditPart = (DiagramEditPart)nextConnection.getTarget();

            // There may be cases where the connection object isn't complete when this
            // method is called, so we just skip this connection in the hope that
            // the create of the other side ( connection.Target ) takes care of updating
            // this link's anchor.
            if (targetEditPart == null) continue;

            int gridId = getSourceGridLocation(diagramEditPart, targetEditPart);

            if (gridId == QA || gridId == QB || gridId == QC) {
                setAnchorPosition(sourceAnchor, SOUTH);
            } else if (gridId == QG || gridId == QF || gridId == QE) {
                setAnchorPosition(sourceAnchor, NORTH);
            } else if (gridId == QH) {
                setAnchorPosition(sourceAnchor, WEST);
            } else {
                if (gridId == QZ || gridId == QD) {
                    setAnchorPosition(sourceAnchor, EAST);
                } else if (gridId == C1 || gridId == C2 || gridId == C3 || gridId == C4 || gridId == C9 || gridId == C21
                           || gridId == C22 || gridId == C23 || gridId == C24) {
                    setAnchorPosition(sourceAnchor, EAST);
                } else if (gridId == C5 || gridId == C6 || gridId == C7 || gridId == C10 || gridId == C15 || gridId == C16
                           || gridId == C20) {
                    setAnchorPosition(sourceAnchor, NORTH);
                } else if (gridId == C8 || gridId == C18) {
                    setAnchorPosition(sourceAnchor, SOUTH);
                } else {
                    setAnchorPosition(sourceAnchor, WEST);
                }
            }

            // Update Target side anchors
            if (updateTargetEnd) targetEditPart.createOrUpdateAnchorsLocations(false);
        }
    }

    public void resetTargetAnchors( boolean updateSourceEnd ) {
        List tConnections = getTargetConnections();
        if (tConnections.isEmpty()) return;

        NodeConnectionEditPart nextConnection = null;
        Iterator iter = tConnections.iterator();
        DiagramEditPart sourceEditPart = null;
        NodeConnectionAnchor targetAnchor = null;

        while (iter.hasNext()) {
            nextConnection = (NodeConnectionEditPart)iter.next();
            targetAnchor = (NodeConnectionAnchor)nextConnection.getTargetAnchor();

            sourceEditPart = (DiagramEditPart)nextConnection.getSource();

            // There may be cases where the connection object isn't complete when this
            // method is called, so we just skip this connection in the hope that
            // the create of the other side ( connection.Source ) takes care of updating
            // this link's anchor.
            if (sourceEditPart == null) continue;

            int gridId = getSourceGridLocation(sourceEditPart, diagramEditPart);

            if (gridId == QA || gridId == QH || gridId == QG) {
                setAnchorPosition(targetAnchor, EAST);
            } else if (gridId == QC || gridId == QD || gridId == QE) {
                setAnchorPosition(targetAnchor, WEST);
            } else if (gridId == QB) {
                setAnchorPosition(targetAnchor, NORTH);
            } else {
                if (gridId == QZ || gridId == QF) {
                    setAnchorPosition(targetAnchor, SOUTH);
                } else if (gridId == C1 || gridId == C2 || gridId == C4 || gridId == C9 || gridId == C11 || gridId == C12
                           || gridId == C14 || gridId == C17 || gridId == C19 || gridId == C21 || gridId == C22 || gridId == C24) {
                    setAnchorPosition(targetAnchor, SOUTH);
                } else if (gridId == C3 || gridId == C13 || gridId == C23) {
                    setAnchorPosition(targetAnchor, NORTH);
                } else if (gridId == C5 || gridId == C6 || gridId == C7 || gridId == C8 || gridId == C10) {
                    setAnchorPosition(targetAnchor, WEST);
                } else {
                    setAnchorPosition(targetAnchor, EAST);
                }
            }

            // Update Source side anchors
            if (updateSourceEnd) sourceEditPart.createOrUpdateAnchorsLocations(false);
        }

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
    public ConnectionAnchor getSourceAnchor( NodeConnectionEditPart connection ) {
        // This anchor manager belongs to the edit part.
        // This edit part knows about all it's target connections
        // An anchor is either target or source
        // 

        if (connection.getSourceAnchor() == null) {
            // Create a anchor for it.
            NodeConnectionAnchor newAnchor = createSourceAnchor();
            connection.setSourceAnchor(newAnchor);
            return newAnchor;
        }
        return connection.getSourceAnchor();
    }

    /**
     * @return
     */
    public ConnectionAnchor getTargetAnchor( NodeConnectionEditPart connection ) {
        // This anchor manager belongs to the edit part.
        // This edit part knows about all it's target connections
        // An anchor is either target or source
        // 
        if (connection.getTargetAnchor() == null) {
            // Create a anchor for it.
            NodeConnectionAnchor newAnchor = createTargetAnchor();
            connection.setTargetAnchor(newAnchor);
            return newAnchor;
        }
        return connection.getTargetAnchor();
    }

    private NodeConnectionAnchor createTargetAnchor() {
        NodeConnectionAnchor newAnchor = new NodeConnectionAnchor(diagramEditPart.getFigure(), NodeConnectionAnchor.IS_TARGET);
        // Set some default direction
        newAnchor.setDirection(NORTH);
        add(newAnchor);
        return newAnchor;
    }

    private NodeConnectionAnchor createSourceAnchor() {
        NodeConnectionAnchor newAnchor = new NodeConnectionAnchor(diagramEditPart.getFigure(), NodeConnectionAnchor.IS_SOURCE);
        // Set some default direction
        newAnchor.setDirection(NORTH);
        add(newAnchor);
        return newAnchor;
    }

    public boolean hasSourceAnchors() {
        NodeConnectionAnchor nextAnchor = null;
        Iterator iter = null;

        if (northAnchors != null && !northAnchors.isEmpty()) {
            iter = northAnchors.iterator();
            while (iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if (nextAnchor != null && nextAnchor.isSource()) return true;
            }
        }

        if (southAnchors != null && !southAnchors.isEmpty()) {
            iter = southAnchors.iterator();
            while (iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if (nextAnchor != null && nextAnchor.isSource()) return true;
            }
        }

        if (eastAnchors != null && !eastAnchors.isEmpty()) {
            iter = eastAnchors.iterator();
            while (iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if (nextAnchor != null && nextAnchor.isSource()) return true;
            }
        }

        if (westAnchors != null && !westAnchors.isEmpty()) {
            iter = westAnchors.iterator();
            while (iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if (nextAnchor != null && nextAnchor.isSource()) return true;
            }
        }

        return false;
    }

    public boolean hasTargetAnchors() {
        NodeConnectionAnchor nextAnchor = null;
        Iterator iter = null;

        if (northAnchors != null && !northAnchors.isEmpty()) {
            iter = northAnchors.iterator();
            while (iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if (nextAnchor != null && !nextAnchor.isSource()) return true;
            }
        }

        if (southAnchors != null && !southAnchors.isEmpty()) {
            iter = southAnchors.iterator();
            while (iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if (nextAnchor != null && !nextAnchor.isSource()) return true;
            }
        }

        if (eastAnchors != null && !eastAnchors.isEmpty()) {
            iter = eastAnchors.iterator();
            while (iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if (nextAnchor != null && !nextAnchor.isSource()) return true;
            }
        }

        if (westAnchors != null && !westAnchors.isEmpty()) {
            iter = westAnchors.iterator();
            while (iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if (nextAnchor != null && !nextAnchor.isSource()) return true;
            }
        }

        return false;
    }

    public void setAnchorPosition( NodeConnectionAnchor theAnchor,
                                   int direction,
                                   List fellowAnchors ) {

        Dimension partSize = ((DiagramModelNode)diagramEditPart.getModel()).getSize();

        int oldDirection = theAnchor.getDirection();

        theAnchor.setDirection(direction);

        boolean moved = move(theAnchor);

        if (moved) {
            setAnchorPositions(oldDirection, direction);
        }

        if (direction == NORTH) {
            if (fellowAnchors.size() == 1) {
                theAnchor.setOffsetH(partSize.width / 2);
                theAnchor.setOffsetV(0);
            } else {
                int nAnchors = fellowAnchors.size();
                int anchorId = getAnchorListId(fellowAnchors, theAnchor) + 1;
                int hOffsetIncrement = partSize.width / (nAnchors + 1);
                theAnchor.setOffsetH(hOffsetIncrement * anchorId);
                theAnchor.setOffsetV(0);
            }
        } else if (direction == SOUTH) {
            if (fellowAnchors.size() == 1) {
                theAnchor.setOffsetH(partSize.width / 2);
                theAnchor.setOffsetV(partSize.height);
            } else {
                int nAnchors = fellowAnchors.size();
                int anchorId = getAnchorListId(fellowAnchors, theAnchor) + 1;
                int hOffsetIncrement = partSize.width / (nAnchors + 1);
                theAnchor.setOffsetH(hOffsetIncrement * anchorId);
                theAnchor.setOffsetV(partSize.height);
            }
        } else if (direction == WEST) {
            if (fellowAnchors.size() == 1) {
                theAnchor.setOffsetH(0);
                theAnchor.setOffsetV(partSize.height / 2);
            } else {
                int nAnchors = fellowAnchors.size();
                int anchorId = getAnchorListId(fellowAnchors, theAnchor) + 1;
                int vOffsetIncrement = partSize.height / (nAnchors + 1);
                theAnchor.setOffsetV(vOffsetIncrement * anchorId);
                theAnchor.setOffsetH(0);
            }
        } else {
            if (fellowAnchors.size() == 1) {
                theAnchor.setOffsetH(partSize.width);
                theAnchor.setOffsetV(partSize.height / 2);
            } else {
                int nAnchors = fellowAnchors.size();
                int anchorId = getAnchorListId(fellowAnchors, theAnchor) + 1;
                int vOffsetIncrement = partSize.height / (nAnchors + 1);
                theAnchor.setOffsetV(vOffsetIncrement * anchorId);
                theAnchor.setOffsetH(partSize.width);
            }
        }

    }

    public void setAnchorPosition( NodeConnectionAnchor theAnchor,
                                   int direction ) {

        Dimension partSize = ((DiagramModelNode)diagramEditPart.getModel()).getSize();

        int oldDirection = theAnchor.getDirection();

        theAnchor.setDirection(direction);

        boolean moved = move(theAnchor);

        if (moved) {
            setAnchorPositions(oldDirection, direction);
        } else if (direction == NORTH) {
            if (getNorthAnchors().size() == 1) {
                theAnchor.setOffsetH(partSize.width / 2);
                theAnchor.setOffsetV(0);
            } else {
                int nAnchors = getNorthAnchors().size();
                int anchorId = getAnchorListId(getNorthAnchors(), theAnchor) + 1;
                int hOffsetIncrement = partSize.width / (nAnchors + 1);
                theAnchor.setOffsetH(hOffsetIncrement * anchorId);
                theAnchor.setOffsetV(0);
            }
        } else if (direction == SOUTH) {
            if (getSouthAnchors().size() == 1) {
                theAnchor.setOffsetH(partSize.width / 2);
                theAnchor.setOffsetV(partSize.height);
            } else {
                int nAnchors = getSouthAnchors().size();
                int anchorId = getAnchorListId(getSouthAnchors(), theAnchor) + 1;
                int hOffsetIncrement = partSize.width / (nAnchors + 1);
                theAnchor.setOffsetH(hOffsetIncrement * anchorId);
                theAnchor.setOffsetV(partSize.height);
            }
        } else if (direction == WEST) {
            if (getWestAnchors().size() == 1) {
                theAnchor.setOffsetH(0);
                theAnchor.setOffsetV(partSize.height / 2);
            } else {
                int nAnchors = getWestAnchors().size();
                int anchorId = getAnchorListId(getWestAnchors(), theAnchor) + 1;
                int vOffsetIncrement = partSize.height / (nAnchors + 1);
                theAnchor.setOffsetV(vOffsetIncrement * anchorId);
                theAnchor.setOffsetH(0);
            }
        } else {
            if (getEastAnchors().size() == 1) {
                theAnchor.setOffsetH(partSize.width);
                theAnchor.setOffsetV(partSize.height / 2);
            } else {
                int nAnchors = getEastAnchors().size();
                int anchorId = getAnchorListId(getEastAnchors(), theAnchor) + 1;
                int vOffsetIncrement = partSize.height / (nAnchors + 1);
                theAnchor.setOffsetV(vOffsetIncrement * anchorId);
                theAnchor.setOffsetH(partSize.width);
            }
        }

    }

    private void reorderAnchors( List anchors,
                                 int direction ) {
        if (anchors != null && !anchors.isEmpty()) {
            // Order from Top to bottom
            for (Iterator iter = anchors.iterator(); iter.hasNext();) {
                setAnchorPosition((NodeConnectionAnchor)iter.next(), direction, anchors);
            }
        }
    }

    public void reorderAllAnchors( boolean updateBothEnds ) {
        // Let's get all source and target connections
        List tConnections = getTargetConnections();
        List sConnections = getSourceConnections();
        if (tConnections.isEmpty() && sConnections.isEmpty()) return;

        AnchorListManager eastAnchors = new AnchorListManager(EAST);
        AnchorListManager westAnchors = new AnchorListManager(WEST);
        AnchorListManager northAnchors = new AnchorListManager(NORTH);
        AnchorListManager southAnchors = new AnchorListManager(SOUTH);

        HashMap otherEndParts = new HashMap();

        NodeConnectionEditPart nextConnection = null;
        DiagramEditPart sourceEditPart = null;
        NodeConnectionAnchor targetAnchor = null;

        // DO TARGET CONNECTIONS
        Iterator iter = tConnections.iterator();
        while (iter.hasNext()) {
            nextConnection = (NodeConnectionEditPart)iter.next();
            targetAnchor = (NodeConnectionAnchor)nextConnection.getTargetAnchor();

            sourceEditPart = (DiagramEditPart)nextConnection.getSource();
            if (updateBothEnds && otherEndParts.get(sourceEditPart) == null) {
                otherEndParts.put(sourceEditPart, "z"); //$NON-NLS-1$
            }

            // There may be cases where the connection object isn't complete when this
            // method is called, so we just skip this connection in the hope that
            // the create of the other side ( connection.Source ) takes care of updating
            // this link's anchor.
            if (sourceEditPart == null) continue;

            int gridId = getSourceGridLocation(sourceEditPart, diagramEditPart);

            if (gridId == QA || gridId == QH || gridId == QG) {
                eastAnchors.addAnchor(sourceEditPart, targetAnchor);
            } else if (gridId == QC || gridId == QD || gridId == QE) {
                westAnchors.addAnchor(sourceEditPart, targetAnchor);
            } else if (gridId == QB) {
                northAnchors.addAnchor(sourceEditPart, targetAnchor);
            } else {
                if (gridId == QZ || gridId == QF) {
                    southAnchors.addAnchor(sourceEditPart, targetAnchor);
                } else if (gridId == C1 || gridId == C2 || gridId == C4 || gridId == C9 || gridId == C11 || gridId == C12
                           || gridId == C14 || gridId == C17 || gridId == C19 || gridId == C21 || gridId == C22 || gridId == C24) {
                    southAnchors.addAnchor(sourceEditPart, targetAnchor);
                } else if (gridId == C3 || gridId == C13 || gridId == C23) {
                    northAnchors.addAnchor(sourceEditPart, targetAnchor);
                } else if (gridId == C5 || gridId == C6 || gridId == C7 || gridId == C8 || gridId == C10) {
                    westAnchors.addAnchor(sourceEditPart, targetAnchor);
                } else {
                    eastAnchors.addAnchor(sourceEditPart, targetAnchor);
                }
            }
        }

        DiagramEditPart targetEditPart = null;
        NodeConnectionAnchor sourceAnchor = null;
        // DO SOURCE CONNECTIONS
        iter = sConnections.iterator();
        while (iter.hasNext()) {
            nextConnection = (NodeConnectionEditPart)iter.next();
            sourceAnchor = (NodeConnectionAnchor)nextConnection.getSourceAnchor();

            targetEditPart = (DiagramEditPart)nextConnection.getTarget();
            if (updateBothEnds && otherEndParts.get(targetEditPart) == null) {
                otherEndParts.put(targetEditPart, "z"); //$NON-NLS-1$
            }

            // There may be cases where the connection object isn't complete when this
            // method is called, so we just skip this connection in the hope that
            // the create of the other side ( connection.Target ) takes care of updating
            // this link's anchor.
            if (targetEditPart == null) continue;

            int gridId = getSourceGridLocation(targetEditPart, diagramEditPart); // diagramEditPart, targetEditPart);

            if (gridId == QA || gridId == QH || gridId == QG) {
                eastAnchors.addAnchor(targetEditPart, sourceAnchor);
            } else if (gridId == QC || gridId == QD || gridId == QE) {
                westAnchors.addAnchor(targetEditPart, sourceAnchor);
            } else if (gridId == QB) {
                northAnchors.addAnchor(targetEditPart, sourceAnchor);
            } else {
                if (gridId == QZ || gridId == QF) {
                    southAnchors.addAnchor(targetEditPart, sourceAnchor);
                } else if (gridId == C1 || gridId == C2 || gridId == C4 || gridId == C9 || gridId == C11 || gridId == C12
                           || gridId == C14 || gridId == C17 || gridId == C19 || gridId == C21 || gridId == C22 || gridId == C24) {
                    southAnchors.addAnchor(targetEditPart, sourceAnchor);
                } else if (gridId == C3 || gridId == C13 || gridId == C23) {
                    northAnchors.addAnchor(targetEditPart, sourceAnchor);
                } else if (gridId == C5 || gridId == C6 || gridId == C7 || gridId == C8 || gridId == C10) {
                    westAnchors.addAnchor(targetEditPart, sourceAnchor);
                } else {
                    eastAnchors.addAnchor(targetEditPart, sourceAnchor);
                }
            }
        }

        // Now we need to reorder the anchors based on position of target edit parts
        reorderAnchors(eastAnchors.getOrderedAnchors(), EAST);
        reorderAnchors(westAnchors.getOrderedAnchors(), WEST);
        reorderAnchors(northAnchors.getOrderedAnchors(), NORTH);
        reorderAnchors(southAnchors.getOrderedAnchors(), SOUTH);
        // Update other ends if requrested
        if (!otherEndParts.isEmpty()) {
            for (Iterator iterEP = otherEndParts.keySet().iterator(); iterEP.hasNext();) {
                targetEditPart = (DiagramEditPart)iterEP.next();
                if (targetEditPart != null) {
                    targetEditPart.createOrUpdateAnchorsLocations(false);
                }

            }
        }

    }

    /**
     * This method for setting anchor positions assumes that we want to walk through all anchor positions to insure proper
     * spacing..... should be called whenever an anchor is "moved" and there are more than to or from.
     * 
     * @param theAnchor
     * @param direction
     */
    private void setAnchorPositions( int oldDirection,
                                     int newDirection ) {
        setAnchorPositions(oldDirection);
        setAnchorPositions(newDirection);
    }

    private void setAnchorPositions( int direction ) {

        Dimension partSize = ((DiagramModelNode)diagramEditPart.getModel()).getSize();
        Iterator iter = null;

        if (direction == NORTH && getNorthAnchors() != null && !getNorthAnchors().isEmpty()) {
            // Walk through the anchors
            int nAnchors = getNorthAnchors().size();
            int hOffsetIncrement = partSize.width / (nAnchors + 1);
            iter = getNorthAnchors().iterator();
            int aCount = 0;
            NodeConnectionAnchor theAnchor = null;
            while (iter.hasNext()) {
                theAnchor = (NodeConnectionAnchor)iter.next();
                theAnchor.setOffsetH(hOffsetIncrement * (aCount + 1));
                theAnchor.setOffsetV(0);
                aCount++;
            }
        } else if (direction == SOUTH && getSouthAnchors() != null && !getSouthAnchors().isEmpty()) {
            // Walk through the anchors
            int nAnchors = getSouthAnchors().size();
            int hOffsetIncrement = partSize.width / (nAnchors + 1);
            iter = getSouthAnchors().iterator();
            int aCount = 0;
            NodeConnectionAnchor theAnchor = null;
            while (iter.hasNext()) {
                theAnchor = (NodeConnectionAnchor)iter.next();
                theAnchor.setOffsetH(hOffsetIncrement * (aCount + 1));
                theAnchor.setOffsetV(partSize.height);
                aCount++;
            }
        } else if (direction == WEST && getWestAnchors() != null && !getWestAnchors().isEmpty()) {
            // Walk through the anchors
            int nAnchors = getWestAnchors().size();
            int vOffsetIncrement = partSize.height / (nAnchors + 1);
            iter = getWestAnchors().iterator();
            int aCount = 0;
            NodeConnectionAnchor theAnchor = null;
            while (iter.hasNext()) {
                theAnchor = (NodeConnectionAnchor)iter.next();
                theAnchor.setOffsetV(vOffsetIncrement * (aCount + 1));
                theAnchor.setOffsetH(0);
                aCount++;
            }

        } else if (getEastAnchors() != null && !getEastAnchors().isEmpty()) {
            // Walk through the anchors
            int nAnchors = getEastAnchors().size();
            int vOffsetIncrement = partSize.height / (nAnchors + 1);
            iter = getEastAnchors().iterator();
            int aCount = 0;
            NodeConnectionAnchor theAnchor = null;
            while (iter.hasNext()) {
                theAnchor = (NodeConnectionAnchor)iter.next();
                theAnchor.setOffsetV(vOffsetIncrement * (aCount + 1));
                theAnchor.setOffsetH(partSize.width);
                aCount++;
            }
        }
    }

    private int getAnchorListId( List anchors,
                                 NodeConnectionAnchor anchor ) {
        int id = 0;
        Iterator iter = anchors.iterator();
        while (iter.hasNext()) {
            if (anchor.equals(iter.next())) {
                break;
            }
            id++;
        }
        return id;
    }

    private int getSourceGridLocation( DiagramEditPart sourcePart,
                                       DiagramEditPart targetPart ) {

        // Grid Location Summary. Assumes that the Target Part Fills QZ Grid Cell
        //
        // | |
        // QC | QB | QA
        // | |
        // --------------------------
        // | |
        // QD | QZ | QH
        // | |
        // --------------------------
        // | |
        // QE | QF | QG
        // | |
        //

        int gridId = QZ;

        int padding = 20;

        Point tMin = new Point(((DiagramModelNode)targetPart.getModel()).getPosition().x - padding,
                               ((DiagramModelNode)targetPart.getModel()).getPosition().y + padding);
        Dimension tSize = new Dimension(((DiagramModelNode)targetPart.getModel()).getSize().width + padding * 2,
                                        ((DiagramModelNode)targetPart.getModel()).getSize().height + padding * 2);

        Point sMin = new Point(((DiagramModelNode)sourcePart.getModel()).getPosition().x - padding,
                               ((DiagramModelNode)sourcePart.getModel()).getPosition().y + padding);
        Dimension sSize = new Dimension(((DiagramModelNode)sourcePart.getModel()).getSize().width + padding * 2,
                                        ((DiagramModelNode)sourcePart.getModel()).getSize().height + padding * 2);

        Point tCenter = new Point(tMin.x + tSize.width / 2, tMin.y + tSize.height / 2);
        Point tMax = new Point(tMin.x + tSize.width, tMin.y + tSize.height);
        Point sMax = new Point(sMin.x + sSize.width, sMin.y + sSize.height);

        // System.out.println(" >>>>>>>>  BAM.getSourceGridLocation()  ------");
        // System.out.println("     Position = " + ((DiagramModelNode)sourcePart.getModel()).getPosition() +
        // "  Source = " + sourcePart);
        // System.out.println("     Position = " + ((DiagramModelNode)targetPart.getModel()).getPosition() +
        // "  Target = " + targetPart);

        if (sMax.y < tMin.y) {
            if (sMin.x > tMax.x) gridId = QA;
            else if (sMax.x < tMin.x) gridId = QC;
            else gridId = QB;
        } else if (sMin.y > tMax.y) {
            if (sMin.x > tMax.x) gridId = QG;
            else if (sMax.x < tMin.x) gridId = QE;
            else gridId = QF;
        } else {
            if (sMin.x > tMax.x) gridId = QH;
            else if (sMax.x < tMin.x) gridId = QD;
            else { // These are cases where objects overlap.
                if (sMin.x > (tCenter.x)) {
                    if (sMax.y < tCenter.y) {
                        gridId = C1;
                    } else if (sMax.y > tCenter.y && sMax.y < tMax.y) {
                        if (sMin.y > tMin.y) gridId = C4;
                        else gridId = C2;
                    } else { // sMax.y >= tMax.y
                        if (sMin.y > tMin.y) gridId = C3;
                        else // sMin.y <= tMin.y
                        gridId = C5;
                    }
                } else if (sMin.x > tMin.x) {
                    if (sMax.y < tCenter.y) {
                        gridId = C6;
                    } else if (sMax.y > tCenter.y && sMax.y < tMax.y) {
                        if (sMin.y > tMin.y) gridId = C9;
                        else gridId = C7;
                    } else { // sMax.y >= tMax.y
                        if (sMin.y > tMin.y) gridId = C8;
                        else // sMin.y <= tMin.y
                        gridId = C10;
                    }
                } else { // sMin.x <= tMin.x
                    if (sMax.x < tMax.x) {
                        if (sMax.x < (tCenter.x)) {
                            if (sMax.y < tCenter.y) {
                                gridId = C11;
                            } else if (sMax.y > tCenter.y && sMax.y < tMax.y) {
                                if (sMin.y > tMin.y) gridId = C14;
                                else gridId = C12;
                            } else { // sMax.y >= tMax.y
                                if (sMin.y > tMin.y) gridId = C13;
                                else // sMin.y <= tMin.y
                                gridId = C15;
                            }
                        } else {
                            if (sMax.y < tCenter.y) {
                                gridId = C16;
                            } else if (sMax.y > tCenter.y && sMax.y < tMax.y) {
                                if (sMin.y > tMin.y) gridId = C19;
                                else gridId = C17;
                            } else { // sMax.y >= tMax.y
                                if (sMin.y > tMin.y) gridId = C18;
                                else // sMin.y <= tMin.y
                                gridId = C20;
                            }
                        }
                    } else { // sMax.x <= tMax.x
                        if (sMax.y < tCenter.y) gridId = C21;
                        else if (sMax.y < tMax.y) gridId = C22;
                        else {
                            if (sMin.y > tMin.y) gridId = C23;
                            else // sMin.y >= tMin.y
                            gridId = C24;
                        }
                    }
                }
            }
        }
        // System.out.println(" --- GRID ID = " + gridId +"------------------------------------\n");
        return gridId;
    }

    private class AnchorInfo {
        private NodeConnectionAnchor anchor;
        private int positionValue = 0;

        public AnchorInfo( NodeConnectionAnchor anchor,
                           int position ) {
            this.anchor = anchor;
            this.positionValue = position;
        }

        public NodeConnectionAnchor getAnchor() {
            return this.anchor;
        }

        public int getPositionValue() {
            return this.positionValue;
        }
    }

    private class AnchorListManager {
        private List anchorArray = new ArrayList();
        private int direction;

        public AnchorListManager( int direction ) {
            this.direction = direction;
        }

        public void addAnchor( DiagramEditPart editPart,
                               NodeConnectionAnchor anchor ) {
            Point newPosition = new Point(((DiagramModelNode)editPart.getModel()).getPosition());
            int newPositionValue = 0;
            if (direction == EAST || direction == WEST) newPositionValue = newPosition.y;
            else newPositionValue = newPosition.x;

            // Now we need to check the relative position of this anchor based on direction

            if (anchorArray.isEmpty()) anchorArray.add(new AnchorInfo(anchor, newPositionValue));
            else {
                // We have more than one in map, so now check postionValue
                int newIndex = 0;
                AnchorInfo anchorInfo = null;
                AnchorInfo nextAnchorInfo = null;
                int size = anchorArray.size();
                for (Iterator iter = anchorArray.iterator(); iter.hasNext();) {
                    anchorInfo = (AnchorInfo)iter.next();
                    if (newPositionValue >= anchorInfo.getPositionValue()) {
                        newIndex++;
                        // Now check if it's at the end or not.
                        if (newIndex >= size) {
                            break;
                        }
                        nextAnchorInfo = (AnchorInfo)anchorArray.get(newIndex);
                        if (newPositionValue <= nextAnchorInfo.getPositionValue()) {
                            // position located to insert at this index
                            break;
                        }
                    } else {
                        break;
                    }
                }

                anchorArray.add(newIndex, new AnchorInfo(anchor, newPositionValue));
            }
        }

        public List getOrderedAnchors() {
            List anchorList = new ArrayList(anchorArray.size());
            AnchorInfo anchorInfo = null;
            for (Iterator iter = anchorArray.iterator(); iter.hasNext();) {
                anchorInfo = (AnchorInfo)iter.next();
                anchorList.add(anchorInfo.getAnchor());
            }

            return anchorList;
        }
    }
}
