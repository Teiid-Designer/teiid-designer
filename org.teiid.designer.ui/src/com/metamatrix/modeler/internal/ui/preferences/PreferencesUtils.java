/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.ui.UiConstants;


/**
 * Utilities for working with Eclipse preferences.
 * @since 5.0
 */
public class PreferencesUtils implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public static final String ECLIPSE_PREFERENCE_FILE_EXTENSION = ".epf"; //$NON-NLS-1$
    
    public static final String[] PREFERENCE_DIALOG_FILTER_EXTENSIONS = new String[] {'*' + ECLIPSE_PREFERENCE_FILE_EXTENSION};
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * A filter appropriate for working with all preferences.
     * @since 5.0
     */
    private static final IPreferenceFilter FILTER = new IPreferenceFilter() {
        public String[] getScopes() {
            return new String[] {InstanceScope.SCOPE, ConfigurationScope.SCOPE};
        }
        public Map getMapping(String theScope) {
            return null;
        }
    };
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Exports all preferences to the specified location on the file system. If the file already exists
     * and overwriting is not specified no work occurs.
     * @param thePath the file path
     * @param theOverwriteFlag the flag indicating if an already existing file should be overwritten
     * @throws CoreException if problem working with the preference service
     * @throws FileNotFoundException if problem with path
     */
    public static void exportAll(String thePath,
                                 boolean theOverwriteFlag) throws CoreException,
                                                                  FileNotFoundException {
        //
        // The following code started from code copied from
        // org.eclipse.ui.internal.wizards.preferences.WizardPreferencesExportPage1
        //

        File exportFile = new File(thePath);
        
        if (!exportFile.exists() || (exportFile.exists() && theOverwriteFlag)) {
            FileOutputStream fos = null;
            
            try {
                fos = new FileOutputStream(thePath);
                IPreferencesService service = Platform.getPreferencesService();
                service.exportPreferences(service.getRootNode(), new IPreferenceFilter[] {FILTER}, fos);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException theException) {
                        Util.log(IStatus.ERROR, theException, theException.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * Imports all preferences contained in the the specified preference file locate on the file system.
     * @param thePath the file path
     * @throws CoreException if problem working with the preference service
     * @throws FileNotFoundException if problem with path
     */
    public static void importAll(String thePath) throws CoreException,
                                                        FileNotFoundException {
        //
        // The following code started from code copied from
        // org.eclipse.ui.internal.wizards.preferences.WizardPreferencesImportPage1
        //
        
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(thePath);
            IPreferencesService service = Platform.getPreferencesService();
            IExportedPreferences prefs = service.readPreferences(fis);
            service.applyPreferences(prefs, new IPreferenceFilter[] {FILTER});
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException theException) {
                    Util.log(IStatus.ERROR, theException, theException.getMessage());
                }
            }
        }
    }
    
    /**
     * Makes sure the specified path ends with the appropriate file extension. If an incorrect extension
     * is used it is replaced with the correct one.
     * @param thePath the path whose path is being normalized
     * @return the normalized path
     * @since 5.0
     * @see #ECLIPSE_PREFERENCE_FILE_EXTENSION
     */
    public static String ensurePathExtension(String thePath) {
        String result = thePath;
        String ext = FileUtils.getExtension(thePath);
        
        if (CoreStringUtil.isEmpty(ext)) {
            result = new StringBuffer(thePath).append(ECLIPSE_PREFERENCE_FILE_EXTENSION).toString();
        } else if (!ext.equals(ECLIPSE_PREFERENCE_FILE_EXTENSION)) {
            result = ensurePathExtension(FileUtils.getFilenameWithoutExtension(thePath));
        }
        
        return result;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Don't allow construction.
     */
    private PreferencesUtils() {}

}
