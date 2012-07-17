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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.connection.NodeConnectionModel;
import org.teiid.designer.diagram.ui.editor.DiagramEditorUtil;
import org.teiid.designer.diagram.ui.util.DiagramEntityAdapter;
import org.teiid.designer.diagram.ui.util.DiagramEntityManager;
import org.teiid.designer.diagram.ui.util.DiagramUiUtilities;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.diagram.DiagramEntity;


/**
 */
abstract public class AbstractDiagramModelNode implements DiagramModelNode, DiagramUiConstants {
    private boolean layoutState = false;

    private Vector m_sourceConnections = new Vector();
    private Vector m_targetConnections = new Vector();

    private DiagramEntity diagramEntity;
    private EObject modelObject;
    private List children = null;
    private DiagramModelNode parent;
    private Diagram diagram;
    private Dimension thisSize;
    private Point thisPosition;

    private boolean needsDiagramEntity = true;

    protected boolean errorState = false;
    protected boolean warningState = false;
    private Image firstOverlayImage;
    private String editorID = null;
    private Image secondOverlayImage;
    private boolean readOnly = false;
    private boolean hideLocationValue = false;
    private boolean heightFixed = false;
    private int fixedHeight = DEFAULT_FIXED_HEIGHT;

    /**
     * Constructor
     */
    public AbstractDiagramModelNode( Diagram diagramModelObject,
                                     EObject modelObject ) {
        this.modelObject = modelObject;
        this.diagram = diagramModelObject;

        this.thisPosition = new Point(0, 0);
        thisSize = new Dimension(100, 15);

        initialize(diagramModelObject, null);
        setErrorState();
    }

    /**
     * Constructor
     */
    public AbstractDiagramModelNode( Diagram diagramModelObject,
                                     EObject modelObject,
                                     Object secondObject ) {
        this.modelObject = modelObject;
        this.diagram = diagramModelObject;

        this.thisPosition = new Point(0, 0);
        thisSize = new Dimension(100, 15);

        initialize(diagramModelObject, secondObject);
        setErrorState();
    }

    /**
     * Constructor
     */
    public AbstractDiagramModelNode( EObject modelObject ) {
        this.modelObject = modelObject;
        this.needsDiagramEntity = false;

        this.thisPosition = new Point(0, 0);
        thisSize = new Dimension(100, 15);

        initialize(null, null);
        setErrorState();
    }

    /**
     * Constructor
     */
    public AbstractDiagramModelNode( Diagram diagramModelObject,
                                     EObject modelObject,
                                     boolean needsDiagramEntity ) {
        this.modelObject = modelObject;
        this.diagram = diagramModelObject;
        this.needsDiagramEntity = needsDiagramEntity;

        this.thisPosition = new Point(0, 0);
        thisSize = new Dimension(100, 15);

        initialize(diagramModelObject, null);
        setErrorState();
    }

    /**
     * Constructor
     */
    public AbstractDiagramModelNode( Diagram diagramModelObject,
                                     EObject modelObject,
                                     Object secondObject,
                                     boolean needsDiagramEntity ) {
        this.modelObject = modelObject;
        this.diagram = diagramModelObject;

        this.thisPosition = new Point(0, 0);
        thisSize = new Dimension(100, 15);

        initialize(diagramModelObject, secondObject);
        setErrorState();
    }

    protected void initialize( Diagram diagramModelObject,
                               Object secondObject ) {
        setReadOnly(DiagramUiUtilities.getReadOnlyState(diagramModelObject));

        if (needsDiagramEntity) {
            // If diagramModelObject == null, then we assume that the diagramEntity needs to be "transient".
            // In otherwords, have no parent...
            diagramEntity = findDiagramEntity(diagramModelObject, null);

            if (diagramEntity != null) {
                setLayoutState(DiagramModelNode.LAYOUT_TRUE);
                this.thisPosition.x = DiagramEntityAdapter.getXPosition(diagramEntity);
                this.thisPosition.y = DiagramEntityAdapter.getYPosition(diagramEntity);
                thisSize.width = DiagramEntityAdapter.getWidth(diagramEntity);
                thisSize.height = DiagramEntityAdapter.getHeight(diagramEntity);
            } else if (!isReadOnly()) {
                // Need to create a new diagram entity here.
                diagramEntity = DiagramUiUtilities.getDiagramEntity(getModelObject(), diagramModelObject);
            }
        }
    }

