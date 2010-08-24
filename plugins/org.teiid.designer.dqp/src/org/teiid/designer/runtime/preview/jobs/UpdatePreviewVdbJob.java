/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import static com.metamatrix.modeler.dqp.DqpPlugin.PLUGIN_ID;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;
import org.teiid.designer.vdb.Vdb;

/**
 * The <code>UpdatePreviewVdbJob</code> synchronizes the Preview VDB with the workspace.
 */
public final class UpdatePreviewVdbJob extends WorkspacePreviewVdbJob {

    /**
     * The changed model whose Preview VDB is being updated (never <code>null</code>).
     */
    private final IFile model;

    /**
     * The server where the preview is being performed (may be <code>null</code> if preview is not enabled).
     */
    private final Server previewServer;

    /**
     * The Preview VDB to be updated (never <code>null</code>).
     */
    private final IFile pvdbFile;

    /**
     * @param changedModel the model whose Preview VDB needs to be updated (never <code>null</code>)
     * @param context the preview context (never <code>null</code>)
     * @param previewServer the server where the preview is being performed (may be <code>null</code>)
     * @throws Exception if unable to construct the Preview VDB path
     */
    public UpdatePreviewVdbJob( IFile changedModel,
                                Server previewServer,
                                PreviewContext context ) throws Exception {
        super(NLS.bind(Messages.UpdatePreviewVdbJob, changedModel.getFullPath().removeFileExtension()), context);
        this.model = changedModel;
        this.previewServer = previewServer;
        this.pvdbFile = getContext().getPreviewVdb(this.model);

        // set job scheduling rule on the PVDB resource
        setRule(getSchedulingRuleFactory().modifyRule(getPreviewVdb()));
    }

    /**
     * @return the model whose Preview VDB is being updated (never <code>null</code>)
     */
    public IFile getModel() {
        return this.model;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.WorkspacePreviewVdbJob#getPreviewVdb()
     */
    @Override
    public IFile getPreviewVdb() {
        return this.pvdbFile;
    }

    /**
     * @return the preview server used for this job (may be <code>null</code>)
     */
    public Server getPreviewServer() {
        return this.previewServer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.WorkspacePreviewVdbJob#runImpl(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus runImpl( IProgressMonitor monitor ) throws Exception {
        IStatus error = null;
        Vdb pvdb = new Vdb(this.pvdbFile, true, monitor);

        // run this only if we have a preview server
        if (this.previewServer != null) {
            try {
                // update/create connection info
                getContext().ensureConnectionInfoIsValid(pvdb, this.previewServer);
            } catch (Exception e) {
                error = new Status(IStatus.ERROR, PLUGIN_ID,
                                   NLS.bind(Messages.UpdatePreviewVdbJobError, this.model.getFullPath()), e);
            }
        }

        try {
            // check if synchronized because new PVDBs generated a resource change event even though nothing has changed
            if (!pvdb.isSynchronized()) {
                pvdb.synchronize(monitor);
                pvdb.save(monitor);
            } else {
                if (pvdb.isModified()) {
                    pvdb.save(monitor);
                }
            }

            return new Status(IStatus.OK, PLUGIN_ID, NLS.bind(Messages.UpdatePreviewVdbJobSuccessfullyCompleted,
                                                              this.pvdbFile.getFullPath()));
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.UpdatePreviewVdbJobError,
                                                                           this.model.getFullPath()), e);
            if (error == null) {
                error = status;
            } else {
                IStatus[] statuses = new IStatus[2];
                statuses[0] = error;
                statuses[1] = status;
                error = new MultiStatus(PLUGIN_ID, IStatus.OK, statuses, NLS.bind(Messages.UpdatePreviewVdbJobError,
                                                                                  this.model.getFullPath()), null);
            }
        }

        return error;
    }

}
