/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import org.eclipse.emf.common.notify.Notification;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.INotificationHelper;

/**
 * ModelerNotificationHelper
 */
public class ModelerNotificationHelper implements INotificationHelper {

    /**
     * Construct an instance of ModelerNotificationHelper.
     * 
     */
    public ModelerNotificationHelper() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.eventsupport.INotificationHelper#getNotifier(org.eclipse.emf.common.notify.Notification)
     */
    public Object getNotifier(Notification notification) {
        return ModelerCore.getModelEditor().getChangedObject(notification);
    }

}
