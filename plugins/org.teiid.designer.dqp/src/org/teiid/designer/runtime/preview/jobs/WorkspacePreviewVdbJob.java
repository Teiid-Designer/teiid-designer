/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import static com.metamatrix.modeler.dqp.DqpPlugin.PLUGIN_ID;
import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.runtime.DebugConstants;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * The <code>WorkspacePreviewVdbJob</code> are jobs that work with Preview VDBs in the Eclipse workspace.
 */
public abstract class WorkspacePreviewVdbJob extends WorkspaceJob implements PreferenceConstants, PreviewVdbJob {

    /**
     * The Workspace Preview VDB job family identifier. The value is {@value} .
     */
    public static final Object WORKSPACE_PREVIEW_FAMILY = PreviewVdbJob.PREVIEW_FAMILY + ".workspace"; //$NON-NLS-1$

    /**
     * The preview context (never <code>null</code>)
     */
    private final PreviewContext context;

    /**
     * @param name the job name (may not be <code>null</code>)
     * @param context the preview context (may not be <code>null</code>)
     */
    public WorkspacePreviewVdbJob( String name,
                                   PreviewContext context ) {
        super(name);
        assert (context != null);
        this.context = context;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
     */
    @Override
    public boolean belongsTo( Object family ) {
        return (WORKSPACE_PREVIEW_FAMILY == family);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.PreviewVdbJob#completedSuccessfully()
     */
    @Override
    public boolean completedSuccessfully() {
        IStatus status = getResult();

        // job has not finished
        if (status == null) return false;

        // job has finished
        return (status.getSeverity() != IStatus.ERROR);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.PreviewVdbJob#getContext()
     */
    @Override
    public PreviewContext getContext() {
        return this.context;
    }

    /**
     * @param monitor the monitor to use for tracking job progress (may be <code>null</code>)
     * @return the results of the job (may not be <code>null</code>)
     * @throws Exception if an unexpected error occurred running the job
     */
    protected abstract IStatus runImpl( IProgressMonitor monitor ) throws Exception;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public final IStatus runInWorkspace( IProgressMonitor monitor ) {
        // add job start message
        if (DqpPlugin.getInstance().isDebugOptionEnabled(DebugConstants.PREVIEW_JOB_START)) {
            Util.log(IStatus.INFO, NLS.bind(Messages.JobStarted, getName()));
        }

        IStatus results = null;
        long startTime = System.currentTimeMillis();
        monitor.setTaskName(getName());

        try {
            results = runImpl(monitor);
            assert (results != null);
            return results;
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                return new Status(IStatus.WARNING, PLUGIN_ID, NLS.bind(Messages.JobCanceled, getName()), e);
            }

            return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.UnexpectedErrorRunningJob, getName()), e);
        } finally {
            // add job completion message
            if (DqpPlugin.getInstance().isDebugOptionEnabled(DebugConstants.PREVIEW_JOB_DONE)) {
                Util.log(IStatus.INFO, NLS.bind(Messages.JobFinished, getName(), (results.getSeverity() != IStatus.ERROR)));
            }

            monitor.done();

            // add job duration message
            if (DqpPlugin.getInstance().isDebugOptionEnabled(DebugConstants.PREVIEW_JOB_DURATION)) {
                String msg;
                long milliseconds = (System.currentTimeMillis() - startTime);
                long hours = milliseconds / (1000 * 60 * 60);
                long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

                if (hours > 0) {
                    msg = NLS.bind(Messages.LongDurationJob, new Object[] {getName(), hours, minutes, seconds});
                } else if (minutes > 0) {
                    msg = NLS.bind(Messages.LessThanAnHourDurationJob, new Object[] {getName(), minutes, seconds});
                } else if (seconds > 0) {
                    msg = NLS.bind(Messages.LessThanAMinuteDurationJob, getName(), seconds);
                } else {
                    msg = NLS.bind(Messages.LessThanASecondDurationJob, getName());
                }

                Util.log(IStatus.INFO, msg);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
     */
    @Override
    public boolean shouldRun() {
        boolean result = DqpPlugin.getInstance().getPreferences().getBoolean(PREVIEW_ENABLED, PREVIEW_ENABLED_DEFAULT);

        // trace message
        if (DqpPlugin.getInstance().isDebugOptionEnabled(DebugConstants.PREVIEW_JOB_SHOULD_RUN)) {
            Util.log(IStatus.INFO, NLS.bind(Messages.JobShouldRun, getName(), result));
        }

        return result;
    }

}
