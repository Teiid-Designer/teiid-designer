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

package com.metamatrix.modeler.diagram.ui.connection;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.DiagramLink;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;

/**
 * NodeConnectionModel
 */
public interface NodeConnectionModel {

    /**
     * @return sourceNode
     */
    Object getSourceNode();

    /**
     * @return targetNode
     */
	Object getTargetNode();

    /**
     * @param node
     */
    void setSourceNode(Object node);

    /**
     * @param node
     */
    void setTargetNode(Object node);

    void setName(String sName);

    /**
     * @param 
     */
    String getName();
    
    int getLineStyle();
    
    int getSourceDecoratorId();
    
	int getTargetDecoratorId();
	
	EObject getModelObject();
	
    List getLabelNodes();
    
    void updateLabels();
    
    List getToolTipStrings();

    void layout();

    void layout(
        ConnectionAnchor ncaSourceAnchor,
        ConnectionAnchor ncaTargetAnchor,
        DiagramEditPart adepParentEditPart);

    void setRouterStyle(String sRouterStyle);
    
    void setRouterStyle(int iRouterStyle);
    
    int getRouterStyle();

    public void placeStereotypeAndName(int iSourceSide, int iTargetSide, PointList plConnectionPoints);
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    public void removePropertyChangeListener(PropertyChangeListener l);
    
    public void firePropertyChange(String prop, Object old, Object newValue);
    
    public DiagramLink getDiagramLink();
    
    // New interface methods for Bendpoint logic.
    public List getBendpoints();
    
    public void insertBendpoint(int index, Bendpoint point);

    public void removeBendpoint(int index);

    public void setBendpoint(int index, Bendpoint point);

    public void setBendpoints(Vector points);
    
    public void refreshBendPoints();
    
    public void clearBendpoints();

}
