/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.part.EditableEditPart;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPart;
import com.metamatrix.modeler.diagram.ui.util.directedit.DirectEditPartManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

/**
 * DiagramSelectionHandler
 */
public class DiagramSelectionHandler implements IDiagramSelectionHandler {
    private DiagramViewer viewer;
    private boolean clearHilites = true;

    /**
     * Construct an instance of DiagramSelectionHandler.
     */
    public DiagramSelectionHandler( DiagramViewer viewer ) {
        super();
        this.viewer = viewer;
    }

    public DiagramViewer getViewer() {
        return this.viewer;
    }

    public void deselectAll() {
        if (!getViewer().getSelectedEditParts().isEmpty()) {
            getViewer().deselectAll();
        }
    }

    public void select( EObject selectedObject ) {
        EditPart selectedPart = findEditPart(selectedObject, false);
        if (selectedPart != null) {
            deselectAll();
            getViewer().select(selectedPart);
        }
    }

    public void select( List selectedEObjects ) {
        // System.out.println("[DiagramSelectionHandler.select 2] TOP");
        List selectedEPs = new ArrayList(selectedEObjects.size());
        Iterator iter = selectedEObjects.iterator();
        EObject nextEO = null;
        EditPart nextEP = null;
        while (iter.hasNext()) {
            nextEO = (EObject)iter.next();
            if (nextEO != null) {
                nextEP = findEditPart(nextEO, false);
                if (nextEP != null) selectedEPs.add(nextEP);
            }
        }
        if (!selectedEPs.isEmpty()) {
            deselectAll();
            // System.out.println("[DiagramSelectionHandler.select 2] About to call viewer.select() at end of select 2");
            getViewer().select(selectedEPs);
        }
        // else {
        // System.out.println("[DiagramSelectionHandler.select 2] WILL NOT!! call viewer.select() at end of select 2");
        // }
        // System.out.println("[DiagramSelectionHandler.select 2] BOT");
    }

    private boolean allSelectedAreEObjects( List objects ) {
        Iterator iter = objects.iterator();
        Object obj = null;
        while (iter.hasNext()) {
            obj = iter.next();
            if (!(obj instanceof EObject)) {
                return false;
            }
        }
        return true;
    }

    public List getSelectedEObjects() {
        List selectedEObjects = new ArrayList();
        Iterator iter = getViewer().getSelectedEditParts().iterator();
        Object obj = null;
        EObject eObj = null;
        while (iter.hasNext()) {
            obj = iter.next();
            if (obj instanceof DiagramEditPart) {
                eObj = ((DiagramEditPart)obj).getModelObject();
                if (eObj != null && !selectedEObjects.contains(eObj)) selectedEObjects.add(eObj);
            } else if (obj instanceof NodeConnectionEditPart) {
                eObj = ((NodeConnectionModel)((NodeConnectionEditPart)obj).getModel()).getModelObject();
                if (eObj != null && !selectedEObjects.contains(eObj)) selectedEObjects.add(eObj);
            }
        }
        return selectedEObjects;
    }

    public void select( ISelection selection ) {
        if (SelectionUtilities.isEmptySelection(selection)) {
            deselectAll();
            return;
        }
        List selectedObjects = SelectionUtilities.getSelectedObjects(selection);
        // Now let's make sure they are all eObjects.
        if (allSelectedAreEObjects(selectedObjects)) {
            if (selectedObjects.size() == 1) {
                select((EObject)selectedObjects.iterator().next());
            } else {
                select(selectedObjects);
            }
        }

    }

    public void clearDependencyHilites() {
        // get all parts and call clearHiliting();
        if (viewerContainsPart()) {

            List contents = getViewer().getRootEditPart().getChildren();

            Iterator iter = contents.iterator();

            Object nextObj = null;
            while (iter.hasNext()) {
                nextObj = iter.next();
                if (nextObj instanceof DiagramEditPart) {
                    ((DiagramEditPart)nextObj).clearHiliting();
                }
            }
        }
    }

    public void hiliteDependencies( Object selectedObject ) {
        clearDependencyHilites();
        if (selectedObject == null) clearConnectionHilites();

        if (selectedObject != null && selectedObject instanceof EObject) {
            EObject selectedEObject = (EObject)selectedObject;

            EditPart selectedPart = findEditPart(selectedEObject, false);

            if (selectedPart != null && selectedPart instanceof DiagramEditPart) {

                List allDependencies = ((DiagramEditPart)selectedPart).getDependencies();
                if (allDependencies != null) {
                    Iterator iter = allDependencies.iterator();
                    EditPart nextEP = null;
                    EObject nextEObject = null;
                    while (iter.hasNext()) {
                        nextEObject = (EObject)iter.next();
                        nextEP = findEditPart(nextEObject, false);
                        if (nextEP != null && nextEP instanceof DiagramEditPart) {
                            ((DiagramEditPart)nextEP).hiliteBackground(DiagramUiConstants.Colors.DEPENDENCY);
                        }
                    }
                }
            }
        } else if (selectedObject != null && selectedObject instanceof NodeConnectionEditPart) {
            // let's get the two ends here....
            EditPart sourceEP = DiagramUiUtilities.getSourceEndEditPart((NodeConnectionEditPart)selectedObject);
            EditPart targetEP = DiagramUiUtilities.getTargetEndEditPart((NodeConnectionEditPart)selectedObject);
            if (sourceEP != null && sourceEP instanceof DiagramEditPart) ((DiagramEditPart)sourceEP).hiliteBackground(DiagramUiConstants.Colors.DEPENDENCY);
            if (targetEP != null && sourceEP instanceof DiagramEditPart) ((DiagramEditPart)targetEP).hiliteBackground(DiagramUiConstants.Colors.DEPENDENCY);
        }
        // System.out.println("[DiagramSelectionHandler.hiliteDependencies] BOT");
    }

