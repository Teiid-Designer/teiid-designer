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

package com.metamatrix.modeler.diagram.ui.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;

/**
 * LayoutHelper
 * This class is designed to provide a diagram a means for breaking down diagram components into managable 
 */
public class LayoutHelper {
    public static final int NO_LINKS_LAYOUT = 0;
    public static final int SIMPLE_LAYOUT = 1; // i.e. no circular links
    public static final int COMPLEX_LAYOUT = 2; // circular links
    protected DiagramModelNode diagramNode;
    
    protected List layoutGroups;
    protected LayoutGroup unConnectedGroup = null;

    /**
     * Construct an instance of LayoutHelper.
     * 
     */
    public LayoutHelper() {
        super();
    }
    
    public LayoutHelper(DiagramModelNode diagramNode) {
        this.diagramNode = diagramNode;
        // Get the children of the diagramNode and go from there.
        processNodes(diagramNode.getChildren());
    }
    
	public LayoutHelper(DiagramModelNode diagramNode, DiagramModelNode startingNode) {
		this.diagramNode = diagramNode;
		// Get the children of the diagramNode and go from there.
		processNodes(diagramNode.getChildren(), startingNode);
	}
    
    public List getLayoutGroups() {
        return layoutGroups;
    }
    
    public void layoutAll() {
        // This performs a layout of all layout groups, individually, and then does
        // a "move" layout to layout the layouts
        // Then it does a call to tell all layouts to set their final positions of all layoutNodes.
        Iterator iter = layoutGroups.iterator();
        LayoutGroup nextGroup = null;
        while( iter.hasNext() ) {
            nextGroup = (LayoutGroup)iter.next();
            nextGroup.layout();
        }
        
        if( unConnectedGroup != null )
            unConnectedGroup.layout();
        
        layoutLayouts();
        
        setFinalPositions();
    }
    
    public void setFinalPositions() {
        Iterator iter = layoutGroups.iterator();
        LayoutGroup nextGroup = null;
        while( iter.hasNext() ) {
            nextGroup = (LayoutGroup)iter.next();
            nextGroup.setFinalPositions();
        }
        
        if( unConnectedGroup != null )
            unConnectedGroup.setFinalPositions();
    }
    
    protected void processNodes(List childNodes) {
        layoutGroups = new ArrayList(); 
        // This method begins to break down the objects in the diagram

        List nonConnectedModelNodes = DiagramUiUtilities.getNonConnectedModelNodes(getDiagramNode());
        if( !nonConnectedModelNodes.isEmpty()) {
            unConnectedGroup = new LayoutGroup(nonConnectedModelNodes, LayoutHelper.NO_LINKS_LAYOUT);
        }
        
        // Now we have to process the connected nodes and create a LayoutGroup for each network.
        List connectedModelNodes = DiagramUiUtilities.getConnectedModelNodes(getDiagramNode());
        
        while( !connectedModelNodes.isEmpty() ) {
            // make a call to a method which returns a group of connected nodes.
            List connectedNodeList = LayoutUtilities.getSingleNetwork(connectedModelNodes);
            if( !connectedNodeList.isEmpty() )
                layoutGroups.add(new LayoutGroup(connectedNodeList));
        }
    }
    
	protected void processNodes(List childNodes, DiagramModelNode startingNode) {
		DiagramModelNode tempStartingNode = startingNode;
		layoutGroups = new ArrayList(); 
		// This method begins to break down the objects in the diagram

		List nonConnectedModelNodes = DiagramUiUtilities.getNonConnectedModelNodes(getDiagramNode());
		if( !nonConnectedModelNodes.isEmpty()) {
			unConnectedGroup = new LayoutGroup(nonConnectedModelNodes, LayoutHelper.NO_LINKS_LAYOUT);
		}
        
		// Now we have to process the connected nodes and create a LayoutGroup for each network.
		List connectedModelNodes = DiagramUiUtilities.getConnectedModelNodes(getDiagramNode());
        
		while( !connectedModelNodes.isEmpty() ) {
			// make a call to a method which returns a group of connected nodes.
			List connectedNodeList = null;
			if( tempStartingNode != null ) {
				connectedNodeList = LayoutUtilities.getSingleNetwork(connectedModelNodes, tempStartingNode);
                // If the starting node has no links, we default to the generic call.
                if( connectedNodeList.isEmpty() ) {
                    tempStartingNode = null;
                    connectedNodeList = LayoutUtilities.getSingleNetwork(connectedModelNodes);
                }
			} else
				connectedNodeList = LayoutUtilities.getSingleNetwork(connectedModelNodes);
				
			if( !connectedNodeList.isEmpty() ) {
				if( tempStartingNode != null) {
					layoutGroups.add(new LayoutGroup(connectedNodeList, tempStartingNode));
					tempStartingNode = null;
				}else
					layoutGroups.add(new LayoutGroup(connectedNodeList));
			}
		}
	}
    
    /**
     * @return
     */
    public DiagramModelNode getDiagramNode() {
        return diagramNode;
    }
    
    private void layoutLayouts() {
        LayoutGroup finalLayoutGroup = null;
        if( layoutGroups != null && !layoutGroups.isEmpty() ) {
            finalLayoutGroup = new LayoutGroup(layoutGroups);
            LayoutUtilities.runColumnLayout(finalLayoutGroup, 100);
        }
        if( unConnectedGroup != null ) {
            if( finalLayoutGroup != null ) {
                double currentHeight = finalLayoutGroup.getCurrentHeight();
                unConnectedGroup.move(20, currentHeight + 150);
            }
        }
        
    }

}
