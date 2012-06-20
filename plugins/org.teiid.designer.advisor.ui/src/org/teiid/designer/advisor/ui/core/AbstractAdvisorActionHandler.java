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
     * @see IAdvisorActionHandler#getStatus()
     */
    @Override
    public AdvisorStatus getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see IAdvisorActionHandler#setStatus(org.eclipse.core.runtime.IStatus)
     */
    @Override
	public void setStatus( AdvisorStatus status ) {
        this.status = status;
    }

}
