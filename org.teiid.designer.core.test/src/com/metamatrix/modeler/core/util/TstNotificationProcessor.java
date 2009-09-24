/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;

import com.metamatrix.modeler.core.transaction.SourcedNotification;

/**
 * TestNotificationProcessor
 */
public class TstNotificationProcessor implements INotifyChangedListener{
    private Collection notifications = new ArrayList();
        
    public int getCount(){
        return notifications.size();
    }
        
    public void flush(){
        notifications.clear();
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged(Notification notification) {
        if(notification instanceof SourcedNotification){
            final Collection chain = ((SourcedNotification)notification).getNotifications();
            notifications.addAll(chain);
        }else{
            notifications.add(notification);
        }
    }
    
    public Collection getNotifications(){
        return notifications;
    }

}
