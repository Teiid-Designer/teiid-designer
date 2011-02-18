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
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.ui.UiConstants;

/**
 * @since 4.3
 */
public class FileUiUtils implements UiConstants {

    public static FileUiUtils INSTANCE = new FileUiUtils();

    /**
     * Obtains the file name of an existing file whose name is the same as the specified input regardless of case. Leading and
     * trailing spaces are stripped from the input.
     * 
     * @param theFullPathName the file name being checked
     * @return the file name of an existing file having the same name but different case; otherwise the input parameter.
     * @throws com.metamatrix.core.util.AssertionError if input paramater is <code>null</code> or empty
     * @since 5.0.1
     */
    public String getExistingCaseVariantFileName( String theFullPathName ) {
        String result = theFullPathName;

        if (result != null) {
            result = theFullPathName.trim();
        }

        CoreArgCheck.isTrue(!CoreStringUtil.isEmpty(result), "The full path name cannot be empty"); //$NON-NLS-1$

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
     * @param path The path to the file to be saved
     * @return The clashing file name or null
     */
    public String getExistingCaseVariantFileName( IPath path ) {
        return getExistingCaseVariantFileName(path.toOSString());
    }

}
