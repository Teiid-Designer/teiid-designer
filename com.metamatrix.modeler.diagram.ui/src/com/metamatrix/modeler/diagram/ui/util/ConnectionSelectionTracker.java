/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.diagram.ui.util;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.swt.events.MouseEvent;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.editor.DiagramViewer;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramSelectionHandler;
import com.metamatrix.modeler.diagram.ui.figure.DiagramPolylineConnection;
import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;


/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ConnectionSelectionTracker extends SelectEditPartTracker {
	private IDiagramSelectionHandler selectionHandler;
	private EditPart selectedEndEditPart;
	/**
	 * @param owner
	 */
	public ConnectionSelectionTracker(EditPart owner ) {
		super(owner);
	}
    
	public ConnectionSelectionTracker(EditPart owner, IDiagramSelectionHandler selectionHandler ) {
		super(owner);
		this.selectionHandler = selectionHandler;
	}
    

	@Override
    protected boolean handleDoubleClick(int button) {
		// Let's rely on the edit part to make the decision.
		if( getSourceEditPart() instanceof DiagramEditPart ) {
			Request request = new Request(RequestConstants.REQ_DIRECT_EDIT);
			getSourceEditPart().performRequest(request);
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.Tool#mouseDoubleClick(org.eclipse.swt.events.MouseEvent, org.eclipse.gef.EditPartViewer)
	 */
	@Override
    public void mouseDoubleClick(MouseEvent me, EditPartViewer viewer) {
//		System.out.println(" -->> ConnectionSelectionTracker.mouseDoubleClick()");
		super.mouseDoubleClick(me, viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.tools.SelectEditPartTracker#performSelection()
	 */
	@Override
    protected void performSelection() {
		// We need to completedly override the super method here if we selected near an end.
		if( selectedEndEditPart != null ) {
			if (hasSelectionOccurred())
				return;
			setFlag(FLAG_SELECTION_PERFORMED, true);
			EditPartViewer viewer = getCurrentViewer();
			// Deselect the connection link
			viewer.deselect(getSourceEditPart());
			selectionHandler.hiliteConnection((NodeConnectionEditPart)getSourceEditPart());
			// Add the selectedEndEditPart
			selectionHandler.setClearHilites(false);
			viewer.select(selectedEndEditPart);
			selectionHandler.setClearHilites(true);
		} else {
			super.performSelection();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.Tool#mouseDown(org.eclipse.swt.events.MouseEvent, org.eclipse.gef.EditPartViewer)
	 */
	@Override
    public void mouseDown(MouseEvent me, EditPartViewer viewer) {
		selectedEndEditPart = getEndEditPart(me, viewer);
		super.mouseDown(me, viewer);
	}
	
	private EditPart getEndEditPart(MouseEvent me, EditPartViewer viewer) {
		// we know we selected this link, so we should be in the bounds of this figure
		
		if( getSourceEditPart() instanceof NodeConnectionEditPart) {
			ZoomManager zm = (ZoomManager)((DiagramViewer)viewer).getEditor().getAdapter(ZoomManager.class);
			double zoomValue = zm.getZoom();
			int threshold = (int)(20/zoomValue);
			
			int scrollX = ((DiagramViewer)viewer).getCurrentHScrollValue();
			int scrollY = ((DiagramViewer)viewer).getCurrentVScrollValue();
			DiagramPolylineConnection dpc = (DiagramPolylineConnection)((AbstractGraphicalEditPart)getSourceEditPart()).getFigure();
			Point p0 = new Point((me.x + scrollX)/zoomValue, (me.y + scrollY)/zoomValue);
			
			Point p1 = dpc.getStart();
			int deltaX = p0.x - p1.x;
			int deltaY = p0.y - p1.y;
			int dist = (int)Math.sqrt( deltaX*deltaX + deltaY*deltaY);
			if( dist < threshold ) {
				return getSourceEndEditPart();
			}
			Point p2 = dpc.getEnd();
			deltaX = p0.x - p2.x;
			deltaY = p0.y - p2.y;
			dist = (int)Math.sqrt( deltaX*deltaX + deltaY*deltaY);
			if( dist < threshold ) {
				return getTargetEndEditPart();
			}
		}

		return null;
	}
	
	private EditPart getSourceEndEditPart() {
		return DiagramUiUtilities.getSourceEndEditPart((NodeConnectionEditPart)getSourceEditPart());
	}
	
	
	private EditPart getTargetEndEditPart() {
		return DiagramUiUtilities.getTargetEndEditPart((NodeConnectionEditPart)getSourceEditPart());
	}

}
