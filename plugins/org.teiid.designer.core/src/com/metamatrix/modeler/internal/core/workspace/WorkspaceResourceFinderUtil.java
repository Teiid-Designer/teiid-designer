/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.id.UUID;
import org.teiid.core.TeiidException;
import org.teiid.designer.core.xmi.ModelImportInfo;
import org.teiid.designer.core.xmi.XMIHeader;
import org.teiid.designer.core.xmi.XMIHeaderReader;
import com.metamatrix.common.vdb.VdbModelInfo;
import com.metamatrix.common.xsd.XsdHeader;
import com.metamatrix.common.xsd.XsdHeaderReader;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.workspace.ResourceFilter;

/**
 * WorkspaceResourceFinder
 */
public class WorkspaceResourceFinderUtil {

    protected static final String URI_REFERENCE_DELIMITER = DatatypeConstants.URI_REFERENCE_DELIMITER;
    protected static final IResource[] EMPTY_IRESOURCE_ARRAY = new IResource[0];
    protected static final IPath[] EMPTY_IPATH_ARRAY = new IPath[0];

    public static final ResourceFilter VDB_RESOURCE_FILTER = new VdbResourceFilter();

    // org.eclipse.emf.common.util.URI constants
    private static final String SCHEME_PLATFORM = "platform:"; //$NON-NLS-1$
    private static final String SCHEME_FILE = "file:"; //$NON-NLS-1$
    private static final String PLATFORM_RESOURCE_SEGMENT = "/resource"; //$NON-NLS-1$
    private static final char DEVICE_IDENTIFIER = ':';
    private static final String AUTHORITY_SEPARATOR = "//"; //$NON-NLS-1$
    private static final char SEGMENT_SEPARATOR = '/';

    protected static final String XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX = ModelerCore.XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX;
    protected static final String XML_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX = "XMLSchema.xsd"; //$NON-NLS-1$
    protected static final String XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX = "MagicXMLSchema.xsd"; //$NON-NLS-1$
    protected static final String XML_SCHEMA_INSTANCE_ECLIPSE_PLATFORM_URI_SUFFIX = "XMLSchema-instance.xsd"; //$NON-NLS-1$

    /** Defines the expected name of the primitive types model file */
    public static final String UML_PRIMITIVE_TYPES_MODEL_FILE_NAME = "primitiveTypes.xmi"; //$NON-NLS-1$

    /** Defines the expected primitive types internal URI */
    protected static final String UML_PRIMITIVE_TYPES_INTERNAL_URI = "http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"; //$NON-NLS-1$

    /** Defines the expected UML2 metamodel URI */
    protected static final String UML_METAMODEL_URI = "pathmap://UML2_METAMODELS/UML2.metamodel.uml2"; //$NON-NLS-1$

    /** Defines the expected name of the relationship types model file */
    public static final String RELATIONSHIP_PRIMITIVE_TYPES_MODEL_FILE_NAME = "builtInRelationshipTypes.xmi"; //$NON-NLS-1$

    /** Defines the expected relationship types internal URI */
    protected static final String RELATIONSHIP_PRIMITIVE_TYPES_INTERNAL_URI = "http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance"; //$NON-NLS-1$

    /**
     * Return the IResource instance corresponding to the specified EMF resource. If the resource is one of the well-known Teiid
     * Designer/EMF global resources such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * then null is returned since there is no IResource in the workspace that represents any one of those models.
     * 
     * @param workspaceUri the URI string
     * @return the IResource identified by the URI if it exists; may return null
     */
    public static IResource findIResource( final Resource resource ) {

        try {
            if (resource != null && resource.getURI() != null && getWorkspace() != null) return findIResource(resource.getURI());
        } catch (final IllegalStateException ise) {
            // do nothing
        }

        return null;
    }

    /**
     * Return the IResource instance corresponding to the specified URI string. The URI represents a relative path within the
     * workspace to particular file resource. If the URI is one of the well-known Teiid Designer/EMF identifiers to a global
     * resource such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * then null is returned since there is no IResource in the workspace that represents any one of those models.
     * 
     * @param workspaceUri the URI string
     * @return the IResource identified by the URI if it exists; may return null
     */
    public static IResource findIResource( final String workspaceUri ) {

        if (!isValidWorkspaceUri(workspaceUri)) return null;
        final String normalizedUriString = normalizeUriString(workspaceUri);

        // MyDefect : 16368 Refactored methods.

        IFile fileResource;
        final IFile[] fileResources = getAllProjectsFileResources();

        // If the workspace URI starts with "http" then check it against the target
        // namespaces of any XML schema in the workspace ...
        fileResource = getResourceStartsWithHttp(fileResources, normalizedUriString);
        if (fileResource != null) return fileResource;

        // Try to convert the workspace URI to a relative path and then match
        // this path in workspace to one of the IResource paths instances
        fileResource = getResourceStartsWithPathSeparator(fileResources, normalizedUriString);
        if (fileResource != null) return fileResource;

        // Try to match the workspace URI to a IResource location ...
        fileResource = getResourceByLocation(fileResources, normalizedUriString);
        if (fileResource != null) return fileResource;

        return null;
    }

