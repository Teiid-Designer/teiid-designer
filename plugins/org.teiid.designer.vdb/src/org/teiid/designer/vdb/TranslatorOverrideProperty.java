/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import org.teiid.designer.core.properties.PropertyDefinition;

import com.metamatrix.core.util.StringUtilities;

/**
 * A translator property that may or may not have an overridden value.
 */
public class TranslatorOverrideProperty {

    private TranslatorPropertyDefinition propDefn;
    private String overriddenValue;

    /**
     * @param propDefn the property definition (may not be <code>null</code>)
     * @param overriddenValue the property value override (can be <code>null</code> or empty)
     */
    public TranslatorOverrideProperty( TranslatorPropertyDefinition propDefn,
                                       String overriddenValue ) {
        assert (propDefn != null);
        this.propDefn = propDefn;
        this.overriddenValue = overriddenValue;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        return this.propDefn.getId().equals(((TranslatorOverrideProperty)obj).getDefinition().getId());
    }

    /**
     * @return the property definition (never <code>null</code>)
     */
    public TranslatorPropertyDefinition getDefinition() {
        return this.propDefn;
    }

    /**
     * @return the property value override (may be <code>null</code> or empty)
     */
    public String getOverriddenValue() {
        return this.overriddenValue;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getDefinition().getId().hashCode();
    }

    /**
     * @return <code>true</code> if the property value is not <code>null</code> and not empty
     */
    public boolean hasOverridenValue() {
        return !StringUtilities.isEmpty(getOverriddenValue());
    }

    /**
     * @return <true> if not a known server property
     * @see TranslatorPropertyDefinition#isUserDefined()
     */
    public boolean isCustom() {
        return this.propDefn.isUserDefined();
    }

    /**
     * @param propDefn the new property definition from the server (may not be <code>null</code>)
     */
    public void setDefinition( PropertyDefinition propDefn ) {
        assert (propDefn != null);
        this.propDefn = new TranslatorPropertyDefinition(propDefn);
    }

    /**
     * @param newValue the new property value override (can be <code>null</code> or empty)
     */
    public void setValue( String newValue ) {
        this.overriddenValue = newValue;
    }

}