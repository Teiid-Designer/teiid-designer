/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.adminapi.PropertyDefinition;

/**
 * 
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( ConnectorType.class )
public class ConnectorTypeTest {

    @Mock
    private ExecutionAdmin admin;

    private Collection propDefs;

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        propDefs = new ArrayList<PropertyDefinition>();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullName() {
        new ConnectorType(null, null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullPropertyDefinitions() {
        new ConnectorType("", null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullAdmin() {
        new ConnectorType("", propDefs, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowCompareToWithNullType() {
        new ConnectorType("name", propDefs, admin).compareTo(null);
    }

    @Test
    public void shouldAllowGetAdmin() {
        new ConnectorType("name", propDefs, admin).getAdmin();
    }

    @Test
    public void shouldAllowGetName() {
        new ConnectorType("name", propDefs, admin).getName();
    }

    @Test
    public void shouldAllowGetPropertyDefinitions() {
        new ConnectorType("name", propDefs, admin).getPropertyDefinitions();
    }

    @Test
    public void shouldAllowGetPropertyDefinition() {
        new ConnectorType("name", propDefs, admin).getPropertyDefinition("propName");
    }

    @Test
    public void shouldCompareNameWhenSameServer() {
        // setup
        String serverUrl = "http://localhost:8080";
        ConnectorType connectorTypeA = MockObjectFactory.createConnectorType("A");
        when(connectorTypeA.getAdmin().getServer().getUrl()).thenReturn(serverUrl);
        ConnectorType connectorTypeB = MockObjectFactory.createConnectorType("B");
        when(connectorTypeB.getAdmin().getServer().getUrl()).thenReturn(serverUrl);

        // tests
        assertThat(connectorTypeA.compareTo(connectorTypeB) < 0, is(true));
        assertThat(connectorTypeB.compareTo(connectorTypeA) > 0, is(true));
        assertThat(connectorTypeA.compareTo(connectorTypeA), is(0));
    }

    @Test
    public void shouldCompareServerUrlWhenDifferentServers() {
        // setup
        String serverUrlA = "http://localhost:8080";
        String serverUrlB = "file:/users/sledge/text.txt";
        ConnectorType connectorTypeA = MockObjectFactory.createConnectorType("A");
        when(connectorTypeA.getAdmin().getServer().getUrl()).thenReturn(serverUrlA);
        ConnectorType connectorTypeB = MockObjectFactory.createConnectorType("B");
        when(connectorTypeB.getAdmin().getServer().getUrl()).thenReturn(serverUrlB);

        // tests
        assertThat(connectorTypeB.compareTo(connectorTypeA) < 0, is(true));
        assertThat(connectorTypeA.compareTo(connectorTypeB) > 0, is(true));
        assertThat(connectorTypeA.compareTo(connectorTypeA), is(0));
    }

}
