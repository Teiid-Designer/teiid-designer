/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.viewsupport;

import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * A {@link org.eclipse.jface.viewers.ILabelProvider} suitable for displaying {@link java.io.File} objects.
 * @since 4.2
 */
public class FileSystemLabelProvider extends LabelProvider {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final Image IMG_FILE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);

    private static final Image IMG_FOLDER = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private boolean useFullName = false;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Image getImage(Object theElement) {
        Image result = null;

        if (theElement instanceof File) {
            result = (((File)theElement).isDirectory() ? IMG_FOLDER : IMG_FILE);
        }

        return result;
    }
    
    
    /** 
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     * @since 4.2
     */
    @Override
    public String getText(Object theElement) {
        String result = null;

        if (theElement instanceof File) {
            File file = (File)theElement;
            
            if (this.useFullName) {
                result = file.getAbsolutePath();
            } else {
                result = file.getName();

                if (result.length() == 0) {
                    result = ((File)theElement).getAbsolutePath();
                }
            }

        }

        return ((result == null) ? super.getText(theElement) : result);
    }
    
    /**
     * Sets this <code>LabelProvider</code> to use or not use the <code>File</code> full name. 
     * @param theFullNameFlag indicates if the full name of the <code>File</code> should be used.
     * @since 4.2
     */
    public void setUseFullName(boolean theFullNameFlag) {
        this.useFullName = theFullNameFlag;
    }

}
