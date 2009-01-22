/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.layout.LayoutGroup;
import com.metamatrix.modeler.diagram.ui.layout.LayoutHelper;
import com.metamatrix.modeler.diagram.ui.layout.LayoutNode;
import com.metamatrix.modeler.diagram.ui.layout.LayoutUtilities;
import com.metamatrix.modeler.diagram.ui.layout.TreeLayout;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipModelNode;
import com.metamatrix.modeler.relationship.ui.model.RelationshipTypeModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipLayoutGroup extends LayoutGroup {
	List subGroups;
	DiagramModelNode startingNode;
	private static final int yPadding = 20;
//	private static final int xPadding = 20;
	
	/**
	 * @param diagramNodes
	 */
	public RelationshipLayoutGroup(List diagramNodes) {
		super(diagramNodes);
		subGroups = new ArrayList();
	}
	
	/**
	 * @param diagramNodes
	 */
	public RelationshipLayoutGroup(List diagramNodes, DiagramModelNode startingNode) {
		super(diagramNodes);
		this.startingNode = startingNode;
		subGroups = new ArrayList();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.layout.LayoutGroup#layout()
	 */
	@Override
    public void layout() {
		switch( getType() ) {
			case LayoutHelper.COMPLEX_LAYOUT:
			case LayoutHelper.SIMPLE_LAYOUT: {
				// layout the relationship group
				// I can start with any RelationshipNode and walk the relationship.
				if( getNumberOfRelationships() == 0 ) {
					if( getNumberOfRelationshipTypes() > 1 ) {
						LayoutNode rootNode = (LayoutNode)getLayoutNodes().get(0);
						
						if( startingNode != null )
							rootNode = getLayoutNode(startingNode);

						TreeLayout layout = new TreeLayout(getLayoutNodes(), 10, 10, 400, 400);
						layout.setRoot(rootNode);
						layout.setOrientation(TreeLayout.ORIENTATION_ROOT_TOP);
						layout.setFixedSpacing(true);
						layout.setFixedXSpacing(60);
						layout.setFixedYSpacing(60);
						layout.setUseObjectsSizes(true);
						layout.run();
						setFinalPositions();
					} else
						super.layout();
				} else 
					runRelationshipLayout();

				setBounds(new Rectangle(0, 0, getCurrentWidth(), getCurrentHeight()));
			} break;
            
			case LayoutHelper.NO_LINKS_LAYOUT: {
				super.layout();
			}
		}
	}
	
//	private void runSpringLayout() {
//		LayoutUtilities.runSpringLayout(this, 2);
//	}
	
	private void runRelationshipLayout() {
		createSubGroups();
		layoutSubGroups();
		
		LayoutUtilities.justifyAllToCorner(getLayoutNodes());
	}

	
	private LayoutNode getRelationshipNode(List nodeList) {
		Iterator iter = nodeList.iterator();
		LayoutNode nextLN = null;
		DiagramModelNode nextDMN = null;
		
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			nextDMN = nextLN.getModelNode();
			if( nextDMN instanceof RelationshipModelNode ) {
				return nextLN;
			}
		}
		
		return null;
	}
	
	private LayoutNode getRelationshipNode(List nodeList, DiagramModelNode startingNode ) {
		Iterator iter = nodeList.iterator();
		LayoutNode nextLN = null;
		DiagramModelNode nextDMN = null;
		
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			nextDMN = nextLN.getModelNode();
			if( nextDMN instanceof RelationshipModelNode && nextDMN.equals(startingNode) ) {
				return nextLN;
			}
		}
		
		return null;
	}
	
	
	// returns list of source layout nodes connected to some other layout node.
	public List getConnectedSourceNodes(LayoutNode someLayoutNode) {
		List sourceLayoutNodes = new ArrayList();
		if( someLayoutNode != null ) {
    		DiagramModelNode modelNode = someLayoutNode.getModelNode();
    		Iterator sourceConnIter = modelNode.getTargetConnections().iterator();
    		NodeConnectionModel nextConn = null;
    		DiagramModelNode nextDMN = null;
    		LayoutNode nextLN = null;
    		while( sourceConnIter.hasNext() ) {
    			nextConn = (NodeConnectionModel)sourceConnIter.next();
    			nextDMN = (DiagramModelNode)nextConn.getSourceNode();
    			nextLN = getLayoutNode(nextDMN);
    			if( nextLN != null && !sourceLayoutNodes.contains(nextLN))
    				sourceLayoutNodes.add(nextLN);
    		}
        }
		if( sourceLayoutNodes.isEmpty())
			return Collections.EMPTY_LIST;
			
		return sourceLayoutNodes;
		
	}
	
	// returns list of source layout nodes connected to some other layout node.
	public List getConnectedTargetNodes(LayoutNode someLayoutNode) {
		List targetLayoutNodes = new ArrayList();
        
		if( someLayoutNode != null ) {
    		DiagramModelNode modelNode = someLayoutNode.getModelNode();
    		Iterator targetConnIter = modelNode.getSourceConnections().iterator();
    		NodeConnectionModel nextConn = null;
    		DiagramModelNode nextDMN = null;
    		LayoutNode nextLN = null;
    		while( targetConnIter.hasNext() ) {
    			nextConn = (NodeConnectionModel)targetConnIter.next();
    			nextDMN = (DiagramModelNode)nextConn.getTargetNode();
    			nextLN = getLayoutNode(nextDMN);
    			if( nextLN != null && !targetLayoutNodes.contains(nextLN))
    			targetLayoutNodes.add(nextLN);
    		}
        }
		if( targetLayoutNodes.isEmpty())
			return Collections.EMPTY_LIST;
			
		return targetLayoutNodes;
	}
	
	private int getNumberOfRelationships() {
		List allNodes = new ArrayList(getLayoutNodes());
		Iterator iter = allNodes.iterator();
		LayoutNode nextLN = null;
		DiagramModelNode nextDMN = null;
		int nRelationships = 0;
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			nextDMN = nextLN.getModelNode();
			if( nextDMN instanceof RelationshipModelNode ) {
				nRelationships++;
			}
		}
		
		return nRelationships;
	}
	
	private int getNumberOfRelationshipTypes() {
		List allNodes = new ArrayList(getLayoutNodes());
		Iterator iter = allNodes.iterator();
		LayoutNode nextLN = null;
		DiagramModelNode nextDMN = null;
		int nRelationshipTypes = 0;
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			nextDMN = nextLN.getModelNode();
			if( nextDMN instanceof RelationshipTypeModelNode ) {
				nRelationshipTypes++;
			}
		}
		
		return nRelationshipTypes;
	}
	
	private void createSubGroups() {
		List allRemainingNodes = new ArrayList(getLayoutNodes());
		LayoutNode nextLN = null;

		LayoutNode nextRelationshipNode = null;
		
		if( startingNode != null ) 
			nextRelationshipNode = getRelationshipNode(allRemainingNodes, startingNode);
		else
			nextRelationshipNode = getRelationshipNode(allRemainingNodes);
			
		RelationshipLayoutSubGroup subgroup = null;

		int currentColumnId = 0;
		int currentRowId = 0;
		
		while( nextRelationshipNode != null && !allRemainingNodes.isEmpty() ) {
			// We found one.
			// now let's get the connected nodes also

			List sourceNodes = new ArrayList(getConnectedSourceNodes(nextRelationshipNode));
			List targetNodes = new ArrayList(getConnectedTargetNodes(nextRelationshipNode));

			List leftOverSourceNodes = new ArrayList(sourceNodes.size());
			List leftOverTargetNodes = new ArrayList(targetNodes.size());
			Iterator iter = sourceNodes.iterator();
			
			while( iter.hasNext() ) {
				nextLN = (LayoutNode)iter.next();
				if( allRemainingNodes.contains(nextLN) ) {
					leftOverSourceNodes.add(nextLN);
				}
			}
			iter = targetNodes.iterator();
			while( iter.hasNext() ) {
				nextLN = (LayoutNode)iter.next();
				if( allRemainingNodes.contains(nextLN) ) {
					leftOverTargetNodes.add(nextLN);
				}
			}
			
			
			List allSubgroupNodes = new ArrayList(sourceNodes.size() + targetNodes.size() + 1);
			allSubgroupNodes.addAll(leftOverSourceNodes);
			allSubgroupNodes.addAll(leftOverTargetNodes);
			allSubgroupNodes.add(nextRelationshipNode);
			subgroup = 
				new RelationshipLayoutSubGroup(
								allSubgroupNodes, 
								nextRelationshipNode, 
								leftOverSourceNodes, 
								leftOverTargetNodes );
								
			subgroup.setColumnId(currentColumnId);
			subgroup.setRowId(currentRowId);

			subgroup.layout();
			subGroups.add(subgroup);
				
			allRemainingNodes.removeAll(allSubgroupNodes);
			
			nextRelationshipNode = getRelationshipNode(allRemainingNodes);
			
			// increment the id to position the next subgroup in the appropriate column
			int columnInc = getColumnIncrement(nextRelationshipNode, sourceNodes, targetNodes);
				
			currentColumnId += columnInc;
			
			if( columnInc == 0 || rowColumnIsTaken(currentRowId, currentColumnId))
				currentRowId++;

		}
	}
	
	private int getColumnIncrement(LayoutNode relationshipNode, List sourceNodes, List targetNodes) {
		// need to find out if the increment is -1, 0, or +1
		LayoutNode nextLN = null;
		
		Iterator iter = sourceNodes.iterator();
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			List sourceLayoutNodes = getConnectedSourceNodes(nextLN);
			if( sourceLayoutNodes.contains(relationshipNode))
				return -1;
		}
		
		iter = sourceNodes.iterator();
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			List targetLayoutNodes = getConnectedTargetNodes(nextLN);
			if( targetLayoutNodes.contains(relationshipNode))
				return 0;
		}
		
		iter = targetNodes.iterator();
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			List sourceLayoutNodes = getConnectedSourceNodes(nextLN);
			if( sourceLayoutNodes.contains(relationshipNode))
				return 0;
		}
		
		iter = targetNodes.iterator();
		while( iter.hasNext() ) {
			nextLN = (LayoutNode)iter.next();
			List targetLayoutNodes = getConnectedTargetNodes(nextLN);
			if( targetLayoutNodes.contains(relationshipNode))
				return 1;
		}
		
		return 0;
	}
	
	private void layoutSubGroups() {
		// Let's just walk the subGroups and place them based on ColumnID and RowId 
		
		double maxWidth = getMaxSubGroupWidth() + 20;
		double maxHeight = getMaxSubGroupHeight() + 20;
		
		Iterator iter = subGroups.iterator();
		RelationshipLayoutSubGroup nextSG = null;
		double newX = 0;
		double newY = 0;
		while( iter.hasNext() ) {
			nextSG = (RelationshipLayoutSubGroup)iter.next();
			newX = nextSG.getColumnId()*maxWidth;
			newY = nextSG.getRowId()*maxHeight;
			nextSG.setPosition(newX, newY);
		}
		
		restackColumns();
	}
	
	private void restackColumns() {
		// Find lowest column value and keep walking 
//		int firstColumnId = getLowestColumnId();
//		int firstRowId = getLowestRowId(firstColumnId);
		
	}
	
	private double getMaxSubGroupWidth() {
		double maxWidth = 10;
		Iterator iter = subGroups.iterator();
		LayoutGroup nextGroup = null;
		while( iter.hasNext() ) {
			nextGroup = (LayoutGroup)iter.next();
			maxWidth = Math.max(nextGroup.getWidth(), maxWidth);
		}
		return maxWidth;
	}
	
	private double getMaxSubGroupHeight() {
		double maxHeight = 10;
		Iterator iter = subGroups.iterator();
		LayoutGroup nextGroup = null;
		while( iter.hasNext() ) {
			nextGroup = (LayoutGroup)iter.next();
			maxHeight = Math.max(nextGroup.getHeight(), maxHeight);
		}
		maxHeight += 2*yPadding;
		
		return maxHeight;
	}
	
	private boolean rowColumnIsTaken(int row, int column) {
		Iterator iter = subGroups.iterator();
		RelationshipLayoutSubGroup nextSG = null;
		while( iter.hasNext() ) {
			nextSG = (RelationshipLayoutSubGroup)iter.next();
			if( nextSG.getColumnId() == column && nextSG.getRowId() == row )
				return true;
		}
		
		return false;
	}
    
    public void layoutSingleRelationship(DiagramModelNode relationshipNode, Point startingPt) {
        LayoutNode relLayoutNode = getLayoutNode(relationshipNode);
        
        if( relLayoutNode != null )  {
            List sourceNodes = new ArrayList(getConnectedSourceNodes(relLayoutNode));
            List targetNodes = new ArrayList(getConnectedTargetNodes(relLayoutNode));
            List allSubgroupNodes = new ArrayList(sourceNodes.size() + targetNodes.size() + 1);
            allSubgroupNodes.addAll(sourceNodes);
            allSubgroupNodes.addAll(targetNodes);
            allSubgroupNodes.add(relLayoutNode);
            RelationshipLayoutSubGroup subgroup = 
                new RelationshipLayoutSubGroup(
                                allSubgroupNodes, 
                                relLayoutNode, 
                                sourceNodes, 
                                targetNodes );
                                
    
            subgroup.layout();
            subgroup.setPosition(startingPt);
            subgroup.setFinalPositions();
        }
    }
	
