/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.layout.spring.SpringLayout;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * LayoutUtilities
 */
public class LayoutUtilities {

    public static List getSingleNetwork(List connectedNodes) {

        DiagramModelNode firstDiagramNode = (DiagramModelNode)connectedNodes.get(0);
        List singleNodeNetwork = getLinkedNodesForNode(firstDiagramNode);        

        if( singleNodeNetwork.isEmpty())
            return Collections.EMPTY_LIST;
            
        connectedNodes.removeAll(singleNodeNetwork);
        
        return singleNodeNetwork;
    }
    
	public static List getSingleNetwork(List connectedNodes, DiagramModelNode startingNode) {

		DiagramModelNode firstDiagramNode = startingNode;
		List singleNodeNetwork = getLinkedNodesForNode(firstDiagramNode);        

		if( singleNodeNetwork.isEmpty())
			return Collections.EMPTY_LIST;
            
		connectedNodes.removeAll(singleNodeNetwork);
        
		return singleNodeNetwork;
	}
    
    public static List getLinkedNodesForNode(DiagramModelNode theNode ) {
        List linkedNodes = new ArrayList();
        
        addLinkedNodesForNode(linkedNodes, theNode);

        return linkedNodes;
    }
    
    private static void addLinkedNodesForNode(List linkedNodes, DiagramModelNode theNode) {

        List connectionList =  new ArrayList();
        
        connectionList.addAll(theNode.getSourceConnections());
        connectionList.addAll(theNode.getTargetConnections());
        
        Iterator iter = connectionList.iterator();
        
        NodeConnectionModel nextConnection = null;
        DiagramModelNode sourceNode = null;
        DiagramModelNode targetNode = null;
        while( iter.hasNext() ) {
            nextConnection = (NodeConnectionModel)iter.next();
            sourceNode = (DiagramModelNode)nextConnection.getSourceNode();
            targetNode = (DiagramModelNode)nextConnection.getTargetNode();
            
            if( sourceNode != null && !sourceNode.equals(theNode) && !linkedNodes.contains(sourceNode) ) {
                linkedNodes.add(sourceNode);
                addLinkedNodesForNode(linkedNodes, sourceNode);
            }
            if( targetNode != null && !targetNode.equals(theNode) && !linkedNodes.contains(targetNode) ) {
                linkedNodes.add(targetNode);
                addLinkedNodesForNode(linkedNodes, targetNode);
            }
        }
    }

    public static int getConnectionCount(List diagramNodes) {

        List allConnectionList =  new ArrayList();
        
        Iterator iter = diagramNodes.iterator();
        
        DiagramModelNode nextDiagramNode = null;
        Object nextNode = null;
        while( iter.hasNext() ) {

            nextNode = iter.next();
            if( nextNode instanceof DiagramModelNode ) {
                nextDiagramNode = (DiagramModelNode)nextNode;
                allConnectionList.addAll(nextDiagramNode.getSourceConnections());
                // Assume here that if we get all the source connections, we get all the 
                // connections without any duplicates,since all are binary.
            }
        }
        if( allConnectionList.isEmpty() )
            return 0;
        return allConnectionList.size();
    }

    public static int runSpringLayout(final LayoutGroup layoutGroup) {
        int result = 0;
        
        SpringLayout layout = new SpringLayout(layoutGroup.getLayoutNodes());
        layout.setAutoEdgeLength(true);
        layout.setEpsilon(5.0);
        layout.setStartLocation(20, 20);
        
//      layout.setAutoEdgeLength(true);
//      layout.setEdgeLength(200);
//      layout.setAutomaticSingleSpacing(true);
//      layout.setSpecifyLayoutSize(true);
//      layout.setLayoutSize(600);
//      layout.setEpsilon(0.5);
//      layout.setHorizontalAlignment(SpringLayout.LEFT_ALIGNMENT);
//      layout.setUseObjectsSizes(false);
//      layout.setVerticalAlignment(SpringLayout.TOP_ALIGNMENT);

        
        
        int size = (int)layout.getSizeEstimate();
        layout.setSpecifyLayoutSize(true);
        layout.setLayoutSize(size);
        
        result = layout.run();
        
        return result;
    }
    
	public static int runSpringLayout(final LayoutGroup layoutGroup, final int sizeFactor) {
		int result = 0;
        
		SpringLayout layout = new SpringLayout(layoutGroup.getLayoutNodes());
		layout.setAutoEdgeLength(true);
		layout.setEpsilon(5.0);
		layout.setStartLocation(20, 20);
        
//		layout.setAutoEdgeLength(true);
//		layout.setEdgeLength(200);
//		layout.setAutomaticSingleSpacing(true);
//		layout.setSpecifyLayoutSize(true);
//		layout.setLayoutSize(600);
//		layout.setEpsilon(0.5);
//		layout.setHorizontalAlignment(SpringLayout.LEFT_ALIGNMENT);
//		layout.setUseObjectsSizes(false);
//		layout.setVerticalAlignment(SpringLayout.TOP_ALIGNMENT);

        
		int size = (int)layout.getSizeEstimate()*sizeFactor;
		layout.setSpecifyLayoutSize(true);
		layout.setLayoutSize(size);
        
		result = layout.run();
        
		return result;
	}
    
