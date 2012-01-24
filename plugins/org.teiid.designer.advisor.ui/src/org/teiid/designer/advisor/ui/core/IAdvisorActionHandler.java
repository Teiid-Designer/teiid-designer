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
public interface IAdvisorActionHandler {

    /**
     * @param type
     * @return
     */
    InfoPopAction[] getActions( int type );

    /**
     * @return
     */
    AdvisorStatus getStatus();

    void setStatus( AdvisorStatus status );
}
