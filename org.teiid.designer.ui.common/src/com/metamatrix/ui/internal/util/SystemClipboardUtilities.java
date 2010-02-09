/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>SystemClipboardUtilities</code> class manages data on the system clipboard.
 */
public class SystemClipboardUtilities {


    public static final String COLUMN_DELIMITER = "\t"; //$NON-NLS-1$
    public static final String ROW_DELIMITER = "\n"; //$NON-NLS-1$

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** Don't allow construction. */
    private SystemClipboardUtilities() {}

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Clears the text contents of the system clipboard.
     */
    public static void clear() { // NO_UCD (Indicates this is ignored by unused code detection tool)
        clear(TextTransfer.getInstance());
    }
    
    /**
     * Clears the data associated with the specified type.
     * @param theTransferType the type being cleared
     */
    public static void clear(Transfer theTransferType) {
        Clipboard cb = getSystemClipboard();
        cb.setContents(new Object[] {null}, new Transfer[] {theTransferType});
    }
    
    /**
     * Converts the specified text (usually from the System Clipboard) representing a table into a list 
     * of lists where the outer lists are the rows and the internal lists are the columns. Lines are terminated 
     * by CRLF and columns are separated by tabs. 
     * @param theTableData the text being converted
     * @return a list of lists where the outer list represents rows and the inner list represents columns
     * @throws IllegalArgumentException if the input is <code>null</code>
     */
    public static List<List<String>> convertTableData(String theTableData) {
        ArgCheck.isNotNull(theTableData);

        ArrayList<List<String>> arylResult = new ArrayList<List<String>>();
        final String DELIMITER = "\r\n"; //$NON-NLS-1$
        boolean firstToken = true;

        StringTokenizer stOuter = new StringTokenizer(theTableData, DELIMITER, true);
        boolean prevTokenWasDelimiter = false;

        while (stOuter.hasMoreTokens()) {
            String token = stOuter.nextToken();

            // code should handle if "\r\n" or just "\n" is found
            if (token.equals("\r")) { //$NON-NLS-1$
                token = stOuter.nextToken();
            }
            
            if (token.equals("\n")) { //$NON-NLS-1$
                if (firstToken) {
                    // first value was delimiter so set first value to empty string
                    token = ""; //$NON-NLS-1$
                    prevTokenWasDelimiter = true;
                } else if (prevTokenWasDelimiter) {
                    // replace delimiter with empty string to signify blank input found for row
                    token = ""; //$NON-NLS-1$
                } else {
                    // don't process this since it just signifies end of row data
                    prevTokenWasDelimiter = true;
                    continue;
                }
            } else {
                prevTokenWasDelimiter = false;
            }

            if (firstToken) {
                firstToken = false;
            }

            List<String> lstLineArry = convertColumnData(token);
            arylResult.add(lstLineArry);
        }

        return arylResult;
    }

    /**
     * Converts the specified text (usually from the System Clipboard) representing a single table row into
     * a list. Data elements are separated by tabs.  Any empty column is filled with a zero length string.
     * @param theRowData the text being converted
     * @return the row data as a list
     * @throws IllegalArgumentException if the input is <code>null</code>
     */

    public static List<String> convertColumnData(String theRowData) {
        ArgCheck.isNotNull(theRowData);

        String sThisToken = ""; //$NON-NLS-1$
        String sLastToken = ""; //$NON-NLS-1$
        final String EMPTY_STRING = ""; //$NON-NLS-1$
        ArrayList<String> arylResult = new ArrayList<String>();

        StringTokenizer stInner = new StringTokenizer(theRowData, COLUMN_DELIMITER, true);

        if (stInner.hasMoreTokens()) {
            boolean firstToken = true;

            while (stInner.hasMoreTokens()) {
                sThisToken = stInner.nextToken();

                if (sThisToken.equals( COLUMN_DELIMITER ) ) {
                    if (firstToken) {
                        // first value was delimiter so set first value to empty string
                        arylResult.add(EMPTY_STRING);
                    } else if (sLastToken.equals( COLUMN_DELIMITER ) ) {
                        // if both this and last were delims,
                        //  we must supply the missing data between them
                        arylResult.add(EMPTY_STRING);
                    } else {
                        // if this is a delim, but the last was not a delim,
                        //  take no action.
                    }
                } else {
                    // if this token is NOT a delim, just add it to the array
                    arylResult.add(sThisToken);
                }
                sLastToken = sThisToken;

                if (firstToken) {
                    firstToken = false;
                }
            }

            if (sLastToken.equals( COLUMN_DELIMITER ) ) {
                arylResult.add(EMPTY_STRING);
            }
        } else {
            // no delimeters found so only one value
            arylResult.add(theRowData);
        }

        return arylResult;
    }

    /**
     * Obtains a <code>Clipboard</code> for the current thread.
     * @return the clipboard
     */
    public static Clipboard getSystemClipboard() {
        return new Clipboard(Display.getCurrent());
    }

    /**
     * Obtains the text contents of the system clipboard.
     * @return the text contents or <code>null</code>
     */
    public static String getContents() {
        return (String)getContents(TextTransfer.getInstance());
    }
    
    /**
     * Copies the <code>toString()</code> of each object in the selection, separated by a linefeed, to the clipboard.
     * If selection is <code>null</code> or empty nothing is copied.
     * @param theSelection the selection being copied to the clipboard
     * @since 4.2
     */
    public static void copyToClipboard(ISelection theSelection) {
        List objects = SelectionUtilities.getSelectedObjects(theSelection);
        
        if (!objects.isEmpty()) {
            StringBuffer text = new StringBuffer();
            
            for (int size = objects.size(), i = 0; i < size; i++) {
                text.append(objects.get(i));
                
                if (i < (size - 1)) {
                    text.append('\n');
                }
            }
            
            setContents(text.toString());
        }
    }
    
    /**
     * Obtains the contents of the system clipboard having the specified type.
     * @return the contents or <code>null</code>
     */
    public static Object getContents(Transfer theTransferType) {
        ArgCheck.isNotNull(theTransferType);
        return getSystemClipboard().getContents(theTransferType);
    }
    
    /**
     * Indicates if the clipboard does not contain any text contents.
     * @return <code>true</code> if empty; <code>false</code> otherwise.
     */
    public static boolean isEmpty() {
        return isEmpty(TextTransfer.getInstance());
    }
    
    /**
     * Indicates if the clipboard does not contain any contents of the specified type.
     * @return <code>true</code> if empty; <code>false</code> otherwise.
     */
    public static boolean isEmpty(Transfer theTransferType) {
        return (getContents(theTransferType) == null);
    }

    /**
     * Method to set the text contents of the system clipboard.
     * @param theContents the text content being saved to the clipboard
     */
    public static void setContents(String theContents) {
        setContents(theContents, TextTransfer.getInstance());
    }
    
    /**
     * Method to set the contents of the system clipboard.
     * @param theContent the content being saved to the clipboard
     * @param theTransferType the type of clipboard data being set
     * @throws IllegalArgumentException if the transfer parameter is <code>null</code>
     */
    public static void setContents(Object theContent,
                                   Transfer theTransferType) {
        ArgCheck.isNotNull(theTransferType);
        
        Clipboard cb = getSystemClipboard();
        cb.setContents(new Object[] {theContent}, new Transfer[] {theTransferType});
    }

}
