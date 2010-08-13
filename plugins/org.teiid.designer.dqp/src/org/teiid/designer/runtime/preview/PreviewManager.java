/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview;

import static com.metamatrix.modeler.dqp.DqpPlugin.PLUGIN_ID;
import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.datatools.JdbcTranslatorHelper;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.IExecutionConfigurationListener;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.ExecutionConfigurationEvent.EventType;
import org.teiid.designer.runtime.ExecutionConfigurationEvent.TargetType;
import org.teiid.designer.runtime.preview.jobs.CompositePreviewJob;
import org.teiid.designer.runtime.preview.jobs.CreatePreviewVdbJob;
import org.teiid.designer.runtime.preview.jobs.DeleteDeployedPreviewVdbJob;
import org.teiid.designer.runtime.preview.jobs.DeletePreviewVdbJob;
import org.teiid.designer.runtime.preview.jobs.DeployPreviewVdbJob;
import org.teiid.designer.runtime.preview.jobs.ModelChangedJob;
import org.teiid.designer.runtime.preview.jobs.ModelProjectOpenedJob;
import org.teiid.designer.runtime.preview.jobs.UpdatePreviewVdbJob;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;
import com.metamatrix.common.xmi.XMIHeader;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelFileUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ResourceChangeUtilities;

/**
 * The <code>PreviewManager</code> is responsible for keeping the hidden Preview VDBs synchronized with the workspace. Also, the
 * Preview Manager is responsible for deploying the Preview VDBs to Teiid to enable data preview. The Preview Manager also removes
 * (undeploys) Preview VDBs from Teiid when their associate workspace model is deleted.
 * <p>
 * For each previewable model in the workspace, an associated Preview VDB will be maintained. This PVDB will be hidden from the
 * user but will be contained within the workspace. When a model is changed, the PVDB is automatically synchronized. When a model
 * is deleted, the PVDB is also deleted. Upon preview of a model object, the PVDB of that model, along with any dependent models
 * will be deployed to the designated preview Teiid server (if there is one). Deploying of the PVDBs is only done if necessary.
 */
