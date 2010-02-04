/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.printing;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.PrinterGraphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;


public class DiagramPrintingAnalyzer extends DiagramPrintingOperation {

    public static PrinterData printerData;
    private GC printerGC;  // Note: Only one GC instance should be created per print job
    private PrinterGraphics printerGraphics;
    private SWTGraphics g;
    private ArrayList arylPageRects = new ArrayList(); 
    private boolean bApplyPageRange;
    private Dimension pageSize;
    private boolean debugMode = false;
    
    
    /**
     * The default print mode. Prints at 100% scale and tiles horizontally and/or vertically, 
     * if necessary.
     */
    public static final int SCALE = 9;

    public DiagramPrintingAnalyzer(Printer p,
                                   GraphicalViewer g) {
        super(p, g);
        setPageSize();
    }

    public static void setPrinterData( PrinterData printerData ) {
//        System.out.println( "[DiagramPrintingAnalyzer.setPrinterData] About to save printerData" );
        DiagramPrintingAnalyzer.printerData = printerData;
    }
    
    public static PrinterData getPrinterData() {
        return DiagramPrintingAnalyzer.printerData;
    }
    
    public Dimension getPageSize() {
        return this.pageSize;
    }
    
    public void reAnalyze() {
        this.reset();
        setPageSize();
    }
    
    public void setPageSize() {

        Dimension dimPageSize = null;
        List listRects = countPages( false );
        
        // report the list
        for ( int i = 0; i < listRects.size(); i++ ) {
//            System.out.println("[DiagramPrintingAnalyzer.getPageSize] rect: " + i + " - " + listRects.get( i ) );   //$NON-NLS-1$
        }
        
        if ( listRects.size() > 0 ) {
            org.eclipse.draw2d.geometry.Rectangle rect 
                = (org.eclipse.draw2d.geometry.Rectangle)listRects.get( 0 );
            int width = rect.width;
            int height = rect.height;
            
            if ( !isPortrait() ) {
                // if LANDSCAPE, swap height and with now, because they were calculated on the basis
                //  of a rotated image
                if( isPrinterPortrait() ) {
                    width = rect.height;
                    height = rect.width;
                }
            } else {
                // if PORTRAIT, use the width and height as-'are' unless printer portrait is Landscape
                if( !isPrinterPortrait() ) {
                    width = rect.height;
                    height = rect.width;
                }
            }
            
            
            dimPageSize = new Dimension(width, height);
            
            if( debugMode ) {
                System.out.println("[DiagramPrintingAnalyzer.getPageSize] number of rects: " + listRects.size() );   //$NON-NLS-1$
                System.out.println("[DiagramPrintingAnalyzer.getPageSize] dim of first rect: " + listRects.get( 0 ) );   //$NON-NLS-1$        
            }
        }

        this.pageSize = dimPageSize;
    }
    
    public void printCurrentAnalysis() {
        if( debugMode ) {
            System.out.println(this);
        }
    }
    
    @Override
    public String toString() {
        PrintSettings settings = getPrintSettings();
        StringBuffer result = new StringBuffer(super.toString());
        result.append("\n ----- Current Diagram Page Print Analysis -----"); //$NON-NLS-1$
        result.append("\n Printer Orient: "); //$NON-NLS-1$
        result.append(getPrinterOrientation());
        result.append("\n    Orientation: "); //$NON-NLS-1$
        result.append(getOrientation());
        result.append("\n        Scaling: "); //$NON-NLS-1$
        result.append(settings.getScaleOptionString());
        if( settings.getScaleOption() == PrintSettings.SCALE_ADJUST_TO_PERCENT ) {
            result.append("\n    Scaled Perc: "); //$NON-NLS-1$
            result.append(settings.getScalePercent());
        }
        result.append("\n     Page Scope: "); //$NON-NLS-1$
        result.append(settings.getPageScopeString());
        result.append("\n   Diagram Size: "); //$NON-NLS-1$
        String dSize = " (W, H) = " + getDiagramSize(); //$NON-NLS-1$
        result.append(dSize);
        result.append("\n   Print Region: "); //$NON-NLS-1$
        String pSize = " (W, H) = " + getPrintRegion().getSize(); //$NON-NLS-1$
        result.append(pSize);
        result.append("\n        # Pages: "); //$NON-NLS-1$
        result.append(getNumberOfPages());
        result.append("\n        # Scale: "); //$NON-NLS-1$
        result.append(getPrintScaleFactor());
        result.append("\n -----------------------------------------------"); //$NON-NLS-1$
        
        return result.toString();
    }
    
