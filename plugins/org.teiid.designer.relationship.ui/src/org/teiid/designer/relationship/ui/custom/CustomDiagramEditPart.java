/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.custom;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.relationship.ui.PluginConstants;
import org.teiid.designer.relationship.ui.part.RelationshipDiagramEditPart;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;


/**
 * UmlPackageDiagramEditPart
 */
public class CustomDiagramEditPart extends RelationshipDiagramEditPart {

    @Override
    public void updateForPreferences() {
        RGB currentBkgdColor = PreferenceConverter.getColor(DiagramUiPlugin.getDefault().getPreferenceStore(),
                                                            PluginConstants.Prefs.Appearance.CUSTOM_RELATIONSHIP_BKGD_COLOR);
        this.getFigure().setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
    }
}
