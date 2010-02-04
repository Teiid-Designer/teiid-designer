/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.dialogs;

import java.io.File;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.ui.UiConstants;


/** 
 * @since 4.3  
 */
public class FileUiUtils implements UiConstants {

    public static FileUiUtils INSTANCE = new FileUiUtils();
    
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix( FileUiUtils.class );

    private static final String CONFIRM_OVERWRITE_TITLE_KEY   = "confirmOverwrite.title"; //$NON-NLS-1$
    private static final String CONFIRM_OVERWRITE_MESSAGE_KEY = "confirmOverwrite.message"; //$NON-NLS-1$
    
        
    private static String getString( final String id ) {
        return Util.getString( I18N_PREFIX + id );  
    }
    
    /**
     * Launch dialog to get user's agreement to overwrite a file 
     * @param someExistingFile The file to be saved
     * @param directoryFile The directory in which to save it
     * @return The user's decision: 0 = Yes; 1 = No; 2 = Cancel
     */
    public int confirmOverwrite( File someExistingFile, File directoryFile ) {
        
        // construct the full path
        IPath existingDirFile = new Path(directoryFile.getAbsolutePath());
        IPath outputFilePath = existingDirFile.append(someExistingFile.getName());
        
        return confirmOverwrite( outputFilePath );
    }
    
    
        
    /**
     * Launch dialog to get user's agreement to overwrite a file 
     * @param path The path to the file to be saved
     * @return The user's decision: 0 = Yes; 1 = No; 2 = Cancel
     */
    public int confirmOverwrite( IPath path ) {
        
       
        File outputFile = path.toFile();
        
        // take no action if file does not exist
        if ( !outputFile.exists() ) return 0;
        
        // construct the dialog
        String sTitle = getString( CONFIRM_OVERWRITE_TITLE_KEY );
        String sMessage = Util.getString( I18N_PREFIX + CONFIRM_OVERWRITE_MESSAGE_KEY, outputFile );
        
        Shell shell = Display.getCurrent().getActiveShell();
        final MessageDialog dialog 
            = new MessageDialog( shell, 
                                 sTitle, 
                                 null, 
                                 sMessage, 
                                 MessageDialog.QUESTION,
                                 new String[] {
                                     IDialogConstants.YES_LABEL,
                                     IDialogConstants.NO_LABEL,
                                     IDialogConstants.CANCEL_LABEL}, 
                                 0 );
        
        final int[] result = new int[1];
        
        // launch the dialog
        shell.getDisplay().syncExec( new Runnable() {
            
            public void run() {
                result[ 0 ] = dialog.open();
            }
        });
        
        return result[ 0 ];
    }
    
    /**
     * Obtains the file name of an existing file whose name is the same as the specified input regardless of case.
     * Leading and trailing spaces are stripped from the input. 
     * @param theFullPathName the file name being checked
     * @return the file name of an existing file having the same name but different case; otherwise the input parameter.
     * @throws com.metamatrix.core.util.AssertionError if input paramater is <code>null</code> or empty
     * @since 5.0.1
     */
    public String getExistingCaseVariantFileName(String theFullPathName) {
        String result = theFullPathName;
        
        if (result != null) {
            result = theFullPathName.trim();
        }

        Assertion.assertTrue(!StringUtil.isEmpty(result));

        File file = new File(result);
        
        // file.exists() returns true even if case is different
        if (file.exists()) {
            String name = file.getName();
            File parentDir = file.getParentFile();
            
            if (parentDir == null) {
                File tempFile = file.getAbsoluteFile();
                parentDir = tempFile.getParentFile();
            }
            
            if (parentDir != null) {
                File[] kids = parentDir.listFiles();
    
                // Walk the parent directory looking for files that do not have the EXACT name,
                // but do have the same name with one or more letters of a different case.
                for (int i = 0; i < kids.length; ++i) {
                    String existingName = kids[i].getName();
    
                    if (existingName.equalsIgnoreCase(name)) {
                        result = kids[i].getAbsolutePath();
                        break;
                    }
                }
            }
        }        
        
        return result;
    }

    
    /**
     * Search the directory of the specified file for files that have the exact same name, but with one or more letters of a
     * different case. For example, "myFile.txt" and "MyFile.txt". Return the filename of the case-variant file, if any, or return
     * null.
     * 
     * @param path
     *            The path to the file to be saved
     * @return The clashing file name or null
     */
    public String getExistingCaseVariantFileName( IPath path) {
        return getExistingCaseVariantFileName(path.toOSString());
    }
    
    /**
     * Strip the extension, if any, from the filename.
     * @param sFileName the file name
     * @return The filename without its extension
     */
    public String stripExtension( String sFileName ) {
        String sResult = sFileName;
        
        String sWork = sFileName;
        int iIndexOfPeriod = sWork.indexOf( '.' );
        
        if ( iIndexOfPeriod != -1 ) {
            sResult = sWork.substring( 0, iIndexOfPeriod );
        }
        
        return sResult;
    }


    
}
