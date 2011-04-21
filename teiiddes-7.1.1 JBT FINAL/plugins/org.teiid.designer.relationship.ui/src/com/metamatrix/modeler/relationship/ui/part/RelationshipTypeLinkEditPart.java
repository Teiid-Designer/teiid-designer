/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.part;

import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.connection.decorator.DecoratorFactory;
import com.metamatrix.modeler.diagram.ui.figure.DiagramPolylineConnection;
import com.metamatrix.modeler.diagram.ui.notation.uml.part.UmlGeneralizationLinkEditPart;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RelationshipTypeLinkEditPart extends UmlGeneralizationLinkEditPart {
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	**/
	@Override
    protected IFigure createFigure() {
		DiagramPolylineConnection connectionFigure = new DiagramPolylineConnection();
        
		List toolTips = ((NodeConnectionModel)getModel()).getToolTipStrings();
		if( toolTips != null && !toolTips.isEmpty() )
			connectionFigure.setToolTip(connectionFigure.createToolTip(toolTips));
        
		PolygonDecoration newDecoration = DecoratorFactory.getDecorator(BinaryAssociation.DECORATOR_ARROW_CLOSED);
		
		int iStandardWidth = 1;
				
		if( newDecoration != null ) {
			newDecoration.setLineWidth(iStandardWidth);
			newDecoration.setFill(true);
			connectionFigure.setTargetDecoration(newDecoration);
			newDecoration.setBackgroundColor( ((AbstractGraphicalEditPart)getDiagramViewer().getContents()).getFigure().getBackgroundColor());
		}

		connectionFigure.setLineStyle( BinaryAssociation.LINE_SOLID );
		connectionFigure.setLineWidth( iStandardWidth );
		connectionFigure.setForegroundColor(ColorConstants.blue);
        
		return connectionFigure;
	}
}
