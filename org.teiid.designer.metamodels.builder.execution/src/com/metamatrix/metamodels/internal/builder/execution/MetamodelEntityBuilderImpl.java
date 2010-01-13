/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.internal.builder.execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.builder.MetamodelEntityBuilder;
import com.metamatrix.metamodels.builder.MetamodelEntityRecord;
import com.metamatrix.metamodels.builder.execution.MetamodelBuilderConstants;
import com.metamatrix.metamodels.builder.execution.MetamodelBuilderExecutionPlugin;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.extension.util.ExtensionUtil;
import com.metamatrix.metamodels.internal.builder.execution.util.MetamodelBuilderUtil;
import com.metamatrix.metamodels.internal.builder.util.MetaClassUriHelper;
import com.metamatrix.metamodels.relational.util.RelationalUtil;

/**
 * Implementation of the MetamodelEntityBuilder - creates EObjects given a MetamodelEntityRecord or List of records.
 */
public class MetamodelEntityBuilderImpl implements MetamodelEntityBuilder, MetamodelBuilderConstants {

    // Instance variables
    private final MultiStatus status;
    private final ResourceSet resources;
    private boolean builderDebugEnabled = false;
    private static final String ENTITY_NAME = "Name"; //$NON-NLS-1$
    private MetamodelEntityRecord currentRecord;

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(MetamodelEntityBuilderImpl.class);

    private static String getString( final String id ) {
        return UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object param1 ) {
        return UTIL.getString(I18N_PREFIX + id, param1);
    }

    private static String getString( final String id,
                                     final Object param1,
                                     final Object param2 ) {
        return UTIL.getString(I18N_PREFIX + id, param1, param2);
    }

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Constructor
     * 
     * @param status - The MultiStatus object used to accumulate errors and warnings during build - May not be null
     * @param resources - The resourceSet where entities will be added (internal ojbect resources must be contained in the
     *        resource set or they will not be found) - May not be null or empty
     */
    public MetamodelEntityBuilderImpl( final MultiStatus status,
                                       final ResourceSet resources ) {
        ArgCheck.isNotNull(status);
        ArgCheck.isNotNull(resources);
        ArgCheck.isNotEmpty(resources.getResources());

        this.status = status;
        this.resources = resources;
    }

    /**
     * Create a new EObject using the given record and then adds the entity to the appropriate parent and sets all supplied
     * feature values.
     * 
     * @param record - The MetamodelEntityRecord to use to drive creation - May not be null
     * @since 4.3
     */
    public EObject create( MetamodelEntityRecord record,
                           IProgressMonitor monitor ) {
        ArgCheck.isNotNull(record);
        this.currentRecord = record;

        EObject entity = null;
        boolean isExisting = false;
        // ---------------------------------------------------------
        // First see if entity with desired name already exists
        // ---------------------------------------------------------
        String parentPath = record.getParentPath();
        // Get the name feature first, if it exists.
        final Map featuresMap = record.getFeaturesNameValueMap();
        final Object nameValue = featuresMap.get(ENTITY_NAME);
        if (nameValue != null) {
            String shortName = (String)nameValue;
            String fullPath = parentPath + "\\\\" + shortName; //$NON-NLS-1$
            final Object existingEntity = MetamodelBuilderUtil.findEObjectByPath(resources,
                                                                                 fullPath,
                                                                                 record.getMetaClassUri(),
                                                                                 status);
            if (existingEntity != null && existingEntity instanceof EObject) {
                entity = (EObject)existingEntity;
                isExisting = true;
            }
        }

        // Get the metaClass Uri and break it apart
        String metaClassUri = record.getMetaClassUri();
        String ePkgUri = MetaClassUriHelper.getPackageUri(metaClassUri);

        // Get the ePackage (May not be null)
        EPackage ePkg = MetamodelBuilderUtil.getEPackageForUri(ePkgUri, status);
        if (ePkg == null) {
            return null;
        }

        // Get the eClass (May not be null)
        EClass eClass = (EClass)MetamodelBuilderUtil.getEClassForUri(metaClassUri, status);
        if (eClass == null) {
            return null;
        }

        // ---------------------------------------------------------
        // If existing entity was not found, try to create one
        // ---------------------------------------------------------
        // Create the entity - Log if unable to create an entity
        EFactory factory = ePkg.getEFactoryInstance();
        if (entity == null) {
            entity = factory.create(eClass);
        }

        if (entity == null) {
            final String msg = getString("unableToCreateEntity", eClass.getName()); //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
            if (this.builderDebugEnabled) {
                MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
            }
            return null;
        }

        // Process the information in the entity record and return the new object
        process(entity, record, factory, isExisting);

        if (monitor != null) {
            monitor.worked(1);
        }

        return entity;
    }

