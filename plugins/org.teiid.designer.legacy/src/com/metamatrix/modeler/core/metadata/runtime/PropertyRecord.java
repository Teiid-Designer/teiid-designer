/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

/**
 * PropertyRecord
 */
public interface PropertyRecord extends MetadataRecord {

    /**
     * Constants for names of accessor methods that map to fields stored  on the PropertyRecords.
     * Note the names do not have "get" on them, this is also the nameInsource
     * of the attributes on SystemPhysicalModel.
     * @since 4.3
     */
    public interface MetadataFieldNames {
        String PROPERTY_NAME_FIELD    = "PropertyName"; //$NON-NLS-1$
        String PROPERTY_VALUE_FIELD    = "PropertyValue"; //$NON-NLS-1$
    }

    /**
     * Return the property name for this record
     * @return property name
     */
    String getPropertyName();

    /**
     * Return the property value for this record
     * @return property value
     */
    String getPropertyValue();

    /**
     * Bollean indiacting if this is an extention property 
     * @return true if it is an extention property
     * @since 4.2
     */
    boolean isExtension();
}
