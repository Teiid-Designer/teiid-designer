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

package com.metamatrix.modeler.relationship.ui.navigation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ide.IGotoMarker;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationDiagramModelFactory;
import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationDiagramNode;
import com.metamatrix.modeler.relationship.ui.navigation.part.NavigationDiagramEditPart;
import com.metamatrix.modeler.relationship.ui.navigation.part.NavigationDiagramPartFactory;
import com.metamatrix.modeler.relationship.ui.navigation.selection.NavigationSelectionHandler;

/**
 * @author BLaFond To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class NavigationEditor extends GraphicalEditor implements ILabelProviderListener, IGotoMarker {
    private NavigationDiagramModelFactory modelFactory;
    private NavigationDiagramNode diagramModelNode;
    private NavigationSelectionHandler selectionHandler;
    private ILabelProvider labelProvider;
    private NavigationView navigationView;

    /**
	 * 
	 */
    public NavigationEditor( ILabelProvider provider,
                             NavigationView navView ) {
        super();
        this.labelProvider = provider;
        this.navigationView = navView;
        setEditDomain(new DefaultEditDomain(this));
        modelFactory = new NavigationDiagramModelFactory(labelProvider);
        modelFactory.setEditor(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
     */
    @Override
    protected void initializeGraphicalViewer() {
        // XXX Auto-generated method stub
        FreeformGraphicalRootEditPart root = new FreeformGraphicalRootEditPart();

        getGraphicalViewer().setRootEditPart(root);
        getGraphicalViewer().setEditPartFactory(new NavigationDiagramPartFactory(navigationView.getNavigationHistory()));

        diagramModelNode = new NavigationDiagramNode();

        getGraphicalViewer().setContents(diagramModelNode);
        // getEditDomain().setActiveTool( getViewer().getContents().getDragTracker(null));
        layout();
    }

    public NavigationGraphicalViewer getViewer() {
        return (NavigationGraphicalViewer)getGraphicalViewer();
    }

    private void clearCurrentDiagram() {
        // Cleanup work??
        // Start with clearing all associations.
        diagramModelNode = null;

        ((NavigationGraphicalViewer)getGraphicalViewer()).deselectAll();
        resetRootEditPart();
    }

    private void resetRootEditPart() {
        FreeformGraphicalRootEditPart root = new FreeformGraphicalRootEditPart();

        getGraphicalViewer().setRootEditPart(root);
    }

    /**
     * Creates the GraphicalViewer on the specified <code>Composite</code>.
     * 
     * @param parent the parent composite
     */
    @Override
    protected void createGraphicalViewer( Composite parent ) {
        GraphicalViewer viewer = new NavigationGraphicalViewer(this);
        selectionHandler = new NavigationSelectionHandler(viewer);
        viewer.createControl(parent);
        setGraphicalViewer(viewer);
        configureGraphicalViewer();
        // hookGraphicalViewer();
        initializeGraphicalViewer();
    }

    public Control getControl() {
        return getGraphicalViewer().getControl();
    }

    public void refreshLayout() {
        layout();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {
        // XXX Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        // XXX Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    public void gotoMarker( IMarker marker ) {
        // XXX Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    @Override
    public boolean isDirty() {
        // XXX Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        // XXX Auto-generated method stub
        return false;
    }

    public void setContents( EObject eObject ) {
        if (!diagramModelNode.getChildren().isEmpty()) {
            List currentChildren = new ArrayList(diagramModelNode.getChildren());
            diagramModelNode.removeChildren(currentChildren);
        }
        if (eObject != null) {
            // MetamodelAspect someAspect = DiagramUiPlugin.getDiagramAspectManager().getUmlAspect( eObject );
            // DiagramModelNode childNode = new FocusModelNode(null, eObject, someAspect);
            // childNode.setPosition(new Point(10, 10) );
            // // childNode.setSize(new Dimension(100, 40));
            // diagramModelNode.addChild(childNode);
        }
    }

    public void setContents( NavigationContext context ) {
        NavigationDiagramEditPart ndep = (NavigationDiagramEditPart)getViewer().getContents();
        ndep.animateForwardNavigation(context.getFocusNode());
        NavigationNode originalFocusNode = ndep.getCurrentFocusNode();

        clearCurrentDiagram();

        diagramModelNode = new NavigationDiagramNode();
        if (modelFactory != null) {
            modelFactory.setContents(diagramModelNode, context);
            getGraphicalViewer().setContents(diagramModelNode);
            // getEditDomain().setActiveTool( getViewer().getContents().getDragTracker(null));
            layout();
            ndep = (NavigationDiagramEditPart)getViewer().getContents();
            if (originalFocusNode != null) ndep.animateBackNavigation(originalFocusNode);
            getViewer().clearAllSelections(true);

            getSelectionHandler().select(context.getFocusNode());
        }
    }

    /**
     * @return
     */
    public NavigationSelectionHandler getSelectionHandler() {
        return selectionHandler;
    }

    private void layout() {
        if (getGraphicalViewer().getContents() != null) ((NavigationDiagramEditPart)getGraphicalViewer().getContents()).layout();
    }

    public void setLabelProvider( ILabelProvider provider ) {
        this.labelProvider = provider;
    }

    public ILabelProvider getLabelProvider() {
        return this.labelProvider;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelProviderListener#labelProviderChanged(org.eclipse.jface.viewers.LabelProviderChangedEvent)
     */
    public void labelProviderChanged( LabelProviderChangedEvent event ) {
        // XXX Auto-generated method stub

    }

}