    public static int runTreeLayout(final LayoutGroup layoutGroup) {
        int result = 0;
        
        TreeLayout layout = new TreeLayout(layoutGroup.getLayoutNodes(), 10, 10, 1000, 1000);
        layout.setRoot((LayoutNode)layoutGroup.getLayoutNodes().get(0));
        
//		layout.setOrientation(TreeLayout.ORIENTATION_ROOT_TOP);
		layout.setFixedSpacing(true);
		layout.setFixedXSpacing(100);
		layout.setFixedYSpacing(100);
		layout.setUseObjectsSizes(true);
//      layout.setHeight(400);
//      layout.setWidth(400);
//      layout.setOrientation(MmTreeLayout.ORIENTATION_ROOT_LEFT);    
//      layout.setFixedSpacing(true);
//      layout.setFixedXSpacing(140);
//      layout.setFixedYSpacing(140);
//      layout.setUseObjectsSizes(true);
        
        result = layout.run();
        return result;
    }
    
    public static int runTreeLayout(final LayoutGroup layoutGroup, final LayoutNode rootNode) {
        int result = 0;
        
        TreeLayout layout = new TreeLayout(layoutGroup.getLayoutNodes(), 10, 10, 1000, 1000);
        layout.setRoot(rootNode);
        
//		layout.setOrientation(TreeLayout.ORIENTATION_ROOT_TOP);
		layout.setFixedSpacing(true);
		layout.setFixedXSpacing(100);
		layout.setFixedYSpacing(100);
		layout.setUseObjectsSizes(true);
//      layout.setHeight(400);
//      layout.setWidth(400);
//      layout.setOrientation(MmTreeLayout.ORIENTATION_ROOT_LEFT);    
//      layout.setFixedSpacing(true);
//      layout.setFixedXSpacing(140);
//      layout.setFixedYSpacing(140);
//      layout.setUseObjectsSizes(true);
        
        result = layout.run();
        return result;
    }
    
    public static int runColumnLayout(final LayoutGroup layoutGroup) {
        int startX = 10;
        int startY = 10;
        ColumnLayout columnLayout = new ColumnLayout(layoutGroup.getLayoutNodes(), startX, startY );
        columnLayout.run();
        
        return 0;
    }
    
    public static int runColumnLayout(final LayoutGroup layoutGroup, final int padding) {
        int startX = 10;
        int startY = 10;
        ColumnLayout columnLayout = new ColumnLayout(layoutGroup.getLayoutNodes(), startX, startY );
        columnLayout.setPadding(padding);
        columnLayout.run();
        
        return 0;
    }
    
    public static LayoutNode[] getLayoutNodeArray(final List nodes) {
        LayoutNode[] nodeArray = new LayoutNode[nodes.size()];
        Object nextNode = null;
        Iterator iter = nodes.iterator();
        int count = 0;
        while( iter.hasNext() ) {
            nextNode = iter.next();
            if(nextNode instanceof LayoutNode ) {
                nodeArray[count] = (LayoutNode)nextNode;
                count++;
            }
        }
        return nodeArray;
    }

    public static void justifyAllToCorner(final List nodes) {
        // Check to see that minimum initial X,Y of all components is < 11
        // else move all components to fill up left/top of window.
        
        Iterator iter = nodes.iterator();
        double minX = 9999;
        double minY = 9999;
        LayoutNode nextComp = null;
        double deltaX = 0;
        double deltaY = 0;
        
        while( iter.hasNext() ) {
            nextComp = (LayoutNode)iter.next();
            minX = Math.min( nextComp.getX(), minX );
            minY = Math.min( nextComp.getY(), minY );
        }
        
        deltaX = minX - 20;
        deltaY = minY - 20;
        

        iter = nodes.iterator();
        while( iter.hasNext() ) {
            nextComp = (LayoutNode)iter.next();
            nextComp.setPosition( new Point(nextComp.getX() - deltaX, nextComp.getY() - deltaY) );
        }    

    }
    public static void justifyAllToCorner(final Object[] nodes) {
        // Check to see that minimum initial X,Y of all components is < 11
        // else move all components to fill up left/top of window.
        
        int nNodes = nodes.length;
        double minX = 9999;
        double minY = 9999;
        LayoutNode nextComp = null;
        double deltaX = 0;
        double deltaY = 0;
        
        for(int i=0; i<nNodes; i++) {
            nextComp = (LayoutNode)nodes[i];
            minX = Math.min( nextComp.getX(), minX );
            minY = Math.min( nextComp.getY(), minY );
        }
        
        deltaX = minX - 20;
        deltaY = minY - 20;
        
        for(int i=0; i<nNodes; i++) {
            nextComp = (LayoutNode)nodes[i];
            nextComp.setPosition( new Point(nextComp.getX() - deltaX, nextComp.getY() - deltaY) );
        } 

    }
    
    public static int getCurrentHeight(final List layoutNodes) {
        
        // Walk through springNodes and get the total width
        double currentHeight = 0;
        double minH = 9999;
        double maxH = -9999;
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
    
    public static int getCurrentHeight(final LayoutNode[] layoutNodes) {
        
        // Walk through springNodes and get the total width
        double currentHeight = 0;
        double minH = 9999;
        double maxH = -9999;
        double nextYPlusH = 0;
        int nNodes = layoutNodes.length;

        LayoutNode nextNode;
        for(int i=0; i<nNodes; i++ ) {
            nextNode = layoutNodes[i];
            minH = Math.min(minH, nextNode.getY());
            nextYPlusH = nextNode.getY() + nextNode.getHeight();
            maxH = Math.max(maxH, nextYPlusH);
        }
        currentHeight = maxH - minH;
        
        return (int)currentHeight;
    }
}
