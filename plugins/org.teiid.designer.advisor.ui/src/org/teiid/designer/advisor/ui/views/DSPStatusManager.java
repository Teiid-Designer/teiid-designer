/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.internal.viewsupport.JobUtils;

/**
 * 
 */
public class DSPStatusManager implements IChangeListener {

    private static DSPValidationHelper helper;

    public static final Object FAMILY_MODEL_PROJECT_STATUS = new Object();
    private static final String JOB_NAME = "Data Services Project Advisor Validation"; //$NON-NLS-1$
    private static final String AUTOBUILD_JOB_NAME = "Building workspace"; //$NON-NLS-1$

    private Collection statusListeners;
    private EventObjectListener modelResourceListener;
    private IResourceChangeListener resourceListener;
    private ModelProjectStatus currentStatus;
    StatusUpdateJob statusRefreshJob;
    private AutoBuildJobListener autoBuildJobListener;
    boolean isListeningForBuildComplete = false;

    private IProject currentProject;

    /**
     * @since 4.3
     */
    public DSPStatusManager() {
        super();
        init();
    }

    private void init() {
        this.helper = new DSPValidationHelper();
        this.statusListeners = new ArrayList();
        this.autoBuildJobListener = new AutoBuildJobListener();
        // viewWorker = VdbViewUtil.getVdbViewWorker();
        this.statusRefreshJob = new StatusUpdateJob(JOB_NAME);
        // Connect to VdbView
        registerAsListener();
        // System.out.println(" WebServicesValidationManager.init() calling updateStatus()");
        updateStatus(false);
    }

