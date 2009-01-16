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
import java.util.Iterator;
import java.util.List;

import org.eclipse.xsd.XSDAttributeGroupDefinition;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;


/**
 * The <code>NotificationUtilities</code> class contains utility methods for use with
 * {@link org.eclipse.emf.common.notify.Notification} objects.
 */
public class NotificationUtilities {

    private static INotificationHelper notificationHelper = new DefaultNotificationHelper();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** No arg construction not allowed. */
    private NotificationUtilities() {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the <code>EObject</code>s added to a list-based property of the event notifier.
     * @param theNotification the event being processed
     * @return a collection of added values or <code>null</code> if the event was not an addition
     * @see #isAdded(Notification)
     */
    public static EObject[] getAddedChildren(Notification theNotification) {
        EObject[] result = new EObject[0];

        if (isAdded(theNotification)) {
            //Backed this change out to fix defect 13609.  Coupled with changes to UoW 13339 AND 13609 as well
            //as the test cases all pass.
            //changes for defect 13339 to handle add_many logic (wrapped in try catch for extra security
//            if(theNotification.getNotifier() instanceof Resource) {
//                try {
//                    final Resource rsrc = (Resource)theNotification.getNotifier();
//                    final int position = theNotification.getPosition();
//                    if(position > -1) {
//                        int size = 1;
//                        if(theNotification.getNewValue() instanceof Collection) {
//                            size = ((Collection)theNotification.getNewValue()).size();
//                        }
//                        
//                        result = new EObject[size];
//                        int tmp = 0;
//                        while(tmp < size) {
//                            result[tmp] = (EObject)rsrc.getContents().get(position + tmp);
//                            tmp++;
//                        }
//                        
//                        return result;
//                    }
//                } catch (Exception err) {
//                    //Added try catch for extra security for new code implementation.
//                    //If we get an exception, let the code fall through to the old implementation.
//                }
//            }
            
            Object added = theNotification.getNewValue();
            List temp = new ArrayList();

            if (added instanceof List) {
                List newKids = (List)added;

                for (int size = newKids.size(), i = 0; i < size; i++) {
                    Object kid = newKids.get(i);

                    if (kid instanceof EObject) {
                        temp.add(kid);
                    }
                }
            } else {
                if (added instanceof EObject) {
                    temp.add(added);
                }
            }

            // if found EObjects put them in the result
            if (!temp.isEmpty()) {
                int size = temp.size();
                result = new EObject[size];

                for (int i = 0; i < size; i++) {
                    result[i] = (EObject)temp.get(i);
                }
            }
        }

        return result;
    }

    /**
     * Gets the <code>EObject</code>s remove from a list-based property of the event notifier.
     * @param theNotification the event being processed
     * @return a collection of removed values or <code>null</code> if the event was not a removal
     * @see #isRemoved(Notification)
     */
    public static EObject[] getRemovedChildren(Notification theNotification) {
        EObject[] result = new EObject[0];

        if (isRemoved(theNotification)) {
            //If this is an up notification... just return the notifier
            Object removed = theNotification.getOldValue();
            if (removed instanceof List) {
                List oldKids = (List)removed;
                int size = oldKids.size();
                result = new EObject[size];

                for (int i = 0; i < size; i++) {
                    result[i] = (EObject)oldKids.get(i);
                }
            } else if( removed instanceof EObject ) {
                result = new EObject[1];
                result[0] = (EObject)removed;
            }

        }
        return result;
    }

    /**
     * Gets the <code>EObject</code> whose state has changed.
     * @param theNotification the notification event
     * @return the target <code>EObject</code> or <code>null</code> if not an <code>EObject</code>
     */
    public static EObject getEObject(Notification theNotification) {
        Object obj = getNotificationHelper().getNotifier(theNotification);

        return (obj instanceof EObject) ? (EObject)obj : null;
    }
    
    /**
     * Gets the <code>Resource</code> whose state has changed.
     * @param theNotification the notification event
     * @return the target <code>Resource</code> or <code>null</code> if not an <code>Resource</code>
     */
    public static Resource getResource(Notification theNotification) {
        Object obj = getNotificationHelper().getNotifier(theNotification);

        return (obj instanceof Resource) ? (Resource)obj : null;
    }

    /**
     * Gets the model object identifier associated with the notification feature.
     * @param theNotification the notification event
     * @return the feature model object identifier or -1 if feature doesn't exist
     */
    public static int getFeatureChanged(Notification theNotification) {
        int result = -1;
        Object obj = theNotification.getFeature();

        if (obj instanceof EStructuralFeature) {
            EStructuralFeature feature = (EStructuralFeature)obj;
            result = feature.getFeatureID();
        }

        return result;
    }

    /**
     * Indicates if a list-based feature of the notifier has been inserted into.  Also handles
     * Notifications where type==SET and the set feature is an EReference, which we also treat
     * as an add.
     * @param theNotification the notification event
     * @return <code>true</code> if an insertion has occurred; <code>false</code> otherwise.
     */
    public static boolean isAdded(Notification notification) {
            final int eventType = notification.getEventType();
            final boolean isValidSet = eventType == Notification.SET && isSetAdd(notification);
            final Object feature = notification.getFeature();
            if (feature instanceof EReference) {
                final EReference ref = (EReference)feature;
                if (!ref.isContainment()){
                    return false;
                }
                // Ignore new attribute group references
                final Object newVal = notification.getNewValue();
                if (newVal instanceof XSDAttributeGroupDefinition) {
                    final XSDAttributeGroupDefinition attrGrp = (XSDAttributeGroupDefinition)newVal;
                    if (attrGrp != attrGrp.getResolvedAttributeGroupDefinition()) {
                        return false;
                    }
                }
            }
            
            return ( (eventType == Notification.ADD) || (eventType == Notification.ADD_MANY) || isValidSet);
        }

    /**
     * Rules for Set as an add...
     * 1) oldValue != null && newValue == null 
     * 2) the sf is a containment EReference
     * 4) if feature is a list only rule 1 applies
     */
    private static boolean isSetAdd(final Notification notification){
        final Object feature = notification.getFeature();
        final Object oldVal = notification.getOldValue();
        final Object newVal = notification.getNewValue();
            
        if(feature instanceof EReference){
            final EReference ref = (EReference)feature;
            //If this is a down notification it is an add if the sf is containment
            //and the newVal != null and the oldVal == null
            return (ref.isContainment() && newVal != null && oldVal == null);
        }else if(feature instanceof List){
            //is and add if feature is a list and newVal != null && oldVal == null
            return (newVal != null && oldVal == null);
        }
            
        return false;
    }

    /**
     * Indicates if the notifier state has changed. State will not have changed when setting a feature to
     * it's present value.
     * @param theNotification the notification event
     * @return <code>true</code> if the notifier state has changed; <code>false</code> otherwise.
     */
    public static boolean isChanged(Notification theNotification) {
        return !theNotification.isTouch();
    }

    /**
     * Indicates if the event notifier is an {@link org.eclipse.emf.ecore.EObject}.
     * @param theNotification the event being processed
     * @return <code>true</code> if notifier is an <code>EObject</code>; <code>false</code> otherwise.
     */
    public static boolean isEObjectNotifier(Notification theNotification) {
        return (getEObject(theNotification) != null);
    }
    
    /**
     * Indicates if the event notifier is an {@link org.eclipse.emf.ecore.resource.Resource}.
     * @param theNotification the event being processed
     * @return <code>true</code> if notifier is an <code>Resource</code>; <code>false</code> otherwise.
     */
    public static boolean isResourceNotifier(Notification theNotification) {
        Object obj = getNotificationHelper().getNotifier(theNotification);
        if( obj instanceof Resource )
            return true;
            
        return false;
    }

    /**
     * Indicates if the given feature was changed by this notification.
     * @param theNotification the notification event
     * @param theFeatureId the feature model identifier being checked
     * @return <code>true</code> if the given feature has changed; <code>false</code> otherwise.
     */
    public static boolean isFeatureChanged(Notification theNotification, int theFeatureId) {
        return isFeatureChanged(theNotification, new int[] { theFeatureId });
    }

    /**
     * Indicates if at least on of the given features were changed by this notification.
     * @param theNotification the notification event
     * @param theFeatureIds the feature model identifiers being checked
     * @return <code>true</code> if at least one feature has changed; <code>false</code> otherwise.
     */
    public static boolean isFeatureChanged(Notification theNotification, int[] theFeatureIds) {
        boolean result = false;
        Object obj = theNotification.getFeature();

        if (obj instanceof EStructuralFeature) {
            EStructuralFeature feature = (EStructuralFeature)obj;

            for (int i = 0; i < theFeatureIds.length; i++) {
                if ((feature.eClass().getClassifierID() == theFeatureIds[i]) && isChanged(theNotification)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Indicates if a value of a list-based feature of the notifier has been moved.
     * @param theNotification the notification event
     * @return <code>true</code> if a value has been moved; <code>false</code> otherwise.
     */
    public static boolean isMoved(Notification theNotification) {
        return (theNotification.getEventType() == Notification.MOVE);
    }

    /**
     * Indicates if muliple values of a list-based feature of the notifier have either been added or
     * removed.
     * @param theNotification the notification event
     * @return <code>true</code> if multiple values have changed; <code>false</code> otherwise.
     */
    public static boolean isMultipleChanges(Notification theNotification) {
        return (
            (theNotification.getEventType() == Notification.ADD_MANY)
                || (theNotification.getEventType() == Notification.REMOVE_MANY));
    }

    /**
     * Indicates if a list-based feature of the notifier has been removed from.
     * @param theNotification the notification event
     * @return <code>true</code> if a removal has occurred; <code>false</code> otherwise.
     */
    public static boolean isRemoved(Notification theNotification) {
        final int eventType = theNotification.getEventType();

        if ( (eventType == Notification.REMOVE) || (eventType == Notification.REMOVE_MANY) || isSetRemove(theNotification) ){
            final Object feature = theNotification.getFeature();
            if(feature instanceof EReference && !((EReference)feature).isContainment() ){
                return false;
            }
            
            //Feature must be containment eReference
            final Object removed = theNotification.getOldValue();
            final Object parent = theNotification.getNotifier();
            
            //If the parent is no longer in a resource (it has also been removed) return false
            //This replaces the notifier not null parent check logic that was incorrect
            if(parent instanceof EObject){
                if( ((EObject)parent).eResource() == null){
                    return false;
                }
            }
            
            boolean isRemoved = true;
            
            if (removed instanceof EObject) {
                isRemoved = isRemoved(parent,(EObject)removed);
            } else if (removed instanceof List) {
                //Iterate over each removed object and ensure it has really been removed from it's parent
                final Iterator removedObjs = ((List)removed).iterator();
                while(removedObjs.hasNext() ){
                    final Object removedObj = removedObjs.next();
                    // If any are not removed, returns false
                    if(removedObj instanceof EObject){
                        if( !isRemoved(parent,(EObject)removedObj)) {
                            isRemoved = false;
                            break;
                        }
                    } else {
                        isRemoved = false;
                        break;
                    }
                }
            } else {
                isRemoved = false;
            }
            
            return isRemoved;
        }            

        return false;
    }
    
    /**
     * Private method for isRemoved check
     * @param parent the parent object
     * @param removedObj the removed Object
     * @return <code>true</code> if a removal has occurred; <code>false</code> otherwise.
     */
    private static boolean isRemoved(Object parent, EObject removedObj) {
        boolean isRemoved = true;
        if( parent instanceof EObject ) {
            //If the eObject has not yet been removed from parent return false
            isRemoved = !((EObject)parent).eContents().contains(removedObj);
        } else if(parent instanceof Resource){
            //If the eObject is a root, but has not been removed from the resource, return false
            isRemoved = !((Resource)parent).getContents().contains(removedObj);
        }
        return isRemoved;
    }
    
    /**
     * Rules for Set as an remove...
     * 1) oldValue == null && newValue != null 
     * 2) the sf is a containment EReference
     * 4) if feature is a list only rule 1 applies
     */        
    private static boolean isSetRemove(final Notification notification){
        final Object feature = notification.getFeature();
        final Object oldVal = notification.getOldValue();
        final Object newVal = notification.getNewValue();
            
        if(feature instanceof EReference){
            final EReference ref = (EReference)feature;
            //it is an remove if the sf is containment
            //and the newVal == null and the oldVal != null
            return (ref.isContainment() && newVal == null && oldVal != null);
        }else if(feature instanceof List){
            //is a remove if feature is a list and newVal == null && oldVal != null
            return (newVal == null && oldVal != null);
        }
            
        return false;
    }

    /**
     * Gets a string representation of the properties of the given <code>Notification</code>.
     * @param theNotification the notification being processed
     * @return the string representation
     */
    public static String paramString(Notification theNotification) {
            String notifierClass = (getNotificationHelper().getNotifier(theNotification) == null) ? "null" //$NON-NLS-1$
    : getNotificationHelper().getNotifier(theNotification).getClass().getName();

        return new StringBuffer().append("Notification:\n  event type=").append(theNotification.getEventType()) //$NON-NLS-1$
        .append("\n  feature=").append(theNotification.getFeature()) //$NON-NLS-1$
        .append("\n  notifier=").append(getNotificationHelper().getNotifier(theNotification)) //$NON-NLS-1$
        .append("\n  notifier class=").append(notifierClass) //$NON-NLS-1$
        .append("\n  position=").append(theNotification.getPosition()) //$NON-NLS-1$
        .append("\n  reset=").append(theNotification.isReset()) //$NON-NLS-1$
        .append("\n  touch=").append(theNotification.isTouch()) //$NON-NLS-1$
        .append("\n  isAdded=" + isAdded(theNotification)) //$NON-NLS-1$
        .append("\n  isChanged=").append(isChanged(theNotification)) //$NON-NLS-1$
        .append("\n  isEObjectNotifier=").append(isEObjectNotifier(theNotification)) //$NON-NLS-1$
        .append("\n  isMoved=").append(isMoved(theNotification)) //$NON-NLS-1$
        .append("\n  isMultipleChanges=").append(isMultipleChanges(theNotification)) //$NON-NLS-1$
        .append("\n  isRemoved=").append(isRemoved(theNotification)) //$NON-NLS-1$
        .append("\n  getAddedChildren=").append(getAddedChildrenPrintString(theNotification)) //$NON-NLS-1$
        .append("\n  getRemovedChildren=").append(getRemovedChildrenPrintString(theNotification)) //$NON-NLS-1$
        .toString();
    }
    
    private static String getAddedChildrenPrintString(Notification notification) {
        if( getAddedChildren(notification) == null || getAddedChildren(notification).length == 0 )
            return "EMPTY"; //$NON-NLS-1$
        
        StringBuffer returnString = new StringBuffer().append(" "); //$NON-NLS-1$
        
        for( int i=0; i<getAddedChildren(notification).length; i++ ) {
            returnString.append("\n     child =").append(getAddedChildren(notification)[i]); //$NON-NLS-1$
        }
        
        
        return returnString.toString();
    }
    
    
    private static String getRemovedChildrenPrintString(Notification notification) {
        if( getRemovedChildren(notification) == null || getRemovedChildren(notification).length == 0 )
            return "EMPTY"; //$NON-NLS-1$
        
        StringBuffer returnString = new StringBuffer().append(" "); //$NON-NLS-1$
        
        for( int i=0; i<getRemovedChildren(notification).length; i++ ) {
            returnString.append("\n     child =").append(getRemovedChildren(notification)[i]); //$NON-NLS-1$
        }
        
        
        return returnString.toString();
    }
    
    public static boolean addedChildrenParentIsNotifier(Notification notification) {
        EObject[] addedChildren = getAddedChildren(notification);
        if( addedChildren == null || addedChildren.length == 0 )
            return false;
        
        //process the new children against the notifier
        boolean notifierIsParent = true;
        Object notifier = getNotificationHelper().getNotifier(notification);

        int nObjects = addedChildren.length;
        for(int i=0; i<nObjects; i++) {
            EObject childsParent = addedChildren[i].eContainer();
            if(childsParent!=null && childsParent.equals(notifier)) {
                //allOK
            } else {
                notifierIsParent = false;
                break;
            }
        }
        
        return notifierIsParent;
    }

    /**
     * @return
     */
    public static INotificationHelper getNotificationHelper() {
        return notificationHelper;
    }

    /**
     * @param helper
     */
    public static void setNotificationHelper(INotificationHelper helper) {
        notificationHelper = helper;
    }
    
//    /**
//     * When dealing with a set notification it is possible for the notifier to be the child and
//     * the new / old value to be the parent.  This method will return true if the notification's 
//     * notifier is the parent of the relationship.
//     * 
//     * Rules for isDown..
//     * if notifier is EObject
//     *      1) Feature is eRef and isContainment == true
//     *      2) Feature is a list
//     * if notifier is Resource
//     *      1) the newValue or oldValue is an instanceof EObject
//     * if notifier is a resource set 
//     *      1) the newValue or oldValue is an instanceof Resource
//     */
//    private static boolean isDown(final Notification notification){
//        final Object potentialParent = notification.getNotifier();
//        final Object feature = notification.getFeature();
//        if(potentialParent instanceof EObject){
//            if(feature instanceof EReference){
//                return ((EReference)feature).isContainment();
//            }else if(feature instanceof List){
//                return true;
//            }
//            
//            return false;
//        }else if(potentialParent instanceof Resource){
//            return (notification.getOldValue() instanceof EObject || notification.getNewValue() instanceof EObject);
//        }else if(potentialParent instanceof ResourceSet){
//            return (notification.getOldValue() instanceof Resource || notification.getNewValue() instanceof Resource);
//        }
//        
//        return true;
//    }
//
//
}
