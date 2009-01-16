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

package com.metamatrix.modeler.mapping.ui.connection;

import java.util.ArrayList;
import java.util.List;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.AbstractNodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * DiagramAssociation
 */
public class MappingLink extends AbstractNodeConnectionModel {

    private DiagramModelNode sourceNode;
    private DiagramModelNode targetNode;

    private String sRouterStyle;

    /**
     * Construct an instance of MappingLink
     * 
     */
    public MappingLink(DiagramModelNode source, DiagramModelNode target) {
        super();
        setSourceNode(source);
        setTargetNode(target);
    }

    /**
     * Construct an instance of MappingLink.
     * 
     */
    public MappingLink(DiagramModelNode source, DiagramModelNode target, String sName) {
        super();

        setSourceNode(source);
        setTargetNode(target);
    }


    public void setSource(DiagramModelNode iSource) {
        sourceNode = iSource;
    }

    public void setTarget(DiagramModelNode iTarget) {
        targetNode = iTarget;
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
        sourceNode = (DiagramModelNode)node;
    }

    /**
     * @param node
     */
    @Override
    public void setTargetNode(Object node) {
        targetNode = (DiagramModelNode)node;
    }


    
    @Override
    public String toString() {
            
        return new StringBuffer().append(" MappingLink:") //$NON-NLS-1$
        .append(" Source = ").append(sourceNode.getName()) //$NON-NLS-1$
        .append(" Target = ").append(targetNode.getName()) //$NON-NLS-1$
        .toString();
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
