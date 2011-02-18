/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Color;

/**
 * This tool implements the selection of multiple objects in rectangular area.
 */
public class LassoSelectionTool extends AbstractTool {

    static final int TOGGLE_MODE = 1;
    static final int APPEND_MODE = 2;

    private int mode;
    
    private boolean fillValue = false;
    private int lineStyle = Graphics.LINE_DASHDOT;
    private Color lineColor = ColorConstants.blue;
    private int lineWidth = 5;   //= 2;
    
    private Shape lassoRectangleFigure;
    private List allChildren = new ArrayList();
    private List selectedEditParts;
    private Request targetRequest;

    private static final Request LASSO_REQUEST = new Request(RequestConstants.REQ_SELECTION);

    /**
     * Creates a new LassoSelectionTool.
     */
    public LassoSelectionTool() {
        setDefaultCursor(Cursors.CROSS);
        setLineStyle(lineStyle);
    }

    private List calculateNewSelection() {

        List newSelections = new ArrayList();
        List children = getAllChildren();

        // Calculate new selections based on which children fall
        // inside the lasso selection rectangle.  Do not select
        // children who are not visible
        for (int i = 0; i < children.size(); i++) {
            EditPart child = (EditPart) children.get(i);
            IFigure figure = ((GraphicalEditPart) child).getFigure();
            Rectangle r = figure.getBounds().getCopy();
            figure.translateToAbsolute(r);
			if( DiagramUiUtilities.isLassoableFigure(figure) && figure.isVisible()) {
	            if (geLassoSelectionRectangle().contains(r.getTopLeft())
	                && geLassoSelectionRectangle().contains(r.getBottomRight()) ) {
	                if (child.getTargetEditPart(LASSO_REQUEST) == child &&
						DiagramUiUtilities.isLassoableEditPart(child) &&
	                    !newSelections.contains(child) ) {
	                    newSelections.add(child);
					}
	            } else if (geLassoSelectionRectangle().intersects(r) ) {
	                if (child.getTargetEditPart(LASSO_REQUEST) == child &&
						DiagramUiUtilities.isLassoableEditPart(child) &&
	                    !newSelections.contains(child) ) {
	                    newSelections.add(child);
					}
	            }
			}
        }
        return newSelections;
    }

    private Request createTargetRequest() {
        return LASSO_REQUEST;
    }

    /**
     * Erases feedback if necessary and puts the tool into the terminal state.
     */
    @Override
    public void deactivate() {
        if (isInState(STATE_DRAG_IN_PROGRESS)) {
            eraseLassoFeedback();
            eraseTargetFeedback();
        }
        super.deactivate();
        allChildren = new ArrayList();
        setState(STATE_TERMINAL);
    }

    private void eraseLassoFeedback() {
        if (lassoRectangleFigure != null) {
            removeFeedback(lassoRectangleFigure);
            lassoRectangleFigure = null;
        }
    }

    private void eraseTargetFeedback() {
        if (selectedEditParts == null)
            return;
        ListIterator oldEditParts = selectedEditParts.listIterator();
        while (oldEditParts.hasNext()) {
            EditPart editPart = (EditPart) oldEditParts.next();
            editPart.eraseTargetFeedback(getTargetRequest());
        }
    }

    /**
     * Returns a list including all of the children
     * of the edit part passed in.
     */
    private List getAllChildren(EditPart editPart, List allChildren) {
        List children = editPart.getChildren();
        for (int i = 0; i < children.size(); i++) {
            GraphicalEditPart child = (GraphicalEditPart) children.get(i);
            allChildren.add(child);
            getAllChildren(child, allChildren);
        }
        return allChildren;
    }

    /**
     * Return a vector including all of the children
     * of the root editpart
     */
    private List getAllChildren() {
        if (allChildren.isEmpty())
            allChildren = getAllChildren(getCurrentViewer().getRootEditPart(), new ArrayList());
        return allChildren;
    }

    /**
     * Returns the name identifier of the command that the tool
     * is currently looking for.
     */
    @Override
    protected String getCommandName() {
        return REQ_SELECTION;
    }

    /**
     * Returns the debug name for this tool.
     */
    @Override
    protected String getDebugName() {
        return "Lasso Tool"; //$NON-NLS-1$
    }

