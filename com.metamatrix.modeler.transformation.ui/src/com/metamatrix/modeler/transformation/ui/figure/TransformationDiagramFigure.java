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

package com.metamatrix.modeler.transformation.ui.figure;

import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFont;
import com.metamatrix.modeler.diagram.ui.actions.ScaledFontManager;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierFigure;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.part.TransformationDiagramLayout;
import com.metamatrix.modeler.transformation.ui.util.TransformationDiagramUtil;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * @since 5.0
 */
public class TransformationDiagramFigure extends FreeformLayer {
    private static final String TARGET_TITLE = "VIEW"; //$NON-NLS-1$
    private static final String SOURCES_TITLE = "SOURCES"; //$NON-NLS-1$
    private static final String SUID_LABEL = "suid"; //$NON-NLS-1$
    private static final String EMPTY_LABEL = ""; //$NON-NLS-1$ 

    private DiagramModelNode dNode;

    /**
     * @since 5.0
     */
    public TransformationDiagramFigure( DiagramModelNode dNode ) {
        super();
        this.dNode = dNode;
        init();
    }

    private void init() {
        this.setLayoutManager(new FreeformLayout());
        // Don't know why, but if you don't setOpaque(true), you cannot move by drag&drop!
        this.setOpaque(true);

        RGB currentBkgdColor = PreferenceConverter.getColor(DiagramUiPlugin.getDefault().getPreferenceStore(),
                                                            PluginConstants.Prefs.Appearance.TRANSFORM_BKGD_COLOR);

        this.setBackgroundColor(GlobalUiColorManager.getColor(currentBkgdColor));

    }

    /**
     * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
     * @since 5.0
     */
    @Override
    protected void paintFigure( Graphics theGraphics ) {
        super.paintFigure(theGraphics);

        // Check to see that the backing diagram is still around
        if (dNode.getModelObject() != null && !ModelObjectUtilities.isStale(dNode.getModelObject())) {
            // Call method to paint bkgd behind Target and to label Target & Sources
            if (!TransformationDiagramUtil.isTreeLayout()) {
                paintBkgdAndTitles(theGraphics);
            }
            paintSuidAndAliasDecorators(theGraphics);
        }
    }

    private void paintBkgdAndTitles( Graphics theGraphics ) {
        Font boldFont = ScaledFontManager.getFont(ScaledFont.TITLE_STYLE);
        Dimension targetDim = FigureUtilities.getStringExtents(TARGET_TITLE, boldFont);
        int headerHeight = targetDim.height + 10;

        int targetTableWidth = getWidthOfTarget();
        int widthOfTargetPanel = targetTableWidth + TransformationDiagramLayout.LEFT_PANEL_LEFT_MARGIN
                                 + TransformationDiagramLayout.LEFT_PANEL_RIGHT_MARGIN;

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
        int startingPoint = widthOfTargetPanel / 2 - targetDim.width / 2; // + TransformationDiagramLayout.rightPanel_leftMargin +
                                                                          // 10;

        theGraphics.setFont(boldFont);
        theGraphics.drawString(TARGET_TITLE, startingPoint, 7);
        startingPoint = widthOfTargetPanel + TransformationDiagramLayout.RIGHT_PANEL_LEFT_MARGIN + 30;
        theGraphics.drawString(SOURCES_TITLE, startingPoint, 7);

    }

    private int getWidthOfTarget() {
        int widthOfTarget = 0;

        DiagramModelNode thisDiagramNode = null;

        Object targetOfDiagram = null;
        List childFigures = this.getChildren();
        Object nextObj = null;

        for (Iterator iter = childFigures.iterator(); iter.hasNext();) {
            nextObj = iter.next();
            if (nextObj instanceof UmlClassifierFigure) {
                thisDiagramNode = ((UmlClassifierFigure)nextObj).getDiagramModelNode();
                if (targetOfDiagram == null) {
                    Diagram diagram = thisDiagramNode.getDiagram();
                    if (diagram != null) {
                        targetOfDiagram = diagram.getTarget();
                    }
                    Object modelObject = thisDiagramNode.getModelObject();
                    if (modelObject == targetOfDiagram) {
                        widthOfTarget = thisDiagramNode.getWidth();
                    }
                }
            }
            if (widthOfTarget > 0) {
                break;
            }
        }

        if (widthOfTarget < 10) {
            widthOfTarget = 10;
        }
        return widthOfTarget;
    }

