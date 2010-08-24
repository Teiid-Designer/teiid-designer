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
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;

/**
 * The <code>DeletePreviewVdbJob</code> job deletes a Preview VDB from the Eclipse workspace.
 */
public final class DeletePreviewVdbJob extends WorkspacePreviewVdbJob {

    /**
     * The Preview VDB to be deleted (never <code>null</code>).
     */
    private final IFile pvdbFile;

    /**
     * Deletes the specified Preview VDB.
     * 
     * @param pvdbToDelete the Preview VDB to delete (may not be <code>null</code>)
     * @param context the preview context (may not be <code>null</code>)
     * @throws Exception if there is a problem getting the Preview VDB for the deleted model
     */
    public DeletePreviewVdbJob( IFile pvdbToDelete,
                                PreviewContext context ) throws Exception {
        super(NLS.bind(Messages.DeletePreviewVdbJob, pvdbToDelete.getFullPath()), context);
        this.pvdbFile = pvdbToDelete;
        assert (this.pvdbFile != null) : "PVDB is null"; //$NON-NLS-1$
        initialize();
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
        super(NLS.bind(Messages.DeletePreviewVdbJobForModel, deletedModel.getFullPath()), context);
        this.pvdbFile = context.getPreviewVdb(deletedModel);
        assert (this.pvdbFile != null) : "PVDB is null"; //$NON-NLS-1$
        initialize();
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
     * <strong>Must be called by constructors.</strong>
     */
    private void initialize() {
        assert (this.pvdbFile != null) : "initialize() called before PVDB file is set"; //$NON-NLS-1$
        // set job scheduling rule on the PVDB resource
        setRule(getSchedulingRuleFactory().deleteRule(this.pvdbFile));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.WorkspacePreviewVdbJob#runImpl(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus runImpl( IProgressMonitor monitor ) throws Exception {
        try {
            this.pvdbFile.delete(true, monitor);
            return new Status(IStatus.OK, PLUGIN_ID, NLS.bind(Messages.DeletePreviewVdbJobSuccessfullyCompleted,
                                                              this.pvdbFile.getFullPath()));
        } catch (Exception e) {
            return new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.DeletePreviewVdbJobError, this.pvdbFile.getFullPath()),
                              e);
        }
    }

}
