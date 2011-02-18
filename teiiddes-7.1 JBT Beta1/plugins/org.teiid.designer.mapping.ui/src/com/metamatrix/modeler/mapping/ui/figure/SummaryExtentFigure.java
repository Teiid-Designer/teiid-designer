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
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import com.metamatrix.modeler.diagram.ui.util.colors.ColorPalette;
import com.metamatrix.ui.graphics.GlobalUiFontManager;


public class SummaryExtentFigure extends /*AbstractDiagramFigure*/ MappingExtentFigure { 

    private int iMappingNodeCount;
    private int iUnmappedNodeCount;
    private String sNumber;
    

    private Label numberLabel;
    private Color defaultBkgdColor = null;
    
    private PointList transformPointsForPointedFace = new PointList();
    private PointList transformPointsForFlatFace = new PointList();
    private boolean bSomeMappingClassesAreVisible;
    private Image imgImage;
    private int iPreferredWidth = 60;
    private int iPreferredHeight = 12;
    
    private int iPreferredLabelWidth = 55;
    private int iPreferredLabelHeight = 12;
    
    /**
     * 
     */
    public SummaryExtentFigure( int iMappingNodeCount,
                                int iUnmappedNodeCount,
                                ColorPalette colorPalette, 
                                boolean bSomeMappingClassesAreVisible,
                                Image imgImage, 
                                int iPosition ) {
        
        super( colorPalette );
        
        this.iMappingNodeCount = iMappingNodeCount;
        this.iUnmappedNodeCount = iUnmappedNodeCount;
        this.bSomeMappingClassesAreVisible = bSomeMappingClassesAreVisible;
        this.imgImage = imgImage;

        sNumber = createDisplayString();
        init( sNumber );
        
        createComponent();
//        System.out.println("[SummaryExtentFigure.ctor] BOT");

    }
    
    private String createDisplayString() {
        
        String sCount = "" + iMappingNodeCount;  //$NON-NLS-1$

        if ( iUnmappedNodeCount > 0 ) {                
            sCount += " / " + iUnmappedNodeCount;  //$NON-NLS-1$
        } 
        
        return sCount;
    }
    
    public Color getDefaultBackgroundColor() {
        
        if ( iUnmappedNodeCount > 0 ) {
            return ColorConstants.yellow;
        }
        return ColorConstants.lightGray;
    }
    
    @Override
    protected void init( String sNumber ) {
        
        if( sNumber != null ) {
            
            numberLabel = new Label( sNumber ); 
            numberLabel.setIcon( imgImage );
            numberLabel.setLabelAlignment( PositionConstants.LEFT );
            numberLabel.setTextAlignment( PositionConstants.LEFT );
            
            int iHeight = 8;
            numberLabel.setFont(GlobalUiFontManager.getFont(new FontData("Arial", iHeight, 3))); //$NON-NLS-1$

            this.add(numberLabel);

            numberLabel.setForegroundColor(ColorConstants.blue);
            numberLabel.setBackgroundColor(ColorConstants.red /*this.getLocalBackgroundColor()*/);
            setLabelSize( numberLabel );
            this.setSize( numberLabel.getSize().width + 20,  iHeight + 2 );
        }

        defaultBkgdColor = getDefaultBackgroundColor();
        
        transformPointsForPointedFace = new PointList();
        transformPointsForFlatFace = new PointList();

        // note: ixOffset plus the specifix x value 
        //       MUST be <= the total width of the figure, or it will not appear! 
        // So, calc the offset from the width of the control;
        // (the width is controlled from the node... see MappingExtentNode.SM_EXTENT_WIDTH) 
        int iXOffset = this.getBounds().width - 16;
        
        
        
        // the figure is only 16 pt tall, so recalc your points to limit 
        // the y range:  Instead of y = 0, 20, 40 (as in MappingExtent); use y = 0, 8, 16
        
        // create points for Pointed Face
        transformPointsForPointedFace.addPoint(0, 0);   // keep
        transformPointsForPointedFace.addPoint(10 + iXOffset, 0);
        transformPointsForPointedFace.addPoint(16 + iXOffset, 8); // this point is the bump   
        transformPointsForPointedFace.addPoint(10 + iXOffset, 16);
        transformPointsForPointedFace.addPoint(0, 16);  // keep
        transformPointsForPointedFace.addPoint(0, 0);   // keep
                
        // create points for Flat Face
        transformPointsForFlatFace.addPoint(0, 0);
        transformPointsForFlatFace.addPoint(10 + iXOffset, 0);
        transformPointsForFlatFace.addPoint(10 + iXOffset, 8); // this point makes it flat
        transformPointsForFlatFace.addPoint(10 + iXOffset, 16);
        transformPointsForFlatFace.addPoint(0, 16);
        transformPointsForFlatFace.addPoint(0, 0);
        
        extentOutline = new Polygon();

        if ( getSomeMappingClassesAreVisible() == true ) {
//            System.out.println("[SummaryExtentFigure.init] About to set to Pointed Face");
            extentOutline.setPoints(transformPointsForPointedFace);
        } else {
//            System.out.println("[SummaryExtentFigure.init] About to set to Flat Face");
            extentOutline.setPoints(transformPointsForFlatFace);
        }

        this.add(extentOutline);

        extentOutline.setLineWidth(2);
        extentOutline.setForegroundColor( ColorConstants.darkBlue );
        extentOutline.setBackgroundColor( getDefaultBackgroundColor() );
        extentOutline.setFill(true);
    }

