/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;


/** 
 * @since 4.2
 */
public class CustomDiagramNotificationHelper {
    private static final int IGNORE = 0;
    private static final int ADDED = 1;
    private static final int REMOVED = 2;
    private static final int CHANGED = 3;
    
    private Notification rootNotification;
    private Diagram customDiagram;
//    private Object helperSource;
    
    Collection addNotifications;
    Collection removeNotifications;
    Collection changedNotifications;
    Collection movedEObjects;
    HashMap undoAddedObjects;
    
    /** 
     * 
     * @since 4.2
     */
    public CustomDiagramNotificationHelper(Notification notification, Diagram customDiagram, Object source ) {
        super();
        this.rootNotification = notification;
        this.customDiagram = customDiagram;
//        helperSource = source;
        init();
        processNotifications();
    }
    
    private void init() {
        addNotifications = new ArrayList();
        removeNotifications = new ArrayList();
        changedNotifications = new ArrayList();
        undoAddedObjects = new HashMap();
    }
    
    private void processNotifications() {
        // Let's check if the notification is sourced or not
        if (rootNotification instanceof SourcedNotification) {
            Collection notifications = ((SourcedNotification)rootNotification).getNotifications();
            Iterator iter = notifications.iterator();
            while (iter.hasNext()) {
                // Process notification so it get's in a list or ignored.
                processNotification((Notification)iter.next());
            }
        } else {
            // Process notification so it get's in a list or ignored.
            processNotification(rootNotification);
        }
        
        processForMoves();
    }
    
    private void processNotification(Notification notification) {
        switch( getNotificationType(notification) ) {
            case ADDED: {
                if(!preProcessAddNofication(notification))
                    addNotifications.add(notification);
            } break;
            
            case REMOVED: {
                if( !preProcessRemoveNofication(notification))
                    removeNotifications.add(notification);
            } break;
            
            case CHANGED: {
                changedNotifications.add(notification);
            } break;
            
            case IGNORE:
            default: {
                
            }
        }
        
    }
    
    private int getNotificationType(Notification notification) {
        if (NotificationUtilities.isAdded(notification)) {
            return ADDED;
        } else if (NotificationUtilities.isRemoved(notification)) {
            return REMOVED;
        } else if (NotificationUtilities.isChanged(notification)) {
            return CHANGED;
        }
        
        return IGNORE;
    }
    
    
    /*
     * This method looks at add and remove lists, and when there is a match, we assume that the object has been moved and not 
     * added or deleted. In this case we remove the notifications from add and remove lists, then add the eObject to the move list.
     */
    private void processForMoves() {
        if( !addNotifications.isEmpty() && !removeNotifications.isEmpty() ) {
            
            Collection tempAdds = new ArrayList(addNotifications);
            Collection tempRemoves = new ArrayList(removeNotifications);
            
            movedEObjects = new ArrayList();
            Notification removeNotification = null;
            Iterator iter = tempRemoves.iterator();
            // Now look at each notification, and see if there is a corresponding "Added" eObject;
            while( iter.hasNext() ) {
                removeNotification = (Notification)iter.next();
                Notification addedNotification = null;
                // If so, then let's treat this as a move.
                EObject[] oldChildren = NotificationUtilities.getRemovedChildren(removeNotification);
 
                for(int i=0; i<oldChildren.length; i++ ) {
                    addedNotification = getAddedNotification(oldChildren[i], tempAdds);
                    if( addedNotification != null )
                        break;
                }
                if( addedNotification != null ) {
                    removeNotifications.remove(removeNotification);
                    addNotifications.remove(addedNotification);
                    for(int i=0; i<oldChildren.length; i++ )
                        movedEObjects.add(oldChildren[i]);
                }
            }
        }
    }
    
    private Notification getAddedNotification(EObject eObj, Collection addList) {
        String targetID = ModelObjectUtilities.getUuid(eObj);
        Iterator iter = addList.iterator();
        Notification nextNotification = null;
        String newChildID = null;
        while( iter.hasNext() ) {
            nextNotification = (Notification)iter.next();
            EObject[] newChildren = NotificationUtilities.getAddedChildren(nextNotification);
            for(int i=0; i<newChildren.length; i++ ) {
                newChildID = ModelObjectUtilities.getUuid(newChildren[i]);
                if( targetID != null && targetID.equals(newChildID)) {
                    return nextNotification;
                }
            }
        }
        
        return null;
    }
    

