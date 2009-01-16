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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.relationship.ui.UiConstants;

/**
 * @author mdrilling
 *
 * Abstract class for all Diagram Model Nodes.
 */
public class FreeNavigationModelNode implements NavigationModelNode, UiConstants {

	private String name;
	
    
	// Read-only values to hold geometric state.
	protected int x = 10;
	protected int y = 10;
	protected int width = 30;
	protected int height = 30;

	/**
	 * Constructor
	 */
	public FreeNavigationModelNode(String labelName) {
		super();
		this.name = labelName;
	}

	/**
	 * Get the List of Children
	 */
	public List getChildren() {
		return Collections.EMPTY_LIST;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setPosition(org.eclipse.draw2d.geometry.Point)
	 */
	public void setPosition(Point position) {
		this.x = position.x;
		this.y = position.y;
        
		firePropertyChange(NavigationModelNodeProperties.LOCATION, null, position);
	}
    

	public void setCenterXY(int newCenterX, int newCenterY) {
		int newX = newCenterX - this.getWidth() / 2;
		int newY = newCenterY - this.getHeight() / 2;

		this.x = newX;           
		this.y = newY;
        
		firePropertyChange(NavigationModelNodeProperties.LOCATION,null, new Point(newX, newY));
	}

	public void setCenterX(int newCenterX) {
		int newX = newCenterX - this.getWidth() / 2;

		this.x = newX;           

		firePropertyChange(
			NavigationModelNodeProperties.LOCATION,
			null,
			new Point(newX, this.y));

	}

	public void setCenterY(int newCenterY) {
		int newY = newCenterY - this.getHeight() / 2;
        
		this.y = newY;
        
		firePropertyChange(
			NavigationModelNodeProperties.LOCATION,
			null,
			new Point(this.x, newY));
	}

	public Point getPosition() {
		return new Point(this.x, this.y);
	}

	public void setSize(Dimension dimension) {
		this.width = dimension.width;
		this.height = dimension.height;
        
		firePropertyChange(NavigationModelNodeProperties.SIZE, null, dimension); 

	}

	/*
	 * Private method to fire a size changed property event when the diagram component properties
	 * are refreshed from the DiagramEntity
	 */
	public void setInitialSize() {
		firePropertyChange(NavigationModelNodeProperties.SIZE, null, getSize()); 
	}

	public Dimension getSize() {
		return new Dimension(this.width, this.height);
	}

	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue) {
		listeners.firePropertyChange(prop, old, newValue);
	}

	// Used to alert the Edit Part that the children have been modified
	// and a refreshChildren() is needed.
	protected void fireStructureChange(String prop, Object child) {
		listeners.firePropertyChange(prop, null, child);
	}


	public void update() {
		firePropertyChange(NavigationModelNodeProperties.PROPERTIES, null, null);
	}
    
	public void update(String property) {
		firePropertyChange(property, null, null);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDisplayString() {
		return getName();
	}

	public String getStereotype() {
		return "<<Stereotype>>"; //$NON-NLS-1$
	}

	public int getXPosition() {
		return this.x;
	}

	public int getYPosition() {
		return this.y;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getCenterX() {
		return (this.x + this.width/2);
	}

	public int getCenterY() {
		return (this.y + this.height/2);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	@Override
    public String toString() {
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: "); //$NON-NLS-1$
		result.append(getName());
		result.append(", sterotype: "); //$NON-NLS-1$
		result.append(getStereotype());
		result.append(", xPosition: "); //$NON-NLS-1$
		result.append(getXPosition());
		result.append(", yPosition: "); //$NON-NLS-1$
		result.append(getYPosition());
		result.append(", height: "); //$NON-NLS-1$
		result.append(getHeight());
		result.append(", width: "); //$NON-NLS-1$
		result.append(getWidth());
		result.append(')');
		return result.toString();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#addChild(com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode)
	 */
	public void addChild(NavigationModelNode child) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#addChildren(java.util.List)
	 */
	public void addChildren(List children) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#getModelObject()
	 */
	public Object getModelObject() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#getParent()
	 */
	public NavigationModelNode getParent() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#hasErrors()
	 */
	public boolean hasErrors() {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#hasWarnings()
	 */
	public boolean hasWarnings() {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#removeChild(com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode)
	 */
	public void removeChild(NavigationModelNode child) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#removeChildren(java.util.List)
	 */
	public void removeChildren(List children) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#setChildren(java.util.List)
	 */
	public void setChildren(List children) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#setModelObject(java.lang.Object)
	 */
	public void setModelObject(Object modelObject) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#setParent(com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode)
	 */
	public void setParent(NavigationModelNode parent) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#updateForErrorsAndWarnings()
	 */
	public void updateForErrorsAndWarnings() {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#getLabelNode()
	 */
	public NavigationModelNode getLabelNode() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#getBounds()
	 */
	public Rectangle getBounds() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#printBounds(java.lang.String)
	 */
	public void printBounds(String prefix) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#addSourceConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
	 */
	public void addSourceConnection(NodeConnectionModel iConnection) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#addTargetConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
	 */
	public void addTargetConnection(NodeConnectionModel iConnection) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#getSourceConnections()
	 */
	public Vector getSourceConnections() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#getTargetConnections()
	 */
	public Vector getTargetConnections() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#removeSourceConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
	 */
	public void removeSourceConnection(NodeConnectionModel iConnection) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#removeTargetConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
	 */
	public void removeTargetConnection(NodeConnectionModel iConnection) {
		// XXX Auto-generated method stub

	}

}
