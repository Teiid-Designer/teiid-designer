/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collection;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 
 */
public class ServerManagerTest {
    private static final String MODEL1 = "Model1";

    private static final String CONNECTOR1 = "Connector1";

    private static final String RESTORED_SERVER1_URL = "mm://localhost:8080";
    private static final String RESTORED_SERVER1_USER = "user8080";

    private static final String RESTORED_SERVER2_URL = "mm://localhost:8180";
    private static final String RESTORED_SERVER2_USER = "user8180";

    private static final String RESTORED_SERVER3_URL = "mm://localhost:8280";
    private static final String RESTORED_SERVER3_USER = "user8280";

    private static final String SERVER1_URL = "mm://server:4321";

    private ServerManager mgr;

    @Mock
    private Server server1;

    @Before
    public void beforeEach() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mgr = new ServerManager(null);
    }

    @Test
    public void shouldAllowGetServers() {
        this.mgr.getServers();
    }

    @Test
    public void shouldAllowSaveState() {
        this.mgr.saveState();
    }

    @Test
    public void shouldConfirmRegisteredListenerIsNotified() {
        // create and register listener
        final IExecutionConfigurationListener listener = mock(IExecutionConfigurationListener.class);
        this.mgr.addListener(listener);

        // generate event
        this.mgr.addServer(this.server1);

        // test to make sure listener was called twice (once for adding server, once for setting default server)
        verify(listener, times(2)).configurationChanged((ExecutionConfigurationEvent)anyObject());
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
        verify(listener, never()).configurationChanged((ExecutionConfigurationEvent)anyObject());
    }

    @Test
    public void shouldFindConnectorsForModel() throws Exception {
        // register the server
        final ExecutionAdmin admin = mock(ExecutionAdmin.class);
        when(server1.getAdmin()).thenReturn(admin);
        final SourceBindingsManager srcBindingsMgr = mock(SourceBindingsManager.class);
        when(admin.getSourceBindingsManager()).thenReturn(srcBindingsMgr);
        final Connector connector = mock(Connector.class);
        when(srcBindingsMgr.getConnectorsForModel(MODEL1)).thenReturn(Collections.singleton(connector));
        when(connector.getName()).thenReturn(CONNECTOR1);
        this.mgr.addServer(server1);

        // test
        final Collection<Connector> connectors = this.mgr.getConnectorsForModel(MODEL1);
        assertThat(connectors.size(), is(1));
        assertThat(connectors.iterator().next().getName(), is(CONNECTOR1));
    }

    @Test
    public void shouldGetServerByUrl() {
        this.mgr.addServer(server1);
        when(server1.getUrl()).thenReturn(SERVER1_URL);
        assertThat(this.mgr.getServer(SERVER1_URL), is(server1));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullListenerForAddListener() {
        this.mgr.addListener(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullListenerForRemoveListener() {
        this.mgr.removeListener(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullReplacedServerForUpdateServer() {
        this.mgr.updateServer(null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForAddServer() {
        this.mgr.addServer(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForIsRegistered() {
        this.mgr.isRegistered(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForRemoveServer() {
        this.mgr.removeServer(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullUpdatedServerForUpdateServer() {
        this.mgr.updateServer(mock(Server.class), null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullUrlForGetServer() {
        this.mgr.getServer(null);
    }

    @Test
    public void shouldRestoreServerRegistry() {
        this.mgr = new ServerManager("testdata");
        this.mgr.restoreState();
        assertThat(this.mgr.getServers().size(), is(3));

        Server server = this.mgr.getServer(RESTORED_SERVER1_URL);
        assertThat(server, notNullValue());
        assertThat(server.getUser(), is(RESTORED_SERVER1_USER));

        server = this.mgr.getServer(RESTORED_SERVER2_URL);
        assertThat(server, notNullValue());
        assertThat(server.getUser(), is(RESTORED_SERVER2_USER));

        server = this.mgr.getServer(RESTORED_SERVER3_URL);
        assertThat(server, notNullValue());
        assertThat(server.getUser(), is(RESTORED_SERVER3_USER));
    }

}
