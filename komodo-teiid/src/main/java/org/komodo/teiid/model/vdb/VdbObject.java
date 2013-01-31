/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import org.komodo.common.util.HashCode;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.teiid.model.ModelObject;

/**
 * The base class for Teiid model objects.
 */
public abstract class VdbObject extends ModelObject implements Comparable<VdbObject> {

    /**
     * Names of the default properties for a VDB object.
     */
    public interface PropertyName {

        /**
         * The Teiid object identifier.
         */
        String ID = VdbObject.class.getSimpleName() + ".id"; //$NON-NLS-1$
    }

    protected String id;

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final VdbObject that) {
        Precondition.notNull(that, "that"); //$NON-NLS-1$

        if (this == that) {
            return 0;
        }

        final String thisClass = getClass().getSimpleName();
        final String thatClass = that.getClass().getSimpleName();
        final int result = thisClass.compareTo(thatClass);

        if (result < 0) {
            return -10;
        }

        if (result > 0) {
            return 10;
        }

        if (StringUtil.isEmpty(this.id)) {
            if (StringUtil.isEmpty(that.id)) {
                return 0;
            }

            return -1;
        }

        if (StringUtil.isEmpty(that.id)) {
            return 1;
        }

        return this.id.compareTo(that.id);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }

        if ((that == null) || !getClass().equals(that.getClass())) {
            return false;
        }

        // check ID
        return (StringUtil.matches(this.id, ((VdbObject)that).id));
    }

    /**
     * @return the ID (can be <code>null</code> or empty)
     */
    public String getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(this.id);
    }

    /**
     * Generates a property change event if the ID is changed.
     * 
     * @param newId the new ID (can be <code>null</code> or empty)
     */
    public void setId(final String newId) {
        if (!StringUtil.matches(this.id, newId)) {
            final String oldValue = this.id;
            this.id = newId;
            firePropertyChangeEvent(PropertyName.ID, oldValue, newId);

            assert StringUtil.matches(this.id, newId);
            assert !StringUtil.matches(this.id, oldValue);
        }
    }

}
