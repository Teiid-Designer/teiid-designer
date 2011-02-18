/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.part;

import java.beans.PropertyChangeEvent;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartManager;
import com.metamatrix.modeler.diagram.ui.util.directedit.LabelCellEditorLocator;
import com.metamatrix.modeler.relationship.RelationshipEditor;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.figure.RelationshipNodeFigure;
import com.metamatrix.modeler.relationship.ui.layout.RelationshipLayoutGroup;
import com.metamatrix.modeler.relationship.ui.model.RelationshipModelNode;

/**
 * @author BLaFond To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class RelationshipNodeEditPart extends AbstractDiagramEditPart implements DirectEditPart {
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private DragTracker myDragTracker = null;
    private DirectEditManager manager;
    private Point lastHoverPoint = new Point(0, 0);

    //	private static final String THIS_CLASS = "RelationshipNodeEditPart"; //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public RelationshipNodeEditPart() {
        super();
    }

    public RelationshipNodeEditPart( String diagramTypeId ) {
        super();
        setDiagramTypeId(diagramTypeId);
        init();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public void init() {
        if (getAnchorManager() == null) {
            setAnchorManager(getEditPartFactory().getAnchorManager(this));
        }
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     **/
    @Override
    protected IFigure createFigure() {

        Point location = new Point(100, 100);
        Figure newFigure = getFigureFactory().createFigure(getModel());
        newFigure.setLocation(location);

        Image firstOverlayImage = ((DiagramModelNode)getModel()).getFirstOverlayImage();
        if (firstOverlayImage != null) {
            ((DiagramFigure)newFigure).addEditButton(firstOverlayImage);
        }
        Image secondOverlayImage = ((DiagramModelNode)getModel()).getSecondOverlayImage();
        if (secondOverlayImage != null) {
            ((DiagramFigure)newFigure).addUpperLeftButton(secondOverlayImage);
        }

        return newFigure;
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     **/
    @Override
    protected void createEditPolicies() {
        installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new HiliteRelationshipNodeSelectionPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPartEditPolicy());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.EditableEditPart#edit()
     */
    public void edit() {
        if (ModelerCore.getModelEditor().hasName(getModelObject())) performDirectEdit();
    }

    public void performDirectEdit() {
        if (manager == null && getLabel() != null) manager = new DirectEditPartManager(this, TextCellEditor.class,
                                                                                       new LabelCellEditorLocator(getLabel()));
        if (manager != null) manager.show();
    }

    @Override
    public void performRequest( Request request ) {
        if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
            if (!(getSelectionHandler().handleDoubleClick(this.getModelObject()))) {
                if (ModelerCore.getModelEditor().hasName(getModelObject())) performDirectEdit();
            }
        }
    }

    // ----------------------------------
    // DirectEditPart interface methods
    // ----------------------------------
    public String getText() {
        return ((DiagramModelNode)getModel()).getName();
    }

    public String getEditString() {
        return ((DiagramModelNode)getModel()).getName();
    }

    public void setText( String newName ) {
        ((DiagramModelNode)getModel()).setName(newName);
    }

    private Label getLabel() {
        Label label = null;
        if (getFigure() instanceof DirectEditFigure) {
            label = ((DirectEditFigure)getFigure()).getLabelFigure();
        }
        return label;
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren() You must implement this method if you want you root
     *      model to have children!
     **/
    @Override
    protected List getModelChildren() {

        List children = ((RelationshipModelNode)getModel()).getChildren();

        return children;
    }

    protected void refreshRestoreButton() {
        Image secondOverlayImage = ((DiagramModelNode)getModel()).getSecondOverlayImage();
        getDiagramFigure().addUpperLeftButton(secondOverlayImage);
        Image firstOverlayImage = ((DiagramModelNode)getModel()).getFirstOverlayImage();
        getDiagramFigure().addEditButton(firstOverlayImage);
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
     **/
    @Override
    protected void refreshVisuals() {

        Point loc = ((DiagramModelNode)getModel()).getPosition();
        Dimension size = ((DiagramModelNode)getModel()).getSize();
        Rectangle r = new Rectangle(loc, new Dimension(size.width, size.height));
        ((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), r);
        getFigure().repaint();
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#resizeChildren()
    **/
    @Override
    public void refreshName() {
        super.refreshName();
        RelationshipModelNode rmn = (RelationshipModelNode)getModel();
        // Need to get the figure and update the type also
        ((RelationshipNodeFigure)getDiagramFigure()).updateForChange(rmn.getStereotype(),
                                                                     rmn.getSourceRoleName(),
                                                                     rmn.getTargetRoleName());
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt ) {
        // 
        String prop = evt.getPropertyName();
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            resizeChildren();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CONNECTION)) {
            refresh();
            createOrUpdateAnchorsLocations(true);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.BUTTONS)) {
            refreshRestoreButton();
            refreshVisuals();
        }

        super.propertyChange(evt);
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.NAME)) {
            layout();
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            ((DiagramModelNode)getModel()).updateAssociations();

        } else if (prop.equals(UiConstants.NavigationModelNodeProperties.LAYOUT)) {
            layoutSourcesAndTargets();
            createOrUpdateAnchorsLocations(true);
            refreshAllLabels();
        }

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CONNECTION)) {
            createOrUpdateAnchorsLocations(true);
            refreshAllLabels();
        }
    }

    @Override
    public void resizeChildren() {
        // call header.resize();
        getDiagramFigure().updateForSize(((DiagramModelNode)getModel()).getSize());
    }

    /**
     * This method is not mandatory to implement, but if you do not implement it, you will not have the ability to
     * rectangle-selects several figures...
     **/
    @Override
    public DragTracker getDragTracker( Request req ) {
        // Unlike in Logical Diagram Editor example, I use a singleton because this
        // method is Entered >> several time, so I prefer to save memory ; and it works!
        if (myDragTracker == null) {
            myDragTracker = new SelectionTracker(this);
        }
        return myDragTracker;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart#getEditManager()
     */
    public DirectEditPartManager getEditManager() {
        return (DirectEditPartManager)manager;
    }

    /**
     * Implemented to determine which role container to drop the incoming eObject list.
     * 
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#drop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.2
     */
    @Override
    public void drop( Point dropPoint,
                      List dropList ) {
        RelationshipEditor reEditor = RelationshipPlugin.createEditor((Relationship)getModelObject());
        int dropId = ((RelationshipNodeFigure)getDiagramFigure()).getDropTargetId(dropPoint);
        switch (dropId) {
            case PluginConstants.Drop.SOURCE_ROLE: {
                try {
                    reEditor.addSourceParticipants(dropList);
                } catch (ModelerCoreException mce) {
                    ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());
                }
            }
                break;

            case PluginConstants.Drop.TARGET_ROLE: {
                try {
                    reEditor.addTargetParticipants(dropList);
                } catch (ModelerCoreException mce) {
                    ModelerCore.Util.log(IStatus.ERROR, mce, mce.getMessage());
                }
            }
                break;

            default: {
                // does nothing;
            }
                break;
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#hilite(boolean)
     * @since 4.2
     */
    @Override
    public void hilite( boolean hilite ) {
        if (hilite) {
            ((RelationshipNodeFigure)getDiagramFigure()).hiliteBackground(ColorConstants.green, getLastHoverPoint());
        } else {
            ((RelationshipNodeFigure)getDiagramFigure()).hiliteBackground(null, getLastHoverPoint());
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#getLastHoverPoint()
     * @since 4.2
     */
    @Override
    public Point getLastHoverPoint() {
        return this.lastHoverPoint;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#setLastHoverPoint(org.eclipse.draw2d.geometry.Point)
     * @since 4.2
     */
    @Override
    public void setLastHoverPoint( Point lastHoverPoint ) {
        this.lastHoverPoint = lastHoverPoint;
    }

    private void layoutSourcesAndTargets() {
        DiagramModelNode relNode = (DiagramModelNode)getModel();
        Point initialPt = relNode.getPosition();

        if (initialPt.x <= 0 || initialPt.y <= 0) {
            List connectedModelNodes = DiagramUiUtilities.getConnectedModelNodes(relNode.getParent());
            RelationshipLayoutGroup rlg = new RelationshipLayoutGroup(connectedModelNodes, relNode);
            rlg.layoutSingleRelationship(relNode, new Point(20, 100));
        }
    }
}