    /**
     * Return the IResource instance corresponding to the specified URI string. The URI represents a relative path within the
     * workspace to particular file resource. If the URI is one of the well-known Teiid Designer/EMF identifiers to a global
     * resource such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * then null is returned since there is no IResource in the workspace that represents any one of those models.
     * 
     * @param resourceUri the URI
     * @return the IResource identified by the URI if it exists; may return null
     */
    public static IResource findIResource( final URI resourceUri ) {

        try {
            if (resourceUri != null && getWorkspace() != null) {
                // Match the Emf resource location against the location
                final String uriString = normalizeUriToString(resourceUri);
                final IFile[] fileResources = getAllProjectsFileResources();

                final IFile fileResource = getResourceByLocation(fileResources, uriString);
                if (fileResource != null) return fileResource;
            }
        } catch (final IllegalStateException ise) {
            // do nothing
            // MyDefect : getWorkspace() now throws this IllegalStateException exception.
            ModelerCore.Util.log(ise);
        }

        return null;

    }

    /**
     * Return the array of IResource instances that match the specified name. The name must consist of only one path segment and may
     * or may not have a file extension. If no IResource instances are found that match this name an empty array is returned.
     * 
     * @param name the name of the IResource
     * @return the IResource[]
     */
    public static IResource[] findIResourceByName( final String name ) {
        if (name == null || name.length() == 0 || getWorkspace() == null) return EMPTY_IRESOURCE_ARRAY;

        // Collect all IResources within all IProjects
        final FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor();
        if (getWorkspace() != null && getWorkspace().getRoot() != null) {
            final IProject[] projects = getWorkspace().getRoot().getProjects();
            for (final IProject project : projects)
                try {
                    project.accept(visitor);
                } catch (final CoreException e) {
                    // do nothing
                }
        }

        // Try to match the specified resource name with one of the IResource instances
        final boolean removeExtension = (name.indexOf('.') == -1);
        final IFile[] fileResources = visitor.getFileResources();
        final ArrayList tmp = new ArrayList();
        for (final IFile fileResource : fileResources)
            if (fileResource != null) {
                IPath path = fileResource.getFullPath();
                // Do not process file names staring with '.' since these
                // are considered reserved for Eclipse specific files
                if (path.lastSegment().charAt(0) == '.') continue;
                if (removeExtension) path = path.removeFileExtension();
                if (name.equalsIgnoreCase(path.lastSegment())) tmp.add(fileResource);
            }

        // If no matching resources are found return an empty array
        if (tmp.size() == 0) return EMPTY_IRESOURCE_ARRAY;

        final IResource[] result = new IResource[tmp.size()];
        tmp.toArray(result);

        return result;
    }

    /**
     * Return the IResource instance that matches the specified path. The path is the relative path in the workspace. If no
     * IResource instance is found that match this path a null is returned.
     * 
     * @param name the path to the IResource
     * @return the IResource
     */
    public static IResource findIResourceByPath( final IPath workspacePath ) {
        if (workspacePath == null || workspacePath.isEmpty() || getWorkspace() == null) return null;

        // Collect all IResources within all IProjects
        final FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor();
        if (getWorkspace() != null && getWorkspace().getRoot() != null) {
            final IProject[] projects = getWorkspace().getRoot().getProjects();
            for (final IProject project : projects)
                try {
                    project.accept(visitor);
                } catch (final CoreException e) {
                    // do nothing
                }
        }

        final IFile[] fileResources = visitor.getFileResources();
        for (final IFile fileResource : fileResources)
            if (fileResource != null) {
                final IPath path = fileResource.getFullPath();
                // Do not process file names staring with '.' since these
                // are considered reserved for Eclipse specific files
                if (path.lastSegment().charAt(0) == '.') continue;
                if (path.equals(workspacePath)) return fileResource;
            }

        return null;
    }

    /**
     * Return the IResource instance that matches the stringified UUID. If the stringified UUID is null, empty, or does not have a
     * UUID.PROTOCOL prefix then null is returned. If no IResource instance is found that matches this UUID then null is returned.
     * 
     * @param stringifiedUuid the stringified form of a UUID instance
     * @return the IResource
     */
    public static IResource findIResourceByUUID( final String stringifiedUuid ) {
        if (CoreStringUtil.isEmpty(stringifiedUuid) || !stringifiedUuid.startsWith(UUID.PROTOCOL) || getWorkspace() == null) return null;

        // Collect all IResources within all IProjects
        final FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor();
        if (getWorkspace() != null && getWorkspace().getRoot() != null) {
            final IProject[] projects = getWorkspace().getRoot().getProjects();
            for (final IProject project : projects)
                try {
                    project.accept(visitor);
                } catch (final CoreException e) {
                    // do nothing
                }
        }

        final IFile[] fileResources = visitor.getFileResources();
        for (final IFile fileResource : fileResources) {
            final IFile iResource = fileResource;
            if (iResource != null && !ModelUtil.isXsdFile(iResource)) {
                final XMIHeader header = ModelUtil.getXmiHeader(iResource);
                if (header != null && stringifiedUuid.equals(header.getUUID())) return iResource;
            }
        }

        return null;
    }

