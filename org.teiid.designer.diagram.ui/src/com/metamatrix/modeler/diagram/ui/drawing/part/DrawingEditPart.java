/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.drawing.DrawingFigureFactory;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory;
import com.metamatrix.modeler.diagram.ui.part.PropertyChangeManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;
import com.metamatrix.modeler.internal.diagram.ui.DebugConstants;

/**
 * DrawingEditPart
 */
public class DrawingEditPart extends AbstractGraphicalEditPart
    implements PropertyChangeListener, DiagramEditPart {
    private static DrawingFigureFactory drawingFigureFactory;
    private PropertyChangeManager changeManager;

    /**
     * Construct an instance of DrawingEditPart.
     * 
     */
    public DrawingEditPart() {
        super();
    }
    
    /* (non-JavaDoc)
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.PROPERTIES)) {
            refreshVisuals();
        }
    }
    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {
//        installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new ResizableEditPolicy());
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new DiagramNodeSelectionEditPolicy());
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        return null;
    }
    
    protected DrawingFigureFactory getFigureFactory() {
        if( drawingFigureFactory == null )
            drawingFigureFactory = new DrawingFigureFactory();
            
        return drawingFigureFactory;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#clearHiliting()
     */
    public void clearHiliting() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#clearSelections(boolean)
     */
    public void clearSelections(boolean clearSubSelections) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#createOrUpdateAnchorsLocations(boolean)
     */
    public void createOrUpdateAnchorsLocations(boolean updateOtherEnds) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getAnchorManager()
     */
    public AnchorManager getAnchorManager() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getDependencies()
     */
    public List getDependencies() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getDiagramFigure()
     */
    public DiagramFigure getDiagramFigure() {
        if (getFigure() instanceof DiagramFigure)
            return (DiagramFigure)getFigure();

        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getDiagramTypeId()
     */
    public String getDiagramTypeId() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getEditPart(com.metamatrix.modeler.diagram.ui.model.DiagramModelNode)
     */
    public DiagramEditPart getEditPart(DiagramModelNode someModelNode) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getEditPart(org.eclipse.emf.ecore.EObject)
     */
	 public EditPart getEditPart(EObject someModelObject, boolean linksAllowed) {
		 return null;
	 }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getEditPartFactory()
     */
    public DiagramEditPartFactory getEditPartFactory() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getModelObject()
     */
    public EObject getModelObject() {
        return ((DiagramModelNode)getModel()).getModelObject();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getNotationId()
     */
    public String getNotationId() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getPrimaryParent()
     */
    public DiagramEditPart getPrimaryParent() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getSelectionHandler()
     */
    public IDiagramSelectionHandler getSelectionHandler() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     */
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
     */
    public ConnectionAnchor getSourceConnectionAnchor(Request request) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
     */
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
     */
    public ConnectionAnchor getTargetConnectionAnchor(Request request) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    public void hiliteBackground(Color hiliteColor) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#isPrimaryParent()
     */
    public boolean isPrimaryParent() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#isResizable()
     */
    public boolean isResizable() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#isSelectablePart()
     */
    public boolean isSelectablePart() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#layout()
     */
    public void layout() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#layout(boolean)
     */
    public void layout(boolean layoutChildren) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshFont(boolean)
     */
    public void refreshFont(boolean refreshChildren) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshName()
     */
    public void refreshName() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#resizeChildren()
     */
    public void resizeChildren() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#selectPrimaryParent()
     */
    public void selectPrimaryParent() {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setAnchorManager(com.metamatrix.modeler.diagram.ui.connection.AnchorManager)
     */
    public void setAnchorManager(AnchorManager anchorManager) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setDiagramTypeId(java.lang.String)
     */
    public void setDiagramTypeId(String sDiagramTypeId) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setNotationId(java.lang.String)
     */
    public void setNotationId(String sNotationId) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setResizable(boolean)
     */
    public void setResizable(boolean canResize) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setSelectionHandler(com.metamatrix.modeler.diagram.ui.editor.DiagramSelectionHandler)
     */
    public void setSelectionHandler(IDiagramSelectionHandler selectionHandler) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#shouldHiliteBackground(java.util.List)
     */
    public boolean shouldHiliteBackground(List sourceEditParts) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#showSelected(boolean)
     */
    public void showSelected(boolean selected) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#updateContent()
     */
    public void updateContent() {

    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#updateModelLocation()
    **/
    public void updateModelPosition() {
        ((DiagramModelNode)getModel()).setPosition(
            new Point(getFigure().getBounds().x, getFigure().getBounds().y));
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#updateModelSize()
    **/
    public void updateModelSize() {
        ((DiagramModelNode)getModel()).setSize(getFigure().getSize());
    }
    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
    **/
    @Override
    protected void refreshVisuals() {
        Point loc = ((DiagramModelNode) getModel()).getPosition();
        Dimension size = ((DiagramModelNode) getModel()).getSize();
        Rectangle r = new Rectangle(loc, size);

        ((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), r);
        getFigure().repaint();
        if( DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_EDIT_PARTS)) { 
            String message = "SIZE = " + size; //$NON-NLS-1$
            DiagramUiConstants.Util.print(DebugConstants.DIAGRAM_EDIT_PARTS, message);
        }
    }
    
    /* (non-JavaDoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#activate()
     * Makes the EditPart aware to changes in the model
     * by adding itself to the model's list of listeners.
     */
    @Override
    public void activate() {
        if (isActive())
            return;
        super.activate();
        ((DiagramModelNode)getModel()).addPropertyChangeListener(this);
        if (getDiagramFigure() != null)
            getDiagramFigure().activate();
    }

    /* (non-JavaDoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#deactivate()
     * Makes the EditPart insensible to changes in the model
     * by removing itself from the model's list of listeners.
     */
    @Override
    public void deactivate() {
        if (!isActive())
            return;
        super.deactivate();
        ((DiagramModelNode)getModel()).removePropertyChangeListener(this);
        if (getDiagramFigure() != null)
            getDiagramFigure().deactivate();
    }

    /* (non-JavaDoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     * You must implement this method if you want you root model to have 
     * children!
    **/
    @Override
    protected List getModelChildren() {
        return Collections.EMPTY_LIST;
    }
    
    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#updateForPreferences()
     */
    public void updateForPreferences() {
        // Default does nothing
    }
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#shouldReveal()
	 */
	public boolean shouldReveal() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getCurrentDiagramFont()
	 */
	public Font getCurrentDiagramFont() {
		return null;
	}
    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#handleZoomChanged()
     * @since 4.2
     */
    public void handleZoomChanged() {
        // Default does nothing;
    }
    
    public PropertyChangeManager getChangeManager() {
        if( changeManager == null ) {
            changeManager = new PropertyChangeManager(this);
        }
        
        return changeManager;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#isUnderConstruction()
     * @since 5.0
     */
    public boolean isUnderConstruction() {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setUnderConstruction(boolean)
     * @since 5.0
     */
    public void setUnderConstruction(boolean theIsUnderConstruction) {
    }
    
    /**
     *  
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#constructionCompleted()
     * @since 5.0
     */
    public void constructionCompleted(boolean updateLinkedParts) {
        setUnderConstruction(false);
    }
    
    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshAllLabels(boolean)
     * @since 5.0
     */
    public void refreshAllLabels(boolean theForceRefresh) {
        // NO OP
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshAnchors(boolean)
     * @since 5.0
     */
    public void refreshAnchors(boolean updateOtherEnds) {
        this.createOrUpdateAnchorsLocations(updateOtherEnds);
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshChildren(boolean)
     * @since 5.0
     */
    public void refreshChildren(boolean theForceRefresh) {
        this.refreshChildren();
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshPath(boolean)
     * @since 5.0
     */
    public void refreshPath(boolean theForceRefresh) {
        // NO Op
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshSourceConnections(boolean)
     * @since 5.0
     */
    public void refreshSourceConnections(boolean theForceRefresh) {
        this.refreshSourceConnections();
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshTargetConnections(boolean)
     * @since 5.0
     */
    public void refreshTargetConnections(boolean theForceRefresh) {
        this.refreshTargetConnections();
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshVisuals(boolean)
     * @since 5.0
     */
    public void refreshVisuals(boolean theForceRefresh) {
        this.refreshVisuals();
    }
    
    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshChildren(boolean)
     * @since 5.0
     */
    public void resizeChildren(boolean theForceRefresh) {
        this.resizeChildren();
    }
}
