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

package com.metamatrix.modeler.mapping.ui.figure;

import org.eclipse.draw2d.ColorConstants;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.modeler.diagram.ui.util.colors.DefaultColorPaletteManager;
import com.metamatrix.modeler.diagram.ui.util.colors.FigureColorPalette;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.xsd.util.ModelerXsdUtils;

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
     * @see com.metamatrix.modeler.diagram.ui.util.colors.ColorPaletteManager#getColorPalette(org.eclipse.emf.ecore.EObject)
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
