/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * LayoutGroup
 * This class represents a group of layout nodes that are either totally connected or totally disconnected.
 * This class will provide the hooks to identify the type of connections (if any) i.e. tree, circular links, etc..
 */
public class LayoutGroup implements LayoutNode {
    private double thisX;
    private double thisY;
    
    private List layoutNodes;
    private int type = LayoutHelper.NO_LINKS_LAYOUT;
	private DiagramModelNode startingNode;
	private int nConnections = 0;
    
    /**
     * Construct an instance of LayoutGroup.
     * 
     */
    public LayoutGroup(List diagramNodes) {
        super();
        convertNodes(diagramNodes);
        calculateType(diagramNodes);
    }
    
    public LayoutGroup(List diagramNodes, int layoutType) {
        super();
        convertNodes(diagramNodes);
    }
    
	/**
	 * @param diagramNodes
	 */
	public LayoutGroup(List diagramNodes, DiagramModelNode startingNode) {
		super();
		convertNodes(diagramNodes);
		calculateType(diagramNodes);
		this.startingNode = startingNode;
	}
    
    private void convertNodes(List diagramNodes) {
        layoutNodes = new ArrayList(diagramNodes.size());
        
        LayoutNode newLayoutNode = null;
        Object nextNode = null;
        Iterator iter = diagramNodes.iterator();
        while( iter.hasNext() ) {
            nextNode = iter.next();
            if( nextNode instanceof DiagramModelNode ) {
                newLayoutNode = new DefaultLayoutNode((DiagramModelNode)nextNode);
                layoutNodes.add(newLayoutNode);
            } else if( nextNode instanceof LayoutNode) {
                layoutNodes.add(nextNode);
            }
        }
    }

    /**
     * @return
     */
    public List getLayoutNodes() {
        return layoutNodes;
    }
    
    

    /**
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * @param i
     */
    public void setType(int i) {
        type = i;
    }
    
    private void calculateType(List diagramNodes) {
        nConnections = LayoutUtilities.getConnectionCount(diagramNodes);
        int nNodes = diagramNodes.size();
        if( nConnections == (nNodes - 1) ) {
            type = LayoutHelper.SIMPLE_LAYOUT;
        } else {
            type = LayoutHelper.COMPLEX_LAYOUT;
        }
    }
    
    public void setFinalPositions() {
        Iterator iter = layoutNodes.iterator();
        LayoutNode nextLayoutNode = null;
        while( iter.hasNext() ) {
            nextLayoutNode = (LayoutNode)iter.next();
            nextLayoutNode.setFinalPosition();
        }
    }
    
    public void setPosition(Point point ) {
        setX(point.x);
        setY(point.y);
        move(point.x, point.y);
//        System.out.println(" -->> LayoutGroup.setPosition():  New XY Point = " + point);
    }
    
    public double getWidth() {
        return getCurrentWidth();
    }
    
    public double getHeight() {
        return getCurrentHeight();
    }
    
    public void move(double x, double y) {
//        System.out.println(" -->> LayoutGroup.move() NEW X = " + x + " Y = " + y);
        Iterator iter = layoutNodes.iterator();
        LayoutNode nextLayoutNode = null;
        while( iter.hasNext() ) {
            nextLayoutNode = (LayoutNode)iter.next();
            double newX = nextLayoutNode.getX() + x;
            double newY = nextLayoutNode.getY() + y;
            nextLayoutNode.setPosition(newX, newY);
        }
    }

