/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.connection;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;


/**
 * 
 */
public class SourceBindingTest {

    private static final Set<ITeiidTranslator> NULL_CONNECTORS = null;
    private static final ITeiidTranslator NULL_CONNECTOR = null;

    @Mock
    private ITeiidServer teiidServer;

    private ITeiidTranslator commonConnector;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);

        commonConnector = mock(ITeiidTranslator.class);
        when(commonConnector.getType()).thenReturn("theType");
        when(commonConnector.getTeiidServer()).thenReturn(teiidServer);
    }

    private ITeiidTranslator getMockConnector() {
        ITeiidTranslator conn = mock(ITeiidTranslator.class);
        ITeiidServer newTeiidServer = mock(ITeiidServer.class);
        when(conn.getType()).thenReturn("theType");
        when(conn.getTeiidServer()).thenReturn(newTeiidServer);

        return conn;
    }

    private ITeiidTranslator getMockConnectorWithCommonAdmin() {
        ITeiidTranslator conn = mock(ITeiidTranslator.class);
        when(conn.getType()).thenReturn("theType");
        when(conn.getTeiidServer()).thenReturn(teiidServer);

        return conn;
    }

    private SourceConnectionBinding getNewSourceBinding() {
        Set<ITeiidTranslator> connectors = new HashSet<ITeiidTranslator>();
        connectors.add(getMockConnector());
        return new SourceConnectionBinding("name", "path", connectors);
    }

    private SourceConnectionBinding getNewSourceBindingWithCommonAdmin() {
        Set<ITeiidTranslator> connectors = new HashSet<ITeiidTranslator>();
        connectors.add(getMockConnectorWithCommonAdmin());
        return new SourceConnectionBinding("name", "path", connectors);
    }

    private SourceConnectionBinding getNewSourceBindingWithMultipleConnectors() {
        Set<ITeiidTranslator> connectors = new HashSet<ITeiidTranslator>();
        connectors.add(getMockConnectorWithCommonAdmin());
        connectors.add(commonConnector);
        return new SourceConnectionBinding("name", "path", connectors);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullName() {
        new SourceConnectionBinding(null, null, NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingEmptyName() {
        new SourceConnectionBinding("", null, NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullPath() {
        new SourceConnectionBinding("name", null, NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingEmptyPath() {
        new SourceConnectionBinding("name", "", NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullConnector() {
        new SourceConnectionBinding("name", "path", NULL_CONNECTOR);
    }

    @Test
    public void shouldCreateSourceBindingWithConnector() {
        new SourceConnectionBinding("name", "path", getMockConnector());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullName_2() {
        new SourceConnectionBinding(null, null, NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingEmptyName_2() {
        new SourceConnectionBinding("", null, NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullPath_2() {
        new SourceConnectionBinding("name", null, NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingEmptyPath_2() {
        new SourceConnectionBinding("name", "", NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullConnectors() {
        new SourceConnectionBinding("name", "", NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingWithEmptyConnectors() {
        new SourceConnectionBinding("name", "path", new HashSet<ITeiidTranslator>());
    }

    @Test
    public void shouldCreateSourceBindingWithConnectors() {
        Set<ITeiidTranslator> connectors = new HashSet<ITeiidTranslator>();
        connectors.add(getMockConnector());
        new SourceConnectionBinding("name", "path", connectors);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowAddConnectorWithNullConnector() {
        getNewSourceBinding().addTranslator(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowAddConnectorWithDifferentAdminConnector() {
        getNewSourceBinding().addTranslator(getMockConnector());
    }

    @Test
    public void shouldAllowAddConnectorWithCommonAdminConnector() {
        getNewSourceBindingWithCommonAdmin().addTranslator(getMockConnectorWithCommonAdmin());
    }

    @Test
    public void shouldAllowGetConnectors() {
        assertThat(getNewSourceBinding().getTranslators(), notNullValue());
    }

    @Test
    public void shouldAllowGetName() {
        assertThat(getNewSourceBinding().getModelName(), notNullValue());
    }

    @Test
    public void shouldAllowGetContainerPath() {
        assertThat(getNewSourceBinding().getModelLocation(), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveConnectorWithNullConnector() {
        getNewSourceBinding().removeTranslator(NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveConnectorWithOneConnector() {
        getNewSourceBinding().removeTranslator(getMockConnector());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveConnectorWithNonBoundConnector() {
        getNewSourceBindingWithMultipleConnectors().removeTranslator(getMockConnector());
    }

    @Test
    public void shouldAllowRemoveConnector() {
        getNewSourceBindingWithMultipleConnectors().removeTranslator(commonConnector);
    }
}
