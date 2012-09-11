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
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.teiid.core.util.SmartTestDesignerSuite;

/**
 * 
 */
public class ServerManagerTest {
    
    private class TestServersProvider implements IServersProvider {
        
        private final String[] HOSTS = new String[] { "localhost", "myserver.com" };
        
        private final int[] PORTS = new int[] { 8080, 8180, 8280, 31443, 31444 };
        
        private List<IServer> servers = new ArrayList<IServer>();
        
        /**
         * Create new instance
         */
        public TestServersProvider() {
            
            for (int port : PORTS) {
                JBoss7Server mockJBossServer = mock(JBoss7Server.class);
                when(mockJBossServer.getManagementPort()).thenReturn(port);
                
                for (String host : HOSTS) {
                    IServer mockServer = mock(IServer.class);
                    when(mockServer.getHost()).thenReturn(host);
                    when(mockServer.loadAdapter(JBoss7Server.class, null)).thenReturn(mockJBossServer);
                
                    servers.add(mockServer);
                }
            }
        }
        
        @Override
        public void removeServerLifecycleListener(IServerLifecycleListener serversListener) {
            // do nothing
        }
        
        @Override
        public IServer[] getServers() {
            return servers.toArray(new IServer[0]);
        }
        
        @Override
        public void addServerLifecycleListener(IServerLifecycleListener serversListener) {
            // do nothing
        }
    }

    private static final String RESTORED_SERVER1_URL = "mm://localhost:8080";
    private static final String RESTORED_SERVER1_USER = "user8080";

    private static final String RESTORED_SERVER2_URL = "mm://localhost:8180";
    private static final String RESTORED_SERVER2_USER = "user8180";

    private static final String RESTORED_SERVER3_URL = "mm://localhost:8280";
    private static final String RESTORED_SERVER3_USER = "user8280";

    private static final String SERVER1_URL = "mm://server:4321";

    private TeiidServerManager mgr;
    
    private IServersProvider serversProvider = new TestServersProvider();

    @Mock
    private TeiidServer server1;

    @Before
    public void beforeEach() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mgr = new TeiidServerManager(null, null, serversProvider);
        
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

