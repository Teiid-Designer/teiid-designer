/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.suppliers.relational;

import org.eclipse.core.resources.IProject;
import org.teiid.designer.advisor.ui.core.status.AdvisorStatus;
import org.teiid.designer.advisor.ui.core.status.AdvisorStatusManager;
import org.teiid.designer.advisor.ui.core.status.AdvisorStatusUpdateJob;
import org.teiid.designer.advisor.ui.scope.RelationalModelingNature;

/**
 * 
 */
public class RelationalModelingStatusManager extends AdvisorStatusManager {
	private static final String ID = RelationalModelingNature.NATURE_ID;
    private static final String JOB_NAME = "Data Services Project Advisor Validation"; //$NON-NLS-1$
    public static final Object FAMILY_MODEL_PROJECT_STATUS = new Object();

    private RelationalProjectValidationHelper helper;

    /**
     * 
     */
    public RelationalModelingStatusManager() {
        super();
    }
    
    /**
     * 
     */
    @Override
    public String getId() {
        return ID;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.AdvisorStatusManager#getJobFamily()
     */
    @Override
    protected Object getJobFamily() {
        return FAMILY_MODEL_PROJECT_STATUS;
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
    	System.out.println("RelationalModelingStatusManager.generateNewStatus()");
        this.helper.setCurrentProject((IProject)getCurrentObject());
        setCurrentStatus(helper.getCurrentStatus());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.advisor.ui.core.status.AdvisorStatusManager#getStatusSnapshot()
     */
    @Override
    public AdvisorStatus getStatusSnapshot() {
        // TODO: Get helper and return current status
        return helper.getCurrentStatus();
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
        this.helper = new RelationalProjectValidationHelper();
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
