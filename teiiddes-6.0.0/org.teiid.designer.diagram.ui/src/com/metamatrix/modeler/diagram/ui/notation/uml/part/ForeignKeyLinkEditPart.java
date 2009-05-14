/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.notation.uml.part;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionEditPart;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.connection.decorator.DecoratorFactory;

import org.eclipse.draw2d.ColorConstants;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;


import com.metamatrix.modeler.diagram.ui.figure.DiagramPolylineConnection;

/**
 * ForeignKeyLinkEditPart
 */
public class ForeignKeyLinkEditPart extends NodeConnectionEditPart {
    
    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
    **/
    @Override
    protected IFigure createFigure() {
        IFigure connectionFigure = new DiagramPolylineConnection();
		PolygonDecoration newDecoration = DecoratorFactory.getDecorator(((NodeConnectionModel)getModel()).getTargetDecoratorId());
		
		if( newDecoration != null ) {
			newDecoration.setScale(6, 5);
			((DiagramPolylineConnection)connectionFigure).setTargetDecoration(newDecoration);
		}
		
//        PolygonDecoration newDecoration = new PolygonDecoration(); //new DiamondDecoration();
////        PolygonDecoration newDecoration1 = new NotNavigableDecoration();
//        //newDecoration.setScale(8.0, 8.0);
//        ((DiagramPolylineConnection)connectionFigure).setTargetDecoration(new PolygonDecoration());
////        ((DiagramPolylineConnection)connectionFigure).setSourceDecoration(null);
//        newDecoration.setForegroundColor(ColorConstants.black);
//        newDecoration.setFill(false);
//        newDecoration.setScale(13, 7);

//        newDecoration1.setForegroundColor(ColorConstants.blue);

        ((DiagramPolylineConnection)connectionFigure).setLineStyle( ((NodeConnectionModel)getModel()).getLineStyle() );
        int iStandardWidth = 1;
        newDecoration.setLineWidth(iStandardWidth);
        ((DiagramPolylineConnection)connectionFigure).setLineWidth( iStandardWidth );
        ((DiagramPolylineConnection)connectionFigure).setForegroundColor(ColorConstants.blue);
        
        return connectionFigure;
    }
}
