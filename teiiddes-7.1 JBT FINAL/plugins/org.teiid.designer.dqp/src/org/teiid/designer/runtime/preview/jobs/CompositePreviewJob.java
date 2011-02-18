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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;

/**
 * The <code>CompositePreviewJob</code> is a job that wraps multiple {@link PreviewVdbJob}s. Jobs can be run in sequence or
 * asynchronously. At least one job must be added before running.
 */
public class CompositePreviewJob extends Job implements PreviewVdbJob {

    /**
     * The preview context (never <code>null</code>).
     */
    private final PreviewContext context;

    /**
     * A list of one or more jobs to run.
     */
    private final List<PreviewVdbJob> jobs;

    /**
     * A listener for the child jobs (may be <code>null</code>).
     */
    private IJobChangeListener listener;

    /**
     * The preview server (may be <code>null</code>).
     */
    private final Server previewServer;

    /**
     * A flag indicating if the jobs should be run in sequence instead of asynchronously. Default value is {@value} .
     */
    private final boolean runInSequence;

    /**
     * Runs the jobs asynchronously.
     * 
     * @param jobName the name of the job (may not be <code>null</code>)
     * @param context the preview context (may not be <code>null</code>)
     * @param previewServer the preview server (may be <code>null</code>)
     */
    public CompositePreviewJob( String jobName,
                                PreviewContext context,
                                Server previewServer ) {
        this(jobName, context, previewServer, false);
    }

    /**
     * @param jobName the name of the job (may not be <code>null</code>)
     * @param context the preview context (may not be <code>null</code>)
     * @param previewServer the preview server (may be <code>null</code>)
     * @param runInSequence a flag indicating if the jobs should run in sequence or asynchronously
     */
    public CompositePreviewJob( String jobName,
                                PreviewContext context,
                                Server previewServer,
                                boolean runInSequence ) {
        super(jobName);
        assert (context != null);

        this.context = context;
        this.jobs = new ArrayList<PreviewVdbJob>();
        this.runInSequence = runInSequence;
        this.previewServer = previewServer;
    }

    /**
     * @param job the job being added to the list of jobs to be run (may not be <code>null</code>)
     */
    public void add( PreviewVdbJob job ) {
        assert (job != null);
        this.jobs.add(job);
    }

    /**
     * @param listener the listener for child jobs (may be <code>null</code>)
     */
    public void addChildJobChangeListener( IJobChangeListener listener ) {
        this.listener = listener;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#canceling()
     */
    @Override
    protected void canceling() {
        for (PreviewVdbJob previewJob : this.jobs) {
            assert (previewJob instanceof Job);
            Job job = (Job)previewJob;
            job.cancel();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.PreviewVdbJob#completedSuccessfully()
     */
    @Override
    public boolean completedSuccessfully() {
        for (PreviewVdbJob job : this.jobs) {
            if (!job.completedSuccessfully()) return false;
        }

        return true;
    }

    /**
     * @return the listener of child job changes (may be <code>null</code>)
     */
    protected IJobChangeListener getChildJobChangeListener() {
        return this.listener;
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
     * @return the jobs that are included in this composite job (never <code>null</code>)
     */
    public List<PreviewVdbJob> getJobs() {
        return this.jobs;
    }

    /**
     * @return the preview server (may be <code>null</code>)
     */
    public Server getPreviewServer() {
        return this.previewServer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus run( IProgressMonitor monitor ) {
        assert (!this.jobs.isEmpty());

        monitor.beginTask(getName(), getJobs().size());

        try {
            for (PreviewVdbJob previewJob : this.jobs) {
                assert (previewJob instanceof Job);
                Job job = (Job)previewJob;
                job.setProgressGroup(monitor, 1);
                if (this.listener != null) job.addJobChangeListener(this.listener);
                job.schedule();

                if (this.runInSequence) {
                    job.join();
                }

                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
            }
        } catch (OperationCanceledException e) {
            // user canceled job
            Util.log(new Status(IStatus.CANCEL, PLUGIN_ID, NLS.bind(Messages.JobCanceled, getName())));
        } catch (InterruptedException e) {
            Util.log(e);
        } finally {
            monitor.done();
        }

        return Status.OK_STATUS;
    }

}
