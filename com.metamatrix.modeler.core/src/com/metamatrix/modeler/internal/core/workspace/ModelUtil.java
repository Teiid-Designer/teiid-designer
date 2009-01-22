/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.FileSeparatorUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.xml.vdb.VdbHeader;
import com.metamatrix.internal.core.xml.vdb.VdbHeaderReader;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.internal.core.xml.xsd.XsdHeader;
import com.metamatrix.internal.core.xml.xsd.XsdHeaderReader;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.resource.MMXmiResource;
import com.metamatrix.modeler.internal.core.resource.vdb.VdbResourceImpl;

/**
 * @since 4.0
 */
public class ModelUtil {

    public static final String MANIFEST_MODEL_NAME = ModelFileUtil.MANIFEST_MODEL_NAME;

    public static final String DOT_PROJECT = ModelFileUtil.DOT_PROJECT;
    public static final String FILE_COLON = ModelFileUtil.FILE_COLON;

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelUtil.class);

    public static interface Constants {
        String MODEL_NOT_PHYSICAL_MESSAGE = getString("modelNotPhysicalMessage"); //$NON-NLS-1$
        String MODEL_RESOURCE_NOT_FOUND_MSG_KEY = "modelResourceNotFoundMessageKey"; //$NON-NLS-1$
    }

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

    private static final String[] EXTENSIONS = new String[] {EXTENSION_XML, EXTENSION_XMI};

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
        ArgCheck.isNotNull(object);
        Object obj = object;
        if (obj instanceof EObject) {
            ModelResource resource = ModelerCore.getModelEditor().findModelResource((EObject)obj);
            if (resource != null) {
                obj = resource.getResource();
            }
        }
        if (obj instanceof IResource && !(obj instanceof IContainer)) {
            obj = ((IResource)obj).getParent();
        }
        if (obj instanceof IContainer) {
            final IContainer ctnr = (IContainer)obj;
            return (ctnr.isAccessible() ? ctnr : null);
        }
        return null;
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
        ArgCheck.isNotNull(object);
        if (object instanceof ModelResource) {
            return (ModelResource)object;
        }
        if (object instanceof EObject) {
            return ModelerCore.getModelEditor().findModelResource((EObject)object);
        }
        if (object instanceof IFile) {
            return ModelerCore.getModelEditor().findModelResource((IFile)object);
        }

        if (object instanceof Resource) {
            return ModelerCore.getModelEditor().findModelResource((Resource)object);
        }
        return null;
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
        ArgCheck.isNotNull(objects);
        Collection<Object> modelResourceSet = new HashSet<Object>();
        for (Iterator i = objects.iterator(); i.hasNext();) {
            Object nextObj = i.next();
            if (nextObj instanceof ModelResource) {
                modelResourceSet.add(nextObj);
            }
            if (nextObj instanceof EObject) {
                Object mr = ModelerCore.getModelEditor().findModelResource((EObject)nextObj);
                if (mr != null) {
                    modelResourceSet.add(mr);
                }
            }
            if (nextObj instanceof IFile) {
                Object mr = ModelerCore.getModelEditor().findModelResource((IFile)nextObj);
                if (mr != null) {
                    modelResourceSet.add(mr);
                }
            }
            if (nextObj instanceof Resource) {
                Object mr = ModelerCore.getModelEditor().findModelResource((Resource)nextObj);
                if (mr != null) {
                    modelResourceSet.add(mr);
                }
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
        if (model != null && !model.isReadOnly()) {
            return model;
        }
        return null;
    }

    /**
     * Return true if the IResource represents a MetaMatrix model file, this method also check if the file exists in a project
     * with model nature.
     * 
     * @param resource The file that may be a model file
     * @return true if it is a ModelFile and part of a ModelProject
     */
    public static boolean isModelFile( final IResource resource ) {
        return isModelFile(resource, true);
    }

    /**
     * Return true if the IPath represents a MetaMatrix model file,
     * 
     * @param path The file that may be a model file
     * @return true if it is a has a model file extension
     */
    public static boolean isModelFile( final IPath path ) {
        final String extension = path.getFileExtension();
        return isModelFileExtension(extension, true);
    }

    /**
     * Return true if the IResource represents a MetaMatrix model file, this method may also check if the file exists in a project
     * with model nature.
     * 
     * @param resource The file that may be a model file
     * @param projectCheck A boolean to determine to perform 'model part of model project' check.
     * @return true if it is a ModelFile may/not be part of a ModelProject
     */
    public static boolean isModelFile( final IResource resource,
                                       final boolean projectCheck ) {
        if (projectCheck) {
            if (!isModelProjectResource(resource)) {
                return false;
            }
        }
        if (resource.getType() == IResource.FILE) {
            final IPath path = ((IFile)resource).getLocation();
            if (path != null) {
                return isModelFile(path.toFile());
            }
        }
        return false;
    }

    /**
     * Return true if the File represents a MetaMatrix model file or an xsd file this method does not check if the file exists in
     * a project with model nature. Returns a false for vdb files.
     * 
     * @param resource The file that may be a model file
     * @return true if it is a ModelFile.
     */
    public static boolean isModelFile( final File resource ) {
        return ModelFileUtil.isModelFile(resource);
    }

    /**
     * Return true if the File represents a MetaMatrix model file, this method does not check if the file exists in a project with
     * model nature.
     * 
     * @param resource The file that may be a model file
     * @return true if it is a ModelFile.
     */
    public static boolean isModelFile( final Resource resource ) {
        if (resource == null) {
            return false;
        }

        final String extension = resource.getURI().fileExtension();
        return isModelFileExtension(extension, true);
    }

    public static boolean isModelFileExtension( final String extension,
                                                boolean caseSensitive ) {
        return ModelFileUtil.isModelFileExtension(extension, caseSensitive);
    }

    public static boolean isIResourceReadOnly( IResource iResource ) {
        ResourceAttributes attributes = iResource.getResourceAttributes();
        return attributes == null ? false : attributes.isReadOnly();
    }

    public static boolean setIResourceReadOnly( IResource iResource,
                                                boolean isReadOnly ) {
        ResourceAttributes attributes = iResource.getResourceAttributes();
        if (attributes == null) {
            return false;
        }
        attributes.setReadOnly(isReadOnly);
        return true;
    }

    /**
     * Return the XMIHeader for the specified inputstream of a model file.
     * 
     * @param resourceStream The inputStream of a metamatrix model file.
     * @return The XMIHeader for the model file
     */
    public static XMIHeader getXmiHeader( final InputStream resourceStream ) {
        return ModelFileUtil.getXmiHeader(resourceStream);
    }

    /**
     * Return the XMIHeader for the specified File or null if the file does not represent a MetaMatrix model file.
     * 
     * @param resource The file of a metamatrix model file.
     * @return The XMIHeader for the model file
     */
    public static XMIHeader getXmiHeader( final File resource ) {
        return ModelFileUtil.getXmiHeader(resource);
    }

    /**
     * Return the XMIHeader for the specified IResource or null if the file does not represent a MetaMatrix model file.
     * 
     * @param resource The IResource of a metamatrix model file.
     * @return The XMIHeader for the model file
     */
    public static XMIHeader getXmiHeader( final IResource resource ) {
        if (resource != null && resource.getType() == IResource.FILE) {
            final IPath path = ((IFile)resource).getLocation();
            if (path != null) {
                return ModelFileUtil.getXmiHeader(path.toFile());
            }
        }
        return null;
    }

    /**
     * Return the XMIHeader for the given vdb file or null if the file does not represent a vdb.
     * 
     * @param vdbArchiveJar The file for the vdb.
     * @return The XMIHeader for the vdb manifest file
     */
    public static XMIHeader getXmiHeaderForVdbArchive( final File vdbArchiveJar ) {
        return ModelFileUtil.getXmiHeaderForVdbArchive(vdbArchiveJar);
    }

    /**
     * Return the VdbHeader for the specified vdb file or null if the file does not represent a vdb.
     * 
     * @param resource The file of a metamatrix vdb file.
     * @return The VdbHeader for the model file
     */
    public static VdbHeader getVdbHeader( final File resource ) {
        if (resource != null && resource.isFile() && resource.exists()) {
            if (isVdbArchiveFile(resource)) {
                try {
                    return VdbHeaderReader.readHeader(resource);
                } catch (MetaMatrixCoreException e) {
                    ModelerCore.Util.log(e);
                }
            }
        }
        return null;
    }

    /**
     * Return the XsdHeader for the specified xsd file or null if the file does not represent a XSD.
     * 
     * @param resource The file of a metamatrix xsd file.
     * @return The XsdHeader for the model file
     */
    public static XsdHeader getXsdHeader( final File resource ) {
        if (resource != null && resource.isFile() && resource.exists()) {
            if (isXsdFile(resource)) {
                try {
                    return XsdHeaderReader.readHeader(resource);
                } catch (MetaMatrixCoreException e) {
                    ModelerCore.Util.log(e);
                }
            }
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
            if (path != null) {
                return getXsdHeader(path.toFile());
            }
        }
        return null;
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
     * Return true if the IResource represents a xsd file.
     * 
     * @param resource The file that may be a xsd file
     * @return true if it is a xsd
     */
    public static boolean isXsdFile( final File resource ) {
        return ModelFileUtil.isXsdFile(resource);
    }

    /**
     * Return true if the Resource represents a xsd file.
     * 
     * @param resource The file that may be a xsd file
     * @return true if it is a xsd
     */
    public static boolean isXsdFile( final Resource resource ) {
        if (resource != null) {
            if (resource instanceof XSDResourceImpl) {
                return true;
            }
            // Check that the resource has the correct lower-case extension
            final URI uri = resource.getURI();
            if (uri != null) {
                final String fileName = uri.lastSegment();
                if (fileName.endsWith(ModelFileUtil.EXTENSION_XSD)) {
                    return true;
                }
            }
        }
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
        if (ModelFileUtil.EXTENSION_XSD.equals(resource.getFileExtension())) {
            return true;
        }
        return false;
    }

    /**
     * Return true if the IPath represents a xsd file.
     * 
     * @param path The path to a file that may be a xsd file
     * @return true if it is a xsd
     */
    public static boolean isXsdFile( final IPath path ) {
        // Check that the resource has the correct lower-case extension
        if (ModelFileUtil.EXTENSION_XSD.equals(path.getFileExtension())) {
            return true;
        }
        return false;
    }

    /**
     * Return true if the IResource represents a MetaMatrix xmi model file.
     * 
     * @param resource The file that may be a MetaMatrix xmi model file
     * @return true if it is a MetaMatrix xmi model
     */
    public static boolean isXmiFile( final File resource ) {
        // Check that the resource has the correct lower-case extension
        if (ModelFileUtil.EXTENSION_XMI.equals(ModelFileUtil.getFileExtension(resource))) {

            // If the file does not yet exist then the only thing
            // we can do is to check the name and extension.
            if (resource != null && !resource.exists()) {
                return true;
            }

            XMIHeader header = ModelFileUtil.getXmiHeader(resource);
            // If the header is not null then we know the file is, at least,
            // a well formed xml document.
            if (header != null) {
                // If the XMI version for the header is not null, then return
                // false if the file represents an older 1.X model file
                if (header.getXmiVersion() != null && header.getXmiVersion().startsWith("1.")) { //$NON-NLS-1$
                    return false;
                }
                // If the UUID for the header is not null, then the file is a
                // MetaMatrix model file containing a ModelAnnotation element.
                if (header.getUUID() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return true if the Resource represents a MetaMatrix xmi model file.
     * 
     * @param resource The file that may be a MetaMatrix xmi model file
     * @return true if it is a MetaMatrix xmi model
     */
    public static boolean isXmiFile( final Resource resource ) {
        if (resource != null) {
            if (resource instanceof EmfResource) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if the IResource represents a MetaMatrix xmi model file.
     * 
     * @param resource The file that may be a MetaMatrix xmi model file
     * @return true if it is a MetaMatrix xmi model
     */
    public static boolean isXmiFile( final IResource resource ) {
        // Check that the resource has the correct lower-case extension
        if (ModelFileUtil.EXTENSION_XMI.equals(resource.getFileExtension())) {
            XMIHeader header = getXmiHeader(resource);
            // If the header is not null then we know the file is, at least,
            // a well formed xml document.
            if (header != null) {
                // If the XMI version for the header is not null, then return
                // false if the file represents an older 1.X model file
                if (header.getXmiVersion() != null && header.getXmiVersion().startsWith("1.")) { //$NON-NLS-1$
                    return false;
                }
                // If the UUID for the header is not null, then the file is a
                // MetaMatrix model file containing a ModelAnnotation element.
                if (header.getUUID() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method that determines that the given resource is a IFile and exists in a modeling project.
     */
    private static boolean isModelProjectResource( final IResource resource ) {
        if (resource != null) {
            IProject proj = resource.getProject();
            if (proj != null && ModelerCore.hasModelNature(proj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if the IResource represents a MetaMatrix vdb file.
     * 
     * @param resource The file that may be a vdb file
     * @return true if it is a vdb File.
     */
    public static boolean isVdbArchiveFile( final IResource resource ) {
        // Check that the resource has the correct lower-case extension
        if (ModelFileUtil.EXTENSION_VDB.equals(resource.getFileExtension())) {
            return true;
        }
        return false;
    }

    /**
     * @since 4.0
     */
    public static boolean isVdbArchiveFile( final IPath path ) {
        // Check that the resource has the correct lower-case extension
        if (path != null && path.getFileExtension() != null) {
            if (ModelFileUtil.EXTENSION_VDB.equals(path.getFileExtension())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @since 4.0
     */
    public static boolean isVdbArchiveFile( final File resource ) {
        return ModelFileUtil.isVdbArchiveFile(resource);
    }

    /**
     * Return true if the Resource represents a vdb archive file.
     * 
     * @param resource The file that may be a vdb file
     * @return true if it is a xsd
     */
    public static boolean isVdbArchiveFile( final Resource resource ) {
        if (resource != null) {
            if (resource instanceof VdbResourceImpl) {
                return true;
            }
            // Check that the resource has the correct lower-case extension
            final URI uri = resource.getURI();
            if (uri != null) {
                final String fileName = uri.lastSegment();
                if (fileName.endsWith(ModelFileUtil.EXTENSION_VDB)) {
                    return true;
                }
            }
        }
        return false;
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
     * Return a java.io.InputStream reference for the MetaMatrix-VdbManifestModel.xmi model file contained within the Vdb archive.
     * If the specified file is not a Vdb archive file or the archive does not contain a manifest model then null is returned.
     * 
     * @param zip the Vdb archive
     * @return the inputstream for the manifest file entry
     */
    public static InputStream getManifestModelContentsFromVdbArchive( final ZipFile zipFile ) {
        return ModelFileUtil.getManifestModelContentsFromVdbArchive(zipFile);
    }

    /**
     * Return a java.io.InputStream reference for the specified zip entry name
     * 
     * @param zip
     * @param zipEntryName the fully qualified name of the zip entry
     * @return the inputstream for the zipfile entry
     */
    public static InputStream getFileContentsFromArchive( final ZipFile zipFile,
                                                          final String zipEntryName ) {
        return ModelFileUtil.getFileContentsFromArchive(zipFile, zipEntryName);
    }

    /**
     * @since 4.0
     */
    private static IStatus newErrorStatus( final String msg ) {
        return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, -1, msg, null);
    }

    /**
     * @since 4.0
     */
    public static IStatus validateFolderName( final String name ) {
        if (name == null) {
            return newErrorStatus(ModelerCore.Util.getString("ModelUtil.folder_must_have_a_non-null_name")); //$NON-NLS-1$
        }
        if (name.trim().length() == 0) {
            return newErrorStatus(ModelerCore.Util.getString("ModelUtil.folder_must_have_a_non-empty_name")); //$NON-NLS-1$
        }

        // Ensure the file name is a valid file name
        return validateName(name, IResource.FOLDER);
    }

    /**
     * @since 4.0
     */
    public static IStatus validateModelFileName( final String name ) {
        if (name == null) {
            return newErrorStatus(ModelerCore.Util.getString("ModelUtil.model_file_name_may_not_be_null")); //$NON-NLS-1$
        }
        if (name.trim().length() == 0) {
            return newErrorStatus(ModelerCore.Util.getString("ModelUtil.model_file_name_may_not_be_zero_length")); //$NON-NLS-1$
        }
        boolean validExtension = false;
        // Try exact match (likely)
        for (int i = 0; i < EXTENSIONS.length; i++) {
            if (name.endsWith(EXTENSIONS[i])) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            return newErrorStatus(ModelerCore.Util.getString("ModelUtil.model_file_name_does_not_have_a_valid_extension")); //$NON-NLS-1$
        }

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
                                         int type ) {

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

    // Refactored/moved from ModelUtilities in modeler.ui

    /**
     * Get a ModelResource for a model file.
     * 
     * @param modelFile
     * @param forceOpen true if the ModelResource should open in responce to this call, false if it is okay to lazily open the
     *        resource.
     * @return
     * @throws CoreException
     */
    public static ModelResource getModelResource( IFile modelFile,
                                                  boolean forceOpen ) throws ModelWorkspaceException {
        if (modelFile == null) {
            return null;
        }
        return ModelerCore.getModelEditor().findModelResource(modelFile);
    }

    /**
     * Method returns a boolean value (true or false) for whether or not a IFile requires validation. The model may be have been
     * saved with auto-build off.
     * 
     * @param targetModelResource
     * @return true if requires validation, false if not.
     * @since 4.2
     */
    public static boolean requiresValidation( IFile file ) {
        ModelResource mr = null;

        // Find Model Resource
        try {
            mr = getModelResource(file, false);
        } catch (ModelWorkspaceException err) {
            String message = getString(Constants.MODEL_RESOURCE_NOT_FOUND_MSG_KEY, file.toString());
            ModelerCore.Util.log(IStatus.ERROR, err, message);
        }

        if (mr != null) {
            return requiresValidation(mr);
        }

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
    public static boolean requiresValidation( ModelResource targetModelResource ) {
        if (targetModelResource == null) return false;

        // TODO: (BML 12/14/04) This check is currently required because XSD files are always tagged with a NOT_INDEXED during
        // the build process because they need to be unloaded and reloaded. Sucks, but that's the way it is.
        // There will be another defect defining that and pointing to this place to remove the next two lines!!
        if (ModelUtil.isXsdFile(targetModelResource.getResource())) return false;

        // sz - added the code to fix defect 15948.
        boolean isIndexModified = isIndexFileLastModifiedAfterResourceFile(targetModelResource);
        if ((targetModelResource.getIndexType() == ModelResource.NOT_INDEXED) || isIndexModified) {
            return true;
        }

        return false;
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
            boolean deresolve = (baseResourceURI != null && !baseResourceURI.isRelative() && baseResourceURI.isHierarchical());
            if (deresolve && !importedResourceURI.isRelative()) {
                URI deresolvedURI = importedResourceURI.deresolve(baseResourceURI, true, true, false);
                if (deresolvedURI.hasRelativePath()) {
                    uri = deresolvedURI;
                }
            }
        }
        return uri;
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
            String[] pathSegments = baseResource.getURI().segments();
            String baseURI = baseResource.getURI().toString();
            String fileSep = FileSeparatorUtil.getFileSeparator(baseURI);
            String deviceLocation = StringUtil.Constants.EMPTY_STRING;
            // find vdbWorkingFolder index
            int vdbFolderIndex = -1;
            IPath projectPath = null;
            for (int i = 0; i < pathSegments.length; i++) {
                if (projectPath == null) {
                    projectPath = new Path(pathSegments[i]);
                    int index = baseURI.indexOf(pathSegments[i]);
                    // Defect 24918 - Platform may be LINUX/UNIX so the device location may NOT always be available.
                    // So, we do a quick check, and if NOT WINDOWS, just set the default "root" to '/'
                    if (Platform.getOS().equals(Platform.OS_WIN32)) {
                        deviceLocation = baseURI.substring(0, index - 1);
                        if (deviceLocation.startsWith(ModelFileUtil.FILE_COLON)) {
                            deviceLocation = deviceLocation.substring(6) + fileSep;
                        }
                    } else deviceLocation = FileSeparatorUtil.FILE_SEPARATOR_LINUX;
                } else {
                    projectPath = projectPath.append(pathSegments[i]);
                }
                if (pathSegments[i].equalsIgnoreCase(ResourceFinder.VDB_WORKING_FOLDER)) {
                    vdbFolderIndex = i;
                    break;
                }
            }
            if (vdbFolderIndex > -1) {
                // increment the index to get to the temp-directory
                projectPath = projectPath.append(pathSegments[vdbFolderIndex + 1]);
                // Now
            }
            IPath finalPath = new Path(deviceLocation).append(projectPath);
            return finalPath;
        }
        return null;
    }

    public static String getRelativePath( IPath source,
                                          IPath base ) {
        StringBuffer upPath = new StringBuffer();
        int baseSegments = base.segmentCount();
        int matchingSegments = source.matchingFirstSegments(base);
        int upSegments = baseSegments - matchingSegments;
        String fileSep = FileSeparatorUtil.getFileSeparator(source.toString());
        if (base.getFileExtension() != null) {
            upSegments--;
        }
        if (upSegments > 0) {
            for (int i = 0; i < upSegments; i++) {
                upPath.append(".." + fileSep);//$NON-NLS-1$
            }
        }
        IPath sourceRelativePath = source.removeFirstSegments(matchingSegments).makeRelative();
        return upPath + sourceRelativePath.toString();
    }

    public static void setModelWorkspaceManagerInitialized() {
        ModelFileUtil.setCache(ModelWorkspaceManager.getModelWorkspaceManager());
    }

    /**
     * Return the virtual model state of the specified model object.
     * 
     * @param eObject
     * @return true if model object is in virtual model.
     */
    public static boolean isVirtual( Object obj ) {
        if (obj != null && obj instanceof EObject) {
            EObject eObject = (EObject)obj;
            final Resource resource = eObject.eResource();
            if (resource instanceof EmfResource) {
                return ModelType.VIRTUAL_LITERAL.equals(((EmfResource)resource).getModelAnnotation().getModelType());
            } else if (resource == null && eObject.eIsProxy()) {
                URI theUri = ((InternalEObject)eObject).eProxyURI().trimFragment();
                if (theUri.isFile()) {
                    File newFile = new File(theUri.toFileString());
                    XMIHeader header = ModelFileUtil.getXmiHeader(newFile);
                    if (header != null && ModelType.VIRTUAL_LITERAL.equals(ModelType.get(header.getModelType()))) return true;
                }
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
    public static boolean isPhysical( Object obj ) {
        if (obj != null && obj instanceof EObject) {
            EObject eObject = (EObject)obj;
            final Resource resource = eObject.eResource();
            if (resource instanceof EmfResource) {
                return ModelType.PHYSICAL_LITERAL.equals(((EmfResource)resource).getModelAnnotation().getModelType());
            } else if (resource == null && eObject.eIsProxy()) {
                URI theUri = ((InternalEObject)eObject).eProxyURI().trimFragment();
                if (theUri.isFile()) {
                    File newFile = new File(theUri.toFileString());
                    XMIHeader header = ModelFileUtil.getXmiHeader(newFile);
                    if (header != null && ModelType.PHYSICAL_LITERAL.equals(ModelType.get(header.getModelType()))) return true;
                }
            }
        }
        return false;
    }

    private static boolean isIndexFileLastModifiedAfterResourceFile( ModelResource targetModelResource ) {

        File rsrcIndexFile = new File(IndexUtil.INDEX_PATH, IndexUtil.getRuntimeIndexFileName(targetModelResource));
        if (!rsrcIndexFile.exists()) {
            return false;
        }

        final IPath path = ((IFile)targetModelResource.getResource()).getLocation();
        long resourceLastModified = path.toFile().lastModified();
        long indexLastModified = rsrcIndexFile.lastModified();

        return (indexLastModified < resourceLastModified);
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
                                     String arg ) {
        return ModelerCore.Util.getString(I18N_PREFIX + id, arg);
    }

    /**
     * Prevents instantiation outside of this class.
     * 
     * @since 4.0
     */
    private ModelUtil() {
    }
}
