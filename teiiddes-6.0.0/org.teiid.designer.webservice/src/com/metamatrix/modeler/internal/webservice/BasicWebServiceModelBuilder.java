/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDDiagnosticSeverity;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.impl.XSDFactoryImpl;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.ResourceNameUtil;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.Import;
import com.metamatrix.metamodels.wsdl.Types;
import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.IWebServiceResource;
import com.metamatrix.modeler.webservice.IWebServiceXsdResource;
import com.metamatrix.modeler.webservice.WebServicePlugin;

/**
 * Basic implementation of {@link com.metamatrix.modeler.webservice.IWebServiceModelBuilder}. This class provides management of
 * the various inputs to the Web Service builder, and can create the {@link ModelGenerator} that is used to execute the builder.
 * 
 * @since 4.2
 */
public class BasicWebServiceModelBuilder implements IWebServiceModelBuilder {

    private IResource parentResource;
    private MetamodelDescriptor modelDescriptor;
    private IPath modelPath;
    private IPath xmlModel;
    private final List resources;
    private Collection selectedWsdlOperations;

    private final Map emfResourceUriByWebServiceResource;
    private final WebServiceResources resourceSet;
    private List xsdWorkspaceResources;
    private final Object xsdWorkspaceResourcesLock = new Object();
    private boolean saveAllBeforeFinish = false;
    private Map urlMap = new HashMap();

    public BasicWebServiceModelBuilder() {
        this.resources = new ArrayList();
        this.resourceSet = new WebServiceResources();
        this.emfResourceUriByWebServiceResource = new HashMap();
        this.selectedWsdlOperations = new HashSet();
        this.xsdWorkspaceResources = new ArrayList();
    }

