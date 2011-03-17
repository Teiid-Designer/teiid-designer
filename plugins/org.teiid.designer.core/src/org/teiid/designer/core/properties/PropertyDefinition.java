/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.properties;

/**
 * The attributes of a property.
 */
public interface PropertyDefinition {

    /**
     * @return the list of non-<code>null</code>, non-empty values that compose all the allowed values (can be <code>null</code> or
     *         empty if not constrained to certain values)
     */
    String[] getAllowedValues();

    /**
     * @return the default value or <code>null</code> or empty if there is not one
     */
    String getDefaultValue();

    /**
     * @return a brief, localized description of this property (can be <code>null</code> or empty)
     */
    String getDescription();

    /**
     * @return a short, localized display name of this property (may not be <code>null</code> or empty)
     */
    String getDisplayName();
    
    /**
     * @return the unique identifier (may not be <code>null</code>)
     */
    String getId();

    /**
     * @return <code>true</code> if intended for expert users
     */
    boolean isAdvanced();

    /**
     * @return <code>true</code> if value should be masked when displayed
     */
    boolean isMasked();

    /**
     * @return <code>true</code> if value can be changed
     */
    boolean isModifiable();

    /**
     * @return <code>true</code> if a non-<code>null</code>, non-empty value must exist
     */
    boolean isRequired();

    /**
     * @param newValue the proposed new value (can be <code>null</code> or empty)
     * @return a localized message explaining why the value is not valid (<code>null</code> or empty if valid)
     */
    String isValidValue( String newValue );

}
