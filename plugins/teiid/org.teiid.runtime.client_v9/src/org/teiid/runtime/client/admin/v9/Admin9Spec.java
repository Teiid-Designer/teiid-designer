/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.admin.v9;

import java.io.InputStream;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.VDB;
import org.teiid.adminapi.VDB.Status;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.admin.AdminSpec;

/**
 *
 */
public class Admin9Spec extends AdminSpec {

    private static final String TEST_VDB = "<vdb name=\"ping\" version=\"1\">" + //$NON-NLS-1$
    "<model visible=\"true\" name=\"Foo\" type=\"PHYSICAL\" path=\"/dummy/Foo\">" + //$NON-NLS-1$
    "<source name=\"s\" translator-name=\"loopback\"/>" + //$NON-NLS-1$
    "<metadata type=\"DDL\"><![CDATA[CREATE FOREIGN TABLE G1 (e1 string, e2 integer);]]> </metadata>" + //$NON-NLS-1$
    "</model>" + //$NON-NLS-1$
    "</vdb>"; //$NON-NLS-1$

    /**
     * @param teiidVersion
     */
    public Admin9Spec(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    @Override
    public Admin createAdmin(ITeiidServer teiidServer) throws AdminException {
        ITeiidAdminInfo teiidAdminInfo = teiidServer.getTeiidAdminInfo();
        char[] passwordArray = null;
        if (teiidAdminInfo.getPassword() != null) {
            passwordArray = teiidAdminInfo.getPassword().toCharArray();
        }

        Admin admin = Admin9Factory.getInstance().createAdmin(teiidServer.getServerVersion(),
                                                              teiidServer.getHost(),
                                                              teiidAdminInfo.getPortNumber(),
                                                              teiidAdminInfo.getUsername(),
                                                              passwordArray);

        return admin;
    }

    @Override
    public String getTestVDB() {
        return TEST_VDB;
    }

    @Override
    public Status getLoadingVDBStatus() {
        return VDB.Status.LOADING;
    }

    @Override
    public void deploy(Admin admin, String fileName, InputStream iStream) throws AdminException {
        admin.deploy(fileName, iStream);
    }

    @Override
    public void undeploy(Admin admin, String vdbName, String version) throws AdminException {
        admin.undeploy(vdbName);
    }
}
