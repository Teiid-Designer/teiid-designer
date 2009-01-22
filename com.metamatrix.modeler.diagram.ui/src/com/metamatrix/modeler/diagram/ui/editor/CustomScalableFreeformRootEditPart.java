/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import org.eclipse.draw2d.LayeredPane;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;


/** 
 * @since 4.3
 */
public class CustomScalableFreeformRootEditPart extends ScalableFreeformRootEditPart {

    protected PageBoundaryGridLayer layerPageGrid;
    
    
    /** 
     * 
     * @since 4.3
     */
    public CustomScalableFreeformRootEditPart() {
        super();
    }
    

    /**
     * @see FreeformGraphicalRootEditPart#createLayers(LayeredPane)
     */
    @Override
    protected void createLayers(LayeredPane layeredPane) {
        
        super.createLayers( layeredPane );
        
        layeredPane.add( getPageGridLayer(), GRID_LAYER);        
    }
    
    public PageBoundaryGridLayer getPageGridLayer() {
        if ( layerPageGrid == null ) {
            layerPageGrid = new PageBoundaryGridLayer();            
        }
        
        return layerPageGrid;
    }
    
    
//    // ????? MOVED THIS TO DiagramViewer
//    public void updateForPreferences( DiagramViewer viewer ) {
//        // i hope this works...
//        PrinterData data = new PrinterData("xdx", "yyy" );
//
//        DiagramPrintingAnalyzer analyzer = new DiagramPrintingAnalyzer( new Printer(data), viewer );
//        
//        switch ( analyzer.getPrintMode() ) {
//            case DiagramPrintingOperation.FIT_PAGE:
////                graphics.scale(Math.min(xScale, yScale) * dpiScale);                
//                viewer.getBounds().width;
//                break;
//            case DiagramPrintingOperation.FIT_WIDTH:
////                graphics.scale(xScale * dpiScale);
//                break;
//            case DiagramPrintingOperation.FIT_HEIGHT:
////                graphics.scale(yScale * dpiScale);
//                break;
//            case DiagramPrintingOperation.SCALE:
////                graphics.scale(yScale * dpiScale * dScalePct );
//                break;
//            default:
////                graphics.scale(dpiScale);
//        }
//
//
//    }



}
