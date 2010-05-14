/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.connection;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionAnchor;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;

/**
 * AnchorManager
 */
public class TransformationAnchorManager implements AnchorManager {
    
    private List northAnchors;
    private List southAnchors;
    private List eastAnchors;
    private List westAnchors;
    
    private DiagramEditPart diagramEditPart;
    
    /**
     * Construct an instance of AnchorManager.
     * 
     */
    public TransformationAnchorManager(DiagramEditPart diagramEditPart) {
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
    private boolean addEastAnchor(NodeConnectionAnchor newAnchor) {
        if( eastAnchors == null )
            eastAnchors = new ArrayList(1);
            
        if( !eastAnchors.contains(newAnchor)) {
            eastAnchors.add(newAnchor);
            return true;
        }
        
        return false;
    }

    /**
     * @param newAnchor
     */
    private boolean addNorthAnchor(NodeConnectionAnchor newAnchor) {
        if( northAnchors == null )
            northAnchors = new ArrayList(1);
            
        if( !northAnchors.contains(newAnchor)) {
            northAnchors.add(newAnchor);
            return true;
        }
        
        return false;
    }

    /**
     * @param newAnchor
     */
    private boolean addSouthAnchor(NodeConnectionAnchor newAnchor) {
        if( southAnchors == null )
            southAnchors = new ArrayList(1);
            
        if( !southAnchors.contains(newAnchor)) {
            southAnchors.add(newAnchor);
            return true;
        }
        
        return false;
            
    }

    /**
     * @param newAnchor
     */
    private boolean addWestAnchor(NodeConnectionAnchor newAnchor) {
        if( westAnchors == null )
            westAnchors = new ArrayList(1);
            
        if( !westAnchors.contains(newAnchor)) {
            westAnchors.add(newAnchor);
            return true;
        }
        
        return false;
    }
    
    public boolean add(NodeConnectionAnchor targetAnchor) {
        boolean added = false;
        
        if( targetAnchor.getDirection() == NORTH ) {
            added = addNorthAnchor(targetAnchor);
        } else if( targetAnchor.getDirection() == SOUTH ) {
            added = addSouthAnchor(targetAnchor);
        } else if( targetAnchor.getDirection() == EAST ) {
            added = addEastAnchor(targetAnchor);
        } else if( targetAnchor.getDirection() == WEST ) {
            added = addWestAnchor(targetAnchor);
        }
        
        return added;
    }
    
    public boolean move(NodeConnectionAnchor targetAnchor) {
        // Need to move the target anchor from one anchor list to another.
        
        // remove from any list.
        boolean removed = remove(targetAnchor);
        
        // Now add to direction's list
        boolean added = add( targetAnchor);
        
        if( removed && added )
            return true;
            
        return false;
    }
    
    public boolean remove(NodeConnectionAnchor targetAnchor) {
        boolean removed = false;
        if( targetAnchor.getDirection() != NORTH &&
            getNorthAnchors() != null && getNorthAnchors().contains(targetAnchor) ) {
            getNorthAnchors().remove(targetAnchor);
            removed = true;
        }
        
        if( targetAnchor.getDirection() != SOUTH &&
            getSouthAnchors() != null && getSouthAnchors().contains(targetAnchor) ) {
            getSouthAnchors().remove(targetAnchor);
            removed = true;
        }
        
        if( targetAnchor.getDirection() != EAST &&
            getEastAnchors() != null && getEastAnchors().contains(targetAnchor) ) {
            getEastAnchors().remove(targetAnchor);
            removed = true;
        } 
        
        if( targetAnchor.getDirection() != WEST &&
            getWestAnchors() != null && getWestAnchors().contains(targetAnchor) ) {
            getWestAnchors().remove(targetAnchor);
            removed = true;
        }
        
        return removed;
    }
    
    public void resetSourceAnchors(boolean updateTargetEnd) {  
        
        List sConnections = getSourceConnections();
        if( sConnections.isEmpty())
            return;
        
        NodeConnectionEditPart nextConnection = null;
        Iterator iter = sConnections.iterator();
        DiagramEditPart targetEditPart = null;
        NodeConnectionAnchor sourceAnchor = null;
        
        while( iter.hasNext() ) {
            nextConnection = (NodeConnectionEditPart)iter.next();
            sourceAnchor = (NodeConnectionAnchor)nextConnection.getSourceAnchor();
            targetEditPart = (DiagramEditPart )nextConnection.getTarget();
            
            if( targetEditPart != null && sourceAnchor != null ) {
                setAnchorPosition( sourceAnchor, WEST);
            
                // Update Target side anchors      
                if( updateTargetEnd )
                    targetEditPart.createOrUpdateAnchorsLocations(false);
            }

        }
    }
    
    public void resetTargetAnchors(boolean updateSourceEnd) {    
        List tConnections = getTargetConnections();
        if( tConnections.isEmpty())
            return;
        
            
        NodeConnectionEditPart nextConnection = null;
        Iterator iter = tConnections.iterator();
        DiagramEditPart sourceEditPart = null;
        NodeConnectionAnchor targetAnchor = null;
        
        while( iter.hasNext() ) {
            nextConnection = (NodeConnectionEditPart)iter.next();
            targetAnchor = (NodeConnectionAnchor)nextConnection.getTargetAnchor();
            
            sourceEditPart = (DiagramEditPart )nextConnection.getSource();
            
            if( sourceEditPart != null && targetAnchor != null ) {
                setAnchorPosition( targetAnchor, EAST);

            
                // Update Source side anchors    
                if( updateSourceEnd )
                    sourceEditPart.createOrUpdateAnchorsLocations(false);
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
    public ConnectionAnchor getSourceAnchor(NodeConnectionEditPart connection) {
        // This anchor manager  belongs to the edit part.
        // This edit part knows about all it's target connections
        // An anchor is either target or source
        // 
        
        if( connection.getSourceAnchor() == null ) {
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
    public ConnectionAnchor getTargetAnchor(NodeConnectionEditPart connection) {
        // This anchor manager  belongs to the edit part.
        // This edit part knows about all it's target connections
        // An anchor is either target or source
        // 
        if( connection.getTargetAnchor() == null ) {
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
        setAnchorPosition(newAnchor, EAST);
        return newAnchor;
    }
    
    
    private NodeConnectionAnchor createSourceAnchor() {
        NodeConnectionAnchor newAnchor = new NodeConnectionAnchor(diagramEditPart.getFigure(), NodeConnectionAnchor.IS_SOURCE);
//      Set some default direction
        newAnchor.setDirection(WEST);
        add(newAnchor);
        setAnchorPosition(newAnchor, WEST);
        return newAnchor;
    }
    
    public boolean hasSourceAnchors() {
        NodeConnectionAnchor nextAnchor = null;
        Iterator iter = null;
        
        if( northAnchors != null && !northAnchors.isEmpty() ) {
            iter = northAnchors.iterator();
            while(iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if( nextAnchor != null && nextAnchor.isSource() )
                    return true;
            }
        }
        
        if( southAnchors != null && !southAnchors.isEmpty() ) {
            iter = southAnchors.iterator();
            while(iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if( nextAnchor != null && nextAnchor.isSource() )
                    return true;
            }
        }
        
        if( eastAnchors != null && !eastAnchors.isEmpty() ) {
            iter = eastAnchors.iterator();
            while(iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if( nextAnchor != null && nextAnchor.isSource() )
                    return true;
            }
        }
        
        if( westAnchors != null && !westAnchors.isEmpty() ) {
            iter = westAnchors.iterator();
            while(iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if( nextAnchor != null && nextAnchor.isSource() )
                    return true;
            }
        }
        
        return false;
    }
    
    
    public boolean hasTargetAnchors() {
        NodeConnectionAnchor nextAnchor = null;
        Iterator iter = null;
        
        if( northAnchors != null && !northAnchors.isEmpty() ) {
            iter = northAnchors.iterator();
            while(iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if( nextAnchor != null && !nextAnchor.isSource() )
                    return true;
            }
        }
        
        if( southAnchors != null && !southAnchors.isEmpty() ) {
            iter = southAnchors.iterator();
            while(iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if( nextAnchor != null && !nextAnchor.isSource() )
                    return true;
            }
        }
        
        if( eastAnchors != null && !eastAnchors.isEmpty() ) {
            iter = eastAnchors.iterator();
            while(iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if( nextAnchor != null && !nextAnchor.isSource() )
                    return true;
            }
        }
        
        if( westAnchors != null && !westAnchors.isEmpty() ) {
            iter = westAnchors.iterator();
            while(iter.hasNext()) {
                nextAnchor = (NodeConnectionAnchor)iter.next();
                if( nextAnchor != null && !nextAnchor.isSource() )
                    return true;
            }
        }
        
        return false;
    }
    
    public void setAnchorPosition(NodeConnectionAnchor theAnchor, int direction ) {
          
        Dimension partSize = ((DiagramModelNode)diagramEditPart.getModel()).getSize();
        
        int oldDirection = theAnchor.getDirection();
        
        theAnchor.setDirection(direction);
        
        boolean moved = move(theAnchor);
        
        if( moved ) {
            setAnchorPositions(oldDirection, direction);
        } else if( direction == NORTH ) {
            theAnchor.setOffsetH(partSize.width/2);
            theAnchor.setOffsetV(0);
        } else if( direction == SOUTH) {
            theAnchor.setOffsetH(partSize.width/2);
            theAnchor.setOffsetV(partSize.height);
        } else if( direction == WEST) {
            theAnchor.setOffsetH(0);
            theAnchor.setOffsetV(partSize.height/2);
        } else {
            theAnchor.setOffsetH(partSize.width);
            theAnchor.setOffsetV(partSize.height/2);
        }

    }
    
    /**
     * This method for setting anchor positions assumes that we want to walk through all anchor positions
     * to insure proper spacing..... should be called whenever an anchor is "moved" and there are more than 
     * to or from.
     * @param theAnchor
     * @param direction
     */
    private void setAnchorPositions(int oldDirection, int newDirection ) {
        setAnchorPositions(oldDirection);
        setAnchorPositions(newDirection);
    }
    
    private void setAnchorPositions( int direction ) {
          
        Dimension partSize = ((DiagramModelNode)diagramEditPart.getModel()).getSize();
        Iterator iter = null;
        
        if( direction == NORTH && getNorthAnchors() != null && !getNorthAnchors().isEmpty() ) {
            // Walk through the anchors
            iter = getNorthAnchors().iterator();
            NodeConnectionAnchor theAnchor = null;
            while( iter.hasNext()) {
                theAnchor = (NodeConnectionAnchor)iter.next();
                theAnchor.setOffsetH(partSize.width/2);
                theAnchor.setOffsetV(0);
            }
        } else if( direction == SOUTH&& getSouthAnchors() != null && !getSouthAnchors().isEmpty() ) {
            // Walk through the anchors
            iter = getSouthAnchors().iterator();
            NodeConnectionAnchor theAnchor = null;
            while( iter.hasNext()) {
                theAnchor = (NodeConnectionAnchor)iter.next();
                theAnchor.setOffsetH(partSize.width/2);
                theAnchor.setOffsetV(partSize.height);
            }
        } else if( direction == WEST&& getWestAnchors() != null && !getWestAnchors().isEmpty() ) {
            // Walk through the anchors
            iter = getWestAnchors().iterator();
            NodeConnectionAnchor theAnchor = null;
            while( iter.hasNext()) {
                theAnchor = (NodeConnectionAnchor)iter.next();
                theAnchor.setOffsetH(0);
                theAnchor.setOffsetV(partSize.height/2);
            }

        } else if( getEastAnchors() != null && !getEastAnchors().isEmpty() ) {
            // Walk through the anchors
            iter = getEastAnchors().iterator();
            NodeConnectionAnchor theAnchor = null;
            while( iter.hasNext()) {
                theAnchor = (NodeConnectionAnchor)iter.next();
                theAnchor.setOffsetH(partSize.width);
                theAnchor.setOffsetV(partSize.height/2);
            }
        }
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

