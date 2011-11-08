/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.Factory;
import org.teiid.designer.extension.Listener;

import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public class ModelExtensionDefinitionTest implements Constants {

    private ModelExtensionDefinition med;

    @Before
    public void beforeEach() {
        this.med = Factory.createDefinitionWithNoPropertyDefinitions();
    }

    @Test
    public void shouldAllowEmptyDescription() {
        this.med.setDescription(null);
        assertEquals(null, this.med.getDescription());

        this.med.setDescription(CoreStringUtil.Constants.EMPTY_STRING);
        assertEquals(CoreStringUtil.Constants.EMPTY_STRING, this.med.getDescription());
    }

    @Test
    public void shouldAllowEmptyMetamodelUri() {
        this.med.setMetamodelUri(null);
        assertEquals(null, this.med.getMetamodelUri());

        this.med.setMetamodelUri(CoreStringUtil.Constants.EMPTY_STRING);
        assertEquals(CoreStringUtil.Constants.EMPTY_STRING, this.med.getMetamodelUri());
    }

    @Test
    public void shouldAllowEmptyNamespacePrefix() {
        this.med.setNamespacePrefix(null);
        assertEquals(null, this.med.getNamespacePrefix());

        this.med.setNamespacePrefix(CoreStringUtil.Constants.EMPTY_STRING);
        assertEquals(CoreStringUtil.Constants.EMPTY_STRING, this.med.getNamespacePrefix());
    }

    @Test
    public void shouldAllowEmptyNamespaceUri() {
        this.med.setNamespaceUri(null);
        assertEquals(null, this.med.getNamespaceUri());

        this.med.setNamespaceUri(CoreStringUtil.Constants.EMPTY_STRING);
        assertEquals(CoreStringUtil.Constants.EMPTY_STRING, this.med.getNamespaceUri());
    }

    @Test
    public void shouldExtendMetaclass() {
        this.med = Factory.createDefinitionWithOneMetaclassAndNoPropertyDefinitions();
        assertTrue(this.med.extendsMetaclass(Constants.DEFAULT_METACLASS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowCheckForEmptyMetaclass() {
        this.med.extendsMetaclass(CoreStringUtil.Constants.EMPTY_STRING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowCheckForEmptyMetamodelUri() {
        this.med.extendsMetamodelUri(CoreStringUtil.Constants.EMPTY_STRING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowCheckForNullMetaclass() {
        this.med.extendsMetaclass(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowCheckForNullMetamodelUri() {
        this.med.extendsMetamodelUri(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowEmptyMetaclassToBeAdded() {
        this.med.addMetaclass(null);
        this.med.addMetaclass(CoreStringUtil.Constants.EMPTY_STRING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullAssistantOnConstruction() {
        new ModelExtensionDefinition(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNullListener() {
        this.med.addListener(null);
    }

    @Test
    public void shouldNotExtendMetaclassThatHasNotBeenAdded() {
        this.med = Factory.createDefinitionWithOneMetaclassAndNoPropertyDefinitions();
        assertFalse(this.med.extendsMetaclass(Constants.DEFAULT_METACLASS + "changed")); //$NON-NLS-1$
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingDescriptionToSameValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setDescription(this.med.getDescription());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingMetamodelUriToSameValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setMetamodelUri(this.med.getMetamodelUri());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingNamespacePrefixToSameValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setNamespacePrefix(this.med.getNamespacePrefix());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingNamespaceUriToSameValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setNamespaceUri(this.med.getNamespaceUri());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventAfterSettingVersionToSameValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setVersion(this.med.getVersion());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldNotReceivePropertyChangeEventsAfterUnregistering() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.removeListener(l);
        this.med.setVersion(this.med.getVersion());
        assertEquals(0, l.getCount());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterAddingMetaclass() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.addMetaclass("addedMetaclass"); //$NON-NLS-1$
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionDefinition.PropertyName.METACLASS.toString(), l.getPropertyName());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterRemovingMetaclass() {
        this.med = Factory.createDefinitionWithOneMetaclassAndNoPropertyDefinitions();
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.removeMetaclass(this.med.getExtendedMetaclasses()[0]);
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionDefinition.PropertyName.METACLASS.toString(), l.getPropertyName());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingDescriptionToDifferentValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setDescription(this.med.getDescription() + "changed"); //$NON-NLS-1$
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionDefinition.PropertyName.DESCRIPTION.toString(), l.getPropertyName());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingMetamodelUriToDifferentValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setMetamodelUri(this.med.getMetamodelUri() + "changed"); //$NON-NLS-1$
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionDefinition.PropertyName.METAMODEL_URI.toString(), l.getPropertyName());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingNamespacePrefixToDifferentValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setNamespacePrefix(this.med.getNamespacePrefix() + "changed"); //$NON-NLS-1$
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionDefinition.PropertyName.NAMESPACE_PREFIX.toString(), l.getPropertyName());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingNamespaceUriToDifferentValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setNamespaceUri(this.med.getNamespaceUri() + "changed"); //$NON-NLS-1$
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionDefinition.PropertyName.NAMESPACE_URI.toString(), l.getPropertyName());
    }

    @Test
    public void shouldReceivePropertyChangeEventAfterSettingVersionToDifferentValue() {
        Listener l = Factory.createPropertyChangeListener();
        this.med.addListener(l);
        this.med.setVersion(this.med.getVersion() + 1);
        assertEquals(1, l.getCount());
        assertEquals(ModelExtensionDefinition.PropertyName.VERSION.toString(), l.getPropertyName());
    }

    @Test
    public void shouldRemoveMetaclass() {
        this.med = Factory.createDefinitionWithOneMetaclassAndNoPropertyDefinitions();
        this.med.removeMetaclass(this.med.getExtendedMetaclasses()[0]);
        assertFalse(this.med.extendsMetaclass(DEFAULT_METACLASS));
    }

    @Test
    public void shouldSetBuiltIn() {
        this.med.markAsBuiltIn();
        assertTrue(this.med.isBuiltIn());
    }

    @Test
    public void shouldSetMetamodelUriOnConstruction() {
        assertEquals(DEFAULT_METAMODEL_URI, this.med.getMetamodelUri());
    }

    @Test
    public void shouldSetNamespacePrefixOnConstruction() {
        assertEquals(DEFAULT_NAMESPACE_PREFIX, this.med.getNamespacePrefix());
    }

    @Test
    public void shouldSetNamespaceUriOnConstruction() {
        assertEquals(DEFAULT_NAMESPACE_URI, this.med.getNamespaceUri());
    }

    @Test
    public void shouldSetNotToBeBuiltInOnConstruction() {
        assertFalse(this.med.isBuiltIn());
    }

    @Test
    public void shouldSetVersionOnConstruction() {
        assertEquals(ModelExtensionDefinitionHeader.DEFAULT_VERSION, this.med.getVersion());
    }

    @Test
    public void medsShouldBeEqual() {
        assertTrue(this.med.equals(Factory.createDefinitionWithNoPropertyDefinitions()));
        assertTrue(Factory.createDefinitionWithOneMetaclassAndNoPropertyDefinitions()
                          .equals(Factory.createDefinitionWithOneMetaclassAndNoPropertyDefinitions()));
    }

    @Test
    public void medsShouldHaveSameHashCodel() {
        assertTrue(this.med.hashCode() == Factory.createDefinitionWithNoPropertyDefinitions().hashCode());
        assertTrue(Factory.createDefinitionWithOneMetaclassAndNoPropertyDefinitions().hashCode() == Factory.createDefinitionWithOneMetaclassAndNoPropertyDefinitions()
                                                                                                           .hashCode());
    }

    @Test
    public void shouldUpdateMetaclassWithNoProperties() {
        this.med = Factory.createDefinitionWithOneMetaclassAndNoPropertyDefinitions();
        String original = this.med.getExtendedMetaclasses()[0];
        String modified = original + "changed"; //$NON-NLS-1$
        this.med.updateMetaclass(original, modified);
        assertEquals(1, this.med.getExtendedMetaclasses().length);
        assertEquals(modified, this.med.getExtendedMetaclasses()[0]);
    }

    @Test
    public void shouldUpdateMetaclassWithProperties() {
        this.med = Factory.createDefinitionWithOneMetaclassWithOnePropertyDefinition();
        String original = this.med.getExtendedMetaclasses()[0];
        String modified = original + "changed"; //$NON-NLS-1$
        this.med.updateMetaclass(original, modified);
        assertEquals(1, this.med.getExtendedMetaclasses().length);
        assertEquals(modified, this.med.getExtendedMetaclasses()[0]);
        assertEquals(1, this.med.getPropertyDefinitions(modified).size());
    }
}
