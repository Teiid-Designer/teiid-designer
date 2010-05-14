/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDImport;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.search.ModelWorkspaceSearch;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * OrganizeImportCommand
 */
public class OrganizeImportCommandFinderHelper {

    private Resource resource;
    private Map refactoredPaths;

    protected OrganizeImportCommandFinderHelper() {
        this.refactoredPaths = new HashMap();
    }

    /**
     * @param paths
     * @since 4.3
     */
    protected void setRefactoredPaths( Map paths ) {
        if (paths != null) {
            this.refactoredPaths = paths;
        } else {
            this.refactoredPaths = new HashMap();
        }
    }

    /**
     * @param resource
     * @since 4.3
     */
    protected void setResource( final Resource resource ) {
        this.resource = resource;
    }

    /**
     * Obtain the URI to an existing model, given the supplied URI and the supplied Resource.
     * 
     * @param uri
     * @param resource
     * @return the URI for an existing IResource, or null if there is no existing IResource
     */
    protected URI findModelUri( final URI uri,
                                final Resource resource ) {
        CoreArgCheck.isNotNull(uri);
        String path = URI.decode(uri.toString());
        if (resource != null) {
            return getModelUri(resource, path);
        }

        return getModelUri(uri);
    }

    /**
     * @param resource
     * @return URI
     * @since 4.3
     */
    private URI getModelUri( final Resource resource,
                             String path ) {

        String resourcePath = WorkspaceResourceFinderUtil.getWorkspaceUri(resource);
        if (resourcePath != null) {
            path = resourcePath;
        } else {
            /**
             * MyCode : 18565 Check if the same resource UUID also exists in the Model container
             */
            if (resource instanceof EmfResource) {
                try {
                    ObjectID objectID = ((EmfResource)resource).getUuid();
                    resourcePath = findResourcePathByUUID(objectID);

                    if (resourcePath != null) {
                        path = resourcePath;
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    ModelerCore.Util.log(e);
                }
            }
        }

        return URI.createURI(path);
    }

    /**
     * @param objectID
     * @return String
     * @throws CoreException
     * @since 4.3
     */
    protected String findResourcePathByUUID( ObjectID objectID ) throws CoreException {
        String path = null;
        if (objectID != null) {
            final Resource systemModels = ModelerCore.getModelContainer().getResourceFinder().findByUUID(objectID, false);
            path = WorkspaceResourceFinderUtil.getWorkspaceUri(systemModels);
        }

        return path;
    }

    /**
     * @param uri
     * @return URI
     * @since 4.3
     */
    private URI getModelUri( final URI uri ) {
        String path = null;
        IResource iResource = WorkspaceResourceFinderUtil.findIResource(uri);

        if (iResource != null) {
            path = iResource.getFullPath().toString();
        } else {

            /**
             * MyCode : 17647 Added the following code to find a valid resource even the uri path is pointing to wrong location.
             */
            iResource = findResourceInProjectByName(uri.lastSegment());
            if (iResource == null) {
                return null;
            }

            path = iResource.getFullPath().toString();
        }

        return URI.createURI(path);
    }

    /**
     * @param name
     * @return IResource
     * @since 4.3
     */
    protected IResource findResourceInProjectByName( final String name ) {

        if (name == null) {
            return null;
        }

        IResource iResource = null;
        IResource[] iResources = WorkspaceResourceFinderUtil.findIResourceByName(name);

        if (iResources.length == 0) {
            return null;
        } else if (iResources.length == 1) {
            iResource = iResources[0];
        } else {
            // Find the IResource with this name in the same IProject as the IResource being operated on
            IResource iRes = WorkspaceResourceFinderUtil.findIResource(this.resource.getURI());
            IProject project = iRes.getProject();
            for (int idx = 0; idx < iResources.length; idx++) {
                if (iResources[idx].getProject() == project) {
                    iResource = iResources[idx];
                    break;
                }
            }
            // If no IResource exists in this project then pick the first on in the array
            if (iResource == null) {
                iResource = iResources[0];
            }
        }

        return iResource;
    }

    /**
     * @param refs
     * @param problems
     * @param handler
     * @return Resource
     * @since 4.3
     */
    protected Resource findResourceWithObject( final Collection refs,
                                               final List problems,
                                               OrganizeImportHandler handler ) {
        final ModelWorkspaceSearch search = new ModelWorkspaceSearch();
        final ModelWorkspaceManager workspaceManager = ModelWorkspaceManager.getModelWorkspaceManager();

        final List modelReferences = new LinkedList();
        HashSet searchedFragments = new HashSet();
        HashSet searchedIDs = new HashSet();
        if (refs.size() != 0) {
            final Iterator iter = refs.iterator();
            while (iter.hasNext()) {
                final EObject reference = (EObject)iter.next();
                final InternalEObject internalEObject = (InternalEObject)reference;
                final URI proxyUri = internalEObject.eProxyURI();
                final String fragment = proxyUri.fragment();
                if (fragment != null) {
                    // Convert to an object ID ...
                    ObjectID id = null;
                    try {
                        id = IDGenerator.getInstance().stringToObject(fragment);
                    } catch (InvalidIDException e) {
                        // not an ID;
                    }
                    if (id == null && searchedFragments.contains(fragment)) {
                        continue;
                    } else if (searchedIDs.contains(id)) {
                        continue;
                    }
                    // Search the workspace for a model resources containing this identifier
                    IPath[] modelPaths = (id == null ? search.getResourcesContainingObjectId(fragment) : search.getResourcesContainingObjectId(id));
                    if (modelPaths != null && modelPaths.length > 0) {
                        for (int i = 0; i < modelPaths.length; ++i) {
                            final IPath resourcePath = modelPaths[i];
                            final ModelResource mResource = (ModelResource)workspaceManager.findModelWorkspaceItem(resourcePath,
                                                                                                                   IResource.FILE);
                            if (mResource != null) {
                                if (!modelReferences.contains(mResource)) {
                                    modelReferences.add(mResource);
                                }
                            }
                        }
                    }
                    if (id != null) {
                        searchedIDs.add(id);
                    } else {
                        searchedFragments.add(fragment);
                    }
                }
            }
        }

        // Try to get the EMF Resource from the ModelResource...
        Resource result = null;
        if (!modelReferences.isEmpty()) {
            ModelResource mResource = null;
            if (modelReferences.size() == 1) {
                mResource = (ModelResource)modelReferences.get(0);
            } else {
                final OrganizeImportHandler theHandler = handler;
                if (theHandler != null) {
                    final Object choice = handler.choose(modelReferences);
                    if (choice instanceof ModelResource) {
                        mResource = (ModelResource)choice;
                    } else if (choice != null) {
                        // Not null, and not the expected type ...
                        final Object[] params = new Object[] {choice.getClass().getName(), ModelResource.class.getName()};
                        final String msg = ModelerCore.Util.getString("OrganizeImportCommand.Unexpected_choice", params); //$NON-NLS-1$
                        throw new AssertionError(msg);
                    }
                }
            }
            if (mResource != null) {
                try {
                    result = mResource.getEmfResource();
                } catch (ModelWorkspaceException e1) {
                    final int code = OrganizeImportCommandHelper.ERROR_GETTING_RESOURCE;
                    final Object[] params = new Object[] {mResource};
                    final String msg = ModelerCore.Util.getString("OrganizeImportCommand.error_getting_resource", params); //$NON-NLS-1$
                    problems.add(new Status(IStatus.ERROR, OrganizeImportCommandHelper.PLUGINID, code, msg, null));
                }
            }
        }
        return result;
    }

    /**
     * @param uri
     * @param monitor
     * @param problems
     * @return Resource
     * @since 4.3
     */
    protected Resource findRefactoredResource( final Resource eResource,
                                               final URI externalResourceURI,
                                               final IProgressMonitor monitor,
                                               final List problems ) {

        ResourceSet eResourceSet = eResource.getResourceSet();
        Resource externalResource = eResourceSet.getResource(externalResourceURI, false);
        if (externalResource == null) {
            Container cntr = ModelerCore.getContainer(eResource);
            if (cntr != null) {
                // Search for the resource by name
                Resource[] eResources = cntr.getResourceFinder().findByName(externalResourceURI.lastSegment(), false, true);
                if (eResources.length == 0) {
                    return null;
                } else if (eResources.length == 1) {
                    return eResources[0];
                } else {
                    // Find best match to the resource containing the external reference
                    List matches = new ArrayList(eResources.length);
                    String parentUriString = eResource.getURI().trimSegments(1).toString();
                    for (int i = 0; i != eResources.length; ++i) {
                        Resource r = eResources[i];
                        String uriString = r.getURI().trimSegments(1).toString();
                        if (parentUriString.startsWith(uriString) || uriString.startsWith(parentUriString)) {
                            matches.add(r);
                        }
                    }

                    if (matches.size() == 0) {
                        externalResource = eResources[0];
                    } else if (matches.size() == 1) {
                        externalResource = (Resource)matches.get(0);
                    } else {
                        externalResource = eResources[0];
                    }
                }
            }
        }
        return externalResource;

        // /**
        // Try to reconstruct the new resource path by matching the path
        // to that of the runtime workspace location. This logic will not
        // work if the actual location of the resource is in a project
        // outside of the workspace folder.
        // */
        // IPath tmpResPath = new Path (uri.path());
        // if (!uri.isRelative()) {
        // IPath workspacePath = ModelerCore.getWorkspace().getRoot().getLocation();
        // int matchingSegments = tmpResPath.matchingFirstSegments(workspacePath);
        // if( matchingSegments != 0 ) {
        // tmpResPath = tmpResPath.removeFirstSegments(matchingSegments).makeAbsolute();
        // }else {
        // // MyDefect : 17647 Added to find path from refactored resource map.
        // tmpResPath = findFromRefactoredPaths(tmpResPath.toString());
        // }
        // }
        //        
        // final IPath resourcePath = getNewResourcePath(tmpResPath.toString());
        // ModelResource mResource = null;
        // Resource result = null;
        // if (resourcePath != null) {
        // try {
        // IFile tmpFile = (IFile) WorkspaceResourceFinderUtil.findIResourceByPath(resourcePath);
        // if(tmpFile == null && resourcePath != null) {
        // tmpFile = (IFile)findResourceInProjectByName(resourcePath.lastSegment());
        // }
        //                
        // mResource = ModelerCore.getModelEditor().findModelResource(tmpFile);
        // if (mResource == null) {
        // mResource = ModelerCore.create(tmpFile);
        // mResource.save(monitor, true);
        // }
        //                
        // if (mResource != null) {
        // result = mResource.getEmfResource();
        // }
        // } catch (ModelWorkspaceException e1) {
        // final int code = OrganizeImportCommandHelper.ERROR_GETTING_RESOURCE;
        // final Object[] params = new Object[]{resourcePath};
        //                final String msg = ModelerCore.Util.getString("OrganizeImportCommand.Error_while_finding_resource_for_path_1",params); //$NON-NLS-1$
        // problems.add( new Status(IStatus.ERROR,OrganizeImportCommandHelper.PLUGINID,code,msg,null) );
        // }
        // }
        // return result;
    }

    // /**
    // *
    // * @param oldPath
    // * @return IPath
    // * @since 4.3
    // */
    // private IPath getNewResourcePath(String oldPath) {
    // if (this.refactoredPaths != null) {
    // String newPath = (String)refactoredPaths.get(oldPath);
    // if (newPath != null) {
    // return new Path(newPath);
    // }
    // }
    //        
    // if((this.refactoredPaths == null || this.refactoredPaths.isEmpty()) && oldPath != null) {
    // return new Path(oldPath);
    // }
    //        
    // return null;
    // }

    /**
     * @param uriPath
     * @return IPath
     * @since 4.3
     */
    protected IPath findFromRefactoredPaths( String uriPath ) {

        if (this.refactoredPaths != null) {
            Set keys = refactoredPaths.keySet();
            for (Iterator iter = keys.iterator(); iter.hasNext();) {
                String oldPath = iter.next().toString();
                if (uriPath.endsWith(oldPath)) {
                    return new Path(oldPath);
                }
            }
        }

        return new Path(uriPath);
    }

    /**
     * @param eobject
     * @param importsAspect
     * @return IPath
     * @since 4.3
     */
    protected IPath findPath( EObject eobject,
                              ImportsAspect importsAspect ) {
        IPath importPath = importsAspect.getModelPath(eobject);
        if (importPath == null) {
            return createPath(eobject);
        }

        return importPath;
    }

    /**
     * @param eobject
     * @return IPath
     * @since 4.3
     */
    private IPath createPath( EObject eobject ) {
        if (eobject instanceof XSDImport) {
            String schemaLocation = ((XSDImport)eobject).getSchemaLocation();
            return getNewPathInfo(schemaLocation);
        }

        return null;
    }

    /**
     * @param schemaLocation
     * @return IPath
     * @since 4.3
     */
    private IPath getNewPathInfo( String schemaLocation ) {

        if (schemaLocation == null) return null;

        IPath currentPath = new Path(schemaLocation);
        Iterator iter = refactoredPaths.keySet().iterator();
        String oldPath;

        while (iter.hasNext()) {
            oldPath = iter.next().toString();
            IPath oldPathRef = new Path(oldPath);
            if (oldPathRef.lastSegment().equals(currentPath.lastSegment())) {
                String newPath = refactoredPaths.get(oldPath).toString();
                return new Path(newPath);
            }
        }

        return null;
    }

}
