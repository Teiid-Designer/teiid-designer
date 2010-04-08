/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.workspace;

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
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.modeler.dqp.internal.workspace.SourceBinding;

/**
 * 
 */
public class SourceBindingTest {

    private static final Set<Connector> NULL_CONNECTORS = null;
    private static final Connector NULL_CONNECTOR = null;

    @Mock
    private ExecutionAdmin commonExecutionAdmin;

    private Connector commonConnector;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);

        commonConnector = mock(Connector.class);
        ConnectorType type = mock(ConnectorType.class);
        when(commonConnector.getType()).thenReturn(type);
        when(type.getAdmin()).thenReturn(commonExecutionAdmin);
    }

    private Connector getMockConnector() {
        Connector conn = mock(Connector.class);
        ConnectorType type = mock(ConnectorType.class);
        ExecutionAdmin admin = mock(ExecutionAdmin.class);
        when(conn.getType()).thenReturn(type);
        when(type.getAdmin()).thenReturn(admin);

        return conn;
    }

    private Connector getMockConnectorWithCommonAdmin() {
        Connector conn = mock(Connector.class);
        ConnectorType type = mock(ConnectorType.class);
        when(conn.getType()).thenReturn(type);
        when(type.getAdmin()).thenReturn(commonExecutionAdmin);

        return conn;
    }

    private SourceBinding getNewSourceBinding() {
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(getMockConnector());
        return new SourceBinding("name", "path", connectors);
    }

    private SourceBinding getNewSourceBindingWithCommonAdmin() {
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(getMockConnectorWithCommonAdmin());
        return new SourceBinding("name", "path", connectors);
    }

    private SourceBinding getNewSourceBindingWithMultipleConnectors() {
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(getMockConnectorWithCommonAdmin());
        connectors.add(commonConnector);
        return new SourceBinding("name", "path", connectors);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullName() {
        new SourceBinding(null, null, NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingEmptyName() {
        new SourceBinding("", null, NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullPath() {
        new SourceBinding("name", null, NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingEmptyPath() {
        new SourceBinding("name", "", NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullConnector() {
        new SourceBinding("name", "path", NULL_CONNECTOR);
    }

    @Test
    public void shouldCreateSourceBindingWithConnector() {
        new SourceBinding("name", "path", getMockConnector());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullName_2() {
        new SourceBinding(null, null, NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingEmptyName_2() {
        new SourceBinding("", null, NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullPath_2() {
        new SourceBinding("name", null, NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingEmptyPath_2() {
        new SourceBinding("name", "", NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingNullConnectors() {
        new SourceBinding("name", "", NULL_CONNECTORS);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCreateSourceBindingWithEmptyConnectors() {
        new SourceBinding("name", "path", new HashSet<Connector>());
    }

    @Test
    public void shouldCreateSourceBindingWithConnectors() {
        Set<Connector> connectors = new HashSet<Connector>();
        connectors.add(getMockConnector());
        new SourceBinding("name", "path", connectors);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowAddConnectorWithNullConnector() {
        getNewSourceBinding().addConnector(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowAddConnectorWithDifferentAdminConnector() {
        getNewSourceBinding().addConnector(getMockConnector());
    }

    @Test
    public void shouldAllowAddConnectorWithCommonAdminConnector() {
        getNewSourceBindingWithCommonAdmin().addConnector(getMockConnectorWithCommonAdmin());
    }

    @Test
    public void shouldAllowGetConnectors() {
        assertThat(getNewSourceBinding().getConnectors(), notNullValue());
    }

    @Test
    public void shouldAllowGetName() {
        assertThat(getNewSourceBinding().getName(), notNullValue());
    }

    @Test
    public void shouldAllowGetContainerPath() {
        assertThat(getNewSourceBinding().getContainerPath(), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveConnectorWithNullConnector() {
        getNewSourceBinding().removeConnector(NULL_CONNECTOR);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveConnectorWithOneConnector() {
        getNewSourceBinding().removeConnector(getMockConnector());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveConnectorWithNonBoundConnector() {
        getNewSourceBindingWithMultipleConnectors().removeConnector(getMockConnector());
    }

    @Test
    public void shouldAllowRemoveConnector() {
        getNewSourceBindingWithMultipleConnectors().removeConnector(commonConnector);
    }
}
