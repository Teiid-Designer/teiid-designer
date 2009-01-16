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

package com.metamatrix.modeler.relationship.ui.navigation.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.PointList;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.AbstractNodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;

/**
 * DiagramAssociation
 */
public class NavigationDiagramLink extends AbstractNodeConnectionModel {

	private NavigationModelNode sourceNode;
	private NavigationModelNode targetNode;

	private String sRouterStyle;

	/**
	 * Construct an instance of MappingLink
	 * 
	 */
	public NavigationDiagramLink(NavigationModelNode sourceNode, NavigationModelNode targetNode) {
		super();
		setSourceNode(sourceNode);
		setTargetNode(targetNode);
	}

	/**
	 * Construct an instance of MappingLink.
	 * 
	 */
	public NavigationDiagramLink(NavigationModelNode sourceNode, NavigationModelNode targetNode, String sName) {
		super();

		setSourceNode(sourceNode);
		setTargetNode(targetNode);
	}


	@Override
    public void setRouterStyle(String sRouterStyle) {

		if (this.sRouterStyle == null || !this.sRouterStyle.equals(sRouterStyle)) {

			this.sRouterStyle = sRouterStyle;
			// refresh the label layout manager

		}
	}

	@Override
    public int getRouterStyle() {
		return DiagramUiConstants.LinkRouter.DIRECT;
	}

	/**
	 * @return sourceNode
	 */
	@Override
    public Object getSourceNode() {
		return sourceNode;
	}

	/**
	 * @return targetNode
	 */
	@Override
    public Object getTargetNode() {
		return targetNode;
	}

	/**
	 * @param node
	 */
	@Override
    public void setSourceNode(Object node) {
		sourceNode = (NavigationModelNode)node;
	}

	/**
	 * @param node
	 */
	@Override
    public void setTargetNode(Object node) {
		targetNode = (NavigationModelNode)node;
	}

    
    /** 
     * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    @Override
    public void firePropertyChange(String prop,
                                   Object old,
                                   Object newValue) {
    }
    
	/**
	 * Creates all the labels and icons for the link.
	 */
	@Override
    public void layout(
		ConnectionAnchor ncaSourceAnchor,
		ConnectionAnchor ncaTargetAnchor,
		DiagramEditPart adepParentEditPart) {

	}
	@Override
    public void placeStereotypeAndName(int iSourceSide, int iTargetSide, PointList plConnectionPoints) {

	}
    
	@Override
    public String toString() {
            
		return new StringBuffer().append(" Navigation Link:") //$NON-NLS-1$
		.append(" Source = ").append(sourceNode.getName()) //$NON-NLS-1$
		.append(" Target = ").append(targetNode.getName()) //$NON-NLS-1$
		.toString();
	}
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDecoratorId()
	 */
	@Override
    public int getTargetDecoratorId() {
		return BinaryAssociation.DECORATOR_NONE;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getDecoratorId()
	 */
	@Override
    public int getSourceDecoratorId() {
		return BinaryAssociation.DECORATOR_NONE;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getLineStyle()
	 */
	@Override
    public int getLineStyle() {
		return BinaryAssociation.LINE_SOLID;
	}
	
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel#getToolTipString()
	 */
	@Override
    public List getToolTipStrings() {
		List newList = new ArrayList(1);
		newList.add(this.toString());
		return newList;
	}
}
