/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.figure;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;

/**
 * AssociationLabelFigure
 */
public class AssociationLabelFigure extends LabeledRectangleFigure {

    /**
     * Construct an instance of AssociationLabelFigure.
     * @param labelString
     * @param newFont
     * @param hiliteSelection
     * @param colorPalette
     */
    public AssociationLabelFigure(
        String labelString,
        Font newFont,
        boolean hiliteSelection,
        ColorPalette colorPalette) {
        super(labelString, newFont, hiliteSelection, colorPalette);
    }

    /**
     * Construct an instance of AssociationLabelFigure.
     * @param labelString
     * @param hiliteSelection
     * @param colorPalette
     */
    public AssociationLabelFigure(String labelString, boolean hiliteSelection, ColorPalette colorPalette) {
        super(labelString, hiliteSelection, colorPalette);
    }

    /**
     * Construct an instance of AssociationLabelFigure.
     * @param labelString
     * @param icon
     * @param hiliteSelection
     * @param colorPalette
     */
    public AssociationLabelFigure(
        String labelString,
        Image icon,
        boolean hiliteSelection,
        ColorPalette colorPalette) {
        super(labelString, icon, hiliteSelection, colorPalette);
    }

    @Override
    public void refreshFont() {
        super.refreshFont();
    }
}