//	private RelationshipLayoutSubGroup getSubGroup(int row, int column) {
//		Iterator iter = subGroups.iterator();
//		RelationshipLayoutSubGroup nextSG = null;
//		while( iter.hasNext() ) {
//			nextSG = (RelationshipLayoutSubGroup)iter.next();
//			if( nextSG.getColumnId() == column && nextSG.getRowId() == row )
//				return nextSG;
//		}
//		
//		return null;
//	}
//	
//	private int getLowestColumnId() {
//		int lowestColumnId = 0;
//		Iterator iter = subGroups.iterator();
//		RelationshipLayoutSubGroup nextSG = null;
//		while( iter.hasNext() ) {
//			nextSG = (RelationshipLayoutSubGroup)iter.next();
//			lowestColumnId = Math.min(lowestColumnId, nextSG.getColumnId());
//		}
//		
//		return lowestColumnId;
//	}
//	
//	private int getLowestRowId(int column) {
//		int lowestRowId = -1;
//		Iterator iter = subGroups.iterator();
//		RelationshipLayoutSubGroup nextSG = null;
//		while( iter.hasNext() ) {
//			nextSG = (RelationshipLayoutSubGroup)iter.next();
//			if( nextSG.getColumnId() == column ) {
//				if( lowestRowId == -1) {
//					lowestRowId = nextSG.getRowId();
//				} else {
//					lowestRowId = Math.min(lowestRowId, nextSG.getRowId());
//				}
//			}
//		}
//		
//		return lowestRowId;
//	}
}
