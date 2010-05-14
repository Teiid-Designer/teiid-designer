/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import com.metamatrix.modeler.internal.xsd.ui.PluginConstants;



/**
 * This class provides information regarding the structure and
 * content of specified file system XSD File objects.
 * This class was basically copied from FileSystemStructureProvider and adjusted to filter all files except XSD file extensions
 */

public class XsdFileSystemStructureProvider implements IImportStructureProvider {

    /**
     * Holds a singleton instance of this class.
     */
    public final static XsdFileSystemStructureProvider INSTANCE = new XsdFileSystemStructureProvider();

    /**
     * Creates an instance of <code>FileSystemStructureProvider</code>.
     */
    private XsdFileSystemStructureProvider() {
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
        if( ((String)element).indexOf(PluginConstants.XSD_EXTENSION) != -1 )
            return true;
        
        return false;
    }
}
