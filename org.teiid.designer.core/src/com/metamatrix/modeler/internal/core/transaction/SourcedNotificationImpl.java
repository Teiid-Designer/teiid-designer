/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import com.metamatrix.modeler.core.transaction.SourcedNotification;

/**
 * SourcedNotificationImpl
 */
public class SourcedNotificationImpl implements SourcedNotification {
    
    private final Object source;
    private final List notifications;
    private Notification notification;
    
    /**
     * Construct an instance of SourcedNotificationImpl.
     * @param source may be null
     * @param top notification  may not be null;
     * 
     */
    public SourcedNotificationImpl(final Object theSource, final Notification thePrimaryNotification) {
        super();
        
        this.source        = theSource;
        this.notification  = thePrimaryNotification;
        this.notifications = new ArrayList();
        if (thePrimaryNotification != null) {
            this.notifications.add(notification);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.SourcedNotification#getSource()
     */
    public Object getSource() {
        return this.source;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNotifier()
     */
    public Object getNotifier() {
        return (this.notification == null ? null : this.notification.getNotifier());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getEventType()
     */
    public int getEventType() {
        return (this.notification == null ? 0 : this.notification.getEventType());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getFeatureID(java.lang.Class)
     */
    public int getFeatureID(Class expectedClass) {
        return (this.notification == null ? 0 : this.notification.getFeatureID(expectedClass));
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getFeature()
     */
    public Object getFeature() {
        return (this.notification == null ? null : this.notification.getFeature());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldValue()
     */
    public Object getOldValue() {
        return (this.notification == null ? null : this.notification.getOldValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewValue()
     */
    public Object getNewValue() {
        return (this.notification == null ? null : this.notification.getNewValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#isTouch()
     */
    public boolean isTouch() {
        return (this.notification == null ? true : this.notification.isTouch());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#isReset()
     */
    public boolean isReset() {
        return (this.notification == null ? false : this.notification.isReset());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getPosition()
     */
    public int getPosition() {
        return (this.notification == null ? 0 : this.notification.getPosition());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#merge(org.eclipse.emf.common.notify.Notification)
     */
    public boolean merge(Notification notification) {
        return (this.notification == null ? false : this.notification.merge(notification));
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldBooleanValue()
     */
    public boolean getOldBooleanValue() {
        return (this.notification == null ? false : this.notification.getOldBooleanValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewBooleanValue()
     */
    public boolean getNewBooleanValue() {
        return (this.notification == null ? false : this.notification.getNewBooleanValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldByteValue()
     */
    public byte getOldByteValue() {
        return (this.notification == null ? 0 : this.notification.getOldByteValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewByteValue()
     */
    public byte getNewByteValue() {
        return (this.notification == null ? 0 : this.notification.getNewByteValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldCharValue()
     */
    public char getOldCharValue() {
        return (this.notification == null ? ' ' : this.notification.getOldCharValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewCharValue()
     */
    public char getNewCharValue() {
        return (this.notification == null ? ' ' : this.notification.getNewCharValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldDoubleValue()
     */
    public double getOldDoubleValue() {
        return (this.notification == null ? 0 : this.notification.getOldDoubleValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewDoubleValue()
     */
    public double getNewDoubleValue() {
        return (this.notification == null ? 0 : this.notification.getNewDoubleValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldFloatValue()
     */
    public float getOldFloatValue() {
        return (this.notification == null ? 0 : this.notification.getOldFloatValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewFloatValue()
     */
    public float getNewFloatValue() {
        return (this.notification == null ? 0 : this.notification.getNewFloatValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldIntValue()
     */
    public int getOldIntValue() {
        return (this.notification == null ? 0 : this.notification.getOldIntValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewIntValue()
     */
    public int getNewIntValue() {
        return (this.notification == null ? 0 : this.notification.getNewIntValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldLongValue()
     */
    public long getOldLongValue() {
        return (this.notification == null ? 0 : this.notification.getOldLongValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewLongValue()
     */
    public long getNewLongValue() {
        return (this.notification == null ? 0 : this.notification.getNewLongValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldShortValue()
     */
    public short getOldShortValue() {
        return (this.notification == null ? 0 : this.notification.getOldShortValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewShortValue()
     */
    public short getNewShortValue() {
        return (this.notification == null ? 0 : this.notification.getNewShortValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getOldStringValue()
     */
    public String getOldStringValue() {
        return (this.notification == null ? null : this.notification.getOldStringValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.Notification#getNewStringValue()
     */
    public String getNewStringValue() {
        return (this.notification == null ? null : this.notification.getNewStringValue());
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.NotificationChain#add(org.eclipse.emf.common.notify.Notification)
     */
    public boolean add(final Notification theNotification) {
        if (theNotification == null || theNotification == this.notification) {
            return false;
        }
        this.notifications.add(theNotification);
        if (this.notification == null) {
            this.notification = theNotification;
        }

        return true;           
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.common.notify.NotificationChain#dispatch()
     */
    public void dispatch() {
        for (Iterator i = this.notifications.iterator(); i.hasNext();) {
            ((NotificationImpl)i.next()).dispatch();
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.transaction.SourcedNotification#getNotifications()
     */
    public Collection getNotifications() {
        return this.notifications;
    }

    /**
     * @see org.eclipse.emf.common.notify.Notification#wasSet()
     * @since 4.3
     */
    public boolean wasSet() {
        return (this.notification == null ? false : this.notification.wasSet());
    }
    
    public Notification getPrimaryNotification() {
        return this.notification;
    }

    /** 
     * @see java.lang.Object#toString()
     * @since 4.3
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(500);
        sb.append("SourcedNotificationImpl: notifier="); //$NON-NLS-1$
        sb.append(this.getNotifier());
        sb.append(", feature="); //$NON-NLS-1$
        sb.append(this.getFeature());
        sb.append(", oldValue="); //$NON-NLS-1$
        sb.append(this.getOldValue());
        sb.append(", newValue="); //$NON-NLS-1$
        sb.append(this.getNewValue());
        return sb.toString();
    }
    
}
