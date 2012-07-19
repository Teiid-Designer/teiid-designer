/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.part;

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
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.actions.ScaledFontManager;
import org.teiid.designer.diagram.ui.connection.AnchorManager;
import org.teiid.designer.diagram.ui.connection.NodeConnectionEditPart;
import org.teiid.designer.diagram.ui.connection.NodeConnectionModel;
import org.teiid.designer.diagram.ui.editor.DiagramEditorUtil;
import org.teiid.designer.diagram.ui.figure.DiagramFigure;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.diagram.ui.util.DiagramUiUtilities;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.metamodels.diagram.DiagramLinkType;


/**
 * AbstractDefaultEditPart
 *
 * @since 8.0
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
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#getModelObject()
    **/
    @Override
	public EObject getModelObject() {
        return ((DiagramModelNode)getModel()).getModelObject();
    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#getDiagramFigure()
    **/
    @Override
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
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#clearSelections(boolean)
    **/
    @Override
	public void clearSelections( boolean clearSubSelections ) {
    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#layout(boolean)
    **/
    @Override
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
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#layout(boolean)
    **/
    @Override
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
    @Override
	public void propertyChange( PropertyChangeEvent evt ) {

        String prop = evt.getPropertyName();

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CHILDREN)) {
            getChangeManager().refresh(PropertyChangeManager.GENERAL, true);
            getChangeManager().refresh(PropertyChangeManager.LAYOUT_ALL, true);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
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

    @Override
	public void updateContent() {

    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#updateModelLocation()
    **/
    @Override
	public void updateModelPosition() {
        ((DiagramModelNode)getModel()).setPosition(new Point(getFigure().getBounds().x, getFigure().getBounds().y));
    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#updateModelSize()
    **/
    @Override
	public void updateModelSize() {
        ((DiagramModelNode)getModel()).setSize(getFigure().getSize());
    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#resizeChildren()
    **/
    @Override
	public void resizeChildren() {
        // default implementation does nothing
    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#resizeChildren()
    **/
    @Override
	public void refreshName() {
        // Need to get the figure and update the name
        getDiagramFigure().updateForName(((DiagramModelNode)getModel()).getName());
    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#selectPrimaryParent()
    **/
    @Override
	public void selectPrimaryParent() {
        DiagramEditPart editPart = getPrimaryParent();
        if (editPart != null) ((EditPart)editPart).setSelected(EditPart.SELECTED_PRIMARY);
    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#getPrimaryParent()
    **/
    @Override
	public DiagramEditPart getPrimaryParent() {
        if (getParent() instanceof DiagramEditPart) {
            if (((DiagramEditPart)getParent()).isPrimaryParent()) return (DiagramEditPart)getParent();
            return getPrimaryParent();
        }
        return null;
    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#isPrimaryParent()
    **/
    @Override
	public boolean isPrimaryParent() {
        return isPrimary;
    }

    protected void setPrimaryParent( boolean primary ) {
        isPrimary = primary;
    }

    /* (non-JavaDoc)
     * @See org.teiid.designer.diagram.ui.DiagramEditPart#isPrimaryParent()
    **/
    @Override
	public boolean isSelectablePart() {
        return isSelectable;
    }

    protected void setSelectablePart( boolean selectable ) {
        isSelectable = selectable;
    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#getEditPartFactory()
     */
    @Override
	public DiagramEditPartFactory getEditPartFactory() {
        return DiagramUiPlugin.getDiagramTypeManager().getDiagram(getDiagramTypeId()).getEditPartFactory();
    }

    /*
     *  (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#getEditPart(org.teiid.designer.diagram.ui.model.DiagramModelNode)
     */
    @Override
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
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#getEditPart(org.eclipse.emf.ecore.EObject)
     */
    @Override
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
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#getNotationId()
     */
    @Override
	public String getNotationId() {
        return sNotationId;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#setNotationId(java.lang.String)
     */
    @Override
	public void setNotationId( String sNotationId ) {
        this.sNotationId = sNotationId;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#getNotationId()
     */
    @Override
	public String getDiagramTypeId() {
        return sDiagramTypeId;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPartFactory#setNotationId(java.lang.String)
     */
    @Override
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

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
     **/
    @Override
    protected List getModelSourceConnections() {
        List sourceConnections;

        sourceConnections = ((DiagramModelNode)getModel()).getSourceConnections();
        return sourceConnections;
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
     **/
    @Override
    protected List getModelTargetConnections() {
        return ((DiagramModelNode)getModel()).getTargetConnections();
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(ConnectionEditPart)
     **/
    @Override
	public ConnectionAnchor getSourceConnectionAnchor( ConnectionEditPart connection ) {
        if (this.getAnchorManager() != null) return this.getAnchorManager().getSourceAnchor((NodeConnectionEditPart)connection);

        return null;
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(Request)
     **/
    @Override
	public ConnectionAnchor getSourceConnectionAnchor( Request request ) {
        // Somehow translate this request to a specific connection
        // ConnectionEditPart someConnection = null;
        // return anchorManager.getSourceAnchor((NodeConnectionEditPart)someConnection);
        return null;
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(ConnectionEditPart)
     **/
    @Override
	public ConnectionAnchor getTargetConnectionAnchor( ConnectionEditPart connection ) {
        if (this.getAnchorManager() != null) return this.getAnchorManager().getTargetAnchor((NodeConnectionEditPart)connection);

        return null;
    }

    /**
     * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(Request)
     **/
    @Override
	public ConnectionAnchor getTargetConnectionAnchor( Request request ) {
        // Somehow translate this request to a specific connection
        // ConnectionEditPart someConnection = null;
        // return anchorManager.getTargetAnchor((NodeConnectionEditPart)someConnection);
        return null;
    }

    @Override
	public AnchorManager getAnchorManager() {
        return anchorManager;
    }

    /**
     * Set the anchors location based on current figure's rectangle size.
     **/
    @Override
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
    @Override
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
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    @Override
	public void hiliteBackground( Color hiliteColor ) {
        // Default does nothing;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#shouldHiliteBackground()
     */
    @Override
	public boolean shouldHiliteBackground( List sourceEditParts ) {
        // Default Behavior
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#setAnchorManager(org.teiid.designer.diagram.ui.connection.AnchorManager)
     */
    @Override
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
        if (value == SELECTED_NONE) showSelected(false);
        else showSelected(true);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#showSelected(boolean)
     */
    @Override
	public void showSelected( boolean selected ) {
        if (getDiagramFigure() != null) getDiagramFigure().showSelected(selected);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#clearHiliting()
     */
    @Override
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
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#getEditPartDependencies()
     */
    @Override
	public List getDependencies() {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#isResizable()
     */
    @Override
	public boolean isResizable() {
        return canResize;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#setResizable(boolean)
     */
    @Override
	public void setResizable( boolean canResize ) {
        this.canResize = canResize;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.part.DiagramEditPart#updateForPreferences()
     */
    @Override
	public void updateForPreferences() {
        // Default does nothing
    }

    @Override
	public boolean shouldReveal() {
        return true;
    }

    /**
     * @return
     */
    @Override
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
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#handleZoomChanged()
     * @since 4.2
     */
    @Override
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
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#drop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.2
     */
    @Override
	public void drop( Point dropPoint,
                      List dropList ) {
        if (dropHelper != null) dropHelper.drop(dropPoint, dropList);
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#hilite(boolean)
     * @since 4.2
     */
    @Override
	public void hilite( boolean hilite ) {
        if (dropHelper != null) dropHelper.hilite(hilite);
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#getLastHoverPoint()
     * @since 4.2
     */
    @Override
	public Point getLastHoverPoint() {
        if (dropHelper != null) return dropHelper.getLastHoverPoint();
        return new Point(0, 0);
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#setLastHoverPoint(org.eclipse.draw2d.geometry.Point)
     * @since 4.2
     */
    @Override
	public void setLastHoverPoint( Point lastHoverPoint ) {
        if (dropHelper != null) dropHelper.setLastHoverPoint(lastHoverPoint);
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DropEditPart#allowsDrop(org.eclipse.draw2d.geometry.Point, java.util.List)
     * @since 4.3
     */
    @Override
	public boolean allowsDrop( Object target,
                               List dropList ) {
        if (dropHelper != null) return dropHelper.allowsDrop(target, dropList);

        return false;
    }

    @Override
	public PropertyChangeManager getChangeManager() {
        if (changeManager == null) {
            changeManager = new PropertyChangeManager(this);
        }

        return changeManager;
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#isUnderConstruction()
     * @since 5.0
     */
    @Override
	public boolean isUnderConstruction() {
        return this.underConstruction;
    }

    /**
     * @param theUnderConstruction The underConstruction to set.
     * @since 5.0
     */
    @Override
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
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#constructionCompleted()
     * @since 5.0
     */
    @Override
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
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#refreshAllLabels(boolean)
     * @since 5.0
     */
    @Override
	public void refreshAllLabels( boolean theForceRefresh ) {
        this.refreshAllLabels();
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#refreshAnchors(boolean)
     * @since 5.0
     */
    @Override
	public void refreshAnchors( boolean updateOtherEnds ) {
        this.createOrUpdateAnchorsLocations(updateOtherEnds);
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#refreshChildren(boolean)
     * @since 5.0
     */
    @Override
	public void refreshChildren( boolean theForceRefresh ) {
        this.refreshChildren();
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#refreshPath(boolean)
     * @since 5.0
     */
    @Override
	public void refreshPath( boolean theForceRefresh ) {
        // NO Op
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#refreshSourceConnections(boolean)
     * @since 5.0
     */
    @Override
	public void refreshSourceConnections( boolean theForceRefresh ) {
        this.refreshSourceConnections();
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#refreshTargetConnections(boolean)
     * @since 5.0
     */
    @Override
	public void refreshTargetConnections( boolean theForceRefresh ) {
        this.refreshTargetConnections();
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#refreshVisuals(boolean)
     * @since 5.0
     */
    @Override
	public void refreshVisuals( boolean theForceRefresh ) {
        this.refreshVisuals();
    }

    /**
     * @see org.teiid.designer.diagram.ui.part.DiagramEditPart#refreshChildren(boolean)
     * @since 5.0
     */
    @Override
	public void resizeChildren( boolean theForceRefresh ) {
        this.resizeChildren();
    }

}
