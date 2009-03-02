/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.UiConstants;

/**
 * @author mdrilling
 *
 * Abstract class for all Diagram Model Nodes.
 */
abstract public class AbstractNavigationModelNode implements NavigationModelNode, UiConstants {
	String toolTip = null;

	private Object modelObject;
	protected List children = null;
	private NavigationModelNode parent;
	private String name;

	private LabelModelNode labelNode;

	private boolean errorState = false;
	private boolean warningState = false;

	private Vector m_sourceConnections = new Vector();
	private Vector m_targetConnections = new Vector();
	
	// Read-only values to hold geometric state.
	protected int x = 10;
	protected int y = 10;
	protected int width = 30;
	protected int height = 30;

	/**
	 * Constructor
	 */
	public AbstractNavigationModelNode(
		NavigationNode nNode,
		NavigationDiagramNode parentDiagramNode,
		String labelName) {
		super();
		this.modelObject = nNode;
		setParent(parentDiagramNode);
		setErrorState();
		if (labelName != null && labelName.length() > 0 && !(labelName.equals(" "))) //$NON-NLS-1$
			labelNode = new LabelModelNode(labelName);
	}

	/**
	 * Constructor
	 */
	public AbstractNavigationModelNode(
		NavigationNode nNode,
		NavigationDiagramNode parentDiagramNode,
		String labelName,
		String toolTip) {
		super();
		this.modelObject = nNode;
		this.toolTip = toolTip;
		setParent(parentDiagramNode);
		setErrorState();
		if (labelName != null && labelName.length() > 0 && !(labelName.equals(" "))) //$NON-NLS-1$
			labelNode = new LabelModelNode(labelName);
	}

	public void setErrorState() {
	}

	/**
	 * Set the Model Object
	 */
	public void setModelObject(Object object) {
		modelObject = object;
	}

	/**
	 * Get the Model Object
	 */
	public Object getModelObject() {
		return modelObject;
	}

	/**
	 * Add a Child
	 */
	public void addChild(NavigationModelNode child) {
		if (children == null) {
			children = new ArrayList();
		}
		child.setParent(this);
		children.add(child);

		fireStructureChange(NavigationModelNodeProperties.CHILDREN, child);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addChildren(java.util.List)
	 */
	public void addChildren(List newChildren) {
		if (newChildren != null && !newChildren.isEmpty()) {
			if (children == null) {
				children = new ArrayList();
			}

			NavigationModelNode nextChild = null;
			Iterator iter = newChildren.iterator();
			while (iter.hasNext()) {
				nextChild = (NavigationModelNode)iter.next();
				nextChild.setParent(this);
				children.add(nextChild);
			}
			fireStructureChange(NavigationModelNodeProperties.CHILDREN, this);
		}
	}

	/**
	 * Remove a Child
	 */
	public void removeChild(NavigationModelNode child) {
		if (children == null) {
			return;
		}

		children.remove(child);

		fireStructureChange(NavigationModelNodeProperties.CHILDREN, child);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#removeChildren(java.util.List)
	 */
	public void removeChildren(List oldChildren) {
		if (oldChildren != null && !oldChildren.isEmpty()) {
			if (children == null) {
				return;
			}
			NavigationModelNode nextChild = null;
			Iterator iter = oldChildren.iterator();

			while (iter.hasNext()) {
				nextChild = (NavigationModelNode)iter.next();

				children.remove(nextChild);
			}
			fireStructureChange(NavigationModelNodeProperties.CHILDREN, this);
		}
	}

	public void updateForChild(boolean isMove) {
		fireStructureChange(NavigationModelNodeProperties.CHILDREN, "EMPTY"); //$NON-NLS-1$
	}

	/**
	 * Set the List of Children
	 */
	public void setChildren(List childList) {
		children = childList;
	}

	/**
	 * Get the List of Children
	 */
	public List getChildren() {
		if (children == null || children.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return children;
	}

	/**
	 * Get the number of Children
	 */
	public int getNumberOfChildren() {
		if (children == null || children.isEmpty()) {
			return 0;
		}

		return children.size();
	}

	public void setParent(NavigationModelNode parent) {
		this.parent = parent;
	}

	public NavigationModelNode getParent() {
		return this.parent;
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

		firePropertyChange(NavigationModelNodeProperties.LOCATION, null, new Point(newX, newY));
	}

	public void setCenterX(int newCenterX) {
		int newX = newCenterX - this.getWidth() / 2;

		this.x = newX;

		firePropertyChange(NavigationModelNodeProperties.LOCATION, null, new Point(newX, this.y));

	}

	public void setCenterY(int newCenterY) {
		int newY = newCenterY - this.getHeight() / 2;

		this.y = newY;

		firePropertyChange(NavigationModelNodeProperties.LOCATION, null, new Point(this.x, newY));
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

	public Object getEditableValue() {
		return this;
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
		return (this.x + this.width / 2);
	}

	public int getCenterY() {
		return (this.y + this.height / 2);
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
	 * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#hasErrors()
	 */
	public boolean hasErrors() {
		return errorState;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#hasWarnings()
	 */
	public boolean hasWarnings() {
		return warningState;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#updateForErrorsAndWarnings()
	 */
	public void updateForErrorsAndWarnings() {
		setErrorState();
		firePropertyChange(NavigationModelNodeProperties.ERRORS, null, null);
	}

	/**
	 * @return
	 */
	public NavigationModelNode getLabelNode() {
		return labelNode;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode#getBounds()
	 */
	public Rectangle getBounds() {
		return new Rectangle(getPosition(), getSize());
	}

	public void printBounds(String prefix) {
		System.out.println(prefix + " MODEL Bounds = " + getBounds()); //$NON-NLS-1$
	}

	public String getToolTip() {
		return toolTip;
	}
	
	public Vector getSourceConnections() {
		return m_sourceConnections;
	}

	public Vector getTargetConnections() {
		return m_targetConnections;
	}

	public void addSourceConnection(NodeConnectionModel iConnection) {
		m_sourceConnections.addElement(iConnection);
	}

	public void addTargetConnection(NodeConnectionModel iConnection) {
		m_targetConnections.addElement(iConnection);
	}
    

	public void removeSourceConnection(NodeConnectionModel iConnection) {
		m_sourceConnections.removeElement(iConnection);
	}

	public void removeTargetConnection(NodeConnectionModel iConnection) {
		m_targetConnections.removeElement(iConnection);
	}
}