    public void layout() {
        switch( type ) {
            case LayoutHelper.COMPLEX_LAYOUT: {
            	int numNodes = this.getLayoutNodes().size();
            	int nConn = nConnections;
            	
            	double connRatio = (double)nConn/(double)numNodes;
            		
            	if( numNodes > 400 && connRatio > 1.2 ) {
					LayoutUtilities.runColumnLayout(this);
            	} else {
					LayoutUtilities.runSpringLayout(this);
            	}
                setBounds(new Rectangle(0, 0, getCurrentWidth(), getCurrentHeight()));
            } break;
            
            case LayoutHelper.SIMPLE_LAYOUT: {
            	if( startingNode == null )
                	LayoutUtilities.runTreeLayout(this);
                else {
                	LayoutNode startNode = getLayoutNode(startingNode);
					LayoutUtilities.runTreeLayout(this, startNode);
                }
                setBounds(new Rectangle(0, 0, getCurrentWidth(), getCurrentHeight()));
            } break;
            
            case LayoutHelper.NO_LINKS_LAYOUT: {
                LayoutUtilities.runColumnLayout(this);
                setBounds(new Rectangle(0, 0, getCurrentWidth(), getCurrentHeight()));
            }
        }
//        System.out.println(" -->> LayoutGroup.layout()");
    }
    public int getCurrentWidth() {
        if( layoutNodes == null || layoutNodes.isEmpty()) {
            return 1;
        }
        
        // Walk through springNodes and get the total width
        double currentWidth = 0;
        double minW = 999999.;
        double maxW = -999999.;
        double nextXPlusW = 0;
        
        Iterator iter = layoutNodes.iterator();
        LayoutNode nextNode;
        while( iter.hasNext() ) {
            nextNode = (LayoutNode)iter.next();
            minW = Math.min(minW, nextNode.getX());
            nextXPlusW = nextNode.getX() + nextNode.getWidth();
            maxW = Math.max(maxW, nextXPlusW);
        }
        currentWidth = maxW - minW;
        
        return (int)currentWidth;
    }
    

    public int getCurrentHeight() {
        if( layoutNodes == null || layoutNodes.isEmpty()) {
            return 1;
        }
        
        // Walk through springNodes and get the total width
        double currentHeight = 0;
        double minH = 999999.;
        double maxH = -999999.;
        double nextYPlusH = 0;
        
        Iterator iter = layoutNodes.iterator();
        LayoutNode nextNode;
        while( iter.hasNext() ) {
            nextNode = (LayoutNode)iter.next();
            minH = Math.min(minH, nextNode.getY());
            nextYPlusH = nextNode.getY() + nextNode.getHeight();
            maxH = Math.max(maxH, nextYPlusH);
        }
        currentHeight = maxH - minH;
        
        return (int)currentHeight;
    }
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#getBounds()
     */
    public Rectangle getBounds() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#getCenterX()
     */
    public double getCenterX() {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#getCenterY()
     */
    public double getCenterY() {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#getModelNode()
     */
    public DiagramModelNode getModelNode() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#getPosition()
     */
    public org.eclipse.draw2d.geometry.Point getPosition() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#getX()
     */
    public double getX() {
        return thisX;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#getY()
     */
    public double getY() {
        return thisY;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#setBounds(java.awt.Rectangle)
     */
    public void setBounds(Rectangle rectangle) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#setCenterX(double)
     */
    public void setCenterX(double x) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#setCenterXY(double, double)
     */
    public void setCenterXY(double x, double y) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#setCenterY(double)
     */
    public void setCenterY(double y) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#setFinalPosition()
     */
    public void setFinalPosition() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#setPosition(double, double)
     */
    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
        move(x, y);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#setX(double)
     */
    public void setX(double x) {
        thisX = x;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.layout.LayoutNode#setY(double)
     */
    public void setY(double y) {
        thisY = y;
    }
    
    public LayoutNode getLayoutNode(Object someObject) { 
    	if( someObject != null && someObject instanceof DiagramModelNode ) {
    		// Walk through layout nodes and return the one that contains a modelObject matching someObject
    		Iterator iter = getLayoutNodes().iterator();
    		LayoutNode nextLN = null;
    		while( iter.hasNext() ) {
    			nextLN = (LayoutNode)iter.next();
    			if( nextLN.getModelNode() != null &&
    				nextLN.getModelNode().equals(someObject))
    				return nextLN;
    		}
    	}
    	return null;
    }

}
