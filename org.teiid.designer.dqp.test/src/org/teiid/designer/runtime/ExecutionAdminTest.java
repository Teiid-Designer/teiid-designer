/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.ConnectorBinding;

/**
 * 
 */
public class ExecutionAdminTest {

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    private ExecutionAdmin getNewAdmin() throws Exception {
        return new ExecutionAdmin(mock(Admin.class), mock(Server.class), mock(EventManager.class));
    }

    private Connector getNewConnector() throws Exception {
        return new Connector(mock(ConnectorBinding.class), mock(ConnectorType.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullAdmin() throws Exception {
        assertThat(new ExecutionAdmin(null, null, null), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullServer() throws Exception {
        assertThat(new ExecutionAdmin(mock(Admin.class), null, null), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullEventManager() throws Exception {
        assertThat(new ExecutionAdmin(mock(Admin.class), mock(Server.class), null), notNullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullNameForAddConnector() throws Exception {
        getNewAdmin().addConnector(null, null, null);

    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowEmptyNameForAddConnector() throws Exception {
        getNewAdmin().addConnector("", null, null);

    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullTypeForAddConnector() throws Exception {
        getNewAdmin().addConnector("name", null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullPropertiesForAddConnector() throws Exception {
        getNewAdmin().addConnector("", mock(ConnectorType.class), null);
    }

    @Test
    public void shouldAddConnector() throws Exception {

        ConnectorBinding cb = mock(ConnectorBinding.class);
        Admin admin = mock(Admin.class);
        stub(admin.getConnectorBinding("name")).toReturn(cb);
        ExecutionAdmin execAdmin = new ExecutionAdmin(admin, mock(Server.class), mock(EventManager.class));
        execAdmin.addConnector("name", mock(ConnectorType.class), new Properties());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullConnectorName() throws Exception {
        getNewAdmin().ensureUniqueConnectorName("");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowZeroLengthConnectorName() throws Exception {
        getNewAdmin().ensureUniqueConnectorName("");
    }

    @Test
    public void shouldAllowEnsureUniqueConnectorName() throws Exception {
        getNewAdmin().ensureUniqueConnectorName("name");
    }

    @Test
    public void shouldAllowGetAdminApi() throws Exception {
        getNewAdmin().getAdminApi();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetConnectorWithNull() throws Exception {
        getNewAdmin().getConnector(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetConnectorWithZeroLengthName() throws Exception {
        getNewAdmin().getConnector("");
    }

    @Test
    public void shouldAllowGetConnector() throws Exception {
        getNewAdmin().getConnector("name");
    }

    @Test
    public void shouldAllowGetConnectors() throws Exception {
        getNewAdmin().getConnectors();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetConnectorsForNullType() throws Exception {
        getNewAdmin().getConnectors(null);
    }

    @Test
    public void shouldAllowGetConnectorsForType() throws Exception {
        getNewAdmin().getConnectors(mock(ConnectorType.class));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetConnectorTypeWithNullName() throws Exception {
        getNewAdmin().getConnectorType(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetConnectorTypeWithZeroLengthName() throws Exception {
        getNewAdmin().getConnectorType("");
    }

    @Test
    public void shouldAllowGetConnectorType() throws Exception {
        getNewAdmin().getConnectorType("name");
    }

    @Test
    public void shouldAllowGetConnectorTypes() throws Exception {
        getNewAdmin().getConnectorTypes();
    }

    @Test
    public void shouldAllowGetEventManager() throws Exception {
        assertThat(getNewAdmin().getEventManager(), notNullValue());
    }

    @Test
    public void shouldAllowGetServer() throws Exception {
        assertThat(getNewAdmin().getServer(), notNullValue());
    }

    @Test
    public void shouldAllowGetSourceBindingManager() throws Exception {
        assertThat(getNewAdmin().getSourceBindingsManager(), notNullValue());
    }

    @Test
    public void shouldAllowGetVdbs() throws Exception {
        getNewAdmin().getVdbs();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetVdbWithNullName() throws Exception {
        getNewAdmin().getVdb(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowGetVdbWithEmptyName() throws Exception {
        getNewAdmin().getVdb("");
    }

    @Test
    public void shouldAllowGetVdbWithName() throws Exception {
        getNewAdmin().getVdb("name");
    }

    @Test
    public void shouldAllowRefresh() throws Exception {
        getNewAdmin().refresh();
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowRemoveConnectorWithNullConnector() throws Exception {
        getNewAdmin().removeConnector(null);
    }

    @Test
    public void shouldAllowRemoveConnectorWithConnector() throws Exception {
        getNewAdmin().removeConnector(new Connector(mock(ConnectorBinding.class), mock(ConnectorType.class)));
    }

    @Test
    public void shouldReturnExceptionOnValidateConnectorNameWithNullName() throws Exception {
        assertThat(getNewAdmin().validateConnectorName(null), instanceOf(Exception.class));
    }

    @Test
    public void shouldReturnExceptionOnValidateConnectorNameWithZeroLengthName() throws Exception {
        assertThat(getNewAdmin().validateConnectorName(""), instanceOf(Exception.class));
    }

    @Test
    public void shouldAllowValidateConnectorName() throws Exception {
        assertThat(getNewAdmin().validateConnectorName("name"), nullValue());
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithNullConnector() throws Exception {
        getNewAdmin().setPropertyValue(null, null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithNullName() throws Exception {
        getNewAdmin().setPropertyValue(getNewConnector(), null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithZeroLengthName() throws Exception {
        getNewAdmin().setPropertyValue(getNewConnector(), "", null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithNullValue() throws Exception {
        getNewAdmin().setPropertyValue(getNewConnector(), "name", null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertyValueWithZeroLengthValue() throws Exception {
        getNewAdmin().setPropertyValue(getNewConnector(), "name", "");
    }

    @Test
    public void shouldAllowSetPropertyValue() throws Exception {
        Connector mockConnector = mock(Connector.class);
        stub(mockConnector.isValidPropertyValue("name", "value")).toReturn(true);
        getNewAdmin().setPropertyValue(mockConnector, "name", "value");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithNullConnector() throws Exception {
        getNewAdmin().setProperties(null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithNullProperties() throws Exception {
        getNewAdmin().setProperties(getNewConnector(), null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithEmptyProperties() throws Exception {
        getNewAdmin().setProperties(getNewConnector(), new Properties());
    }

    @Test
    public void shouldAllowSetProperties() throws Exception {
        ExecutionAdmin admin = mock(ExecutionAdmin.class);
        Connector connector = getNewConnector();

        Properties newProps = new Properties();
        newProps.put("prop_1", "value_1");

        admin.setProperties(connector, newProps);
    }
}
