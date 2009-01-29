/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.jdom.Document;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.DateUtil;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.core.vdb.VdbConstants;
import com.metamatrix.internal.core.xml.JdomHelper;
import com.metamatrix.internal.core.xml.xmi.ModelImportInfo;
import com.metamatrix.internal.core.xml.xmi.XMIHeader;
import com.metamatrix.internal.core.xml.xmi.XMIHeaderReader;
import com.metamatrix.internal.core.xml.xsd.XsdHeader;
import com.metamatrix.internal.core.xml.xsd.XsdHeaderReader;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xsd.XsdResourceFactory;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelSourceAspect;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.internal.core.resource.EResourceFactory;
import com.metamatrix.modeler.internal.core.resource.EResourceSetImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.sdt.ModelerSdtPlugin;
import com.metamatrix.vdb.edit.VdbArtifactGenerator;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbContextValidator;
import com.metamatrix.vdb.edit.VdbContextValidatorResult;
import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbGenerationContext;
import com.metamatrix.vdb.edit.VdbGenerationContextFactory;
import com.metamatrix.vdb.edit.VdbGenerationContextParameters;
import com.metamatrix.vdb.edit.loader.VDBWriter;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ModelSourceProperty;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.ProblemMarker;
import com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/**
 * @since 5.0
 */
public class SharedWsVdbContextEditor extends VdbContextImpl implements VdbContextEditor {

    public static final int DEFAULT_VDB_FORM = VdbFileWriter.FORM_JAR;

    public static final int FILE_COPY_ERROR_CODE = 100101;
    public static final int CREATE_BACKUP_ERROR_CODE = 100102;
    public static final int CREATE_ARCHIVER_ERROR_CODE = 100103;
    public static final int ARCHIVE_ADD_ERROR_CODE = 100104;
    public static final int ARCHIVE_WRITE_ERROR_CODE = 100105;
    public static final int ARCHIVE_CLOSE_ERROR_CODE = 100106;
    public static final int RESTORE_BACKUP_ERROR_CODE = 100107;
    public static final int NO_MODELS_ERROR_CODE = 100108;
    public static final int FILE_WRITE_ERROR_CODE = 100109;
    public static final int CLOSE_STREAM_ERROR_CODE = 100110;
    public static final int ARTIFACT_GENERATOR_CANCEL_ERROR_CODE = 100111;
    public static final int ARTIFACT_GENERATOR_EXECUTE_ERROR_CODE = 100112;
    public static final int SAVEAS_COPY_CONTENTS_ERROR_CODE = 100113;
    public static final int SAVEAS_SAVE_CONTEXT_ERROR_CODE = 100114;
    public static final int SAVEAS_CLOSE_CONTEXT_ERROR_CODE = 100115;
    public static final int ERROR_LOADING_RESOURCE_ERROR_CODE = 100116;
    public static final int WARNING_REMOVING_MODEL_ERROR_CODE = 100117;
    public static final int WARNING_REMOVING_NONMODEL_ERROR_CODE = 100118;
    public static final int UPDATING_NONMODEL_ERROR_CODE = 100119;

    private static final String BACKUP_EXTENSION = ".BAK"; //$NON-NLS-1$

    private ResourceSet sharedContainer;
    private VdbGenerationContextFactory vdbGenerationContextFactory;
    private VdbContextValidator vdbContextValidator;
    private List generators;
    private int vdbArchiveForm;
    private boolean saveIsRequired;

