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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.core.TeiidException;
import org.teiid.designer.core.xmi.XMIHeader;
import org.teiid.designer.core.xmi.XMIHeaderReader;
import org.teiid.logging.LogManager;

import com.metamatrix.common.vdb.VdbHeader;
import com.metamatrix.common.vdb.VdbHeaderReader;
import com.metamatrix.common.xsd.XsdHeader;
import com.metamatrix.common.xsd.XsdHeaderReader;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.FileSeparatorUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metadata.runtime.RuntimeMetadataPlugin;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.resource.MMXmiResource;

/**
 * @since 4.0
 */
public class ModelUtil {

    public static final String DOT_PROJECT = ModelFileUtil.DOT_PROJECT;
    public static final String FILE_COLON = ModelFileUtil.FILE_COLON;

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelUtil.class);

    public static final String EXTENSION_XML = ModelFileUtil.EXTENSION_XML;

    public static final String EXTENSION_XMI = ModelFileUtil.EXTENSION_XMI;
    public static final String EXTENSION_XSD = ModelFileUtil.EXTENSION_XSD;
    public static final String EXTENSION_VDB = ModelFileUtil.EXTENSION_VDB;
    public static final String EXTENSION_ECORE = ModelFileUtil.EXTENSION_ECORE;
    public static final String EXTENSION_WSDL = ModelFileUtil.EXTENSION_WSDL;
    public static final String DOT_EXTENSION_XML = ".xml"; //$NON-NLS-1$

    public static final String DOT_EXTENSION_XMI = ".xmi"; //$NON-NLS-1$
    public static final String DOT_EXTENSION_XSD = ".xsd"; //$NON-NLS-1$
    public static final String DOT_EXTENSION_VDB = ".vdb"; //$NON-NLS-1$
    public static final String DOT_EXTENSION_ECORE = ".ecore"; //$NON-NLS-1$
    public static final String DOT_EXTENSION_WSDL = ".wsdl"; //$NON-NLS-1$
    
    public static final String MODEL_CLASS_RELATIONAL = "Relational"; //$NON-NLS-1$
    public static final String MODEL_CLASS_XML = "XML"; //$NON-NLS-1$
    public static final String MODEL_CLASS_XML_SCHEMA = "XML Schema (XSD)"; //$NON-NLS-1$
    public static final String MODEL_CLASS_WEB_SERVICE = "Web Service"; //$NON-NLS-1$
    public static final String MODEL_CLASS_FUNCTION = "Function (Deprecated)"; //$NON-NLS-1$
    public static final String MODEL_CLASS_MODEL_EXTENSION = "Model Extension (Deprecated)"; //$NON-NLS-1$
    
    public static final String URI_XML_VIEW_MODEL = "http://www.metamatrix.com/metamodels/XmlDocument"; //$NON-NLS-1$
    public static final String URI_WEB_SERVICES_VIEW_MODEL = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$
    public static final String URI_FUNCTION_MODEL = "http://www.metamatrix.com/metamodels/MetaMatrixFunction"; //$NON-NLS-1$
    public static final String URI_EXTENSION_MODEL = ExtensionPackage.eNS_URI;
    public static final String URI_RELATIONAL_MODEL = "http://www.metamatrix.com/metamodels/Relational"; //$NON-NLS-1$
    public static final String URI_XML_SCHEMA_MODEL = XSDPackage.eNS_URI;
    
    private static final String[] EXTENSIONS = new String[] {EXTENSION_XML, EXTENSION_XMI};

    private static XmiHeaderCache cache;

    /**
     * Returns the lowest-level workspace container in the specified object's hierarchy, which may be the object itself. The
     * container can only be determined if the specified object is an {@link EObject} or {@link IResource}.
     * 
     * @param object The object whose hierarchy is to be searched for a workspace container; may not be null.
     * @return The lowest-level workspace container in the specified object's hierarchy, or null if the container cannot be
     *         determined.
     * @since 4.0
     */
    public static IContainer getContainer( final Object object ) {
        CoreArgCheck.isNotNull(object);
        Object obj = object;
        if (obj instanceof EObject) {
            final ModelResource resource = ModelerCore.getModelEditor().findModelResource((EObject)obj);
            if (resource != null) obj = resource.getResource();
        }
        if (obj instanceof IResource && !(obj instanceof IContainer)) obj = ((IResource)obj).getParent();
        if (obj instanceof IContainer) {
            final IContainer ctnr = (IContainer)obj;
            return (ctnr.isAccessible() ? ctnr : null);
        }
        return null;
    }

    /**
     * Returns the file extension portion of this file, or an empty string if there is none.
     * <p>
     * The file extension portion is defined as the string following the last period (".") character in the file name. If there is
     * no period in the file name, the file has no file extension portion. If the name ends in a period, the file extension
     * portion is the empty string.
     * </p>
     * 
     * @param resource
     * @return the file extension or <code>null</code>
     * @since 4.3
     */
    public static String getFileExtension( final File resource ) {
        return FileUtil.getExtension(resource);
    }

    /**
     * Returns the model for the specified object, which may be the object itself. The model can only be determined if the
     * specified object is a {@link ModelResource}, {@link EObject}, {@link IFile} or {@link Resource}.
     * 
     * @param object The object whose hierarchy is to be searched for a model; may not be null.
     * @return The model for the specified object, or null if the model cannot be determined.
     * @since 4.0
     */
    public static ModelResource getModel( final Object object ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(object);
        if (object instanceof ModelResource) return (ModelResource)object;
        if (object instanceof EObject) return ModelerCore.getModelEditor().findModelResource((EObject)object);
        if (object instanceof IFile) return ModelerCore.getModelEditor().findModelResource((IFile)object);

        if (object instanceof Resource) return ModelerCore.getModelEditor().findModelResource((Resource)object);
        return null;
    }
    
    public static String getModelClass(final Object object) throws ModelWorkspaceException {
    	CoreArgCheck.isNotNull(object);
    	ModelResource mr = getModel(object);
    	
    	if( mr != null ) {
    		String mmURI = mr.getPrimaryMetamodelUri();
    		if( mmURI.equals(URI_RELATIONAL_MODEL) ) return MODEL_CLASS_RELATIONAL;
    		if( mmURI.equals(URI_FUNCTION_MODEL) ) return MODEL_CLASS_FUNCTION;
    		if( mmURI.equals(URI_EXTENSION_MODEL) ) return MODEL_CLASS_MODEL_EXTENSION;
    		if( mmURI.equals(URI_XML_VIEW_MODEL) ) return MODEL_CLASS_XML;
    		if( mmURI.equals(URI_WEB_SERVICES_VIEW_MODEL) ) return MODEL_CLASS_WEB_SERVICE;
    		if( mmURI.equals(URI_XML_SCHEMA_MODEL) ) return MODEL_CLASS_XML_SCHEMA;
    	}
    	
    	return null;
    }

    /**
     * Get a ModelResource for a model file.
     * 
     * @param modelFile
     * @param forceOpen true if the ModelResource should open in responce to this call, false if it is okay to lazily open the
     *        resource.
     * @return
     * @throws CoreException
     */
    public static ModelResource getModelResource( final IFile modelFile,
                                                  final boolean forceOpen ) throws ModelWorkspaceException {
        if (modelFile == null) return null;
        return ModelerCore.getModelEditor().findModelResource(modelFile);
    }

    private static void getDependentModelResources( ModelResource modelResource,
                                                    Collection<ModelResource> resources,
                                                    Collection<ModelResource> modelsProcessed,
                                                    boolean includeVirtualModels ) throws ModelWorkspaceException {
        if (!modelsProcessed.contains(modelResource)) {
            modelsProcessed.add(modelResource);
            Collection dependents = getDependentResources(modelResource);

            for (Iterator i = dependents.iterator(); i.hasNext();) {
                ModelResource model = (ModelResource)i.next();

                if (model.getModelType().getValue() == ModelType.PHYSICAL) {
                    if (!resources.contains(model)) {
                        resources.add(model);
                    }
                } else if (model.getModelType().getValue() == ModelType.VIRTUAL) {
                    if (includeVirtualModels && !resources.contains(model)) {
                        resources.add(model);
                    }

                    getDependentModelResources(model, resources, modelsProcessed, includeVirtualModels);
                }
            }
        }
    }

    public static void getDependentModelResources( ModelResource modelResource,
                                                   Collection<ModelResource> resources,
                                                   boolean includeVirtualModels ) throws ModelWorkspaceException {
        getDependentModelResources(modelResource, resources, new ArrayList(), includeVirtualModels);
    }

    /**
     * Convenience method to obtain a list of ModelResource instances which the input model resource depends upon
     * 
     * @param resource
     * @return Collection of ModelResource's
     * @since 4.2
     */
    public static Collection getDependentResources( ModelResource resource ) throws ModelWorkspaceException {
        Collection result = Collections.EMPTY_LIST;

        IResource theResource = resource.getResource();

        // Get the array of resources that this resource depends upon
        IResource[] dependents = WorkspaceResourceFinderUtil.getDependentResources(theResource);

        ModelResource mo = null;
        for (int i = 0; i != dependents.length; ++i) {
            mo = getModelResource((IFile)dependents[i], true);
            if (mo != null) {
                if (result.isEmpty()) {
                    result = new ArrayList();
                }
                result.add(mo);
            }
        }

        return result;
    }

    /**
     * Returns the models for the specified objects, which may be the objects themselves. The model can only be determined if the
     * specified object is a {@link ModelResource}, {@link EObject}, {@link IFile} or {@link Resource}.
     * 
     * @param objects The list of objects whose hierarchy is to be searched for a model; may not be null.
     * @return The Collection of models for the specified objects. May be empty if no models can be determined.
     * @since 4.0
     */
    public static Collection getModels( final List objects ) throws ModelWorkspaceException {
        CoreArgCheck.isNotNull(objects);
        final Collection<Object> modelResourceSet = new HashSet<Object>();
        for (final Iterator i = objects.iterator(); i.hasNext();) {
            final Object nextObj = i.next();
            if (nextObj instanceof ModelResource) modelResourceSet.add(nextObj);
            if (nextObj instanceof EObject) {
                final Object mr = ModelerCore.getModelEditor().findModelResource((EObject)nextObj);
                if (mr != null) modelResourceSet.add(mr);
            }
            if (nextObj instanceof IFile) {
                final Object mr = ModelerCore.getModelEditor().findModelResource((IFile)nextObj);
                if (mr != null) modelResourceSet.add(mr);
            }
            if (nextObj instanceof Resource) {
                final Object mr = ModelerCore.getModelEditor().findModelResource((Resource)nextObj);
                if (mr != null) modelResourceSet.add(mr);
            }
        }

        return modelResourceSet;
    }

    /**
     * Returns the modifiable model for the specified object, which may be the object itself. The model can only be determined if
     * the specified object is an {@link EObject} or {@link IFile}.
     * 
     * @param object The object whose hierarchy is to be searched for a model; may not be null.
     * @return The modifiable model for the specified object, or null if the model cannot be determined or is not modifiable.
     * @since 4.0
     */
    public static ModelResource getModifiableModel( final Object object ) throws ModelWorkspaceException {
        final ModelResource model = getModel(object);
        if (model != null && !model.isReadOnly()) return model;
        return null;
    }

    public static String getName( final ModelResource modelResource ) {
        String name = modelResource.getItemName();

        try {
            name = ((ModelResource)modelResource).getCorrespondingResource().getFullPath().removeFileExtension().lastSegment();
        } catch (ModelWorkspaceException e) {
            ModelerCore.Util.log(e);
        }

        return name;
    }

    /**
     * Method returns a relative path URI value between a baseResourceURI and an importedResourceURI
     * 
     * @param baseResourceURI
     * @param importedResourceURI
     * @return relative URI
     * @since 5.0
     */
    public static URI getRelativeLocation( final URI baseResourceURI,
                                           final URI importedResourceURI ) {
        URI uri = importedResourceURI;
        if (importedResourceURI.isFile()) {
            final boolean deresolve = (baseResourceURI != null && !baseResourceURI.isRelative() && baseResourceURI.isHierarchical());
            if (deresolve && !importedResourceURI.isRelative()) {
                final URI deresolvedURI = importedResourceURI.deresolve(baseResourceURI, true, true, false);
                if (deresolvedURI.hasRelativePath()) uri = deresolvedURI;
            }
        }
        return uri;
    }

    public static String getRelativePath( final IPath source,
                                          final IPath base ) {
        final StringBuffer upPath = new StringBuffer();
        final int baseSegments = base.segmentCount();
        final int matchingSegments = source.matchingFirstSegments(base);
        int upSegments = baseSegments - matchingSegments;
        final String fileSep = FileSeparatorUtil.getFileSeparator(source.toString());
        if (base.getFileExtension() != null) upSegments--;
        if (upSegments > 0) for (int i = 0; i < upSegments; i++)
            upPath.append(".." + fileSep);//$NON-NLS-1$
        final IPath sourceRelativePath = source.removeFirstSegments(matchingSegments).makeRelative();
        return upPath + sourceRelativePath.toString();
    }

    /**
     * @since 4.0
     */
    static String getString( final String id ) {
        return ModelerCore.Util.getString(I18N_PREFIX + id);
    }

    /**
     * @since 5.0
     */
    private static String getString( final String id,
                                     final String arg ) {
        return ModelerCore.Util.getString(I18N_PREFIX + id, arg);
    }

    /**
     * Return the VdbHeader for the specified vdb file or null if the file does not represent a vdb.
     * 
     * @param resource The file of a Teiid Designer vdb file.
     * @return The VdbHeader for the model file
     */
    public static VdbHeader getVdbHeader( final File resource ) {
        if (resource != null && resource.isFile() && resource.exists()) if (ModelFileUtil.isVdbArchiveFile(resource)) try {
            return VdbHeaderReader.readHeader(resource);
        } catch (final TeiidException e) {
            ModelerCore.Util.log(e);
        }
        return null;
    }

    /**
     * Method returns a VDB project file system path based on a resource within a VDB. Example:
     * E:\Apps\Designer\workspace\.metadata\.plugins\com.metamatrix.vdb.edit\vdbWorkingFolder\1055014098_111203908 If the
     * "vdbWorkingFolder" is NOT found, then the method returns NULL.
     * 
     * @param baseResource
     * @return
     * @since 5.0
     */
    public static IPath getVdbProjectPathURI( final MMXmiResource baseResource ) {
        if (baseResource.getURI().isFile() && baseResource.getURI().hasAbsolutePath()) {
            final String[] pathSegments = baseResource.getURI().segments();
            final String baseURI = baseResource.getURI().toString();
            final String fileSep = FileSeparatorUtil.getFileSeparator(baseURI);
            String deviceLocation = CoreStringUtil.Constants.EMPTY_STRING;
            // find vdbWorkingFolder index
            int vdbFolderIndex = -1;
            IPath projectPath = null;
            for (int i = 0; i < pathSegments.length; i++) {
                if (projectPath == null) {
                    projectPath = new Path(pathSegments[i]);
                    final int index = baseURI.indexOf(pathSegments[i]);
                    // Defect 24918 - Platform may be LINUX/UNIX so the device location may NOT always be available.
                    // So, we do a quick check, and if NOT WINDOWS, just set the default "root" to '/'
                    if (Platform.getOS().equals(Platform.OS_WIN32)) {
                        deviceLocation = baseURI.substring(0, index - 1);
                        if (deviceLocation.startsWith(ModelFileUtil.FILE_COLON)) deviceLocation = deviceLocation.substring(6)
                                                                                                  + fileSep;
                    } else deviceLocation = File.separator;
                } else projectPath = projectPath.append(FileUtils.normalizeFileName(pathSegments[i]));
                if (pathSegments[i].equalsIgnoreCase(ResourceFinder.VDB_WORKING_FOLDER)) {
                    vdbFolderIndex = i;
                    break;
                }
            }
            if (vdbFolderIndex > -1) // increment the index to get to the temp-directory
            projectPath = projectPath.append(pathSegments[vdbFolderIndex + 1]);
            // Now
            final IPath finalPath = new Path(deviceLocation).append(projectPath);
            return finalPath;
        }
        return null;
    }

    /**
     * Return the XMIHeader for the specified File or null if the file does not represent a MetaMatrix model file.
     * 
     * @param resource The file of a metamatrix model file.
     * @return The XMIHeader for the model file
     */
    public static XMIHeader getXmiHeader( final File resource ) {
        if (resource != null && resource.isFile() && resource.exists() && resource.canRead()) {
            // check cache
            if (cache != null) {
                final XMIHeader header = cache.getCachedXmiHeader(resource);
                if (header != null) return header;
            }
            try {
                final XMIHeader header = XMIHeaderReader.readHeader(resource);
                // add to cache
                if (cache != null) cache.setXmiHeaderToCache(resource, header);
                return header;
            } catch (final TeiidException e) {
                LogManager.logWarning(RuntimeMetadataPlugin.PLUGIN_ID, e, e.getMessage());
            } catch (final IllegalArgumentException iae) {
                // Swallowing this exception because we're doing all three checks that would produce it.
                // If this exception is caught, it's because the files really were closed/deleted in another thread and this
                // thread didn't know about it.
                // Fixes Defect 22117
            }
        }

        return null;
    }

    /**
     * Return the XMIHeader for the specified IResource or null if the file does not represent a Teiid Designer model file.
     * 
     * @param resource The IResource of a Teiid Designer model file.
     * @return The XMIHeader for the model file
     */
    public static XMIHeader getXmiHeader( final IResource resource ) {
        if (resource != null && resource.getType() == IResource.FILE) {
            final IPath path = ((IFile)resource).getLocation();
            if (path != null) return getXmiHeader(path.toFile());
        }
        return null;
    }

    /**
     * Return the XsdHeader for the specified xsd file or null if the file does not represent a XSD.
     * 
     * @param resource The file of a Teiid Designer xsd file.
     * @return The XsdHeader for the model file
     */
    public static XsdHeader getXsdHeader( final File resource ) {
        if (resource != null && resource.isFile() && resource.exists()) if (ModelFileUtil.isXsdFile(resource)) try {
            return XsdHeaderReader.readHeader(resource);
        } catch (final TeiidException e) {
            ModelerCore.Util.log(e);
        }
        return null;
    }

    /**
     * Return the XsdHeader for the specified IResource or null if the file does not represent an XSD file.
     * 
     * @param resource The IResource of a XSD file.
     * @return The XsdHeader for the model file
     */
    public static XsdHeader getXsdHeader( final IResource resource ) {
        if (resource != null && resource.getType() == IResource.FILE) {
            final IPath path = ((IFile)resource).getLocation();
            if (path != null) return getXsdHeader(path.toFile());
        }
        return null;
    }

    private static boolean isIndexFileLastModifiedAfterResourceFile( final ModelResource targetModelResource ) {

        final File rsrcIndexFile = new File(IndexUtil.INDEX_PATH, IndexUtil.getRuntimeIndexFileName(targetModelResource));
        if (!rsrcIndexFile.exists()) return false;

        final IPath path = ((IFile)targetModelResource.getResource()).getLocation();
        final long resourceLastModified = path.toFile().lastModified();
        final long indexLastModified = rsrcIndexFile.lastModified();

        return (indexLastModified < resourceLastModified);
    }

    public static boolean isIResourceReadOnly( final IResource iResource ) {
        final ResourceAttributes attributes = iResource.getResourceAttributes();
        return attributes == null ? false : attributes.isReadOnly();
    }

    /**
     * Return true if the IPath represents a Teiid Designer model file,
     * 
     * @param path The file that may be a model file
     * @return true if it is a has a model file extension
     */
    public static boolean isModelFile( final IPath path ) {
        final String extension = path.getFileExtension();
        return ModelFileUtil.isModelFileExtension(extension, true);
    }

    /**
     * Return true if the IResource represents a Teiid Designer model file, this method also check if the file exists in a project
     * with model nature.
     * 
     * @param resource The file that may be a model file
     * @return true if it is a ModelFile and part of a ModelProject
     */
    public static boolean isModelFile( final IResource resource ) {
        return isModelFile(resource, true);
    }

    /**
     * Return true if the IResource represents a Teiid Designer model file, this method may also check if the file exists in a
     * project with model nature.
     * 
     * @param resource The file that may be a model file
     * @param projectCheck A boolean to determine to perform 'model part of model project' check.
     * @return true if it is a ModelFile may/not be part of a ModelProject
     */
    public static boolean isModelFile( final IResource resource,
                                       final boolean projectCheck ) {
        if (projectCheck) if (!isModelProjectResource(resource)) return false;
        if (resource.getType() == IResource.FILE) {
            final IPath path = ((IFile)resource).getLocation();
            if (path != null) return ModelFileUtil.isModelFile(path.toFile());
        }
        return false;
    }

    /**
     * Return true if the File represents a Teiid Designer model file, this method does not check if the file exists in a project
     * with model nature.
     * 
     * @param resource The file that may be a model file
     * @return true if it is a ModelFile.
     */
    public static boolean isModelFile( final Resource resource ) {
        if (resource == null) return false;

        final String extension = resource.getURI().fileExtension();
        return ModelFileUtil.isModelFileExtension(extension, true);
    }

    /**
     * Helper method that determines that the given resource is a IFile and exists in a modeling project.
     */
    private static boolean isModelProjectResource( final IResource resource ) {
        if (resource != null) {
            final IProject proj = resource.getProject();
            if (proj != null && ModelerCore.hasModelNature(proj)) return true;
        }
        return false;
    }

    /**
     * Return the virtual model state of the specified model object.
     * 
     * @param eObject
     * @return true if model object is in virtual model.
     */
    public static boolean isPhysical( final Object obj ) {
        if (obj != null && obj instanceof EObject) {
            final EObject eObject = (EObject)obj;
            final Resource resource = eObject.eResource();
            if (resource instanceof EmfResource) return ModelType.PHYSICAL_LITERAL.equals(((EmfResource)resource).getModelAnnotation().getModelType());
            else if (resource == null && eObject.eIsProxy()) {
                final URI theUri = ((InternalEObject)eObject).eProxyURI().trimFragment();
                if (theUri.isFile()) {
                    final File newFile = new File(theUri.toFileString());
                    final XMIHeader header = getXmiHeader(newFile);
                    if (header != null && ModelType.PHYSICAL_LITERAL.equals(ModelType.get(header.getModelType()))) return true;
                }
            }
        } else if (obj != null && obj instanceof EmfResource) {
        	if( ((EmfResource)obj).getModelAnnotation() == null ) {
        		return false;
        	}
            return ModelType.PHYSICAL_LITERAL.equals(((EmfResource)obj).getModelAnnotation().getModelType());
        }
        return false;
    }

    /**
     * @since 4.0
     */
    public static boolean isValidFolderNameForPackage( final String name ) {
        return validateFolderName(name).getSeverity() != IStatus.ERROR;
    }

    /**
     * @since 4.0
     */
    public static boolean isValidModelFileName( final String name ) {
        return validateModelFileName(name).getSeverity() != IStatus.ERROR;
    }

    /**
     * @since 4.0
     */
    public static boolean isVdbArchiveFile( final IPath path ) {
        // Check that the resource has the correct lower-case extension
        if (path != null && path.getFileExtension() != null) if (ModelFileUtil.EXTENSION_VDB.equals(path.getFileExtension())) return true;
        return false;
    }

    /**
     * Return true if the IResource represents a Teiid Designer vdb file.
     * 
     * @param resource The file that may be a vdb file
     * @return true if it is a vdb File.
     */
    public static boolean isVdbArchiveFile( final IResource resource ) {
        // Check that the resource has the correct lower-case extension
        if (ModelFileUtil.EXTENSION_VDB.equals(resource.getFileExtension())) return true;
        return false;
    }

    /**
     * Return true if the Resource represents a vdb archive file.
     * 
     * @param resource The file that may be a vdb file
     * @return true if it is a xsd
     */
    public static boolean isVdbArchiveFile( final Resource resource ) {
        if (resource != null) {
            // Check that the resource has the correct lower-case extension
            final URI uri = resource.getURI();
            if (uri != null) {
                final String fileName = uri.lastSegment();
                if (fileName.endsWith(ModelFileUtil.EXTENSION_VDB)) return true;
            }
        }
        return false;
    }

    /**
     * Return the virtual model state of the specified model object.
     * 
     * @param eObject
     * @return true if model object is in virtual model.
     */
    public static boolean isVirtual( final Object obj ) {
        if (obj != null) {
            if (obj instanceof EObject) {
                final EObject eObject = (EObject)obj;
                final Resource resource = eObject.eResource();
                if (resource instanceof EmfResource) return ModelType.VIRTUAL_LITERAL.equals(((EmfResource)resource).getModelAnnotation().getModelType());
                else if (resource == null && eObject.eIsProxy()) {
                    final URI theUri = ((InternalEObject)eObject).eProxyURI().trimFragment();
                    if (theUri.isFile()) {
                        final File newFile = new File(theUri.toFileString());
                        final XMIHeader header = getXmiHeader(newFile);
                        if (header != null && ModelType.VIRTUAL_LITERAL.equals(ModelType.get(header.getModelType()))) return true;
                    }
                }
            } else if (obj instanceof EmfResource) {
                return ModelType.VIRTUAL_LITERAL.equals(((EmfResource)obj).getModelAnnotation().getModelType());
            }
        }
        return false;
    }

    /**
     * Return true if the IResource represents a Teiid Designer xmi model file.
     * 
     * @param resource The file that may be a Teiid Designer xmi model file
     * @return true if it is a Teiid Designer xmi model
     */
    public static boolean isXmiFile( final File resource ) {
        // Check that the resource has the correct lower-case extension
        if (ModelFileUtil.EXTENSION_XMI.equals(ModelFileUtil.getFileExtension(resource))) {

            // If the file does not yet exist then the only thing
            // we can do is to check the name and extension.
            if (resource != null && !resource.exists()) return true;

            final XMIHeader header = getXmiHeader(resource);
            // If the header is not null then we know the file is, at least,
            // a well formed xml document.
            if (header != null) {
                // If the XMI version for the header is not null, then return
                // false if the file represents an older 1.X model file
                if (header.getXmiVersion() != null && header.getXmiVersion().startsWith("1.")) return false; //$NON-NLS-1$
                // If the UUID for the header is not null, then the file is a
                // Teiid Designer model file containing a ModelAnnotation element.
                if (header.getUUID() != null) return true;
            }
        }
        return false;
    }

    /**
     * Return true if the IResource represents a Teiid Designer xmi model file.
     * 
     * @param resource The file that may be a Teiid Designer xmi model file
     * @return true if it is a Teiid Designer xmi model
     */
    public static boolean isXmiFile( final IResource resource ) {
        // Check that the resource has the correct lower-case extension
        if (ModelFileUtil.EXTENSION_XMI.equals(resource.getFileExtension())) {
            final XMIHeader header = getXmiHeader(resource);
            // If the header is not null then we know the file is, at least,
            // a well formed xml document.
            if (header != null) {
                // If the XMI version for the header is not null, then return
                // false if the file represents an older 1.X model file
                if (header.getXmiVersion() != null && header.getXmiVersion().startsWith("1.")) return false; //$NON-NLS-1$
                // If the UUID for the header is not null, then the file is a
                // Teiid Designer model file containing a ModelAnnotation element.
                if (header.getUUID() != null) return true;
            }
        }
        return false;
    }

    /**
     * Return true if the Resource represents a Teiid Designer xmi model file.
     * 
     * @param resource The file that may be a Teiid Designer xmi model file
     * @return true if it is a Teiid Designer xmi model
     */
    public static boolean isXmiFile( final Resource resource ) {
        if (resource != null) if (resource instanceof EmfResource) return true;
        return false;
    }

    // Refactored/moved from ModelUtilities in modeler.ui

    /**
     * Return true if the IPath represents a xsd file.
     * 
     * @param path The path to a file that may be a xsd file
     * @return true if it is a xsd
     */
    public static boolean isXsdFile( final IPath path ) {
        // Check that the resource has the correct lower-case extension
        if (ModelFileUtil.EXTENSION_XSD.equals(path.getFileExtension())) return true;
        return false;
    }

    /**
     * Return true if the IResource represents a xsd file.
     * 
     * @param resource The file that may be a xsd file
     * @return true if it is a xsd
     */
    public static boolean isXsdFile( final IResource resource ) {
        // Check that the resource has the correct lower-case extension
        if (ModelFileUtil.EXTENSION_XSD.equals(resource.getFileExtension())) return true;
        return false;
    }

    /**
     * Return true if the Resource represents a xsd file.
     * 
     * @param resource The file that may be a xsd file
     * @return true if it is a xsd
     */
    public static boolean isXsdFile( final Resource resource ) {
        if (resource != null) {
            if (resource instanceof XSDResourceImpl) return true;
            // Check that the resource has the correct lower-case extension
            final URI uri = resource.getURI();
            if (uri != null) {
                final String fileName = uri.lastSegment();
                if (fileName.endsWith(ModelFileUtil.EXTENSION_XSD)) return true;
            }
        }
        return false;
    }
    
    public static boolean isXsdFile( final File file ) {
        if (file != null) {
            // Check that the resource has the correct lower-case extension
            final String fileName = file.getName();
            if (fileName != null) {
                if (fileName.endsWith(ModelFileUtil.EXTENSION_XSD)) return true;
            }
        }
        return false;
    }

    /**
     * @since 4.0
     */
    private static IStatus newErrorStatus( final String msg ) {
        return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, -1, msg, null);
    }

    /**
     * Method returns a boolean value (true or false) for whether or not a IFile requires validation. The model may be have been
     * saved with auto-build off.
     * 
     * @param targetModelResource
     * @return true if requires validation, false if not.
     * @since 4.2
     */
    public static boolean requiresValidation( final IFile file ) {
        ModelResource mr = null;

        // Find Model Resource
        try {
            mr = getModelResource(file, false);
        } catch (final ModelWorkspaceException err) {
            final String message = getString(Constants.MODEL_RESOURCE_NOT_FOUND_MSG_KEY, file.toString());
            ModelerCore.Util.log(IStatus.ERROR, err, message);
        }

        if (mr != null) return requiresValidation(mr);

        // If we ever get here it's an error, so let's
        return false;
    }

    /**
     * Method returns a boolean value (true or false) for whether or not a model resource requires validation. The model may be
     * have been saved with auto-build off.
     * 
     * @param targetModelResource
     * @return true if requires validation, false if not.
     * @since 4.2
     */
    public static boolean requiresValidation( final ModelResource targetModelResource ) {
        if (targetModelResource == null) return false;

        // TODO: (BML 12/14/04) This check is currently required because XSD files are always tagged with a NOT_INDEXED during
        // the build process because they need to be unloaded and reloaded. Sucks, but that's the way it is.
        // There will be another defect defining that and pointing to this place to remove the next two lines!!
        if (ModelUtil.isXsdFile(targetModelResource.getResource())) return false;

        // sz - added the code to fix defect 15948.
        final boolean isIndexModified = isIndexFileLastModifiedAfterResourceFile(targetModelResource);
        if ((targetModelResource.getIndexType() == ModelResource.NOT_INDEXED) || isIndexModified) return true;

        return false;
    }

    public static boolean setIResourceReadOnly( final IResource iResource,
                                                final boolean isReadOnly ) {
        final ResourceAttributes attributes = iResource.getResourceAttributes();
        if (attributes == null) return false;
        attributes.setReadOnly(isReadOnly);
        return true;
    }

    public static void setModelWorkspaceManagerInitialized() {
        cache = ModelWorkspaceManager.getModelWorkspaceManager();
    }

    /**
     * @since 4.0
     */
    public static IStatus validateFolderName( final String name ) {
        if (name == null) return newErrorStatus(ModelerCore.Util.getString("ModelUtil.folder_must_have_a_non-null_name")); //$NON-NLS-1$
        if (name.trim().length() == 0) return newErrorStatus(ModelerCore.Util.getString("ModelUtil.folder_must_have_a_non-empty_name")); //$NON-NLS-1$

        // Ensure the file name is a valid file name
        return validateName(name, IResource.FOLDER);
    }

    /**
     * @since 4.0
     */
    public static IStatus validateModelFileName( final String name ) {
        if (name == null) return newErrorStatus(ModelerCore.Util.getString("ModelUtil.model_file_name_may_not_be_null")); //$NON-NLS-1$
        if (name.trim().length() == 0) return newErrorStatus(ModelerCore.Util.getString("ModelUtil.model_file_name_may_not_be_zero_length")); //$NON-NLS-1$
        boolean validExtension = false;
        // Try exact match (likely)
        for (final String element : EXTENSIONS)
            if (name.endsWith(element)) {
                validExtension = true;
                break;
            }

        if (!validExtension) return newErrorStatus(ModelerCore.Util.getString("ModelUtil.model_file_name_does_not_have_a_valid_extension")); //$NON-NLS-1$

        // Ensure the file name is a valid file name
        return validateName(name, IResource.FILE);
    }

    /**
     * This is a re-implementation of {@link IWorkspace#validateName(java.lang.String, int)}, which only works if inside Eclipse.
     * 
     * @see IWorkspace#validateName
     * @since 4.0
     */
    private static IStatus validateName( final String segment,
                                         final int type ) {

        /* segment must not begin or end with a whitespace */
        if (Character.isWhitespace(segment.charAt(0)) || Character.isWhitespace(segment.charAt(segment.length() - 1))) {
            final String message = ModelerCore.Util.getString("ModelUtil.resources.invalidWhitespace", segment); //$NON-NLS-1$
            return newErrorStatus(message);
        }

        /* segment must not end with a dot */
        if (segment.endsWith(".")) { //$NON-NLS-1$
            final String message = ModelerCore.Util.getString("ModelUtil.resources.invalidDot", segment); //$NON-NLS-1$
            return newErrorStatus(message);
        }
        return ModelStatusImpl.VERIFIED_OK;
    }

    /**
     * Prevents instantiation outside of this class.
     * 
     * @since 4.0
     */
    private ModelUtil() {
    }

    public static interface Constants {
        String MODEL_NOT_PHYSICAL_MESSAGE = getString("modelNotPhysicalMessage"); //$NON-NLS-1$
        String MODEL_RESOURCE_NOT_FOUND_MSG_KEY = "modelResourceNotFoundMessageKey"; //$NON-NLS-1$
    }

    public interface XmiHeaderCache {
        XMIHeader getCachedXmiHeader( File resource );

        void setXmiHeaderToCache( File resource,
                                  XMIHeader header );
    }
}
