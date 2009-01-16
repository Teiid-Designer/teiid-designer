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

package com.metamatrix.ui.internal.widget;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * The <code>StatusLabel</code> widget is a wrapping label with an icon.
 */
public class StatusLabel extends Composite  {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final int DEFAULT_IMAGE_WIDTH = 16;
    
    private static final int DEFAULT_ROWS = 2;
    
    /** Error image suitable for a label provider. */
    private static final Image ERROR_IMAGE;
    
    /** Info image suitable for a label provider. */
    private static final Image INFO_IMAGE;
           
    /** Warning image suitable for a label provider. */
    private static final Image WARNING_IMAGE;
  
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    static {
        ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
        ERROR_IMAGE = sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
        INFO_IMAGE = sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
        WARNING_IMAGE = sharedImages.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private int rows = DEFAULT_ROWS;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROLS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private Label lblImage;
    
    private Label lblText;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @param theParent
     * @since 4.1
     */
    public StatusLabel(final Composite theParent) {
        super(theParent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.horizontalSpacing = 3;
        setLayout(layout);
        
        this.lblImage = new Label(this, SWT.NONE);
        GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        gd.widthHint = DEFAULT_IMAGE_WIDTH; // why is this needed to get the image to show ???
        this.lblImage.setLayoutData(gd);

        this.lblText = new Label(this, SWT.WRAP);
        this.lblText.setLayoutData(new GridData(GridData.FILL_BOTH));
        setRows(DEFAULT_ROWS);
    }
    
    /**
     * @see org.eclipse.swt.widgets.Composite#setLayout(org.eclipse.swt.widgets.Layout)
     */
    @Override
    public void setLayout(Layout theLayout) {
        if (theLayout instanceof GridLayout) {
            super.setLayout(theLayout);
        }
    }
    
    /**
     * @see org.eclipse.swt.widgets.Control#setLayoutData(java.lang.Object)
     */
    @Override
    public void setLayoutData(Object theLayoutData) {
        if (theLayoutData instanceof GridData) {
            super.setLayoutData(theLayoutData);
        }
    }

    /** 
     * @return
     * @since 4.1
     */
    public Image getImage() {
        return this.lblImage.getImage();
    }
    
    /** 
     * @return
     * @since 4.1
     */
    public int getRows() {
        return this.rows;
    }
    
    /** 
     * @return
     * @since 4.1
     */
    public String getText() {
        return this.lblText.getText();
    }
    
    /** 
     * @param theImage
     * @since 4.1
     */
    public void setImage(Image theImage) {
        this.lblImage.setImage(theImage);
    }
    
    /** 
     * @param theRows
     * @since 4.1
     */
    public void setRows(int theRows) {
        if (theRows > 0) {
            GridData gd = (GridData)this.lblText.getLayoutData();
            gd.heightHint = theRows * this.lblText.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        }
    }
    
    /** 
     * @param theText
     * @since 4.1
     */
    public void setText(String theText) {
        this.lblText.setText(theText);
    }
    
    /** 
     * @param theText
     * @since 4.1
     */
    public void setText(String theText, int severity) {
        if(severity == IStatus.ERROR) {
            setImage(ERROR_IMAGE);
        }else if(severity == IStatus.WARNING) {
            setImage(WARNING_IMAGE);
        }else if(severity == IStatus.INFO) {
            setImage(INFO_IMAGE);
        }
        this.lblText.setText(theText);
    }

}
