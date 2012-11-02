/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * Exception thrown if a {@link ITeiidServer} is constructed but a related
 * {@link IServer} cannot be found in the {@link ServerCore#getServers()}
 * collection.
 * 
 * @since 8.0
 */
public class OrphanedTeiidServerException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String teiidServerUrl;
    
    /**
     * Create a new instance
     * 
     * @param teiidAdminInfo 
     */
    public OrphanedTeiidServerException(ITeiidAdminInfo teiidAdminInfo) {
        this.teiidServerUrl = teiidAdminInfo.getUrl();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return DqpPlugin.Util.getString(getClass().getSimpleName() + ".message", teiidServerUrl); //$NON-NLS-1$
    }

}
