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
        assertThat(ExecutionConfigurationEvent.createAddServerEvent(mock(TeiidServer.class)), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateRemoveServerEventWithNullServer() {
        ExecutionConfigurationEvent.createRemoveServerEvent(null);
    }

    @Test
    public void shouldCreateRemoveServerEventWithServer() {
        assertThat(ExecutionConfigurationEvent.createRemoveServerEvent(mock(TeiidServer.class)), notNullValue());
    }

    @Test
    public void shouldCreateUpdateServerEventWithNullServer() {
        ExecutionConfigurationEvent.createUpdateServerEvent(null, null);
    }

    @Test
    public void shouldCreateUpdateServerEventWithNullUpdatedServer() {
        ExecutionConfigurationEvent.createUpdateServerEvent(mock(TeiidServer.class), null);
    }

    @Test
    public void shouldCreateUpdateServerEventWithServers() {
        ExecutionConfigurationEvent.createUpdateServerEvent(mock(TeiidServer.class), mock(TeiidServer.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateAddDataSourceEventWithNullDataSource() {
        ExecutionConfigurationEvent.createAddDataSourceEvent(null);
    }

    @Test
    public void shouldCreateAddDataSourceEventWithDataSource() {
        ExecutionConfigurationEvent.createAddDataSourceEvent(mock(ITeiidDataSource.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateRemoveDataSourceEventWithNullDataSource() {
        ExecutionConfigurationEvent.createRemoveDataSourceEvent(null);
    }

    @Test
    public void shouldCreateRemoveDataSourceEventWithDataSource() {
        ExecutionConfigurationEvent.createRemoveDataSourceEvent(mock(ITeiidDataSource.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotCreateUpdateDataSourceEventWithNullDataSource() {
        ExecutionConfigurationEvent.createUpdateDataSourceEvent(null);
    }

    @Test
    public void shouldCreateUpdateDataSourceEventWithDataSource() {
        ExecutionConfigurationEvent.createUpdateDataSourceEvent(mock(ITeiidDataSource.class));
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetTranslatorWithAddServerEvent() {
        ExecutionConfigurationEvent.createAddServerEvent(mock(TeiidServer.class)).getTranslator();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetTranslatorWithRemoveServerEvent() {
        ExecutionConfigurationEvent.createRemoveServerEvent(mock(TeiidServer.class)).getTranslator();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetTranslatorWithUpdateServerEvent() {
        ExecutionConfigurationEvent.createUpdateServerEvent(mock(TeiidServer.class), null).getTranslator();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetServerWithAddDataSourceEvent() {
        ExecutionConfigurationEvent.createAddDataSourceEvent(mock(ITeiidDataSource.class)).getServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetServerWithRemoveDataSourceEvent() {
        ExecutionConfigurationEvent.createRemoveDataSourceEvent(mock(ITeiidDataSource.class)).getServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetServerWithUpdateDataSourceEvent() {
        ExecutionConfigurationEvent.createUpdateDataSourceEvent(mock(ITeiidDataSource.class)).getServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetUpdatedServerWithAddDataSourceEvent() {
        ExecutionConfigurationEvent.createAddDataSourceEvent(mock(ITeiidDataSource.class)).getUpdatedServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetUpdatedServerWithRemoveDataSourceEvent() {
        ExecutionConfigurationEvent.createRemoveDataSourceEvent(mock(ITeiidDataSource.class)).getUpdatedServer();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotAllowGetUpdatedServerWithUpdateDataSourceEvent() {
        ExecutionConfigurationEvent.createUpdateDataSourceEvent(mock(ITeiidDataSource.class)).getUpdatedServer();
    }

    @Test
    public void shouldAllowGetTargetTypeWithAddServerEvent() {
        assertThat(ExecutionConfigurationEvent.createAddServerEvent(mock(TeiidServer.class)).getTargetType(), notNullValue());
    }

    @Test
    public void shouldAllowGetEventTypeWithAddServerEvent() {
        assertThat(ExecutionConfigurationEvent.createAddServerEvent(mock(TeiidServer.class)).getEventType(), notNullValue());
    }
}
