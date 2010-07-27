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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;

/**
 * The <code>TeiidPreviewVdbJob</code> class are jobs that either deploy Preview VDBs to a Teiid server or remove Preview VDBs
 * from a Teiid Server. If the preview server is <code>null</code> the job will not run.
 */
public abstract class TeiidPreviewVdbJob extends Job implements PreviewVdbJob {

    /**
     * Preview is disabled by setting the system property "org.teiid.designer.runtime.preview.teiid.disable" to <code>true</code>.
     * Defaults to <code>false</code>. Disabling prevents Preview VDBs from being deployed to Teiid instances and prevents Preview
     * VDBs and data sources from being deleted from Teiid instances.
     */
    public static final boolean DISABLE = Boolean.getBoolean("org.teiid.designer.runtime.preview.teiid.disable"); //$NON-NLS-1$

    /**
     * The Teiid Preview VDB job family identifier. The value is {@value} .
     */
    public static final Object TEIID_PREVIEW_FAMILY = PreviewVdbJob.PREVIEW_FAMILY + ".teiid"; //$NON-NLS-1$

    /**
     * The preview context (never <code>null</code>)
     */
    private final PreviewContext context;

    /**
     * The preview server (may be <code>null</code>).
     */
    private final Server previewServer;

    /**
     * @param jobName the job name (may not be <code>null</code>)
     * @param context the preview context (may not be <code>null</code>)
     * @param previewServer the previewServer (may be <code>null</code>)
     */
    public TeiidPreviewVdbJob( String jobName,
                               PreviewContext context,
                               Server previewServer ) {
        super(jobName);
        assert (context != null);
        this.context = context;
        this.previewServer = previewServer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
     */
    @Override
    public boolean belongsTo( Object family ) {
        return (TEIID_PREVIEW_FAMILY == family);
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
     * @return the preview server (may be <code>null</code>)
     */
    protected final Server getPreviewServer() {
        return this.previewServer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected final IStatus run( IProgressMonitor monitor ) {
        assert (getPreviewServer() != null) : "Teiid server is null"; //$NON-NLS-1$
        long startTime = System.currentTimeMillis();
        monitor.setTaskName(getName());

        try {
            IStatus results = runImpl(monitor);
            assert (results != null);
            return results;
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                return new Status(IStatus.WARNING, PLUGIN_ID, Messages.bind(Messages.JobCanceled, getName()), e);
            }

            return new Status(IStatus.ERROR, PLUGIN_ID, Messages.bind(Messages.UnexpectedErrorRunningJob, getName()), e);
        } finally {
            monitor.done();

            // add job completed message
            String msg;
            long milliseconds = (System.currentTimeMillis() - startTime);
            long hours = milliseconds / (1000 * 60 * 60);
            long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
            long seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

            if (hours > 0) {
                msg = Messages.bind(Messages.LongDurationJob, new Object[] {getName(), hours, minutes, seconds});
            } else if (minutes > 0) {
                msg = Messages.bind(Messages.LessThanAnHourDurationJob, new Object[] {getName(), minutes, seconds});
            } else if (seconds > 0) {
                msg = Messages.bind(Messages.LessThanAMinuteDurationJob, getName(), seconds);
            } else {
                msg = Messages.bind(Messages.LessThanASecondDurationJob, getName());
            }

            Util.log(IStatus.INFO, msg);
        }
    }

    /**
     * This method will not be called if there is no preview Teiid server.
     * 
     * @param monitor the monitor to use for tracking job progress (may be <code>null</code>)
     * @return the results of the job (may not be <code>null</code>)
     * @throws Exception if an unexpected error occurred running the job
     */
    protected abstract IStatus runImpl( IProgressMonitor monitor ) throws Exception;

    /**
     * {@inheritDoc}
     * <p>
     * Will return <code>false</code> when there is no Teiid preview server or when the preview server ping timeouts.
     * 
     * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
     */
    @Override
    public boolean shouldRun() {
        return !DISABLE && ((getPreviewServer() != null) && getPreviewServer().ping().isOK()) && super.shouldRun();
    }

}