    /**
     * Create new EObjects using the given records. Adds the entities to the appropriate parent and sets all supplied feature
     * values.
     * 
     * @param records - the list of MetamodelEntityRecords to use to drive creation - May not be null
     * @since 4.3
     */
    public List create( List records,
                        IProgressMonitor monitor ) {
        ArgCheck.isNotNull(records);

        List eObjs = new ArrayList(records.size());
        Iterator iter = records.iterator();
        while (iter.hasNext()) {
            EObject eObj = create((MetamodelEntityRecord)iter.next(), monitor);
            if (eObj != null) {
                eObjs.add(eObj);
            }
        }

        return eObjs;
    }

    // ==================================================================================
    // H E L P E R M E T H O D S
    // ==================================================================================

    /* 
     * Process the information in the entity record for the given EObject
     * @param - entity - The EObject to process
     * @param - entityRecord - The MetamodelEntityRecord information to use to populate
     *           the new entity.
     */
    private void process( final EObject entity,
                          final MetamodelEntityRecord entityRecord,
                          EFactory factory,
                          boolean isExisting ) {
        if (!isExisting) {
            // Add the entity to a parent or resource first
            boolean added = addEntity(entity, entityRecord);
            if (!added) {
                return;
            }
        }

        // Set the name feature first, if it exists. Makes subsequent error messages more readable...
        final Map featuresMap = entityRecord.getFeaturesNameValueMap();

        if (!isExisting) {
            final Object nameValue = featuresMap.get(ENTITY_NAME);
            if (entity != null && nameValue != null) {
                setFeatureValue(entity, factory, ENTITY_NAME, nameValue, isExisting);
            }
        }

        // Process all the remaining feature name / value pairs (dont do 'name' again)
        final Iterator properties = featuresMap.entrySet().iterator();
        while (properties.hasNext()) {
            final Map.Entry next = (Map.Entry)properties.next();
            final String name = (String)next.getKey();
            final Object value = next.getValue();
            if (name != null && entity != null && !name.equals(ENTITY_NAME)) {
                setFeatureValue(entity, factory, name, value, isExisting);
            }
        }
    }

    /*
     * Add the given entity to the appropriate parent (or resource if a root object) 
     * @param entity - EObject to add (guaranteed to be not null)
     * @param entityRecord - MetamodelEntityRecord to drive addition
     * @return - true if success
     * @since 4.3
     */
    private boolean addEntity( final EObject entity,
                               final MetamodelEntityRecord entityRecord ) {
        final String path = entityRecord.getParentPath();
        ArgCheck.isNotNull(path);

        // Find the rsrc using the path info - Log if null
        final Resource rsrc = MetamodelBuilderUtil.findResource(resources, path);
        if (rsrc == null) {
            final String msg = getString("noRsrc", entityRecord.getParentPath()); //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
            if (this.builderDebugEnabled) {
                MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
            }
            return false;
        }

        // Find the parent using the path info - Log if null (root objects will return the resource)
        final Object parent = MetamodelBuilderUtil.findEObjectByPath(resources,
                                                                     path,
                                                                     entityRecord.getParentMetaClassUri(),
                                                                     status);
        if (parent == null) {
            final String msg = getString("noParent", entity.eClass().getName(), entityRecord.getParentPath()); //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
            if (this.builderDebugEnabled) {
                MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
            }
            return false;
        }

        // Add the entity as a child to the given parent (parent may be EObject or a Resource)
        return addChildToParent(rsrc, entity, parent);
    }

