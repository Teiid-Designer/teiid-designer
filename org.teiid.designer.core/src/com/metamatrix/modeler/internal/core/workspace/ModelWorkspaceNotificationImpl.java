/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification;

/**
 * ModelWorkspaceNotificationImpl
 */
public class ModelWorkspaceNotificationImpl extends NotificationImpl implements ModelWorkspaceNotification {
    protected final IResourceDelta delta;
    protected final Object notifier;   
    protected final IResourceChangeEvent event;  
    private boolean isRename = false;

    //-------------------------------------------------------------------------
    //   C O N S T R U C T O R                
    //-------------------------------------------------------------------------    
    /**
     * Construct an instance of ModelWorkspaceNotificationImpl.
     * @param eventType
     * @param delta
     * @param isPre
     * @param isAutoBuild
     */    
    public ModelWorkspaceNotificationImpl(final int eventType, final IResourceDelta delta, final IResourceChangeEvent event){
        super(eventType, null, delta);
        ArgCheck.isNotNull(event);
        
        this.delta = delta;
        this.event= event;
        if(delta != null) {
			this.notifier = delta.getResource();
        } else {
        	this.notifier = event.getResource(); 
        }
    }
    
    public IResourceChangeEvent getChangeEvent() {
        return this.event;
    }

    //-------------------------------------------------------------------------
    //   I N T E R F A C E   M E T H O D S               
    //-------------------------------------------------------------------------  
    /**
     * @return ResourceDelta instance
     */
    public IResourceDelta getDelta() {
        return delta;
    }
    
    /**
     * Return true if this a rename notification
     */
    public boolean isRename(){
        return this.isRename;
    }

    /**
     * set is rename attribute
     */
    public void setIsRename(final boolean isRename){
        this.isRename = isRename;
    }

    /**
     * Return true is event is an "autoBuild" event
     */
    public boolean isAutoBuild() {
        return ResourceChangeUtilities.isAutoBuild(this.event);
    }
    
    /**
     * Return true is event is an "preAutoBuild" event
     */
    public boolean isPreAutoBuild() {
        return ResourceChangeUtilities.isPreAutoBuild(this.event);
    }
    
    @Override
    public Object getNotifier(){
        return this.notifier;
    }    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isPostAutoBuild()
     */
    public boolean isPostAutoBuild() {
        return ResourceChangeUtilities.isPostAutoBuild(this.event);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isPostChange()
     */
    public boolean isPostChange() {
        return ResourceChangeUtilities.isPostChange(this.event);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isPreClose()
     */
    public boolean isPreClose() {
        return ResourceChangeUtilities.isPreClose(this.event);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isPreDelete()
     */
    public boolean isPreDelete() {
        return ResourceChangeUtilities.isPreDelete(this.event);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isOpen()
     */
    public boolean isOpen() {
        return ResourceChangeUtilities.isOpened(this.delta);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isClose()
     */
    public boolean isClose() {
        return getEventType() == CLOSING;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isPreNotification()
     */
    public boolean isPreNotification() {
        return ResourceChangeUtilities.isPreEvent(this.event);
    }       

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isPreNotification()
     */
    public boolean isFile() {
        return ResourceChangeUtilities.isFile(this.delta);
    }       

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isPreNotification()
     */
    public boolean isFolder() {
        return ResourceChangeUtilities.isFolder(this.delta);
    }       

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification#isPreNotification()
     */
    public boolean isProject() {
    	//return ResourceChangeUtilities.isProject(this.delta);
    	// TO DO: Handle project close notifications
    	if(!ResourceChangeUtilities.isProject(this.delta)) {
    		if(!(this.notifier instanceof IProject)) {
    			return false;
    		}
    	}
        return true;
    }

    //-------------------------------------------------------------------------
    //   I N S T A N C E    M E T H O D S            
    //-------------------------------------------------------------------------    
    @Override
    public String toString(){
        String typeString = getNotificationTypePhrase();
        
        String resourceType = " "; //$NON-NLS-1$
        if(delta != null && delta.getResource() != null){
            if(delta.getResource() instanceof IProject){
                resourceType = " Project Notification: "; //$NON-NLS-1$
            }else if (delta.getResource() instanceof IFile){
                resourceType = " File Notification: "; //$NON-NLS-1$
            }else if (delta.getResource() instanceof IFolder){ 
                resourceType = " Folder Notification: "; //$NON-NLS-1$
            }
        }
        
        
        StringBuffer toString = new StringBuffer(typeString);
        toString.append(resourceType);
        toString.append(" on "); //$NON-NLS-1$        
        toString.append(getNotifier() );
        toString.append("\nisPreAutoBuild = " + isPreAutoBuild() ); //$NON-NLS-1$ 
        toString.append("\nisPostAutoBuild = " + isPostAutoBuild() ); //$NON-NLS-1$       
        toString.append("\nisAutoBuild = " + isAutoBuild() ); //$NON-NLS-1$
        toString.append("\nisRename = " + isRename() ); //$NON-NLS-1$
        toString.append("\nisClose = " + isClose() ); //$NON-NLS-1$     
        toString.append("\nisOpen = " + isOpen() ); //$NON-NLS-1$   
        toString.append("\nisFile = " + isFile() ); //$NON-NLS-1$
        toString.append("\nisFolder = " + isFolder() ); //$NON-NLS-1$
        toString.append("\nisProject = " + isProject() ); //$NON-NLS-1$        
        toString.append("\nisPostChange = " + isPostChange() ); //$NON-NLS-1$        
        
        return toString.toString();
    }
    
    public String getNotificationTypePhrase() {
        String typeString = null;
        final int type = this.getEventType();
        switch (type) {
            case Notification.ADD : typeString = "Add"; //$NON-NLS-1$
                break;
            case ModelWorkspaceNotification.CHANGE : typeString = "Change"; //$NON-NLS-1$                
                break;
            case Notification.MOVE : typeString = "Move"; //$NON-NLS-1$                
                break;
            case Notification.REMOVE : typeString = "Remove"; //$NON-NLS-1$                
                break;
            case Notification.SET : typeString = "Set"; //$NON-NLS-1$                
                break;
            case ModelWorkspaceNotification.CLOSING : typeString = "Closing"; //$NON-NLS-1$                
                break;
            case ModelWorkspaceNotification.OPEN : typeString = "Open"; //$NON-NLS-1$                
                break;

            default :
                typeString = "Unknown Type"; //$NON-NLS-1$
                break;
        }
        return typeString;
    }


}
