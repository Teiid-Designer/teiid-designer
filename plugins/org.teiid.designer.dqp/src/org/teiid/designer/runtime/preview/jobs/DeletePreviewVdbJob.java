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
import org.eclipse.core.runtime.Status;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;

/**
 * The <code>DeletePreviewVdbJob</code> job deletes a Preview VDB from the Eclipse workspace.
 */
public final class DeletePreviewVdbJob extends WorkspacePreviewVdbJob {

    /**
     * The Preview VDB to be deleted (never <code>null</code>).
     */
    private final IFile pvdb;

    /**
     * Deletes the specified Preview VDB.
     * 
     * @param pvdbToDelete the Preview VDB to delete (may not be <code>null</code>)
     * @param context the preview context (may not be <code>null</code>)
     * @throws Exception if there is a problem getting the Preview VDB for the deleted model
     */
    public DeletePreviewVdbJob( IFile pvdbToDelete,
                                PreviewContext context ) throws Exception {
        super(Messages.bind(Messages.DeletePreviewVdbJob, pvdbToDelete.getFullPath()), context);
        this.pvdb = pvdbToDelete;
    }

    /**
     * Deletes the Preview VDB associated with the specified model.
     * 
     * @param context the preview context (may not be <code>null</code>)
     * @param deletedModel the model that was just deleted (may not be <code>null</code>)
     * @throws Exception if there is a problem getting the Preview VDB for the deleted model
     */
    public DeletePreviewVdbJob( PreviewContext context,
                                IFile deletedModel ) throws Exception {
        super(Messages.bind(Messages.DeletePreviewVdbJobForModel, deletedModel.getFullPath()), context);
        this.pvdb = context.getPreviewVdb(deletedModel);
        assert (this.pvdb != null);
    }

    /**
     * @return the Preview VDB being deleted (never <code>null</code>)
     */
    public IFile getPvdb() {
        return this.pvdb;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.WorkspacePreviewVdbJob#runImpl(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus runImpl( IProgressMonitor monitor ) throws Exception {
        try {
            this.pvdb.delete(true, monitor);
            return new Status(IStatus.OK, PLUGIN_ID, Messages.bind(Messages.DeletePreviewVdbJobSuccessfullyCompleted,
                                                                   this.pvdb.getFullPath()));
        } catch (Exception e) {
            return new Status(IStatus.ERROR, PLUGIN_ID,
                              Messages.bind(Messages.DeletePreviewVdbJobError, this.pvdb.getFullPath()), e);
        }
    }

}
