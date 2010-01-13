/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.internal.builder.execution.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.builder.execution.MetamodelBuilderConstants;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.core.extension.impl.ExtensionFactoryImpl;
import com.metamatrix.metamodels.internal.builder.util.MetaClassUriHelper;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.core.ObjectExtension;
import com.metamatrix.modeler.internal.core.XsdObjectExtension;

/**
 * This is a helper class to encapsulate reusable methods developed for the Metamodel Entity builder framework.
 */
public class MetamodelBuilderUtil implements MetamodelBuilderConstants {
    // Cache of all datatypes already looked up.
    private static final HashMap DT_CACHE = new HashMap();

    // The Global XSD Schema. Cache so we don't have to look this up everytime we need to
    // search for datatypes.
    private static XSDSchema GLOBAL_XSD;

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(MetamodelBuilderUtil.class);

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
    // S T A T I C M E T H O D S
    // ==================================================================================

    /**
     * Helper method to find a child with the given name of the given metaClass (may be null). If the metaClass is null you will
     * get the first child with a matching name.
     * 
     * @param name - Name of the entity to find - May not be null
     * @param parent - The parent of the object to find (may be an EObject or a Resource, but may not be null)
     * @param metaClassUri - The metaClassUri of the given parent to resolve name collisions (may be null)
     * @return
     * @since 4.3
     */
    public static Object findChild( final String name,
                                    final Object parent,
                                    final String metaClassUri ) {
        // Name and parent may not be null.
        if (name == null || parent == null) {
            return null;
        }

        // Get collection of children to search
        final String eClassName = metaClassUri == null ? null : MetaClassUriHelper.getEClassName(metaClassUri);
        Collection children = Collections.EMPTY_LIST;
        if (parent instanceof Resource) {
            children = ((Resource)parent).getContents();
        } else if (parent instanceof EObject) {
            children = ((EObject)parent).eContents();
        }

        // Important to note that metaClassUri is only usable at the leaf level. At all other
        // levels the first entity with the correct name will be returned.
        final Iterator it = children.iterator();
        while (it.hasNext()) {
            final EObject next = (EObject)it.next();
            final String nextName = getName(next);
            if (name.equals(nextName)) {
                // As mentioned above... metaClassUri will only be passed in when we are at
                // the leaf node of a given path. It would result in incorrect results if
                // passed in at every level of the path.
                if (metaClassUri == null) {
                    return next;
                }

                if (next.eClass().getName().equals(eClassName)) {
                    return next;
                }
            }

        }

        return null;
    }

    /**
     * Helper method to return the name of a given EObject
     * 
     * @param entity - EObject to search for name feature - May not be null
     * @return the name of the EObject or it's eClass name.
     * @since 4.3
     */
    public static String getName( final EObject entity ) {
        if (entity == null) {
            return null;
        }

        // Look for a name feature and return that value
        final EStructuralFeature feature = entity.eClass().getEStructuralFeature(NAME);
        if (feature != null) {
            return (String)entity.eGet(feature);
        }

        return entity.eClass().getName();
    }

