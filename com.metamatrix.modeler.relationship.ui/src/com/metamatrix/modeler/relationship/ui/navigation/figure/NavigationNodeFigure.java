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

package com.metamatrix.modeler.relationship.ui.navigation.figure;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import com.metamatrix.modeler.relationship.ui.navigation.model.NavigationModelNode;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface NavigationNodeFigure {
	
	NavigationModelNode getNavigationModelNode();
    
	void layoutFigure();
	
	void setSizeToMinimum();

	void activate();
    
	void deactivate();

	/**
	 * Method providing the EditPart and others, access to the figure to update itself for a size change
	 * @param newSize
	 */
	void updateForSize(Dimension newSize);
    
	/**
	 * Method providing the EditPart and others, access to the figure to update itself for a location change
	 * @param newlocation
	 */
	void updateForLocation(Point newLocation);
    
    
	/**
	 * Method providing the EditPart and others, access to the figure to update itself for a name change
	 * @param newName
	 */
	void updateForName(String newName);

    
	/**
	 * Method providing the EditPart and others, access to the figure to update itself for a font change
	 * @param font
	 */
	void updateForFont(Font font);

	/**
	 * Method to tell the figure to add/or remove error icon decorator
	 * @param hasErrors
	 */
	void updateForError(boolean hasErrors);
    
	/**
	 * Method to tell the figure to add/or remove warning icon decorator
	 * @param hasWarnings
	 */
	void updateForWarning(boolean hasWarnings);
    
	/**
	 * Method to allow a call to figures to generically set a hilite backaground color.
	 * In particular, UmlAttributes...
	 * @param hiliteColor
	 */
	void hiliteBackground(Color hiliteColor);
    
	/**
	 * Method to allow a call to figures to generically tell the figure to render itself to a select state.
	 * In particular, UmlAttributes...
	 * @param hiliteColor
	 */
	void showSelected(boolean selected);
	
	void printBounds(String prefix);
}
