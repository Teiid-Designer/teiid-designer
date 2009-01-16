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

package com.metamatrix.modeler.relationship.ui.navigation.part;


import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.swt.events.MouseEvent;

import com.metamatrix.modeler.diagram.ui.part.DiagramEditPart;
import com.metamatrix.modeler.relationship.ui.navigation.selection.NavigationSelectionHandler;


/**
 * @author blafond
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class NavigationSelectionTracker extends SelectEditPartTracker {
	private NavigationSelectionHandler selectionHandler;
	private EditPart thisEditPart;
	/**
	 * @param owner
	 */
	public NavigationSelectionTracker(EditPart owner ) {
		super(owner);
		this.thisEditPart = owner;
	}
    
	public NavigationSelectionTracker(EditPart owner, NavigationSelectionHandler selectionHandler ) {
		super(owner);
		this.selectionHandler = selectionHandler;
		this.thisEditPart = owner;
	}
    
	@Override
    protected boolean handleButtonDown(int button) {
		return super.handleButtonDown(button);
	}

	@Override
    protected boolean handleButtonUp(int button) {
        if( getSourceEditPart().getViewer() != null &&
            getSourceEditPart().getViewer().getControl() != null) {
            return super.handleButtonUp(button);
        }
        
        return false;
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

	@Override
    protected boolean handleDragStarted() {
		return super.handleDragStarted();
	}

	@Override
    protected boolean hasSelectionOccurred() {
		return super.hasSelectionOccurred();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.Tool#mouseDoubleClick(org.eclipse.swt.events.MouseEvent, org.eclipse.gef.EditPartViewer)
	 */
	@Override
    public void mouseDoubleClick(MouseEvent me, EditPartViewer viewer) {
		// XXX Auto-generated method stub
		super.mouseDoubleClick(me, viewer);
		selectionHandler.handleDoubleClick(me, thisEditPart);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.Tool#mouseHover(org.eclipse.swt.events.MouseEvent, org.eclipse.gef.EditPartViewer)
	 */
	@Override
    public void mouseHover(MouseEvent me, EditPartViewer viewer) {
		// XXX Auto-generated method stub
		super.mouseHover(me, viewer);
	}
	
	@Override
    public void mouseDown(MouseEvent me, EditPartViewer viewer) {
//		setViewer(viewer);
//
//		getCurrentInput().setInput(me);
//		getCurrentInput().setMouseButton(me.button, true);
//
//		startX = me.x;
//		startY = me.y;
		selectionHandler.setLastMousePoint(new Point(me.x, me.y));

		super.handleButtonDown(me.button);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.tools.AbstractTool#handleHover()
	 */
	@Override
    protected boolean handleHover() {
		// XXX Auto-generated method stub
		return super.handleHover();
	}

}
