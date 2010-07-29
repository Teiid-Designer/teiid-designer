/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import static com.metamatrix.modeler.dqp.DqpPlugin.PLUGIN_ID;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;
import org.teiid.designer.runtime.preview.PreviewManager;

/**
 *
 */
public final class PreviewSetupJob extends CompositePreviewJob {

    /**
     * The name of the Preview VDB that is being setup to be previewed.
     */
    private final String projectPreviewVdbName;

    /**
     * A collection of Preview VDBs that need to be deployed.
     */
    private final List<IFile> pvdbsToDeploy = new ArrayList<IFile>();

    /**
     * @param modelBeingPreviewed the name of the model being previewed (may not be <code>null</code> or empty)
     * @param projectPreviewVdbName the name of the project Preview VDB that is being setup to be previewed (may not be
     *        <code>null</code> or empty)
     * @param context the preview context (may not be <code>null</code>)
     * @param previewServer the preview server (may be <code>null</code>)
     */
    public PreviewSetupJob( String modelBeingPreviewed,
                            String projectPreviewVdbName,
                            PreviewContext context,
                            Server previewServer ) {
        super(Messages.bind(Messages.PreviewSetupJob, modelBeingPreviewed), context, previewServer, true);
        assert (modelBeingPreviewed != null && modelBeingPreviewed.length() != 0) : "model being previewed is null or empty"; //$NON-NLS-1$
        this.projectPreviewVdbName = projectPreviewVdbName;
    }

    /**
     * Creates a {@link DeployPreviewVdbJob} for the specified Preview VDB. <strong>Must be called before this job is
     * scheduled.</strong>
     * 
     * @param pvdb the Preview VDB that needs to be deployed (may not be <code>null</code>)
     */
    public void deploy( IFile pvdb ) {
        this.pvdbsToDeploy.add(pvdb);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.CompositePreviewJob#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus run( IProgressMonitor monitor ) {
        PreviewContext context = getContext();

        try {
            // add deploy jobs first
            for (IFile pvdbToDeploy : this.pvdbsToDeploy) {
                add(new DeployPreviewVdbJob(pvdbToDeploy, context, getPreviewServer()));
            }

            // add merge jobs last
            for (IFile pvdbToMerge : this.pvdbsToDeploy) {
            	// REMOVE the .vdb extension for the source vdb
            	String sourceVdbName = pvdbToMerge.getFullPath().removeFileExtension().lastSegment().toString();
            	if( ! sourceVdbName.equals(this.projectPreviewVdbName) ) {
            		add(new MergePreviewVdbJob(sourceVdbName, PreviewManager.getPreviewVdbVersion(pvdbToMerge), this.projectPreviewVdbName, 1, context, getPreviewServer()));
            	}
            }

            // now ready to run
            return super.run(monitor);
        } catch (Exception e) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Messages.bind(Messages.PreviewSetupJobError, getName()), e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
     */
    @Override
    public boolean shouldRun() {
        return ((getPreviewServer() != null) && getPreviewServer().ping().isOK() && !this.pvdbsToDeploy.isEmpty() && super.shouldRun());
    }

}
