package com.metamatrix.modeler.internal.core.workspace;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;

public class ModelObjectAnnotationHelper {

    public static final String EXTENDED_PROPERTY_NAMESPACE = "ext-custom:"; //$NON-NLS-1$

    public ModelObjectAnnotationHelper() {
        super();
    }

    private static String getNamespace( final String str ) {
        CoreArgCheck.isNotNull(str, "str"); //$NON-NLS-1$

        int semiColonIndex = str.indexOf(':') + 1;
        if (semiColonIndex > 0) {
            return str.substring(0, semiColonIndex);
        }

        return null;
    }

    /**
     * Retrieves the tagged resource <code>Annotation</code> object referenced to a <code>EObject</code>
     * 
     * @param modelObject the <code>EObject</code>. may not be null
     * @param forceCreate forces creation of the annotation if it does not exist.
     * @return the <code>Annotation</code>
     * @throws ModelerCoreException
     */
    public Annotation getModelObjectAnnotation( final EObject modelObject,
                                                final boolean forceCreate ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$

        Annotation annotation = ModelerCore.getModelEditor().getAnnotation(modelObject, forceCreate);
        return annotation;
    }

    /**
     * Retrieves the value of an object stored on an <code>Annotation</code> in the tags map based on the input key
     * 
     * @param modelObject the <code>EObject</code>. may not be null;
     * @param key the key for the mapped value. may not be null;
     * @return the object value stored in the annotation's tags
     * @throws ModelerCoreException
     */
    public Object getPropertyValue( final EObject modelObject,
                                    final String key ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(key, "key"); //$NON-NLS-1$
        Annotation annotation = getModelObjectAnnotation(modelObject, false);
        if (annotation != null) {
            return annotation.getTags().get(key);
        }
        return null;
    }

    /**
     * Retrieves the value of an object stored on an <code>Annotation</code> in the tags map based on the input key uses upper and
     * lower case (excluding the namespace which is always lower case).
     * 
     * @param modelObject the <code>EObject</code>. may not be null;
     * @param key the key for the mapped value. may not be null;
     * @return the object value stored in the annotation's tags
     * @throws ModelerCoreException
     */
    public Object getPropertyValueAnyCase( final EObject modelObject,
                                           final String key ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(key, "key"); //$NON-NLS-1$
        Annotation annotation = getModelObjectAnnotation(modelObject, false);
        if (annotation != null) {
            int beginIndex = key.indexOf(":"); //$NON-NLS-1$
            String upperCase = key.substring(0, beginIndex) + key.substring(beginIndex).toUpperCase();
            Object tag = annotation.getTags().get(upperCase);
            if (tag == null) {
                String lowerCase = key.substring(0, beginIndex) + key.substring(beginIndex).toLowerCase();
                tag = annotation.getTags().get(lowerCase);
            }
            return tag;

        }
        return null;
    }

    /**
     * Returns all properties who's keys are prefixed with the given name-space prefix
     * 
     * @param modelObject the <code>EObject</code>. may not be null;
     * @param namespacePrefix
     * @return the properties (never <code>null</code>)
     * @throws ModelWorkspaceException
     */
    public Properties getProperties( final EObject modelObject,
                                     final String namespacePrefix ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(namespacePrefix, "namespacePrefix"); //$NON-NLS-1$
        Properties props = new Properties();

        Annotation annotation;

        if (modelObject instanceof Annotation) {
            annotation = (Annotation)modelObject;
        } else {
            annotation = getModelObjectAnnotation(modelObject, false);
        }

        if (annotation != null) {
            EMap tags = annotation.getTags();
            Set<Object> keys = tags.keySet();
            for (Object nextKey : keys) {
                String namespace = getNamespace((String)nextKey);
                if (namespace != null && namespace.equals(namespacePrefix)) {
                    props.put(nextKey, tags.get(nextKey));
                }
            }
        }

        return props;
    }

    public Properties getAllProperties( final EObject modelObject ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$
        Properties props = new Properties();

        Annotation annotation = getModelObjectAnnotation(modelObject, false);
        if (annotation != null) {
            EMap tags = annotation.getTags();
            Set<Object> keys = tags.keySet();
            for (Object nextKey : keys) {
                props.put(nextKey, tags.get(nextKey));
            }
        }
        return props;
    }