    @Override
	public Diagram getDiagram() {
        return this.diagram;
    }

    protected DiagramEntity findDiagramEntity( Diagram diagram,
                                               Object secondaryObject ) {
        DiagramEntity de = (DiagramEntity)DiagramEntityManager.getEntity(diagram, getModelObject());// null;
        return de;
    }

    public void setErrorState() {
        int state = DiagramUiConstants.NO_ERRORS;
        errorState = false;
        warningState = false;

        if (getModelObject() != null) {
            state = DiagramEditorUtil.getErrorState(getModelObject());
            if (state == DiagramUiConstants.HAS_ERROR) errorState = true;
            else if (state == DiagramUiConstants.HAS_WARNING) warningState = true;
        }

    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#recoverObjectProperties()
     */
    @Override
	public void recoverObjectProperties() {
        setInitialSize();
        setInitialPosition();
    }

    /**
     * Set the Model Object
     */
    @Override
	public void setModelObject( EObject object ) {
        modelObject = object;
    }

    /**
     * Set the Diagram Model Object
     */
    @Override
	public void setDiagramModelObject( DiagramEntity object ) {
        diagramEntity = object;
    }

    /**
     * Get the Model Object
     */
    @Override
	public EObject getModelObject() {
        return modelObject;
    }

    /**
     * Get the Diagram Model Object
     */
    @Override
	public DiagramEntity getDiagramModelObject() {
        return diagramEntity;
    }

    /**
     * Add a Child
     */
    @Override
	public void addChild( DiagramModelNode child ) {
        if (children == null) {
            children = new ArrayList();
        }
        child.setParent(this);
        children.add(child);
        fireStructureChange(DiagramNodeProperties.CHILDREN, child);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#addChildren(java.util.List)
     */
    @Override
	public void addChildren( List newChildren ) {
        if (newChildren != null && !newChildren.isEmpty()) {
            if (children == null) {
                children = new ArrayList();
            }

            DiagramModelNode nextChild = null;
            Iterator iter = newChildren.iterator();
            while (iter.hasNext()) {
                nextChild = (DiagramModelNode)iter.next();
                nextChild.setParent(this);
                children.add(nextChild);
            }
            fireStructureChange(DiagramNodeProperties.CHILDREN, this);
        }
    }

    /**
     * Remove a Child
     */
    @Override
	public void removeChild( DiagramModelNode child,
                             boolean deleteDiagramEntity ) {
        if (children == null) {
            return;
        }

        children.remove(child);
        fireStructureChange(DiagramNodeProperties.CHILDREN, child);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#removeChildren(java.util.List)
     */
    @Override
	public void removeChildren( List oldChildren,
                                boolean deleteDiagramEntities ) {
        if (oldChildren != null && !oldChildren.isEmpty()) {
            if (children == null) {
                return;
            }
            DiagramModelNode nextChild = null;
            Iterator iter = oldChildren.iterator();

            while (iter.hasNext()) {
                nextChild = (DiagramModelNode)iter.next();

                children.remove(nextChild);
            }
            fireStructureChange(DiagramNodeProperties.CHILDREN, this);
        }
    }

    @Override
	public void updateForChild( boolean isMove ) {
        fireStructureChange(DiagramNodeProperties.CHILDREN, "EMPTY"); //$NON-NLS-1$
    }

    /**
     * Set the List of Children
     */
    @Override
	public void setChildren( List childList ) {
        children = childList;
    }

    /**
     * Get the List of Children
     */
    @Override
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

    @Override
	public void setParent( DiagramModelNode parent ) {
        this.parent = parent;
    }

    @Override
	public DiagramModelNode getParent() {
        return this.parent;
    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#setPosition(org.eclipse.draw2d.geometry.Point)
     */
    @Override
	public void setPosition( Point position ) {
        if (position.x != thisPosition.x || position.y != thisPosition.y) {
            if (isWritableDiagramEntity(diagramEntity)) {
                DiagramEntityAdapter.setPosition(diagramEntity, position.x, position.y);
            }

            this.thisPosition.x = position.x;
            this.thisPosition.y = position.y;

            firePropertyChange(DiagramNodeProperties.LOCATION, null, position);
        }
    }

    /*
     * Private method to fire a position changed property event when the diagram component properties
     * are refreshed from the DiagramEntity
     */
    private void setInitialPosition() {
        if (diagramEntity != null) {
            this.thisPosition.x = DiagramEntityAdapter.getXPosition(diagramEntity);
            this.thisPosition.y = DiagramEntityAdapter.getYPosition(diagramEntity);
        }

        firePropertyChange(DiagramNodeProperties.LOCATION, null, getPosition());
    }

    @Override
	public void setCenterXY( int newCenterX,
                             int newCenterY ) {
        int newX = newCenterX - this.getWidth() / 2;
        int newY = newCenterY - this.getHeight() / 2;

        if (newX != thisPosition.x || newY != thisPosition.y) {
            if (isWritableDiagramEntity(diagramEntity)) {
                DiagramEntityAdapter.setXPosition(diagramEntity, newX);
                DiagramEntityAdapter.setYPosition(diagramEntity, newY);
            }

            this.thisPosition.x = newX;
            this.thisPosition.y = newY;

            firePropertyChange(DiagramNodeProperties.LOCATION, null, thisPosition);
        }
    }

    @Override
	public void setCenterX( int newCenterX ) {
        int newX = newCenterX - this.getWidth() / 2;

        if (newX != thisPosition.x) {
            if (isWritableDiagramEntity(diagramEntity)) {
                DiagramEntityAdapter.setXPosition(diagramEntity, newX);
            }

            this.thisPosition.x = newX;

            firePropertyChange(DiagramNodeProperties.LOCATION, null, thisPosition);
        }
    }

    @Override
	public void setCenterY( int newCenterY ) {
        int newY = newCenterY - this.getHeight() / 2;

        if (newY != thisPosition.y) {
            if (isWritableDiagramEntity(diagramEntity)) {
                DiagramEntityAdapter.setYPosition(diagramEntity, newY);
            }

            this.thisPosition.y = newY;

            firePropertyChange(DiagramNodeProperties.LOCATION, null, thisPosition);
        }
    }

    @Override
	public Point getPosition() {
        return this.thisPosition; // new Point(this.thisPosition);
    }

    @Override
	public void setSize( Dimension dimension ) {
        if (dimension.height != thisSize.height || dimension.width != thisSize.width) {
            thisSize.width = dimension.width;
            thisSize.height = dimension.height;
            firePropertyChange(DiagramNodeProperties.SIZE, null, dimension);
        }
    }

    /*
     * method to fire a size changed property event when the diagram component properties
     * are refreshed from the DiagramEntity
     */
    public void setInitialSize() {
        firePropertyChange(DiagramNodeProperties.SIZE, null, thisSize);
    }

    @Override
	public Dimension getSize() {
        return thisSize;
    }

    transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    @Override
	public void addPropertyChangeListener( PropertyChangeListener l ) {
        listeners.addPropertyChangeListener(l);
    }

    @Override
	public void removePropertyChangeListener( PropertyChangeListener l ) {
        listeners.removePropertyChangeListener(l);
    }

    @Override
	public void firePropertyChange( String prop,
                                    Object old,
                                    Object newValue ) {
        listeners.firePropertyChange(prop, old, newValue);
    }

    // Used to alert the Edit Part that the children have been modified
    // and a refreshChildren() is needed.
    public void fireStructureChange( String prop,
                                     Object child ) {
        listeners.firePropertyChange(prop, null, child);
    }

    public Object getEditableValue() {
        return this;
    }

    @Override
	public void update() {
        firePropertyChange(DiagramNodeProperties.PROPERTIES, null, null);
    }

    @Override
	public void update( String property ) {
        firePropertyChange(property, null, null);
    }

    @Override
	public void update( String prop,
                        String oldValue,
                        String newValue ) {
        firePropertyChange(prop, oldValue, newValue);
    }

    @Override
	public void setName( String name ) {
        if (isWritableDiagramEntity(diagramEntity)) {
            String oldName = "xxxXxxx"; //$NON-NLS-1$

            DiagramEntityAdapter.setName(diagramEntity, name);

            firePropertyChange(DiagramNodeProperties.NAME, oldName, name);
        }
    }

    @Override
	public String getName() {
        if (diagramEntity != null) return DiagramEntityAdapter.getName(diagramEntity);

        return null;
    }

    @Override
	public String getDisplayString() {
        return getName();
    }

    public String getPath() {
        return "Path"; //$NON-NLS-1$
    }

    public int getXPosition() {
        return this.thisPosition.x;
    }

    public int getYPosition() {
        return this.thisPosition.y;
    }

    @Override
	public int getX() {
        return this.thisPosition.x;
    }

    @Override
	public int getY() {
        return this.thisPosition.y;
    }

    @Override
	public int getCenterX() {
        return (this.thisPosition.x + thisSize.width / 2);
    }

    @Override
	public int getCenterY() {
        return (this.thisPosition.y + thisSize.height / 2);
    }

    @Override
	public int getWidth() {
        return thisSize.width;
    }

    @Override
	public int getHeight() {
        return thisSize.height;
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
        return m_sourceConnections;
    }

    @Override
	public Vector getTargetConnections() {
        return m_targetConnections;
    }

    @Override
	public void addSourceConnection( NodeConnectionModel iConnection ) {
        m_sourceConnections.addElement(iConnection);
    }

    @Override
	public void addTargetConnection( NodeConnectionModel iConnection ) {
        m_targetConnections.addElement(iConnection);
    }

    @Override
	public void removeSourceConnection( NodeConnectionModel iConnection ) {
        m_sourceConnections.removeElement(iConnection);
    }

    @Override
	public void removeTargetConnection( NodeConnectionModel iConnection ) {
        m_targetConnections.removeElement(iConnection);
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

        firePropertyChange(DiagramNodeProperties.CONNECTION, null, null);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#hasErrors()
     */
    @Override
	public boolean hasErrors() {
        return errorState;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#hasWarnings()
     */
    @Override
	public boolean hasWarnings() {
        return warningState;
    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#wasLayedOut()
     */
    @Override
	public boolean wasLayedOut() {
        return layoutState;
    }

    private void setLayoutState( boolean newState ) {
        this.layoutState = newState;
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
        this.editorID = editorID;
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

    @Override
	public boolean isReadOnly() {
        return readOnly;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#updateForErrorsAndWarnings()
     */
    @Override
	public void updateForErrorsAndWarnings() {
        setErrorState();
        firePropertyChange(DiagramNodeProperties.ERRORS, null, null);
    }

    private boolean isWritableDiagramEntity( DiagramEntity de ) {
        boolean isOK = false;
        if (de != null) {
            if (getModelObject() != null && !isReadOnly()) {
                isOK = true;
            }
        }
        return isOK;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.model.DiagramModelNode#setReadOnly(boolean)
     */
    @Override
	public void setReadOnly( boolean readOnly ) {
        this.readOnly = readOnly;
    }

    /**
     * @see org.teiid.designer.diagram.ui.model.DiagramModelNode#hideLocation()
     * @since 4.2
     */
    @Override
	public boolean hideLocation() {
        return hideLocationValue;
    }

    /**
     * @see org.teiid.designer.diagram.ui.model.DiagramModelNode#setHideLocation(boolean)
     * @since 4.2
     */
    @Override
	public void setHideLocation( boolean hideLocation ) {
        hideLocationValue = hideLocation;
    }

    @Override
	public String getEditorID() {
        return this.editorID;
    }

    public void setEditorID( String editorID ) {
        this.editorID = editorID;
    }

    @Override
	public int getFixedHeight() {
        return this.fixedHeight;
    }

    @Override
	public void setFixedHeight( int theFixedHeight ) {
        this.fixedHeight = theFixedHeight;

        if (children != null && !children.isEmpty()) {
            for (Iterator iter = children.iterator(); iter.hasNext();) {
                DiagramModelNode nextChildNode = (DiagramModelNode)iter.next();
                nextChildNode.setFixedHeight(theFixedHeight);
            }
        }
    }

    @Override
	public boolean isHeightFixed() {
        return this.heightFixed;
    }

    @Override
	public void setHeightFixed( boolean theHeightFixed ) {
        this.heightFixed = theHeightFixed;
        if (children != null && !children.isEmpty()) {
            for (Iterator iter = children.iterator(); iter.hasNext();) {
                DiagramModelNode nextChildNode = (DiagramModelNode)iter.next();
                nextChildNode.setHeightFixed(theHeightFixed);
            }
        }
    }
}
