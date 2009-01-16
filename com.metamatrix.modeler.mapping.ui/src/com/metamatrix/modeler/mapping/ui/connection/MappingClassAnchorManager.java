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

package com.metamatrix.modeler.mapping.ui.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionAnchor;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.mapping.ui.DebugConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.model.MappingExtentNode;
import com.metamatrix.modeler.mapping.ui.part.MappingDiagramEditPart;
import com.metamatrix.modeler.mapping.ui.part.MappingExtentEditPart;

/**
 * AnchorManager
 */
public class MappingClassAnchorManager implements AnchorManager {
    private List eastAnchors;
    private List westAnchors;
    private List southAnchors;

    private DiagramEditPart diagramEditPart;

    /**
     * Construct an instance of AnchorManager.
     */
    public MappingClassAnchorManager( DiagramEditPart diagramEditPart ) {
        this.diagramEditPart = diagramEditPart;
        init();
    }

    private void init() {
    }

    private boolean isCoarse() {
        MappingDiagramEditPart diagramEP = (MappingDiagramEditPart)diagramEditPart.getParent();
        return diagramEP.isCoarseMapping();
    }

    private boolean isPrimary() {
        MappingDiagramEditPart diagramEP = (MappingDiagramEditPart)diagramEditPart.getParent();
        return diagramEP.isPrimary(diagramEditPart);
    }

