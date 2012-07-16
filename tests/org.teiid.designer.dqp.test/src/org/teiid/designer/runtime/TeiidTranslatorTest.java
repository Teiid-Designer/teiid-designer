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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.designer.runtime.connection.IConnectionProperties;

/**
 * 
 */
public class TeiidTranslatorTest {

    private static final String TRANSLATOR_NAME = "translatorName";
    private static final String TRANSLATOR_TYPE_NAME = "translatorTypeName";
    private static final String TYPE_NAME = "theType";
    private static final String PROP_NAME = "name";
    private static Properties PROPERTIES;
    private static Collection<PropertyDefinition> PROP_DEFS;

    @Mock
    private ExecutionAdmin commonExecutionAdmin;
    @Mock
    private PropertyDefinition propertyDefinition;
    @Mock
    private Translator translator;

    private TeiidTranslator teiidTranslator;
    
    

    @BeforeClass
    public static void oneTimeSetup() {
        PROPERTIES = new Properties();
        PROPERTIES.setProperty(IConnectionProperties.CONNECTOR_TYPE, TRANSLATOR_TYPE_NAME);
    }

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);

        PROP_DEFS = new ArrayList<PropertyDefinition>(1);
        when(propertyDefinition.getName()).thenReturn("name");
        PROP_DEFS.add(propertyDefinition);
        
        this.translator = mock(Translator.class);

        when(this.translator.getName()).thenReturn(TRANSLATOR_NAME);
        when(this.translator.getProperties()).thenReturn(PROPERTIES);
        when(this.translator.getType()).thenReturn(TYPE_NAME);
        
        Server server = mock(Server.class);
        this.commonExecutionAdmin = mock(ExecutionAdmin.class);
        when(this.commonExecutionAdmin.getServer()).thenReturn(server);
        this.teiidTranslator = new TeiidTranslator(this.translator, PROP_DEFS, this.commonExecutionAdmin);
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullBinding() {
        new TeiidTranslator(null, null, null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullType() {
        new TeiidTranslator(translator, null, null);
    }

    @Test
    public void shouldGetCorrectName() {
        assertThat(this.teiidTranslator.getName(), is(TRANSLATOR_NAME));
    }

    @Test
    public void shouldGetProperties() {
        assertThat(this.teiidTranslator.getProperties(), is(sameInstance(PROPERTIES)));
    }

    @Test
    public void shouldGetType() {
        assertThat(this.teiidTranslator.getType(), is(TYPE_NAME));
    }

    @Test
    public void shouldAllowGetPropertyValue() {
        this.teiidTranslator.getPropertyValue(null);
    }

    @Test
    public void shouldReturnInvalidValueForMissingProperty() {
        when(this.teiidTranslator.getPropertyDefinition(PROP_NAME)).thenReturn(null); // remove property
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "true"), notNullValue());
    }

    // ======================================================================================
    // TEST VALID PROPERTY VALUES BASED ON TYPES
    // ======================================================================================

    @Test
    public void shouldDetectInvalidPropertyValueOfNull() {
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidBooleanPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Boolean.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "true"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "false"), nullValue());
    }

    @Test
    public void shouldReturnInvalidBooleanPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Boolean.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "blue"), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidCharacterPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Character.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "c"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, ";"), nullValue());
    }

    @Test
    public void shouldReturnInvalidCharacterPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Character.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "cc"), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidBytePropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Byte.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Byte.toString(Byte.MAX_VALUE)), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "-1"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Byte.toString(Byte.MIN_VALUE)), nullValue());
    }

    @Test
    public void shouldReturnInvalidBytePropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Byte.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Long.toString((long)Byte.MAX_VALUE + (long)1)), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Long.toString((long)Byte.MIN_VALUE - (long)1)), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidShortPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Short.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Short.toString(Short.MAX_VALUE)), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "-1"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Short.toString(Short.MIN_VALUE)), nullValue());
    }

    @Test
    public void shouldReturnInvalidShortPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Short.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Long.toString((long)Short.MAX_VALUE + 1)), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Long.toString((long)Short.MIN_VALUE - 1)), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidIntegerPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Integer.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Integer.toString(Integer.MAX_VALUE)), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "-1"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Integer.toString(Integer.MIN_VALUE - 1)), nullValue());
    }

    @Test
    public void shouldReturnInvalidIntegerPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Integer.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Double.toString(Integer.MAX_VALUE + 1)), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Double.toString((double)Integer.MIN_VALUE - 1)), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidLongPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Long.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "1"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Long.toString(Long.MAX_VALUE)), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "-1"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Long.toString(Long.MIN_VALUE)), nullValue());
    }

    @Test
    public void shouldReturnInvalidLongPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Long.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Double.toString((double)Long.MAX_VALUE + 1)), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Double.toString((double)Long.MIN_VALUE - 1)), notNullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidFloatPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Float.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "1.0"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Float.toString(Float.MAX_VALUE)), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Float.toString(Float.MAX_VALUE * 10)), nullValue()); // Infinity
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, "-1.0"), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Float.toString(Float.MIN_VALUE)), nullValue());
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, Float.toString((float)-1.0 / (float)0.0)), nullValue()); // -Infinity
    }

    @Test
    public void shouldReturnInvalidFloatPropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Float.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldDetectValidDoublePropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Double.TYPE.getName());

        // tests
        assertThat(teiidTranslator.isValidPropertyValue(PROP_NAME, "1.0"), nullValue());
        assertThat(teiidTranslator.isValidPropertyValue(PROP_NAME, Double.toString(Double.MAX_VALUE)), nullValue());
        assertThat(teiidTranslator.isValidPropertyValue(PROP_NAME, Float.toString((float)Double.MAX_VALUE + 1)), nullValue()); // Infinity
        assertThat(teiidTranslator.isValidPropertyValue(PROP_NAME, "-1.0"), nullValue());
        assertThat(teiidTranslator.isValidPropertyValue(PROP_NAME, Double.toString(Double.MIN_VALUE)), nullValue());
        assertThat(teiidTranslator.isValidPropertyValue(PROP_NAME, Double.toString(-1.0 / 0.0)), nullValue()); // -Infinity
    }

    @Test
    public void shouldReturnInvalidDoublePropertyValue() {
        // configure property definition
        when(propertyDefinition.getPropertyTypeClassName()).thenReturn(Double.TYPE.getName());

        // tests
        assertThat(this.teiidTranslator.isValidPropertyValue(PROP_NAME, null), notNullValue());
    }

    @Test
    public void shouldSetPropertyValue() throws Exception {

        Properties props = new Properties();
        props.setProperty(PROP_NAME, "oldValue");
        when(translator.getProperties()).thenReturn(props);
        when(translator.getPropertyValue(PROP_NAME)).thenReturn(props.getProperty(PROP_NAME));

        String newValue = "newValue";
        this.teiidTranslator.setPropertyValue(PROP_NAME, newValue);
        assertThat(this.teiidTranslator.getProperties().getProperty(PROP_NAME), is(newValue));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowNullPropertyName() throws Exception {
        this.teiidTranslator.setPropertyValue(null, null);
    }

    @Test
    public void shouldAllowSetProperties() throws Exception {
        Properties props = new Properties();

        when(translator.getProperties()).thenReturn(props);

        Properties newProps = new Properties();
        newProps.put("prop_1", "value_1");
        newProps.put("prop_2", "value_2");

        this.teiidTranslator.setProperties(newProps);
        assertThat((String)props.get("prop_1"), is("value_1"));
        assertThat((String)props.get("prop_2"), is("value_2"));
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithNullProperties() throws Exception {
        this.teiidTranslator.setProperties(null);
    }

    @Test( expected = IllegalArgumentException.class )
    public void shouldNotAllowSetPropertiesWithEmptyProperties() throws Exception {
        this.teiidTranslator.setProperties(new Properties());
    }

    @Test
    public void shouldBeEqualWhenSameNameSameServer() {
        // setup
    	Translator thisBinding = MockObjectFactory.createTranslator(TRANSLATOR_NAME, TRANSLATOR_TYPE_NAME);
    	Translator thatBinding = MockObjectFactory.createTranslator(TRANSLATOR_NAME, TRANSLATOR_TYPE_NAME);

        TeiidTranslator thisTranslator = new TeiidTranslator(thisBinding, PROP_DEFS, this.commonExecutionAdmin);
        TeiidTranslator thatTranslator = new TeiidTranslator(thatBinding, PROP_DEFS, this.commonExecutionAdmin);

        // test
        assertEquals("Translators should be equal if they have the same name and same server", thisTranslator, thatTranslator);
    }

    @Test
    public void shouldNotBeEqualWhenSameNameDifferentServer() {
        // setup
        TeiidTranslator thisTranslator = MockObjectFactory.createTeiidTranslator(TRANSLATOR_NAME, TRANSLATOR_TYPE_NAME, PROP_DEFS);
        TeiidTranslator thatTranslator = MockObjectFactory.createTeiidTranslator(TRANSLATOR_NAME, TRANSLATOR_TYPE_NAME, PROP_DEFS);

        // test
        assertFalse("Translators should not be equal if they have the same name but different servers",
                    thisTranslator.equals(thatTranslator));
    }

    @Test
    public void shouldNotBeEqualWhenDifferentNameSameServer() {
        // setup
        TeiidTranslator thisTranslator = MockObjectFactory.createTeiidTranslator(TRANSLATOR_NAME, TRANSLATOR_TYPE_NAME, PROP_DEFS);
        Translator thatBinding = MockObjectFactory.createTranslator("differentName", TRANSLATOR_TYPE_NAME);
        TeiidTranslator thatTranslator = new TeiidTranslator(thatBinding, PROP_DEFS, this.commonExecutionAdmin);

        // test
        assertFalse("Translators should not be equal if they have different names but the same server",
                    thisTranslator.equals(thatTranslator));
    }

    @Test
    public void shouldHaveSameHashcodeIfEquals() {
        // setup
    	Translator thisBinding = MockObjectFactory.createTranslator(TRANSLATOR_NAME, TRANSLATOR_TYPE_NAME);
    	Translator thatBinding = MockObjectFactory.createTranslator(TRANSLATOR_NAME, TRANSLATOR_TYPE_NAME);

        TeiidTranslator thisTranslator = new TeiidTranslator(thisBinding, PROP_DEFS, this.commonExecutionAdmin);
        TeiidTranslator thatTranslator = new TeiidTranslator(thatBinding, PROP_DEFS, this.commonExecutionAdmin);

        // tests
        assertEquals(thisTranslator, thatTranslator);
        assertEquals(thisTranslator.hashCode(), thatTranslator.hashCode());
    }

    @Test
    public void shouldNotHaveSameHashcodeIfNotEquals() {
        // setup
        TeiidTranslator thisTranslator = MockObjectFactory.createTeiidTranslator(TRANSLATOR_NAME, TRANSLATOR_TYPE_NAME, PROP_DEFS);
        TeiidTranslator thatTranslator = MockObjectFactory.createTeiidTranslator("differentName", TRANSLATOR_TYPE_NAME, PROP_DEFS);

        // tests
        assertFalse(thisTranslator.equals(thatTranslator));
        assertFalse(thisTranslator.hashCode() == thatTranslator.hashCode());
    }

}
