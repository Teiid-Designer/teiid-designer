/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.manifest.ModelReference;

/**
 * WsVdbInputResourceFinder
 */
public class WsVdbInputResourceFinder implements VdbInputResourceFinder {

    /** Length of a UUID string with protocol (e.g. "mmuuid:0b5fb081-1275-1eec-8518-c32201e76066") */
    private static final int UUID_STRING_LENGTH = 43;

    private Container cntr;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of WsVdbInputResourceFinder.
     */
    public WsVdbInputResourceFinder() {
        super();
        try {
            this.cntr = ModelerCore.getModelContainer();
        } catch (CoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.vdb.internal.edit.VdbInputResourceFinder#getEmfResource(org.eclipse.core.runtime.IPath)
     */
    public Resource getEmfResource( final IPath modelPath ) throws VdbEditException {
        ArgCheck.isNotNull(modelPath);
        assertEclipseRuntime();

        Resource eResource = null;
        IResource iResource = ResourcesPlugin.getWorkspace().getRoot().getFile(modelPath);
        if (iResource == null) {
            iResource = WorkspaceResourceFinderUtil.findIResource(modelPath.toString());
        }
        if (iResource != null) {
            eResource = getEmfResource(iResource);
        }

        final URI modelUri = URI.createURI(modelPath.toString());
        if (eResource == null) {
            eResource = this.cntr.getResource(modelUri, false);
        }

        if (eResource == null) {
            final String modelUriString = URI.decode(modelUri.toString());
            for (Iterator iter = this.cntr.getResources().iterator(); iter.hasNext();) {
                final Resource rsrc = (Resource)iter.next();
                if (URI.decode(rsrc.getURI().toString()).endsWith(modelUriString)) {
                    eResource = rsrc;
                    break;
                }
            }
        }

        return eResource;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.VdbInputResourceFinder#getEmfResource(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 4.3
     */
    public Resource getEmfResource( final ModelReference modelRef ) throws VdbEditException {
        ArgCheck.isNotNull(modelRef);
        assertEclipseRuntime();

        Resource eResource = null;

        // Look in the model container for a resource with this UUID
        final String stringifiedUuid = modelRef.getUuid();
        if (!StringUtil.isEmpty(stringifiedUuid) && stringifiedUuid.startsWith(UUID.PROTOCOL)) {
            ObjectID uuid = stringToObjectID(stringifiedUuid);
            if (uuid != null) {
                eResource = this.cntr.getResourceFinder().findByUUID(uuid, false);
            }
        }

        // Model location as stored within the ModelReference object is expected to be of the
        // form "/project/.../model.xmi" when working with VDBs in the Modeler's workspace
        final String location = modelRef.getModelLocation();
        if (eResource == null && !StringUtil.isEmpty(location)) {
            final IPath modelPath = new Path(location).makeAbsolute();
            eResource = getEmfResource(modelPath);

            if (eResource == null) {
                final String name = modelPath.lastSegment();
                IResource[] iResources = WorkspaceResourceFinderUtil.findIResourceByName(name);
                IResource iResource = null;
                if (iResources.length == 1) {
                    iResource = iResources[0];
                } else if (iResources.length > 1) {
                    final String projectName = modelPath.segment(0);
                    for (int i = 0; i != iResources.length; ++i) {
                        if (projectName.equals(iResources[i].getFullPath().segment(0))) {
                            iResource = iResources[i];
                            break;
                        }
                    }
                    if (iResource == null) {
                        iResource = iResources[0];
                    }
                }
                if (iResource != null) {
                    eResource = getEmfResource(iResource);
                }
            }
        }

        return eResource;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.VdbInputResourceFinder#getEmfResourceStream(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.3
     */
    public InputStream getEmfResourceStream( final Resource emfResource ) throws VdbEditException {
        ArgCheck.isNotNull(emfResource);
        assertEclipseRuntime();

        if (emfResource.getURI().isFile()) {
            final File f = new File(emfResource.getURI().toFileString());
            if (f.exists()) {
                try {
                    return new FileInputStream(f);
                } catch (FileNotFoundException e) {
                    throw new VdbEditException(e);
                }
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.VdbInputResourceFinder#getEmfResourcePath(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public IPath getEmfResourcePath( final Resource emfResource ) throws VdbEditException {
        if (emfResource != null && emfResource.getURI() != null) {
            assertEclipseRuntime();

            // If the resource is the Teiid Designer built-in datatypes model
            // then return the specific logical URI for that model
            final URI resourceUri = emfResource.getURI();
            String resourceUriString = (resourceUri.isFile() ? resourceUri.toFileString() : URI.decode(resourceUri.toString()));
            // Convert the URI string to use a path separator consistent with those find in IResource paths
            resourceUriString = new Path(resourceUriString).toString();

            // Check if this is a global resource but checking the URI string
            // against the names of the known global resources:
            // "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
            // "http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"
            // "http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance"
            // "http://www.w3.org/2001/XMLSchema"
            // "http://www.w3.org/2001/MagicXMLSchema"
            // "http://www.w3.org/2001/XMLSchema-instance"
            if (WorkspaceResourceFinderUtil.isGlobalResource(resourceUriString)) {
                final Object[] params = new Object[] {resourceUriString};
                final String msg = VdbEditPlugin.Util.getString("VdbInputResourceFinder.unable_to_return_an_path_for_the_global_resource", params); //$NON-NLS-1$
                throw new VdbEditException(msg);
            }

            // If the corresponding IResource can be found then return the relative path within the workspace
            FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor();
            IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
            IProject[] projects = wsRoot.getProjects();
            for (int i = 0; i < projects.length; i++) {
                try {
                    projects[i].accept(visitor);
                } catch (CoreException e) {
                    // do nothing
                }
            }

            // Match the Emf resource location against the location of each IFile instance
            final IFile[] fileResources = visitor.getFileResources();
            for (int i = 0; i < fileResources.length; i++) {
                IFile fileResource = fileResources[i];
                String resourceLocation = fileResource.getLocation().toString();
                if (resourceUriString.endsWith(resourceLocation)) {
                    return fileResource.getFullPath();
                }
            }
        }
        return null;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    protected void assertEclipseRuntime() throws VdbEditException {
        final IWorkspace iworkspace = ResourcesPlugin.getWorkspace();
        if (iworkspace == null) {
            // Not running in Eclipse ...
            final String msg = VdbEditPlugin.Util.getString("VdbInputResourceFinder.Not_running_in_Eclipse_workspace;_use_a_different_ResourceFinder"); //$NON-NLS-1$
            throw new VdbEditException(msg);
        }
    }

    protected ObjectID stringToObjectID( final String uuidString ) {
        if (uuidString == null || uuidString.length() < UUID_STRING_LENGTH) {
            return null;
        }

        ObjectID uuid = null;
        try {
            uuid = IDGenerator.getInstance().stringToObject(uuidString, UUID.PROTOCOL);
        } catch (InvalidIDException e) {
            VdbEditPlugin.Util.log(IStatus.ERROR, e.getMessage());
        }
        return uuid;
    }

    protected Resource getEmfResource( final IResource iResource ) throws VdbEditException {
        Resource eResource = null;
        if (iResource != null) {
            final ModelWorkspace workspace = ModelerCore.getModelWorkspace();
            final ModelResource mResource = workspace.findModelResource(iResource);
            if (mResource != null) {
                try {
                    eResource = mResource.getEmfResource();
                    Assertion.isNotNull(eResource);
                } catch (ModelWorkspaceException e) {
                    throw new VdbEditException(e);
                }
            } else {
                URI location = null;
                if (iResource.getLocation() != null && iResource.getLocation().toFile().exists()) {
                    location = URI.createFileURI(iResource.getLocation().toOSString());
                } else if (iResource.getFullPath().toFile().exists()) {
                    location = URI.createFileURI(iResource.getFullPath().toOSString());
                }
                if (location != null) {
                    try {
                        eResource = this.cntr.getOrCreateResource(location);
                    } catch (ModelerCoreException e) {
                        ModelerCore.Util.log(e);
                    }
                }
            }
        }
        return eResource;
    }

    // ==================================================================================
    // I N N E R C L A S S
    // ==================================================================================

    private static class FileResourceCollectorVisitor implements IResourceVisitor {
        private List resources;

        public FileResourceCollectorVisitor() {
            this.resources = new ArrayList();
        }

        public boolean visit( IResource resource ) {
            if (resource.exists() && resource instanceof IFile) {
                resources.add(resource);
            }
            return true;
        }

        public IFile[] getFileResources() {
            return (IFile[])resources.toArray(new IFile[resources.size()]);
        }
    }

}
