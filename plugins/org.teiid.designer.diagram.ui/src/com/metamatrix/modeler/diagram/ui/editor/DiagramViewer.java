/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.IDiagramType;
import com.metamatrix.modeler.diagram.ui.PluginConstants;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.pakkage.PackageDiagramContentProvider;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.diagram.ui.printing.DiagramPrintingAnalyzer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;

/**
 * DiagramViewer
 */
public class DiagramViewer extends ScrollingGraphicalViewer {
    private PackageDiagramContentProvider packageDiagramProvider;
    private DiagramEditor editor;
    private boolean inFocus = false;

    public DiagramViewer( DiagramEditor editor ) {
        super();
        this.editor = editor;
        packageDiagramProvider = new PackageDiagramContentProvider();
        setKeyHandler(new DiagramKeyHandler(this));
    }

    @Override
    protected void handleFocusGained( FocusEvent theFe ) {
        super.handleFocusGained(theFe);
        inFocus = true;
    }

    @Override
    protected void handleFocusLost( FocusEvent theFe ) {
        super.handleFocusLost(theFe);
        inFocus = false;
    }

    public boolean hasFocus() {
        return inFocus;
    }

    public ITreeContentProvider getContentProvider() {
        return packageDiagramProvider;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    @Override
    public ISelection getSelection() {
        // Let's interecept the selection in the viewer and get the "ModelObjects"
        if (getSelectedEditParts().isEmpty() && getContents() != null) {
            Object standinObject = null;
            Diagram currentDiagram = editor.getDiagram();
            // Let's ask the DiagramType for it's selection stand-in
            if (currentDiagram != null) {
                IDiagramType idt = DiagramUiPlugin.getDiagramTypeManager().getDiagram(currentDiagram.getType());
                if (idt != null) standinObject = idt.getDiagramSelectionStandin(currentDiagram);
            }

            if (standinObject != null) return new StructuredSelection(standinObject);

            return super.getSelection();
        }
        return new StructuredSelection(getSelectedModelObjects());
    }

    private List getSelectedModelObjects() {
        List modelObjects = new ArrayList(getSelectedEditParts().size());
        List selectedEPs = getSelectedEditParts();
        boolean selectedDiagram = false;

        if (selectedEPs.size() == 1) {
            // Check here to see if "Diagram" was selected
            Object oneObject = selectedEPs.get(0);
            if (oneObject instanceof DiagramEditPart && ((DiagramEditPart)oneObject).getModelObject() instanceof Diagram) {
                // if Package Diagram, get the "target",
                // If not, then do nothing;
                Diagram oneDiagram = (Diagram)((DiagramEditPart)oneObject).getModelObject();
                if (oneDiagram.getType() != null && oneDiagram.getType().equals(PluginConstants.PACKAGE_DIAGRAM_TYPE_ID)) {
                    Object targetObject = getRealDiagramTarget(oneDiagram);
                    if (targetObject != null) {
                        modelObjects.add(targetObject);
                    }
                    selectedDiagram = true;
                }
            }
        }

        if (!selectedDiagram && getSelectionHandler() != null) {
            modelObjects = new ArrayList(getSelectionHandler().getSelectedEObjects());
            // Iterator iter = selectedEPs.iterator();
            // DiagramEditPart nextPart = null;
            // Object nextObject = null;
            // while( iter.hasNext() ) {
            // nextObject = iter.next();
            // if( nextObject instanceof DiagramEditPart ) {
            // nextPart = (DiagramEditPart)nextObject;
            // if( nextPart.getModelObject() != null ) {
            // modelObjects.add(nextPart.getModelObject());
            // if ( DiagramUiConstants.Util.isDebugEnabled(DebugConstants.DIAGRAM_SELECTION) ) {
            //                            String debugMessage = "getSelectionModelObjects():  next model object = " + nextPart.getModelObject(); //$NON-NLS-1$
            // DiagramUiConstants.Util.print(this.getClass(), debugMessage);
            // }
            // }
            //    
            // }
            // }
        }

        return modelObjects;
    }

    public void deselectAll( boolean fireSelectionChanged ) {
        EditPart part;
        List list = primGetSelectedEditParts();
        setFocus(null);
        for (int i = 0; i < list.size(); i++) {
            part = (EditPart)list.get(i);
            part.setSelected(EditPart.SELECTED_NONE);
        }
        list.clear();

        if (fireSelectionChanged) fireSelectionChanged();
    }

    public IDiagramSelectionHandler getSelectionHandler() {
        return editor.getSelectionHandler();
    }

    public DiagramEditor getEditor() {
        return this.editor;
    }

    public void setInput( EObject input ) {
        if (getEditor() != null) {
            // Convert input to Diagram object
            // Let's ask the PackageDiagramContentProvider for it's package diagram??
            ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(input);

            Diagram diagram = packageDiagramProvider.getPackageDiagram(modelResource, input);

            if (diagram != null && getEditor().canOpenContext(diagram)) getEditor().openContext(diagram);

        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPartViewer#select(org.eclipse.gef.EditPart)
     */
    @Override
    public void select( EditPart editpart ) {
        super.select(editpart);
        reveal(editpart);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPartViewer#select(org.eclipse.gef.EditPart)
     */
    public void select( List editparts ) {
        Iterator iter = editparts.iterator();
        EditPart nextEP = null;
        int iPart = 0;

        // Only want to reveal last EP in list.
        int nParts = editparts.size();

        while (iter.hasNext()) {
            nextEP = (EditPart)iter.next();
            if (iPart == 0) super.select(nextEP);
            else super.appendSelection(nextEP);
            iPart++;
            if (iPart == nParts) reveal(nextEP);
        }
    }

    public void clearAllSelections( boolean fireSelection ) {
        deselectAll(fireSelection);
    }

    private Object getRealDiagramTarget( Diagram diagram ) {
        Object targetObject = diagram.getTarget();
        if (targetObject != null) {
            if (targetObject instanceof ModelAnnotation) {
                ModelResource modelResource = ModelUtilities.getModelResourceForModelObject(diagram);
                if (modelResource != null) return modelResource.getResource();
            }
            return targetObject;
        }

        return null;
    }

    public void setVerticalRangeToValue( int someYValue ) {
        // System.out.println(" -->> DiagramViewer.setVRangeToValue():  value = " + someYValue);
        Viewport viewport = getFigureCanvas().getViewport();
        viewport.getVerticalRangeModel().setMinimum(someYValue);
        viewport.validate();
        // RangeModel model = viewport.getVerticalRangeModel();
        // System.out.println(
        // " -->> DiagramViewer.setVRangeToValue():  min = "
        // + model.getMinimum() +
        // "  max = " + model.getMaximum());

    }

    public int getMinimumYValue() {
        return 0;
    }

    public Rectangle2D getBounds() {
        int w = getFigureCanvas().getViewport().getBounds().width;
        int h = getFigureCanvas().getViewport().getBounds().height;
        return new Rectangle(w, h);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPartViewer#reveal(org.eclipse.gef.EditPart)
     */
    @Override
    public void reveal( EditPart editPart ) {
        if (isValidViewer()) {
            if (editPart instanceof DiagramEditPart) {
                EObject eObject = ((DiagramEditPart)editPart).getModelObject();

                if (getSelectionHandler() != null) {
                    if (getSelectionHandler().shouldReveal(eObject)) {
                        if (((DiagramEditPart)editPart).shouldReveal()) super.reveal(editPart);
                    }
                } else super.reveal(editPart);
            } else {
                super.reveal(editPart);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPartViewer#reveal(org.eclipse.gef.EditPart)
     */
    public void reveal( EObject eObject ) {
        if (isValidViewer()) {
            if (eObject != null) {
                EditPart editPart = getSelectionHandler().findDiagramChildEditPart(eObject, false);
                if (editPart != null) reveal(editPart);
            }
        }
    }

    public int getCurrentVScrollValue() {
        FigureCanvas scrolledCanvas = (FigureCanvas)getControl();
        return scrolledCanvas.getViewport().getViewLocation().y;
    }

    public int getCurrentHScrollValue() {
        FigureCanvas scrolledCanvas = (FigureCanvas)getControl();
        return scrolledCanvas.getViewport().getViewLocation().x;
    }

    /**
     * This method provides a way to tell the viewer to expose an edit part. reveal() doesn't seem to be able to do the job, when
     * a part is dragged out of the viewpor view and dropped. The scroll bars react, but the part is hidden.
     * 
     * @param ep
     * @since 4.2
     */
    public void exposePart( EditPart ep ) {
        double currentZoom = getEditor().getCurrentZoomFactor();
        Dimension vpSize = getFigureCanvas().getViewport().getSize();
        Point locationEP = new Point(((DiagramModelNode)ep.getModel()).getPosition());
        Dimension epSize = ((DiagramModelNode)ep.getModel()).getSize();
        Point vpLocation = new Point(getFigureCanvas().getViewport().getViewLocation());

        // Need to adjust vp rectangle for zoom factor
        Dimension correctedVPSize = new Dimension((int)(vpSize.width / currentZoom), (int)(vpSize.height / currentZoom));
        Point correctedVPLocation = new Point((int)(vpLocation.x / currentZoom), (int)(vpLocation.y / currentZoom));

        org.eclipse.draw2d.geometry.Rectangle vpRect = new org.eclipse.draw2d.geometry.Rectangle(correctedVPLocation,
                                                                                                 correctedVPSize);

        if (!vpRect.contains(locationEP)) {
            int dxExpose = (int)(50 / currentZoom);
            int dyExpose = (int)(50 / currentZoom);
            if (epSize.width < dxExpose) dxExpose = epSize.width - 5;
            if (epSize.height < dyExpose) dyExpose = epSize.height - 5;

            int newX = vpLocation.x;
            int newY = vpLocation.y;
            int dxVp = 0;
            int dyVp = 0;

            if (locationEP.x > vpRect.right()) dxVp = locationEP.x - vpRect.right() + dxExpose;
            else if (locationEP.x < vpRect.x) dxVp = locationEP.x - vpRect.x - dxExpose;
            if (locationEP.y > vpRect.bottom()) dyVp = locationEP.y - vpRect.bottom() + dxExpose;
            else if (locationEP.y < vpRect.y) dyVp = locationEP.y - vpRect.y - dxExpose;

            dxVp = (int)(dxVp * currentZoom);
            dyVp = (int)(dyVp * currentZoom);
            newX += dxVp;
            newY += dyVp;

            getFigureCanvas().getViewport().setViewLocation(newX, newY);
        }
    }

    public Point getViewportLocation() {
        FigureCanvas scrolledCanvas = (FigureCanvas)getControl();
        return new Point(scrolledCanvas.getViewport().getViewLocation());
    }

    public void updateForPrintPreferences() {
        //        System.out.println("[DiagramViewer.updateForPrintPreferences] TOP");  //$NON-NLS-1$

        CustomScalableFreeformRootEditPart repCustom = null;
        PageBoundaryGridLayer pgLayer = null;
        if (getRootEditPart() != null && getRootEditPart() instanceof CustomScalableFreeformRootEditPart) {
            repCustom = (CustomScalableFreeformRootEditPart)getRootEditPart();
            pgLayer = repCustom.getPageGridLayer();
        } else {
            // quit if we have the wrong kind of root edit part
            return;
        }

        PrinterData data = DiagramPrintingAnalyzer.getPrinterData();

        if (data == null) {
            return;
        }

        //        System.out.println("\n[DiagramViewer.updateForPrintPreferences] ..." );  //$NON-NLS-1$
        DiagramPrintingAnalyzer analyzer = new DiagramPrintingAnalyzer(new Printer(data), this);

        // Added this helper/debug method
        // To enable, set DiagramPrintingAnalyzer & DigramPrintingOperation debugMode = TRUE
        // Else, this call does nothing.
        analyzer.printCurrentAnalysis();

        Dimension dimSize = analyzer.getPageSize();
        double dZoomFactor = getEditor().getCurrentZoomFactor();

        int iZoomedWidth = (int)Math.floor(dimSize.width * dZoomFactor);
        int iZoomedHeight = (int)Math.floor(dimSize.height * dZoomFactor);

        Dimension dimZoomedSize = new Dimension(iZoomedWidth, iZoomedHeight);
        //  System.out.println("[DiagramViewer.updateForPrintPreferences] dimSize: " + dimSize);  //$NON-NLS-1$
        //  System.out.println("[DiagramViewer.updateForPrintPreferences] dZoomFactor: " + dZoomFactor);  //$NON-NLS-1$
        //  System.out.println("[DiagramViewer.updateForPrintPreferences] iZoomedWidth: " + iZoomedWidth);  //$NON-NLS-1$
        //  System.out.println("[DiagramViewer.updateForPrintPreferences] iZoomedHeight: " + iZoomedHeight);  //$NON-NLS-1$
        //  System.out.println("[DiagramViewer.updateForPrintPreferences] dimZoomedSize: " + dimZoomedSize + "\n");  //$NON-NLS-1$ //$NON-NLS-2$
        //                                                   
        //      System.out.println("\n[DiagramViewer.updateForPrintPreferences] ... done." );  //$NON-NLS-1$
        pgLayer.setSpacing(dimZoomedSize);
    }

    public boolean isValidViewer() {
        // Defect 19618 needs to know if the viewer is valid. Basically, ScrollingGraphicalViewer.reveal() method had a NULL
        // canvas.
        if (getFigureCanvas() == null || getControl() == null || getControl().isDisposed()) return false;

        return true;
    }

}
