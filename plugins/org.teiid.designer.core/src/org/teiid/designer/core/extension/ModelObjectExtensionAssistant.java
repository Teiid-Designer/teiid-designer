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
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#namespaceSupportedBy(java.lang.Object)
     */
    @Override
    public boolean supportsMyNamespace( Object modelObject ) throws Exception {
        return ModelExtensionUtils.isSupportedNamespace(getModelResource(modelObject), getNamespacePrefix());
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
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getOverriddenValue(java.lang.Object, java.lang.String)
     * @throws IllegalArgumentException if the model object is not an {@link EObject}, if property is from a different namespace, or
     *             if the associated property definition can't be found
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
        // make sure right namespace
        if (!ModelExtensionPropertyDefinition.Utils.isExtensionPropertyId(propId, getNamespacePrefix())) {
            throw new Exception(Util.getString(PREFIX + "wrongNamespacePrefix", propId, getNamespacePrefix())); //$NON-NLS-1$
        }

        // make sure the property definition is found
        PropertyDefinition propDefn = getPropertyDefinition(modelObject, propId);

        if (propDefn == null) {
            Util.log(Util.getString(PREFIX + "propertyDefinitionNotFound", propId)); //$NON-NLS-1$
            return null;
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
        Properties props = new Properties();

        if (supportsMyNamespace(modelObject)) {
            Annotation annotation = ModelExtensionUtils.getModelObjectAnnotation((EObject)modelObject, false);

            if (annotation != null) {
                EMap<String, String> tags = annotation.getTags();

                for (String propId : tags.keySet()) {
                    // only get properties of my namespace
                    if (getNamespacePrefix().equals(ModelExtensionPropertyDefinition.Utils.getNamespacePrefix(propId))) {
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

        // if no overridden value then return default value if property definition exists
        if (CoreStringUtil.isEmpty(value)) {
            PropertyDefinition propDefn = getPropertyDefinition(modelObject, propId);
            return ((propDefn == null) ? null : propDefn.getDefaultValue());
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
        Properties props = new Properties();

        if (supportsMyNamespace(modelObject)) {
            // get properties with overridden values
            props = getOverriddenValues(modelObject);

            // add properties using default value
            for (ModelExtensionPropertyDefinition propDefn : getModelExtensionDefinition().getPropertyDefinitions(modelObject.getClass()
                                                                                                                             .getName())) {
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
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#hasExtensionProperties(java.io.File)
     */
    @Override
    public boolean hasExtensionProperties( File file ) throws Exception {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath location = Path.fromOSString(file.getAbsolutePath());
        IFile modelFile = workspace.getRoot().getFileForLocation(location);

        if ((modelFile != null) && ModelUtil.isModelFile(modelFile.getFullPath())) {
            return ModelExtensionUtils.getSupportedNamespaces(getModelResource(modelFile)).contains(getNamespacePrefix());
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
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getModelExtensionDefinition(java.lang.Object)
     */
    @Override
    public ModelExtensionDefinition getModelExtensionDefinition( Object modelObject ) throws Exception {
        ModelResource modelResource = getModelResource(modelObject);
        return ModelExtensionUtils.getModelExtensionDefinition(this, modelResource);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#removeModelExtensionDefinition(java.lang.Object)
     */
    @Override
    public void removeModelExtensionDefinition( Object modelObject ) throws Exception {
        ModelResource modelResource = getModelResource(modelObject);
        ModelExtensionUtils.removeModelExtensionDefinition(modelResource, getNamespacePrefix());

        // remove any other model objects overridden properties
        ModelExtensionDefinition definition = getModelExtensionDefinition();

        for (Object eObject : modelResource.getEObjects()) {
            assert eObject instanceof EObject;
            Annotation annotation = ModelExtensionUtils.getModelObjectAnnotation((EObject)modelObject, false);

            if (annotation != null) {
                String metaclassName = eObject.getClass().getName();

                for (ModelExtensionPropertyDefinition propDefn : definition.getPropertyDefinitions(metaclassName)) {
                    removeProperty(eObject, propDefn.getId());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#removeProperty(java.lang.Object, java.lang.String)
     */
    @Override
    public void removeProperty( Object modelObject,
                                String propId ) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);

        // make sure right namespace
        if (!ModelExtensionPropertyDefinition.Utils.isExtensionPropertyId(propId, getNamespacePrefix())) {
            throw new Exception(Util.getString(PREFIX + "wrongNamespacePrefix", propId, getNamespacePrefix())); //$NON-NLS-1$
        }

        ModelExtensionUtils.removeProperty((EObject)modelObject, propId, true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#saveModelExtensionDefinition(java.lang.Object)
     */
    @Override
    public void saveModelExtensionDefinition( Object modelObject ) throws Exception {
        ModelExtensionUtils.updateModelExtensionDefinition(getModelResource(modelObject), getModelExtensionDefinition());
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
            throw new Exception(Util.getString(PREFIX + "propertyDefinitionNotFound", propId)); //$NON-NLS-1$
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

}
