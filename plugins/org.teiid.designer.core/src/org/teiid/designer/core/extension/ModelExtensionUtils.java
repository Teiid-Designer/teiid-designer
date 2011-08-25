/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.extension;

import static com.metamatrix.modeler.core.ModelerCore.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
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
 * Utilities that manage persisting of model extension definitions in model objects.
 */
public class ModelExtensionUtils {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelExtensionUtils.class);

    private static final String DEFN_PREFIX = "modelExtensionDefinition"; //$NON-NLS-1$

    private static final char DELIM = ':';

    private static final String EXTENDED_METACLASS_PREFIX = "extendedMetaclass"; //$NON-NLS-1$

    private static final String PROP_DEFN_PREFIX = "propertyDefinition"; //$NON-NLS-1$

    private static String constructKey( String prefix,
                                        String id ) {
        return prefix + DELIM + id;
    }

    /**
     * @param modelResource the model resource being checked (cannot be <code>null</code>)
     * @param namespacePrefix the namespace prefix of the model extension definition being looked for (cannot be <code>null</code>
     *            or empty)
     * @return <code>true</code> if the model resource contains the model extension definition with the specified namespace prefix
     * @throws Exception if there is a problem accessing the model resource
     */
    public static boolean containsModelExtensionDefinition( ModelResource modelResource,
                                                            String namespacePrefix ) throws Exception {
        // transaction logic not needed as this is a readonly operation
        CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$
        return (getDefinitionAnnotation(modelResource, false, namespacePrefix) != null);
    }

    private static Annotation getAnnotation( ModelResource modelResource,
                                             Annotation parentAnnotation,
                                             String tagId,
                                             String value,
                                             boolean forceCreate ) throws Exception {
        // transaction logic not needed as this is not a public method
        CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(parentAnnotation, "parentAnnotation is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(tagId, "tagId is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(value, "value is null"); //$NON-NLS-1$

        Annotation annotation = null;
        EMap<String, String> tags = parentAnnotation.getTags();

        if (!tags.containsKey(tagId)) {
            tags.put(tagId, value);
        }

        // find the tag entry associated with the ID
        for (Object obj : tags.entrySet()) {
            EStringToStringMapEntryImpl entry = (EStringToStringMapEntryImpl)obj;

            if (entry.getValue().equals(value)) {
                annotation = getModelObjectAnnotation(entry, false);

                if ((annotation == null) && forceCreate) {
                    annotation = getModelObjectAnnotation(entry, true);
                }

                break;
            }
        }

        return annotation;
    }

    private static Annotation getDefinitionAnnotation( ModelResource modelResource,
                                                       boolean forceCreate,
                                                       String namespacePrefix ) throws Exception {
        // transaction logic not needed as this is not a public method
        Annotation annotation = getResourceAnnotation(modelResource, forceCreate);

        if (annotation != null) {
            String key = constructKey(DEFN_PREFIX, namespacePrefix);
            return getAnnotation(modelResource, annotation, key, namespacePrefix, forceCreate);
        }

        return null;
    }

    private static Annotation getModelObjectAnnotationImpl( EObject modelObject,
                                                            boolean forceCreate ) throws ModelerCoreException {
        // transaction logic not needed as this is not a public method
        CoreArgCheck.isNotNull(modelObject, "modelObject is null"); //$NON-NLS-1$
        return ModelerCore.getModelEditor().getAnnotation(modelObject, forceCreate);
    }

    /**
     * @param modelObject the model object whose annotation is being requested (cannot be <code>null</code>)
     * @param forceCreate <code>true</code> if the annotation should be created if it does not exist
     * @return the annotation (never <code>null</code>)
     * @throws ModelerCoreException
     */
    public static Annotation getModelObjectAnnotation( EObject modelObject,
                                                       boolean forceCreate ) throws ModelerCoreException {
        // transaction needed if forcing creation of objects
        if (forceCreate) {
            boolean requiredStart = ModelerCore.startTxn(true, true, Util.getString(I18N_PREFIX + "getModelObjectAnnotation"), //$NON-NLS-1$
                                                         modelObject);
            boolean succeeded = false;

            try {
                Annotation annotation = getModelObjectAnnotationImpl(modelObject, true);
                return annotation;
            } finally {
                // if we started the transaction, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        return getModelObjectAnnotationImpl(modelObject, false);
    }

    private static Annotation getResourceAnnotationImpl( ModelResource modelResource,
                                                         boolean forceCreate ) throws ModelWorkspaceException {
        // transaction logic not needed as this is not a public method
        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        Annotation annotation = null;

        if ((modelResource.getAnnotations() != null) && (modelResource.getModelAnnotation() != null)) {
            annotation = modelResource.getAnnotations().getAnnotation(modelResource.getModelAnnotation());

            if ((annotation == null) && forceCreate) {
                annotation = ModelResourceContainerFactory.createNewAnnotation(modelResource.getModelAnnotation(),
                                                                               modelResource.getEmfResource());

                ModelContents contents = ModelerCore.getModelEditor().getModelContents(modelResource);
                AnnotationContainer ac = contents.getAnnotationContainer(true);

                if (ac != null) {
                    annotation.setAnnotationContainer(ac);
                }
            }
        }

        return annotation;
    }

    /**
     * Retrieves the tagged resource <code>Annotation</code> object referenced to a <code>ModelResource</code>'s
     * <code>ModelAnnotation</code>
     * 
     * @param modelResource the <code>ModelResource</code>. may not be null
     * @param forceCreate forces creation of the annotation if it does not exist.
     * @return the annotation
     * @throws ModelWorkspaceException
     */
    public static Annotation getResourceAnnotation( ModelResource modelResource,
                                                    boolean forceCreate ) throws ModelWorkspaceException {
        // transaction needed if forcing creation of objects
        if (forceCreate) {
            boolean requiredStart = ModelerCore.startTxn(true, true, Util.getString(I18N_PREFIX + "getResourceAnnotation"), //$NON-NLS-1$
                                                         modelResource);
            boolean succeeded = false;

            try {
                return getResourceAnnotationImpl(modelResource, true);
            } finally {
                // if we started the transaction, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        return getResourceAnnotationImpl(modelResource, false);
    }

    /**
     * @param modelResource the model resource whose supported namespaces is being checked (cannot be <code>null</code>)
     * @return the namespace prefixes of all model extension definitions that are persisted in the model resource (never
     *         <code>null</code>)
     * @throws Exception if there is a problem accessing the model resource
     */
    public static Collection<String> getSupportedNamespaces( ModelResource modelResource ) throws Exception {
        // transaction logic not needed since readonly operation
        CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$
        Annotation annotation = getResourceAnnotation(modelResource, false);

        if (annotation != null) {
            EMap<String, String> tags = annotation.getTags();
            Collection<String> namespaces = new ArrayList<String>();

            for (String key : tags.keySet()) {
                if (key.startsWith(DEFN_PREFIX)) {
                    // the value is the namespace prefix
                    namespaces.add(tags.get(key));
                }
            }

            return namespaces;
        }

        return Collections.emptyList();
    }

    /**
     * @param modelObject the model object being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the model object is related to a model extension definition
     */
    public static boolean isModelExtensionDefinitionRelated( EObject modelObject ) {
        if (modelObject instanceof EStringToStringMapEntryImpl) {
            String key = ((EStringToStringMapEntryImpl)modelObject).getKey();

            if (key.startsWith(DEFN_PREFIX + DELIM)) {
                return true;
            }
        } else if (modelObject instanceof Annotation) {
            return isModelExtensionDefinitionRelated(((Annotation)modelObject).getAnnotatedObject());
        }

        return false;

    }

    private static void removeMetaclassAnnotation( ModelResource modelResource,
                                                   EStringToStringMapEntryImpl metaclassTag ) throws Exception {
        Annotation metaclassAnnotation = getModelObjectAnnotation(metaclassTag, false);

        if (metaclassAnnotation == null) {
            throw new Exception(Util.getString(I18N_PREFIX + "metaclassAnnotationNotFound", metaclassTag.getKey())); //$NON-NLS-1$
        }

        EMap<String, String> tags = metaclassAnnotation.getTags();

        for (Object object : tags.entrySet()) {
            if (!(object instanceof EStringToStringMapEntryImpl)) {
                throw new Exception(Util.getString(I18N_PREFIX + "metaclassEntryUnexpectedClass", object.getClass())); //$NON-NLS-1$
            }

            if (((EStringToStringMapEntryImpl)object).getKey().startsWith(PROP_DEFN_PREFIX)) {
                removePropertyDefinition(modelResource, (EStringToStringMapEntryImpl)object);
            }
        }

        // delete annotation
        ModelResourceContainerFactory.deleteAnnotation(metaclassAnnotation);
    }

    /**
     * @param modelObject the model object whose property is being removed (cannot be <code>null</code>)
     * @param propId the property ID being removed (cannot be <code>null</code> or empty)
     * @param deleteModelObjectAnnotation <code>true</code> if the model object annotation should be deleted if there is no other
     *            remaining information remaining
     * @throws Exception if there is a problem accessing the model object's model resource
     */
    public static void removeProperty( EObject modelObject,
                                       String propId,
                                       boolean deleteModelObjectAnnotation ) throws Exception {
        CoreArgCheck.isNotNull(modelObject, "modelObject is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(propId, "id is empty"); //$NON-NLS-1$

        Annotation annotation = getModelObjectAnnotation(modelObject, deleteModelObjectAnnotation);

        if (annotation != null) {
            annotation.getTags().remove(propId);

            if (deleteModelObjectAnnotation && annotation.getTags().isEmpty()) {
                ModelResourceContainerFactory.deleteAnnotation(annotation);
            }
        }
    }

    private static void removePropertyDefinition( ModelResource modelResource,
                                                  EStringToStringMapEntryImpl propertyDefinitionTag ) throws Exception {
        Annotation propertyDefinitionAnnotation = getModelObjectAnnotation(propertyDefinitionTag, false);

        if (propertyDefinitionAnnotation == null) {
            throw new Exception(Util.getString(I18N_PREFIX + "propertyDefinitionAnnotationNotFound", propertyDefinitionTag.getKey())); //$NON-NLS-1$
        }

        // delete property definition
        EMap<String, String> tags = propertyDefinitionAnnotation.getTags();

        for (Object object : tags.entrySet()) {
            if (!(object instanceof EStringToStringMapEntryImpl)) {
                Util.log(IStatus.ERROR, Util.getString(I18N_PREFIX + "propertyDefinitionEntryUnexpectedClass", object.getClass())); //$NON-NLS-1$
            }

            String key = ((EStringToStringMapEntryImpl)object).getKey();

            if (PropertyTagKeys.ALLOWED_VALUES.equals(key)) {
                Annotation allowedValuesAnnotation = getModelObjectAnnotation((EStringToStringMapEntryImpl)object, false);

                if (allowedValuesAnnotation == null) {
                    throw new Exception(Util.getString(I18N_PREFIX + "allowedValuesAnnotationNotFound", //$NON-NLS-1$
                                                       propertyDefinitionTag.getKey()));
                }

                // delete annotation
                ModelResourceContainerFactory.deleteAnnotation(allowedValuesAnnotation);
                break;
            }
        }

        // delete annotation
        ModelResourceContainerFactory.deleteAnnotation(propertyDefinitionAnnotation);
    }

    /**
     * @param modelResource the model resource where the model extension definition is being removed (cannot be <code>null</code>)
     * @param namespacePrefix the namespace prefix of the model extension definition being removed (cannot be <code>null</code> or
     *            empty)
     * @throws Exception if there is a problem accessing the model resource
     */
    public static void removeModelExtensionDefinition( ModelResource modelResource,
                                                       String namespacePrefix ) throws Exception {
        CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$
        Annotation annotation = getResourceAnnotation(modelResource, false);

        if (annotation != null) {
            Annotation defnAnnotation = getDefinitionAnnotation(modelResource, false, namespacePrefix);

            if (defnAnnotation == null) {
                throw new Exception(Util.getString(I18N_PREFIX + "defnAnnotationNotFound", namespacePrefix)); //$NON-NLS-1$
            }

            boolean requiredStart = ModelerCore.startTxn(true, true,
                                                         Util.getString(I18N_PREFIX + "removeModelExtensionDefinition"), //$NON-NLS-1$
                                                         modelResource);
            boolean succeeded = false;

            try {
                // find extended metaclass annotations
                EMap<String, String> tags = defnAnnotation.getTags();

                for (Object object : tags.entrySet()) {
                    if (!(object instanceof EStringToStringMapEntryImpl)) {
                        throw new Exception(Util.getString(I18N_PREFIX + "modelExtensionDefinitionTagUnexpectedClass", //$NON-NLS-1$
                                                           object.getClass()));
                    }

                    EStringToStringMapEntryImpl entry = (EStringToStringMapEntryImpl)object;

                    if (entry.getKey().startsWith(EXTENDED_METACLASS_PREFIX)) {
                        removeMetaclassAnnotation(modelResource, entry);
                    }
                }

                // delete annotation
                ModelResourceContainerFactory.deleteAnnotation(defnAnnotation);
                annotation.getTags().removeKey(constructKey(DEFN_PREFIX, namespacePrefix));

                // delete resource annotation if no more tags
                if (annotation.getTags().isEmpty()) {
                    ModelResourceContainerFactory.deleteAnnotation(annotation);
                }
            } finally {
                // if we started the transaction, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * @param modelResource the model resource where the model resource definition is being stored (cannot be <code>null</code>)
     * @param definition the model extension definition being stored (cannot be <code>null</code>)
     * @throws Exception if there is a problem access the model resource
     */
    public static void updateModelExtensionDefinition( ModelResource modelResource,
                                                       ModelExtensionDefinition definition ) throws Exception {
        // TODO need to remove things from model that no longer exist in definition

        CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(definition, "definition is null"); //$NON-NLS-1$

        // update model extension definition properties
        String namespacePrefix = definition.getNamespacePrefix();

        boolean requiredStart = ModelerCore.startTxn(true, true, Util.getString(I18N_PREFIX + "updateModelExtensionDefinition"), //$NON-NLS-1$
                                                     modelResource);
        boolean succeeded = false;

        try {
            Annotation definitionAnnotation = getDefinitionAnnotation(modelResource, true, namespacePrefix);
            EMap<String, String> definitionTags = definitionAnnotation.getTags();

            // metamodel URI
            String metamodelUri = definition.getMetamodelUri();

            if (!CoreStringUtil.equals(definitionTags.get(DefinitionTagKeys.METAMODEL), metamodelUri)) {
                definitionTags.put(DefinitionTagKeys.METAMODEL, metamodelUri);
            }

            // namespace prefix
            if (!CoreStringUtil.equals(definitionTags.get(DefinitionTagKeys.NAMESPACE_PREFIX), namespacePrefix)) {
                definitionTags.put(DefinitionTagKeys.NAMESPACE_PREFIX, namespacePrefix);
            }

            // namespace URI
            String namespaceUri = definition.getNamespaceUri();

            if (!CoreStringUtil.equals(definitionTags.get(DefinitionTagKeys.NAMESPACE_URI), namespaceUri)) {
                definitionTags.put(DefinitionTagKeys.NAMESPACE_URI, namespaceUri);
            }

            // version
            int version = definition.getVersion();

            if (!CoreStringUtil.equals(definitionTags.get(DefinitionTagKeys.VERSION), Integer.toString(version))) {
                definitionTags.put(DefinitionTagKeys.VERSION, Integer.toString(version));
            }

            // description
            String description = definition.getDescription();

            if (CoreStringUtil.isEmpty(description)) {
                definitionTags.remove(DefinitionTagKeys.DESCRIPTION);
            } else if (!CoreStringUtil.equals(definitionTags.get(DefinitionTagKeys.DESCRIPTION), description)) {
                definitionTags.put(DefinitionTagKeys.DESCRIPTION, description);
            }

            // properties
            for (String extendedMetaclassName : definition.getExtendedMetaclasses()) {
                String metaclassKey = constructKey(EXTENDED_METACLASS_PREFIX, extendedMetaclassName);
                Annotation metaclassAnnotation = getAnnotation(modelResource, definitionAnnotation, metaclassKey,
                                                               extendedMetaclassName, true);

                // find the tag entry associated with the extended metaclass
                for (ModelExtensionPropertyDefinition propDefn : definition.getPropertyDefinitions(extendedMetaclassName)) {
                    String propKey = constructKey(PROP_DEFN_PREFIX, propDefn.getSimpleId());
                    Annotation propDefAnnotation = getAnnotation(modelResource, metaclassAnnotation, propKey,
                                                                 propDefn.getSimpleId(), true);
                    EMap<String, String> propDefTags = propDefAnnotation.getTags();
                    String value = null;

                    // advanced
                    value = Boolean.toString(propDefn.isAdvanced());

                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.ADVANCED), value)) {
                        propDefTags.put(PropertyTagKeys.ADVANCED, value);
                    }

                    // allowed values
                    String[] allowedValues = propDefn.getAllowedValues();

                    // don't save if no allowed values or if a boolean
                    if ((allowedValues == null) || (allowedValues.length == 0)
                            || ModelExtensionPropertyDefinition.Type.BOOLEAN.getRuntimeType().equals(propDefn.getRuntimeType())) {
                        propDefTags.removeKey(PropertyTagKeys.ALLOWED_VALUES);
                    } else {
                        Annotation allowedValuesAnnotation = getAnnotation(modelResource, propDefAnnotation,
                                                                           PropertyTagKeys.ALLOWED_VALUES,
                                                                           CoreStringUtil.Constants.EMPTY_STRING, true);
                        EMap<String, String> allowedValuesTags = allowedValuesAnnotation.getTags();

                        // add each value
                        for (String allowedValue : allowedValues) {
                            allowedValuesTags.put(allowedValue, CoreStringUtil.Constants.EMPTY_STRING);
                        }
                    }

                    // display name
                    value = propDefn.getDisplayName();

                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.DISPLAY_NAME), value)) {
                        if (CoreStringUtil.isEmpty(value)) {
                            propDefTags.removeKey(PropertyTagKeys.DISPLAY_NAME);
                        } else {
                            propDefTags.put(PropertyTagKeys.DISPLAY_NAME, value);
                        }
                    }

                    // default value
                    value = propDefn.getDefaultValue();

                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.DEFAULT_VALUE), value)) {
                        if (CoreStringUtil.isEmpty(value)) {
                            propDefTags.removeKey(PropertyTagKeys.DEFAULT_VALUE);
                        } else {
                            propDefTags.put(PropertyTagKeys.DEFAULT_VALUE, value);
                        }
                    }

                    // description
                    value = propDefn.getDescription();

                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.DESCRIPTION), value)) {
                        if (CoreStringUtil.isEmpty(value)) {
                            propDefTags.removeKey(PropertyTagKeys.DESCRIPTION);
                        } else {
                            propDefTags.put(PropertyTagKeys.DESCRIPTION, value);
                        }
                    }

                    // id
                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.ID), propDefn.getSimpleId())) {
                        propDefTags.put(PropertyTagKeys.ID, propDefn.getSimpleId());
                    }

                    // modifiable
                    value = Boolean.toString(propDefn.isModifiable());

                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.MODIFIABLE), value)) {
                        propDefTags.put(PropertyTagKeys.MODIFIABLE, value);
                    }

                    // masked
                    value = Boolean.toString(propDefn.isMasked());

                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.MASKED), value)) {
                        propDefTags.put(PropertyTagKeys.MASKED, value);
                    }

                    // required
                    value = Boolean.toString(propDefn.isRequired());

                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.REQUIRED), value)) {
                        propDefTags.put(PropertyTagKeys.REQUIRED, value);
                    }

                    // runtimeType
                    value = propDefn.getRuntimeType();

                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.RUNTIME_TYPE), value)) {
                        propDefTags.put(PropertyTagKeys.RUNTIME_TYPE, value);
                    }
                }
            }
        } finally {
            // if we started the transaction, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * Don't allow construction.
     */
    private ModelExtensionUtils() {
        // nothing to do
    }

    private interface DefinitionTagKeys {
        String DESCRIPTION = "description"; //$NON-NLS-1$
        String METAMODEL = "metamodel"; //$NON-NLS-1$
        String NAMESPACE_PREFIX = "namespacePrefix"; //$NON-NLS-1$
        String NAMESPACE_URI = "namespaceUri"; //$NON-NLS-1$
        String VERSION = "version"; //$NON-NLS-1$
    }

    private interface PropertyTagKeys {
        String ADVANCED = "advance"; //$NON-NLS-1$
        String ALLOWED_VALUES = "allowedValues"; //$NON-NLS-1$
        String DEFAULT_VALUE = "defaultValue"; //$NON-NLS-1$
        String DESCRIPTION = "description"; //$NON-NLS-1$
        String DISPLAY_NAME = "displayName"; //$NON-NLS-1$
        String ID = "id"; //$NON-NLS-1$
        String MASKED = "masked"; //$NON-NLS-1$
        String MODIFIABLE = "modifiable"; //$NON-NLS-1$
        String REQUIRED = "required"; //$NON-NLS-1$
        String RUNTIME_TYPE = "runtimeType"; //$NON-NLS-1$
    }
}
