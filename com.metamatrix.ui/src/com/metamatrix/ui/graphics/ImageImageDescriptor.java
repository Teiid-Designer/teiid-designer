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

package com.metamatrix.ui.graphics;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 * The <code>ExistingImageDescriptor</code> is a descriptor for an existing <code>Image</code>.
 * Most code copied from org.eclipse.jdt.internal.ui.viewsupport.ImageImageDescriptor. Since it is an internal
 * package it was not used.
 */
public class ImageImageDescriptor extends ImageDescriptor {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** The image being used. */
    private Image image;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Construct an <code>ImageImageDescriptor</code>.
     * @param theImage the image to use in the descriptor
     */
    public ImageImageDescriptor(Image theImage) {
        image = theImage;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object theObj) {
        return (theObj != null) &&
               getClass().equals(theObj.getClass()) &&
               image.equals(((ImageImageDescriptor)theObj).image);
    }
    
    /**
     * Gets the <code>Image</code> used by this descriptor.
     * @return the image
     */
    public Image getImage() {
        return image;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.resource.ImageDescriptor#getImageData()
     */
    @Override
    public ImageData getImageData() {
        return image.getImageData();
    }

    /* (non-Javadoc)
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return image.hashCode();
    }

}
