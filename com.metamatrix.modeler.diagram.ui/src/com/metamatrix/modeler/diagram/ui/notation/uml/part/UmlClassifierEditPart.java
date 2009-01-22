/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.part;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.AccessibleAnchorProvider;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditorUtil;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.figure.ExpandableFigure;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierContainerFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierContainerNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.model.UmlClassifierNode;
import com.metamatrix.modeler.diagram.ui.part.AbstractNotationEditPart;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.ExpandableDiagram;
import com.metamatrix.modeler.diagram.ui.part.PropertyChangeManager;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.HiliteDndNodeSelectionEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.SelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditFigure;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartManager;
import com.metamatrix.modeler.diagram.ui.util.directedit.LabelCellEditorLocator;
import com.metamatrix.modeler.internal.diagram.ui.DebugConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.event.IRevealHideListener;

/**
 * UmlClassifierEditPart
 */
public class UmlClassifierEditPart extends AbstractNotationEditPart implements
                                                                   DirectEditPart {

    // ===========================================================================================================================
    // Constants

    private static final int ARROW_BORDER_5 = 5; // Pixels around collapse arrow that will be still be selectable
    private static final int ARROW_BORDER_8 = 8; // Pixels around collapse arrow that will be still be selectable

    // ===========================================================================================================================
    // Variables

    private DragTracker myDragTracker = null;
    private DirectEditManager manager;
    private IRevealHideListener revealHideListener;

    // ===========================================================================================================================
    // Constructors

    public UmlClassifierEditPart() {
        super();
    }

    public UmlClassifierEditPart(String diagramTypeId) {
        super();
        setDiagramTypeId(diagramTypeId);
        init();
    }

    // ===========================================================================================================================
    // Methods

    /*
     * Classifier should only show arrow if it can collapse and that is determined by it's parent. This will solve the problem of
     * nested classifieres too.
     */
    private boolean canCollapse() {
        boolean bResult = (this.getParent() instanceof ExpandableDiagram);

        return bResult;
    }

    private boolean clickedOnArrow() {
        if (getDiagramFigure() instanceof ExpandableFigure) {
            SelectionTracker tracker = (SelectionTracker)getDragTracker(null);
            if (tracker != null && tracker.getLastMouseLocation() != null) {

                DiagramViewer dvViewer = null;

                // jh Fix for Defect 21054
                // since getViewer is documented to throw an NPE, catch it and
                // treat it as a false condition
                try {
                    dvViewer = (DiagramViewer)getViewer();
                } catch (NullPointerException npe) {
                    return false;
                }

                if (dvViewer == null) {
                    return false;
                }

                DiagramEditor deEditor = dvViewer.getEditor();

                double zoomFactor = deEditor.getCurrentZoomFactor();

                Point rawMousePoint = new Point(tracker.getLastMouseLocation());
                Point mousePoint = new Point(rawMousePoint.x / zoomFactor, rawMousePoint.y / zoomFactor);
                Point rawViewportLoc = ((DiagramViewer)getViewer()).getViewportLocation();
                Point viewportLoc = new Point(rawViewportLoc.x / zoomFactor, rawViewportLoc.y / zoomFactor);
                // Let's get the rectangle for the figure for the name in the header...
                ExpandableFigure cFigure = (ExpandableFigure)getDiagramFigure();
                IFigure arrowFigure = cFigure.getExpansionFigure();
                // get x,y offset for nested classifier

                if (arrowFigure != null) {
                    // Get name figure bounds and correct for scrolling and classifier nesting
                    Rectangle arrowRect = new Rectangle(arrowFigure.getBounds());
                    arrowRect.x = arrowRect.x + ((IFigure)cFigure).getBounds().x - ARROW_BORDER_5 - viewportLoc.x;
                    arrowRect.y = arrowRect.y + ((IFigure)cFigure).getBounds().y - ARROW_BORDER_5 - viewportLoc.y;
                    // Let's add a coupleof pixels on the bounds
                    arrowRect.width += ARROW_BORDER_5;
                    arrowRect.height += ARROW_BORDER_8;
                    if (arrowRect.contains(mousePoint)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void collapse() {
        if (canCollapse()) {

            // must grab the child EObjects before we execute the collapse
            List lstChildEObjects = getChildEObjects();

            // System.out.println("[UmlClassifierEditPart.collapse] about to call collapse on: " + getDiagramFigure() );
            ((UmlClassifierFigure)getDiagramFigure()).collapse();
            refresh();
            layout(true);

            // if in a MappingClassDiagram, synchronize with the tree
            if (getRevealHideListener() != null 
             && getRevealHideListener().isRevealHideBehaviorEnabled() 
             && lstChildEObjects != null 
             && lstChildEObjects != Collections.EMPTY_LIST) {
                // get the list of newly revealed EObjects

                // announce them to the reveal listener
                getRevealHideListener().notifyElementsHidden(this, lstChildEObjects);
            }
        }
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies() You need to tell how children nodes will be layed
     *      out...
     */
    @Override
    protected void createEditPolicies() {
        setPrimaryParent(true);
        installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new NonResizableEditPolicy());
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new HiliteDndNodeSelectionEditPolicy());
        installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPartEditPolicy());
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {

        Point posn = new Point(100, 100);
        UmlClassifierFigure newFigure = (UmlClassifierFigure)getFigureGenerator().createFigure(getModel());
        UmlClassifierNode model = (UmlClassifierNode)getModel();
        if (model.getParent() instanceof ExpandableDiagram && ((ExpandableDiagram)model.getParent()).canExpand()) {
            newFigure.setExpandable(true);
        }

        newFigure.setLocation(posn);
        Image firstOverlayImage = model.getFirstOverlayImage();
        Image secondOverlayImage = model.getSecondOverlayImage();
        if (firstOverlayImage != null) {
            newFigure.addEditButton(firstOverlayImage);
        }
        if (secondOverlayImage != null) {
            newFigure.addImage(secondOverlayImage, DiagramUiConstants.Position.UPPER_RIGHT);
        }

        // let's check to see if the name is italisized?
        int nameFontStyle = ScaledFont.BOLD_STYLE;

        if (model.isAbstract()) {
            nameFontStyle = ScaledFont.BOLD_ITALICS_STYLE;
        }

        newFigure.setNameFontStyle(nameFontStyle);

        return newFigure;
    }

    public boolean doubleClickedName() {
        SelectionTracker tracker = (SelectionTracker)getDragTracker(null);
        if (tracker != null && tracker.getLastMouseLocation() != null) {
            double zoomFactor = ((DiagramViewer)getViewer()).getEditor().getCurrentZoomFactor();
            Point rawMousePoint = new Point(tracker.getLastMouseLocation());
            Point mousePoint = new Point(rawMousePoint.x / zoomFactor, rawMousePoint.y / zoomFactor);
            Point rawViewportLoc = ((DiagramViewer)getViewer()).getViewportLocation();
            Point viewportLoc = new Point(rawViewportLoc.x / zoomFactor, rawViewportLoc.y / zoomFactor);
            // Let's get the rectangle for the figure for the name in the header...
            UmlClassifierFigure cFigure = (UmlClassifierFigure)getDiagramFigure();
            IFigure nameFigure = cFigure.getNameFigure();
            // get x,y offset for nested classifier
            Point nestedOffset = DiagramUiUtilities.getNestedRelativeLocation(this);

            if (nameFigure != null) {
                // Get name figure bounds and correct for scrolling and classifier nesting
                Rectangle nameRect = new Rectangle(nameFigure.getBounds());
                nameRect.x = nameRect.x + cFigure.getBounds().x - 3 - viewportLoc.x + nestedOffset.x;
                nameRect.y = nameRect.y + cFigure.getBounds().y - 3 - viewportLoc.y + nestedOffset.y;
                // Let's add a coupleof pixels on the bounds
                nameRect.width += 6;
                nameRect.height += 6;
                if (nameRect.contains(mousePoint)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.EditableEditPart#edit()
     */
    public void edit() {
        if (!clickedOnArrow()) {
            if (doubleClickedName()) {
                if (getSelectionHandler().shouldRename(getModelObject())) {
                    if (ModelerCore.getModelEditor().hasName(getModelObject())
                        && !ModelObjectUtilities.isReadOnly(getModelObject()))
                        performDirectEdit();
                }
            } else {
                // Let's delegate to the modelEditorManager here
                ModelEditorManager.edit(getModelObject());
            }
        }
    }

    public void expand() {
        if (canCollapse()) {
            // System.out.println("[UmlClassifierEditPart.collapse] about to call expand on: " + this );
            ((UmlClassifierFigure)getDiagramFigure()).expand();
            refresh();
            layout(true);
            Dimension newSize = getFigure().getSize();
            ((DiagramModelNode)getModel()).setSize(newSize);

            // if in a MappingClassDiagram, synchronize with the tree
            if ( getRevealHideListener() != null 
              && getRevealHideListener().isRevealHideBehaviorEnabled() ) { 

                // announce them to the reveal listener
                getRevealHideListener().notifyElementsRevealed(this, getChildEObjects());
            }
        }
    }

    @Override
    public Object getAdapter(Class key) {
        if (key == AccessibleAnchorProvider.class)
            return new DefaultAccessibleAnchorProvider() {

                @Override
                public List getSourceAnchorLocations() {
                    List list = new ArrayList();
                    Dimension thisSize = getFigure().getSize();

                    list.add(new Point(0, 0));
                    list.add(new Point(thisSize.width / 2, 0));
                    list.add(new Point(thisSize.width, 0));
                    list.add(new Point(thisSize.width, thisSize.height / 2));
                    list.add(new Point(thisSize.width, thisSize.height));
                    list.add(new Point(thisSize.width / 2, thisSize.height));
                    list.add(new Point(0, thisSize.height));
                    list.add(new Point(0, thisSize.height / 2));

                    return list;
                }

                @Override
                public List getTargetAnchorLocations() {
                    List list = new ArrayList();
                    Dimension thisSize = getFigure().getSize();

                    list.add(new Point(0, 0));
                    list.add(new Point(thisSize.width / 2, 0));
                    list.add(new Point(thisSize.width, 0));
                    list.add(new Point(thisSize.width, thisSize.height / 2));
                    list.add(new Point(thisSize.width, thisSize.height));
                    list.add(new Point(thisSize.width / 2, thisSize.height));
                    list.add(new Point(0, thisSize.height));
                    list.add(new Point(0, thisSize.height / 2));
                    return list;
                }
            };
        return super.getAdapter(key);
    }

    private List getChildEObjects() {
        List lstChildren = Collections.EMPTY_LIST;
        ArrayList arylEObjects = new ArrayList();

        if (getChildren() != null && !getChildren().isEmpty()) {

            UmlClassifierContainerEditPart container = (UmlClassifierContainerEditPart)getChildren().get(0);

            lstChildren = container.getChildren();
            Iterator it = lstChildren.iterator();

            while (it.hasNext()) {
                Object oTemp = it.next();

                if (oTemp instanceof UmlAttributeEditPart) {
                    UmlAttributeEditPart attr = (UmlAttributeEditPart)oTemp;
                    // System.out.println("[UmlClassifierEditPart.getChildEObjects] a child ModelObject: " + attr.getModelObject()
                    // );
                    arylEObjects.add(attr.getModelObject());
                }
            }
            lstChildren = arylEObjects;
        }

        return lstChildren;
    }

    /**
     * This method is not mandatory to implement, but if you do not implement it, you will not have the ability to
     * rectangle-selects several figures...
     */
    @Override
    public DragTracker getDragTracker(Request req) {
        // Unlike in Logical Diagram Editor example, I use a singleton because this
        // method is Entered >> several time, so I prefer to save memory ; and it works!
        if (myDragTracker == null) {
            myDragTracker = new SelectionTracker(this);
        }
        return myDragTracker;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart#getEditManager()
     */
    public DirectEditPartManager getEditManager() {
        return (DirectEditPartManager)manager;
    }

    public String getEditString() {
        return ((DiagramModelNode)getModel()).getName();
    }

    private Label getLabel() {
        Label label = null;
        if (getFigure() instanceof DirectEditFigure) {
            label = ((DirectEditFigure)getFigure()).getLabelFigure();
        }
        return label;
    }

    public IRevealHideListener getRevealHideListener() {
        if ( revealHideListener == null ) {

            DiagramController deController = ((DiagramViewer)getViewer()).getEditor().getDiagramController();
            
            if ( deController instanceof IRevealHideListener ) {
                revealHideListener = (IRevealHideListener)deController;
            }
        }
        
        return revealHideListener;
    }

    
    public String getText() {
        return ((DiagramModelNode)getModel()).getName();
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    @Override
    public void hiliteBackground(Color hiliteColor) {
        getDiagramFigure().hiliteBackground(hiliteColor);
    }

    public void init() {
        if (getAnchorManager() == null) {
            setAnchorManager(getEditPartFactory().getAnchorManager(this));
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#layout(boolean)
     */
    @Override
    public void layout(boolean layoutChildren) {
        // Need to set the stack order of the containers....
        // Get the children of this edit part
        // If container edit parts, get the model nodes
        resetContainerStackIndex();
        super.layout(layoutChildren);
    }

    public void performDirectEdit() {
        if (manager == null)
            manager = new DirectEditPartManager(this, TextCellEditor.class, new LabelCellEditorLocator(getLabel()));
        manager.show();
    }

    @Override
    public void performRequest(Request request) {
        if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
            if (!(getSelectionHandler().handleDoubleClick(this.getModelObject()))) {
                if (ModelerCore.getModelEditor().hasName(getModelObject()))
                    performDirectEdit();
            }
        } else if (request.getType() == RequestConstants.REQ_SELECTION && canCollapse()) {
            if (clickedOnArrow()) {
                if (((UmlClassifierNode)getModel()).isExpanded())
                    ((UmlClassifierNode)getModel()).collapse();
                else
                    ((UmlClassifierNode)getModel()).expand();
            }
        }
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //boolean refresh = false;
        // 
        String prop = evt.getPropertyName();

        super.propertyChange(evt);
        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LAYOUT)) {
            getChangeManager().refresh(PropertyChangeManager.LAYOUT_ALL, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            getChangeManager().refresh(PropertyChangeManager.RESIZE_CHILDREN, false);
            getChangeManager().refresh(PropertyChangeManager.GENERAL, false);
            if( !((DiagramModelNode)getModel()).getSourceConnections().isEmpty() ||
                            !((DiagramModelNode)getModel()).getTargetConnections().isEmpty()  ) {
                            ((DiagramModelNode)getModel()).updateAssociations();
                        }
                        
            getChangeManager().refresh(PropertyChangeManager.LABELS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.IMAGES)) {
            refreshImages();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.CONNECTION)) {
            if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_EDIT_PARTS)) {
                DiagramUiConstants.Util.print(DebugConstants.DIAGRAM_EDIT_PARTS,
                                              "Connection Changed for EditPart Model = " + ((DiagramModelNode)getModel()).getName()); //$NON-NLS-1$
            }
            
            getChangeManager().refresh(PropertyChangeManager.SOURCE_CONNECTIONS, false);
            getChangeManager().refresh(PropertyChangeManager.TARGET_CONNECTIONS, false);
            getChangeManager().refresh(PropertyChangeManager.ANCHORS, false);
            getChangeManager().refresh(PropertyChangeManager.GENERAL, false);
            getChangeManager().refresh(PropertyChangeManager.LABELS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION) ){
            if( !((DiagramModelNode)getModel()).getSourceConnections().isEmpty() ||
                !((DiagramModelNode)getModel()).getTargetConnections().isEmpty()  ) {
                ((DiagramModelNode)getModel()).updateAssociations();
            }
            
            getChangeManager().refresh(PropertyChangeManager.LABELS, false);
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.IMAGES)) {
            refreshImages();
        }

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.PATH)) {
            refreshPath();
            getChangeManager().refresh(PropertyChangeManager.RESIZE_CHILDREN, false);
        }

        // If parent is ExpandableDiagram
        if (canCollapse()) {
            if (prop.equals(DiagramUiConstants.DiagramNodeProperties.COLLAPSE)) {
                // Let's control construction refresh()
                Diagram diagram = ((DiagramModelNode)this.getModel()).getDiagram();
                boolean handleConstruction = ! DiagramEditorUtil.isDiagramUnderConstruction( diagram );
                if( handleConstruction ) {
                    DiagramEditorUtil.setDiagramUnderConstruction( diagram );
                }
                collapse();
                getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
                getChangeManager().refresh(PropertyChangeManager.ANCHORS, false);
                if( handleConstruction ) {
                    DiagramEditorUtil.setDiagramConstructionComplete( diagram, true );
                }
            }

            if (prop.equals(DiagramUiConstants.DiagramNodeProperties.EXPAND)) {
                Diagram diagram = ((DiagramModelNode)this.getModel()).getDiagram();
                boolean handleConstruction = ! DiagramEditorUtil.isDiagramUnderConstruction( diagram );
                if( handleConstruction ) {
                    DiagramEditorUtil.setDiagramUnderConstruction( diagram );
                }
                expand();
                getChangeManager().refresh(PropertyChangeManager.VISUALS, false);
                getChangeManager().refresh(PropertyChangeManager.ANCHORS, false);
                if( handleConstruction ) {
                    DiagramEditorUtil.setDiagramConstructionComplete( diagram, true );
                }
            }
        }
    }

    public void refreshImages() {
        DiagramModelNode dmn = (DiagramModelNode)getModel();
        if (dmn != null) {
            UmlClassifierFigure figure = (UmlClassifierFigure)getDiagramFigure();
            figure.addImage(dmn.getSecondOverlayImage(), DiagramUiConstants.Position.UPPER_RIGHT);
            Button button = figure.getEditButton();
            if (button != null) {
                for (Iterator iter = button.getChildren().iterator(); iter.hasNext();) {
                    Object obj = iter.next();
                    if (obj instanceof Label) {
                        ((Label)obj).setIcon(dmn.getFirstOverlayImage());
                        break;
                    }
                }
            }
            layout(true);
        }

    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.DiagramEditPart#resizeChildren()
     */
    @Override
    public void refreshName() {
        // Now we need to update the abstract title....
        // let's check to see if the name is italisized?
        int nameFontStyle = ScaledFont.BOLD_STYLE;

        if (((UmlClassifierNode)getModel()).isAbstract()) {
            nameFontStyle = ScaledFont.BOLD_ITALICS_STYLE;
        }

        ((UmlClassifierFigure)getDiagramFigure()).setNameFontStyle(nameFontStyle);
        // Need to get the figure and update the name
        super.refreshName();
    }

    public void refreshPath() {
        ((UmlClassifierFigure)getDiagramFigure()).updateForPath(((UmlClassifierNode)getModel()).getPath());
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
     */
    @Override
    protected void refreshVisuals() {
        Point loc = ((DiagramModelNode)getModel()).getPosition();
        Dimension size = ((DiagramModelNode)getModel()).getSize();
        Rectangle r = new Rectangle(loc, size);

        ((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), r);
        getFigure().repaint();
        if (DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_EDIT_PARTS)) {
            String message = "SIZE = " + size; //$NON-NLS-1$
            DiagramUiConstants.Util.print(DebugConstants.DIAGRAM_EDIT_PARTS, message);
        }
    }

    private void resetContainerStackIndex() {
        Iterator iter = getChildren().iterator();
        EditPart nextChild = null;
        while (iter.hasNext()) {
            nextChild = (EditPart)iter.next();
            if (nextChild instanceof UmlClassifierContainerEditPart) {
                UmlClassifierContainerNode cn = (UmlClassifierContainerNode)((DiagramEditPart)nextChild).getModel();
                UmlClassifierContainerFigure cf = (UmlClassifierContainerFigure)((DiagramEditPart)nextChild).getDiagramFigure();
                cf.setStackOrderValue(cn.getType());
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#resizeChildren()
     */
    @Override
    public void resizeChildren() {
        getDiagramFigure().updateForSize(((DiagramModelNode)getModel()).getSize());
    }

    public void setRevealHideListener(IRevealHideListener listener) {
        this.revealHideListener = listener;
    }

    public void setText(String newName) {
        ((DiagramModelNode)getModel()).setName(newName);
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.part.DiagramEditPart#shouldHiliteBackground()
     */
    @Override
    public boolean shouldHiliteBackground(List sourceEditParts) {
        // We'll start off by checking to see that list of sourceEditParts do not have the same parent as this.
        return true;
    }
}
