/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ExposeHelper;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ViewportExposeHelper;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramLinkType;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.connection.AnchorManager;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.figure.DiagramFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.diagram.ui.DebugConstants;

/**
 * AbstractDefaultEditPart
 */
public abstract class AbstractDefaultEditPart extends AbstractGraphicalEditPart
    implements PropertyChangeListener, DiagramEditPart, DropEditPart {

    private boolean canResize = true;
    private boolean isPrimary = false;
    private boolean isSelectable = true;
    private String sNotationId;
    private String sDiagramTypeId;
    private DropEditPartHelper dropHelper = null;
    private boolean underConstruction;
    private PropertyChangeManager changeManager;

    /** The figure's anchors. */
    // private NodeConnectionAnchor sourceAnchor;
    // private NodeConnectionAnchor targetAnchor;
    private AnchorManager anchorManager;
    private static final String SOURCE = "source"; //$NON-NLS-1$
    private static final String TARGET = "target"; //$NON-NLS-1$  

    private Font currentDiagramFont;

    public AbstractDefaultEditPart() {
        super();
        init();
    }

    private void init() {
        // anchorManager = new AnchorManager(this);
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#getModelObject()
    **/
    public EObject getModelObject() {
        return ((DiagramModelNode)getModel()).getModelObject();
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#getDiagramFigure()
    **/
    public DiagramFigure getDiagramFigure() {
        if (getFigure() instanceof DiagramFigure) return (DiagramFigure)getFigure();

        return null;
    }

    /* (non-JavaDoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#activate()
     * Makes the EditPart aware to changes in the model
     * by adding itself to the model's list of listeners.
     */
    @Override
    public void activate() {
        if (isActive()) return;
        super.activate();
        ((DiagramModelNode)getModel()).addPropertyChangeListener(this);
        if (getDiagramFigure() != null) getDiagramFigure().activate();
    }

    /* (non-JavaDoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#deactivate()
     * Makes the EditPart insensible to changes in the model
     * by removing itself from the model's list of listeners.
     */
    @Override
    public void deactivate() {
        if (!isActive()) return;
        super.deactivate();
        ((DiagramModelNode)getModel()).removePropertyChangeListener(this);
        if (getDiagramFigure() != null) getDiagramFigure().deactivate();
    }

    /* (non-JavaDoc)
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     * You must implement this method if you want you root model to have 
     * children!
    **/
    @Override
    protected List getModelChildren() {
        List children = ((DiagramModelNode)getModel()).getChildren();
        return children;
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#clearSelections(boolean)
    **/
    public void clearSelections( boolean clearSubSelections ) {
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#layout(boolean)
    **/
    public void layout( boolean layoutChildren ) {
        // Layout out it's children first
        if (layoutChildren) {
            List editPartChildren = getChildren();
            Iterator iter = editPartChildren.iterator();
            EditPart nextEP = null;
            while (iter.hasNext()) {
                nextEP = (EditPart)iter.next();
                if (nextEP instanceof DiagramEditPart) ((DiagramEditPart)nextEP).layout(layoutChildren);
            }
        }
        // Then do a getFigure().layout here.
        if (getDiagramFigure() != null) getDiagramFigure().layoutFigure();

        updateModelSize();
        // get it's children and update their model positions.
        List containerChildren = getChildren();
        Iterator iter = containerChildren.iterator();
        while (iter.hasNext()) {
            Object nextObj = iter.next();
            if (nextObj instanceof DiagramEditPart) {
                ((DiagramEditPart)nextObj).updateModelPosition();
            }
        }
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#layout(boolean)
    **/
    public void layout() {
        // Then do a getFigure().layout here.
        if (getDiagramFigure() != null) getDiagramFigure().layoutFigure();

        updateModelSize();
        // get it's children and update their model positions.
        List containerChildren = getChildren();
        Iterator iter = containerChildren.iterator();
        while (iter.hasNext()) {
            Object nextObj = iter.next();
            if (nextObj instanceof DiagramEditPart) {
                ((DiagramEditPart)nextObj).updateModelPosition();
            }
        }
    }

    /* (non-JavaDoc)
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
    **/
    public void propertyChange( PropertyChangeEvent evt ) {

        String prop = evt.getPropertyName();

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CHILDREN)) {
            getChangeManager().refresh(PropertyChangeManager.GENERAL, true);
            getChangeManager().refresh(PropertyChangeManager.LAYOUT_ALL, true);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            if (DiagramUiConstants.Util.isDebugEnabled(com.metamatrix.modeler.internal.ui.DebugConstants.PROPERTIES)) {
                String debugMessage = "Location Property Change on EP = " + ((DiagramModelNode)getModel()).getName() + " Position = " + ((DiagramModelNode)getModel()).getPosition(); //$NON-NLS-2$ //$NON-NLS-1$
                DiagramUiConstants.Util.print(com.metamatrix.modeler.internal.ui.DebugConstants.PROPERTIES, debugMessage);
            }
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.PROPERTIES)) {
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.NAME)) {
            getChangeManager().refresh(PropertyChangeManager.NAME, false);
            getChangeManager().refresh(PropertyChangeManager.LABELS, false);
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
            DiagramEditPart topParent = DiagramUiUtilities.getTopClassifierParent(this);
            //System.out.println(" -->> ADEP.propertyChange(NAME):  topParent = " + topParent); //$NON-NLS-1$
            if (topParent != null) {
                topParent.getChangeManager().refresh(PropertyChangeManager.LAYOUT_ALL, false);
                // topParent.layout(LAYOUT_CHILDREN);
            }
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.ERRORS)) {
            getDiagramFigure().updateForError(((DiagramModelNode)getModel()).hasErrors());
            getDiagramFigure().updateForWarning(((DiagramModelNode)getModel()).hasWarnings());
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LAYOUT)) {
            getChangeManager().refresh(PropertyChangeManager.LAYOUT, false);
        }
    }

    public void updateContent() {

    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#updateModelLocation()
    **/
    public void updateModelPosition() {
        ((DiagramModelNode)getModel()).setPosition(new Point(getFigure().getBounds().x, getFigure().getBounds().y));
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#updateModelSize()
    **/
    public void updateModelSize() {
        ((DiagramModelNode)getModel()).setSize(getFigure().getSize());
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#resizeChildren()
    **/
    public void resizeChildren() {
        // default implementation does nothing
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#resizeChildren()
    **/
    public void refreshName() {
        // Need to get the figure and update the name
        getDiagramFigure().updateForName(((DiagramModelNode)getModel()).getName());
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#selectPrimaryParent()
    **/
    public void selectPrimaryParent() {
        DiagramEditPart editPart = getPrimaryParent();
        if (editPart != null) ((EditPart)editPart).setSelected(EditPart.SELECTED_PRIMARY);
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#getPrimaryParent()
    **/
    public DiagramEditPart getPrimaryParent() {
        if (getParent() instanceof DiagramEditPart) {
            if (((DiagramEditPart)getParent()).isPrimaryParent()) return (DiagramEditPart)getParent();
            return getPrimaryParent();
        }
        return null;
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#isPrimaryParent()
    **/
    public boolean isPrimaryParent() {
        return isPrimary;
    }

    protected void setPrimaryParent( boolean primary ) {
        isPrimary = primary;
    }

    /* (non-JavaDoc)
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#isPrimaryParent()
    **/
    public boolean isSelectablePart() {
        return isSelectable;
    }

    protected void setSelectablePart( boolean selectable ) {
        isSelectable = selectable;
    }

    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getEditPartFactory()
     */
    public DiagramEditPartFactory getEditPartFactory() {
        return DiagramUiPlugin.getDiagramTypeManager().getDiagram(getDiagramTypeId()).getEditPartFactory();
    }

    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getEditPart(com.metamatrix.modeler.diagram.ui.model.DiagramModelNode)
     */
    public DiagramEditPart getEditPart( DiagramModelNode someModelNode ) {
        if (getModel() != null && getModel().equals(someModelNode)) {
            return this;
        }

        // // Check the children
        // List contents = this.getChildren();
        //
        // Iterator iter = contents.iterator();
        // Object nextObj = null;
        DiagramEditPart matchedPart = (DiagramEditPart)this.getViewer().getEditPartRegistry().get(someModelNode);

        // while (iter.hasNext() && matchedPart == null) {
        // nextObj = iter.next();
        // if (nextObj instanceof DiagramEditPart) {
        // matchedPart = ((DiagramEditPart)nextObj).getEditPart(someModelNode);
        // }
        // }

        return matchedPart;
    }

    /*
     *  (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getEditPart(org.eclipse.emf.ecore.EObject)
     */
    public EditPart getEditPart( EObject someModelObject,
                                 boolean linksAllowed ) {
        if (getModelObject() != null) {
            if (getModelObject() instanceof Diagram) {
                EObject targetEObject = ((Diagram)getModelObject()).getTarget();
                if (targetEObject != null && targetEObject.equals(someModelObject)) {
                    return this;
                }
            } else if (getModelObject().equals(someModelObject)) {
                return this;
            }
            if (linksAllowed) {
                // Need to check any connection objects....
                Object nextObject = null;
                NodeConnectionEditPart ncep = null;
                Iterator iter = getSourceConnections().iterator();
                EObject connEObject = null;
                while (iter.hasNext()) {
                    nextObject = iter.next();
                    if (nextObject instanceof NodeConnectionEditPart) {
                        ncep = (NodeConnectionEditPart)nextObject;
                        connEObject = ((NodeConnectionModel)ncep.getModel()).getModelObject();
                        if (connEObject != null && connEObject.equals(someModelObject)) {
                            return ncep;
                        }
                    }
                }
            }
        }

        // Check the children
        List contents = this.getChildren();

        Iterator iter = contents.iterator();
        Object nextObj = null;
        EditPart matchedPart = null;

        while (iter.hasNext() && matchedPart == null) {
            nextObj = iter.next();
            if (nextObj instanceof DiagramEditPart) {
                matchedPart = ((DiagramEditPart)nextObj).getEditPart(someModelObject, linksAllowed);
            }
        }

        return matchedPart;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getNotationId()
     */
    public String getNotationId() {
        return sNotationId;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#setNotationId(java.lang.String)
     */
    public void setNotationId( String sNotationId ) {
        this.sNotationId = sNotationId;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#getNotationId()
     */
    public String getDiagramTypeId() {
        return sDiagramTypeId;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPartFactory#setNotationId(java.lang.String)
     */
    public void setDiagramTypeId( String sDiagramTypeId ) {
        this.sDiagramTypeId = sDiagramTypeId;
    }

    /**
     * A convenience method which uses the Root to obtain the EditPartViewer.
     * 
     * @throws NullPointerException if the root is not found
     * @return the EditPartViewer
     */
    @Override
    public EditPartViewer getViewer() {
        if (getRoot() != null) {
            return getRoot().getViewer();
        }
        return null;
    }

    /**
     * A convenience method which uses the Root to obtain the EditPartViewer.
     * 
     * @throws NullPointerException if the root is not found
     * @return the EditPartViewer
     */
    public boolean isValidViewer() {
        boolean bIsValid = false;

        try {
            EditPartViewer epv = getRoot().getViewer();

            if (epv != null) {
                bIsValid = true;
            }
        } catch (NullPointerException npe) {
            bIsValid = false;
        }

        return bIsValid;
    }

    @Override
    public Object getAdapter( Class key ) {
        if (key == ExposeHelper.class) return new ViewportExposeHelper(this);

        return super.getAdapter(key);
    }

    // =================================================================================================
    /**
     * Adds the specified source <code>ConnectionEditPart</code> at an index. This method is used to update the
     * {@link #sourceConnections} List. This method is called from {@link #addSourceConnection(ConnectionEditPart, int)}.
     * Subclasses should not call or override this method.
     * 
     * @param connection the ConnectionEditPart
     * @param index the index of the add
     */
    @Override
    protected void primAddSourceConnection( ConnectionEditPart connection,
                                            int index ) {
        if (shouldDebug()) {
            String message = "EditPart Model = " //$NON-NLS-1$
                             + ((DiagramModelNode)getModel()).getName() + " connection = " + connection; //$NON-NLS-1$
            DiagramUiConstants.Util.print(getEnabledDebugContext(), message);
        }
        super.primAddSourceConnection(connection, index);
    }

    /**
     * Adds the specified target <code>ConnectionEditPart</code> at an index. This method is used to update the
     * {@link #targetConnections} List. This method is called from {@link #addTargetConnection(ConnectionEditPart, int)}.
     * Subclasses should not call or override this method.
     * 
     * @param connection the ConnectionEditPart
     * @param index the index of the add
     */
    @Override
    protected void primAddTargetConnection( ConnectionEditPart connection,
                                            int index ) {
        if (shouldDebug()) {
            String message = "EditPart Model = " //$NON-NLS-1$
                             + ((DiagramModelNode)getModel()).getName() + " connection = " + connection; //$NON-NLS-1$
            DiagramUiConstants.Util.print(getEnabledDebugContext(), message);
        }
        super.primAddTargetConnection(connection, index);
    }

    /**
     * OVERRIDING method in AbstractGraphicalEditPart The changes to addTargetConnection() were removing the connection after it
     * was added and this didn't make sense for how we were implementing connections. This will have to be fixed in the future
     */
    @Override
    protected void addTargetConnection( ConnectionEditPart connection,
                                        int index ) {
        primAddTargetConnection(connection, index);
        connection.setTarget(this);
        fireTargetConnectionAdded(connection, index);
    }

    /**
     * OVERRIDING method in AbstractGraphicalEditPart The changes to addTargetConnection() were removing the connection after it
     * was added and this didn't make sense for how we were implementing connections. This will have to be fixed in the future
     */
    @Override
    protected void addSourceConnection( ConnectionEditPart connection,
                                        int index ) {
        primAddSourceConnection(connection, index);
        connection.setSource(this);
        if (isActive()) connection.activate();
        fireSourceConnectionAdded(connection, index);
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
        List sourceConnections;

        sourceConnections = ((DiagramModelNode)getModel()).getSourceConnections();
        if (shouldDebug()) {
            String message = "EditPart Model = " //$NON-NLS-1$
                             + ((DiagramModelNode)getModel()).getName() + " Number = " + sourceConnections.size(); //$NON-NLS-1$
            DiagramUiConstants.Util.print(getEnabledDebugContext(), message);
        }
        return sourceConnections;
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
     **/
    @Override
    protected List getModelTargetConnections() {
        List targetConnections;

        targetConnections = ((DiagramModelNode)getModel()).getTargetConnections();
        if (shouldDebug()) {
            String message = "EditPart Model = " //$NON-NLS-1$
                             + ((DiagramModelNode)getModel()).getName() + " Number = " + targetConnections.size(); //$NON-NLS-1$
            DiagramUiConstants.Util.print(getEnabledDebugContext(), message);
        }
        return targetConnections;
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(ConnectionEditPart)
     **/
    public ConnectionAnchor getSourceConnectionAnchor( ConnectionEditPart connection ) {
        if (shouldDebug()) {
            String message = "EditPart Model = " //$NON-NLS-1$
                             + ((DiagramModelNode)getModel()).getName() + " connection = " + connection; //$NON-NLS-1$
            DiagramUiConstants.Util.print(getEnabledDebugContext(), message);
        }
        if (this.getAnchorManager() != null) return this.getAnchorManager().getSourceAnchor((NodeConnectionEditPart)connection);

        return null;
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(Request)
     **/
    public ConnectionAnchor getSourceConnectionAnchor( Request request ) {
        // Somehow translate this request to a specific connection
        // ConnectionEditPart someConnection = null;
        // return anchorManager.getSourceAnchor((NodeConnectionEditPart)someConnection);
        return null;
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(ConnectionEditPart)
     **/
    public ConnectionAnchor getTargetConnectionAnchor( ConnectionEditPart connection ) {
        if (this.getAnchorManager() != null) return this.getAnchorManager().getTargetAnchor((NodeConnectionEditPart)connection);

        return null;
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(Request)
     **/
    public ConnectionAnchor getTargetConnectionAnchor( Request request ) {
        // Somehow translate this request to a specific connection
        // ConnectionEditPart someConnection = null;
        // return anchorManager.getTargetAnchor((NodeConnectionEditPart)someConnection);
        return null;
    }

    public AnchorManager getAnchorManager() {
        return anchorManager;
    }

    /**
     * Set the anchors location based on current figure's rectangle size.
     **/
    public void createOrUpdateAnchorsLocations( boolean updateOtherEnds ) {
        if (this.getAnchorManager() == null) return;

        Dimension size = ((DiagramModelNode)getModel()).getSize();

        if (size != null) {
            this.getAnchorManager().reorderAllAnchors(updateOtherEnds);
        }

        // Need to update bendpoints here too??

        List sourceConn = getModelSourceConnections();
        if (sourceConn != null && !sourceConn.isEmpty()) {
            Iterator iter = sourceConn.iterator();
            NodeConnectionModel ncm = null;
            while (iter.hasNext()) {
                ncm = (NodeConnectionModel)iter.next();
                ncm.refreshBendPoints();
            }
        }
    }

    /**
     * Update the positions of the association's labels
     **/
    public void refreshFont( boolean refreshChildren ) {
        setCurrentDiagramFont(ScaledFontManager.getFont());
        // Walk the children and tell them to refresh font.
        // Layout out it's children first
        if (refreshChildren) {
            List editPartChildren = getChildren();
            Iterator iter = editPartChildren.iterator();
            EditPart nextEP = null;
            while (iter.hasNext()) {
                nextEP = (EditPart)iter.next();
                if (nextEP instanceof DiagramEditPart) ((DiagramEditPart)nextEP).refreshFont(refreshChildren);
            }
        }
        // Then do a getFigure().layout here.
        if (getDiagramFigure() != null) {
            getDiagramFigure().refreshFont();
            ((DiagramModelNode)getModel()).setSize(getFigure().getSize());
            ((DiagramModelNode)getModel()).setPosition(new Point(getFigure().getBounds().x, getFigure().getBounds().y));
        }
        refreshAllLabels();
    }

    /**
     * Update the positions of the association's labels
     **/
    public void refreshAllLabels() {
        Dimension size = ((DiagramModelNode)getModel()).getSize();
        String currentRouterType = DiagramLinkType.get(DiagramEditorUtil.getCurrentDiagramRouterStyle()).getName();
        if (size != null) {

            List sConnections = getSourceConnections();
            if (!sConnections.isEmpty()) {
                for (int iSource = 0; iSource < sConnections.size(); iSource++) {
                    if (sConnections.get(iSource) instanceof NodeConnectionEditPart) {
                        NodeConnectionEditPart ncepPart = (NodeConnectionEditPart)sConnections.get(iSource);

                        NodeConnectionModel daAssociation = (NodeConnectionModel)ncepPart.getModel();
                        daAssociation.setRouterStyle(currentRouterType);
                        resizeLabelsForAssociation(daAssociation);
                        refreshAssociationLabels(ncepPart, daAssociation, SOURCE);
                    }
                }
            }
        }

        if (size != null) {

            List tConnections = getTargetConnections();
            if (!tConnections.isEmpty()) {
                for (int iTarget = 0; iTarget < tConnections.size(); iTarget++) {
                    if (tConnections.get(iTarget) instanceof NodeConnectionEditPart) {
                        NodeConnectionEditPart ncepPart = (NodeConnectionEditPart)tConnections.get(iTarget);
                        NodeConnectionModel daAssociation = (NodeConnectionModel)ncepPart.getModel();
                        daAssociation.setRouterStyle(currentRouterType);
                        resizeLabelsForAssociation(daAssociation);
                        refreshAssociationLabels(ncepPart, daAssociation, TARGET);
                    }
                }
            }
        }

    }

    private void resizeLabelsForAssociation( NodeConnectionModel daAssociation ) {
        Iterator iter = daAssociation.getLabelNodes().iterator();
        DiagramModelNode nextLabelNode = null;
        DiagramEditPart nextEditPart = null;
        while (iter.hasNext()) {
            nextLabelNode = (DiagramModelNode)iter.next();
            nextEditPart = DiagramUiUtilities.getDiagramEditPart(this, nextLabelNode);
            if (nextEditPart != null && nextEditPart.getDiagramFigure() != null) {
                nextLabelNode.setSize(nextEditPart.getFigure().getSize());
            }
        }
    }

    private void refreshAssociationLabels( NodeConnectionEditPart ncepPart,
                                           NodeConnectionModel daAssociation,
                                           String sType ) {

        ConnectionAnchor ncaSourceAnchor = null;
        ConnectionAnchor ncaTargetAnchor = null;

        // figure out the source and target edit parts and pass to DiagramAssociation

        if (sType.equals(SOURCE)) {
            ncaSourceAnchor = this.getAnchorManager().getSourceAnchor(ncepPart);
            if (ncaSourceAnchor == null) ncaSourceAnchor = this.getSourceConnectionAnchor(ncepPart);

            // now figure out the target anchor:
            DiagramEditPart depTargetEditPart = (DiagramEditPart)ncepPart.getTarget();

            if (depTargetEditPart != null) {
                ncaTargetAnchor = depTargetEditPart.getTargetConnectionAnchor(ncepPart);
                if (ncaTargetAnchor == null) ncaTargetAnchor = this.getTargetConnectionAnchor(ncepPart);
                if (ncaSourceAnchor != null && ncaTargetAnchor != null) daAssociation.layout(ncaSourceAnchor,
                                                                                             ncaTargetAnchor,
                                                                                             this);
            }
        } else {
            ncaTargetAnchor = this.getAnchorManager().getTargetAnchor(ncepPart);
            if (ncaTargetAnchor == null) ncaTargetAnchor = this.getTargetConnectionAnchor(ncepPart);

            // now figure out the source anchor:
            DiagramEditPart depSourceEditPart = (DiagramEditPart)ncepPart.getSource();

            if (depSourceEditPart != null) {
                ncaSourceAnchor = depSourceEditPart.getSourceConnectionAnchor(ncepPart);
                if (ncaSourceAnchor == null) ncaSourceAnchor = this.getSourceConnectionAnchor(ncepPart);

                if (ncaSourceAnchor != null && ncaTargetAnchor != null) daAssociation.layout(ncaSourceAnchor,
                                                                                             ncaTargetAnchor,
                                                                                             this);
            }
        }

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    public void hiliteBackground( Color hiliteColor ) {
        // Default does nothing;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#shouldHiliteBackground()
     */
    public boolean shouldHiliteBackground( List sourceEditParts ) {
        // Default Behavior
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setAnchorManager(com.metamatrix.modeler.diagram.ui.connection.AnchorManager)
     */
    public void setAnchorManager( AnchorManager anchorManager ) {
        this.anchorManager = anchorManager;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPart#setSelected(int)
     */
    @Override
    public void setSelected( int value ) {
        super.setSelected(value);
        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_SELECTION)) {
            String debugMessage = "Set selected value to " + value + " for EP = " + this; //$NON-NLS-2$ //$NON-NLS-1$
            DiagramUiConstants.Util.print(DebugConstants.DIAGRAM_SELECTION, debugMessage);
        }
        if (value == SELECTED_NONE) showSelected(false);
        else showSelected(true);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#showSelected(boolean)
     */
    public void showSelected( boolean selected ) {
        if (getDiagramFigure() != null) getDiagramFigure().showSelected(selected);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#clearHiliting()
     */
    public void clearHiliting() {
        // Check the children
        List contents = this.getChildren();

        Iterator iter = contents.iterator();
        Object nextObj = null;

        while (iter.hasNext()) {
            nextObj = iter.next();
            if (nextObj instanceof DiagramEditPart) {
                ((DiagramEditPart)nextObj).clearHiliting();
            }
        }
        hiliteBackground(null);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#getEditPartDependencies()
     */
    public List getDependencies() {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#isResizable()
     */
    public boolean isResizable() {
        return canResize;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#setResizable(boolean)
     */
    public void setResizable( boolean canResize ) {
        this.canResize = canResize;
    }

    private boolean shouldDebug() {
        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_CONNECTIONS)
            || DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_EDIT_PARTS)) {
            return true;
        }

        return false;
    }

    private String getEnabledDebugContext() {
        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_CONNECTIONS)) {
            return DebugConstants.DIAGRAM_CONNECTIONS;
        }
        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_EDIT_PARTS)) {
            return DebugConstants.DIAGRAM_EDIT_PARTS;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#updateForPreferences()
     */
    public void updateForPreferences() {
        // Default does nothing
    }

    public boolean shouldReveal() {
        return true;
    }

    /**
     * @return
     */
    public Font getCurrentDiagramFont() {
        return currentDiagramFont;
    }

    /**
     * @param font
     */
    public void setCurrentDiagramFont( Font font ) {
        currentDiagramFont = font;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#handleZoomChanged()
     * @since 4.2
     */
    public void handleZoomChanged() {
        // Default does nothing;
    }

    /**
     * @return Returns the dropHelper.
     * @since 4.3
     */
    public DropEditPartHelper getDropHelper() {
        return this.dropHelper;
    }

    /**
     * @param dropHelper The dropHelper to set.
     * @since 4.3
     */
    public void setDropHelper( DropEditPartHelper dropHelper ) {
        this.dropHelper = dropHelper;
    }

    /**
     * Implemented to determine which role container to drop the incoming eObject list.
     * 
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#drop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.2
     */
    public void drop( Point dropPoint,
                      List dropList ) {
        if (dropHelper != null) dropHelper.drop(dropPoint, dropList);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#hilite(boolean)
     * @since 4.2
     */
    public void hilite( boolean hilite ) {
        if (dropHelper != null) dropHelper.hilite(hilite);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#getLastHoverPoint()
     * @since 4.2
     */
    public Point getLastHoverPoint() {
        if (dropHelper != null) return dropHelper.getLastHoverPoint();
        return new Point(0, 0);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#setLastHoverPoint(org.eclipse.draw2d.geometry.Point)
     * @since 4.2
     */
    public void setLastHoverPoint( Point lastHoverPoint ) {
        if (dropHelper != null) dropHelper.setLastHoverPoint(lastHoverPoint);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DropEditPart#allowsDrop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.3
     */
    public boolean allowsDrop( Object target,
                               List dropList ) {
        if (dropHelper != null) return dropHelper.allowsDrop(target, dropList);

        return false;
    }

    public PropertyChangeManager getChangeManager() {
        if (changeManager == null) {
            changeManager = new PropertyChangeManager(this);
        }

        return changeManager;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#isUnderConstruction()
     * @since 5.0
     */
    public boolean isUnderConstruction() {
        return this.underConstruction;
    }

    /**
     * @param theUnderConstruction The underConstruction to set.
     * @since 5.0
     */
    public void setUnderConstruction( boolean theUnderConstruction ) {
        this.underConstruction = theUnderConstruction;
        List contents = this.getChildren();

        Iterator iter = contents.iterator();
        Object nextObj = null;
        while (iter.hasNext()) {
            nextObj = iter.next();
            if (nextObj instanceof DiagramEditPart) {
                ((DiagramEditPart)nextObj).setUnderConstruction(theUnderConstruction);
            }
        }
        getChangeManager().reset();
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#constructionCompleted()
     * @since 5.0
     */
    public void constructionCompleted( boolean updateLinkedParts ) {
        if (this.underConstruction) {
            // Find all it's children and tell them first
            List contents = this.getChildren();

            Iterator iter = contents.iterator();
            Object nextObj = null;
            while (iter.hasNext()) {
                nextObj = iter.next();
                if (nextObj instanceof DiagramEditPart) {
                    ((DiagramEditPart)nextObj).constructionCompleted(updateLinkedParts);
                }
            }
            getChangeManager().executeRefresh(updateLinkedParts);
        }
        setUnderConstruction(false);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshAllLabels(boolean)
     * @since 5.0
     */
    public void refreshAllLabels( boolean theForceRefresh ) {
        this.refreshAllLabels();
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshAnchors(boolean)
     * @since 5.0
     */
    public void refreshAnchors( boolean updateOtherEnds ) {
        this.createOrUpdateAnchorsLocations(updateOtherEnds);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshChildren(boolean)
     * @since 5.0
     */
    public void refreshChildren( boolean theForceRefresh ) {
        this.refreshChildren();
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshPath(boolean)
     * @since 5.0
     */
    public void refreshPath( boolean theForceRefresh ) {
        // NO Op
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshSourceConnections(boolean)
     * @since 5.0
     */
    public void refreshSourceConnections( boolean theForceRefresh ) {
        this.refreshSourceConnections();
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshTargetConnections(boolean)
     * @since 5.0
     */
    public void refreshTargetConnections( boolean theForceRefresh ) {
        this.refreshTargetConnections();
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshVisuals(boolean)
     * @since 5.0
     */
    public void refreshVisuals( boolean theForceRefresh ) {
        this.refreshVisuals();
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#refreshChildren(boolean)
     * @since 5.0
     */
    public void resizeChildren( boolean theForceRefresh ) {
        this.resizeChildren();
    }

}
