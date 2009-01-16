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

package com.metamatrix.modeler.diagram.ui.model;



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

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.util.DiagramEntityManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.diagram.ui.DebugConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * @author blafond
 * AbstractLocalDiagramModelNode
 */
abstract public class AbstractLocalDiagramModelNode implements DiagramModelNode, DiagramUiConstants {

    private Vector m_sourceConnections = new Vector();
    private Vector m_targetConnections = new Vector();

    private EObject modelObject;
    private List children = null;
    private DiagramModelNode parent;
    private Image firstOverlayImage;
    private Image secondOverlayImage;
    private String editorID;
	protected Diagram diagram;
	private boolean readOnly = false;
    
    private boolean errorState = false;
    private boolean warningState = false;
    
    private int x;
    private int y;
    protected int width;
    protected int height;
    private String name;
    
    private boolean heightFixed = false;
    private int fixedHeight = DEFAULT_FIXED_HEIGHT;
    
    /**
     * Constructor
     */
    public AbstractLocalDiagramModelNode ( EObject modelObject, String name ){
        this.modelObject = modelObject;
        setName(name);
    }

    /**
     * Set the Model Object
     */
    public void setModelObject(EObject object) {
        modelObject = object;

        setReadOnly(DiagramUiUtilities.getReadOnlyState(object));
    }
    
    /**
     * Set the Diagram Model Object
     */
    public void setDiagramModelObject(DiagramEntity object) {
        // Does nothing
    }
    
    /**
     * Get the Model Object
     */
    public EObject getModelObject() {
        return modelObject;
    }
    
    /**
     * Get the Diagram Model Object
     */
    public DiagramEntity getDiagramModelObject() {
        return null;
    }

