/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.viewers.ILabelProvider;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationLink;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.NavigationEditor;
import com.metamatrix.modeler.relationship.ui.navigation.part.NavigationLayoutUtil;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NavigationDiagramModelFactory implements UiConstants {
//	private static final String KEY_RELATIONSHIP_DIAGRAM_NAME = "DiagramNames.relationshipDiagram"; //$NON-NLS-1$
//	private static final String THIS_CLASS = "RelationshipDiagramModelFactory"; //$NON-NLS-1$
	    
//	private String sNotationId;
//	private String errorMessage;
//	private NotationModelGenerator generator;
	    
	        
//	private static DiagramFactory diagramFactory;
	private NavigationEditor editor = null;
	private ILabelProvider labelProvider;
	    
//	static {
//		diagramFactory = DiagramFactory.eINSTANCE;
//	}
	/**
	 * Construct an instance of TransformModelFactory.
	 * 
	 */
	public NavigationDiagramModelFactory(ILabelProvider provider) {
		super();
		this.labelProvider = provider;
	}
    
	/**
	 * Create a DiagramModelNode.
	 */
    
	public NavigationModelNode createModel( Object baseObject ) {
        
		// Return null if the baseObject is not a com.metamatrix.metamodels.diagram.Diagram
		if( ! (baseObject instanceof Diagram) ) {
			return null;
		}
        
//		Diagram diagram = (Diagram)baseObject;
        
		NavigationDiagramNode diagramModelNode = null;

        
		// Create the DiagramNode 
		diagramModelNode = new NavigationDiagramNode(); 
            
//		diagramModelNode.addChildren(getRelationshipDiagramContents(diagramModelNode, diagram));

		return diagramModelNode;
	}

	protected List getRelationshipDiagramContents(DiagramModelNode diagramModelNode, Diagram relationshipDiagram ) {
		List diagramContents = new ArrayList();
		return diagramContents;
	}
	
	public void setContents(NavigationDiagramNode diagramModelNode, NavigationContext navContext) {
		// here's the meat!!!!!
		
		// now create RelationshipModelNodes and add to contents.
		FocusModelNode firstNode = null;
		
		NavigationNode focusNode = navContext.getFocusNode();
		String labelName = null;
		if( focusNode !=  null ) {
			labelName = labelProvider.getText(focusNode);
			firstNode = new FocusModelNode(focusNode, diagramModelNode, labelName, focusNode.getLabel());
			
			diagramModelNode.addChild(firstNode);
			
			if( firstNode.getLabelNode() != null ) {
				diagramModelNode.addChild(firstNode.getLabelNode());
			}
			
		}
		
		Iterator iter = navContext.getNavigationLinks().iterator();
		NavigationLink nextLink = null;
		int nLinks = navContext.getNavigationLinks().size();
		int iLink = 0;
		NavigationModelNode nextTargetNode = null;
		while( iter.hasNext() ) {
			nextLink = (NavigationLink)iter.next();
			nextTargetNode = addNavigationNode(diagramModelNode, navContext, nextLink, nLinks, iLink);
			if( firstNode != null && nextTargetNode != null )
				addNavigationDiagramLink(firstNode, nextTargetNode);
			
			iLink++;		
		}
	}
	
	private NavigationModelNode addNavigationNode(
			NavigationDiagramNode diagramModelNode, 
			NavigationContext navContext, 
			NavigationLink link,
			int nLinks,
			int iLink ) {
				
		NavigationContainerModelNode containerNode = null;
		List childNodes = navContext.getNodes(link);

		int viewDiameter = -10;

		if( getEditor() != null ) {
			int innerPad = 40;
			int outerPad = 30;
			
			if( viewDiameter < 0 ) 
				viewDiameter = NavigationLayoutUtil.getViewDiameter(getEditor().getViewer());
					
			Rectangle sizeAndLocation = NavigationLayoutUtil.getNextCircularNodePoint(nLinks, iLink, viewDiameter, innerPad, outerPad);
			String nextLabel =  navContext.getNonFocusNodeRole(link);
			containerNode = 
				new NavigationContainerModelNode(diagramModelNode, sizeAndLocation.getSize(), nextLabel, navContext.getTooltip(link));
			
			int containerDiameter = sizeAndLocation.getSize().width;
			
			NavigationNode nextNode = null;
			NavigationModelNode nextNMN = null;
			Iterator iter = childNodes.iterator();
			Rectangle nodeSizeAndLocation = null;
			int nNodes = childNodes.size();
			int iNode = 0;
			String toolTip = null;
			
			while( iter.hasNext() )  {
				nodeSizeAndLocation = NavigationLayoutUtil.getNextCircularNodePoint(nNodes, iNode, containerDiameter, 0, 10);
				nextNode = (NavigationNode)iter.next();
				toolTip = navContext.getTooltip(nextNode);
				nextNMN = new NonFocusModelNode(nextNode, diagramModelNode, null, nodeSizeAndLocation.getSize(), toolTip);
				containerNode.addChild(nextNMN);
				iNode++;
			}
			diagramModelNode.addChild(containerNode);
			if( containerNode.getLabelNode() != null ) {
				diagramModelNode.addChild(containerNode.getLabelNode());
			}
		}
		
		return containerNode;
//		if( childNodes.size() > 1 ) {	
//			if( getEditor() != null ) {
//
//				if( viewDiameter < 0 ) 
//					viewDiameter = NavigationLayoutUtil.getViewDiameter(getEditor().getViewer());
//					
//				Rectangle sizeAndLocation = NavigationLayoutUtil.getNextCircularNodePoint(nLinks, iLink, viewDiameter, 20);
//				NavigationContainerModelNode containerNode = new NavigationContainerModelNode(diagramModelNode, sizeAndLocation.getSize());
//				
//				NavigationNode nextNode = null;
//				NavigationModelNode nextNMN = null;
//				Iterator iter = childNodes.iterator();
//				while( iter.hasNext() )  {
//					nextNode = (NavigationNode)iter.next();
//					nextNMN = new NonFocusModelNode(nextNode, diagramModelNode, null);
//					containerNode.addChild(nextNMN);
//				}
//				diagramModelNode.addChild(containerNode);
//			}
//		} else {
//			Iterator iter = childNodes.iterator();
//			
//			NavigationNode nextNode = null;
//			NavigationModelNode nextNMN = null;
//			String labelName = null;
//			
//			while( iter.hasNext() ) {
//				nextNode = (NavigationNode)iter.next();
//				labelName = labelProvider.getText(nextNode);
//				nextNMN = new NonFocusModelNode(nextNode, diagramModelNode, labelName);
//				diagramModelNode.addChild(nextNMN);
//				diagramModelNode.addChild(nextNMN.getLabelNode());			
//			}
//		}
	}
	
	protected NavigationDiagramLink getTargetConnectionModel(NavigationModelNode focusNode, NavigationModelNode targetContainerNode) {
		NavigationDiagramLink association = new NavigationDiagramLink(focusNode, targetContainerNode);
		return association;
	}
	
	protected void addNavigationDiagramLink(NavigationModelNode focusNode, NavigationModelNode targetContainerNode) {
		NavigationDiagramLink association = getTargetConnectionModel(focusNode, targetContainerNode);
		
		((NavigationModelNode)association.getSourceNode()).addSourceConnection(association);
		((NavigationModelNode)association.getTargetNode()).addTargetConnection(association);
            
//		focusNode.updateAssociations();
//		targetContainerNode.updateAssociations();
	}

//	/**
//	 * Helper method to get the UmlAspect given an EObject
//	 */
//	public MetamodelAspect getUmlAspect(EObject eObject) {
//        
//		return DiagramUiPlugin.getDiagramAspectManager().getUmlAspect( eObject );   
//	}
	/**
	 * @param editor
	 */
	public void setEditor(NavigationEditor editor) {
		this.editor = editor;
	}

	/**
	 * @return
	 */
	public NavigationEditor getEditor() {
		return editor;
	}

}
