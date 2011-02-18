/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection.decorator;

import org.eclipse.draw2d.PolygonDecoration;
import com.metamatrix.modeler.diagram.ui.connection.BinaryAssociation;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DecoratorFactory {
	
	public static PolygonDecoration getDecorator(int decoratorID) {
		PolygonDecoration decorator = null;
		
		switch( decoratorID ) {
			case BinaryAssociation.DECORATOR_ARROW_CLOSED: {
				decorator = new ClosedArrowDecoration();
				decorator.setFill(false);
				decorator.setScale(10, 5);
			} break;
			
			case BinaryAssociation.DECORATOR_ARROW_FILLED: {
				decorator = new ClosedArrowDecoration();
				decorator.setFill(true);
				decorator.setScale(10, 5);
			} break;
			
			case BinaryAssociation.DECORATOR_ARROW_OPEN: {
				decorator = new OpenArrowDecoration();
				decorator.setScale(10, 5);
			} break;
			
			case BinaryAssociation.DECORATOR_DIAMOND_FILLED: {
				decorator = new DiamondDecoration();
				decorator.setFill(true);
				decorator.setScale(5, 5);
			} break;
			
			case BinaryAssociation.DECORATOR_DIAMOND_OPEN: {
				decorator = new DiamondDecoration();
				decorator.setFill(false);
				decorator.setScale(5, 5);
			} break;
			
			case BinaryAssociation.DECORATOR_NON_NAVIGABLE: {
				decorator = new NotNavigableDecoration();
				decorator.setScale(6, 4);
			} break;
			
			default:
			break;
		}
		
		return decorator;
	}

}
