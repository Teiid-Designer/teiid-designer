/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

/**
 * 
 */
public abstract class ModelObjectExtensionAssistant extends ModelExtensionAssistant {

    /**
     * Obtains from the ModelResource, the ModelExtensionDefinition.
     * 
     * @param modelObject the model object whose ModelExtensionDefinition is being requested (cannot be <code>null</code>)
     * @return the ModelExtensionDefinition for this assistant's namespacePrefix or <code>null</code> if not supported by the model
     *         object
     * @throws Exception if there is a problem obtaining the ModelExtensionDefinition
     */
    public abstract ModelExtensionDefinition getModelExtensionDefinition( Object modelObject ) throws Exception;

    /**
     * Obtains from the model object, the overridden property value of the specified property definition identifier. If the current
     * property value is equal to the default value then a <code>null</code> or empty value is returned.
     * 
     * @param modelObject the model object whose extension property's overridden value is being requested (cannot be
     *            <code>null</code> )
     * @param propId the property identifier whose property's overridden value is being requested (cannot be <code>null</code> or
     *            empty)
     * @return the extension property's overridden value (can be <code>null</code> or empty if there is no overridden value)
     * @throws Exception if there is a problem obtaining the extension property's overridden value or if the property definition
     *             can't be found
     */
    public abstract String getOverriddenValue( Object modelObject,
                                               String propId ) throws Exception;

    /**
     * Obtains from the model object, the property identifiers and their associated property's overridden value for all extension
     * properties of this assistant's namespace. Properties whose current value is not different from the default value will not be
     * returned.
     * 
     * @param modelObject the model object whose extension properties are being requested (cannot be <code>null</code>)
     * @return the extension properties that have overridden values for the specified model object (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the extension properties
     */
    public abstract Properties getOverriddenValues( Object modelObject ) throws Exception;

    /**
     * Obtains from the model object, the property value of the specified property definition identifier. If the current value is
     * empty, the default value is returned.
     * 
     * @param modelObject the model object whose extension property value is being requested (cannot be <code>null</code>)
     * @param propId the property identifier whose property value is being requested (cannot be <code>null</code> or empty)
     * @return the current extension property value (can be <code>null</code> or empty)
     * @throws Exception if there is a problem obtaining the extension property value
     */
    public abstract String getPropertyValue( Object modelObject,
                                             String propId ) throws Exception;

    /**
     * Obtains from the model object, the property identifiers and current property value for all extension properties of this
     * assistant's namespace. Properties whose current value is empty will be set to the default value.
     * 
     * @param modelObject the model object whose extension properties are being requested (cannot be <code>null</code>)
     * @return the current extension property values for the specified model object (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the extension properties
     */
    public abstract Properties getPropertyValues( Object modelObject ) throws Exception;

    /**
     * @param modelResource the model resource whose supported namespaces are being obtained (cannot be <code>null</code>)
     * @return the namespace prefixes of all model extension definitions that are persisted in the model resource (never
     *         <code>null</code>)
     * @throws Exception if there is a problem accessing the model resource
     */
    public abstract Collection<String> getSupportedNamespaces( Object modelObject ) throws Exception;

    /**
     * @param file the file being checked for extension properties (cannot be <code>null</code>)
     * @return <code>true</code> if the file contains extension properties defined by the assistant's namespace prefix
     * @throws Exception if there is a problem working with the file
     */
    public abstract boolean hasExtensionProperties( File file ) throws Exception;

    /**
     * @param modelObject the model object being checked for the existence of extension properties (cannot be <code>null</code>)
     * @return <code>true</code> if the model object has extension properties
     * @throws Exception if there is a problem accessing the model object
     */
    public abstract boolean hasExtensionProperties( Object modelObject ) throws Exception;

    /**
     * @param modelObject the model object being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the model object is related to a model extension definition
     * @throws Exception if there is a problem accessing the model object
     */
    public abstract boolean isModelExtensionDefinitionRelated( Object modelObject ) throws Exception;

    /**
     * Removes the model extension definition and it's associated properties from the model object.
     * 
     * @param modelObject the model object whose resource the model extension definition is being removed from (cannot be
     *            <code>null</code>)
     * @throws Exception if the model extension definition could not be removed
     */
    public abstract void removeModelExtensionDefinition( Object modelObject ) throws Exception;

    /**
     * @param modelObject the model object whose property is being removed (cannot be <code>null</code>)
     * @param propId the extension property full identifier (cannot be <code>null</code>)
     * @throws Exception if the extension property could not be removed
     */
    public abstract void removeProperty( Object modelObject,
                                         String propId ) throws Exception;

    /**
     * @param modelObject the model object or resource where the model extension definition will be saved to (cannot be
     *            <code>null</code>)
     * @throws Exception if there is a problem saving the definition
     */
    public abstract void saveModelExtensionDefinition( Object modelObject ) throws Exception;

    /**
     * @param modelObject the model object whose property is being set (cannot be <code>null</code>)
     * @param propId the extension property identifier (cannot be <code>null</code> or empty)
     * @param newValue the new value (can be <code>null</code> or empty)
     * @throws Exception if there is a problem setting the extension property value
     */
    public abstract void setPropertyValue( Object modelObject,
                                           String propId,
                                           String newValue ) throws Exception;

    /**
     * @param modelObject the model object whose resource is being checked to see if it has the specified model extension definition
     *            saved (cannot be <code>null</code>)
     * @return <code>true</code> if the model object's resource contains the namespace prefix
     * @throws Exception if there is a problem checking the model object's resource
     */
    public abstract boolean supportsMyNamespace( Object modelObject ) throws Exception;

}
