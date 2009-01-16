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

package com.metamatrix.modeler.diagram.ui.part;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * DiagramEditPart provides an interface for all Metamatrix EditParts.
 * This interface is specialized to provide a standard set of methods coordinated with
 * DiagramFigure and DiagramModelNode interface methods to simplify coordinate of selection,
 * resizing, updates and other control-type functions.
 */
public interface DiagramEditPart extends NodeEditPart {
    static final boolean LAYOUT_CHILDREN = true;
    static final boolean DO_NOT_LAYOUT_CHILDREN = false;
    
    /**
     * Simple boolean method to provide state during the diagram construction process.
     * @return
     * @since 5.0
     */
    boolean isUnderConstruction();
    
    /**
     * Simple boolean method to set state during the diagram construction process.
     * @param isUnderConstruction
     * @since 5.0
     */
    void setUnderConstruction(boolean isUnderConstruction);
    
    /**
     *  
     * 
     * @since 5.0
     */
    void constructionCompleted(boolean updateLinkedParts);
    
    /**
     * Getter method whic returns a specific Figure type, i.e. DiagramFigure
     * @return diagramFigure
     */
    DiagramFigure getDiagramFigure();

    /**
     * This method is provided to allow the edit part to be the manager of the layout of it's children
     * and also to indicate to it's children that they should also call layout().
     * Through property changes, children may be added/removed (i.e. refreshChildren()) or things renamed.
     * If these changes occur to the model, then the edit part needs to communicate to it's children to layout
     * or update their figures, then update or layout it's figure.
     * @param layoutChildren
     */
    void layout(boolean layoutChildren);
    
    /**
     * This method is provided to allow the edit part to be the manager of the layout of it's children
     * and not affect the children (see layout(layoutChildren) method.
     * @param layoutChildren
     */
    void layout();

    /**
     * This method provides the edit part the ability to selectively and recursively clear selection within
     * the edit part hiearchy.
     * @param clearSubSelections
     */
    void clearSelections(boolean clearSubSelections);
    
    /**
     * This method provides the edit part the ability to tell it's edit parts to upate it's content.
     * Rename actions, add/remove child, change date-type, stereotype, whatever are all actions
     * that would require the edit parts to update their Figures.
     * Again, the EditPart is the controller, and when it receives a property change event, it has to tell it's
     * figure update it's content from the model object data?
     */
    void updateContent();
    
    /**
     * Used by the edit part to update the model location with current location of "figure"
     *
     */
    void updateModelPosition();
    
    /**
     * Used by the edit part to update the model size with current size of "figure"
     *
     */
    void updateModelSize();
    
    /**
     * Used by the edit part to update the figure for any changes in preferences
     *
     */
    void updateForPreferences();
    
    /**
     * Used by the edit part to access the underlying model object that the edit part, model, and figure
     * are based on or referenced to. This is the hook method to send back the 'real' selected item in a diagram.
     * @return modelObject;
     */
    EObject getModelObject();
    
    /**
     * Used by the edit part to force a recursive resize of all it's children.
     *
     */
    void resizeChildren();
    
    /**
     * Method to walk a edit part's ancestry, and select the primary parent.  This allows a 'classifier container', for
     * instance, to delegate it's selection to the 'classifier'.
     *
     */
    void selectPrimaryParent();
    
    /**
     * Method to walk a edit part's ancestry, and find the primary parent.  A primary parent is defined as the
     * edit part that actually exists as a child of the 'diagram'.
     * @return primaryParent DiagramEditPart;
     */
    DiagramEditPart getPrimaryParent();
    
    
    /**
     * Boolean method to indicate whether or not this EditPart exists as a direct child of a diagram.
     * @return isPrimary boolean;
     */
    boolean isPrimaryParent();
    
    
    /**
     * Boolean method to indicate whether or not this EditPart is selectable.
     * @return isSelectable boolean;
     */
    boolean isSelectablePart();
    /**
     * Convenience method to access the edit part's edit part factory.
     * @return diagramEditPartFactory
     */
    DiagramEditPartFactory getEditPartFactory();
    
