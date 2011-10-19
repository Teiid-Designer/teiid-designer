/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinitionImpl;
import org.teiid.designer.extension.properties.Translation;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * The <code>ModelExtensionAssistant</code> is used when a model extension definition file is parsed and also when working with
 * model objects.
 */
public abstract class ModelExtensionAssistant {

    private ModelExtensionDefinition definition;

    /**
     * This method should only be called by the {@link ModelExtensionDefinitionParser}.
     * 
     * @param metaclassName the metaclass name being extended (cannot be <code>null</code> or empty)
     * @param propertyDefinition the property definition being added (cannot be <code>null</code>)
     */
    public void addPropertyDefinition( String metaclassName,
                                       ModelExtensionPropertyDefinition propertyDefinition ) {
        assert this.definition != null : "model extension definition is null"; //$NON-NLS-1$
        this.definition.addPropertyDefinition(metaclassName, propertyDefinition);
    }

    /**
     * This method should only be called by the {@link ModelExtensionDefinitionParser}.
     * 
     * @param namespacePrefix the unique namespace prefix (never <code>null</code> or empty)
     * @param namespaceUri the unique namespace URI (never <code>null</code> or empty)
     * @param metamodelUri the metamodel URI this definition is extending (never <code>null</code> or empty)
     * @param description the description of the definition (can be <code>null</code> or empty)
     * @param version the definition version (can be <code>null</code> or empty)
     * @return the new model extension definition (never <code>null</code>)
     */
    public ModelExtensionDefinition createModelExtensionDefinition( String namespacePrefix,
                                                                    String namespaceUri,
                                                                    String metamodelUri,
                                                                    String description,
                                                                    String version ) {
        this.definition = new ModelExtensionDefinition(this, namespacePrefix, namespaceUri, metamodelUri, description, version);
        return this.definition;
    }

    /**
     * Create a ModelExtensionDefinition using the header info
     * 
     * @param medHeader the ModelExtensionDefinitionHeader (cannot be <code>null</code>)
     * @return the new model extension definition (never <code>null</code>)
     */
    public ModelExtensionDefinition createModelExtensionDefinition( ModelExtensionDefinitionHeader medHeader ) {
        CoreArgCheck.isNotNull(medHeader, "ModelExtensionDefinitionHeader is null"); //$NON-NLS-1$
        return createModelExtensionDefinition(medHeader.getNamespacePrefix(), medHeader.getNamespaceUri(),
                                              medHeader.getMetamodelUri(), medHeader.getDescription(),
                                              String.valueOf(medHeader.getVersion()));
    }

    /**
     * This method should only be called by the {@link ModelExtensionDefinitionParser}.
     * 
     * @param simpleId the property identifier without the namespace prefix (cannot be <code>null</code> or empty)
     * @param runtimeType the Teiid runtime type (cannot be <code>null</code> or empty)
     * @param required <code>true</code> string if this property must have a value (cannot be <code>null</code> or empty)
     * @param defaultValue a default value (can be <code>null</code> or empty)
     * @param fixedValue a constant value, when non-<code>null</code> and non-empty, indicates the property value cannot be changed
     *            (can be <code>null</code> or empty)
     * @param advanced <code>true</code> string if this property should only be shown to advances users (cannot be <code>null</code>
     *            or empty)
     * @param masked <code>true</code> string if this property value must be masked (cannot be <code>null</code> or empty)
     * @param index <code>true</code> string if this property value must be indexed for use by the Teiid server (cannot be
     *            <code>null</code> or empty)
     * @param allowedValues the allowed property values (can be <code>null</code> or empty)
     * @param descriptions the one or more translations of the property description (can be <code>null</code> or empty)
     * @param displayNames the one or more translations of the property display name (can be <code>null</code> or empty)
     * @return the new extension property definition (never <code>null</code>)
     */
    public ModelExtensionPropertyDefinition createPropertyDefinition( String simpleId,
                                                                      String runtimeType,
                                                                      String required,
                                                                      String defaultValue,
                                                                      String fixedValue,
                                                                      String advanced,
                                                                      String masked,
                                                                      String index,
                                                                      Set<String> allowedValues,
                                                                      Set<Translation> descriptions,
                                                                      Set<Translation> displayNames ) {
        assert this.definition != null : "model extension definition is null"; //$NON-NLS-1$
        return new ModelExtensionPropertyDefinitionImpl(this.definition,
                                                        simpleId,
                                                        runtimeType,
                                                        required,
                                                        defaultValue,
                                                        fixedValue,
                                                        advanced,
                                                        masked,
                                                        index,
                                                        allowedValues,
                                                        descriptions,
                                                        displayNames);
    }

    /**
     * @return the model extension definition (MED) (never <code>null</code>)
     */
    public ModelExtensionDefinition getModelExtensionDefinition() {
        return this.definition;
    }

    /**
     * @return the namespace prefix (never <code>null</code> or empty)
     */
    public final String getNamespacePrefix() {
        return this.definition.getNamespacePrefix();
    }

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
     * properties of registered namespaces. Properties whose current value is not different from the default value will not be
     * returned.
     * 
     * @param modelObject the model object whose extension properties are being requested (cannot be <code>null</code>)
     * @return the extension properties that have overridden values for the specified model object (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the extension properties
     */
    public abstract Properties getOverriddenValues( Object modelObject ) throws Exception;

    /**
     * @param modelObject the model object whose property definition is being requested (cannot be <code>null</code>)
     * @param propId the property identifier whose property definition is being requested (cannot be <code>null</code> or empty)
     * @return the property definition or <code>null</code> if not found
     */
    protected ModelExtensionPropertyDefinition getPropertyDefinition( Object modelObject,
                                                                      String propId ) {
        CoreArgCheck.isNotNull(modelObject, "modelObject is null"); //$NON-NLS-1$

        // make sure right namespace
        if (ModelExtensionPropertyDefinition.Utils.isExtensionPropertyId(propId, getNamespacePrefix())) {
            return this.definition.getPropertyDefinition(modelObject.getClass().getName(), propId);
        }

        return null;
    }

    /**
     * Obtains from the ModelResource, the ModelExtensionDefinition. If there is no ModelExtensionDefinition for this
     * namespacePrefix, returns <code>null</code>
     * 
     * @param modelObject the model object whose ModelExtensionDefinition is being requested (cannot be <code>null</code>)
     * @return the ModelExtensionDefinition for the requested namespacePrefix
     * @throws Exception if there is a problem obtaining the ModelExtensionDefinition
     */
    public abstract ModelExtensionDefinition getModelExtensionDefinition( Object modelObject ) throws Exception;

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
     * Obtains from the model object, the property identifiers and current property value for all extension properties of registered
     * namespaces. Properties whose current value is empty will be set to the default value.
     * 
     * @param modelObject the model object whose extension properties are being requested (cannot be <code>null</code>)
     * @return the current extension property values for the specified model object (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the extension properties
     */
    public abstract Properties getPropertyValues( Object modelObject ) throws Exception;

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