    public static String getAbsoluteLocation( final File base,
                                               final String relativePath ) {
        final URI baseLocation = URI.createFileURI(base.getAbsolutePath());
        URI relLocation = URI.createURI(relativePath, false);
        if (baseLocation.isHierarchical() && !baseLocation.isRelative() && relLocation.isRelative()) relLocation = relLocation.resolve(baseLocation);
        return URI.decode(relLocation.toString());
    }

    private static IFile[] getAllProjectsFileResources() {
        // Collect all IResources within all IProjects

        final FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor();
        final IWorkspace workSpace = getWorkspace();
        if (workSpace != null && workSpace.getRoot() != null) {
            final IWorkspaceRoot wsRoot = workSpace.getRoot();
            final IProject[] projects = wsRoot.getProjects();
            for (final IProject project : projects)
                if (project.isOpen()) try {
                    project.accept(visitor);
                } catch (final CoreException e) {
                    // do nothing
                    ModelerCore.Util.log(e);
                }
        }

        return visitor.getFileResources();
    }

    public static Collection getAllWorkspaceResources() {
        return getAllWorkspaceResources(null);
    }

    public static Collection getAllWorkspaceResources( final ResourceFilter filter ) {
        // Collect all IResources within all IProjects
        final FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor(filter);
        final IProject[] projects = ModelerCore.getWorkspace().getRoot().getProjects();
        for (final IProject project : projects)
            try {
                project.accept(visitor);
            } catch (final CoreException e) {
                // do nothing
            }

        final Collection fileResources = visitor.getFileResourcesCollection();
        final Iterator itor = fileResources.iterator();
        while (itor.hasNext()) {
            final IFile fileResource = (IFile)itor.next();
            final IPath path = fileResource.getFullPath();
            // Do not process file names starting with '.' since these
            // are considered reserved for Eclipse specific files
            if (path.lastSegment().charAt(0) == '.') itor.remove();
        } // endwhile

        return fileResources;
    }

    /**
     * Return IPath[] array representing the workspace paths to dependent IResource instances. The dependent paths are found by
     * reading the model imports declarations in the specified resource. Only paths to IResource instances will be returned whereas
     * import declarations to one of the well-know Teiid Designer/EMF global resources such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * will not be returned in the result. If the method is called outside of the Eclipse runtime environment, or if the specified
     * IResource is null or cannot be found on the file system then an empty array will be returned.
     * 
     * @param iResource the IResource to examine for import declarations. If null, or it not running in a Eclipse runtime
     *        environment, an empty array will be returned.
     * @return the IPath[] for paths to the dependent IResource instances
     */
    public static IPath[] getDependentResourcePaths( final IResource iResource ) {
        if (iResource == null || getWorkspace() == null) return EMPTY_IPATH_ARRAY;

        final IResource[] dependentIResources = getDependentResources(iResource);
        if (dependentIResources == null || dependentIResources.length == 0) return EMPTY_IPATH_ARRAY;

        final IPath[] result = new IPath[dependentIResources.length];
        for (int i = 0; i != dependentIResources.length; ++i) {
            final IResource dependentIResource = dependentIResources[i];
            result[i] = dependentIResource.getFullPath();
        }

        return result;
    }

