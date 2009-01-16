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

package com.metamatrix.modeler.diagram.ui.pakkage;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.connection.BlockConnectionRouter;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.layout.LayoutHelper;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDefaultEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.LabelEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.DiagramXYLayoutEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.LassoDragTracker;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.modeler.ui.IDiagramTypeEditPart;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * UmlPackageDiagramEditPart
 */
public class PackageDiagramEditPart extends AbstractDiagramEditPart
                                 implements DiagramUiConstants,
                                             IDiagramTypeEditPart {

    // router style
//    private String sCurrentRouterStyle = DiagramRouterStyles.MANHATTAN_ROUTER;
    private int routerStyle = DiagramEditorUtil.getCurrentDiagramRouterStyle();

    /** Singleton instance of MarqueeDragTracker. */
    static DragTracker m_dragTracker = null;

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {

        Figure newFigure = getFigureFactory().createFigure(getModel(), "umlDiagramNotation");  //$NON-NLS-1$
		setCurrentDiagramFont(ScaledFontManager.getFont());
        return newFigure;
    }
    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     * You need to tell how children nodes will be layed out...
    **/
    @Override
    protected void createEditPolicies() {
        setSelectablePart(false);
//        installEditPolicy(EditPolicy.CONTAINER_ROLE, new HiliteDndNodeSelectionEditPolicy());
//        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new HiliteDndNodeSelectionEditPolicy());
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramXYLayoutEditPolicy());
    }

    /**
     * This method is not mandatory to implement, but if you do not implement
     * it, you will not have the ability to rectangle-selects several figures...
    **/
    @Override
    public DragTracker getDragTracker(Request req) {
        // Unlike in Logical Diagram Editor example, I use a singleton because this 
        // method is Entered  >>  several time, so I prefer to save memory ; and it works!
        if (m_dragTracker == null) {
            m_dragTracker = new LassoDragTracker();
        }
        return m_dragTracker;
    }

    @Override
    public void layout(boolean layoutChildren) {
        if( !hasChildren() )
            return;
        
        if( layoutChildren) {
            EditPart canvasEditPart = getViewer().getContents();
            List canvasChildren = canvasEditPart.getChildren();
            Iterator iter = canvasChildren.iterator();
            EditPart nextEditPart = null;
            
            while (iter.hasNext()) {
                nextEditPart = (EditPart)iter.next();
                if ( nextEditPart instanceof DiagramEditPart ) {
                    ((DiagramEditPart)nextEditPart).layout(layoutChildren);
                }
            }
        }
//        super.layout(layoutChildren);

        
        // Check it's model node, if was layed out, don't re-layout!!
        
        if( !((DiagramModelNode)getModel()).wasLayedOut() ) {
            layout();
        } else {
            ((DiagramModelNode)getModel()).recoverObjectProperties();
        }
          
        // now process the Association Labels, if any:
        List arylSourceConnections = new ArrayList();
        if ( !arylSourceConnections.isEmpty() ) {
            Iterator itSourceConns = arylSourceConnections.iterator();
            
            while( itSourceConns.hasNext() ) {
                NodeConnectionModel ncmSourceConn = (NodeConnectionModel)itSourceConns.next();
                ncmSourceConn.setRouterStyle( LinkRouter.types[routerStyle] );                
                ncmSourceConn.layout();                                    
            }
        
        }
        
		if( layoutChildren) {
			refreshAssociationLabels();
		}
    }
    private boolean hasChildren() {
        if( getViewer().getContents().getChildren().size() > 0 )
            return true;
        return false;
    }
    
    private void refreshAssociationLabels() {
		EditPart canvasEditPart = getViewer().getContents();
		List canvasChildren = canvasEditPart.getChildren();
		Iterator iter = canvasChildren.iterator();
		EditPart nextEditPart = null;
        
		while (iter.hasNext()) {
			nextEditPart = (EditPart)iter.next();
			if ( nextEditPart instanceof AbstractDefaultEditPart ) {
				((AbstractDefaultEditPart)nextEditPart).refreshAllLabels();
			}
		}
    }
    
    @Override
    public void layout() {
        if( !hasChildren() )
            return;
            
		// Let's check the current selection.  If only one edit part is selected and it is a relationship
		// then we get it's model object and pass it it.
		DiagramModelNode selectedModelNode = null;
		if( getViewer().getSelectedEditParts().size() == 1 ) {
			Object selectedPart = getViewer().getSelectedEditParts().get(0);
			if( selectedPart != null &&
				selectedPart instanceof DiagramEditPart && 
				!(selectedPart.equals(this)) ) {
				EditPart sep = (EditPart)selectedPart;
				if( sep.getParent() == this )
					selectedModelNode = (DiagramModelNode)((DiagramEditPart)selectedPart).getModel();
			}
		}
        
        LayoutHelper layoutHelper = new LayoutHelper((DiagramModelNode)getModel(), selectedModelNode);
        layoutHelper.layoutAll();
//        layout(false);
        createOrUpdateAnchorsLocations(true);
        refreshAllLabels();
        refreshVisuals();

    }
    
    /* (non-JavaDoc)
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        String prop = evt.getPropertyName();

        if (prop.equals(DiagramNodeProperties.CHILDREN)) {
            refreshChildren();
            // Walk each child EP and tell to layout(true);
            layoutChildren();
            
        } else if (prop.equals(DiagramNodeProperties.SIZE)) {
            createOrUpdateAnchorsLocations(true);
            refreshAllLabels();
            refreshVisuals();
        } else if (prop.equals(DiagramNodeProperties.LOCATION)) {
            if( Util.isTraceEnabled(this.getClass())) {
                String debugMessage = "Location Property Change on EP = " + ((DiagramModelNode)getModel()).getName() + " Position = " + ((DiagramModelNode)getModel()).getPosition(); //$NON-NLS-2$ //$NON-NLS-1$
                Util.print(this.getClass(), debugMessage);
            }
            createOrUpdateAnchorsLocations(true);
            refreshAllLabels();
            refreshVisuals();
        } else if (prop.equals(DiagramNodeProperties.PROPERTIES)) {
            refreshVisuals();
            //            refreshChildren();
            //            layout(DiagramEditPart.LAYOUT_CHILDREN);
        } else if (prop.equals(DiagramNodeProperties.NAME)) {
            refreshName();
            refreshAllLabels();
            refreshVisuals();
        } else if(prop.equals(DiagramNodeProperties.ROUTER)) {
            notifyRouterChanged((String)evt.getOldValue(), (String)evt.getNewValue());
        }
    }
    
    private void layoutChildren() {
        Iterator iter = this.getChildren().iterator();
        EditPart nextEP = null;
        while( iter.hasNext() ) {
            nextEP = (EditPart)iter.next();
            if( nextEP instanceof DiagramEditPart) {
                ((DiagramEditPart)nextEP).layout(true);
            }
        }
    }
    
    /** 
     * Set the anchors location based on current figure's rectangle size.
    **/
    @Override
    public void createOrUpdateAnchorsLocations(boolean updateOtherEnds) {
        Iterator iter = this.getChildren().iterator();
        EditPart nextEP = null;
        while( iter.hasNext() ) {
            nextEP = (EditPart)iter.next();
            if( nextEP instanceof DiagramEditPart && !(nextEP instanceof LabelEditPart) ) {
                ((DiagramEditPart)nextEP).createOrUpdateAnchorsLocations(updateOtherEnds);
                ((AbstractDefaultEditPart)nextEP).refreshAllLabels();
            }
        }
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
    **/
    @Override
    protected void refreshVisuals() {
        ConnectionLayer cLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
        switch( routerStyle ) {
            case LinkRouter.ORTHOGONAL: {
                cLayer.setConnectionRouter(new BlockConnectionRouter(30));
            } break;
            case LinkRouter.MANUAL: {
                // These lines will enable the Bendpoint routing -------------------
                AutomaticRouter router = new FanRouter();
                router.setNextRouter(new BendpointConnectionRouter());
                cLayer.setConnectionRouter(router);
            } break;
            case LinkRouter.DIRECT:
            default: {
                cLayer.setConnectionRouter(new FanRouter());
            } break;
        }

    }
    
    /** 
     * Reset font from Font Managar and call layout on all diagram objects.
    **/
    @Override
    public void refreshFont(boolean refreshChildren) {
        // Diagram needs to also do a layout here
        super.refreshFont(refreshChildren);
        
        // But not at the diagram level
        
        if( hasChildren()) {
            EditPart canvasEditPart = getViewer().getContents();
            List canvasChildren = canvasEditPart.getChildren();
            Iterator iter = canvasChildren.iterator();
            EditPart nextEditPart = null;
            
            while (iter.hasNext()) {
                nextEditPart = (EditPart)iter.next();
                if ( nextEditPart instanceof DiagramEditPart ) {
                    ((DiagramEditPart)nextEditPart).layout(true);
                }
            }
            if( hasConnections())
				refreshAssociationLabels();
        }
    }
    
    public boolean hasConnections() {
        EditPart canvasEditPart = getViewer().getContents();
        Iterator iter = canvasEditPart.getChildren().iterator();
        DiagramEditPart nextEditPart = null;

        while (iter.hasNext()) {
            nextEditPart = (DiagramEditPart)iter.next();
            if( !nextEditPart.getSourceConnections().isEmpty() ||
                !nextEditPart.getTargetConnections().isEmpty() )
                return true;
        }
        return false;
    }
    
    @Override
    public void updateForPreferences() {
        RGB currentBkgdColor = 
            PreferenceConverter.getColor(
                DiagramUiPlugin.getDefault().getPreferenceStore(),
                PluginConstants.Prefs.Appearance.PACKAGE_BKGD_COLOR);
        this.getFigure().setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));

        ((PackageDiagramNode)getModel()).refreshAssociationLabels();
        
        refreshFont(true);
        
        // Let's check the router style
        
