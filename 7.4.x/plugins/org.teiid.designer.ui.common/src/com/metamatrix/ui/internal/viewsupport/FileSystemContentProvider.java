/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.viewsupport;

import java.io.File;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A <code>ITreeContentProvider</code> for the file system.
 * @since 4.2
 */
public class FileSystemContentProvider implements ITreeContentProvider {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final Object[] NO_CHILDREN = new Object[0];
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @since 4.2
     */
    public void dispose() {
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 4.2
     */
    public Object[] getChildren(Object theParent) {
        Object[] result = null;
        
        if (theParent instanceof File) {
           result = ((File)theParent).listFiles();
        }
        
        return ((result == null) ? NO_CHILDREN : result);
    }

    /** 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    public Object[] getElements(Object theInput) {
        return File.listRoots();
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     * @since 4.2
     */
    public Object getParent(Object theElement) {
        return ((theElement instanceof File) ? ((File)theElement).getParentFile() : null);
    }

    /** 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @since 4.2
     */
    public boolean hasChildren(Object theElement) {
        Object[] kids = getChildren(theElement);
        return ((kids != null) && (kids.length > 0));
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void inputChanged(Viewer theViewer,
                             Object theOldInput,
                             Object theNewInput) {
    }

}
