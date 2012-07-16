/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.layout.spring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.draw2d.geometry.Rectangle;
import org.teiid.designer.diagram.ui.connection.NodeConnectionModel;
import org.teiid.designer.diagram.ui.layout.DefaultLayoutNode;
import org.teiid.designer.diagram.ui.layout.DiagramLayout;
import org.teiid.designer.diagram.ui.layout.LayoutNode;
import org.teiid.designer.diagram.ui.layout.LayoutUtilities;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;

public class SpringLayout extends DiagramLayout {
    static final String CLASS_NAME = "SpringLayout"; //$NON-NLS-1$
    public static final int LEFT_ALIGNMENT = 0;
    public static final int CENTER_ALIGNMENT = 2;
    public static final int RIGHT_ALIGNMENT = 1;
    public static final int TOP_ALIGNMENT = 3;
    public static final int ERROR_NO_COMPONENT = 1;
    public static final int ERROR_NON_CONNECTED_GRAPH = 2;
    public static final int ERROR_ALL_COMPONENTS_FIXED = 3;
    private boolean _autoEdgeLength = true;
    private double _edgeLength = 1000.0;
    private boolean _useObjectsSizes = true;
//    private boolean _fixSelected = false;
    private double _XSpacing = 10.0;
    private double _YSpacing = 10.0;
    private boolean _automaticSingleSpacing = true;
    private boolean _specifyLayoutSize = false;
    private double _layoutSize = 500.0;
    private int _horizontalAlignment = 2;
    private int _verticalAlignment = 2;
    private int _repaintPeriod = 0;
    private double _epsilon = 1.5;
    private final HashMap _nodeConstraints;
    private final WeakHashMap _linkConstraints;
    private DiagramModelNode diagramNode;
    
    private LayoutNode[] springNodes;

    public SpringLayout( List nodes) {

        _nodeConstraints = new HashMap();
        _linkConstraints = new WeakHashMap();
        
        // Find and set the diagram node parent object
        Object firstNode = nodes.get(0);
        if( firstNode instanceof DiagramModelNode )
            diagramNode = ((DiagramModelNode)firstNode).getParent();
        else if( firstNode instanceof LayoutNode ) {
            diagramNode = ((LayoutNode)firstNode).getModelNode().getParent();
        }

        createSpringNodes(nodes);
    }
    
    public void createSpringNodes(List nodes) {
        // Walk through the node list and create the list of new spring nodes.
        if( nodes != null && !nodes.isEmpty() ) {

            LayoutNode nextSpringNode = null;
            Iterator iter = nodes.iterator();
            int nNodes = nodes.size();
            springNodes = new LayoutNode[nNodes];
            int iNode = 0;
            Object nextObject = null;
            while( iter.hasNext()) {
                nextObject = iter.next();
                // Could be a list of diagramNodes or layoutNodes
                if( nextObject instanceof DiagramModelNode ) {
                    nextSpringNode = new DefaultLayoutNode((DiagramModelNode)nextObject);
                } else if( nextObject instanceof LayoutNode ) {
                    nextSpringNode = (LayoutNode)nextObject;
                }
                SpringNodeConstraints newConstraint = null;
                if( nextSpringNode != null ) {
                    springNodes[iNode] = nextSpringNode;
                    Object modelObject = nextSpringNode.getModelNode().getModelObject();
                    newConstraint = new SpringNodeConstraints(false, 50.0);
                    _nodeConstraints.put(modelObject, newConstraint);
                    iNode++;
                }
            }
        }
    }