    private IFigure getLassoFeedbackFigure() {
        if (lassoRectangleFigure == null) {
            lassoRectangleFigure = new RectangleFigure();
            FigureUtilities.makeGhostShape(lassoRectangleFigure);
            lassoRectangleFigure.setFill(fillValue);
            lassoRectangleFigure.setLineStyle(lineStyle);
            lassoRectangleFigure.setLineWidth(lineWidth);
            lassoRectangleFigure.setForegroundColor(lineColor);
            addFeedback(lassoRectangleFigure);
        }
        return lassoRectangleFigure;
    }

    private Rectangle geLassoSelectionRectangle() {
        return new Rectangle(getStartLocation(), getLocation());
    }

    private int getSelectionMode() {
        return mode;
    }

    private Request getTargetRequest() {
        if (targetRequest == null)
            targetRequest = createTargetRequest();
        return targetRequest;
    }

    /**
     * Sets the selection mode to <code>TOGGLE_MODE</code> or
     * <code>APPEND_MODE</code> depending on the keyboard input.
     */
    @Override
    protected boolean handleButtonDown(int button) {
        if (!isGraphicalViewer())
            return true;
        if (button != 1) {
            setState(STATE_INVALID);
            handleInvalidInput();
        }
        if (stateTransition(STATE_INITIAL, STATE_DRAG_IN_PROGRESS)) {
            
            // jh Case 4514: Give this a default; otherwise old state may persist 
            setSelectionMode(TOGGLE_MODE);
            
            if (getCurrentInput().isControlKeyDown())
                setSelectionMode(TOGGLE_MODE);
            else if (getCurrentInput().isShiftKeyDown())
                setSelectionMode(APPEND_MODE);
        }
        return true;
    }

    /**
     * Erases feedback and performs the selection.
     */
    @Override
    protected boolean handleButtonUp(int button) {

        if (stateTransition(STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
            eraseTargetFeedback();
            eraseLassoFeedback();
            performLassoSelect();
        }
        handleFinished();
        return true;
    }

    /**
     * Calculates the selection and updates the feedback.
     */
    @Override
    protected boolean handleDragInProgress() {
        if (isInState(STATE_DRAG | STATE_DRAG_IN_PROGRESS)) {
            showLassoFeedback();
            eraseTargetFeedback();
            selectedEditParts = calculateNewSelection();
            showTargetFeedback();
        }
        return true;
    }

    /**
     * This method is called when mouse or keyboard input is
     * invalid and erases the feedback.
     */
    @Override
    protected boolean handleInvalidInput() {
        eraseTargetFeedback();
        eraseLassoFeedback();
        return true;
    }

    private boolean isGraphicalViewer() {
        return getCurrentViewer() instanceof GraphicalViewer;
    }

    private void performLassoSelect() {
        EditPartViewer viewer = getCurrentViewer();

        List newSelections = calculateNewSelection();

        // If in multi select mode, add the new selections to the already
        // selected group; otherwise, clear the selection and select the new group
        if (getSelectionMode() == APPEND_MODE) {
            for (int i = 0; i < newSelections.size(); i++) {
                EditPart editPart = (EditPart) newSelections.get(i);
                viewer.appendSelection(editPart);
            }
        } else {
            viewer.setSelection(new StructuredSelection(newSelections));
        }
    }

    /**
     * Sets the EditPartViewer.  Also sets the appropriate default cursor
     * based on the type of viewer.
     */
    @Override
    public void setViewer(EditPartViewer viewer) {
        if (viewer == getCurrentViewer())
            return;
        super.setViewer(viewer);
        if (viewer instanceof GraphicalViewer)
            setDefaultCursor(Cursors.CROSS);
        else
            setDefaultCursor(Cursors.NO);
    }

    private void setSelectionMode(int mode) {
        this.mode = mode;
    }

    private void showLassoFeedback() {
        Rectangle rect = geLassoSelectionRectangle().getCopy();
        getLassoFeedbackFigure().translateToRelative(rect);
        getLassoFeedbackFigure().setBounds(rect);
    }

    private void showTargetFeedback() {
        for (int i = 0; i < selectedEditParts.size(); i++) {
            EditPart editPart = (EditPart) selectedEditParts.get(i);
            editPart.showTargetFeedback(getTargetRequest());
        }
    }

    public void setLineWidth(int newWidth) {
        lineWidth = newWidth;
    }
    
    public void setLineStyle(int newStyle) {
        lineStyle = newStyle;
    }
    
    public void setLineColor(Color newColor ) {
        lineColor = newColor;
    }
    
    public void setFill(boolean shouldFill) {
        fillValue = shouldFill;
    }

}