    /**
     * Search for an existing extension for a given EObject - This method was ported from ModelEditor with the removal of logic
     * that made it not UnitTestable
     * 
     * @param eObject - EObject to find existing extension for - May not be null
     * @param status - MultiStatus to use to accumulate warnings and errors
     * @return an ObjectExtension for the given EObject
     * @throws ModelerCoreException
     * @since 4.3
     */
    public static EObject getExtension( EObject eObject,
                                        final MultiStatus status ) throws ModelerCoreException {
        ArgCheck.isNotNull(eObject);

        final EClass eClass = eObject.eClass();
        final EPackage ePackage = eClass.getEPackage();

        // There is never an extension object for ECore ...
        if (EcorePackage.eINSTANCE.equals(ePackage)) {
            return null;
        }

        // Get the Resource for the given EObject... may not be null
        final Resource resource = eObject.eResource();
        if (resource == null) {
            return null;
        }

        if (resource instanceof XSDResourceImpl) {
            // Special logic for XSDResources that do not have ModelAnnotations
            XSDResourceImpl xsdResource = (XSDResourceImpl)resource;
            XSDSchema xsdSchema = xsdResource.getSchema();

            if (xsdSchema != null) {
                // Get the extension XPackage for the schema - null is returned if extension is defined.
                final XPackage extPackage = XsdObjectExtension.getExtensionPackage(xsdSchema);
                if (extPackage != null) {
                    final XClass xclass = extPackage.findXClass(eClass);
                    if (xclass != null) {
                        EObject result = null;
                        try {
                            EPackage pkg = extPackage;
                            ExtensionFactory factory = null;
                            final EFactory existingFactory = pkg.getEFactoryInstance();
                            if (existingFactory == null || !(existingFactory instanceof ExtensionFactory)) {
                                factory = new ExtensionFactoryImpl();
                                factory.setEPackage(extPackage);
                            }
                            result = new XsdObjectExtension(eObject, xclass, null);
                        } catch (Throwable e) {
                            final String msg = getString("extensionErr"); //$NON-NLS-1$
                            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg, e);
                        }
                        return result;
                    }
                }
            }
        } else {
            // Get the ModelAnnotation for the model that contains the eObject - May not be null
            final ModelAnnotation model = getModelAnnotation(resource);
            if (model != null) {
                final XPackage extPackage = model.getExtensionPackage();
                if (extPackage != null) {
                    final XClass xclass = extPackage.findXClass(eClass);
                    if (xclass != null) {
                        EObject result = null;
                        try {
                            EPackage pkg = extPackage;
                            ExtensionFactory factory = null;
                            final EFactory existingFactory = pkg.getEFactoryInstance();
                            if (existingFactory == null || !(existingFactory instanceof ExtensionFactory)) {
                                factory = new ExtensionFactoryImpl();
                                factory.setEPackage(extPackage);
                            }
                            result = new ObjectExtension(eObject, xclass);
                        } catch (Throwable e) {
                            final String msg = getString("extensionErr"); //$NON-NLS-1$
                            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg, e);
                        }
                        return result;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Helper method to find an entity by path within the given resourceSet
     * 
     * @param resources - ResourceSet to search
     * @param path - Full path (including ModelName) to EObject
     * @param metaClassUri - EClass for entity - May be null.
     * @param status - MultiStatus to accumulate errors and warnings
     * @return matching entity - May be a Resource or EObject
     * @since 4.3
     */
    public static Object findEObjectByPath( final ResourceSet resources,
                                            final String path,
                                            final String metaClassUri,
                                            final MultiStatus status ) {
        Object currentParent = null;

        // Tokenize the path using the path seperator
        final StringTokenizer pathTokens = new StringTokenizer(path, PATH_SEPARATOR);
        boolean failed = false;
        while (pathTokens.hasMoreTokens() && !failed) {
            if (currentParent == null) {
                currentParent = findResource(resources, pathTokens.nextToken());
                if (currentParent == null) {
                    // We did not find a resource for the first token... break out
                    failed = true;
                }
            } else {
                final String name = pathTokens.nextToken();
                // Look for child with a name that matches the current token.
                // If we are at the last token, use the metaClassUri passed in (optional)
                if (pathTokens.hasMoreTokens()) {
                    currentParent = findChild(name, currentParent, null);
                } else {
                    currentParent = findChild(name, currentParent, metaClassUri);
                }

                if (currentParent == null) {
                    // No enity found for current token in path... break out
                    failed = true;
                }
            }
        }

        // If we did not find a match, look for internal or existing objects
        // Current examples are Types which can be XSD types or MM Internal types or MetaModel Entities
        if (currentParent == null) {
            if (path.startsWith(MM_DT_URI) || path.startsWith(XSD_DT_URI)) {
                // Datatypes... find inernal EOBject
                currentParent = getInternalObjectByUri(resources, path, status);
            } else if (path.startsWith(MM_URI) || path.startsWith(ECORE_URI)) {
                // MetaModel Entity... find EClass
                currentParent = getEClassForUri(path, status);
            }
        }

        return currentParent;
    }

    /**
     * Find a resource in the given resourceSet using the given path
     * 
     * @param resources - ResourceSet to search... guaranteed to be nonNull
     * @param path - Path to use... guaranteed to be nonNull
     * @return matching resource
     * @since 4.3
     */
    public static Resource findResource( final ResourceSet resources,
                                         final String path ) {
        // TODO add logic for XSDs
        final StringTokenizer pathTokens = new StringTokenizer(path, PATH_SEPARATOR);
        final String modelName = pathTokens.nextToken() + MODEL_EXT;
        final Iterator rsrcs = resources.getResources().iterator();
        while (rsrcs.hasNext()) {
            // Use the last segment of the URI adding the ModelExtension to search for a resource
            // This logic will break when we start processing XSDs.
            final Resource rsrc = (Resource)rsrcs.next();
            if (rsrc.getURI().lastSegment().equals(modelName)) {
                return rsrc;
            }
        }

        return null;
    }

    /**
     * Helper to find a ModelAnnotation in a given Resource. Ported from ModelEditorImpl and removed logic making this method Unit
     * Testable
     * 
     * @param eResource - resrource to search
     * @return given ModelAnnotation
     * @since 4.3
     */
    public static ModelAnnotation getModelAnnotation( final Resource eResource ) {
        if (eResource == null) {
            return null;
        }

        ModelAnnotation annot = null;
        for (final Iterator iter = eResource.getContents().iterator(); iter.hasNext();) {
            final EObject root = (EObject)iter.next();
            if (root instanceof ModelAnnotation) {
                annot = (ModelAnnotation)root;
                break;
            }
        }
        return annot;
    }

    /**
     * Helper to return constant for model type
     * 
     * @param rsr - Resource to interrogate
     * @return model type constant
     * @since 4.3
     */
    public static int getModelType( final Resource rsr ) {
        final ModelAnnotation ma = getModelAnnotation(rsr);
        if (ma != null) {
            final String pmu = ma.getPrimaryMetamodelUri();
            if (RelationalPackage.eNS_URI.equals(pmu)) {
                return RELATIONAL_MODEL;
            } else if (ma.getModelType() == ModelType.EXTENSION_LITERAL) {
                return EXTENSION_MODEL;
            }
        }

        return UNKNOWN_MODEL;
    }

    /**
     * Helper to find the AnnotationContainer for the given resource
     * 
     * @param eResource - Resource to search
     * @return
     * @since 4.3
     */
    public static AnnotationContainer getAnnotationContainer( final Resource eResource ) {
        if (eResource == null) {
            return null;
        }
        for (final Iterator iter = eResource.getContents().iterator(); iter.hasNext();) {
            final EObject root = (EObject)iter.next();
            if (root instanceof AnnotationContainer) {
                return (AnnotationContainer)root;
            }
        }
        return null;
    }

    /**
     * Helper to get the ePkg for a give ePkg URI
     * 
     * @param ePkgUri
     * @param status
     * @return
     * @since 4.3
     */
    public static EPackage getEPackageForUri( final String ePkgUri,
                                              final MultiStatus status ) {
        final EPackage ePkg = EPackage.Registry.INSTANCE.getEPackage(ePkgUri);
        if (ePkg == null) {
            final String msg = getString("noPkg", ePkgUri); //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
            return null;
        }

        return ePkg;
    }

    /**
     * Helper to get the EClass for a given metaclass uri
     * 
     * @param metaClassUri
     * @param status
     * @return
     * @since 4.3
     */
    public static EClassifier getEClassForUri( final String metaClassUri,
                                               final MultiStatus status ) {
        String ePkgUri = MetaClassUriHelper.getPackageUri(metaClassUri);
        String eClassName = MetaClassUriHelper.getEClassName(metaClassUri);

        EPackage ePkg = getEPackageForUri(ePkgUri, status);
        if (ePkg == null) {
            return null;
        }

        EClassifier eClass = ePkg.getEClassifier(eClassName);
        if (eClass == null) {
            final String msg = getString("noEntity", eClassName, ePkgUri); //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
            return null;
        }

        return eClass;
    }

    /**
     * Helper to find an internal EObject in the given resource set using the given URI
     * 
     * @param resources - ResourceSet to search
     * @param uri - URI to use for searching
     * @param status - MultiStatus to record warnings and errors
     * @return - matching EObject
     * @since 4.3
     */
    public static Object getInternalObjectByUri( final ResourceSet resources,
                                                 final String uri,
                                                 final MultiStatus status ) {
        // URI may not be null and must contain the POUND seperator between resource and entity values
        if (uri == null || uri.indexOf(POUND) == -1) {
            final String msg = getString("badInternalUri", uri); //$NON-NLS-1$
            addStatus(status, IStatus.ERROR, msg);
            return null;
        }

        // Create a search name for both XSD and MM Types
        final int index = uri.indexOf(POUND);
        final String resourceUri = uri.substring(0, index);
        final String entityName = uri.substring(index + 1);
        final String mmName = MM_PREFIX + entityName;
        final String xsdName = XSD_PREFIX + entityName;

        if (resourceUri == null || entityName == null) {
            // Log error and return null
            final String msg = getString("badInternalUri", uri); //$NON-NLS-1$
            addStatus(status, IStatus.ERROR, msg);
            return null;
        }

        // Search the cache first
        Object result = DT_CACHE.get(entityName);
        if (result != null) {
            return result;
        }

        // Find the XSD Schema for either the Global types or MM BuiltIn types depending on
        // the passed in URI.
        XSDSchema schema = null;
        boolean isMM = false;
        if (resourceUri.equals(XSD_RESOURCE_URI)) {
            schema = getGlobalXsd();
        } else if (resourceUri.equals(MM_DT_RESOURCE_URI)) {
            // The builtInDatatypes.xsd must either be contained in the give resourceSet
            // or that resourceSet must know how to resolve it (Our Container knows how to do that)
            isMM = true;
            final URI dtURI = URI.createURI(MM_DT_RESOURCE_URI);
            final XSDResourceImpl dts = (XSDResourceImpl)resources.getResource(dtURI, false);
            if (dts != null && dts.isLoaded()) {
                schema = dts.getSchema();
            }

        }

        // The schema must not be null
        if (schema == null) {
            final String msg = getString("noSchema", uri); //$NON-NLS-1$
            addStatus(status, IStatus.ERROR, msg);
            return null;
        }

        // Search the given schema's global types for one with the correct name
        final Iterator types = schema.getTypeDefinitions().iterator();
        while (types.hasNext()) {
            XSDTypeDefinition type = (XSDTypeDefinition)types.next();
            if (entityName.equals(type.getName())) {
                if (isMM) {
                    DT_CACHE.put(mmName, type);
                } else {
                    DT_CACHE.put(xsdName, type);
                }

                return type;
            }
        }

        return null;
    }

    /**
     * Helper to add a new status with no exception
     */
    public static void addStatus( final MultiStatus parent,
                                  final int severity,
                                  final String msg ) {
        addStatus(parent, severity, msg, null);
    }

    /**
     * Helper to add a new status to the given MultiStatus
     */
    public static void addStatus( final MultiStatus parent,
                                  final int severity,
                                  final String msg,
                                  final Throwable err ) {
        final Status sts = new Status(severity, pluginID, 0, msg, err);
        parent.add(sts);
    }

    // Lazy accessor for the cached Global XSD Schema
    private static XSDSchema getGlobalXsd() {
        if (GLOBAL_XSD == null) {
            GLOBAL_XSD = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        }

        return GLOBAL_XSD;
    }
}
