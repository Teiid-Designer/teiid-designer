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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionHeader;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.Translation;

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
class ModelExtensionUtils {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelExtensionUtils.class);

    private static final String DEFN_PREFIX = "modelExtensionDefinition"; //$NON-NLS-1$

    private static final char DELIM = ':';

    private static final String EXTENDED_METACLASS_PREFIX = "extendedMetaclass"; //$NON-NLS-1$

    private static final String PROP_DEFN_PREFIX = "propertyDefinition"; //$NON-NLS-1$

    private static String constructKey( String prefix,
                                        String id ) {
        return prefix + DELIM + id;
    }

    private static String getKeyId( String prefix,
                                    String key ) {
        String prefixStr = prefix + DELIM;
        if (key.startsWith(prefixStr)) {
            return key.substring(key.indexOf(prefixStr) + prefixStr.length());
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Determine if the ModelResource contains a ModelExtensionDefinition with the specified Namespace Prefix.
     * 
     * @param modelResource the model resource being checked (cannot be <code>null</code>)
     * @param namespacePrefix the namespace prefix of the model extension definition being looked for (cannot be <code>null</code>
     *        or empty)
     * @return <code>true</code> if the model resource contains the model extension definition with the specified namespace prefix
     * @throws Exception if there is a problem accessing the model resource
     */
    public static boolean containsModelExtensionDefinition( ModelResource modelResource,
                                                            String namespacePrefix ) throws Exception {
        // transaction logic not needed as this is a readonly operation
        CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$
        return (getDefinitionAnnotation(modelResource, false, namespacePrefix) != null);
    }

    /**
     * Get the ModelExtensionDefinitionHeader for the specified Namespace Prefix for the ModelResource
     * 
     * @param modelResource the model resource being checked (cannot be <code>null</code>)
     * @param namespacePrefix the namespace prefix of the model extension definition being looked for (cannot be <code>null</code>
     *        or empty)
     * @return the ModelExtensionDefinitionHeader
     * @throws Exception if there is a problem accessing the model resource
     */
    public static ModelExtensionDefinitionHeader getModelExtensionDefinitionHeader( ModelResource modelResource,
                                                                                    String namespacePrefix ) throws Exception {
        // transaction logic not needed as this is a readonly operation
        CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(namespacePrefix, "namespacePrefix is empty"); //$NON-NLS-1$

        ModelExtensionDefinitionHeader header = null;
        Annotation defnAnnotation = getDefinitionAnnotation(modelResource, false, namespacePrefix);

        if (defnAnnotation != null) {
            EMap<String, String> definitionTags = defnAnnotation.getTags();
            String metamodelUri = definitionTags.get(DefinitionTagKeys.METAMODEL);
            String namespacePrfx = definitionTags.get(DefinitionTagKeys.NAMESPACE_PREFIX);
            String namespaceUri = definitionTags.get(DefinitionTagKeys.NAMESPACE_URI);
            String description = definitionTags.get(DefinitionTagKeys.DESCRIPTION);
            String versionStr = definitionTags.get(DefinitionTagKeys.VERSION);
            int version = Integer.parseInt(versionStr);
            header = new ModelExtensionDefinitionHeader(namespacePrfx,
                                                        namespaceUri,
                                                        metamodelUri,
                                                        getSupportedModelTypes(modelResource, defnAnnotation),
                                                        description,
                                                        version);
        }

        return header;
    }

    /**
     * Reads the annotations from the supplied ModelResource, converting to a ModelExtensionDefinition object
     * 
     * @param assistant the ModelExtensionAssistant
     * @param modelResource the model resource (cannot be <code>null</code>)
     * @return the ModelExtensionDefinition
     * @throws Exception if there is a problem accessing the model resource
     */
    public static ModelExtensionDefinition getModelExtensionDefinition( ModelExtensionAssistant assistant,
                                                                        ModelResource modelResource ) throws Exception {
        // transaction logic not needed as this is a readonly operation
        CoreArgCheck.isNotNull(assistant, "assistant is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$

        ModelExtensionDefinition med = null;
        String namespacePrefix = assistant.getNamespacePrefix();
        // Get the Header info from the modelResource for the supplied namespacePrefix
        ModelExtensionDefinitionHeader medHeader = getModelExtensionDefinitionHeader(modelResource, namespacePrefix);

        if (medHeader != null) {
            // Create a ModelExtensionDefinition
            med = assistant.createModelExtensionDefinition(medHeader);
            // Get annotation for this namespacePrefix from the modelResource
            Annotation defnAnnotation = getDefinitionAnnotation(modelResource, false, namespacePrefix);
            if (defnAnnotation != null) {
                // Get Tags from the Annotation
                EMap<String, String> definitionTags = defnAnnotation.getTags();
                // Iterate over Tags, process the extended metaclass tag info
                for (Object object : definitionTags.entrySet()) {
                    if (!(object instanceof EStringToStringMapEntryImpl)) {
                        throw new Exception(Util.getString(I18N_PREFIX + "modelExtensionDefinitionTagUnexpectedClass", //$NON-NLS-1$
                                                           object.getClass()));
                    }

                    EStringToStringMapEntryImpl entry = (EStringToStringMapEntryImpl)object;

                    // Get the Annotation for each extended Metaclass
                    if (entry.getKey().startsWith(EXTENDED_METACLASS_PREFIX)) {
                        Annotation metaclassAnnotation = getModelObjectAnnotation(entry, false);

                        if (metaclassAnnotation == null) {
                            throw new Exception(Util.getString(I18N_PREFIX + "metaclassAnnotationNotFound", entry.getKey())); //$NON-NLS-1$
                        }

                        // Metaclass Name
                        String metaclassName = getKeyId(EXTENDED_METACLASS_PREFIX, entry.getKey());

                        // Get Property Definitions for this Metaclass
                        EMap<String, String> metaclassTags = metaclassAnnotation.getTags();
                        for (Object metaclassTagObj : metaclassTags.entrySet()) {
                            if (!(metaclassTagObj instanceof EStringToStringMapEntryImpl)) {
                                throw new Exception(
                                                    Util.getString(I18N_PREFIX + "metaclassEntryUnexpectedClass", metaclassTagObj.getClass())); //$NON-NLS-1$
                            }

                            // Find Property Definition Tags
                            if (((EStringToStringMapEntryImpl)metaclassTagObj).getKey().startsWith(PROP_DEFN_PREFIX)) {
                                // Get PropertyDefinition Annotation
                                Annotation propertyDefinitionAnnotation = getModelObjectAnnotation((EStringToStringMapEntryImpl)metaclassTagObj,
                                                                                                   false);

                                if (propertyDefinitionAnnotation == null) {
                                    throw new Exception(
                                                        Util.getString(I18N_PREFIX + "propertyDefinitionAnnotationNotFound", ((EStringToStringMapEntryImpl)metaclassTagObj).getKey())); //$NON-NLS-1$
                                }
                                // Get ProperyDefinition Values
                                String simpleId = null;
                                Set<Translation> displayNames = null;
                                String runtimeType = null;
                                String required = Boolean.toString(ModelExtensionPropertyDefinition.REQUIRED_DEFAULT);
                                String defaultValue = null;
                                String fixedValue = null;
                                String advanced = Boolean.toString(ModelExtensionPropertyDefinition.ADVANCED_DEFAULT);
                                String masked = Boolean.toString(ModelExtensionPropertyDefinition.MASKED_DEFAULT);
                                String index = Boolean.toString(ModelExtensionPropertyDefinition.INDEX_DEFAULT);
                                Set<String> allowedValues = null;
                                Set<Translation> descriptions = null;

                                // Get Value for each Tag
                                EMap<String, String> propDefnTags = propertyDefinitionAnnotation.getTags();
                                for (Object propDefnTagObj : propDefnTags.entrySet()) {
                                    if (!(propDefnTagObj instanceof EStringToStringMapEntryImpl)) {
                                        Util.log(IStatus.ERROR,
                                                 Util.getString(I18N_PREFIX + "propertyDefinitionEntryUnexpectedClass", propDefnTagObj.getClass())); //$NON-NLS-1$
                                    }

                                    // Get Tag Key/Value
                                    String key = ((EStringToStringMapEntryImpl)propDefnTagObj).getKey();
                                    String value = ((EStringToStringMapEntryImpl)propDefnTagObj).getValue();

                                    // Id
                                    if (PropertyTagKeys.ID.equals(key)) {
                                        simpleId = value;
                                        // Display Names (multiple for locales)
                                    } else if (PropertyTagKeys.DISPLAY_NAMES.equals(key)) {
                                        displayNames = new HashSet<Translation>();
                                        Annotation displayNamesAnnotation = getModelObjectAnnotation((EStringToStringMapEntryImpl)propDefnTagObj,
                                                                                                     false);
                                        EMap<String, String> displayNamesTags = displayNamesAnnotation.getTags();

                                        for (Object temp : displayNamesTags.entrySet()) {
                                            if (!(temp instanceof EStringToStringMapEntryImpl)) {
                                                throw new Exception(Util.getString(I18N_PREFIX
                                                        + "modelExtensionDefinitionTagUnexpectedClass", temp.getClass())); //$NON-NLS-1$
                                            }

                                            EStringToStringMapEntryImpl displayNameEntry = (EStringToStringMapEntryImpl)temp;
                                            String localeTxt = displayNameEntry.getKey();
                                            displayNames.add(new Translation(I18nUtil.parseLocaleString(localeTxt),
                                                                             displayNameEntry.getValue()));
                                        }
                                        // Descriptions (multiple for locales)
                                    } else if (PropertyTagKeys.DESCRIPTIONS.equals(key)) {
                                        descriptions = new HashSet<Translation>();
                                        Annotation desciptionsAnnotation = getModelObjectAnnotation((EStringToStringMapEntryImpl)propDefnTagObj,
                                                                                                    false);
                                        EMap<String, String> descriptionsTags = desciptionsAnnotation.getTags();

                                        for (Object temp : descriptionsTags.entrySet()) {
                                            if (!(temp instanceof EStringToStringMapEntryImpl)) {
                                                throw new Exception(Util.getString(I18N_PREFIX
                                                        + "modelExtensionDefinitionTagUnexpectedClass", temp.getClass())); //$NON-NLS-1$
                                            }

                                            EStringToStringMapEntryImpl descriptionEntry = (EStringToStringMapEntryImpl)temp;
                                            String localeTxt = descriptionEntry.getKey();
                                            descriptions.add(new Translation(I18nUtil.parseLocaleString(localeTxt),
                                                                             descriptionEntry.getValue()));
                                        }
                                        // Runtime type
                                    } else if (PropertyTagKeys.RUNTIME_TYPE.equals(key)) {
                                        runtimeType = value;
                                        // Required
                                    } else if (PropertyTagKeys.REQUIRED.equals(key)) {
                                        required = value;
                                        // Default Value
                                    } else if (PropertyTagKeys.DEFAULT_VALUE.equals(key)) {
                                        defaultValue = value;
                                        // Fixed Value
                                    } else if (PropertyTagKeys.FIXED_VALUE.equals(key)) {
                                        fixedValue = value;
                                        // Advanced
                                    } else if (PropertyTagKeys.ADVANCED.equals(key)) {
                                        advanced = value;
                                        // Masked
                                    } else if (PropertyTagKeys.MASKED.equals(key)) {
                                        masked = value;
                                        // Index
                                    } else if (PropertyTagKeys.INDEX.equals(key)) {
                                        index = value;
                                        // Allowed Values
                                    } else if (PropertyTagKeys.ALLOWED_VALUES.equals(key)) {
                                        allowedValues = new HashSet<String>();
                                        Annotation allowedValuesAnnotation = getModelObjectAnnotation((EStringToStringMapEntryImpl)propDefnTagObj,
                                                                                                      false);

                                        if (allowedValuesAnnotation == null) {
                                            throw new Exception(Util.getString(I18N_PREFIX + "allowedValuesAnnotationNotFound", //$NON-NLS-1$
                                                                               ((EStringToStringMapEntryImpl)propDefnTagObj).getKey()));
                                        }

                                        EMap<String, String> allowedValuesTags = allowedValuesAnnotation.getTags();

                                        for (String allowedValue : allowedValuesTags.keySet()) {
                                            allowedValues.add(allowedValue);
                                        }
                                    }
                                }

                                // Create a property definition based on the read in properties, then add it.
                                ModelExtensionPropertyDefinition propertyDefn = assistant.createPropertyDefinition(simpleId,
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

                                // Add the property definition to the ModelExtensionDefinition
                                assistant.addPropertyDefinition(metaclassName, propertyDefn);
                            }
                        }
                    }
                }
            }
        }
        return med;
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
            if (forceCreate) {
                tags.put(tagId, value);
            } else {
                return null;
            }
        }

        // find the tag entry associated with the ID
        for (Object obj : tags.entrySet()) {
            EStringToStringMapEntryImpl entry = (EStringToStringMapEntryImpl)obj;

            if (entry.getKey().equals(tagId)) {
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
                succeeded = true;
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

    private static Annotation getModelTypesAnnotation( ModelResource modelResource,
                                                       Annotation definitionAnnotation,
                                                       boolean forceCreate ) throws Exception {
        return getAnnotation(modelResource, definitionAnnotation, DefinitionTagKeys.MODEL_TYPES,
                             CoreStringUtil.Constants.EMPTY_STRING, forceCreate);
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
            	Annotation annotation = getResourceAnnotationImpl(modelResource, true);
            	succeeded = true;
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

        return getResourceAnnotationImpl(modelResource, false);
    }

    private static Set<String> getSupportedModelTypes( ModelResource modelResource, Annotation definitionAnnotation ) throws Exception {
        Annotation modelTypesAnnotation = getModelTypesAnnotation(modelResource, definitionAnnotation, false);

        if (modelTypesAnnotation != null) {
            EMap<String, String> modelTypesTags = modelTypesAnnotation.getTags();
            Set<String> modelTypes = new HashSet<String>(modelTypesTags.size());
    
            for (String modelType : modelTypesTags.keySet()) {
                modelTypes.add(modelType);
            }
    
            return modelTypes;
        }

        return Collections.emptySet();
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
     * @param modelResource the model resource whose namespaces are being checked (cannot be <code>null</code>)
     * @param namespace the namespace being verified (cannot be <code>null</code>)
     * @return 'true' if the supplied namespace is supported by the modelResource
     * @throws Exception if there is a problem accessing the model resource
     */
    public static boolean isSupportedNamespace( ModelResource modelResource,
                                                String namespace ) throws Exception {
        CoreArgCheck.isNotNull(modelResource, "modelResource is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(namespace, "namespace is null"); //$NON-NLS-1$

        Collection<String> namespaces = getSupportedNamespaces(modelResource);
        for (String ns : namespaces) {
            if (namespace.equals(ns)) {
                return true;
            }
        }
        return false;
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

                // delete annotation
                if (allowedValuesAnnotation != null) {
                    ModelResourceContainerFactory.deleteAnnotation(allowedValuesAnnotation);
                }
            }
            if (PropertyTagKeys.DISPLAY_NAMES.equals(key)) {
                Annotation displayNamesAnnotation = getModelObjectAnnotation((EStringToStringMapEntryImpl)object, false);

                // delete annotation
                if (displayNamesAnnotation != null) {
                    ModelResourceContainerFactory.deleteAnnotation(displayNamesAnnotation);
                }
            }
            if (PropertyTagKeys.DESCRIPTIONS.equals(key)) {
                Annotation descriptionsAnnotation = getModelObjectAnnotation((EStringToStringMapEntryImpl)object, false);

                // delete annotation
                if (descriptionsAnnotation != null) {
                    ModelResourceContainerFactory.deleteAnnotation(descriptionsAnnotation);
                }
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
                // find model types annotation
                Annotation modelTypesAnnotation = getModelTypesAnnotation(modelResource, defnAnnotation, false);

                if (modelTypesAnnotation != null) {
                    ModelResourceContainerFactory.deleteAnnotation(modelTypesAnnotation);
                }

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
                succeeded = true;
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
     * Update the <code>ModelResource</code> annotations, with the supplied <code>ModelExtensionDefinition</code> properties.
     * 
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

            // model types
            Collection<String> supportedModelTypes = definition.getSupportedModelTypes();

            if (supportedModelTypes.isEmpty()) {
                definitionTags.remove(DefinitionTagKeys.MODEL_TYPES);
            } else {
                Annotation modelTypesAnnotation = getModelTypesAnnotation(modelResource, definitionAnnotation, true);
                EMap<String, String> modelTypesTags = modelTypesAnnotation.getTags();
                modelTypesTags.clear();

                // add all supported model types
                for (String modelType : supportedModelTypes) {
                    modelTypesTags.put(modelType, CoreStringUtil.Constants.EMPTY_STRING);
                }
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

                    DISPLAY_NAMES: {
                        Set<Translation> displayNames = propDefn.getDisplayNames();

                        if (!displayNames.isEmpty()) {
                            Annotation displayNamesAnnotation = getAnnotation(modelResource, propDefAnnotation,
                                                                              PropertyTagKeys.DISPLAY_NAMES,
                                                                              CoreStringUtil.Constants.EMPTY_STRING, true);
                            EMap<String, String> displayNamesTags = displayNamesAnnotation.getTags();
                            displayNamesTags.clear();

                            for (Translation translation : displayNames) {
                                displayNamesTags.put(translation.getLocale().toString(), translation.getTranslation());
                            }
                        }

                        break DISPLAY_NAMES;
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

                    DESCRIPTIONS: {
                        Set<Translation> descriptions = propDefn.getDescriptions();

                        if (!descriptions.isEmpty()) {
                            Annotation descriptionsAnnotation = getAnnotation(modelResource, propDefAnnotation,
                                                                              PropertyTagKeys.DESCRIPTIONS,
                                                                              CoreStringUtil.Constants.EMPTY_STRING, true);
                            EMap<String, String> descriptionsTags = descriptionsAnnotation.getTags();
                            descriptionsTags.clear();

                            for (Translation translation : descriptions) {
                                descriptionsTags.put(translation.getLocale().toString(), translation.getTranslation());
                            }
                        }

                        break DESCRIPTIONS;
                    }

                    // id
                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.ID), propDefn.getSimpleId())) {
                        propDefTags.put(PropertyTagKeys.ID, propDefn.getSimpleId());
                    }

                    // index
                    value = Boolean.toString(propDefn.shouldBeIndexed());

                    if (!CoreStringUtil.equals(propDefTags.get(PropertyTagKeys.INDEX), value)) {
                        propDefTags.put(PropertyTagKeys.INDEX, value);
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
            succeeded = true;
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
        String MODEL_TYPES = "modelTypes"; //$NON-NLS-1$
        String NAMESPACE_PREFIX = "namespacePrefix"; //$NON-NLS-1$
        String NAMESPACE_URI = "namespaceUri"; //$NON-NLS-1$
        String VERSION = "version"; //$NON-NLS-1$
    }

    private interface PropertyTagKeys {
        String ADVANCED = "advance"; //$NON-NLS-1$
        String ALLOWED_VALUES = "allowedValues"; //$NON-NLS-1$
        String DEFAULT_VALUE = "defaultValue"; //$NON-NLS-1$
        String DESCRIPTIONS = "description"; //$NON-NLS-1$
        String DISPLAY_NAMES = "displayName"; //$NON-NLS-1$
        String ID = "id"; //$NON-NLS-1$
        String MASKED = "masked"; //$NON-NLS-1$
        String MODIFIABLE = "modifiable"; //$NON-NLS-1$
        String REQUIRED = "required"; //$NON-NLS-1$
        String RUNTIME_TYPE = "runtimeType"; //$NON-NLS-1$
        String FIXED_VALUE = "fixedValue"; //$NON-NLS-1$
        String INDEX = "index"; //$NON-NLS-1$
    }
}
