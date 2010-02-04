/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
