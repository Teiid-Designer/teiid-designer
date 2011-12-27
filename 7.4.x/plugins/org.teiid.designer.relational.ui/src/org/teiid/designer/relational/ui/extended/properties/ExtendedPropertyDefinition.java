/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.ui.extended.properties;

import static com.metamatrix.modeler.relational.ui.UiConstants.Util;
import org.teiid.core.properties.PropertyDefinition;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtilities;

/**
 * A property definition object for an extended metadata property
 */
public class ExtendedPropertyDefinition implements PropertyDefinition {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ExtendedPropertyDefinition.class);
    public static final String NAMESPACE = "ext-custom"; //$NON-NLS-1$
    public static final String SEPARATOR = ":"; //$NON-NLS-1$
    public static final String EXT_PROPERTY_NAMESPACE = NAMESPACE + SEPARATOR;

    /**
     * @param proposedName the proposed property name
     * @return an error message or <code>null</code> if name is valid
     */
    public static String validateName( String proposedName ) {
        // must have a name
        if (StringUtilities.isEmpty(proposedName)) {
            return Util.getString(PREFIX + "emptyPropertyName"); //$NON-NLS-1$
        }

        // make sure only letters
        for (char c : proposedName.toCharArray()) {
            if (!Character.isLetter(c)) {
                return Util.getString(PREFIX + "invalidPropertyName", proposedName); //$NON-NLS-1$
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
            return Util.getString(PREFIX + "emptyPropertyValue"); //$NON-NLS-1$
        }

        // valid
        return null;
    }

    /**
     * A non-empty, non-null value used for a custom property or <code>null</code> if a delegate is present.
     */
    private String defaultValue;

    /**
     * Used when a Teiid server property is found.
     */
    private PropertyDefinition delegate;

    /**
     * A unique ID for a custom property or <code>null</code> if a delegate is present.
     */
    private String id;

    /**
     * @param delegate a property definition from a Teiid server (may not be <code>null</code>)
     * @throws IllegalArgumentException if <code>delegate</code> is <code>null</code>
     */
    public ExtendedPropertyDefinition( PropertyDefinition delegate ) {
        CoreArgCheck.isNotNull(delegate);
        this.delegate = delegate;
    }

    /**
     * Used to construct a custom property added by the user.
     * 
     * @param id the unique identifier (may not be <code>null</code>)
     * @param defaultValue the default value (may not be <code>null</code> or empty)
     * @throws IllegalArgumentException if <code>id</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>defaultValue</code> is <code>null</code> or empty
     */
    public ExtendedPropertyDefinition( String id,
                                       String defaultValue ) {
        CoreArgCheck.isNotEmpty(id);
        CoreArgCheck.isNotEmpty(defaultValue);
        this.id = id;
        this.defaultValue = defaultValue;
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

        return getId().equals(((ExtendedPropertyDefinition)obj).getId());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getAllowedValues()
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
     * @see org.teiid.core.properties.PropertyDefinition#getDefaultValue()
     */
    @Override
    public String getDefaultValue() {
        if (this.delegate != null) {
            return this.delegate.getDefaultValue();
        }

        return this.defaultValue;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        if (this.delegate != null) {
            return this.delegate.getDisplayName();
        }

        return getId();
    }

    /**
     * 
     */
    public String getNameWithoutNamespace() {

        String id = getId();
        if (id.startsWith(EXT_PROPERTY_NAMESPACE)) {
            return id.substring(EXT_PROPERTY_NAMESPACE.length());
        }

        return id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getId()
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
     * @see org.teiid.core.properties.PropertyDefinition#isAdvanced()
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
     * @see org.teiid.core.properties.PropertyDefinition#isMasked()
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
     * @see org.teiid.core.properties.PropertyDefinition#isModifiable()
     */
    @Override
    public boolean isModifiable() {
        if (this.delegate != null) {
            return this.delegate.isModifiable();
        }

        // default to false
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#isRequired()
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
     * @see org.teiid.core.properties.PropertyDefinition#isValidValue(java.lang.String)
     */
    @Override
    public String isValidValue( String newValue ) {
        if (this.delegate != null) {
            return this.delegate.isValidValue(newValue);
        }

        return ExtendedPropertyDefinition.validateValue(newValue);
    }

    /**
     * Marks this definition as being one not found on the current default Teiid server.
     * 
     * @param defaultValue the default value (may not be <code>null</code> or empty)
     * @throws IllegalArgumentException if <code>defaultValue</code> is <code>null</code> or empty
     */
    public void markAsUserDefined( String defaultValue ) {
        CoreArgCheck.isNotEmpty(defaultValue);

        if (this.delegate != null) {
            this.id = this.delegate.getId();
            this.delegate = null;
            this.defaultValue = defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.core.properties.PropertyDefinition#getDescription()
     */
    @Override
    public String getDescription() {
        return null;
    }

}
