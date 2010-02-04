/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.part;

import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.connection.decorator.DecoratorFactory;
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
