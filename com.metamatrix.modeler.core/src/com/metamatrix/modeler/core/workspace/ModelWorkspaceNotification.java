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

import org.eclipse.core.resources.IResourceDelta;

import org.eclipse.emf.common.notify.Notification;

/**
 * ModelWorkspaceNotification
 */
public interface ModelWorkspaceNotification extends Notification {
    public static final int CHANGE = 50;
    public static final int OPEN = 51;
    public static final int CLOSING = 52;
    public static final int RELOADED = 53;
    
    /**
     * @return ResourceDelta instance
     */
    public IResourceDelta getDelta();
    
    /**
     * Return true if event is for "pre-" event
     */    
    public boolean isPreNotification();
    
    /**
     * Return true if this a rename notification
     */    
    public boolean isRename();

    /**
     * Return true is event is a "postAutoBuild" event
     */    
    public boolean isPostAutoBuild();

    /**
     * Return true is event is a "postAutoBuild" or "preAutoBuild" event
     */    
    public boolean isAutoBuild();
    
    /**
     * Return true is event is a "preAutoBuild" event
     */    
    public boolean isPreAutoBuild();    

    /**
     * Return true is event is a "postChange" event
     */    
    public boolean isPostChange();    

    /**
     * Return true is event is a "preClose" event
     */    
    public boolean isPreClose();   
    
    /**
     * Return true is event is a "preDelete" event
     */    
    public boolean isPreDelete();    
 
    /**
     * Return true is event is an "open" event
     */    
    public boolean isOpen();    

    /**
     * Return true is event is a "close" event
     */    
    public boolean isClose();
    
    /**
     * Return true if the Notifier is an IFile
     */
    public boolean isFile();
    
    /**
     * return true if the Notifier is an IFolder
     */
    public boolean isFolder();
    
    /**
     * Return true if the Notifier is an IProject
     */
    public boolean isProject();   
    
}
