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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.adminapi.PropertyDefinition;

/**
 * 
 */
public class ConnectorTest {

    @Mock
    private ConnectorType connectorType;
    @Mock
    private PropertyDefinition propertyDefinition;
    @Mock
    private ConnectorBinding connectorBinding;

    private static final String PROP_NAME = "name";

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    private Connector getNewConnector() {
        return new Connector(connectorBinding, connectorType);
    }

    @Test( expected = AssertionError.class )
    public void shouldNotAllowNullBinding() {
        new Connector(null, null);
    }

    @Test( expected = AssertionError.class )
    public void shouldNotAllowNullType() {
        new Connector(connectorBinding, null);
    }

    @Test
    public void shouldAllowGetName() {
        getNewConnector().getName();
    }

    @Test
    public void shouldAllowGetProperties() {
        getNewConnector().getProperties();
    }

    @Test
    public void shouldAllowGetType() {
        getNewConnector().getType();
    }

    @Test
    public void shouldAllowGetPropertyValue() {
        getNewConnector().getPropertyValue(null);
    }

    @Test
    public void shouldReturnInvalidValueForMissingProperty() {
        assertThat(getNewConnector().isValidPropertyValue(PROP_NAME, "true"), is(false));
    }

    // ======================================================================================
    // TEST VALID PROPERTY VALUES BASED ON TYPES
    // ======================================================================================

    @Test
    public void shouldDetectInvalidPropertyValueOfNull() {
        assertThat(getNewConnector().isValidPropertyValue(PROP_NAME, null), is(false));
    }

    @Test
    public void shouldDetectValidBooleanPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Boolean.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "true"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "false"), is(true));
    }

    @Test
    public void shouldReturnInvalidBooleanPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Boolean.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "blue"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, null), is(false));
    }

    @Test
    public void shouldDetectValidCharacterPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Character.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "c"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "1"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, ";"), is(true));
    }

    @Test
    public void shouldReturnInvalidCharacterPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Character.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "cc"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, null), is(false));
    }

    @Test
    public void shouldDetectValidBytePropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Byte.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "1"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "127"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-1"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-128"), is(true));
    }

    @Test
    public void shouldReturnInvalidBytePropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Byte.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "128"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-129"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, null), is(false));
    }

    @Test
    public void shouldDetectValidShortPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Short.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "1"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "32767"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-1"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-32768"), is(true));
    }

    @Test
    public void shouldReturnInvalidShortPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Short.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "32768"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-32769"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, null), is(false));
    }

    @Test
    public void shouldDetectValidIntegerPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Integer.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "1"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "2147483647"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-1"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-2147483648"), is(true));
    }

    @Test
    public void shouldReturnInvalidIntegerPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Integer.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "2147483648"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-2147483649"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, null), is(false));
    }

    @Test
    public void shouldDetectValidLongPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Long.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "1"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "9223372036854775807"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-1"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-9223372036854775808"), is(true));
    }

    @Test
    public void shouldReturnInvalidLongPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Long.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();

        assertThat(connector.isValidPropertyValue(PROP_NAME, "9223372036854775808"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-9223372036854775809"), is(false));
        assertThat(connector.isValidPropertyValue(PROP_NAME, null), is(false));
    }

    @Test
    public void shouldDetectValidFloatPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Float.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();
        // System.out.println(Float.MIN_VALUE);
        // System.out.println(Float.MAX_VALUE);
        assertThat(connector.isValidPropertyValue(PROP_NAME, "1.0"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "3.4028235E37"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "3.4028235E38"), is(true)); // TREATED AS INFINITY
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-1.0"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-3.4028235E38"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-3.4028235E39"), is(true)); // TREATED AS INFINITY
    }

    @Test
    public void shouldReturnInvalidFloatPropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Float.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);

        assertThat(getNewConnector().isValidPropertyValue(PROP_NAME, null), is(false));
    }

    @Test
    public void shouldDetectValidDoublePropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Double.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);
        Connector connector = getNewConnector();
        // System.out.println(Double.MIN_VALUE);
        // System.out.println(Double.MAX_VALUE);
        assertThat(connector.isValidPropertyValue(PROP_NAME, "1.0"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "1.7976931348623157E307"), is(true));
        // Anything above greater than 1.7976931348623157E308 is deemed INFINITY and it's still a valid double
        assertThat(connector.isValidPropertyValue(PROP_NAME, "1.7976931348623157E308"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-1.0"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-1.7976931348623157E308"), is(true));
        assertThat(connector.isValidPropertyValue(PROP_NAME, "-1.7976931348623157E309"), is(true));
    }

    @Test
    public void shouldReturnInvalidDoublePropertyValue() {
        stub(propertyDefinition.getPropertyTypeClassName()).toReturn(Double.class.getName());
        stub(connectorType.getPropertyDefinition(PROP_NAME)).toReturn(propertyDefinition);

        assertThat(getNewConnector().isValidPropertyValue(PROP_NAME, null), is(false));
    }

    @Test
    public void shouldAllowSetPropertyValue() throws Exception {
        ExecutionAdmin admin = mock(ExecutionAdmin.class);
        stub(connectorType.getAdmin()).toReturn(admin);
        stub(connectorBinding.getProperties()).toReturn(new Properties());

        getNewConnector().setPropertyValue("", "");
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullPropertyName() throws Exception {
        getNewConnector().setPropertyValue(null, null);
    }

    @Test
    public void shouldAllowSetProperties() throws Exception {
        ExecutionAdmin admin = mock(ExecutionAdmin.class);
        stub(connectorType.getAdmin()).toReturn(admin);
        Properties props = new Properties();

        stub(connectorBinding.getProperties()).toReturn(props);

        Properties newProps = new Properties();
        newProps.put("prop_1", "value_1");
        newProps.put("prop_2", "value_2");

        getNewConnector().setProperties(newProps);
        assertThat((String)props.get("prop_1"), is("value_1"));
        assertThat((String)props.get("prop_2"), is("value_2"));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNowAllowSetPropertiesWithNullProperties() throws Exception {
        getNewConnector().setProperties(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNowAllowSetPropertiesWithEmptyProperties() throws Exception {
        getNewConnector().setProperties(new Properties());
    }
}
