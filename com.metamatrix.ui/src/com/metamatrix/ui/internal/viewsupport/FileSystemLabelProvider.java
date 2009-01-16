/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
