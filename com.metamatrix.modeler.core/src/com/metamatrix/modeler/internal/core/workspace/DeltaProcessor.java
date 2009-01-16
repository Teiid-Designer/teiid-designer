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

package com.metamatrix.modeler.internal.core.workspace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notification;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification;

/**
 * DeltaProcessor
 */
public class DeltaProcessor implements IResourceChangeListener {
    private final ModelWorkspaceManager manager;

    /**
     * Construct an instance of DeltaProcessor. This should only be called by ModelWorkspaceManager.
     */
    DeltaProcessor( final ModelWorkspaceManager manager ) {
        super();
        this.manager = manager;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged( final IResourceChangeEvent event ) {
        if (ModelerCore.DEBUG_NOTIFICATIONS) {
            ResourceChangeUtilities.debug(event);
        }

        if (ResourceChangeUtilities.isProjectClosing(event)) {
            fireProjectClosing(event);
            return;
        } else if (ResourceChangeUtilities.isPreDelete(event)) {
            firePreDelete(event);
            return;
        }

        final IResourceDelta delta = event.getDelta();
        if (delta == null) {
            return;
        }

        List deltaResourceList = new ArrayList();
        deltaResourceList.add(delta);
        buildDeltaResourceList(event, delta, deltaResourceList);
        final Iterator iter = deltaResourceList.iterator();
        while (iter.hasNext()) {
            IResourceDelta nextDelta = (IResourceDelta)iter.next();
            debug(nextDelta, event);
            fireNotification(event, nextDelta);
        }
    }

    private void buildDeltaResourceList( final IResourceChangeEvent event,
                                         final IResourceDelta delta,
                                         final List deltaResourceList ) {
        if (delta == null) {
            return;
        }

        IResourceDelta[] deltas = delta.getAffectedChildren();
        if (deltas.length == 0) {
            return;
        }
        if (ResourceChangeUtilities.isRename(event, deltas)) {
            fireRename(deltas, event);
            return;
        }

        if (deltas.length > 1) {
            // reorder deltas so that the delta with removed type is at the begining
            if (delta.getKind() == IResourceDelta.CHANGED) {
                List newDeltas = new ArrayList();
                for (int i = 0; i < deltas.length; i++) {
                    final IResourceDelta nextDelta = deltas[i];
                    if (nextDelta.getKind() == IResourceDelta.REMOVED) {
                        newDeltas.add(0, nextDelta);
                    } else {
                        newDeltas.add(nextDelta);
                    }
                }
                for (int i = 0; i < newDeltas.size(); i++) {
                    deltas[i] = (IResourceDelta)newDeltas.get(i);
                }
            }
        }

        for (int i = 0; i < deltas.length; i++) {
            final IResourceDelta nextDelta = deltas[i];
            deltaResourceList.add(nextDelta);
            buildDeltaResourceList(event, nextDelta, deltaResourceList);
        }
    }

    /**
     * Using the given params construct and fire the appropriate notification to the ModelWorkspaceManager
     * 
     * @param delta
     * @param isPre true if the IResourceChangeEvent is a "pre-" event
     * @param isAutoBuild true if the IResourceChangeEvent is an "autoBuild" event
     */
    private void fireNotification( final IResourceChangeEvent event,
                                   IResourceDelta delta ) {
        final IResource rsrc = delta.getResource();

        // only fire notifications for ModelResource changed
        if (rsrc.getType() == IResource.FILE) {
            if (!ModelUtil.isModelFile(rsrc, false) && !ModelUtil.isVdbArchiveFile(rsrc)) {
                return;
            }
        }

        final int kind = delta.getKind();
        ModelWorkspaceNotification notification;

        try {
            switch (kind) {
                case IResourceDelta.ADDED:
                    notification = new ModelWorkspaceNotificationImpl(Notification.ADD, delta, event);
                    manager.fire(notification);
                    break;
                case IResourceDelta.CHANGED:
                    if (ResourceChangeUtilities.isDescriptionChange(delta) || ResourceChangeUtilities.isMarkersChange(delta)
                        || ResourceChangeUtilities.isTypeChange(delta)
                    // this check removed due to defect 16065
                    // ResourceChangeUtilities.isSynchChange(delta)*/
                    ) {
                        break;
                    }
                    if (ResourceChangeUtilities.isContentChanged(delta)) {
                        notification = new ModelWorkspaceNotificationImpl(ModelWorkspaceNotification.CHANGE, delta, event);
                        manager.fire(notification);
                        break;
                    }
                    if (ResourceChangeUtilities.isReplaced(delta)) {
                        notification = new ModelWorkspaceNotificationImpl(Notification.ADD, delta, event);
                        manager.fire(notification);
                        notification = new ModelWorkspaceNotificationImpl(Notification.REMOVE, delta, event);
                        manager.fire(notification);
                        break;
                    }
                    if (ResourceChangeUtilities.isMovedFrom(delta) || ResourceChangeUtilities.isMovedTo(delta)) {
                        notification = new ModelWorkspaceNotificationImpl(Notification.MOVE, delta, event);
                        manager.fire(notification);
                        break;
                    }
                    if (rsrc instanceof IProject && ResourceChangeUtilities.isOpened(delta)) {
                        if (((IProject)rsrc).isOpen()) {
                            notification = new ModelWorkspaceNotificationImpl(ModelWorkspaceNotification.OPEN, delta, event);
                            manager.fire(notification);
                            break;
                        }
                        notification = new ModelWorkspaceNotificationImpl(Notification.REMOVE, delta, event);
                        manager.fire(notification);
                        break;
                    }
                    break;
                case IResourceDelta.REMOVED:
                    notification = new ModelWorkspaceNotificationImpl(Notification.REMOVE, delta, event);
                    manager.fire(notification);
                    break;
                case IResourceDelta.ADDED_PHANTOM:
                case IResourceDelta.REMOVED_PHANTOM:
                case IResourceDelta.NO_CHANGE:
                    break;
                default:
                    throw new ModelerCoreRuntimeException(
                                                          ModelerCore.Util.getString("DeltaProcessor.Unsupported_Resource_Delta_type___{0}_1", kind)); //$NON-NLS-1$
            }
        } catch (CoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    /**
     * Seperate fireNotification logic to handle rename to ensure that remove and add events are compressed into a single set
     * 
     * @param deltas
     */
    private void fireRename( final IResourceDelta[] deltas,
                             IResourceChangeEvent event ) {
        ArgCheck.isNotNull(deltas);
        if (deltas.length != 2) {
            ArgCheck.isTrue(deltas.length == 2,
                            ModelerCore.Util.getString("DeltaProcessor.Deltas_length_must_be_2_for_a_rename_1")); //$NON-NLS-1$
        }

        IResourceDelta delta = null;
        if (ResourceChangeUtilities.isAdded(deltas[0])) {
            delta = deltas[0];
        } else if (ResourceChangeUtilities.isAdded(deltas[1])) {
            delta = deltas[1];
        }
        // if(ResourceChangeUtilities.isRemoved(deltas[0]) ){
        // delta = deltas[0];
        // }else if(ResourceChangeUtilities.isRemoved(deltas[1]) ){
        // delta = deltas[1];
        // }

        if (delta == null) {
            throw new ModelerCoreRuntimeException(
                                                  ModelerCore.Util.getString("DeltaProcessor.Unable_to_find_added_resource_for_rename_notification_2")); //$NON-NLS-1$
        }

        final ModelWorkspaceNotificationImpl notification = new ModelWorkspaceNotificationImpl(Notification.SET, delta, event);
        notification.setIsRename(true);

        try {
            manager.fire(notification);
        } catch (CoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    /**
     * Logic to fire a Closing notification
     */
    private void fireProjectClosing( final IResourceChangeEvent event ) {
        final ModelWorkspaceNotificationImpl notification = new ModelWorkspaceNotificationImpl(
                                                                                               ModelWorkspaceNotification.CLOSING,
                                                                                               null, event);
        try {
            manager.fire(notification);
        } catch (CoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    /**
     * Logic to fire a Pred-Delete notification
     */
    private void firePreDelete( final IResourceChangeEvent event ) {
        final ModelWorkspaceNotificationImpl notification = new ModelWorkspaceNotificationImpl(
                                                                                               Notification.REMOVE,
                                                                                               null, event);
        try {
            manager.fire(notification);
        } catch (CoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    private void debug( final IResourceDelta delta,
                        final IResourceChangeEvent event ) {
        if (ModelerCore.DEBUG_NOTIFICATIONS) {
            System.out.println("--------------------START---------------------------");//$NON-NLS-1$
            System.out.println("resource = " + delta.getResource()); //$NON-NLS-1$
            System.out.println("isPre = " + ResourceChangeUtilities.isPreEvent(event)); //$NON-NLS-1$
            System.out.println("isPostAutoBuild = " + ResourceChangeUtilities.isPostAutoBuild(event)); //$NON-NLS-1$
            System.out.println("isPostChange = " + ResourceChangeUtilities.isPostChange(event)); //$NON-NLS-1$
            System.out.println("isPreAutoBuild = " + ResourceChangeUtilities.isPreAutoBuild(event)); //$NON-NLS-1$
            System.out.println("kind=" + delta.getKind()); //$NON-NLS-1$
            System.out.println("isAdded=" + ResourceChangeUtilities.isAdded(delta)); //$NON-NLS-1$
            System.out.println("isChanged=" + ResourceChangeUtilities.isChanged(delta)); //$NON-NLS-1$
            System.out.println("isContentChanged=" + ResourceChangeUtilities.isContentChanged(delta)); //$NON-NLS-1$
            System.out.println("isDescriptionChange=" + ResourceChangeUtilities.isDescriptionChange(delta)); //$NON-NLS-1$
            System.out.println("isFile=" + ResourceChangeUtilities.isFile(delta)); //$NON-NLS-1$
            System.out.println("isFolder=" + ResourceChangeUtilities.isFolder(delta)); //$NON-NLS-1$
            System.out.println("isMovedFrom=" + ResourceChangeUtilities.isMovedFrom(delta)); //$NON-NLS-1$
            System.out.println("isMovedTo=" + ResourceChangeUtilities.isMovedTo(delta)); //$NON-NLS-1$
            System.out.println("isOpened=" + ResourceChangeUtilities.isOpened(delta)); //$NON-NLS-1$
            System.out.println("isProject=" + ResourceChangeUtilities.isProject(delta)); //$NON-NLS-1$
            System.out.println("isRemoved=" + ResourceChangeUtilities.isRemoved(delta)); //$NON-NLS-1$
            System.out.println("isReplaced=" + ResourceChangeUtilities.isReplaced(delta)); //$NON-NLS-1$
            System.out.println("isTypeChange=" + ResourceChangeUtilities.isTypeChange(delta)); //$NON-NLS-1$
            System.out.println("----------------------END-------------------------");//$NON-NLS-1$
        }
    }
}
