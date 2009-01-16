/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.core.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.INotifyChangedListener;

import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.internal.core.xml.xmi.XMIHeaderReader;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.DuplicateResourceException;
import com.metamatrix.modeler.core.resource.EObjectHrefConverter;
import com.metamatrix.modeler.core.resource.EResource;
import com.metamatrix.modeler.core.resource.EResourceSet;

/**
 *
 * @since 4.3
 */
public class EResourceSetImpl extends ResourceSetImpl implements EResourceSet {
    
    private static final char SEGMENT_SEPARATOR = '/';
    
//    private static final boolean DEBUG = false;
        
//    private EObjectFinder eObjectFinder;
//    private EResourceFinder eResourceFinder;
//    private Map physicalToLogicalUri;
    private EObjectHrefConverter eObjectHrefConverter;
    
    private final List externalResourceSets;
    private final IChangeNotifier changeNotifier;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Constructor for EResourceSetImpl.
     * @param container The {@link Container} referencing this
     */
    public EResourceSetImpl() {
        super();

        this.externalResourceSets = new ArrayList(7);
//        this.physicalToLogicalUri = new HashMap();
        this.changeNotifier       = new ChangeNotifier();
        
        // Add an EContentAdapter to all resources in this resource set
        this.eAdapters().add(new EContentAdapter() {
            @Override
            public void notifyChanged(Notification notification) {
                super.notifyChanged(notification);
                EResourceSetImpl.this.getChangeNotifier().fireNotifyChanged(notification);
            }
        });        
    }
    
    //==================================================================================
    //                   O V E R R I D D E N   M E T H O D S
    //==================================================================================

    /** 
     * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#getLoadOptions()
     * @since 4.3
     */
    @Override
    public Map getLoadOptions() {
        final Map options = new HashMap(super.getLoadOptions());
        
        // Disable notifications upon load
        if (options.get(XMLResource.OPTION_DISABLE_NOTIFY) == null) {
            options.put(XMLResource.OPTION_DISABLE_NOTIFY, Boolean.TRUE);
        }

        return options;
    }
    
    /** 
     * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#createResource(org.eclipse.emf.common.util.URI)
     * @since 4.2
     */
    @Override
    public Resource createResource(final URI uri) {
        canCreateResource(uri);
        return super.createResource(uri);
    }

    /** 
     * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#delegatedGetResource(org.eclipse.emf.common.util.URI, boolean)
     * @since 4.3
     */
    @Override
    protected Resource delegatedGetResource(final URI uri, final boolean loadOnDemand) {
        // Check the EPackage registry for this URI
        Resource eResource = super.delegatedGetResource(uri,loadOnDemand);
        
        // Check the external resources sets for this URI
        if (eResource == null) {
            ResourceSet[] eResourceSets = getExternalResourceSets();
            for (int i = 0; i < eResourceSets.length; i++) {
                eResource = eResourceSets[i].getResource(uri,false);
                if (eResource != null) {
                    break;
                }
            }
        }
        
        return eResource;
    }
    
    /** 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getResource(org.eclipse.emf.common.util.URI, boolean)
     */
    @Override
    public Resource getResource(final URI uri, final boolean loadOnDemand) {
        
        Resource eResource = super.getResource(uri, loadOnDemand);
        
        // If the resource URI starts with a '/' then it may be of the form
        // "/Project/.../Resource".  Try to match the path against existing
        // file URIs in the resource set.
        if (eResource == null && uri.toString().charAt(0) == SEGMENT_SEPARATOR) {
            String relativePath = uri.toFileString();
            for (Iterator iter = getResources().iterator(); iter.hasNext();) {
                Resource rsrc    = (Resource)iter.next();
                String uriString = rsrc.getURI().toFileString();
                if (uriString.endsWith(relativePath)) {
                    return rsrc;
                }
            }
        }
        return eResource;
    }