    @Override
    public int run() {
        int resultValue = run(springNodes, 0.0, 0.0);
        
        // Here's where we need to fix!!!!!
        //Here's where...
        
//        if (resultValue == 2) {
//            int iVAlign = getVerticalAlignment();
//            setVerticalAlignment(3);
//
//            Hashtable hashtable = new Hashtable();
//
////            LayoutNode[] allLayoutNodes = DiagramUiUtilities.getNodeArray(diagramNode.getChildren());
//            
//            int nSpringNodes = springNodes.length;
//            
//            for (int iNode = 0; iNode < nSpringNodes; iNode++)
//                hashtable.put(springNodes[iNode].getModelNode(), new Integer(iNode));
//            
//            // Set up Connection Array                
//            NodeConnectionModel[][] connectionNodes = new NodeConnectionModel[nSpringNodes][nSpringNodes];
//
//            for (int iNode = 0; iNode < springNodes.length; iNode++) {
//                
//                LayoutNode nextNode = springNodes[iNode];
//                
//                if (nextNode != null && nextNode instanceof NodeConnectionModel) {
//                    
//                    NodeConnectionModel connectionModel = (NodeConnectionModel)nextNode;
//                    
//                    Object sourceNode = hashtable.get(connectionModel.getSourceNode());
//                    
//                    if (sourceNode != null) {
//                        
//                        Object targetNode = hashtable.get(connectionModel.getTargetNode());
//                        
//                        if (targetNode != null) {
//                            int sourceId = ((Integer)sourceNode).intValue();
//                            int targetId = ((Integer)targetNode).intValue();
//                            connectionNodes[sourceId][targetId] = connectionModel;
//                            connectionNodes[targetId][sourceId] = connectionModel;
//                        }
//                    }
//                }
//            }
//            Vector singlesVector = new Vector();
//            Vector connectedVector = new Vector();
//            if (connectionNodes != null) {
//                for (int iConn = 0; iConn < connectionNodes.length; iConn++) {
//                    boolean bool = true;
//                    for (int jConn = 0; jConn < connectionNodes.length; jConn++) {
//                        if (connectionNodes[iConn][jConn] != null)
//                            bool = false;
//                    }
//                    if (bool)
//                        singlesVector.add(springNodes[iConn]);
//                    else
//                        connectedVector.add(springNodes[iConn]);
//                }
//            }
//
//            Rectangle2D rectangle2d;
//            
//            if (singlesVector.size() > 0)
//                rectangle2d = layoutSingleComponents((LayoutNode[])singlesVector.toArray(new LayoutNode[1]));
//            else
//                rectangle2d = new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);
//                
//            if (connectedVector.size() > 0) {
//                resultValue = layoutConnectedComponents(
//                        (Vector)connectedVector.clone(),
//                        connectionNodes,
//                        hashtable,
//                        rectangle2d);
//            }
//                        
//            setVerticalAlignment(iVAlign);
//        }
        
        // move to upper left hand corner
        LayoutUtilities.justifyAllToCorner(springNodes);
        
        // set the final positions on the model objects
        setFinalNodePositions();
        
        return resultValue;
    }

    int run(LayoutNode[] nodeArray, double deltaX, double deltaY) {
        int nNodes = nodeArray.length;
        
        Spring spring = new Spring(this);
        
        if (nNodes == 0)
            return 0;
            
        Rectangle rectangle2d;
        
        if (_specifyLayoutSize)
            rectangle2d = new Rectangle(getStartX(), getStartY(), (int)_layoutSize, (int)_layoutSize);
        else {
            int length = (int)getSizeEstimate();
            rectangle2d = new Rectangle(getStartX(), getStartY(), length, length);
        }
            
        spring.setRectangle(rectangle2d);
        spring.setAutoEdgeLength(_autoEdgeLength);
        spring.setEdgeLength(_edgeLength);
        spring.setWidthIgnored(!_useObjectsSizes);
        spring.setHeightIgnored(!_useObjectsSizes);
//        spring.setFixSelected(_fixSelected);
        spring.setRepaintPeriod(_repaintPeriod);
        spring.setEpsilon(_epsilon);
        spring.setNodeConstraints(_nodeConstraints);
        spring.setLinkConstraints(_linkConstraints);
        
        String string = spring.compute(nodeArray, nNodes);

        for (int i = 0; i < nodeArray.length; i++) {
            LayoutNode nextNode = nodeArray[i];
            nextNode.setCenterXY(spring.centerX[i], spring.centerY[i]);
        }
        Rectangle rectangle = new Rectangle(nodeArray[0].getBounds());
        

        double startX = rectangle.x;
        double startY = rectangle.y;
        double currentW = rectangle.width;
        double currentH = rectangle.height;
        
        for (int i = 1; i < nodeArray.length; i++) {
            double thisX = nodeArray[i].getX();
            double thisY = nodeArray[i].getY();
            double thisW = nodeArray[i].getBounds().width;
            double thisH = nodeArray[i].getBounds().height;
            startX = Math.min(thisX, startX);
            startY = Math.min(thisY, startY);
            currentW = Math.max(currentW, thisW);
            currentH = Math.max(currentH, thisH);
        }
            
        double xOffset;
        
        double totalCenterX = rectangle.getCenter().x;
        double totalCenterY = rectangle.getCenter().y;
        
        if (_horizontalAlignment == 0)
            xOffset = -deltaX + rectangle.x - getStartX();
        else if (_horizontalAlignment == 2)
            xOffset = (-deltaX + rectangle.x + rectangle.width * 0.5 - totalCenterX);
        else
            xOffset = (-deltaX + rectangle.x + rectangle.width - getStartX() - this.getWidth());
            
        double yOffset;
        
        if (_verticalAlignment == 2)
            yOffset = (-deltaY + rectangle.y + rectangle.height * 0.5 - totalCenterY);
        else
            yOffset = -deltaY + rectangle.y - getStartY();
        
        // reposition all objects
        for (int i = 0; i < nNodes; i++) {
            LayoutNode nextNode = nodeArray[i];
            nextNode.setCenterXY(spring.centerX[i] - xOffset, spring.centerY[i] - yOffset);
        }

        if (string == null)
            return 0;
        if (string.endsWith("to layout")) //$NON-NLS-1$
            return 1;
        if (string.endsWith("non-connected graph!")) //$NON-NLS-1$
            return 2;
        return 3;
    }


    public SpringNodeConstraints getNodeConstraints(LayoutNode springNode) {
        if (!_nodeConstraints.containsKey(springNode))
            throw new IllegalArgumentException("Node " + springNode + " is not managed by this layout"); //$NON-NLS-1$ //$NON-NLS-2$
        return (SpringNodeConstraints)_nodeConstraints.get(springNode);
    }

