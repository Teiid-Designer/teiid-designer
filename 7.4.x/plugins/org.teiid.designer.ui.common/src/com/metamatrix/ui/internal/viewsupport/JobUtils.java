/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.viewsupport;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import com.metamatrix.ui.UiConstants;

/**
 * The <code>JobUtils</code> class provides job utility methods when running in the Eclipse environment.
 * 
 * @since 5.0
 */
public class JobUtils {
	
	/**
	 * Determines if a job with the given name exists within the Job Manager
	 * 
	 * @param jobName
	 * @return true if job exists, fals if not
	 */
    public static boolean jobExists( String jobName ) {
        Job[] allJobs = Job.getJobManager().find(null);
        for (int i = 0; i < allJobs.length; i++) {
            if (allJobs[i].getName() != null && allJobs[i].getName().equals(jobName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates if the current job, if there is one, is for the specified family
     * 
     * @param family the job family being requested
     * @return <code>true</code> if current job exists and is of the specified family; <code>false</code> if no current job or
     *         current job is not of the specified family;
     */
    public static boolean jobIsRunning( Object family ) {
        boolean result = false;

        if ((family != null)) {
            IJobManager jobMgr = Job.getJobManager();
            Job currentJob = jobMgr.currentJob();
            if (currentJob != null) {
                return currentJob.belongsTo(family);
            }
        }

        return result;
    }

    /**
     * Indicates if any jobs from the specified families are waiting, executing, or sleeping.
     * 
     * @param theJobFamilies the job families whose statuses are being requested
     * @param theMustExistInAllFamiliesFlag the flag indicating if jobs must exist in all families
     * @return <code>true</code> if jobs exist; <code>false</code> if jobs do not exist or if the specified family is
     *         <code>null</code>
     */
    public static boolean jobsExist( List theJobFamilies,
                                     boolean theMustExistInAllFamiliesFlag ) {
        boolean result = false;

        if ((theJobFamilies != null) && !theJobFamilies.isEmpty()) {
            IJobManager jobMgr = Job.getJobManager();

            for (int size = theJobFamilies.size(), i = 0; i < size; ++i) {
                Object family = theJobFamilies.get(i);

                if ((family != null) && (jobMgr.find(family).length != 0)) {
                    result = true;
                }

                if ((theMustExistInAllFamiliesFlag && !result) || result) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Method to set the auto-build state for the Workspace.
     * 
     * @param doBuild
     * @return true if changed state, false if not
     */
    public static boolean setAutoBuild( final boolean doBuild ) {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        if (workspace.isAutoBuilding() == doBuild) {
            // Did not change autobuild value
            return false;
        }

        IWorkspaceDescription description = workspace.getDescription();
        description.setAutoBuilding(doBuild);
        try {
            // System.out.println(" JobUtils.setAutoBuild(): NEW AUTOBUILD = " + doBuild);
            workspace.setDescription(description);
        } catch (CoreException e) {
            UiConstants.Util.log(IStatus.ERROR, e, UiConstants.Util.getString("JobUtils.autoBuildProblem")); //$NON-NLS-1$
        }

        return true;
    }

    /**
     * Indicates if any build/validation jobs are waiting, executing, or sleeping.
     * 
     * @return <code>true</code> if validation jobs exist; <code>false</code>.
     * @since 5.0
     */
    public static boolean validationJobsExist() {
        List<Object> families = new ArrayList<Object>(2);
        families.add(ResourcesPlugin.FAMILY_MANUAL_BUILD);
        families.add(ResourcesPlugin.FAMILY_AUTO_BUILD);

        return jobsExist(families, false);
    }

    /**
     * No construction allowed.
     */
    private JobUtils() {
    }

}
