/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import static com.metamatrix.modeler.dqp.DqpPlugin.PLUGIN_ID;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;

/**
 *
 */
public final class MergePreviewVdbJob extends TeiidPreviewVdbJob {

    /**
     * The name of the VDB being merged into the target VDB (never <code>null</code>).
     */
    private final String sourceVdbName;

    /**
     * The VDB version of the source Preview VDB.
     */
    private final int sourceVdbVersion;

    /**
     * The name of the VDB that the source VDB is being merged into (never <code>null</code>).
     */
    private final String targetVdbName;

    /**
     * The VDB version of the target Preview VDB. This is the project PVDB.
     */
    private final int targetVdbVersion;

    /**
     * @param sourceVdbName the name of the VDB being merged into the target VDB (may not be <code>null</code> or empty)
     * @param sourceVdbVersion the version of the source VDB
     * @param targetVdbName the name of the VDB that the source VDB is being merged into (may not be <code>null</code> or empty)
     * @param targetVdbVersion the version of the target VDB
     * @param context the preview context (may not be <code>null</code>)
     * @param previewServer the preview server (may be <code>null</code>)
     */
    public MergePreviewVdbJob( String sourceVdbName,
                               int sourceVdbVersion,
                               String targetVdbName,
                               int targetVdbVersion,
                               PreviewContext context,
                               Server previewServer ) {
        super(Messages.bind(Messages.MergePreviewVdbJob, sourceVdbName, targetVdbName), context, previewServer);

        assert (sourceVdbName != null && sourceVdbName.length() != 0) : "source VDB is null or empty"; //$NON-NLS-1$
        assert (targetVdbName != null && targetVdbName.length() != 0) : "target VDB is null or empty"; //$NON-NLS-1$

        this.sourceVdbName = sourceVdbName;
        this.sourceVdbVersion = sourceVdbVersion;
        this.targetVdbName = targetVdbName;
        this.targetVdbVersion = targetVdbVersion;
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
            admin.mergeVdbs(this.sourceVdbName, this.sourceVdbVersion, this.targetVdbName, this.targetVdbVersion);
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, Messages.bind(Messages.MergePreviewVdbJobError,
                                                                                       this.sourceVdbName,
                                                                                       this.targetVdbName), e));
        }

        // all good
        return new Status(IStatus.INFO, PLUGIN_ID, Messages.bind(Messages.MergePreviewVdbJobSuccessfullyCompleted,
                                                                 this.sourceVdbName,
                                                                 this.targetVdbName));
    }

}
