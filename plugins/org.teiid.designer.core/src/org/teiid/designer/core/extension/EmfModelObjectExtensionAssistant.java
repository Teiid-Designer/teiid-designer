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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.properties.PropertyDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelObjectClassNameVisitor;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * The <code>EmfModelObjectExtensionAssistant</code> is a model extension assistant that knows how to work with {@link EObject}s.
 */
public class EmfModelObjectExtensionAssistant extends ModelObjectExtensionAssistant {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(EmfModelObjectExtensionAssistant.class);

    /**
     * @param modelObject the model object (must be either an {@link EObject}, {@link ModelResource}, or an {@link IFile}.
     * @return the model resource or <code>null</code> if not found
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
        // make sure the property definition is found
        PropertyDefinition propDefn = getPropertyDefinition(modelObject, propId);

        if (propDefn == null) {
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
                ModelExtensionDefinition med = getModelExtensionDefinition();
                EMap<String, String> tags = annotation.getTags();

                for (String propId : tags.keySet()) {
                    // only get properties of my namespace
                    if (ModelExtensionPropertyDefinition.Utils.isExtensionPropertyId(propId, med)) {
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
     * @param modelObject the model object whose property definition is being requested (cannot be <code>null</code>)
     * @param propId the property identifier whose property definition is being requested (cannot be <code>null</code> or empty)
     * @return the property definition or <code>null</code> if not found
     */
    protected ModelExtensionPropertyDefinition getPropertyDefinition( Object modelObject,
                                                                      String propId ) {
        CoreArgCheck.isNotNull(modelObject, "modelObject is null"); //$NON-NLS-1$

        // make sure right namespace
        if (ModelExtensionPropertyDefinition.Utils.isExtensionPropertyId(propId, getModelExtensionDefinition())) {
            return getModelExtensionDefinition().getPropertyDefinition(modelObject.getClass().getName(), propId);
        }

        return null;
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
     * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#getSupportedNamespaces(java.lang.Object)
     */
    @Override
    public Collection<String> getSupportedNamespaces( Object modelObject ) throws Exception {
        ModelResource modelResource = getModelResource(modelObject);

        if (modelResource == null) {
            return Collections.emptyList();
        }

        return ModelExtensionUtils.getSupportedNamespaces(modelResource);
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
            ModelResource modelResource = getModelResource(modelFile);

            if (modelResource != null) {
                return ModelExtensionUtils.getSupportedNamespaces(modelResource).contains(getNamespacePrefix());
            }
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
     * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#getModelExtensionDefinition(java.lang.Object)
     */
    @Override
    public ModelExtensionDefinition getModelExtensionDefinition( Object modelObject ) throws Exception {
        ModelResource modelResource = getModelResource(modelObject);

        if (modelResource == null) {
            return null;
        }

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

        if (modelResource != null) {
            ModelExtensionUtils.removeModelExtensionDefinition(modelResource, getNamespacePrefix());

            // find model objects with classes that match the extended metaclasses in the MED
            ModelExtensionDefinition definition = getModelExtensionDefinition();
            String[] metaclasses = definition.getExtendedMetaclasses();
            ModelObjectClassNameVisitor visitor = new ModelObjectClassNameVisitor(Arrays.asList(metaclasses));
            ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor, ModelVisitorProcessor.MODE_VISIBLE_CONTAINMENTS);
            processor.walk(modelResource, ModelVisitorProcessor.DEPTH_INFINITE);

            // remove overridden properties
            for (EObject eObject : visitor.getResult()) {
                Annotation annotation = ModelExtensionUtils.getModelObjectAnnotation(eObject, false);

                if (annotation != null) {
                    String metaclassName = eObject.getClass().getName();

                    for (ModelExtensionPropertyDefinition propDefn : definition.getPropertyDefinitions(metaclassName)) {
                        removeProperty(eObject, propDefn.getId());
                    }
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

        // make sure property defined in MED
        if (!ModelExtensionPropertyDefinition.Utils.isExtensionPropertyId(propId, getModelExtensionDefinition())) {
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
        ModelResource modelResource = getModelResource(modelObject);
        assert (modelResource !=  null) : "Model resource should not be null"; //$NON-NLS-1$
        boolean update = supportsMyNamespace(modelObject); // see if this is an update
        ModelExtensionUtils.updateModelExtensionDefinition(modelResource, getModelExtensionDefinition());

        // remove any properties that are no longer supported by the MED
        if (update) {
//            ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
//
//            for (Object eObject : modelResource.getEObjects()) {
//                assert eObject instanceof EObject;
//                // TODO remove orphaned properties from model object. getOverriddenValues only returns registered properties
//                Properties overriddenProperties = getOverriddenValues(eObject);
//
//                if (!overriddenProperties.isEmpty()) {
//                    String metaclassName = eObject.getClass().getName();
//
//                    for (String propId : overriddenProperties.stringPropertyNames()) {
//                        // remove property if it doesn't exist any more
//                        if (registry.getPropertyDefinition(metaclassName, propId) == null) {
//                            removeProperty(modelObject, propId);
//                        }
//                    }
//                }
//            }
        }
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
        Annotation annotation = ModelExtensionUtils.getModelObjectAnnotation((EObject)modelObject, true);

        // default values are not saved in the model object annotation
        if (valueIsDefault) {
            if (annotation != null) {
                annotation.getTags().removeKey(propId);

                // delete the model object annotation if no more properties
                if (annotation.getTags().isEmpty()) {
                    ModelResourceContainerFactory.deleteAnnotation(annotation);
                }
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
     * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#supportsMedOperation(java.lang.String,
     *      java.lang.Object)
     */
    @Override
    public boolean supportsMedOperation( String proposedOperationName,
                                         Object context ) {
        CoreArgCheck.isNotEmpty(proposedOperationName, "proposedOperationName is empty"); //$NON-NLS-1$

        try {
            if (context instanceof IFile) {
                ModelResource modelResource = getModelResource(context);

                if (modelResource == null) {
                    return false;
                }

                Collection<String> modelTypes = getModelExtensionDefinition().getSupportedModelTypes();

                // if supported model types is empty then all all model types are supported
                if (!modelTypes.isEmpty()) {
                    String modelTypeLiteral = modelResource.getModelType().getLiteral();

                    if (!modelTypes.contains(modelTypeLiteral)) {
                        return false; // model type not supported
                    }
                }

                if (MedOperations.ADD_MED_TO_MODEL.equals(proposedOperationName)) {
                    // model must NOT be currently supporting namespace
                    if (!supportsMyNamespace(modelResource)) {
                        // make sure model is of right metamodel URI to be extended by the MED
                        String metamodelUri = getModelExtensionDefinition().getMetamodelUri();

                        // MED does not have URI yet
                        if (CoreStringUtil.isEmpty(metamodelUri)) {
                            return false;
                        }

                        // check to make sure MED metamodel URI is the same as model's
                        return metamodelUri.equals(modelResource.getPrimaryMetamodelUri());
                    }

                    // model already has namespace stored so can't be added again
                    return false;
                }

                if (MedOperations.DELETE_MED_FROM_MODEL.equals(proposedOperationName)) {
                    return supportsMyNamespace(modelResource); // model must be currently supporting namespace
                }
            }
        } catch (Exception e) {
            Util.log(e);
            return false;
        }

        return super.supportsMedOperation(proposedOperationName, context);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#namespaceSupportedBy(java.lang.Object)
     */
    @Override
    public boolean supportsMyNamespace( Object modelObject ) throws Exception {
        ModelResource modelResource = getModelResource(modelObject);

        if (modelResource == null) {
            return false;
        }

        return ModelExtensionUtils.isSupportedNamespace(modelResource, getNamespacePrefix());
    }

}
