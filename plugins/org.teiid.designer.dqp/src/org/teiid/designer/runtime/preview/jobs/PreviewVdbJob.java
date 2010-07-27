/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.runtime.preview.PreviewContext;

/**
 * The <code>PreviewVdbJob</code> interface identifies any job that involves one or more Preview VDBs either in the workspace or
 * deployed to Teiid.
 */
public interface PreviewVdbJob {

    /**
     * The job family. The value is {@value}.
     */
    Object PREVIEW_FAMILY = "org.teiid.designer.runtime.preview.jobs"; //$NON-NLS-1$

    /**
     * @return <code>true</code> if this job completed successfully (i.e., severity is not {@link IStatus#ERROR an error}.
     */
    boolean completedSuccessfully();

    /**
     * @return the preview context (never <code>null</code>)
     */
    PreviewContext getContext();

}
