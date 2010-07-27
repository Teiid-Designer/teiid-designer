/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.preview.PreviewContext;

/**
 *
 */
public abstract class TeiidPreviewVdbCleanupJob extends TeiidPreviewVdbJob {

    /**
     * Preview is disabled by setting the system property "org.teiid.designer.runtime.preview.teiid.cleanup.disable" to
     * <code>true</code>. Defaults to <code>false</code>. Disabling will prevent Preview VDBs and data sources from being deleted
     * off Teiid instances.
     */
    public static final boolean DISABLE = Boolean.getBoolean("org.teiid.designer.runtime.preview.teiid.cleanup.disable"); //$NON-NLS-1$

    /**
     * The Teiid cleanup job family identifier. The value is {@value} .
     */
    public static final Object TEIID_CLEANUP_FAMILY = TeiidPreviewVdbJob.TEIID_PREVIEW_FAMILY + ".cleanup"; //$NON-NLS-1$

    /**
     * @param name the name of the job (may not be <code>null</code>)
     * @param context the preview context (may not be <code>null</code>)
     * @param previewServer the preview server (may not be <code>null</code>)
     */
    public TeiidPreviewVdbCleanupJob( String name,
                                      PreviewContext context,
                                      Server previewServer ) {
        super(name, context, previewServer);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
     */
    @Override
    public boolean belongsTo( Object family ) {
        return (TEIID_CLEANUP_FAMILY == family);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
     */
    @Override
    public boolean shouldRun() {
        return !DISABLE && super.shouldRun();
    }

}
