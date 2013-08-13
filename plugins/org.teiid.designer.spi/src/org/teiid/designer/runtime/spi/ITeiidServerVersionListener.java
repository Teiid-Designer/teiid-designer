/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/**
 *
 */
public interface ITeiidServerVersionListener {

    /**
     * Server has been changed
     *
     * @param server
     */
    void serverChanged(ITeiidServer server);

    /**
     * Version of Teiid Instance has been changed
     * 
     * @param version
     */
    void versionChanged(ITeiidServerVersion version);
    
}