@ThreadSafe
public final class PreviewManager extends JobChangeAdapter
    implements IExecutionConfigurationListener, IPreferenceChangeListener, IResourceChangeListener, INotifyChangedListener,
    PreviewContext {

    private static final String PROJECT_VDB_SUFFIX = "_project"; //$NON-NLS-1$

    /**
     * @param project the project whose Preview VDB is being requested (may not be <code>null</code>)
     * @return the name of the project's Preview VDB (never <code>null</code>)
     */
    public static String getPreviewProjectVdbName( IProject project ) {
        assert (project != null) : "Project is null"; //$NON-NLS-1$
        char delim = '_';
        StringBuilder name = new StringBuilder(ModelerCore.workspaceUuid().toString() + delim);

        name.append(project.getName()).append(PROJECT_VDB_SUFFIX);

        return name.toString();
    }

    /**
     * @param pvdbFile the Preview VDB (may not be <code>null</code> and must be a VDB)
     * @return the version of the given Preview VDB
     */
    public static int getPreviewVdbVersion( IFile pvdbFile ) {
        assert (pvdbFile != null) : "PVDB is null"; //$NON-NLS-1$
        assert (ModelUtil.isVdbArchiveFile(pvdbFile)) : "IFile is not a VDB"; //$NON-NLS-1$

        Vdb pvdb = new Vdb(pvdbFile, true, null);
        return pvdb.getVersion();
    }

    /**
     * @param file the file being checked (may not be <code>null</code>)
     * @return <code>true</code> if the file is a previewable model
     */
    public static boolean isPreviewable( IFile file ) {
        if (ModelUtil.isModelFile(file)) {
            if (ModelUtil.isXsdFile(file)) {
                return true;
            }
            String metamodelUri = null;
            File modelFile = file.getFullPath().toFile();

            if (modelFile.exists()) {
                XMIHeader header = ModelFileUtil.getXmiHeader(modelFile);
                metamodelUri = header.getPrimaryMetamodelURI();
            } else {
                try {
                    ModelResource modelResource = ModelUtil.getModelResource(file, true);
                    assert (modelResource != null); // already determined it is a model
                    MetamodelDescriptor descriptor = ModelerCore.getModelEditor().getPrimaryMetamodelDescriptor(modelResource);
                    metamodelUri = descriptor.getNamespaceURI();
                } catch (ModelWorkspaceException e) {
                    Util.log(e);
                    return false;
                }
            }

            // must be from a relational or web service model
            if (RelationalPackage.eNS_URI.equals(metamodelUri) || WebServicePackage.eNS_URI.equals(metamodelUri)
                || XmlDocumentPackage.eNS_URI.equals(metamodelUri)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param resource the resource being checked
     * @return <code>true</code> if the resource is a Preview VDB
     */
    private static boolean isPreviewVdb( IResource resource ) {
        if (ModelUtil.isVdbArchiveFile(resource)) {
            Vdb vdb = new Vdb((IFile)resource, null);
            return vdb.isPreview();
        }

        return false;
    }

    /**
     * The preview context (never <code>null</code>).
     */
    private final PreviewContext context;

    /**
     * A flag indicating if preview is enabled. This will match the value of preferenced
     * {@link PreferenceConstants#PREVIEW_ENABLED}.
     */
    private boolean previewEnabled = true;

    /**
     * The Teiid server being used for preview (may be <code>null</code>).
     */
    private volatile AtomicReference<Server> previewServer = new AtomicReference<Server>();

    /**
     * A map with the project path as the key and a collection of PVDBs and there deploy flag indicating if the PVDB needs to be
     * deployed.
     */
    @GuardedBy( "statusLock" )
    private Map<IPath, Collection<PreviewVdbStatus>> deploymentStatusMap = new HashMap<IPath, Collection<PreviewVdbStatus>>();

    /**
     * Lock used for when accessing PVDB deployment status.
     */
    private final ReadWriteLock statusLock = new ReentrantReadWriteLock();

    /**
     * Constructs a <code>PreviewManager</code> using the default {@link PreviewContext}.
     * 
     * @throws Exception if there is a problem initializing the preview feature
     */
    public PreviewManager() throws Exception {
        this(null);
    }

    /**
     * @param context the preview context (may be <code>null</code>)
     * @throws Exception if there is a problem initializing the preview feature
     */
    public PreviewManager( PreviewContext context ) throws Exception {
        this.context = (context == null) ? this : context;
        startup();
    }

    /**
     * @param pvdb the PVDB being added to the workspace
     */
    private void addWorkspacePvdb( IFile pvdb ) {
        IPath projectPath = pvdb.getProject().getFullPath();

        try {
            this.statusLock.writeLock().lock();
            Collection<PreviewVdbStatus> statuses = this.deploymentStatusMap.get(projectPath);

            if (statuses == null) {
                statuses = new ArrayList<PreviewVdbStatus>();
                this.deploymentStatusMap.put(projectPath, statuses);
            }

            statuses.add(new PreviewVdbStatus(pvdb));
        } finally {
            this.statusLock.writeLock().unlock();
        }
    }

    IStatus checkPreviewVdbForErrors( Vdb pvdb ) {
        try {
            for (IMarker marker : pvdb.getProblems()) {
                if (marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING) == IMarker.SEVERITY_ERROR) {
                    return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.ModelErrorMarkerExists,
                                                                         marker.getAttribute(IMarker.MESSAGE, ""))); //$NON-NLS-1$
                }
            }

            return Status.OK_STATUS;
        } catch (Exception e) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Messages.UnexpectedErrorGettingVdbMarkers, e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.IExecutionConfigurationListener#configurationChanged(org.teiid.designer.runtime.ExecutionConfigurationEvent)
     */
    @Override
    public void configurationChanged( ExecutionConfigurationEvent event ) {
        if (event.getEventType().equals(EventType.DEFAULT) && event.getTargetType().equals(TargetType.SERVER)) {
            setPreviewServer(event.getUpdatedServer());
        }
    }

    /**
     * @param pvdbPath the path of the PVDB being deleted from the workspace
     */
    private void deleteWorkspacePvdb( IPath pvdbPath ) {
        IPath projectPath = getProjectPath(pvdbPath);

        try {
            this.statusLock.writeLock().lock();
            Collection<PreviewVdbStatus> statuses = this.deploymentStatusMap.get(projectPath);

            for (PreviewVdbStatus status : statuses) {
                if (status.getPath().equals(pvdbPath)) {
                    statuses.remove(status);
                    break;
                }
            }
        } finally {
            this.statusLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
     */
    @Override
    public void done( IJobChangeEvent event ) {
        Job job = event.getJob();
        job.removeJobChangeListener(this);

        IStatus status = job.getResult();

        if ((status == null) || (status.getSeverity() == IStatus.CANCEL)) {
            return;
        } else if (status.getSeverity() == IStatus.ERROR) {
            Util.log(job.getResult());
        } else {
            if (job instanceof CreatePreviewVdbJob) {
                handlePreviewVdbCreated((CreatePreviewVdbJob)job);
            } else if (job instanceof DeletePreviewVdbJob) {
                handlePreviewVdbDeleted((DeletePreviewVdbJob)job);
            } else if (job instanceof UpdatePreviewVdbJob) {
                handlePreviewVdbUpdated((UpdatePreviewVdbJob)job);
            } else if (job instanceof ModelProjectOpenedJob) {
                handleModelProjectOpened((ModelProjectOpenedJob)job);
            } else if (job instanceof DeployPreviewVdbJob) {
                handlePreviewVdbDeployed((DeployPreviewVdbJob)job);
            }

            if (status.getSeverity() != IStatus.OK) {
                Util.log(job.getResult());
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.PreviewContext#ensureConnectionInfoIsValid(org.teiid.designer.vdb.Vdb,
     *      org.teiid.designer.runtime.Server)
     */
    public IStatus ensureConnectionInfoIsValid( Vdb previewVdb,
                                                Server previewServer ) throws Exception {
        assert (previewServer != null) : "Preview server is null"; //$NON-NLS-1$

        if (previewVdb.getModelEntries().isEmpty()) {
            return Status.OK_STATUS;
        }

        // PVDB has only one model
        VdbModelEntry modelEntry = previewVdb.getModelEntries().iterator().next();
        IFile model = modelEntry.findFileInWorkspace();
        ModelResource modelResource = ModelUtil.getModelResource(model, true);

        boolean isSourceModel = modelResource.getModelType() == ModelType.PHYSICAL_LITERAL;

        if (!isSourceModel) {
            return Status.OK_STATUS;
        }

        int errors = 0;
        IStatus connectionInfoError = null;

        // if we have a preview server and connection information on the model then assign a data source
        IConnectionInfoHelper helper = new ConnectionInfoHelper();

        if (helper.hasConnectionInfo(modelResource)) {
            String jndiName = this.context.getPreviewVdbJndiName(previewVdb.getFile().getFullPath());
            ExecutionAdmin execAdmin = previewServer.getAdmin();

            // create data source on server if we need to
            if (!execAdmin.dataSourceExists(jndiName)) {
                execAdmin.getOrCreateDataSource(model, jndiName);
            }

            if (!jndiName.equals(modelEntry.getJndiName())) {
                modelEntry.setJndiName(jndiName);
            }
        } else {
            ++errors;
            connectionInfoError = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.ModelDoesNotHaveConnectionInfoError,
                                                                                model.getFullPath()), null);
        }

        IStatus translatorError = null;

        // if no translator name see if we can get one
        if (StringUtilities.isEmpty(modelEntry.getTranslator())) {
            IConnectionProfile connectionProfile = helper.getConnectionProfile(modelResource);

            // get translator
            if (connectionProfile == null) {
                ++errors;
                translatorError = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.ModelTranslatorCannotBeSetError,
                                                                                model.getFullPath()), null);
            } else {
                modelEntry.setTranslator(JdbcTranslatorHelper.getTranslator(connectionProfile));
            }
        }

        if (errors == 0) {
            return Status.OK_STATUS;
        }

        if (errors == 2) {
            IStatus[] statuses = new IStatus[2];
            statuses[0] = connectionInfoError;
            statuses[1] = translatorError;
            return new MultiStatus(PLUGIN_ID, IStatus.OK, statuses, NLS.bind(Messages.ModelConnectionInfoError,
                                                                             model.getFullPath()), null);
        }

        if (connectionInfoError != null) return connectionInfoError;
        return translatorError;
    }

    /**
     * Handler for a resource change event indicating the specified file has been deleted.
     * 
     * @param file the file that has been deleted
     */
    private void fileDeleted( IFile file ) {
        if (ModelUtil.isModelFile(file.getFullPath())) {
            try {
                IFile pvdb = this.context.getPreviewVdb(file);

                if ((pvdb != null) && pvdb.exists()) {
                    DeletePreviewVdbJob job = new DeletePreviewVdbJob(this.context, file);
                    job.addJobChangeListener(this);
                    job.schedule();
                }
            } catch (Exception e) {
                Util.log(IStatus.ERROR, e, NLS.bind(Messages.DeletedModelProcessingError, file.getFullPath()));
            }
        }
    }

    /**
     * @param project the project whose Preview VDBs are being requested
     * @param onlyThoseNeedingToBeDeployed <code>true</code> if only Preview VDBs needing to be deployed should be returned
     * @return the project Preview VDBs (never <code>null</code>)
     */
    List<IFile> findProjectPvdbs( IProject project,
                                  boolean onlyThoseNeedingToBeDeployed ) {
        List<IFile> pvdbsToDeploy = new ArrayList<IFile>();
        Collection<PreviewVdbStatus> statuses = null;

        try {
            this.statusLock.readLock().lock();
            statuses = this.deploymentStatusMap.get(project.getFullPath());
        } finally {
            this.statusLock.readLock().unlock();
        }

        // statuses could be null at startup when the default server is being set
        if ((statuses != null) && !statuses.isEmpty()) {
            for (PreviewVdbStatus status : statuses) {
                if (!onlyThoseNeedingToBeDeployed) {
                    pvdbsToDeploy.add(this.context.getPreviewVdb(status.getFile()));
                } else {
                    IFile pvdbFile = status.getFile();

                    if (needsToBeDeployed(pvdbFile)) {
                        pvdbsToDeploy.add(this.context.getPreviewVdb(pvdbFile));
                    }
                }
            }
        }

        return pvdbsToDeploy;
    }

    /**
     * @param container the container whose Preview VDBs are being requested
     * @param pvdbPaths the paths to all the Preview VDBs under the specified container
     */
    private void findPvdbs( IContainer container,
                            List<IPath> pvdbPaths ) throws Exception {
        for (IResource resource : container.members(IContainer.INCLUDE_HIDDEN)) {
            if (resource instanceof IContainer) {
                findPvdbs((IContainer)resource, pvdbPaths);
            } else if (isPreviewVdb(resource)) {
                pvdbPaths.add(resource.getFullPath());
            }
        }
    }

    private IProject[] getAllProjects() {
        return ResourcesPlugin.getWorkspace().getRoot().getProjects();
    }

    private IFile getFile( IPath path ) {
        return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    }

    Server getPreviewServer() {
        return this.previewServer.get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.PreviewContext#getPreviewVdb(IResource)
     */
    @Override
    public IFile getPreviewVdb( IResource projectOrModel ) {
        IFile pvdb = null;
        IPath pathToPvdb = null;
        String pvdbName = getPreviewVdbName(projectOrModel);

        if (projectOrModel instanceof IProject) {
            pathToPvdb = projectOrModel.getFullPath().append(pvdbName);
        } else {
            assert (projectOrModel instanceof IFile) : "Resource is not a file:" + projectOrModel.getFullPath(); //$NON-NLS-1$
            pathToPvdb = projectOrModel.getParent().getFullPath().append(pvdbName);
        }

        pvdb = getFile(pathToPvdb);
        return pvdb;
    }

    private String getPreviewVdbDeployedName( IFile pvdb ) {
        return this.context.getPreviewVdbDeployedName(pvdb.getFullPath());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.PreviewContext#getPreviewVdbDeployedName(IPath)
     */
    @Override
    public String getPreviewVdbDeployedName( IPath pvdbPath ) {
        return pvdbPath.removeFileExtension().lastSegment();
    }

    private String getPreviewVdbJndiName( IFile pvdb ) {
        return this.context.getPreviewVdbJndiName(pvdb.getFullPath());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.PreviewContext#getPreviewVdbJndiName(org.eclipse.core.runtime.IPath)
     */
    @Override
    public String getPreviewVdbJndiName( IPath pvdbPath ) {
        return getPreviewVdbDeployedName(pvdbPath);
    }

    private String getPreviewVdbName( IResource projectOrModel ) {
        char delim = '_';
        StringBuilder name = new StringBuilder(ModelerCore.workspaceUuid().toString() + delim);

        if (projectOrModel instanceof IProject) {
            name = new StringBuilder(PreviewManager.getPreviewProjectVdbName((IProject)projectOrModel));
        } else {
            assert (projectOrModel instanceof IFile) : "IResource is not an IFile"; //$NON-NLS-1$

            if (projectOrModel.getFileExtension().equalsIgnoreCase(TeiidVdb.VDB_EXTENSION)) {
                String vdbName = projectOrModel.getFullPath().removeFileExtension().lastSegment();
                if (vdbName.startsWith(ModelerCore.workspaceUuid().toString())) {
                    return projectOrModel.getFullPath().lastSegment();
                }
            }
            IPath modelPath = projectOrModel.getFullPath().removeFileExtension();

            for (String segment : modelPath.segments()) {
                name.append(segment).append(delim);
            }

            // remove last delimiter
            name.deleteCharAt(name.length() - 1);
        }

        name.append(Vdb.FILE_EXTENSION);
        return name.toString();
    }

    private IPath getProjectPath( IPath pvdbPath ) {
        return pvdbPath.uptoSegment(1);
    }

    private PreviewVdbStatus getStatus( IPath pvdbPath ) {
        Collection<PreviewVdbStatus> statuses = null;

        try {
            this.statusLock.readLock().lock();
            statuses = this.deploymentStatusMap.get(getProjectPath(pvdbPath));
        } finally {
            this.statusLock.readLock().unlock();
        }

        for (PreviewVdbStatus status : statuses) {
            if (status.getPath().equals(pvdbPath)) return status;
        }

        return null;
    }

    /**
     * Handles the file resource changes.
     * 
     * @param fileDelta the delta being processed
     */
    private void handleFileChanged( IResourceDelta fileDelta ) {
        assert (fileDelta != null) : "fileDelta is null"; //$NON-NLS-1$
        assert (fileDelta.getResource() instanceof IFile) : "fileDelta resource=" + fileDelta.getResource(); //$NON-NLS-1$
        IFile file = (IFile)fileDelta.getResource();

        // When a model is created a resource added event is followed immediately by a resource changed event. So, the
        // handling of a changed model first makes sure the Preview VDB exists before synchronizing with the changed model.
        if (ResourceChangeUtilities.isChanged(fileDelta) && ResourceChangeUtilities.isContentChanged(fileDelta)
            && isPreviewable(file)) {
            modelChanged(file);
        } else if (ResourceChangeUtilities.isRemoved(fileDelta)) {
            // this occurs when a model is deleted or renamed (old model location)
            fileDeleted(file);
        } else if (ResourceChangeUtilities.isAdded(fileDelta) && isPreviewable(file)) {
            // this occurs on a rename (new model location) => ResourceChangeUtilities.isMovedFrom(fileDelta)
            // this also occurs on a save as
            modelChanged(file);
        }
    }

    /**
     * Handles the folder resource changes.
     * 
     * @param folderDelta the delta being processed
     */
    private void handleFolderChanged( IResourceDelta folderDelta ) {
        assert (folderDelta != null) : "folderDelta is null"; //$NON-NLS-1$
        assert (folderDelta.getResource() instanceof IFolder) : "folderDelta resource=" + folderDelta.getResource(); //$NON-NLS-1$

        for (IResourceDelta delta : folderDelta.getAffectedChildren()) {
            IResource resource = delta.getResource();

            if (resource instanceof IFolder) {
                handleFolderChanged(delta);
            } else if (resource instanceof IFile) {
                handleFileChanged(delta);
            }
        }
    }

    /**
     * Handler for when a {@link ModelProjectOpenedJob} has finished.
     * 
     * @param job the job being processed
     */
    private void handleModelProjectOpened( ModelProjectOpenedJob job ) {
        // delete any PVDBs in the workspace that don't have an associated model
        IProject project = job.getProject();
        List<IPath> pvdbPaths = new ArrayList<IPath>();

        try {
            findPvdbs(project, pvdbPaths);

            if (!pvdbPaths.isEmpty()) {
                CompositePreviewJob batchJob = new CompositePreviewJob(NLS.bind(Messages.DeleteOrphanedPreviewVdbsJob,
                                                                                project.getName()), this.context,
                                                                       getPreviewServer());
                Collection<PreviewVdbStatus> statuses = null;

                try {
                    this.statusLock.readLock().lock();
                    // these are the PVDBs that have associated models
                    statuses = this.deploymentStatusMap.get(project.getFullPath());
                } finally {
                    this.statusLock.readLock().unlock();
                }

                for (IPath pvdbPath : pvdbPaths) {
                    boolean delete = true;

                    for (PreviewVdbStatus status : statuses) {
                        if (status.getPath().equals(pvdbPath)) {
                            delete = false;
                            break;
                        }
                    }

                    // delete PVDB if there is no associated model
                    if (delete) {
                        // remove project part of path because it gets added back in
                        IPath path = pvdbPath.removeFirstSegments(1);
                        DeletePreviewVdbJob deletePvdbJob = new DeletePreviewVdbJob(project.getFile(path), this.context);
                        batchJob.add(deletePvdbJob);
                    }
                }

                // only schedule if there are jobs to run
                if (!batchJob.getJobs().isEmpty()) {
                    batchJob.schedule();
                }
            }
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, NLS.bind(Messages.DeleteOrphanedPreviewVdbsJobError, project.getName()));
        }
    }

    /**
     * Handler for when a {@link CreatePreviewVdbJob} has finished.
     * 
     * @param job the job being processed
     */
    private void handlePreviewVdbCreated( CreatePreviewVdbJob job ) {
        IFile pvdb = job.getPvdb();
        addWorkspacePvdb(pvdb);
    }

    /**
     * Handler for when a {@link DeletePreviewVdbJob} has finished.
     * 
     * @param job the job being processed
     */
    private void handlePreviewVdbDeleted( DeletePreviewVdbJob job ) {
        assert (job.completedSuccessfully()) : "Delete Preview VDB job did not complete successfully"; //$NON-NLS-1$
        previewVdbDeletedPostProcessing(job.getPvdb().getFullPath());
    }

    /**
     * Handler for when a {@link DeployPreviewVdbJob} has finished.
     * 
     * @param job the job being processed
     */
    private void handlePreviewVdbDeployed( DeployPreviewVdbJob job ) {
        IFile pvdbFile = job.getPreviewVdb();
        setNeedsToBeDeployedStatus(pvdbFile, false);
    }

    /**
     * Handler for when a {@link UpdatePreviewVdbJob} has finished.
     * 
     * @param job the job being processed
     */
    private void handlePreviewVdbUpdated( UpdatePreviewVdbJob job ) {
        IFile changedModel = job.getModel();
        IFile pvdbFile = this.context.getPreviewVdb(changedModel);

        // change deploy status if necessary
        if ((job.getPreviewServer() != null) && !needsToBeDeployed(pvdbFile)) {
            setNeedsToBeDeployedStatus(pvdbFile, true);

            // delete deployed PVDB
            Job deleteDeployedPvdbJob = new DeleteDeployedPreviewVdbJob(getPreviewVdbDeployedName(pvdbFile),
                                                                        getPreviewVdbVersion(pvdbFile),
                                                                        getPreviewVdbJndiName(pvdbFile), this.context,
                                                                        job.getPreviewServer());
            deleteDeployedPvdbJob.schedule();
        }
    }

    /**
     * Indicates if the preview preference is enabled.
     * 
     * @return <code>true</code> if preview is enabled
     */
    public boolean isPreviewEnabled() {
        return this.previewEnabled;
    }

    /**
     * Handler for a resource change event indicating the specified model has changed.
     * 
     * @param model the model that has changed
     */
    private void modelChanged( IFile model ) {
        assert (model != null) : "model is null"; //$NON-NLS-1$
        assert isPreviewable(model) : "model is not previewable: " + model; //$NON-NLS-1$

        try {
            ModelChangedJob job = new ModelChangedJob(model, this.context, getPreviewServer());
            job.addChildJobChangeListener(this);
            job.schedule();
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, NLS.bind(Messages.ModelChangedJobError, model.getFullPath()));
        }
    }

    /**
     * Handler for a resource change event indicating a project has changed. This means a child resource has changed.
     * 
     * @param delta the delta to process
     */
    private void modelProjectChanged( IResourceDelta delta ) {
        for (IResourceDelta fileDelta : delta.getAffectedChildren()) {
            if (fileDelta.getResource() instanceof IFile) {
                handleFileChanged(fileDelta);
            } else if (fileDelta.getResource() instanceof IFolder) {
                handleFolderChanged(fileDelta);
            }
        }
    }

    /**
     * Handler for a resource change event indicating the specified project that has been closed.
     * 
     * @param project the project that was closed (may not be <code>null</code>)
     * @param context the preview context to use when working with the preview server (may not be <code>null</code>)
     */
    private void modelProjectClosed( IProject project,
                                     PreviewContext context ) {
        // treat as if project was deleted
        modelProjectDeleted(project, context);
    }

    /**
     * Handler for a resource change event indicating the specified project will be deleted.
     * 
     * @param project the project being deleted (may not be <code>null</code>)
     * @param context the preview context to use when working with the preview server (may not be <code>null</code>)
     */
    private void modelProjectDeleted( IProject project,
                                      PreviewContext context ) {
        assert (project != null) : "project is null"; //$NON-NLS-1$
        assert (context != null) : "context is null"; //$NON-NLS-1$

        // all the Preview VDBs under this project so just post process them
        try {
            List<IPath> pvdbPaths = new ArrayList<IPath>();
            findPvdbs(project, pvdbPaths);

            for (IPath pvdbPath : pvdbPaths) {
                previewVdbDeletedPostProcessing(pvdbPath);
            }
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, NLS.bind(Messages.PreviewVdbDeletedPostProcessingError, project.getName()));
        }
    }

    /**
     * Handler for a resource change event indicating the specified project has been opened.
     * 
     * @param project the project that was opened
     */
    private void modelProjectOpened( IProject project ) {
        try {
            ModelProjectOpenedJob job = new ModelProjectOpenedJob(project, this.context);
            job.addChildJobChangeListener(this);
            job.addJobChangeListener(this);
            job.schedule();
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, NLS.bind(Messages.ModelProjectOpenedJobError, project.getName()));
        }
    }

    /**
     * @param pvdbPath the path of the PVDB whose deploy status is being requested.
     * @return <code>true</code> if the PVDB needs to be deployed
     */
    boolean needsToBeDeployed( IFile pvdbFile ) {
        try {
            this.statusLock.readLock().lock();
            PreviewVdbStatus status = getStatus(pvdbFile.getFullPath());
            assert (status != null) : "PVDB status not found in status map:" + pvdbFile.getFullPath(); //$NON-NLS-1$
            boolean deploy = status.shouldDeploy();

            if (!deploy) {
                // make sure server has a copy of the Preview VDB
                if (getPreviewServer() != null) {
                    try {
                        ExecutionAdmin admin = getPreviewServer().getAdmin();
                        deploy = (admin.getVdb(getPreviewVdbDeployedName(pvdbFile)) == null);

                        // server does not have a copy. update status map.
                        if (deploy) {
                            setNeedsToBeDeployedStatus(pvdbFile, true);
                        }
                    } catch (Exception e) {
                        Util.log(e);
                    }
                }
            }

            return deploy;
        } finally {
            this.statusLock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public void notifyChanged( Notification notification ) {
        if (!isPreviewEnabled()) return;
        // TODO needs to react to changes in a model's connection profile by deleting data source on server, updating translator
        // name, create new data source on server. The event target eObject should be the ModelAnnotation.
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener#preferenceChange(org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent)
     */
    @Override
    public void preferenceChange( PreferenceChangeEvent event ) {
        if (event.getKey().equals(PreferenceConstants.PREVIEW_ENABLED)) {
            this.previewEnabled = Boolean.parseBoolean(event.getNewValue().toString());

            // clear deploy status map as everything will have to be deployed once preview is enabled again
            if (!this.previewEnabled) {
                this.deploymentStatusMap.clear();
            }
        }
    }

    @SuppressWarnings( "unused" )
    public void previewSetup( final Object objectToPreview,
                              IProgressMonitor monitor ) throws Exception {
        assert isPreviewEnabled() : "previewSetup should not be called if preview is disabled"; //$NON-NLS-1$
        Server previewServer = getPreviewServer();
        assert (previewServer != null) : "Should not be called when preview server is null"; //$NON-NLS-1$

        ModelResource model = ModelUtil.getModel(objectToPreview);
        IFile modelToPreview = (IFile)model.getCorrespondingResource();
        IFile pvdbFile = this.context.getPreviewVdb(modelToPreview);
        Vdb pvdb = new Vdb(pvdbFile, true, monitor);
        List<IFile> projectPvdbsToDeploy = findProjectPvdbs(modelToPreview.getProject(), true);

        if (monitor.isCanceled()) {
            throw new InterruptedException();
        }

        monitor.beginTask(Messages.PreviewSetupTask, 3 + (2 * projectPvdbsToDeploy.size()));

        PREVIEW_MODEL_VALIDATION_CHECK_TASK: {
            // make sure no errors
            monitor.subTask(NLS.bind(Messages.PreviewSetupValidationCheckTask, model.getItemName()));
            IStatus status = checkPreviewVdbForErrors(pvdb);

            if (status.getSeverity() == IStatus.ERROR) {
                throw new CoreException(status);
            }

            monitor.worked(1);

            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
        }

        ExecutionAdmin admin = previewServer.getAdmin();

        // collect all the Preview VDB parent folders so that we can make sure workspace is in sync with file system
        Set<IContainer> parents = new HashSet<IContainer>();

        PREVIEW_MODEL_CONNECTION_INFO_TASK: {
            // make sure all connection information is valid
            if (model.getModelType() == ModelType.PHYSICAL_LITERAL) {
                monitor.subTask(NLS.bind(Messages.PreviewSetupConnectionInfoTask, model.getItemName()));
                IStatus status = this.context.ensureConnectionInfoIsValid(pvdb, previewServer);

                if (status.getSeverity() == IStatus.ERROR) {
                    throw new CoreException(status);
                }

                // save if necessary
                if (pvdb.isModified()) {
                    pvdb.save(monitor);
                }
            }

            monitor.worked(1);

            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
        }

        PREVIEW_MODEL_DEPLOY_TASK: {
            // deploy model's PVDB if necessary
            if (needsToBeDeployed(pvdbFile)) {
                // make sure PVDB is in sync with file system
                IContainer parent = pvdbFile.getParent();
                monitor.subTask(NLS.bind(Messages.PreviewSetupRefreshWorkspaceTask, parent.getFullPath()));

                if (!parents.contains(parent)) {
                    refreshLocal(parent);
                    parents.add(parent);
                }

                // deploy and update status map
                monitor.subTask(NLS.bind(Messages.PreviewSetupDeployTask, model.getItemName()));
                admin.deployVdb(pvdbFile);
                setNeedsToBeDeployedStatus(pvdbFile, false);
            }

            monitor.worked(1);

            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
        }

        // deploy any project PVDBs if necessary
        for (IFile projectPvdbFile : projectPvdbsToDeploy) {
            PROJECT_MODEL_DEPLOY_TASK: {
                if (pvdbFile.equals(projectPvdbFile)) {
                    monitor.worked(1);
                    continue;
                }

                Vdb projectModelPvdb = new Vdb(projectPvdbFile, true, null);

                // make sure no errors
                monitor.subTask(NLS.bind(Messages.PreviewSetupValidationCheckTask, projectModelPvdb.getName()));
                IStatus status = checkPreviewVdbForErrors(projectModelPvdb);

                if (status.getSeverity() == IStatus.ERROR) {
                    throw new CoreException(status);
                }

                monitor.subTask(NLS.bind(Messages.PreviewSetupConnectionInfoTask, projectModelPvdb.getName()));
                status = this.context.ensureConnectionInfoIsValid(projectModelPvdb, previewServer);

                if (status.getSeverity() == IStatus.ERROR) {
                    throw new CoreException(status);
                }

                // save if necessary
                if (projectModelPvdb.isModified()) {
                    projectModelPvdb.save(monitor);
                }

                // make sure parent is in sync with file system
                IContainer parent = projectPvdbFile.getParent();
                monitor.subTask(NLS.bind(Messages.PreviewSetupRefreshWorkspaceTask, parent.getFullPath()));

                if (!(parent instanceof IWorkspaceRoot) && !parents.contains(parent)) {
                    refreshLocal(parent);
                    parents.add(parent);
                }

                // deploy PVDB
                monitor.subTask(NLS.bind(Messages.PreviewSetupDeployTask, projectPvdbFile.getName()));
                admin.deployVdb(projectPvdbFile);
                setNeedsToBeDeployedStatus(projectPvdbFile, false);

                monitor.worked(1);

                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
            }
        }

        // merge into project PVDB
        for (IFile pvdbToMerge : projectPvdbsToDeploy) {
            MERGE_TASK: {
                monitor.subTask(NLS.bind(Messages.PreviewSetupMergeTask, pvdbToMerge.getName()));

                // REMOVE the .vdb extension for the source vdb
                String sourceVdbName = pvdbToMerge.getFullPath().removeFileExtension().lastSegment().toString();
                String projectPreviewVdbName = getPreviewProjectVdbName(modelToPreview.getProject());

                if (!sourceVdbName.equals(projectPreviewVdbName)) {
                    admin.mergeVdbs(sourceVdbName, PreviewManager.getPreviewVdbVersion(pvdbToMerge), projectPreviewVdbName, 1);
                }

                monitor.worked(1);

                if (monitor.isCanceled()) {
                    throw new InterruptedException();
                }
            }
        }

        monitor.done();
    }

    /**
     * Must be called after a Preview VDB is deleted.
     * 
     * @param pvdbPath the path of the Preview VDB that was just deleted (never <code>null</code>)
     */
    private void previewVdbDeletedPostProcessing( IPath pvdbPath ) {
        deleteWorkspacePvdb(pvdbPath);

        // delete deployed PVDB
        IFile pvdbFile = getFile(pvdbPath);
        String jndiName = this.context.getPreviewVdbJndiName(pvdbPath);
        Job job = new DeleteDeployedPreviewVdbJob(this.context.getPreviewVdbDeployedName(pvdbPath),
                                                  getPreviewVdbVersion(pvdbFile), jndiName, this.context, getPreviewServer());
        job.schedule();
    }

    private void refreshLocal( IContainer parent ) throws CoreException {
        // make sure all workspace PVDBs are in sync with the file system
        try {
            parent.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
            throw e;
        }
    }

    /**
     * Resets all PVDBs deploy status to needing to be deployed.
     */
    private void resetAllDeployedStatuses() {
        try {
            this.statusLock.writeLock().lock();

            // set all PVDBs to needing to be deployed
            for (Map.Entry<IPath, Collection<PreviewVdbStatus>> entry : this.deploymentStatusMap.entrySet()) {
                for (PreviewVdbStatus status : entry.getValue()) {
                    status.setDeploy(true);
                }
            }
        } finally {
            this.statusLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    @Override
    public void resourceChanged( IResourceChangeEvent event ) {
        if (!isPreviewEnabled()) return;
        // ResourceChangeUtilities.debug(event);

        if (ResourceChangeUtilities.isPreClose(event)) {
            IProject project = (IProject)event.getResource();

            if (ModelerCore.hasModelNature(project)) {
                modelProjectClosed(project, this.context);
            }
        } else if (ResourceChangeUtilities.isPreDelete(event)) {
            IProject project = (IProject)event.getResource();

            if (project.isOpen()) {
                if (ModelerCore.hasModelNature(project)) {
                    modelProjectDeleted(project, this.context);
                }
            } else if (this.deploymentStatusMap.containsKey(project.getFullPath())) {
                // you can't check the nature of a closed project so see if in deployment map delete it
                modelProjectDeleted(project, this.context);
            }
        } else if (event.getResource() == null) {
            IResourceDelta delta = event.getDelta();
            IResource deltaResource = delta.getResource();

            if (deltaResource instanceof IWorkspaceRoot) {
                for (IResourceDelta kidDelta : delta.getAffectedChildren()) {
                    IResource kidResource = kidDelta.getResource();

                    if (kidResource instanceof IProject) {
                        IProject project = (IProject)kidResource;

                        if (ModelerCore.hasModelNature(project)) {
                            if (ResourceChangeUtilities.isOpened(kidDelta)) {
                                modelProjectOpened(project);
                            } else if (ResourceChangeUtilities.isChanged(kidDelta)) {
                                modelProjectChanged(kidDelta);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param pvdbPath the path of the PVDB whose deploy status is being changed
     * @param deploy <code>true</code> if the PVDB needs to be deployed
     */
    private void setNeedsToBeDeployedStatus( IFile pvdb,
                                             boolean deploy ) {
        assert (pvdb != null) : "PVDB is null"; //$NON-NLS-1$
        setNeedsToBeDeployedStatus(pvdb.getFullPath(), deploy);
    }

    /**
     * @param pvdbPath the path of the PVDB whose deploy status is being changed
     * @param deploy <code>true</code> if the PVDB needs to be deployed
     */
    private void setNeedsToBeDeployedStatus( IPath pvdbPath,
                                             boolean deploy ) {
        PreviewVdbStatus status = getStatus(pvdbPath);
        status.setDeploy(deploy);
    }

    private void setPreviewServer( Server server ) {
        final PreviewContext previewContext = this.context;
        final Server oldServer = getPreviewServer();

        // set new server
        this.previewServer.set(server);

        // mark all PVDBs as needing to be deployed
        resetAllDeployedStatuses();

        // cleanup old server if it can be reached
        if ((oldServer != null) && oldServer.ping().isOK()) {
            PreviewContext oldContext = new PreviewContext() {

                @Override
                public IStatus ensureConnectionInfoIsValid( Vdb previewVdb,
                                                            Server previewServer ) throws Exception {
                    return previewContext.ensureConnectionInfoIsValid(previewVdb, oldServer);
                }

                @Override
                public IFile getPreviewVdb( IResource projectOrModel ) {
                    return previewContext.getPreviewVdb(projectOrModel);
                }

                @Override
                public String getPreviewVdbDeployedName( IPath pvdbPath ) {
                    return previewContext.getPreviewVdbDeployedName(pvdbPath);
                }

                @Override
                public String getPreviewVdbJndiName( IPath pvdbPath ) {
                    return previewContext.getPreviewVdbJndiName(pvdbPath);
                }
            };

            // delete all Preview VDBs on old server
            for (IProject project : getAllProjects()) {
                for (IFile pvdbFile : findProjectPvdbs(project, false)) {
                    Job deleteDeployedPvdbJob = new DeleteDeployedPreviewVdbJob(getPreviewVdbDeployedName(pvdbFile),
                                                                                getPreviewVdbVersion(pvdbFile),
                                                                                getPreviewVdbJndiName(pvdbFile), oldContext,
                                                                                oldServer);
                    deleteDeployedPvdbJob.schedule();
                }
            }
        }
    }

    /**
     * Shutdowns the <code>PreviewManager</code>. This will remove any Preview VDBs from the default Teiid server.
     */
    public void shutdown( IProgressMonitor monitor ) throws Exception {
        // remove listeners
        ModelerCore.getModelContainer().getChangeNotifier().removeListener(this);
        DqpPlugin.getInstance().getPreferences().removePreferenceChangeListener(this);

        // cleanup PVDs
        if ((getPreviewServer() != null) && isPreviewEnabled()) {
            for (IProject project : getAllProjects()) {
                // Exception thrown if project is already closed.
                if (project.isOpen()) {
                    modelProjectDeleted(project, this.context);
                }
            }
        }
    }

    /**
     * Tasks done when first constructed.
     */
    private void startup() throws Exception {
        // add listeners
        ModelerCore.getModelContainer().getChangeNotifier().addListener(this);
        DqpPlugin.getInstance().getPreferences().addPreferenceChangeListener(this);

        // get current preference value to see if preview should be enabled
        IEclipsePreferences prefs = DqpPlugin.getInstance().getPreferences();
        this.previewEnabled = prefs.getBoolean(PreferenceConstants.PREVIEW_ENABLED, PreferenceConstants.PREVIEW_ENABLED_DEFAULT);

        // when Eclipse starts you don't get an open project event so pretend we did get one and call the event handler
        if (this.previewEnabled) {
            for (IProject project : getAllProjects()) {
                if (project.isOpen() && (ModelerCore.hasModelNature(project))) {
                    modelProjectOpened(project);
                }
            }
        }
    }

    class PreviewVdbStatus {

        private boolean deploy = true;

        private final IFile pvdb;

        public PreviewVdbStatus( IFile pvdb ) {
            this.pvdb = pvdb;
        }

        @Override
        public boolean equals( Object obj ) {
            if ((obj != null) && (obj instanceof PreviewVdbStatus)) {
                PreviewVdbStatus other = (PreviewVdbStatus)obj;
                return getPath().equals(other.getPath());
            }

            return false;
        }

        public IFile getFile() {
            return this.pvdb;
        }

        public IPath getPath() {
            return this.pvdb.getFullPath();
        }

        @Override
        public int hashCode() {
            return getPath().hashCode();
        }

        public void setDeploy( boolean deploy ) {
            this.deploy = deploy;
        }

        public boolean shouldDeploy() {
            return this.deploy;
        }

        @Override
        public String toString() {
            return getPath().toString();
        }
    }

}
