package org.teiid.designer.advisor.ui.core.status;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class AdvisorStatusUpdateJob extends Job {
    private boolean contextChanged;
    
    private AdvisorStatusManager statusManager;

    // constructors:
    public AdvisorStatusUpdateJob( String name, AdvisorStatusManager manager ) {
        super(name);
        setUser(false);
        setSystem(true);
        
        this.statusManager = manager;
    }

    @Override
    public boolean belongsTo( Object family ) {
        return family == this.statusManager.getJobFamily();
    }

    // Implementation of abstract methods:
    @Override
    protected IStatus run( IProgressMonitor monitor ) {
    	System.out.println(" >>> AdvisorStatusUpdateJob.run()");
        // let's wire this job up to listen for vdb context change events

    	this.statusManager.generateNewStatus();

        // Only we know about the last context, so we need to set the flag to allow the listeners
        // that knowledge
        if (this.statusManager.getCurrentStatus() != null) {
            // status.setContextChanged(contextChanged);

        	this.statusManager.notifyStatusChanged(this.statusManager.getCurrentStatus());
        }
        return Status.OK_STATUS;
    }

    public void setContextChanged( boolean theContextChanged ) {
        contextChanged = theContextChanged;
    }
}