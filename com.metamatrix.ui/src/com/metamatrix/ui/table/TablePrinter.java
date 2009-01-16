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

package com.metamatrix.ui.table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.metamatrix.ui.internal.InternalUiConstants;

/**
 * TablePrinter
 * (jh)
 */
public class TablePrinter {

    private Table table;
    private GC gc;
    private FontData fontData;
    private int iAverageCharWidth;
    private int iColumnCharFudgeFactor;                    
    private int iLineHeight;
    private Rectangle clientArea;
    private int iTableRowMargin;

    private TableColumn[] columns;
    private TableItem[] items;
    
    // each entry in this array is an Arraylist containing a ColumnData for
    //  each contiguous column that will fit on page in one pass.
    private ArrayList[] columnsPerSectionArray;

    private int x;
    private int y;  

    // for file save default delimiter to comma
    private static final String TAB_DELIM_FILEEXT = ".txt";  //$NON-NLS-1$
    private static final String COMMA_DELIM_FILEEXT = ".csv";  //$NON-NLS-1$
    private String DEFAULT_FILEEXT = COMMA_DELIM_FILEEXT;
    
    private static final String TAB_CHOOSER_FILEEXT_PATTERN = "*.txt";  //$NON-NLS-1$
    private static final String COMMA_CHOOSER_FILEEXT_PATTERN = "*.csv";  //$NON-NLS-1$

    private static final char NEWLINE = '\n';  
    private static final char TAB = '\t';  
    private static final char COMMA = ',';  
    private static final char PERIOD = '.';  
    
    private char chDelim = COMMA;
    
    
    // display strings from i18n.properties
    private static final String PRINT_JOB_STATUS   
        = InternalUiConstants.Util.getString("TablePrinter.jobStatus"); //$NON-NLS-1$
    private static final String FILE_CHOOSER_DIALOG_TITLE   
        = InternalUiConstants.Util.getString("TablePrinter.fileChooserDialog.title"); //$NON-NLS-1$
    private static final String DEFAULT_FILENAME   
        = InternalUiConstants.Util.getString("TablePrinter.defaultFileName.text"); //$NON-NLS-1$
    private static final String CSV_EXTENSION_DESC   
        = InternalUiConstants.Util.getString("TablePrinter.csvExtensionDescription.text"); //$NON-NLS-1$
    private static final String TAB_EXTENSION_DESC   
        = InternalUiConstants.Util.getString("TablePrinter.tabExtensionDescription.text"); //$NON-NLS-1$

    /**
     * Construct an instance of TablePrinter.
     * 
     */
    public TablePrinter() {
        super();
    }
    
    
    public void printTable( Table table ) {
        
        this.table = table;
        
        PrintDialog dialog = new PrintDialog( getShell() );
        PrinterData data = dialog.open();

        if ( data != null ) {
            
            if ( data.printToFile ) {
                printToFile( table );                            
            } else {
                printToPaper( table, data );
            }
        }        
    }

    private Shell getShell() {
        return table.getShell();
    }
            
