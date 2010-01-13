/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityAdapter;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;

/**
 * AbstractDrawingModelNode Nodes that reference persistent DiagramEntity's, but don't have a model object (EObject reference)
 * underneath them.
 */
public class DrawingModelNode implements DiagramModelNode, DiagramUiConstants {
    private DiagramEntity diagramEntity;
    private EObject modelObject;
    private DiagramModelNode parent;
    private boolean readOnly = false;

    private boolean heightFixed = false;
    private int fixedHeight = DEFAULT_FIXED_HEIGHT;

    /**
     * Construct an instance of AbstractDrawingModelNode. This constructor is used for "new" diagram nodes
     */
    public DrawingModelNode( DiagramModelNode parentDiagramNode,
                             DiagramEntity de ) {
        parent = parentDiagramNode;
        setReadOnly(DiagramUiUtilities.getReadOnlyState(parentDiagramNode));
        setDiagramModelObject(de);
        setModelObject(de);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addChild(com.metamatrix.modeler.diagram.ui.model.DiagramModelNode)
     */
    public void addChild( DiagramModelNode child ) {
        // Drawing objects have no children
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addChildren(java.util.List)
     */
    public void addChildren( List children ) {
        // Drawing objects have no children
    }

    transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener( PropertyChangeListener l ) {
        listeners.addPropertyChangeListener(l);
    }

    public void firePropertyChange( String prop,
                                    Object old,
                                    Object newValue ) {
        listeners.firePropertyChange(prop, old, newValue);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addSourceConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
     */
    public void addSourceConnection( NodeConnectionModel iConnection ) {
        // Currently can't connect to DrawingModelNode (maybe notes later?)
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addTargetConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
     */
    public void addTargetConnection( NodeConnectionModel iConnection ) {
        // Currently can't connect to DrawingModelNode (maybe notes later?)
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getAssociations()
     */
    public List getAssociations( HashMap nodeMap ) {
        return Collections.EMPTY_LIST; // Currently can't connect to DrawingModelNode (maybe notes later?)
    }

    public int getXPosition() {
        if (diagramEntity != null) return DiagramEntityAdapter.getXPosition(diagramEntity);
        return 0;
    }

    public int getYPosition() {
        if (diagramEntity != null) return DiagramEntityAdapter.getYPosition(diagramEntity);
        return 0;
    }

    public int getX() {
        if (diagramEntity != null) return DiagramEntityAdapter.getXPosition(diagramEntity);
        return 0;
    }

    public int getY() {
        if (diagramEntity != null) return DiagramEntityAdapter.getYPosition(diagramEntity);
        return 0;
    }

    public int getCenterX() {
        if (diagramEntity != null) {
            return (DiagramEntityAdapter.getXPosition(diagramEntity) + DiagramEntityAdapter.getWidth(diagramEntity) / 2);
        }
        return 0;
    }

    public int getCenterY() {
        if (diagramEntity != null) return (DiagramEntityAdapter.getYPosition(diagramEntity) + DiagramEntityAdapter.getHeight(diagramEntity) / 2);
        return 0;
    }

    public int getWidth() {
        if (diagramEntity != null) return DiagramEntityAdapter.getWidth(diagramEntity);
        return 0;
    }

    public int getHeight() {
        if (diagramEntity != null) return DiagramEntityAdapter.getHeight(diagramEntity);
        return 0;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getChildren()
     */
    public List getChildren() {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getDependencies()
     */
    public List getDependencies() {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getDiagramModelObject()
     */
    public DiagramEntity getDiagramModelObject() {
        return diagramEntity;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getDisplayString()
     */
    public String getDisplayString() {
        return "DiagramEntity"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getModelObject()
     */
    public EObject getModelObject() {
        return modelObject;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getName()
     */
    public String getName() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getParent()
     */
    public DiagramModelNode getParent() {
        return parent;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getPosition()
     */
    public Point getPosition() {
        if (diagramEntity != null) {
            return DiagramEntityAdapter.getPosition(diagramEntity);
        }
        return new Point(0, 0);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getSize()
     */

    public Dimension getSize() {
        if (diagramEntity != null) {
            return DiagramEntityAdapter.getSize(diagramEntity);
        }
        return new Dimension(10, 10);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getSourceConnections()
     */
    public Vector getSourceConnections() {
        return new Vector();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getTargetConnections()
     */
    public Vector getTargetConnections() {
        return new Vector();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#hasErrors()
     */
    public boolean hasErrors() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#hasWarnings()
     */
    public boolean hasWarnings() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#recoverObjectProperties()
     */
    public void recoverObjectProperties() {
        setInitialSize();
        setInitialPosition();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#removeChild(com.metamatrix.modeler.diagram.ui.model.DiagramModelNode)
     */
    public void removeChild( DiagramModelNode child,
                             boolean deleteDiagramEntity ) {
        // NO CHILDREN FOR DRAWING OBJECTS
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#removeChildren(java.util.List)
     */
    public void removeChildren( List children,
                                boolean deleteDiagramEntities ) {
        // NO CHILDREN
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener( PropertyChangeListener l ) {
        listeners.removePropertyChangeListener(l);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#removeSourceConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
     */
    public void removeSourceConnection( NodeConnectionModel iConnection ) {
        // NO Connections
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#removeTargetConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
     */
    public void removeTargetConnection( NodeConnectionModel iConnection ) {
        // NO Connections
    }

    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setCenterXY(int, int)
     */
    public void setCenterXY( int newCenterX,
                             int newCenterY ) {
        int newX = newCenterX - this.getWidth() / 2;
        int newY = newCenterY - this.getHeight() / 2;

        DiagramEntityAdapter.setXPosition(diagramEntity, newX);
        DiagramEntityAdapter.setYPosition(diagramEntity, newY);

        firePropertyChange(DiagramNodeProperties.LOCATION, null, new Point(newX, newY));
    }

    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setCenterX(int)
     */
    public void setCenterX( int newCenterX ) {
        if (diagramEntity != null) {
            int newX = newCenterX - this.getWidth() / 2;

            DiagramEntityAdapter.setXPosition(diagramEntity, newX);

            firePropertyChange(DiagramNodeProperties.LOCATION, null, new Point(newX,
                                                                               DiagramEntityAdapter.getYPosition(diagramEntity)));
        }
    }

    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setCenterY(int)
     */
    public void setCenterY( int newCenterY ) {
        if (diagramEntity != null) {
            int newY = newCenterY - this.getHeight() / 2;

            DiagramEntityAdapter.setXPosition(diagramEntity, newY);

            firePropertyChange(DiagramNodeProperties.LOCATION, null, new Point(DiagramEntityAdapter.getXPosition(diagramEntity),
                                                                               newY));
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setChildren(java.util.List)
     */
    public void setChildren( List children ) {
        // Does nothing
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setDiagramModelObject(com.metamatrix.metamodels.diagram.DiagramEntity)
     */
    public void setDiagramModelObject( DiagramEntity de ) {
        diagramEntity = de;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setModelObject(org.eclipse.emf.ecore.EObject)
     */
    public void setModelObject( EObject modelObject ) {
        this.modelObject = modelObject;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setName(java.lang.String)
     */
    public void setName( String newName ) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setParent(com.metamatrix.modeler.diagram.ui.model.DiagramModelNode)
     */
    public void setParent( DiagramModelNode parent ) {
        this.parent = parent;
    }

    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setPosition(org.eclipse.draw2d.geometry.Point)
     */
    public void setPosition( Point position ) {
        if (diagramEntity != null) {
            DiagramEntityAdapter.setPosition(diagramEntity, position.x, position.y);
            firePropertyChange(DiagramNodeProperties.LOCATION, null, position);
        }
    }

    /*
     * Private method to fire a position changed property event when the diagram component properties
     * are refreshed from the DiagramEntity
     */
    private void setInitialPosition() {
        if (diagramEntity != null) {
            firePropertyChange(DiagramNodeProperties.LOCATION, null, getPosition());
        }
    }

    public void setSize( Dimension dimension ) {
        if (diagramEntity != null) {
            DiagramEntityAdapter.setSize(diagramEntity, dimension.width, dimension.height);
            firePropertyChange(DiagramNodeProperties.SIZE, null, dimension);
        }
    }

    /*
     * Private method to fire a size changed property event when the diagram component properties
     * are refreshed from the DiagramEntity
     */
    public void setInitialSize() {
        if (diagramEntity != null) {
            firePropertyChange(DiagramNodeProperties.SIZE, null, getSize());
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#update()
     */
    public void update() {

    }

    public void update( String property ) {
        firePropertyChange(property, null, null);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#update(java.lang.String, java.lang.String, java.lang.String)
     */
    public void update( String prop,
                        String oldValue,
                        String newValue ) {
        firePropertyChange(prop, oldValue, newValue);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#updateAssociations()
     */
    public void updateAssociations() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#updateForChild()
     */
    public void updateForChild( boolean isMove ) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#wasLayedOut()
     */
    public boolean wasLayedOut() {
        return false;
    }

    /**
     * Sets the user text string
     * 
     * @param someString
     */
    public void setUserString( String someString ) {
        if (diagramEntity != null) {
            String oldString = DiagramEntityAdapter.getUserString(diagramEntity);

            DiagramEntityAdapter.setUserString(diagramEntity, someString);

            firePropertyChange(DiagramNodeProperties.NAME, oldString, someString);
        }
    }

    public String getUserString() {
        if (diagramEntity != null) return DiagramEntityAdapter.getUserString(diagramEntity);

        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getFirstOverlayImage()
     */
    public Image getFirstOverlayImage() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setFirstOverlayImage(org.eclipse.swt.graphics.Image)
     */
    public void setFirstOverlayImage( Image image,
                                      String editorID ) {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getFirstOverlayImage()
     */
    public Image getSecondOverlayImage() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setFirstOverlayImage(org.eclipse.swt.graphics.Image)
     */
    public void setSecondOverlayImage( Image image,
                                       int positionIndex ) {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#updateForErrorsAndWarnings()
     */
    public void updateForErrorsAndWarnings() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getDiagram()
     */
    public Diagram getDiagram() {
        // XXX Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#isReadOnly()
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setReadOnly(boolean)
     */
    public void setReadOnly( boolean readOnly ) {
        this.readOnly = readOnly;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#hideLocation()
     * @since 4.2
     */
    public boolean hideLocation() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setHideLocation(boolean)
     * @since 4.2
     */
    public void setHideLocation( boolean hideLocation ) {
    }

    public String getEditorID() {
        return null;
    }

    public int getFixedHeight() {
        return this.fixedHeight;
    }

    public void setFixedHeight( int theFixedHeight ) {
        this.fixedHeight = theFixedHeight;
    }

    public boolean isHeightFixed() {
        return this.heightFixed;
    }

    public void setHeightFixed( boolean theHeightFixed ) {
        this.heightFixed = theHeightFixed;
    }
}
