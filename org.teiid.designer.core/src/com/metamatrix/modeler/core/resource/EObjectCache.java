/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.resource;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.id.ObjectID;

/**
 * EObjectCache - manager of maps containing EObject instances for models
 * in the workspace.  The maps are keyed on the UUID associated with the EObject.
 * Models not found in the "Model Container" cannot be found through the
 * ObjectManager.
 */
public interface EObjectCache {

    /**
     * Explicitly remove all of the objects from the cache.
     */
    void clear();

    /**
     * Returns the number of EObject instances contained within the cache
     */
    int size();

    /**
     * Returns an array of EObject instances contained within this cache.  
     */
    EObject[] values();

    /**
     * Returns the value to which this cache maps the specified key.  Returns
     * <tt>null</tt> if the map contains no mapping for this key.  
     * @param key key whose associated value is to be returned.
     * @return the value to which this cache maps the specified key, or
     *         <tt>null</tt> if the map contains no mapping for this key.
     * @see #containsKey(Object)
     */
    EObject get(ObjectID key);

    /**
     * Returns <tt>true</tt> if this cache contains a mapping for the specified key.  
     * @param key key whose presence in this cache is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified key.
     */
    boolean containsKey(ObjectID key);

    /**
     * Returns <tt>true</tt> if this cache maps one or more keys to the specified value.
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this cache maps one or more keys to the
     *         specified value.
     */
    boolean containsValue(EObject value);

    /**
     * Add the specified EObject to the cache using its ObjectID as the key.
     * If the cache previously contained a mapping for this ObjectID, the old 
     * value is replaced by the specified value.
     * @param value EObject to be added to the cache using its ObjectID as the key.
     * @param recurse if true, all EObject instances owned by this EObject will
     *        also be added to the cache.
     */
    void add(EObject value, boolean recurse);

    /**
     * Add the specified array of EObjects to the cache using their associated 
     * ObjectIDs as the keys. If the cache previously contained mappings for 
     * for any of the ObjectIDs, the old value is replaced by the specified value.
     * @param values array of EObject instances to be added to the cache 
     *        using their ObjectIDs as the keys.
     * @param recurse if true, all EObject instances owned by these EObjects will
     *        also be added to the cache.
     */
    void add(EObject[] values, boolean recurse);
    
    /**
     * Remove from the cache the specified EObject (if it exists).
     * @param value EObject to be removed from the cache.
     * @param recurse if true, all EObject instances owned by this EObject will
     *        also be removed from the cache.
     */
    void remove(EObject value, boolean recurse);
    
    /**
     * Remove from the cache the EObject instance (if it exists) having the specified UUID.
     * @param key UUID of the EObject to be removed from the cache.
     * @param recurse if true, all EObject instances owned by the resultant EObject will
     *        also be removed from the cache.
     */
    void remove(ObjectID key, boolean recurse);

    /**
     * Remove from the cache the array of EObject instances (if they exists). 
     * @param values array of EObject instances to be removed from the cache 
     * @param recurse if true, all EObject instances owned by these EObjects will
     *        also be removed from the cache.
     */
    void remove(EObject[] values, boolean recurse);

    /**
     * Remove from the cache the EObject instances (if it exists) having UUIDs found in the 
     * specified array. 
     * @param keys array of UUIDs for the EObjects to be removed from the cache.
     * @param recurse if true, all EObject instances owned by these resultant EObjects will
     *        also be removed from the cache.
     */
    void remove(ObjectID[] keys, boolean recurse);

}
