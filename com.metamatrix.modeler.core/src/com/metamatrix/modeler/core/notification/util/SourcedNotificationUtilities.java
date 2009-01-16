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

package com.metamatrix.modeler.core.notification.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;

import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.transaction.SourcedNotification;

/**
 * The <code>SourcedNotificationUtil</code> class contains utility methods for use with
 * {@link com.metamatrix.modeler.core.transaction.SourcedNotification} objects.
 */
public class SourcedNotificationUtilities {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** No arg construction not allowed. */
    private SourcedNotificationUtilities() {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS FOR SOURCED NOTIFICAIONS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets a Collection of Add Notifications from the Sourced Notification.
     * @param sourcedNotification the sourced notification
     * @return a collection of add Notifications or empty collection if the SourcedNotification
     * doesnt contain any add Notifications.
     */
    public static Collection getAddNotifications(SourcedNotification sourcedNotification) {
        Collection result = new ArrayList();
        if(sourcedNotification != null) {
            Iterator iter = sourcedNotification.getNotifications().iterator();
            while(iter.hasNext()) {
                Notification notification = (Notification)iter.next();
                if(NotificationUtilities.isAdded(notification)) {
                    result.add(notification);
                }
            }
        }
        return result;
    }
    
    /**
     * Gets a Collection of Removed Notifications from the Sourced Notification.
     * @param sourcedNotification the sourced notification
     * @return a collection of Removed Notifications or empty collection if the SourcedNotification
     * doesnt contain any Removed Notifications.
     */
    public static Collection getRemovedNotifications(SourcedNotification sourcedNotification) {
        Collection result = new ArrayList();
        if(sourcedNotification != null) {
            Iterator iter = sourcedNotification.getNotifications().iterator();
            while(iter.hasNext()) {
                Notification notification = (Notification)iter.next();
                if(NotificationUtilities.isRemoved(notification)) {
                    result.add(notification);
                }
            }
        }
        return result;
    }
    
    /**
     * Gets a Collection of Changed Notifications from the Sourced Notification.
     * @param sourcedNotification the sourced notification
     * @return a collection of Changed Notifications or empty collection if the SourcedNotification
     * doesnt contain any Changed Notifications.
     */
    public static Collection getChangedNotifications(SourcedNotification sourcedNotification) {
        Collection result = new ArrayList();
        if(sourcedNotification != null) {
            Iterator iter = sourcedNotification.getNotifications().iterator();
            while(iter.hasNext()) {
                Notification notification = (Notification)iter.next();
                if(NotificationUtilities.isChanged(notification)) {
                    result.add(notification);
                }
            }
        }
        return result;
    }

    /** Extracts all feature IDs from the SourcedNotification for the expected Class
      * 
      * @param sn the SourcedNotification to analyze
      * @param expectedClass the class whose features to look for.
      * @return a Set of Integers wrapping the FeatureIDs.  A -1 feature indicates an
      *   unrelated feature.
      */
    public static Set getAffectedFeatureIDs(SourcedNotification sn, Class expectedClass) {
        Set rv = new HashSet();
        
        Iterator itor = sn.getNotifications().iterator();
        while (itor.hasNext()) {
            Notification noti = (Notification) itor.next();
            rv.add(new Integer(noti.getFeatureID(expectedClass)));
        } // endwhile

        return rv;
    }

    /** Extracts all notifiers from all notifications contained in the SourcedNotification.
      * 
      * @param sn the SourcedNotification to analyze.
      * @return a Set of all unique notifiers
      */
    public static Set getAllNotifiers(SourcedNotification sn) {
        Collection notifications = sn.getNotifications();
        Set rv = new HashSet(notifications.size());

        Iterator itor = notifications.iterator();
        while (itor.hasNext()) {
            Notification noti = (Notification) itor.next();
            rv.add(noti.getNotifier());
        } // endwhile

        return rv;
    }
      
    /** Handle notifications generically; if the notification is a SourcedNotification, break it down;
     * if not, just add the notifier for the notification to the returned set.  
     * @param notification
     * @param gatherAnnotatedObjects should annotated objects be added to the return set
     * @return
     * @since 4.3
     */
    public static Set gatherNotifiers(final Notification notification, final boolean gatherAnnotatedObjects) {
        // compare the current EObject with the notification target(s)
        Set notifyingObjects;
        if (notification instanceof SourcedNotification) {
            final SourcedNotification sn = (SourcedNotification) notification;
            notifyingObjects = SourcedNotificationUtilities.getAllNotifiers(sn);
        } else {
            final Object target = notification.getNotifier();
            notifyingObjects = Collections.singleton(target);
        }
        if (gatherAnnotatedObjects) {
                final Set affectedObjects = new HashSet(notifyingObjects.size());
                for (final Iterator it = notifyingObjects.iterator(); it.hasNext();) {
                    final Object target = it.next();
                    // changes to extended properties are on the annotation
                    if (target instanceof EStringToStringMapEntryImpl) {
                        final EObject mightBeAnnotation = ((EStringToStringMapEntryImpl) target).eContainer();
                        if ( mightBeAnnotation instanceof Annotation ) {
                            affectedObjects.add(((Annotation) mightBeAnnotation).getAnnotatedObject());
                        }
                    }
                    affectedObjects.add(target);
                }
                notifyingObjects = affectedObjects;
        }
        return notifyingObjects;
    }      
}
