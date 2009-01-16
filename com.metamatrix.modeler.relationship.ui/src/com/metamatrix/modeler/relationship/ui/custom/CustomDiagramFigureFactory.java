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
