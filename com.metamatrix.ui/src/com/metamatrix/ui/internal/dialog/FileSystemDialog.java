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

package com.metamatrix.ui.internal.dialog;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.internal.viewsupport.FileSystemContentProvider;
import com.metamatrix.ui.internal.viewsupport.FileSystemLabelProvider;
import com.metamatrix.ui.internal.viewsupport.NonDirectoryFileViewerFilter;


/**
 * A {@link org.eclipse.jface.dialogs.Dialog} suitable for choosing one or more {@link java.io.File}
 * objects from the file system.
 * @since 4.2
 */
public class FileSystemDialog extends ElementTreeSelectionDialog
                              implements UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(FileSystemDialog.class);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public FileSystemDialog(Shell theParent) {
        this(theParent, new FileSystemLabelProvider(), new FileSystemContentProvider());
    }

	protected FileSystemDialog(Shell theParent,
	                           ILabelProvider theLabelProvider,
	                           ITreeContentProvider theContentProvider) {
  		super(theParent, theLabelProvider, theContentProvider);
  		setTitle(Util.getString(PREFIX + "title")); //$NON-NLS-1$
  		setMessage(Util.getString(PREFIX + "msg")); //$NON-NLS-1$
  		
  		if (theContentProvider instanceof FileSystemContentProvider) {
  		    setInput(this);
  		}
	}
	
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets this <code>Dialog</code> to only show {@link java.io.File} objects that are directories. 
     * @since 4.2
     */
	public void setOnlyShowFolders() {
	    addFilter(new NonDirectoryFileViewerFilter());
	}
	
}
