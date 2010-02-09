/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.viewsupport;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import com.metamatrix.ui.UiConstants;


/**
 * The <code>JobUtils</code> class provides job utility methods when running in the Eclipse
 * environment.
 * @since 5.0
 */
public class JobUtils {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean jobExists(String jobName) {
        Job[] allJobs = Job.getJobManager().find(null);
        for( int i=0; i<allJobs.length; i++ ) {
            if( allJobs[i].getName() != null && allJobs[i].getName().equals(jobName) ) {
                return true;
            }
        }
        return false;
    }

    public static boolean setAutoBuild(final boolean doBuild) {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        if( workspace.isAutoBuilding() == doBuild ) {
            // Did not change autobuild value
            return false;
        }

        IWorkspaceDescription description = workspace.getDescription();
        description.setAutoBuilding(doBuild);
        try {
            //System.out.println(" JobUtils.setAutoBuild(): NEW AUTOBUILD = " + doBuild);
            workspace.setDescription(description);
        } catch (CoreException e) {
            UiConstants.Util.log(IStatus.ERROR, e, UiConstants.Util.getString("JobUtils.autoBuildProblem")); //$NON-NLS-1$
        }

        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * No construction allowed.
     */
    private JobUtils() {}

}
