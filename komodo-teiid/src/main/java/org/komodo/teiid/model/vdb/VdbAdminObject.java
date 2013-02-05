/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.HashCode;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.teiid.model.Describable;
import org.komodo.teiid.model.Propertied;

/**
 * The base class for Teiid admin model objects.
 */
public abstract class VdbAdminObject extends VdbObject implements Describable, Propertied {

    /**
     * Default property names.
     */
    public interface PropertyName extends VdbObject.PropertyName {

        /**
         * The Teiid admin object description.
         */
        String DESCRIPTION = Describable.DESCRIPTION;

        /**
         * The Teiid admin object additonal properties.
         */
        String PROPERTIES = Propertied.PROPERTIES;
    }

    private String description;

    private Map<String, String> props;

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object that) {
        if (super.equals(that)) {
            final VdbAdminObject other = (VdbAdminObject)that;

            // check properties
            if (!CollectionUtil.matches(this.props, other.props)) {
                return false;
            }

            // check description
            return StringUtil.matches(this.description, other.description);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Describable#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Propertied#getProperties()
     */
    @Override
    public Map<String, String> getProperties() {
        if (this.props == null) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(this.props);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Propertied#getProperty(java.lang.String)
     */
    @Override
    public String getProperty(final String name) {
        Precondition.notEmpty(name, "name"); //$NON-NLS-1$

        if (this.props == null) {
            return null;
        }

        return this.props.get(name);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(super.hashCode(), this.description, this.props);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Propertied#removeProperty(java.lang.String)
     */
    @Override
    public void removeProperty(final String name) {
        setProperty(name, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Describable#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(final String newDescription) {
        if (!StringUtil.matches(this.description, newDescription)) {
            final String oldValue = this.description;
            this.description = newDescription;
            firePropertyChangeEvent(PropertyName.DESCRIPTION, oldValue, newDescription);

            assert StringUtil.matches(this.description, newDescription);
            assert !StringUtil.matches(this.description, oldValue);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.Propertied#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void setProperty(final String name,
                            final String newValue) {
        Precondition.notEmpty(name, "name"); //$NON-NLS-1$

        final String oldValue = getProperty(name);

        if (!StringUtil.matches(oldValue, newValue)) {
            boolean changed = false;

            if (StringUtil.isEmpty(newValue) && (this.props != null)) {
                if (this.props.containsKey(name)) {
                    this.props.remove(name);
                    changed = true;

                    if (this.props.isEmpty()) {
                        this.props = null;
                    }
                }
            } else {
                if (this.props == null) {
                    this.props = new HashMap<String, String>();
                }

                this.props.put(name, newValue);
                changed = true;
            }

            if (changed) {
                firePropertyChangeEvent(name, oldValue, newValue);

                assert StringUtil.matches(getProperty(name), newValue);
                assert !StringUtil.matches(getProperty(name), oldValue);
            }
        }
    }

}
