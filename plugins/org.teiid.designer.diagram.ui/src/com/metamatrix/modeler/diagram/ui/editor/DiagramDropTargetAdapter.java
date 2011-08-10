/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import java.util.Collections;
import java.util.List;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import com.metamatrix.modeler.diagram.ui.part.DropEditPart;
import com.metamatrix.modeler.internal.ui.util.EObjectTransfer;

/**
 * DiagramDropTargetAdapter
 */
public class DiagramDropTargetAdapter extends AbstractTransferDropTargetListener {
    private EditPart currentEditPart = null;

    /**
     * Construct an instance of DiagramDropTargetAdapter.
     * 
     * @param domain
     * @param viewer
     */
    public DiagramDropTargetAdapter( EditPartViewer viewer ) {
        super(viewer);
    }

    /**
     * @see org.eclipse.jface.util.TransferDropTargetListener#getTransfer()
     * @since 4.3
     */
    @Override
    public Transfer getTransfer() {
        return EObjectTransfer.getInstance();
    }

    /**
     * @see org.eclipse.jface.util.TransferDropTargetListener#isEnabled(org.eclipse.swt.dnd.DropTargetEvent)
     * @since 4.3
     */
    @Override
    public boolean isEnabled( DropTargetEvent event ) {
        return true;
    }

    private List getEventEObjects( DropTargetEvent event ) {
        Transfer[] transfers = ((DropTarget)event.getSource()).getTransfer();
        for (int i = 0; i < transfers.length; i++) {
            if (transfers[i] instanceof EObjectTransfer) {
                EObjectTransfer transfer = (EObjectTransfer)transfers[i];
                if (transfer.getObject() != null) return (List)transfer.getObject();
            }
        }
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void drop( DropTargetEvent event ) {
        event.detail = DND.DROP_COPY;
        updateTargetEditPart();
        List dropList = getEventEObjects(event);
        if (!dropList.isEmpty()) {
            ((DropEditPart)getTargetEditPart()).drop(getCurrentAbsolutePoint(), dropList);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void dragOver( DropTargetEvent event ) {
        boolean allowsDrop = false;
        setCurrentEditPart();
        List dropList = getEventEObjects(event);
        if (currentEditPart != null && !dropList.isEmpty()) {
            if (currentEditPart instanceof DropEditPart) {
                ((DropEditPart)currentEditPart).setLastHoverPoint(getCurrentAbsolutePoint());
                allowsDrop = ((DropEditPart)currentEditPart).allowsDrop(currentEditPart, dropList);
            }
        }

        if (!allowsDrop) event.detail = DND.DROP_NONE;
        else event.detail = DND.DROP_COPY;
        super.dragOver(event);
    }

    /**
     */
    protected void helper( DropTargetEvent theEvent ) {
        // System.out.println("  DiagramDropTargetAdapter.helper() Called");
    }

    private Point getCurrentAbsolutePoint() {
        FigureCanvas canvas = (FigureCanvas)getViewer().getControl();
        Point newPoint = new Point(getDropLocation());
        newPoint.x += canvas.getViewport().getViewLocation().x;
        newPoint.y += canvas.getViewport().getViewLocation().y;
        return newPoint;
    }

    /**
     * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#updateTargetRequest()
     * @since 4.2
     */
    @Override
    protected void updateTargetRequest() {
        updateTargetEditPart();
        getTargetRequest().setType(RequestConstants.REQ_ADD);
    }

    /**
     * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#createTargetRequest()
     * @since 4.2
     */
    @Override
    protected Request createTargetRequest() {
        Request req = new Request();
        req.setType(RequestConstants.REQ_ADD);
        return req;
    }

    private void setCurrentEditPart() {
        currentEditPart = getViewer().findObjectAtExcluding(getDropLocation(),
                                                            getExclusionSet(),
                                                            new EditPartViewer.Conditional() {
                                                                public boolean evaluate( EditPart editpart ) {
                                                                    return editpart.getTargetEditPart(proxyGetTargetRequest()) != null;
                                                                }
                                                            });
    }

    Request proxyGetTargetRequest() {
        return getTargetRequest();
    }
}
