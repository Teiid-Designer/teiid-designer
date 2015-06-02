/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.XmiVdb;

/**
 * The <code>UpdatePreviewVdbJob</code> synchronizes the Preview VDB with the workspace.
 *
 * @since 8.0
 */
public final class UpdatePreviewVdbJob extends WorkspacePreviewVdbJob {

    /**
     * The changed model whose Preview VDB is being updated (never <code>null</code>).
     */
    private final IFile model;

    /**
     * The server where the preview is being performed (may be <code>null</code> if preview is not enabled).
     */
    private final ITeiidServer previewServer;

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
                                ITeiidServer previewServer,
                                PreviewContext context ) throws Exception {
        super(NLS.bind(Messages.UpdatePreviewVdbJob, changedModel.getFullPath().removeFileExtension()), context);
        this.model = changedModel;
        this.previewServer = previewServer;
        this.pvdbFile = getContext().getPreviewVdb(this.model);

        // set job scheduling rule on the PVDB resource
        ISchedulingRule[] rules = new ISchedulingRule[2];
        rules[0] = getSchedulingRuleFactory().modifyRule(this.pvdbFile);
        rules[1] = getSchedulingRuleFactory().buildRule();
        setRule(MultiRule.combine(rules));
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
    public ITeiidServer getPreviewServer() {
        return this.previewServer;
    }

    /**
     * Perform 2 distinct operations:
     * 1. Tries to [re]deploy the preview vdb to a connected preview server
     * 2. Synchronizes the preview vdb with the file on the native filesystem
     * 
     * @return
     *  If both operations succeed then an OK status is returned.
     *  If 1 operation fails then the ERROR status is normally returned. In the
     *  case of the preview server not being connected this is not an important
     *  enough state to justify a dialog displaying 'Server not started' so it is set
     *  as an INFO status instead.
     *  
     *  If both operations fail then an ERROR multi status is returned.
     * 
     * @see org.teiid.designer.runtime.preview.jobs.WorkspacePreviewVdbJob#runImpl(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus runImpl( IProgressMonitor monitor ) throws Exception {
        IStatus status = null;
        Vdb pvdb = new XmiVdb(this.pvdbFile, true, monitor);

        // run this only if we have a preview server
        if (this.previewServer == null || ! previewServer.isConnected()) {
        	return Status.OK_STATUS;
        }
        
        // Only if there is a fully connected preview server do we attempt to
        // deploy the preview vdb on it.
        try {
            // update/create connection info
            getContext().ensureConnectionInfoIsValid(pvdb, this.previewServer);
        } catch (Exception e) {
            status = new Status(IStatus.ERROR, PLUGIN_ID,
                           NLS.bind(Messages.UpdatePreviewVdbJobError, this.model.getFullPath()), e);
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

            if (status == null) {
                // Only if the preview server update was successful too
                // can we return an OK status
                status = new Status(IStatus.OK, PLUGIN_ID, NLS.bind(Messages.UpdatePreviewVdbJobSuccessfullyCompleted,
                                                              this.pvdbFile.getFullPath()));
            }
            
        } catch (Exception e) {
            IStatus syncStatus = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.UpdatePreviewVdbJobError,
                                                                           this.model.getFullPath()), e);
            if (status == null) {
                status = syncStatus;
            } else {
                IStatus[] statuses = new IStatus[2];
                statuses[0] = status;
                statuses[1] = syncStatus;
                status = new MultiStatus(PLUGIN_ID, IStatus.ERROR, statuses, NLS.bind(Messages.UpdatePreviewVdbJobError,
                                                                                  this.model.getFullPath()), null);
            }
        }

        return status;
    }

}