    /**
     * 
     * Method used to locate sub-editpart or child/subchild of another given
     * an EObject
     * @param childModelObject
     */
    EditPart getEditPart(EObject childModelObject, boolean linksAllowed);
    
    /**
     * 
     * Method used to locate sub-editpart or child/subchild of another given
     * an DiagramModelNode
     * @param someModelNode
     */
    DiagramEditPart getEditPart(DiagramModelNode someModelNode);
    
    /**
     * 
     * Method used to locate dependent edit parts
     *  @return dependencyList;
     */
    List getDependencies();
    
    /**
     * A convenience method which uses the Root to obtain the EditPartViewer.
     * @throws NullPointerException if the root is not found
     * @return the EditPartViewer
     */
    EditPartViewer getViewer();
    
    /** Method used to locate get the EditPart's NotationID
     */
    String getNotationId();
    
    /** Method used to locate set the EditPart's NotationID
     * @param sNotationId
     */
    void setNotationId(String sNotationId);
    
    /** Method used to locate get the EditPart's NotationID
     */
    String getDiagramTypeId();
    
    /** Method used to locate set the EditPart's NotationID
     * @param sNotationId
     */
    void setDiagramTypeId(String sDiagramTypeId);
    
    PropertyChangeManager getChangeManager();
    
    void createOrUpdateAnchorsLocations(boolean updateOtherEnds);
    
    /**
     * Simple method designed to allow quick call to the edit part to refresh the "name"
     * object from the eObject and set the name of the diagram entity to the same, if it 
     * exists.
     *
     */
    void refreshName();
    
    /**
     * Simple method designed to allow quick call to the edit part to refresh the "font"
     * of any object.
     *
     */
    void refreshFont(boolean refreshChildren);
    
    void refreshSourceConnections(boolean forceRefresh);
    
    void refreshTargetConnections(boolean forceRefresh);
    
    void refreshVisuals(boolean forceRefresh);
    
    void refreshAllLabels(boolean forceRefresh);
    
    void refreshAnchors(boolean updateOtherEnds);
    
    void refreshPath(boolean forceRefresh);
    
    void refreshChildren(boolean forceRefresh);
    
    void resizeChildren(boolean forceRefresh);
    
    /**
     * Method to allow a call to edit parts to generically set a hilite backaground color.
     * In particular, UmlAttributes...
     * @param hiliteColor
     */
    void hiliteBackground(Color hiliteColor);
    
    /**
     * Method to allow a call to edit parts to generically clear all hiliting from this
     * EditPart and any children... recursively.
     * In particular, for use in dependency hiliting....
     * @param hiliteColor
     */
    void clearHiliting();
    
    /**
     * Method to allow a call to edit parts to generically tell the part to render it's figure in a select state.
     * In particular, UmlAttributes...
     * @param selected
     */
    void showSelected(boolean selected);
    
    /**
     * Method to determine whether an edit part should be hilited.
     * @return
     */
    boolean shouldHiliteBackground(List sourceEditParts);

    /**
     * Method to set edit part's AnchorManager
     * @return
     */
    void setAnchorManager(AnchorManager anchorManager);
    /**
     * Method to return edit part's AnchorManager
     * @return
     */
    AnchorManager getAnchorManager();
    
    /** Method used to return the EditPart's diagram selection handlerD
     */
    IDiagramSelectionHandler getSelectionHandler();
    
    /** Method used to set the EditPart's diagram selection handler
     * @param sNotationId
     */
    void setSelectionHandler(IDiagramSelectionHandler selectionHandler);
    
    /**
     * Set the resizable state of the diagram edit part.
     * Used primarily by the createChildEditPolicy() method of the XY Layout edit policy
     * @param canResize boolean
     */
    void setResizable(boolean canResize);
    
    /**
     * get the resizable state of the diagram edit part.
     * Used primarily by the createChildEditPolicy() method of the XY Layout edit policy
     * @return isResizable boolean
     */
    boolean isResizable();
    
    boolean shouldReveal();
    
	/**
	 * @return
	 */
	Font getCurrentDiagramFont();

    /**
     * method to give the edit part a chance to update itself based on the current 
     * zoom factor (i.e. not everything wants to scale with zoom) 
     * @since 4.2
     */
    void handleZoomChanged();
    
}
