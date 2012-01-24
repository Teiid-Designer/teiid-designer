/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.Factory;
import org.teiid.designer.extension.Listener;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.Type;

/**
 * 
 */
public class ModelExtensionPropertyDefinitionImplTest {

    private ModelExtensionPropertyDefinitionImpl propDefn;

    @Before
    public void beforeEach() {
        this.propDefn = new ModelExtensionPropertyDefinitionImpl(Factory.createDefaultNamespaceProvider());
    }

    @Test
    public void cloneShouldBeEquals() {
        assertEquals(this.propDefn, this.propDefn.clone());
    }

    @Test
    public void cloneShouldHaveSameHashCode() {
        assertEquals(this.propDefn.hashCode(), this.propDefn.clone().hashCode());
    }

    @Test
    public void cloneShouldNotBeExactlyEquals() {
        assertTrue(this.propDefn != this.propDefn.clone());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullNamespaceProvider() {
        new ModelExtensionPropertyDefinitionImpl(null);
    }

    @Test
    public void shouldAllowNullParametersAtConstruction() {
        new ModelExtensionPropertyDefinitionImpl(Factory.createDefaultNamespaceProvider(),
                                                 null,
                                                 null,
                                                 null,
                                                 null,
                                                 null,
                                                 null,
                                                 null,
                                                 null,
                                                 null,
                                                 null,
                                                 null);
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterChangingAdvancedFlag() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setAdvanced(!this.propDefn.isAdvanced());
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.ADVANCED.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingAdvancedFlagToSameValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setAdvanced(this.propDefn.isAdvanced());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterAddingAnAllowedValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertTrue(this.propDefn.addAllowedValue(Constants.DEFAULT_STRING_ALLOWED_VALUES[0]));
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.ALLOWED_VALUES.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterAddingAnAllowedValueThatAlreadyExists() {
        String value = Constants.DEFAULT_STRING_ALLOWED_VALUES[0];
        this.propDefn.addAllowedValue(value);
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertFalse(this.propDefn.addAllowedValue(value));
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterRemovingAnAllowedValue() {
        String value = Constants.DEFAULT_STRING_ALLOWED_VALUES[0];
        this.propDefn.addAllowedValue(value);
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertTrue(this.propDefn.removeAllowedValue(value));
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.ALLOWED_VALUES.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterRemovingAnAllowedValueThatDoesNotExist() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertFalse(this.propDefn.removeAllowedValue(Constants.DEFAULT_STRING_ALLOWED_VALUES[0]));
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingAllowedValues() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setAllowedValues(new HashSet<String>(Arrays.asList(Constants.DEFAULT_STRING_ALLOWED_VALUES)));
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.ALLOWED_VALUES.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingAllowedValuesToSameValue() {
        this.propDefn.setAllowedValues(new HashSet<String>(Arrays.asList(Constants.DEFAULT_STRING_ALLOWED_VALUES)));
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setAllowedValues(this.propDefn.allowedValues());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingDefaultValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setDefaultValue("defaultValue"); //$NON-NLS-1$
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.DEFAULT_VALUE.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingDefaultValueToSameValue() {
        this.propDefn.setDefaultValue("defaultValue"); //$NON-NLS-1$
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setDefaultValue(this.propDefn.getDefaultValue());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingFixedValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setFixedValue("fixedValue"); //$NON-NLS-1$
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.FIXED_VALUE.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingFixedValueToSameValue() {
        this.propDefn.setFixedValue("fixedValue"); //$NON-NLS-1$
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setFixedValue(this.propDefn.getFixedValue());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterChangingIndexedFlag() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setIndex(!this.propDefn.shouldBeIndexed());
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.INDEX.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingIndexedFlagToSameValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setIndex(this.propDefn.shouldBeIndexed());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterChangingMaskedFlag() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setMasked(!this.propDefn.isMasked());
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.MASKED.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingMaskedFlagToSameValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setMasked(this.propDefn.isMasked());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterChangingRequiredFlag() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setRequired(!this.propDefn.isRequired());
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.REQUIRED.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingRequiredFlagToSameValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setRequired(this.propDefn.isRequired());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterChangingSimpleId() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setSimpleId("simpleId"); //$NON-NLS-1$
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.SIMPLE_ID.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingSimpleIdToSameValue() {
        this.propDefn.setSimpleId("simpleId"); //$NON-NLS-1$
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setSimpleId(this.propDefn.getSimpleId());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterChangingRuntimeType() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setType(Type.BIG_DECIMAL);
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.TYPE.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingRuntimeTypeToSameValue() {
        this.propDefn.setType(Type.BIG_DECIMAL);
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setType(this.propDefn.getType());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterAddingADescription() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertTrue(this.propDefn.addDescription(Constants.DEFAULT_TRANSLATIONS[0]));
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.DESCRIPTION.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterAddingADescriptionThatAlreadyExists() {
        Translation value = Constants.DEFAULT_TRANSLATIONS[0];
        this.propDefn.addDescription(value);
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertFalse(this.propDefn.addDescription(value));
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterRemovingADescription() {
        Translation value = Constants.DEFAULT_TRANSLATIONS[0];
        this.propDefn.addDescription(value);
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertTrue(this.propDefn.removeDescription(value));
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.DESCRIPTION.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterRemovingADescriptionThatDoesNotExist() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertFalse(this.propDefn.removeDescription(Constants.DEFAULT_TRANSLATIONS[0]));
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingDescriptions() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setDescriptions(new HashSet<Translation>(Arrays.asList(Constants.DEFAULT_TRANSLATIONS)));
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.DESCRIPTION.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingDescriptionsToSameValue() {
        this.propDefn.setDescriptions(new HashSet<Translation>(Arrays.asList(Constants.DEFAULT_TRANSLATIONS)));
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setDescriptions(this.propDefn.getDescriptions());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterAddingADisplayName() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertTrue(this.propDefn.addDisplayName(Constants.DEFAULT_TRANSLATIONS[0]));
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.DISPLAY_NAME.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterAddingADisplayNameThatAlreadyExists() {
        Translation value = Constants.DEFAULT_TRANSLATIONS[0];
        this.propDefn.addDisplayName(value);
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertFalse(this.propDefn.addDisplayName(value));
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterRemovingADisplayName() {
        Translation value = Constants.DEFAULT_TRANSLATIONS[0];
        this.propDefn.addDisplayName(value);
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertTrue(this.propDefn.removeDisplayName(value));
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.DISPLAY_NAME.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterRemovingADisplayNameThatDoesNotExist() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        assertFalse(this.propDefn.removeDisplayName(Constants.DEFAULT_TRANSLATIONS[0]));
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingDisplayNames() {
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setDisplayNames(new HashSet<Translation>(Arrays.asList(Constants.DEFAULT_TRANSLATIONS)));
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionPropertyDefinition.PropertyName.DISPLAY_NAME.toString(), l.getPropertyName());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingDisplayNamesToSameValue() {
        this.propDefn.setDisplayNames(new HashSet<Translation>(Arrays.asList(Constants.DEFAULT_TRANSLATIONS)));
        Listener l = Factory.createPropertyChangeListener();
        this.propDefn.addListener(l);
        this.propDefn.setDisplayNames(this.propDefn.getDisplayNames());
        assertEquals(0, l.getCount());
    }

}
