/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.layout;

import java.util.ArrayList;
import java.util.List;

import com.metamatrix.modeler.diagram.ui.layout.LayoutGroup;
import com.metamatrix.modeler.diagram.ui.layout.LayoutHelper;
import com.metamatrix.modeler.diagram.ui.layout.LayoutUtilities;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipLayoutHelper extends LayoutHelper {
	/**
	 * 
	 */
	public RelationshipLayoutHelper() {
		super();
	}

	/**
	 * @param diagramNode
	 */
	public RelationshipLayoutHelper(DiagramModelNode diagramNode, DiagramModelNode selectedRelationshipNode) {
		super(diagramNode, selectedRelationshipNode);
	}
	
	// Need to override base method because we need a new type of group here.  RelationshipLayoutGroup.
	@Override
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
			if( connectedModelNodes.size() == 1 ) {
				layoutGroups.add(new RelationshipLayoutGroup(connectedModelNodes, null));
				connectedModelNodes.clear();
			} else {
				if( tempStartingNode != null ) {
					connectedNodeList = LayoutUtilities.getSingleNetwork(connectedModelNodes, tempStartingNode);
				} else
					connectedNodeList = LayoutUtilities.getSingleNetwork(connectedModelNodes);
				if( !connectedNodeList.isEmpty() )
					layoutGroups.add(new RelationshipLayoutGroup(connectedNodeList, tempStartingNode));
			}
			tempStartingNode = null;
			
		}
	}
}
