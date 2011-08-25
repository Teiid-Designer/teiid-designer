/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static org.teiid.designer.extension.ExtensionPlugin.Util;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.properties.ModelExtensionProperty;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinitionImpl;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * The <code>ModelExtensionAssistant</code> is used when a model extension definition file is parsed and also when working with
 * model objects.
 */
public abstract class ModelExtensionAssistant {

    /**
     * @param modelExtensionDefinition the model extension defintion where the property definition is being added (cannot be
     *            <code>null</code>)
     * @param metaclassName the metaclass name being extended (cannot be <code>null</code> or empty)
     * @param propertyDefinition the property definition being added (cannot be <code>null</code>)
     */
    public void addPropertyDefinition( ModelExtensionDefinition modelExtensionDefinition,
                                       String metaclassName,
                                       ModelExtensionPropertyDefinition propertyDefinition ) {
        CoreArgCheck.isNotNull(modelExtensionDefinition, "modelExtensionDefinition is null"); //$NON-NLS-1$
        modelExtensionDefinition.addPropertyDefinition(metaclassName, propertyDefinition);
    }

    /**
     * @param modelObject the model object being checked (cannot be <code>null</code>)
     * @param namespacePrefix the namespace prefix of the model extension definition that is being looked for (cannot be
     *            <code>null</code>)
     * @return <code>true</code> if the definition was found in the model object's resource
     * @throws Exception if there is a problem accessing the model object's resource
     */
    protected abstract boolean containsModelExtensionDefinition( Object modelObject,
                                                                 String namespacePrefix ) throws Exception;

    /**
     * @param namespacePrefix the unique namespace prefix (never <code>null</code> or empty)
     * @param namespaceUri the unique namespace URI (never <code>null</code> or empty)
     * @param metamodelUri the metamodel URI this definition is extending (never <code>null</code> or empty)
     * @return the new model extension definition (never <code>null</code>)
     */
    public ModelExtensionDefinition createModelExtensionDefinition( String namespacePrefix,
                                                                    String namespaceUri,
                                                                    String metamodelUri ) {
        return new ModelExtensionDefinition(this, namespacePrefix, namespaceUri, metamodelUri);
    }

    /**
     * @param propDefn the property definition for the new property (cannot be <code>null</code>)
     * @return the new property set to the default value (never <code>null</code>)
     */
    public ModelExtensionProperty createProperty( ModelExtensionPropertyDefinition propDefn ) {
        return new ModelExtensionProperty(propDefn);
    }

    /**
     * @param simpleId the property identifier without the namespace prefix (cannot be <code>null</code> or empty)
     * @param displayName the display name (may be <code>null</code> or empty)
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
     * @return the new extension property definition (never <code>null</code>)
     */
    public ModelExtensionPropertyDefinition createPropertyDefinition( String simpleId,
                                                                      String displayName,
                                                                      String runtimeType,
                                                                      String required,
                                                                      String defaultValue,
                                                                      String fixedValue,
                                                                      String advanced,
                                                                      String masked,
                                                                      String index ) {
        return new ModelExtensionPropertyDefinitionImpl(getNamespacePrefix(),
                                                        simpleId,
                                                        displayName,
                                                        runtimeType,
                                                        required,
                                                        defaultValue,
                                                        fixedValue,
                                                        advanced,
                                                        masked,
                                                        index);
    }

    /**
     * @return the namespace prefix (never <code>null</code> or empty)
     */
    public abstract String getNamespacePrefix();

    /**
     * @param modelObject the model object whose supported namespaces is being requested (cannot be <code>null</code>)
     * @return the namespace prefixes contained in the model object's resource (never <code>null</code>)
     * @throws Exception if there is a problem accessing the model object's model resource
     */
    public abstract Collection<String> getSupportedNamespaces( Object modelObject ) throws Exception;

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
        CoreArgCheck.isNotEmpty(propId, "propId is empty"); //$NON-NLS-1$