    private void paintSuidAndAliasDecorators( Graphics theGraphics ) {
        // get the tNode
        Diagram diagram = (Diagram)dNode.getModelObject();
        EObject virtualTable = diagram.getTarget();
        if (virtualTable != null && virtualTable.eResource() != null) {
            EObject tRoot = TransformationHelper.getTransformationMappingRoot(virtualTable);
            if (tRoot != null) {
                EObject targetTableEObject = TransformationHelper.getTransformationLinkTarget(tRoot);

                if (TransformationHelper.tableSupportsUpdate(targetTableEObject)) {
                    Font smallFont = ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);
                    theGraphics.setFont(smallFont);
                    theGraphics.setForegroundColor(ColorConstants.black);
                    Dimension suidDim = FigureUtilities.getStringExtents(SUID_LABEL, smallFont);
                    int strHeight = suidDim.height;

                    // Get the Inputs to the TNode

                    List sources = TransformationHelper.getSourceEObjects(tRoot);
                    if (!sources.isEmpty()) {
                        EObject nextSource = null;
                        DiagramModelNode sourceDNode = null;
                        String suidString = null;
                        for (Iterator iter = sources.iterator(); iter.hasNext();) {
                            nextSource = (EObject)iter.next();
                            sourceDNode = DiagramUiUtilities.getDiagramModelNode(nextSource, dNode);
                            if (sourceDNode != null) {
                                // Let's do each source table (put SUID in upper left corner above table)

                                suidString = getSuidText(nextSource, tRoot);

                                if (suidString != null) {
                                    Point thePoint = new Point(sourceDNode.getPosition());
                                    thePoint.x = thePoint.x + 5;
                                    thePoint.y = thePoint.y - strHeight;
                                    theGraphics.drawString(suidString, thePoint);
                                }
                            }

                        }
                    }

                    // Let's do the Target Table (put SUID in upper right corner above table)
                    DiagramModelNode targetNode = DiagramUiUtilities.getDiagramModelNode(targetTableEObject, dNode);
                    Point thePoint = new Point(targetNode.getPosition());
                    thePoint.x = thePoint.x + targetNode.getWidth() - suidDim.width - 3;
                    thePoint.y = thePoint.y - strHeight;
                    theGraphics.drawString(SUID_LABEL, thePoint);
                }

                Font smallFont = ScaledFontManager.getFont(ScaledFont.SMALLER_PLAIN_STYLE);
                theGraphics.setFont(smallFont);
                theGraphics.setForegroundColor(ColorConstants.black);
                List sources = TransformationHelper.getSourceEObjects(tRoot);

                if (!sources.isEmpty()) {
                    EObject nextSource = null;
                    DiagramModelNode sourceDNode = null;
                    for (Iterator iter = sources.iterator(); iter.hasNext();) {
                        nextSource = (EObject)iter.next();
                        sourceDNode = DiagramUiUtilities.getDiagramModelNode(nextSource, dNode);
                        if (sourceDNode != null) {
                            // Now we check for number of aliases. Place in upper left just left of the corner.
                            List aliases = TransformationHelper.getSqlAliasesForSource(tRoot, nextSource);

                            if (aliases != null && !aliases.isEmpty() && aliases.size() > 1) {
                                String aliasString = EMPTY_LABEL + aliases.size();
                                int strWidth = FigureUtilities.getStringExtents(aliasString, smallFont).width;
                                Point thePoint = new Point(sourceDNode.getPosition());
                                thePoint.x = thePoint.x - 2 - strWidth; // + sourceDNode.getWidth() - 10;
                                thePoint.y = thePoint.y + 3; // - strHeight;
                                theGraphics.drawString(aliasString, thePoint);
                            }
                        }
                    }
                }

            }
        }
    }

    private String getSuidText( EObject sourceEObject,
                                EObject tRoot ) {
        String text = null;
        // Only allow SUID decorators if Target Table supports it

        if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable(sourceEObject)) {
            // get the target for this transformation

            EObject targetTableEObject = TransformationHelper.getTransformationLinkTarget(tRoot);
            if (targetTableEObject != null) {
                if (TransformationHelper.tableSupportsUpdate(targetTableEObject)) {
                    // We get the SUID status from the SqlTransformation object
                    // Let's get the source node model object
                    text = "s"; //$NON-NLS-1$
                    if (TransformationHelper.supportsUpdate(tRoot, sourceEObject)) {
                        text = text + "u"; //$NON-NLS-1$
                    }
                    if (TransformationHelper.supportsInsert(tRoot, sourceEObject)) {
                        text = text + "i"; //$NON-NLS-1$
                    }
                    if (TransformationHelper.supportsDelete(tRoot, sourceEObject)) {
                        text = text + "d"; //$NON-NLS-1$
                    }
                }
            }
        }

        return text;
    }

}