    /** 
     * @see org.eclipse.emf.ecore.resource.ResourceSet#getEObject(org.eclipse.emf.common.util.URI, boolean)
     */
    @Override
    public EObject getEObject(final URI uri, final boolean loadOnDemand) {
        
        // If the resource URI is a file URI but there is no file on the file system
        // corresponding to this location then return null because there is no resource
        // that can be loaded to resolve this proxy and we do not want to create an
        // empty resource in our container by calling getResource(URI,true)
        URI eResourceURI = uri.trimFragment();
        if (eResourceURI.isFile()) {
            File f = new File(eResourceURI.toFileString());
            if (!f.exists()) {
                return null;
            }
        }
        
        // If the resource is the built-in datatypes resource then we potentially need to convert
        // from a logical URI to a physical URI.  For example, if the logicalURI was 
        // "http://www.w3.org/2001/XMLSchema#string" we need to remap this to the physical URI of 
        // "file:/E:/.../cache/www.w3.org/2001/XMLSchema.xsd#//string;XSDSimpleTypeDefinition=7".
        URI lookupUri = uri;
        if (getEObjectHrefConverter() != null) {
            URI physicalUri = getEObjectHrefConverter().getPhysicalURI(uri);
            if (physicalUri != null) {
                lookupUri = physicalUri;
            }
        }

        EObject eObject = super.getEObject(lookupUri, loadOnDemand);
        if (eObject == null) {
            final String msg = ModelerCore.Util.getString("EResourceSetImpl.Unresolved_proxy",lookupUri); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.WARNING, msg);
        }
        return eObject;
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

//    /** 
//     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#getEObjectFinder()
//     * @since 4.3
//     */
//    public EObjectFinder getEObjectFinder() {
//        if (this.eObjectFinder == null) {
//            this.eObjectFinder = new DefaultEObjectFinder(this);
//        }
//        return this.eObjectFinder;
//    }

//    /** 
//     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#getEResourceFinder()
//     * @since 4.3
//     */
//    public EResourceFinder getEResourceFinder() {
//        if (this.eResourceFinder == null) {
//            this.eResourceFinder = new DefaultEResourceFinder(this);
//        }
//        return this.eResourceFinder;
//    }

//    /** 
//     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#setEObjectFinder(com.metamatrix.modeler.internal.core.resource.EObjectFinder)
//     * @since 4.3
//     */
//    public void setEObjectFinder(final EObjectFinder theFinder) {
//        this.eObjectFinder = theFinder;
//    }

//    /** 
//     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#setEResourceFinder(com.metamatrix.modeler.internal.core.resource.EResourceFinder)
//     * @since 4.3
//     */
//    public void setEResourceFinder(final EResourceFinder theFinder) {
//        this.eResourceFinder = theFinder;
//    }

    /** 
     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#getEObjectHrefConverter()
     * @since 4.3
     */
    public EObjectHrefConverter getEObjectHrefConverter() {
        if (this.eObjectHrefConverter == null) {
            this.eObjectHrefConverter = new BuiltInTypesHrefConverter(this);

        }
        return this.eObjectHrefConverter;
    }

    /** 
     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#setEObjectHrefConverter(com.metamatrix.modeler.internal.core.resource.EObjectHrefConverter)
     * @since 4.3
     */
    public void setEObjectHrefConverter(final EObjectHrefConverter theConverter) {
        this.eObjectHrefConverter = theConverter;
    }

    /** 
     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#addListener(org.eclipse.emf.edit.provider.INotifyChangedListener)
     * @since 4.3
     */
    public void addListener(final INotifyChangedListener notifyChangedListener) {
        changeNotifier.addListener(notifyChangedListener);
    }

    /** 
     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#removeListener(org.eclipse.emf.edit.provider.INotifyChangedListener)
     * @since 4.3
     */
    public void removeListener(final INotifyChangedListener notifyChangedListener) {
        changeNotifier.removeListener(notifyChangedListener);
    }

    /**
     * Add a ResourceSet to be used for resolution of a resource URI.  The
     * specified ResourceSet will be treated as read-only and will never be
     * used to load a resource for the URI being checked.
     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#addExternalResourceSet(org.eclipse.emf.ecore.resource.ResourceSet, java.util.Map)
     * @param listener
     */
    public void addExternalResourceSet(final ResourceSet resourceSet, final Map physicalToLogicalUri) {
        if (resourceSet != null && !this.externalResourceSets.contains(resourceSet)) {
            this.externalResourceSets.add(resourceSet);
            
            // If URI mappings are specified then add them to the URIConverter
            if (physicalToLogicalUri != null) {
                for (Iterator i = physicalToLogicalUri.entrySet().iterator(); i.hasNext();) {
                    final Map.Entry entry = (Map.Entry)i.next();
                    final URI physicalURI = (URI)entry.getKey();
                    final URI logicalURI  = (URI)entry.getValue();
                    getURIConverter().getURIMap().put(logicalURI, physicalURI);
                }
            }
        }
    }
    public void addExternalResourceSet(final ResourceSet resourceSet) {
        if (resourceSet != null && !this.externalResourceSets.contains(resourceSet)) {
            this.externalResourceSets.add(resourceSet);
        }
    }
    
