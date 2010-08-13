/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.runtime.DebugConstants;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 *
 */
public abstract class TeiidPreviewVdbCleanupJob extends TeiidPreviewVdbJob {

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
        boolean result = super.shouldRun();

        if (result) {
            // check preference
            result = DqpPlugin.getInstance().getPreferences().getBoolean(PREVIEW_TEIID_CLEANUP_ENABLED,
                                                                         PREVIEW_TEIID_CLEANUP_ENABLED_DEFAULT);

            // add trace message only if not running since superclass has already logged trace message if enabled
            if (!result && DqpPlugin.getInstance().isDebugOptionEnabled(DebugConstants.PREVIEW_JOB_SHOULD_RUN)) {
                Util.log(IStatus.INFO, NLS.bind(Messages.JobShouldRun, getName(), result));
            }
        }

        return result;
    }

}
