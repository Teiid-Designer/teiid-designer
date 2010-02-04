/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.part;



import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.layout.DefaultLayoutNode;
import com.metamatrix.modeler.diagram.ui.layout.LayoutGroup;
import com.metamatrix.modeler.diagram.ui.layout.LayoutHelper;
import com.metamatrix.modeler.diagram.ui.layout.LayoutNode;
import com.metamatrix.modeler.diagram.ui.layout.LayoutUtilities;
import com.metamatrix.modeler.diagram.ui.layout.MmTreeLayout;
import com.metamatrix.modeler.diagram.ui.layout.TreeLayout;
import com.metamatrix.modeler.diagram.ui.layout.spring.SpringLayout;
import com.metamatrix.modeler.diagram.ui.model.AbstractFreeDiagramModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDefaultEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.AbstractFreeEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.DiagramXYLayoutEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.LassoDragTracker;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.ui.IDiagramTypeEditPart;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.query.ui.UiPlugin;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * TransformationDiagramEditPart
 */

public class TransformationDiagramEditPart extends AbstractDiagramEditPart 
                                         implements IPropertyChangeListener,
                                                      IDiagramTypeEditPart {

    /** Singleton instance of MarqueeDragTracker. */
    static DragTracker m_dragTracker = null;
    private boolean isDependencyDiagram = false;
    private TransformationDiagramLayout diagramLayout = new TransformationDiagramLayout();
    
    private String sCurrentRouterStyle = DiagramUiConstants.DiagramRouterStyles.FAN_ROUTER;

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {

        Figure newFigure = getFigureFactory().createFigure(getModel());
		setCurrentDiagramFont(ScaledFontManager.getFont());
        // Set dependency diagram boolean if necessary.
        if( getDiagramType() != null &&
            getDiagramType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID) )
            isDependencyDiagram = true;

        return newFigure;
    }
    
    private String getDiagramType() {
        return ((Diagram)((DiagramModelNode)getModel()).getModelObject()).getType();
    }
    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     * You need to tell how children nodes will be layed out...
    **/
    @Override
    protected void createEditPolicies() {
        setSelectablePart(false);
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
    
    private boolean hasChildren() {
        if( getViewer().getContents().getChildren().size() > 0 )
            return true;
        return false;
    }
    
    /* (non-JavaDoc)
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        String prop = evt.getPropertyName();

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CHILDREN)) {
            layout();
        }
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LAYOUT)) {
            layout();
        }
    }
    /** 
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     * @since 5.0
     */
    public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent theEvent) {
        if (theEvent.getProperty().equals(UiConstants.Prefs.TREE_DIAGRAM_LAYOUT)) {
            // We need to re-open the diagram to fully re-draw and layout.
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    ModelEditorManager.open(getModelObject(), true);
                }                    
            });
            // now we need to re-layout the diagram
        }
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
        
        if( !((DiagramModelNode)getModel()).wasLayedOut() || isDependencyDiagram  ) {
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
                ncmSourceConn.setRouterStyle( sCurrentRouterStyle );                
                ncmSourceConn.layout();                                    
            }
        
        }
    }
    
    @Override
    public void layout() {
        if( !hasChildren() )
            return;
        
        if( getDiagramType() != null &&
            getDiagramType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID) ) {
            runDependencyLayout();
        } else {
            // Let's just use the TransformationDiagramLayout here and not the manager.
            diagramLayout.setDiagramNode((DiagramModelNode)getModel());
            diagramLayout.run();
            this.getFigure().repaint();
        }

        
        // Update Anchors and Links
        updateAnchorsAndLinks();
    }
    
    private void runDependencyLayout() {
        EObject primaryVG = ((Diagram)((DiagramModelNode)getModel()).getModelObject()).getTarget();
        DiagramModelNode vgDiagramNode = DiagramUiUtilities.getDiagramModelNode(primaryVG, (DiagramModelNode)getModel());
        
        // Now let's create a layoutGroup with only TNodes and Tables
        List tNodesAndTables = new ArrayList();
        List allChildren = ((DiagramModelNode)getModel()).getChildren();
        Iterator iter = allChildren.iterator();
        Object nextNode = null;
        while (iter.hasNext()) {
            nextNode = iter.next();
 
            if ( nextNode instanceof DiagramModelNode && !(nextNode instanceof AbstractFreeDiagramModelNode)) {
                // Add each component (table or transformation) to the layout manager.
                tNodesAndTables.add( nextNode );
            }
        }
        
        LayoutGroup layoutGroup = new LayoutGroup(tNodesAndTables);
        if( layoutGroup.getType() == LayoutHelper.SIMPLE_LAYOUT ) {
            LayoutNode rootNode = new DefaultLayoutNode(vgDiagramNode);
//            LayoutUtilities.runTreeLayout(layoutGroup, rootNode);

            TreeLayout layout = new TreeLayout(layoutGroup.getLayoutNodes(), 10, 10, 400, 400);
            layout.setRoot(rootNode);
            layout.setOrientation(MmTreeLayout.ORIENTATION_ROOT_LEFT);
            layout.setFixedSpacing(true);
            layout.setFixedXSpacing(100);
            layout.setFixedYSpacing(100);
            layout.setUseObjectsSizes(true);
            layout.run();
            layoutGroup.setFinalPositions();
        } else {
            // Use Spring Layout here            
            LayoutUtilities.runColumnLayout(layoutGroup, 20);
            
            SpringLayout layout = new SpringLayout(layoutGroup.getLayoutNodes());
            layout.setAutoEdgeLength(true);

            layout.setStartLocation(20, 20);
            layout.setAutomaticSingleSpacing(true);
            layout.setSpecifyLayoutSize(true);
            layout.setEpsilon(.2);
            layout.setHorizontalAlignment(SpringLayout.LEFT_ALIGNMENT);
            layout.setUseObjectsSizes(false);
            layout.setVerticalAlignment(SpringLayout.TOP_ALIGNMENT);
            layout.setXSpacing(400);
            layout.setYSpacing(400);
            int size = (int)layout.getSizeEstimate();
            layout.setLayoutSize(size*2);
            
            layout.run();
                  
            layoutGroup.setFinalPositions();
        }
    }
    
    private void updateAnchorsAndLinks() {
        EditPart canvasEditPart = getViewer().getContents();
        List canvasChildren = canvasEditPart.getChildren();
        Iterator iter = canvasChildren.iterator();
        EditPart nextEditPart = null;
            
        while (iter.hasNext()) {
            nextEditPart = (EditPart)iter.next();
            if ( nextEditPart instanceof DiagramEditPart && ! (nextEditPart instanceof AbstractFreeEditPart )) {
                ((DiagramEditPart)nextEditPart).createOrUpdateAnchorsLocations(false);
                ((AbstractDefaultEditPart)nextEditPart).refreshAllLabels();
            }
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
        }
    }
    
    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
    **/
    @Override
    protected void refreshVisuals() {
        ConnectionLayer cLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
        if ( sCurrentRouterStyle.equals( DiagramUiConstants.DiagramRouterStyles.FAN_ROUTER ) ) {        
            cLayer.setConnectionRouter(new FanRouter());
        } 
    }
    
    public DiagramEditPart getTransformEditPart() {
        
        EditPart canvasEditPart = getViewer().getContents();
        Iterator iter = canvasEditPart.getChildren().iterator();
        DiagramEditPart nextEditPart = null;

        while (iter.hasNext()) {
            nextEditPart = (DiagramEditPart)iter.next();
            if( nextEditPart instanceof TransformationEditPart )
                return nextEditPart;
        }
        
        return null;
    }
    
    public DiagramModelNode getTargetOfTransform() {
        DiagramModelNode targetGroupNode = null;
        
        // get TransformDiagramNode.
        DiagramEditPart transformEP = getTransformEditPart();
        
        if( transformEP != null ) {
            // get it's source connections.
            List sourceConnections = transformEP.getSourceConnections();

            // get the "targetNode" on this connection.
            if( sourceConnections.size() == 1 ) {
                NodeConnectionEditPart sourceConnection = (NodeConnectionEditPart)sourceConnections.iterator().next();
                targetGroupNode = (DiagramModelNode)((NodeConnectionModel)sourceConnection.getModel()).getTargetNode();
            }
        }

        
        return targetGroupNode;
    }
    
    
    @Override
    public void updateForPreferences() {
        RGB currentBkgdColor;
        if (isDependencyDiagram) {
            // dependency diagram:
            currentBkgdColor = PreferenceConverter.getColor(
                DiagramUiPlugin.getDefault().getPreferenceStore(), 
                PluginConstants.Prefs.Appearance.DEPENDENCY_BKGD_COLOR);
        } else {
            // regular transformation diagram:
            currentBkgdColor = PreferenceConverter.getColor(
                DiagramUiPlugin.getDefault().getPreferenceStore(), 
                PluginConstants.Prefs.Appearance.TRANSFORM_BKGD_COLOR);
        } // endif

        this.getFigure().setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
        refreshFont(true);
        layout(false);
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.AbstractDefaultEditPart#activate()
     * @since 5.0
     */
    @Override
    public void activate() {
        super.activate();
        UiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    /** 
     * @see com.metamatrix.modeler.diagram.ui.part.AbstractDefaultEditPart#deactivate()
     * @since 5.0
     */
    @Override
    public void deactivate() {
        super.deactivate();
        UiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
    }
}

