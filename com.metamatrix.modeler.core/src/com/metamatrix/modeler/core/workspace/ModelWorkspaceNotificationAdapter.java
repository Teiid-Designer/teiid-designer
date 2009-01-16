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

package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.Notification;



/**
 * The <code>ModelWorkspaceNotificationAdapter</code> class provides default (empty) implementations for the methods
 * described by the <code>ModelWorkspaceNotificationListener</code> interface. Classes that wish to deal with
 * individual methods can extend this class and override those methods which they are interested in.
 */
public class ModelWorkspaceNotificationAdapter implements ModelWorkspaceNotificationListener {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener#notifyAdd(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyAdd(ModelWorkspaceNotification theNotification) {}
    
    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChange(ModelWorkspaceNotification theNotification) {}

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged(Notification theNotification) {}
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener#notifyClosing(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyClosing(ModelWorkspaceNotification theNotification) {}
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener#notifyMove(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyMove(ModelWorkspaceNotification theNotification) {}
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener#notifyOpen(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyOpen(ModelWorkspaceNotification theNotification) {}
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener#notifyRemove(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyRemove(ModelWorkspaceNotification theNotification) {}

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener#notifyRename(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyRename(ModelWorkspaceNotification theNotification) {}
    
    /** 
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener#notifyReloaded(com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification)
     * @since 4.2
     */
    public void notifyReloaded(ModelWorkspaceNotification notification) {}
    
    /** 
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener#notifyClean(org.eclipse.core.resources.IProject)
     * @since 5.5
     */
    public void notifyClean(IProject proj) {}

}