    private void setApplyPageRange( boolean bApplyPageRange ) {
        this.bApplyPageRange = bApplyPageRange;
    }
    
    private boolean shouldApplyPageRange() {
        return bApplyPageRange;
    }
    
    public int getNumberOfPages() {
        if( arylPageRects != null )
            return arylPageRects.size();
        
        return 0;
    }

    public ArrayList countPages( boolean bApplyPageRange ) {
        
        setApplyPageRange( bApplyPageRange );
        arylPageRects = new ArrayList();
        
        // calling super's printPages(); it will in turn call our printImage(),
        //  which will count rectangles instead of printing the images.  In
        //  this way we can reuse the Operation's printing code and not have
        //  to maintain it separately here.
        super.printPages();
        
//        System.out.println("[DPA.countPages] - pages: " + arylPageRects.size() );
//        if ( arylPageRects.size() > 0 ) {
//            org.eclipse.draw2d.geometry.Rectangle rect 
//                = (org.eclipse.draw2d.geometry.Rectangle)arylPageRects.get( 0 );
//            System.out.println("[DPA.countPages] - page size: " + rect );
//        }
        
        // do not let the printerGraphics persist
        disposeOfPrinterGraphics();
        
        return arylPageRects;
    }
    
    
    @Override
    protected void printImage( PrinterGraphics graphics, Image sourceImage, int xOffset, int yOffset, Rectangle clipRect ) {
        if( debugMode ) {
            System.out.println("[DPA.printImage()]  clipRect = " + clipRect); //$NON-NLS-1$
        }
        // this method does not print, but just collects the rectangles so they can be counted and measured
        arylPageRects.add(new Rectangle(clipRect));// new Rectangle( clipRect.x, clipRect.y, clipRect.height, clipRect.width ) );
    }
    
    @Override
    protected boolean okToPrint( int iPageNo ) {
        
        if ( shouldApplyPageRange() ) {
            return super.okToPrint( iPageNo );
        }
        
        return true;        
    }
    
    /**
     * Returns a new PrinterGraphics setup for the Printer associated with this
     * PrintOperation.
     *      THIS IS THE METHOD FROM PrintOperation with as few mods as possible
     * @return PrinterGraphics The new PrinterGraphics
     */
    @Override
    protected PrinterGraphics getFreshPrinterGraphics() {
        if (printerGraphics != null) {
            printerGraphics.dispose();
            g.dispose();
            printerGraphics = null;
            g = null;
        }
        
        g = new SWTGraphics( getPrinterGC() );
        printerGraphics = new PrinterGraphics(g, getPrinter() );
        setupGraphicsForPage(printerGraphics);
        return printerGraphics;
    }

    protected GC getPrinterGC() {
        if ( printerGC == null ) {
            printerGC = new GC( getPrinter() );
        }
        
        return printerGC;
    }
    
    protected void disposeOfPrinterGraphics() {
        if (printerGraphics != null) {
            printerGraphics.dispose();
            g.dispose();
            printerGraphics = null;
            g = null;
        }
        
        if ( printerGC != null ) {
            printerGC.dispose();
            printerGC = null;
        }
    }
    
    @Override
    protected void setupGraphicsForPage(PrinterGraphics pg) {
        Rectangle printRegion = getPrintRegion();
        pg.clipRect( printRegion );
        pg.translate( printRegion.getTopLeft() );
    }
    
    
}
