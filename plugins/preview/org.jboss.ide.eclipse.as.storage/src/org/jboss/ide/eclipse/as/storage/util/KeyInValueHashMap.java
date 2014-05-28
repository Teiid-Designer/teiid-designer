/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.jboss.ide.eclipse.as.storage.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A {@link HashMap} which stores values that also reference their
 * own keys. More memory efficient than storing separate references
 * to the keys and values.
 * 
 * @param <K> 
 * @param <V> 
 * @since 8.0
 */
public class KeyInValueHashMap<K, V> extends AbstractMap<K, V> {

    /**
     * Adapter interface that clients of the class should implement
     * and pass to its constructor so that the key (K) can be derived
     * from the value (V).
     *
     * @param <K>
     * @param <V>
     */
    public interface KeyFromValueAdapter<K, V> {
       
        /**
         * Get the key from the value
         * 
         * @param value
         * 
         * @return key (K) from the value (V)
         */
        K getKey(V value);

    }
    
    private class EntryWrapper implements Map.Entry<K, V> {

        V value;
        
        /**
         * @param value
         */
        public EntryWrapper(V value) {
            this.value = value;
        }

        @Override
        public K getKey() {
            return (K) adapter.getKey(value);
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            EntryWrapper other = (EntryWrapper)obj;
            if (!getOuterType().equals(other.getOuterType())) return false;
            if (this.value == null) {
                if (other.value != null) return false;
            } else if (!this.value.equals(other.value)) return false;
            return true;
        }

        private KeyInValueHashMap getOuterType() {
            return KeyInValueHashMap.this;
        }
    }
    
    private Set<Map.Entry<K, V>> entrySet = new HashSet<Map.Entry<K, V>>();
    
    private KeyFromValueAdapter adapter;
    
    /**
     * Create a new instance
     *  
     * @param adapter that can convert from the value into the key
     */
    public KeyInValueHashMap(KeyFromValueAdapter adapter) {
        this.adapter = adapter;
    }
    
    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("Use add rather than put since the key is part of the value"); //$NON-NLS-1$
    }
    
    /**
     * Add a value to this map where its key will be derived
     * by the {@link KeyFromValueAdapter}
     * 
     * @param value
     * 
     * @return true if the value was added.
     */
    public boolean add(V value) {
        EntryWrapper entry = new EntryWrapper(value);
        return entrySet.add(entry);
    }
    
    /**
     * Remove a value from this map where the key will
     * be determined by the {@link KeyFromValueAdapter}
     * 
     * @param value
     * 
     * @return removed value or null.
     */
    @Override
    public V remove(Object value) {
        EntryWrapper entry = new EntryWrapper((V) value);
        if (entrySet.remove(entry))
            return (V) value;

        return null;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return entrySet;
    }

}
