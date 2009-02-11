/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.util.ModelObjectCollector;
import com.metamatrix.modeler.core.util.UriHelper;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.MMXmiResource;

/**
 * @since 4.3
 */
public class DefaultResourceFinder implements ResourceFinder {

    private final Container container;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * @param container
     * @since 4.3
     */
    public DefaultResourceFinder( final Container container ) {
        super();
        this.container = container;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.modeler.core.container#findByUUID(com.metamatrix.core.id.ObjectID, boolean)
     * @since 4.3
     */
    public Resource findByUUID( final ObjectID uuid,
                                final boolean searchExternal ) {
        Resource result = findResourceByUUID(uuid, new ArrayList(getContainer().getResources()));
        if (result == null && searchExternal) {
            ResourceSet[] externalSets = getContainer().getExternalResourceSets();
            for (int i = 0; i != externalSets.length; ++i) {
                result = findResourceByUUID(uuid, new ArrayList(externalSets[i].getResources()));
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.container#findByUUID(com.metamatrix.core.id.ObjectID,
     *      org.eclipse.emf.ecore.resource.Resource[])
     * @since 4.3
     */
    public Resource findByUUID( final ObjectID uuid,
                                final Resource[] scope ) {
        return findResourceByUUID(uuid, Arrays.asList(scope));
    }

    /**
     * @see com.metamatrix.modeler.core.container#findByImport(com.metamatrix.metamodels.core.ModelImport, boolean)
     * @since 4.3
     */
    public Resource findByImport( final ModelImport theImport,
                                  final boolean searchExternal ) {

        Resource result = findResourceByImport(theImport, new ArrayList(getContainer().getResources()));
        if (result == null && searchExternal) {
            ResourceSet[] externalSets = getContainer().getExternalResourceSets();
            for (int i = 0; i != externalSets.length; ++i) {
                result = findResourceByImport(theImport, new ArrayList(externalSets[i].getResources()));
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.container#findByImport(com.metamatrix.metamodels.core.ModelImport,
     *      org.eclipse.emf.ecore.resource.Resource[])
     * @since 4.3
     */
    public Resource findByImport( final ModelImport theImport,
                                  final Resource[] scope ) {
        return findResourceByImport(theImport, Arrays.asList(scope));
    }

    /**
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByImport(org.eclipse.xsd.XSDSchemaDirective, boolean)
     * @since 4.3
     */
    public Resource findByImport( final XSDSchemaDirective theImport,
                                  final boolean searchExternal ) {
        Resource result = findResourceByImport(theImport, new ArrayList(getContainer().getResources()));
        if (result == null && searchExternal) {
            ResourceSet[] externalSets = getContainer().getExternalResourceSets();
            for (int i = 0; i != externalSets.length; ++i) {
                result = findResourceByImport(theImport, new ArrayList(externalSets[i].getResources()));
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByImport(org.eclipse.xsd.XSDSchemaDirective,
     *      org.eclipse.emf.ecore.resource.Resource[])
     * @since 4.3
     */
    public Resource findByImport( final XSDSchemaDirective theImport,
                                  final Resource[] scope ) {
        return findResourceByImport(theImport, Arrays.asList(scope));
    }

    /**
     * @see com.metamatrix.modeler.core.container#findByName(java.lang.String, boolean, boolean)
     * @since 4.3
     */
    public Resource[] findByName( final String theName,
                                  final boolean caseSensitive,
                                  final boolean searchExternal ) {
        Collection result = findResourcesByName(theName, caseSensitive, new ArrayList(getContainer().getResources()));
        if (searchExternal) {
            ResourceSet[] externalSets = getContainer().getExternalResourceSets();
            for (int i = 0; i != externalSets.length; ++i) {
                result.addAll(findResourcesByName(theName, caseSensitive, new ArrayList(externalSets[i].getResources())));
            }
        }
        return (Resource[])result.toArray(new Resource[result.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.container#findByURI(org.eclipse.emf.common.util.URI, boolean)
     * @since 4.3
     */
    public Resource findByURI( final URI theUri,
                               final boolean searchExternal ) {
        Resource result = getContainer().getResource(theUri, false);
        if (result == null && searchExternal) {
            ResourceSet[] externalSets = getContainer().getExternalResourceSets();
            for (int i = 0; i != externalSets.length; ++i) {
                result = externalSets[i].getResource(theUri, false);
                if (result != null) {
                    break;
                }
            }
        }
        // If the result cannot be an external resource then check its resource set
        if (result != null && !searchExternal && ModelerCore.getContainer(result) != getContainer()) {
            result = null;
        }
        return result;
    }

    /**
     * Used to find a resource using a relative URI referenced to a known resource
     * 
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findByWorkspaceUri(org.eclipse.emf.common.util.URI,
     *      org.eclipse.emf.ecore.resource.Resource)
     * @since 5.0.2
     */
    public Resource findByWorkspaceUri( final URI theRelativeUri,
                                        final Resource knownResource ) {
        try {
            // Defect 23396 - an NPE was thrown here because a relativeUri was provide (i.e. BooksDatatypes.xsd) which resulted
            // in a NULL model Resource or null corresponding resource.
            ModelResource mr = ModelerCore.getModelEditor().findModelResource(knownResource);
            if (mr != null && mr.getCorrespondingResource() != null) {
                IPath thePath = mr.getCorrespondingResource().getFullPath().removeLastSegments(1).append(theRelativeUri.toString());
                IResource iResrc = ResourcesPlugin.getWorkspace().getRoot().findMember(thePath);
                if (iResrc instanceof IFile) {
                    ModelResource modelResource = ModelerCore.getModelEditor().findModelResource((IFile)iResrc);
                    if (modelResource != null) {
                        return modelResource.getEmfResource();
                    }
                }
            }
        } catch (ModelWorkspaceException theException) {
            ModelerCore.Util.log(theException);
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.container#isExternalResource(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    public boolean isExternalResource( final URI theUri ) {
        if (theUri != null) {
            return isExternalResource(findByURI(theUri, true));
        }
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.container#isExternalResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public boolean isExternalResource( final Resource theResource ) {
        if (theResource != null && ModelerCore.getContainer(theResource) == getContainer()) {
            return false;
        }
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.container#isBuiltInResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public boolean isBuiltInResource( final Resource theResource ) {
        if (theResource != null) {
            return isBuiltInResource(theResource.getURI());
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.container#isBuiltInResource(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    public boolean isBuiltInResource( final URI theUri ) {
        if (theUri == null) {
            return false;
        }

        // The Container for this ResourceFinder should not have a built-in resouce within its contents.
        // If we find a non-external resource by this URI then return false.
        if (findByURI(theUri, false) != null) {
            return false;
        }

        String uriString = URI.decode(theUri.toString());

        // If the URI is to the Teiid Designer built-in datatypes model
        if (uriString.startsWith(BUILTIN_DATATYPES_URI) || uriString.endsWith(DATATYPES_MODEL_FILE_NAME)) {
            return true;
        }

        // If the URI is to the Teiid Designer built-in UML primitive types model
        if (uriString.startsWith(UML_PRIMITIVE_TYPES_INTERNAL_URI) || uriString.endsWith(UML_PRIMITIVE_TYPES_MODEL_FILE_NAME)) {
            return true;
        }

        // If the URI is to the Teiid Designer built-in relationship model
        if (uriString.startsWith(RELATIONSHIP_PRIMITIVE_TYPES_INTERNAL_URI)
            || uriString.endsWith(RELATIONSHIP_PRIMITIVE_TYPES_MODEL_FILE_NAME)) {
            return true;
        }

        // If the URI is to one of the XSD global resources
        ResourceSet globalResourceSet = XSDSchemaImpl.getGlobalResourceSet();
        if (globalResourceSet != null && globalResourceSet.getResource(theUri, false) != null) {
            return true;
        }

        // If the URI is a logical URI to one of the XSD global resources
        if (uriString.endsWith(SCHEMA_FOR_SCHEMA_URI_2001_SUFFIX) || uriString.endsWith(MAGIC_SCHEMA_URI_2001_SUFFIX)
            || uriString.endsWith(SCHEMA_INSTANCE_URI_2001_SUFFIX)) {

            return true;
        }

        // If the URI is to one of the Teiid Designer metamodel resources
        if (uriString.startsWith(METAMODEL_PREFIX)) {
            return true;
        }

        // If the URI is to one of the IBM UML2 metamodel resources
        if (uriString.startsWith(UML2_METAMODELS_PREFIX)) {
            return true;
        }

        // If the URI is to the Teiid Designer built-in system models
        if (isBuiltInSystemResource(theUri)) {
            return true;
        }

        // If the URI is to one of our metamodels
        try {
            if (ModelerCore.getMetamodelRegistry().containsURI(theUri)) {
                return true;
            }
        } catch (Exception e) {
            // do nothing
        }

        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.container#isBuiltInSystemResource(org.eclipse.emf.common.util.URI)
     * @since 4.3
     */
    public boolean isBuiltInSystemResource( final URI theUri ) {
        if (theUri != null) {
            // If the models are not found in an external resource set then
            // they cannot be one of the built-in shared resources
            if (!isExternalResource(theUri)) {
                return false;
            }

            // Check if the string form of the URI matches one of the expected names
            String uriString = URI.decode(theUri.toString());
            if (uriString.startsWith(SYSTEM_PHYSICAL_INTERNAL_URI) || uriString.startsWith(SYSTEM_VIRTUAL_INTERNAL_URI)
                || uriString.endsWith(SYSTEM_PHYSICAL_MODEL_FILE_NAME) || uriString.endsWith(SYSTEM_VIRTUAL_MODEL_FILE_NAME)) {

                return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.container#isBuiltInSystemResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public boolean isBuiltInSystemResource( final Resource theResource ) {
        if (theResource != null) {
            return isBuiltInSystemResource(theResource.getURI());
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.container#findSystemResources()
     * @since 4.3
     */
    public Resource[] findSystemResources() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see com.metamatrix.modeler.core.container#findByEObject(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public Resource findByEObject( final EObject eObject ) {
        if (eObject == null) {
            return null;
        }
        Resource result = null;
        if (eObject.eIsProxy()) {
            URI proxyURI = ((InternalEObject)eObject).eProxyURI();
            result = findByURI(proxyURI.trimFragment(), true);
        } else {
            result = eObject.eResource();
        }
        return result;
    }

    /**
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findReferencesFrom(org.eclipse.emf.ecore.resource.Resource,
     *      boolean, boolean)
     * @since 4.3
     */
    public Resource[] findReferencesFrom( final Resource theResource,
                                          final boolean recurse,
                                          final boolean includeExternal ) {

        final List result = getExternallyReferencedResources(theResource, recurse, includeExternal);

        if (!includeExternal) {
            // Filter out Resources that are not contained within the Finder's resource set
            for (final Iterator iter = result.iterator(); iter.hasNext();) {
                final Resource eResource = (Resource)iter.next();
                if (isExternalResource(eResource)) {
                    iter.remove();
                }
            }
        }
        return (Resource[])result.toArray(new Resource[result.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findReferencesTo(org.eclipse.emf.ecore.resource.Resource,
     *      boolean)
     * @since 4.3
     */
    public Resource[] findReferencesTo( final Resource theResource,
                                        final boolean recurse ) {
        final Collection result = new HashSet();
        if (theResource != null) {

            // Create a copy of the resource set contents (prevent ConcurrentModificationException)
            final List eResources = new ArrayList(getContainer().getResources());

            // Remove the resource we are finding references to from the collection
            eResources.remove(theResource);

            // For each resource in the resource set, record which of those resources
            // references the input resource ...
            for (final Iterator i = eResources.iterator(); i.hasNext();) {
                final Resource eResource = (Resource)i.next();

                final List externalResources = getExternallyReferencedResources(eResource, recurse, true);
                for (final Iterator j = externalResources.iterator(); j.hasNext();) {
                    final Resource rsrc = (Resource)j.next();
                    if (rsrc == theResource) {
                        result.add(eResource);
                        break;
                    }
                }
            }
        }
        return (Resource[])result.toArray(new Resource[result.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findUnresolvedResourceLocations(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public String[] findUnresolvedResourceLocations( final Resource theResource ) {
        final Collection result = new HashSet();
        if (theResource != null) {
            List dummyList = new ArrayList();
            Set unresolvedURIs = new HashSet();

            // just get the unresolved URI's
            addExternallyReferencedResources(theResource, false, true, dummyList, unresolvedURIs);

            // Check each one, decode it and add to result
            for (Iterator iter = unresolvedURIs.iterator(); iter.hasNext();) {
                URI uri = (URI)iter.next();
                result.add(URI.decode(uri.toString()));
            }

        }
        return (String[])result.toArray(new String[result.size()]);
    }

    /**
     * @see com.metamatrix.modeler.core.container.ResourceFinder#findMissingImportLocations(org.eclipse.emf.ecore.resource.Resource)
     * @since 5.0.2
     */
    public String[] findMissingImportLocations( final Resource theResource ) {
        final Collection result = new HashSet();
        if (theResource != null && theResource instanceof MMXmiResource) {

            // just get the Resolved external resources (i.e. desired imports)
            Collection unresolvedResources = getResolvedExternalResources(theResource);

            // For each unresolved resource, create a location and check existing imports for this location.
            for (Iterator iter = unresolvedResources.iterator(); iter.hasNext();) {
                Resource nextRes = (Resource)iter.next();

                String location = ModelerCore.getModelEditor().createModelLocation((MMXmiResource)theResource, nextRes);

                if (location != null) {
                    ModelImport existingImport = ModelerCore.getModelEditor().getExistingModelImportForLocation((MMXmiResource)theResource,
                                                                                                                location);
                    // If no existing import, add location to result
                    if (existingImport == null) {
                        result.add(location);
                    }
                }
            }
        }
        return (String[])result.toArray(new String[result.size()]);
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    protected Container getContainer() {
        return this.container;
    }

    protected Resource findResourceByUUID( final ObjectID uuid,
                                           final List eResources ) {
        Resource result = null;
        if (uuid != null && eResources != null) {

            // Iterate through each resource in the resource set
            for (Iterator iter = eResources.iterator(); iter.hasNext();) {
                Resource rsrc = (Resource)iter.next();
                if (rsrc instanceof MMXmiResource) {
                    if (uuid.equals(((MMXmiResource)rsrc).getUuid())) {
                        result = rsrc;
                        break;
                    }
                }
            }
        }
        return result;
    }

    protected Resource findResourceByImport( final ModelImport theImport,
                                             final List eResources ) {
        Resource result = null;
        if (theImport != null && eResources != null) {

            // A ModelImport that references an EResource instance
            // will have the UUID of that EResource
            final String uuidString = theImport.getUuid();
            if (!StringUtil.isEmpty(uuidString)) {
                final ObjectID uuid = stringToObjectID(uuidString);
                if (uuid != null) {
                    result = findResourceByUUID(uuid, eResources);
                    if (result != null) {
                        return result;
                    }
                }
            }

            // A ModelImport that references a Resource instance will have
            // the relative path to that resource in its location
            final String modelLocation = theImport.getModelLocation();
            if (!StringUtil.isEmpty(modelLocation) && theImport.eResource() != null) {
                // Defect 23340 - Simplifying this check.
                URI modelLocationURI = URI.createURI(modelLocation);
                if (modelLocationURI.isRelative()) {
                    result = findByWorkspaceUri(modelLocationURI, theImport.eResource());
                } else {
                    result = findByURI(modelLocationURI, true);
                }

                if (result != null && eResources.contains(result)) {
                    return result;
                }
                result = null;
            }

            // A ModelImport that references an XSDResource instance
            // will have the name of that XSDResource in its path/location
            final String path = theImport.getPath();
            final String modelPath = (StringUtil.isEmpty(path) ? modelLocation : path);
            if (!StringUtil.isEmpty(modelPath)) {

                // Check if the path represents a logic URI that can be found
                final URI uri = URI.createURI(modelPath);
                result = findByURI(uri, true);
                if (result != null && eResources.contains(result)) {
                    return result;
                }
                result = null;

                // Check if the resource name matches any resource in the list
                final String name = uri.lastSegment();
                final Collection results = findResourcesByName(name, true, eResources);
                if (results.size() == 1) {
                    result = (Resource)results.iterator().next();
                } else if (results.size() > 1) {
                    for (Iterator iter = eResources.iterator(); iter.hasNext();) {
                        Resource rsrc = (Resource)iter.next();

                        // jh Defect 23067: Changed 'toFileString()' to 'toString()'
                        // so it will handle both files and urls correctly
                        String uriString = rsrc.getURI().toString();
                        if (uriString.endsWith(modelPath)) {
                            return rsrc;
                        }
                    }
                }
            }
        }
        return result;
    }

    protected Resource findResourceByImport( final XSDSchemaDirective theImport,
                                             final List eResources ) {
        Resource result = null;
        if (theImport != null && eResources != null) {

            // An XSDSchemaDirective referencing an XSDResource instance will have
            // the relative path to that resource in its location
            final String schemaLocation = theImport.getSchemaLocation();
            if (!StringUtil.isEmpty(schemaLocation) && theImport.eResource() != null) {

                // Check if the path represents a logic URI that can be found
                final URI uri = URI.createURI(schemaLocation);
                result = findByURI(uri, true);
                if (result != null && eResources.contains(result)) {
                    return result;
                }
                result = null;

                XSDResourceImpl eResource = (XSDResourceImpl)theImport.eResource();
                URI baseLocationURI = eResource.getURI();
                // If the base resource URI was created as a file URI then it's path is encoded so before we
                // resolve the referenced resource we need to encode it's relative path
                URI schemaLocationURI = UriHelper.makeAbsoluteUri(baseLocationURI, schemaLocation);
                // URI schemaLocationURI = (baseLocationURI.isFile() ? URI.createURI(schemaLocation, false):
                // URI.createURI(schemaLocation));
                // if (baseLocationURI.isHierarchical() && !baseLocationURI.isRelative() && schemaLocationURI.isRelative()) {
                // schemaLocationURI = schemaLocationURI.resolve(baseLocationURI);
                // }
                result = findByURI(schemaLocationURI, true);
                if (result != null && eResources.contains(result)) {
                    return result;
                }
                result = null;

                // Check if the resource name matches any resource in the list
                final String name = URI.createURI(schemaLocation).lastSegment();
                final Collection results = findResourcesByName(name, true, eResources);
                if (results.size() == 1) {
                    result = (Resource)results.iterator().next();

                } else if (results.size() > 1) {
                    // Ensure that all referenced schemas are resolved
                    getExternallyReferencedResources(eResource, true, false);

                    // Match the input XSDSchemaDirective to one in the resource
                    for (final Iterator i = eResource.getSchema().eContents().iterator(); i.hasNext();) {
                        EObject eObj = (EObject)i.next();
                        if (eObj instanceof XSDSchemaDirective && theImport == eObj) {
                            XSDSchema resolvedSchema = ((XSDSchemaDirective)eObj).getResolvedSchema();
                            if (resolvedSchema != null) {
                                Resource refResource = resolvedSchema.eResource();
                                if (eResources.contains(refResource)) {
                                    return refResource;
                                }
                            }
                        }
                    }

                }
            }

        }
        return result;
    }

    protected Collection findResourcesByName( final String name,
                                              final boolean caseSensitive,
                                              final List eResources ) {
        Collection result = new HashSet(3);
        if (name != null && eResources != null) {

            // Iterate through each resource in the resource set
            for (Iterator iter = eResources.iterator(); iter.hasNext();) {
                Resource rsrc = (Resource)iter.next();
                if (rsrc != null) {
                    final String rsrcName = rsrc.getURI().lastSegment();
                    if (caseSensitive && name.equals(rsrcName)) {
                        result.add(rsrc);
                    }
                    if (!caseSensitive && name.equalsIgnoreCase(rsrcName)) {
                        result.add(rsrc);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return the list of external resources referenced by the specified resource
     * 
     * @param eResource the resource to process for references
     * @param recurse If true, the result will include all direct and indirect dependent resources otherwise only the direct
     *        dependencies are returned.
     * @param includeExternal If true, external resource references will be included in the resulant array, otherwise they will be
     *        excluded from the result.
     * @return result
     */
    protected List getExternallyReferencedResources( final Resource eResource,
                                                     final boolean recurse,
                                                     final boolean includeExternal ) {

        List result = new ArrayList();
        Set unresolvedURIs = new HashSet();
        if (eResource != null) {
            addExternallyReferencedResources(eResource, recurse, includeExternal, result, unresolvedURIs);

            // Log any resource references that cannot be resolved in the resource set
            if (!unresolvedURIs.isEmpty()) {
                for (Iterator iter = unresolvedURIs.iterator(); iter.hasNext();) {
                    URI uri = (URI)iter.next();
                    String msg = ModelerCore.Util.getString("DefaultResourceFinder.Unable_to_resolve_ref_to_resource_with_uri", uri); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR, msg);
                }
            }

        }
        return result;
    }

    /**
     * Return the list of external resources referenced by the specified resource
     * 
     * @param eResource the resource to process for references
     * @return result
     */
    protected Collection getResolvedExternalResources( final Resource eResource ) {

        List externalResources = new ArrayList();
        if (eResource != null) {
            addExternallyReferencedResources(eResource, false, true, externalResources, new HashSet());
        }
        return externalResources;
    }

    /**
     * Add external resources referenced by the specified resource to the resultant list
     * 
     * @param eResource the resource to process for references
     * @param recurse if true, the result will include all direct and indirect dependent resources otherwise only the direct
     *        dependencies are returned.
     * @param includeExternal If true, external resource references will be included in the resulant array, otherwise they will be
     *        excluded from the result.
     * @param result the resultant list to add to
     */
    protected void addExternallyReferencedResources( final Resource eResource,
                                                     final boolean recurse,
                                                     final boolean includeExternal,
                                                     final List result,
                                                     final Set unresolvedResourceURIs ) {
        if (eResource != null) {

            // The resource must be loaded to process its references
            if (!eResource.isLoaded()) {
                try {
                    eResource.load(getContainer().getLoadOptions());
                } catch (IOException err) {
                    String msg = ModelerCore.Util.getString("DefaultResourceFinder.Error_loading_resource", eResource); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR, msg);
                }
            }

            // If the resource is an XSDResource instance
            if (eResource instanceof XSDResourceImpl) {
                addExternallyReferencedResourcesForXsd((XSDResourceImpl)eResource,
                                                       recurse,
                                                       includeExternal,
                                                       result,
                                                       unresolvedResourceURIs);

                // If the resource is an EResource instances
            } else if (eResource instanceof MMXmiResource) {
                addExternallyReferencedResourcesForXmi((MMXmiResource)eResource,
                                                       recurse,
                                                       includeExternal,
                                                       result,
                                                       unresolvedResourceURIs);
            }
        }
    }

    /**
     * Add external resources referenced by the specified XMI resource to the resultant list
     * 
     * @param eResource the resource to process for references
     * @param recurse if true, the result will include all direct and indirect dependent resources otherwise only the direct
     *        dependencies are returned.
     * @param includeExternal If true, external resource references will be included in the resulant array, otherwise they will be
     *        excluded from the result.
     * @param result the resultant list to add to
     */
    protected void addExternallyReferencedResourcesForXmi( final MMXmiResource eResource,
                                                           final boolean recurse,
                                                           final boolean includeExternal,
                                                           final List result,
                                                           final Set unresolvedResourceURIs ) {
        if (eResource != null) {

            // Collect all the EObject instances in this resource using the
            // ModelObjectCollector class to avoid a ConcurrentModificationException
            // that may occur when using the TreeIterator (i.e. super.getAllContents())
            final ModelObjectCollector moc = new ModelObjectCollector(eResource);

            // Iterate through the contents of this resource collecting externally referenced resources
            final List partialResult = new ArrayList();
            for (final Iterator i = moc.getEObjects().iterator(); i.hasNext();) {
                final EObject eObject = (EObject)i.next();

                for (final Iterator j = eObject.eClass().getEAllReferences().iterator(); j.hasNext();) {
                    final EReference eReference = (EReference)j.next();
                    processReference(eResource, eObject, eReference, partialResult, unresolvedResourceURIs);
                }
            }

            // Add the result for this resource to the overall result
            for (Iterator i = partialResult.iterator(); i.hasNext();) {
                Resource rsrc = (Resource)i.next();
                if (rsrc != null && !result.contains(rsrc)) {
                    if (!includeExternal && isExternalResource(rsrc)) {
                        continue;
                    }
                    result.add(rsrc);
                    if (recurse) {
                        addExternallyReferencedResources(rsrc, recurse, includeExternal, result, unresolvedResourceURIs);
                    }
                }
            }
        }
    }

    /**
     * Add external resources referenced by the specified XSD resource to the resultant list
     * 
     * @param eResource the resource to process for references
     * @param recurse if true, the result will include all direct and indirect dependent resources otherwise only the direct
     *        dependencies are returned.
     * @param includeExternal If true, external resource references will be included in the resulant array, otherwise they will be
     *        excluded from the result.
     * @param result the resultant list to add to
     */
    protected void addExternallyReferencedResourcesForXsd( final XSDResourceImpl eResource,
                                                           final boolean recurse,
                                                           final boolean includeExternal,
                                                           final List result,
                                                           final Set unresolvedResourceURIs ) {
        if (eResource != null) {
            // Resolve all schema directives (import/include/redefine)
            Set visitedXsdResources = new HashSet();
            resolveSchemaDirectives(eResource, recurse, visitedXsdResources, unresolvedResourceURIs);

            // Add the resource referenced through the directive to the overall result
            XSDSchema schema = eResource.getSchema();
            for (final Iterator i = schema.eContents().iterator(); i.hasNext();) {
                EObject eObj = (EObject)i.next();
                if (eObj instanceof XSDSchemaDirective) {
                    XSDSchema resolvedSchema = ((XSDSchemaDirective)eObj).getResolvedSchema();
                    Resource rsrc = (resolvedSchema != null ? resolvedSchema.eResource() : null);
                    if (rsrc != null && !result.contains(rsrc)) {
                        if (!includeExternal && isExternalResource(rsrc)) {
                            continue;
                        }
                        result.add(rsrc);
                        if (recurse) {
                            addExternallyReferencedResources(rsrc, recurse, includeExternal, result, unresolvedResourceURIs);
                        }
                    }
                }
            }

            // Ensure that the schema for schema resource (e.g. "http://www.w3.org/2001/XMLSchema") is added to the result;
            if (includeExternal) {
                Resource rsrc = schema.getSchemaForSchema().eResource();
                if (rsrc != null && !result.contains(rsrc)) {
                    result.add(rsrc);
                }
            }
        }
    }

    /**
     * Walk through all XSDDirectives for the specified XSDResource and attempt to resolve those that are undefined.
     * 
     * @param eResource
     * @param recurse
     * @param visited
     * @since 4.3
     */
    protected void resolveSchemaDirectives( final XSDResourceImpl eResource,
                                            final boolean recurse,
                                            final Set visited,
                                            final Set unresolvedResourceURIs ) {

        if (eResource != null && !visited.contains(eResource)) {
            // The resource must be loaded to retrieve its contents
            if (!eResource.isLoaded()) {
                try {
                    eResource.load(getContainer().getLoadOptions());
                } catch (IOException err) {
                    String msg = ModelerCore.Util.getString("DefaultResourceFinder.Error_loading_resource", eResource); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR, msg);
                }
            }
            // Add this resource to the list of those visited
            visited.add(eResource);

            // Check all imports to see if they were resolved
            for (final Iterator i = eResource.getSchema().eContents().iterator(); i.hasNext();) {
                EObject eObj = (EObject)i.next();
                if (eObj instanceof XSDSchemaDirective) {
                    XSDSchema resolvedSchema = resolveSchemaDirective((XSDSchemaDirective)eObj);

                    // Log any unresolved schema directives
                    if (resolvedSchema == null || resolvedSchema.eResource() == null
                        || resolvedSchema.eResource().getResourceSet() == null) {
                        URI unresolvedURI = URI.createURI(((XSDSchemaDirective)eObj).getSchemaLocation());
                        unresolvedResourceURIs.add(unresolvedURI);
                        continue;
                    }

                    // Follow the chain and resolve all directives for the schema being imported
                    if (recurse) resolveSchemaDirectives((XSDResourceImpl)resolvedSchema.eResource(),
                                                         recurse,
                                                         visited,
                                                         unresolvedResourceURIs);
                }
            }
        }
    }

    /**
     * Resolve the specified XSDSchemaDirective and resolve against resources in the resource set
     * 
     * @param eResource
     * @param recurse
     * @param visited
     * @since 4.3
     */
    protected XSDSchema resolveSchemaDirective( final XSDSchemaDirective directive ) {
        XSDSchema resolvedSchema = null;
        if (directive != null) {
            resolvedSchema = directive.getResolvedSchema();

            // Import is not yet resolved, attempt to locate the reference ...
            if (resolvedSchema == null && directive instanceof XSDImportImpl) {
                resolvedSchema = ((XSDImportImpl)directive).importSchema();
            }

            // If the resolvedSchema reference exists but is an eProxy then attempt to resolve it
            if (resolvedSchema != null && resolvedSchema.eIsProxy()) {
                resolvedSchema = (XSDSchema)EcoreUtil.resolve(resolvedSchema, getContainer());
            }

            // Directive is not yet resolved, attempt to locate the referenced
            // XSDResource using the schema location information in the directive
            String location = directive.getSchemaLocation();
            XSDResourceImpl eResource = (XSDResourceImpl)directive.eResource();
            if (resolvedSchema == null && eResource != null && !StringUtil.isEmpty(location)) {
                XSDResourceImpl refdResource = null;

                URI schemaLocationUri = UriHelper.makeAbsoluteUri(eResource.getURI(), location);
                // URI schemaLocationUri = getAbsoluteLocation(eResource.getURI(), location);
                refdResource = (XSDResourceImpl)findByURI(schemaLocationUri, false);

                // Update the directive with the resolved schema
                if (refdResource != null) {
                    resolvedSchema = refdResource.getSchema();
                    directive.setResolvedSchema(resolvedSchema);
                    if (directive instanceof XSDImport) {
                        ((XSDSchemaImpl)resolvedSchema).imported((XSDImport)directive);
                    } else if (directive instanceof XSDInclude) {
                        ((XSDSchemaImpl)resolvedSchema).included((XSDInclude)directive);
                    } else if (directive instanceof XSDRedefine) {
                        ((XSDSchemaImpl)resolvedSchema).redefined((XSDRedefine)directive);
                    }
                }
            }
        }
        return resolvedSchema;
    }

    /**
     * Add the specified list any external resource for this EReference feature
     */
    protected void processReference( final Resource eResource,
                                     final EObject eObject,
                                     final EReference eReference,
                                     final List externalResources,
                                     final Set unresolvedResourceURIs ) {

        if (!eReference.isContainment() && !eReference.isContainer() && !eReference.isVolatile()) {
            // The reference is NOT the container NOR a containment feature ...
            final Object value = eObject.eGet(eReference, false);

            if (eReference.isMany()) {
                // There may be many values ...
                final Iterator valueIter = ((List)value).iterator();
                while (valueIter.hasNext()) {
                    final Object valueInList = valueIter.next();
                    if (valueInList instanceof EObject) {
                        processReferenceValue(eResource,
                                              eObject,
                                              eReference,
                                              (EObject)valueInList,
                                              externalResources,
                                              unresolvedResourceURIs);
                    }
                }
            } else {
                // There may be 0..1 value ...
                if (value instanceof EObject) {
                    processReferenceValue(eResource,
                                          eObject,
                                          eReference,
                                          (EObject)value,
                                          externalResources,
                                          unresolvedResourceURIs);
                }
            }
        }
    }

    /**
     * Add the specified list any external resource for this EReference feature
     */
    protected void processReferenceValue( final Resource eResource,
                                          final EObject eObject,
                                          final EReference eReference,
                                          final EObject value,
                                          final List externalResources,
                                          final Set unresolvedResourceURIs ) {
        if (value == null) {
            return;
        }

        // Check if the object is an EMF proxy ...
        if (value.eIsProxy()) {
            if (value instanceof InternalEObject) {
                final InternalEObject iObject = (InternalEObject)value;
                final URI proxyUri = iObject.eProxyURI();
                Assertion.isNotNull(proxyUri);

                // Get the URI of the resource ...
                URI resourceUri = proxyUri.trimFragment();

                // Make the relative URI absolute if necessary
                URI baseLocationUri = eResource.getURI();
                URI proxyLocationUri = UriHelper.makeAbsoluteUri(baseLocationUri, resourceUri);
                // URI proxyLocationUri = resourceUri;
                // if (baseLocationUri.isHierarchical() && !baseLocationUri.isRelative() && proxyUri.isRelative()) {
                // proxyLocationUri = proxyLocationUri.resolve(baseLocationUri);
                // }
                Resource rsrc = findByURI(proxyLocationUri, true);

                // If the resource URI is a workspace relative path (e.g. "/project/.../model.xmi")
                if (rsrc == null && baseLocationUri.isFile() && resourceUri.toString().charAt(0) == '/') {
                    String baseLocation = URI.decode(baseLocationUri.toFileString());
                    String projectName = resourceUri.segment(0);
                    String proxyLocation = URI.decode(resourceUri.toString());
                    int index = baseLocation.indexOf(projectName);
                    if (index != -1) {
                        proxyLocation = baseLocation.substring(0, index - 1) + proxyLocation;
                        rsrc = findByURI(URI.createFileURI(proxyLocation), true);
                    }
                }

                if (rsrc != null && eResource != rsrc && !externalResources.contains(rsrc)) {
                    externalResources.add(rsrc);
                } else if (rsrc == null) {
                    unresolvedResourceURIs.add(resourceUri);
                }
            }
        } else {
            Resource rsrc = value.eResource();
            if (eResource != rsrc && !externalResources.contains(rsrc)) {
                externalResources.add(rsrc);
            }
        }
    }

    // protected URI getAbsoluteLocation(final URI baseLocationUri, final String location) {
    // Assertion.isNotNull(baseLocationUri);
    // Assertion.isNotNull(location);
    //        
    // // If the base resource URI was created as a file URI then it's path is encoded so before we
    // // resolve the referenced resource we need to encode it's relative path
    // URI locationUri = (baseLocationUri.isFile() ? URI.createURI(location, false): URI.createURI(location));
    // return getAbsoluteLocation(baseLocationUri,locationUri);
    // }
    //   
    // protected URI getAbsoluteLocation(final URI baseLocationUri, final URI relativeLocationUri) {
    // Assertion.isNotNull(baseLocationUri);
    // Assertion.isNotNull(relativeLocationUri);
    //        
    // URI locationUri = relativeLocationUri;
    // if (baseLocationUri.isHierarchical() && !baseLocationUri.isRelative() && locationUri.isRelative()) {
    // locationUri = locationUri.resolve(baseLocationUri);
    // }
    // return locationUri;
    // }

    protected ObjectID stringToObjectID( final String str ) {
        if (str == null || str.length() < UUID_STRING_LENGTH) {
            return null;
        }
        ObjectID uuid = null;

        // Extract the UUID from the input string
        String uuidString = extractUuidFromString(str);
        if (uuidString == null) {
            // Try to lowercase the string and extract the UUID
            uuidString = extractUuidFromString(str.toLowerCase());
        }
        if (uuidString != null) {
            try {
                uuid = IDGenerator.getInstance().stringToObject(uuidString, UUID.PROTOCOL);
            } catch (InvalidIDException e) {
                ModelerCore.Util.log(IStatus.ERROR, e.getMessage());
            }
        }
        return uuid;
    }

    /**
     */
    protected String extractUuidFromString( final String str ) {
        if (str == null || str.length() < UUID_STRING_LENGTH) {
            return null;
        }
        String uuidString = null;

        int strLength = str.length();
        if (strLength == UUID_STRING_LENGTH) {
            // The string is of the form "mmuuid:0b5fb081-1275-1eec-8518-c32201e76066"
            if (str.startsWith(UUID_PROTOCOL_WITH_STANDARD_DELIMITER)) {
                uuidString = str;

                // The string is of the form "mmuuid/0b5fb081-1275-1eec-8518-c32201e76066"
            } else if (str.startsWith(UUID_PROTOCOL_WITH_ALTERNATE_DELIMITER)) {
                uuidString = UUID_PROTOCOL_WITH_STANDARD_DELIMITER
                             + str.substring(UUID_PROTOCOL_WITH_ALTERNATE_DELIMITER.length());
            }

        } else {
            // Extract the UUID if it is embedded within a larger string like a eProxy URI
            // of the form "/project/folder/file.xmi#mmuuid:0b5fb081-1275-1eec-8518-c32201e76066"
            int beginIndex = str.indexOf(UUID_PROTOCOL_WITH_STANDARD_DELIMITER);
            if (beginIndex != -1) {
                int endIndex = Math.min(strLength, beginIndex + UUID_STRING_LENGTH);
                if (endIndex == strLength) {
                    uuidString = str.substring(beginIndex);
                } else if (endIndex < strLength) {
                    uuidString = str.substring(beginIndex, endIndex);
                }
            }
            // Extract the UUID if it is embedded within a larger string like a eProxy URI
            // of the form "/project/folder/file.xmi#mmuuid/0b5fb081-1275-1eec-8518-c32201e76066"
            beginIndex = str.indexOf(UUID_PROTOCOL_WITH_ALTERNATE_DELIMITER);
            if (beginIndex != -1) {
                int endIndex = Math.min(strLength, beginIndex + UUID_STRING_LENGTH);
                if (endIndex == strLength) {
                    uuidString = UUID_PROTOCOL_WITH_STANDARD_DELIMITER
                                 + str.substring(beginIndex + UUID_PROTOCOL_WITH_ALTERNATE_DELIMITER.length());
                } else if (endIndex < strLength) {
                    uuidString = UUID_PROTOCOL_WITH_STANDARD_DELIMITER
                                 + str.substring(beginIndex + UUID_PROTOCOL_WITH_ALTERNATE_DELIMITER.length(), endIndex);
                }
            }

        }
        return uuidString;
    }

}
