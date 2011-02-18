/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.part;

import java.beans.PropertyChangeEvent;
import java.util.List;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractDiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.EditableEditPart;
import com.metamatrix.modeler.diagram.ui.util.HiliteDndNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.transformation.ui.figure.TransformationFigure;
import com.metamatrix.modeler.transformation.ui.model.TransformationNode;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;

/**
 */
public class TransformationEditPart extends AbstractDiagramEditPart implements EditableEditPart {
    private ChopboxAnchor singleAnchor;
    private DragTracker myDragTracker = null;

    public TransformationEditPart() {
        super();
    }

    public TransformationEditPart( String diagramTypeId ) {
        super();
        setDiagramTypeId(diagramTypeId);
        init();
    }

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
        TransformationFigure transformFigure = (TransformationFigure)getFigureFactory().createFigure(getModel());
        transformFigure.setLocation(location);

        return transformFigure;
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     **/
    @Override
    protected void createEditPolicies() {

        // installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramXYLayoutEditPolicy());
        installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
        // installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new DiagramNodeSelectionEditPolicy());
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new HiliteDndNodeSelectionEditPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPartEditPolicy());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.EditableEditPart#edit()
     */
    public void edit() {

        if (ModelEditorManager.canEdit(((DiagramModelNode)getModel()).getModelObject())) {
            ModelEditorManager.edit(((DiagramModelNode)getModel()).getModelObject());
        } else {
            // We might have an Operations Object here. In which case we go ahead and try to edit

            if (((DiagramModelNode)getModel()).getModelObject() != null) {
                // Get the diagram's Target
                Diagram diagram = ((DiagramModelNode)getModel()).getDiagram();
                if (diagram != null) {
                    if (diagram.getTarget() != null && TransformationHelper.isOperation(diagram.getTarget())) {
                        ModelEditorManager.edit(diagram.getTarget());
                    }
                }
            }
        }
    }

    @Override
    public void performRequest( Request request ) {
        if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
            getSelectionHandler().handleDoubleClick(this.getModelObject());
        }
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren() You must implement this method if you want you root
     *      model to have children!
     **/
    @Override
    protected List getModelChildren() {

        List children = ((TransformationNode)getModel()).getChildren();

        return children;
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
     **/
    @Override
    protected void refreshVisuals() {

        Point loc = ((DiagramModelNode)getModel()).getPosition();
        Dimension size = ((DiagramModelNode)getModel()).getSize();
        Rectangle r = new Rectangle(loc, size);
        ((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), r);
        getFigure().repaint();
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt ) {
        // 
        String prop = evt.getPropertyName();
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            resizeChildren();
        }

        super.propertyChange(evt);

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CONNECTION)) {
            refresh();
            createOrUpdateAnchorsLocations(true);
            refreshAllLabels();
        }
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            ((DiagramModelNode)getModel()).updateAssociations();
        }
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SUBSCRIPT)) {
            ((TransformationFigure)getDiagramFigure()).setSubscript(((TransformationNode)getModel()).getSubscript());
            refreshVisuals();
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

    public ConnectionAnchor getAnchor() {
        if (singleAnchor == null) singleAnchor = new ChopboxAnchor(this.getFigure());

        return singleAnchor;
    }

    // =================================================================================================
    // =================================================================================================
    // Connection Methods from NodeEditPart interface and Overriding AbstractGraphicalEditPart
    // =================================================================================================
    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
     **/
    @Override
    protected List getModelSourceConnections() {
        return ((TransformationNode)getModel()).getSourceConnections();
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
     **/
    @Override
    protected List getModelTargetConnections() {
        return ((TransformationNode)getModel()).getTargetConnections();
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(ConnectionEditPart)
     **/
    @Override
    public ConnectionAnchor getSourceConnectionAnchor( ConnectionEditPart connection ) {
        return getAnchor();
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(Request)
     **/
    @Override
    public ConnectionAnchor getSourceConnectionAnchor( Request request ) {
        return getAnchor();
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(ConnectionEditPart)
     **/
    @Override
    public ConnectionAnchor getTargetConnectionAnchor( ConnectionEditPart connection ) {
        return getAnchor();
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(Request)
     **/
    @Override
    public ConnectionAnchor getTargetConnectionAnchor( Request request ) {
        return getAnchor();
    }
}