    public void removeNodeConstraints(LayoutNode springNode) {
        if (!_nodeConstraints.containsKey(springNode))
            throw new IllegalArgumentException("Node " + springNode + " is not managed by this layout"); //$NON-NLS-1$ //$NON-NLS-2$
        _nodeConstraints.put(springNode, null);
    }

    public void setNodeConstraints(
        LayoutNode springNode,
        SpringNodeConstraints springnodeconstraints) {
        if (!_nodeConstraints.containsKey(springNode))
            throw new IllegalArgumentException("Node " + springNode + " is not managed by this layout"); //$NON-NLS-1$ //$NON-NLS-2$
        _nodeConstraints.put(springNode, springnodeconstraints);
    }

    public SpringLinkConstraints getLinkConstraints(NodeConnectionModel connectionModel) {
        return (SpringLinkConstraints)_linkConstraints.get(connectionModel);
    }

    public void removeLinkConstraints(NodeConnectionModel connectionModel) {
        _linkConstraints.remove(connectionModel);
    }

    public void setLinkConstraints(
        NodeConnectionModel connectionModel,
        SpringLinkConstraints springlinkconstraints) {
        _linkConstraints.put(connectionModel, springlinkconstraints);
    }

    public boolean getAutoEdgeLength() {
        return _autoEdgeLength;
    }

    public double getEdgeLength() {
        return _edgeLength;
    }

    public double getEpsilon() {
        return _epsilon;
    }

    public int getRepaintPeriod() {
        return _repaintPeriod;
    }

    public boolean getUseObjectsSizes() {
        return _useObjectsSizes;
    }

    public void setAutoEdgeLength(boolean bool) {
        _autoEdgeLength = bool;
    }

    public void setEdgeLength(double d) {
        _edgeLength = d;
    }

    public void setEpsilon(double d) {
        _epsilon = d;
    }

    public void setRepaintPeriod(int i) {
        _repaintPeriod = i;
    }

    public void setUseObjectsSizes(boolean bool) {
        _useObjectsSizes = bool;
    }

    public int getHorizontalAlignment() {
        return _horizontalAlignment;
    }

    public void setHorizontalAlignment(int i) {
        if (i <= 2)
            _horizontalAlignment = i;
    }

    public int getVerticalAlignment() {
        return _verticalAlignment;
    }

    public void setVerticalAlignment(int i) {
        if (i >= 2)
            _verticalAlignment = i;
    }

    public boolean getSpecifyLayoutSize() {
        return _specifyLayoutSize;
    }

    public void setSpecifyLayoutSize(boolean bool) {
        _specifyLayoutSize = bool;
    }

    public boolean getAutomaticSingleSpacing() {
        return _automaticSingleSpacing;
    }

    public void setAutomaticSingleSpacing(boolean bool) {
        _automaticSingleSpacing = bool;
    }

    public double getLayoutSize() {
        return _layoutSize;
    }

    public void setLayoutSize(double d) {
        _layoutSize = d;
    }

    public double getXSpacing() {
        return _XSpacing;
    }

    public void setXSpacing(double d) {
        _XSpacing = d;
    }

    public double getYSpacing() {
        return _YSpacing;
    }

    public void setYSpacing(double d) {
        _YSpacing = d;
    }
    
    private void setFinalNodePositions() {
        for( int i=0; i<springNodes.length; i++ ) {
            springNodes[i].setFinalPosition();
        }
    }
    /**
     * @return
     */
    public DiagramModelNode getDiagramNode() {
        return diagramNode;
    }
    
    public double getSizeEstimate() {
        double totalArea = 0.0;
        int nNodes = springNodes.length;
        double areaFactor = 5 + 2*Math.sqrt(nNodes);
        
        for( int i=0; i<springNodes.length; i++ ) {
                totalArea += (springNodes[i].getWidth() * springNodes[i].getHeight());
        }
        totalArea = areaFactor*totalArea;
        double length = Math.sqrt(totalArea);
        
        return length;
        
    }

//    public int getCurrentWidth() {
//        // Walk through springNodes and get the total width
//        double currentWidth = 0;
//        double nextXPlusW = 0;
//        for( int i=0; i<springNodes.length; i++ ) {
//            nextXPlusW = (springNodes[i].getCenterX() + springNodes[i].getWidth()/2);
//            currentWidth = Math.max(currentWidth, nextXPlusW);
//        }
//        
//        return (int)currentWidth;
//    }
//    
//
//    public int getCurrentHeight() {
//        // Walk through springNodes and get the total width
//        double currentHeight = 0;
//        double nextYPlusH = 0;
//        for( int i=0; i<springNodes.length; i++ ) {
//            nextYPlusH = (springNodes[i].getCenterY() + springNodes[i].getHeight()/2);
//            currentHeight = Math.max(currentHeight, nextYPlusH);
//        }
//        
//        return (int)currentHeight;
//    }
}
