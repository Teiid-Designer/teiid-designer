/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.util.colors;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


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
    
    @Override
	public ColorPalette getColorPalette(EObject eObject) {
        ColorPalette newPalette = null;
        // if( not virtual, then color palette should be default;
        
        if( ModelObjectUtilities.isVirtual(eObject) ) {
            newPalette = new FigureColorPalette();
            newPalette.setColors(
                 DiagramUiConstants.Colors.VIRTUAL_GROUP_HEADER,
                 DiagramUiConstants.Colors.VIRTUAL_GROUP_BKGRND,
                 ColorConstants.black,
                 ColorConstants.lightBlue,
                 ColorConstants.lightGreen,
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
                ColorConstants.lightBlue,
                ColorConstants.lightGreen,
                DiagramUiConstants.Colors.OUTLINE
                );
        } else {
            // For now let's just create a "physical" set
            newPalette = new FigureColorPalette();
            newPalette.setColors(
                DiagramUiConstants.Colors.GROUP_HEADER,
                DiagramUiConstants.Colors.GROUP_BKGRND,
                ColorConstants.black,
                ColorConstants.lightBlue,
                ColorConstants.lightGreen,
                DiagramUiConstants.Colors.OUTLINE
                );
        }
        
        return newPalette;
    }
    
    

	/* (non-Javadoc)
	 * @See org.teiid.designer.diagram.ui.util.colors.ColorPaletteManager#getColorPalette(java.lang.Object)
	 */
	@Override
	public ColorPalette getColorPalette(Object object) {
		if( object instanceof EObject )
			return getColorPalette((EObject) object);
			
		return null;
	}

}
