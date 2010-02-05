/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.PluginConstants;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.pakkage.PackageDiagramEditPart;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * UmlPackageDiagramEditPart
 */
public class CustomDiagramEditPart extends PackageDiagramEditPart {

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     **/
    @Override
    protected IFigure createFigure() {

        Figure newFigure = getFigureFactory().createFigure(getModel(), "umlDiagramNotation"); //$NON-NLS-1$
        setCurrentDiagramFont(ScaledFontManager.getFont());
        return newFigure;
    }

    @Override
    public void updateForPreferences() {
        RGB currentBkgdColor = PreferenceConverter.getColor(DiagramUiPlugin.getDefault().getPreferenceStore(),
                                                            PluginConstants.Prefs.Appearance.CUSTOM_BKGD_COLOR);
        this.getFigure().setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
    }
}
