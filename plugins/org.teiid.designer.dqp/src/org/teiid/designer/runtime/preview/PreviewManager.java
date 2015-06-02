/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;
import static org.teiid.designer.runtime.DqpPlugin.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.ModelerCoreRuntimeException;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.common.xmi.XMIHeader;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.util.CoreModelObjectNotificationHelper;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelFileUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.core.workspace.ResourceChangeUtilities;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.datatools.JdbcTranslatorHelper;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.datatools.connection.DataSourceConnectionConstants;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.function.FunctionPackage;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.webservice.WebServicePackage;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.TeiidDataSourceFactory;
import org.teiid.designer.runtime.preview.jobs.CompositePreviewJob;
import org.teiid.designer.runtime.preview.jobs.CreatePreviewVdbJob;
import org.teiid.designer.runtime.preview.jobs.DeleteDeployedPreviewVdbJob;
import org.teiid.designer.runtime.preview.jobs.DeletePreviewVdbJob;
import org.teiid.designer.runtime.preview.jobs.ModelChangedJob;
import org.teiid.designer.runtime.preview.jobs.ModelProjectOpenedJob;
import org.teiid.designer.runtime.preview.jobs.UpdatePreviewVdbJob;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent.EventType;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent.TargetType;
import org.teiid.designer.runtime.spi.FailedTeiidDataSource;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.spi.TeiidExecutionException;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.VdbSource;
import org.teiid.designer.vdb.VdbSourceInfo;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.XmiVdb;


/**
 * The <code>PreviewManager</code> is responsible for keeping the hidden Preview VDBs synchronized with the workspace. Also, the
 * Preview Manager is responsible for deploying the Preview VDBs to Teiid to enable data preview. The Preview Manager also removes
 * (undeploys) Preview VDBs from Teiid when their associate workspace model is deleted.
 * <p>
 * For each previewable model in the workspace, an associated Preview VDB will be maintained. This PVDB will be hidden from the
 * user but will be contained within the workspace. When a model is changed, the PVDB is automatically synchronized. When a model
 * is deleted, the PVDB is also deleted. Upon preview of a model object, the PVDB of that model, along with any dependent models
 * will be deployed to the designated preview Teiid Instance (if there is one). Deploying of the PVDBs is only done if necessary.
 *
 * @since 8.0
 */
