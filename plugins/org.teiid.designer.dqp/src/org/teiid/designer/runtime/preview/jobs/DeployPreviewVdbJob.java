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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;

/**
 * The <code>DeployPreviewVdbJob</code> deploys a Preview VDB to a Teiid server.
 */
public final class DeployPreviewVdbJob extends TeiidPreviewVdbJob {

    /**
     * The Preview VDB being deployed.
     */
    private final IFile pvdb;

    /**
     * @param pvdb the Preview VDB being deployed (may not be <code>null</code>)
     * @param context the preview context (may not be <code>null</code>)
     * @param previewServer the preview server (may be <code>null</code>)
     */
    public DeployPreviewVdbJob( IFile pvdb,
                                PreviewContext context,
                                Server previewServer ) {
        super(Messages.bind(Messages.DeployPreviewVdbJob, pvdb.getFullPath().removeFileExtension().lastSegment()), context,
              previewServer);
        this.pvdb = pvdb;
    }

    /**
     * @return the Preview VDB being deployed (never <code>null</code>)
     */
    public IFile getPreviewVdb() {
        return this.pvdb;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.TeiidPreviewVdbJob#runImpl(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus runImpl( IProgressMonitor monitor ) throws Exception {
        ExecutionAdmin admin = getPreviewServer().getAdmin();

        try {
            admin.deployVdb(this.pvdb);
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, Messages.bind(Messages.DeployPreviewVdbJobError,
                                                                                       this.pvdb.getFullPath()), e));
        }

        return new Status(IStatus.INFO, PLUGIN_ID, Messages.bind(Messages.DeployPreviewVdbJobSuccessfullyCompleted,
                                                                 this.pvdb.getFullPath()));
    }

}
