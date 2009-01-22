/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.printing;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PrinterGraphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;

public class DiagramPrintingOperation extends PrintGraphicalViewerOperation {

    private PrintSettings psSettings;
    private int iPrintMode = -1;
    private Rectangle printRegion;
    private String orientation = PrintSettings.PORTRAIT;
    private String printerOrientation = PrintSettings.PORTRAIT;
    private Dimension diagramSize;
    private double printScaleFactor = 1.0;
    private boolean debugMode = false;

    /**
     * The default print mode. Prints at 100% scale and tiles horizontally and/or vertically, if necessary.
     */
    public static final int SCALE = 9;

    public DiagramPrintingOperation( Printer p,
                                     GraphicalViewer g ) {
        super(p, g);
        loadPrintSettings();
    }

    public String getOrientation() {
        return this.orientation;
    }

    public String getPrinterOrientation() {
        return this.printerOrientation;
    }

    public boolean isPrinterPortrait() {
        org.eclipse.swt.graphics.Rectangle oClientArea = getPrinter().getClientArea();
        return (oClientArea.height > oClientArea.width);
    }

    public boolean isPortrait() {
        return getPrintSettings().isPortrait();
    }

    public void reset() {
        printRegion = null;
        psSettings = null;
        loadPrintSettings();
    }

    public void loadPrintSettings() {
        PrintSettings settings = getPrintSettings();
        if (settings.isPortrait()) orientation = PrintSettings.PORTRAIT;
        else orientation = PrintSettings.LANDSCAPE;

        // NOTE: Actual Printer Settings are changing the Client Area aspect. In other words,
        // the client area is established by the portrait/landscape selected in the printer
        // preferences. We need to take this into account.
        if (isPrinterPortrait()) printerOrientation = PrintSettings.PORTRAIT;
        else printerOrientation = PrintSettings.LANDSCAPE;

    }

    /**
     * Copied from: PrintOperation Returns a Rectangle that represents the region that can be printed to. The x, y, height, and
     * width values are using the printers coordinates.
     * 
     * @return the print region
     */
    public Rectangle getPrintRegionORIGINAL() {

        /*  getPrintRegion()
         *  Applicable Settings:
         *      Margins: ( all 4 )
         *      Scale: FIT_TO_ONE_PAGE OR FIT_TO_ONE_PAGE_HIGH OR FIT_TO_ONE_PAGE_WIDE
         */

        // Since this uses Preferences, we cannot do this lazily/once
        org.eclipse.swt.graphics.Rectangle trim = getPrinter().computeTrim(0, 0, 0, 0);
        org.eclipse.swt.graphics.Rectangle clientArea = getPrinter().getClientArea();
        org.eclipse.swt.graphics.Point printerDPI = getPrinter().getDPI();

        PrintMargins printMargin = getPrintSettings().getMargins();

        printRegion = new Rectangle();

        printRegion.x = (int)Math.max((printMargin.left * printerDPI.x) - trim.width, clientArea.x);
        printRegion.y = (int)Math.max((printMargin.top * printerDPI.y) - trim.height, clientArea.y);

        printRegion.width = (int)((clientArea.x + clientArea.width) - printRegion.x - Math.max(0,
                                                                                               (printMargin.right * printerDPI.x)
                                                                                               - trim.width));

        printRegion.height = (int)((clientArea.y + clientArea.height) - printRegion.y - Math.max(0,
                                                                                                 (printMargin.bottom * printerDPI.y)
                                                                                                 - trim.height));
        //     System.out.println("[DiagramPrintingAnalyzer.getPrintRegion] resulting printRegion: " + printRegion.toString() );  //$NON-NLS-1$

        return printRegion;
    }