    private boolean isInputSet() {
        if (!isCoarse()) {
            EObject eObj = diagramEditPart.getModelObject();
            if (TransformationHelper.isSqlInputSet(eObj)) {
                return true;
            }
        }
        return false;
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
    private List getWestAnchors() {
        return westAnchors;
    }

    /**
     * @return
     */
    private List getSouthAnchors() {
        return southAnchors;
    }

    /**
     * @param newAnchor
     */
    private boolean addEastAnchor( NodeConnectionAnchor newAnchor ) {
        if (eastAnchors == null) eastAnchors = new ArrayList(1);

        if (!containsEquivalentAnchor(eastAnchors, newAnchor)) {
            eastAnchors.add(newAnchor);
            return true;
        }

        return false;
    }

    /**
     * @param newAnchor
     */
    private boolean addWestAnchor( NodeConnectionAnchor newAnchor ) {
        if (westAnchors == null) westAnchors = new ArrayList(1);

        if (!containsEquivalentAnchor(westAnchors, newAnchor)) {
            westAnchors.add(newAnchor);
            return true;
        }

        return false;
    }

    /**
     * @param newAnchor
     */
    private boolean addSouthAnchor( NodeConnectionAnchor newAnchor ) {
        if (southAnchors == null) southAnchors = new ArrayList(1);

        if (!containsEquivalentAnchor(southAnchors, newAnchor)) {
            southAnchors.add(newAnchor);
            return true;
        }

        return false;
    }

    private boolean containsEquivalentAnchor( List list,
                                              NodeConnectionAnchor newAnchor ) {
        if (eastAnchors == null) eastAnchors = new ArrayList(1);

        Iterator it = list.iterator();

        while (it.hasNext()) {
            NodeConnectionAnchor ncaTemp = (NodeConnectionAnchor)it.next();

            if (ncaTemp.equals(newAnchor)) {
                return true;
            }
        }

        return false;
    }

    public boolean add( NodeConnectionAnchor targetAnchor ) {
        boolean added = false;

        if (targetAnchor.getDirection() == EAST) {
            added = addEastAnchor(targetAnchor);
        } else if (targetAnchor.getDirection() == WEST) {
            added = addWestAnchor(targetAnchor);
        } else if (targetAnchor.getDirection() == SOUTH) {
            added = addSouthAnchor(targetAnchor);
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

        if (targetAnchor.getDirection() != EAST && getEastAnchors() != null && getEastAnchors().contains(targetAnchor)) {
            getEastAnchors().remove(targetAnchor);
            removed = true;
        }

        if (targetAnchor.getDirection() != WEST && getWestAnchors() != null && getWestAnchors().contains(targetAnchor)) {
            getWestAnchors().remove(targetAnchor);
            removed = true;
        }

        if (targetAnchor.getDirection() != SOUTH && getSouthAnchors() != null && getSouthAnchors().contains(targetAnchor)) {
            getSouthAnchors().remove(targetAnchor);
            removed = true;
        }

        return removed;
    }

    public void resetSourceAnchors( boolean updateTargetEnd ) {
        cleanUpAnchors();

        List sConnections = getSourceConnections();
        if (sConnections.isEmpty()) {
            return;
        }

        NodeConnectionEditPart nextConnection = null;
        Iterator iter = sConnections.iterator();
        DiagramEditPart targetEditPart = null;
        NodeConnectionAnchor sourceAnchor = null;

        while (iter.hasNext()) {
            nextConnection = (NodeConnectionEditPart)iter.next();
            sourceAnchor = (NodeConnectionAnchor)nextConnection.getSourceAnchor();
            targetEditPart = (DiagramEditPart)nextConnection.getTarget();

            if (targetEditPart != null) {
                setSourceAnchorPosition(sourceAnchor, targetEditPart);
                nextConnection.refresh();
                // Update Target side anchors
                if (updateTargetEnd) targetEditPart.createOrUpdateAnchorsLocations(false);
            }

        }
    }

    public void resetTargetAnchors( boolean updateSourceEnd ) {
        cleanUpAnchors();

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

            if (sourceEditPart != null) {
                setAnchorPosition(targetAnchor, EAST);
                nextConnection.refresh();
                // Update Source side anchors
                if (updateSourceEnd) sourceEditPart.createOrUpdateAnchorsLocations(false);
            }
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
        newAnchor.setDirection(EAST);
        add(newAnchor);
        return newAnchor;
    }

    private NodeConnectionAnchor createSourceAnchor() {
        NodeConnectionAnchor newAnchor = new NodeConnectionAnchor(diagramEditPart.getFigure(), NodeConnectionAnchor.IS_SOURCE);
        // Set some default direction
        newAnchor.setDirection(WEST);
        add(newAnchor);
        return newAnchor;
    }

    public boolean hasSourceAnchors() {
        NodeConnectionAnchor nextAnchor = null;
        Iterator iter = null;

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

        if (southAnchors != null && !southAnchors.isEmpty()) {
            iter = southAnchors.iterator();
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

        if (southAnchors != null && !southAnchors.isEmpty()) {
            iter = southAnchors.iterator();
            while (iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if (nextAnchor != null && !nextAnchor.isSource()) return true;
            }
        }

        return false;
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

    private int getAttributeYMidpoint( int id ) {
        int yPosition = 0;
        UmlClassifierNode classifierNode = (UmlClassifierNode)diagramEditPart.getModel();
        if (classifierNode != null && classifierNode.getAttributesContainer() != null) {
            int containerY = classifierNode.getAttributesContainer().getY();
            // We need to get the Children of this edit part, and get the Container??
            List attributes = classifierNode.getAttributes();
            if (id < attributes.size()) {
                yPosition = ((DiagramModelNode)attributes.get(id)).getCenterY() + containerY;
                if (UiConstants.Util.isDebugEnabled(DebugConstants.MAPPING_DIAGRAM_CONNECTIONS)) {
                    UiConstants.Util.print(DebugConstants.MAPPING_DIAGRAM_CONNECTIONS,
                                           "Found Y for Attribute ID = " + id + "  Y = " + yPosition); //$NON-NLS-2$ //$NON-NLS-1$
                }
            }
        }
        return yPosition;
    }

    private int getAttributeYMidpoint( EObject attributeEObject ) {
        int yPosition = 0;

        UmlClassifierNode classifierNode = (UmlClassifierNode)diagramEditPart.getModel();
        if (classifierNode != null && classifierNode.getAttributesContainer() != null) {
            int containerY = classifierNode.getAttributesContainer().getY();

            DiagramModelNode attributeNode = getAttributeNode(attributeEObject);

            if (attributeNode != null) {
                yPosition = attributeNode.getCenterY() + containerY;
                if (UiConstants.Util.isDebugEnabled(DebugConstants.MAPPING_DIAGRAM_CONNECTIONS)) {
                    UiConstants.Util.print(DebugConstants.MAPPING_DIAGRAM_CONNECTIONS,
                                           "Found Y for Attribute = " + attributeNode + "  Y = " + yPosition); //$NON-NLS-2$ //$NON-NLS-1$
                }
            }
        }

        return yPosition;
    }

    private DiagramModelNode getAttributeNode( EObject attributeEObject ) {
        UmlClassifierNode classifierNode = (UmlClassifierNode)diagramEditPart.getModel();
        if (classifierNode != null && classifierNode.getAttributesContainer() != null) {
            Iterator iter = classifierNode.getAttributes().iterator();
            DiagramModelNode nextNode = null;
            while (iter.hasNext()) {
                nextNode = (DiagramModelNode)iter.next();
                if (nextNode.getModelObject() != null && nextNode.getModelObject() == attributeEObject) // nextNode.getModelObject().equals(attributeEObject))
                return nextNode;
            }
        }
        return null;
    }

    public void setAnchorPosition( NodeConnectionAnchor theAnchor,
                                   int direction ) {
        if (diagramEditPart.getModel() != null) {
            Dimension partSize = ((DiagramModelNode)diagramEditPart.getModel()).getSize();

            int oldDirection = theAnchor.getDirection();

            theAnchor.setDirection(direction);

            boolean moved = move(theAnchor);

            if (moved) {
                setAnchorPositions(oldDirection, direction);
            } else if (direction == WEST) {

                if (isCoarse()) {

                } else {
                    if (isPrimary()) {
                        // This is where we make sure that the "WEST" anchor y locations, are matched up with the
                        // corresponding attribute locations
                        // there should be one anchor for each attribute.
                        // Walk through the anchors
                        int anchorId = getAnchorListId(getWestAnchors(), theAnchor) + 1;
                        int vOffset = getAttributeYMidpoint(anchorId - 1); // partSize.height/(nAnchors+1);
                        theAnchor.setOffsetV(vOffset);
                        theAnchor.setOffsetH(0);
                    } else {
                        theAnchor.setOffsetH(0);
                        theAnchor.setOffsetV(partSize.height / 2);
                    }
                }
            } else {
                theAnchor.setOffsetH(partSize.width);
                theAnchor.setOffsetV(partSize.height / 2);
            }
        }
    }

    public void setSourceAnchorPosition( NodeConnectionAnchor theAnchor,
                                         DiagramEditPart targetEditPart ) {
        if (diagramEditPart.getModel() != null) {
            Dimension partSize = ((DiagramModelNode)diagramEditPart.getModel()).getSize();

            theAnchor.setDirection(WEST);

            if (isCoarse()) {

            } else {
                if (isPrimary() && targetEditPart instanceof MappingExtentEditPart) {
                    EObject targetEObject = ((MappingExtentNode)targetEditPart.getModel()).getExtent().getMappingReference();
                    // This is where we make sure that the "WEST" anchor y locations, are matched up with the
                    // corresponding attribute locations
                    // there should be one anchor for each attribute.
                    // Walk through the anchors
                    int vOffset = getAttributeYMidpoint(targetEObject); // partSize.height/(nAnchors+1);
                    theAnchor.setOffsetV(vOffset);
                    theAnchor.setOffsetH(0);
                } else if (isInputSet()) {
                    theAnchor.setDirection(SOUTH);
                    theAnchor.setOffsetH(partSize.width / 2);
                    theAnchor.setOffsetV(partSize.height);
                } else {
                    theAnchor.setOffsetH(0);
                    theAnchor.setOffsetV(partSize.height / 2);
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
        if (diagramEditPart.getModel() != null) {
            Dimension partSize = ((DiagramModelNode)diagramEditPart.getModel()).getSize();
            Iterator iter = null;

            if (direction == WEST && getWestAnchors() != null && !getWestAnchors().isEmpty()) {
                // Walk through the anchors
                iter = getWestAnchors().iterator();
                NodeConnectionAnchor theAnchor = null;
                while (iter.hasNext()) {
                    theAnchor = (NodeConnectionAnchor)iter.next();
                    theAnchor.setOffsetH(0);
                    theAnchor.setOffsetV(0);
                }

            } else if (getEastAnchors() != null && !getEastAnchors().isEmpty()) {
                // Walk through the anchors
                iter = getEastAnchors().iterator();
                NodeConnectionAnchor theAnchor = null;
                while (iter.hasNext()) {
                    theAnchor = (NodeConnectionAnchor)iter.next();
                    theAnchor.setOffsetH(partSize.width);
                    theAnchor.setOffsetV(partSize.height / 2);
                }
            } else if (getSouthAnchors() != null && !getSouthAnchors().isEmpty()) {
                // Walk through the anchors
                iter = getSouthAnchors().iterator();
                NodeConnectionAnchor theAnchor = null;
                while (iter.hasNext()) {
                    theAnchor = (NodeConnectionAnchor)iter.next();
                    theAnchor.setOffsetH(partSize.width / 2);
                    theAnchor.setOffsetV(partSize.height);
                }
            }
        }
    }

    private void cleanUpAnchors() {
        // Get list of all connections and their anchors to this edit part.

        List sConnections = getSourceConnections();
        List tConnections = getTargetConnections();

        List connectionAnchors = new ArrayList(sConnections.size() + tConnections.size());

        if (sConnections.isEmpty()) {
            // Let's clear out the WEST anchors here.
            if (westAnchors != null) westAnchors.clear();
            if (southAnchors != null) southAnchors.clear();
        } else {
            NodeConnectionEditPart nextConnection = null;
            Iterator sIter = sConnections.iterator();
            while (sIter.hasNext()) {
                nextConnection = (NodeConnectionEditPart)sIter.next();
                if (nextConnection.getSourceAnchor() != null) connectionAnchors.add(nextConnection.getSourceAnchor());
            }
        }

        if (tConnections.isEmpty()) {
            // Let's clear out the WEST anchors here.
            if (eastAnchors != null) eastAnchors.clear();
        } else {
            NodeConnectionEditPart nextConnection = null;
            Iterator tIter = tConnections.iterator();
            while (tIter.hasNext()) {
                nextConnection = (NodeConnectionEditPart)tIter.next();
                if (nextConnection.getTargetAnchor() != null) connectionAnchors.add(nextConnection.getTargetAnchor());
            }
        }

        if (!connectionAnchors.isEmpty()) {
            List staleAnchors = new ArrayList();
            // Walk through each list and gather up list of any anchors not in connections list
            Iterator iter = null;
            NodeConnectionAnchor nextAnchor = null;
            if (westAnchors != null) {
                iter = westAnchors.iterator();
                while (iter.hasNext()) {
                    nextAnchor = (NodeConnectionAnchor)iter.next();
                    if (connectionAnchors.contains(nextAnchor)) {
                        // Do nothing
                    } else {
                        staleAnchors.add(nextAnchor);
                    }
                }
            }
            if (eastAnchors != null) {
                iter = eastAnchors.iterator();
                while (iter.hasNext()) {
                    nextAnchor = (NodeConnectionAnchor)iter.next();
                    if (connectionAnchors.contains(nextAnchor)) {
                        // Do nothing
                    } else {
                        staleAnchors.add(nextAnchor);
                    }
                }
            }

            if (southAnchors != null) {
                iter = southAnchors.iterator();
                while (iter.hasNext()) {
                    nextAnchor = (NodeConnectionAnchor)iter.next();
                    if (connectionAnchors.contains(nextAnchor)) {
                        // Do nothing
                    } else {
                        staleAnchors.add(nextAnchor);
                    }
                }
            }

            // Remove all extra from their lists.
            if (!staleAnchors.isEmpty()) {
                iter = staleAnchors.iterator();
                while (iter.hasNext()) {
                    nextAnchor = (NodeConnectionAnchor)iter.next();
                    if (westAnchors != null) westAnchors.remove(nextAnchor);
                    if (eastAnchors != null) eastAnchors.remove(nextAnchor);
                    if (southAnchors != null) southAnchors.remove(nextAnchor);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.connection.AnchorManager#reorderAllAnchors()
     * @since 4.2
     */
    public void reorderAllAnchors( boolean updateBothEnds ) {
        resetSourceAnchors(updateBothEnds);
        resetTargetAnchors(updateBothEnds);
    }
}
