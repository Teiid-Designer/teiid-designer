/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * 
 */
public class ExecutionConfigurationEventTest {

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateAddServerEventWithNullServer() {
        ExecutionConfigurationEvent.createAddServerEvent(null);
    }

    @Test
    public void shouldCreateAddServerEventWithServer() {
        assertThat(ExecutionConfigurationEvent.createAddServerEvent(mock(Server.class)), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateRemoveServerEventWithNullServer() {
        ExecutionConfigurationEvent.createRemoveServerEvent(null);
    }

    @Test
    public void shouldCreateRemoveServerEventWithServer() {
        assertThat(ExecutionConfigurationEvent.createRemoveServerEvent(mock(Server.class)), notNullValue());
    }

    @Test
    public void shouldCreateUpdateServerEventWithNullServer() {
        ExecutionConfigurationEvent.createUpdateServerEvent(null, null);
    }

    @Test
    public void shouldCreateUpdateServerEventWithNullUpdatedServer() {
        ExecutionConfigurationEvent.createUpdateServerEvent(mock(Server.class), null);
    }

    @Test
    public void shouldCreateUpdateServerEventWithServers() {
        ExecutionConfigurationEvent.createUpdateServerEvent(mock(Server.class), mock(Server.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateAddConnectorEventWithNullConnector() {
        ExecutionConfigurationEvent.createAddConnectorEvent(null);
    }

    @Test
    public void shouldCreateAddConnectorEventWithConnector() {
        ExecutionConfigurationEvent.createAddConnectorEvent(mock(Connector.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateRemoveConnectorEventWithNullConnector() {
        ExecutionConfigurationEvent.createRemoveConnectorEvent(null);
    }

    @Test
    public void shouldCreateRemoveConnectorEventWithConnector() {
        ExecutionConfigurationEvent.createRemoveConnectorEvent(mock(Connector.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateUpdateConnectorEventWithNullConnector() {
        ExecutionConfigurationEvent.createUpdateConnectorEvent(null);
    }

    @Test
    public void shouldCreateUpdateConnectorEventWithConnector() {
        ExecutionConfigurationEvent.createUpdateConnectorEvent(mock(Connector.class));
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetConnectorWithAddServerEvent() {
        ExecutionConfigurationEvent.createAddServerEvent(mock(Server.class)).getConnector();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetConnectorWithRemoveServerEvent() {
        ExecutionConfigurationEvent.createRemoveServerEvent(mock(Server.class)).getConnector();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetConnectorWithUpdateServerEvent() {
        ExecutionConfigurationEvent.createUpdateServerEvent(mock(Server.class), null).getConnector();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetServerWithAddConnectorEvent() {
        ExecutionConfigurationEvent.createAddConnectorEvent(mock(Connector.class)).getServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetServerWithRemoveConnectorEvent() {
        ExecutionConfigurationEvent.createRemoveConnectorEvent(mock(Connector.class)).getServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetServerWithUpdateConnectorEvent() {
        ExecutionConfigurationEvent.createUpdateConnectorEvent(mock(Connector.class)).getServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetUpdatedServerWithAddConnectorEvent() {
        ExecutionConfigurationEvent.createAddConnectorEvent(mock(Connector.class)).getUpdatedServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetUpdatedServerWithRemoveConnectorEvent() {
        ExecutionConfigurationEvent.createRemoveConnectorEvent(mock(Connector.class)).getUpdatedServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetUpdatedServerWithUpdateConnectorEvent() {
        ExecutionConfigurationEvent.createUpdateConnectorEvent(mock(Connector.class)).getUpdatedServer();
    }

    @Test
    public void shouldAllowGetTargetTypeWithAddServerEvent() {
        assertThat(ExecutionConfigurationEvent.createAddServerEvent(mock(Server.class)).getTargetType(), notNullValue());
    }

    @Test
    public void shouldAllowGetEventTypeWithAddServerEvent() {
        assertThat(ExecutionConfigurationEvent.createAddServerEvent(mock(Server.class)).getEventType(), notNullValue());
    }
}
