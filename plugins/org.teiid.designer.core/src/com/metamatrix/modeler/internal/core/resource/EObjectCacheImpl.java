/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.id.ObjectID;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.resource.EObjectCache;


/**
 * EObjectCacheImpl - manager of maps containing EObject instances for resources
 * in a resource set.  The maps are keyed on the UUID associated with the EObject.
 * @since 4.3
 */
public class EObjectCacheImpl implements EObjectCache {

    private static final int MAX_MAP_SIZE = 1500;
    private static final int MAP_CONSOLIDATION_SIZE = 300;

    private final Collection mapOfMaps;
    private Map currentMap;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public EObjectCacheImpl() {
        this.currentMap = createMap();
        this.mapOfMaps  = new ArrayList();
        this.mapOfMaps.add(this.currentMap);
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#add(org.eclipse.emf.ecore.EObject[], boolean)
     * @since 4.3
     */
    public void add(final EObject[] values,
                    final boolean recurse) {
        for (int i = 0; i != values.length; ++i) {
            add(values[i],recurse);
        }
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#add(org.eclipse.emf.ecore.EObject, boolean)
     * @since 4.3
     */
    public void add(final EObject value,
                    final boolean recurse) {
        CoreArgCheck.isNotNull(value);

        final Object key = getCacheKey(value);

        // If the manager already has an EObject for this key then remove it.
        // A new EObject instance may have been instantiated due to reloading
        // a resource so we want this instance in the maps now.
        final Map eObjMap = findMapForKey(key);
        if (eObjMap != null) {
            eObjMap.remove(key);
        }

        // Add the new value to the current map
        if (this.currentMap.size() < MAX_MAP_SIZE) {
            this.currentMap.put(key, value);
        } else {
            boolean newMap = true;

            // Add to existing map if one has room
            for (final Iterator iter = mapOfMaps.iterator(); iter.hasNext();) {
                final Map next = (Map)iter.next();
                if (next != this.currentMap && next.size() < MAX_MAP_SIZE) {
                    this.currentMap = next;
                    this.currentMap.put(key, value);
                    newMap = false;
                    break;
                }
            }

            // need to create a new map
            if (newMap) {
                this.currentMap = createMap();
                mapOfMaps.add(this.currentMap);
                this.currentMap.put(key, value);
            }
        }

        // Continue the add operation which will add this EObject
        // instance to the cache along with the contents of this
        // EObject by recursively calling add(EObject,boolean) on
        // the owned EObject instances.
        if (recurse) {
            for (final Iterator iter = value.eContents().iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if (obj instanceof EObject) {
                    add((EObject)obj, recurse);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#clear()
     * @since 4.3
     */
    public void clear() {
        try {
            for (final Iterator iter = mapOfMaps.iterator(); iter.hasNext();) {
                Map next = (Map)iter.next();
                next.clear();
                iter.remove();
            }
            // Reset the state back to when it was first constructed
            this.currentMap = createMap();
            this.mapOfMaps.add(this.currentMap);

        } finally {
            runGC();
        }

    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#containsKey(com.metamatrix.core.id.ObjectID)
     * @since 4.3
     */
    public boolean containsKey(final ObjectID key) {
        if (this.currentMap.containsKey(key)) {
            return true;
        }
        for (final Iterator iter = mapOfMaps.iterator(); iter.hasNext();) {
            Map next = (Map)iter.next();
            if (next != this.currentMap && next.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#containsValue(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean containsValue(final EObject value) {
        if (this.currentMap.containsValue(value)) {
            return true;
        }
        for (final Iterator iter = mapOfMaps.iterator(); iter.hasNext();) {
            Map next = (Map)iter.next();
            if (next != this.currentMap && next.containsValue(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#get(com.metamatrix.core.id.ObjectID)
     * @since 4.3
     */
    public EObject get(final ObjectID key) {
        if (key == null) {
            return null;
        }
        final Map map = findMapForKey(key);
        if (map != null) {
            return (EObject)map.get(key);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#remove(org.eclipse.emf.ecore.EObject, boolean)
     * @since 4.3
     */
    public void remove(final EObject value,
                       final boolean recurse) {
        CoreArgCheck.isNotNull(value);

        final Object key = getCacheKey(value);

        final Map map = findMapForValue(value);
        if (map != null) {
            map.remove(key);

            // See if we should consolidate this map
            if(map != this.currentMap && map.size() < MAP_CONSOLIDATION_SIZE) {
                consolidateMap(map);
            }
        }

        // Remove the entire tree
        if (recurse) {
            for (final Iterator iter = value.eContents().iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if (obj instanceof EObject) {
                    remove((EObject)obj, recurse);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#remove(org.eclipse.emf.ecore.EObject[], boolean)
     * @since 4.3
     */
    public void remove(final EObject[] values,
                       final boolean recurse) {
        for (int i = 0; i != values.length; ++i) {
            remove(values[i],recurse);
        }
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#remove(com.metamatrix.core.id.ObjectID, boolean)
     * @since 4.3
     */
    public void remove(final ObjectID key,
                       final boolean recurse) {
        CoreArgCheck.isNotNull(key);

        EObject value = null;

        final Map map = findMapForKey(key);
        if (map != null) {
            value = (EObject)map.remove(key);

            // See if we should consolidate this map
            if(map != this.currentMap && map.size() < MAP_CONSOLIDATION_SIZE) {
                consolidateMap(map);
            }
        }

        // Remove the entire tree
        if (recurse && value != null) {
            for (final Iterator iter = value.eContents().iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if (obj instanceof EObject) {
                    remove((EObject)obj, recurse);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#remove(com.metamatrix.core.id.ObjectID[], boolean)
     * @since 4.3
     */
    public void remove(final ObjectID[] keys,
                       final boolean recurse) {
        for (int i = 0; i != keys.length; ++i) {
            remove(keys[i],recurse);
        }
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#size()
     * @since 4.3
     */
    public int size() {
        int size = 0;
        for (final Iterator iter = mapOfMaps.iterator(); iter.hasNext();) {
            Map next = (Map)iter.next();
            size += next.size();
        }
        return size;
    }

    /**
     * @see com.metamatrix.modeler.core.resource.EObjectCache#values()
     * @since 4.3
     */
    public EObject[] values() {
        final List values = new ArrayList(size());
        for (final Iterator iter = mapOfMaps.iterator(); iter.hasNext();) {
            Map next = (Map)iter.next();
            values.addAll(next.values());
        }
        return (EObject[])values.toArray(new EObject[values.size()]);
    }

    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================

    protected Object getCacheKey(final EObject value) {
        CoreArgCheck.isNotNull(value);
        return ModelerCore.getObjectId(value);
    }

    protected void consolidateMap(final Map map) {
        if(map == this.currentMap) {
            return;
        }

        try {
            // Try currentMap first
            final int size = map.size();
            if (this.currentMap.size() + size < MAX_MAP_SIZE) {
                this.currentMap.putAll(map);
                this.mapOfMaps.remove(map);
                return;
            }

            // Check other maps in the list
            for (final Iterator iter = mapOfMaps.iterator(); iter.hasNext();) {
                final Map next = (Map)iter.next();
                if ((next != this.currentMap) && (next.size() + size < MAX_MAP_SIZE)) {
                    next.putAll(map);
                    this.mapOfMaps.remove(map);
                    return;
                }
            }

        } finally {
            runGC();
        }

    }

    protected Map findMapForValue(final EObject eObject){
        final Object id = ModelerCore.getObjectId(eObject);
        return findMapForKey(id);
    }

    protected Map findMapForKey(final Object id) {
        if(id == null) {
            return null;
        }

        //check current map first;
        if(this.currentMap.containsKey(id) ) {
            return this.currentMap;
        }

        final Iterator maps = mapOfMaps.iterator();
        while(maps.hasNext() ) {
            final Map next = (Map)maps.next();
            if(next != this.currentMap && next.containsKey(id) ) {
                return next;
            }
        }

        return null;
    }

    /**
     * @return Returns the mapOfMaps.
     * @since 4.3
     */
    protected Collection getMapOfMaps() {
        return this.mapOfMaps;
    }

    protected Map createMap() {
        return new HashMap();
    }

    protected void runGC() {
//        System.gc();
//        Thread.yield();
    }
}