    /*
     * Add the given Child to the given Parent 
     * @param rsrc - Resource both objects exist
     * @param child - Child EObject
     * @param parent - Parent object (may be Resource for roots, else EObject)
     * @return true if success
     * @since 4.3
     */
    private boolean addChildToParent( final Resource rsrc,
                                      final EObject child,
                                      final Object parent ) {
        // Simple case... just add entity as a root.
        if (parent instanceof Resource) {
            ((Resource)parent).getContents().add(child);
            return true;
        }

        // Find the correct utility class to use for adding the given child to the given parent
        // by primary metamodel information.
        final EObject parentEObj = (EObject)parent;
        final int rsrcType = MetamodelBuilderUtil.getModelType(rsrc);
        boolean added = false;
        switch (rsrcType) {
            case RELATIONAL_MODEL: {
                added = RelationalUtil.addChildToParent(child, parentEObj);
                break;
            }
            case EXTENSION_MODEL: {
                added = ExtensionUtil.addChildToParent(child, parentEObj);
            }
        }

        // Log any failures
        if (!added) {
            final String msg = getString("noAdd", child.eClass().getName(), parentEObj.eClass().getName()); //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
            if (this.builderDebugEnabled) {
                MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
            }
        }

        return added;
    }

    /*
     * For a multiValued feature add the new value to the existing list of values 
     * @param eObject - Feature owner
     * @param featureName - name of feature to add new value
     * @param value - value to add to feature
     * @return - true of success
     * @since 4.3
     */
    private boolean addFeatureValue( final EObject eObject,
                                     final String featureName,
                                     final Object value ) {
        // find the feature... return false if no feature found
        EStructuralFeature feature = getFeature(eObject.eClass(), featureName, false);
        if (feature != null && feature.isMany()) {
            List values = (List)eObject.eGet(feature);
            if (value instanceof Collection) {
                return values.addAll((Collection)value);
            }

            return values.add(value);
        }
        return false;
    }

