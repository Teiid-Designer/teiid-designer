/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.figure;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierFigure;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.part.MappingDiagramLayout;
import com.metamatrix.modeler.transformation.ui.part.TransformationDiagramLayout;
import com.metamatrix.ui.graphics.GlobalUiColorManager;


/** 
 * @since 5.0
 */
public class MappingDiagramFigure extends FreeformLayer {
    private static final String TARGET_TITLE = "VIEW"; //$NON-NLS-1$
    private static final String SOURCES_TITLE = "SOURCES"; //$NON-NLS-1$
    /** 
     * 
     * @since 5.0
     */
    public MappingDiagramFigure() {
        super();
        init();
    }
    
    private void init() {
        this.setLayoutManager(new FreeformLayout());
        // Don't know why, but if you don't setOpaque(true), you cannot move by drag&drop!
        this.setOpaque(true);

        RGB currentBkgdColor = PreferenceConverter.getColor(
                DiagramUiPlugin.getDefault().getPreferenceStore(),
                PluginConstants.Prefs.Appearance.MAPPING_BKGD_COLOR);

        this.setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));
    }


    /** 
     * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
     * @since 5.0
     */
    @Override
    protected void paintFigure(Graphics theGraphics) {
        super.paintFigure(theGraphics);
        // Call method to paint bkgd behind Target and to label Target & Sources
        paintBkgdAndTitles(theGraphics);
    }
    
    private void paintBkgdAndTitles(Graphics theGraphics) {
        Font boldFont = ScaledFontManager.getFont(ScaledFont.TITLE_STYLE);
        Dimension targetDim = FigureUtilities.getStringExtents(TARGET_TITLE, boldFont);
        int headerHeight = targetDim.height + 10;
        
        int targetTableWidth = getWidthOfTarget();
        int widthOfTargetPanel = targetTableWidth + 
            MappingDiagramLayout.MAPPING_CLASS_MARGIN + 
            MappingDiagramLayout.LEFT_PANEL_RIGHT_MARGIN;
        
        // Paint a rectangle around the target table (Left side) and outline in black
        theGraphics.setForegroundColor(ColorConstants.black);
        theGraphics.setBackgroundColor(ColorConstants.cyan);
        theGraphics.fillRectangle(0, 0, widthOfTargetPanel, 3000);
        theGraphics.drawRectangle(0, 0, widthOfTargetPanel, 3000);
        // Draw rectangle accross title header to separate titles.
        theGraphics.drawRectangle(0, 0, 3000, headerHeight);
        
        // Create Target and Sources titles
        
        theGraphics.setForegroundColor(ColorConstants.buttonDarker);
        // Center the target over the Target Table
        int startingPoint = widthOfTargetPanel/2 - targetDim.width/2; //+ TransformationDiagramLayout.rightPanel_leftMargin + 10;
        
        theGraphics.setFont(boldFont);
        theGraphics.drawString(TARGET_TITLE, startingPoint, 8);
        startingPoint = widthOfTargetPanel + TransformationDiagramLayout.RIGHT_PANEL_LEFT_MARGIN + 30;
        theGraphics.drawString(SOURCES_TITLE, startingPoint, 8);
        
    }
    
    private int getWidthOfTarget() {
        int widthOfTarget = 0;
        
        DiagramModelNode thisDiagramNode = null;

        Object targetOfDiagram = null;
        List childFigures = this.getChildren();
        Object nextObj = null;

        for( Iterator iter = childFigures.iterator(); iter.hasNext(); ) {
            nextObj = iter.next();
            if( nextObj instanceof UmlClassifierFigure ) {
                thisDiagramNode = ((UmlClassifierFigure)nextObj).getDiagramModelNode();
                if( targetOfDiagram == null ) {
                    Diagram diagram = thisDiagramNode.getDiagram();
                    if( diagram != null ) {
                        targetOfDiagram = diagram.getTarget();
                    }
                    Object modelObject = thisDiagramNode.getModelObject();
                    if( modelObject == targetOfDiagram ) {
                        widthOfTarget = thisDiagramNode.getWidth();
                    }
                }
            }
            if( widthOfTarget > 0 ) {
                break;
            }
        }
        
        if( widthOfTarget < 10 ) {
            widthOfTarget = 10;
        }
        return widthOfTarget;
    }
    

}
