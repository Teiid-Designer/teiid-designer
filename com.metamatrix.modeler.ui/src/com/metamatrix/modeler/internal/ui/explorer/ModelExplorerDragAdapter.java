/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.explorer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.ui.views.navigator.NavigatorDragAdapter;

import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.util.EObjectTransfer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>ModelExplorerDragAdapter</code> prevents certain resources from being dragged unless
 * they are being dragged from outside of Eclipse.
 */
public class ModelExplorerDragAdapter extends NavigatorDragAdapter
                                      implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    protected ISelectionProvider selectionProvider;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public ModelExplorerDragAdapter(ISelectionProvider theProvider) {
        super(theProvider);
        selectionProvider = theProvider;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.views.navigator.NavigatorDragAdapter#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
     */
    @Override
    public void dragStart(DragSourceEvent theEvent) {
        super.dragStart(theEvent);

        IStructuredSelection selection = (IStructuredSelection)selectionProvider.getSelection();
        EObjectTransfer.getInstance().setObject(null);
        
        if (!selection.isEmpty()) {
            theEvent.doit = true;
            theEvent.detail = DND.DROP_NONE;
            
            if( SelectionUtilities.isAllEObjects(selection) ) {

                List selectedEObjects = new ArrayList(SelectionUtilities.getSelectedEObjects(selection));
            	// Defect 15947 NPE dragging EObjects into EMPTY EDITOR PANEL
                // IF NO OPEN MODEL EDITORS, want to stop this.....
                if( ModelEditorManager.getOpenResources().isEmpty() ) {
                	theEvent.doit = false;
                }
                if( theEvent.doit )
                	EObjectTransfer.getInstance().setObject(selectedEObjects);
            } else {
                for (Iterator itr = selection.iterator(); itr.hasNext();) {
                    Object next = itr.next();
                    
                    // don't allow drag if invalid source
                    if ((next instanceof IResource) && isInvalidDragSource((IResource)next)) {
                        theEvent.doit = false;
                    }
                    if( ! theEvent.doit )
                        break;
                }
            }

        }
    }
    
    @Override
    public void dragFinished(DragSourceEvent theEvent) {
        //super.dragFinished(theEvent);
    }

    /**
     * Indicates if the resource is a valid drag source
     * @param theResource the resource being dragged
     * @return <code>true</code> if resource is an invalid drag source; <code>false</code> otherwise.
     */
    private boolean isInvalidDragSource(IResource theResource) {
        boolean result = false;
        
        if (theResource instanceof IFile) {
            // invalid drag sources are models, xsd files, and vdb files
            if (ModelUtilities.isModelFile(theResource) ||
                ModelUtil.isVdbArchiveFile(theResource) ||
                ModelUtil.isXsdFile(theResource)) {
                
                // if it's parent is not a project than it came from the filesystem
                result = (theResource.getProject() == null);
            }
        } else if (theResource instanceof IFolder) {
            // don't allow folders to be dragged if contain files that can't be dragged
            try {
                IResource[] contents = ((IFolder)theResource).members(false);
                
                if ((contents != null) && (contents.length > 0)) {
                    for (int i = 0; i < contents.length; i++) {
                        if (isInvalidDragSource(contents[i])) {
                            result = true;
                            break;
                        }
                    }
                }
            } catch (CoreException theException) {
                Util.log(theException);
            }
        }
        
        return result;
    }

}
