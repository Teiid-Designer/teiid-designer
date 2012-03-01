/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.core.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.views.status.StatusListener;

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
public class AdvisorStatusManager implements IChangeListener, IStatusManager {

    private Object currentObject;

    private static final String ID = "DefaultStatusManager"; //$NON-NLS-1$
    private static final String ADVISOR_JOB_NAME = "Workspace Advisor Validation"; //$NON-NLS-1$
    private static final String AUTOBUILD_JOB_NAME = "Building workspace"; //$NON-NLS-1$
    public static final Object FAMILY_ADVISOR_STATUS = new Object();

    private static Collection statusListeners;
    private EventObjectListener modelResourceListener;
    private IResourceChangeListener resourceListener;
    private AdvisorStatus currentStatus;
    private AdvisorStatusUpdateJob statusRefreshJob;
    private AutoBuildJobListener autoBuildJobListener;
    boolean isListeningForBuildComplete = false;

    public AdvisorStatusManager() {
        super();
        setup();
        statusListeners = new ArrayList();
        this.autoBuildJobListener = new AutoBuildJobListener();
        // viewWorker = VdbViewUtil.getVdbViewWorker();
        this.statusRefreshJob = getNewStatusUpdateJob();
        // Connect to VdbView
        registerAsListener();
        // System.out.println(" WebServicesValidationManager.init() calling updateStatus()");
        updateStatus(false);
    }

    public void addListener( IAdvisorStatusListener listener ) {

        if (statusListeners == null) {
            statusListeners = new ArrayList();
        }
        statusListeners.add(listener);
        // System.out.println(" WebServicesValidationManager.addListener() calling updateStatus()");
        updateStatus(false);
    }

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

    protected void generateNewStatus() {
    	System.out.println("AdvisorStatusManager.generateNewStatus() (NO OP METHOD)");
    }

    /**
     * @return Returns the currentStatus.
     * @since 4.3
     */
    public AdvisorStatus getCurrentStatus() {
        return this.currentStatus;
    }

    public void setCurrentStatus( AdvisorStatus status ) {
        this.currentStatus = status;
    }

    /**
     * @return currentObject
     */
    public Object getCurrentObject() {
        return currentObject;
    }

    /**
     * String ID defined for the specific contribution to AdvisorStatusExtensionManger Should be overridden
     * 
     * @return Id the unique string ID of the status manager contribution
     */
    public String getId() {
        return ID;
    }

    protected Object getJobFamily() {
        return FAMILY_ADVISOR_STATUS;
    }

    protected String getJobName() {
        return ADVISOR_JOB_NAME;
    }

    /**
     * Utility method to get a snapshot status of the current Vdb Context.
     * 
     * @return
     * @since 5.0
     */
    public AdvisorStatus getStatusSnapshot() {
        return null;
    }
    
    public AdvisorStatusUpdateJob getNewStatusUpdateJob() {
    	return new AdvisorStatusUpdateJob(getJobName(), this);
    }

    protected void setup() {
        // NO OP
    }

    protected void notifyStatusChanged( AdvisorStatus status ) {
        this.currentStatus = status;
        if (shouldNotify()) {
            for (Iterator iter = statusListeners.iterator(); iter.hasNext();) {
                ((IAdvisorStatusListener)iter.next()).notifyStatusChanged(status);
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

    public void removeListener( StatusListener listener ) {
        if (statusListeners == null) {
            statusListeners = new ArrayList();
        }
        statusListeners.remove(listener);

        if (!statusListeners.isEmpty()) {
            // System.out.println(" WebServicesValidationManager.removeListener() calling updateStatus()");
            updateStatus(false);
        }
    }

    /**
     * @param currentObject Sets currentObject to the specified value.
     */
    public boolean setCurrentObject( Object focusedObject ) {
        if (currentObject != focusedObject) {
            this.currentObject = focusedObject;
            return true;
        }

        return false;
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

        Job[] wsJobs = Job.getJobManager().find(getJobFamily());
        int nJobs = wsJobs.length;
        if (JobUtils.jobIsRunning(getJobFamily())) {
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

                    Job.getJobManager().addJobChangeListener(this.autoBuildJobListener);

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

        if (forceJob || shouldAddJob() || forceUpdate) {
            // On startup, the Default display thread may not be available, so ....
            if (Display.getDefault() == null) {
                // System.out.println("  WSVM.updateStatus() No Default Thread, so update without it");
                generateNewStatus();
                if (getCurrentStatus() != null) {
                    // status.setWorkspaceChanged(workspaceChanged);
                    notifyStatusChanged(getCurrentStatus());
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

    protected class AutoBuildJobListener extends JobChangeAdapter {

        //
        // methods
        //

        @Override
        public void done( IJobChangeEvent theEvent ) {

            if (!JobUtils.validationJobsExist() && theEvent.getJob().getName().equals(AUTOBUILD_JOB_NAME)) {
                // System.out.println(" AutoBuildJobListener.done():  ##### validation Finished. #####         AUTOBUILD = " +
                // ResourcesPlugin.getWorkspace().isAutoBuilding());
                updateStatus(false);
                isListeningForBuildComplete = false;
            }

        }

    }

    protected final class MarkerDeltaListener implements IResourceChangeListener {
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
    } // endclass MarkerDeltaListener

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startup() {
		// TODO Auto-generated method stub
		
	}

}
