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
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * A label and icon widget for displaying an IStatus.
 */
public class MessageLabel extends CLabel {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    
    private Color fNormalMsgAreaBackground;
    private boolean hideMessageIfOk = true;

    /**
     * Creates a new message line as a child of the given parent.
     */
    public MessageLabel(Composite parent) {
        this(parent, SWT.LEFT);
    }

    /**
     * Creates a new message line as a child of the parent and with the given SWT stylebits.
     */
    public MessageLabel(Composite parent, int style) {
        super(parent, style);
        fNormalMsgAreaBackground= null;
    }

    
    private Image findImage(IStatus status) {
        if (status.isOK()) {
            return null;
        } else if (status.matches(IStatus.ERROR)) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
        } else if (status.matches(IStatus.WARNING)) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
        } else if (status.matches(IStatus.INFO)) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
        }
        return null;
    }

    /**
     * Sets the message and image to the given status.
     * <code>null</code> is a valid argument and will set the empty text and no image
     */
    public void setErrorStatus(IStatus status) {
        if (status != null) {
            String message;
            if ( hideMessageIfOk && status.isOK() ) { 
                message = EMPTY_STRING;
            } else {
                message = status.getMessage();
            }
            
            if (message == null) {
                message = EMPTY_STRING;
            }
            setText(message);
            setImage(findImage(status));
            setBackground(JFaceColors.getErrorBackground(getDisplay()));
            this.pack();
            this.redraw();
        } else {     
            setText(EMPTY_STRING);   
            setImage(null);
            setBackground(fNormalMsgAreaBackground);    
        }
    }
    
    public void setOkMessageHidden(boolean flag) {
        this.hideMessageIfOk = flag;
    }
    
}

