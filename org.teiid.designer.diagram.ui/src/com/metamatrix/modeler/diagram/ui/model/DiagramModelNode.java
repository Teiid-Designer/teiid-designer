/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.model;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;

/**
 * @author blafond
 *
 * interface for all Diagram Model Nodes.
 */
public interface DiagramModelNode {
    
    public static final boolean LAYOUT_TRUE = true;
    public static final boolean LAYOUT_FALSE = false;
    public static final int DEFAULT_FIXED_HEIGHT = 200;
    public static final int CONTAINER_FIXED_HEIGHT = 100;
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
    void setParent(DiagramModelNode parent);
    /**
     * returns the parent DiagramModelNode for a given node.
     * @return parentNode DiagramModelNode
     */
    DiagramModelNode getParent();
    
    /**
     * Sets the list of children to the new input list.
     * @param children
     */
    void setChildren(List children);

    /**
     * Adds child DiagramModelNode to this node
     * @param child
     */
    void addChild(DiagramModelNode child);
    
    /**
     * Adds list of children to this diagram node
     * @param children
     */
    void addChildren(List children);
    
    /**
     * Removes input child node from this node
     * @param child
     */
    void removeChild(DiagramModelNode child, boolean deleteDiagramEntity);
    
    /**
     * Removes list of children from this node
     * @param children
     */
    void removeChildren(List children, boolean deleteDiagramEntities);
    
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
    void setModelObject(EObject modelObject);
    
    /**
     * Sets the DiagramEntity EObject reference for the model node.
     * This object is the object which maintains x, y, h, w, name and other properties.
     * This object may or may not be persisted to the model file base on the persistance
     * of the parent Diagram object.
     * @param diagramModelObject
     */
    void setDiagramModelObject(DiagramEntity diagramModelObject);
    
    /**
     * Returns the current reference EObject model object
     * @return modelObject
     */
    EObject getModelObject();
    
    /**
     * Returns the current diagram entity object
     * @return diagramEntity
     */
    DiagramEntity getDiagramModelObject();
    
	/**
	 * Returns list of Diagram
	 * @return node's diagram
	 */
	Diagram getDiagram();
	
	/**
	 * Returns current writable state of node
	 * @return
	 */
	boolean isReadOnly();
	
	/**
	 * set read 0nly state of diagram node
	 * @param readOnly
	 */
	void setReadOnly(boolean readOnly);
    
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
     * Interface method to allow generic update of the model and its children based on a change in the
     * child structure of this node.
     * @param isMove provides a way to distinguish between an add/remove and a "move" change.
     */
    void updateForChild(boolean isMove);
    
    /**
     * Interface method to allow generic update of the model to notify it's listeners that a property has
     * changed.
     */
    void update( );
    
    /**
     * Interface method to allow generic update of the model to notify it's listeners that a specific property 
     * has changed.
     * @param property
     * @since 4.2
     */
    void update(String property);
    
    /**
     * Interface method to allow generic update of the model to notify it's listeners that a specific property 
     * has changed.
     * @param property
     * @param oldValue
     * @param newValue
     * @since 4.2
     */
    void update(String property, String oldValue, String newValue);

    /**
     * Interface method to allow generic update of the model to notify it's listeners that associations have
     * changed.
     */
    void updateAssociations();
    
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

    /**
     * Interface method to generically ask the model node for any association objects.
     * @return associations
     */
    List getAssociations(HashMap nodeMap);
    
    /**
     * 
     * Method used to locate dependent edit parts
     *  @return dependencyList;
     */
    List getDependencies();
    
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
    
    /**
     * Generic method to tell the model node to recover it's properties stored in the diagramEntity
     * and fire the appropriate property change events so the visual objects are notified and display
     * the correct values.
     */
    void recoverObjectProperties();
    
    /**
     * Generic method which can be called by the EditPart (controller) to ask the node if it has been layed
     * out or not.  This typcally means that the diagramEntity's x,y values are zero or negative.
     */
    boolean wasLayedOut();
    
    /**
     * This method provides a generic method to retrieve an overlay image for a specific DiagramModelNode.
     * How this image is displayed and where is up to the Figure's implementation.
     * @return
     */
    Image getFirstOverlayImage();
    
    /**
     * This method provides a generic method to set the value of an overlay image for a specific DiagramModelNode.
     * @param image
     */
    void setFirstOverlayImage(Image image, String editorID);
    
    /**
     * This method provides a generic method to retrieve an overlay image for a specific DiagramModelNode.
     * How this image is displayed and where is up to the Figure's implementation.
     * @return
     */
    Image getSecondOverlayImage();
    
    /**
     * This method provides a generic method to set the value of an overlay image for a specific DiagramModelNode.
     * @param image
     * @param positionIndex - See DiagramUiConstants.Position for valid values.
     */
    void setSecondOverlayImage(Image image, int positionIndex);
    
    /**
     * Simple boolean value which can be accessed to override the showing of the path or location in a diagram component.
     * Mainly used for classifiers 
     * @return
     * @since 4.2
     */
    boolean hideLocation();
    /**
     * Setter for DiagramModelNode hide location method. 
     * @param hideLocation
     * @since 4.2
     */
    void setHideLocation(boolean hideLocation);
    
    /**
     * String ID that diagram figures can use to implement an edit button on the object. If the editorID is non-null
     * the ModelEditorManager.edit(eObject, editorID) method is called
     * Mainly used for classifiers 
     * @return editorID string
     * @since 4.2
     */
    String getEditorID();

    /**
     * 
     * @param prop
     * @param old
     * @param newValue
     */
    void firePropertyChange(String prop, Object old, Object newValue);

    /**
     * getter method returns the fixed height value for this diagram model node. see isHeightFixed() 
     * @return fixedHeight integer in pixels
     * @since 5.0.2
     */
    int getFixedHeight();

    /**
     * sets the fixed height value for a diagram model node 
     * @param theFixedHeight in pixels
     * @since 5.0.2
     */
    void setFixedHeight(int theFixedHeight);
    
    /**
     * returns whether or not a diagram model node is a fixed height node
     * @return
     * @since 5.0.2
     */
    boolean isHeightFixed();
    
    /**
     * sets the isHeightFixed value for a diagram model node. 
     * @param theHeightFixed
     * @since 5.0.2
     */
    void setHeightFixed(boolean theHeightFixed);
}
