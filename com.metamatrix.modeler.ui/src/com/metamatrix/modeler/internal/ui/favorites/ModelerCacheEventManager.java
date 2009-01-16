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

package com.metamatrix.modeler.internal.ui.favorites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;
import com.metamatrix.modeler.internal.ui.IModelerCacheListener;
import com.metamatrix.modeler.internal.ui.ModelerCacheEvent;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;

class ModelerCacheEventManager
    implements INotifyChangedListener, IResourceChangeListener, IResourceDeltaVisitor, EventObjectListener, UiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Collection of registered listeners of cache events. */
    private ListenerList listeners;

    /** The cache being managed. */
    private final EObjectModelerCache cache;

    /** Used to find the ModelResource of EObjects. */
    private final ModelEditor modelEditor = ModelerCore.getModelEditor();

    private boolean removeDeltaProcessed = false;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param theCache
     * @since 4.2
     */
    public ModelerCacheEventManager( EObjectModelerCache theCache ) {
        this.cache = theCache;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Adds the specified listener to the collection of listeners receiving {@link ModelerCacheEvent}s. Listeners already
     * registered will not be added again.
     * 
     * @param theListener the listener being added
     * @since 4.2
     */
    void addListener( IModelerCacheListener theListener ) {
        if (this.listeners == null) {
            this.listeners = new ListenerList(ListenerList.IDENTITY);
        }

        this.listeners.add(theListener);
    }

    /**
     * Notifies all registered {@link IModelerCacheListener}s of the specified event.
     * 
     * @param theEvent the event being processed
     * @since 4.2
     */
    void fireCacheEvent( final ModelerCacheEvent theEvent ) {
        if (this.listeners != null) {
            final Object[] cacheListeners = this.listeners.getListeners();

            for (int i = 0; i < cacheListeners.length; i++) {
                ((IModelerCacheListener)cacheListeners[i]).cacheChanged(theEvent);
            }
        }
    }

    /**
     * Finds the cache items affected by the specified <code>Notification</code>. Basically an item is affected if the
     * notification's EObject is an ancestor.
     * 
     * @param theNotification the notification being processed
     * @return the affected items or an empty collection
     * @since 4.2
     */
    private Collection getAffectedItems( Notification theNotification ) {
        if (theNotification instanceof SourcedNotification) {
            ArgCheck.isTrue(false, "Input should not be a SourcedNotification."); //$NON-NLS-1$
        }

        Collection result = Collections.EMPTY_LIST;
        Object obj = NotificationUtilities.getEObject(theNotification);

        if (obj != null) {
            if (this.cache.contains(obj)) {
                if (result.isEmpty()) {
                    result = new ArrayList();
                }

                result.add(obj);
            }

            // now see if any descendants are in the cache
            Collection descendants = this.cache.getCachedDescendants((EObject)obj);

            if (!descendants.isEmpty()) {
                if (result.isEmpty()) {
                    result = new ArrayList();
                }

                result.addAll(descendants);
            }
        }

        return result;
    }

    /**
     * Finds the cache items affected by the specified <code>Notification</code>. Basically an item is affected if the
     * notification's EObject is an ancestor.
     * 
     * @param theNotification the notification being processed
     * @return the affected items or an empty collection
     * @since 4.2
     */
    public Collection getAffectedItems( ModelResource modelResource ) {
        Collection result = Collections.EMPTY_LIST;

        if (!this.cache.isEmpty() && modelResource != null) {
            result = new ArrayList();
            Iterator iter = this.cache.iterator();
            ModelResource mr = null;
            EObject eObj = null;
            while (iter.hasNext()) {
                eObj = (EObject)iter.next();
                mr = modelEditor.findModelResource(eObj);
                if (modelResource != null && modelResource.equals(mr)) result.add(eObj);
            }
        }

        return result;
    }

    /**
     * Finds the items in the cache that are stale and have no <code>ModelResource</code>
     * 
     * @return the stale items or an empty collection.
     * @since 4.2
     */
    public Collection getStaleItems() {
        Collection result = Collections.EMPTY_LIST;

        if (!this.cache.isEmpty()) {
            result = new ArrayList();
            Iterator iter = this.cache.iterator();
            ModelResource modelResource = null;
            EObject eObj = null;
            while (iter.hasNext()) {
                eObj = (EObject)iter.next();
                modelResource = modelEditor.findModelResource(eObj);
                if (modelResource == null) result.add(eObj);
            }
        }

        return result;
    }

    /**
     * Indicates if the specified <code>EObject</code> has been affected by a resource change event for the specified
     * <code>IResource</code>.
     * 
     * @param theEObject the object being checked
     * @param theResource the resource that was changed
     * @return <code>true</code>if affected; <code>false</code> otherwise.
     * @since 4.2
     */
    private boolean isAffected( EObject theEObject,
                                IResource theResource ) {
        boolean result = false;

        if (!this.cache.isEmpty()) {
            ModelResource modelResource = modelEditor.findModelResource(theEObject);

            if (modelResource == null) {
                result = true;
            } else {
                IResource model = modelResource.getResource();

                if (model != null) {
                    int type = theResource.getType();

                    if ((type == IResource.FILE) && model.equals(theResource)) {
                        result = true;
                    } else if (type == IResource.FOLDER) {
                        if (model.getFullPath().toString().startsWith(theResource.getFullPath().toString())) {
                            result = true;
                        }
                    } else if ((type == IResource.PROJECT) && model.getProject().equals(theResource)) {
                        result = true;
                    }
                }
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     * @since 4.2
     */
    public void notifyChanged( Notification theNotification ) {
        // don't care about adds
        if (!this.cache.isEmpty()) {
            if (theNotification instanceof SourcedNotification) {
                Collection notifications = ((SourcedNotification)theNotification).getNotifications();
                Notification nextNotification = null;
                Iterator iter = notifications.iterator();
                while (iter.hasNext()) {
                    nextNotification = (Notification)iter.next();
                    if (NotificationUtilities.isRemoved(nextNotification)) {
                        processRemove(nextNotification);
                    } else if (NotificationUtilities.isChanged(nextNotification)) {
                        processChange(nextNotification);
                    }
                }
            } else {
                if (NotificationUtilities.isRemoved(theNotification)) {
                    processRemove(theNotification);
                } else if (NotificationUtilities.isChanged(theNotification)) {
                    processChange(theNotification);
                }
            }
        }
    }

    /**
     * Sends out the appropriate events for a change.
     * 
     * @param theNotification the notification being processed
     * @since 4.2
     */
    private void processChange( Notification theNotification ) {
        Collection changedObjs = null;
        ModelerCacheEvent event = null;

        if (theNotification instanceof SourcedNotification) {
            Iterator itr = ((SourcedNotification)theNotification).getNotifications().iterator();

            while (itr.hasNext()) {
                Collection temp = getAffectedItems((Notification)itr.next());

                if (!temp.isEmpty()) {
                    if (changedObjs == null) {
                        changedObjs = new HashSet();
                    }

                    changedObjs.addAll(temp);
                }
            }
        } else {
            Collection temp = getAffectedItems(theNotification);

            if (!temp.isEmpty()) {
                changedObjs = new HashSet();
                changedObjs.addAll(temp);
            }
        }

        if (changedObjs != null) {
            Object obj = (changedObjs.size() == 1) ? changedObjs.iterator().next() : changedObjs;
            event = new ModelerCacheEvent(ModelerCacheEvent.CHANGE, obj);
            fireCacheEvent(event);
        }
    }

    /**
     * Sends out the appropriate events for a remove of an <code>EObject</code>.
     * 
     * @param theNotification the notification being processed
     * @since 4.2
     */
    private void processRemove( Notification theNotification ) {
        Collection removedObjs = null;
        ModelerCacheEvent event = null;

        if (theNotification instanceof SourcedNotification) {
            Iterator itr = ((SourcedNotification)theNotification).getNotifications().iterator();

            while (itr.hasNext()) {
                Collection temp = removeAffectedItems((Notification)itr.next());

                if (!temp.isEmpty()) {
                    if (removedObjs == null) {
                        removedObjs = new HashSet();
                    }

                    // add to collection for event
                    removedObjs.addAll(temp);
                }
            }
        } else {
            Collection temp = removeAffectedItems(theNotification);

            if (!temp.isEmpty()) {
                removedObjs = new HashSet();
                removedObjs.addAll(temp);
            }
        }

        if (removedObjs != null) {
            Object obj = (removedObjs.size() == 1) ? removedObjs.iterator().next() : removedObjs;
            event = new ModelerCacheEvent(ModelerCacheEvent.REMOVE, obj);
            fireCacheEvent(event);
        }
    }

    /**
     * Removes the cache items affected by the specified <code>IResource</code>. Basically an item is affected if the
     * <code>IResource</code> is an ancestor.
     * 
     * @param IResource the resource who is a potential ancestor
     * @return the r items or an empty collection
     * @since 4.2
     */
    private Collection removeAffectedItems( IResource theResource ) {
        List result = Collections.EMPTY_LIST;
        Iterator itr = this.cache.iterator();

        while (itr.hasNext()) {
            EObject obj = (EObject)itr.next();

            if (isAffected(obj, theResource)) {
                if (result.isEmpty()) {
                    result = new ArrayList();
                }

                // add to result
                result.add(obj);

                // remove from cache
                itr.remove();
            }
        }

        return result;
    }

    /**
     * Finds the cache items affected by the specified <code>Notification</code>. Basically an item is affected if the
     * notification's EObject is an ancestor.
     * 
     * @param theNotification the notification being processed
     * @return the affected items or an empty collection
     * @since 4.2
     */
    private Collection removeAffectedItems( Notification theNotification ) {
        if (theNotification instanceof SourcedNotification) {
            ArgCheck.isTrue(false, "Input should not be a SourcedNotification."); //$NON-NLS-1$
        }

        Collection result = Collections.EMPTY_LIST;
        EObject[] kids = NotificationUtilities.getRemovedChildren(theNotification);

        for (int i = 0; i < kids.length; i++) {
            if (this.cache.contains(kids[i])) {
                if (result.isEmpty()) {
                    result = new ArrayList();
                }

                // add to result
                result.add(kids[i]);

                // remove from cache
                this.cache.remove(kids[i]);
            }

            // now see if any descendants are in the cache
            Collection descendants = this.cache.getCachedDescendants(kids[i]);

            if (!descendants.isEmpty()) {
                if (result.isEmpty()) {
                    result = new ArrayList();
                }

                // add to result
                result.addAll(descendants);

                // remove from cache
                this.cache.removeAll(descendants);
            }
        }

        return result;
    }

    /**
     * Removes the specified listener from the collection of listeners receiving {@link ModelerCacheEvent}s.
     * 
     * @param theListener the listener being removed
     * @since 4.2
     */
    void removeListener( IModelerCacheListener theListener ) {
        if (this.listeners != null) {
            this.listeners.remove(theListener);

            if (this.listeners.isEmpty()) {
                this.listeners = null;
            }
        }
    }

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     * @since 4.2
     */
    public void resourceChanged( IResourceChangeEvent theEvent ) {
        if (!this.cache.isEmpty()) {
            IResource resource = theEvent.getResource();

            // don't process if resource not in model project
            if ((resource != null) && !ModelerCore.hasModelNature(resource.getProject())) {
                return;
            }

            if (ResourceChangeUtilities.isProjectClosing(theEvent)) {
                // project closing
                Collection removedObjs = removeAffectedItems(resource);

                // send REMOVE event if necessary
                if (!removedObjs.isEmpty()) {
                    Object obj = (removedObjs.size() > 1) ? removedObjs : removedObjs.iterator().next();
                    fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.REMOVE, obj));
                }
            } else if (ResourceChangeUtilities.isProjectRenamed(theEvent)) {
                // we don't currently allow projects to be renamed but added just in case in the future we do
                Collection changedObjs = removeAffectedItems(resource);

                // send CHANGE event if necessary
                if (!changedObjs.isEmpty()) {
                    Object obj = (changedObjs.size() > 1) ? changedObjs : changedObjs.iterator().next();
                    fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.CHANGE, obj));
                }
            } else if (ResourceChangeUtilities.isPreDelete(theEvent)) {
                // project being deleted
                IProject project = (IProject)theEvent.getResource();

                if (project.isOpen()) {
                    validateContents();
                }
            } else {
                if (theEvent.getType() == IResourceChangeEvent.POST_CHANGE) {
                    removeDeltaProcessed = false;
                    IResourceDelta delta = theEvent.getDelta();

                    if ((delta != null) && ResourceChangeUtilities.isRename(theEvent, delta.getAffectedChildren())) {
                        // isRename does not work
                    } else {
                        // not a rename so visit
                        try {
                            theEvent.getDelta().accept(this);
                        } catch (CoreException theException) {
                            Util.log(theException);
                        }
                    }
                }
            }
        }
    }

    /**
     * Iterates over the cache removing items that no longer exist. One event is fired containing all objects removed.
     * 
     * @since 4.2
     */
    private void validateContents() {
        List removedObjs = null;
        Iterator itr = this.cache.iterator();

        while (itr.hasNext()) {
            EObject obj = (EObject)itr.next();

            if (obj.eResource() == null) {
                if (removedObjs == null) {
                    removedObjs = new ArrayList();
                }

                // add to objects in event
                removedObjs.add(obj);

                // remove from cache
                itr.remove();
            }
        }

        // send REMOVE event if necessary
        if (removedObjs != null) {
            Object obj = (removedObjs.size() > 1) ? removedObjs : removedObjs.get(0);
            fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.REMOVE, obj));
        }
    }

    /**
     * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
     * @since 4.2
     */
    public boolean visit( IResourceDelta theDelta ) {
        boolean result = true;

        // System.out.println("VISIT:resource="+theDelta.getResource()+", added="+ResourceChangeUtilities.isAdded(theDelta)+", removed="+ResourceChangeUtilities.isRemoved(theDelta)+", changed="+ResourceChangeUtilities.isChanged(theDelta)+", content changed="+ResourceChangeUtilities.isContentChanged(theDelta)+", repaced="+
        // ResourceChangeUtilities.isReplaced(theDelta)+", moved from="+
        // ResourceChangeUtilities.isMovedFrom(theDelta)+", moved to="+ ResourceChangeUtilities.isMovedTo(theDelta));
        // if a remove and not a rename
        if (ResourceChangeUtilities.isRemoved(theDelta) && !ResourceChangeUtilities.isMovedTo(theDelta)
            && !this.removeDeltaProcessed) {

            // just process first remove delta
            this.removeDeltaProcessed = true;
            validateContents();

            // don't visit children
            result = false;
        } else if (ResourceChangeUtilities.isMovedFrom(theDelta)) {
            // this is the newly renamed resource. find it's descendants
            Collection objs = removeAffectedItems(theDelta.getResource());

            if (!objs.isEmpty()) {
                // should be a CHANGE event but defect in metadata causes the EObject model resource to be null in
                // getAffectedItems(IResource)
                // fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.CHANGE, objs));
                fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.REMOVE, objs));
            }
        }

        // check children recursively
        return result;
    }

    /**
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent( EventObject obj ) {
        ModelResourceEvent event = (ModelResourceEvent)obj;
        if (event.getType() == ModelResourceEvent.RELOADED) {
            // we need to remove all objects in the cache that don't have a model resource anymore or who's model
            // resource is the one on the event.
            ModelResource changedResource = event.getModelResource();
            // Collection objs = getStaleItems();
            //
            // if (!objs.isEmpty()) {
            // this.cache.removeAll(objs);
            // }

            Collection changedObjs = removeAffectedItems(changedResource.getResource());
            if (!changedObjs.isEmpty()) {
                fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.REMOVE, changedObjs));
            }

        }
    }

}
