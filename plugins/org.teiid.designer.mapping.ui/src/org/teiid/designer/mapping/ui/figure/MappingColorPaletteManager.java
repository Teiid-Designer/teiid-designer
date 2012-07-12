/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.diagram.ui.util.colors.ColorPalette;
import org.teiid.designer.diagram.ui.util.colors.DefaultColorPaletteManager;
import org.teiid.designer.diagram.ui.util.colors.FigureColorPalette;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.metamodels.transformation.StagingTable;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.xsd.util.ModelerXsdUtils;


/**
 * MappingColorPaletteManager
 */
public class MappingColorPaletteManager extends DefaultColorPaletteManager {

    /**
     * Construct an instance of MappingColorPaletteManager.
     * 
     */
    public MappingColorPaletteManager() {
        super();
    }

    
    /* (non-Javadoc)
     * @See org.teiid.designer.diagram.ui.util.colors.ColorPaletteManager#getColorPalette(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public ColorPalette getColorPalette(EObject eObject) {
        ColorPalette newPalette = null;
        
        if( eObject != null && eObject instanceof StagingTable ) {
            // For now let's just create a "virtual" set
            newPalette = new FigureColorPalette();
            newPalette.setColors(
                UiConstants.Colors.TEMP_TABLE_HEADER,
                UiConstants.Colors.VIRTUAL_GROUP_BKGRND,
                ColorConstants.black,
                UiConstants.Colors.SELECTION,
                UiConstants.Colors.HILITE,
                UiConstants.Colors.OUTLINE
                );
        } else if(eObject == null ) {
            newPalette = new FigureColorPalette();
            newPalette.setColors(
                UiConstants.Colors.VIRTUAL_GROUP_HEADER,
                UiConstants.Colors.VIRTUAL_GROUP_BKGRND,
                ColorConstants.black,
                UiConstants.Colors.SELECTION,
                UiConstants.Colors.HILITE,
                UiConstants.Colors.OUTLINE
                );
        } else if( TransformationHelper.isSqlInputSet(eObject) || 
                   TransformationHelper.isSqlInputParameter(eObject)) {
            newPalette = new FigureColorPalette();
            newPalette.setColors(
                UiConstants.Colors.INPUT_SET_HEADER,
                UiConstants.Colors.INPUT_SET_BKGRND,
                ColorConstants.black,
                UiConstants.Colors.SELECTION,
                UiConstants.Colors.HILITE,
                UiConstants.Colors.OUTLINE
                );
        } else if (ModelerXsdUtils.isEnumeratedType(eObject) || ModelerXsdUtils.isEnumeratedTypeValue(eObject)) {
            newPalette = new FigureColorPalette();
            newPalette.setColors(
                ColorConstants.white,
                ColorConstants.white,
                ColorConstants.black,
                UiConstants.Colors.SELECTION,
                UiConstants.Colors.HILITE,
                UiConstants.Colors.OUTLINE
                );
        } else{
            newPalette = super.getColorPalette(eObject);
        }
        
        return newPalette;
    }

}
