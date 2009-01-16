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
