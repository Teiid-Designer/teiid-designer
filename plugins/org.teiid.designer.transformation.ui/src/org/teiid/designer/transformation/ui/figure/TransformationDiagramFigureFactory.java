/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.figure;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.figure.AbstractDiagramFigureFactory;
import org.teiid.designer.diagram.ui.figure.DiagramFigure;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.diagram.ui.notation.NotationFigureGenerator;
import org.teiid.designer.diagram.ui.util.colors.ColorPalette;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.model.TransformationDiagramNode;
import org.teiid.designer.transformation.ui.model.TransformationNode;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;


/**
 * TransformationFigureFactory
 *
 * @since 8.0
 */
public class TransformationDiagramFigureFactory extends AbstractDiagramFigureFactory implements UiConstants {
    private static final int TRANSFORM = 1;
    private static final int TRANSFORM_DIAGRAM = 0;
    private static final int DEPENEDNCY_DIAGRAM = 2;
    
    /**
     * Construct an instance of UmlFigureFactory.
     * 
     */
    public TransformationDiagramFigureFactory() {
        super();
    }

    @Override
    public Figure createFigure(Object modelObject, String sNotationId ) {

        Figure newFigure = null;
        switch( getObjectType(modelObject) ) {
            
            case TRANSFORM:
            case DEPENEDNCY_DIAGRAM:
            case TRANSFORM_DIAGRAM: {
                newFigure = createFigure(modelObject);
            } break;
            
            // Delegate to the UML figure factory to make figures for all other model types.
            default: {
                // Here's where we get the notation manager and tell it to create a figure
                // for this modelObject.  So it'll come back in whatever "Notation" it desires.
                NotationFigureGenerator generator = DiagramUiPlugin.getDiagramNotationManager().getFigureGenerator(sNotationId);
                if( generator != null )
                    newFigure = generator.createFigure(modelObject);
                else {
                    ModelerCore.Util.log( IStatus.ERROR, Util.getString(DiagramUiConstants.Errors.FIGURE_GENERATOR_FAILURE));
                }
            } break;
            
        }

        return newFigure;
    }
    

    @Override
    public Figure createFigure(Object modelObject) {

        Figure newFigure = null;
        ColorPalette colorPalette = null;

        int objectType = getObjectType(modelObject);
        switch( objectType ) {
            case DEPENEDNCY_DIAGRAM:
            case TRANSFORM_DIAGRAM: {
                if (objectType == DEPENEDNCY_DIAGRAM) {
                    newFigure = new FreeformLayer();
                    newFigure.setLayoutManager(new FreeformLayout());
                    // Don't know why, but if you don't setOpaque(true), you cannot move by drag&drop!
                    newFigure.setOpaque(true);
                    // dependency:
                    RGB currentBkgdColor = PreferenceConverter.getColor(
                            DiagramUiPlugin.getDefault().getPreferenceStore(),
                            PluginConstants.Prefs.Appearance.DEPENDENCY_BKGD_COLOR);
                    newFigure.setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
                } else {
                    newFigure = new TransformationDiagramFigure((DiagramModelNode)modelObject);
                } // endif
                
            } break;
            
            case TRANSFORM: {
                TransformationNode tNode = (TransformationNode)modelObject;
                colorPalette = getColorPalette((DiagramModelNode)modelObject);
                newFigure = new TransformationFigure(tNode, "T", colorPalette); //$NON-NLS-1$
                // Now let's find out if this is a union query
                if( tNode.isUnion() ) {
                    ((TransformationFigure)newFigure).setSubscript("u"); //$NON-NLS-1$
                }
                
                // Need to notify t-objects to update their error/warning status
                if( modelObject instanceof DiagramModelNode ) {
                    DiagramModelNode node = (DiagramModelNode)modelObject;
                    ((DiagramFigure)newFigure).setDiagramModelNode(node);
                    if( node.hasErrors())
                        ((DiagramFigure)newFigure).updateForError(true);
                    else if( node.hasWarnings() )
                        ((DiagramFigure)newFigure).updateForWarning(true);
                }
            } break;
            
            // Delegate to the UML figure factory to make figures for all other model types.
            default: {
            } break;
            
        }

        return newFigure;
    }
    
    private int getObjectType( Object modelObject ) {
        int objectType = -1;
        
        if( modelObject != null ) {
            if (modelObject instanceof TransformationDiagramNode) {
                // look deeper, to see if a dependency diagram:
                Diagram d = (Diagram) ((TransformationDiagramNode)modelObject).getModelObject();
                if (d.getType() != null 
                 && d.getType().equals(PluginConstants.DEPENDENCY_DIAGRAM_TYPE_ID)) {
                    // we are a dependency diagram:
                    objectType = DEPENEDNCY_DIAGRAM;
                } else {
                    // regular diagram:
                    objectType = TRANSFORM_DIAGRAM;
                } // endif -- diagram type
            } else if (modelObject instanceof TransformationNode) {
                objectType = TRANSFORM;
            }
        }
        return objectType;
    }
    
    private ColorPalette getColorPalette(DiagramModelNode node) {
        return DiagramUiPlugin.
                    getDiagramTypeManager().getDiagram(PluginConstants.TRANSFORMATION_DIAGRAM_TYPE_ID).
                            getColorPaletteManager().getColorPalette(node.getModelObject());
    }
}
