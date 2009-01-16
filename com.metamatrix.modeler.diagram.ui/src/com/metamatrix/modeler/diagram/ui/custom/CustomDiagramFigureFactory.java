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

package com.metamatrix.modeler.diagram.ui.custom;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigureFactory;
import com.metamatrix.modeler.diagram.ui.notation.NotationFigureGenerator;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * PackageDiagramFigureFactory
 */
public class CustomDiagramFigureFactory extends AbstractDiagramFigureFactory implements DiagramUiConstants {
    private static final int CUSTOM_DIAGRAM = 0;

    /**
     * Construct an instance of UmlFigureFactory.
     * 
     */
    public CustomDiagramFigureFactory() {
        super();
    }

    @Override
    public Figure createFigure(Object modelObject, String sNotationId) {

        Figure newFigure = null;
        switch( getObjectType(modelObject) ) {
            
            case CUSTOM_DIAGRAM: {
                newFigure = new FreeformLayer();
                newFigure.setLayoutManager(new FreeformLayout());
                // Don't know why, but if you don't setOpaque(true), you cannot move by drag&drop!
                newFigure.setOpaque(true);
                RGB currentBkgdColor = 
                    PreferenceConverter.getColor(
                        DiagramUiPlugin.getDefault().getPreferenceStore(),
                        PluginConstants.Prefs.Appearance.CUSTOM_BKGD_COLOR);
                newFigure.setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
            } break;
            
            default: {
                // Here's where we get the notation manager and tell it to create a figure
                // for this modelObject.  So it'll come back in whatever "Notation" it desires.
                NotationFigureGenerator generator = DiagramUiPlugin.getDiagramNotationManager().getFigureGenerator(sNotationId);
                if( generator != null )
                    newFigure = generator.createFigure(modelObject);
                else {
                    ModelerCore.Util.log( IStatus.ERROR, Util.getString(Errors.FIGURE_GENERATOR_FAILURE));
                }
                    
            } break;
        }

        return newFigure;
    }
    
    protected int getObjectType( Object modelObject ) {
        int objectType = -1;
        
        if( modelObject != null ) {
            if (modelObject instanceof CustomDiagramNode) {
                objectType = CUSTOM_DIAGRAM;
            }
        }
        return objectType;
    }
}
