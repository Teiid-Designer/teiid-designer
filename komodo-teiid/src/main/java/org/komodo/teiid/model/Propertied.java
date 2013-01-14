/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model;

import java.util.Map;

/**
 * Indicates the Teiid object may have generic properties.
 */
public interface Propertied {

    /**
     * The VDB manifest (<code>vdb.xml</code>) identifiers related to property elements.
     */
    public interface ManifestId {

        /**
         * The property element attribute identifiers.
         */
        interface Attributes {

            /**
             * The property name attribute identifier.
             */
            String NAME = "name"; //$NON-NLS-1$

            /**
             * The property value attribute identifier.
             */
            String VALUE = "value"; //$NON-NLS-1$
        }

        /**
         * The property element identifier.
         */
        String PROPERTY = "property"; //$NON-NLS-1$
    }

    /**
     * The name of the property for the additional properties.
     */
    String PROPERTIES = Propertied.class.getSimpleName() + ".properties"; //$NON-NLS-1$

    /**
     * @return an unmodifiable collection of the additional properties (never <code>null</code>)
     */
    Map<String, String> getProperties();

    /**
     * @param name the name of the generic property whose value is being requested (cannot be <code>null</code> or empty)
     * @return the value or <code>null</code> if property does not exist
     */
    String getProperty(final String name);

    /**
     * Removes a generic property. If property exists, a property change event is fired after the property is removed.
     * 
     * @param name the name of the generic property being removed (cannot be <code>null</code> or empty)
     */
    void removeProperty(final String name);

    /**
     * If property does not currently exist, it is created. Generates a property change event if the property is changed.
     * 
     * @param name the name of the generic property being set (cannot be <code>null</code> or empty)
     * @param newValue the new property value (can be <code>null</code> or empty)
     */
    void setProperty(final String name,
                     final String newValue);

}
