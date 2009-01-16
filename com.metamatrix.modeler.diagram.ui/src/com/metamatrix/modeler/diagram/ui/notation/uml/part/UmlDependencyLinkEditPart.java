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

package com.metamatrix.modeler.diagram.ui.notation.uml.part;

import java.util.List;

import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.connection.decorator.DecoratorFactory;

import org.eclipse.draw2d.ColorConstants;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.metamatrix.modeler.diagram.ui.figure.DiagramPolylineConnection;
import com.metamatrix.modeler.diagram.ui.util.ConnectionSelectionTracker;
import com.metamatrix.modeler.diagram.ui.util.DiagramConnectionEndpointEditPolicy;
import com.metamatrix.modeler.diagram.ui.util.DiagramNodeSelectionEditPolicy;

/**
 * ForeignKeyLinkEditPart
 */
public class UmlDependencyLinkEditPart extends NodeConnectionEditPart {
	private DragTracker dragTracker = null;
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {
		DiagramPolylineConnection connectionFigure = new DiagramPolylineConnection();
        
		List toolTips = ((NodeConnectionModel)getModel()).getToolTipStrings();
		if( toolTips != null && !toolTips.isEmpty() )
			connectionFigure.setToolTip(connectionFigure.createToolTip(toolTips));
        
		PolygonDecoration newDecoration = DecoratorFactory.getDecorator(((NodeConnectionModel)getModel()).getTargetDecoratorId());
		
		int iStandardWidth = 1;
				
		if( newDecoration != null ) {
			newDecoration.setLineWidth(iStandardWidth);
			connectionFigure.setTargetDecoration(newDecoration);
			if( ((NodeConnectionModel)getModel()).getTargetDecoratorId() == BinaryAssociation.DECORATOR_ARROW_CLOSED ){
				newDecoration.setFill(true);
				newDecoration.setBackgroundColor( ((AbstractGraphicalEditPart)getDiagramViewer().getContents()).getFigure().getBackgroundColor());
			}
		}

		connectionFigure.setLineStyle( ((NodeConnectionModel)getModel()).getLineStyle() );
		connectionFigure.setLineWidth( iStandardWidth );
		connectionFigure.setForegroundColor(ColorConstants.blue);
        
		return connectionFigure;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
    protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new DiagramConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new DiagramNodeSelectionEditPolicy());
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	@Override
    public DragTracker getDragTracker(Request req) {
		if( dragTracker == null )
			dragTracker = new ConnectionSelectionTracker(this, getDiagramViewer().getSelectionHandler());
			
		return dragTracker;
	}
}