    /**
     * @since 5.0
     */
    public SharedWsVdbContextEditor( final File theVdbFile,
                                     final File theVdbWorkingFolder,
                                     final ResourceSet theSharedContainer ) {
        super(theVdbFile, theVdbWorkingFolder);
        ArgCheck.isNotNull(theSharedContainer);
        this.sharedContainer = theSharedContainer;
        this.generators = new ArrayList();
        this.vdbArchiveForm = DEFAULT_VDB_FORM;
        this.saveIsRequired = false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContext#open(org.eclipse.core.runtime.IProgressMonitor, boolean)
     * @since 5.0
     */
    @Override
    public synchronized void open( final IProgressMonitor theMonitor,
                                   final boolean notify ) throws IOException {
        super.open(theMonitor, false);

        // If no progress monitor was specified then create a NullProgressMonitor so
        // that we do not have to check for null everywhere within this method
        final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);

        // Set the task name and total work for the progress monitor
        int totalFileCount = getVirtualDatabase().getModels().size();
        String taskName = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Loading_vdb_contents_into_container", getVdbFile().getName()); //$NON-NLS-1$
        monitor.beginTask(taskName, totalFileCount);
        int amountWorked = 0;

        final TempDirectory tempDir = getTempDirectory();

        // Load the models extracted from the VDB into the shared container
        final List problems = new ArrayList();
        final List modelRefs = getVirtualDatabase().getModels();
        for (final Iterator i = modelRefs.iterator(); i.hasNext();) {
            final ModelReference modelRef = (ModelReference)i.next();
            try {
                final String pathInTempDir = modelRef.getModelLocation();
                final File tempDirFile = getTempDirectoryFile(tempDir, pathInTempDir);
                if (tempDirFile != null && tempDirFile.exists()) {
                    URI fileUri = URI.createFileURI(tempDirFile.getAbsolutePath());
                    Resource eResource = getVdbResourceSet().getResource(fileUri, false);
                    if (eResource == null) {
                        eResource = getVdbResourceSet().createResource(fileUri);
                    }
                    if (!eResource.isLoaded()) {
                        eResource.load(getLoadOptions());
                    }
                } else {
                    final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Unable_to_find_file_in_temp_directory", pathInTempDir); //$NON-NLS-1$
                    problems.add(msg);
                }
                monitor.worked(amountWorked++);
            } catch (Exception e) {
                problems.add(e.getLocalizedMessage());
            }
        }

        // If any problems were encountered extracting the models throw an exception
        if (!problems.isEmpty()) {
            StringBuffer sb = new StringBuffer(2000);
            for (Iterator i = problems.iterator(); i.hasNext();) {
                sb.append((String)i.next());
                if (i.hasNext()) {
                    sb.append(StringUtil.Constants.NEW_LINE);
                }
            }
            throw new IOException(sb.toString());
        }

        // Notify all listeners about the change
        if (notify) {
            fireStateChanged();
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#addModel(org.eclipse.core.runtime.IProgressMonitor, java.io.File,
     *      java.lang.String, boolean)
     * @since 5.0
     */
    public ModelReference[] addModel( final IProgressMonitor theMonitor,
                                      final File theModel,
                                      final String pathInArchive,
                                      final boolean addDependentModels ) throws VdbEditException {
        return addModel(theMonitor, theModel, pathInArchive, addDependentModels, new ArrayList());
    }

    private ModelReference[] addModel( final IProgressMonitor theMonitor,
                                       final File theModel,
                                       final String pathInArchive,
                                       final boolean addDependentModels,
                                       final List modelsToBeAdded ) throws VdbEditException {
        ArgCheck.isNotNull(theModel);
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        assertContextIsOpen();

        // Check if a ModelReference for this path already exists ...
        ModelReference existing = getModelReference(pathInArchive);
        if (existing != null) {
            return new ModelReference[] {existing};
        }

        // Check that the specified file exists
        if (!theModel.exists()) {
            String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Model_cannot_be_found", theModel.getAbsolutePath()); //$NON-NLS-1$
            throw new VdbEditException(msg);
        }
        // Check that the specified file is a model
        if (!ModelUtil.isXmiFile(theModel) && !ModelUtil.isXsdFile(theModel)) {
            String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.File_is_not_model", theModel.getAbsolutePath()); //$NON-NLS-1$
            throw new VdbEditException(msg);
        }

        // Check if there is a ModelReference with this model name but different location.
        // We cannot allow two XMI models with the same name even if they have different paths because
        // the runtime metadata created for the server does not contain path information so we
        // cannot distinguish which models a table or procedure belongs to. (defects 17751, 19011, 226002)
        if (!ModelUtil.isXsdFile(theModel)) {
            List models = getVirtualDatabase().getModels();
            for (Iterator i = models.iterator(); i.hasNext();) {
                ModelReference modelRef = (ModelReference)i.next();
                if (theModel.getName().equalsIgnoreCase(modelRef.getName())
                    && !XSDPackage.eNS_URI.equals(modelRef.getPrimaryMetamodelUri())) {
                    Object[] params = new Object[] {theModel.getName(), modelRef.getModelLocation()};
                    String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Model_same_name_as_existing", params); //$NON-NLS-1$
                    throw new VdbEditException(msg);
                }
            }
        }

        // If no progress monitor was specified then create a NullProgressMonitor so
        // that we do not have to check for null everywhere within this method
        final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);

        // Set the task name and total work for the progress monitor
        String taskName = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Adding_model", pathInArchive); //$NON-NLS-1$
        monitor.setTaskName(taskName);
        monitor.worked(1);

        // Add this specified model to the list of all models being added
        if (!modelsToBeAdded.contains(theModel)) {
            modelsToBeAdded.add(theModel);
        }

        final List result = new ArrayList();

        // Add dependent models by reading the import declarations
        final File tempDirFolder = new File(getTempDirectory().getPath());
        if (addDependentModels) {
            final String[] importLocations = getImportLocations(theModel, true);
            for (int i = 0; i < importLocations.length; i++) {
                File importedModel = new File(importLocations[i]);
                if (importedModel.exists()) {

                    // If the imported model is already in the list of models being added, then skip it
                    if (modelsToBeAdded.contains(importedModel)) {
                        continue;
                    }
                    modelsToBeAdded.add(importedModel);

                    // Add the imported model to the VDB
                    String importedModelPathInArchive = getPathRelativeToFolder(tempDirFolder, importedModel);
                    ModelReference[] modelRefs = addModel(monitor,
                                                          importedModel,
                                                          importedModelPathInArchive,
                                                          addDependentModels,
                                                          modelsToBeAdded);
                    result.addAll(Arrays.asList(modelRefs));
                }
            }
        }

        // Create the ModelReference and add it to the manifest
        String normalizedPath = createNormalizedPath(pathInArchive).toString();
        ModelReference modelRef = createModelReference(theModel, normalizedPath);
        result.add(modelRef);

        // Ensure manifest resource is marked as modified
        getManifestResource().setModified(true);

        // notify listeners:
        fireStateChanged();

        return (ModelReference[])result.toArray(new ModelReference[result.size()]);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#addNonModel(org.eclipse.core.runtime.IProgressMonitor, java.io.File,
     *      java.lang.String)
     * @since 5.0
     */
    public NonModelReference addNonModel( final IProgressMonitor monitor,
                                          final File theNonModel,
                                          final String pathInArchive ) throws VdbEditException {
        ArgCheck.isNotNull(theNonModel);
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        assertContextIsOpen();

        // Check if a ModelReference for this path already exists ...
        NonModelReference existing = getNonModelReference(pathInArchive);
        if (existing != null) {
            return existing;
        }

        // Check that the specified file exists
        if (!theNonModel.exists()) {
            String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.NonModel_cannot_be_found", theNonModel.getAbsolutePath()); //$NON-NLS-1$
            throw new VdbEditException(msg);
        }

        // Create the NonModelReference and add it to the manifest
        String normalizedPath = createNormalizedPath(pathInArchive).toString();
        NonModelReference nonModelRef = createNonModelReference(theNonModel, normalizedPath);

        // Ensure manifest resource is marked as modified
        getManifestResource().setModified(true);

        // notify listeners context has changed
        fireStateChanged();

        return nonModelRef;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#removeModel(org.eclipse.core.runtime.IProgressMonitor,
     *      com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 5.0
     */
    public IStatus removeModel( final IProgressMonitor monitor,
                                final ModelReference theReference ) {
        ArgCheck.isNotNull(theReference);
        assertContextIsOpen();

        Status status = null;

        // Remove the NonModelReference from the manifest
        ModelReference modelRef = getModelReference(theReference.getModelLocation());
        if (modelRef != null) {
            getVirtualDatabase().getModels().remove(modelRef);

            // Ensure manifest resource is marked as modified
            getManifestResource().setModified(true);

            String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Removed_model", modelRef.getModelLocation()); //$NON-NLS-1$
            status = new Status(IStatus.OK, VdbEditPlugin.PLUGIN_ID, 0, msg, null);
        } else {
            String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Unable_to_find_modelReference", theReference.getModelLocation()); //$NON-NLS-1$
            status = new Status(IStatus.WARNING, VdbEditPlugin.PLUGIN_ID, WARNING_REMOVING_MODEL_ERROR_CODE, msg, null);
        }

        // notify listeners context has changed
        fireStateChanged();

        return status;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#removeNonModel(org.eclipse.core.runtime.IProgressMonitor,
     *      com.metamatrix.vdb.edit.manifest.NonModelReference)
     * @since 5.0
     */
    public IStatus removeNonModel( final IProgressMonitor monitor,
                                   final NonModelReference theReference ) {
        ArgCheck.isNotNull(theReference);
        assertContextIsOpen();

        Status status = null;

        // Remove the NonModelReference from the manifest
        NonModelReference nonModelRef = getNonModelReference(theReference.getPath());
        if (nonModelRef != null) {
            getVirtualDatabase().getNonModels().remove(nonModelRef);

            // Ensure manifest resource is marked as modified
            getManifestResource().setModified(true);

            String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Removed_nonModel", nonModelRef.getPath()); //$NON-NLS-1$
            status = new Status(IStatus.OK, VdbEditPlugin.PLUGIN_ID, 0, msg, null);
        } else {
            String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Unable_to_find_nonModelReference", theReference.getPath()); //$NON-NLS-1$
            status = new Status(IStatus.WARNING, VdbEditPlugin.PLUGIN_ID, WARNING_REMOVING_NONMODEL_ERROR_CODE, msg, null);
        }

        // notify listeners context has changed
        fireStateChanged();

        return status;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#setDescription(java.lang.String)
     * @since 5.0
     */
    public void setDescription( final String description ) {
        assertContextIsOpen();

        // Set the new description on the VirtualDatabase instance
        getVirtualDatabase().setDescription(description);

        // Ensure manifest resource is marked as modified
        getManifestResource().setModified(true);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#setSaveIsRequired()
     * @since 5.0
     */
    public void setSaveIsRequired() {
        this.saveIsRequired = true;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#isSaveRequired()
     * @since 5.0
     */
    public boolean isSaveRequired() {
        assertContextIsOpen();

        if (this.saveIsRequired) {
            return true;
        }
        if (getManifestResource().isModified()) {
            return true;
        }
        final TempDirectory sourceTempDir = getTempDirectory();
        final File sourceDirectory = new File(sourceTempDir.getPath());
        final File[] sourceFiles = findAllFilesInDirectoryRecursively(sourceDirectory);

        return requiresSynchronizeManifest(sourceDirectory, sourceFiles);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#save(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.0
     */
    public synchronized IStatus save( final IProgressMonitor theMonitor ) {
        assertContextIsOpen();

        // Create a list of problems resulting from the save operation
        final List problems = new ArrayList();

        // List of all files and contributed artifacts to write to the VDB
        final List artifactsToWrite = new ArrayList();

        // If no progress monitor was specified then create a NullProgressMonitor so
        // that we do not have to check for null everywhere within this method
        final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);

        // Get a list of all files within the source temp directory
        final TempDirectory sourceTempDir = getTempDirectory();
        final File sourceDirectory = new File(sourceTempDir.getPath());
        final File[] sourceFiles = findAllFilesInDirectoryRecursively(sourceDirectory);

        // Create a second temp directory to be used solely for the creation of the VDB file
        final File vdbWorkingFolder = sourceDirectory.getParentFile();
        final TempDirectory targetTempDir = createTempDirectory(vdbWorkingFolder);
        final File targetDirectory = new File(targetTempDir.getPath());

        // Set the task name and total work for the progress monitor
        int totalFileCount = 2 * (sourceFiles.length + 1);
        String taskName = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Saving", getVdbFile().getName()); //$NON-NLS-1$
        monitor.beginTask(taskName, totalFileCount);

        File sourceFile = null;
        try {
            for (int i = 0; i < sourceFiles.length; i++) {
                sourceFile = sourceFiles[i];
                // Update the properties in ConfigurationInfo.DEF that carry the VDB name
                if (VdbConstants.DEF_FILE_NAME.equals(sourceFile.getName())) {
                    final String vdbName = FileUtils.getFilenameWithoutExtension(getVdbFile().getName());
                    VDBWriter.updateConfigDefFile(sourceFile, vdbName, this.getExecutionProperties());
                    break;
                }
            }
        } catch (Exception e) {
            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_updating_in_scratch", sourceFile.getName()); //$NON-NLS-1$
            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, UPDATING_NONMODEL_ERROR_CODE, msg, e));
        }
        // -------------------------------------------------------------------------
        // Synchronize the VDB with the temp directory
        // -------------------------------------------------------------------------

        // Synchronize the contents of the temp directory with the manifest model
        synchronizeManifest(sourceDirectory, sourceFiles);

        // Synchronize the contents of the temp directory with the shared container
        synchronizeContainer(getVdbResourceSet(), sourceDirectory, sourceFiles);

        // -------------------------------------------------------------------------
        // Copy models/non-models to scratch directory for saving
        // -------------------------------------------------------------------------

        // Save any global/shared resources that are referenced by models in the VDB to the target
        // directory so that it can be added to the VDB
        saveGlobalResourcesInVdb(targetDirectory, getVdbResourceSet(), problems, artifactsToWrite);

        // Copy all the files from the source directory into the target directory
        try {
            for (int i = 0; i < sourceFiles.length; i++) {
                sourceFile = sourceFiles[i];

                // Create an Artifact for each file to be added to the VDB
                String pathInTempDir = getPathRelativeToFolder(sourceDirectory, sourceFile);
                IPath normalizedPath = createNormalizedPath(pathInTempDir);
                File targetFile = copyTempDirectoryEntryForSave(normalizedPath.toString(), sourceTempDir, targetTempDir);
                artifactsToWrite.add(new Artifact(normalizedPath, targetFile, 1));

                monitor.setTaskName(VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Copying_to_scratch", targetFile.getName())); //$NON-NLS-1$
                monitor.worked(1);
            }
        } catch (Exception e) {
            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_copying_to_scratch", sourceFile.getName()); //$NON-NLS-1$
            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, FILE_COPY_ERROR_CODE, msg, e));
        }

        // If any errors related to synchronzing the manifest model or populating the scratch
        // directory were encountered then return before attempting to create the VDB
        if (!problems.isEmpty()) {
            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_setting_up_scratch_directory"); //$NON-NLS-1$
            final IStatus result = createSingleIStatus(problems, msg);
            if (result.getSeverity() == IStatus.ERROR) {
                targetTempDir.remove();

                // Notify listeners or the save - whether or not it was successful.
                fireStateChanged();

                return result;
            }
            VdbEditPlugin.Util.log(result);
        }

        // -------------------------------------------------------------------------
        // Validate the models
        // -------------------------------------------------------------------------

        // Clear all problem markers prior to validation
        getVirtualDatabase().getMarkers().clear();
        for (Iterator i = getVirtualDatabase().getModels().iterator(); i.hasNext();) {
            ((ModelReference)i.next()).getMarkers().clear();
        }

        // Validate the models to be saved
        final List modelList = new ArrayList();
        modelList.add(getManifestResource());
        // Defect 22865 -
        modelList.addAll(getTempDirectoryResources(getVdbResourceSet()));
        final VdbContextValidatorResult validatorResult = getVdbContextValidator().validate(monitor,
                                                                                            (Resource[])modelList.toArray(new Resource[modelList.size()]));

        // Add problem markers from the validation to the VDB
        for (Iterator i = modelList.iterator(); i.hasNext();) {
            final Resource eResource = (Resource)i.next();
            IStatus[] validationProblems = validatorResult.getProblems(eResource);
            for (int j = 0; j < validationProblems.length; j++) {
                int severity = validationProblems[j].getSeverity();
                String msg = validationProblems[j].getMessage();
                Throwable e = validationProblems[j].getException();
                if (eResource == getManifestResource()) {
                    addProblemMarker(getVirtualDatabase(), severity, msg, e);
                } else if (eResource.getURI().isFile()) {
                    String pathInTempDir = getPathRelativeToFolder(sourceDirectory, new File(eResource.getURI().toFileString()));
                    ModelReference modelRef = getModelReference(pathInTempDir);
                    addProblemMarker(modelRef, severity, msg, e);
                }
            }
        }

        // Update the VirtualDatabase instance in the manifest model prior to writing to the target directory
        final Date currentDate = DateUtil.getCurrentDate();
        setVdbProblems(getVirtualDatabase(), problems);
        getVirtualDatabase().setName(FileUtils.getFilenameWithoutExtension(getVdbFile().getName()));
        getVirtualDatabase().setTimeLastChangedAsDate(currentDate);
        getVirtualDatabase().setTimeLastProducedAsDate(currentDate);

        // Save the manifest model, which only exists in memory and does not exist in the source directory,
        // to the target directory for so that it can be added to the VDB
        saveInternalResourceInVdb(targetDirectory,
                                  getManifestResource(),
                                  new Path(MANIFEST_MODEL_NAME),
                                  problems,
                                  artifactsToWrite);

        // -------------------------------------------------------------------------
        // Create backup copy of the VDB file
        // -------------------------------------------------------------------------

        // Make a backup copy of the VDB, if it exists, prior to overwriting it
        boolean fatalError = false;
        File backupVdbFile = null;
        if (getVdbFile().exists() && getVdbFile().length() > 0) {
            try {
                String fromFileName = getVdbFile().getAbsolutePath();
                String toFileName = fromFileName + BACKUP_EXTENSION;
                FileUtils.copy(fromFileName, toFileName, true);
                backupVdbFile = new File(toFileName);
            } catch (Exception e) {
                fatalError = true;

                // Notify listeners or the save - whether or not it was successful.
                fireStateChanged();

                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_writing_backup", getVdbFile().getName()); //$NON-NLS-1$
                return new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, CREATE_BACKUP_ERROR_CODE, msg, e);
            } finally {
                if (fatalError) {
                    targetTempDir.remove();
                    if (backupVdbFile != null && backupVdbFile.exists()) {
                        backupVdbFile.delete();
                    }
                }
            }
        }

        // -------------------------------------------------------------------------
        // Generate additional artifacts for the VDB
        // -------------------------------------------------------------------------

        // Generate additional Artifact instance through execution of the artifact generators
        problems.clear();
        generateAdditionalArtifacts(monitor,
                                    this.generators,
                                    sourceDirectory,
                                    targetDirectory,
                                    getVdbResourceSet(),
                                    problems,
                                    artifactsToWrite);

        // -------------------------------------------------------------------------
        // Write the new VDB archive file
        // -------------------------------------------------------------------------

        // Build up the specification for the archive ...
        IPath vdbFilePath = null;
        VdbFileWriter writer = null;
        try {
            vdbFilePath = new Path(getVdbFile().getAbsolutePath());
            writer = new VdbFileWriter(vdbFilePath, this.vdbArchiveForm);
        } catch (Exception e) {
            fatalError = true;
            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_creating_vdb_file_writer"); //$NON-NLS-1$

            // Need to notify because actions need to know this failed so enablement can stay in sync with VDB state. (i.e.
            // modified or not)
            fireStateChanged();

            return new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, CREATE_ARCHIVER_ERROR_CODE, msg, e);
        } finally {
            if (fatalError) {
                targetTempDir.remove();
                if (backupVdbFile != null && backupVdbFile.exists()) {
                    backupVdbFile.delete();
                }
            }
        }

