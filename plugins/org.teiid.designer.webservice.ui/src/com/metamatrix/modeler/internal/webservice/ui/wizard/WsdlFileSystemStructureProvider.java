/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;

import com.metamatrix.modeler.webservice.ui.IUiConstants;

/** 
 * @since 5.0
 */
public class WsdlFileSystemStructureProvider  implements IImportStructureProvider {

    /**
     * Holds a singleton instance of this class.
     */
    public final static WsdlFileSystemStructureProvider INSTANCE = new WsdlFileSystemStructureProvider();

    /**
     * Creates an instance of <code>FileSystemStructureProvider</code>.
     */
    private WsdlFileSystemStructureProvider() {
        super();
    }

    /* (non-Javadoc)
     * Method declared on IImportStructureProvider
     */
    public List getChildren(Object element) {
        File folder = (File) element;
        String[] children = folder.list();
        int childrenLength = children == null ? 0 : children.length;
        List result = new ArrayList(childrenLength);
        for (int i = 0; i < childrenLength; i++) {
            if( isXsdFile(children[i]) ) {
                result.add(new File(folder, children[i]));
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * Method declared on IImportStructureProvider
     */
    public InputStream getContents(Object element) {
        try {
            return new FileInputStream((File) element);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * Method declared on IImportStructureProvider
     */
    public String getFullPath(Object element) {
        return ((File) element).getPath();
    }

    /* (non-Javadoc)
     * Method declared on IImportStructureProvider
     */
    public String getLabel(Object element) {

        //Get the name - if it is empty then return the path as it is a file root
        File file = (File) element;
        String name = file.getName();
        if (name.length() == 0)
            return file.getPath();

        return name;
    }

    /* (non-Javadoc)
     * Method declared on IImportStructureProvider
     */
    public boolean isFolder(Object element) {
        return ((File) element).isDirectory();
    }
    
    /* (non-Javadoc)
     * Method declared on IImportStructureProvider
     */
    public boolean isXsdFile(Object element) {
        if( ((String)element).endsWith(IUiConstants.WSDL_FILE_EXTENSION))
            return true;
        
        return false;
    }
}
