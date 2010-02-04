/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.transaction;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

/**
 * SourcedNotification
 */
public interface SourcedNotification extends Notification, NotificationChain {
    /**
     * Return the source of this object for this notification... may be null
     */
    Object getSource();
    
    Collection getNotifications();
}
