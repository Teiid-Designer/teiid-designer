/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.core;

import org.teiid.designer.advisor.ui.core.status.AdvisorStatus;


/**
 * 
 */
public abstract class AbstractAdvisorActionHandler implements IAdvisorActionHandler {

    private AdvisorStatus status;

    /**
     * 
     */
    public AbstractAdvisorActionHandler() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IAdvisorActionHandler.AdvisorActionHandler#getStatus()
     */
    @Override
    public AdvisorStatus getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IAdvisorActionHandler.AdvisorActionHandler#setStatus(org.eclipse.core.runtime.IStatus)
     */
    public void setStatus( AdvisorStatus status ) {
        this.status = status;
    }

}
