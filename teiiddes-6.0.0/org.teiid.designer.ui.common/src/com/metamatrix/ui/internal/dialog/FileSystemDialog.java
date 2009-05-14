/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
