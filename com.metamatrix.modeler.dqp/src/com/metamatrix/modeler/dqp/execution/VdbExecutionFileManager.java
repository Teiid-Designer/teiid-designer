/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.execution;



/** 
 * This component is responsible for copying the validated VDB, system vdb, dqp configuration, and
 * properties files to a well known "vdbExecutionWorkingFolder" prior to launching DQP
 * @since 4.3
 */
public interface VdbExecutionFileManager {

    public static final String VDB_EXT = ".vdb";  //$NON-NLS-1$
    public static final String LOG_EXT = ".log";  //$NON-NLS-1$
    public static final String DQP_LOG_EXT = "_1.log";  //$NON-NLS-1$
    public static final String DQP_LOG_PROP = "dqp.logFile";  //$NON-NLS-1$
    public static final String DQP_LOG_LEVEL_PROP = "dqp.logLevel";  //$NON-NLS-1$
}