        // test to make sure listener was called twice (once for adding server, once for setting default server)
        verify(listener, times(2)).configurationChanged((ExecutionConfigurationEvent) anyObject());
    }

    @Test
    public void shouldConfirmServerIsNotAddedMultipleTimes() {
        // setup
        when(this.server1.hasSameKey(this.server1)).thenReturn(true);

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
        when(this.server1.hasSameKey(server1)).thenReturn(true);
        assertThat(this.mgr.addServer(this.server1).isOK(), is(true));
        assertThat(this.mgr.isRegistered(this.server1), is(true));
    }

    @Test
    public void shouldConfirmServerIsRemoved() {
        // first add
        when(server1.hasSameKey(server1)).thenReturn(true);
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
    public void shouldGetServerByUrl() {
        this.mgr.addServer(server1);
        when(server1.getUrl()).thenReturn(SERVER1_URL);
        assertThat(this.mgr.getServer(SERVER1_URL), is(server1));
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
        this.mgr.updateServer(mock(TeiidServer.class), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullUrlForGetServer() {
        this.mgr.getServer(null);
    }

    @Test
    public void shouldOldRestoreServerRegistry() throws Exception {
        // setup
        MockObjectFactory.createModelContainer();

        this.mgr = new TeiidServerManager(SmartTestDesignerSuite.getTestDataPath(getClass()) + File.separator + "oldregistrydata", null, serversProvider);
        this.mgr.restoreState();
        assertThat(this.mgr.getServers().size(), is(3));

        TeiidServer teiidServer = this.mgr.getServer(RESTORED_SERVER1_URL);
        assertThat(teiidServer, notNullValue());
        assertThat(teiidServer.getTeiidAdminInfo().getUsername(), is(RESTORED_SERVER1_USER));
        assertThat(teiidServer, is(not(this.mgr.getDefaultServer())));

        teiidServer = this.mgr.getServer(RESTORED_SERVER2_URL);
        assertThat(teiidServer, notNullValue());
        assertThat(teiidServer.getTeiidAdminInfo().getUsername(), is(RESTORED_SERVER2_USER));
        assertThat(teiidServer, is(sameInstance(this.mgr.getDefaultServer()))); // server was persisted as the default preview server

        teiidServer = this.mgr.getServer(RESTORED_SERVER3_URL);
        assertThat(teiidServer, notNullValue());
        assertThat(teiidServer.getTeiidAdminInfo().getUsername(), is(RESTORED_SERVER3_USER));
        assertThat(teiidServer, is(not(this.mgr.getDefaultServer())));
    }

    @Test
    public void shouldRestoreServerRegistry() throws Exception {
        // setup
        MockObjectFactory.createModelContainer();

        this.mgr = new TeiidServerManager(SmartTestDesignerSuite.getTestDataPath(getClass()), null, serversProvider);
        this.mgr.restoreState();
        assertThat(this.mgr.getServers().size(), is(2));

        String customLabel = "My Custom Label";
        String adminPort = "31443";
        boolean adminSecure = true;
        String adminUser = "admin";
        String adminPassword = "admin";
        boolean adminPersistPassword = true;
        String jdbcPort = "31000";
        boolean jdbcSecure = false;
        String jdbcUser = "teiid";
        String jdbcPassword = null;
        boolean jdbcPersistPassword = false;
        EventManager eventMgr = mock(EventManager.class);
        IServer parentServer = mock(IServer.class);

        // construct a server just to get its URL
        TeiidAdminInfo adminInfo = new TeiidAdminInfo(adminPort, adminUser, adminPassword, adminPersistPassword, adminSecure);
        TeiidJdbcInfo jdbcInfo = new TeiidJdbcInfo(jdbcPort, jdbcUser, jdbcPassword, jdbcPersistPassword, jdbcSecure);
        TeiidServer testServer = new TeiidServer(null, adminInfo, jdbcInfo, eventMgr, parentServer);
        adminInfo.setHostProvider(testServer);
        jdbcInfo.setHostProvider(testServer);

        TeiidServer teiidServer = this.mgr.getServer(testServer.getUrl());
        assertThat(teiidServer, notNullValue());
        assertThat(teiidServer, is(this.mgr.getDefaultServer()));
        assertThat(teiidServer.getCustomLabel(), is(customLabel));
        assertThat(teiidServer.getHost(), is(HostProvider.DEFAULT_HOST));
        assertThat(teiidServer.getTeiidAdminInfo().getPort(), is(adminPort));
        assertThat(teiidServer.getTeiidAdminInfo().getUsername(), is(adminUser));
        assertThat(teiidServer.getTeiidAdminInfo().getPassword(), is(adminPassword));
        assertThat(teiidServer.getTeiidAdminInfo().isPasswordBeingPersisted(), is(adminPersistPassword));
        assertThat(teiidServer.getTeiidAdminInfo().isSecure(), is(adminSecure));
        assertThat(teiidServer.getTeiidJdbcInfo().getPort(), is(jdbcPort));
        assertThat(teiidServer.getTeiidJdbcInfo().getUsername(), is(jdbcUser));
        assertThat(teiidServer.getTeiidJdbcInfo().getPassword(), is(jdbcPassword));
        assertThat(teiidServer.getTeiidJdbcInfo().isPasswordBeingPersisted(), is(jdbcPersistPassword));
        assertThat(teiidServer.getTeiidJdbcInfo().isSecure(), is(jdbcSecure));

        String host = "myserver.com";
        customLabel = "";
        adminPort = "31444";
        adminSecure = false;
        adminUser = "admin2";
        adminPassword = null;
        adminPersistPassword = false;
        jdbcPort = "31001";
        jdbcSecure = true;
        jdbcUser = "teiid2";
        jdbcPassword = "teiid";
        jdbcPersistPassword = true;

        // construct a server just to get its URL
        adminInfo = new TeiidAdminInfo(adminPort, adminUser, adminPassword, adminPersistPassword, adminSecure);
        jdbcInfo = new TeiidJdbcInfo(jdbcPort, jdbcUser, jdbcPassword, jdbcPersistPassword, jdbcSecure);
        testServer = new TeiidServer(host, adminInfo, jdbcInfo, eventMgr, parentServer);
        adminInfo.setHostProvider(testServer);
        jdbcInfo.setHostProvider(testServer);

        teiidServer = this.mgr.getServer(testServer.getUrl());
        assertThat(teiidServer, notNullValue());
        assertThat(teiidServer, is(not(this.mgr.getDefaultServer())));
        assertThat(teiidServer.getCustomLabel(), nullValue()); // customLabel is empty string but gets set as a null
        assertThat(teiidServer.getHost(), is(host));
        assertThat(teiidServer.getTeiidAdminInfo().getPort(), is(adminPort));
        assertThat(teiidServer.getTeiidAdminInfo().getUsername(), is(adminUser));
        assertThat(teiidServer.getTeiidAdminInfo().getPassword(), is(adminPassword));
        assertThat(teiidServer.getTeiidAdminInfo().isPasswordBeingPersisted(), is(adminPersistPassword));
        assertThat(teiidServer.getTeiidAdminInfo().isSecure(), is(adminSecure));
        assertThat(teiidServer.getTeiidJdbcInfo().getPort(), is(jdbcPort));
        assertThat(teiidServer.getTeiidJdbcInfo().getUsername(), is(jdbcUser));
        assertThat(teiidServer.getTeiidJdbcInfo().getPassword(), is(jdbcPassword));
        assertThat(teiidServer.getTeiidJdbcInfo().isPasswordBeingPersisted(), is(jdbcPersistPassword));
        assertThat(teiidServer.getTeiidJdbcInfo().isSecure(), is(jdbcSecure));
    }

}
