/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.favorites;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.internal.ui.IModelerCacheListener;
import com.metamatrix.modeler.internal.ui.ModelerCacheEvent;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;


/** 
 * @since 4.2
 */
public final class EObjectModelerCache extends AbstractSet
                                       implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** The delegate cache. */
    private HashSet cache;
    
    /** ModelerCacheEventManager for notifications and resource changes. */
    private ModelerCacheEventManager eventMgr; 
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs an <code>EObjectModelerCache</code>.
     */
    public EObjectModelerCache() {
        this.cache = new HashSet();
        this.eventMgr = new ModelerCacheEventManager(this);
        
        // hook up listener
        ModelUtilities.addNotifyChangedListener(this.eventMgr);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this.eventMgr);
        
        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, this.eventMgr);
        } catch (EventSourceException e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * @see java.util.Collection#add(java.lang.Object)
     * @throws IllegalArgumentException if input is <code>null</code> or not an {@link EObject}
     * @since 4.2
     */
    @Override
    public boolean add(Object theEObject) {
        ArgCheck.isNotNull(theEObject);
        ArgCheck.isInstanceOf(EObject.class, theEObject);
        
        boolean result = this.cache.add(theEObject);
        
        if (result) {
            this.eventMgr.fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.ADD, theEObject));
        }
        
        return result;
    }
    
    /** 
     * @see java.util.Set#addAll(java.util.Collection)
     * @throws IllegalArgumentException if the collection contains an element that is not an {@link EObject}
     * @since 4.2
     */
    @Override
    public boolean addAll(Collection theEObjects) {
        ArgCheck.isNotNull(theEObjects);

        boolean result = false;
        
        if (!theEObjects.isEmpty()) {
            Collection addedObjs = new ArrayList(theEObjects.size());
            Iterator itr = theEObjects.iterator();
            
            while (itr.hasNext()) {
                Object obj = itr.next();
                ArgCheck.isInstanceOf(EObject.class, obj);
                
                if (this.cache.add(obj)) {
                    addedObjs.add(obj);
                    
                    if (!result) {
                        result = true;
                    }
                }
            }
            
            if (result) {
                this.eventMgr.fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.ADD, addedObjs));
            }
        }
        
        return result;
    }
    
    /**
     * Adds the specified listener to the collection of listeners receiving {@link ModelerCacheEvent}s. Listeners
     * already registered will not be added again.
     * @param theListener the listener being added
     * @since 4.2
     */
    public void addCacheListener(IModelerCacheListener theListener) {
        this.eventMgr.addListener(theListener);
    }

    /** 
     * @see java.util.Collection#clear()
     * @since 4.2
     */
    @Override
    public void clear() {
        if (!isEmpty()) {
            this.cache.clear();
            this.eventMgr.fireCacheEvent(ModelerCacheEvent.CLEAR_CACHE_EVENT);
        }
    }

    /** 
     * @see java.util.Collection#contains(java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean contains(Object theEObject) {
        return this.cache.contains(theEObject);
    }
    
    /**
     * Gets all cache items that are descendants of the specified object. 
     * @param theAncestor the object whose descendants are being requested
     * @return the descendants or an empty collection
     * @since 4.2
     */
    public Collection getCachedDescendants(EObject theAncestor) {
        Set result = Collections.EMPTY_SET;
        
        // only search if cache is not empty
        if (!isEmpty()) {
            List kids = theAncestor.eContents();
            
            // only look at cache if object has children
            if ((kids != null) && !kids.isEmpty()) {
                for (int size = kids.size(), i = 0; i < size; i++) {
                    EObject kid = (EObject)kids.get(i);
                    
                    // first see if kid is in cache
                    if (contains(kid)) {
                        // add to list
                        if (result.isEmpty()) {
                            result = new HashSet();
                        }
                        
                        result.add(kid);
                    } 
                    
                    // now see if any descendants in cache
                    Collection temp = getCachedDescendants(kid);
                    
                    if (!temp.isEmpty()) {
                        if (result.isEmpty()) {
                            result = new HashSet();
                        }
                        
                        result.addAll(temp);
                    }
                }
            }
        }
        
        return result;
    }
    
    /** 
     * @see java.util.Collection#isEmpty()
     * @since 4.2
     */
    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    /** 
     * @see java.util.Collection#iterator()
     * @since 4.2
     */
    @Override
    public Iterator iterator() {
        return this.cache.iterator();
    }
    
    /** 
     * @see java.util.Collection#remove(java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean remove(Object theEObject) {
        boolean result = this.cache.remove(theEObject);
        
        if (result) {
            this.eventMgr.fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.REMOVE, theEObject));
        }
        
        return result;
    }
    
    /** 
     * @see java.util.AbstractSet#removeAll(java.util.Collection)
     * @since 4.2
     */
    @Override
    public boolean removeAll(Collection theEObjects) {
        ArgCheck.isNotNull(theEObjects);

        boolean result = false;
        
        if (!theEObjects.isEmpty()) {
            Collection removedObjs = new ArrayList(theEObjects.size());
            Iterator itr = theEObjects.iterator();
            
            while (itr.hasNext()) {
                Object obj = itr.next();
                
                if (this.cache.remove(obj)) {
                    removedObjs.add(obj);
                    
                    if (!result) {
                        result = true;
                    }
                }
            }
            
            if (result) {
                this.eventMgr.fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.REMOVE, removedObjs));
            }
        }
        
        return result;
    }
    
    /**
     * Removes the specified listener from the collection of listeners receiving {@link ModelerCacheEvent}s. 
     * @param theListener the listener being removed
     * @since 4.2
     */
    public void removeCacheListener(IModelerCacheListener theListener) {
        this.eventMgr.removeListener(theListener);
    }

    /** 
     * @see java.util.Collection#retainAll(java.util.Collection)
     * @since 4.2
     */
    @Override
    public boolean retainAll(Collection theEObjects) {
        boolean result = false;
        
        if (!isEmpty()) {
            Collection removedObjs = new ArrayList();
            Iterator itr = iterator();
    
            while (itr.hasNext()) {
                Object obj = itr.next();

                if (!theEObjects.contains(obj)) {
                    itr.remove();
                    removedObjs.add(obj);
                    result = true;
                }
            }
            
            if (result) {
                this.eventMgr.fireCacheEvent(new ModelerCacheEvent(ModelerCacheEvent.REMOVE, removedObjs));
            }
        }
        
        return result;
    }

    /** 
     * @see java.util.Collection#size()
     * @since 4.2
     */
    @Override
    public int size() {
        return this.cache.size();
    }

    /** 
     * @see java.util.Collection#toArray()
     * @since 4.2
     */
    @Override
    public Object[] toArray() {
        return this.cache.toArray();
    }

    /** 
     * @see java.util.Collection#toArray(java.lang.Object[])
     * @since 4.2
     */
    @Override
    public Object[] toArray(Object[] theEObjects) {
        return this.cache.toArray(theEObjects);
    }

}
