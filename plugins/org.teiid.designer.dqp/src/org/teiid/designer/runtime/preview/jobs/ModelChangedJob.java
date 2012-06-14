/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;
import org.teiid.designer.runtime.preview.PreviewManager;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * The <code>ModelChangedJob</code> job synchronizes and saves the Preview VDB associated with the changed model. If the Preview
 * does not yet exist it is created.
 */
public final class ModelChangedJob extends CompositePreviewJob {

    /**
     * The model that has changed (never <code>null</code>).
     */
    private final IFile changedModel;

    /**
     * @param changedModel the model that has changed (never <code>null</code>)
     * @param context the preview context (never <code>null</code>)
     * @param previewServer the preview server (may be <code>null</code>)
     * @throws Exception if there is a problem obtaining the Preview VDB for the changed model
     */
    public ModelChangedJob( IFile changedModel,
                            PreviewContext context,
                            Server previewServer ) throws Exception {
        super(NLS.bind(Messages.ModelChangedJob, changedModel), context, previewServer, true); // run jobs in sequence
        assert PreviewManager.isPreviewableResource(changedModel) : "model is not previewable" + changedModel.getFullPath(); //$NON-NLS-1$
        this.changedModel = changedModel;
        process(previewServer);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
     */
    @Override
    public boolean belongsTo( Object family ) {
        return (WorkspacePreviewVdbJob.WORKSPACE_PREVIEW_FAMILY == family);
    }

    /**
     * Constructs a {@link CreatePreviewVdbJob} and a {@link UpdatePreviewVdbJob} for the changed model.
     * 
     * @param previewServer the server where preview is being done (may be <code>null</code>)
     * @throws Exception if there is a problem with the Preview VDB
     */
    private void process( Server previewServer ) throws Exception {
        PreviewContext context = getContext();

        try {
            if (this.changedModel.exists()) {
                if (!this.changedModel.isSynchronized(IResource.DEPTH_INFINITE)) {
                    this.changedModel.refreshLocal(IResource.DEPTH_INFINITE, null);
                }

                // make sure Preview VDB exists for model
                add(new CreatePreviewVdbJob(this.changedModel, context));

                // sync Preview VDB with workspace
                add(new UpdatePreviewVdbJob(this.changedModel, previewServer, context));
            } else {
                cancel();
            }
        } catch (Exception e) {
            cancel();
            DqpPlugin.Util.log(e);
        }
    }

}