    private void printToPaper( Table table, PrinterData data ) {
        /*
         * Strategy: Write only as many column headings as will fit in the page width,
         *            then write all rows for just those columns,
         *            then write the next set of columns,
         *            then write all rows for that set of columns,
         *            etc., etc.,...
         * 
         */
            

        Printer printer = new Printer( data );
         
        if ( printer.startJob( PRINT_JOB_STATUS ) ) { 
             
            // get a graphics context to write on
            gc = new GC( printer );
            
            // apply the font info from the table to the graphics context
            fontData = table.getFont().getFontData()[0];
            
            // create fonts to use later, based on the table font
            Font fntStandard 
                = new Font( printer, fontData.getName(), fontData.getHeight(), fontData.getStyle() );
            Font fntBold 
                = new Font( printer, fontData.getName(), fontData.getHeight(), SWT.BOLD );
                                
            // capture the average character width and other useful constants 
            iAverageCharWidth = gc.getFontMetrics().getAverageCharWidth();
            iColumnCharFudgeFactor = 3;                    
            iLineHeight = fontData.getHeight() * 4;
            iTableRowMargin = iAverageCharWidth * 24; 
                
            // get the area to write on                        
            clientArea = printer.getClientArea();
                        
            // get the column headings row and the data rows
            columns = table.getColumns();
            items = table.getItems();

            // init x and y                
            x = clientArea.x;
            y = clientArea.y;

            // create the data structure that will drive the process.  A 'section' is a set of
            //  contiguous columns that will fit in the page width.
            createColumnsPerSectionArray();
            
            
            // write to the gc, driven by the 'columns per section' array
            for ( int iSection = 0; iSection < columnsPerSectionArray.length; iSection++ ) {
                
                // get the Section ArrayList
                ArrayList aryl = columnsPerSectionArray[ iSection ];
                
                // The array of ArrayLists may have some empty entries; when we get to one
                //  we are done
                if ( aryl == null ) {
                    break;
                }
                
                // ================
                // HEADINGS
                // ================
                // set font bold for the headings
                gc.setFont( fntBold );
                
                // write the col headings
                for ( int iEntry = 0; iEntry < aryl.size(); iEntry++ ) {
                    
                    // get the next ColumnData
                    ColumnData cdColData = (ColumnData)aryl.get( iEntry );
                    
                    // write the column heading
                    gc.drawText( columns[ cdColData.iIndex ].getText(), x, y );

                    // move x forward
                    x += cdColData.iWidth;                    
                }

                // skip a line
                y += iLineHeight * 3;

                // reset x
                x = clientArea.x;

                // ================
                // ROWS
                // ================
                // return font to standard
                gc.setFont( fntStandard );
                
                // write the rows
                for (int row = 0; row < items.length; row++) {
                    for ( int iEntry2 = 0; iEntry2 < aryl.size(); iEntry2++ ) {

                        // get the column data  
                        ColumnData cdColData = (ColumnData)aryl.get( iEntry2 );
                        
                        // write the column data (a table cell)
                        gc.drawText( items[row].getText( cdColData.iIndex ), x, y );

                        // move x forward
                        x += cdColData.iWidth;                    
                    }
 
                    // reset x
                    x = clientArea.x;

                    // move y forward
                    y += iLineHeight * 3;    
                }
                
                // end of section...
                // reset x
                x = clientArea.x;

                // skip 2 lines
                y += iLineHeight * 3;
                y += iLineHeight * 3;

            }          
            
            // end
            printer.endJob();
        }
    }
    
    
    private int calcColumnWidth( int iColumn ) {
        
        // init to width of the column header
        int iGreatestColWidth = columns[ iColumn ].getText().length();
        String sText = "";   //$NON-NLS-1$        
        
        // examine the width of the cells in this column, looking for largest one
        for( int iRow = 0; iRow < items.length; iRow++ ) {
            sText = items[ iRow ].getText( iColumn );
            if ( sText.length() > iGreatestColWidth ) {
                iGreatestColWidth = sText.length();
            }
        }

        // convert to pixel count width
        iGreatestColWidth = iAverageCharWidth * ( iGreatestColWidth + iColumnCharFudgeFactor );

        return iGreatestColWidth;   
    }
    
    private ArrayList[] createColumnsPerSectionArray() {
        
        int iCurrentSection = 0;
        int iCurrentX = 0;
                 
        // create an array of ArrayLists
        columnsPerSectionArray = new ArrayList[ columns.length ];
        
        // add first ArrayList to it
        columnsPerSectionArray[ iCurrentSection ] = new ArrayList();
                
        // walk the columns
        for (int iColIndex = 0; iColIndex < columns.length; iColIndex++) {
            
            // calc max width of this col 
            int iColWidth = calcColumnWidth( iColIndex );
            
            
            // calc proposed new x value (after adding the new col)
            int iNewX = iCurrentX + iColWidth;
            
            // test to see if new col will fit and take approproiate action
            
            if ( iNewX > ( clientArea.width - iTableRowMargin ) ) {
                // this col will NOT fit, so end this section and move on
                iCurrentSection++;
                columnsPerSectionArray[ iCurrentSection ] = new ArrayList();
                columnsPerSectionArray[ iCurrentSection ].add( new ColumnData( iColIndex, iColWidth ) );
                iCurrentX = 0;
                
            } else {
                // this one will fit; add it to current section and increment the current X
                columnsPerSectionArray[ iCurrentSection ].add( new ColumnData( iColIndex, iColWidth ) );
                iCurrentX += iColWidth;                                
            }
        }
                                      
        return columnsPerSectionArray;
    }


