/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.outline;

import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.DragAndDropCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * ModelOutlineTreeViewerDropAdapter
 *
 * @since 8.0
 */
public class ModelOutlineTreeViewerDropAdapter extends EditingDomainViewerDropAdapter {

    /**
     * Construct an instance of ModelOutlineTreeViewerDropAdapter.
     * @param domain
     * @param viewer
     */
    public ModelOutlineTreeViewerDropAdapter(EditingDomain domain, Viewer viewer) {
        super(domain, viewer);
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void drop(DropTargetEvent event) {
        boolean started = ModelerCore.startTxn(UiConstants.Util.getString("ModelOutlineTreeViewer.dndUndoLabel"), this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // A command was created if the source was available early, and the
            // information used to create it was cached...
            //
            if (dragAndDropCommandInformation != null) {
                // Recreate the command.
                //
                command = dragAndDropCommandInformation.createCommand();
            } else {
                // Otherwise, the source should be available now as event.data, and we
                // can create the command.
                //
                source = extractDragSource(event.data);
                Object target = extractDropTarget(event.item);
                command =
                    DragAndDropCommand.create(
                        domain,
                        target,
                        getLocation(event),
                        event.operations,
                        originalOperation,
                        source);
            }

            // If the command can execute...
            //
            if (command.canExecute()) {
                // Execute it.
                //
                try {
                    ModelerCore.getModelEditor().executeCommand(null, command);
                } catch (ModelerCoreException e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getMessage() );
                    succeeded = false;
                }
            } else {
                // Otherwise, let's call the whole thing off.
                //
                event.detail = DND.DROP_NONE;
                command.dispose();
            }

            // Clean up the state.
            //
            command = null;
            commandTarget = null;
            source = null;
            succeeded = true;
        } finally {
            if ( started ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void dragEnter(DropTargetEvent event) {
        boolean started = ModelerCore.startTxn(false, false, null, this); 
        boolean succeeded = false;
        try {
            super.dragEnter(event);
            succeeded = true;
        } finally {
            if ( started ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void dragLeave(DropTargetEvent event) {
        boolean started = ModelerCore.startTxn(false, false, null, this); 
        boolean succeeded = false;
        try {
            super.dragLeave(event);
            succeeded = true;
        } finally {
            if ( started ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }        
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void dragOperationChanged(DropTargetEvent event) {
        boolean started = ModelerCore.startTxn(false, false, null, this); 
        boolean succeeded = false;
        try {
            super.dragOperationChanged(event);
            succeeded = true;
        } finally {
            if ( started ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }        
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void dragOver(DropTargetEvent event) {
        boolean started = ModelerCore.startTxn(false, false, null, this); 
        boolean succeeded = false;
        try {
            super.dragOver(event);
            succeeded = true;
        } finally {
            if ( started ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }        
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    public void dropAccept(DropTargetEvent event) {
        boolean started = ModelerCore.startTxn(false, false, null, this); 
        boolean succeeded = false;
        try {
            super.dropAccept(event);
            succeeded = true;
        } finally {
            if ( started ) {
                if ( succeeded ) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }         
    }
    
    /**
     * @see org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter#helper(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @Override
    protected void helper(DropTargetEvent theEvent) {
        super.helper(theEvent);        

        if ((this.source != null) && !this.source.isEmpty() && theEvent.item != null) {
            Object parent = theEvent.item.getData();

            // if event is an insert before need to get parent of the current parent
            if ((theEvent.feedback & DND.FEEDBACK_INSERT_BEFORE) == DND.FEEDBACK_INSERT_BEFORE) {
                if (parent instanceof EObject) {
                    Object temp = ((EObject)parent).eContainer();
                    
                    // if temp is null then parent must be the model resource
                    if (temp == null) {
                        temp = ModelUtilities.getModelResourceForModelObject((EObject)parent);
                    }
                        
                    parent = temp;
                }
            }

            // inspect the selected tree nodes. if one of them can't be moved stop the DND
            Iterator itr = this.source.iterator();
    
            while (itr.hasNext()) {
                Object child = itr.next();

                if ((parent != null) &&
                    (child instanceof EObject) &&
                    !ModelerCore.getModelEditor().isValidParent(parent, (EObject)child)) {
                    theEvent.detail = DND.DROP_NONE;
                    break;
                }
            }
        }
    }

}
