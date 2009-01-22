/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;

/**
 * This listener receives notification of changes to model workspace. This listener
 * helps maintain workspace items in synch with the file system.
 */
public interface ModelWorkspaceNotificationListener extends INotifyChangedListener {

    /**
     * Notification indicating a workspace item has been added
     * @param notification A Notification for item added.
     */
    void notifyAdd(ModelWorkspaceNotification notification);

    /**
     * Notification indicating a workspace item has been rmoved
     * @param notification A Notification for item removed.
     */
    void notifyRemove(ModelWorkspaceNotification notification);
    
    /**
     * Notification indicating a workspace item has been renamed
     * @param notification A Notification for item renamed.
     */
    void notifyRename(ModelWorkspaceNotification notification);     

    /**
     * Notification indicating a workspace item has been moved
     * @param notification A Notification for item moved.
     */
    void notifyMove(ModelWorkspaceNotification notification);

    /**
     * Notification indicating a workspace item has been reloaded from the file system
     * @param notification A Notification for item reload.
     */
    void notifyReloaded(ModelWorkspaceNotification notification);

    /**
     * Notification indicating a workspace item has been opeaned
     * @param notification A Notification for item opened.
     */
    void notifyOpen(ModelWorkspaceNotification notification);
    
    /**
     * Notification indicating a workspace item is closing
     * @param notification A Notification for item is closing.
     */
    void notifyClosing(ModelWorkspaceNotification notification);

    /**
     * Notification indicating when project clean was initiated
     * @param the project on which the clean was initiated.
     */
    void notifyClean(IProject proj);
}
