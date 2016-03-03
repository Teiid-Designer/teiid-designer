/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import java.util.Collection;
import java.util.Properties;

/**
 * @since 8.0
 *
 */
public interface ITeiidTranslator {
	
	enum TranslatorPropertyType{IMPORT, OVERRIDE, EXTENSION_METADATA}

    /**
     * Obtains all the names of the properties whose values are invalid.
     * 
     * @return the names of the properties with invalid values (never <code>null</code> but can be empty)
     */
    Collection<String> findInvalidProperties(TranslatorPropertyType propType);

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.teiidServerapi.AdminObject#getName()
     */
    String getName();

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.teiidServerapi.AdminObject#getProperties()
     */
    Properties getProperties();

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.teiidServerapi.AdminObject#getPropertyValue(java.lang.String)
     */
    String getPropertyValue(String name, TranslatorPropertyType type);

    /**
     * @return type
     */
    String getType();

    /**
     * @return the execution teiidServer (never <code>null</code>)
     */
    ITeiidServer getTeiidServer();

    /**
     * @return the string version of the default value for each property (empty string if no default)
     */
    Properties getDefaultPropertyValues();

    /**
     * @param name the property name
     * @param value the proposed new value
     * @return null if the property exists and the proposed value is valid or an error message
     * @since 7.0
     */
    String isValidPropertyValue(String name, String value, TranslatorPropertyType type);

    /**
     * Sets a connector property.
     * 
     * @param name the property name (never <code>null</code>)
     * @param value the new property value
     * @throws Exception if there is a problem changing the property
     * @since 5.0
     */
    void setPropertyValue(String name, String value, TranslatorPropertyType type) throws Exception;

    /**
     * @param changedProperties the list of properties that are being changed (never <code>null</code> or empty)
     * @throws Exception if there is a problem changing the properties
     * @since 7.0
     */
    void setProperties(Properties changedProperties) throws Exception;

    /**
     * @param name the name of the <code>TeiidPropertyDefinition</code> being requested (never <code>null</code> or empty)
     * @return the property definition or <code>null</code> if not found
     */
    TeiidPropertyDefinition getPropertyDefinition( String name , TranslatorPropertyType type);
    
    /**
     * @return an immutable collection of property definitions (never <code>null</code>);
     * @since 7.0
     */
    Collection<TeiidPropertyDefinition> getPropertyDefinitions();
    
    /**
     * @return an immutable collection of import property definitions (never <code>null</code>);
     * @since 7.0
     */
    Collection<TeiidPropertyDefinition> getImportPropertyDefinitions();
    
    /**
     * @return an immutable collection of import property definitions (never <code>null</code>);
     * @since 7.0
     */
    Collection<TeiidPropertyDefinition> getExtensionPropertyDefinitions();
}
