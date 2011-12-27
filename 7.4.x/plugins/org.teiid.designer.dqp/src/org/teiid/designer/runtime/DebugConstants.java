/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.PLUGIN_ID;

/**
 *
 */
public interface DebugConstants {

    /**
     * Option indicating if this plugin debug option is turned on.
     */
    String DEBUG = PLUGIN_ID + "/debug"; //$NON-NLS-1$

    /**
     * <strong>Should not be used in the code.</strong>
     */
    String PREVIEW = DEBUG + "/preview"; //$NON-NLS-1$

    /**
     * <strong>Should not be used in the code.</strong>
     */
    String PREVIEW_JOBS = PREVIEW + "/jobs"; //$NON-NLS-1$

    /**
     * Option indicating if a trace message should be logged when a preview job is asked if it should run.
     */
    String PREVIEW_JOB_SHOULD_RUN = PREVIEW_JOBS + "/jobShouldRun"; //$NON-NLS-1$

    /**
     * Option indicating if a trace message should be logged when a preview job is started.
     */
    String PREVIEW_JOB_START = PREVIEW_JOBS + "/jobStart"; //$NON-NLS-1$

    /**
     * Option indicating if a trace message should be logged when a preview job is finished running.
     */
    String PREVIEW_JOB_DONE = PREVIEW_JOBS + "/jobDone"; //$NON-NLS-1$

    /**
     * Option indicating if a trace message should be logged for how long a preview job took to run.
     */
    String PREVIEW_JOB_DURATION = PREVIEW_JOBS + "/jobDuration"; //$NON-NLS-1$

}