    public void updateStatus( boolean forceUpdate ) {
        // First check if the workbench state is OK and that there are listeners who care...
        // IF NOT, then RETURN and DO NOTHING
        if (!shouldNotify()) {
            return;
        }

        // if (!registeredWithVdbViewWorker) {
        // registerWithVdbViewWorker();
        // }

        if (JobUtils.validationJobsExist()) {
            if (!isListeningForBuildComplete) {
                if (ResourcesPlugin.getWorkspace().isAutoBuilding()) {
                    // System.out.println(" WSVM.updateStatus():  ##### validation running #####.  AUTOBUILD = " +
                    // ResourcesPlugin.getWorkspace().isAutoBuilding());

                    Platform.getJobManager().addJobChangeListener(this.autoBuildJobListener);

                    isListeningForBuildComplete = true;
                } else {
                    isListeningForBuildComplete = false;
                }
            }
            return;
        }

        // Update the status
        boolean workspaceChanged = false;
        boolean forceJob = false;
        // if (viewWorker.getCurrentVdbContext() != null && currentVdbContext != null) {
        // if (viewWorker.getCurrentVdbContext() == currentVdbContext) {
        // contextChanged = false;
        // } else {
        // contextChanged = true;
        // setCurrentVdbContext(viewWorker.getCurrentVdbContext());
        // }
        // } else if (viewWorker.getCurrentVdbContext() == null && currentVdbContext != null) {
        // contextChanged = true;
        // setCurrentVdbContext(viewWorker.getCurrentVdbContext());
        // } else if (viewWorker.getCurrentVdbContext() != null && currentVdbContext == null) {
        // contextChanged = true;
        // setCurrentVdbContext(viewWorker.getCurrentVdbContext());
        // }
        //
        // if (contextChanged) {
        // // Job[] wsJobs = Platform.getJobManager().find(FAMILY_WEB_SERVICE_STATUS);
        // // int nJobs = wsJobs.length;
        // // System.out.println(" ====>> WSVM.updateStatus():  Context Changed, Cancel all UPDATE JOBS.  N Jobs = " + nJobs);
        // Platform.getJobManager().cancel(FAMILY_WEB_SERVICE_STATUS);
        // }
        // statusRefreshJob.setContextChanged(contextChanged);
        //
        // // If the context changed, we want to reset the context on the helper, then force
        // // a job to be queued, maybe cancel any that are pending.
        // if (contextChanged) {
        // forceJob = true;
        // }

        if (forceJob || shouldAddJob() || forceUpdate) {
            // On startup, the Default display thread may not be available, so ....
            if (Display.getDefault() == null) {
                // System.out.println("  WSVM.updateStatus() No Default Thread, so update without it");
                ModelProjectStatus status = generateNewStatus();
                if (status != null) {
                    status.setWorkspaceChanged(workspaceChanged);
                    notifyStatusChanged(status);
                }
            } else {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        // System.out.println("  ---->>>> WSVM.updateStatus(): scheduling new JOB");
                        statusRefreshJob.schedule(400);
                    }
                });
            }
        }
    }

    private void registerAsListener() {
        // ----------------------------------------------------------
        // REGISTER for VDB Context changes from VdbViewWorker and fire updateStatus
        // ----------------------------------------------------------
        // registerWithVdbViewWorker();

        // -----------------------------------------------------------
        // REGISTER for Resources changes and fire updateStatus when
        // resources are added/removed, changed or reloaded
        // -----------------------------------------------------------
        modelResourceListener = new EventObjectListener() {

            @Override
            public void processEvent( EventObject obj ) {
                final ModelResourceEvent event = (ModelResourceEvent)obj;
                final IResource file = event.getResource();

                if (event.getType() == ModelResourceEvent.ADDED || event.getType() == ModelResourceEvent.CHANGED
                    || event.getType() == ModelResourceEvent.RELOADED || event.getType() == ModelResourceEvent.REMOVED) {
                    // System.out.println(" WebServicesValidationManager.processEvent() calling updateStatus()");
                    updateStatus(false);
                }

            }
        };

        try {
            com.metamatrix.modeler.ui.UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class,
                                                                                         modelResourceListener);
        } catch (EventSourceException e) {
        	AdvisorUiConstants.UTIL.log(IStatus.ERROR, e, e.getMessage());
        }

        // -----------------------------------------------------------
        // REGISTER for Resources Changes changes and fire updateStatus when
        // deltas relating to vdb resources have changed
        // -----------------------------------------------------------
        resourceListener = new MarkerDeltaListener();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener);
    }

    // Listener methods

    public void addListener( DSPStatusListener listener ) {
        if (statusListeners == null) {
            statusListeners = new ArrayList();
        }
        statusListeners.add(listener);
        // System.out.println(" WebServicesValidationManager.addListener() calling updateStatus()");
        updateStatus(false);
    }

    public void removeListener( DSPStatusListener listener ) {
        if (statusListeners == null) {
            statusListeners = new ArrayList();
        }
        statusListeners.remove(listener);

        if (!statusListeners.isEmpty()) {
            // System.out.println(" WebServicesValidationManager.removeListener() calling updateStatus()");
            updateStatus(false);
        }
    }

    void notifyStatusChanged( ModelProjectStatus status ) {
        this.currentStatus = status;
        if (shouldNotify()) {
            for (Iterator iter = statusListeners.iterator(); iter.hasNext();) {
                ((DSPStatusListener)iter.next()).notifyStatusChanged(status);
            }
        }
    }

    private boolean shouldNotify() {
        boolean result = true;

        // if (statusListeners != null && !statusListeners.isEmpty()) {
        // // Check the workbench state
        // WorkbenchState state = ProductCustomizerMgr.getInstance().getProductCharacteristics().getWorkbenchState();
        // if (state.isStandard() || state.isStartingUp() && viewWorker != null) {
        // if (viewWorker.isVdbOpen() && viewWorker.getCurrentVdbContext().getVirtualDatabase() != null) {
        // result = true;
        // } else if (!viewWorker.isVdbOpen()) {
        // result = true;
        // }
        // }
        // }

        return result;
    }

    /**
     * Need to be wired up to the current VDB Context.
     * 
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 5.0
     */
    public void stateChanged( IChangeNotifier theSource ) {
        // Just update, don't worry about state of context because updateStatus() will take care of changes
        // System.out.println(" WebServicesValidationManager.stateChanged() calling updateStatus()");
        updateStatus(false);
    }

    // Helper methods

    private final class MarkerDeltaListener implements IResourceChangeListener {
        public void resourceChanged( IResourceChangeEvent event ) {
            // TODO find out if this handles both the enterprise and lightweight cases.
            boolean refreshNeeded = false;

            IMarkerDelta[] markerDeltas = event.findMarkerDeltas(IMarker.PROBLEM, true);
            List<IMarkerDelta> changes = new ArrayList<IMarkerDelta>(markerDeltas.length);

            examineDelta(markerDeltas, changes);

            if (markerDeltas.length != changes.size()) {
                refreshNeeded = true;
            }

            // Refresh everything if markers were added or removed
            if (refreshNeeded) {
                // System.out.println(" WebServicesValidationManager.resourceChanged(MARKERS) calling updateStatus()");
                updateStatus(false);
            } // endif
        }

        private void examineDelta( IMarkerDelta[] deltas,
                                   List changes ) {
            for (int idx = 0; idx < deltas.length; idx++) {
                IMarkerDelta delta = deltas[idx];
                int kind = delta.getKind();

                if (kind == IResourceDelta.CHANGED) {
                    changes.add(deltas[idx].getMarker());
                } // endif
            } // endfor
        }
    } // endclass MarkerDeltaListener

    /**
     * Method for plugin to call during stop() so this class can unregister itself as a listener.
     * 
     * @since 4.3
     */
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
        try {
            UiPlugin.getDefault().getEventBroker().removeListener(ModelResourceEvent.class, modelResourceListener);
        } catch (EventSourceException e) {
        	AdvisorUiConstants.UTIL.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    /**
     * @return Returns the currentStatus.
     * @since 4.3
     */
    public ModelProjectStatus getCurrentStatus() {
        return this.currentStatus;
    }

    ModelProjectStatus generateNewStatus() {
        ModelProjectStatus status = null;

        // if (viewWorker.isVdbOpen()) {
        // // helper.setVdbContext(viewWorker.getCurrentVdbContext());
        // // System.out.println("  >>>>>>  WSVM.generateNewStatus(): Asking Helper for NEW WebServiceStatus:  VDB = " +
        // // helper.getVdbContext().getVirtualDatabase().getName());
        // status = helper.getCurrentStatus();
        // } else {
        helper.setCurrentProject(getCurrentProject());
        status = helper.getCurrentStatus();
        // }
        // System.out.println("  <<<<<<  WSVM.generateNewStatus(): DONE ---------------------------");
        return status;
    }

    /**
     * Utility method to get a snapshot status of the current Vdb Context.
     * 
     * @return
     * @since 5.0
     */
    public ModelProjectStatus getStatusSnapshot() {
        ModelProjectStatus status = null;

        // if (viewWorker.isVdbOpen()) {
        // helper.setVdbContext(viewWorker.getCurrentVdbContext());
        // status = helper.getCurrentStatus();
        // } else {
        // helper.setVdbContext(null);
        status = helper.getCurrentStatus();
        // }

        return status;
    }

    /**
     * This method checks the job manager to determine if there is already a job in the QUEUE or not. We don't want more than ONE
     * job in the queue (i.e. NOT running) so we don't do too much validation.
     * 
     * @return
     * @since 5.0
     */
    private boolean shouldAddJob() {
        boolean result = false;

        Job[] wsJobs = Platform.getJobManager().find(FAMILY_MODEL_PROJECT_STATUS);
        int nJobs = wsJobs.length;
        if (JobUtils.jobIsRunning(FAMILY_MODEL_PROJECT_STATUS)) {
            // If Job is running, only return TRUE if there is Only One Job. If there are more than one, then
            // there is already a queued job, so FALSE is OK
            if (nJobs == 1) {
                result = true;
            }
        } else if (nJobs == 0) {
            // If NO Job is running, only return TRUE if there are NO Jobs in the queue.
            result = true;
        }
        return result;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // JobListener INNER CLASSes
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private class AutoBuildJobListener extends JobChangeAdapter {

        //
        // methods
        //

        public void done( IJobChangeEvent theEvent ) {

            if (!JobUtils.validationJobsExist() && theEvent.getJob().getName().equals(AUTOBUILD_JOB_NAME)) {
                // System.out.println(" AutoBuildJobListener.done():  ##### validation Finished. #####         AUTOBUILD = " +
                // ResourcesPlugin.getWorkspace().isAutoBuilding());
                updateStatus(false);
                isListeningForBuildComplete = false;
            }

        }

    }

    //
    // Inner class
    //
    private final class StatusUpdateJob extends Job {
        private boolean contextChanged;

        // constructors:
        StatusUpdateJob( String name ) {
            super(name);
            setUser(false);
            setSystem(true);
        }

        // Implementation of abstract methods:
        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            // let's wire this job up to listen for vdb context change events

            ModelProjectStatus status = generateNewStatus();

            // Only we know about the last context, so we need to set the flag to allow the listeners
            // that knowledge
            if (status != null) {
                // status.setContextChanged(contextChanged);

                notifyStatusChanged(status);
            }
            return Status.OK_STATUS;
        }

        @Override
        public boolean belongsTo( Object family ) {
            return family == FAMILY_MODEL_PROJECT_STATUS;
        }

        public void setContextChanged( boolean theContextChanged ) {
            contextChanged = theContextChanged;
        }
    }

    /**
     * @return currentProject
     */
    public IProject getCurrentProject() {
    	if( this.currentProject != null && !this.currentProject.exists() ) {
    		this.currentProject = null;
    	}
        return this.currentProject;
    }

    /**
     * Sets the current model project to calculate status on.
     * 
     * @param nextCurrentProject Sets currentProject to the specified value.
     * @return true if project was changed, false if not
     */
    public boolean setCurrentProject( IProject nextCurrentProject ) {
        boolean projectChanged = false;
        IProject currentProject = getCurrentProject();
        if ((currentProject == null && nextCurrentProject != null) || (currentProject != null && nextCurrentProject == null)) {
            projectChanged = true;
        } else if (currentProject == null && nextCurrentProject == null) {
            projectChanged = false;
        } else if (!currentProject.getName().equalsIgnoreCase(nextCurrentProject.getName())) {
            projectChanged = true;
        }
        this.currentProject = nextCurrentProject;

        return projectChanged;
    }

}