    /*
     * Set the value of a given feautre  
     * @param eObject - Feature owner
     * @param featureName - Name of feature to set
     * @param value - Value to set on feature
     * @since 4.3
     */
    private void setFeatureValue( final EObject eObject,
                                  final EFactory factory,
                                  final String featureName,
                                  final Object value,
                                  final boolean isExisting ) {
        // Since we are only doing creates, should not need to set null values. If null
        // value is encountered, skip the set operation altogether. We may need to do
        // 'unSet' if skipping it doesn't work out.
        if (value == null && !isExisting) {
            return;
        }

        // This is one of the most risky operations to perform generically.
        // Wrap in a try catch block to ensure we can log a meaningful message on failure.
        try {
            // Special case for description
            if (featureName != null && featureName.equalsIgnoreCase(DESCRIPTION)) {
                setDescription(eObject, (String)value);
                return;
            }

            // Find the feature
            EStructuralFeature feature = getFeature(eObject.eClass(), featureName, false);
            if (feature == null) {
                // Look for extension
                EObject extension = MetamodelBuilderUtil.getExtension(eObject, status);
                if (extension != null) {
                    // Found an extension... recall with ObjectExtension instead of the
                    // Extended EObject.
                    setFeatureValue(extension, factory, featureName, value, isExisting);
                    return;
                }
                // Could not find feature OR extension... log and return;
                final String msg = getString("noFeature", featureName, getEntityString(this.currentRecord, eObject)); //$NON-NLS-1$
                MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
                if (this.builderDebugEnabled) {
                    MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
                }
                return;
            }

            // If the feature is not changeable, log message and return
            if (!feature.isChangeable()) {
                // Could not find feature OR extension... log and return;
                final String msg = getString("featureNotChangeable", featureName, getEntityString(this.currentRecord, eObject)); //$NON-NLS-1$
                MetamodelBuilderUtil.addStatus(status, IStatus.WARNING, msg);
                if (this.builderDebugEnabled) {
                    MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
                }
                return;
            }

            // All references are passed in as comma delimited paths
            if (feature instanceof EReference && value instanceof String) {
                final Collection refObjects = new ArrayList();
                final StringTokenizer refs = new StringTokenizer((String)value, ","); //$NON-NLS-1$
                Object first = null;
                while (refs.hasMoreTokens()) {
                    // resolve each path
                    final String ref = refs.nextToken();
                    final Object next = MetamodelBuilderUtil.findEObjectByPath(resources, ref, null, status);
                    if (next != null) {
                        refObjects.add(next);
                        if (first == null) {
                            first = next;
                        }
                    } else {
                        final String msg = getString("badRef", ref, featureName); //$NON-NLS-1$ 
                        MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
                        if (this.builderDebugEnabled) {
                            MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
                        }
                    }
                }

                // If we only have one entity, don't use the collection as not all
                // refs are multivalued.
                if (refObjects.size() == 1) {
                    setFeatureValue(eObject, factory, featureName, first, isExisting);
                } else if (refObjects.size() > 1) {
                    setFeatureValue(eObject, factory, featureName, refObjects, isExisting);
                }
            } else if (!feature.isMany()) {
                // Convert the String value to the correct EDataType
                if (value instanceof String && feature instanceof EAttribute) {
                    final EDataType type = ((EAttribute)feature).getEAttributeType();
                    if (type instanceof EEnum) {
                        Object valObj = factory.createFromString(type, (String)value);
                        eObject.eSet(feature, valObj);
                    } else {
                        Object dtValue = EcoreFactory.eINSTANCE.createFromString(type, (String)value);
                        eObject.eSet(feature, dtValue);
                    }
                } else if (value instanceof Character && feature instanceof EAttribute) {
                    final EDataType type = ((EAttribute)feature).getEAttributeType();
                    if (type.getName().equalsIgnoreCase("EBoolean")) { //$NON-NLS-1$
                        Object val = getBooleanForChar((Character)value);
                        eObject.eSet(feature, val);
                    }
                } else {
                    eObject.eSet(feature, value);
                }
            } else if (feature.isMany()) {
                // Use logic for adding values to multivalued features
                if (value != null) {
                    addFeatureValue(eObject, featureName, value);
                } else {
                    // Cannot add null to multiValued feature
                    final String msg = getString("addNullToMultiFeature", featureName, getEntityString(this.currentRecord, eObject)); //$NON-NLS-1$
                    MetamodelBuilderUtil.addStatus(status, IStatus.WARNING, msg);
                    if (this.builderDebugEnabled) {
                        MetamodelBuilderExecutionPlugin.Util.log(IStatus.WARNING, msg);
                    }
                }
            }
        } catch (Exception err) {
            // Log any failures -
            final String msg = getString("errFeature", featureName, getEntityString(this.currentRecord, eObject)); //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg, err);
            if (this.builderDebugEnabled) {
                MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
            }
        }
    }

    /*
     * Helper method for converting character to the appropriate Boolean 
     * @param character - the character to interpret as a Boolean
     * @return the Boolean value for the supplied character
     */
    private Boolean getBooleanForChar( Character character ) {
        Boolean result = null;
        char charValue = character.charValue();
        if (charValue == 't' || charValue == 'T' || charValue == '1' || charValue == 'Y' || charValue == 'y') {
            result = Boolean.TRUE;
        } else if (charValue == 'f' || charValue == 'F' || charValue == '0' || charValue == 'N' || charValue == 'n') {
            result = Boolean.FALSE;
        }
        return result;
    }

