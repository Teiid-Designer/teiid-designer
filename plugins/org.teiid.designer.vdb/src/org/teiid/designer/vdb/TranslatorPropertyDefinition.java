/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.VdbPlugin.UTIL;

import org.teiid.designer.core.properties.PropertyDefinition;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;

/**
 * A property definition that either represents one that exists on the current Teiid default server or one that does not. If it does
 * not exist on the current server, or a current server does not exist, this definition will either be one that the user created or
 * one that was initially found on a server.
 */
public class TranslatorPropertyDefinition implements PropertyDefinition {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(TranslatorPropertyDefinition.class);

    /**
     * @param proposedName the proposed property name
     * @return an error message or <code>null</code> if name is valid
     */
    public static String validateName( String proposedName ) {
        // must have a name
        if (StringUtilities.isEmpty(proposedName)) {
            return UTIL.getString(PREFIX + "emptyPropertyName"); //$NON-NLS-1$
        }

        // make sure only letters
        for (char c : proposedName.toCharArray()) {
            if (!Character.isLetter(c)) {
                return UTIL.getString(PREFIX + "invalidPropertyName", proposedName); //$NON-NLS-1$
            }
        }

        // valid name
        return null;
    }

    /**
     * @param proposedValue the proposed value
     * @return an error message or <code>null</code> if value is valid
     */
    public static String validateValue( String proposedValue ) {
        // must have a value
        if (StringUtilities.isEmpty(proposedValue)) {
            return UTIL.getString(PREFIX + "emptyPropertyValue"); //$NON-NLS-1$
        }

        // valid
        return null;
    }

    private PropertyDefinition delegate;
    private String id;

    /**
     * @param delegate a property definition from a Teiid server (may not be <code>null</code>)
     */
    public TranslatorPropertyDefinition( PropertyDefinition delegate ) {
        this.delegate = delegate;
    }

    /**
     * @param id the unique identifier (may not be <code>null</code>)
     */
    public TranslatorPropertyDefinition( String id ) {
        this.id = id;
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

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        return getId().equals(((TranslatorPropertyDefinition)obj).getId());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getAllowedValues()
     */
    @Override
    public String[] getAllowedValues() {
        if (this.delegate != null) {
            return this.delegate.getAllowedValues();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getDefaultValue()
     */
    @Override
    public String getDefaultValue() {
        if (this.delegate != null) {
            return this.delegate.getDefaultValue();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        if (this.delegate != null) {
            return this.delegate.getDescription();
        }

        return UTIL.getString(PREFIX + "customPropertyDescription"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        if (this.delegate != null) {
            return this.delegate.getDisplayName();
        }

        return getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#getId()
     */
    @Override
    public String getId() {
        if (this.delegate != null) {
            return this.delegate.getId();
        }

        return this.id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isAdvanced()
     */
    @Override
    public boolean isAdvanced() {
        if (this.delegate != null) {
            return this.delegate.isAdvanced();
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isMasked()
     */
    @Override
    public boolean isMasked() {
        if (this.delegate != null) {
            return this.delegate.isMasked();
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isModifiable()
     */
    @Override
    public boolean isModifiable() {
        if (this.delegate != null) {
            return this.delegate.isModifiable();
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isRequired()
     */
    @Override
    public boolean isRequired() {
        if (this.delegate != null) {
            return this.delegate.isRequired();
        }

        return true;
    }

    /**
     * @return <code>true</code> if this is a user-defined property definition or a property definition not found on the current
     *         server
     */
    public boolean isUserDefined() {
        return (this.delegate == null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.properties.PropertyDefinition#isValidValue(java.lang.String)
     */
    @Override
    public String isValidValue( String newValue ) {
        if (this.delegate != null) {
            return this.delegate.isValidValue(newValue);
        }

        return TranslatorPropertyDefinition.validateValue(newValue);
    }

    /**
     * Marks this definition as being one not found on the current default Teiid server.
     */
    public void markAsUserDefined() {
        if (this.delegate != null) {
            this.id = this.delegate.getId();
            this.delegate = null;
        }
    }
}
