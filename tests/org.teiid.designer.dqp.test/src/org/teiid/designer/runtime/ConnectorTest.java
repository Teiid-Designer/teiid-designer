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
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Properties;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.teiid.adminapi.ConnectionFactory;
import org.teiid.adminapi.PropertyDefinition;

/**
 * 
 */
@RunWith( PowerMockRunner.class )
@PrepareForTest( ConnectorType.class )
public class ConnectorTest {

    private static final String CONNECTOR_NAME = "connectorName";
    private static final String CONNECTOR_TYPE_NAME = "connectorTypeName";
    private static final String PROP_NAME = "name";
    private static Properties PROPERTIES;

    @Mock
    private ConnectorType connectorType;
    @Mock
    private PropertyDefinition propertyDefinition;
    @Mock
    private ConnectionFactory connectionFactory;

    private Connector connector;

    @BeforeClass
    public static void oneTimeSetup() {
        PROPERTIES = new Properties();
        PROPERTIES.setProperty(IConnectorProperties.CONNECTOR_TYPE, CONNECTOR_TYPE_NAME);
    }

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);

        this.connectionFactory = mock(ConnectionFactory.class);
        when(this.connectionFactory.getName()).thenReturn(CONNECTOR_NAME);
        when(this.connectionFactory.getProperties()).thenReturn(PROPERTIES);

        when(this.connectorType.getPropertyDefinition(PROP_NAME)).thenReturn(this.propertyDefinition);
        this.connector = new Connector(this.connectionFactory, this.connectorType);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullBinding() {
        new Connector(null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullType() {
        new Connector(connectionFactory, null);
    }

    @Test
    public void shouldGetCorrectName() {
        assertThat(this.connector.getName(), is(CONNECTOR_NAME));
    }

    @Test
    public void shouldGetProperties() {
        assertThat(this.connector.getProperties(), is(sameInstance(PROPERTIES)));
    }

    @Test
    public void shouldGetType() {
        assertThat(this.connector.getType(), is(sameInstance(this.connectorType)));
    }

    @Test
    public void shouldAllowGetPropertyValue() {
        this.connector.getPropertyValue(null);
    }

    @Test
    public void shouldReturnInvalidValueForMissingProperty() {
        when(this.connectorType.getPropertyDefinition(PROP_NAME)).thenReturn(null); // remove property
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "true"), notNullValue());
    }

    // ======================================================================================
    // TEST VALID PROPERTY VALUES BASED ON TYPES
    // ======================================================================================

    @Test
    public void shouldDetectInvalidPropertyValueOfNull() {
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidBooleanPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Boolean.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "true"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "false"), nullValue());
    }

    @Test
    public void shouldReturnInvalidBooleanPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Boolean.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "blue"), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidCharacterPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Character.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "c"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, ";"), nullValue());
    }

    @Test
    public void shouldReturnInvalidCharacterPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Character.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "cc"), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidBytePropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Byte.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Byte.toString(Byte.MAX_VALUE)), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "-1"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Byte.toString(Byte.MIN_VALUE)), nullValue());
    }

    @Test
    public void shouldReturnInvalidBytePropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Byte.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Long.toString((long)Byte.MAX_VALUE + (long)1)), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Long.toString((long)Byte.MIN_VALUE - (long)1)), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidShortPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Short.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Short.toString(Short.MAX_VALUE)), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "-1"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Short.toString(Short.MIN_VALUE)), nullValue());
    }

    @Test
    public void shouldReturnInvalidShortPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Short.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Long.toString((long)Short.MAX_VALUE + 1)), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Long.toString((long)Short.MIN_VALUE - 1)), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidIntegerPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Integer.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Integer.toString(Integer.MAX_VALUE)), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "-1"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Integer.toString(Integer.MIN_VALUE - 1)), nullValue());
    }

    @Test
    public void shouldReturnInvalidIntegerPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Integer.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Double.toString(Integer.MAX_VALUE + 1)), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Double.toString((double)Integer.MIN_VALUE - 1)), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidLongPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Long.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Long.toString(Long.MAX_VALUE)), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "-1"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Long.toString(Long.MIN_VALUE)), nullValue());
    }

    @Test
    public void shouldReturnInvalidLongPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Long.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Double.toString((double)Long.MAX_VALUE + 1)), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Double.toString((double)Long.MIN_VALUE - 1)), notNullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidFloatPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Float.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "1.0"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Float.toString(Float.MAX_VALUE)), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Float.toString(Float.MAX_VALUE * 10)), nullValue()); // Infinity
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, "-1.0"), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Float.toString(Float.MIN_VALUE)), nullValue());
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, Float.toString((float)-1.0 / (float)0.0)), nullValue()); // -Infinity
    }

    @Test
    public void shouldReturnInvalidFloatPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Float.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidDoublePropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Double.TYPE.getName());

        // tests
        assertThat(connector.isValidPropertyValue(PROP_NAME, "1.0"), nullValue());
        assertThat(connector.isValidPropertyValue(PROP_NAME, Double.toString(Double.MAX_VALUE)), nullValue());
        assertThat(connector.isValidPropertyValue(PROP_NAME, Float.toString((float)Double.MAX_VALUE + 1)), nullValue()); // Infinity
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-1.0"), nullValue());
        assertThat(connector.isValidPropertyValue(PROP_NAME, Double.toString(Double.MIN_VALUE)), nullValue());
        assertThat(connector.isValidPropertyValue(PROP_NAME, Double.toString(-1.0 / 0.0)), nullValue()); // -Infinity
    }

    @Test
    public void shouldReturnInvalidDoublePropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Double.TYPE.getName());

        // tests
        assertThat(this.connector.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldSetPropertyValue() throws Exception {
        ExecutionAdmin admin = mock(ExecutionAdmin.class);
        when(connectorType.getAdmin()).thenReturn(admin);

        Properties props = new Properties();
        props.setProperty(PROP_NAME, "oldValue");
        when(connectionFactory.getProperties()).thenReturn(props);
        when(connectionFactory.getPropertyValue(PROP_NAME)).thenReturn(props.getProperty(PROP_NAME));

        String newValue = "newValue";
        this.connector.setPropertyValue(PROP_NAME, newValue);
        assertThat(this.connector.getProperties().getProperty(PROP_NAME), is(newValue));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullPropertyName() throws Exception {
        this.connector.setPropertyValue(null, null);
    }

    @Test
    public void shouldAllowSetProperties() throws Exception {
        ExecutionAdmin admin = mock(ExecutionAdmin.class);
        when(connectorType.getAdmin()).thenReturn(admin);
        Properties props = new Properties();

        when(connectionFactory.getProperties()).thenReturn(props);

        Properties newProps = new Properties();
        newProps.put("prop_1", "value_1");
        newProps.put("prop_2", "value_2");

        this.connector.setProperties(newProps);
        assertThat((String)props.get("prop_1"), is("value_1"));
        assertThat((String)props.get("prop_2"), is("value_2"));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithNullProperties() throws Exception {
        this.connector.setProperties(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithEmptyProperties() throws Exception {
        this.connector.setProperties(new Properties());
    }

    @Test
    public void shouldBeEqualWhenSameNameSameServer() {
        // setup
        ConnectionFactory thisBinding = MockObjectFactory.createConnectionFactory(CONNECTOR_NAME, CONNECTOR_TYPE_NAME);
        ConnectionFactory thatBinding = MockObjectFactory.createConnectionFactory(CONNECTOR_NAME, CONNECTOR_TYPE_NAME);
        ConnectorType connectorType = MockObjectFactory.createConnectorType(CONNECTOR_TYPE_NAME);
        Connector thisConnector = new Connector(thisBinding, connectorType);
        Connector thatConnector = new Connector(thatBinding, connectorType);

        // test
        assertEquals("Connectors should be equal if they have the same name and same server", thisConnector, thatConnector);
    }

    @Test
    public void shouldNotBeEqualWhenSameNameDifferentServer() {
        // setup
        Connector thisConnector = MockObjectFactory.createConnector(CONNECTOR_NAME, CONNECTOR_TYPE_NAME);
        Connector thatConnector = MockObjectFactory.createConnector(CONNECTOR_NAME, CONNECTOR_TYPE_NAME);

        // test
        assertFalse("Connectors should not be equal if they have the same name but different servers",
                    thisConnector.equals(thatConnector));
    }

    @Test
    public void shouldNotBeEqualWhenDifferentNameSameServer() {
        // setup
        Connector thisConnector = MockObjectFactory.createConnector(CONNECTOR_NAME, CONNECTOR_TYPE_NAME);
        ConnectionFactory thatBinding = MockObjectFactory.createConnectionFactory("differentName", CONNECTOR_TYPE_NAME);
        Connector thatConnector = new Connector(thatBinding, thisConnector.getType());

        // test
        assertFalse("Connectors should not be equal if they have different names but the same server",
                    thisConnector.equals(thatConnector));
    }

    @Test
    public void shouldHaveSameHashcodeIfEquals() {
        // setup
        ConnectionFactory thisBinding = MockObjectFactory.createConnectionFactory(CONNECTOR_NAME, CONNECTOR_TYPE_NAME);
        ConnectionFactory thatBinding = MockObjectFactory.createConnectionFactory(CONNECTOR_NAME, CONNECTOR_TYPE_NAME);
        ConnectorType connectorType = MockObjectFactory.createConnectorType(CONNECTOR_TYPE_NAME);
        Connector thisConnector = new Connector(thisBinding, connectorType);
        Connector thatConnector = new Connector(thatBinding, connectorType);

        // tests
        assertEquals(thisConnector, thatConnector);
        assertEquals(thisConnector.hashCode(), thatConnector.hashCode());
    }

    @Test
    public void shouldNotHaveSameHashcodeIfNotEquals() {
        // setup
        Connector thisConnector = MockObjectFactory.createConnector(CONNECTOR_NAME, CONNECTOR_TYPE_NAME);
        Connector thatConnector = MockObjectFactory.createConnector("differentName", CONNECTOR_TYPE_NAME);

        // tests
        assertFalse(thisConnector.equals(thatConnector));
        assertFalse(thisConnector.hashCode() == thatConnector.hashCode());
    }

}