    /**
     * Return the array of external resource sets registered with
     * this resource set. 
     * @see com.metamatrix.modeler.internal.core.resource.EResourceSet#getExternalResourceSets()
     * @return
     * @since 4.3
     */
    public ResourceSet[] getExternalResourceSets() {
        return (ResourceSet[])this.externalResourceSets.toArray(new ResourceSet[this.externalResourceSets.size()]);
    }
    
    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * Returns the IChangeNotifier associated with the EResourceSet.  
     * @return IChangeNotifier
     * @since 3.1
     */
    public IChangeNotifier getChangeNotifier() {
        return this.changeNotifier;
    }
    
    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================
    
    /** 
     * Determine whether the resource given by the URI can be loaded.  This method should
     * throw a {@link WrappedException} or a runtime exception if the resource cannot be loaded.
     * @param uri
     * @return
     * @since 4.2
     */
    protected void canCreateResource(final URI uri) {
        checkForInvalidXmiVersion(uri);
        checkForDuplicateUuid(uri);
    }
    
    /**
     * Check if the UUID corresponding to the specified resource URI 
     * already exists in this resource set.  If one does exist a 
     * DuplicateResourceException is thrown.
     * @param uri
     * @since 4.3
     */
    protected void checkForDuplicateUuid(final URI uri) {
        final XMIHeader header = doGetXMIHeader(uri);
        if ( header != null && header.getUUID() != null) {
            // There is already a file at this URI, so check the UUID ...
            final String uuidString = header.getUUID();
            try {
                final ObjectID uuid = IDGenerator.getInstance().stringToObject(uuidString);
                
                for (Iterator iter = getResources().iterator(); iter.hasNext();) {
                    Resource rsrc = (Resource)iter.next();
                    if (rsrc instanceof EResource && uuid.equals(((EResource)rsrc).getUuid())) {
                        final Object[] params = new Object[] {URI.decode(uri.toString()), URI.decode(rsrc.getURI().toString())};
                        final String msg = ModelerCore.Util.getString("EResourceSetImpl.Duplicate_resource_UUID_encountered",params); //$NON-NLS-1$
                        throw new DuplicateResourceException(rsrc,null,msg);                    
                    }
                }
            } catch (InvalidIDException e) {
                ModelerCore.Util.log(e);
            }
        }
    }
    
    /**
     * Check if the XMI version corresponding to the specified resource URI 
     * represents an older 1.x version of the XMI specification.  If it does
     * a MetaMatrixRuntimeException is thrown indicating that the resource must 
     * be converted to an xmi:version="2.0" file
     * @param uri
     * @since 4.3
     */
    protected void checkForInvalidXmiVersion(final URI uri) {
        final XMIHeader header = doGetXMIHeader(uri);
        if (header != null && header.getXmiVersion() != null && header.getXmiVersion().startsWith("1.")) { //$NON-NLS-1$
            Object[] params = new Object[]{uri};
            String msg = ModelerCore.Util.getString("EResourceSetImpl.Old_model_format_encountered",params); //$NON-NLS-1$
            throw new MetaMatrixRuntimeException(msg);                    
        }
    }
    
    /**
     * Read the XMIHeader from the underlying file specified by the 
     * resource URI.  If the URI is not a file URI or the underlying 
     * file does not exists then null is returned. 
     * @param uri
     * @return
     * @since 4.3
     */
    protected XMIHeader doGetXMIHeader( final URI uri ) {
        // First, normalize the URI to the 'physical' location ...
        URIConverter theURIConverter = getURIConverter();
        URI normalizedURI = theURIConverter.normalize(uri);
        
        //  If the file has an absolute path ...
        //  [note: test isFile first, because URI.toFileString returns NULL if not a file.]
        if (normalizedURI.isFile()) {
            // Find the corresponding file for this location ...
            File f = new File(normalizedURI.toFileString());
            
            // Return the header only if the file exists (if it doesn't exist, there's nothing to read) ...
            if (f.isFile() && f.exists()) {
                try {
                    return XMIHeaderReader.readHeader(f);
                } catch (MetaMatrixCoreException e) {
                    ModelerCore.Util.log(e);
                }
            }
        }
        return null;
    }

}
