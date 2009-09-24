/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.custom;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.relationship.ui.PluginConstants;
import com.metamatrix.modeler.relationship.ui.figure.RelationshipDiagramFigureFactory;
import com.metamatrix.modeler.relationship.ui.figure.RelationshipNodeFigure;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * PackageDiagramFigureFactory
 */
public class CustomDiagramFigureFactory extends RelationshipDiagramFigureFactory {
    private static final int CUSTOM_DIAGRAM = 0;

    /**
     * Construct an instance of UmlFigureFactory.
     */
    public CustomDiagramFigureFactory() {
        super();
    }

    @Override
    public Figure createFigure( Object modelObject ) {

        Figure newFigure = null;
        switch (getObjectType(modelObject)) {

            case CUSTOM_DIAGRAM: {
                newFigure = new FreeformLayer();
                newFigure.setLayoutManager(new FreeformLayout());
                // Don't know why, but if you don't setOpaque(true), you cannot move by drag&drop!
                newFigure.setOpaque(true);
                RGB currentBkgdColor = PreferenceConverter.getColor(DiagramUiPlugin.getDefault().getPreferenceStore(),
                                                                    PluginConstants.Prefs.Appearance.CUSTOM_RELATIONSHIP_BKGD_COLOR);
                newFigure.setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
            }
                break;

            default: {
                newFigure = super.createFigure(modelObject);
            }
        }
        if (newFigure instanceof RelationshipNodeFigure) {
            CustomDiagramModelFactory cdmf = (CustomDiagramModelFactory)DiagramUiPlugin.getDiagramTypeManager().getDiagram(PluginConstants.CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID).getModelFactory();
            if (cdmf != null) {
                ((RelationshipNodeFigure)newFigure).setFactory(cdmf);
            }
        }

        return newFigure;
    }

    protected int getObjectType( Object modelObject ) {
        int objectType = -1;

        if (modelObject != null) {
            if (modelObject instanceof CustomDiagramNode) {
                objectType = CUSTOM_DIAGRAM;
            }
        }
        return objectType;
    }
}
