/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation.model;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
/**
 * @author blafond
 *
 * interface for all Diagram Model Nodes.
 */
public interface NavigationModelNode {
	// ----------------------------------
	// Child/Parent related methods.
	// ----------------------------------
    
	/**
	 * Returns list of DiagramModelNode children
	 * @return currentChildren List
	 */
	List getChildren();
    
	/**
	 * sets the parent DiagramModelNode for a give Node.
	 * @param parent DiagramModelNode
	 */
	void setParent(NavigationModelNode parent);
	/**
	 * returns the parent DiagramModelNode for a given node.
	 * @return parentNode DiagramModelNode
	 */
	NavigationModelNode getParent();
    
	/**
	 * Sets the list of children to the new input list.
	 * @param children
	 */
	void setChildren(List children);

	/**
	 * Adds child DiagramModelNode to this node
	 * @param child
	 */
	void addChild(NavigationModelNode child);
    
	/**
	 * Adds list of children to this diagram node
	 * @param children
	 */
	void addChildren(List children);
    
	/**
	 * Removes input child node from this node
	 * @param child
	 */
	void removeChild(NavigationModelNode child);
    
	/**
	 * Removes list of children from this node
	 * @param children
	 */
	void removeChildren(List children);
    
	// ----------------------------------
	// Size/Position related methods
	// ----------------------------------
    
	/**
	 * Sets the x,y position of the model node
	 * @param position
	 */
	void setPosition(Point position);
    
	/**
	 * Sets the w,h size of the model node
	 * @param size
	 */
	void setSize(Dimension size);
    
	/**
	 * Returns the current x,y position of the model node
	 * @return point
	 */
	Point getPosition();
    
	/**
	 * Returns the current x position of the model node
	 * @return x
	 */
	int getX();
    
	/**
	 * Returns the current y position of the model node
	 * @return y
	 */
	int getY();
    
	/**
	 * Returns the current x position of the geometric center of the model node
	 * @return centerX
	 */
	int getCenterX();
    
	/**
	 * Returns the current y position of the geometric center of the model node
	 * @return centerY
	 */
	int getCenterY();
    
	/**
	 * Set's the x position of the model node based on a center x value.
	 * This method also uses the current width of the model node.
	 * @param newX
	 */
	void setCenterX(int newX);
    
	/**
	 * Set's the y position of the model node based on a center y value.
	 * This method also uses the current height of the model node.
	 * @param newY
	 */
	void setCenterY(int newY);
    
	/**
	 * Set's the x and y position of the model node based on a center x and y values.
	 * This method also uses the current width and height of the model node.
	 * * @param newX
	 * @param newY
	 */
	void setCenterXY(int newX, int newY);
    
	/**
	 * Returns the current w,h size (Dimension) of the model node
	 * @return dimension
	 */
	Dimension getSize();
	
	Rectangle getBounds();
	
	void printBounds(String prefix);

	/**
	 * Returns the current width in pixels of the model node.
	 * @return width
	 */
	int getWidth();
    
	/**
	 * Returns the current height in pixels of the model node.
	 * @return height
	 */
	int getHeight();
    
	// ----------------------------------
	// Text related items
	// ----------------------------------
    
	/**
	 * Returns the current name of the model node
	 * @return name
	 */
	String getName();
    
	/**
	 * Sets the name of the model node.
	 * @param newName
	 */
	void setName(String newName);
    
	/**
	 * Allows model nodes to provide a displayed name different from their
	 * model object name. i.e. attributes with data-types.
	 * @return displayString
	 */
	String getDisplayString();
    
	/**
	 * Sets the EObject model object for the model node.
	 * @param modelObject
	 */
	void setModelObject(Object modelObject);
    
	/**
	 * Returns the current reference EObject model object
	 * @return modelObject
	 */
	Object getModelObject();
    
    
	// ----------------------------------
	// Property Listener methods
	// ----------------------------------
    
	/**
	 * Adds a property change listener to the model node.
	 * Usually this is the "EditPart" which is the controller for this particular node.
	 */
	void addPropertyChangeListener(PropertyChangeListener l) ;
    
	/**
	 * Removes the property change listener
	 * @param l
	 */
	void removePropertyChangeListener(PropertyChangeListener l);
    
	// ----------------------------------
	// Various utility methods.
	// ----------------------------------
    
	/**
	 * Interface method to allow generic update of the model to notify it's listeners that a property has
	 * changed.
	 */
	void update( );
    
	/**
	 * Interface method to allow generic update of the model to notify it's listeners that a specific property 
	 * has changed.
	 */
	void update(String property);

    
	/**
	 * Returns current error state of model object
	 * @return
	 */
	boolean hasErrors();
    
	/**
	 * Returns current warning state of model object
	 * @return
	 */
	boolean hasWarnings();
    
	/**
	 * 
	 */
	void updateForErrorsAndWarnings();
    
	NavigationModelNode getLabelNode();
    
	/**
	 * Returns a Vector list of the current source connection model nodes.
	 * @return sourceConnectionList
	 */
	Vector getSourceConnections();

	/**
	 * Returns a Vector list of the current target connection model nodes.
	 * @return targetConnectionList
	 */
	Vector getTargetConnections();

	/**
	 * Adds a source connection node to the connection list.
	 * @param iConnection
	 */
	void addSourceConnection(NodeConnectionModel iConnection);

	/**
	 * Adds a target connection node to the connection list.
	 * @param iConnection
	 */
	void addTargetConnection(NodeConnectionModel iConnection);
    
	/**
	 * Removes a source connection node from the connection list.
	 * @param iConnection
	 */
	void removeSourceConnection(NodeConnectionModel iConnection);

	/**
	 * Removes a target connection node from the connection list.
	 * @param iConnection
	 */
	void removeTargetConnection(NodeConnectionModel iConnection);
}
