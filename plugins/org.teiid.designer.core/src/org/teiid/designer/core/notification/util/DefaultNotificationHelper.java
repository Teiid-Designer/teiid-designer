/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.notification.util;

import org.eclipse.emf.common.notify.Notification;

/**
 * DefaultNotificationHelper
 *
 * @since 8.0
 */
public class DefaultNotificationHelper implements INotificationHelper {

    /**
     * Construct an instance of DefaultNotificationHelper.
     * 
     */
    public DefaultNotificationHelper() {
        super();
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.ui.common.eventsupport.INotificationHelper#getNotifier(org.eclipse.emf.common.notify.Notification)
     */
    @Override
	public Object getNotifier(Notification notification) {
        return notification.getNotifier();
    }

}
