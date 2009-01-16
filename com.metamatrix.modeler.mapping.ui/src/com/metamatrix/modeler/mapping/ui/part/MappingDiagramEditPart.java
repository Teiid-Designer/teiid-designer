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

package com.metamatrix.modeler.mapping.ui.part;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlClassifierEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractFreeEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.ExpandableDiagram;
import com.metamatrix.modeler.diagram.ui.util.DiagramXYLayoutEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.LassoDragTracker;
import com.metamatrix.modeler.mapping.ui.DebugConstants;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramController;
import com.metamatrix.modeler.mapping.ui.model.MappingDiagramNode;
import com.metamatrix.modeler.mapping.ui.model.MappingExtentNode;
import com.metamatrix.modeler.mapping.ui.util.MappingUiUtil;
import com.metamatrix.modeler.ui.IDiagramTypeEditPart;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * TransformationDiagramEditPart
 */

public class MappingDiagramEditPart extends AbstractDiagramEditPart 
                                  implements ExpandableDiagram,
                                              IDiagramTypeEditPart {

    // ===========================================================================================================================
    // Static Variables

    /** Singleton instance of MarqueeDragTracker. */
    private static DragTracker m_dragTracker = null;

    // ===========================================================================================================================
    // Variables

    private String sCurrentRouterStyle = DiagramUiConstants.DiagramRouterStyles.FAN_ROUTER;
    // private static int layout_ExecutionsCount = 0;
    private boolean bLayoutInProgress = false;

    // private List lstTreeOrderedMappingClassList;

    // ===========================================================================================================================
    // Methods

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#addChildVisual(org.eclipse.gef.EditPart, int)
     * @since 5.0
     */
    @Override
    protected void addChildVisual(EditPart childEditPart,
                                  int index) {
        IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
        Object obj = childEditPart.getModel();
        if (obj instanceof DiagramModelNode) {
            DiagramModelNode model = (DiagramModelNode)obj;
            obj = model.getModelObject();
            if (obj instanceof MappingClass && ((MappingClass)obj).isRecursive() && child instanceof UmlClassifierFigure) {
                if( MappingUiUtil.getCurrentTreeMappingAdapter() != null ) {
                    obj = MappingUiUtil.getCurrentTreeMappingAdapter().getMappingClassLocation((MappingClass)obj);
                    if (obj instanceof XmlDocumentNode && ((XmlDocumentNode)obj).isExcludeFromDocument()) {
                        ((UmlClassifierFigure)child).getEditButton().setEnabled(false);
                    }
                }
            }
        }
        getContentPane().add(child, index);
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {

        Figure newFigure = getFigureFactory().createFigure(getModel());
        setCurrentDiagramFont(ScaledFontManager.getFont());
        return newFigure;
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies() You need to tell how children nodes will be layed
     *      out...
     */
    @Override
    protected void createEditPolicies() {
        setSelectablePart(false);
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramXYLayoutEditPolicy());
    }

    /**
     * This method is not mandatory to implement, but if you do not implement it, you will not have the ability to
     * rectangle-selects several figures...
     */
    @Override
    public DragTracker getDragTracker(Request req) {
        // Unlike in Logical Diagram Editor example, I use a singleton because this
        // method is Entered >> several time, so I prefer to save memory ; and it works!
        if (m_dragTracker == null) {
            m_dragTracker = new LassoDragTracker();
        }
        return m_dragTracker;
    }

    @Override
    public void layout(boolean layoutChildren) {

        if (layoutChildren) {
            EditPart canvasEditPart = getViewer().getContents();
            List canvasChildren = canvasEditPart.getChildren();
            Iterator iter = canvasChildren.iterator();
            EditPart nextEditPart = null;

            while (iter.hasNext()) {
                nextEditPart = (EditPart)iter.next();
                if (nextEditPart instanceof DiagramEditPart) {
                    ((DiagramEditPart)nextEditPart).layout(layoutChildren);
                }
            }
        }
        // super.layout(layoutChildren);

        // Check it's model node, if was layed out, don't re-layout!!

        if (!((DiagramModelNode)getModel()).wasLayedOut()) {
            if (UiConstants.Util.isDebugEnabled(DebugConstants.MAPPING_DIAGRAM_EDIT_PARTS)) {
                String message = "calling layout()"; //$NON-NLS-1$
                UiConstants.Util.print(DebugConstants.MAPPING_DIAGRAM_EDIT_PARTS, message);
            }
            layout();
        } else {
            if (UiConstants.Util.isDebugEnabled(DebugConstants.MAPPING_DIAGRAM_EDIT_PARTS)) {
                String message = "calling recoverObjectProperties()"; //$NON-NLS-1$
                UiConstants.Util.print(DebugConstants.MAPPING_DIAGRAM_EDIT_PARTS, message);
            }
            ((DiagramModelNode)getModel()).recoverObjectProperties();
        }

        // now process the Association Labels, if any:
        List arylSourceConnections = new ArrayList();
        if (!arylSourceConnections.isEmpty()) {
            Iterator itSourceConns = arylSourceConnections.iterator();

            while (itSourceConns.hasNext()) {
                NodeConnectionModel ncmSourceConn = (NodeConnectionModel)itSourceConns.next();
                ncmSourceConn.setRouterStyle(sCurrentRouterStyle);
                ncmSourceConn.layout();
            }

        }
    }

    private boolean hasChildren() {
        if (getViewer().getContents().getChildren().size() > 0)
            return true;
        return false;
    }

    @Override
    public void layout() {
        // System.out.println("\n\n[MappingDiagramLayout.layout_ExecutionsCount] Execution: " + ++layout_ExecutionsCount );
        // System.out.println("[MappingDiagramLayout.layout_ExecutionsCount] I am a: " + this.getModelObject() );

        if (bLayoutInProgress) {
            // no action if layout is already in progress
            // System.out.println("[MappingDiagramEditPart.layout] Bailing out, layout is in progress...");
            return;
        }

        // System.out.println("[MappingDiagramEditPart.layout] Continuing, starting a new layout...");
        bLayoutInProgress = true;

        // Let's just use the TransformationDiagramLayout here and not the manager.
        MappingDiagramLayout diagramLayout = new MappingDiagramLayout((DiagramModelNode)getModel(), isCoarseMapping());

        // if running in "Detailed" mode, we need find the lowest value "extent" y
        int yValue = getLowestYExtentValue();
        if( !isCoarseMapping() ) {
            MappingDiagramController controller = (MappingDiagramController)((DiagramViewer)getViewer()).getEditor().getDiagramController();
            if( controller != null ) {
                yValue = yValue + controller.getScrollOffset();
            }
        }
        diagramLayout.run(120, yValue);

        // Update Anchors and Links
        updateAnchorsAndLinks();

        bLayoutInProgress = false;
        this.getFigure().repaint();
    }

    private void updateAnchorsAndLinks() {
        EditPart canvasEditPart = getViewer().getContents();
        List canvasChildren = canvasEditPart.getChildren();
        Iterator iter = canvasChildren.iterator();
        EditPart nextEditPart = null;

        while (iter.hasNext()) {
            nextEditPart = (EditPart)iter.next();
            if (nextEditPart instanceof DiagramEditPart && !(nextEditPart instanceof AbstractFreeEditPart)) {
                ((DiagramEditPart)nextEditPart).createOrUpdateAnchorsLocations(false);
            }
        }
    }

    private int getLowestYExtentValue() {
        int minYValue = 20;
        int yValue = -99;

        EditPart canvasEditPart = getViewer().getContents();
        Iterator iter = canvasEditPart.getChildren().iterator();
        DiagramEditPart nextEditPart = null;
        int partY = 0;
        while (iter.hasNext()) {
            nextEditPart = (DiagramEditPart)iter.next();
            if (nextEditPart instanceof MappingExtentEditPart) {
                partY = ((DiagramModelNode)nextEditPart.getModel()).getY();
                if (yValue < 0)
                    yValue = partY;
                else
                    yValue = Math.min(partY, yValue);
            }
        }
        if (yValue < minYValue)
            yValue = minYValue;
        return yValue;
    }

    private boolean isClassifer(DiagramEditPart editPart) {
        return editPart instanceof UmlClassifierEditPart;
    }

    public TopAndBottomClassifierInfo getTopAndBottomClassifierInfo() {
        TopAndBottomClassifierInfo tab = new TopAndBottomClassifierInfo();
        boolean firstRun = true;

        EditPart canvasEditPart = getViewer().getContents();
        Iterator iter = canvasEditPart.getChildren().iterator();
        DiagramEditPart nextEditPart = null;
        while (iter.hasNext()) {
            nextEditPart = (DiagramEditPart)iter.next();
            if (isClassifer(nextEditPart)) {
                DiagramModelNode diagramModelNode = (DiagramModelNode)nextEditPart.getModel();
                int topY = diagramModelNode.getY();
                int bottomY = topY + diagramModelNode.getHeight();

                if (firstRun) {
                    // first time, just take the current EP as everything:
                    firstRun = false;
                    tab.bottomPart = nextEditPart;
                    tab.topPart = nextEditPart;
                    tab.bottomY = bottomY;
                    tab.topY = topY;

                } else {
                    // not the first time, check both top and bottom bounds:
                    if (bottomY > tab.bottomY) {
                        tab.bottomY = bottomY;
                        tab.bottomPart = nextEditPart;
                    } // endif
                    if (topY < tab.topY) {
                        tab.topY = topY;
                        tab.topPart = nextEditPart;
                    } // endif
                } // endif -- firstRun
            } // endif -- isClassifier
        } // endwhile -- child editParts

        return tab;
    }

    public int getLowestYValue() {
        int yValue = 0;

        EditPart canvasEditPart = getViewer().getContents();
        Iterator iter = canvasEditPart.getChildren().iterator();
        DiagramEditPart nextEditPart = null;
        int partY = 0;
        while (iter.hasNext()) {
            nextEditPart = (DiagramEditPart)iter.next();
            if (nextEditPart.getModel() != null && nextEditPart.getModel() instanceof DiagramModelNode) {
                partY = ((DiagramModelNode)nextEditPart.getModel()).getY();
                yValue = Math.min(partY, yValue);
            }
        }

        return yValue;
    }

    public int getHighestYValue() {
        int yValue = -99;

        EditPart canvasEditPart = getViewer().getContents();
        Iterator iter = canvasEditPart.getChildren().iterator();
        DiagramEditPart nextEditPart = null;
        int partY = 0;
        while (iter.hasNext()) {
            nextEditPart = (DiagramEditPart)iter.next();
            if (nextEditPart.getModel() != null && nextEditPart.getModel() instanceof DiagramModelNode) {
                partY = ((DiagramModelNode)nextEditPart.getModel()).getY()
                        + ((DiagramModelNode)nextEditPart.getModel()).getHeight();
                yValue = Math.max(partY, yValue);
            }
        }

        return yValue;
    }

    public boolean hasConnections() {
        EditPart canvasEditPart = getViewer().getContents();
        Iterator iter = canvasEditPart.getChildren().iterator();
        DiagramEditPart nextEditPart = null;

        while (iter.hasNext()) {
            nextEditPart = (DiagramEditPart)iter.next();
            if (!nextEditPart.getSourceConnections().isEmpty() || !nextEditPart.getTargetConnections().isEmpty())
                return true;
        }
        return false;
    }

    /**
     * Reset font from Font Managar and call layout on all diagram objects.
     */
    @Override
    public void refreshFont(boolean refreshChildren) {
        // Diagram needs to also do a layout here
        super.refreshFont(refreshChildren);

        // But not at the diagram level

        if (hasChildren()) {
            EditPart canvasEditPart = getViewer().getContents();
            List canvasChildren = canvasEditPart.getChildren();
            Iterator iter = canvasChildren.iterator();
            EditPart nextEditPart = null;

            while (iter.hasNext()) {
                nextEditPart = (EditPart)iter.next();
                if (nextEditPart instanceof DiagramEditPart) {
                    ((DiagramEditPart)nextEditPart).layout(true);
                }
            }
        }
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
     */
    @Override
    protected void refreshVisuals() {
        ConnectionLayer cLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
        if (sCurrentRouterStyle.equals(DiagramUiConstants.DiagramRouterStyles.FAN_ROUTER)) {
            cLayer.setConnectionRouter(new FanRouter());
        }
    }

    public boolean isCoarseMapping() {
        return ((MappingDiagramNode)getModel()).isCoarse();
    }

    /**
     * Helper method used to assess whether any edit part contains the same reference EObject as the diagram's target. Implemented
     * for the MappingClassAnchorManager, so it can treat the MappingClass edit part differently when it's in a Detailed diagram
     * and it's the primary mapping class, and not a source table
     * 
     * @param editPart
     * @return
     */
    public boolean isPrimary(DiagramEditPart editPart) {
        boolean primary = false;

        EObject diagramTarget = ((Diagram)getModelObject()).getTarget();
        if (editPart.getModelObject() != null && editPart.getModelObject().equals(diagramTarget))
            primary = true;

        return primary;
    }

    @Override
    public void updateForPreferences() {
        RGB currentBkgdColor = PreferenceConverter.getColor(DiagramUiPlugin.getDefault().getPreferenceStore(),
                                                            PluginConstants.Prefs.Appearance.MAPPING_BKGD_COLOR);
        this.getFigure().setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
        refreshFont(true);
        layout(false);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#handleZoomChanged()
     * @since 4.2
     */
    @Override
    public void handleZoomChanged() {
        // Let's update all mapping extent nodes
        DiagramEditPart canvasEditPart = (DiagramEditPart)getViewer().getContents();
        Iterator iter = canvasEditPart.getChildren().iterator();
        DiagramEditPart nextEditPart = null;

        MappingDiagramNode mdn = (MappingDiagramNode)canvasEditPart.getModel();
        int currentYOrigin = mdn.getCurrentYOrigin();

        while (iter.hasNext()) {
            nextEditPart = (DiagramEditPart)iter.next();
            if (nextEditPart instanceof MappingExtentEditPart) {
                MappingExtentNode men = (MappingExtentNode)nextEditPart.getModel();
                men.updateModelForExtent();
                men.setExtentPosition(currentYOrigin);
            }
        }

        // layout();
    }

    public static class TopAndBottomClassifierInfo {

        public int topY;
        public int bottomY;
        public DiagramEditPart topPart;
        public DiagramEditPart bottomPart;
    }

    public boolean canExpand() {
        return isCoarseMapping();
    }

    public void collapseAll() {
    }

    public void collapse(Object child) {
    }

    public void expandAll() {
    }

    public void expand(Object child) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        super.propertyChange(evt);
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.COLLAPSE)
            || prop.equals(DiagramUiConstants.DiagramNodeProperties.EXPAND)) {
            layout();
        }
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.AbstractDefaultEditPart#setUnderConstruction(boolean)
     * @since 5.0
     */
    @Override
    public void setUnderConstruction(boolean theUnderConstruction) {
        List contents = this.getChildren();
        
        Iterator iter = contents.iterator();
        Object nextObj = null;
        while (iter.hasNext() ) {
            nextObj = iter.next();
            if (nextObj instanceof DiagramEditPart) {
                ((DiagramEditPart)nextObj).setUnderConstruction(theUnderConstruction);
            }
        }
        super.setUnderConstruction(theUnderConstruction);
    }
    
    

}
