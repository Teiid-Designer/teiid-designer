/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.editor;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.Viewer;
import com.metamatrix.modeler.internal.ui.util.ModelObjectNotificationHelper;


/** 
 * @since 5.0
 */
public class OperationEditorNotifyChangedListener implements
                                                 INotifyChangedListener {

    private Viewer viewer;
    private IResource iResource;
    
    /** 
     * 
     * @since 5.0
     */
    public OperationEditorNotifyChangedListener() {
        super();
    }
    
    public void initialize(Viewer viewer, IResource iResource) {
        if( this.viewer == null ) {
            this.viewer = viewer;
            this.iResource = iResource;
        }
    }
    /** 
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     * @since 5.0
     */
    public void notifyChanged(Notification theNotification) {
        boolean refreshTree = false;
        if( iResource != null ) {
            // For starter's let's throw this thing at a SourceNotificationHelper
            ModelObjectNotificationHelper notificationHelper = new ModelObjectNotificationHelper(theNotification);
            if( notificationHelper.shouldHandleNotification() ) {
                List changedModels = notificationHelper.getModifiedResources();
                for( Iterator iter = changedModels.iterator(); iter.hasNext(); ) {
                    Object nextObj = iter.next();
                    if( nextObj != null && 
                        nextObj instanceof IResource &&
                        nextObj.equals(iResource) ) {
                        refreshTree = true;
                        break;
                    }
                }
                
            }
        }
        
        if( refreshTree ) {
            this.viewer.refresh();
        }
    }

}
