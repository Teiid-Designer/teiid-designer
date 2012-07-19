/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.Notification;



/**
 * The <code>ModelWorkspaceNotificationAdapter</code> class provides default (empty) implementations for the methods
 * described by the <code>ModelWorkspaceNotificationListener</code> interface. Classes that wish to deal with
 * individual methods can extend this class and override those methods which they are interested in.
 *
 * @since 8.0
 */
public class ModelWorkspaceNotificationAdapter implements ModelWorkspaceNotificationListener {

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyAdd(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void notifyAdd(ModelWorkspaceNotification theNotification) {}
    
    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChange(ModelWorkspaceNotification theNotification) {}

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void notifyChanged(Notification theNotification) {}
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyClosing(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void notifyClosing(ModelWorkspaceNotification theNotification) {}
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyMove(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void notifyMove(ModelWorkspaceNotification theNotification) {}
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyOpen(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void notifyOpen(ModelWorkspaceNotification theNotification) {}
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyRemove(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void notifyRemove(ModelWorkspaceNotification theNotification) {}

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyRename(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public void notifyRename(ModelWorkspaceNotification theNotification) {}
    
    /** 
     * @see org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyReloaded(org.teiid.designer.core.workspace.ModelWorkspaceNotification)
     * @since 4.2
     */
    @Override
	public void notifyReloaded(ModelWorkspaceNotification notification) {}
    
    /** 
     * @see org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyClean(org.eclipse.core.resources.IProject)
     * @since 5.5
     */
    @Override
	public void notifyClean(IProject proj) {}

}