    /*
     * 
     */
    public void printToFile( Table table /*, IFile file*/ ) {
        
        columns = table.getColumns();
        items = table.getItems();
        char chDelim = getDelimiter();

        StringBuffer sbText = new StringBuffer();
        
        FileWriter fwFileWriter     = null;
        BufferedWriter bwOutWriter  = null;
        PrintWriter pwPrintWriter   = null;
        
        
        
        try {        

            // get the target file and path from user; this will also tell us what delim to use        
            String sFileSpec = getTargetPath();
            
            // quit now if user quit the dialog
            if ( sFileSpec == null ) { return; }

            // otherwise continue...       
            if ( sFileSpec.endsWith( COMMA_DELIM_FILEEXT ) ) {
                chDelim = COMMA;
            } else {
                chDelim = TAB;                
            }
                                    
            // construct the header row
            for (int col = 0; col < columns.length; col++) {
                        
                // if not the first col, add delim after the prev col
                if ( col > 0 ) {                
                    sbText.append( chDelim );
                }               
                    
                // add the next col                                                
//                System.out.println( "[TablePrinter.printToFile] about to add: " + columns[col].getText() );
                sbText.append( columns[col].getText() );                        
            }
            sbText.append( NEWLINE );
            
            
            // construct and write out the remaining rows
            for ( int row = 0; row < items.length; row++ ) {
                    
                for ( int col = 0; col < columns.length; col++ ) {
                    
                    // if not the first col, add delim after the prev col
                    if ( col > 0 ) {                
                        sbText.append( chDelim );
                    }               
                    
                    // add the next col                                                
//                    System.out.println( "[TablePrinter.printToFile] about to add: " + items[row].getText(col) );
                    sbText.append( items[row].getText(col) );                        
                }
    
                sbText.append( NEWLINE );                
            }
            
            // save the file                        
//            System.out.println( "[TablePrinter.printToFile] about to create FileWriter with path: " + sFileSpec ); //$NON-NLS-1$

            fwFileWriter = new FileWriter( sFileSpec );
            
            bwOutWriter = new BufferedWriter( fwFileWriter );
            pwPrintWriter = new PrintWriter( bwOutWriter );
            pwPrintWriter.write( sbText.toString() );


        } catch ( IOException ioe ) {
            InternalUiConstants.Util.log( ioe );                
        } finally{
            
            if ( pwPrintWriter != null ) {
                pwPrintWriter.close();
            }
           
            try{
                if ( bwOutWriter != null ) {
                    bwOutWriter.close();
                }
            } catch(java.io.IOException e){}
            
            try{
                if ( fwFileWriter != null ) {
                    fwFileWriter.close();
               }
            } catch(java.io.IOException e){}
        }
    }
    
    public void setDelimiter( char chDelim ) {
        this.chDelim = chDelim;         

        if ( this.chDelim == COMMA ) {
            DEFAULT_FILEEXT = COMMA_DELIM_FILEEXT;
        }
        if ( this.chDelim == TAB ) {
            DEFAULT_FILEEXT = TAB_DELIM_FILEEXT;
        }
    }

    public char getDelimiter() {
        return chDelim;
    }

   /**
    * Use file chooser dialog to let user choose a filename and path in which to save the file
    */
    public String getTargetPath() {
        
        FileDialog dlg = new FileDialog( getShell(), SWT.SAVE | SWT.SINGLE );
        dlg.setFilterExtensions( new String[]{ COMMA_CHOOSER_FILEEXT_PATTERN, 
                                               TAB_CHOOSER_FILEEXT_PATTERN } );  
        dlg.setFilterNames( new String[]{ CSV_EXTENSION_DESC, 
                                         TAB_EXTENSION_DESC } );
        dlg.setText( FILE_CHOOSER_DIALOG_TITLE );
        dlg.setFileName( DEFAULT_FILENAME + DEFAULT_FILEEXT );  
         
        // present the dialog 
        String sPath = dlg.open();
        
        // If there is no file extension, add the default file extension
        if( sPath != null && sPath.indexOf( PERIOD ) == -1 ) {
            sPath += DEFAULT_FILEEXT; 
        }
        
        return sPath;       
    }
    
    // =========================================
    // Inner class: ColumnData
    // =========================================
 
     class ColumnData {
         
         public int iIndex;
         public int iWidth;
        
         public ColumnData( int iIndex, int iWidth ) {
            this.iIndex = iIndex;
            this.iWidth = iWidth;
         }

     }        
}