    /**
     * Add a Child
     */
    public void addChild(DiagramModelNode child) {
        if( children == null ) {
            children = new ArrayList();
        }

        child.setParent(this);
        children.add(child);
        
        if ( Util.isDebugEnabled(DebugConstants.DIAGRAM_MODEL_NODE)) {
            Util.debug(DebugConstants.DIAGRAM_MODEL_NODE,
                "AbstractLocalDiagramModelNode.addChild() for  Model = " + getName() + " Child = " + child ); //$NON-NLS-2$ //$NON-NLS-1$
        }
        fireStructureChange(DiagramUiConstants.DiagramNodeProperties.CHILDREN, child);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addChildren(java.util.List)
     */
    public void addChildren(List newChildren) {
        if( newChildren != null && !newChildren.isEmpty() ) {
            if (children == null) {
                children = new ArrayList();
            }
            
            Object nextChild;
            Iterator iter = newChildren.iterator();
            while( iter.hasNext() ) {
                nextChild = iter.next();
                if( nextChild instanceof DiagramModelNode )
                    ((DiagramModelNode)nextChild).setParent(this);
                children.add(nextChild);
                
                if ( Util.isDebugEnabled(DebugConstants.DIAGRAM_MODEL_NODE)) {
                    Util.debug(DebugConstants.DIAGRAM_MODEL_NODE,
                    "AbstractLocalDiagramModelNode.addChildren() for  Model = " + getName() + " Child = " + nextChild ); //$NON-NLS-2$ //$NON-NLS-1$
                }
            }
            fireStructureChange(DiagramNodeProperties.CHILDREN, this);
        }
    }

    /**
     * Remove a Child
     */
    public void removeChild(DiagramModelNode child, boolean deleteDiagramEntity) {
        if( children == null ) {
            return;
        }
        Diagram diagram = child.getDiagram();
        
        DiagramEntity staleDE = child.getDiagramModelObject();
        if( staleDE != null )
			DiagramEntityManager.removeEntity(diagram, child.getModelObject());
			
        children.remove(child);
        
        if( deleteDiagramEntity && staleDE != null && !ModelObjectUtilities.isReadOnly(getModelObject()) ) {
            DiagramEntityManager.cleanUpDiagram(diagram);
            DiagramEntityManager.cleanDiagramEntities(diagram);
        }
        if ( Util.isDebugEnabled(DebugConstants.DIAGRAM_MODEL_NODE)) {
            Util.debug(DebugConstants.DIAGRAM_MODEL_NODE,
                "AbstractLocalDiagramModelNode.removeChild() from  Model = " + getName() + " Child = " + child ); //$NON-NLS-2$ //$NON-NLS-1$
        }
        fireStructureChange(DiagramUiConstants.DiagramNodeProperties.CHILDREN, child);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#removeChildren(java.util.List)
     */
    public void removeChildren(List oldChildren, boolean deleteDiagramEntities) {
        if( oldChildren != null && !oldChildren.isEmpty() ) {
            if (children == null) {
                return;
            }
            Diagram diagram = null;
            
            DiagramModelNode nextChild = null;
            Iterator iter = oldChildren.iterator();
            List removeList = new ArrayList(oldChildren.size());
            
            while( iter.hasNext() ) {
                nextChild = (DiagramModelNode)iter.next();
                DiagramEntity staleDE = nextChild.getDiagramModelObject();
                if( diagram == null )
                    diagram = nextChild.getDiagram();
                if( staleDE != null )
					DiagramEntityManager.removeEntity(diagram, nextChild.getModelObject());
					
                children.remove(nextChild);
                
                if( deleteDiagramEntities && staleDE != null ) {
					removeList.add(staleDE);
                }
                if ( Util.isDebugEnabled(DebugConstants.DIAGRAM_MODEL_NODE)) {
                    Util.debug(DebugConstants.DIAGRAM_MODEL_NODE,
                        "AbstractLocalDiagramModelNode.removeChildren() for  Model = " + getName() + " Child = " + nextChild ); //$NON-NLS-2$ //$NON-NLS-1$
                }
            }
            
            if( deleteDiagramEntities && !removeList.isEmpty() ) {
                DiagramEntityManager.cleanDiagramEntities(diagram);
				DiagramUiUtilities.deleteDiagramEntities(removeList, this);
            }
				
            fireStructureChange(DiagramNodeProperties.CHILDREN, this);
        }
    }

    public void updateForChild(boolean isMove) {
        fireStructureChange(DiagramUiConstants.DiagramNodeProperties.CHILDREN, "EMPTY"); //$NON-NLS-1$
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
        if( children == null || children.isEmpty() ) {
            return Collections.EMPTY_LIST;
        }
        return children;
    }

    /**
     * Get the number of Children
     */
    public int getNumberOfChildren() {
        if( children == null || children.isEmpty() ) {
            return 0;
        }
            
        return children.size();
    }
    
    public void setParent(DiagramModelNode parent) {
        this.parent = parent;
    }
    
    public DiagramModelNode getParent() {
        return this.parent;
    }
    public void setPosition(Point position) {
        this.x = position.x;
        this.y = position.y;
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.LOCATION, null, position);
    }
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setCenterXY(int, int)
     */
    public void setCenterXY(int newCenterX, int newCenterY) {
        int newX = newCenterX - this.getWidth() / 2;
        int newY = newCenterY - this.getHeight() / 2;

        this.x = newX;           
        this.y = newY;
        
        firePropertyChange(DiagramNodeProperties.LOCATION,null, new Point(newX, newY));
    }

    public void setCenterX(int newCenterX) {
        int newX = newCenterX - this.getWidth()/2;
        this.x = newX;
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.LOCATION, null, new Point( newX, this.y ));
    }
    