    /**
     * Return IResource[] array representing the dependent IResource instances. The dependent resources are found by reading the
     * model imports declarations in the specified resource. Only references to IResource instances that can be found in the
     * workspace will be returned. If an import declaration to one of the well-know Teiid Designer/EMF global resources such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * is encountered, a corresponding IResource does not exist in the workspace and, therefore, cannot be returned as part of the
     * result. If the method is called outside of the Eclipse runtime environment, or if the specified IResource is null or cannot
     * be found on the file system then an empty array will be returned.
     * 
     * @param iResource the IResource to examine for import declarations. If null, or it not running in a Eclipse runtime
     *        environment, an empty array will be returned.
     * @return the IResource[] references of dependent resources
     */
    public static IResource[] getDependentResources( final IResource iResource ) {
        if (iResource == null || getWorkspace() == null) return EMPTY_IRESOURCE_ARRAY;

        final File iResourceFile = iResource.getRawLocation().toFile();
        if (!iResourceFile.exists()) return EMPTY_IRESOURCE_ARRAY;

        final Collection result = new ArrayList();
        try {

            // Get the header information from the XSD file
            if (ModelUtil.isXsdFile(iResource)) {
                final XsdHeader header = XsdHeaderReader.readHeader(iResourceFile);
                if (header != null) {

                    // Add all the imported schema locations
                    String[] locations = header.getImportSchemaLocations();
                    for (int i = 0; i != locations.length; ++i) {
                        final String location = locations[i];

                        IResource dependentIResource = findIResource(location);
                        if (dependentIResource == null) {
                            final String absolutePath = getAbsoluteLocation(iResourceFile, location);
                            dependentIResource = findIResource(absolutePath);
                        }
                        if (dependentIResource != null && !result.contains(dependentIResource)) result.add(dependentIResource);
                    }

                    // Add all the included schema locations
                    locations = header.getIncludeSchemaLocations();
                    for (int i = 0; i != locations.length; ++i) {
                        final String location = locations[i];
                        IResource dependentIResource = findIResource(location);
                        if (dependentIResource == null) {
                            final String absolutePath = getAbsoluteLocation(iResourceFile, location);
                            dependentIResource = findIResource(absolutePath);
                        }
                        if (dependentIResource != null && !result.contains(dependentIResource)) result.add(dependentIResource);
                    }
                }

                // Get the header information from the XMI file
            } else if (ModelUtil.isModelFile(iResource)) {
                final XMIHeader header = XMIHeaderReader.readHeader(iResourceFile);
                if (header != null) {

                    final ModelImportInfo[] infos = header.getModelImportInfos();
                    for (final ModelImportInfo info : infos) {
                        IResource dependentIResource = null;

                        final String location = info.getLocation();
                        final String path = info.getPath();
                        if (!CoreStringUtil.isEmpty(path)) dependentIResource = findIResource(path);
                        else if (!CoreStringUtil.isEmpty(location)) {
                            final String depPath = iResource.getFullPath().removeLastSegments(1).append(location).toString();
                            if (!isGlobalResource(depPath)) {
                                dependentIResource = findIResource(depPath);
                                if (dependentIResource == null) {
                                    final String absolutePath = getAbsoluteLocation(iResourceFile, location);
                                    dependentIResource = findIResource(absolutePath);
                                }
                            }
                        }
                        if (dependentIResource != null && !result.contains(dependentIResource)) result.add(dependentIResource);

                    }
                }

                // Get the header information from the VDB archive file
            } else if (ModelUtil.isVdbArchiveFile(iResource)) {
                final XMIHeader header = ModelUtil.getXmiHeader(iResourceFile);
                if (header != null) {

                    final ModelImportInfo[] infos = header.getModelImportInfos();
                    for (final ModelImportInfo info : infos) {
                        IResource dependentIResource = null;

                        final String location = info.getLocation();
                        final String path = info.getPath();
                        if (!CoreStringUtil.isEmpty(path)) dependentIResource = findIResource(path);
                        else if (!CoreStringUtil.isEmpty(location)) if (!isGlobalResource(location)) {
                            dependentIResource = findIResource(location);
                            if (dependentIResource == null) {
                                final String absolutePath = getAbsoluteLocation(iResourceFile, location);
                                dependentIResource = findIResource(absolutePath);
                            }
                        }
                        if (dependentIResource != null && !result.contains(dependentIResource)) result.add(dependentIResource);
                    }
                }
            }

        } catch (final Exception err) {
            final Object[] params = new Object[] {iResource.getFullPath()};
            final String msg = ModelerCore.Util.getString("WorkspaceResourceFinderUtil.Error_getting_model_imports_from_resource", params); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.ERROR, err, msg);
        }

