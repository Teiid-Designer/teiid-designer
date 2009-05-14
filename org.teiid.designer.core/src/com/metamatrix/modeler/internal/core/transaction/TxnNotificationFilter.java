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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.modeler.core.transaction.SourcedNotification;

/**
 * @since 4.3
 */
public class TxnNotificationFilter {

    private final List eventSets;
    private final ResourceSet resourceSet;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * @since 5.0
     */
    public TxnNotificationFilter( final ResourceSet theResourceSet ) {
        ArgCheck.isNotNull(theResourceSet);
        this.eventSets = new ArrayList();
        this.resourceSet = theResourceSet;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * Return the list of SourceNotification instances representing the finalized set of notifications for a transaction
     */
    public List getSourcedNotifications( final Object source ) {
        // Remove any empty NotifierEventSet instances
        removeEmptyEventSets();

        // Get a SourcedNotification for each NotifierEventSet
        List result = new ArrayList(this.eventSets.size());
        for (Iterator i = this.eventSets.iterator(); i.hasNext();) {
            NotifierEventSet es = (NotifierEventSet)i.next();
            if (!es.isEmpty()) {
                SourcedNotification sn = es.getSourcedNotification(source);
                if (sn != null) {
                    result.add(sn);
                }
            }
        }
        return result;
    }

    /**
     * Clean any state for this filter
     */
    public void clear() {
        for (Iterator i = this.eventSets.iterator(); i.hasNext();) {
            NotifierEventSet es = (NotifierEventSet)i.next();
            es.clear();
        }
        this.eventSets.clear();
    }

    /**
     * Process the new notification
     * 
     * @param notification
     * @return
     * @since 5.0
     */
    public boolean addNotification( final Notification notification ) {
        // If the notification can be ignored then return
        if (isIgnorable(notification)) {
            return false;
        }

        // Remove any empty NotifierEventSet instances
        removeEmptyEventSets();

        // If there is an existing NotifierEventSet instance for this notifier then
        // simple add the new notification to that instance
        final Object notifier = notification.getNotifier();
        NotifierEventSet eventSet = getExistingEventSet(notifier);
        if (eventSet != null) {
            eventSet.addNotification(notification);
            return true;
        }

        // If we can determine immediately that the notification represents a new root
        // object then create a new NotifierEventSet instance and return
        final Object feature = notification.getFeature();
        if (feature == null && (notifier instanceof Resource || notifier instanceof ResourceSet)) {
            eventSet = new NotifierEventSet(notifier);
            eventSet.addNotification(notification);
            this.eventSets.add(eventSet);
            return true;
        }

        // If the new notifier is the ancestor of the notifier for an existing NotifierEventSet
        // instance then we should remove the existing instance and create a new NotifierEventSet
        // for the parent notifier
        for (Iterator i = this.eventSets.iterator(); i.hasNext();) {
            NotifierEventSet es = (NotifierEventSet)i.next();

            // If the new notifier is the ancestor of the notifier for an existing NotifierEventSet
            if (!es.isEmpty() && isAncestor(notifier, es.getNotifier())) {

                // One exception to this is when the notifier for a remove notification is not
                // a child of any NotifierEventSet added objects. We don't want to remove
                // notifications in which an object is removed from one eContainer and
                // added to a newly created eContainer (e.g. remove column from an existing
                // table and add it to a new created table)
                if (isRemove(notification) && !isAncestor(es.getAddObjects(), notifier)) {
                    // do nothing
                } else {
                    es.clear();
                    i.remove();
                }
            }
        }
        eventSet = new NotifierEventSet(notifier);
        eventSet.addNotification(notification);
        this.eventSets.add(eventSet);

        return true;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Return true if it is determined that the specified notification can be ignored by this filter. The following rules are used
     * to determine when a notification can be ignored: <li>notification with a null notifier <li>touch notification <li>
     * notification with an EObject notifier and null feature <li>add/remove notification associated with creating a new
     * ModelAnnotation, DiagramContainer, TransformationContainer, AnnotationContainer instance <li>notification for a resource
     * not related to adding or removing EObjects <li>set notification in which the old and new value are the same <li>
     * notification in which the resource for the notifier is not within the ResourceSet associated with this filter <li>
     * notification in which the notifier is a child/descendant of an existing NotifierEventSet's notifier
     * 
     * @param notification
     * @return
     */
    protected boolean isIgnorable( final Notification notification ) {
        if (notification == null || notification.getNotifier() == null) {
            return true;
        }

        // If the notification is just a touch, don't add it.
        if (notification.isTouch()) {
            return true;
        }

        // If the notification does not change any feature of an EObject then don't process it. The
        // feature could be null if the notifier was a Resource or ResourceSet, so check that it is
        // an EObject notifier
        final Object notifier = notification.getNotifier();
        if (notification.getFeature() == null && notifier instanceof EObject) {
            return true;
        }

        // Check if this notification cannot be ignored due to special circumstances ...
        if (isSpecial(notification)) {
            return false;
        }

        // Check for other specific ignorable event types
        final Object oldVal = notification.getOldValue();
        final Object newVal = notification.getNewValue();
        switch (notification.getEventType()) {
            case Notification.REMOVE: {
                if (newVal == null
                    && (oldVal instanceof ModelAnnotation || oldVal instanceof DiagramContainer
                        || oldVal instanceof TransformationContainer || oldVal instanceof AnnotationContainer)) {
                    return true;
                }
                break;
            }
            case Notification.ADD: {
                if (oldVal == null
                    && (newVal instanceof ModelAnnotation || newVal instanceof DiagramContainer
                        || newVal instanceof TransformationContainer || newVal instanceof AnnotationContainer)) {
                    return true;
                }
                break;
            }
            case Notification.SET: {
                // If the feature is null then igore the notification
                if (notification.getFeature() == null) {
                    return true;
                }
                // If the old and new values are equal, then ignore the notification
                if (oldVal == newVal || (oldVal != null && oldVal.equals(newVal))) {
                    return true;
                }
                break;
            }
            case Notification.ADD_MANY: {
                break;
            }
            case Notification.REMOVE_MANY: {
                break;
            }
            case Notification.UNSET: {
                break;
            }
            case Notification.MOVE: {
                break;
            }
            default: {
                // do nothing
            }
        }

        // Don't process notifications for objects that are not contained in resources
        // associated with this notification filter
        Resource r = null;
        if (notifier instanceof EObject) {
            r = ((EObject)notifier).eResource();
            if (r == null || !this.resourceSet.getResources().contains(r)) {
                return true;
            }
        } else if (notifier instanceof Resource) {
            r = (Resource)notifier;
            if (!this.resourceSet.getResources().contains(r)) {
                return true;
            }
        } else if (notifier instanceof EClass) {
            r = ((EClass)notifier).eResource();
            if (r == null || !this.resourceSet.getResources().contains(r)) {
                return true;
            }
        }

        // Check if the notifier for this notification is a descendant of one of the
        // notifiers in an existing NotifierEventSet. If we have captured notifications
        // for the parent object, then we can ignore the notifications for the child.
        for (Iterator i = this.eventSets.iterator(); i.hasNext();) {
            NotifierEventSet es = (NotifierEventSet)i.next();

            // If the NotifierEventSet notifier is a parent of the notification's notifier
            // then we can ignore the child notification
            if (!es.isEmpty() && isAncestor(es.getNotifier(), notifier)) {

                // One exception to this is when the notifier for a remove notification is not
                // a child of any NotifierEventSet added objects. We don't want to ignore
                // notifications in which an object is removed from one eContainer and
                // added to a newly created eContainer (e.g. remove column from an existing
                // table and add it to a new created table)
                if (isRemove(notification) && !isAncestor(es.getAddObjects(), notifier)) {
                    return false;
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Return true if this notification must not be ignored due to special circumstances The current list of special circumstances
     * are as follows: <li>If the notifier is a SqlTranformation instance and there is an existing NotifierEventSet for its parent
     * SqlTransformationMappingRoot
     * 
     * @param notification
     * @return
     */
    protected boolean isSpecial( final Notification notification ) {
        final Object notifier = notification.getNotifier();
        switch (notification.getEventType()) {
            case Notification.REMOVE:
            case Notification.REMOVE_MANY:
            case Notification.ADD:
            case Notification.ADD_MANY: {
                // If the notifier is a SqlTranformation instance and we have an NotifierEventSet
                // for its parent SqlTransformationMappingRoot instance then do not ignore this
                // notification. The notification is needed to update SQL text due to a SqlAlias
                // being added or removed (see defect 21257)
                if (notifier instanceof SqlTransformation && getExistingEventSet(((EObject)notifier).eContainer()) != null) {
                    return true;
                }
                break;
            }
            default: {
                // do nothing
            }
        }
        return false;
    }

    /**
     * Return the target of this notification. If the notification is an add or remove the target is the new or old value,
     * respectively. If the notification is a set then the target is the notifier.
     * 
     * @param notification
     * @return
     */
    protected Collection getNotificationTarget( final Notification notification ) {
        final Object oldVal = notification.getOldValue();
        final Object newVal = notification.getNewValue();

        Collection target = Collections.singletonList(notification.getNotifier());
        switch (notification.getEventType()) {
            case Notification.REMOVE:
                target = Collections.singletonList(oldVal);
                break;
            case Notification.REMOVE_MANY:
                target = ((List)oldVal).isEmpty() ? Collections.EMPTY_LIST : ((List)oldVal);
                break;
            case Notification.ADD:
                target = Collections.singletonList(newVal);
                break;
            case Notification.ADD_MANY:
                target = ((List)newVal).isEmpty() ? Collections.EMPTY_LIST : ((List)newVal);
                break;
            default: {
                // do nothing
            }
        }
        return target;
    }

    /**
     * Return true if the event type of this notification is REMOVE or REMOVE_MANY, otherwise return false;
     * 
     * @param notification
     * @return
     */
    protected boolean isRemove( final Notification notification ) {
        switch (notification.getEventType()) {
            case Notification.REMOVE:
            case Notification.REMOVE_MANY:
                return true;
            default: {
                // do nothing
            }
        }
        return false;
    }

    /**
     * Return true if the event type of this notification is ADD or ADD_MANY, otherwise return false;
     * 
     * @param notification
     * @return
     */
    protected boolean isAdd( final Notification notification ) {
        switch (notification.getEventType()) {
            case Notification.ADD:
            case Notification.ADD_MANY:
                return true;
            default: {
                // do nothing
            }
        }
        return false;
    }

    /**
     * Return any existing NotifierEventSet with the specified notifier
     * 
     * @param notifier
     * @return
     */
    protected NotifierEventSet getExistingEventSet( final Object notifier ) {
        for (Iterator i = this.eventSets.iterator(); i.hasNext();) {
            NotifierEventSet es = (NotifierEventSet)i.next();
            if (es.getNotifier() == notifier) {
                return es;
            }
        }
        return null;
    }

    /**
     * Check all existing NotifierEventSet instances and remove those that current are empty
     */
    protected void removeEmptyEventSets() {
        for (Iterator i = this.eventSets.iterator(); i.hasNext();) {
            NotifierEventSet eventSet = (NotifierEventSet)i.next();
            if (eventSet.isEmpty()) {
                eventSet.clear();
                i.remove();
            }
        }
    }

    /**
     * Return true if this NotifierEventSet has an add or remove type notification in which its target is an ancestor of the
     * specified value
     * 
     * @param es
     * @return
     */
    protected boolean isAncestor( final Collection ancestors,
                                  final Object obj ) {
        if (ancestors != null && !ancestors.isEmpty()) {
            for (Iterator i = ancestors.iterator(); i.hasNext();) {
                if (isAncestor(i.next(), obj)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the second object is directly or indirectly contained by the first object, i.e., whether the second object
     * is in the content tree of the first.
     * 
     * @param ancestor the ancestor object in question.
     * @param obj the object to test.
     * @return whether the first object is an ancestor of the second object.
     */
    protected boolean isAncestor( final Object ancestor,
                                  final Object obj ) {
        if (ancestor == null || obj == null || ancestor == obj) {
            return false;
        }

        if (obj instanceof EObject) {
            if (ancestor instanceof EObject) {
                return EcoreUtil.isAncestor((EObject)ancestor, (EObject)obj);
            } else if (ancestor instanceof Resource) {
                return EcoreUtil.isAncestor((Resource)ancestor, (EObject)obj);
            } else if (ancestor instanceof ResourceSet) {
                return EcoreUtil.isAncestor((ResourceSet)ancestor, (EObject)obj);
            }

        } else if (obj instanceof Resource) {
            if (ancestor instanceof ResourceSet) {
                Object parent = ((Resource)obj).getResourceSet();
                if (ancestor == parent) {
                    return true;
                }
            }
        }

        return false;
    }

    // ==================================================================================
    // I N N E R C L A S S
    // ==================================================================================

    /**
     * A NotifierEventSet instance is a collection of all notifications with the same notifier
     */
    private class NotifierEventSet {

        private final Object notifier;
        private final List notifications;
        private final Map addEvents;
        private final Map removeEvents;
        private final Set addObjects;
        private final Set removeObjects;

        // ==================================================================================
        // C O N S T R U C T O R S
        // ==================================================================================

        /**
         * @since 4.3
         */
        public NotifierEventSet( final Object theNotifier ) {
            this.notifier = theNotifier;
            this.notifications = new ArrayList();
            this.addEvents = new HashMap();
            this.removeEvents = new HashMap();
            this.addObjects = new HashSet();
            this.removeObjects = new HashSet();
        }

        // ==================================================================================
        // P U B L I C M E T H O D S
        // ==================================================================================

        public boolean addNotification( final Notification notification ) {
            // If the notification can be ignored then return
            if (isIgnorable(notification)) {
                return false;
            }

            // Check if the same object was added and removed within the same set of notifications
            final Object oldVal = notification.getOldValue();
            final Object newVal = notification.getNewValue();

            switch (notification.getEventType()) {
                case Notification.REMOVE: {
                    Notification event = (Notification)this.addEvents.get(oldVal);
                    if (event != null && event.getEventType() == Notification.ADD) {
                        this.addEvents.remove(oldVal);
                        this.addObjects.remove(oldVal);
                        this.notifications.remove(event);
                        return false;
                    }
                    if (oldVal != null) {
                        event = (Notification)this.removeEvents.get(oldVal);
                        if (event != null) {
                            // Do not overwrite a containment notification
                            Object feature = event.getFeature();
                            if (feature instanceof EReference && ((EReference)feature).isContainment()) {
                                break;
                            }
                        }
                        // Assertion.isNull(this.removeEvents.get(oldVal));
                        this.removeEvents.put(oldVal, notification);
                        this.removeObjects.add(oldVal);
                    }
                    break;
                }
                case Notification.REMOVE_MANY: {
                    if (oldVal instanceof List && !((List)oldVal).isEmpty()) {
                        Object firstOldVal = ((List)oldVal).get(0);
                        Notification event = (Notification)this.addEvents.get(firstOldVal);
                        if (event != null && event.getEventType() == Notification.ADD_MANY) {
                            Object eventVal = event.getNewValue();
                            if (eventVal instanceof List && oldVal.equals(eventVal)) {
                                this.addEvents.remove(firstOldVal);
                                this.addObjects.removeAll((List)oldVal);
                                this.notifications.remove(event);
                                return false;
                            }
                        }
                        if (firstOldVal != null) {
                            event = (Notification)this.removeEvents.get(firstOldVal);
                            if (event != null) {
                                // Do not overwrite a containment notification
                                Object feature = event.getFeature();
                                if (feature instanceof EReference && ((EReference)feature).isContainment()) {
                                    break;
                                }
                            }
                            // Assertion.isNull(this.removeEvents.get(firstOldVal));
                            this.removeEvents.put(firstOldVal, notification);
                            this.removeObjects.addAll((List)oldVal);
                        }
                    }
                    break;
                }
                case Notification.ADD: {
                    Notification event = (Notification)this.removeEvents.get(newVal);
                    if (event != null && event.getEventType() == Notification.REMOVE) {
                        this.removeEvents.remove(newVal);
                        this.removeObjects.remove(newVal);
                        this.notifications.remove(event);

                        // If a remove event is followed by an add event on the same notifier and
                        // feature, check if this indicates a MOVE
                        if (isMoveNotification(event, notification)) {
                            Notification moveEvent = createMoveNotification(event, notification);
                            this.notifications.add(moveEvent);
                            return true;
                        }

                        return false;
                    }
                    if (newVal != null) {
                        event = (Notification)this.addEvents.get(newVal);
                        if (event != null) {
                            // Do not overwrite a containment notification
                            Object feature = event.getFeature();
                            if (feature instanceof EReference && ((EReference)feature).isContainment()) {
                                break;
                            }
                        }
                        // Assertion.isNull(this.addEvents.get(newVal));
                        this.addEvents.put(newVal, notification);
                        this.addObjects.add(newVal);
                    }
                    break;
                }
                case Notification.ADD_MANY: {
                    if (newVal instanceof List && !((List)newVal).isEmpty()) {
                        Object firstNewVal = ((List)newVal).get(0);
                        Notification event = (Notification)this.removeEvents.get(firstNewVal);
                        if (event != null && event.getEventType() == Notification.REMOVE_MANY) {
                            Object eventVal = event.getOldValue();
                            if (eventVal instanceof List && newVal.equals(eventVal)) {
                                this.removeEvents.remove(firstNewVal);
                                this.removeObjects.removeAll((List)newVal);
                                this.notifications.remove(event);
                                return false;
                            }
                        }
                        if (firstNewVal != null) {
                            event = (Notification)this.addEvents.get(firstNewVal);
                            if (event != null) {
                                // Do not overwrite a containment notification
                                Object feature = event.getFeature();
                                if (feature instanceof EReference && ((EReference)feature).isContainment()) {
                                    break;
                                }
                            }
                            // Assertion.isNull(this.addEvents.get(firstNewVal));
                            this.addEvents.put(firstNewVal, notification);
                            this.addObjects.addAll((List)newVal);
                        }
                    }
                    break;
                }
                case Notification.SET: {
                    // Setting a new value on a single valued feature is equivalent to an add
                    if (newVal != null && oldVal == null) {
                        Notification event = (Notification)this.removeEvents.get(newVal);
                        if (event != null && event.getEventType() == Notification.SET) {
                            this.removeEvents.remove(newVal);
                            this.notifications.remove(event);
                            return false;
                        }
                        event = (Notification)this.addEvents.get(newVal);
                        if (event != null) {
                            // Do not overwrite a containment notification
                            Object feature = event.getFeature();
                            if (feature instanceof EReference && ((EReference)feature).isContainment()) {
                                break;
                            }
                        }
                        // Assertion.isNull(this.addEvents.get(newVal));
                        this.addEvents.put(newVal, notification);
                        this.addObjects.add(newVal);

                        // Nulling an old value on a single valued feature is equivalent to an remove
                    } else if (newVal == null && oldVal != null) {
                        Notification event = (Notification)this.addEvents.get(oldVal);
                        if (event != null && event.getEventType() == Notification.SET) {
                            this.addEvents.remove(oldVal);
                            this.addObjects.remove(oldVal);
                            this.notifications.remove(event);
                            return false;
                        }
                        event = (Notification)this.removeEvents.get(oldVal);
                        if (event != null) {
                            // Do not overwrite a containment notification
                            Object feature = event.getFeature();
                            if (feature instanceof EReference && ((EReference)feature).isContainment()) {
                                break;
                            }
                        }
                        // Assertion.isNull(this.removeEvents.get(oldVal));
                        this.removeEvents.put(oldVal, notification);
                        this.removeObjects.add(oldVal);
                    }
                    break;
                }
                case Notification.UNSET: {
                    break;
                }
                case Notification.MOVE: {
                    break;
                }
                default: {
                    // do nothing
                }
            }

            // Add the notification to the event set
            this.notifications.add(notification);

            return true;
        }

        public Notification getPrimaryNotification() {
            if (this.notifications.isEmpty()) {
                return null;
            }
            for (Iterator i = this.notifications.iterator(); i.hasNext();) {
                final Notification n = (Notification)i.next();

                // Return any ADD or REMOVE notifications as the primary notification if one exists
                final Object feature = n.getFeature();
                if (feature instanceof EReference && ((EReference)feature).isContainment()) {
                    switch (n.getEventType()) {
                        case Notification.REMOVE: {
                            return n;
                        }
                        case Notification.REMOVE_MANY: {
                            return n;
                        }
                        case Notification.ADD: {
                            return n;
                        }
                        case Notification.ADD_MANY: {
                            return n;
                        }
                        case Notification.SET: {
                            break;
                        }
                        case Notification.UNSET: {
                            break;
                        }
                        case Notification.MOVE: {
                            break;
                        }
                        default: {
                            // do nothing
                        }
                    }
                }
            }

            return (Notification)this.notifications.get(0);
        }

        public Object getNotifier() {
            return this.notifier;
        }

        public SourcedNotification getSourcedNotification( final Object source ) {
            SourcedNotification sn = null;
            if (!isEmpty()) {
                sn = new SourcedNotificationImpl(source, getPrimaryNotification());
                for (Iterator i = this.notifications.iterator(); i.hasNext();) {
                    sn.add((Notification)i.next());
                }
            }
            return sn;
        }

        public Collection getRemoveObjects() {
            return this.removeObjects;
        }

        public Collection getAddObjects() {
            return this.addObjects;
        }

        public boolean isEmpty() {
            return this.notifications.isEmpty();
        }

        public void clear() {
            this.notifications.clear();
            this.addEvents.clear();
            this.removeEvents.clear();
        }

        // ==================================================================================
        // P R O T E C T E D M E T H O D S
        // ==================================================================================

        protected boolean isIgnorable( final Notification notification ) {
            if (notification == null || notification.getNotifier() == null) {
                return true;
            }

            // If the notification is just a touch, don't add it.
            if (notification.isTouch()) {
                return true;
            }

            // If this notification does not apply to this NotifierEventSet ...
            if (notification.getNotifier() != this.notifier) {
                return true;
            }

            // If the notification does not change any feature of an EObject then don't process it. The
            // feature could be null if the notifier was a Resource or ResourceSet, so check that it is
            // an EObject notifier
            if (notification.getFeature() == null && notification.getNotifier() instanceof EObject) {
                return true;
            }

            return false;
        }

        protected boolean isMoveNotification( final Notification removeEvent,
                                              final Notification addEvent ) {
            if (addEvent.getNotifier() instanceof EObject && addEvent.getFeature() == removeEvent.getFeature()
                && addEvent.getPosition() != removeEvent.getPosition()) {
                return true;
            }
            return false;
        }

        protected Notification createMoveNotification( final Notification removeEvent,
                                                       final Notification addEvent ) {
            Notification moveEvent = new ENotificationImpl((InternalEObject)addEvent.getNotifier(), // notifier
                                                           Notification.MOVE, // eventType
                                                           (EStructuralFeature)addEvent.getFeature(), // feature
                                                           new Integer(removeEvent.getPosition()), // oldValue
                                                           addEvent.getNewValue(), // newValue
                                                           addEvent.getPosition()); // position
            return moveEvent;

        }

    }
}
