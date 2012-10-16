/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;

/**
 * The <code>DeleteDeployedPreviewVdbJob</code> deletes a Preview VDB from a Teiid server and also deletes its associated JNDI
 * name.
 *
 * @since 8.0
 */
public final class DeleteDeployedPreviewVdbJob extends TeiidPreviewVdbCleanupJob {

    /**
     * The data source name (never <code>null</code> or empty).
     */
    private final String jndiName;

    /**
     * The Preview VDB name (never <code>null</code> or empty).
     */
    private final String pvdbName;

    /**
     * The version of the Preview VDB.
     */
    private final int pvdbVersion;

    /**
     * @param pvdbName the name of the Preview VDB (may not be <code>null</code> or empty)
     * @param pvdbVersion the Preview VDB version
     * @param jndiName the data source name (may not be <code>null</code> or empty)
     * @param context the preview context (may not be <code>null</code>)
     * @param previewServer the preview server (may be <code>null</code>)
     */
    public DeleteDeployedPreviewVdbJob( String pvdbName,
                                        int pvdbVersion,
                                        String jndiName,
                                        PreviewContext context,
                                        TeiidServer previewServer ) {
        super(NLS.bind(Messages.DeleteDeployedPreviewVdbJob, pvdbName), context, previewServer);

        assert !StringUtilities.isEmpty(pvdbName) : "Preview VDB name is null or empty"; //$NON-NLS-1$
        assert !StringUtilities.isEmpty(jndiName) : "JNDI name is null or empty"; //$NON-NLS-1$

        this.pvdbName = pvdbName;
        this.pvdbVersion = pvdbVersion;
        this.jndiName = jndiName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.TeiidPreviewVdbJob#runImpl(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus runImpl( IProgressMonitor monitor ) throws Exception {
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        TeiidServer teiidServer = getPreviewServer();
        int errors = 0;
        IStatus deleteVdbErrorStatus = null;

        // delete PVDB from server
        try {
            if (teiidServer.getAdmin().getVdb(this.pvdbName) != null) {
                teiidServer.getAdmin().undeployVdb(this.pvdbName, this.pvdbVersion);
            }
        } catch (Exception e) {
            ++errors;
            deleteVdbErrorStatus = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.DeleteDeployedPreviewVdbJobError,
                                                                                 this.pvdbName), e);
        }

        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        // delete data source from server
        IStatus deleteDataSourceErrorStatus = null;

        try {
            if (teiidServer.getAdmin().dataSourceExists(this.jndiName)) {
                teiidServer.getAdmin().deleteDataSource(this.jndiName);
            }
        } catch (Exception e) {
            ++errors;
            deleteDataSourceErrorStatus = new Status(IStatus.ERROR, PLUGIN_ID,
                                                     NLS.bind(Messages.DeleteDeployedPreviewVdbJobError, this.pvdbName), e);
        }

        // no problems
        if (errors == 0) {
            return new Status(IStatus.INFO, PLUGIN_ID, NLS.bind(Messages.DeleteDeployedPreviewVdbJobSuccessfullyCompleted,
                                                                this.pvdbName));
        }

        // couldn't delete PVDB or data source
        if (errors == 2) {
            IStatus[] statuses = new IStatus[2];
            statuses[0] = deleteVdbErrorStatus;
            statuses[1] = deleteDataSourceErrorStatus;
            return new MultiStatus(PLUGIN_ID, IStatus.OK, statuses, NLS.bind(Messages.DeleteDeployedPreviewVdbJobError,
                                                                             this.pvdbName), null);
        }

        // just couldn't delete VDB
        if (deleteVdbErrorStatus != null) {
            throw new CoreException(deleteVdbErrorStatus);
        }

        // just couldn't delete data source
        throw new CoreException(deleteDataSourceErrorStatus);
    }

}