        return (IResource[])result.toArray(new IResource[result.size()]);
    }

    /**
     * Returns true if the specified URI string is one of the well-known Teiid Designer/EMF identifiers to a global resource such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
     * "http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"
     * "http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * otherwise false is returned.
     * 
     * @param uri the URI string
     */
    public static String getGlobalResourceUri( final String uri ) {
        if (uri == null) return null;

        // If the URI is to the Teiid Designer built-in datatypes model ...
        if (uri.startsWith(DatatypeConstants.BUILTIN_DATATYPES_URI)) return DatatypeConstants.BUILTIN_DATATYPES_URI;
        if (uri.endsWith(DatatypeConstants.DATATYPES_MODEL_FILE_NAME)) return DatatypeConstants.BUILTIN_DATATYPES_URI;

        // If the URI is to the Teiid Designer built-in UML primitive types model ...
        if (uri.startsWith(UML_PRIMITIVE_TYPES_INTERNAL_URI)) return UML_PRIMITIVE_TYPES_INTERNAL_URI;
        if (uri.endsWith(UML_PRIMITIVE_TYPES_MODEL_FILE_NAME)) return UML_PRIMITIVE_TYPES_INTERNAL_URI;
        if (uri.startsWith(UML_METAMODEL_URI)) return UML_METAMODEL_URI;

        // If the URI is to the Teiid Designer built-in relationship model ...
        if (uri.endsWith(RELATIONSHIP_PRIMITIVE_TYPES_INTERNAL_URI)) return RELATIONSHIP_PRIMITIVE_TYPES_INTERNAL_URI;
        if (uri.endsWith(RELATIONSHIP_PRIMITIVE_TYPES_MODEL_FILE_NAME)) return RELATIONSHIP_PRIMITIVE_TYPES_INTERNAL_URI;

        // If the URI is to one of the Emf XMLSchema resources ...
        if (uri.startsWith(ModelerCore.XML_SCHEMA_INSTANCE_GENERAL_URI)) return ModelerCore.XML_SCHEMA_INSTANCE_GENERAL_URI;
        if (uri.startsWith(ModelerCore.XML_SCHEMA_GENERAL_URI)) return ModelerCore.XML_SCHEMA_GENERAL_URI;
        if (uri.startsWith(ModelerCore.XML_MAGIC_SCHEMA_GENERAL_URI)) return ModelerCore.XML_MAGIC_SCHEMA_GENERAL_URI;
        if (uri.equals(ModelerCore.XML_XSD_GENERAL_URI)) return ModelerCore.XML_XSD_GENERAL_URI;

        // If the URI is in the form of an Eclipse platform path to
        // one of the Emf XMLSchema resources ...
        if (uri.startsWith(XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX)) // MagicXMLSchema.xsd suffix on the resource URI
        if (uri.indexOf(XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX) > 0) return ModelerCore.XML_MAGIC_SCHEMA_GENERAL_URI;
        else if (uri.indexOf(XML_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX) > 0) return ModelerCore.XML_SCHEMA_GENERAL_URI;
        else if (uri.indexOf(XML_SCHEMA_INSTANCE_ECLIPSE_PLATFORM_URI_SUFFIX) > 0) return ModelerCore.XML_SCHEMA_INSTANCE_GENERAL_URI;

        // Next check if the import reference is to one of our system models
        try {
            final Resource[] systemModels = ModelerCore.getSystemVdbResources();
            for (int i = 0; i != systemModels.length; ++i) {
                final String systemModelUri = URI.decode(systemModels[i].getURI().toString());
                if (uri.equalsIgnoreCase(systemModelUri)) return systemModelUri;
            }
        } catch (final Exception e) {
            // do nothing
        }

        // Next check if the import reference is to one of our metamodels
        try {
            if (ModelerCore.getMetamodelRegistry().containsURI(uri)) {
                final URI metamodelUri = ModelerCore.getMetamodelRegistry().getURI(uri);
                return ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(metamodelUri).getNamespaceURI();
            }
        } catch (final Exception e) {
            // do nothing
        }

        return null;
    }

    private static IFile getResourceByLocation( final IFile[] fileResources,
                                                final String workspaceUri ) {

        IFile fileResource = null;
        String resourceLocation;

        // Try to match the workspace URI to a IResource location ...
        for (final IFile fileResource2 : fileResources) {
            fileResource = fileResource2;
            resourceLocation = fileResource.getLocation().toOSString();
            if (workspaceUri.endsWith(resourceLocation)) return fileResource;
            resourceLocation = fileResource.getLocation().toString();
            if (workspaceUri.endsWith(resourceLocation)) return fileResource;
        }

        // Case 5683 - look for a match of the supplied workspaceUri (usually file.ext) to the
        // last segment of the fileResource path.
        for (final IFile fileResource2 : fileResources) {
            fileResource = fileResource2;
            final String fileNameSegment = fileResource.getLocation().lastSegment();
            if (fileNameSegment != null && fileNameSegment.equalsIgnoreCase(workspaceUri)) return fileResource;
        }

        // MyDefect : 16368 Added to fix the defect
        for (final IFile fileResource2 : fileResources) {
            fileResource = fileResource2;
            final IPath resrcLocation = fileResource.getLocation();
            resourceLocation = resrcLocation.toOSString();
            if (resourceLocation.endsWith(workspaceUri)) return fileResource;
            resourceLocation = resrcLocation.toString();
            if (resourceLocation.endsWith(workspaceUri)) return fileResource;
        }

        return null;
    }

    private static IFile getResourceStartsWithHttp( final IFile[] fileResources,
                                                    final String workspaceUri ) {

        IFile fileResource = null;
        String targetNamespace;

        if (workspaceUri.startsWith("http")) for (final IFile fileResource2 : fileResources) { //$NON-NLS-1$
            fileResource = fileResource2;
            targetNamespace = getXsdTargetNamespace(fileResource);
            if (workspaceUri.equals(targetNamespace)) return fileResource;
        }

        return null;
    }

    private static IFile getResourceStartsWithPathSeparator( final IFile[] fileResources,
                                                             final String workspaceUri ) {

        IFile fileResource = null;
        IPath pathInWorkspace;

        if (workspaceUri.charAt(0) == IPath.SEPARATOR) {
            pathInWorkspace = new Path(workspaceUri);
            for (final IFile fileResource2 : fileResources) {
                fileResource = fileResource2;
                if (fileResource != null && fileResource.getFullPath().equals(pathInWorkspace)) return fileResource;
            }
        }

        return null;
    }

    public static Collection getResourcesThatUse( final IResource resource ) {
        return getResourcesThatUse(resource, null);
    }

    public static Collection getResourcesThatUse( final IResource resource,
                                                  final ResourceFilter filter ) {
        // search the workspace for any models that import anything beneath the path that is moving
        final Collection allResources = getAllWorkspaceResources(filter);
        final Collection colDependentResources = new ArrayList();

        // check to see if any of the resources found depend upon the specified resource:
        final IPath targetPath = resource.getFullPath();
        for (final Iterator iter = allResources.iterator(); iter.hasNext();) {
            final IResource nextResource = (IResource)iter.next();
            final IPath[] paths = getDependentResourcePaths(nextResource);
            for (final IPath path : paths)
                if (path.equals(targetPath)) {
                    final String modelPath = nextResource.getFullPath().toString();
                    // If the URI is to the Teiid Designer built-in datatypes resource or to one
                    // of the Emf XMLSchema resources then continue since there is no
                    // ModelReference to add.
                    if (modelPath != null && !isGlobalResource(modelPath)) colDependentResources.add(nextResource);
                    break;
                }
        }

        return colDependentResources;
    }

    public static void getResourcesThatUseRecursive( final IResource resource,
                                                     final ResourceFilter filter,
                                                     final Collection dependentResources ) {
        // search the workspace for any models that import anything beneath the path that is moving
        final Collection allResources = getAllWorkspaceResources(filter);

        // check to see if any of the resources found depend upon the specified resource:
        final IPath targetPath = resource.getFullPath();
        for (final Iterator iter = allResources.iterator(); iter.hasNext();) {
            final IResource nextResource = (IResource)iter.next();
            final IPath[] paths = getDependentResourcePaths(nextResource);
            for (final IPath path : paths)
                if (path.equals(targetPath)) {
                    final String modelPath = nextResource.getFullPath().toString();
                    // If the URI is to the Teiid Designer built-in datatypes resource or to one
                    // of the Emf XMLSchema resources then continue since there is no
                    // ModelReference to add.
                    if (modelPath != null && !isGlobalResource(modelPath) && !dependentResources.contains(nextResource)) {
                        dependentResources.add(nextResource);
                        getResourcesThatUseRecursive(nextResource, filter, dependentResources);
                    }
                    break;
                }
        }
    }

    /**
     * Return IResource[] array representing vdb archive IResource instances in the workspace that contain a version of any
     * IResource in the specified collection. If the method is called outside of the Eclipse runtime environment, or if the
     * specified collection is null, empty or contains IResource instances that cannot be found on the file system then an empty
     * array will be returned.
     * 
     * @param iResources the collection to search all vdb archive files for references to. If null or it not running in a Eclipse
     *        runtime environment, an empty array will be returned.
     * @return the IResource[] representing vdb archive files within the workspace
     * @param resource
     * @return
     * @since 4.3
     */
    public static IResource[] getVdbResourcesThatContain( final Collection resources ) {
        if (resources == null || resources.isEmpty() || getWorkspace() == null) return EMPTY_IRESOURCE_ARRAY;

        // Collect only vdb archive resources from the workspace
        final Collection vdbResources = getAllWorkspaceResources(VDB_RESOURCE_FILTER);

        // Retrieve any vdb archive resources that reference the specified IResource
        final Collection result = new HashSet();
        for (final Iterator iter = resources.iterator(); iter.hasNext();) {
            final IResource resource = (IResource)iter.next();
            result.addAll(getVdbResourcesThatContain(resource, vdbResources));
        }
        return (IResource[])result.toArray(new IResource[result.size()]);
    }

    /**
     * Return IResource[] array representing vdb archive IResource instances in the workspace that contain a version of the
     * specified IResource. If the method is called outside of the Eclipse runtime environment, or if the specified IResource is
     * null or cannot be found on the file system then an empty array will be returned.
     * 
     * @param iResource the IResource to search all vdb archive files for references to. If null or it not running in a Eclipse
     *        runtime environment, an empty array will be returned.
     * @return the IResource[] representing vdb archive files within the workspace
     * @param resource
     * @return
     * @since 4.3
     */
    public static IResource[] getVdbResourcesThatContain( final IResource resource ) {
        if (resource == null || !resource.exists() || getWorkspace() == null) return EMPTY_IRESOURCE_ARRAY;

        // Collect only vdb archive resources from the workspace
        final Collection vdbResources = getAllWorkspaceResources(VDB_RESOURCE_FILTER);

        // Retrieve any vdb archive resources that reference the specified IResource
        final Collection result = getVdbResourcesThatContain(resource, vdbResources);
        return (IResource[])result.toArray(new IResource[result.size()]);
    }

    /**
     * From the collection of workspace resources return the vdb archive resources that reference this IResource
     * 
     * @param resource
     * @param workspaceResources
     * @return
     * @since 4.3
     */
    private static Collection getVdbResourcesThatContain( final IResource resource,
                                                          final Collection workspaceResources ) {
        Collection result = Collections.EMPTY_LIST;
        if (resource == null || !resource.exists() || ModelUtil.isVdbArchiveFile(resource)) return result;
        if (workspaceResources == null || workspaceResources.isEmpty()) return result;

        // Check if any vdb archive resources in the workspace reference this resource ...
        String targetUuid = null;
        final XMIHeader xmiHeader = ModelUtil.getXmiHeader(resource);
        if (xmiHeader != null) targetUuid = xmiHeader.getUUID();
        final String targetPath = resource.getFullPath().makeAbsolute().toString();
        result = new ArrayList();

        // Match the UUID or full path of the specified resource to those referenced in the vdb manifest
        for (final Iterator iter = workspaceResources.iterator(); iter.hasNext();) {
            final IResource nextResource = (IResource)iter.next();
            if (!ModelUtil.isVdbArchiveFile(nextResource)) continue;
            final File vdbFile = nextResource.getRawLocation().toFile();
            if (vdbFile.exists()) {
                final com.metamatrix.common.vdb.VdbHeader vdbHeader = ModelUtil.getVdbHeader(vdbFile);
                if (vdbHeader != null) {
                    final VdbModelInfo[] infos = vdbHeader.getModelInfos();
                    for (int i = 0; i != infos.length; ++i) {
                        if (targetUuid != null && targetUuid.equals(infos[i].getUUID())) {
                            result.add(nextResource);
                            break;
                        }
                        if (targetPath.equals(infos[i].getPath())) {
                            result.add(nextResource);
                            break;
                        }
                        if (targetPath.equals(infos[i].getLocation())) {
                            result.add(nextResource);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Returns the workbench associated with this object.
     */
    private static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Return the URI string defining the relative path within the workspace for the specified IResource.
     * 
     * @param resource
     * @return
     */
    public static String getWorkspaceUri( final IResource resource ) {
        if (resource != null) return resource.getFullPath().toString();
        return null;
    }

    /**
     * Return the URI string defining the relative path within the workspace for the specified EMF resource. If the resource is one
     * of the well-known Teiid Designer/EMF global resources such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * then a logic URI is returned null since there is no IResource in the workspace that represents any one of those models.
     * 
     * @param resource
     * @return
     */
    public static String getWorkspaceUri( final Resource resource ) {
        if (resource != null && resource.getURI() != null && getWorkspace() != null) {

            // If the resource is the Teiid Designer built-in datatypes model
            // then return the specific logical URI for that model
            final URI resourceUri = resource.getURI();

            // if it is a global resource just return the uri
            String resourceUriString = resourceUri.toString();
            if (isGlobalResource(resourceUriString)) return getGlobalResourceUri(resourceUriString);
            resourceUriString = normalizeUriToString(resourceUri);
            if (isGlobalResource(resourceUriString)) return getGlobalResourceUri(resourceUriString);

            // If the corresponding IResource can be found then
            // return the relative path within the workspace
            final IResource iResource = findIResource(resource);
            if (iResource != null) return iResource.getFullPath().toString();

            // Try and compute the relative path for the Emf resource
            // by checking its path against those of all known workspace
            // projects. If the resource URI is prefixed by a known
            // project location path then remove that path from the resource
            // URI in order to create a relative path.
            String path = resourceUriString;
            try {
                final IProject[] projects = getWorkspace().getRoot().getProjects();
                for (final IProject project : projects) {
                    final IPath iPath = project.getLocation().removeLastSegments(1);
                    final String projectLocation = iPath.toString();
                    int beginIndex = path.indexOf(projectLocation);
                    if (beginIndex >= 0) {
                        beginIndex = beginIndex + projectLocation.length();
                        return path.substring(beginIndex);
                    }
                }
            } catch (final Exception e) {
                path = resourceUriString;
            }

            // Try and compute the relative path from the Emf resource
            // URI and the Platform installation location
            path = resourceUriString;
            final String osPath = resourceUri.toFileString();
            if (osPath == null) return null;

            // Remove the path to the workspace from the resource path ...
            final IPath wsPath = Platform.getLocation();
            final String wsPathStr = wsPath.toOSString();
            if (osPath.startsWith(wsPathStr)) path = osPath.substring(wsPathStr.length());
            return path;

        }
        return null;
    }

    private static String getXsdTargetNamespace( final IResource iResource ) {
        if (ModelUtil.isXsdFile(iResource)) {
            final String location = iResource.getLocation().toOSString();
            final File resourceFile = new File(location);
            if (resourceFile.exists()) try {
                final XsdHeader header = XsdHeaderReader.readHeader(resourceFile);
                if (header != null) return header.getTargetNamespaceURI();
            } catch (final TeiidException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
        return null;
    }

    /**
     * Returns true if the specified URI string is one of the well-known Teiid Designer/EMF identifiers to a global resource such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
     * "http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"
     * "http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * otherwise false is returned.
     * 
     * @param uri the URI string
     */
    public static boolean isGlobalResource( final String uri ) {
        if (uri == null) return false;

        // http://www.w3.org/2001/xml.xsd
        //       "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
        //       "http://www.w3.org/2001/MagicXMLSchema"; //$NON-NLS-1$
        //       "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$

        // If the URI is to the Teiid Designer built-in datatypes model ...
        if (uri.startsWith(DatatypeConstants.BUILTIN_DATATYPES_URI)) return true;
        if (uri.endsWith(DatatypeConstants.DATATYPES_MODEL_FILE_NAME)) return true;

        // If the URI is to the Teiid Designer built-in UML primitive types model ...
        if (uri.startsWith(UML_PRIMITIVE_TYPES_INTERNAL_URI)) return true;
        if (uri.endsWith(UML_PRIMITIVE_TYPES_MODEL_FILE_NAME)) return true;
        if (uri.startsWith(UML_METAMODEL_URI)) return true;

        // If the URI is to the Teiid Designer built-in relationship model ...
        if (uri.endsWith(RELATIONSHIP_PRIMITIVE_TYPES_INTERNAL_URI)) return true;
        if (uri.endsWith(RELATIONSHIP_PRIMITIVE_TYPES_MODEL_FILE_NAME)) return true;

        // If the URI is to one of the Emf XMLSchema resources ...
        if (uri.startsWith(ModelerCore.XML_SCHEMA_INSTANCE_GENERAL_URI) || uri.startsWith(ModelerCore.XML_MAGIC_SCHEMA_GENERAL_URI)
            || uri.startsWith(ModelerCore.XML_SCHEMA_GENERAL_URI) || uri.startsWith(ModelerCore.XML_XSD_GENERAL_URI)) return true;

        // If the URI is in the form of an Eclipse platform path to
        // one of the Emf XMLSchema resources ...
        if (uri.startsWith(XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX)) // MagicXMLSchema.xsd suffix on the resource URI
        if (uri.indexOf(XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX) > 0) return true;
        else if (uri.indexOf(XML_SCHEMA_ECLIPSE_PLATFORM_URI_SUFFIX) > 0) return true;
        else if (uri.indexOf(XML_SCHEMA_INSTANCE_ECLIPSE_PLATFORM_URI_SUFFIX) > 0) return true;

        // Next check if the import reference is to one of our system models
        try {
            final Resource[] systemModels = ModelerCore.getSystemVdbResources();
            for (int i = 0; i != systemModels.length; ++i) {
                final String systemModelUri = URI.decode(systemModels[i].getURI().toString());
                if (uri.equalsIgnoreCase(systemModelUri)) return true;
            }
        } catch (final Exception e) {
            // do nothing
        }

        // Next check if the import reference is to one of our metamodels
        try {
            if (ModelerCore.getMetamodelRegistry().containsURI(uri)) return true;
        } catch (final Exception e) {
            // do nothing
        }

        // MyCode : 18565
        if (isLocalResource(uri)) return false;

        return false;
    }

    private static boolean isLocalResource( final String uri ) {

        boolean found = false;
        try {
            final String uriName = URI.createURI(uri).lastSegment();
            final Resource[] systemModels = ModelerCore.getModelContainer().getResourceFinder().findByName(uriName, false, false);

            for (int i = 0; i != systemModels.length; ++i) {
                final URI resUri = systemModels[i].getURI();
                if (resUri.lastSegment().equalsIgnoreCase(uriName)) found = true;
            }

        } catch (final Exception e) {
            ModelerCore.Util.log(e);
        }

        return found;
    }

    public static boolean isValidWorkspaceUri( final String workspaceUri ) {

        if (workspaceUri == null || workspaceUri.length() == 0 || getWorkspace() == null) return false;

        // If the URI is to the Teiid Designer built-in datatypes resource or to one
        // of the Emf XMLSchema resources then return null since there is no
        // IResource in the workspace for any of these models
        if (isGlobalResource(workspaceUri)) return false;

        return true;
    }

    public static String normalizeUriString( final String uriString ) {
        final String normalizedUriString = removeSchemeAndAuthority(uriString);
        return normalizedUriString;
    }

    private static String normalizeUriToString( final URI uri ) {
        final String uriString = new Path(URI.decode(uri.toString())).toString();
        return normalizeUriString(uriString);
    }

    private static String removeSchemeAndAuthority( final String uri ) {
        String normalizedUri = uri;

        // EMF resource URIs are generally of the form
        // scheme://authority/device/pathSegment1/pathSegment2...
        // in which the scheme and/or authority may or may not exist
        if (normalizedUri != null) {
            // remove scheme from the URI string
            if (normalizedUri.startsWith(SCHEME_FILE)) normalizedUri = normalizedUri.substring(SCHEME_FILE.length());
            else if (normalizedUri.startsWith(SCHEME_PLATFORM)) {
                normalizedUri = normalizedUri.substring(SCHEME_PLATFORM.length());
                if (normalizedUri.startsWith(PLATFORM_RESOURCE_SEGMENT)) normalizedUri = normalizedUri.substring(PLATFORM_RESOURCE_SEGMENT.length());
            }

            // remove the authority
            if (normalizedUri.startsWith(AUTHORITY_SEPARATOR)) {
                normalizedUri = normalizedUri.substring(AUTHORITY_SEPARATOR.length());
                final int beginIndex = normalizedUri.indexOf(SEGMENT_SEPARATOR);
                if (beginIndex > -1) normalizedUri = normalizedUri.substring(beginIndex);
            }

            // remove the leading separator if it preceeds a device
            if (normalizedUri.indexOf(DEVICE_IDENTIFIER) > 0 && normalizedUri.charAt(0) == SEGMENT_SEPARATOR) normalizedUri = normalizedUri.substring(1);
        }
        return normalizedUri;
    }

    public static class FileResourceCollectorVisitor implements IResourceVisitor {
        private final List resources;
        private ResourceFilter resFilt;

        public FileResourceCollectorVisitor() {
            this(ResourceFilter.ACCEPT_ALL);
        }

        public FileResourceCollectorVisitor( final ResourceFilter rf ) {
            this.resources = new ArrayList();
            if (rf != null) resFilt = rf;
            else resFilt = ResourceFilter.ACCEPT_ALL;
        }

        public IFile[] getFileResources() {
            return (IFile[])resources.toArray(new IFile[resources.size()]);
        }

        public Collection getFileResourcesCollection() {
            return resources;
        }

        public boolean visit( final IResource resource ) {
            if (resource.exists() && resource.getType() == IResource.FILE && resFilt.accept(resource)) resources.add(resource);
            return true;
        }
    }

    static class VdbResourceFilter implements ResourceFilter {
        public boolean accept( final IResource res ) {
            return ModelUtil.isVdbArchiveFile(res);
        }

    }
}
