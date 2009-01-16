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

package com.metamatrix.modeler.webservice.ui.editor;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;

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
