/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.editors;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * ModelEditorProjectListener is an implementation of IResourceChangeListener that is responsible for
 * closing un-initialized editors that are otherwise left open when a Project is closed or deleted.
 * I have no idea why Eclipse does not handle this for us, but it doesn't.  Eclipse also does not make
 * this easy - uninitialized editor tabs are difficult to close or even find out what resource they
 * are placeholders for.  I have a nagging feeling that there must be a better way to do this, but I
 * haven't found it.
 */
public class ModelEditorProjectListener implements IResourceChangeListener {


    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        int type = event.getType();
        if ( type == IResourceChangeEvent.PRE_CLOSE || type == IResourceChangeEvent.PRE_DELETE ) {
            final IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
            
            // NPE today when exiting using the new VDB Explorer View. Closing the view is ending up firing an event
            if( page == null )
                return;
            
            // PRE_CLOSE and PRE_DELETE are fired when a project is about to close or be deleted.
            // The resource is always a Project, and the event.getDelta() will return null, so it's useless.
            String projectName = event.getResource().getName();
            // get references to all the open editors
            final IEditorReference[] referenceArray = page.getEditorReferences();
            for ( int i=0 ; i<referenceArray.length ; ++i ) {
                // Any IEditorReference that has a null editor must be closed by this class.
                // Editors that have been created and initialized can handle their own close.
                if ( referenceArray[i].getEditor(false) == null ) {
                    // Have to find the project name, but I could not find any way to get a file or even
                    // an IMomento for this EditorReference.  The only way I can find the project name is
                    // to parse it out of the tooltip (I CAN'T BELIEVE I HAVE TO DO THIS)
                    String filePath = referenceArray[i].getTitleToolTip();
                    
                    // make sure filePath exists and has a slash as a safety check
                    if ((filePath != null) && (filePath.length() > 0)) {
                        int index = filePath.indexOf('/');
                        
                        if (index != -1) {
                            String project = filePath.substring(0, index);

                            if ( projectName.equals(project) ) {
                                // this editor is in the project that is closing/being deleted.  It must be closed.
                                final IEditorReference ref = referenceArray[i];
                                Display.getDefault().syncExec(new Runnable() {
                                    public void run() {
                                        // casting to WorkbenchPage is the only way I've found to close an editor reference
                                        page.closeEditors(new IEditorReference[] {ref}, false);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    }

}
