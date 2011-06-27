/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import static com.metamatrix.modeler.ui.UiConstants.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.navigator.CommonDragAdapterAssistant;

import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.util.EObjectTransfer;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>ModelNavigatorDragAssistant</code> prevents certain resources from being dragged unless they are being dragged from
 * outside of Eclipse.
 */
public class ModelNavigatorDragAssistant extends CommonDragAdapterAssistant {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.navigator.CommonDragAdapterAssistant#dragStart(org.eclipse.swt.dnd.DragSourceEvent,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void dragStart( DragSourceEvent anEvent,
                           IStructuredSelection selection ) {
        EObjectTransfer.getInstance().setObject(null);

        if (!selection.isEmpty()) {
            anEvent.doit = true;
            anEvent.detail = DND.DROP_NONE;

            if (SelectionUtilities.isAllEObjects(selection)) {
                List selectedEObjects = new ArrayList(SelectionUtilities.getSelectedEObjects(selection));
                // Defect 15947 NPE dragging EObjects into EMPTY EDITOR PANEL
                // IF NO OPEN MODEL EDITORS, want to stop this.....
                if (ModelEditorManager.getOpenResources().isEmpty()) {
                    anEvent.doit = false;
                }

                if (anEvent.doit) {
                    EObjectTransfer.getInstance().setObject(selectedEObjects);
                }
            } else {
                for (Iterator itr = selection.iterator(); itr.hasNext();) {
                    Object next = itr.next();

                    // don't allow drag if invalid source
                    if ((next instanceof IResource) && isInvalidDragSource((IResource)next)) {
                        anEvent.doit = false;
                    }

                    if (!anEvent.doit)
                        break;
                }
            }

        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.navigator.CommonDragAdapterAssistant#getSupportedTransferTypes()
     */
    @Override
    public Transfer[] getSupportedTransferTypes() {
        return new Transfer[] {EObjectTransfer.getInstance()};
    }

    /**
     * Indicates if the resource is a valid drag source
     * 
     * @param theResource the resource being dragged
     * @return <code>true</code> if resource is an invalid drag source; <code>false</code> otherwise.
     */
    private boolean isInvalidDragSource( IResource theResource ) {
        boolean result = false;

        if (theResource instanceof IFile) {
            // invalid drag sources are models, xsd files, and vdb files
            if (ModelUtilities.isModelFile(theResource) || ModelUtil.isVdbArchiveFile(theResource)
                    || ModelUtil.isXsdFile(theResource)) {

                // if it's parent is not a project than it came from the filesystem
                result = (theResource.getProject() == null);
            }
        } else if (theResource instanceof IFolder) {
            // don't allow folders to be dragged if contain files that can't be dragged
            try {
                IResource[] contents = ((IFolder)theResource).members(false);

                if ((contents != null) && (contents.length > 0)) {
                    for (IResource resource : contents) {
                        if (isInvalidDragSource(resource)) {
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

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.navigator.CommonDragAdapterAssistant#setDragData(org.eclipse.swt.dnd.DragSourceEvent,
     *      org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public boolean setDragData( DragSourceEvent anEvent,
                                IStructuredSelection aSelection ) {
        return false;
    }

}
