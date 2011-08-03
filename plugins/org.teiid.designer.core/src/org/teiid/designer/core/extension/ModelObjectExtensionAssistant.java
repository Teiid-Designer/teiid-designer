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
import java.util.Collections;
import java.util.Properties;

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
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * The <code>ModelObjectExtensionAssistant</code> is a model extension assistant that knows how to work with {@link EObject}s.
 */
public abstract class ModelObjectExtensionAssistant extends ModelExtensionAssistant {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ModelObjectExtensionAssistant.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#contains(java.lang.Object,
     *      org.teiid.designer.extension.definition.ModelExtensionDefinition)
     */
    @Override
    protected boolean contains( Object modelObject,
                                ModelExtensionDefinition definition ) {
        // TODO implement contains
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getModelExtensionDefinitions(java.lang.Object)
     */
    @Override
    protected Collection<ModelExtensionDefinition> getModelExtensionDefinitions( Object modelObject ) {
        // TODO implement contains
        return Collections.emptyList();
    }

    /**
     * @param modelObject the model object whose annotationis being requested (cannot be <code>null</code>)
     * @param forceCreate <code>true</code> if the annotation should be created if it does not exist
     * @return the annotation (never <code>null</code>)
     * @throws ModelerCoreException
     */
    protected Annotation getModelObjectAnnotation( EObject modelObject,
                                                   boolean forceCreate ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject is null"); //$NON-NLS-1$
        return ModelerCore.getModelEditor().getAnnotation(modelObject, forceCreate);
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
     */
    @Override
    public String getOverriddenValue( Object modelObject,
                                      String propId ) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);
        Annotation annotation = getModelObjectAnnotation((EObject)modelObject, false);

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
     */
    @Override
    public Properties getOverriddenValues( Object modelObject ) throws Exception {
        CoreArgCheck.isNotNull(modelObject, "modelObject is null"); //$NON-NLS-1$
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);

        Annotation annotation = getModelObjectAnnotation((EObject)modelObject, false);
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
            assert propDefn != null : "propDefn should not be null because getOverriddenValue checks this"; //$NON-NLS-1$
            return propDefn.getDefaultValue();
        }

        return value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getExtensionPropertyValues(java.lang.Object)
     * @throws IllegalArgumentException if the model object is <code>null</code> or not an {@link EObject}
     */
    @Override
    public Properties getPropertyValues( Object modelObject ) throws Exception {
        CoreArgCheck.isNotNull(modelObject, "modelObject is null"); //$NON-NLS-1$
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);

        Properties props = getOverriddenValues(modelObject);

        // need to add in properties that have default values
        for (ModelExtensionPropertyDefinition propDefn : getRegistry().getPropertyDefinitions(modelObject.getClass().getName())) {
            if (!props.containsKey(propDefn.getId())) {
                String defaultValue = propDefn.getDefaultValue();
                props.put(propDefn.getId(), (CoreStringUtil.isEmpty(defaultValue) ? CoreStringUtil.Constants.EMPTY_STRING
                                                                                 : defaultValue));
            }
        }

        return props;
    }

    /**
     * Retrieves the tagged resource <code>Annotation</code> object referenced to a <code>ModelResource</code>'s
     * <code>ModelAnnotation</code>
     * 
     * @param modelResource the <code>ModelResource</code>. may not be null
     * @param forceCreate forces creation of the annotation if it does not exist.
     * @return the <code>Annotation</code>
     * @throws ModelWorkspaceException
     */
    protected Annotation getResourceAnnotation( ModelResource modelResource,
                                                boolean forceCreate ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        Annotation annotation = null;

        if ((modelResource.getAnnotations() != null) && (modelResource.getModelAnnotation() != null)) {
            annotation = modelResource.getAnnotations().getAnnotation(modelResource.getModelAnnotation());

            if ((annotation == null) && forceCreate) {
                annotation = ModelResourceContainerFactory.createNewAnnotation(modelResource.getModelAnnotation(),
                                                                               modelResource.getEmfResource());

                ModelContents contents = ModelerCore.getModelEditor().getModelContents(modelResource);
                AnnotationContainer ac = contents.getAnnotationContainer(false);

                if (ac != null) {
                    annotation.setAnnotationContainer(ac);
                }
            }
        }

        return annotation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#hasExtensionProperties(java.io.File, java.lang.String)
     */
    @Override
    public boolean hasExtensionProperties( File file,
                                           String namespacePrefix ) throws Exception {
        // TODO implement hasExtensionProperties
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#removeModelExtensionDefinition(java.lang.Object,
     *      org.teiid.designer.extension.definition.ModelExtensionDefinition)
     */
    @Override
    protected void removeModelExtensionDefinition( Object modelObject,
                                                   ModelExtensionDefinition definition ) {
        // TODO implement removeModelExtensionDefinition
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#removeProperty(java.lang.Object, java.lang.String)
     */
    @Override
    protected void removeProperty( Object modelObject,
                                   String id ) throws Exception {
        CoreArgCheck.isNotNull(modelObject, "modelObject is null"); //$NON-NLS-1$
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);
        CoreArgCheck.isNotEmpty(id, "id is empty"); //$NON-NLS-1$

        Annotation annotation = ModelerCore.getModelEditor().getAnnotation((EObject)modelObject, false);

        if (annotation != null) {
            annotation.getTags().remove(id);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#saveModelExtensionDefinition(java.lang.Object,
     *      org.teiid.designer.extension.definition.ModelExtensionDefinition)
     */
    @Override
    protected void saveModelExtensionDefinition( Object modelObject,
                                                 ModelExtensionDefinition definition ) throws Exception {
        CoreArgCheck.isNotNull(modelObject, "modelObject is null"); //$NON-NLS-1$
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);

        // ModelResource modelResource = ModelerCore.getModelEditor().findModelResource((EObject)modelObject);
        // Annotation annotation = getResourceAnnotation(modelResource, true);
        // EMap<String, String> tags = annotation.getTags();

        // TODO implement saveModelExtensionDefinition
        //
        // String uuid = tags.put(definition.getNamespacePrefix(), property.getValue());
        // tags.g
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
        Annotation annotation = ModelerCore.getModelEditor().getAnnotation((EObject)modelObject, true);

        // only set value if different than the default
        if (CoreStringUtil.equals(propDefn.getDefaultValue(), newValue)) {
            // remove existing value from model object
            annotation.getTags().removeKey(propId);
        } else {
            annotation.getTags().put(propId, newValue);
        }
    }
}
