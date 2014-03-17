/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.admin;

import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.runtime.client.admin.v7.Admin7Spec;
import org.teiid.runtime.client.admin.v8.Admin8Spec;

/**
 *
 */
public abstract class AdminSpec {

    protected static final ITeiidServerVersion TEIID_8_0 = TeiidServerVersion.TEIID_8_SERVER;

    private final ITeiidServerVersion teiidVersion;

    /**
     * @param teiidVersion
     */
    public AdminSpec(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
    }

    protected ITeiidServerVersion getTeiidVersion() {
        return this.teiidVersion;
    }

    /**
     * @param teiidVersion
     * @return admin spec for specific teiid version
     */
    public static AdminSpec getInstance(ITeiidServerVersion teiidVersion) {
        if (teiidVersion.isLessThan(TEIID_8_0))
            return new Admin7Spec(teiidVersion);
        else
            return new Admin8Spec(teiidVersion);
    }

    /**
     * @return test VDB configuration
     */
    public abstract String getTestVDB();

    /**
     * @param teiidServer
     * @return new admin instance
     * @throws AdminException 
     */
    public abstract Admin createAdmin(ITeiidServer teiidServer) throws AdminException;
}
