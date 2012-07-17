/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.model;

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
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.connection.NodeConnectionModel;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.diagram.DiagramEntity;


/**
 * @author blafond AbstractFreeDiagramModelNode This class provides a base class for any DiagramModelNode that doesn't have any
 *         need for an EObject or DiagramEntity. First cut is for Label objects on Associations
 */
abstract public class AbstractFreeDiagramModelNode implements DiagramModelNode, DiagramUiConstants {
    private int x;
    private int y;
    private int width;
    private int height;
    private String name;

    private DiagramModelNode parent;
    private Image firstOverlayImage;
    private Image secondOverlayImage;

    private boolean heightFixed = false;
    private int fixedHeight = DEFAULT_FIXED_HEIGHT;

    /**
     * Constructor
     */
    public AbstractFreeDiagramModelNode( String name ) {
        setName(name);
    }

    /**
     * Set the Model Object
     */
    @Override
	public void setModelObject( EObject object ) {
        // Does Nothing
    }

    /**
     * Set the Diagram Model Object
     */
    @Override
	public void setDiagramModelObject( DiagramEntity object ) {
        // Does nothing
    }

    /**
     * Get the Model Object
     */
    @Override
	public EObject getModelObject() {
        return null;
    }

    /**
     * Get the Diagram Model Object
     */
    @Override
	public DiagramEntity getDiagramModelObject() {
        return null;
    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#addChild(org.teiid.designer.diagram.ui.model.DiagramModelNode)
     */
    @Override
	public void addChild( DiagramModelNode child ) {
        // Does Nothing;
    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#removeChild(org.teiid.designer.diagram.ui.model.DiagramModelNode)
     */
    @Override
	public void removeChild( DiagramModelNode child,
                             boolean deleteDiagramEntity ) {
        // Does Nothing;
        return;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#addChildren(java.util.List)
     */
    @Override
	public void addChildren( List children ) {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#removeChildren(java.util.List)
     */
    @Override
	public void removeChildren( List children,
                                boolean deleteDiagramEntities ) {

    }

    /**
     * Set the List of Children
     */
    @Override
	public void setChildren( List childList ) {
        // Does Nothing;
    }

    /**
     * Get the List of Children
     */
    @Override
	public List getChildren() {
        // Does Nothing;
        return Collections.EMPTY_LIST;
    }

    @Override
	public void updateForChild( boolean isMove ) {
        fireStructureChange(DiagramUiConstants.DiagramNodeProperties.CHILDREN, "EMPTY"); //$NON-NLS-1$
    }

    /**
     * Get the number of Children
     */
    public int getNumberOfChildren() {
        // Does Nothing;
        return 0;
    }

    @Override
	public void setParent( DiagramModelNode parent ) {
        this.parent = parent;
    }

    @Override
	public DiagramModelNode getParent() {
        return this.parent;
    }

    @Override
	public void setPosition( Point position ) {
        this.x = position.x;
        this.y = position.y;
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.LOCATION, null, position);
    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#setCenterXY(int, int)
     */
    @Override
	public void setCenterXY( int newCenterX,
                             int newCenterY ) {
        int newX = newCenterX - this.getWidth() / 2;
        int newY = newCenterY - this.getHeight() / 2;

        this.x = newX;
        this.y = newY;

        firePropertyChange(DiagramNodeProperties.LOCATION, null, new Point(newX, newY));
    }

    @Override
	public void setCenterX( int newCenterX ) {
        int newX = newCenterX - this.getWidth() / 2;
        this.x = newX;
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.LOCATION, null, new Point(newX, this.y));
    }

    @Override
	public void setCenterY( int newCenterY ) {
        int newY = newCenterY - this.getHeight() / 2;
        this.y = newY;
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.LOCATION, null, new Point(this.x, newY));
    }

    @Override
	public Point getPosition() {
        Point posn = new Point(this.x, this.y);
        return posn;
    }

    @Override
	public void setSize( Dimension dimension ) {
        this.width = dimension.width;
        this.height = dimension.height;
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.SIZE, null, dimension);
    }

    @Override
	public Dimension getSize() {
        Dimension size = new Dimension(this.width, this.height);
        return size;
    }

    transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    @Override
	public void addPropertyChangeListener( PropertyChangeListener l ) {
        listeners.addPropertyChangeListener(l);
    }

    @Override
	public void firePropertyChange( String prop,
                                    Object old,
                                    Object newValue ) {
        listeners.firePropertyChange(prop, old, newValue);
    }

    // Used to alert the Edit Part that the children have been modified
    // and a refreshChildren() is needed.
    protected void fireStructureChange( String prop,
                                        Object child ) {
        listeners.firePropertyChange(prop, null, child);
    }

    public Object getEditableValue() {
        return this;
    }

    @Override
	public void removePropertyChangeListener( PropertyChangeListener l ) {
        listeners.removePropertyChangeListener(l);
    }

    @Override
	public void update() {
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.PROPERTIES, null, null);
    }

