/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview;

import org.eclipse.osgi.util.NLS;

/**
 *
 *
 * @since 8.0
 */
public final class Messages extends NLS {

    public static String CreatePreviewVdbJob;
    public static String CreatePreviewVdbJobError;
    public static String CreatePreviewVdbJobSuccessfullyCompleted;
    public static String DeleteDeployedPreviewVdbJob;
    public static String DeleteDeployedPreviewVdbJobError;
    public static String DeleteDeployedPreviewVdbJobSuccessfullyCompleted;
    public static String DeletedModelProcessingError;
    public static String DeleteOrphanedPreviewVdbsJob;
    public static String DeleteOrphanedPreviewVdbsJobError;
    public static String DeletePreviewVdbJob;
    public static String DeletePreviewVdbJobError;
    public static String DeletePreviewVdbJobForModel;
    public static String DeletePreviewVdbJobSuccessfullyCompleted;
    public static String DeployPreviewVdbDependencyError;
    public static String DeployStatusCacheError;
    public static String JobCanceled;
    public static String JobFinished;
    public static String JobShouldRun;
    public static String JobStarted;
    public static String LessThanAMinuteDurationJob;
    public static String LessThanAnHourDurationJob;
    public static String LessThanASecondDurationJob;
    public static String LongDurationJob;
    public static String MergePreviewVdbJob;
    public static String MergePreviewVdbJobError;
    public static String MergePreviewVdbJobSuccessfullyCompleted;
    public static String ModelChangedJob;
    public static String ModelChangedJobError;
    public static String ModelDoesNotHaveConnectionInfoError;
    public static String ModelErrorMarkerExists;
    public static String ModelProjectOpenedJob;
    public static String ModelProjectOpenedJobError;
    public static String PreviewSetupConnectionInfoTask;
    public static String PreviewSetupDeployTask;
    public static String PreviewSetupMergeTask;
    public static String PreviewSetupRefreshWorkspaceTask;
    public static String PreviewSetupTask;
    public static String PreviewSetupValidationCheckTask;
    public static String PreviewShutdownTeiidCleanupTask;
    public static String PreviewVdbDeletedPostProcessingError;
    public static String UnexpectedErrorGettingVdbMarkers;
    public static String UnexpectedErrorRunningJob;
    public static String UpdatePreviewVdbJob;
    public static String UpdatePreviewVdbJobError;
    public static String UpdatePreviewVdbJobSuccessfullyCompleted;
    public static String JarDeploymentJarNotFound;
    public static String JarDeploymentFailed;
    public static String JarDeploymentJarNotReadable;

    static {
        NLS.initializeMessages("org.teiid.designer.runtime.preview.messages", Messages.class); //$NON-NLS-1$
    }

}
