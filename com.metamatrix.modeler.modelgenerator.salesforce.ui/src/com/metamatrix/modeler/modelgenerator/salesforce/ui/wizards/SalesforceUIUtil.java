/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.metamatrix.core.util.FileUtils;

public class SalesforceUIUtil implements FileUtils.Constants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Collection of Model file extensions.
     */
    public static String[] MODEL_FILE_EXTENSIONS = new String[] {"xmi"}; //$NON-NLS-1$
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////

	public static ImageDescriptor getImageDescriptor(String new_model_banner) {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * Indicates if the specified extensions is a valid file extension. 
     * @param theExtension the extension being checked
     * @return <code>true</code>if valid; <code>false</code> otherwise.
     * @since 4.2
     */
    private static boolean isFileExtensionOfType(String theExtension, String[] extensionTypes) {
        boolean result = false;
        
        if ((theExtension != null) && (theExtension.length() > 0)) {
            for (int i = 0; i < extensionTypes.length; i++) {
                if (theExtension.equalsIgnoreCase(extensionTypes[i])) {
                    result = true;
                    break;
                }
            }
        }
        
        return result;
    }
        
    /**
     * Indicates if the specified file system file is a Model. 
     * @param theFile the file being checked
     * @return <code>true</code>if a XMI file; <code>false</code> otherwise.
     * @since 4.2
     */
	public static boolean isModelFile(IFile theFile) {
        boolean result = false;
        String name = theFile.getName();
        int index = name.lastIndexOf(FileUtils.Constants.FILE_EXTENSION_SEPARATOR);
        
        if ((index != -1) && ((index + 2) < name.length())) {
            result = isFileExtensionOfType(name.substring(index + 1), MODEL_FILE_EXTENSIONS);
        }
        
        return result;
	}
	
	/**
     * Creates an extension which can be used in a {@link org.eclipse.swt.widgets.FileDialog}. Prefixes the
     * specified extension with the file name wildcard and the extension separator character.
     * @param theExtension the extension being used
     * @since 4.2
     */
    public static String createFileDialogExtension(String theExtension) {
        return new StringBuffer().append(FILE_NAME_WILDCARD)
                                 .append(FILE_EXTENSION_SEPARATOR_CHAR)
                                 .append(theExtension)
                                 .toString();
    }

	public static Image getImage(String service_icon) {
		// TODO Auto-generated method stub
		return null;
	}

}
