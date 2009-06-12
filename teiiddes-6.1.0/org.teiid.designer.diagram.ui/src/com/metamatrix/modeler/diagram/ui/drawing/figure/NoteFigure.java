/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import com.metamatrix.modeler.diagram.ui.figure.AbstractDiagramFigure;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.ui.graphics.GlobalUiFontManager;

/**
 * NoteFigure
 */
public class NoteFigure extends AbstractDiagramFigure {

    private Polygon documentOutline;
    private Label textLabel;
    private static Color defaultBkgdColor = null;
    private static final int FOLD_WIDTH = 20;
    private static final int ySpacing = 2;
    private static final int xInset = 6;
    
    private PointList documentPoints = new PointList();
    
    /**
     * 
     */
    public NoteFigure(ColorPalette colorPalette) {
        super(colorPalette);
                
        init("someText"); //$NON-NLS-1$
        
        createComponent();
    }
    /**
     * 
     */
    public NoteFigure(String noteText, ColorPalette colorPalette) {
        super(colorPalette);
        
        init(noteText);
        
        createComponent();
    }
    
    private void init(String someText) {
        defaultBkgdColor = getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID);
        
        documentOutline = new Polygon();
        documentPoints.addPoint(0, 0);
        documentPoints.addPoint(100, 0);
        documentPoints.addPoint(100+FOLD_WIDTH, FOLD_WIDTH);
        documentPoints.addPoint(100+FOLD_WIDTH, 100);
        documentPoints.addPoint(0, 100);
        documentPoints.addPoint(0, 0);
        documentOutline.setPoints(documentPoints);

        this.add(documentOutline);

        documentOutline.setLineWidth(2);
        documentOutline.setForegroundColor(ColorConstants.darkBlue);
        documentOutline.setBackgroundColor(defaultBkgdColor);
        
        if( someText != null ) {

            textLabel = new Label(someText);
            textLabel.setFont(GlobalUiFontManager.getFont(new FontData("Arial", 10, 0))); //$NON-NLS-1$
            this.add(textLabel);

            textLabel.setForegroundColor(ColorConstants.black);
            textLabel.setBackgroundColor(this.getBackgroundColor());
            setLabelSize(textLabel);

        }
        
    }
    
    private void createComponent() {        
        setInitialSize();
        layoutThisFigure(this.getSize());
    }
    
    private void setLabelSize( Label label ) {

        Font theFont = label.getFont();

        int labelWidth = FigureUtilities.getTextExtents(label.getText(), theFont).width;
        if (label.getIcon() != null)
            labelWidth += label.getIcon().getBounds().width;
        int labelHeight = FigureUtilities.getTextExtents(label.getText(), theFont).height;
    
        label.setSize(labelWidth, labelHeight);
    }
     
    private void setInitialSize() {
        int maxWidth = 10;
        int maxHeight = 10 + ySpacing*2;
        if( textLabel != null ) {
            maxHeight += textLabel.getBounds().height + ySpacing;
            maxWidth = Math.max( maxWidth, textLabel.getSize().width );
        }
        
        maxWidth += xInset*3;
        replacePoint( 1, maxWidth - FOLD_WIDTH, 0);
        replacePoint( 2, maxWidth, FOLD_WIDTH);
        replacePoint( 3, maxWidth, maxHeight);
        replacePoint( 4, 0, maxHeight);
        
        documentOutline.setPoints(documentPoints);
        
        this.setSize(new Dimension(maxWidth, maxHeight));
    }
    
    private void layoutThisFigure(Dimension newSize) {
        this.setSize(newSize);
        
        if( textLabel != null ) {
            textLabel.setLocation( 
                new Point(xInset, xInset + FOLD_WIDTH) );
        }
    }
    
    @Override
    public void updateForSize(Dimension size){
        
        int thisHeight = size.height;
        int thisWidth = size.width;
        replacePoint( 0, 0, 0);
        replacePoint( 1, thisWidth - FOLD_WIDTH, 0);
        replacePoint( 2, thisWidth, FOLD_WIDTH);
        replacePoint( 3, thisWidth, thisHeight);
        replacePoint( 4, 0, thisHeight);
        replacePoint( 5, 0, 0);

        documentOutline.setPoints(documentPoints);
        
        this.layoutThisFigure(size);
        
        this.repaint();
        
    }
    
    public void layoutComponent() {
        this.layoutThisFigure(this.getSize());
    }
    
    public void updateSize() {
       
    }
    
    private void replacePoint(int index, int newX, int newY ) {
        documentPoints.setPoint(new Point(newX, newY), index);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    @Override
    public void hiliteBackground(Color hiliteColor) {
        if( hiliteColor == null ) {
            documentOutline.setBackgroundColor(defaultBkgdColor);
        } else {
            documentOutline.setBackgroundColor(hiliteColor);
        }
        
    }
    
    
    @Override
    public void showSelected(boolean selected) {
        if( selected )
            documentOutline.setBackgroundColor(getColor(ColorPalette.SELECTION_COLOR_ID));
        else
            documentOutline.setBackgroundColor(getColor(ColorPalette.SECONDARY_BKGD_COLOR_ID));
    }
    /* (non-Javadoc)
     * @see org.eclipse.draw2d.IFigure#setSize(int, int)
     */
    @Override
    public void setSize(int w, int h) {
//System.out.println(" -->> MappingExtentFigure.setSize():  New Size = [" + w + "," +  h + "]");
        super.setSize(w, h);
    }

}