    public void setCenterY(int newCenterY) {
        int newY = newCenterY - this.getHeight()/2;
        this.y = newY;
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.LOCATION, null, new Point( this.x, newY));
    }

    public Point getPosition() {
        Point posn = new Point(this.x, this.y);
        return posn;
    }

    public void setSize(Dimension dimension) {
        this.width = dimension.width;
        this.height = dimension.height;
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.SIZE, null, dimension);
    }

    public Dimension getSize() {
        Dimension size = new Dimension(this.width, this.height);
        return size;
    }
    
    transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    

    public void addPropertyChangeListener(PropertyChangeListener l) {
        listeners.addPropertyChangeListener(l);
    }

    public void firePropertyChange(String prop, Object old, Object newValue) {
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


    public void removePropertyChangeListener(PropertyChangeListener l) {
        listeners.removePropertyChangeListener(l);
    }

    public void update() {
        firePropertyChange(DiagramUiConstants.DiagramNodeProperties.PROPERTIES, null, null);
    }
    
    public void update(String property) {
        firePropertyChange(property, null, null);
    }
    
    public void update(String prop, String oldValue, String newValue) {
        firePropertyChange(prop, oldValue, newValue);
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    
    public String getDisplayString() {
        return getName();
    }

//    public void setPath(String newString) {
//        path = newString;
//    }
    
    public String getPath() {
        return "Path"; //$NON-NLS-1$
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
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getHeight()
     */
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public String toString()
    {
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
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getSourceConnections()
     */
    public Vector getSourceConnections()
    {
      return m_sourceConnections;
    }
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getTargetConnections()
     */
    public Vector getTargetConnections()
    {
      return m_targetConnections;
    }
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addSourceConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
     */
    public void addSourceConnection(NodeConnectionModel iConnection)
    {
      m_sourceConnections.addElement(iConnection);
    }
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#addTargetConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
     */
    public void addTargetConnection(NodeConnectionModel iConnection)
    {
      m_targetConnections.addElement(iConnection);
    }
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#removeSourceConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
     */
    public void removeSourceConnection(NodeConnectionModel iConnection) {
        m_sourceConnections.removeElement(iConnection);
    }
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#removeTargetConnection(com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel)
     */
    public void removeTargetConnection(NodeConnectionModel iConnection) {
        m_targetConnections.removeElement(iConnection);
    }
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getAssociations()
     */
    public List getAssociations(HashMap nodeMap) {
        return Collections.EMPTY_LIST;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#updateAssociations()
     */
    public void updateAssociations() {
        // Does nothing
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

    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#recoverObjectProperties()
     */
    public void recoverObjectProperties(){
        //Default Impl
    }
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#wasLayedOut()
     */
    public boolean wasLayedOut() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getDependencies()
     */
    public List getDependencies() {
        return Collections.EMPTY_LIST;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getFirstOverlayImage()
     */
    public Image getFirstOverlayImage() {
        return firstOverlayImage;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setFirstOverlayImage(org.eclipse.swt.graphics.Image)
     */
    public void setFirstOverlayImage(Image image, String editorID) {
        this.firstOverlayImage = image;
        this.editorID = editorID;
    }
    
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#getSecondOverlayImage()
     */
    public Image getSecondOverlayImage() {
        return secondOverlayImage;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.model.DiagramModelNode#setSecondOverlayImage(org.eclipse.swt.graphics.Image)
     */
    public void setSecondOverlayImage(Image image, int positionIndex) {
        this.secondOverlayImage = image;
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
		return diagram;
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
	public void setReadOnly(boolean readOnly) {
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
    public void setHideLocation(boolean hideLocation) {
    }
    
    public String getEditorID() {
        return this.editorID;
    }
    public void setEditorID(String editorID) {
        this.editorID = editorID;
    }

    
    public int getFixedHeight() {
        //return DiagramModelNode.DEFAULT_FIXED_HEIGHT;
        return this.fixedHeight;
    }

    
    public void setFixedHeight(int theFixedHeight) {
        this.fixedHeight = theFixedHeight;
    }

    
    public boolean isHeightFixed() {
        return this.heightFixed;
    }

    
    public void setHeightFixed(boolean theHeightFixed) {
        this.heightFixed = theHeightFixed;
        if( children != null && !children.isEmpty() ) {
            for( Iterator iter = children.iterator(); iter.hasNext(); ) {
                DiagramModelNode nextChildNode = (DiagramModelNode)iter.next();
                nextChildNode.setHeightFixed(theHeightFixed);
            }
        }
    }
}