        ModelExtensionRegistry registry = getRegistry();
        return registry.getPropertyDefinition(modelObject.getClass().getName(), propId);
    }

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
     * Must be called only within the Eclipse runtime.
     * 
     * @return the model extension registry (never <code>null</code>)
     */
    protected ModelExtensionRegistry getRegistry() {
        return ExtensionPlugin.getInstance().getRegistry();
    }

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
     * @param namespacePrefix the namespace prefix of the model extension definition being deleted (cannot be <code>null</code>)
     * @throws Exception if the model extension definition could not be removed
     */
    public abstract void removeModelExtensionDefinition( Object modelObject,
                                                         String namespacePrefix ) throws Exception;

    /**
     * @param modelObject the model object whose property is being removed (cannot be <code>null</code>)
     * @param propId the extension property full identifier (cannot be <code>null</code>)
     * @throws Exception if the extension property could not be removed
     */
    protected abstract void removeProperty( Object modelObject,
                                            String propId ) throws Exception;

    /**
     * @param modelResource the model object or resource where the specified definition will be saved to (cannot be
     *            <code>null</code>)
     * @param definition the model extension definition that needs to be saved (cannot be <code>null</code>)
     * @throws Exception if there is a problem saving the definition
     */
    public abstract void saveModelExtensionDefinition( Object modelObject,
                                                       ModelExtensionDefinition definition ) throws Exception;

    /**
     * @param propertyDefinition the property definition (cannot be <code>null</code>)
     * @param allowedValues the new allowed values (can be <code>null</code>)
     */
    public void setAllowedValues( ModelExtensionPropertyDefinition propertyDefinition,
                                  String[] newAllowedValues ) {
        CoreArgCheck.isNotNull(propertyDefinition, "propertyDefinition is null"); //$NON-NLS-1$
        propertyDefinition.setAllowedValues(newAllowedValues);
    }

    /**
     * @param modelExtensionDefinition the model extension definition whose description is being changed (cannot be
     *            <code>null</code>)
     * @param newDescription the new definition description (can be <code>null</code> or empty)
     */
    public void setDescription( ModelExtensionDefinition modelExtensionDefinition,
                                String newDescription ) {
        CoreArgCheck.isNotNull(modelExtensionDefinition, "modelExtensionDefinition is null"); //$NON-NLS-1$
        modelExtensionDefinition.setDescription(newDescription);
    }

    /**
     * @param propertyDefinition the property definition whose description is being changed (cannot be <code>null</code>)
     * @param newDescription the new property definition description (can be <code>null</code> or empty)
     */
    public void setDescription( ModelExtensionPropertyDefinition propertyDefinition,
                                String newDescription ) {
        CoreArgCheck.isNotNull(propertyDefinition, "propertyDefinition is null"); //$NON-NLS-1$
        propertyDefinition.setDescription(newDescription);
    }

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
     * If the version string is not parsable into an integer the default version is used.
     * 
     * @param modelExtensionDefinition the model extension definition whose version is being changed (cannot be <code>null</code>)
     * @param versionString the version string (must be a string that can be parsed into an integer)
     * @see ModelExtensionDefinition#DEFAULT_VERSION
     */
    public void setVersion( ModelExtensionDefinition modelExtensionDefinition,
                            String versionString ) {
        CoreArgCheck.isNotNull(modelExtensionDefinition, "modelExtensionDefinition is null"); //$NON-NLS-1$

        try {
            int version = Integer.parseInt(versionString);
            modelExtensionDefinition.setVersion(version);
        } catch (NumberFormatException e) {
            Util.log(IStatus.ERROR, e, NLS.bind(Messages.invalidDefinitionFileVersion,
                                                modelExtensionDefinition.getNamespacePrefix(),
                                                ModelExtensionDefinition.DEFAULT_VERSION));
            modelExtensionDefinition.setVersion(ModelExtensionDefinition.DEFAULT_VERSION);
        }
    }

    /**
     * @param modelObject the model object whose resource is being checked to see if it has the specified model extension definition
     *            saved (cannot be <code>null</code>)
     * @param namespacePrefix the namespace prefix being looked for (cannot be <code>null</code> or empty)
     * @return <code>true</code> if the model object's resource contains the namespace prefix
     * @throws Exception if there is a problem checking the model object's resource
     */
    public abstract boolean supports( Object modelObject,
                                      String namespacePrefix ) throws Exception;

    /**
     * Removes unregistered model extension definitions and unregistered extension properties stored in the model object's resource.
     * 
     * @param modelObject the model object whose extension definitions and properties are being synchronized (cannot be
     *            <code>null</code>)
     * @throws Exception if there is a problem working with the model object's resource
     */
    public void syncProperties( Object modelObject ) throws Exception {
        ModelExtensionRegistry registry = getRegistry();

        // remove non-registered definitions
        for (String namespacePrefix : getSupportedNamespaces(modelObject)) {
            // if definition is registered just save it to make sure it is the latest version
            if (registry.isRegistered(namespacePrefix)) {
                saveModelExtensionDefinition(modelObject, registry.getDefinition(namespacePrefix));
            } else {
                removeModelExtensionDefinition(modelObject, namespacePrefix);
            }
        }

        // remove non-registered properties
        for (String propId : getPropertyValues(modelObject).stringPropertyNames()) {
            String namespacePrefix = ModelExtensionPropertyDefinition.Utils.getNamespacePrefix(propId);

            if (CoreStringUtil.isEmpty(namespacePrefix) || !registry.isRegistered(namespacePrefix)) {
                removeProperty(modelObject, propId);
            }
        }
    }

}
