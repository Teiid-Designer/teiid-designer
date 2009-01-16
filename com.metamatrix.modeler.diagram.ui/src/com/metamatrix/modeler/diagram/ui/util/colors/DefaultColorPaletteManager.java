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

package com.metamatrix.modeler.diagram.ui.util.colors;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;

/**
 * ColorPalatteManager
 */
public class DefaultColorPaletteManager implements ColorPaletteManager {
    /**
     * Construct an instance of ColorPalatteManager.
     * 
     */
    public DefaultColorPaletteManager() {
        super();
    }
    
    public ColorPalette getColorPalette(EObject eObject) {
        ColorPalette newPalette = null;
        // if( not virtual, then color palette should be default;
        
        if( ModelObjectUtilities.isVirtual(eObject) ) {
            newPalette = new FigureColorPalette();
            newPalette.setColors(
                 DiagramUiConstants.Colors.VIRTUAL_GROUP_HEADER,
                 DiagramUiConstants.Colors.VIRTUAL_GROUP_BKGRND,
                 ColorConstants.black,
                 DiagramUiConstants.Colors.SELECTION,
                 DiagramUiConstants.Colors.HILITE,
                 DiagramUiConstants.Colors.OUTLINE
                );
        } else if( ModelObjectUtilities.isLogical(eObject) || 
                   ModelObjectUtilities.isExtension(eObject) ||
                   ModelObjectUtilities.isFunction(eObject) ) {
            newPalette = new FigureColorPalette();
            newPalette.setColors(
                DiagramUiConstants.Colors.LOGICAL_GROUP_HEADER,
                DiagramUiConstants.Colors.LOGICAL_GROUP_BKGRND,
                ColorConstants.black,
                DiagramUiConstants.Colors.SELECTION,
                DiagramUiConstants.Colors.HILITE,
                DiagramUiConstants.Colors.OUTLINE
                );
        } else {
            // For now let's just create a "physical" set
            newPalette = new FigureColorPalette();
            newPalette.setColors(
                DiagramUiConstants.Colors.GROUP_HEADER,
                DiagramUiConstants.Colors.GROUP_BKGRND,
                ColorConstants.black,
                DiagramUiConstants.Colors.SELECTION,
                DiagramUiConstants.Colors.HILITE,
                DiagramUiConstants.Colors.OUTLINE
                );
        }
        
        return newPalette;
    }
    
    

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.diagram.ui.util.colors.ColorPaletteManager#getColorPalette(java.lang.Object)
	 */
	public ColorPalette getColorPalette(Object object) {
		if( object instanceof EObject )
			return getColorPalette((EObject) object);
			
		return null;
	}

}
