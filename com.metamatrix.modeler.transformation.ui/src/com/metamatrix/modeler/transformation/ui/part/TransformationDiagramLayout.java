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

package com.metamatrix.modeler.transformation.ui.part;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.layout.DiagramLayout;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlPackageNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.transformation.ui.model.TransformationNode;
import com.metamatrix.modeler.transformation.ui.util.TransformationDiagramUtil;

/**
 * TransformationDiagramLayout
 */
public class TransformationDiagramLayout extends DiagramLayout {
    public static final int TOP_MARGIN = 50;
    public static final int LEFT_PANEL_LEFT_MARGIN = 30;
    public static final int LEFT_PANEL_RIGHT_MARGIN = 30;
    public static final int RIGHT_PANEL_LEFT_MARGIN = 30;
    public static final int TABLE_GAP =  20;
    
    private DiagramModelNode modelRoot = null;
    private DiagramModelNode transformationNode = null;
    private DiagramModelNode diagramModelNode = null;
    
    /**
     * Construct an instance of TransformationDiagramLayout.
     * 
     */
    public TransformationDiagramLayout() {
        super();
    }

    /**
     * Construct an instance of TransformationDiagramLayout.
     * @param newNodes
     */
    public TransformationDiagramLayout(List newNodes) {
        super(newNodes);
    }
    @Override
    public int run() {
        if( TransformationDiagramUtil.isTreeLayout()) {
            layoutInTree();
        } else {
            layoutInPanels();
        }
        return SUCCESSFUL;
    }
    
    public void setDiagramNode(DiagramModelNode diagramModelNode) {
        clear();
        this.diagramModelNode = diagramModelNode;
        // Let's populate the layout components
        setTransformation(getTransformationNode());
        setRoot(getRootNode());
        setSourceNodes();
    }

    public void setRoot(DiagramModelNode rootNode) {
        modelRoot = rootNode;
    }
    
    public void setTransformation(DiagramModelNode transNode) {
        transformationNode = transNode;
    }

    private void layoutInPanels() {
        // Make sure root node isn't null - Defect 14331 NPE
        if( modelRoot == null )
            return;
        
        // Define three X positions, root, transformation and source stack
        int rootX = LEFT_PANEL_LEFT_MARGIN;
        int rootY = TOP_MARGIN;
        int transX = 0; 
        int transY = 0;
        int stackX = 0;
        
        modelRoot.setPosition(new Point(rootX, rootY));
        
        transY = TOP_MARGIN; //modelRoot.getY() + modelRoot.getHeight()/2 - transformationNode.getHeight()/2;
        transX = rootX + modelRoot.getWidth() + LEFT_PANEL_RIGHT_MARGIN - transformationNode.getWidth()/2 ;
        
        transformationNode.setPosition(new Point(transX, transY));
        
        stackX = rootX + modelRoot.getWidth() + LEFT_PANEL_RIGHT_MARGIN + RIGHT_PANEL_LEFT_MARGIN;
        if( getComponentCount() > 0 ) {
            DiagramModelNode[] nodeArray = getNodeArray();
            int currentX = stackX;
            int nNodes = nodeArray.length;
            
            for( int i=0; i<nNodes; i++) {
                DiagramModelNode next = nodeArray[i];
                next.setPosition(new Point(currentX, rootY));
                currentX += next.getSize().width + TABLE_GAP;
            }
        }
        
    }
    
    private void layoutInTree() {
        // Make sure root node isn't null - Defect 14331 NPE
        if( modelRoot == null )
            return;
        
        // Define three X positions, root, transformation and source stack
        int rootX = 10;
        int rootY = 10;
        int transX = 0; 
        int transY = 0;
        int stackX = 0;
        
        int minNodeWidth = 9999;
        int minNodeHeight = 9999;
        int maxNodeWidth = 0;
        int maxNodeHeight = 0;

        int stackHeight = 0;
        
        modelRoot.setPosition(new Point(rootX, rootY));
        transY = modelRoot.getY() + modelRoot.getHeight()/2 - transformationNode.getHeight()/2;
        transformationNode.setPosition(new Point(transX, transY));
                
        transX = rootX + modelRoot.getWidth() + 90;
        stackX = transX + transformationNode.getWidth() + 120; 
        
        if( getComponentCount() > 0 ) {
            DiagramModelNode[] nodeArray = getNodeArray();
            int currentY = TOP_MARGIN;
            int nNodes = nodeArray.length;
            
            for( int i=0; i<nNodes; i++) {
                DiagramModelNode next = nodeArray[i];
                maxNodeWidth = Math.max( next.getSize().width, maxNodeWidth );
                maxNodeHeight = Math.max( next.getSize().height, maxNodeHeight );
                minNodeWidth = Math.min( next.getSize().width, minNodeWidth );
                minNodeHeight = Math.min( next.getSize().height, minNodeHeight );
                
                currentY = stackHeight + TOP_MARGIN;
                next.setPosition(new Point(stackX, currentY));

                stackHeight = stackHeight + next.getSize().height + 10;
                
            }
        }
        
        // Set rootY
        if( stackHeight < modelRoot.getHeight() ) {
            // Do Nothing
        } else {
            rootY = stackHeight/2 - modelRoot.getHeight()/2 + 10;
            modelRoot.setPosition(new Point(rootX, rootY));
        }
        
        // Set the T Node
        transY = modelRoot.getY() + modelRoot.getHeight()/2 - transformationNode.getHeight()/2;
        transformationNode.setPosition(new Point(transX, transY));
    }
    
    private void setSourceNodes() {
        Iterator iter = getCurrentSourceNodes().iterator();
        DiagramModelNode nextNode = null;
        while( iter.hasNext() ) {
            nextNode = (DiagramModelNode)iter.next();
            add(nextNode);
        }
    }
    
    private DiagramModelNode getTransformationNode() {
        // walk children and look for TransformationNode type
        Iterator iter = diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while( iter.hasNext() ) {
            nextNode = (DiagramModelNode)iter.next();
            if( nextNode instanceof TransformationNode )
                return nextNode;
        }
        
        return null;
    }
    
    private List getCurrentSourceNodes() {
        DiagramModelNode rootNode = getRootNode();
        
        List currentSourceNodes = new ArrayList();
        
        Iterator iter = diagramModelNode.getChildren().iterator();
        DiagramModelNode nextNode = null;
        while( iter.hasNext() ) {
            nextNode = (DiagramModelNode)iter.next();
            if( (nextNode instanceof UmlClassifierNode || nextNode instanceof UmlPackageNode) && nextNode != rootNode )
                currentSourceNodes.add(nextNode);
        }
        
        return currentSourceNodes;
    }
    
    private DiagramModelNode getRootNode() {
        DiagramModelNode root = null;
        DiagramModelNode transformationModelNode = getTransformationNode();
        // Get it's target
        Diagram diagram = transformationModelNode.getDiagram();
        if( diagram != null ) {
            EObject targetObject = diagram.getTarget();
            if( targetObject != null ) {
                root = DiagramUiUtilities.getDiagramModelNode(targetObject, transformationModelNode.getParent());
            }
        }
        return root;
    }
}
