/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Stopwatch;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.ResourceSetFinder;
import com.metamatrix.modeler.core.workspace.ModelBuffer;
import com.metamatrix.modeler.core.workspace.ModelBufferFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.Openable;
import com.metamatrix.modeler.internal.core.container.DefaultContainerResourceSetFinder;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.resource.EmfResourceSet;
import com.metamatrix.modeler.internal.core.util.OverflowingLRUCache;

/**
 * The buffer manager manages the set of open buffers.
 * It implements an LRU cache of buffers.
 */
public class ModelBufferManager implements ModelBufferFactory {

    protected static final int DEFAULT_MODEL_BUFFER_CACHE_LIMIT = 300;

    private static ModelBufferManager DEFAULT_MODEL_BUFFER_MANAGER;

    /**
     * Returns the default buffer manager.
     */
    public synchronized static ModelBufferManager getDefaultBufferManager() {
        if (DEFAULT_MODEL_BUFFER_MANAGER == null) {
            DEFAULT_MODEL_BUFFER_MANAGER = new ModelBufferManager();
        }
        return DEFAULT_MODEL_BUFFER_MANAGER;
    }

    /**
     * Cache of buffers. The key and value for an entry
     * in the table is the identical buffer.
     */
    private final OverflowingLRUCache openBuffers;

    /**
     * The finder that is used to identify which {@link ResourceSet} should be used when creating
     * a new {@link ModelBuffer}
     */
    private ResourceSetFinder resourceSetFinder;

    private final Map emfResourceToModelResource;

    /**
     * Creates a new buffer manager.
     */
    public ModelBufferManager() {
        this(DEFAULT_MODEL_BUFFER_CACHE_LIMIT);
    }

    /**
     * Creates a new buffer manager with a custom size.
     */
    protected ModelBufferManager( int cacheSize ) {
        Stopwatch stopwatch = null;
        if ( ModelerCore.DEBUG_MODEL_WORKSPACE ) {
            stopwatch = new Stopwatch();
            stopwatch.start();
        }
        this.openBuffers = new ModelBufferCache(cacheSize);
        this.resourceSetFinder = new DefaultContainerResourceSetFinder();
        if ( ModelerCore.DEBUG_MODEL_WORKSPACE ) {
            stopwatch.stop();
            ModelerCore.Util.log(IStatus.INFO,ModelerCore.Util.getString("ModelBufferManager.Time_to_create_ModelBufferManager",stopwatch)); //$NON-NLS-1$
        }
        this.emfResourceToModelResource = new HashMap();
    }

    protected void registerEmfResource( final Resource resource, final Openable openable ) {
        this.emfResourceToModelResource.put(resource,openable);
    }

    protected void unregisterEmfResource( final Resource resource ) {
        this.emfResourceToModelResource.remove(resource);
    }

