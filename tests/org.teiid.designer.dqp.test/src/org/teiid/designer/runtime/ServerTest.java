/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import org.eclipse.wst.server.core.IServer;
import org.junit.Before;
import org.junit.Test;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.spi.EventManager;
import org.teiid.designer.runtime.spi.HostProvider;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/**
 * 
 */
public class ServerTest {

    private static final String PORT = "31000";
    private static final String USER = "user";
    private static final String PSWD = "pswd";
    private static final ISecureStorageProvider SECURE_STORAGE_PROVIDER = new DefaultStorageProvider();
    private static final boolean SECURE = true;

    private ITeiidServerVersion serverVersion;
    private ITeiidAdminInfo adminInfo;
    private ITeiidJdbcInfo jdbcInfo;
    private EventManager eventMgr;
    private ITeiidServer teiidServer;
    private IServer parentServer;

    @Before
    public void beforeEach() {
        this.serverVersion = mock(ITeiidServerVersion.class);
        this.adminInfo = mock(ITeiidAdminInfo.class);
        this.jdbcInfo = mock(ITeiidJdbcInfo.class);
        this.eventMgr = mock(EventManager.class);
        this.parentServer = mock(IServer.class);

        ITeiidServerManager teiidServerManager = mock(ITeiidServerManager.class);
        ModelerCore.setTeiidServerManager(teiidServerManager);

        this.teiidServer = new TeiidServer(serverVersion, adminInfo, jdbcInfo, eventMgr, parentServer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerWithNullServerVersion() {
        new TeiidServer(null, this.adminInfo, this.jdbcInfo, this.eventMgr, this.parentServer);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerWithNullAdminInfo() {
        new TeiidServer(serverVersion, null, this.jdbcInfo, this.eventMgr, this.parentServer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerWithNullJdbcInfo() {
        new TeiidServer(serverVersion, this.adminInfo, null, this.eventMgr, this.parentServer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerWithNullEventManager() {
        new TeiidServer(serverVersion, this.adminInfo, this.jdbcInfo, null, this.parentServer);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerWithNullParentServer() {
        new TeiidServer(serverVersion, this.adminInfo, this.jdbcInfo, this.eventMgr, null);
    }

    @Test
    public void shouldReturnFalseForEqualsWithNull() {
        assertThat(this.teiidServer.equals(null), is(false));
    }

    @Test
    public void shouldReturnTrueForEqualsWithSelf() {
        assertThat(this.teiidServer.equals(this.teiidServer), is(true));
    }

    @Test
    public void shouldSetCustomLabelWithNonNullValue() {
        String CUSTOM_LABEL = "customLabel";
        this.teiidServer.setCustomLabel(CUSTOM_LABEL);
        assertThat(this.teiidServer.getCustomLabel(), is(CUSTOM_LABEL));
    }

    @Test
    public void shouldSetCustomLabelWithNullValue() {
        this.teiidServer.setCustomLabel("oldLabel");
        this.teiidServer.setCustomLabel(null);
        assertThat(this.teiidServer.getCustomLabel(), nullValue());
    }

    @Test
    public void shouldVerifyCustomLabelIsNullAfterConstruction() {
        assertThat(this.teiidServer.getCustomLabel(), nullValue());
    }

    @Test
    public void shouldVerifyDefaultHostAfterConstruction() {
        assertThat(this.teiidServer.getHost(), is(HostProvider.DEFAULT_HOST));
    }

    @Test
    public void shouldBeEqualsWhenAllPropertiesAreTheSame() {
        ITeiidServer server1 = new TeiidServer(serverVersion,
                                    new TeiidAdminInfo(PORT, USER, SECURE_STORAGE_PROVIDER, PSWD, SECURE),
                                    new TeiidJdbcInfo(PORT, USER, SECURE_STORAGE_PROVIDER, PSWD, SECURE),
                                    this.eventMgr, this.parentServer);
        ITeiidServer server2 = new TeiidServer(serverVersion,
                                    new TeiidAdminInfo(PORT, USER, SECURE_STORAGE_PROVIDER, PSWD, SECURE),
                                    new TeiidJdbcInfo(PORT, USER, SECURE_STORAGE_PROVIDER, PSWD, SECURE),
                                    this.eventMgr, this.parentServer);
        assertThat(server1.equals(server2), is(true));
    }

}
