/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.navigation.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.teiid.designer.diagram.ui.connection.NodeConnectionModel;
import org.teiid.designer.relationship.ui.UiConstants;


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
	@Override
	public List getChildren() {
		return Collections.EMPTY_LIST;
	}

	/*
	 *  (non-Javadoc)
	 * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#setPosition(org.eclipse.draw2d.geometry.Point)
	 */
	@Override
	public void setPosition(Point position) {
		this.x = position.x;
		this.y = position.y;
        
		firePropertyChange(NavigationModelNodeProperties.LOCATION, null, position);
	}
    

	@Override
	public void setCenterXY(int newCenterX, int newCenterY) {
		int newX = newCenterX - this.getWidth() / 2;
		int newY = newCenterY - this.getHeight() / 2;

		this.x = newX;           
		this.y = newY;
        
		firePropertyChange(NavigationModelNodeProperties.LOCATION,null, new Point(newX, newY));
	}

	@Override
	public void setCenterX(int newCenterX) {
		int newX = newCenterX - this.getWidth() / 2;

		this.x = newX;           

		firePropertyChange(
			NavigationModelNodeProperties.LOCATION,
			null,
			new Point(newX, this.y));

	}

	@Override
	public void setCenterY(int newCenterY) {
		int newY = newCenterY - this.getHeight() / 2;
        
		this.y = newY;
        
		firePropertyChange(
			NavigationModelNodeProperties.LOCATION,
			null,
			new Point(this.x, newY));
	}

	@Override
	public Point getPosition() {
		return new Point(this.x, this.y);
	}

	@Override
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

	@Override
	public Dimension getSize() {
		return new Dimension(this.width, this.height);
	}

	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}

	@Override
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


	@Override
	public void update() {
		firePropertyChange(NavigationModelNodeProperties.PROPERTIES, null, null);
	}
    
	@Override
	public void update(String property) {
		firePropertyChange(property, null, null);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
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

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getCenterX() {
		return (this.x + this.width/2);
	}

	@Override
	public int getCenterY() {
		return (this.y + this.height/2);
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
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
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#addChild(org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode)
	 */
	@Override
	public void addChild(NavigationModelNode child) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#addChildren(java.util.List)
	 */
	@Override
	public void addChildren(List children) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#getModelObject()
	 */
	@Override
	public Object getModelObject() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#getParent()
	 */
	@Override
	public NavigationModelNode getParent() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#hasErrors()
	 */
	@Override
	public boolean hasErrors() {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#hasWarnings()
	 */
	@Override
	public boolean hasWarnings() {
		// XXX Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#removeChild(org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode)
	 */
	@Override
	public void removeChild(NavigationModelNode child) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#removeChildren(java.util.List)
	 */
	@Override
	public void removeChildren(List children) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#setChildren(java.util.List)
	 */
	@Override
	public void setChildren(List children) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#setModelObject(java.lang.Object)
	 */
	@Override
	public void setModelObject(Object modelObject) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#setParent(org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode)
	 */
	@Override
	public void setParent(NavigationModelNode parent) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#updateForErrorsAndWarnings()
	 */
	@Override
	public void updateForErrorsAndWarnings() {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#getLabelNode()
	 */
	@Override
	public NavigationModelNode getLabelNode() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#printBounds(java.lang.String)
	 */
	@Override
	public void printBounds(String prefix) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#addSourceConnection(org.teiid.designer.diagram.ui.connection.NodeConnectionModel)
	 */
	@Override
	public void addSourceConnection(NodeConnectionModel iConnection) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#addTargetConnection(org.teiid.designer.diagram.ui.connection.NodeConnectionModel)
	 */
	@Override
	public void addTargetConnection(NodeConnectionModel iConnection) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#getSourceConnections()
	 */
	@Override
	public Vector getSourceConnections() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#getTargetConnections()
	 */
	@Override
	public Vector getTargetConnections() {
		// XXX Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#removeSourceConnection(org.teiid.designer.diagram.ui.connection.NodeConnectionModel)
	 */
	@Override
	public void removeSourceConnection(NodeConnectionModel iConnection) {
		// XXX Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.ui.navigation.model.NavigationModelNode#removeTargetConnection(org.teiid.designer.diagram.ui.connection.NodeConnectionModel)
	 */
	@Override
	public void removeTargetConnection(NodeConnectionModel iConnection) {
		// XXX Auto-generated method stub

	}

}