    @Override
    protected void createComponent() {        
        setInitialSize();
        layoutThisFigure(this.getSize());
    }
    
    @Override
    protected void setLabelSize( Label label ) {

        Font theFont = label.getFont();

        int labelWidth = FigureUtilities.getTextExtents(label.getText(), theFont).width;
        if (label.getIcon() != null)
            labelWidth += label.getIcon().getBounds().width;

        int iTextWidth 
            = FigureUtilities.getTextExtents( numberLabel.getText(), numberLabel.getFont() ).width;
        Image icon = numberLabel.getIcon();
        
        if ( icon != null ) {
            int iIconWidth = icon.getBounds().width;
            int iFudgeFactor = 0;
            
            int iGap = iPreferredLabelWidth - ( iTextWidth + iIconWidth + iFudgeFactor );
//            System.out.println("\n[SummaryExtentFigure.setLabelSize] calc: iGap = iPreferredLabelWidth - ( iTextWidth + iIconWidth + iFudgeFactor ); " );
//            System.out.println("[SummaryExtentFigure.setLabelSize] iPreferredLabelWidth: " + iPreferredLabelWidth);
//            System.out.println("[SummaryExtentFigure.setLabelSize] iTextWidth: " + iTextWidth);
//            System.out.println("[SummaryExtentFigure.setLabelSize] iIconWidth: " + iIconWidth);
//            System.out.println("[SummaryExtentFigure.setLabelSize] iFudgeFactor: " + iFudgeFactor);
//            System.out.println("[SummaryExtentFigure.setLabelSize] iGap: " + iGap);

            label.setIconTextGap( iGap );
        }
        
        label.setSize( iPreferredLabelWidth, iPreferredLabelHeight );
    }
     
    @Override
    protected void setInitialSize() {

        this.setSize(new Dimension(iPreferredWidth, iPreferredHeight));
    }
    
    @Override
    protected void layoutThisFigure(Dimension newSize) {
        this.setSize(newSize);
        
        int centerX = newSize.width/2;
        int centerY = newSize.height/2;
        
        // adjust a bit more to the left sw do don't leave too much left
        //  margin before the icon
        int iShiftLeft = 6;
        
        if( numberLabel != null ) {
            
            int iDiff = newSize.width - numberLabel.getSize().width;
            iShiftLeft = iDiff/2 - 2;
            
            numberLabel.setLocation( 
                new Point(centerX - numberLabel.getBounds().width/2 - iShiftLeft,
                          centerY - numberLabel.getBounds().height/2) );
        }
    }
    
    @Override
    public void updateForSize(Dimension size){

        refreshOutlinePoints();
        /*
         * jh Lyra enh note: These resizing calcs force a certain shape and size.
         *                   We need to rewrite this to get the shape and size we want
         *                   when a new total size comes in.
         */
//        int thisHeight = size.height;
//        int thisWidth = size.width;
//        int twoThirds = (int)(2.0*thisWidth/3);
//        replacePoint( 0, 0, 0);
//        replacePoint( 1, twoThirds, 0);
//        replacePoint( 2, thisWidth, thisHeight/2);
//        replacePoint( 3, twoThirds, thisHeight);
//        replacePoint( 4, 0, thisHeight);
//        replacePoint( 5, 0, 0);


        
        if ( getSomeMappingClassesAreVisible() == true ) {
//          System.out.println("[SummaryExtentFigure.refreshOutlinePoints] About to set to Pointed Face");
          extentOutline.setPoints( transformPointsForPointedFace );
          extentOutline.setBackgroundColor( getDefaultBackgroundColor() );

        } else {
//          System.out.println("[SummaryExtentFigure.refreshOutlinePoints] About to set to Flat Face");
          extentOutline.setPoints( transformPointsForFlatFace );
          extentOutline.setBackgroundColor( getDefaultBackgroundColor() );
        }
        
        // do NOT layout the figure with a new size.  Extents and their graphics/text should not
        //  respond to zooming, because the tree node the attach to never changes its text size
        //  or height.  (Note: commenting this out had no effect on resizing...
        this.layoutThisFigure ( size );
        
        this.repaint();
    }
    
    @Override
    public void layoutComponent() {
        this.layoutThisFigure(this.getSize());
    }
    
    @Override
    public void updateSize() {
       
    }
    
