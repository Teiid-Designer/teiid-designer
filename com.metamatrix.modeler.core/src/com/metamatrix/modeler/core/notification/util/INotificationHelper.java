/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.notification.util;

import org.eclipse.emf.common.notify.Notification;

/**
 * INotificationHelper
 * 
 * This class provides an generic interface for accessing notification info that may be different than the
 * generic notification.  In particular, modeler.ui needs to access the real EObject which is only accessed
 * through ModelerCore.getModelEditor().getChangedObject(notification).  But we don't want metamatrix.ui
 * dependent on modeler... so the modeler.ui plugin creates it's own implementation of this interface and
 * sets the helper in NotificationUtilities on plugin start-up.
 * 
 */
public interface INotificationHelper {

    public Object getNotifier(Notification notification);

}