    /**
     * Copied from: PrintOperation Returns a Rectangle that represents the region that can be printed to. The x, y, height, and
     * width values are using the printers coordinates.
     * 
     * @return the print region
     */
    @Override
    public Rectangle getPrintRegion() {
        if (printRegion == null) {
            org.eclipse.swt.graphics.Rectangle clientArea = getPrinter().getClientArea();
            org.eclipse.swt.graphics.Rectangle trim = getPrinter().computeTrim(0, 0, 0, 0);
            org.eclipse.swt.graphics.Point printerDPI = getPrinter().getDPI();

            /*  getPrintRegion()
             *  Applicable Settings:
             *      Margins: ( all 4 )
             *      Scale: FIT_TO_ONE_PAGE OR FIT_TO_ONE_PAGE_HIGH OR FIT_TO_ONE_PAGE_WIDE
             *      Orientation
             */

            // apply Orientation to the margins
            PrintMargins printMargin = getPrintSettings().getMargins();

            PrintMargins orientedPrintMargins = new PrintMargins();

            if (isPortrait()) {
                // Portrait
                if (isPrinterPortrait()) {
                    // Use standard margins
                    orientedPrintMargins.setTop(new Double(printMargin.top));
                    orientedPrintMargins.setRight(new Double(printMargin.right));
                    orientedPrintMargins.setBottom(new Double(printMargin.bottom));
                    orientedPrintMargins.setLeft(new Double(printMargin.left));
                } else {
                    // Use Landscape margins
                    orientedPrintMargins.setTop(new Double(printMargin.right));
                    orientedPrintMargins.setRight(new Double(printMargin.bottom));
                    orientedPrintMargins.setBottom(new Double(printMargin.left));
                    orientedPrintMargins.setLeft(new Double(printMargin.top));
                }
            } else {
                if (isPrinterPortrait()) {
                    // Use standard margins
                    orientedPrintMargins.setTop(new Double(printMargin.right));
                    orientedPrintMargins.setRight(new Double(printMargin.bottom));
                    orientedPrintMargins.setBottom(new Double(printMargin.left));
                    orientedPrintMargins.setLeft(new Double(printMargin.top));
                } else {
                    // Use portrait margins
                    orientedPrintMargins.setTop(new Double(printMargin.top));
                    orientedPrintMargins.setRight(new Double(printMargin.right));
                    orientedPrintMargins.setBottom(new Double(printMargin.bottom));
                    orientedPrintMargins.setLeft(new Double(printMargin.left));
                }
            }

            printRegion = new Rectangle();

            // BML 8/9/05 - Removed the "Trim" from the equations. Client Area seemed sufficient
            printRegion.x = (int)Math.max((orientedPrintMargins.left * printerDPI.x), clientArea.x);
            printRegion.y = (int)Math.max((orientedPrintMargins.top * printerDPI.y), clientArea.y);
            printRegion.width = (int)((clientArea.x + clientArea.width) - printRegion.x - (orientedPrintMargins.right * printerDPI.x));
            printRegion.height = (int)((clientArea.y + clientArea.height) - printRegion.y - (orientedPrintMargins.bottom * printerDPI.y));

            if (debugMode) {
                System.out.println("\n[DiagramPrintingOperation.getPrintRegion()]  - Client Area = " + clientArea); //$NON-NLS-1$
                System.out.println("\n[DiagramPrintingOperation.getPrintRegion()]  - Trim Area   = " + trim); //$NON-NLS-1$
                System.out.println("\n[DiagramPrintingOperation.getPrintRegion()]  - Margins = " + orientedPrintMargins); //$NON-NLS-1$
                System.out.println("[DiagramPrintingOperation.getPrintRegion()]  - resulting printRegion: " + printRegion.toString()); //$NON-NLS-1$
                double wdth = (double)printRegion.width / (double)printerDPI.x;
                double hght = (double)printRegion.height / (double)printerDPI.x;
                System.out.println("[DiagramPrintingOperation.getPrintRegion()]  - Page Size (" + wdth + ", " + hght + ") inches "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

        }
        return printRegion;
    }

    /**
     * originally implemented in: PrintFigureOperation Returns the current print mode. The print mode is one of:
     * {@link #FIT_HEIGHT}, {@link #FIT_PAGE}, or {@link #FIT_WIDTH}.
     * 
     * @return the print mode
     */
    @Override
    protected int getPrintMode() {

        // do NOT lazily create because conditions may have changed

        // calc print mode from our prefs
        if (((Boolean)getPrintSettings().getSetting(PrintSettings.FIT_TO_ONE_PAGE)).booleanValue()) {
            iPrintMode = FIT_PAGE;
        } else if (((Boolean)getPrintSettings().getSetting(PrintSettings.FIT_TO_ONE_PAGE_WIDE)).booleanValue()) {
            iPrintMode = FIT_HEIGHT;
        } else if (((Boolean)getPrintSettings().getSetting(PrintSettings.FIT_TO_ONE_PAGE_HIGH)).booleanValue()) {
            iPrintMode = FIT_WIDTH;
        } else if (((Boolean)getPrintSettings().getSetting(PrintSettings.ADJUST_TO_PERCENT)).booleanValue()) {
            iPrintMode = SCALE;
        } else {
            iPrintMode = TILE;
        }
        return iPrintMode;
    }

    protected boolean okToPrint( int iPageNo ) {
        boolean bResult = false;

        int iScope = ((Integer)getPrintSettings().getSetting(PrintSettings.SCOPE)).intValue();

        switch (iScope) {
            case PrinterData.ALL_PAGES:
                bResult = true;
                break;
            case PrinterData.PAGE_RANGE:
                int iStartPage = ((Integer)getPrintSettings().getSetting(PrintSettings.START_PAGE)).intValue();
                int iEndPage = ((Integer)getPrintSettings().getSetting(PrintSettings.END_PAGE)).intValue();
                if ((iStartPage <= iPageNo) && (iPageNo <= iEndPage)) {
                    bResult = true;
                }
                break;
            default:
                break;
        }

        return bResult;
    }

    /**
     * Copied from: PrintFigureOperation Sets up Graphics object for the given IFigure.
     * 
     * @parampgPrinterGraphics The Graphics to setup
     * @param figure The IFigure used to setup graphics
     */

    protected void setupPrinterGraphicsFor( Graphics pgPrinterGraphics,
                                            IFigure figure,
                                            Rectangle bndsSourceImage ) {

        /*  setupPrinterGraphicsFor()
         *  Applicable Settings:
         *      Orientation: Portrait OR Landscape
         *      Scale: FIT_TO_ONE_PAGE_HIGH OR FIT_TO_ONE_PAGE_WIDE
         */
        double dpiScale = getPrinter().getDPI().x / Display.getCurrent().getDPI().x;
        if (debugMode) {
            System.out.println(" DPO.setupPrinterGraphics()  DIP Printer = " + getPrinter().getDPI().x); //$NON-NLS-1$
            System.out.println(" DPO.setupPrinterGraphics()  DIP Display = " + Display.getCurrent().getDPI().x); //$NON-NLS-1$
        }

        Rectangle printRegion = getPrintRegion();

        // put the print region in display coordinates
        printRegion.width /= dpiScale;
        printRegion.height /= dpiScale;
        if (debugMode) {
            System.out.println(" DPO.setupPrinterGraphics()  dpiScale        = " + dpiScale); //$NON-NLS-1$
            System.out.println(" DPO.setupPrinterGraphics()  bndsSourceImage = " + bndsSourceImage); //$NON-NLS-1$
            System.out.println(" DPO.setupPrinterGraphics()  printRegion     = " + printRegion); //$NON-NLS-1$
        }
        diagramSize = new Dimension(bndsSourceImage.getSize());

        double yScale = 0.0;
        double xScale = 0.0;

        if (isPortrait()) {
            xScale = (double)printRegion.width / bndsSourceImage.width;
            yScale = (double)printRegion.height / bndsSourceImage.height;
        } else {
            xScale = (double)printRegion.width / bndsSourceImage.width;
            yScale = (double)printRegion.height / bndsSourceImage.height;
        }
        if (debugMode) {
            System.out.println(" DPO.setupPrinterGraphics()  Scales:  X = " + xScale + "  Y = " + yScale); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // if( xScale > 1 || yScale > 1 )
        // xScale = yScale = 1.0;
        double finalScale = 1.0;

        switch (getPrintMode()) {
            case FIT_PAGE:
                if (isPortrait()) {
                    xScale = (double)printRegion.width / bndsSourceImage.width;
                    yScale = (double)printRegion.height / bndsSourceImage.height;
                    finalScale = 1.0 * dpiScale;

                    if (xScale > 1 && yScale > 1) finalScale = 1.0 * dpiScale;
                    else if (xScale < 1 && yScale < 1) {
                        finalScale = Math.min(xScale, yScale) * dpiScale;
                    } else {
                        if (xScale > 1) {
                            // Check to see if xScale/yScale < 1.0
                            if (yScale * xScale < 1.0) finalScale = 1.0 * dpiScale;
                            else finalScale = yScale * dpiScale;
                        } else {
                            finalScale = xScale * dpiScale;
                        }
                    }
                } else {
                    finalScale = 1.0 * dpiScale;

                    if (xScale > 1 && yScale > 1) finalScale = 1.0 * dpiScale;
                    else if (xScale < 1 && yScale < 1) {
                        finalScale = Math.min(xScale, yScale) * dpiScale;
                    } else {
                        if (xScale > 1) {
                            finalScale = yScale * dpiScale;
                        } else {
                            if (yScale * xScale > 1.0) finalScale = 1.0 * dpiScale;
                            else finalScale = xScale * dpiScale;
                        }
                    }
                }

                break;
            case FIT_WIDTH:
                if (isPortrait()) {
                    if (xScale > 1) {
                        finalScale = 1.0 * dpiScale;
                    } else {
                        finalScale = xScale * dpiScale;
                    }
                } else {
                    if (yScale > 1) {
                        finalScale = 1.0 * dpiScale;
                    } else {
                        finalScale = yScale * dpiScale;
                    }
                }

                break;
            case FIT_HEIGHT:
                if (isPortrait()) {
                    if (yScale > 1) {
                        finalScale = 1.0 * dpiScale;
                    } else {
                        finalScale = yScale * dpiScale;
                    }
                } else {
                    if (xScale > 1) {
                        finalScale = 1.0 * dpiScale;
                    } else {
                        finalScale = xScale * dpiScale;
                    }
                }
                break;
            case SCALE:
                int iScalePct = ((Integer)getPrintSettings().getSetting(PrintSettings.SCALING_PERCENTAGE)).intValue();
                double dScalePct = iScalePct;
                double dCentage = 0.01;
                dScalePct = (dScalePct * dCentage);

                // The SCALE calc only uses the one value it assumes to be the longest (yScale).
                double dScaleFactor = 1.0;
                finalScale = dScaleFactor * dpiScale * dScalePct;

                break;
            default:
                setGraphicsScale(pgPrinterGraphics, dpiScale);
        }
        printScaleFactor = finalScale;
        setGraphicsScale(pgPrinterGraphics, finalScale);
        pgPrinterGraphics.setForegroundColor(figure.getForegroundColor());
        pgPrinterGraphics.setBackgroundColor(figure.getBackgroundColor());
        pgPrinterGraphics.setFont(figure.getFont());

    }

    private void setGraphicsScale( Graphics g,
                                   double scale ) {
        if (debugMode) {
            System.out.println(" DPO.setGraphicsScale()  scale = " + scale); //$NON-NLS-1$
        }
        g.scale(scale);
    }

    @Override
    protected void printPages() {
        if (isPortrait()) {
            if (isPrinterPortrait()) printPortrait_PrinterIsPortrait();
            else printPortrait_PrinterIsLandscape();
        } else {
            if (isPrinterPortrait()) printLandscape_PrinterIsPortrait();
            else printLandscape_PrinterIsLandscape();
        }
    }

    protected void printPortrait_PrinterIsPortrait() {
        /*  printPages()
         *  Applicable Settings:
         *      Page Order: over then down OR down then over
         *      Scope: all pages OR page range; 
         *             if page range: startPage, endPage;
         *                 and use Page Order to figure out sequence
         *                 of pages so we can interpret "3 thru 7"
         */

        int iPageNo = 0;

        PrinterGraphics graphics = getFreshPrinterGraphics();

        if (debugMode) {
            System.out.println("[DPO.printPortraitPortrait] graphics Scale = " + graphics.getAbsoluteScale()); //$NON-NLS-1$
        }
        IFigure figure = getPrintSource();
        if (debugMode) {
            System.out.println("[DPO.printPortraitPortrait] figure.getBounds(): " + figure.getBounds()); //$NON-NLS-1$
        }
        Image sourceImage = getSourceImage(figure);

        org.eclipse.swt.graphics.Rectangle swtRectSource = sourceImage.getBounds();
        if (debugMode) {
            System.out.println("[DPO.printPortraitPortrait()] sourceImage dimensions: " + sourceImage); //$NON-NLS-1$
        }
        Rectangle bounds = new Rectangle(swtRectSource.x, swtRectSource.y, swtRectSource.width, swtRectSource.height);

        if (debugMode) {
            System.out.println("[DPO.printPortraitPortrait] sourceImage dimensions: " + swtRectSource); //$NON-NLS-1$
        }
        setupPrinterGraphicsFor(graphics, figure, bounds);

        int x = bounds.x;
        int y = bounds.y;
        Rectangle clipRect = new Rectangle();

        // note: the Page Order is implemented in the nested loops.
        // 'Over then Down' has an outer Y loop and an inner X loop
        // 'Down then Over' has an outer X loop and an inner Y loop

        // ==================
        // 'Over then Down'
        // ==================
        int iPagesPrinted = 0;

        if (((Boolean)getPrintSettings().getSetting(PrintSettings.OVER_THEN_DOWN)).booleanValue()) {

            while (y < bounds.y + bounds.height) {
                while (x < bounds.x + bounds.width) {
                    iPageNo++;

                    // Note: when we did the following 5 lines only for the pages we were going to print,
                    // we did not 'walk' the pages properly and hence did not print 'Page Ranges'
                    // correctly. For ex: printing 2-4 actually printed 1-3
                    graphics.pushState();
                    if (debugMode) {
                        System.out.println(" DPO.printPortraitPortrait():  Graphics Scale = " + graphics.getAbsoluteScale()); //$NON-NLS-1$
                    }
                    graphics.translate(-x, -y);
                    graphics.getClip(clipRect);
                    clipRect.setLocation(x, y);
                    graphics.clipRect(clipRect);

                    // Print the page, depending on page range rules
                    if (okToPrint(iPageNo)) {
                        // print this page
                        iPagesPrinted++;

                        // print this page
                        // Note: Instead of using 'figure.paint( graphics ) for Portrait, we are using the 'printImage()'
                        // method we had to develop for Landscape. This is because 'figure.paint()' does not
                        // capture the icons in a diagram (for example, the 'A's that decorate Attributes
                        // or the 'T' in a T-node diamond), but the techniques used in our 'getSourceImage()' method
                        // do capture them. The tradeoff is that the image printed by 'figure.graphics()' is
                        // beautifully anti-aliased (meaning SMOOTH), while the image printed by 'printImage()'
                        // is a little crude.
                        //                             
                        // figure.paint(graphics);
                        //                                          
                        printImage(graphics, sourceImage, x, y, clipRect);
                    } else {
                        //                        System.out.println("[DPO.printPagesInPortrait-OverThenDown] SKIPPING Page: " + iPageNo + " clipRect: " + clipRect  );   //$NON-NLS-1$
                    }
                    graphics.popState();
                    x += clipRect.width;
                }
                x = bounds.x;
                y += clipRect.height;
            }

        } else {
            // ==================
            // 'Down then Over'
            // ==================
            while (x < bounds.x + bounds.width) {

                while (y < bounds.y + bounds.height) {
                    iPageNo++;

                    graphics.pushState();
                    graphics.translate(-x, -y);
                    graphics.getClip(clipRect);
                    clipRect.setLocation(x, y);
                    graphics.clipRect(clipRect);

                    // Print the page, depending on page range rules
                    if (okToPrint(iPageNo)) {
                        // print this page
                        iPagesPrinted++;
                        //                        System.out.println("[DPO.printPagesInPortrait-DownThenOver] About to print Page: " + iPageNo + " clipRect: " + clipRect  );   //$NON-NLS-1$

                        // print this page
                        printImage(graphics, sourceImage, x, y, clipRect);
                    } else {
                        //                        System.out.println("[DPO.printPagesInPortrait-DownThenOver] SKIPPING Page: " + iPageNo + " clipRect: " + clipRect  );   //$NON-NLS-1$
                    }
                    graphics.popState();
                    y += clipRect.height;
                }
                y = bounds.y;
                x += clipRect.width;
            }
        }

        // Need to dispose of this RESOURCE!!!!!! free up memory and "handles"
        sourceImage.dispose();
    }

    protected void printPortrait_PrinterIsLandscape() {
        /*  printPages()
         *  Applicable Settings:
         *      Page Order: over then down OR down then over
         *      Scope: all pages OR page range; 
         *             if page range: startPage, endPage;
         *                 and use Page Order to figure out sequence
         *                 of pages so we can interpret "3 thru 7"
         */

        int iPageNo = 0;

        PrinterGraphics graphics = getFreshPrinterGraphics();

        if (debugMode) {
            System.out.println("[DPO.printPortraitLandscape] graphics Scale = " + graphics.getAbsoluteScale()); //$NON-NLS-1$
        }
        IFigure figure = getPrintSource();
        //        System.out.println("[DiagramPrintingOperation.printPages] figure.getBounds(): " + figure.getBounds() );   //$NON-NLS-1$

        Image originalImage = getSourceImage(figure);

        // For printerLandscape, we must rotate the image
        Image sourceImage = rotateImageLeft(originalImage);

        org.eclipse.swt.graphics.Rectangle swtRectSource = sourceImage.getBounds();

        Rectangle bounds = new Rectangle(swtRectSource.x, swtRectSource.y, swtRectSource.width, swtRectSource.height);

        if (debugMode) {
            System.out.println("[DPO.printPortraitLandscape] sourceImage dimensions: " + swtRectSource); //$NON-NLS-1$
        }
        setupPrinterGraphicsFor(graphics, figure, bounds);

        int x = bounds.x;
        int y = bounds.y;
        Rectangle clipRect = new Rectangle();
        graphics.pushState();
        graphics.translate(-x, -y);
        graphics.getClip(clipRect);
        clipRect.setLocation(x, y);
        graphics.clipRect(clipRect);
        int clipWidth = clipRect.width;

        // note: the Page Order is implemented in the nested loops.
        // 'Over then Down' has an outer Y loop and an inner X loop
        // 'Down then Over' has an outer X loop and an inner Y loop

        // ==================
        // 'Over then Down'
        // ==================
        int iPagesPrinted = 0;

        if (((Boolean)getPrintSettings().getSetting(PrintSettings.OVER_THEN_DOWN)).booleanValue()) {
            // 
            x = bounds.x + bounds.width - clipWidth;
            y = 0;
            while (x + clipWidth > 0) {
                while (y < bounds.y + bounds.height) {
                    iPageNo++;

                    // Note: when we did the following 5 lines only for the pages we were going to print,
                    // we did not 'walk' the pages properly and hence did not print 'Page Ranges'
                    // correctly. For ex: printing 2-4 actually printed 1-3
                    graphics.pushState();
                    graphics.translate(-x, -y);
                    graphics.getClip(clipRect);
                    clipRect.setLocation(x, y);
                    graphics.clipRect(clipRect);

                    // Print the page, depending on page range rules
                    if (okToPrint(iPageNo)) {
                        // print this page
                        iPagesPrinted++;

                        printImage(graphics, sourceImage, x, y, clipRect);
                    } else {
                        //                        System.out.println("[DPO.printPagesInPortrait-OverThenDown] SKIPPING Page: " + iPageNo + " clipRect: " + clipRect  );   //$NON-NLS-1$
                    }
                    graphics.popState();
                    y += clipRect.height;
                }
                y = 0;
                x -= clipWidth;
            }

        } else {
            // ==================
            // 'Down then Over'
            // ==================
            x = bounds.x + bounds.width - clipWidth;
            y = 0;
            while (y < bounds.y + bounds.height) {
                while (x + clipWidth > 0) {
                    iPageNo++;

                    graphics.pushState();
                    graphics.translate(-x, -y);
                    graphics.getClip(clipRect);
                    clipRect.setLocation(x, y);
                    graphics.clipRect(clipRect);

                    // Print the page, depending on page range rules
                    if (okToPrint(iPageNo)) {
                        // print this page
                        iPagesPrinted++;
                        //                        System.out.println("[DPO.printPagesInPortrait-DownThenOver] About to print Page: " + iPageNo + " clipRect: " + clipRect  );   //$NON-NLS-1$

                        // print this page
                        printImage(graphics, sourceImage, x, y, clipRect);
                    } else {
                        //                        System.out.println("[DPO.printPagesInPortrait-DownThenOver] SKIPPING Page: " + iPageNo + " clipRect: " + clipRect  );   //$NON-NLS-1$
                    }
                    graphics.popState();
                    x -= clipWidth;
                }
                x = bounds.x + bounds.width - clipWidth;
                y += clipRect.height;
            }
        }

        // Need to dispose of this RESOURCE!!!!!! free up memory and "handles"
        sourceImage.dispose();
        if (originalImage != null && !originalImage.isDisposed()) originalImage.dispose();
    }

    protected void printLandscape_PrinterIsPortrait() {
        /*  
         *  Applicable Settings:
         *      Page Order: over then down OR down then over
         *      Scope: all pages OR page range; 
         *             if page range: startPage, endPage;
         *                 and use Page Order to figure out sequence
         *                 of pages so we can interpret "3 thru 7"
         *             
         *             
         *  Special LANDSCAPE note:
         *      To get to landscape we must adjust 2 things:
         *          1. Rotate the source image (90 degrees to the left (-90))
         *          2. Change the usage of 'y' in the algorithm that walks
         *             through the source image chopping out rectangles:
         *              Instead of growing from zero to the max y value,
         *              start at the value 'max y - height' and fall to zero.
         *          3. Reverse the application of the Page Order rules             
         *              The loops are similar to Portrait, except:
         *              -- Outer 'y', Inner 'x' goes with DOWN_THEN_OVER in Landscape;
         *                 (it went with OVER_THEN_DOWN in Portrait)
         *             
         */

        int iPageNo = 0;

        PrinterGraphics graphics = getFreshPrinterGraphics();
        if (debugMode) {
            System.out.println(" [DPO.printPagesInLandscape] graphics Scale = " + graphics.getAbsoluteScale()); //$NON-NLS-1$
        }
        IFigure figure = getPrintSource();
        Image startingImage = getSourceImage(figure);

        if (debugMode) {
            org.eclipse.swt.graphics.Rectangle swtRectSourceBeforeRotation = startingImage.getBounds();
            System.out.println("\n[DPO.printPagesInLandscape] sourceImage dimensions BEFORE rotation: " + swtRectSourceBeforeRotation); //$NON-NLS-1$
        }
        // For landscape, we must rotate the image
        Image sourceImage = rotateImageRight(startingImage);

        org.eclipse.swt.graphics.Rectangle swtRectSource = sourceImage.getBounds();

        //        System.out.println("[DPO.printPagesInLandscape] sourceImage dimensions AFTER rotation: " + swtRectSource );   //$NON-NLS-1$
        if (debugMode) {
            System.out.println("[DPO.printPagesInLandscape] sourceImage dimensions AFTER rotation: " + swtRectSource); //$NON-NLS-1$
            //            System.out.println("\n[DPO.DPO.printPagesInLandscape] sourceImage dimensions: " + swtRectSource );   //$NON-NLS-1$
        }
        Rectangle bounds = new Rectangle(swtRectSource.x, swtRectSource.y, swtRectSource.width, swtRectSource.height);

        setupPrinterGraphicsFor(graphics, figure, bounds);
        Rectangle clipRect = new Rectangle();

        // determine starting x
        int x = bounds.x;

        // determine starting y
        int y = bounds.y;
        graphics.pushState();
        graphics.translate(-x, -y);
        graphics.getClip(clipRect);
        clipRect.setLocation(x, y);
        graphics.clipRect(clipRect);
        graphics.popState();
        int iMaxY = bounds.y + bounds.height - clipRect.height;
        y = iMaxY;

        // note: the Page Order is implemented in the nested loops. (It is the reverse of Portrait.)
        // 'Down then Over' has an outer Y loop and an inner X loop
        // 'Over then Down' has an outer X loop and an inner Y loop

        // ==================
        // 'Down then Over'
        // ==================
        int iPagesPrinted = 0;

        if (((Boolean)getPrintSettings().getSetting(PrintSettings.DOWN_THEN_OVER)).booleanValue()) {

            // note: limit is 0 for landscape
            while (y + clipRect.height >= 0) {
                while (x < bounds.x + bounds.width) {
                    iPageNo++;

                    // Note: when we did the following 5 lines only for the pages we were going to print,
                    // we did not 'walk' the pages properly and hence did not print 'Page Ranges'
                    // correctly. For ex: printing 2-4 actually printed 1-3
                    graphics.pushState();
                    graphics.translate(-x, -y);
                    graphics.getClip(clipRect);
                    clipRect.setLocation(x, y);
                    graphics.clipRect(clipRect);

                    // Print the page, depending on page range rules
                    if (okToPrint(iPageNo)) {
                        // print this page
                        iPagesPrinted++;

                        // print this page
                        printImage(graphics, sourceImage, x, y, clipRect);

                    } else {
                        //                        System.out.println("[DPO.printPagesInLandscape-OverThenDown] SKIPPING Page: " + iPageNo + " clipRect: " + clipRect  );   //$NON-NLS-1$
                    }

                    graphics.popState();

                    x += clipRect.width;
                }

                // next x:
                x = bounds.x;

                // next y:
                y -= clipRect.height;
            }

        } else {
            // ==================
            // 'Over then Down'
            // ==================
            while (x < bounds.x + bounds.width) {

                // note: limit is 0 for landscape
                while (y + clipRect.height >= 0) {
                    iPageNo++;

                    graphics.pushState();
                    graphics.translate(-x, -y);
                    graphics.getClip(clipRect);
                    clipRect.setLocation(x, y);
                    graphics.clipRect(clipRect);

                    // Print the page, depending on page range rules
                    if (okToPrint(iPageNo)) {
                        // print this page
                        iPagesPrinted++;

                        // print this page
                        printImage(graphics, sourceImage, x, y, clipRect);

                    } else {
                        //                        System.out.println("[DPO.printPagesInLandscape-OverThenDown] SKIPPING Page: " + iPageNo + " clipRect: " + clipRect  );   //$NON-NLS-1$
                    }

                    graphics.popState();

                    // next y:
                    y -= clipRect.height;
                }
                y = iMaxY;
                x += clipRect.width;
            }
        }

        // Need to dispose of this RESOURCE!!!!!! free up memory and "handles"
        if (startingImage != null) startingImage.dispose();
        sourceImage.dispose();
    }

    protected void printLandscape_PrinterIsLandscape() {
        // This is the same case as portrait printer & portrait diagram image.
        printPortrait_PrinterIsPortrait();
    }

    protected Image getSourceImage( IFigure figure ) {

        Rectangle diagramSize = figure.getBounds();
        Display display = Display.getDefault();
        Image image = new Image(display, diagramSize.width, diagramSize.height);
        GC graphicContext = new GC(image);
        SWTGraphics sourceGraphics = new SWTGraphics(graphicContext);
        figure.paint(sourceGraphics);

        return image;
    }

    protected void printImage( PrinterGraphics graphics,
                               Image sourceImage,
                               int xOffset,
                               int yOffset,
                               Rectangle clipRect ) {

        getPrinter().startPage();

        // note: we must trim the clipRect's w and h when necessary to ensure they will not exceed
        // the w and h of the source image
        int iSourceWidth = clipRect.width;
        int iSourceHeight = clipRect.height;

        int iDestX = clipRect.x;
        if (iDestX < 0) iDestX = 0;
        int iDestY = clipRect.y;
        if (iDestY < 0) iDestY = 0;
        int iDestWidth = clipRect.width;
        int iDestHeight = clipRect.height;

        int iOverage = 0;
        int iNewWidth = 0;
        int iNewHeight = 0;

        //        System.out.println("\n\n[DPO.printImage - clipRect at start: " + clipRect );    //$NON-NLS-1$

        // ====================================================
        // trim width when rectangle exceeds the RIGHT edge of the image
        // ====================================================
        // if the x offset plus the clip's width exceeds the src images width, trim it back
        if ((xOffset + clipRect.width) > sourceImage.getBounds().width) {
            iOverage = (xOffset + clipRect.width) - sourceImage.getBounds().width;
            //            System.out.println("[DPO.printImage - About to TRIM WIDTH; iOverage: " + iOverage );    //$NON-NLS-1$
            iNewWidth = clipRect.width - iOverage;

            if (iNewWidth == 0) {
                //                System.out.println("[DPO.printImage - iNewWidth is ZEROOOOOOOOO!!!!!!!! " );    //$NON-NLS-1$
            }
            iSourceWidth = iNewWidth;
            iDestWidth = iNewWidth;
        }

        if (xOffset < 0) {
            iNewWidth = (xOffset + clipRect.width);
            xOffset = 0;
            iSourceWidth = iNewWidth;
            iDestWidth = iNewWidth;
        }

        // ====================================================
        // trim height when rectangle precedes the TOP of the image
        // if the y offset is negative, recalc the height and trim y forward to zero
        // ====================================================
        if (yOffset < 0) {

            /* add y and height.  Since y is negative, this will find the portion of the
             *  height that overlaps the image.  
             *      For example,                       
             *          height = 100
             *          yOffset = -75
             *          -75 + 100 = 25...the piece of the image left to print is 25 high.                   
             */

            iNewHeight = (yOffset + clipRect.height);
            yOffset = 0;

            // must also recalc clipRect.y to get the partial rectangle to print
            // nearest the start (left) of the page
            /*
             * Problem: In Landscape, the last partial page of a row is right-justified on
             *   the page, when it would be more natural to have it left-justified.
             *   
             * Unfortunately, we are not able to control how things print in the printregion.
             *   (I tried everything.)
             * 
             * Barry suggested that we could get it if we rotated the other direction (+90??),
             *   and then print from the top...worth a try as a defect fix later.  
             * 
             */

            iSourceHeight = iNewHeight;
            iDestHeight = iNewHeight;
        }

        // ====================================================
        // trim height when rectangle exceeds the BOTTOM of the image
        // if the y offset plus the clip's height exceeds the src images height, trim it back
        // ====================================================
        if ((yOffset + clipRect.height) > sourceImage.getBounds().height) {
            iOverage = (yOffset + clipRect.height) - sourceImage.getBounds().height;
            //            System.out.println("[DPO.printImage - About to TRIM HEIGHT; iOverage: " + iOverage );    //$NON-NLS-1$
            iNewHeight = clipRect.height - iOverage;

            iSourceHeight = iNewHeight;
            iDestHeight = iNewHeight;
        }

        if (debugMode) {
            System.out.println("\n[DPO.printImage - About to call drawImage] ::       xOffset: " + xOffset); //$NON-NLS-1$
            System.out.println("[DPO.printImage - About to call drawImage] ::       yOffset: " + yOffset); //$NON-NLS-1$
            System.out.println("[DPO.printImage - About to call drawImage] ::     Clip Rect: " + clipRect); //$NON-NLS-1$
            System.out.println("[DPO.printImage - About to call drawImage] ::  iSourceWidth: " + iSourceWidth); //$NON-NLS-1$
            System.out.println("[DPO.printImage - About to call drawImage] :: iSourceHeight: " + iSourceHeight); //$NON-NLS-1$
            System.out.println("[DPO.printImage - About to call drawImage] ::        iDestX: " + iDestX); //$NON-NLS-1$
            System.out.println("[DPO.printImage - About to call drawImage] ::        iDestY: " + iDestY); //$NON-NLS-1$
            System.out.println("[DPO.printImage - About to call drawImage] ::    iDestWidth: " + iDestWidth); //$NON-NLS-1$
            System.out.println("[DPO.printImage - About to call drawImage] ::   iDestHeight: " + iDestHeight); //$NON-NLS-1$
        }
        // Draw the image
        graphics.drawImage(sourceImage, xOffset, yOffset, iSourceWidth, iSourceHeight, iDestX, iDestY, iDestWidth, iDestHeight);

        getPrinter().endPage();
    }

    protected Image rotateImageRight( Image img ) {

        ImageData srcImageData = img.getImageData();

        PaletteData srcPal = srcImageData.palette;
        PaletteData destPal;
        Image destImage;
        ImageData destImageData;

        /* construct a new ImageData */
        if (srcPal.isDirect) {
            destPal = new PaletteData(srcPal.redMask, srcPal.greenMask, srcPal.blueMask);
        } else {
            destPal = new PaletteData(srcPal.getRGBs());
        }

        destImageData = new ImageData(srcImageData.height, srcImageData.width, srcImageData.depth, destPal);

        /* rotate by rearranging the pixels */
        for (int i = 0; i < srcImageData.width; i++) {
            for (int j = 0; j < srcImageData.height; j++) {
                int pixel = srcImageData.getPixel(i, j);
                destImageData.setPixel(j, srcImageData.width - 1 - i, pixel);
            }
        }

        destImage = new Image(Display.getDefault(), destImageData);

        return destImage;
    }

    protected Image rotateImageLeft( Image img ) {

        ImageData srcImageData = img.getImageData();

        PaletteData srcPal = srcImageData.palette;
        PaletteData destPal;
        Image destImage;
        ImageData destImageData;

        /* construct a new ImageData */
        if (srcPal.isDirect) {
            destPal = new PaletteData(srcPal.redMask, srcPal.greenMask, srcPal.blueMask);
        } else {
            destPal = new PaletteData(srcPal.getRGBs());
        }

        destImageData = new ImageData(srcImageData.height, srcImageData.width, srcImageData.depth, destPal);

        /* rotate by rearranging the pixels */
        for (int i = 0; i < srcImageData.width; i++) {
            for (int j = 0; j < srcImageData.height; j++) {
                int pixel = srcImageData.getPixel(i, j);
                destImageData.setPixel(srcImageData.height - 1 - j, i, pixel);
            }
        }

        destImage = new Image(Display.getDefault(), destImageData);

        return destImage;
    }

    public PrintSettings getPrintSettings() {
        if (psSettings == null) {
            psSettings = new PrintSettings(this.getPrinter().getPrinterData());
            //            System.out.println("PRINT SETTINGS ==========> " + psSettings.toString() );  //$NON-NLS-1$
        }
        return psSettings;
    }

    /**
     * @return Returns the diagramSize.
     * @since 4.3
     */
    public Dimension getDiagramSize() {
        // if( !isPortrait())
        // return new Dimension(diagramSize.height, diagramSize.width);

        return this.diagramSize;
    }

    /**
     * @return Returns the printScaleFactor.
     * @since 4.3
     */
    public double getPrintScaleFactor() {
        return this.printScaleFactor;
    }

}