//        int newRouterStyle = DiagramUiUtilities.getCurrentRouterStyle();
//        if( newRouterStyle != routerStyle) {
//            routerStyle = newRouterStyle;
//            refreshVisuals();
//            // now update all connections
//            notifyRouterChanged();
//        }
        
    }
    
    private void notifyRouterChanged(String oldValue, String newValue) {

        int newType = DiagramLinkType.ORTHOGONAL;
        if( newValue != null ) {
            newType = DiagramLinkType.get(newValue).getValue();
        }
//        int newRouterStyle = ((Diagram)((DiagramModelNode)getModel()).getModelObject()).getLinkType().getValue();
//        if( newRouterStyle != routerStyle) {
            routerStyle = newType;
            refreshVisuals();
//        }
        
        List allSourceConnections = DiagramUiUtilities.getAllSourceConnections((DiagramModelNode)getModel());
        if( allSourceConnections != null && !allSourceConnections.isEmpty() ) {
            Iterator iter = allSourceConnections.iterator();
            Object nextObj = null;
            String routerType = LinkRouter.types[routerStyle];
            while( iter.hasNext() ) {
                nextObj = iter.next();
                if( nextObj instanceof NodeConnectionModel) {
                    ((NodeConnectionModel)nextObj).setRouterStyle(routerType);
                    ((NodeConnectionModel)nextObj).firePropertyChange(
                         DiagramNodeProperties.ROUTER,
                         oldValue, newValue);
                }
            }
        }
    }
}
