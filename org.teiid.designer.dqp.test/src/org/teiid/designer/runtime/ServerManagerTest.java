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
import static org.mockito.Mockito.verify;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * 
 */
public class ServerManagerTest {
    private static final String MODEL1 = "Model1";
    private static final String MODEL2 = "Model2";
    private static final String MODEL3 = "Model3";
    private static final String MODEL4 = "Model4";
    private static final String MODEL5 = "Model5";

    private static final String CONNECTOR_TYPE1 = "ConnectorType1";
    private static final String CONNECTOR_TYPE2 = "ConnectorType2";
    private static final String CONNECTOR_TYPE3 = "ConnectorType3";
    private static final String CONNECTOR_TYPE4 = "ConnectorType4";
    private static final String CONNECTOR_TYPE5 = "ConnectorType5";

    private static final String CONNECTOR1 = "Connector1";
    private static final String CONNECTOR2 = "Connector2";
    private static final String CONNECTOR3 = "Connector3";
    private static final String CONNECTOR4 = "Connector4";
    private static final String CONNECTOR5 = "Connector5";
    private static final String CONNECTOR6 = "Connector6";
    private static final String CONNECTOR7 = "Connector7";
    private static final String CONNECTOR8 = "Connector8";
    private static final String CONNECTOR9 = "Connector9";

    private static final String RESTORED_SERVER1_URL = "mm://localhost:8080";
    private static final String RESTORED_SERVER1_USER = "user8080";

    private static final String RESTORED_SERVER2_URL = "mm://localhost:8180";
    private static final String RESTORED_SERVER2_USER = "user8180";

    private static final String RESTORED_SERVER3_URL = "mm://localhost:8280";
    private static final String RESTORED_SERVER3_USER = "user8280";

    private static final String SERVER1_URL = "mm://server:4321";
    private static final Server SERVER1;

    private ServerManager mgr;

    static {
        Map<String, String> connectorNameConnectorTypeNameMap = new HashMap<String, String>();
        connectorNameConnectorTypeNameMap.put(CONNECTOR1, CONNECTOR_TYPE1);
        connectorNameConnectorTypeNameMap.put(CONNECTOR2, CONNECTOR_TYPE2);
        connectorNameConnectorTypeNameMap.put(CONNECTOR3, CONNECTOR_TYPE3);
        connectorNameConnectorTypeNameMap.put(CONNECTOR4, CONNECTOR_TYPE4);
        connectorNameConnectorTypeNameMap.put(CONNECTOR5, CONNECTOR_TYPE5);
        connectorNameConnectorTypeNameMap.put(CONNECTOR6, CONNECTOR_TYPE1);
        connectorNameConnectorTypeNameMap.put(CONNECTOR7, CONNECTOR_TYPE1);
        connectorNameConnectorTypeNameMap.put(CONNECTOR8, CONNECTOR_TYPE1);
        connectorNameConnectorTypeNameMap.put(CONNECTOR9, CONNECTOR_TYPE1);

        Map<String, String> modelNameConectorNameMap = new HashMap<String, String>();
        modelNameConectorNameMap.put(MODEL1, CONNECTOR1);
        modelNameConectorNameMap.put(MODEL2, CONNECTOR1);
        modelNameConectorNameMap.put(MODEL3, CONNECTOR1);
        modelNameConectorNameMap.put(MODEL4, CONNECTOR1);
        modelNameConectorNameMap.put(MODEL5, CONNECTOR1);

        SERVER1 = new TestServer(SERVER1_URL, "userA", "pwdA", false, mock(EventManager.class),
                                 connectorNameConnectorTypeNameMap, modelNameConectorNameMap);
    }

    @Before
    public void beforeEach() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mgr = new ServerManager(null);
        SERVER1.getAdmin().refresh();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullListenerForAddListener() {
        this.mgr.addListener(null);
    }

    @Test
    public void shouldConfirmRegisteredListenerIsNotified() {
        // create and register listener
        IExecutionConfigurationListener listener = mock(IExecutionConfigurationListener.class);
        this.mgr.addListener(listener);

        // generate event
        this.mgr.addServer(SERVER1);

        // test to make sure listener was called once
        verify(listener).configurationChanged((ExecutionConfigurationEvent)anyObject());
    }

    @Test
    public void shouldConfirmUnregisteredListenerIsNotNotified() {
        // create and register listener
        IExecutionConfigurationListener listener = mock(IExecutionConfigurationListener.class);
        this.mgr.addListener(listener);
        this.mgr.removeListener(listener);

        // generate event
        this.mgr.addServer(SERVER1);

        // test to make sure listener was called once
        verify(listener, never()).configurationChanged((ExecutionConfigurationEvent)anyObject());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForAddServer() {
        this.mgr.addServer(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullUrlForGetServer() {
        this.mgr.getServer(null);
    }

    @Test
    public void shouldAllowGetServers() {
        this.mgr.getServers();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForIsRegistered() {
        this.mgr.isRegistered(null);
    }

    @Test
    public void shouldConfirmServerIsRegistered() {
        assertThat(this.mgr.addServer(SERVER1).isOK(), is(true));
        assertThat(this.mgr.isRegistered(SERVER1), is(true));
    }

    @Test
    public void shouldConfirmServerIsRemoved() {
        // first add
        this.mgr.addServer(SERVER1);
        assertThat(this.mgr.isRegistered(SERVER1), is(true));

        // now remove
        assertThat(this.mgr.removeServer(SERVER1).isOK(), is(true));
        assertThat(this.mgr.isRegistered(SERVER1), is(false));
    }

    @Test
    public void shouldConfirmServerIsNotAddedMultipleTimes() {
        // add
        this.mgr.addServer(SERVER1);
        assertThat(this.mgr.getServers().size(), is(1));

        // add again
        this.mgr.addServer(SERVER1);
        assertThat(this.mgr.getServers().size(), is(1));
    }

    @Test
    public void shouldConfirmServerIsNotRegistered() {
        assertThat(this.mgr.isRegistered(SERVER1), is(false));
    }

    @Test
    public void shouldGetServerByUrl() {
        this.mgr.addServer(SERVER1);
        assertThat(this.mgr.getServer(SERVER1_URL), is(SERVER1));
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

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServerForRemoveServer() {
        this.mgr.removeServer(null);
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
    public void shouldNotAllowNullUpdatedServerForUpdateServer() {
        this.mgr.updateServer(mock(Server.class), null);
    }

    @Test
    public void shouldAllowSaveState() {
        this.mgr.saveState();
    }

    @Test
    public void shouldFindConnectorsForModel() throws Exception {
        // register the server
        this.mgr.addServer(SERVER1);

        // test
        Collection<Connector> connectors = this.mgr.getConnectorsForModel(MODEL1);
        assertThat(connectors.size(), is(1));
        assertThat(connectors.iterator().next().getName(), is(CONNECTOR1));
    }

}
