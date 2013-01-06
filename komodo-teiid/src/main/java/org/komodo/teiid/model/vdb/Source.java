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
 * The Teiid model source business object.
 */
public class Source extends VdbObject {

    /**
     * VDB model source property names.
     */
    public interface PropertyName extends VdbObject.PropertyName {

        /**
         * The source connection JNDI name.
         */
        String JNDI_NAME = Source.class.getSimpleName() + ".jndiName"; //$NON-NLS-1$

        /**
         * The source translator name.
         */
        String TRANSLATOR_NAME = Source.class.getSimpleName() + ".translatorName"; //$NON-NLS-1$
    }

    private String jndiName;
    private String translatorName;

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbAdminObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object that) {
        if (super.equals(that)) {
            final Source thatSource = (Source)that;
            return (StringUtil.matches(this.jndiName, thatSource.jndiName) && StringUtil.matches(this.translatorName,
                                                                                                 thatSource.translatorName));
        }

        return false;
    }

    /**
     * @return the source connection JNDI name (can be <code>null</code> or empty)
     */
    public String getJndiName() {
        return this.jndiName;
    }

    /**
     * @return the source translator name (can be <code>null</code> or empty)
     */
    public String getTranslatorName() {
        return this.translatorName;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.komodo.teiid.model.vdb.VdbObject#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(super.hashCode(), this.jndiName, this.translatorName);
    }

    /**
     * Generates a property change event if the JNDI name is changed.
     * 
     * @param newJndiName the new JNDI name (can be <code>null</code> or empty)
     */
    public void setJndiName(final String newJndiName) {
        if (!StringUtil.matches(this.jndiName, newJndiName)) {
            final String oldValue = this.jndiName;
            this.jndiName = newJndiName;
            firePropertyChangeEvent(PropertyName.JNDI_NAME, oldValue, this.jndiName);

            assert StringUtil.matches(this.jndiName, newJndiName);
            assert !StringUtil.matches(this.jndiName, oldValue);
        }
    }

    /**
     * Generates a property change event if the translator name is changed.
     * 
     * @param newTranslatorName the new translator name (can be <code>null</code> or empty)
     */
    public void setTranslatorName(final String newTranslatorName) {
        if (!StringUtil.matches(this.translatorName, newTranslatorName)) {
            final String oldValue = this.translatorName;
            this.translatorName = newTranslatorName;
            firePropertyChangeEvent(PropertyName.TRANSLATOR_NAME, oldValue, this.translatorName);

            assert StringUtil.matches(this.translatorName, newTranslatorName);
            assert !StringUtil.matches(this.translatorName, oldValue);
        }
    }

}
