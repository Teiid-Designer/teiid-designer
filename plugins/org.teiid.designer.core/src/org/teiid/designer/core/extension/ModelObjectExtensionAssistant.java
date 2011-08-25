/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.extension;

import static com.metamatrix.modeler.core.ModelerCore.Util;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * The <code>ModelObjectExtensionAssistant</code> is a model extension assistant that knows how to work with {@link EObject}s.
 */
public abstract class ModelObjectExtensionAssistant extends ModelExtensionAssistant {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ModelObjectExtensionAssistant.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#containsModelExtensionDefinition(java.lang.Object,
     *      java.lang.String)
     * @throws IllegalArgumentException if the model object is not an {@link EObject} or {@link ModelResource} or if the model
     *             resource cannot be found
     */
    @Override
    protected boolean containsModelExtensionDefinition( Object modelObject,
                                                        String namespacePrefix ) throws Exception {
        return ModelExtensionUtils.containsModelExtensionDefinition(getModelResource(modelObject), namespacePrefix);
    }

    /**
     * @param modelObject the model object (must be either an
     * @return the model resource (never <code>null</code>)
     * @throws IllegalArgumentException if the model object is not either an {@link EObject} or a {@link ModelResource}
     */
    protected ModelResource getModelResource( Object modelObject ) throws Exception {
        ModelResource modelResource = null;

        if (modelObject instanceof ModelResource) {
            modelResource = (ModelResource)modelObject;
        } else if (modelObject instanceof EObject) {
            modelResource = ModelerCore.getModelEditor().findModelResource((EObject)modelObject);
        } else if (modelObject instanceof IFile) {
            modelResource = ModelerCore.getModelEditor().findModelResource((IFile)modelObject);
        }

        // should have a model resource
        if (modelResource == null) {
            CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$
        }

        return modelResource;
    }

    /**
     * @param propId the property identifier whose namespace is being requested (cannot be <code>null</code> or empty)
     * @return the namespace prefix or <code>null</code> if not found
     */
    protected String getNamespacePrefix( String propId ) {
        CoreArgCheck.isNotEmpty(propId, "propId is empty"); //$NON-NLS-1$
        return ModelExtensionPropertyDefinition.Utils.getNamespacePrefix(propId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getOverriddenValue(java.lang.Object, java.lang.String)
     * @throws IllegalArgumentException if the model object is not an {@link EObject}
     */
    @Override
    public String getOverriddenValue( Object modelObject,
                                      String propId ) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);
        Annotation annotation = ModelExtensionUtils.getModelObjectAnnotation((EObject)modelObject, false);

        if (annotation != null) {
            EMap<String, String> tags = annotation.getTags();
            return getOverriddenValue(modelObject, propId, tags.get(propId));
        }

        // model object is using default value
        return null;
    }

    /**
     * @param modelObject the model object whose property value is being requested (cannot be <code>null</code>)
     * @param propId the identifier of the property being checked (cannot be <code>null</code> or empty)
     * @param currentValue the model object's current property value (can be <code>null</code> or empty)
     * @return the overridden value (never <code>null</code> but can be empty)
     * @throws Exception if there is a problem obtaining the property value
     */
    protected String getOverriddenValue( Object modelObject,
                                         String propId,
                                         String currentValue ) throws Exception {
        CoreArgCheck.isNotEmpty(propId, "propId is empty"); //$NON-NLS-1$

        String namespacePrefix = getNamespacePrefix(propId);

        // make sure there is a namespace
        if (CoreStringUtil.isEmpty(namespacePrefix)) {
            throw new Exception(Util.getString(PREFIX + "emptyNamespacePrefix", propId)); //$NON-NLS-1$
        }

        // make sure namespace is registered
        if (!getRegistry().isRegistered(namespacePrefix)) {
            throw new Exception(Util.getString(PREFIX + "namespacePrefixUnregistered", propId)); //$NON-NLS-1$
        }

        // make sure the property definition is found
        PropertyDefinition propDefn = getPropertyDefinition(modelObject, propId);

        if (propDefn == null) {
            throw new Exception(Util.getString(PREFIX + "propertyDefinitiontNotFound", propId)); //$NON-NLS-1$
        }

        String defaultValue = propDefn.getDefaultValue();

        // don't add if value equals default value
        if ((CoreStringUtil.isEmpty(currentValue) && CoreStringUtil.isEmpty(defaultValue))
                || CoreStringUtil.equals(currentValue, defaultValue)) {
            currentValue = null;
        }

        // value is different than default value
        return currentValue;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getOverriddenValues(java.lang.Object)
     * @throws IllegalArgumentException if the model object is not an {@link EObject}
     */
    @Override
    public Properties getOverriddenValues( Object modelObject ) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);

        Annotation annotation = ModelExtensionUtils.getModelObjectAnnotation((EObject)modelObject, false);
        Properties props = new Properties();

        if (annotation != null) {
            EMap<String, String> tags = annotation.getTags();

            for (String propId : tags.keySet()) {
                try {
                    String overridenValue = getOverriddenValue(modelObject, propId, tags.get(propId));

                    if (!CoreStringUtil.isEmpty(overridenValue)) {
                        props.put(propId, overridenValue);
                    }
                } catch (Exception e) {
                    Util.log(e);
                }
            }
        }

        return props;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getExtensionPropertyValue(java.lang.Object,
     *      java.lang.String)
     */
    @Override
    public String getPropertyValue( Object modelObject,
                                    String propId ) throws Exception {
        String value = getOverriddenValue(modelObject, propId);

        // if no overridden value then return default value
        if (CoreStringUtil.isEmpty(value)) {
            PropertyDefinition propDefn = getPropertyDefinition(modelObject, propId);
            assert propDefn != null : "propDefn '" + propId + "' should not be null because getOverriddenValue checks this"; //$NON-NLS-1$ //$NON-NLS-2$
            return propDefn.getDefaultValue();
        }

        return value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getExtensionPropertyValues(java.lang.Object)
     * @throws IllegalArgumentException if the model object is not an {@link EObject}
     */
    @Override
    public Properties getPropertyValues( Object modelObject ) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);

        ModelExtensionRegistry registry = getRegistry();
        Properties props = getOverriddenValues(modelObject);

        // need to add in properties that have default values so see what MEDs are contained in the model
        Collection<ModelExtensionAssistant> assistants = registry.getModelExtensionAssistants(modelObject.getClass().getName());

        // just return props if no assistants found
        if (assistants.isEmpty()) {
            return props;
        }

        // just take first assistant
        ModelExtensionAssistant assistant = assistants.iterator().next();
        String metaclassName = modelObject.getClass().getName();

        for (String savedNamespacePrefix : assistant.getSupportedNamespaces(modelObject)) {
            for (ModelExtensionPropertyDefinition propDefn : registry.getPropertyDefinitions(savedNamespacePrefix, metaclassName)) {
                if (!props.containsKey(propDefn.getId())) {
                    String defaultValue = propDefn.getDefaultValue();

                    // add only if there is a default value
                    if (!CoreStringUtil.isEmpty(defaultValue)) {
                        props.put(propDefn.getId(), defaultValue);
                    }
                }
            }
        }

        return props;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getSupportedNamespaces(java.lang.Object)
     */
    @Override
    public Collection<String> getSupportedNamespaces( Object modelObject ) throws Exception {
        return ModelExtensionUtils.getSupportedNamespaces(getModelResource(modelObject));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#hasExtensionProperties(java.io.File)
     */
    @Override
    public boolean hasExtensionProperties( File file ) throws Exception {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath location = Path.fromOSString(file.getAbsolutePath());
        IFile modelFile = workspace.getRoot().getFileForLocation(location);

        if ((modelFile != null) && ModelUtil.isModelFile(modelFile.getFullPath())) {
            return getSupportedNamespaces(modelFile).contains(getNamespacePrefix());
        }

        // none found
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#hasExtensionProperties(java.lang.Object)
     */
    @Override
    public boolean hasExtensionProperties( Object modelObject ) throws Exception {
        return !getPropertyValues(modelObject).isEmpty();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#isModelExtensionDefinitionRelated(java.lang.Object)
     * @throws IllegalArgumentException if the model object is not an {@link EObject}
     */
    @Override
    public boolean isModelExtensionDefinitionRelated( Object modelObject ) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);
        return ModelExtensionUtils.isModelExtensionDefinitionRelated((EObject)modelObject);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#removeModelExtensionDefinition(java.lang.Object,
     *      java.lang.String)
     */
    @Override
    public void removeModelExtensionDefinition( Object modelObject,
                                                String namespacePrefix ) throws Exception {
        ModelExtensionUtils.removeModelExtensionDefinition(getModelResource(modelObject), namespacePrefix);

        // TODO remove any other model objects overridden properties
        for (String propId : getOverriddenValues(modelObject).stringPropertyNames()) {
            removeProperty(modelObject, propId);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#removeProperty(java.lang.Object, java.lang.String)
     */
    @Override
    protected void removeProperty( Object modelObject,
                                   String propId ) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);
        ModelExtensionUtils.removeProperty((EObject)modelObject, propId, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#saveModelExtensionDefinition(java.lang.Object,
     *      org.teiid.designer.extension.definition.ModelExtensionDefinition)
     */
    @Override
    public void saveModelExtensionDefinition( Object modelObject,
                                              ModelExtensionDefinition definition ) throws Exception {
        ModelExtensionUtils.updateModelExtensionDefinition(getModelResource(modelObject), definition);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#setExtensionPropertyValue(java.lang.Object,
     *      java.lang.String, java.lang.String)
     */
    @Override
    public void setPropertyValue( Object modelObject,
                                  String propId,
                                  String newValue ) throws Exception {
        PropertyDefinition propDefn = getPropertyDefinition(modelObject, propId);

        if (propDefn == null) {
            throw new Exception(Util.getString(PREFIX + "propertyDefinitiontNotFound", propId)); //$NON-NLS-1$
        }

        CoreArgCheck.isInstanceOf(EObject.class, modelObject);

        boolean valueIsDefault = CoreStringUtil.equals(propDefn.getDefaultValue(), newValue);
        Annotation annotation = ModelExtensionUtils.getModelObjectAnnotation((EObject)modelObject, false);

        // default values are not saved in the model object annotation
        if (valueIsDefault) {
            if (annotation != null) {
                annotation.getTags().removeKey(propId);
            }
        } else {
            // value is not equal to default so create annotation if necessary
            if (annotation == null) {
                annotation = ModelExtensionUtils.getModelObjectAnnotation((EObject)modelObject, true);
            }

            // only save if new value is different than old value
            String oldValue = (String)annotation.getTags().get(propId);

            // only set value if different than the default
            if (!CoreStringUtil.equals(oldValue, newValue)) {
                // remove key if empty value
                if (CoreStringUtil.isEmpty(newValue)) {
                    annotation.getTags().removeKey(propId);
                } else {
                    annotation.getTags().put(propId, newValue);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#supports(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean supports( Object modelObject,
                             String namespacePrefix ) throws Exception {
        for (String supportedNamespacePrefix : getSupportedNamespaces(modelObject)) {
            if (supportedNamespacePrefix.equals(namespacePrefix)) {
                return true;
            }
        }

        return false;
    }

}