    protected ModelResource getModelResource( final Resource resource ) {
        // At this point, we know the resource is in the ModelContainer, so see if the model
        // is a model resource ...
        ModelResource result = (ModelResource)this.emfResourceToModelResource.get(resource);

        if ( result == null ) {

            // See if the resource's ResourceSet is the ModelerCore.getModelContainer();
            ResourceSet resourceSet = resource.getResourceSet();
            try {
                Container container = null;
                if ( resourceSet instanceof EmfResourceSet ) {
                    container = ((EmfResourceSet)resourceSet).getContainer();
                } else if ( resource instanceof EmfResource ) {
                    container = ((EmfResource)resource).getContainer();
                }
                if ( container == null || container != ModelerCore.getModelContainer() ) {
                    // It isn't, so don't bother lookup up a ModelResource
                    return null;
                }
                resourceSet = container;
            } catch (CoreException e2) {
                // Couldn't get the ModelContainer, so just continue with the rest of the logic ...
                ModelerCore.Util.log(e2);
            }

            // Didn't find a ModelResource in the map, so the resource must have
            // been loaded because another (opened) model referenced it and somebody
            // followed the reference to the (indirectly-loaded) resource.

            // Get the URI of the resource ...
            final URI resourceURI = resource.getURI();
            String fileString = null;
            if (resourceURI.isFile()) {
                fileString = resourceURI.toFileString();
            } else {
                IResource iResource = WorkspaceResourceFinderUtil.findIResource(resourceURI);
                if (iResource != null) {
                    fileString = iResource.getLocation().toOSString();
                }
            }
            if ( fileString == null ) {
                // The fileString may be of the form "platform:/resource/<path to workspace resource>".
                // (This is the case with the XSD editor.)
                // So, try letting the Eclipse platform resolve the URL ...
                try {
                    final URL fileUrl = new URL(resourceURI.toString());
                    final URL resolvedFileUrl = FileLocator.resolve(fileUrl);
                    if ( resolvedFileUrl != null ) {
                        fileString = resolvedFileUrl.getFile();
                    }
                } catch (MalformedURLException e1) {
                    //ModelerCore.Util.log(e1);       // don't do anything and continue
                } catch (IOException e1) {
                    ModelerCore.Util.log(e1);       // just log and continue
                }
            }
            if ( fileString == null ) {
                // Try resolving the URI via the model container's URIConverter ...
                final URI normalizedUri = resourceSet.getURIConverter().normalize(resourceURI);
                if ( "file".equals(normalizedUri.scheme()) ) { //$NON-NLS-1$
                    fileString = normalizedUri.toFileString();
                }
            }
            if ( fileString == null ) {
                return null;        // no point in continuing
            }

//            // Optimization for XSDs:
//            if ( fileString.endsWith(".xsd") ) { //$NON-NLS-1$
//                // XML Schemas do not have a corresponding ModelResource
//                return null;
//            }

            final IPath path = new Path(fileString);
            try {
                if(ResourcesPlugin.getPlugin() == null) {
                    return null;
                }

                final IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
                IFile resourceFile = wsRoot.getFileForLocation(path);
                // See if the path is absolute with respect to the workspace
                if ( resourceFile == null ) {
                    resourceFile = wsRoot.getFile(path);
                }
                // If the resourceFile reference is null then a file with the
                // specified path does not exist in the set of existing projects
                if (resourceFile == null) {
                    return null;
                }
                final ModelWorkspaceManager mgr = ModelWorkspaceManager.getModelWorkspaceManager();
                final ModelResource mResource = (ModelResource)mgr.findModelWorkspaceItem(resourceFile);
                if ( mResource == null ) {
                    // The model must have been a model in the container that was outside the ModelWorkspace
                    return null;
                }
                mResource.open(null);
                result = mResource;
            } catch (Throwable e) {
                ModelerCore.Util.log(e);
            }
        }
        return result;
    }

    OverflowingLRUCache getOpenBufferCache() {
        return openBuffers;
    }

    /**
     * @see ModelBufferFactory#createBuffer(IOpenable)
     */
    public ModelBuffer createBuffer( final Openable owner) throws ModelWorkspaceException {
        ArgCheck.isInstanceOf(ModelWorkspaceItem.class,owner);
        ModelWorkspaceItem item = (ModelWorkspaceItem)owner;
        IResource resource = item.getResource();
        ResourceSet emfResourceSet = this.resourceSetFinder.getResourceSet(resource);
        final ModelBuffer buffer = new ModelBufferImpl(
                                            resource instanceof IFile ? (IFile)resource : null,
                                            owner, emfResourceSet,
                                            item.isReadOnly());
        return buffer;
    }

    /**
     * Adds a buffer to the table of open buffers.  This is generally called by the Openable implementation
     * when it opens it's buffer.
     */
    protected void addBuffer(final ModelBuffer buffer) {
        openBuffers.put(buffer.getOwner(), buffer);
    }

    /**
     * Returns the open buffer associated with the given owner,
     * or <code>null</code> if the owner does not have an open
     * buffer associated with it.
     */
    public ModelBuffer getOpenBuffer(Openable owner) {
        ArgCheck.isNotNull(owner);
        return (ModelBuffer)openBuffers.get(owner);
    }

    /**
     * Returns the default buffer factory.
     */
    public ModelBufferFactory getDefaultBufferFactory() {
        return this;
    }
    /**
     * Returns an enumeration of all open buffers.
     * <p>
     * The <code>Enumeration</code> answered is thread safe.
     *
     * @see OverflowingLRUCache
     * @return Enumeration of IBuffer
     */
    public Iterator getOpenBuffers() {
        synchronized (openBuffers) {
            return new LinkedList(openBuffers.values()).iterator();
        }
    }

    /**
     * Removes a buffer from the table of open buffers.
     */
    protected void removeBuffer(final ModelBuffer buffer) {
        openBuffers.remove(buffer.getOwner());
    }

    public ResourceSetFinder getResourceSetFinder() {
        return this.resourceSetFinder;
    }
}
