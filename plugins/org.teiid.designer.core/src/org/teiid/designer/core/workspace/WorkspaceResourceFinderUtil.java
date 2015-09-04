/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.TeiidDesignerException;
import org.teiid.core.designer.id.UUID;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.common.xsd.XsdHeader;
import org.teiid.designer.common.xsd.XsdHeaderReader;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.reader.ZipReaderCallback;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.xmi.ModelImportInfo;
import org.teiid.designer.core.xmi.XMIHeader;
import org.teiid.designer.core.xmi.XMIHeaderReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * WorkspaceResourceFinder
 *
 * @since 8.0
 */
public class WorkspaceResourceFinderUtil {

    protected static final String URI_REFERENCE_DELIMITER = DatatypeConstants.URI_REFERENCE_DELIMITER;

    /**
     * Resource filter for return only VDB archive files
     */
    public static final ResourceFilter VDB_RESOURCE_FILTER = new VdbResourceFilter();

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
     * @param resource
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
     * @return the IFile identified by the URI if it exists; may return null
     */
    public static IFile findIResource( final String workspaceUri ) {
        if (!isValidWorkspaceUri(workspaceUri)) return null;
        final String normalizedUriString = normalizeUriString(workspaceUri);

        // MyDefect : 16368 Refactored methods.

        IFile fileResource;
        final Collection<IFile> fileResources = getProjectFileResources();

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
                final Collection<IFile> fileResources = getProjectFileResources();

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
     * Return the collection of {@link IFile} instances that match the specified name.
     * The name must consist of only one path segment and may or may not have
     * a file extension. If no {@link IFile} instances are found that match this name an
     * empty collection is returned.
     * 
     * @param name the name of the IResource
     * @return the {@link IFile} collection
     */
    public static Collection<IFile> findIResourceByName( final String name ) {
        if (name == null || name.length() == 0 || getWorkspace() == null)
            return Collections.emptyList();

        FileNameResourceCollectorVisitor visitor = new FileNameResourceCollectorVisitor(name);
        getProjectFileResources(visitor);
        return visitor.getFileResources();
    }
    
    /**
     * Return the collection of {@link IFile} instances that match the specified name.
     * The name must consist of only one path segment and may or may not have a
     * file extension. If no {@link IFile} instances are found that match this name
     * an empty collection is returned.
     * 
     * @param name the name of the IResource
     * @param project the target project
     * @return the {@link IFile} collection
     */
    public static Collection<IFile> findIResourceInProjectByName( final String name, final IProject project) {
        if (name == null || name.length() == 0 || getWorkspace() == null)
            return Collections.emptyList();

        FileNameResourceCollectorVisitor visitor = new FileNameResourceCollectorVisitor(name);
        try {
            project.accept(visitor);
        } catch (final CoreException e) {
            // do nothing
            ModelerCore.Util.log(e);
        }
        return visitor.getFileResources();
    }

    /**
     * Return the {@link IFile} instance that matches the specified path.
     * The path is the relative path in the workspace. If no
     * file is found that match this path a null is returned.
     *
     * @param workspacePath the path to the resource
     * @return the {@link IFile}
     */
    public static IFile findIResourceByPath( final IPath workspacePath ) {
        if (workspacePath == null || workspacePath.isEmpty() || getWorkspace() == null)
            return null;

        FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor() {
            @Override
            public boolean visit(IResource resource) {
                if (! resource.exists() || resource.getType() != IResource.FILE || !getResourceFilter().accept(resource)) 
                    return false;

                IPath path = resource.getFullPath();
                // Do not process file names staring with '.' since these
                // are considered reserved for Eclipse specific files
                if (path.lastSegment().charAt(0) == '.')
                    return false;

                if (path.equals(workspacePath))
                    addResource(resource);

                return true;
            }
        };

        getProjectFileResources(visitor);

        if (visitor.getFileResources().size() == 0)
            return null;

        // Return the first one in the collection as there should be only one
        return visitor.getFileResources().iterator().next();
    }

    /**
     * Return the {@link IFile} instance that matches the stringified UUID.
     * If the stringified UUID is null, empty, does not have a
     * UUID.PROTOCOL prefix or no match is found then null is returned.
     * 
     * @param stringifiedUuid the stringified form of a UUID instance
     * @return the {@link IFile}
     */
    public static IFile findIResourceByUUID( final String stringifiedUuid ) {
        if (CoreStringUtil.isEmpty(stringifiedUuid) || !stringifiedUuid.startsWith(UUID.PROTOCOL) || getWorkspace() == null) return null;

        // Visitor should only look for XMI files with UUIDs in them
        // If the resource is a FOLDER or PROJECT we should return TRUE so that the resource's children will get visited
        // If we encounter a XSD or XMI file, we return false so the visitation stops at the model
        
        FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor() {
            @Override
            public boolean visit(IResource resource) {
                if (resource.exists() && resource.getType() == IResource.FILE && getResourceFilter().accept(resource) ) { 
                    if (ModelUtil.isXsdFile(resource))
                        return false;

                    if( ModelUtil.isModelFile(resource, true)) {
                    	XMIHeader header = ModelUtil.getXmiHeader(resource);
                    	if (header != null && stringifiedUuid.equals(header.getUUID())) {
                    		addResource(resource);
                    	}
                    	return false;
                    }

                    if( resource.getType() == IResource.PROJECT || 
                    	resource.getType() == IResource.FOLDER ) {
                    	return true;
                    }
                    
                    return false;
                }
                
                return true;

            }
        };

        getProjectFileResources(visitor);

        if (visitor.getFileResources().size() == 0)
            return null;

        // Return the first one in the collection as there should be only one
        return visitor.getFileResources().iterator().next();
    }

    /**
     * Get the absolute location of give relative path using the given base
     *
     * @param base
     * @param relativePath
     *
     * @return absolute file path
     */
    public static String getAbsoluteLocation( final File base, final String relativePath ) {
        final URI baseLocation = URI.createFileURI(base.getAbsolutePath());
        URI relLocation = URI.createURI(relativePath, false);
        if (baseLocation.isHierarchical() && !baseLocation.isRelative() && relLocation.isRelative()) relLocation = relLocation.resolve(baseLocation);
        return URI.decode(relLocation.toString());
    }

    /**
     * Find all files in open modelling projects,
     * ie. this will ignore projects that lack a modelling nature
     *
     * @return collection of file resources
     */
    public static Collection<IFile> getProjectFileResources() {
        final FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor();
        getProjectFileResources(visitor);

        return visitor.getFileResources();
    }

    /**
     * Find all files in open modelling projects using the given visitor.
     *
     * @param visitor
     */
    public static void getProjectFileResources(IResourceVisitor visitor) {
        final IWorkspace workspace = getWorkspace();
        if (workspace == null || workspace.getRoot() == null)
            return;

        final IWorkspaceRoot wsRoot = workspace.getRoot();
        final IProject[] projects = wsRoot.getProjects();
        for (final IProject project : projects) {
            if (! project.isOpen())
                continue;

            // Ignore of projects without a modelling nature
            if (! ModelerCore.hasModelNature(project))
                continue;

            try {
                project.accept(visitor);
            } catch (final CoreException e) {
                // do nothing
                ModelerCore.Util.log(e);
            }
        }
    }

    /**
     * Get all file resources contained in the given project
     *
     * @param project
     *
     * @return collection of file resources
     */
    public static Collection<IFile> getProjectFileResources(final IProject project) {
        return getProjectFileResources(project, null);
    }

    /**
     * Get all file resources contained in the given project,
     * filtered by the given filter.
     *
     * @param project
     * @param filter
     *
     * @return collection of file resources
     */
    public static Collection<IFile> getProjectFileResources(final IProject project, final ResourceFilter filter ) {
        CoreArgCheck.isNotNull(project);

        if (! ModelerCore.hasModelNature(project))
            return Collections.emptyList();

        // Collect all IResources within the given project
        final FileResourceCollectorVisitor visitor = new FileResourceCollectorVisitor(filter);
        try {
            project.accept(visitor);
        } catch (final CoreException e) {
            ModelerCore.Util.log(e);
        }

        // List is required since visitor's collection is unmodifiable
        final Collection<IFile> fileResources = new ArrayList<IFile>(visitor.getFileResources());
        final Iterator<IFile> iterator = fileResources.iterator();
        while(iterator.hasNext()) {
            final IFile fileResource = iterator.next();
            final IPath path = fileResource.getFullPath();

            // Do not process file names starting with '.' since these
            // are considered reserved for Eclipse specific files
            if (path.lastSegment().charAt(0) == '.')
                iterator.remove();
        }

        return fileResources;
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
     * be found on the file system then an empty list will be returned.
     * 
     * @param iResource the IResource to examine for import declarations. If null, or it not running in a Eclipse runtime
     *        environment, an empty list will be returned.
     * @return the IResource[] references of dependent resources
     */
    public static List<IFile> getDependentResources( final IResource iResource ) {
        if (iResource == null || getWorkspace() == null)
            return Collections.emptyList();

        final List<IFile> result = new ArrayList();
        try {
            final File iResourceFile = ModelUtil.getLocation(iResource).toFile();
            if (!iResourceFile.exists())
                return Collections.emptyList();

            // Get the header information from the XSD file
            if (ModelUtil.isXsdFile(iResource)) {
                final XsdHeader header = XsdHeaderReader.readHeader(iResourceFile);
                if (header != null) {

                    // Add all the imported schema locations
                    String[] locations = header.getImportSchemaLocations();
                    for (int i = 0; i != locations.length; ++i) {
                        final String location = locations[i];

                        IFile dependentIResource = findIResource(location);
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
                        IFile dependentIResource = findIResource(location);
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
                        IFile dependentIResource = null;

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
                        IFile dependentIResource = null;

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

        return result;
    }

    /**
     * Returns a valid path reference if the specified URI string is one of the
     * well-known Teiid Designer/EMF identifiers to a global resource such as
     * <p>
     * "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
     * "http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"
     * "http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance" "http://www.w3.org/2001/XMLSchema"
     * "http://www.w3.org/2001/MagicXMLSchema" "http://www.w3.org/2001/XMLSchema-instance"
     * </p>
     * otherwise null is returned.
     *
     * @param uri the URI string
     * @return a valid path reference or null
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

    private static IFile getResourceByLocation( final Collection<IFile> fileResources, final String workspaceUri ) {
        String resourceLocation;

        // Try to match the workspace URI to a IResource location ...
        for (final IFile fileResource : fileResources) {
            IPath location = fileResource.getLocation();

            resourceLocation = location.toOSString();
            if (workspaceUri.endsWith(resourceLocation))
                return fileResource;

            if (resourceLocation.endsWith(workspaceUri))
                return fileResource;

            resourceLocation = location.toString();
            if (workspaceUri.endsWith(resourceLocation))
                return fileResource;

            if (resourceLocation.endsWith(workspaceUri))
                return fileResource;

            // Case 5683 - look for a match of the supplied workspaceUri (usually file.ext) to the
            // last segment of the fileResource path.
            final String fileNameSegment = location.lastSegment();
            if (workspaceUri.equalsIgnoreCase(fileNameSegment))
                return fileResource;
        }

        return null;
    }

    private static IFile getResourceStartsWithHttp( final Collection<IFile> fileResources, final String workspaceUri ) {
        String targetNamespace;

        if (! workspaceUri.startsWith("http")) //$NON-NLS-1$
            return null;

         for (final IFile fileResource : fileResources) {
            targetNamespace = getXsdTargetNamespace(fileResource);
            if (workspaceUri.equals(targetNamespace))
                return fileResource;
        }

        return null;
    }

    private static IFile getResourceStartsWithPathSeparator( final Collection<IFile> fileResources, final String workspaceUri ) {
        if (workspaceUri.charAt(0) != IPath.SEPARATOR)
            return null;

        IPath pathInWorkspace = new Path(workspaceUri);
        for (final IFile fileResource : fileResources) {
            if (fileResource == null)
                continue;

            if (fileResource.getFullPath().equals(pathInWorkspace))
                return fileResource;
        }

        return null;
    }

    /**
     * Find all resources in the same project that use the given resource.
     * <p>
     * To control the depth of the search so as to find indirect relationships between
     * resources, set the depth value accordingly:
     * <ul>
     * <li>{@link IResource#DEPTH_ZERO} only direct resource dependencies;</li>
     * <li>{@link IResource#DEPTH_INFINITE} direct and indirect resource dependencies</li>
     * </ul>
     *
     * @param resource
     * @param depth one of {@link IResource#DEPTH_ZERO} or {@link IResource#DEPTH_INFINITE}
     *
     * @return collection of resources that use the given resource
     */
    public static Collection<IFile> getResourcesThatUse( final IResource resource, int depth ) {
        return getResourcesThatUse(resource, null, depth);
    }

    /**
     * Find all resources in the same project that use the given resource,
     * filtered by the given filter.
     * <p>
     * To control the depth of the search so as to find indirect relationships between
     * resources, set the depth value accordingly:
     * <ul>
     * <li>{@link IResource#DEPTH_ZERO} only direct resource dependencies;</li>
     * <li>{@link IResource#DEPTH_INFINITE} direct and indirect resource dependencies</li>
     * </ul>
     *
     * @param resource
     * @param filter
     * @param depth one of {@link IResource#DEPTH_ZERO} or {@link IResource#DEPTH_INFINITE}
     * @return collection of resources that use the given resource
     */
    public static Collection<IFile> getResourcesThatUse( final IResource resource, final ResourceFilter filter, int depth ) {
        Collection<IFile> fileResources = new HashSet<IFile>();
        getResourcesThatUse(resource, filter, depth, fileResources);
        return fileResources;
    }

    private static void getResourcesThatUse(final IResource resource, final ResourceFilter filter, int depth, Collection<IFile> resultDependents) {
        // search the resource's project for any models that import anything beneath the path of the resource
        IProject project = resource.getProject();
        IPath targetPath = resource.getFullPath();
        Collection<IFile> projectResources = getProjectFileResources(project, filter);

        /* Check to see if any of the resources found
         * depend upon the specified resource
         */

        // Iterate through all the file resources
        for (IFile fileResource : projectResources) {
            // Get all the dependents of this file resource
            final Collection<IFile> dependents = getDependentResources(fileResource);

            // Iterate through the dependents
            for (final IResource dependent : dependents) {
                if (! targetPath.equals(dependent.getFullPath()))
                    continue;

                // This dependent imports the target resource
                final String modelPath = fileResource.getFullPath().toString();

                // If the URI is to the Teiid Designer built-in datatypes resource or to one
                // of the Emf XMLSchema resources then continue since there is no
                // ModelReference to add.
                if (isGlobalResource(modelPath))
                    continue;

                // only add and recurse if haven't already
                if (resultDependents.add(fileResource) && (IResource.DEPTH_INFINITE == depth)) {
                    getResourcesThatUse(fileResource, filter, depth, resultDependents);
                }
                
                break;
            }
        }
    }

    private static boolean dependsOn(final IResource resourceBeingChecked,
                                     final IResource model) {
        final Collection<IFile> dependents = getDependentResources(resourceBeingChecked);

        for (final IResource dependent : dependents) {
            if (model.equals(dependent)) {
                return true;
            }

            // recurse
            if (dependsOn(dependent, model)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param model the model being checked (cannot be <code>null</code>)
     * @return the model that is causing the circular dependencies (can be <code>null</code> if no circular dependencies)
     */
    public static IFile getFirstResourceHavingCircularDependency(final IResource model) {
        final Collection<IFile> dependents = getDependentResources(model);

        if ((dependents == null) || dependents.isEmpty()) {
            return null;
        }

        for (final IFile dependent : dependents) {
            if (dependsOn(dependent, model)) {
                return dependent;
            }
        }

        return null;
    }

    /**
     * Return {@link IFile} collection representing vdb instances in the given resources' projects that contain a 
     * version of any resource in the specified collection.
     * 
     * If the method is called outside of the Eclipse runtime environment, or if the specified collection is null, 
     * empty or contains resource instances that cannot be found on the file system then an empty
     * collection will be returned.
     *
     * @param resources the collection to search all vdb archive files for references to. If null or it not 
     *        running in a Eclipse runtime environment, an empty collection will be returned.
     * @return the {@link IFile} collection representing vdb archive files within the workspace
     */
    public static Collection<IFile> getVdbResourcesThatContain( final Collection<IResource> resources ) {
        if (resources == null || resources.isEmpty() || getWorkspace() == null)
            return Collections.emptyList();

        // Retrieve any vdb archive resources that reference the specified IResource
        final Collection result = new HashSet();
        for (IResource resource : resources) {
            result.addAll(getVdbResourcesThatContain(resource));
        }

        return result;
    }

    /**
     * Return {@link IFile} collection representing vdb instances in the given resource's project that contains a 
     * version of the given resource.
     *
     * If the method is called outside of the Eclipse runtime environment, or if the given resource is null, 
     * or the resource cannot be found on the file system then an empty collection will be returned.
     *
     * @param resource to search all vdb archive files for references to. If null or it not
     *        running in a Eclipse runtime environment, an empty collection will be returned.
     * @return the {@link IFile} collection representing vdb archive files within the workspace
     */
    public static Collection<IFile> getVdbResourcesThatContain( final IResource resource ) {
        if (resource == null || !resource.exists() || getWorkspace() == null)
            return Collections.emptyList();

        final Collection result = new HashSet();

        // Collect vdb resources from resource's project
        final Collection<IFile> vdbResources = getProjectFileResources(resource.getProject(), VDB_RESOURCE_FILTER);
        result.addAll(getVdbResourcesThatContain(resource, vdbResources));

        return result;
    }

    private static boolean vdbContainsResource(File vdbFile, final IPath targetResourcePath) {
        final Boolean[] status = new Boolean[1];
        status[0] = false;

        try {
            ZipReaderCallback callback = new ZipReaderCallback() {

               @Override
                public void process(InputStream inputStream) throws Exception {
                   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                   Document xmlDocument = dBuilder.parse(inputStream);

                   NodeList modelList = xmlDocument.getElementsByTagName("model"); //$NON-NLS-1$
                   for (int i = 0; i < modelList.getLength(); ++i) {
                       Node modelNode = modelList.item(i);
                       if (modelNode.getNodeType() != Node.ELEMENT_NODE)
                           continue;

                       Element element = (Element)modelNode;
                       if (!element.hasAttribute("path")) //$NON-NLS-1$
                          continue;

                       String path = element.getAttribute("path"); //$NON-NLS-1$
                       if (path.equals(targetResourcePath.toOSString())) {
                           status[0] = true;
                           return;
                       }
                   }
                }
            };

            ModelUtil.readVdbHeader(vdbFile, callback);

        } catch (Exception ex) {
            ModelerCore.Util.log(ex);
        }

        return status[0];
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
                                                          final Collection<IFile> workspaceResources ) {
        Collection<IResource> result = Collections.emptyList();
        if (resource == null || !resource.exists() || ModelUtil.isVdbArchiveFile(resource))
            return result;

        if (workspaceResources == null || workspaceResources.isEmpty())
            return result;

        // Check if any vdb archive resources in the workspace reference this resource ...
        result = new ArrayList<IResource>();

        // Match the full path of the specified resource to those referenced in the vdb manifest
        for (IFile fileResource : workspaceResources) {
            if (!ModelUtil.isVdbArchiveFile(fileResource))
                continue;

            File vdbFile = null;
            try {
                vdbFile = ModelUtil.getLocation(fileResource).toFile();
            } catch (CoreException ex) {
                ModelerCore.Util.log(ex);
            }

            if (vdbFile == null || ! vdbFile.exists())
                continue;

            if (vdbContainsResource(vdbFile, resource.getFullPath().makeAbsolute()))
                result.add(fileResource);
        }

        return result;
    }

    /**
     * Returns the workbench associated with this object.
     */
    private static IWorkspace getWorkspace() {
        return ModelerCore.getWorkspace();
    }

    /**
     * Return the URI string defining the relative path within the workspace for the specified IResource.
     * 
     * @param resource
     * @return string representation of the resource URI
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
     * @return string representation of the resource URI
     */
    public static String getWorkspaceUri( final Resource resource ) {
        if (resource == null || resource.getURI() == null || getWorkspace() == null)
            return null;

        // If the resource is the Teiid Designer built-in datatypes model
        // then return the specific logical URI for that model
        final URI resourceUri = resource.getURI();

        // if it is a global resource just return the uri
        String resourceUriString = resourceUri.toString();
        if (isGlobalResource(resourceUriString))
            return getGlobalResourceUri(resourceUriString);

        resourceUriString = normalizeUriToString(resourceUri);
        if (isGlobalResource(resourceUriString))
            return getGlobalResourceUri(resourceUriString);

        // If the corresponding IResource can be found then
        // return the relative path within the workspace
        final IResource iResource = findIResource(resource);
        if (iResource != null)
            return iResource.getFullPath().toString();

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
        if (osPath == null)
            return null;

        // Remove the path to the workspace from the resource path ...
        final IPath wsPath = Platform.getLocation();
        final String wsPathStr = wsPath.toOSString();
        if (osPath.startsWith(wsPathStr))
            path = osPath.substring(wsPathStr.length());

        return path;
    }

    private static String getXsdTargetNamespace( final IResource iResource ) {
        if (ModelUtil.isXsdFile(iResource)) {
            final String location = iResource.getLocation().toOSString();
            final File resourceFile = new File(location);
            if (resourceFile.exists()) try {
                final XsdHeader header = XsdHeaderReader.readHeader(resourceFile);
                if (header != null) return header.getTargetNamespaceURI();
            } catch (final TeiidDesignerException e) {
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
     * @return true if the uri represents a global resource or false otherwise
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

            if ((systemModels != null) && (systemModels.length != 0)) {
                for (int i = 0; i != systemModels.length; ++i) {
                    final URI resUri = systemModels[i].getURI();
                    if (resUri.lastSegment().equalsIgnoreCase(uriName)) found = true;
                }
            }
        } catch (final Exception e) {
            ModelerCore.Util.log(e);
        }

        return found;
    }

    /**
     * Is the given workspace URI valid.
     *
     * @param workspaceUri
     *
     * @return true if valid, false otherwise
     */
    public static boolean isValidWorkspaceUri( final String workspaceUri ) {

        if (workspaceUri == null || workspaceUri.length() == 0 || getWorkspace() == null) return false;

        // If the URI is to the Teiid Designer built-in datatypes resource or to one
        // of the Emf XMLSchema resources then return null since there is no
        // IResource in the workspace for any of these models
        if (isGlobalResource(workspaceUri)) return false;

        return true;
    }

    /**
     * Normalise the given uri string reprentation
     *
     * @param uriString
     *
     * @return normalized uri string
     */
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

    /**
     * Visitor that collects file resources conforming to an optional filter
     */
    public static class FileResourceCollectorVisitor implements IResourceVisitor {
        private final List resources;

        protected ResourceFilter resourceFilter;

        /**
         * Create a new default instance with an 'accept everything filter'
         */
        public FileResourceCollectorVisitor() {
            this(ResourceFilter.ACCEPT_ALL);
        }

        /**
         * Create a new instance with filter
         *
         * @param resourceFilter
         */
        public FileResourceCollectorVisitor( final ResourceFilter resourceFilter ) {
            this.resources = new ArrayList();
            if (resourceFilter != null)
                this.resourceFilter = resourceFilter;
            else
                this.resourceFilter = ResourceFilter.ACCEPT_ALL;
        }

        protected boolean addResource(final IResource resource) {
            return resources.add(resource);
        }

        protected ResourceFilter getResourceFilter() {
            return resourceFilter;
        }

        /**
         * Get the found file resource collection
         *
         * @return collection of {@link IFile} resources
         */
        public Collection<IFile> getFileResources() {
            return Collections.unmodifiableCollection(resources);
        }

        @Override
		public boolean visit( final IResource resource ) {
            if (resource.exists() && resource.getType() == IResource.FILE && getResourceFilter().accept(resource)) 
                addResource(resource);

            return true;
        }
    }

    /**
     * Visits resources and returns those that match a particular name
     */
    private static class FileNameResourceCollectorVisitor extends FileResourceCollectorVisitor {

        private final String name;

        private final boolean removeExtension;

        /**
         * Create new instance
         *
         * @param name
         */
        public FileNameResourceCollectorVisitor(String name) {
            this.name = name;
            this.removeExtension = name.indexOf('.') == -1;
        }

        @Override
        public boolean visit(IResource resource) {
            /*
             * Always needs to return true since the search will not recurse into
             * projects and folders!
             *
             * However, we only add the resource if it matches the name.
             */
            if (! resource.exists() || resource.getType() != IResource.FILE || ! getResourceFilter().accept(resource)) 
                return true;

            IPath path = resource.getFullPath();
            // Do not process file names staring with '.' since these
            // are considered reserved for Eclipse specific files
            if (path.lastSegment().charAt(0) == '.')
                return true;

            if (removeExtension)
                path = path.removeFileExtension();

            if (name.equalsIgnoreCase(path.lastSegment()))
                addResource(resource);

            return true;
        }
    }

    /**
     * Visitor that collects vdb resources
     */
    public static class VdbResourceCollectorVisitor extends FileResourceCollectorVisitor {

        private final String optionalName;

        /**
         * Create a default instance which will simply find all vdb files
         */
        public VdbResourceCollectorVisitor() {
            this(null);
        }

        /**
         * Create an instance which will find vdb files with the given name
         *
         * @param vdbName
         */
        public VdbResourceCollectorVisitor(String vdbName) {
            this.optionalName = vdbName;
            this.resourceFilter = new VdbResourceFilter();
        }

        @Override
        public boolean visit(IResource resource) {
            /*
             * Always needs to return true since the search will not recurse into
             * projects and folders!
             *
             * However, we only add the resource if its a vdb, which is determined via the
             * resource filter.
             */

            if (! resource.exists() || resource.getType() != IResource.FILE || ! getResourceFilter().accept(resource)) 
                return true;

            if (optionalName == null) {// no optional name specified
                addResource(resource);
            }
            else if (optionalName != null && optionalName.equalsIgnoreCase(resource.getFullPath().lastSegment())) {
                // optional name specified
                addResource(resource);
            }

            return true;
        }
    }

    /**
     * Filter to find the vdb resources
     */
    private static class VdbResourceFilter implements ResourceFilter {
        @Override
		public boolean accept( final IResource res ) {
            return ModelUtil.isVdbArchiveFile(res);
        }

    }
}