    /*
     * Helper method for getting a readable string for the current EObject being processed, for
     * more readable error messages.  The string is of the format "Type:path\\objectName", for example
     * "Column MyModel\\Catalog\\MyTable\\ColumnA".
     * @param entityRecord the entity record being processed
     * @param eObject the eObject being processed
     * @return the readable string
     */
    private String getEntityString( MetamodelEntityRecord entityRecord,
                                    EObject eObject ) {
        StringBuffer sb = new StringBuffer();
        if (eObject != null) {
            EClass ec = eObject.eClass();
            // append class type to buffer
            sb.append(ec.getName() + " "); //$NON-NLS-1$
            // add the full name of the object including path, if possible
            if (entityRecord != null && entityRecord.getParentPath() != null) {
                sb.append(entityRecord.getParentPath() + "\\\\"); //$NON-NLS-1$
            } else {
                sb.append("UnknownPath" + "\\\\"); //$NON-NLS-1$   //$NON-NLS-2$
            }
            // add the eObject name value if available
            EStructuralFeature nameFeature = getFeature(ec, ENTITY_NAME, false);
            if (nameFeature != null) {
                final Object nameValue = eObject.eGet(nameFeature);
                if (nameValue != null) {
                    sb.append(nameValue.toString());
                } else {
                    sb.append("UnknownName"); //$NON-NLS-1$
                }
            }
        }
        return sb.toString();
    }

    /*
     * Helper method for setting descriptions on eObjects  
     * @param eObject - entity to set description
     * @param value - description
     * @since 4.3
     */
    private void setDescription( EObject eObject,
                                 final String value ) {
        // TODO should be checking to ensure resource supports annotations - No need in initial implementation.

        Resource eResource = eObject.eResource();
        // Resource may not be null - log failure
        if (eResource == null) {
            final String msg = getString("noResource"); //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
            if (this.builderDebugEnabled) {
                MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
            }
            return;
        }

        // Get the AnnotationContainer for this resource
        AnnotationContainer cntr = MetamodelBuilderUtil.getAnnotationContainer(eResource);
        if (cntr == null) {
            EFactory factory = CorePackage.eINSTANCE.getEFactoryInstance();
            cntr = (AnnotationContainer)factory.create(CorePackage.eINSTANCE.getAnnotationContainer());
            eResource.getContents().add(cntr);
        }

        // Check if an annotation exists for this object
        final Annotation existing = cntr.findAnnotation(eObject);
        if (existing != null) {
            // Reset the description if the annotation exists
            existing.setDescription(value);
        } else {
            // Create the annotation and add it to the AnnotationContainer
            EFactory factory = CorePackage.eINSTANCE.getEFactoryInstance();
            Annotation annot = (Annotation)factory.create(CorePackage.eINSTANCE.getAnnotation());
            annot.setAnnotatedObject(eObject);
            annot.setDescription(value);
            annot.setAnnotationContainer(cntr);
        }
    }

    /*
     * Find the EStructuralFeature with the given name for the given EClass  
     * @param eClass - EClass to search 
     * @param featureName - Name of feature to search for - May not be null
     * @param caseSensitiveMatch - flag to determine whether name must match case.
     * @return matching EStructuralFeature
     * @since 4.3
     */
    private EStructuralFeature getFeature( final EClass eClass,
                                           final String featureName,
                                           final boolean caseSensitiveMatch ) {
        if (featureName == null) {
            return null;
        }

        // Simple case - if case sensitive, just ask the eClass for the featureName
        if (caseSensitiveMatch) {
            EStructuralFeature next = eClass.getEStructuralFeature(featureName);
            if (next != null) {
                return next;
            }
        }

        // Iterate over the featurs comparing names.
        for (Iterator iter = eClass.getEAllStructuralFeatures().iterator(); iter.hasNext();) {
            final EStructuralFeature next = (EStructuralFeature)iter.next();
            if (featureName.equalsIgnoreCase(next.getName())) {
                return next;
            }
        }

        // No match found... return null
        return null;
    }
}
