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

package com.metamatrix.modeler.internal.ui.outline;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.DragAndDropCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * ModelOutlineTreeViewerDropAdapter
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
