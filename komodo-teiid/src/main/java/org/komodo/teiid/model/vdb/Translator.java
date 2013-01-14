/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import org.komodo.common.util.HashCode;
import org.komodo.common.util.StringUtil;

/**
 * The Teiid translator business object.
 */
public class Translator extends VdbAdminObject {

    /**
     * The VDB manifest (<code>vdb.xml</code>) identifiers related to translator elements.
     */
    public interface ManifestId {

        /**
         * The VDB translator element attribute identifiers.
         */
        interface Attributes {

            /**
             * The translator description attribute identifier.
             */
            String DESCRIPTION = "description"; //$NON-NLS-1$

            /**
             * The translator name attribute identifier.
             */
            String NAME = "name"; //$NON-NLS-1$

            /**
             * The translator type attribute identifier.
             */
            String TYPE = "type"; //$NON-NLS-1$
        }
    }

    /**
     * Translator property names.
     */
    public interface PropertyName extends VdbObject.PropertyName {

        /**
         * The translator type.
         */
        String TYPE = Translator.class.getSimpleName() + ".type"; //$NON-NLS-1$
    }

    private String type;

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object that) {
        if (super.equals(that)) {
            return StringUtil.matches(this.type, ((Translator)that).type);
        }

        return false;
    }

    /**
     * @return the type the translator type (can be <code>null</code> or empty)
     */
    public String getType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(super.hashCode(), this.type);
    }

    /**
     * Generates a property change event if the type is changed.
     * 
     * @param newType the new translator type (can be <code>null</code> or empty)
     */
    public void setType(final String newType) {
        if (!StringUtil.matches(this.type, newType)) {
            final String oldValue = this.type;
            this.type = newType;
            firePropertyChangeEvent(PropertyName.TYPE, oldValue, this.type);

            assert StringUtil.matches(this.type, newType);
            assert !StringUtil.matches(this.type, oldValue);
        }
    }

}
