/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.printing;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.printing.PrinterData;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.preferences.DiagramPrintPreferencePage;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;


public class PrintSettings implements
                          DiagramUiConstants,
                          PluginConstants {


    // Types stored in Preferences
    public static final int ORIENTATION_PORTRAIT = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;
    public static final int SCALE_FIT_TO_ONE_PAGE = 1;
    public static final int SCALE_FIT_TO_ONE_PAGE_HIGH = 2;
    public static final int SCALE_FIT_TO_ONE_PAGE_WIDE = 3;
    public static final int SCALE_ADJUST_TO_PERCENT = 0;
    
    public static final String PORTRAIT = "portrait"; //$NON-NLS-1$
    public static final String LANDSCAPE = "landscape"; //$NON-NLS-1$
    public static final String FIT_TO_ONE_PAGE = "fitToOnePage"; //$NON-NLS-1$
    public static final String FIT_TO_PAGE = "Fit to One Page"; //$NON-NLS-1$
    public static final String FIT_TO_ONE_PAGE_HIGH = "fitToOnePageHigh"; //$NON-NLS-1$
    public static final String FIT_TO_HEIGHT = "Fit to One Page High"; //$NON-NLS-1$
    public static final String FIT_TO_ONE_PAGE_WIDE = "fitToOnePageWide"; //$NON-NLS-1$
    public static final String FIT_TO_WIDTH = "Fit to One Page Wide"; //$NON-NLS-1$
    public static final String ADJUST_TO_PERCENT = "adjustToPercent"; //$NON-NLS-1$
    public static final String SCALING_PERCENTAGE = "scalingPercentage"; //$NON-NLS-1$
    public static final String TOP_MARGIN = "top"; //$NON-NLS-1$
    public static final String RIGHT_MARGIN = "right"; //$NON-NLS-1$
    public static final String BOTTOM_MARGIN = "bottom"; //$NON-NLS-1$
    public static final String LEFT_MARGIN = "left"; //$NON-NLS-1$
    public static final String OVER_THEN_DOWN = "overThenDown"; //$NON-NLS-1$
    public static final String DOWN_THEN_OVER = "downThenOver"; //$NON-NLS-1$

    // Types stored in PrinterData
    public static final String SCOPE = "scope"; //$NON-NLS-1$
    public static final String PRINT_ALL_PAGES = "Print All Pages"; //$NON-NLS-1$
    public static final String PRINT_RANGE_OF_PAGES = "Print Range of Pages"; //$NON-NLS-1$
    public static final String PRINT_SELECTED_PAGES = "Print Selected Pages"; //$NON-NLS-1$
    public static final String START_PAGE = "startPage"; //$NON-NLS-1$
    public static final String END_PAGE = "endPage"; //$NON-NLS-1$
    public static final String PRINT_TO_FILE = "printToFile"; //$NON-NLS-1$
    public static final String FILE_NAME = "fileName"; //$NON-NLS-1$
    
    // to interpret Scope
    public static final int SCOPE_ALL_PAGES = PrinterData.ALL_PAGES;
    public static final int SCOPE_PAGE_RANGE = PrinterData.PAGE_RANGE;
    
    private int orientation = ORIENTATION_PORTRAIT;
    private boolean isPortrait = true;
    private int scaleOption = SCALE_ADJUST_TO_PERCENT;
    private int scalePercent = 100;
    private boolean isOverThenDown = true;
    private int pageScope = PrinterData.ALL_PAGES;
    private boolean printAllPages = true;
    private boolean printRange = false;
    private boolean printSelection = false;
    private int firstPage = 0;
    private int lastPage = 0;
    private boolean printToFile = false;
    private String fileName = null;
    
    PrinterData pdPrintData;
    IPreferenceStore preferenceStore;
    PrintMargins margins;
    
    
    public PrintSettings( PrinterData pdPrintData ) {
        super();
        this.pdPrintData = pdPrintData;
        
        init();
    }

    public PrinterData getPrinterData() {
        return pdPrintData;
    }
    
    @Override
    public String toString() {
        String s = "";  //$NON-NLS-1$
        
        s += "\n             PORTRAIT: " + getSetting( PORTRAIT ).toString(); //$NON-NLS-1$
        s += "\n            LANDSCAPE: " + getSetting( LANDSCAPE ).toString();  //$NON-NLS-1$
        s += "\n      FIT_TO_ONE_PAGE: " + getSetting( FIT_TO_ONE_PAGE ).toString(); //$NON-NLS-1$
        s += "\n FIT_TO_ONE_PAGE_HIGH: " + getSetting( FIT_TO_ONE_PAGE_HIGH ).toString();  //$NON-NLS-1$
        s += "\n FIT_TO_ONE_PAGE_WIDE: " + getSetting( FIT_TO_ONE_PAGE_WIDE ).toString();  //$NON-NLS-1$
        s += "\n    ADJUST_TO_PERCENT: " + getSetting( ADJUST_TO_PERCENT ).toString();  //$NON-NLS-1$
        s += "\n   SCALING_PERCENTAGE: " + getSetting( SCALING_PERCENTAGE ).toString();  //$NON-NLS-1$
        s += "\n           TOP_MARGIN: " + getSetting( TOP_MARGIN ).toString();  //$NON-NLS-1$
        s += "\n         RIGHT_MARGIN: " + getSetting( RIGHT_MARGIN ).toString();  //$NON-NLS-1$
        s += "\n        BOTTOM_MARGIN: " + getSetting( BOTTOM_MARGIN ).toString();  //$NON-NLS-1$
        s += "\n          LEFT_MARGIN: " + getSetting( LEFT_MARGIN ).toString();  //$NON-NLS-1$
        s += "\n       OVER_THEN_DOWN: " + getSetting( OVER_THEN_DOWN ).toString();  //$NON-NLS-1$
        s += "\n       DOWN_THEN_OVER: " + getSetting( DOWN_THEN_OVER ).toString();  //$NON-NLS-1$
        s += "\n                SCOPE: " + getSetting( SCOPE ).toString();  //$NON-NLS-1$
        s += "\n           START_PAGE: " + getSetting( START_PAGE ).toString();  //$NON-NLS-1$
        s += "\n             END_PAGE: " + getSetting( END_PAGE ).toString();  //$NON-NLS-1$
        s += "\n        PRINT_TO_FILE: " + getSetting( PRINT_TO_FILE ).toString();  //$NON-NLS-1$
        s += "\n            FILE_NAME: " + getSetting( FILE_NAME ).toString();  //$NON-NLS-1$
        
        return s;
    }
    
    public void init() {
        // establish the Print Preferences
        loadSettings();
    }
    
    public void loadSettings() {
        IPreferenceStore store = getPreferenceStore();
        // ================================================
        //  1. Orientation   
        // ================================================
        
        isPortrait = store.getBoolean( Prefs.Print.PORTRAIT );
        
        // ================================================
        //  2. Margins   
        // ================================================
        
        scaleOption = SCALE_ADJUST_TO_PERCENT;
        if( store.getBoolean( Prefs.Print.FIT_TO_ONE_PAGE ) )
            scaleOption = SCALE_FIT_TO_ONE_PAGE;
        else if(store.getBoolean( Prefs.Print.FIT_TO_ONE_PAGE_HIGH ) )
            scaleOption = SCALE_FIT_TO_ONE_PAGE_HIGH;
        else if(store.getBoolean( Prefs.Print.FIT_TO_ONE_PAGE_WIDE ) )
            scaleOption = SCALE_FIT_TO_ONE_PAGE_WIDE;

        scalePercent = store.getInt( Prefs.Print.SCALING_PERCENTAGE );
        
        // ================================================
        //  3. Margins   
        // ================================================

        if( margins == null )
            margins = new PrintMargins();
        
        margins.top     = store.getDouble( Prefs.Print.TOP_MARGIN );
        margins.bottom  = store.getDouble( Prefs.Print.BOTTOM_MARGIN );
        margins.left    = store.getDouble( Prefs.Print.LEFT_MARGIN );
        margins.right   = store.getDouble( Prefs.Print.RIGHT_MARGIN );
        
        // ================================================
        //  4. Page Order
        // ================================================
        isOverThenDown = store.getBoolean( Prefs.Print.OVER_THEN_DOWN );
        
        // ================================================
        //  5. Scope
        // ================================================
        pageScope = pdPrintData.scope;
        if( pageScope == PrinterData.ALL_PAGES) {
            printAllPages = true;
            printSelection = false;
            printRange = false;
        } else if( pageScope == PrinterData.PAGE_RANGE) {
            printAllPages = false;
            printSelection = false;
            printRange = true;
        } else {
            printAllPages = false;
            printSelection = true;
            printRange = false;
        }

        firstPage = pdPrintData.startPage;
        lastPage = pdPrintData.endPage;
        printToFile = pdPrintData.printToFile;
        fileName = pdPrintData.fileName;
    }

    private IPreferenceStore getPreferenceStore() {
        if ( preferenceStore == null ) {
            DiagramPrintPreferencePage printPrefPage
                = new DiagramPrintPreferencePage();
            preferenceStore = printPrefPage.getPreferenceStore();
        }
        
        return preferenceStore;
    }
    
    public Object getSetting( String sType ) {
        IPreferenceStore store = getPreferenceStore();

        // ================================================
        //  1. Orientation
        // ================================================
        if ( sType.equals( PORTRAIT ) ) {
            return new Boolean( store.getBoolean( Prefs.Print.PORTRAIT ) );
        }
        else
        if ( sType.equals( LANDSCAPE ) ) {
            return new Boolean( store.getBoolean( Prefs.Print.LANDSCAPE ) );
        }
        else
        // ================================================
        //  2. Scaling
        // ================================================
        if ( sType.equals( FIT_TO_ONE_PAGE ) ) {
            return new Boolean( store.getBoolean( Prefs.Print.FIT_TO_ONE_PAGE ) );
        }
        else
        if ( sType.equals( FIT_TO_ONE_PAGE_WIDE ) ) {
            return new Boolean( store.getBoolean( Prefs.Print.FIT_TO_ONE_PAGE_WIDE ) );
        }
        else
        if ( sType.equals( FIT_TO_ONE_PAGE_HIGH ) ) {
            return new Boolean( store.getBoolean( Prefs.Print.FIT_TO_ONE_PAGE_HIGH ) );
        }
        else
        if ( sType.equals( ADJUST_TO_PERCENT ) ) {
            return new Boolean( store.getBoolean( Prefs.Print.ADJUST_TO_PERCENT ) );
        }
        else
        if ( sType.equals( SCALING_PERCENTAGE ) ) {
            return new Integer( store.getInt( Prefs.Print.SCALING_PERCENTAGE ) );
        }
        else
        // ================================================
        //  3. Margins   
        // ================================================
        if ( sType.equals( TOP_MARGIN ) ) {
            return new Double( store.getDouble( Prefs.Print.TOP_MARGIN ) );
        }
        else
        if ( sType.equals( RIGHT_MARGIN ) ) {
            return  new Double( store.getDouble( Prefs.Print.RIGHT_MARGIN ) );
        }
        else
        if ( sType.equals( BOTTOM_MARGIN ) ) {
            return  new Double( store.getDouble( Prefs.Print.BOTTOM_MARGIN ) );
        }
        else
        if ( sType.equals( LEFT_MARGIN ) ) {
            return  new Double( store.getDouble( Prefs.Print.LEFT_MARGIN ) );
        }
        else
        // ================================================
        //  4. Page Order
        // ================================================
        if ( sType.equals( OVER_THEN_DOWN ) ) {
            return new Boolean( store.getBoolean( Prefs.Print.OVER_THEN_DOWN ) );
        }
        else
        if ( sType.equals( DOWN_THEN_OVER ) ) {
            return new Boolean( store.getBoolean( Prefs.Print.DOWN_THEN_OVER ) );
        }
        // ================================================
        //  5. Scope
        // ================================================
        if ( sType.equals( SCOPE ) ) {
            return new Integer( pdPrintData.scope );
        }
        else
        if ( sType.equals( START_PAGE ) ) {
            return new Integer( pdPrintData.startPage );
        }
        else
        if ( sType.equals( END_PAGE ) ) {
            return new Integer( pdPrintData.endPage );            
        }
        else
        // ================================================
        //  6. Print to File
        // ================================================
        if ( sType.equals( PRINT_TO_FILE ) ) {
            return new Boolean( pdPrintData.printToFile );
        }                
        else
        if ( sType.equals( FILE_NAME ) ) {
            if ( pdPrintData.fileName != null ) {
                return new String( pdPrintData.fileName ); 
            }
            return "";    //$NON-NLS-1$
            
        }
        else
        // ================================================
        //  7. Unknown
        // ================================================
        {
            // unknown setting
            return null;
        }
        
    }

    
    public void setSetting( String sType, Object oValue ) {
        /*
         * this method will only allow the PrinterData settings to be changed.
         * to change the other settings you must use the Diagram Print Preference Page.
         * 
         * jhTODO: we might make this method complete later, so that PrintSettings
         * could better serve as a 'Business Object'.
         */

        // ================================================
        //  5. Scope
        // ================================================
        if ( sType.equals( SCOPE ) ) {
            pdPrintData.scope = ((Integer)oValue).intValue();
        }
        else
        if ( sType.equals( START_PAGE ) ) {
            pdPrintData.startPage = ((Integer)oValue).intValue();
        }
        else
        if ( sType.equals( END_PAGE ) ) {
            pdPrintData.endPage = ((Integer)oValue).intValue();
        }
        else
        // ================================================
        //  6. Print to File
        // ================================================
        if ( sType.equals( PRINT_TO_FILE ) ) {
            pdPrintData.printToFile = ((Boolean)oValue).booleanValue();
        }                
        else
        if ( sType.equals( FILE_NAME ) ) {
            pdPrintData.fileName = ((String)oValue).toString();
        }        
    }

    public String getPageScopeString() {
        if( pageScope == PrinterData.ALL_PAGES )
            return PRINT_ALL_PAGES;
        else if( pageScope == PrinterData.PAGE_RANGE )
            return PRINT_RANGE_OF_PAGES;
        else
            return PRINT_SELECTED_PAGES;
    }
    
    public String getScaleOptionString() {
        if( scaleOption == SCALE_FIT_TO_ONE_PAGE )
            return FIT_TO_PAGE;
        else if( scaleOption == SCALE_FIT_TO_ONE_PAGE_HIGH )
            return FIT_TO_HEIGHT;
        else if( scaleOption == SCALE_FIT_TO_ONE_PAGE_WIDE)
            return FIT_TO_WIDTH;
        else
            return ADJUST_TO_PERCENT;
    }
    
    public PrintMargins getMargins() {
        
        if ( margins == null ) {
            margins = new PrintMargins();
            
            margins.setTop( (Double)getSetting( TOP_MARGIN ) );
            margins.setRight( (Double)getSetting( RIGHT_MARGIN ) );
            margins.setBottom( (Double)getSetting( BOTTOM_MARGIN ) );
            margins.setLeft( (Double)getSetting( LEFT_MARGIN ) );
        }

        return margins;
    }

    /** 
     * @return Returns the isPortrait.
     * @since 4.3
     */
    public boolean isPortrait() {
        return this.isPortrait;
    }

    /** 
     * @return Returns the orientation.
     * @since 4.3
     */
    public int getOrientation() {
        return this.orientation;
    }

    /** 
     * @return Returns the scaleOption.
     * @since 4.3
     */
    public int getScaleOption() {
        return this.scaleOption;
    }

    /** 
     * @return Returns the scalePercent.
     * @since 4.3
     */
    public int getScalePercent() {
        return this.scalePercent;
    }

    /** 
     * @return Returns the isOverThenDown.
     * @since 4.3
     */
    public boolean isOverThenDown() {
        return this.isOverThenDown;
    }
    
    /** 
     * @return Returns the printAllPages.
     * @since 4.3
     */
    public boolean printAllPages() {
        return this.printAllPages;
    }

    /** 
     * @return Returns the printRange.
     * @since 4.3
     */
    public boolean printRangeOfPages() {
        return this.printRange;
    }

    /** 
     * @return Returns the printSelection.
     * @since 4.3
     */
    public boolean printSelectedPages() {
        return this.printSelection;
    }

    /** 
     * @return Returns the firstPage.
     * @since 4.3
     */
    public int getFirstPage() {
        return this.firstPage;
    }

    /** 
     * @return Returns the lastPage.
     * @since 4.3
     */
    public int getLastPage() {
        return this.lastPage;
    }

    /** 
     * @return Returns the fileName.
     * @since 4.3
     */
    public String getFileName() {
        return this.fileName;
    }

    /** 
     * @return Returns the printToFile.
     * @since 4.3
     */
    public boolean isPrintToFile() {
        return this.printToFile;
    }


}