@ThreadSafe
public final class PreviewManager extends JobChangeAdapter
    implements IExecutionConfigurationListener, IPreferenceChangeListener, IResourceChangeListener, INotifyChangedListener,
    PreviewContext {

    private static final String PROJECT_VDB_SUFFIX = "_project"; //$NON-NLS-1$
    
    String projectPreviewVdbName = null;

    /**
     * @param targetVdb the version of the source VDB
     * @param modelPreviewVdbs list of preview vdbs for models in the project
     * @throws Exception if there is a problem with the merge
     */
    public void addPreviewVdbImports( Vdb targetVdb, List<IFile> modelPreviewVdbs) throws Exception {
    
    	// merge into project PVDB
        for (IFile previewVdbFile : modelPreviewVdbs) {
        	if (targetVdb.getFile().equals(previewVdbFile)) continue;
        	
        	// REMOVE the .vdb extension for the source vdb
	        String modelPreviewVdbName = previewVdbFile.getFullPath().removeFileExtension().lastSegment().toString();
	        targetVdb.addImportVdb(modelPreviewVdbName);
	    }	
    }
    
    /**
     * @param vdb targetVdb the version of the source VDB
     * @param currentModelProject the version of the target VDB
     * @throws Exception if there is a problem with the merge
     */
    public void addPreviewSourceVdbImports( Vdb vdb, IProject currentModelProject ) throws Exception {
    
    	// merge into project PVDB
    	List<IResource> filesInProject = getVdbSourceResourcesForProject(currentModelProject);
        for (IResource fileInProject : filesInProject) {
        	ModelResource mr = ModelUtil.getModelResource((IFile)fileInProject, true);
        	if ( mr != null && ModelUtil.isVdbSourceObject(fileInProject) ) {
	        	String vdbName = getVdbSourceModelVdbName(mr);
	        	vdb.addImportVdb(vdbName);
        	}
	    }	
    }
    
    
    /**
     * @param modelFile the model whose dependencies are being checked
     * @param pvdbFile the Preview VDB whose model entry is being checked to see if it is a dependency
     * @return <code>true</code> if the model depends on the Preview VDBs model entry
     */
    private boolean dependsOn( IFile modelFile,
                               IFile pvdbFile ) throws Exception {
        // TODO implement dependsOn
        assert (ModelUtil.isVdbArchiveFile(pvdbFile)) : "IFile is not a VDB"; //$NON-NLS-1$
        
        Vdb pvdb = new XmiVdb(pvdbFile, null);
        Set<VdbEntry> models = pvdb.getModelEntries();
        // project PVDB has no entries
        if (!models.isEmpty()) {
            IFile file = models.iterator().next().findFileInWorkspace();

            for (IResource dependency : WorkspaceResourceFinderUtil.getDependentResources(modelFile)) {
                if (dependency.equals(file)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param project the project whose Preview VDB is being requested (may not be <code>null</code>)
     * @return the name of the project's Preview VDB (never <code>null</code>)
     */
    public static String getPreviewProjectVdbName( IProject project ) {
        assert (project != null) : "Project is null"; //$NON-NLS-1$
        StringBuilder name = new StringBuilder(XmiVdb.getPreviewVdbPrefix(project));
        String vdbName = name.append(project.getName()).append(PROJECT_VDB_SUFFIX).toString();
        if (vdbName.contains(StringUtilities.SPACE)) {
            vdbName = vdbName.replaceAll(StringUtilities.SPACE, StringUtilities.UNDERSCORE);
        }
        return vdbName;
    }

    /**
     * @param pvdbFile the Preview VDB (may not be <code>null</code> and must be a VDB)
     * @return the version of the given Preview VDB
     * @throws Exception
     */
    public static int getPreviewVdbVersion( IFile pvdbFile ) throws Exception {
        assert (pvdbFile != null) : "PVDB is null"; //$NON-NLS-1$
        assert (ModelUtil.isVdbArchiveFile(pvdbFile)) : "IFile is not a VDB"; //$NON-NLS-1$

        return VdbUtil.getVdbVersion(pvdbFile);
    }

    /**
     * @param file the file being checked (may not be <code>null</code>)
     * @return <code>true</code> if the file is a previewable model
     */
    public static boolean isPreviewableResource( IFile file ) {
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
                    if (descriptor == null) {
                        return false;
                    }
                    metamodelUri = descriptor.getNamespaceURI();
                } catch (ModelWorkspaceException e) {
                    Util.log(e);
                    return false;
                }
            }

            // must be from a relational or web service model
            if (RelationalPackage.eNS_URI.equals(metamodelUri) || WebServicePackage.eNS_URI.equals(metamodelUri)
                || XmlDocumentPackage.eNS_URI.equals(metamodelUri) || FunctionPackage.eNS_URI.equals(metamodelUri)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isProjectPreviewVdb( IFile pvdbFile ) throws Exception {
        if (PreviewManager.isPreviewVdb(pvdbFile)) {
            return pvdbFile.getFullPath().removeFileExtension().toString().endsWith("tion"); //$NON-NLS-1$
        }

        return false;
    }

    /**
     * @param resource the resource being checked
     * @return <code>true</code> if the resource is a Preview VDB
     */
    private static boolean isPreviewVdb( IResource resource ) throws Exception {
        if (ModelUtil.isVdbArchiveFile(resource)) {
        	return VdbUtil.isPreviewVdb((IFile)resource);
        }
        return false;
    }

    /**
     * A flag indicating if preview is enabled. This will match the value of preferenced
     * {@link PreferenceConstants#PREVIEW_ENABLED}.
     */
    private boolean previewEnabled = true;

    /**
     * The Teiid Instance being used for preview (may be <code>null</code>).
     */
    private volatile AtomicReference<ITeiidServer> previewServer = new AtomicReference<ITeiidServer>();

    /**
     * A map with the project path as the key and a collection of PVDBs and there deploy flag indicating if the PVDB needs to be
     * deployed.
     */
    @GuardedBy( "statusLock" )
    private Map<IPath, Set<PreviewVdbStatus>> deploymentStatusMap = new HashMap<IPath, Set<PreviewVdbStatus>>();

    /**
     * Lock used for when accessing PVDB deployment status.
     */
    private final ReadWriteLock statusLock = new ReentrantReadWriteLock();

    /**
     * Tracking flag for signalling that the preview manager should try
     * to update the preview vdbs on the Teiid Instance. Turned on if vdb
     * update originally fails and the server is not connected.
     */
    private boolean retryOnNextRefreshOfServer;

    /**
     * Singleton instance of the preview manager
     */
    private static PreviewManager instance;

    /**
     * @return singleton instance
     */
    public static PreviewManager getInstance() {
        if (instance == null)
            instance = new PreviewManager();

        return instance;
    }

    /**
     * Constructs a <code>PreviewManager</code> using the default {@link PreviewContext}.
     *
     * @throws Exception if there is a problem initializing the preview feature
     */
    private PreviewManager() {
        startup();
    }

    /**
     * @param pvdb the PVDB being added to the workspace
     */
    private void addWorkspacePvdb( IFile pvdb ) {
        IPath projectPath = pvdb.getProject().getFullPath();

        try {
            this.statusLock.writeLock().lock();
            Set<PreviewVdbStatus> statuses = this.deploymentStatusMap.get(projectPath);

            if (statuses == null) {
                statuses = new HashSet<PreviewVdbStatus>();
                this.deploymentStatusMap.put(projectPath, statuses);
            }

            statuses.add(new PreviewVdbStatus(pvdb));
        } finally {
            this.statusLock.writeLock().unlock();
        }
    }

    /**
     * Preview VDBs don't have markers like non-preview VDBs. So we have to go get the model out of the PVDB and see if it has any
     * errors.
     * 
     * @param pvdb the Preview VDB being checked for errors
     * @return a status indicating if the model inside the PVDB has any errors
     */
    IStatus checkPreviewVdbForErrors( Vdb pvdb ) {
        // a PVDB will have either zero entries (if a project PVDB) or one model entry
        Set<VdbEntry> modelEntries = pvdb.getModelEntries();

        if (!modelEntries.isEmpty()) {
            try {
                IFile modelFile = modelEntries.iterator().next().findFileInWorkspace();

                // if the model inside the PVDB has errors return an error status
                for (IMarker marker : modelFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
                    Object attr = marker.getAttribute(IMarker.SEVERITY);

                    if (attr != null) {
                        int severity = ((Integer)attr).intValue();

                        if (severity == IMarker.SEVERITY_ERROR) {
                            return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.ModelErrorMarkerExists,
                                                                                 marker.getAttribute(IMarker.MESSAGE, ""))); //$NON-NLS-1$
                        }
                    }
                }
            } catch (Exception e) {
                return new Status(IStatus.ERROR, PLUGIN_ID, Messages.UnexpectedErrorGettingVdbMarkers, e);
            }
        }

        return Status.OK_STATUS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.spi.IExecutionConfigurationListener#configurationChanged(org.teiid.designer.runtime.spi.ExecutionConfigurationEvent)
     */
    @Override
    public void configurationChanged( ExecutionConfigurationEvent event ) {
        if (event.getEventType().equals(EventType.DEFAULT) && event.getTargetType().equals(TargetType.SERVER)) {
            setPreviewServer(event.getUpdatedServer());
        } else if (event.getEventType().equals(EventType.REFRESH) && retryOnNextRefreshOfServer) {
            try {
                startup();
            } catch (Exception ex) {
                // Swallow this exception since it should have already been
                // reported when the application was started.
            }
        // Vdb Removed
        } else if (event.getEventType().equals(EventType.REMOVE) && event.getTargetType().equals(TargetType.VDB)) {
        	resetAllDeployedStatuses();
        } else if (event.getEventType().equals(EventType.CONNECTED)) {
            try {
                // Clear up the server's previews on refreshing the client connection
                cleanServer(new NullProgressMonitor(), event.getServer());
            } catch (Exception ex) {
                DqpPlugin.Util.log(ex);
            }
        }
    }

    private Job createDeleteDeployedPreviewVdbJob( IPath pvdbPath ) {
        // delete deployed PVDB
        IFile pvdbFile = getFile(pvdbPath);
        String jndiName = this.getPreviewVdbJndiName(pvdbPath);
        Job job = new DeleteDeployedPreviewVdbJob(this.getPreviewVdbDeployedName(pvdbPath),
                                                  jndiName, this, getPreviewServer());
        return job;
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
     *      org.teiid.designer.runtime.spi.ITeiidServer)
     */
    @Override
    public IStatus ensureConnectionInfoIsValid( Vdb previewVdb,
                                                ITeiidServer previewServer )  throws Exception {
        assert (previewServer != null) : "Preview server is null"; //$NON-NLS-1$

        if (previewVdb.getModelEntries().isEmpty()) {
            return Status.OK_STATUS;
        }

        // PVDB always contain either zero (project PVDBs) or one model
        VdbModelEntry modelEntry = (VdbModelEntry)previewVdb.getModelEntries().iterator().next();
        IFile model = modelEntry.findFileInWorkspace();
        ModelResource modelResource = ModelUtil.getModelResource(model, true);

        boolean isSourceModel = modelResource.getModelType() == ModelType.PHYSICAL_LITERAL;

        if (!isSourceModel) {
            return Status.OK_STATUS;
        }

        IStatus connectionInfoError = null;

        // if we have a preview server and connection information on the model then assign a data source
        IConnectionInfoHelper helper = new ConnectionInfoHelper();

        if (helper.hasConnectionInfo(modelResource)) {
        	// Get JBossDs JNDI name - if available, it is used
        	String srcJndiName = helper.getJndiProperty(modelResource);
        	String translatorName = helper.getTranslatorName(modelResource);
        	
        	if( ! DataSourceConnectionConstants.Translators.LOOPBACK.equals(translatorName) ) {
	        	// JBossDs JNDI found - use it
	        	if(!CoreStringUtil.isEmpty(srcJndiName)) {
	        		modelEntry.setJndiName(0,srcJndiName);
	        	// Not found - default to original method
	        	} else {
	        		String jndiName = this.getPreviewVdbJndiName(previewVdb.getFile().getFullPath());
	        		// create data source on server if we need to
	        		if (!previewServer.dataSourceExists(jndiName)) {
	        			TeiidDataSourceFactory factory = new TeiidDataSourceFactory();
	        			ITeiidDataSource theDataSource = null;
	        			try {
	        				theDataSource = factory.createDataSource(previewServer, model, jndiName, true);
						} catch (TeiidExecutionException ex) {
							switch( ex.getCode() ) {
								case ITeiidDataSource.ERROR_CODES.JDBC_DRIVER_SOURCE_NOT_FOUND: {
									connectionInfoError = new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), null);
								} break;
								case ITeiidDataSource.ERROR_CODES.DATA_SOURCE_TYPE_DOES_NOT_EXIST_ON_SERVER: {
									connectionInfoError = new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), null);						
								} break;
								case ITeiidDataSource.ERROR_CODES.DATA_SOURCE_COULD_NOT_BE_CREATED: {
									connectionInfoError = new Status(IStatus.ERROR, PLUGIN_ID, ex.getMessage(), null);
								} break;
								
								default: break;
							}
						}
	        			if( theDataSource instanceof FailedTeiidDataSource ) {
	        				FailedTeiidDataSource fds = (FailedTeiidDataSource)theDataSource;
	        				
	        				switch( fds.getReasonCode() ) {
		        				case ITeiidDataSource.ERROR_CODES.NO_CONNECTION_PROVIDER: {
		        					connectionInfoError = new Status(IStatus.ERROR, PLUGIN_ID, 
											"No connection provider associated with model: '" + fds.getModelName() + "'. Could not create data source",  null);
								} break;
		        				case ITeiidDataSource.ERROR_CODES.NO_CONNECTION_PROFILE_DEFINED_IN_MODEL: {
		        					connectionInfoError = new Status(IStatus.ERROR, PLUGIN_ID, 
											"No connection profile associated with model: '" + fds.getModelName() + "'. Could not create data source",  null);
								} break;
		        				case ITeiidDataSource.ERROR_CODES.NO_TEIID_RELATED_PROPERTIES_IN_PROFILE: {
									connectionInfoError = new Status(IStatus.ERROR, PLUGIN_ID, 
											"No Teiid connection properties associated with model: '" + fds.getModelName() + "'. Could not create data source",  null);
								} break;
	        				}
	        			}
	        		}
	                if (!jndiName.equals(getSourceJndiName(modelEntry)) ) {
	                    modelEntry.setJndiName(0, jndiName);
	                }
	        	}
        	}
        } else {
            connectionInfoError = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.ModelDoesNotHaveConnectionInfoError,
                                                                                model.getFullPath()), null);
        }

        // If modelEntry translator name is null (or 'loopback'), see if it can be overridden.
        //   - need to check for loopback, since it may be used for default (see below)
        String vdbSrcTranslatorName = getSourceTranslatorName(modelEntry);
        String resourceTranslatorName = helper.getTranslatorName(modelResource);
        boolean modelEntryHasLoopback = CoreStringUtil.isEmpty(vdbSrcTranslatorName) || vdbSrcTranslatorName.equalsIgnoreCase(DataSourceConnectionConstants.Translators.LOOPBACK);
        boolean resourceHasLoopback = !CoreStringUtil.isEmpty(resourceTranslatorName) && resourceTranslatorName.equalsIgnoreCase(DataSourceConnectionConstants.Translators.LOOPBACK);
        
        // ONLY check for connection profile if resource translator AND vdb model entry translator are not both LOOPBACK
        // If not, then check for connection profile. If no profile, then use loopback. if Profile, then get the translator from the profile.
        if ( modelEntryHasLoopback && !resourceHasLoopback) {
    		IConnectionProfile connectionProfile = helper.getConnectionProfile(modelResource);

    		// get translator
    		if (connectionProfile == null) {
    			// Teiid throws an error when deploying VDB if translator is not set so set to LOOPBACK as default
    			modelEntry.setTranslatorName(0, DataSourceConnectionConstants.Translators.LOOPBACK);
    		} else {
    			modelEntry.setTranslatorName(0, JdbcTranslatorHelper.getTranslator(connectionProfile));
    		}
        }

        return ((connectionInfoError == null) ? Status.OK_STATUS : connectionInfoError);
        
    }

    /**
     * Handler for a resource change event indicating the specified file has been deleted.
     * 
     * @param file the file that has been deleted
     */
    private void fileDeleted( IFile file ) {
        if (ModelUtil.isModelFile(file.getFullPath())) {
            try {
                IFile pvdbFile = this.getPreviewVdb(file);

                // if the Preview VDB exists then the associated model was selected and deleted by user
                if ((pvdbFile != null) && pvdbFile.exists()) {
                    DeletePreviewVdbJob job = new DeletePreviewVdbJob(this, file);
                    job.addJobChangeListener(this);
                    job.schedule();
                } else {
                    // Preview VDB doesn't exist so user must've deleted a folder. Just delete the deployed PVDB.
                    Job job = new DeleteDeployedPreviewVdbJob(getPreviewVdbDeployedName(pvdbFile),
                                                              getPreviewVdbJndiName(pvdbFile),
                                                              this, getPreviewServer());
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

            // make copy in case another thread is updating
            if ((statuses != null) && !statuses.isEmpty()) {
                statuses = new ArrayList<PreviewVdbStatus>(statuses);
            }
        } finally {
            this.statusLock.readLock().unlock();
        }

        // statuses could be null at startup when the default teiid instance is being set
        if ((statuses != null) && !statuses.isEmpty()) {
            Collection<PreviewVdbStatus> missingPvdbs = new ArrayList<PreviewVdbStatus>();

            for (PreviewVdbStatus status : statuses) {
                IFile pvdbFile = status.getFile();

                // do a check to make sure the PVDB still exists
                if (pvdbFile.exists()) {
                    if (!onlyThoseNeedingToBeDeployed) {
                        pvdbsToDeploy.add(this.getPreviewVdb(status.getFile()));
                    } else {
                        if (needsToBeDeployed(pvdbFile)) {
                            pvdbsToDeploy.add(this.getPreviewVdb(pvdbFile));
                        }
                    }
                } else {
                    missingPvdbs.add(status);
                }
            }

            // if cache is out of sync cleanup (means there is a bug with maintaining the cache)
            if (!missingPvdbs.isEmpty()) {
                Util.log(Messages.DeployStatusCacheError);

                try {
                    this.statusLock.writeLock().lock();
                    statuses = this.deploymentStatusMap.get(project.getFullPath());

                    for (PreviewVdbStatus status : missingPvdbs) {
                        statuses.remove(status);
                    }
                } finally {
                    this.statusLock.writeLock().unlock();
                }
            }
        }

        return pvdbsToDeploy;
    }

    /**
     * @param container the container whose Preview VDBs are being requested
     * @param pvdbPaths a collection that will be filled with the paths to all the Preview VDBs under the specified container
     */
    private void findPvdbs( IContainer container,
                            List<IPath> pvdbPaths ) throws Exception {
        if (container instanceof IProject && ! ((IProject)container).isOpen())
            return;

        for (IResource resource : container.members(IContainer.INCLUDE_HIDDEN)) {
            if (resource instanceof IContainer) {
                findPvdbs((IContainer)resource, pvdbPaths);
            } else if (isPreviewVdb(resource)) {
                pvdbPaths.add(resource.getFullPath());
            }
        }
    }

    private IFile getFile( IPath path ) {
        return ModelerCore.getWorkspace().getRoot().getFile(path);
    }
    
    private String getFullDeployedVdbName(ITeiidVdb deployedVdb) { 
        String fullVdbName = deployedVdb.getDeployedName();

        if (!fullVdbName.endsWith(Vdb.FILE_EXTENSION)) {
            fullVdbName = fullVdbName + Vdb.FILE_EXTENSION;
        }
        return fullVdbName;
    	
    }

    ITeiidServer getPreviewServer() {
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
        return this.getPreviewVdbDeployedName(pvdb.getFullPath());
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
        return this.getPreviewVdbJndiName(pvdb.getFullPath());
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

    private String getPreviewVdbJndiName( String pvdbName ) {
        int index = pvdbName.lastIndexOf('.');

        if (index == -1) return pvdbName;
        String jndiName = pvdbName.substring(0, index);

        if (jndiName.contains(StringUtilities.SPACE)) {
            jndiName = jndiName.replaceAll(StringUtilities.SPACE, StringUtilities.UNDERSCORE);
        }
        return jndiName;
    }

    private String getResourceNameForPreviewVdb( IFile pvdbFile ) {
        // see if a project PVDB
        if (pvdbFile.getFullPath().removeFileExtension().lastSegment().endsWith(PROJECT_VDB_SUFFIX)) {
            String projectVdbName = pvdbFile.getProject().getName();
            if (projectVdbName.contains(StringUtilities.SPACE)) {
                projectVdbName = projectVdbName.replaceAll(StringUtilities.SPACE, StringUtilities.UNDERSCORE);
            }
            return projectVdbName;
        }

        String name = pvdbFile.getFullPath().removeFileExtension().lastSegment();
        String prefix = XmiVdb.getPreviewVdbPrefix(pvdbFile);
        int index = name.indexOf(prefix);

        if (index == -1) {
            return name;
        }

        // model PVDB
        return name.substring(index + prefix.length());
    }

    private String getPreviewVdbName( IResource projectOrModel ) {
        StringBuilder name = null;

        if (projectOrModel instanceof IProject) {
            name = new StringBuilder(PreviewManager.getPreviewProjectVdbName((IProject)projectOrModel));
        } else {
            assert (projectOrModel instanceof IFile) : "IResource is not an IFile"; //$NON-NLS-1$
            String prefix = XmiVdb.getPreviewVdbPrefix(projectOrModel);

            if (projectOrModel.getFileExtension().equalsIgnoreCase(ITeiidVdb.VDB_EXTENSION)) {
                String vdbName = projectOrModel.getFullPath().removeFileExtension().lastSegment();

                if (vdbName.startsWith(prefix)) {
                    return projectOrModel.getFullPath().lastSegment();
                }
            }

            name = new StringBuilder(prefix);
            name.append(projectOrModel.getFullPath().removeFileExtension().lastSegment());
        }

        name.append(Vdb.FILE_EXTENSION);
        return name.toString();
    }

    private IPath getProjectPath( IPath pvdbPath ) {
        return pvdbPath.uptoSegment(1);
    }
    
    private List<IResource> getVdbSourceResourcesForProject(IProject project) throws Exception {
        ModelResource[] mrs = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().getModelResources();
        List<IResource> resources = new ArrayList<IResource>();

        for (ModelResource mr : mrs) {
            if (mr.getModelProject().getProject().equals(project)) {
            	if( ModelUtil.isVdbSourceObject(mr) ) {
            		resources.add(mr.getCorrespondingResource());
            	}
            }
        }
        
        return resources;
    }

    private PreviewVdbStatus getStatus( IPath pvdbPath ) {
        Collection<PreviewVdbStatus> statuses = null;

        try {
            this.statusLock.readLock().lock();
            statuses = this.deploymentStatusMap.get(getProjectPath(pvdbPath));

            // make copy in case another thread is updating
            if ((statuses != null) && !statuses.isEmpty()) {
                statuses = new ArrayList<PreviewVdbStatus>(statuses);
            }
        } finally {
            this.statusLock.readLock().unlock();
        }

        if(statuses==null) return null;
        
        for (PreviewVdbStatus status : statuses) {
            if (status.getPath().equals(pvdbPath)) return status;
        }

        return null;
    }

    private String getVdbSourceModelVdbName(ModelResource modelResource) throws Exception {
    	if( modelResource.getModelAnnotation() != null) {
    		return ModelUtil.getVdbName(modelResource);
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
            && isPreviewableResource(file)) {
            modelChanged(file);
        } else if (ResourceChangeUtilities.isRemoved(fileDelta)) {
            // this occurs when a model is deleted or renamed (old model location)
            fileDeleted(file);
        } else if (ResourceChangeUtilities.isAdded(fileDelta) && isPreviewableResource(file)) {
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
        IProject project = job.getProject();
        List<IPath> pvdbPaths = new ArrayList<IPath>();

        try {
            findPvdbs(project, pvdbPaths);

            if (!pvdbPaths.isEmpty()) {
                CompositePreviewJob batchJob = new CompositePreviewJob(NLS.bind(Messages.DeleteOrphanedPreviewVdbsJob,
                                                                                project.getName()), this,
                                                                       getPreviewServer());
                Collection<PreviewVdbStatus> statuses = null;

                try {
                    this.statusLock.readLock().lock();
                    // these are the PVDBs that have associated models
                    statuses = this.deploymentStatusMap.get(project.getFullPath());

                    // make sure collection is not null
                    if (statuses == null) {
                        statuses = new ArrayList<PreviewVdbStatus>(0);
                    } else if (!statuses.isEmpty()) {
                        // make copy in case another thread is updating
                        statuses = new ArrayList<PreviewVdbStatus>(statuses);
                    }
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

                    // remove project part of path because it gets added back in later
                    IPath path = pvdbPath.removeFirstSegments(1);
                    IFile modelFile = project.getFile(path);

                    if (delete) {
                        // delete PVDB if there is no associated model
                        DeletePreviewVdbJob deletePvdbJob = new DeletePreviewVdbJob(modelFile, this);
                        batchJob.add(deletePvdbJob);
                    } else {
                        // Make sure PVDB is synchronized. One way a PVDB won't be synchronized is if the model was dirty when
                        // the project was closed and the user saved the file as part of the project being closed.
                        UpdatePreviewVdbJob updatePvdbJob = new UpdatePreviewVdbJob(modelFile, getPreviewServer(), this);
                        updatePvdbJob.addJobChangeListener(new JobChangeAdapter() {
                            
                            @Override
                            public void done(IJobChangeEvent event) {
                                /* 
                                 * If server is not connected then retry when it does. However,
                                 * when a server connects it only sends out a refresh event so
                                 * set a tracking flag.
                                 */
                                if( !event.getResult().isOK() && (getPreviewServer()==null || !getPreviewServer().isConnected()) ) {
                                    retryOnNextRefreshOfServer = true;
                                } else {
                                    retryOnNextRefreshOfServer = false;
                                }
                                
                                event.getJob().removeJobChangeListener(this);
                            }
                        });
                        batchJob.add(updatePvdbJob);
                    }
                }

                // run job
                batchJob.schedule();
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
        IFile pvdb = job.getPreviewVdb();
        if( pvdb != null ) {
        	addWorkspacePvdb(pvdb);
        }
    }

    /**
     * Handler for when a {@link DeletePreviewVdbJob} has finished.
     * 
     * @param job the job being processed
     */
    private void handlePreviewVdbDeleted( DeletePreviewVdbJob job ) {
        assert (job.completedSuccessfully()) : "Delete Preview VDB job did not complete successfully"; //$NON-NLS-1$
        previewVdbDeletedPostProcessing(job.getPreviewVdb().getFullPath());
    }

    /**
     * Handler for when a {@link UpdatePreviewVdbJob} has finished.
     * 
     * @param job the job being processed
     */
    private void handlePreviewVdbUpdated( UpdatePreviewVdbJob job ) {
        IFile changedModel = job.getModel();
        IFile pvdbFile = this.getPreviewVdb(changedModel);

        // change deploy status if necessary
        if ((job.getPreviewServer() != null) && !needsToBeDeployed(pvdbFile)) {
            setNeedsToBeDeployedStatus(pvdbFile, true);
        }
    }

    /**
     * @param project the project whose deployment status is being requested (may not be <code>null</code>)
     * @return <code>true</code> if this project has deployment status
     */
    private boolean hasDeploymentStatus( IProject project ) {
        assert (project != null) : "project is null"; //$NON-NLS-1$

        try {
            this.statusLock.readLock().lock();
            return this.deploymentStatusMap.containsKey(project.getFullPath());
        } finally {
            this.statusLock.readLock().unlock();
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
        assert isPreviewableResource(model) : "model is not previewable: " + model; //$NON-NLS-1$

        try {
            ModelChangedJob job = new ModelChangedJob(model, this, getPreviewServer());
            job.addChildJobChangeListener(this);
            job.schedule(500); // delay to let auto build start
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
     */
    private void modelProjectClosed( IProject project ) {
        // treat as if project was deleted
        modelProjectDeleted(project);
    }

    /**
     * Handler for a resource change event indicating the specified project will be deleted.
     * 
     * @param project the project being deleted (may not be <code>null</code>)
     */
    private void modelProjectDeleted( IProject project ) {
        assert (project != null) : "project is null"; //$NON-NLS-1$

        // all the Preview VDBs under this project have been deleted so delete project from status map and delete deployed PVDBs
        Collection<PreviewVdbStatus> statuses = null;

        try {
            this.statusLock.writeLock().lock();
            statuses = this.deploymentStatusMap.remove(project.getFullPath());
        } finally {
            this.statusLock.writeLock().unlock();
        }

        if (statuses != null) {
            try {
                for (PreviewVdbStatus status : statuses) {
                    Job job = createDeleteDeployedPreviewVdbJob(status.getPath());
                    job.schedule();
                }
            } catch (Exception e) {
                Util.log(IStatus.ERROR, e, NLS.bind(Messages.PreviewVdbDeletedPostProcessingError, project.getName()));
            }
        }
    }

    /**
     * Handler for a resource change event indicating the specified project has been opened.
     * 
     * @param project the project that was opened
     */
    private void modelProjectOpened( IProject project ) {
        try {
            ModelProjectOpenedJob job = new ModelProjectOpenedJob(project, this);
            job.addChildJobChangeListener(this);
            job.addJobChangeListener(this);
            job.schedule(1000); // delay to let build start
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, NLS.bind(Messages.ModelProjectOpenedJobError, project.getName()));
        }
    }

    /**
     * @param pvdbPath the path of the PVDB whose deploy status is being requested.
     * @return <code>true</code> if the PVDB needs to be deployed
     */
    boolean needsToBeDeployed( IFile pvdbFile ) {
        PreviewVdbStatus status = getStatus(pvdbFile.getFullPath());
        assert (status != null) : "PVDB status not found in status map:" + pvdbFile.getFullPath(); //$NON-NLS-1$
        if( status == null ) {
        	return false;
        }
        boolean deploy = status.shouldDeploy();
        if (!deploy) {
            // make sure server has a copy of the Preview VDB
            if (getPreviewServer() != null) {
                try {
                    String vdbName = getPreviewVdbDeployedName(pvdbFile);

                    // server does not have a copy. update status map.
                    if (getPreviewServer().hasVdb(vdbName)) {
                        setNeedsToBeDeployedStatus(pvdbFile, true);
                    }
                } catch (Exception e) {
                    Util.log(e);
                }
            }
        }

        return deploy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public void notifyChanged( Notification notification ) {
        if (!isPreviewEnabled()) return;

        CoreModelObjectNotificationHelper notificationHelper = new CoreModelObjectNotificationHelper(notification);
        if (notificationHelper.getModifiedResources().size() == 1) {
            IFile changedRes = (IFile)notificationHelper.getModifiedResources().get(0);
            for (Object obj : notificationHelper.getAddOrRemoveTargets()) {
                if (obj instanceof Annotation) {
                    if (((Annotation)obj).getAnnotatedObject() instanceof ModelAnnotation) {
                        Job job = createDeleteDeployedPreviewVdbJob(getPreviewVdb(changedRes).getFullPath());
                        job.schedule();
                        // System.out.println(" PreviewManager.notifyChanged() add/remove Target = "
                        // + ModelerCore.getModelEditor().getName((EObject)obj));
                    }
                }
            }
        }
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

            // if turning on preview make sure all PVDBs exist and are synchronized
            if (this.previewEnabled) {
                synchronizeWorkspace();
            } else {
                // mark all deploy statuses as needing to be deployed
                resetAllDeployedStatuses();
            }
        }
    }

    /**
     * @param objectToPreview the object to preview
     * @param monitor the progress monitor
     * @throws Exception if issues with setting up preview vdbs for project
     */
    @SuppressWarnings( {"unused", "deprecation"} )
    public void previewSetup( final Object objectToPreview,
                              IProgressMonitor monitor ) throws Exception {
        assert isPreviewEnabled() : "previewSetup should not be called if preview is disabled"; //$NON-NLS-1$
        ITeiidServer previewServer = getPreviewServer();
        assert (previewServer != null) : "Should not be called when preview server is null"; //$NON-NLS-1$
        
        ModelResource model = ModelUtil.getModel(objectToPreview);
        IFile modelToPreview = (IFile)model.getCorrespondingResource();
        IProject currentModelProject = modelToPreview.getProject();
        IFile pvdbFile = this.getPreviewVdb(modelToPreview);
        Vdb pvdb = new XmiVdb(pvdbFile, true, monitor);
        projectPreviewVdbName = getPreviewProjectVdbName(currentModelProject);

        List<IFile> projectPvdbsToDeploy = findProjectPvdbs(currentModelProject, true);

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

        // collect all the Preview VDB parent folders so that we can make sure workspace is in sync with file system
        Set<IContainer> parents = new HashSet<IContainer>();

        PREVIEW_MODEL_CONNECTION_INFO_TASK: {
            // make sure all connection information is valid
            if (model.getModelType() == ModelType.PHYSICAL_LITERAL) {
                monitor.subTask(NLS.bind(Messages.PreviewSetupConnectionInfoTask, model.getItemName()));
                IStatus status = this.ensureConnectionInfoIsValid(pvdb, previewServer);

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
                getPreviewServer().deployVdb(pvdbFile);
                setNeedsToBeDeployedStatus(pvdbFile, false);
            }

            monitor.worked(1);

            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
        }

        // collection for PVDBs that will be deployed and merged into the project PVDB
        List<IFile> affectedPreviewVDBs = new ArrayList<IFile>(projectPvdbsToDeploy.size());

        // deploy any project PVDBs if necessary
        for (IFile projectPvdbFile : projectPvdbsToDeploy) {
            PROJECT_MODEL_DEPLOY_TASK: {
        	   
                if (pvdbFile.equals(projectPvdbFile)) {
                    monitor.worked(1);
                    continue;
                }
                
                Vdb projectModelPvdb = new XmiVdb(projectPvdbFile, true, null);
                
                // make sure no errors in any models that are dependencies of the model being previewed
                String name = getResourceNameForPreviewVdb(projectPvdbFile);
                monitor.subTask(NLS.bind(Messages.PreviewSetupValidationCheckTask, name));
                IStatus status = checkPreviewVdbForErrors(projectModelPvdb);
                boolean error = false;
                
                // if the model has an error only throw exception if that model is a dependency
                if (status.getSeverity() == IStatus.ERROR) {
                    error = true;

                    if (dependsOn(modelToPreview, projectPvdbFile)) {
                        throw new CoreException(status);
                    }
                    
                    affectedPreviewVDBs.add(projectPvdbFile);
                } 

                if (!error) {
                    monitor.subTask(NLS.bind(Messages.PreviewSetupConnectionInfoTask, name));
                    status = this.ensureConnectionInfoIsValid(projectModelPvdb, previewServer);

                    // save if necessary
                    boolean wasSaved = false;
                    if (projectModelPvdb.isModified()) {
                        wasSaved = true;
                        projectModelPvdb.save(monitor);
                    }

                    // make sure parent is in sync with file system
                    IContainer parent = projectPvdbFile.getParent();
                    monitor.subTask(NLS.bind(Messages.PreviewSetupRefreshWorkspaceTask, parent.getFullPath()));

                    if (!(parent instanceof IWorkspaceRoot) && (wasSaved || !parents.contains(parent))) {
                        refreshLocal(parent);
                        parents.add(parent);
                    }

                    // deploy PVDB
                    monitor.subTask(NLS.bind(Messages.PreviewSetupDeployTask, name));
                    
                    try {
                        getPreviewServer().deployVdb(projectPvdbFile);
                        setNeedsToBeDeployedStatus(projectPvdbFile, false);
                    } catch (Exception e) {
                        // only care if server exception when deploying a PVDB that is a dependency or a project PVDB
                        if (dependsOn(modelToPreview, projectPvdbFile) || PreviewManager.isProjectPreviewVdb(projectPvdbFile)) {
                            String modelName = getResourceNameForPreviewVdb(projectPvdbFile);
                            status = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.DeployPreviewVdbDependencyError,
                                                                                   modelName), e);
                            throw new CoreException(status);
                        }

                        // make sure this PVDB does not get merged since it didn't get deployed
                        affectedPreviewVDBs.remove(projectPvdbFile);
                    }

                    if (monitor.isCanceled()) {
                        throw new InterruptedException();
                    }
                }

                monitor.worked(1); // end deploy task
            }
        }

        //Find and set the project level PVDB
        IFile projectVdbIFile = null;
        List<IFile> localPreviewVdbs = findProjectPvdbs(currentModelProject, false);
        for (IFile projectVdb : localPreviewVdbs){
        	if (isProjectLevelPreviewVdb(projectVdb)){
        		projectVdbIFile = projectVdb;
        		break;
        	}
        }
        
        localPreviewVdbs.removeAll(affectedPreviewVDBs);
        
        // merge into project PVDB
        MERGE_TASK: {
            monitor.subTask(NLS.bind(Messages.PreviewSetupMergeTask, projectPreviewVdbName));

            if( getPreviewServer().getServerVersion().isSevenServer()) {
	            VERSION_7_7_MERGE: {
	                // merge into project PVDB
            		affectedPreviewVDBs.add(pvdbFile); // add in model being previewed
            		
	                for (IFile pvdbToMerge : affectedPreviewVDBs) {
                        String name = getResourceNameForPreviewVdb(pvdbToMerge);
                        monitor.subTask(NLS.bind(Messages.PreviewSetupMergeTask, name));

                        // REMOVE the .vdb extension for the source vdb
                        String sourceVdbName = pvdbToMerge.getFullPath().removeFileExtension().lastSegment().toString();
                        String projectPreviewVdbName = getPreviewProjectVdbName(modelToPreview.getProject());

                        if (!sourceVdbName.equals(projectPreviewVdbName)) {
                        	getPreviewServer().mergeVdbs(sourceVdbName, PreviewManager.getPreviewVdbVersion(pvdbToMerge), projectPreviewVdbName, 1);
                        }

                        monitor.worked(1);

                        if (monitor.isCanceled()) {
                            throw new InterruptedException();
                        }
                    }
	            }
            } else {
            	VERSION_8_8_DEPLOY : {
		            if (projectVdbIFile != null) {
		            	ITeiidVdb deployedProjectVdb = getPreviewServer().getVdb(projectPreviewVdbName);
		            	Vdb localProjectVdb = new XmiVdb(projectVdbIFile, new NullProgressMonitor());
		            	localProjectVdb.removeAllImportVdbs();
		            	// Add imports for all preview vdbs under project
		                addPreviewVdbImports(localProjectVdb, localPreviewVdbs);
		                // Add imports for all VDB source models within this project
		                addPreviewSourceVdbImports(localProjectVdb, currentModelProject);
		                
		                if( deployedProjectVdb != null ) {
			                String fullProjectVdbName = getFullDeployedVdbName(deployedProjectVdb);
		                	getPreviewServer().undeployVdb(fullProjectVdbName);
		                }
		                localProjectVdb.save(null);
		                localProjectVdb.getFile().refreshLocal(IResource.DEPTH_INFINITE, null);
		                getPreviewServer().deployVdb(localProjectVdb.getFile()); 
		            }
	            }
            }

            monitor.worked(1);

            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
        }
       
        monitor.done();
    }
    
	/**
	 * @param pvdbFile
	 * @return
	 */
	private boolean isProjectLevelPreviewVdb(IFile pvdbFile) {
		return pvdbFile.getFullPath().toString().contains(projectPreviewVdbName);
	}

    /**
     * Must be called after a Preview VDB is deleted.
     * 
     * @param pvdbPath the path of the Preview VDB that was just deleted (never <code>null</code>)
     */
    private void previewVdbDeletedPostProcessing( IPath pvdbPath ) {
        deleteWorkspacePvdb(pvdbPath);

        // delete deployed PVDB
        Job job = createDeleteDeployedPreviewVdbJob(pvdbPath);
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
            this.statusLock.readLock().lock();

            // set all PVDBs to needing to be deployed
            for (Map.Entry<IPath, Set<PreviewVdbStatus>> entry : this.deploymentStatusMap.entrySet()) {
                for (PreviewVdbStatus status : entry.getValue()) {
                    status.setDeploy(true);
                }
            }
        } finally {
            this.statusLock.readLock().unlock();
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
                modelProjectClosed(project);
            }
        } else if (ResourceChangeUtilities.isPreDelete(event)) {
            IProject project = (IProject)event.getResource();

            if (hasDeploymentStatus(project)) {
                modelProjectDeleted(project);
            }
        } else if (event.getResource() == null) {
            IResourceDelta delta = event.getDelta();
            IResource deltaResource = delta.getResource();

            if (deltaResource instanceof IWorkspaceRoot) {
                for (IResourceDelta kidDelta : delta.getAffectedChildren()) {
                    IResource kidResource = kidDelta.getResource();

                    if (kidResource instanceof IProject) {
                        IProject project = (IProject)kidResource;

                        if (ModelerCore.hasModelNature(project) && project.isOpen()) {
                            if (ResourceChangeUtilities.isOpened(kidDelta) || ResourceChangeUtilities.isDescriptionChanged(kidDelta) ) {
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
        if( status != null ) {
        	status.setDeploy(deploy);
        }
    }

    /**
     * Cycles through all projects in the workspace and creates
     * jobs to delete each preview vdb and re-create it.
     *
     * @return list of jobs for processing the preview vdbs
     */
    private Collection<Job> rebuildPreviewVdbs() {
        List<Job> jobs = new ArrayList<Job>();

        /*
         * Loop through all the projects and find their preview vdbs
         */
        for (IProject project : DotProjectUtils.getOpenModelProjects()) {
            List<IPath> pvdbPaths = new ArrayList<IPath>();
            try {
                if (project.isOpen() && ModelerCore.hasModelNature(project)) {
                    findPvdbs(project, pvdbPaths);
                }
            } catch (Exception ex) {
                Util.log(ex);
            }

            /*
             * For each preview vdb, create a delete preview job
             * for the file
             */
            boolean previewVdbsDeleted = false;
            for (IPath pvdbPath : pvdbPaths) {
                IPath path = pvdbPath.removeFirstSegments(1);
                IFile modelFile = project.getFile(path);

                DeletePreviewVdbJob deletePvdbJob;
                try {
                    deletePvdbJob = new DeletePreviewVdbJob(modelFile, this);
                    jobs.add(deletePvdbJob);
                    previewVdbsDeleted = true;
                } catch (Exception ex) {
                    Util.log(ex);
                }
            }

            /*
             * If any preview vdbs of this project are to be deleted then they also
             * need to be recreated against the new default teiid instance so add an appropriate job.
             */
            if (previewVdbsDeleted) {
                try {
                    ModelProjectOpenedJob job = new ModelProjectOpenedJob(project, this);
                    jobs.add(job);
                } catch (Exception ex) {
                    Util.log(ex);
                }
            }
        }

        return jobs;
    }

    private void setPreviewServer( ITeiidServer teiidServer ) {
        final ITeiidServer oldServer = getPreviewServer();
        final ITeiidServerVersion oldServerVersion = oldServer == null ? null : oldServer.getServerVersion();

        // set new server
        this.previewServer.set(teiidServer);

        boolean serverDeleted = (teiidServer == null);

        // mark all PVDBs as needing to be deployed
        resetAllDeployedStatuses();

        // If the server is being deleted, then we need to set up a job listener so we can close the server when ALL
        // delete jobs are completed. This removes any "sessions" via the adminAPI objects
        Collection<Job> jobs = new ArrayList<Job>();

        // cleanup old server if it can be reached
        if ((oldServer != null && oldServer.isConnected())) {
            // delete all Preview VDBs on old server
            for (IProject project : DotProjectUtils.getOpenModelProjects()) {
                for (IFile pvdbFile : findProjectPvdbs(project, false)) {
                    Job deleteDeployedPvdbJob = new DeleteDeployedPreviewVdbJob(getPreviewVdbDeployedName(pvdbFile),
                                                                                    getPreviewVdbJndiName(pvdbFile), this,
                                                                                    oldServer);
                    jobs.add(deleteDeployedPvdbJob);
                }
            }
        }

        /*
         * If the change in the preview server has resulted in a server version
         * change then all preview vdbs need to be erased and rebuilt since
         * they may contain non-compatible metadata.
         */
        if (teiidServer == null || (! teiidServer.getServerVersion().equals(oldServerVersion))) {
            jobs.addAll(rebuildPreviewVdbs());
        }

        if (! jobs.isEmpty()) {
            try {
                CountDownLatch latch = new CountDownLatch(jobs.size());
                IJobChangeListener shutdownJobListener = new ServerDeleteJobListener(latch, oldServer, serverDeleted);
                for (Job job : jobs) {
                    job.addJobChangeListener(shutdownJobListener);
                    job.schedule();
                }

                // wait for at most 10 seconds plus a quarter second per job
                latch.await(10 + (int)(jobs.size() / 4.0), TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Util.log(e);
            }
        }
    }

    /**
     * @param monitor
     * @param instance
     *
     * @throws CoreException
     * @throws Exception
     * @throws InterruptedException
     */
    private void cleanServer(IProgressMonitor monitor, ITeiidServer instance) throws CoreException, Exception, InterruptedException {
        IEclipsePreferences prefs = DqpPlugin.getInstance().getPreferences();

        if (instance == null) {
            // Nothing to do
            return;
        }

        if (! instance.isConnected()) {
            // Nothing we can do
            return;
        }

        if (! prefs.getBoolean(PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED,
                                    PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED_DEFAULT)) {
            // Nothing the user wants us to do!
            return;
        }

        // cleanup PVDs if necessary
        Collection<Job> jobs = new ArrayList<Job>();

        for (ITeiidVdb vdb : instance.getVdbs()) {
            if (! vdb.isPreviewVdb()) {
                continue;
            }
            
            if( vdb.getName().contains(ModelerCore.workspaceUuid().toString())) {
	            Job job = new DeleteDeployedPreviewVdbJob(vdb.getName(),
	                                                      getPreviewVdbJndiName(vdb.getName()),
	                                                      this, instance);
	            jobs.add(job);
            }
        }

        if ((monitor != null) && monitor.isCanceled()) {
            // Interrupted and abort!
            return;
        }

        if (jobs.isEmpty()) {
            // Nothing to do
            return;
        }

        CountDownLatch latch = new CountDownLatch(jobs.size());
        IJobChangeListener shutdownJobListener = new ShutdownJobListener(latch, monitor);

        for (Job job : jobs) {
            job.addJobChangeListener(shutdownJobListener);
            job.schedule();

            if (monitor.isCanceled()) {
                break;
            }
        }

        if ((monitor != null) && monitor.isCanceled()) {
            // Interrupted and abort!
            return;
        }

        monitor.subTask(NLS.bind(Messages.PreviewShutdownTeiidCleanupTask, jobs.size()));
        latch.await(); // wait until all cleanup jobs are finished
    }

    /**
     * Shutdowns the <code>PreviewManager</code>. This will remove any Preview VDBs from the default Teiid Instance.
     * 
     * @param monitor the progress monitor (may be <code>null</code>)
     * @throws Exception if issues result from shutting down this class
     */
    public void shutdown( IProgressMonitor monitor ) throws Exception {
        try {
            // remove listeners
            ModelerCore.getWorkspace().removeResourceChangeListener(this);
            ModelerCore.getModelContainer().getChangeNotifier().removeListener(this);
            IEclipsePreferences prefs = DqpPlugin.getInstance().getPreferences();
            prefs.removePreferenceChangeListener(this);

            if (isPreviewEnabled())
                cleanServer(monitor, getPreviewServer());
        }
        catch (Exception ex) {
            throw ex;
        } finally {
            // make sure shutdown is not called more than once
            this.previewEnabled = false;
            this.previewServer.set(null);
        }
    }

    /**
     * Tasks done when first constructed.
     */
    private void startup() {
        // add listeners
        try {
            ModelerCore.getModelContainer().getChangeNotifier().addListener(this);
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, Util.getString("serverManagerErrorConstructingPreviewManager")); //$NON-NLS-1$
        }

        ModelerCore.getWorkspace().addResourceChangeListener(this);
        IEclipsePreferences prefs = DqpPlugin.getInstance().getPreferences();
        prefs.addPreferenceChangeListener(this);

        // get current preference value to see if preview should be enabled
        this.previewEnabled = prefs.getBoolean(PreferenceConstants.PREVIEW_ENABLED, PreferenceConstants.PREVIEW_ENABLED_DEFAULT);

        // when Eclipse starts you don't get an open project event so pretend we did get one and call the event handler
        if (this.previewEnabled) {
            for (IProject project : DotProjectUtils.getOpenModelProjects()) {
                if (project.isOpen() && (ModelerCore.hasModelNature(project))) {
                    modelProjectOpened(project);
                }
            }
        }
    }

    /**
     * Make sure all PVDBs exist and are synchronized.
     */
    private void synchronizeWorkspace() {
        for (IProject project : DotProjectUtils.getOpenModelProjects()) {
            modelProjectOpened(project);
        }
    }
    
    /*
     * 
     */
    private String getSourceJndiName(VdbModelEntry entry) {
    	// Assuming a single source....
    	VdbSourceInfo info = entry.getSourceInfo();
    	
    	if( !info.isMultiSource() && info.getSourceCount() == 1 ) {
    		VdbSource source = entry.getSourceInfo().getSources().iterator().next();
    		return source.getJndiName();
    	}
    	
    	throw new ModelerCoreRuntimeException("Invalid VDB Source info for VDB model entry" + entry.getName());
    }
    
    /*
     * 
     */
    private String getSourceTranslatorName(VdbModelEntry entry) {
    	// Assuming a single source....
    	VdbSourceInfo info = entry.getSourceInfo();
    	
    	if( !info.isMultiSource() && info.getSourceCount() == 1 ) {
    		VdbSource source = entry.getSourceInfo().getSources().iterator().next();
    		return source.getTranslatorName();
    	}
    	
    	throw new ModelerCoreRuntimeException("Invalid VDB Source info for VDB model entry" + entry.getName());
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

        /**
         * @return the Preview VDB
         */
        public IFile getFile() {
            return this.pvdb;
        }

        /**
         * @return the full workspace path of the Preview VDB
         */
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

        /**
         * @return <code>true</code> if the Preview VDB needs to be deployed
         */
        public boolean shouldDeploy() {
            return this.deploy;
        }

        @Override
        public String toString() {
            return getPath().toString();
        }
    }

    class ShutdownJobListener extends JobChangeAdapter {

        private final CountDownLatch latch;

        private final IProgressMonitor monitor;

        public ShutdownJobListener( CountDownLatch latch,
                                    IProgressMonitor monitor ) {
            this.latch = latch;
            this.monitor = monitor;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
         */
        @Override
        public void done( IJobChangeEvent event ) {
            this.latch.countDown();
            if (monitor != null) monitor.subTask(NLS.bind(Messages.PreviewShutdownTeiidCleanupTask, latch.getCount()));
        }
    }

    class ServerDeleteJobListener extends JobChangeAdapter {
        private final CountDownLatch latch;
        private final ITeiidServer teiidServer;
        private boolean serverDeleted = false;

        public ServerDeleteJobListener( CountDownLatch latch,
                                        ITeiidServer teiidServer,
                                        boolean serverDeleted ) {
            this.latch = latch;
            this.teiidServer = teiidServer;
            this.serverDeleted = serverDeleted;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
         */
        @Override
        public void done( IJobChangeEvent event ) {
            this.latch.countDown();
            if (this.latch.getCount() == 0) {
                if (serverDeleted && this.teiidServer != null) {
                    this.teiidServer.disconnect();
                }
            }
        }
    }
}
