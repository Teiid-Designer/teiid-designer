/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.komodo.common.util.StringUtil;
import org.s_ramp.xmlns._2010.s_ramp.Property;

/**
 * An S-RAMP artifact's properties.
 */
public class SrampArtifactProperties implements Map<String, String> {

    private static final List<Property> NO_PROPS = Collections.emptyList();
    private final List<Property> properties;
    private final boolean readonly;

    /**
     * A readonly set of properties.
     * 
     * @param properties the S-RAMP artifact properties (can be <code>null</code>)
     */
    public SrampArtifactProperties(final List<Property> properties) {
        this(properties, true);
    }

    /**
     * @param properties the S-RAMP artifact properties (can be <code>null</code>)
     * @param readonly <code>true</code> if the properties should be readonly
     */
    public SrampArtifactProperties(final List<Property> properties,
                                   final boolean readonly) {
        this.properties = ((properties == null) ? NO_PROPS : properties);
        this.readonly = readonly;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        if (this.readonly) {
            throw new UnsupportedOperationException(); // TODO add message
        }

        this.properties.clear();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(final Object key) {
        if (key == null) {
            throw new NullPointerException(); // TODO add message
        }

        if (!(key instanceof String)) {
            throw new ClassCastException(); // TODO add message
        }

        if (this.properties.isEmpty()) {
            return false;
        }

        for (Property prop : this.properties) {
            if (key.equals(prop.getPropertyName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            throw new NullPointerException(); // TODO add message
        }

        if (!(value instanceof String)) {
            throw new ClassCastException(); // TODO add message
        }

        if (this.properties.isEmpty()) {
            return false;
        }

        for (Property prop : this.properties) {
            if (value.equals(prop.getPropertyValue())) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public String get(final Object key) {
        if (key == null) {
            throw new NullPointerException(); // TODO add message
        }

        if (!(key instanceof String)) {
            throw new ClassCastException(); // TODO add message
        }

        if (this.properties.isEmpty()) {
            return null;
        }

        for (Property prop : this.properties) {
            if (key.equals(prop.getPropertyName())) {
                return prop.getPropertyValue();
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#keySet()
     */
    @Override
    public Set<String> keySet() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public String put(final String key,
                      final String value) {
        if (this.readonly) {
            throw new UnsupportedOperationException(); // TODO add message
        }

        // TODO impl
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<? extends String, ? extends String> m) {
        if (this.readonly) {
            throw new UnsupportedOperationException(); // TODO add message
        }

        // TODO impl
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public String remove(final Object key) {
        if (this.readonly) {
            throw new UnsupportedOperationException(); // TODO add message
        }

        if (key == null) {
            throw new NullPointerException(); // TODO add message
        }

        if (!(key instanceof String)) {
            throw new ClassCastException(); // TODO add message
        }

        if (isEmpty()) {
            return null;
        }

        for (final Property prop : this.properties) {
            if (key.equals(prop.getPropertyName())) {
                return prop.getPropertyValue();
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        return this.properties.size();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.util.Map#values()
     */
    @Override
    public Collection<String> values() {
        if (isEmpty()) {
            return StringUtil.EMPTY_LIST;
        }

        final List<String> values = new ArrayList<String>(this.properties.size());

        for (final Property prop : this.properties) {
            values.add(prop.getPropertyValue());
        }

        return values;
    }

}
