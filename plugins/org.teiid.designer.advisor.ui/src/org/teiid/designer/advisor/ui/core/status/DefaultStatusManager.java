package org.teiid.designer.advisor.ui.core.status;

import org.teiid.designer.advisor.ui.views.status.StatusValidationConstants;

public class DefaultStatusManager  extends AdvisorStatusManager {
    private static final String JOB_NAME = "Default Advisor"; //$NON-NLS-1$
    public static final Object DEFAULT_ADVISOR_JOB_FAMILY = new Object();

    /**
     * 
     */
    public DefaultStatusManager() {
        super();
    }
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.AdvisorStatusManager#getJobFamily()
     */
    @Override
    protected Object getJobFamily() {
        return DEFAULT_ADVISOR_JOB_FAMILY;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.AdvisorStatusManager#getJobName()
     */
    @Override
    protected String getJobName() {
        return JOB_NAME;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.AdvisorStatusManager#generateNewStatus()
     */
    @Override
    protected void generateNewStatus() {
    	System.out.println("DefaultStatusProvider.generateNewStatus()");
        setCurrentStatus(StatusValidationConstants.STATUS_MSGS.ADVISOR_NO_PROJECT_SELECTED);
    }

    /**
     * {@inheritDoc}{

}

     * 
     * @see org.teiid.designer.advisor.ui.core.status.AdvisorStatusManager#getStatusSnapshot()
     */
    @Override
    public AdvisorStatus getStatusSnapshot() {
        // TODO: Get helper and return current status
        return StatusValidationConstants.STATUS_MSGS.ADVISOR_NO_PROJECT_SELECTED;
    }
    
    /**
     * 
     */
    public AdvisorStatusUpdateJob getNewStatusUpdateJob() {
    	return new AdvisorStatusUpdateJob(getJobName(), this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.AdvisorStatusManager#setup()
     */
    @Override
    protected void setup() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.AdvisorStatusManager#updateStatus(boolean)
     */
    @Override
    public void updateStatus( boolean forceUpdate ) {
        super.updateStatus(forceUpdate);
    }

}