    /**
     * Removes all properties stored on a model object's annotation based on a name-space prefix
     * 
     * @param modelObject the <code>EObject</code>. may not be null;
     * @param namespacePrefix the unique prefix for the property key
     * @throws ModelerCoreException
     */
    public void removeProperties( final EObject modelObject,
                                  final String namespacePrefix ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(namespacePrefix, "namespacePrefix"); //$NON-NLS-1$

        Annotation annotation = getModelObjectAnnotation(modelObject, false);

        if (annotation != null) {
            EMap tags = annotation.getTags();
            Set<Object> keys = new HashSet(tags.keySet());
            for (Object nextKey : keys) {
                String namespace = getNamespace((String)nextKey);
                if (namespace != null && namespace.equals(namespacePrefix)) {
                    tags.remove(nextKey);
                }
            }
        }
    }

    /**
     * Removes a single property stored on a model object's annotation based on key
     * 
     * @param modelObject the <code>EObject</code>. Cannot be null;
     * @param key the unique value for the property key. Cannot be null.
     * @throws ModelerCoreException if problem obtaining the model object annotation.
     */
    public void removeProperty( final EObject modelObject,
                                final String key ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(key, "key"); //$NON-NLS-1$

        Annotation annotation = getModelObjectAnnotation(modelObject, false);

        if (annotation != null) {
            EMap tags = annotation.getTags();
            Set<String> keys = new HashSet(tags.keySet());
            for (String nextKey : keys) {
                if (nextKey.equals(key)) {
                    tags.remove(nextKey);
                    break;
                }
            }
        }
    }

    /**
     * Adds a property tag to an object stored on an <code>Annotation</code> in the tags map based on the input key
     * 
     * @param modelObject the <code>EObject</code>. may not be null;
     * @param key the key for the mapped value. may not be null;
     * @param value the object value to store in the annotation
     * @throws ModelWorkspaceException
     */
    public void addProperty( final EObject modelObject,
                             final String key,
                             final Object value ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(key, "key"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(value, "value"); //$NON-NLS-1$
        Annotation annotation = getModelObjectAnnotation(modelObject, true);

        annotation.getTags().put(key, value);
    }

    /**
     * Determines if an object has extension properties. The object may be an IFile (if it is a model) or an EObject (if the
     * object is a model object). In the case of IFile, the ModelAnnotation will be used to check for extension properties, since
     * any properties will be tied to that node. Connection and Translator properties will be excluded from the check for both
     * IFile and EObject, since these are not extended properties, but part of the connection profile.
     * 
     * @param object the <code>Object</code>. may not be null;
     * @throws ModelWorkspaceException
     */
    public boolean hasExtensionProperties( final Object object ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(object, "object"); //$NON-NLS-1$
        boolean hasExtendedProperties = false;
        Annotation annotation = null;
        if (object instanceof IFile) {
            // get model annotation
            ModelResource modelResource = ModelUtil.getModelResource((IFile)object, false);
            if (modelResource != null) {
                ModelAnnotation modelAnnotation = modelResource.getModelAnnotation();
                annotation = getModelObjectAnnotation(modelAnnotation, false);
                if (annotation != null) {
                    hasExtendedProperties = checkForExtendedProperties(annotation);
                }
            }
        } else if (object instanceof EObject) {
            annotation = getModelObjectAnnotation((EObject)object, false);
            if (annotation != null) {
                hasExtendedProperties = checkForExtendedProperties(annotation);
            }

        }

        return hasExtendedProperties;
    }

    private boolean checkForExtendedProperties( Annotation annotation ) {

        for (Object object : annotation.getTags().keySet()) {
            String key = (String)object;
            if (key.startsWith(EXTENDED_PROPERTY_NAMESPACE)) {
                return true;
            }
        }

        return false;

    }

    /**
     * Returns all extended properties, if any, for the given <code>EOBject</code>.
     * 
     * @param object the <code>Object</code>. may not be null;
     * @throws ModelWorkspaceException
     */
    public Properties getExtendedProperties( final EObject eObject ) throws ModelerCoreException {

        CoreArgCheck.isNotNull(eObject, "eObject"); //$NON-NLS-1$
        Properties properties = getProperties(eObject, EXTENDED_PROPERTY_NAMESPACE);

        return properties;
    }

    /**
     * @param modelObject the <code>EObject</code>. may not be null;
     * @param props the properties stored on the EObjects annotation
     * @throws ModelerCoreException
     */
    public void addProperties( final EObject modelObject,
                               final Properties props ) throws ModelerCoreException {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(props, "props"); //$NON-NLS-1$

        Annotation annotation = getModelObjectAnnotation(modelObject, true);
        Set<Object> keys = props.keySet();
        for (Object nextKey : keys) {
            annotation.getTags().put(nextKey, props.get(nextKey));
        }
    }
}