    // =========================================================================
    // Methods for the Web Service Model
    // =========================================================================

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getXmlModel()
     * @since 4.2
     */
    public IPath getXmlModel() {
        return this.xmlModel;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getParentResource()
     * @since 4.2
     */
    public IResource getParentResource() {
        return this.parentResource;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getModelPath()
     * @since 4.2
     */
    public IPath getModelPath() {
        return this.modelPath;
    }

    public Map getUrlMap() {
        return this.urlMap;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setMetamodelDescriptor(com.metamatrix.modeler.core.metamodel.MetamodelDescriptor)
     * @since 4.2
     */
    public void setMetamodelDescriptor( MetamodelDescriptor theDescriptor ) {
        this.modelDescriptor = theDescriptor;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setParentResource(org.eclipse.core.resources.IResource)
     * @since 4.2
     */
    public void setParentResource( IResource theResource ) {
        this.parentResource = theResource;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setModelPath(org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public void setModelPath( IPath thePath ) {
        this.modelPath = thePath;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setXmlModel(org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public void setXmlModel( IPath theXmlModel ) {
        this.xmlModel = theXmlModel;
    }

    // =========================================================================
    // Methods for the Input Files (WSDL and XSD)
    // =========================================================================

    protected IWebServiceResource addOrFind( final IWebServiceResource resource ) {
        final Iterator iter = this.resources.iterator();
        while (iter.hasNext()) {
            final IWebServiceResource r = (IWebServiceResource)iter.next();
            if (r.equals(resource)) {
                return r;
            }
        }
        // not found, so add
        this.resources.add(resource);
        return resource;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#addResource(java.io.File)
     * @since 4.2
     */
    public IWebServiceResource addResource( File theFile ) throws CoreException {
        ArgCheck.isNotNull(theFile);

        // Check whether the file exists ...
        if (!theFile.exists()) {
            final Object[] params = new Object[] {theFile};
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.FileDoesNotExist", params); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        // Construct the URI and load the file ...
        final URI uri = URI.createFileURI(theFile.getAbsolutePath());
        Resource resource = null;

        // if there is a problem adding/loading resource throw exception
        try {
            resource = this.resourceSet.add(uri);
        } catch (Exception theException) {
            this.resourceSet.remove(uri);
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.ProblemLoadingWsdl", theFile); //$NON-NLS-1$
            final Status status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, IStatus.OK, msg, theException);
            throw new CoreException(status);
        }

        // Get the target namespace and construct a web service resource ...
        final String targetNamespace = this.resourceSet.getTargetNamespace(resource);
        AbstractWebServiceResource wsResource = new FileSystemWebServiceResource(targetNamespace, theFile);
        this.emfResourceUriByWebServiceResource.put(wsResource.getFullPath(), uri);

        // Add to the collection(s) ...
        wsResource = (AbstractWebServiceResource)addOrFind(wsResource);

        return wsResource;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#addResource(org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public IWebServiceResource addResource( IFile theFile ) throws CoreException {
        ArgCheck.isNotNull(theFile);

        // Check whether the file exists ...
        if (!theFile.exists()) {
            final Object[] params = new Object[] {theFile};
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.FileDoesNotExist", params); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        // Construct the URI and load the file ...
        final URI uri = URI.createFileURI(theFile.getFullPath().toString());
        Resource resource = null;

        // if there is a problem adding/loading resource throw exception
        try {
            resource = this.resourceSet.add(uri);
        } catch (Exception theException) {
            this.resourceSet.remove(uri);
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.ProblemLoadingWsdl", theFile); //$NON-NLS-1$
            final Status status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, IStatus.OK, msg, theException);
            throw new CoreException(status);
        }

        // Get the target namespace and construct a web service resource ...
        final String targetNamespace = this.resourceSet.getTargetNamespace(resource);
        AbstractWebServiceResource wsResource = new WorkspaceFileWebServiceResource(targetNamespace, theFile);
        this.emfResourceUriByWebServiceResource.put(wsResource.getFullPath(), uri);

        // Add to the collection(s) ...
        wsResource = (AbstractWebServiceResource)addOrFind(wsResource);

        return wsResource;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#resolve(com.metamatrix.modeler.webservice.IWebServiceResource,
     *      java.io.File)
     * @since 4.2
     */
    public void resolve( IWebServiceResource resource,
                         File theFile ) {
        ArgCheck.isNotNull(resource);
        ArgCheck.isNotNull(theFile);

        // Create the new web service resource ...
        final IWebServiceResource resolved = new FileSystemWebServiceResource(resource.getNamespace(), theFile);

        // Find the original resolved ...
        final IWebServiceResource origResolvedResource = resolved.getResolvedResource();

        // Set the input resource as resolved ...
        if (resource instanceof AbstractWebServiceResource) {
            ((AbstractWebServiceResource)resource).setResolvedResource(resolved);
        }

        // Remove the original resolved ...
        if (origResolvedResource != null) {
            final Collection resolvesResources = origResolvedResource.getResourcesResolved();
            if (resolvesResources.size() == 0) {
                this.remove(origResolvedResource);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#unresolve(com.metamatrix.modeler.webservice.IWebServiceResource)
     * @since 4.2
     */
    public void unresolve( IWebServiceResource resource ) {
        ArgCheck.isNotNull(resource);

        if (resource.isResolved()) {
            resource.setResolvedResource(null);
        }
        final URI uri = (URI)this.emfResourceUriByWebServiceResource.remove(resource.getFullPath());
        if (uri != null) {
            this.resourceSet.remove(uri);
        }
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#resolve(com.metamatrix.modeler.webservice.IWebServiceResource,
     *      org.eclipse.core.resources.IFile)
     * @since 4.2
     */
    public void resolve( IWebServiceResource resource,
                         IFile theFile ) {
        ArgCheck.isNotNull(resource);
        ArgCheck.isNotNull(theFile);

        // Create the new web service resource ...
        final IWebServiceResource resolved = new WorkspaceFileWebServiceResource(resource.getNamespace(), theFile);

        // Find the original resolved ...
        final IWebServiceResource origResolvedResource = resolved.getResolvedResource();

        // Set the input resource as resolved ...
        if (resource instanceof AbstractWebServiceResource) {
            ((AbstractWebServiceResource)resource).setResolvedResource(resolved);
        }

        // Remove the original resolved ...
        if (origResolvedResource != null) {
            final Collection resolvesResources = origResolvedResource.getResourcesResolved();
            if (resolvesResources.size() == 0) {
                this.remove(origResolvedResource);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#remove(com.metamatrix.modeler.webservice.IWebServiceResource)
     * @since 4.2
     */
    public void remove( IWebServiceResource theResource ) {
        ArgCheck.isNotNull(theResource);

        // Remove this resource ...
        if (!this.resources.remove(theResource)) {
            return;
        }

        // Unload the EMF resource (if already loaded) ...
        final URI uri = (URI)this.emfResourceUriByWebServiceResource.remove(theResource.getFullPath());
        if (uri != null) {
            this.resourceSet.remove(uri);
        }

        // And remove anything else that can be ...

        // Remove from the referencing resources ...
        if (theResource instanceof AbstractWebServiceResource) {
            final AbstractWebServiceResource wsResource = (AbstractWebServiceResource)theResource;
            wsResource.removeFromAllReferencers();

            // Remove the referenced resources ...
            final Iterator iter = wsResource.getReferencedResources().iterator();
            while (iter.hasNext()) {
                final AbstractWebServiceResource referenced = (AbstractWebServiceResource)iter.next();
                wsResource.removeReferencedResource(referenced);

                // If reference isn't referenced by anybody else, clean it up ...
                if (referenced.getReferencingResources().size() == 0) {
                    remove(referenced);
                }
            }
        }

    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getResources()
     * @since 4.2
     */
    public Collection getResources() {
        return resources; // return is non-null
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getEmfResource(com.metamatrix.modeler.webservice.IWebServiceResource)
     * @since 4.2
     */
    public Resource getEmfResource( IWebServiceResource theResource ) {
        final URI uri = (URI)this.emfResourceUriByWebServiceResource.get(theResource.getFullPath());
        if (uri != null) {
            return this.resourceSet.get(uri);
        }
        return null;
    }

    // =========================================================================
    // Adding WSDL Resources to builder
    // =========================================================================

    /**
     * This method will take any WSDL resources added to the resource list and will interrogate them for imports that reference
     * other wSDL documents. If some are found they are added to the resource list and all internal collections. After all are
     * found recursively, the complete set will be returned in the Collection returned from this method.
     * 
     * @return a Collection of IWebServiceResource instances
     */
    public Collection getWSDLResources() {

        /*
         * this list will collect all of the web service resources that are processed in this method so they can be returned from
         * this method.
         */
        final ArrayList resourcesCollection = new ArrayList();

        /*
         * This is the 'stack' of resources that remain to be processed. The code in the while below can add to this list in the
         * while loop to emulate recursion. We load this stack initially with any resources that have been explicitly added to the
         * builder by the user (these will most likely be WSDL documents).
         */
        final LinkedList resourcesToProcessForDependencies = new LinkedList(resources);

        while (!resourcesToProcessForDependencies.isEmpty()) {

            final IWebServiceResource wsResourceToProcess = (IWebServiceResource)resourcesToProcessForDependencies.removeFirst();

            resourcesCollection.add(wsResourceToProcess);

            /*
             * we check to see if the wsResourceToProcess is not null and whether it is an UnresolvableWebServiceResource.
             * We dont try to process unresolvables because we cannot know what their content is.
             */
            if (wsResourceToProcess != null && !(wsResourceToProcess instanceof UnresolvedWebServiceResource)) {
                /*
                 * get the URI for the ws resource we are processing.
                 */
                final URI uri = URI.createFileURI(wsResourceToProcess.getFullPath());

                /*
                 * use the handle to a resource set to resolve that uri to a physical resource.
                 */
                final Resource resourceToProcess = resourceSet.add(uri);

                /*
                 * we are only concerned with resources that represent WSDL documents in this method.
                 */
                if (resourceSet.isWsdl(resourceToProcess)) {
                    final List roots = resourceToProcess.getContents();

                    /*
                     * iterate through the contents of the resource looking for 'Definitions'
                     */
                    final Iterator iter = roots.iterator();
                    while (iter.hasNext()) {
                        final Object object = iter.next();
                        if (object instanceof Definitions) {
                            final Definitions defns = (Definitions)object;

                            /*
                             * We found a 'Definitions' object now lets iterate through the 'import' child objects and create web
                             * service resources for each adding them to the proper collections as we go.
                             */
                            final List imports = defns.getImports();
                            final Iterator iter2 = imports.iterator();
                            while (iter2.hasNext()) {
                                IWebServiceResource wsr = null;
                                final Import theImport = (Import)iter2.next();
                                final URI locationUri = URI.createURI(theImport.getLocation());
                                final URI resolvedLocationUri = locationUri.resolve(uri); // resolve relative to the file ..
                                if (resolvedLocationUri != null && resolvedLocationUri.isFile()) {
                                    final File f = new File(resolvedLocationUri.toFileString());

                                    if (f.exists()) {
                                        // Create a new web service resource for each ...

                                        wsr = new FileSystemWebServiceResource(theImport.getNamespace(), f);

                                    }
                                }

                                /*
                                 * the physical resource file did not exist so we indicate such to the user by presenting them
                                 * with this information. An UnresolvedWebServiceResources shows up in the UI as an error
                                 * condition.
                                 */
                                if (wsr == null) {
                                    wsr = new UnresolvedWebServiceResource(theImport.getNamespace());
                                }

                                wsr = addOrFind(wsr);
                                if (wsr instanceof AbstractWebServiceResource) {
                                    ((AbstractWebServiceResource)wsResourceToProcess).addReferencedResource(wsr);
                                }

                                /*
                                 * add this resource to the stack so we can process things that it might depend on as well. A type
                                 * of recursion. We protect against circular imports by checking first whether this resources is
                                 * already in the collection of resources.
                                 */
                                if (emfResourceUriByWebServiceResource.get(wsr.getFullPath()) == null) {
                                    emfResourceUriByWebServiceResource.put(wsr.getFullPath(), resolvedLocationUri);

                                    resourcesToProcessForDependencies.add(wsr);
                                }
                            }

                        }
                    }
                }
            }

        }
        return resourcesCollection;

    }

    // =========================================================================
    // Placing XSDs into the Workspace
    // =========================================================================

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getXsdDestinations()
     * @since 4.2
     */
    public Collection getXsdDestinations() {
        List xsdResources = this.xsdWorkspaceResources;
        if (xsdResources == null) {
            synchronized (xsdWorkspaceResourcesLock) {
                if (this.xsdWorkspaceResources == null) {
                    this.xsdWorkspaceResources = new ArrayList();

                    // Discover all (directly and indirectly) referenced XSDs ...
                    final List xsdWsrTuples = doDiscoverReferencedXsds();

                    // keep track of destination names so we dont reuse
                    List usedNames = new ArrayList();

                    // Iterate through all of the referenced XSDs, and create the IWebServiceXsdResource objects ...
                    final Iterator iter = xsdWsrTuples.iterator();
                    while (iter.hasNext()) {
                        final XSDWebServiceResourceTuple tuple = (XSDWebServiceResourceTuple)iter.next();
                        final XSDSchema schema = tuple.getSchema();
                        final IWebServiceResource wsr = tuple.getResource();
                        final boolean inWorkspaceAlready = wsr instanceof WorkspaceFileWebServiceResource;
                        if (wsr.isWsdl() || (wsr.isXsd() && !inWorkspaceAlready)) {
                            final IWebServiceXsdResource wsXsdResource = doCreateWebServiceXsdResource(schema, wsr, usedNames);
                            if (wsXsdResource != null) {
                                this.xsdWorkspaceResources.add(wsXsdResource);
                            }
                        }
                    }
                }
                xsdResources = this.xsdWorkspaceResources;
            }
        }
        return xsdResources;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setDestinationPath(com.metamatrix.modeler.webservice.IWebServiceXsdResource,
     *      org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public void setDestinationPath( final IWebServiceXsdResource xsdResource,
                                    final IPath workspacePathForXsd ) {
        if (xsdResource instanceof IInternalWebServiceXsdResource) {
            // See if it is different than the existing one ...
            final IPath existing = xsdResource.getDestinationPath();
            boolean match = false;
            if (existing == null) {
                if (workspacePathForXsd == null) {
                    match = true;
                }
            } else {
                if (workspacePathForXsd == null) {
                    match = existing.segmentCount() == 0;
                } else {
                    match = existing.makeAbsolute().equals(workspacePathForXsd.makeAbsolute());
                }
            }
            if (!match) {
                ((IInternalWebServiceXsdResource)xsdResource).setDestinationPath(workspacePathForXsd.makeAbsolute());
            }

            // // If the path has changed, then mark the XSD resources as needing to be rediscovered ...
            // if ( !match ) {
            // synchronized(xsdWorkspaceResourcesLock) {
            // this.xsdWorkspaceResources = null;
            // }
            // }
        }
    }

    /**
     * @param schema the XSD, never null
     * @return
     * @since 4.2
     */
    public IWebServiceXsdResource doCreateWebServiceXsdResource( final XSDSchema schema,
                                                                 final IWebServiceResource origResource,
                                                                 List usedNames ) {
        final WebServiceXsdResource result = new WebServiceXsdResource(schema, schema.getTargetNamespace(),
                                                                       schema.eResource().getURI().toFileString());

        try {
            URI schemaUri = schema.eResource().getURI();
            if (schemaUri.isFile()) {
                schemaUri = URI.createFileURI(schemaUri.toFileString());
            } else {
                schemaUri = schema.eResource().getURI();
            }

            final URI wsdlUri = URI.createFileURI(origResource.getFullPath());

            final IPath webServiceContainerPath = this.modelPath.removeLastSegments(1);

            if (wsdlUri.equals(schemaUri)) {
                /*
                 * This means that the schema represented by this schema object was embedded in the WSDL Document itself in the
                 * 'types' section and must contain schema content other than just 'imports'. In this case we need to 'make up' a
                 * name for this schema and save it as a seperate file so that the web service model can reference constructs
                 * contained in it. We have no model to represent a WSDL document so the schema elements must be moved into a
                 * seperate file to be referenced by other objects in the workspace.
                 */

                /*
                 * this will trim off the .wsdl file extension.
                 */
                final URI wsdlUriWOFileExtension = wsdlUri.trimFileExtension();

                /*
                 * here we get the last segment which will be the wsdl file name without the file extension.
                 */
                final String name = wsdlUriWOFileExtension.lastSegment();

                final String uniqueName = getUniqueName(name, usedNames);

                final IPath uniqueSchemaNamePath = webServiceContainerPath.append(uniqueName
                                                                                  + ResourceNameUtil.DOT_XSD_FILE_EXTENSION);

                result.setDestinationPath(uniqueSchemaNamePath);

            } else {

                /*
                 * here we attempt to make the xsd uri relative to the WDSL uri.
                 */
                URI relativeUri = schemaUri.deresolve(wsdlUri);

                // If this is not a file we assume it is a URL. Get the path and reassign the URI value
                // as the path.
                if (!relativeUri.isFile()) {
                    relativeUri = URI.createURI(relativeUri.path());
                }

                /*
                 * here we check to see if the 'relativized' path really turned out relative. If it did then we maintain the
                 * relative location from the WSDL file to the XSD in the workspace. Otherwise we simply put the XSD at the root
                 * of the project with its original name.
                 */
                String workspacePath = null;
                if (relativeUri.isRelative()) {
                    workspacePath = relativeUri.toString();

                } else {
                    workspacePath = schemaUri.lastSegment();
                }

                final IPath newPath = webServiceContainerPath.append(workspacePath);

                result.setDestinationPath(newPath);
            }

        } catch (RuntimeException err) {
            WebServicePlugin.Util.log(err);
        }
        return result;
    }

    /**
     * this gets a new, unique name given a string and an existing collection The name will be incremented by adding "x", where x
     * is an integer, until a unique name is found
     * 
     * @param name the String to make unique
     * @param collection the existing collection to compare against
     * @return the unique name
     */
    private String getUniqueName( String name,
                                  Collection collection ) {
        if (collection == null) {
            collection = Collections.EMPTY_SET;
        }
        String result = name;
        int incr = 1;
        while (collection.contains(result)) {
            result = name + incr;
            incr++;
        }
        collection.add(result);
        return result;
    }

    /**
     * @param includeReferencedXsds true if all XSDs, including those referenced directly or indirectly by the XSDs, are to be
     *        included, or false if only the directly referenced XSDs should be included.
     * @return the map of XSDSchema to the original IWebServiceResource where it came from ...
     * @since 4.2
     */
    protected List doDiscoverReferencedXsds() {

        /*
         * This list will be the accumulator for all schemas discovered in this method.
         */
        final List wsResourceTuples = new LinkedList();

        /*
         * This List will contain the physical paths to all of the schemas that have been added to
         * the wsResourceTuples.  It will be used to prevent a schema from being added to the 
         * wsResourceTuplesList more than once.
         */
        final List schemaPathsAlreadyAdded = new ArrayList();

        for (Iterator iter = getResources().iterator(); iter.hasNext();) {

            /*
             * pull the first resource off of the Linked List stack.
             */
            final IWebServiceResource wsr = (IWebServiceResource)iter.next();

            /*
             * Get the WebServiceResource that represents the actual physically resolved resource.
             */
            final IWebServiceResource resolved = wsr.getLastResolvedResource();

            /*
             * if this WebServiceResource could not be resolved to a physical resource that exists, then we skip processing it.
             */
            if (resolved == null) {
                continue;
            }

            /*
             * here we decide if we should go find WSDLs or XSD files that are referenced by the WebServiceResource we are
             * currently processing.
             */
            boolean findXsdSchema = false;
            if (resolved.isWsdl()) {
                findXsdSchema = true;
            } else if (resolved.isXsd() && !(resolved instanceof WorkspaceFileWebServiceResource)) {
                /*
                 * If the XSD is in the workspace already, then we assume that we dont need to go get any resources that it
                 * references. We assume that things that it references are in the workspace as well.
                 */
                findXsdSchema = true;
            }

            // If the resource is a WSDL or is a XSD that is outside of the workspace ...
            if (findXsdSchema) {
                // Create emf objects from the resource so that we can interrogate them.
                final Resource emfResource = this.getEmfResource(wsr);

                if (emfResource != null) {
                    final List roots = emfResource.getContents();
                    final Iterator rootIter = roots.iterator();
                    while (rootIter.hasNext()) {
                        /*
                         * here we iterate through all of the root content objects of the resource.
                         */
                        final EObject root = (EObject)rootIter.next();
                        List schemas = new ArrayList();
                        /*
                         * if this resource represents a WSDL document.
                         */
                        if (root instanceof Definitions) {
                            schemas.addAll(doDiscoverReferencedXsdsForWSDLResource((Definitions)root, emfResource));
                        } else if (root instanceof XSDSchema) {
                            schemas.addAll(processSchemas((XSDSchema)root, schemas, emfResource, new ArrayList()));
                        }

                        // Process what we've found ...
                        final Iterator schemaIter = schemas.iterator();
                        while (schemaIter.hasNext()) {
                            final XSDSchema schema = (XSDSchema)schemaIter.next();
                            if (schema != null) {

                                XSDWebServiceResourceTuple tuple = new XSDWebServiceResourceTuple(wsr, schema);
                                String schemaPath = schema.eResource().getURI().toString();

                                /*
                                 * Here we check to see if the resource this scehma resides is a WSDL document.  
                                 * If this condition is true then we just go ahead and add this schema to the List of schemas to
                                 * return from this method.  We do this because this schema cannot be referred to by other
                                 * schemas since it is embedded in a WSDL document. This prevents the problem that we are trying
                                 * to avoid by checking for duplicates in the list of schema paths of having duplicate schemas
                                 * in the list returned from this method.
                                 */
                                if (root instanceof Definitions) {

                                    wsResourceTuples.add(tuple);

                                } else {

                                    /*
                                     * if the map does not already contain the schema we are working with,
                                     * then add it to the List and the map so that we dont add it twice.
                                     * otherwise we may wind up adding duplicate schemas to the list of schemas to be returned.
                                     */
                                    if (!schemaPathsAlreadyAdded.contains(schemaPath)) {
                                        schemaPathsAlreadyAdded.add(schemaPath);
                                        wsResourceTuples.add(tuple);
                                    }
                                }

                            }

                        }
                    }
                }
            }
        }

        return wsResourceTuples;
    }

    protected List doDiscoverReferencedXsdsForWSDLResource( Definitions wsdlDefinition,
                                                            Resource emfResource ) {

        final List schemas = new ArrayList();

        final Types types = wsdlDefinition.getTypes();
        if (types != null) {

            // Get all of the <schema> instances from the <types> element of the WSDL document.
            final EList schemasFromTypes = types.getSchemas();
            if (schemasFromTypes != null) {
                final Iterator itSchemas = schemasFromTypes.iterator();
                // Iterate through all of the schema objects in the WSDL types section.
                while (itSchemas.hasNext()) {
                    final XSDSchemaImpl schemaTemp = (XSDSchemaImpl)itSchemas.next();

                    processSchemas(schemaTemp, schemas, emfResource, new ArrayList());

                }
            }
        }
        return schemas;
    }

    /**
     * This method will return all of the referencing directives contained in the passed in XSDSchema object.
     * 
     * @param schema the schema to inspect for referencing directives.
     * @return a List of XSDSchemaDirective instances.
     */
    protected List getSchemaImportsAndIncludes( XSDSchema schema ) {
        final List referencingDirectives = new LinkedList();

        for (Iterator iter = schema.eContents().iterator(); iter.hasNext();) {
            final Object contentObject = iter.next();
            if (contentObject instanceof XSDSchemaDirective) {
                referencingDirectives.add(contentObject);
            } else {
                /*
                 * this code is here because all directives must come before any other entities in an XSD file, with the exception
                 * of annotations. This way we dont iterate through the rest of the entities in the XSD needlessly.
                 */
                if (!(contentObject instanceof XSDAnnotation)) {
                    break;
                }
            }
        }

        return referencingDirectives;
    }

    /**
     * This method is used to recursively find all of the schemas that the passed in schemas depend on. The returned List will
     * contain XSDSchema instances representing the referenced schemas that are referenced by the schema that is passed in.
     * 
     * @param schema the schema for which to get references
     * @param schemas This List is used in the recursion to 'collect' all of the schemas found recursively. Should be empty when
     *        this method is first called.
     * @param referrringResource This this resource is the parent Resource that represents the schema
     * @param schmaLocationToXSDImpl Another map used in the recursion. Pass in an empty map to call initially.
     * @return A List of XSDSchema instances.
     */
    protected List processSchemas( final XSDSchema schema,
                                   final List schemas,
                                   final Resource referrringResource,
                                   final List schemaLocationsAlreadyAdded ) {

        /*
         * Iterate through the root contents of each of the <schema> elements in the <types> section of the WSDL doc.
         */
        for (Iterator contents = schema.getContents().iterator(); contents.hasNext();) {
            // defect 18274 - was throwing ClassCastException for certain WSDLs.
            // Not all XSDSchemaContent objects are XSDSchemaContentImpls...
            final XSDSchemaContent xsdSchemaContent = (XSDSchemaContent)contents.next();

            /*
             * This will handle both XSDImports and XSDIncludes so that we can process all schemas that are referenced by the
             * given schema.
             */
            if (xsdSchemaContent instanceof XSDSchemaDirective) {

                final XSDSchemaDirective xsdImportImpl = (XSDSchemaDirective)xsdSchemaContent;

                /*
                 * Get the schema location information from the xsd:import instance.
                 */
                String sSchemaLocation = xsdImportImpl.getSchemaLocation();

                /*
                 * if the schema location value was not set on the XSDImportImpl, we try to get it from the underlying DOM
                 * element.
                 */
                if (sSchemaLocation == null) {
                    Node schemaObject = xsdImportImpl.getElement().getAttributes().getNamedItem(XSDConstants.SCHEMALOCATION_ATTRIBUTE);

                    if (schemaObject != null) {
                        sSchemaLocation = xsdImportImpl.getElement().getAttributes().getNamedItem(XSDConstants.SCHEMALOCATION_ATTRIBUTE).toString();
                        sSchemaLocation = trimNamedItem(sSchemaLocation, XSDConstants.SCHEMALOCATION_ATTRIBUTE);
                    } else {
                        // This is a namespace import.
                        continue;
                    }
                }

                /*
                 * create a URI from the schemaLocation value of the xsd:import element.
                 */
                URI uriSchemaLocation = URI.createURI(sSchemaLocation);

                /*
                 * If that URI is relative to its referring resource, then we make it absolute for the purposes of creating a
                 * resource.
                 */
                if (uriSchemaLocation.isRelative()) {

                    /*
                     * create an absolute URI for the referrign resource.
                     */
                    final URI referringResourceUri = referrringResource.getURI();

                    uriSchemaLocation = uriSchemaLocation.resolve(referringResourceUri);
                }

                /*
                 * we process further only if this schema has not already been added to the Map. This will prevent us from
                 * entering infinite recursion when two schemas both import eachother.
                 */
                if (!schemaLocationsAlreadyAdded.contains(uriSchemaLocation)) {

                    // load the schema, if it can be loaded
                    Resource resSchemaResource = null;

                    try {
                        /*
                         * Here we try to load the schema into a Model container from the physical location pointed to by the
                         * schema location in the xsd:import.
                         */
                        resSchemaResource = ModelerCore.getModelContainer().getResource(uriSchemaLocation, true);

                        if (resSchemaResource == null) {
                            /*
                             * we were unable to load the schema from the schema location, therefore we add it to the list of
                             * referenced schemas as being 'NonResolvable'.
                             */
                            NonResolvableXSDSchema xsdImpl = new NonResolvableXSDSchema(uriSchemaLocation);
                            if (!schemas.contains(xsdImpl)) {
                                schemas.add(xsdImpl);
                            }
                            String sMessage = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.ProblemLoadingXsdFromImport", URI.decode(uriSchemaLocation.toString())); //$NON-NLS-1$  
                            /*
                             * we also add a 'diagnostic' which is an error message to be displayed to the user indicating the
                             * problem with this import.
                             */
                            addDiagnosticToSchema(xsdImpl, sMessage);
                        }
                    } catch (CoreException theException) {
                        NonResolvableXSDSchema xsdImpl = new NonResolvableXSDSchema(uriSchemaLocation);
                        addDiagnosticToSchema(xsdImpl, theException.getLocalizedMessage());
                        if (!schemas.contains(xsdImpl)) {
                            schemas.add(xsdImpl);
                        }
                    } catch (Exception theException) {
                        NonResolvableXSDSchema xsdImpl = new NonResolvableXSDSchema(uriSchemaLocation);
                        addDiagnosticToSchema(xsdImpl, theException.getLocalizedMessage());
                        if (!schemas.contains(xsdImpl)) {
                            schemas.add(xsdImpl);
                        }
                    }

                    // add this schema to the schemas collection
                    if (resSchemaResource != null && resSchemaResource.getContents() != null
                        && resSchemaResource.getContents().size() > 0) {
                        Object o = resSchemaResource.getContents().get(0);

                        if (o instanceof XSDSchema) {

                            final XSDSchema tempSchema = (XSDSchema)o;
                            /*
                             * We successfully resolved this schema so put it in the map before recursing to avoid circular
                             * reference problems with schemas that import eachother.
                             */
                            schemaLocationsAlreadyAdded.add(uriSchemaLocation);
                            /*
                             * Everything was successful, so we add the XSDSchema instance content of the imported schema to the
                             * list of referenced schemas.
                             */
                            if (!schemas.contains(o)) {
                                schemas.add(o);
                            }

                            /*
                             * now we recurse to find all of the schemas that this schema refers to.
                             */
                            processSchemas(tempSchema, schemas, resSchemaResource, schemaLocationsAlreadyAdded);
                        }
                    }
                }

            } else if (xsdSchemaContent instanceof XSDAnnotation) {
                /*
                 * This means that there still could be imports only in this schema so we simply ignore this content and continue
                 * to look through the root elements in the schema instance. all imports/includes must come before any other
                 * entity declarations in a schema. Annotations can be mixed in there with them as well.
                 */
                continue;
            } else {
                /*
                 * This means that there is content other than imports and annotations in this <schema> element. in that case we
                 * must add it as a 'schema' to the list of schemas that must be 'processed'. The schema content in this case will
                 * actually be spawned to a seperate file as a new schema document so the new web service model can refer to it.
                 * This is done because WSDL entities are not referenceable in the modeler workspace.
                 */
                if (!schemas.contains(schema)) {
                    schemas.add(schema);
                }
                break;
            }

        }
        return schemas;
    }

    private void addDiagnosticToSchema( XSDSchemaImpl schema,
                                        String sMessage ) {

        XSDDiagnostic xsdDiagnostic = new XSDFactoryImpl().createXSDDiagnostic();

        XSDDiagnosticSeverity severity = XSDDiagnosticSeverity.get(XSDDiagnosticSeverity.ERROR);

        xsdDiagnostic.setSeverity(severity);

        xsdDiagnostic.setMessage(sMessage);

        xsdDiagnostic.setPrimaryComponent(schema);
        xsdDiagnostic.setNode(schema.getElement());

        Element theElement = schema.getElement();
        xsdDiagnostic.setNode(theElement);

        schema.getDiagnostics().add(xsdDiagnostic);

    }

    private String trimNamedItem( String sNamedItem,
                                  String sId ) {
        String sResult = sNamedItem;

        String sMatchString = sId + "=" //$NON-NLS-1$
                              + "\""; //$NON-NLS-1$

        if (sResult.startsWith(sMatchString)) {
            sResult = sResult.substring(sMatchString.length() - 1);
        }

        sResult = sResult.replace('\"', ' ');
        sResult = sResult.trim();

        return sResult;
    }

    public String getNamespaceFromImport( XSDSchema schema ) {
        String sResultNamespace = null;

        for (int i = 0; i < schema.eContents().size(); i++) {
            Object o = schema.eContents().get(i);
            if (o instanceof XSDImportImpl) {
                XSDImportImpl xsdImport = (XSDImportImpl)o;
                Element element = xsdImport.getElement();
                if (element != null) {
                    NamedNodeMap nodeMap = element.getAttributes();
                    if (nodeMap != null) {
                        sResultNamespace = nodeMap.getNamedItem(XSDConstants.NAMESPACE_ATTRIBUTE).toString();
                        break;
                    }
                }
            }
        }

        return sResultNamespace;
    }

    // =========================================================================
    // Validation
    // =========================================================================

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#validateWSDLNamespaces()
     * @since 5.0
     */
    public IStatus validateWSDLNamespaces() {
        final List problems = new LinkedList();
        boolean foundErrors = false;

        // Check that the model's parent resource is valid ...
        if (this.parentResource == null) {
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.MissingWebServiceModelLocation"); //$NON-NLS-1$
            problems.add(new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, MISSING_PARENT_LOCATION, msg, null));
            foundErrors = true;
        } else {
            if (!this.parentResource.exists()) {
                final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.NonExistantWebServiceModelLocation"); //$NON-NLS-1$
                problems.add(new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, PARENT_LOCATION_NONEXISTANT, msg, null));
                foundErrors = true;
            }
        }

        // Check that the model's path is valid ...
        if (this.modelPath == null) {
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.MissingWebServiceModelPath"); //$NON-NLS-1$
            problems.add(new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, MISSING_MODEL_PATH, msg, null));
            foundErrors = true;
        }

        // Check that the metamodel descriptor is valid ...
        if (this.modelDescriptor == null) {
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.MissingWebServiceModelDescriptor"); //$NON-NLS-1$
            problems.add(new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, MISSING_DESCRIPTOR, msg, null));
            foundErrors = true;
        }

        // Check that all namespaces resolved ...
        List unresolved = new ArrayList();
        Iterator itr = getResources().iterator();

        while (itr.hasNext()) {
            IWebServiceResource resource = (IWebServiceResource)itr.next();

            if (!resource.isResolved()) {
                final Object[] params = new Object[] {resource.getNamespace()};
                final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.UnresolvedNamespace", params); //$NON-NLS-1$
                unresolved.add(new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, UNRESOLVED_NAMESPACE, msg, null));
                foundErrors = true;
            }
        }

        if (!unresolved.isEmpty()) {
            int size = unresolved.size();

            if (size == 1) {
                problems.add(unresolved.get(0));
            } else if (size > 1) {
                IStatus[] errors = new IStatus[size];

                for (int i = 0; i < size; ++i) {
                    errors[i] = (IStatus)unresolved.get(i);
                }

                final Object[] params = new Object[] {new Integer(size)};
                final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.MultipleUnresolvedNamespaces", params); //$NON-NLS-1$
                problems.add(new MultiStatus(WebServicePlugin.PLUGIN_ID, UNRESOLVED_NAMESPACE, errors, msg, null));
            }
        }
        return constructStatuses(problems, foundErrors);
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#validateWSDLNamespaces()
     * @since 5.0
     */
    public IStatus validateXSDNamespaces() {
        final List problems = new LinkedList();
        boolean foundErrors = false;

        // Check that the destinations are all valid ...
        final Iterator destIter = this.getXsdDestinations().iterator();
        while (destIter.hasNext()) {
            final IWebServiceXsdResource dest = (IWebServiceXsdResource)destIter.next();
            final IStatus status = dest.isValid();
            if (status.getSeverity() == IStatus.WARNING || status.getSeverity() == IStatus.ERROR) {
                problems.add(status);
                foundErrors = true;
            }
        }

        return constructStatuses(problems, foundErrors);
    }

    /**
     * Create and return an IStatus based
     * 
     * @since 5.0
     */
    public IStatus constructStatuses( List problems,
                                      boolean foundErrors ) {
        // Construct the statuses ...
        IStatus result = null;
        if (problems.size() == 0) {
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.ReadyToGenerate"); //$NON-NLS-1$
            result = new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, msg, null);
        } else if (problems.size() == 1) {
            result = (IStatus)problems.get(0);
        } else {
            final String msg = foundErrors ? WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.UnableToGenerate") : //$NON-NLS-1$
            WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.ReadyToGenerate"); //$NON-NLS-1$
            final MultiStatus multi = new MultiStatus(WebServicePlugin.PLUGIN_ID, MULTIPLE_MESSAGES, msg, null);
            final Iterator iter = problems.iterator();
            while (iter.hasNext()) {
                final IStatus problem = (IStatus)iter.next();
                multi.add(problem);
            }
            result = multi;
        }

        return result;
    }

    // =========================================================================
    // Generation / Execution
    // =========================================================================

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getModelGenerator()
     * @since 4.2
     */
    public ModelGenerator getModelGenerator( boolean isNewModel ) {
        // if ( this.generator == null ) {
        // synchronized(this) {
        // if ( this.generator == null ) {
        // // Create the generator ...
        // this.generator = doCreateModelGenerator();
        // }
        // }
        // }
        // return this.generator;
        return doCreateModelGenerator(isNewModel);
    }

    /**
     * Factory method for the ModelGenerator.
     * 
     * @since 4.2
     */
    protected ModelGenerator doCreateModelGenerator( boolean isNewModel ) {
        final ModelGenerator generator = WebServicePlugin.createModelGenerator(this);
        String desc = null;
        // if ( this.wsdlFiles.size() != 0 ) {
        // final StringBuffer sb = new StringBuffer();
        // boolean first = true;
        // final Iterator iter = this.wsdlFiles.keySet().iterator();
        // while (iter.hasNext()) {
        // final Object wsdlFile = iter.next();
        //                
        // if (wsdlFile instanceof IFile) {
        // sb.append(((IFile)wsdlFile).getFullPath());
        // } else if (wsdlFile instanceof File) {
        // sb.append(((File)wsdlFile).getAbsolutePath());
        // } else {
        // final Object[] params = new Object[] {wsdlFile};
        // final String msg = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.FileIsNotWSDL",params); //$NON-NLS-1$
        // throw new IllegalArgumentException(msg);
        // }
        //
        // if ( !first ) {
        // sb.append(", "); //$NON-NLS-1$
        // }
        // first = false;
        // }
        // final Object[] params = new Object[] {sb.toString()};
        // desc = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.GenerateFromFollowingWSDL",params); //$NON-NLS-1$
        // } else {
        // desc = WebServicePlugin.Util.getString("BasicWebServiceModelBuilder.GenerateFromZeroWSDL"); //$NON-NLS-1$
        // }
        generator.setDescription(desc);
        // Defect 23340
        generator.setNewModelCase(isNewModel);
        generator.setSaveAllBeforeFinish(this.saveAllBeforeFinish);
        return generator;
    }

    public void setSaveAllBeforeFinish( boolean theDoSave ) {
        this.saveAllBeforeFinish = theDoSave;
    }

    public List getAllNewResources() {
        List resources = new ArrayList(10);
        if (!xsdWorkspaceResources.isEmpty()) {
            for (Iterator iter = xsdWorkspaceResources.iterator(); iter.hasNext();) {
                IWebServiceXsdResource xsdRes = (IWebServiceXsdResource)iter.next();
                IPath xsdPath = xsdRes.getDestinationPath();
                IResource resource = null;

                ModelWorkspaceItem wsItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(xsdPath,
                                                                                                                    IResource.FILE);
                if (wsItem != null) {
                    resource = wsItem.getResource();
                }
                if (resource != null) {
                    resources.add(resource);
                }
            }
        }
        if (xmlModel != null) {
            IResource resource = null;

            ModelWorkspaceItem wsItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(xmlModel,
                                                                                                                IResource.FILE);
            if (wsItem != null) {
                resource = wsItem.getResource();
            }
            if (resource != null) {
                resources.add(resource);
            }
        }
        if (modelPath != null) {
            IResource resource = null;

            ModelWorkspaceItem wsItem = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(modelPath,
                                                                                                                IResource.FILE);
            if (wsItem != null) {
                resource = wsItem.getResource();
            }
            if (resource != null) {
                resources.add(resource);
            }
        }
        return resources;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#setSelectedOperations(java.util.Collection)
     * @since 5.0
     */
    public void setSelectedOperations( Collection operations ) {
        this.selectedWsdlOperations = operations;
    }

    /**
     * @see com.metamatrix.modeler.webservice.IWebServiceModelBuilder#getSelectedOperations()
     * @since 5.0
     */
    public Collection getSelectedOperations() {
        return this.selectedWsdlOperations;
    }

    /**
     * This class is used as a container for a Tuple of Web Service Resource and XSDSchema instance. It is a helper class to
     * simplify the data structures required to map which Schemas have already been resolved
     */
    public class XSDWebServiceResourceTuple {

        private IWebServiceResource resource;
        private XSDSchema schema;

        public XSDWebServiceResourceTuple( IWebServiceResource resource,
                                           XSDSchema schema ) {
            this.schema = schema;
            this.resource = resource;
        }

        public IWebServiceResource getResource() {
            return this.resource;
        }

        public void setResource( IWebServiceResource resource ) {
            this.resource = resource;
        }

        public XSDSchema getSchema() {
            return this.schema;
        }

        public void setSchema( XSDSchema schema ) {
            this.schema = schema;
        }

    }
}