    @Override
    protected void replacePoint(int index, int newX, int newY ) {
//        extentOutline.getPoints().setPoint(new Point(newX, newY), index);

//        System.out.println("[SummaryExtentFigure.replacePoint] index: " + index );
//        System.out.println("[SummaryExtentFigure.replacePoint] newX: " + newX );
//        System.out.println("[SummaryExtentFigure.replacePoint] newY: " + newY );

        if ( getSomeMappingClassesAreVisible() == true ) {
            transformPointsForPointedFace.setPoint(new Point(newX, newY), index);
        } else {
            transformPointsForFlatFace.setPoint(new Point(newX, newY), index);
        }
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
            extentOutline.setBackgroundColor(defaultBkgdColor); //getColor(ColorPalette.PRIMARY_BKGD_COLOR_ID));
    }
    /* (non-Javadoc)
     * @see org.eclipse.draw2d.IFigure#setSize(int, int)
     */
    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
    }
    
    @Override
    public void setDefaultBkgdColor(Color someColor) {
		defaultBkgdColor = someColor;
    }
    
    @Override
    public void setOutlineColor(Color someColor) {
        extentOutline.setForegroundColor(someColor);
    }
    
    
    @Override
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
    
    @Override
    protected void paintOutline(Graphics graphics) {
//        System.out.println("[SummaryExtentFigure.paintOutline] TOP " );

      PointList pts = extentOutline.getPoints();
//      System.out.println("\n[SummaryExtentFigure.paintOutline] Label size: " + numberLabel.getBounds() );
//      System.out.println("[SummaryExtentFigure.paintOutline] SE Figure size: " + this.getBounds() );
        
//      if ( getSomeMappingClassesAreVisible() == true ) {
//          System.out.println("[SummaryExtentFigure.paintOutline] Should be Pointed Face - " + createDisplayString() );
//          System.out.println("[SummaryExtentFigure.paintOutline] SE Figure size: " + this.getBounds() );
//          System.out.println("[SummaryExtentFigure.paintOutline] pts.getPoint(2): " + pts.getPoint(2) );
//      } else {
//          System.out.println("[SummaryExtentFigure.paintOutline] Should be Flat Face - " + createDisplayString() );
//          System.out.println("[SummaryExtentFigure.paintOutline] SE Figure size: " + this.getBounds() );
//          System.out.println("[SummaryExtentFigure.paintOutline] pts.getPoint(2): " + pts.getPoint(2) );
//      }
      
        int orgX = this.getBounds().x;
        int orgY = this.getBounds().y;
        
//        System.out.println("[SummaryExtentFigure.paintOutline]  point list size: " + pts.size() );            
        
        graphics.setLineWidth(1);
        graphics.setForegroundColor(ColorConstants.buttonDarkest);
        
//        System.out.println("[SummaryExtentFigure.paintOutline] About to call first drawLine " );
        graphics.drawLine(orgX + pts.getPoint(0).x, orgY + pts.getPoint(0).y + 1,
                          orgX + pts.getPoint(1).x, orgY + pts.getPoint(1).y + 1 );
        graphics.drawLine(orgX + pts.getPoint(1).x, orgY + pts.getPoint(1).y,
                          orgX + pts.getPoint(2).x-1, orgY + pts.getPoint(2).y );
        
        graphics.setForegroundColor(ColorConstants.buttonDarkest);
        graphics.drawLine(orgX + pts.getPoint(2).x-1, orgY + pts.getPoint(2).y,
                          orgX + pts.getPoint(3).x, orgY + pts.getPoint(3).y-2 );
        graphics.drawLine(orgX + pts.getPoint(3).x, orgY + pts.getPoint(3).y-2,
                          orgX + pts.getPoint(4).x, orgY + pts.getPoint(4).y-2 );
//        System.out.println("[SummaryExtentFigure.paintOutline] After calling drawLine 4 x" );

        // jh experiment:
        graphics.drawLine(orgX + pts.getPoint(4).x, orgY + pts.getPoint(4).y-2,
                          orgX + pts.getPoint(5).x, orgY + pts.getPoint(5).y-2 );    
//        System.out.println("[SummaryExtentFigure.paintOutline] BOT " );
    }

    private void refreshOutlinePoints() {
//        System.out.println("[SummaryExtentFigure.refreshOutlinePoints] TOP");
        
//        System.out.println("[SummaryExtentFigure.refreshOutlinePoints] 2");
        if ( getSomeMappingClassesAreVisible() == true ) {
//            System.out.println("[SummaryExtentFigure.refreshOutlinePoints] About to set to Pointed Face");
            extentOutline.setPoints( transformPointsForPointedFace );            
        } else {
//            System.out.println("[SummaryExtentFigure.refreshOutlinePoints] About to set to Flat Face");
            extentOutline.setPoints( transformPointsForFlatFace );
        }
        
        this.add( numberLabel );
//        System.out.println("[SummaryExtentFigure.refreshOutlinePoints] BOT");
    }
    
    public void setSomeMappingClassesAreVisible( boolean b ) {
        bSomeMappingClassesAreVisible = b;
        refreshOutlinePoints();
    }

    public boolean getSomeMappingClassesAreVisible() {
        return bSomeMappingClassesAreVisible;
    }
}



