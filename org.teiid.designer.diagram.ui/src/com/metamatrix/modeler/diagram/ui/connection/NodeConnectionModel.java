/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
