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

package com.metamatrix.modeler.relationship.ui.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.metamatrix.modeler.diagram.ui.layout.LayoutGroup;
import com.metamatrix.modeler.diagram.ui.layout.LayoutNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipLayoutSubGroup extends LayoutGroup {
	private List ownedSourceNodes;
	private List ownedTargetNodes;
	private LayoutNode relationshipNode;
	private int columnId = 0;
	private int rowId = 0;
	/**
	 * 
	 */
	public RelationshipLayoutSubGroup(List allNodes, LayoutNode relationshipNode, List sourceNodes, List targetNodes) {
		super(allNodes);
		this.relationshipNode = relationshipNode;
		ownedSourceNodes = new ArrayList(sourceNodes);
		ownedTargetNodes = new ArrayList(targetNodes);
	}
	
	

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.layout.LayoutGroup#layout()
	 */
	@Override
    public void layout() {
		LayoutNode nextLN = null;
		int xSeparation = 60;
		// Stack sourceNodes
		Iterator iter = ownedSourceNodes.iterator();
		int currentY = 0;
		int ySpacing = 18;
		int sourceNodeWidth = 10;
		int stackHeight = 0;
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			nextLN.setPosition(0, currentY);
			currentY += nextLN.getHeight() + ySpacing;
			stackHeight += nextLN.getHeight();
			sourceNodeWidth = Math.max(sourceNodeWidth, (int)nextLN.getWidth());
		}

		stackHeight = stackHeight + ySpacing*(ownedSourceNodes.size()-1);
		// Set RelationshipNode Position
		relationshipNode.setPosition(sourceNodeWidth + xSeparation, stackHeight/2 - relationshipNode.getHeight());
		// Stack targetNodes
				
		iter = ownedTargetNodes.iterator();
		currentY = 0;
		int currentX = (int)relationshipNode.getX() + (int)relationshipNode.getWidth() + xSeparation;
		int startY = (int)relationshipNode.getY() + (int)relationshipNode.getHeight();
		stackHeight = 0;
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			nextLN.setPosition(currentX, currentY);
			currentY += nextLN.getHeight() + ySpacing;
			stackHeight += nextLN.getHeight();
			sourceNodeWidth = Math.max(sourceNodeWidth, (int)nextLN.getWidth());
		}
		stackHeight = stackHeight + ySpacing*(ownedTargetNodes.size()-1);
		int deltaY = startY - stackHeight/2;
		moveNodes(ownedTargetNodes, 0, 0 + deltaY);
//		LayoutUtilities.justifyAllToCorner(getLayoutNodes());
	}
	
	private void moveNodes(List layoutNodes, int deltaX, int deltaY) {
		LayoutNode nextLN = null;
		Iterator iter = layoutNodes.iterator();
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			nextLN.setPosition(nextLN.getX() + deltaX, nextLN.getY() + deltaY);
		}
	}
	/**
	 * @return
	 */
	public LayoutNode getRelationshipNode() {
		return relationshipNode;
	}

	/**
	 * @return
	 */
	public List getOwnedSourceNodes() {
		if( ownedSourceNodes == null || ownedSourceNodes.isEmpty())
			return Collections.EMPTY_LIST;
		return ownedSourceNodes;
	}

	/**
	 * @return
	 */
	public List getOwnedTargetNodes() {
		if( ownedTargetNodes == null || ownedTargetNodes.isEmpty())
			return Collections.EMPTY_LIST;
		return ownedTargetNodes;
	}

	/**
	 * @return
	 */
	public int getColumnId() {
		return columnId;
	}

	/**
	 * @param i
	 */
	public void setColumnId(int i) {
		columnId = i;
	}

	/**
	 * @return
	 */
	public int getRowId() {
		return rowId;
	}

	/**
	 * @param i
	 */
	public void setRowId(int i) {
		rowId = i;
	}

}
