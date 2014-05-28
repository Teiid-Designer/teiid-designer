/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.wst.server.core.IServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.runtime.spi.EventManager;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.HostProvider;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

/**
 * 
 */
public class ServerManagerTest {

    private static final String RESTORED_SERVER1_URL = "mm://localhost:8080";
    private static final String RESTORED_SERVER1_USER = "user8080";

    private static final String RESTORED_SERVER2_URL = "mm://localhost:8180";
    private static final String RESTORED_SERVER2_USER = "user8180";

    private static final String RESTORED_SERVER3_URL = "mm://localhost:8280";
    private static final String RESTORED_SERVER3_USER = "user8280";

    private TeiidServerManager mgr;
    
    private IServersProvider serversProvider = new TestServersProvider();

    @Mock
    private ITeiidServer server1;

    @Before
    public void beforeEach() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(server1.getServerVersion()).thenReturn(TeiidServerVersion.DEFAULT_TEIID_SERVER);

        String stateLocationPath = System.getProperty("java.io.tmpdir");
        this.mgr = new TeiidServerManager(stateLocationPath, serversProvider, new DefaultStorageProvider());
        // State must be set to started
        this.mgr.restoreState();
    }
    
        
    @After
    public void afterEach() throws Exception {
    	MockObjectFactory.dispose();
    }

    @Test
    public void shouldAllowGetServers() {
        this.mgr.getServers();
    }

    @Test
    public void shouldAllowShutdown() {
        try {
            this.mgr.shutdown(null);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void shouldConfirmRegisteredListenerIsNotified() {
        // create and register listener
        final IExecutionConfigurationListener listener = mock(IExecutionConfigurationListener.class);
        this.mgr.addListener(listener);

        // generate event
        this.mgr.addServer(this.server1);

        // test to make sure listener was called twice (once for adding server, once for setting default teiid instance)
        verify(listener, times(2)).configurationChanged((ExecutionConfigurationEvent) anyObject());
    }

    @Test
    public void shouldConfirmServerIsNotAddedMultipleTimes() {
        // add
        this.mgr.addServer(this.server1);
        assertThat(this.mgr.getServers().size(), is(1));

        // add again
        this.mgr.addServer(this.server1);
        assertThat(this.mgr.getServers().size(), is(1));
    }

    @Test
    public void shouldConfirmServerIsNotRegistered() {
        assertThat(this.mgr.isRegistered(this.server1), is(false));
    }

    @Test
    public void shouldConfirmServerIsRegistered() {
        assertThat(this.mgr.addServer(this.server1).isOK(), is(true));
        assertThat(this.mgr.isRegistered(this.server1), is(true));
    }

    @Test
    public void shouldConfirmServerIsRemoved() {
        // first add
        this.mgr.addServer(this.server1);
        assertThat(this.mgr.isRegistered(this.server1), is(true));

        // now remove
        assertThat(this.mgr.removeServer(this.server1).isOK(), is(true));
        assertThat(this.mgr.isRegistered(this.server1), is(false));
    }

    @Test
    public void shouldConfirmUnregisteredListenerIsNotNotified() {
        // create and register listener
        final IExecutionConfigurationListener listener = mock(IExecutionConfigurationListener.class);
        this.mgr.addListener(listener);
        this.mgr.removeListener(listener);

        // generate event
        this.mgr.addServer(this.server1);

        // test to make sure listener was called once
        verify(listener, never()).configurationChanged((ExecutionConfigurationEvent) anyObject());
    }

    @Test
    public void shouldGetServerById() {
        String serverId = "mm://server:4321-8.2-12345";

        this.mgr.addServer(server1);
        when(server1.getId()).thenReturn(serverId);
        assertThat(this.mgr.getServer(serverId), is(server1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullListenerForAddListener() {
        this.mgr.addListener(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullListenerForRemoveListener() {
        this.mgr.removeListener(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullReplacedServerForUpdateServer() {
        this.mgr.updateServer(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullServerForAddServer() {
        this.mgr.addServer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullServerForIsRegistered() {
        this.mgr.isRegistered(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullServerForRemoveServer() {
        this.mgr.removeServer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullUpdatedServerForUpdateServer() {
        this.mgr.updateServer(mock(ITeiidServer.class), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullUrlForGetServer() {
        this.mgr.getServer((String) null);
    }

    @Test
    public void shouldRestoreServerRegistry() throws Exception {
        // setup
        MockObjectFactory.createModelContainer();

        this.mgr = new TeiidServerManager(SmartTestDesignerSuite.getTestDataPath(getClass()), 
                                                                       serversProvider, new DefaultStorageProvider());
        this.mgr.restoreState();
        assertThat(this.mgr.getServers().size(), is(2));

        ITeiidServerVersion serverVersion = new TeiidServerVersion("8.1.2");
        
        String customLabel = "My Custom Label";
        String adminPort = "31443";
        boolean adminSecure = true;
        String adminUser = "admin";
        String adminPassword = "admin";
        String jdbcPort = "31000";
        boolean jdbcSecure = false;
        String jdbcUser = "teiid";
        String jdbcPassword = null;
        EventManager eventMgr = mock(EventManager.class);
        IServer parentServer1 = mock(IServer.class);
        when(parentServer1.getId()).thenReturn("server1");
        ISecureStorageProvider secureStorageProvider = new DefaultStorageProvider();

        // construct a server just to get its URL
        ITeiidAdminInfo adminInfo = new TeiidAdminInfo(adminPort, adminUser, secureStorageProvider, adminPassword, adminSecure);
        ITeiidJdbcInfo jdbcInfo = new TeiidJdbcInfo(jdbcPort, jdbcUser, secureStorageProvider, jdbcPassword, jdbcSecure);
        TeiidServer testServer = new TeiidServer(serverVersion, adminInfo, jdbcInfo, eventMgr, parentServer1);
        adminInfo.setHostProvider(testServer);
        jdbcInfo.setHostProvider(testServer);

        ITeiidServer teiidServer = this.mgr.getServer(testServer.getId());
        assertThat(teiidServer, notNullValue());
        assertThat(teiidServer, is(this.mgr.getDefaultServer()));
        assertThat(teiidServer.getCustomLabel(), is(customLabel));
        assertThat(teiidServer.getHost(), is(HostProvider.DEFAULT_HOST));
        assertThat(teiidServer.getTeiidAdminInfo().getPort(), is(adminPort));
        assertThat(teiidServer.getTeiidAdminInfo().getUsername(), is(adminUser));
        assertThat(teiidServer.getTeiidAdminInfo().getPassword(), is(adminPassword));
        assertThat(teiidServer.getTeiidAdminInfo().isSecure(), is(adminSecure));
        assertThat(teiidServer.getTeiidJdbcInfo().getPort(), is(jdbcPort));
        assertThat(teiidServer.getTeiidJdbcInfo().getUsername(), is(jdbcUser));
        assertThat(teiidServer.getTeiidJdbcInfo().getPassword(), is(jdbcPassword));
        assertThat(teiidServer.getTeiidJdbcInfo().isSecure(), is(jdbcSecure));

        serverVersion = new TeiidServerVersion("8.2.1");
        IServer parentServer2 = mock(IServer.class);
        when(parentServer2.getId()).thenReturn("server2");
        when(parentServer2.getHost()).thenReturn("myserver.com");
        customLabel = "";
        adminPort = "31444";
        adminSecure = false;
        adminUser = "admin2";
        adminPassword = null;
        jdbcPort = "31001";
        jdbcSecure = true;
        jdbcUser = "teiid2";
        jdbcPassword = "teiid";

        // construct a server just to get its URL
        adminInfo = new TeiidAdminInfo(adminPort, adminUser, secureStorageProvider, adminPassword, adminSecure);
        jdbcInfo = new TeiidJdbcInfo(jdbcPort, jdbcUser, secureStorageProvider, jdbcPassword, jdbcSecure);
        testServer = new TeiidServer(serverVersion, adminInfo, jdbcInfo, eventMgr, parentServer2);
        adminInfo.setHostProvider(testServer);
        jdbcInfo.setHostProvider(testServer);

        teiidServer = this.mgr.getServer(testServer.getId());
        assertThat(teiidServer, notNullValue());
        assertThat(teiidServer, is(not(this.mgr.getDefaultServer())));
        assertThat(teiidServer.getCustomLabel(), nullValue()); // customLabel is empty string but gets set as a null
        assertThat(teiidServer.getHost(), is(parentServer2.getHost()));
        assertThat(teiidServer.getTeiidAdminInfo().getPort(), is(adminPort));
        assertThat(teiidServer.getTeiidAdminInfo().getUsername(), is(adminUser));
        assertThat(teiidServer.getTeiidAdminInfo().getPassword(), is(adminPassword));
        assertThat(teiidServer.getTeiidAdminInfo().isSecure(), is(adminSecure));
        assertThat(teiidServer.getTeiidJdbcInfo().getPort(), is(jdbcPort));
        assertThat(teiidServer.getTeiidJdbcInfo().getUsername(), is(jdbcUser));
        assertThat(teiidServer.getTeiidJdbcInfo().getPassword(), is(jdbcPassword));
        assertThat(teiidServer.getTeiidJdbcInfo().isSecure(), is(jdbcSecure));
    }

}
