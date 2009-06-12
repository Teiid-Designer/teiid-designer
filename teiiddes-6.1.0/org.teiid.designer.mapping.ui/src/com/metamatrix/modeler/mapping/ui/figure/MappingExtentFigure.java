/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.figure;


import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
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
 * MappingExtentFigure
 */
public class MappingExtentFigure extends AbstractDiagramFigure {

    protected Polygon extentOutline;
    private Label nameLabel;
    private Color defaultBkgdColor = null;
    
    private PointList transformPoints = new PointList();
    
    /**
     * 
     */
    public MappingExtentFigure(String name, ColorPalette colorPalette) {
        
        super(colorPalette);
        
        init(name);
        
        createComponent();
     }
    
    /**
     * 
     */
    public MappingExtentFigure( ColorPalette colorPalette) {
        
        super(colorPalette);
        
        createComponent();
     }

    public void setName( String sName ) {
        init( sName );
    }
    
    
    
    protected void init(String name) {
        defaultBkgdColor = getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID);
        
        extentOutline = new Polygon();
        transformPoints.addPoint(0, 0);
        transformPoints.addPoint(10, 0);
        transformPoints.addPoint(20, 20);
        transformPoints.addPoint(10, 40);
        transformPoints.addPoint(0, 40);
        transformPoints.addPoint(0, 0);
        extentOutline.setPoints(transformPoints);

        this.add(extentOutline);

        extentOutline.setLineWidth(2);
        extentOutline.setForegroundColor(ColorConstants.darkBlue);

        extentOutline.setBackgroundColor(getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID));
        
        extentOutline.setFill(true);
        
        if( name != null ) {

            nameLabel = new Label(name);
            nameLabel.setFont(GlobalUiFontManager.getFont(new FontData( "Arial", 16, 3))); //$NON-NLS-1$
            this.add(nameLabel);

            nameLabel.setForegroundColor(ColorConstants.black);
            nameLabel.setBackgroundColor(this.getLocalBackgroundColor());
            setLabelSize(nameLabel);
        }
        
    }
    
    protected void createComponent() {        
        setInitialSize();
        layoutThisFigure(this.getSize());
    }
    
    protected void setLabelSize( Label label ) {

        Font theFont = label.getFont();

        int labelWidth = FigureUtilities.getTextExtents(label.getText(), theFont).width;
        if (label.getIcon() != null)
            labelWidth += label.getIcon().getBounds().width;
        int labelHeight = FigureUtilities.getTextExtents(label.getText(), theFont).height;
    
        label.setSize(labelWidth, labelHeight);
    }
     
    protected void setInitialSize() {

        int maxWidth = 21;
        int maxHeight = 12;

        this.setSize(new Dimension(maxWidth, maxHeight));
    }
    
    protected void layoutThisFigure(Dimension newSize) {
        this.setSize(newSize);
        
        int centerX = newSize.width/2;
        int centerY = newSize.height/2;
        
        if( nameLabel != null ) {
            nameLabel.setLocation( 
                new Point(centerX - nameLabel.getBounds().width/2,
                          centerY - nameLabel.getBounds().height/2) );
        }
    }
    
    @Override
    public void updateForSize(Dimension size){
        
        int thisHeight = size.height;
        int thisWidth = size.width;
        int twoThirds = (int)(2.0*thisWidth/3);
        replacePoint( 0, 0, 0);
        replacePoint( 1, twoThirds, 0);
        replacePoint( 2, thisWidth, thisHeight/2);
        replacePoint( 3, twoThirds, thisHeight);
        replacePoint( 4, 0, thisHeight);
        replacePoint( 5, 0, 0);

        extentOutline.setPoints(transformPoints);
        
        this.layoutThisFigure(size);
        
        this.repaint();
        
    }
    
    public void layoutComponent() {
        this.layoutThisFigure(this.getSize());
    }
    
    public void updateSize() {
       
    }
    
    protected void replacePoint(int index, int newX, int newY ) {
        transformPoints.setPoint(new Point(newX, newY), index);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.diagram.ui.figure.DiagramFigure#hiliteBackground(org.eclipse.swt.graphics.Color)
     */
    @Override
    public void hiliteBackground(Color hiliteColor) {
        if( hiliteColor == null ) {
            extentOutline.setBackgroundColor(defaultBkgdColor);
        } else {
            extentOutline.setBackgroundColor(hiliteColor);
        }
        
    }
    
    
    @Override
    public void showSelected(boolean selected) {
        if( selected )
            extentOutline.setBackgroundColor(getColor(ColorPalette.SELECTION_COLOR_ID));
        else
            extentOutline.setBackgroundColor( getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID) );
    }
    /* (non-Javadoc)
     * @see org.eclipse.draw2d.IFigure#setSize(int, int)
     */
    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
    }
    
    public void setDefaultBkgdColor(Color someColor) {
		defaultBkgdColor = someColor;
    }
    
    public void setOutlineColor(Color someColor) {
        extentOutline.setForegroundColor(someColor);
    }
    
    
    public void setOutlineWidth(int newWidth) {
        extentOutline.setLineWidth(newWidth);
    }
    
    @Override
    public void paint(Graphics graphics) {
        graphics.pushState();
        super.paint(graphics);
        paintOutline(graphics);
        graphics.popState();
        graphics.restoreState();
    }
    
    protected void paintOutline(Graphics graphics) {
        int orgX = this.getBounds().x;
        int orgY = this.getBounds().y;
        PointList pts = extentOutline.getPoints();
        
        graphics.setLineWidth(1);
        graphics.setForegroundColor(ColorConstants.buttonDarkest);
        
        graphics.drawLine(orgX + pts.getPoint(0).x, orgY + pts.getPoint(0).y + 1,
                          orgX + pts.getPoint(1).x, orgY + pts.getPoint(1).y + 1 );
        graphics.drawLine(orgX + pts.getPoint(1).x, orgY + pts.getPoint(1).y,
                          orgX + pts.getPoint(2).x-1, orgY + pts.getPoint(2).y );
        
        graphics.setForegroundColor(ColorConstants.buttonDarkest);
        graphics.drawLine(orgX + pts.getPoint(2).x-1, orgY + pts.getPoint(2).y,
                          orgX + pts.getPoint(3).x, orgY + pts.getPoint(3).y-2 );
        graphics.drawLine(orgX + pts.getPoint(3).x, orgY + pts.getPoint(3).y-2,
                          orgX + pts.getPoint(4).x, orgY + pts.getPoint(4).y-2 );
    }

}