    /**
     * This method looks at an add notification, checks to see if any of the added children also have
     * an accompanying diagram entity added that's diagram is this custom diagram. If so, we add these children to the undoAddNotifications list
     * so the are processed diffently. 
     * @param notification
     * @return true if added children are also in 
     * @since 4.2
     */
    private boolean preProcessAddNofication(Notification notification) {
        // Get the added children
        EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
        // See if any of them have a associationed "added Diagram Entity"
        for(int i=0; i<newChildren.length; i++ ) {
            if( newChildren[i] instanceof DiagramEntity)
                return true;
            else if( wasDiagramEntityAdded(newChildren[i])) {
                if( undoAddedObjects.get(newChildren[i]) == null )
                    undoAddedObjects.put(newChildren[i], "x"); //$NON-NLS-1$$
                return true;
            }
                
        }
        return false;
    }
    
    /**
     * This method looks at an add notification, checks to see if any of the added children also have
     * an accompanying diagram entity added that's diagram is this custom diagram. If so, we add these children to the undoAddNotifications list
     * so the are processed diffently. 
     * @param notification
     * @return true if added children are also in 
     * @since 4.2
     */
    private boolean preProcessRemoveNofication(Notification notification) {
        // Get the added children
        EObject[] oldChildren = NotificationUtilities.getRemovedChildren(notification);
        // See if any of them have a associationed "added Diagram Entity"
        for(int i=0; i<oldChildren.length; i++ ) {
            if( oldChildren[i] instanceof DiagramEntity)
                return true;
                
        }
        return false;
    }
    
    private boolean wasDiagramEntityAdded(EObject eObj) {
        String targetID = ModelObjectUtilities.getUuid(eObj);
        
        // Need to walk through the whole notification list
        if (rootNotification instanceof SourcedNotification) {
            Notification nextNotification = null;
            Collection notifications = ((SourcedNotification)rootNotification).getNotifications();
            Iterator iter = notifications.iterator();
            while (iter.hasNext()) {
                nextNotification = (Notification)iter.next();
                // Process notification so it get's in a list or ignored.
                if( getNotificationType(nextNotification) == ADDED ) {
                    EObject[] newChildren = NotificationUtilities.getAddedChildren(nextNotification);
                    
                    for(int i=0; i<newChildren.length; i++ ) {
                        if( newChildren[i] instanceof DiagramEntity ){
                            Diagram deDiagram = ((DiagramEntity)newChildren[i]).getDiagram();
                            if( deDiagram != null && deDiagram == customDiagram ) {
                                EObject modelObject = ((DiagramEntity)newChildren[i]).getModelObject();
                                if( modelObject != null && modelObject == eObj)
                                    return true;
                            }
                        }
                    }
                }
            }
        } else {
            // Process notification so it get's in a list or ignored.
            if( getNotificationType(rootNotification) == ADDED ) {
                EObject[] newChildren = NotificationUtilities.getAddedChildren(rootNotification);
                String newChildID = null;
                for(int i=0; i<newChildren.length; i++ ) {
                    if( newChildren[i] instanceof DiagramEntity ) {
                        EObject modelObject = ((DiagramEntity)newChildren[i]).getModelObject();
                        if( modelObject != null )
                            newChildID = ModelObjectUtilities.getUuid(newChildren[i]);
                    }

                    if( newChildID != null && targetID != null && targetID.equals(newChildID)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    public Collection getAddNotifications() {
        if( addNotifications == null || addNotifications.isEmpty() )
            return Collections.EMPTY_LIST;
        
        return this.addNotifications;
    }
    public Collection getChangedNotifications() {
        if( changedNotifications == null || changedNotifications.isEmpty() )
            return Collections.EMPTY_LIST;
        
        return this.changedNotifications;
    }
    public Collection getMovedEObjects() {
        if( movedEObjects == null || movedEObjects.isEmpty() )
            return Collections.EMPTY_LIST;
        
        return this.movedEObjects;
    }
    
    public Collection getRemoveNotifications() {
        if( removeNotifications == null || removeNotifications.isEmpty() )
            return Collections.EMPTY_LIST;
        
        return this.removeNotifications;
    }
    
    public Collection getUndoAddedEObjects() {
        if( undoAddedObjects == null || undoAddedObjects.isEmpty() )
            return Collections.EMPTY_LIST;
        
        return new ArrayList(this.undoAddedObjects.keySet());
    }
}
