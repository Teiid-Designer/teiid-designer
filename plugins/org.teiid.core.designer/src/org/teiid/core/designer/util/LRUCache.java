/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.core.designer.util;


import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * @since 8.0
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default amount of space in the cache
     */
    protected static final int DEFAULT_SPACELIMIT = 100;
    
    protected int maxSize;
    
    /**
     * Creates a new cache.  Size of cache is defined by 
     * <code>DEFAULT_SPACELIMIT</code>.
     */
    public LRUCache() {
        this(DEFAULT_SPACELIMIT);
    }
    
    public LRUCache(int maxSize) {
        super(16, .75f, true);
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return size() > maxSize;
    }

    public int getSpaceLimit() {
        return maxSize;
    }

}
