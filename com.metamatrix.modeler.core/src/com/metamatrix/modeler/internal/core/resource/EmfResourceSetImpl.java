/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.DuplicateResourceException;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.util.UriPathConverter;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceFactory;
import com.metamatrix.modeler.internal.core.util.BasicUriPathConverter;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * @author Dennis Fuglsang
 *
 * @since 3.1
 */
public class EmfResourceSetImpl extends ResourceSetImpl implements EmfResourceSet, IEditingDomainProvider {

    //#############################################################################
    //# Instance Attributes
    //#############################################################################
    private final Container container;
    private ResourceSet[] externalResourceSets;
    private UriPathConverter pathConverter;

    //#############################################################################
    //# Constructors
    //#############################################################################

    /**
     * Constructor for EmfResourceSetImpl.
     * @param container The {@link Container} referencing this
     */
	public EmfResourceSetImpl(Container container) {
        super();
        if ( container == null ) {
            final String msg = ModelerCore.Util.getString("EmfResourceSetImpl.The_Container_reference_may_not_be_null_1"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
    	this.container = container;
        this.externalResourceSets = new ResourceSet[0];
	}

	//#############################################################################
	//# Overridden ResourceSetImpl methods
	//#############################################################################


    /**
     * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#createResource(org.eclipse.emf.common.util.URI)
     * @since 4.2
     */
    @Override
    public Resource createResource(URI uri) {
        canCreateResource(uri);
        return super.createResource(uri);
    }

    /**
     * Determine whether the resource given by the URI can be loaded.  This method should
     * throw a {@link WrappedException} or a runtime exception if the resource cannot be loaded.
     * @param uri
     * @return
     * @since 4.2
     */
    protected void canCreateResource(final URI uri) {
        final XMIHeader header = doGetXMIHeader(uri);
        if ( header != null ) {
            // There is already a file at this URI, so check the UUID ...
            final String uuidString = header.getUUID();
            if ( uuidString != null ) {
                try {
                    final ObjectID uuid = IDGenerator.getInstance().stringToObject(uuidString);

                    Resource myExistingResource = this.container.getResourceFinder().findByUUID(uuid, false);
                    if (myExistingResource != null) {
                        final URI existingResourceUri = myExistingResource.getURI();
                        final Object[] params = new Object[] {URI.decode(uri.toString()),URI.decode(existingResourceUri.toString())};
                        final String msg = ModelerCore.Util.getString("EmfResourceSetImpl.Unable_to_load_model_at_0_because_same_as_1",params); //$NON-NLS-1$
                        throw new DuplicateResourceException(myExistingResource,null,msg);
                    }

                    final Object existingObject = this.container.getEObjectFinder().find(uuid);
                    if ( existingObject != null ) {
                        // There is already an object in the container with this ID; don't allow the loading ...
                        if ( existingObject instanceof EObject ) {
                            // First, find the resource URI for the object that already exists ...
                            final Resource existingResource = ((EObject)existingObject).eResource();
                            if ( existingResource != null ) {
                                // If the resource associated with the existing object does not exist
                                // in this resource set then the existing object must have been found
                                // in some external resource set and is not considered a duplicate
                                // Fix for 18439
                                final ResourceSet cntr = existingResource.getResourceSet();
                                if (cntr instanceof Container && cntr != this.getContainer()) {
                                    return;
                                }
                                if (cntr instanceof EmfResourceSet && cntr != this) {
                                    return;
                                }
                                final URI existingResourceUri = existingResource.getURI();
                                final Object[] params = new Object[] {URI.decode(uri.toString()),URI.decode(existingResourceUri.toString())};
                                final String msg = ModelerCore.Util.getString("EmfResourceSetImpl.Unable_to_load_model_at_0_because_same_as_1",params); //$NON-NLS-1$
                                throw new DuplicateResourceException(existingResource,null,msg);
                            }
                        }
                        // Use a default message if necessary ...
                        final Object[] params = new Object[] {uri};
                        final String msg = ModelerCore.Util.getString("EmfResourceSetImpl.Unable_to_load_model_at_0_because_already_loaded",params); //$NON-NLS-1$

                        throw new DuplicateResourceException(msg);
                    }
                } catch (InvalidIDException e) {
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }
        }
    }

    /**
     * Returns a resolved resource available outside of the resource set.
     * It is called by {@link #getResource(URI, boolean) getResource(URI, boolean)}
     * after it has determined that the URI cannot be resolved
     * based on the existing contents of the resource set.
     * @param uri the URI
     * @param loadOnDemand whether demand loading is required.
     */
    @Override
    protected Resource delegatedGetResource(URI uri, boolean loadOnDemand) {
        if ( ModelerCore.DEBUG && ModelerCore.DEBUG_METAMODEL ) {
            Object[] params = new Object[]{uri,new Boolean(loadOnDemand)};
            ModelerCore.Util.log(IStatus.INFO, ModelerCore.Util.getString("EmfResourceSetImpl.DEBUG.EmfResourceSetImpl.delegatedGetResource_for_URI,_loadOnDemand_2",params)); //$NON-NLS-1$
        }

        // Check the metamodel registry for this URI
        final MetamodelRegistry registry = this.getContainer().getMetamodelRegistry();
        if ( registry != null && registry.containsURI(uri) ) {
            final Resource resource = registry.getResource(uri);

            if ( ModelerCore.DEBUG && ModelerCore.DEBUG_METAMODEL ) {
                Object[] params = new Object[]{uri};
                ModelerCore.Util.log(IStatus.INFO, ModelerCore.Util.getString("EmfResourceSetImpl.DEBUG.Returning_resource_in_the_MetamodelRegistry_for_URI_3",params)); //$NON-NLS-1$
            }

            return resource;
        }

        // Check all read-only resource sets for this URI
        for (int i = 0; i < this.externalResourceSets.length; i++) {
            ResourceSet resourceSet = this.externalResourceSets[i];
            Resource resource = resourceSet.getResource(uri,false);
            if (resource != null) {
                return resource;
            }
        }

        return super.delegatedGetResource(uri,loadOnDemand);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getResource(org.eclipse.emf.common.util.URI, boolean)
     */
    @Override
    public Resource getResource(final URI uri, final boolean loadOnDemand) {

        Resource resource = null;
        URI resourceURI   = uri;
        try {
            //Get current uow
            UnitOfWork txn = container.getEmfTransactionProvider().getCurrent();
            boolean alreadyStarted  = true;
            if(!txn.isStarted() ){
                txn.begin();
                alreadyStarted = false;
            }

            // First try retrieving the resource without forcing a load
            resource = super.getResource(resourceURI,false);

            // If the resource was not found, next check if the resource URI string
            // is a workspace relative uri of the form "/Project/.../Resource" then
            // convert it into absolute file URI and try retrieving the resource again.
            boolean canSearchWorkspace = canSearchWorkspace();
            if (resource == null && resourceURI.isRelative() && canSearchWorkspace) {
                resourceURI = this.getUriPathConverter().makeAbsolute(resourceURI,null);
                resource = super.getResource(resourceURI,false);
            }

            // If the resource was still not found, check if the resource URI has a
            // file extension that is currently unknown to the resource factory registry
            if (resource == null) {
                // If this URI represents a Federate Designer model file for which
                // there is no set Resource.Factory registered, then register
                // one before loading it.  Fix for defect 11168.
                this.registerResourceExtension(resourceURI,loadOnDemand);
            }

            // If the resource was not found or not yet loaded ...
            if (resource == null || (!resource.isLoaded() && loadOnDemand)) {
                if(resourceURI.isFile()) {
                    File resourceFile = new File(resourceURI.toFileString());
                    if(resourceFile.exists()) {
                        resource = super.getResource(resourceURI,loadOnDemand);
                    }
                } else {
                    resource = super.getResource(resourceURI,loadOnDemand);
                }

                // It must have just be loaded so it should not be considered modified
                if (resource != null) {
                    resource.setModified(false);
                }
            }

            if(!alreadyStarted){
                txn.commit();
            }
        } catch (ModelerCoreException t) {
            final Object[] params = new Object[]{uri,new Boolean(loadOnDemand)};
            final String msg = ModelerCore.Util.getString("EmfResourceSetImpl.Error_in_EmfResourceSetImpl.getResource()_2",params); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR,t,msg);
            // don't throw
        } catch (DuplicateResourceException t) {
            throw t;    // throw things from super.getResource(...)
        } catch (RuntimeException t) {
            final Object[] params = new Object[]{uri,new Boolean(loadOnDemand)};
            final String msg = ModelerCore.Util.getString("EmfResourceSetImpl.Error_in_EmfResourceSetImpl.getResource()_2",params); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR,t,msg);
            throw t;    // throw things from super.getResource(...)
        }
        return resource;
    }

    /**
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getEObject(org.eclipse.emf.common.util.URI, boolean)
     */
    @Override
    public EObject getEObject(URI uri, boolean loadOnDemand) {
        //super.getEObject(uri, loadOnDemand);
        ArgCheck.isNotNull(uri);
        if (uri != null) {
            URI resourceURI = uri.trimFragment();
            String resourceUriString = resourceURI.toString();
            String uriFragmentString = uri.fragment();

            // If the resource is the built-in datatypes resource then we use
            // the datatype manager to lookup the EObject.  It is better to
            // use the datatype manager which will find a datatype by UUID string
            // or name whereas calling resource.getEObject(String) only works
            // with one of those forms.  The logic to use the datatype manager
            // is also necessary to allow older 4.0 models to be read into a 4.1
            // or later modeler instance.
            if (DatatypeConstants.BUILTIN_DATATYPES_URI.equals(resourceUriString) ||
                ModelerCore.XML_SCHEMA_GENERAL_URI.equals(resourceUriString) ) {
                try {
                    DatatypeManager dtMgr = container.getDatatypeManager();
                    if (dtMgr != null) {
                        return dtMgr.findDatatype(uriFragmentString);
                    }
                    final Object[] params = new Object[]{uri,container};
                    final String msg = ModelerCore.Util.getString("EmfResourceSetImpl.Error_in_EmfResourceSetImpl.getResource()_3",params); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR,msg);
                } catch (ModelerCoreException e1) {
                    ModelerCore.Util.log(IStatus.ERROR,e1,e1.getMessage());
                }
            }

            // Call EmfResourceSet.getResource(URI,false) using the URI without its
            // fragment.  If the result is non-null, then the Resource exists in the
            // ResourceSet, so try to resolve the fragment portion of the URI.  Return
            // either null if the fragment doesn't resolve, or the EObject from the
            // resolution of the fragment.
            Resource resource = this.getResource(resourceURI,false);
            if (resource != null) {
                resource = this.getResource(resourceURI,loadOnDemand);
                return super.getEObject(uri, loadOnDemand);
            }

            // If the resource URI string is a workspace relative uri of the
            // form "/Project/.../Resource" then convert it into absolute file
            // URI and try the lookup again.
            boolean canSearchWorkspace = canSearchWorkspace();
            if (resourceURI.isRelative() && canSearchWorkspace) {
                resourceURI = this.getUriPathConverter().makeAbsolute(resourceURI,null);
                resource = super.getResource(resourceURI,false);
                if (resource != null) {
                    return resource.getEObject(uriFragmentString);
                }
            }

            // If the resource URI is a file URI but there is no file on the file system
            // corresponding to this location then return null because there is no resource
            // that can be loaded to resolve this proxy and we do not want to create an
            // empty resource in our container by calling getResource(URI,true)
            if (resourceURI.isFile()) {
                File resourceFile = new File(resourceURI.toFileString());
                if (!resourceFile.exists()) {
                    return null;
                }
            }

            // The result from EmfResourceSet.getResource(URI,false) returned null, so
            // either the underlying resource is available but has not yet been needed (and
            // no Resource exists for it), or there isn't even an underlying resource
            // (i.e., the URI is bad).  In either case, call ResourceSet.getResource(URI,loadOnDemand)
            // within a try-catch (EmfResourceSet.getResource(URI,loadOnDemand) is not called because
            // it catches and logs all runtime exceptions).  If there is no exception, continue with
            // the resolution of the fragment and return either the resolved EObject or null.
            // If there is an exception, then there is no underlying resource, so return null
            // and do not attempt to resolve any fragment (which may be a UUID) against any other
            // resource within the workspace.  Resetting of eProxy URIs will occur when the
            // model imports for this resource are reorganized.
            try {
                resource = super.getResource(resourceURI,loadOnDemand);
                if (resource != null) {
                    return resource.getEObject(uriFragmentString);
                }
            } catch (Throwable e) {
                // There is no underlying resource so return null
                resource = this.getResource(resourceURI,false);
                if (resource != null) {
                    this.getResources().remove(resource);
                }
                return null;
            }
        }
        return super.getEObject(uri, loadOnDemand);
    }

    //#############################################################################
    //# EmfResourceSet methods
    //#############################################################################

    /**
     * @see com.metamatrix.modeler.internal.core.resource.EmfResourceSet#getContainer()
     */
    public Container getContainer() {
        return this.container;
    }

	//#############################################################################
	//# org.eclipse.emf.edit.domain.IEditingDomainProvider methods
	//#############################################################################

    /**
     * @see org.eclipse.emf.edit.domain.IEditingDomainProvider#getEditingDomain()
     */
    public EditingDomain getEditingDomain() {
        return ((ContainerImpl)container).getEditingDomain();
    }

    //#############################################################################
    //# Public methods
    //#############################################################################

    /**
     * Add a ResourceSet to be used for resolution of a resource URI.  The
     * specified ResourceSet will be treated as read-only and will never be
     * used to load a resource for the URI being checked.
     * @param listener
     */
    public void addExternalResourceSet(final ResourceSet resourceSet) {
        if (resourceSet != null) {
            ArrayList tmp = new ArrayList();
            tmp.addAll(Arrays.asList(this.externalResourceSets));
            tmp.add(resourceSet);
            this.externalResourceSets = new ResourceSet[tmp.size()];
            tmp.toArray(this.externalResourceSets);
        }
    }

    /**
     * Return the array of external resource sets registered with
     * this resource set.
     * @return
     * @since 4.3
     */
    public ResourceSet[] getExternalResourceSets() {
        return this.externalResourceSets;
    }

    /**<p>
     * </p>
     * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#getLoadOptions()
     * @since 4.0
     */
    @Override
    public Map getLoadOptions() {
        final Map options = new HashMap(super.getLoadOptions());
        final Map cntrOptions = this.container.getOptions();
        if (cntrOptions.containsKey(XMLResource.OPTION_XML_MAP)) {
            options.put(XMLResource.OPTION_XML_MAP, cntrOptions.get(XMLResource.OPTION_XML_MAP));
        }
//        options.put(XMLResource.OPTION_DISABLE_NOTIFY, Boolean.TRUE);
        return options;
    }

    /**
     * @return
     */
    public UriPathConverter getUriPathConverter() {
        if (this.pathConverter == null) {
            this.pathConverter = new BasicUriPathConverter();
        }
        return pathConverter;
    }

    /**
     * @param helper
     */
    public void setUriPathConverter(final UriPathConverter converter) {
        this.pathConverter = converter;
    }

    //#############################################################################
    //# Protected methods
    //#############################################################################

    /**
     * Register a Resource.Factory for URI's file extension if this URI represents
     * a Federate Designer model file for which there is no set factory.
     */
    protected void registerResourceExtension(final URI uri, final boolean loadOnDemand) {
        // If we are demanding the load of a resource for which we do not
        // have a Resource.Factory already registered, then check
        if (uri != null && loadOnDemand) {
            final String extension = uri.fileExtension();

            // If the URI is to a VDB archive file then return.  We cannot register
            // this extension to be created as a MtkXmiResourceFactory
//            if (uri != null && ModelUtil.EXTENSION_VDB.equalsIgnoreCase(extension)) {
//                return;
//            }

            // Get the registry map from file extension to resource factory ...
            final Resource.Factory.Registry registry = super.getResourceFactoryRegistry();
            final Map extensionToFactoryMap = registry.getExtensionToFactoryMap();

            // If a Resource.Factory already is already registered for this extension, then return
            if (extensionToFactoryMap.get(extension) != null) {
                return;
            }

            // If the URI represents the path to an existing file ...
            XMIHeader header = doGetXMIHeader(uri);
            // If the header is not null then we know the file is, at least,
            // a well formed xml document.
            if (header != null) {
                // If the XMI version for the header is not null, then return
                // if the file represents an older 1.X model file
                if (header.getXmiVersion() != null && header.getXmiVersion().startsWith("1.")) { //$NON-NLS-1$
                    return;
                }
                // If the UUID for the header is not null, then the file is a
                // Federate Designer model file containing a ModelAnnotation element
                // Register the same Resource.Factory used with .xmi model files
                // for this extension ...
                if (header.getUUID() != null) {
                    Resource.Factory factory = (Resource.Factory) extensionToFactoryMap.get(ModelUtil.EXTENSION_XMI);
                    if (factory != null) {
                        extensionToFactoryMap.put(extension, factory);
                    } else {
                        extensionToFactoryMap.put(extension, new MtkXmiResourceFactory());
                    }
                }
            }
        }
    }

    protected XMIHeader doGetXMIHeader( final URI uri ) {
        // First, normalize the URI to the 'physical' location ...
        URIConverter theURIConverter = getURIConverter();
        URI normalizedURI = theURIConverter.normalize(uri);

        // If the file has an absolute path ...
        //  [note: test isFile first, because URI.toFileString returns NULL if not a file.]
        if (normalizedURI.isFile() && normalizedURI.hasAbsolutePath()) {
            // Find the corresponding file for this location ...
            File resource = new File(normalizedURI.toFileString());

            // Return the header only if the file exists (if it doesn't exist, there's nothing to read) ...
            if (resource.exists()) {
                XMIHeader header = ModelUtil.getXmiHeader(resource);
                return header;
            }
        }
        return null;
    }

    protected boolean canSearchWorkspace() {
        return ModelerCore.isModelContainer(getContainer()); //(ResourcesPlugin.getWorkspace() != null);

    }

}
