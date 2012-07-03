/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.ui.wizards.wsdl;

import org.teiid.core.util.FileUtils;

import com.metamatrix.modeler.webservice.WebServicePlugin;

/**
 *
 */
public abstract class WsdlFileExtensions implements FileUtils.Constants {

    /**
     * WSDL extensions prefixed with the file wildcard and extension separator. Suitable for use in the
     * {@link org.eclipse.swt.widgets.FileDialog}.
     * 
     * @since 4.2
     */
    public static final String[] FILE_DIALOG_WSDL_EXTENSIONS;
    
    static {
        // create WSDL file dialog extension array
        FILE_DIALOG_WSDL_EXTENSIONS = new String[WebServicePlugin.WSDL_FILE_EXTENSIONS.length];

        int i = 0;
        for (; i < WebServicePlugin.WSDL_FILE_EXTENSIONS.length; i++) {
            FILE_DIALOG_WSDL_EXTENSIONS[i] = new StringBuilder(FILE_NAME_WILDCARD).append(FILE_EXTENSION_SEPARATOR_CHAR).append(WebServicePlugin.WSDL_FILE_EXTENSIONS[i]).toString();
        }
    }
}
