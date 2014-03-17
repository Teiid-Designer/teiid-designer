/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.admin.v7;

import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.admin.AdminSpec;

/**
 *
 */
public class Admin7Spec extends AdminSpec {

    /**
     * Test VDB model
     */
    public static final String TEST_VDB = "<vdb name=\"ping\" version=\"1\">" + //$NON-NLS-1$
    "<model visible=\"true\" name=\"Foo\" type=\"VIRTUAL\" path=\"/dummy/Foo\">" + //$NON-NLS-1$
    "<source name=\"s\" translator-name=\"loopback\"/>" + //$NON-NLS-1$
    "</model>" + //$NON-NLS-1$
    "</vdb>"; //$NON-NLS-1$

    /**
     * @param teiidVersion
     */
    public Admin7Spec(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    @Override
    public String getTestVDB() {
        return TEST_VDB;
    }

    @Override
    public Admin createAdmin(ITeiidServer teiidServer) throws AdminException {
        ITeiidAdminInfo teiidAdminInfo = teiidServer.getTeiidAdminInfo();
        char[] passwordArray = null;
        if (teiidAdminInfo.getPassword() != null) {
            passwordArray = teiidAdminInfo.getPassword().toCharArray();
        }

        Admin admin = Admin7Factory.getInstance().createAdmin(teiidAdminInfo.getUsername(), 
                                                            passwordArray, 
                                                            teiidAdminInfo.getUrl());

        return admin;
    }
}
