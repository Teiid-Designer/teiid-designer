/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.connector.metadata;

import java.util.Map;

/**
 */
public class PropertyHolder implements Map.Entry {
    private Object key;
    private String value;
    
    /**
     * 
     */
    public PropertyHolder(Object key) {
        this.key = key;
    }

    /* 
     * @see java.util.Map.Entry#getKey()
     */
    public Object getKey() {
        return key;
    }

    /* 
     * @see java.util.Map.Entry#getValue()
     */
    public Object getValue() {
        return value;
    }

    /* 
     * @see java.util.Map.Entry#setValue(java.lang.Object)
     */
    public Object setValue(Object value) {
        this.value = (String) value;
        return this.value;
    }

}