    public void selectAndReveal( EObject selectedObject ) {

    }

    public EditPart findEditPart( EObject selectedObject,
                                  boolean linksAllowed ) {
        // System.out.println("[DiagramSelectionHandler.findEditPart] TOP; selectedObject: " + selectedObject );
        EditPart matchingPart = null;

        if (viewerContainsPart()) {

            List contents = getViewer().getRootEditPart().getChildren();

            Iterator iter = contents.iterator();

            Object nextObj = null;

            while (iter.hasNext() && matchingPart == null) {
                nextObj = iter.next();
                if (linksAllowed) {
                    matchingPart = ((DiagramEditPart)nextObj).getEditPart(selectedObject, linksAllowed);
                } else {
                    if (nextObj instanceof DiagramEditPart) {
                        if (selectedObject instanceof ModelAnnotation) matchingPart = (DiagramEditPart)nextObj;
                        else matchingPart = ((DiagramEditPart)nextObj).getEditPart(selectedObject, linksAllowed);
                    }
                }
            }
        }

        // System.out.println("[DiagramSelectionHandler.findEditPart] BOT; About to return: matchingPart: " + matchingPart );
        return matchingPart;
    }

    public EditPart findDiagramChildEditPart( EObject selectedObject,
                                              boolean linksAllowed ) {
        if (viewerContainsPart()) {

            List contents = getViewer().getRootEditPart().getChildren();

            Iterator iter = contents.iterator();

            Object diagramEP = null;

            while (iter.hasNext()) {
                diagramEP = iter.next();
                if (diagramEP instanceof EditPart) {
                    // Check the children
                    List dContents = ((EditPart)diagramEP).getChildren();

                    Iterator innerIter = dContents.iterator();
                    EditPart nextEP = null;

                    while (innerIter.hasNext()) {
                        nextEP = (EditPart)innerIter.next();
                        if (nextEP instanceof DiagramEditPart) {
                            if (((DiagramEditPart)nextEP).getModelObject().equals(selectedObject)) return nextEP;
                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean handleDoubleClick( EObject selectedObject ) {
        boolean handledHere = false;
        if (getViewer() != null) {
            final EditPart selectedEP = findEditPart(selectedObject, false);
            if (selectedEP != null && selectedEP instanceof EditableEditPart) {
                // Check to see if we want to direct edit
                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                    public void run() {
                        ((EditableEditPart)selectedEP).edit();
                    }
                });
                handledHere = true;
            }
        }

        return handledHere;
    }

    public String getDiagramType() {
        if (getViewer().getEditor().getDiagram() != null) return getViewer().getEditor().getDiagram().getType();

        return "UNKNOWN TYPE"; //$NON-NLS-1$
    }

    public boolean shouldReveal( EObject eObject ) {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler#hiliteConnection(null)
     */
    public void hiliteConnection( NodeConnectionEditPart connectionEditPart ) {
        clearConnectionHilites();
        connectionEditPart.hilite(true);
    }

    public void clearConnectionHilites() {
        if (!clearHilites) return;

        if (viewerContainsPart()) {

            EditPart rootPart = getViewer().getRootEditPart().getContents();
            List contents = rootPart.getChildren();

            Iterator iter = contents.iterator();

            Object nextObj = null;

            while (iter.hasNext()) {
                nextObj = iter.next();
                if (nextObj instanceof DiagramEditPart) {
                    List connections = ((DiagramEditPart)nextObj).getSourceConnections();
                    Iterator innerIter = connections.iterator();
                    Object nextConn = null;
                    while (innerIter.hasNext()) {
                        nextConn = innerIter.next();
                        if (nextConn instanceof NodeConnectionEditPart) {
                            ((NodeConnectionEditPart)nextConn).hilite(false);
                        }
                    }
                }
            }
        }
    }

    /*
     * Private method added to handle all null checks for viewer. There were some cases where the
     * diagram editor was closing and the diagram cleared out. In this instance, the rootEditPart was 
     * null.  This method takes care of all that for any method in this class that wishes to check viewer content.
     */
    private boolean viewerContainsPart() {
        return (viewer != null && viewer.getRootEditPart() != null && viewer.getRootEditPart().getContents() != null);
    }

    /**
     * @param b
     */
    public void setClearHilites( boolean clear ) {
        clearHilites = clear;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler#fireMouseExit()
     */
    public void fireMouseExit() {
        // This is where we need to locate any DirectEditParts, find out if they have a DEM and
        // find out if it's in the edit mode, then call commit;
        if (viewerContainsPart()) {

            EditPart rootPart = getViewer().getRootEditPart().getContents();
            List contents = rootPart.getChildren();

            Iterator iter = contents.iterator();

            Object nextObj = null;
            DirectEditPartManager depm = null;
            while (iter.hasNext()) {
                nextObj = iter.next();
                if (nextObj instanceof DirectEditPart) {
                    depm = ((DirectEditPart)nextObj).getEditManager();
                    if (depm != null) depm.commitAndDispose();
                }
            }
        }
    }

    public boolean shouldRename( EObject dClickedEObject ) {
        return true;
    }

    /**
     * Defect 19537 - new renameInline() method
     * 
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler#renameInline(org.eclipse.emf.ecore.EObject)
     * @since 5.0.2
     */
    public void renameInline( EObject theSelectedEObject ) {
        if (getViewer() != null) {
            final EditPart selectedEP = findEditPart(theSelectedEObject, false);
            if (selectedEP != null && selectedEP instanceof DirectEditPart) {
                // Check to see if we want to direct edit
                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                    public void run() {
                        ((DirectEditPart)selectedEP).performDirectEdit();
                    }
                });
            }
        }
    }

}
