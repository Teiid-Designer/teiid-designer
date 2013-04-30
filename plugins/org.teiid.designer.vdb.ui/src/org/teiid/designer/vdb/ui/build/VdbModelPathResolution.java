/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.build;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMarkerResolution;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.Messages;
import org.teiid.designer.vdb.ui.editor.VdbEditor;
import org.teiid.designer.vdb.ui.util.VdbUiRefactorHandler;

/**
 *
 */
public class VdbModelPathResolution implements IMarkerResolution {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IMarkerResolution#getLabel()
     */
    @Override
    public String getLabel() {
        return Messages.synchronizeVdbLabel;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IMarkerResolution#run(org.eclipse.core.resources.IMarker)
     */
    @Override
    public void run(IMarker marker) {
        IResource resource = marker.getResource();

        // Fix the Marked Model Resource
        if(isVdbFile(resource)) {
            final IFile theVdbFile = (IFile)resource;
            
        	VdbEditor editor = VdbUiRefactorHandler.getVdbEditorForFile(theVdbFile);
        	if( editor != null ) {
        		String message = NLS.bind(Messages.fixVdbPath_OpenEditorMessage, theVdbFile.getName());
        		boolean result = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages.fixVdbPath_OpenEditorTitle, message);
        		if( !result ) return;
        		VdbUiRefactorHandler.closeVdbEditor(editor);
        	}

            // Add the selected Med
            UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                @Override
                public void run() {
                  fixVdb(theVdbFile);
                }
            });

        }
    }
    
    void fixVdb( IFile theVdb ) {
    	VdbUtil.synchronizeVdb(theVdb, false);
    }
    
    /**
     * @param resource the resource being checked (never <code>null</code>)
     * @return <code>true</code> if resource is a VDB file
     */
    private boolean isVdbFile( IResource resource ) {
        return ((resource.getType() == IResource.FILE) && VdbConstants.VDB_FILE_EXTENSION.equals(resource.getFileExtension()) && resource.exists());
    }

}