    @Override
	public void update( String property ) {
        firePropertyChange(property, null, null);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#update(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public void update( String prop,
                        String oldValue,
                        String newValue ) {
        firePropertyChange(prop, oldValue, newValue);
    }

    @Override
	public void setName( String name ) {
        if (this.name == null || !this.name.equals(name)) {
            String oldName = this.name;
            this.name = name;
            firePropertyChange(DiagramUiConstants.DiagramNodeProperties.NAME, oldName, name);
        }
    }

    @Override
	public String getName() {
        return this.name;
    }

    @Override
	public String getDisplayString() {
        return getName();
    }

    // public void setPath(String newString) {
    // path = newString;
    // }

    public String getPath() {
        return "Path"; //$NON-NLS-1$
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
        return (this.x + this.width / 2);
    }

    @Override
	public int getCenterY() {
        return (this.y + this.height / 2);
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
        result.append(", path: "); //$NON-NLS-1$
        result.append(getPath());
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

    @Override
	public Vector getSourceConnections() {
        return new Vector();
    }

    @Override
	public Vector getTargetConnections() {
        return new Vector();
    }

    @Override
	public void addSourceConnection( NodeConnectionModel iConnection ) {
        // Do Nothing
    }

    @Override
	public void addTargetConnection( NodeConnectionModel iConnection ) {
        // Do Nothing
    }

    @Override
	public void removeSourceConnection( NodeConnectionModel iConnection ) {
        // Do Nothing
    }

    @Override
	public void removeTargetConnection( NodeConnectionModel iConnection ) {
        // Do Nothing
    }

    @Override
	public List getAssociations( HashMap nodeMap ) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#updateAssociations()
     */
    @Override
	public void updateAssociations() {
        // Does nothing
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#hasErrors()
     */
    @Override
	public boolean hasErrors() {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#hasWarnings()
     */
    @Override
	public boolean hasWarnings() {
        return false;
    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#recoverObjectProperties()
     */
    @Override
	public void recoverObjectProperties() {
        // Default Impl
    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#wasLayedOut()
     */
    @Override
	public boolean wasLayedOut() {
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#getDependencies()
     */
    @Override
	public List getDependencies() {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#getFirstOverlayImage()
     */
    @Override
	public Image getFirstOverlayImage() {
        return firstOverlayImage;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#setFirstOverlayImage(org.eclipse.swt.graphics.Image)
     */
    @Override
	public void setFirstOverlayImage( Image image,
                                      String editorID ) {
        this.firstOverlayImage = image;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#getSecondOverlayImage()
     */
    @Override
	public Image getSecondOverlayImage() {
        return secondOverlayImage;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#setSecondOverlayImage(org.eclipse.swt.graphics.Image)
     */
    @Override
	public void setSecondOverlayImage( Image image,
                                       int positionIndex ) {
        this.secondOverlayImage = image;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#updateForErrorsAndWarnings()
     */
    @Override
	public void updateForErrorsAndWarnings() {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#getDiagram()
     */
    @Override
	public Diagram getDiagram() {
        // XXX Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#isReadOnly()
     */
    @Override
	public boolean isReadOnly() {
        return true;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#setReadOnly(boolean)
     */
    @Override
	public void setReadOnly( boolean readOnly ) {
        // XXX Auto-generated method stub
    }

    /**
     * @see org.teiid.designer.diagram.ui.model.DiagramModelNode#hideLocation()
     * @since 4.2
     */
    @Override
	public boolean hideLocation() {
        return false;
    }

    /**
     * @see org.teiid.designer.diagram.ui.model.DiagramModelNode#setHideLocation(boolean)
     * @since 4.2
     */
    @Override
	public void setHideLocation( boolean hideLocation ) {
    }

    @Override
	public String getEditorID() {
        return null;
    }

    @Override
	public int getFixedHeight() {
        return this.fixedHeight;
    }

    @Override
	public void setFixedHeight( int theFixedHeight ) {
        this.fixedHeight = theFixedHeight;
    }

    @Override
	public boolean isHeightFixed() {
        return this.heightFixed;
    }

    @Override
	public void setHeightFixed( boolean theHeightFixed ) {
        this.heightFixed = theHeightFixed;
    }
}