        // Add all entries to the VdbFileWriter ...
        for (Iterator i = artifactsToWrite.iterator(); i.hasNext();) {
            final Artifact artifact = (Artifact)i.next();
            final IPath path = artifact.path;
            final File content = artifact.content;
            final int work = artifact.workToWrite;
            try {
                monitor.setTaskName(VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Adding_to_archive", content.getName())); //$NON-NLS-1$
                writer.addEntry(path, content);
            } catch (IOException e) {
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_adding_to_archive", path); //$NON-NLS-1$
                problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, ARCHIVE_ADD_ERROR_CODE, msg, e));
            } finally {
                monitor.worked(work);
            }
        }

        // Write the new archive file ...
        IStatus writeStatus = null;
        final String desc = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Writing_archive"); //$NON-NLS-1$
        try {
            writer.open();
            writeStatus = writer.write(monitor);
        } catch (Throwable e) {
            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_writing_vdb_file", getVdbFile().getName()); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, ARCHIVE_WRITE_ERROR_CODE, msg, e);
            problems.add(status);
            if (writeStatus == null) {
                writeStatus = status;
            }
        } finally {
            try {
                writer.close();
            } catch (Throwable e3) {
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_closing_writer", getVdbFile().getName()); //$NON-NLS-1$
                problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, ARCHIVE_CLOSE_ERROR_CODE, msg, null));
            }

            // If any errors related to writing the VDB file were encountered then restore the backup copy
            boolean saveBackup = false;
            if ((writeStatus.getSeverity() == IStatus.ERROR) && backupVdbFile != null && backupVdbFile.exists()) {
                try {
                    String toFileName = getVdbFile().getAbsolutePath();
                    String fromFileName = toFileName + BACKUP_EXTENSION;
                    FileUtils.copy(fromFileName, toFileName, true);
                } catch (Exception e) {
                    saveBackup = true;
                    final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_restoring_backup", getVdbFile().getName()); //$NON-NLS-1$
                    VdbEditPlugin.Util.log(IStatus.ERROR, e, msg);
                }
            }

            // Create a single status merging problems encountered by the artifact generators
            // with any errors related to writing the VDB file. The merge status must be
            // created after the backup file is removed otherwise errors produced by the
            // artifact generators would cause the backup VDB to overwrite the newly saved file
            IStatus problemStatus = createSingleIStatus(problems, desc);
            writeStatus = merge(writeStatus, problemStatus, desc);

            // Clean up scratch directory and backup copy of VDB
            targetTempDir.remove();
            if (!saveBackup && backupVdbFile != null && backupVdbFile.exists()) {
                backupVdbFile.delete();
            }

            // Reset the flag indicating that save is required
            this.saveIsRequired = false;
        }

        // Notify listeners of the save
        fireStateChanged();

        return writeStatus;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#saveAs(org.eclipse.core.runtime.IProgressMonitor, java.io.File)
     * @since 5.0
     */
    public synchronized IStatus saveAs( final IProgressMonitor monitor,
                                        final File newVdbFile ) throws VdbEditException {
        ArgCheck.isNotNull(newVdbFile);
        assertContextIsOpen();

        // If saving to the same VDB file then perform a normal save operation
        if (newVdbFile.equals(getVdbFile())) {
            return save(monitor);
        }
        // If the target VDB file already exists, then remove it before creating the new instance
        if (newVdbFile.exists()) {
            newVdbFile.delete();
        }

        // Create a temporary SharedWsVdbContextEditor instance to use during the save
        final File vdbWorkingFolder = getVdbWorkingFolder();
        SharedWsVdbContextEditor editor = new SharedWsVdbContextEditor(newVdbFile, vdbWorkingFolder, createSaveAsResourceSet());
        editor.setVdbContextValidator(new SharedWsVdbContextValidator());
        editor.addArtifactGenerator(new WsdlArtifactGenerator());
        editor.addArtifactGenerator(new RuntimeIndexArtifactGenerator());

        // Open the temporary editor
        try {
            editor.open(monitor);
        } catch (IOException e) {
            throw new VdbEditException(e);
        }

        // Get a list of all files within the source temp directory
        final TempDirectory sourceTempDir = getTempDirectory();
        final File sourceDirectory = new File(sourceTempDir.getPath());
        final File[] sourceFiles = sourceDirectory.listFiles();

        // Get the target locations for the temporary editor
        final TempDirectory targetTempDir = editor.getTempDirectory();
        final File targetDirectory = new File(targetTempDir.getPath());

        // If the archive paths in the original VBD are of the form "/<vdbName>/<modelName>" then
        // we need to change the paths to be consistent with the new VDB name. Search for a folder
        // under the TempDirectory with the same name as the VDB. If one is found, copy its contents
        // to a folder with the new VDB name and remove the old folder. Copy all other files/folders
        // from the original temp directory to the new temp directory
        final String origVdbName = FileUtils.getFilenameWithoutExtension(getVdbFile().getName());
        for (int i = 0; i < sourceFiles.length; i++) {
            try {
                if (sourceFiles[i].isDirectory() && origVdbName.equalsIgnoreCase(sourceFiles[i].getName())) {
                    String newVdbName = FileUtils.getFilenameWithoutExtension(newVdbFile.getName());
                    File targetFolder = new File(targetDirectory, newVdbName);
                    FileUtils.copyDirectoryContentsRecursively(sourceFiles[i], targetFolder);
                } else if (sourceFiles[i].isDirectory()) {
                    File targetFolder = new File(targetDirectory, sourceFiles[i].getName());
                    FileUtils.copyDirectoryContentsRecursively(sourceFiles[i], targetFolder);
                } else {
                    File sourceFile = new File(sourceDirectory, sourceFiles[i].getName());
                    File targetFile = new File(targetDirectory, sourceFiles[i].getName());
                    FileUtils.copy(sourceFile.getCanonicalPath(), targetFile.getCanonicalPath(), true);
                }
            } catch (Throwable e) {
                final Object[] params = new Object[] {sourceFiles[i], newVdbFile.getName()};
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_copying_contents_before_saving", params); //$NON-NLS-1$
                throw new VdbEditException(e, SAVEAS_COPY_CONTENTS_ERROR_CODE, msg);
            }
        }

        // Copy the WsdlOptions from the source to the target
        editor.getVirtualDatabase().setWsdlOptions(getVirtualDatabase().getWsdlOptions());

        // Save the temporary editor
        IStatus status = null;
        try {
            status = editor.save(monitor);
        } catch (Throwable e) {
            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_saving_temp_context", newVdbFile.getName()); //$NON-NLS-1$
            throw new VdbEditException(e, SAVEAS_SAVE_CONTEXT_ERROR_CODE, msg);
        } finally {
            try {
                editor.close(monitor, false, false);
            } catch (Throwable e) {
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_closing_temp_context", newVdbFile.getName()); //$NON-NLS-1$
                throw new VdbEditException(e, SAVEAS_CLOSE_CONTEXT_ERROR_CODE, msg);
            }
            editor.dispose();
        }
        return status;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#setVdbContextValidator(com.metamatrix.vdb.edit.VdbContextValidator)
     * @since 5.0
     */
    public void setVdbContextValidator( final VdbContextValidator theValidator ) {
        ArgCheck.isNotNull(theValidator);
        this.vdbContextValidator = theValidator;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#getVdbResourceSet()
     * @since 5.0
     */
    public ResourceSet getVdbResourceSet() {
        return this.sharedContainer;
    }

    // Defect 22865 - need a consistent way to access only those resources residing in the temp directory. We don't care about
    // FunctionDefinitions.xmi and any other model/resource in the config directory.....
    private List getTempDirectoryResources( ResourceSet vdbResourceSet ) {
        final List fullModelList = new ArrayList(vdbResourceSet.getResources());

        // Now let's create another list and remove the FunctionDefinitions.xmi file if there.
        List tempDirResList = new ArrayList(fullModelList.size());
        String tempDirFolderName = new File(getTempDirectory().getPath()).getAbsolutePath();

        for (Iterator iter = fullModelList.iterator(); iter.hasNext();) {
            Resource nextRes = (Resource)iter.next();
            URI uri = nextRes.getURI();
            String temp = ((uri.isFile()) ? uri.toFileString() : toString());

            if (temp.startsWith(tempDirFolderName)) {
                tempDirResList.add(nextRes);
            }
        }

        if (tempDirResList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return tempDirResList;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#addArtifactGenerator(com.metamatrix.vdb.edit.VdbArtifactGenerator)
     * @since 5.0
     */
    public void addArtifactGenerator( final VdbArtifactGenerator theGenerator ) {
        if (theGenerator != null) {
            this.generators.add(theGenerator);
        }
    }

    /**
     * @see com.metamatrix.vdb.internal.edit.VdbContextImpl#close(org.eclipse.core.runtime.IProgressMonitor, boolean, boolean)
     * @since 5.0
     */
    @Override
    public synchronized void close( final IProgressMonitor theMonitor,
                                    final boolean notify,
                                    final boolean vetoable ) {

        if (isOpen()) {
            if (!vetoable || fireVetoableChange(CLOSING_EVENT_NAME, OPENED_EVENT_NAME, CLOSED_EVENT_NAME)) {
                // no one vetoed, proceed:

                // If no progress monitor was specified then create a NullProgressMonitor so
                // that we do not have to check for null everywhere within this method
                final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);

                // Make a copy of the resources in the shared container
                List eResources = getTempDirectoryResources(getVdbResourceSet());
                // Set the task name and total work for the progress monitor
                String taskName = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Closing_context", getVdbFile().getName()); //$NON-NLS-1$
                monitor.beginTask(taskName, eResources.size());
                int amountWorked = 0;

                try {
                    for (Iterator i = eResources.iterator(); i.hasNext();) {
                        final Resource eResource = (Resource)i.next();

                        // Unload the resource before removing from the shared container
                        if (eResource.isLoaded()) {
                            eResource.unload();
                        }

                        // Remove it from the shared resource set
                        getVdbResourceSet().getResources().remove(eResource);
                        monitor.worked(amountWorked++);

                    }

                } finally {
                    super.close(monitor, false, vetoable);

                    // Notify all listeners about the change
                    if (notify) {
                        fireStateChanged();
                    }
                }
            } // endif -- veto
        }
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbContextEditor#dispose()
     * @since 5.0
     */
    @Override
    public synchronized void dispose() {
        try {
            // First ensure that the context is closed
            close(null, false, false);

            if (this.generators != null) {
                this.generators.clear();
            }
            super.dispose();

        } catch (Exception e) {
            String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_disposing_of_vdbContextEditor"); //$NON-NLS-1$
            VdbEditPlugin.Util.log(IStatus.ERROR, e, msg);
        } finally {
            this.generators = null;
            this.sharedContainer = null;
            this.vdbContextValidator = null;
            this.vdbGenerationContextFactory = null;
        }
    }

    public void setExecutionProperty( String propertyName,
                                      String propertyValue ) {
        this.getExecutionProperties().setProperty(propertyName, propertyValue);
    }

    public VdbGenerationContextFactory getVdbGenerationContextFactory() {
        if (this.vdbGenerationContextFactory == null) {
            this.vdbGenerationContextFactory = new VdbGenerationContextFactoryImpl();
        }
        return this.vdbGenerationContextFactory;
    }

    public void setVdbGenerationContextFactory( final VdbGenerationContextFactory theVdbGenerationContextFactory ) {
        ArgCheck.isNotNull(theVdbGenerationContextFactory);
        this.vdbGenerationContextFactory = theVdbGenerationContextFactory;
    }

    public VdbContextValidator getVdbContextValidator() {
        if (this.vdbContextValidator == null) {
            this.vdbContextValidator = new NullVdbContextValidator();
        }
        return this.vdbContextValidator;
    }

    protected File[] findAllFilesInDirectoryRecursively( final File directory ) {
        final File[] files = FileUtils.findAllFilesInDirectoryRecursively(directory.getAbsolutePath());
        final List result = new ArrayList(Arrays.asList(files));
        for (Iterator i = result.iterator(); i.hasNext();) {
            File f = (File)i.next();
            // Filter out hidden files (e.g. ".project")
            if (f.getName().startsWith(".")) { //$NON-NLS-1$
                i.remove();
            }
        }
        return (File[])result.toArray(new File[result.size()]);
    }

    protected ResourceSet createSaveAsResourceSet() {
        EResourceSetImpl rs = new EResourceSetImpl();
        Map map = rs.getResourceFactoryRegistry().getExtensionToFactoryMap();
        if (!map.containsKey(ModelUtil.EXTENSION_XMI)) {
            map.put(ModelUtil.EXTENSION_XMI, new EResourceFactory());
        }

        if (!map.containsKey(ModelUtil.EXTENSION_XSD)) {
            map.put(ModelUtil.EXTENSION_XSD, new XsdResourceFactory());
        }

        // Add the XSD global resources set as an external resource set
        rs.addExternalResourceSet(XSDSchemaImpl.getGlobalResourceSet());

        // Add the built-in datatypes global resource set as an external resource set
        rs.addExternalResourceSet(ModelerSdtPlugin.getGlobalResourceSet());

        return rs;
    }

    protected void addProblemMarker( final ModelReference markerCntr,
                                     final int severity,
                                     final String msg,
                                     final Throwable e ) {
        ArgCheck.isNotNull(markerCntr);
        createProblem(markerCntr, severity, msg, e);
    }

    protected void addProblemMarker( final VirtualDatabase markerCntr,
                                     final int severity,
                                     final String msg,
                                     final Throwable e ) {
        ArgCheck.isNotNull(markerCntr);
        createProblem(markerCntr, severity, msg, e);
    }

    protected void createProblem( final ProblemMarkerContainer theMarkerContainer,
                                  final int severity,
                                  final String msg,
                                  final Throwable t ) {

        final ProblemMarker marker = ManifestFactory.eINSTANCE.createProblemMarker();
        switch (severity) {
            case IStatus.ERROR:
                marker.setSeverity(Severity.ERROR_LITERAL);
                break;
            case IStatus.WARNING:
                marker.setSeverity(Severity.WARNING_LITERAL);
                break;
            case IStatus.INFO:
                marker.setSeverity(Severity.INFO_LITERAL);
                break;
            case IStatus.OK:
                marker.setSeverity(Severity.OK_LITERAL);
                break;
        }
        if (theMarkerContainer instanceof VirtualDatabase && ((VirtualDatabase)theMarkerContainer).getName() != null) {
            marker.setTarget(((VirtualDatabase)theMarkerContainer).getName());

        } else if (theMarkerContainer instanceof ModelReference
                   && ((ModelReference)theMarkerContainer).getModelLocation() != null) {
            marker.setTarget(((ModelReference)theMarkerContainer).getModelLocation());
        }
        marker.setMessage(msg);
        if (t != null) {
            marker.setStackTrace(StringUtil.getStackTrace(t));
        }
        marker.setMarked(theMarkerContainer);
    }

    protected void setVdbProblems( final VirtualDatabase vdb,
                                   final List statuses ) {

        // Set the ProblemMarkers on the VirtualDatabase instance
        for (Iterator i = statuses.iterator(); i.hasNext();) {
            IStatus status = (IStatus)i.next();
            addVdbStatus(vdb, status);
        }

        // Add a warning to the VirtualDatabase if there are no models
        if (vdb.getModels().isEmpty()) {
            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Vdb_has_no_models"); //$NON-NLS-1$

            // Check existing markers for "The VDB has no models" warning
            boolean foundNoModelsWarning = false;
            for (Iterator i = vdb.getMarkers().iterator(); i.hasNext();) {
                ProblemMarker marker = (ProblemMarker)i.next();
                if (marker.getMessage().equals(msg)) {
                    foundNoModelsWarning = true;
                    break;
                }
            }
            // Add the warning if it did not already exist then add it
            if (!foundNoModelsWarning) {
                createProblem(vdb, IStatus.WARNING, msg, null);
            }
        }
    }

    protected void addVdbStatus( final VirtualDatabase vdb,
                                 final IStatus status ) {
        if (status == null) {
            return;
        }
        if (status.isMultiStatus()) {
            IStatus[] statuses = status.getChildren();
            for (int i = 0; i < statuses.length; i++) {
                addVdbStatus(vdb, statuses[i]);
            }
        } else if (status.getSeverity() == IStatus.WARNING) {
            createProblem(vdb, status.getSeverity(), status.getMessage(), status.getException());
        } else if (status.getSeverity() == IStatus.ERROR) {
            createProblem(vdb, status.getSeverity(), status.getMessage(), status.getException());
        }
    }

    protected IStatus merge( final IStatus status1,
                             final IStatus status2,
                             final String desc ) {
        ArgCheck.isNotNull(status1);

        if (status2 == null) {
            return status1;
        }
        if (status2 instanceof Status && status2.isOK()) {
            return status1;
        }
        // Otherwise merge status2 into status 1 ...
        final List statuses = new LinkedList();
        for (int i = 0; i != 2; ++i) {
            final IStatus xstatus = (i == 0 ? status2 : status1);
            // Add the model status information ...
            if (xstatus instanceof MultiStatus) {
                final IStatus[] xStatuses = ((MultiStatus)xstatus).getChildren();
                for (int j = 0; j < xStatuses.length; ++j) {
                    final IStatus status = xStatuses[j];
                    statuses.add(status);
                }
            } else {
                statuses.add(status2);
            }
        }
        final IStatus results = createSingleIStatus(statuses, desc);
        return results;
    }

    protected IStatus createSingleIStatus( final List problems,
                                           final String desc ) {
        ArgCheck.isNotNull(problems);
        ArgCheck.isNotNull(desc);

        // Put all of the problems into a single IStatus ...
        final String PLUGINID = VdbEditPlugin.PLUGIN_ID;
        IStatus resultStatus = null;
        if (problems.isEmpty()) {
            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Completed", desc); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.OK, PLUGINID, 0, msg, null);
            resultStatus = status;
        } else if (problems.size() == 1) {
            resultStatus = (IStatus)problems.get(0);
        } else {
            // There were problems, so determine whether there were warnings and errors ...
            int numErrors = 0;
            int numWarnings = 0;
            for (Iterator i = problems.iterator(); i.hasNext();) {
                final IStatus aStatus = (IStatus)i.next();
                if (aStatus.getSeverity() == IStatus.WARNING) {
                    ++numWarnings;
                } else if (aStatus.getSeverity() == IStatus.ERROR) {
                    ++numErrors;
                }
            }

            // Create the final status ...
            final IStatus[] statusArray = (IStatus[])problems.toArray(new IStatus[problems.size()]);
            if (numWarnings != 0 && numErrors == 0) {
                final Object[] params = new Object[] {desc, new Integer(numWarnings)};
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Completed_with_warnings", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, 0, statusArray, msg, null);
            } else if (numWarnings == 0 && numErrors != 0) {
                final Object[] params = new Object[] {desc, new Integer(numErrors)};
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Resulted_in_errors", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, 0, statusArray, msg, null);
            } else if (numWarnings != 0 && numErrors != 0) {
                final Object[] params = new Object[] {desc, new Integer(numWarnings), new Integer(numErrors)};
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Resulted_in_errors_and_warnings", params); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, 0, statusArray, msg, null);
            } else {
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Completed", desc); //$NON-NLS-1$
                resultStatus = new MultiStatus(PLUGINID, 0, statusArray, msg, null);
            }
        }
        return resultStatus;
    }

    protected void synchronizeContainer( final ResourceSet cntr,
                                         final File directory,
                                         final File[] files ) {
        ArgCheck.isNotNull(cntr);
        ArgCheck.isNotNull(directory);
        ArgCheck.isNotNull(files);
        assertContextIsOpen();

        // Create a set of the file URIs for the contents of the temp directory
        final Set fileUris = new HashSet(files.length);
        for (int i = 0; i < files.length; i++) {
            File fileInTempDir = files[i];
            if (ModelUtil.isXmiFile(fileInTempDir) || ModelUtil.isXsdFile(fileInTempDir)) {
                fileUris.add(URI.createFileURI(fileInTempDir.getAbsolutePath()));
            }
        }

        // Synchronize the contents of the temp directory with the resource list in the
        // shared container by first removing any resource instances that no longer
        // exist on the file system
        // Defect 22865 - changed call to getTempDirectoryResources()
        final List eResources = getTempDirectoryResources(cntr);
        for (Iterator i = eResources.iterator(); i.hasNext();) {
            URI eResourceUri = ((Resource)i.next()).getURI();
            if (!fileUris.contains(eResourceUri)) {
                // Resource no longer exists
                i.remove();
            } else {
                fileUris.remove(eResourceUri);
            }
        }

        // Synchronize the contents of the temp directory with the resource list in the
        // shared container by next creating resource instances for new files on the
        // file system
        for (Iterator i = fileUris.iterator(); i.hasNext();) {
            URI fileUri = (URI)i.next();
            if (cntr.getResource(fileUri, false) == null) {
                cntr.createResource(fileUri);
            }
        }
    }

    protected void synchronizeManifest( final File directory,
                                        final File[] files ) {
        ArgCheck.isNotNull(directory);
        ArgCheck.isNotNull(files);
        assertContextIsOpen();

        // Create a map of the relative path within the temp directory to the File instance
        final Map pathsInTempDir = new HashMap(files.length);
        for (int i = 0; i < files.length; i++) {
            String pathInTempDir = getPathRelativeToFolder(directory, files[i]);
            String normalizedPath = createNormalizedPath(pathInTempDir).toString();
            pathsInTempDir.put(normalizedPath, files[i]);
        }

        // Synchronize the contents of the temp directory with the manifest model by first
        // removing any ModelReference/NonModelReference instances that no longer exist on
        // the file system
        for (Iterator i = getVirtualDatabase().getModels().iterator(); i.hasNext();) {
            ModelReference modelRef = (ModelReference)i.next();
            String pathInArchive = createNormalizedPath(modelRef.getModelLocation()).toString();
            if (!pathsInTempDir.containsKey(pathInArchive)) {
                // ModelReference no longer exists
                i.remove();
            } else {
                // ModelReference exists so refresh the checkSum value
                long checkSum = getCheckSum((File)pathsInTempDir.get(pathInArchive));
                modelRef.setChecksum(checkSum);
                pathsInTempDir.remove(pathInArchive);
            }
        }
        for (Iterator i = getVirtualDatabase().getNonModels().iterator(); i.hasNext();) {
            NonModelReference nonModelRef = (NonModelReference)i.next();
            String pathInArchive = createNormalizedPath(nonModelRef.getPath()).toString();
            if (!pathsInTempDir.containsKey(pathInArchive)) {
                // NonModelReference no longer exists
                i.remove();
            } else {
                // NonModelReference exists so refresh the checkSum value
                long checkSum = getCheckSum((File)pathsInTempDir.get(pathInArchive));
                nonModelRef.setChecksum(checkSum);
                pathsInTempDir.remove(pathInArchive);
            }
        }

        // Synchronize the contents of the temp directory with the manifest model by next
        // creating any ModelReference/NonModelReference instances for new files on the
        // file system
        for (Iterator i = pathsInTempDir.entrySet().iterator(); i.hasNext();) {
            final Map.Entry entry = (Map.Entry)i.next();
            String pathInTempDir = (String)entry.getKey();
            File fileInTempDir = (File)entry.getValue();
            if (ModelUtil.isXmiFile(fileInTempDir) || ModelUtil.isXsdFile(fileInTempDir)) {
                ModelReference modelRef = getModelReference(pathInTempDir);
                if (modelRef == null) {
                    createModelReference(fileInTempDir, pathInTempDir);
                }
            } else {
                NonModelReference nonModelRef = getNonModelReference(pathInTempDir);
                if (nonModelRef == null) {
                    createNonModelReference(fileInTempDir, pathInTempDir);
                }
            }
        }
    }

    protected boolean requiresSynchronizeManifest( final File directory,
                                                   final File[] files ) {
        ArgCheck.isNotNull(directory);
        ArgCheck.isNotNull(files);
        assertContextIsOpen();

        // Create a map of the relative path within the temp directory to the File instance
        final Map pathsInTempDir = new HashMap(files.length);
        for (int i = 0; i < files.length; i++) {
            String pathInTempDir = getPathRelativeToFolder(directory, files[i]);
            String normalizedPath = createNormalizedPath(pathInTempDir).toString();
            pathsInTempDir.put(normalizedPath, files[i]);
        }

        // Compare the existing manifest model references against the contents of the temp
        // directory checking for ModelReference/NonModelReference instances that either no
        // longer exist on the file system or have changed
        for (Iterator i = getVirtualDatabase().getModels().iterator(); i.hasNext();) {
            ModelReference modelRef = (ModelReference)i.next();
            String pathInArchive = createNormalizedPath(modelRef.getModelLocation()).toString();
            if (!pathsInTempDir.containsKey(pathInArchive)) {
                return true;
            }
            long checkSum = getCheckSum((File)pathsInTempDir.get(pathInArchive));
            if (checkSum != modelRef.getChecksum()) {
                return true;
            }
            pathsInTempDir.remove(pathInArchive);
        }
        for (Iterator i = getVirtualDatabase().getNonModels().iterator(); i.hasNext();) {
            NonModelReference nonModelRef = (NonModelReference)i.next();
            String pathInArchive = createNormalizedPath(nonModelRef.getPath()).toString();
            if (!pathsInTempDir.containsKey(pathInArchive)) {
                return true;
            }
            long checkSum = getCheckSum((File)pathsInTempDir.get(pathInArchive));
            if (checkSum != nonModelRef.getChecksum()) {
                return true;
            }
            pathsInTempDir.remove(pathInArchive);
        }

        // Compare the manifest model references against the contents of the temp directory checking
        // for new files that have no corresponding ModelReference/NonModelReference instances
        for (Iterator iter = pathsInTempDir.entrySet().iterator(); iter.hasNext();) {
            final Map.Entry entry = (Map.Entry)iter.next();
            String pathInTempDir = (String)entry.getKey();
            File fileInTempDir = (File)entry.getValue();
            if (ModelUtil.isXmiFile(fileInTempDir) || ModelUtil.isXsdFile(fileInTempDir)) {
                ModelReference modelRef = getModelReference(pathInTempDir);
                if (modelRef == null) {
                    return true;
                }
            } else {
                NonModelReference nonModelRef = getNonModelReference(pathInTempDir);
                if (nonModelRef == null) {
                    return true;
                }
            }
        }
        return false;
    }

    protected ModelReference createModelReference( final File f,
                                                   final String pathInArchive ) {
        ArgCheck.isNotNull(f);
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);

        final ModelReference modelReference = ManifestFactory.eINSTANCE.createModelReference();
        modelReference.setModelLocation(pathInArchive);
        modelReference.setUri(null);
        modelReference.setVisible(true);
        modelReference.setTimeLastSynchronizedAsDate(DateUtil.getCurrentDate());
        modelReference.setName(f.getName());

        try {
            // If the Resource represents a model resource ...
            if (ModelUtil.isXmiFile(f)) {
                XMIHeader header = XMIHeaderReader.readHeader(f);
                if (header != null) {
                    if (header.getModelType() != null) {
                        modelReference.setModelType(ModelType.get(header.getModelType()));
                    }
                    if (header.getUUID() != null) {
                        modelReference.setUuid(header.getUUID());
                    }
                    if (header.getPrimaryMetamodelURI() != null) {
                        modelReference.setPrimaryMetamodelUri(header.getPrimaryMetamodelURI());
                    }
                    modelReference.setVisible(header.isVisible());
                }

                // Set the model source information on the reference
                try {
                    URI fileUri = URI.createFileURI(f.getAbsolutePath());
                    Resource eResource = getVdbResourceSet().getResource(fileUri, true);
                    Assertion.isNotNull(eResource);
                    Properties modelSourceProps = getModelSourceProperties(eResource);
                    if (modelSourceProps != null) {
                        final ModelSource modelSource = ManifestFactory.eINSTANCE.createModelSource();
                        modelReference.setModelSource(modelSource);
                        for (Iterator i = modelSourceProps.entrySet().iterator(); i.hasNext();) {
                            final Map.Entry entry = (Map.Entry)i.next();
                            final String name = (String)entry.getKey();
                            final String value = (String)entry.getValue();
                            if (name != null && value != null) {
                                ModelSourceProperty srcProp = ManifestFactory.eINSTANCE.createModelSourceProperty();
                                srcProp.setName(name);
                                srcProp.setValue(value);
                                srcProp.setSource(modelSource);
                            }
                        }
                    }
                } catch (Throwable e) {
                    String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_loading", f.getName()); //$NON-NLS-1$
                    VdbEditPlugin.Util.log(IStatus.ERROR, e, msg);
                }

                // If the Resource represents an XSD resource ...
            } else if (ModelUtil.isXsdFile(f)) {
                modelReference.setModelType(ModelType.TYPE_LITERAL);
                modelReference.setPrimaryMetamodelUri(XSDPackage.eNS_URI);

                // Else no ModelResource was found so the path must represent a
                // non-Federate Designer model file so store information about the
                // file in the ModelReference properties
            } else {
                modelReference.setModelType(ModelType.UNKNOWN_LITERAL);
            }
            modelReference.setVirtualDatabase(getVirtualDatabase());

            // Get checksum from file to later set on ModelReference
            if (f.exists()) {
                final long checkSum = getCheckSum(f);
                modelReference.setChecksum(checkSum);
            }

        } catch (Throwable e) {
            VdbEditPlugin.Util.log(e);
        }

        return modelReference;

    }

    protected Properties getModelSourceProperties( final Resource eResource ) {
        ArgCheck.isNotNull(eResource);

        // Check for model source information immediately under the model root
        for (Iterator iter = eResource.getContents().iterator(); iter.hasNext();) {
            EObject eObject = (EObject)iter.next();
            SqlAspect aspect = AspectManager.getSqlAspect(eObject);
            if (aspect != null && aspect instanceof SqlModelSourceAspect) {
                return ((SqlModelSourceAspect)aspect).getProperties(eObject);
            }
        }
        return null;
    }

    protected NonModelReference createNonModelReference( final File f,
                                                         final String pathInArchive ) {
        ArgCheck.isNotNull(f);
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);

        final NonModelReference nonModelRef = ManifestFactory.eINSTANCE.createNonModelReference();
        nonModelRef.setPath(pathInArchive);
        nonModelRef.setName(f.getName());
        nonModelRef.setChecksum(getCheckSum(f));
        nonModelRef.setVirtualDatabase(getVirtualDatabase());

        return nonModelRef;
    }

    protected void saveInternalResourceInVdb( final File targetDirectory,
                                              final Resource internalResource,
                                              final IPath pathInArchive,
                                              final List problems,
                                              final List artifactsToWrite ) {
        ArgCheck.isNotNull(targetDirectory);
        ArgCheck.isNotNull(internalResource);
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotNull(problems);
        ArgCheck.isNotNull(artifactsToWrite);
        assertContextIsOpen();

        OutputStream ostream = null;
        try {
            File targetFile = new File(targetDirectory, pathInArchive.toString());
            if (targetFile.exists()) {
                targetFile.delete();
            }
            ostream = new FileOutputStream(targetFile);
            internalResource.save(ostream, getLoadOptions());
            artifactsToWrite.add(new Artifact(pathInArchive, targetFile, 1));
        } catch (Exception e) {
            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_writing_to_scratch", pathInArchive); //$NON-NLS-1$
            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, FILE_WRITE_ERROR_CODE, msg, e));
        } finally {
            if (ostream != null) {
                try {
                    ostream.close();
                } catch (IOException ignored) {
                }
            }
        }

    }

    protected void saveGlobalResourcesInVdb( final File targetDirectory,
                                             final ResourceSet resourceSet,
                                             final List problems,
                                             final List artifactsToWrite ) {
        ArgCheck.isNotNull(targetDirectory);
        ArgCheck.isNotNull(resourceSet);
        ArgCheck.isNotNull(problems);
        ArgCheck.isNotNull(artifactsToWrite);
        assertContextIsOpen();

        // Defect 22865 - changed call to getTempDirectoryResources()
        final List eResources = getTempDirectoryResources(resourceSet);
        for (Iterator i = eResources.iterator(); i.hasNext();) {
            Resource eResource = (Resource)i.next();

            // If any model resource has a reference to the built-in datatypes resource
            // then save that resource to the scratch directory for archiving
            if (hasImportTo(eResource, DatatypeConstants.BUILTIN_DATATYPES_URI)) {
                URI uri = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI);
                Resource mmTypes = resourceSet.getResource(uri, false);

                // Save the built-in datatypes resource to the VDB
                if (mmTypes != null) {
                    IPath pathInArchive = new Path(DatatypeConstants.DATATYPES_MODEL_FILE_NAME);
                    saveInternalResourceInVdb(targetDirectory, mmTypes, pathInArchive, problems, artifactsToWrite);
                }
                break;
            }
        }
    }

    protected String[] getImportLocations( final File modelFile,
                                           boolean makeAbsolute ) {
        ArgCheck.isNotNull(modelFile);

        final List result = new ArrayList();
        if (modelFile != null && modelFile.exists()) {
            try {
                // Create a list of all schema directive or model import locations found in the resource
                if (ModelUtil.isXsdFile(modelFile)) {
                    XsdHeader header = XsdHeaderReader.readHeader(modelFile);
                    if (header != null) {
                        result.addAll(Arrays.asList(header.getImportSchemaLocations()));
                        result.addAll(Arrays.asList(header.getIncludeSchemaLocations()));
                    }
                } else if (ModelUtil.isXmiFile(modelFile)) {
                    File tempDirFolder = new File(getTempDirectory().getPath());
                    File[] modelFiles = findAllFilesInDirectoryRecursively(tempDirFolder);

                    XMIHeader header = XMIHeaderReader.readHeader(modelFile);
                    if (header != null) {
                        ModelImportInfo[] infos = header.getModelImportInfos();
                        for (int i = 0; i < infos.length; i++) {
                            String location = infos[i].getLocation();
                            String path = infos[i].getPath();
                            // If the ModelImport has a "modelLocation" value then it is a newer model
                            // file. The "modelLocation" value is the location of the imported model
                            // relative to the model file containing the ModelImport
                            if (!StringUtil.isEmpty(location)) {
                                result.add(location);

                                // If the ModelImport has a "modelLocation" value then it is an older model
                                // file. The "path" value is the workspace relative location of the imported
                                // file. It must be converted into a location of the imported model
                                // relative to the model file containing the ModelImport
                            } else if (!StringUtil.isEmpty(path)) {
                                if (path.startsWith("http")) { //$NON-NLS-1$
                                    result.add(path);
                                } else {
                                    for (int j = 0; j < modelFiles.length; j++) {
                                        final File f = modelFiles[j];
                                        if (f.getAbsolutePath().endsWith(path)) {
                                            URI modelUri = URI.createFileURI(modelFile.getAbsolutePath());
                                            URI importUri = URI.createFileURI(f.getAbsolutePath());
                                            URI deresolvedURI = importUri.deresolve(modelUri, true, true, false);
                                            if (deresolvedURI.hasRelativePath()) {
                                                importUri = deresolvedURI;
                                                result.add(URI.decode(importUri.toString()));
                                                break;
                                            }
                                        }

                                    }
                                } // else
                            }
                        } // for
                    }
                }
                // Make the locations absolute if requested
                if (makeAbsolute) {
                    final List tmp = new ArrayList(result.size());
                    for (Iterator i = result.iterator(); i.hasNext();) {
                        String location = (String)i.next();
                        URI baseUri = URI.createFileURI(modelFile.getAbsolutePath());
                        URI locationUri = URI.createURI(location);
                        if (baseUri.isHierarchical() && !baseUri.isRelative() && locationUri.isRelative()) {
                            locationUri = locationUri.resolve(baseUri);
                        }
                        String uriString = (locationUri.isFile() ? locationUri.toFileString() : URI.decode(locationUri.toString()));
                        tmp.add(uriString);
                    }
                    result.clear();
                    result.addAll(tmp);
                }
            } catch (Exception e) {
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_reading_header", modelFile.getName()); //$NON-NLS-1$
                VdbEditPlugin.Util.log(IStatus.ERROR, e, msg);
            }
        }
        return (String[])result.toArray(new String[result.size()]);
    }

    protected String[] getImportLocations( final Resource eResource,
                                           boolean makeAbsolute ) {
        ArgCheck.isNotNull(eResource);

        final List result = new ArrayList();
        if (eResource != null && eResource.getURI().isFile()) {
            final File file = new File(eResource.getURI().toFileString());
            return getImportLocations(file, makeAbsolute);
        }
        return (String[])result.toArray(new String[result.size()]);
    }

    protected boolean hasImportTo( final Resource eResource,
                                   final String theLocationToMatch ) {
        ArgCheck.isNotNull(eResource);
        ArgCheck.isNotNull(theLocationToMatch);

        // Check the schema directive or model import locations against the specified location for a match
        String[] locations = getImportLocations(eResource, false);
        for (int i = 0; i < locations.length; i++) {
            String location = locations[i];
            if (location.equalsIgnoreCase(theLocationToMatch)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param monitor
     * @param problems
     * @param artifactsToWrite
     * @since 4.2
     */
    protected void generateAdditionalArtifacts( final IProgressMonitor theMonitor,
                                                final List artifactGenerators,
                                                final File sourceDirectory,
                                                final File targetDirectory,
                                                final ResourceSet resourceSet,
                                                final List problems,
                                                final List artifactsToWrite ) {

        ArgCheck.isNotNull(artifactGenerators);
        ArgCheck.isNotNull(sourceDirectory);
        ArgCheck.isNotNull(targetDirectory);
        ArgCheck.isNotNull(resourceSet);
        ArgCheck.isNotNull(problems);
        ArgCheck.isNotNull(artifactsToWrite);
        assertContextIsOpen();
        if (this.generators.size() == 0) {
            return;
        }

        // If no progress monitor was specified then create a NullProgressMonitor so
        // that we do not have to check for null everywhere within this method
        final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);

        // Create the VdbGenerationContext
        VdbGenerationContext genContext = createVdbGenerationContext(monitor,
                                                                     sourceDirectory,
                                                                     targetDirectory,
                                                                     resourceSet,
                                                                     artifactsToWrite);

        // If the VdbGenerationContext is an InternalVdbGenerationContext instance
        // then set additional parameters for use by "internal" artifact generators
        if (genContext instanceof InternalVdbGenerationContext) {
            ((InternalVdbGenerationContext)genContext).setResourceSet(getVdbResourceSet());
            ((InternalVdbGenerationContext)genContext).setVdbContext(this);
        }

        // Defect 22865 - changed call to getTempDirectoryResources()
        final List eResources = getTempDirectoryResources(resourceSet);
        // Ensure that all resources are loaded prior to executing any artifact generators
        for (Iterator i = eResources.iterator(); i.hasNext();) {
            Resource eResource = (Resource)i.next();
            if (!eResource.isLoaded()) {
                try {
                    eResource.load(getLoadOptions());
                } catch (Throwable e) {
                    final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_loading_resource", eResource.getURI().lastSegment()); //$NON-NLS-1$
                    problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, ERROR_LOADING_RESOURCE_ERROR_CODE, msg, e));
                }
            }
        }

        // Loop over the generators and execute them ...
        executeGenerators(monitor, genContext, problems);

        // Add all of the problems ...
        problems.addAll(genContext.getProblems());

        // Get all of the additional artifacts ...
        final Map newArtifactsByPath = genContext.getGeneratedArtifactsByPath();
        for (Iterator i = newArtifactsByPath.entrySet().iterator(); i.hasNext();) {
            final Map.Entry entry = (Map.Entry)i.next();

            // Write the stream to a temp file and add the File to the artifacts ...
            File tempFile = null;
            try {
                final IPath path = createNormalizedPath((String)entry.getKey());
                final Object content = entry.getValue();
                tempFile = new File(targetDirectory, path.toOSString());
                if (content instanceof String) {
                    InputStream stream = null;
                    try {
                        stream = new ByteArrayInputStream(((String)content).getBytes());
                        FileUtils.write(stream, tempFile);
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_closing_stream", path); //$NON-NLS-1$
                                problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, CLOSE_STREAM_ERROR_CODE, msg, e));
                            }
                        }
                    }
                } else if (content instanceof Document) {
                    // Write to temp file and add file to artifacts ...
                    final OutputStream fileStream = new FileOutputStream(tempFile);
                    try {
                        JdomHelper.write((Document)content, fileStream);
                    } finally {
                        try {
                            fileStream.close();
                        } catch (IOException e) {
                            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_closing_stream", path); //$NON-NLS-1$
                            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, CLOSE_STREAM_ERROR_CODE, msg, e));
                        }
                    }
                } else if (content instanceof InputStream) {
                    // Write to temp file and add file to artifacts ...
                    InputStream stream = (InputStream)content;
                    try {
                        FileUtils.write(stream, tempFile);
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_closing_stream", path); //$NON-NLS-1$
                            problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, CLOSE_STREAM_ERROR_CODE, msg, e));
                        }
                    }
                } else if (content instanceof File) {
                    tempFile = (File)content;
                }

                // Record that there is a new artifact (temp file) to be written to the VDB archive file
                if (tempFile.exists()) {
                    artifactsToWrite.add(new Artifact(path, tempFile, 0));
                }
            } catch (Throwable e) {
                final Object[] params = new Object[] {tempFile.getName(), getVdbFile(), e.getMessage()};
                final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_writing_artifact_file", params); //$NON-NLS-1$
                problems.add(new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID, FILE_WRITE_ERROR_CODE, msg, e));
            }

        }

        // If the VdbGenerationContext is an InternalVdbGenerationContext instance then clean up its state
        if (genContext instanceof InternalVdbGenerationContext) {
            ((InternalVdbGenerationContext)genContext).dispose();
        }
        genContext = null;
    }

    protected VdbGenerationContext createVdbGenerationContext( final IProgressMonitor theMonitor,
                                                               final File sourceDirectory,
                                                               final File targetDirectory,
                                                               final ResourceSet resourceSet,
                                                               final List artifactsToWrite ) {
        ArgCheck.isNotNull(sourceDirectory);
        ArgCheck.isNotNull(targetDirectory);
        ArgCheck.isNotNull(resourceSet);
        ArgCheck.isNotNull(artifactsToWrite);
        assertContextIsOpen();

        // If no progress monitor was specified then create a NullProgressMonitor so
        // that we do not have to check for null everywhere within this method
        final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);
        monitor.setTaskName(VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Creating_vdb_generation_context")); //$NON-NLS-1$

        // Create all the maps needed by the VdbGenerationContextImpl
        final Map modelNamesByResource = new HashMap();
        final Map modelPathsByResource = new HashMap();
        final Map modelVisibilityByResource = new HashMap();
        final Map problemsByObjectId = new HashMap();

        // Populate the maps from the supplied ResourceSet
        // Defect 22865 - changed call to getTempDirectoryResources()
        final List modelList = getTempDirectoryResources(resourceSet);
        final Resource[] models = (Resource[])modelList.toArray(new Resource[modelList.size()]);
        for (Iterator i = modelList.iterator(); i.hasNext();) {
            final Resource r = (Resource)i.next();
            final URI uri = r.getURI();

            // Add entry to the modelPathsByResource map
            String pathInTempDir = (uri.isFile() ? getPathRelativeToFolder(sourceDirectory, new File(uri.toFileString())) : URI.decode(uri.toString()));
            modelPathsByResource.put(r, pathInTempDir);

            // Add entry to the modelNamesByResource map
            modelNamesByResource.put(r, uri.lastSegment());

            // Add entry to the modelVisibilityByResource map
            ModelReference modelRef = getModelReference(pathInTempDir);
            if (modelRef != null) {
                Boolean isVisible = (modelRef.isVisible() ? Boolean.TRUE : Boolean.FALSE);
                modelVisibilityByResource.put(r, isVisible);
            }

            // Add entries to the problemsByObjectId map
            if (modelRef != null) {
                for (Iterator j = modelRef.getMarkers().iterator(); j.hasNext();) {
                    ProblemMarker marker = (ProblemMarker)j.next();
                    EObject target = getProblemMarkerEObject(marker);
                    if (target != null) {
                        ObjectID targetId = ModelerCore.getObjectId(target);
                        List targetProblems = (List)problemsByObjectId.get(targetId);
                        if (targetProblems == null) {
                            targetProblems = new ArrayList(7);
                            problemsByObjectId.put(targetId, targetProblems);
                        }
                        targetProblems.add(marker);
                    }
                }
            }
        }

        // Determine the existing paths ...
        final List paths = new ArrayList();
        for (Iterator i = artifactsToWrite.iterator(); i.hasNext();) {
            final Artifact artifact = (Artifact)i.next();
            paths.add(artifact.path);
        }
        final IPath[] existingArtifactPaths = (IPath[])paths.toArray(new IPath[paths.size()]);

        // Create the generation context ...
        final VdbGenerationContextParameters parameters = new VdbGenerationContextParameters();
        parameters.setModels(models);
        parameters.setExistingPathsInVdb(existingArtifactPaths);
        parameters.setModelNameByResource(modelNamesByResource);
        parameters.setModelVisibilityByResource(modelVisibilityByResource);
        parameters.setProblemsByObjectId(problemsByObjectId);
        parameters.setTempFolderAbsolutePath(targetDirectory.getAbsolutePath());
        parameters.setWorkspacePathByResource(modelPathsByResource);
        final VdbGenerationContext genContext = getVdbGenerationContextFactory().createVdbGenerationContext(parameters, monitor);

        return genContext;
    }

    protected void executeGenerators( final IProgressMonitor theMonitor,
                                      final VdbGenerationContext genContext,
                                      final List problems ) {
        ArgCheck.isNotNull(genContext);
        ArgCheck.isNotNull(problems);

        // If no progress monitor was specified then create a NullProgressMonitor so
        // that we do not have to check for null everywhere within this method
        final IProgressMonitor monitor = (theMonitor == null ? new NullProgressMonitor() : theMonitor);

        for (Iterator i = this.generators.iterator(); i.hasNext();) {
            if (monitor.isCanceled()) {
                return;
            }
            final VdbArtifactGenerator generator = (VdbArtifactGenerator)i.next();

            // Create a separate thread to do the generation. This is so that we can cancel it
            // if the (customer-supplied) generator goes on ad infinitum.
            final ArtifactGeneratorThread thread = new ArtifactGeneratorThread(genContext, generator);
            thread.start();

            // Poll for cancellation ...
            try {
                while (!monitor.isCanceled() && thread.isAlive()) {
                    final String displayMessage = genContext.getProgressMessage();
                    monitor.setTaskName(displayMessage);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // shouldn't really happen!
            } finally {
                // Looks for an exception in the connection thread ...
                final Throwable error = thread.getThrowable();
                if (error instanceof InterruptedException) {
                    // canceled!!!
                    final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Generator_cancelled", generator.getClass().getName()); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.WARNING, VdbEditPlugin.PLUGIN_ID,
                                                      ARTIFACT_GENERATOR_CANCEL_ERROR_CODE, msg, error);
                    problems.add(status);
                } else if (error != null) {
                    final String msg = VdbEditPlugin.Util.getString("SharedWsVdbContextEditor.Error_executing_generator", generator.getClass().getName()); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.ERROR, VdbEditPlugin.PLUGIN_ID,
                                                      ARTIFACT_GENERATOR_EXECUTE_ERROR_CODE, msg, error);
                    problems.add(status);
                    VdbEditPlugin.Util.log(status);
                }
            }
        }
    }

    protected EObject getProblemMarkerEObject( final ProblemMarker marker ) {
        if (marker != null && marker.getTargetUri() != null && marker.getTargetUri().startsWith(UUID.PROTOCOL)) {
            for (Iterator i = getVdbResourceSet().getResources().iterator(); i.hasNext();) {
                Resource r = (Resource)i.next();
                EObject eObj = r.getEObject(marker.getTargetUri());
                if (eObj != null) {
                    return eObj;
                }
            }
        }
        return null;
    }

    private class Artifact {
        public final IPath path;
        public final File content;
        public final int workToWrite;

        public Artifact( final IPath path,
                         final File content,
                         final int workToWrite ) {
            this.path = path;
            this.content = content;
            this.workToWrite = workToWrite;
        }
    }

    private class ArtifactGeneratorThread extends Thread {
        private VdbGenerationContext context;
        private VdbArtifactGenerator generator;
        private Throwable throwable;

        protected ArtifactGeneratorThread( final VdbGenerationContext context,
                                           final VdbArtifactGenerator generator ) {
            super("VdbArtifactGeneratorThread"); //$NON-NLS-1$
            this.context = context;
            this.generator = generator;
        }

        @Override
        public void run() {
            boolean requiredStart = ModelerCore.startTxn(false, false, "Generate Artifacts", context); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                this.generator.execute(context);
                succeeded = true;
            } catch (Throwable t) {
                this.throwable = t;
            } finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        public Throwable getThrowable() {
            return this.throwable;
        }

    }

}
