/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.Type;
import org.teiid.designer.extension.properties.Translation;

import com.metamatrix.core.util.CoreStringUtil;

/**
 * 
 */
public class ModelExtensionDefinitionValidatorTest implements Constants {

    private static final String ALL_SPACES = "   "; //$NON-NLS-1$

    private static final String INTERIOR_SPACES = "a   z"; //$NON-NLS-1$

    private static final String SPECIAL_CHARS = "!@#$%^&*()_+=-~`|\\}]{[:;\"'?/>.<,"; //$NON-NLS-1$

    @Test
    public void emptyDescriptionShouldBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateDescription(CoreStringUtil.Constants.EMPTY_STRING)));
    }

    @Test
    public void interiorSpacesAsDescriptionShouldBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateDescription(INTERIOR_SPACES)));
    }

    @Test
    public void specialCharactersAsDescriptionShouldBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateDescription(INTERIOR_SPACES)));
    }

    @Test
    public void nullDescriptionShouldBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateDescription(null)));
    }

    @Test
    public void specialCharactersAsMetaclassNameShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetaclassName(SPECIAL_CHARS)));
    }

    @Test
    public void interiorSpacesAsMetaclassNameShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetaclassName(INTERIOR_SPACES)));
    }

    @Test
    public void allSpacesAsMetaclassNameShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetaclassName(ALL_SPACES)));
    }

    @Test
    public void emptyMetaclassNameShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetaclassName(CoreStringUtil.Constants.EMPTY_STRING)));
    }

    @Test
    public void nullMetaclassNameShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetaclassName(null)));
    }

    @Test
    public void interiorSpacesAsMetamodelUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetamodelUri(INTERIOR_SPACES, null)));
    }

    @Test
    public void specialCharactersAsMetamodelUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetamodelUri(SPECIAL_CHARS, null)));
    }

    @Test
    public void allSpacesAsMetamodelUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetamodelUri(ALL_SPACES, null)));
    }

    @Test
    public void emptyMetamodelUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetamodelUri(CoreStringUtil.Constants.EMPTY_STRING,
                                                                                                  null)));
    }

    @Test
    public void nullMetamodelUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateMetamodelUri(null, null)));
    }

    @Test
    public void interiorSpacesAsNamespacePrefixShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateNamespacePrefix(INTERIOR_SPACES, null)));
    }

    @Test
    public void specialCharactersAsNamespacePrefixShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateNamespacePrefix(SPECIAL_CHARS, null)));
    }

    @Test
    public void emptyNamespacePrefixShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateNamespacePrefix(CoreStringUtil.Constants.EMPTY_STRING,
                                                                                                     null)));
    }

    @Test
    public void nullNamespacePrefixShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateNamespacePrefix(null, null)));
    }

    @Test
    public void interiorSpacesAsNamespaceUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateNamespaceUri(INTERIOR_SPACES, null)));
    }

    @Test
    public void specialCharactersAsNamespaceUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateNamespaceUri(SPECIAL_CHARS, null)));
    }

    @Test
    public void allSpacesAsNamespaceUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateNamespaceUri(ALL_SPACES, null)));
    }

    @Test
    public void emptyNamespaceUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateNamespaceUri(CoreStringUtil.Constants.EMPTY_STRING,
                                                                                                  null)));
    }

    @Test
    public void nullNamespaceUriShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateNamespaceUri(null, null)));
    }

    @Test
    public void allSpacesAsVersionShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateVersion(ALL_SPACES)));
    }

    @Test
    public void emptyVersionShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateVersion(CoreStringUtil.Constants.EMPTY_STRING)));
    }

    @Test
    public void nullVersionShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateVersion(null)));
    }

    @Test
    public void allSpacesAsVersionValueShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateVersion(ALL_SPACES)));
    }

    @Test
    public void nonIntegerAsVersionValueShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateVersion("abc"))); //$NON-NLS-1$
    }

    @Test
    public void negativeIntegerAsVersionValueShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateVersion("-5"))); //$NON-NLS-1$
    }

    @Test
    public void positiveIntegerAsVersionValueShouldBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateVersion("5"))); //$NON-NLS-1$
    }

    @Test
    public void zeroAsVersionValueShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateVersion("0"))); //$NON-NLS-1$
    }

    @Test
    public void allSpacesAsTranslationTextShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateTranslationText(ALL_SPACES)));
    }

    @Test
    public void emptyTranslationTextShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateTranslationText(CoreStringUtil.Constants.EMPTY_STRING)));
    }

    @Test
    public void nullTranslationTextShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateTranslationText(null)));
    }

    @Test
    public void nullTranslationTLocaleShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateTranslationLocale(null)));
    }

    @Test
    public void emptyPropertyFixedValueShouldBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyFixedValue(CoreStringUtil.Constants.EMPTY_STRING,
                                                                                                       null)));
    }

    @Test
    public void nullPropertyFixedValueShouldBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyDefaultValue(null, null, null)));
    }

    @Test
    public void emptyPropertyDefaultValueShouldBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyDefaultValue(CoreStringUtil.Constants.EMPTY_STRING,
                                                                                                         null, null)));
    }

    @Test
    public void nullPropertyDefaultValueShouldBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyFixedValue(null, null)));
    }

    @Test
    public void emptyPropertySimpleIdShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertySimpleId(CoreStringUtil.Constants.EMPTY_STRING,
                                                                                                      null)));
    }

    @Test
    public void nullPropertySimpleIdShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertySimpleId(null, null)));
    }

    @Test
    public void allSpacesAsPropertySimpleIdShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertySimpleId(ALL_SPACES, null)));
    }

    @Test
    public void interiorSpacesAsPropertySimpleIdShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertySimpleId(INTERIOR_SPACES, null)));
    }

    @Test
    public void specialCharactersAsPropertySimpleIdShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertySimpleId(SPECIAL_CHARS, null)));
    }

    @Test
    public void allSpacesAsRuntimeTypeShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyRuntimeType(ALL_SPACES)));
    }

    @Test
    public void emptyRuntimeTypeShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyRuntimeType(CoreStringUtil.Constants.EMPTY_STRING)));
    }

    @Test
    public void nullRuntimeTypeShouldNotBeValid() {
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyRuntimeType(null)));
    }

    @Test
    public void allEnumeratedRuntimeTypesShouldBeValid() {
        for (Type type : Type.values()) {
            assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyRuntimeType(type.toString())));
        }
    }

    @Test
    public void propertyAdvancededAttributeShouldAlwaysBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyAdvancedAttribute(true)));
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyAdvancedAttribute(false)));
    }

    @Test
    public void propertyIndexedAttributeShouldAlwaysBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyIndexedAttribute(true)));
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyIndexedAttribute(false)));
    }

    @Test
    public void propertyMaskedAttributeShouldAlwaysBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyMaskedAttribute(true)));
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyMaskedAttribute(false)));
    }

    @Test
    public void propertyRequiredAttributeShouldAlwaysBeValid() {
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyRequiredAttribute(true)));
        assertTrue(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyRequiredAttribute(false)));
    }

    @Test
    public void translationsWithDuplicatesShouldNotBeValid() {
        Collection<Translation> translations = Constants.Utils.getTranslationsWithDuplicates();
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validateTranslations(CoreStringUtil.Constants.EMPTY_STRING,
                                                                                                  translations, false)));
    }

    @Test
    public void allowedValuesWithDuplicatesShouldNotBeValid() {
        Collection<String> allowedValues = Constants.Utils.getStringAllowedValuesWithDuplicates();
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyAllowedValues(Type.STRING.toString(),
                                                                                                           allowedValues.toArray(new String[allowedValues.size()]))));
    }

    @Test
    public void allowedValuesWithWrongTypeShouldNotBeValid() {
        Collection<String> allowedValues = Constants.Utils.getStringAllowedValues();
        assertFalse(CoreStringUtil.isEmpty(ModelExtensionDefinitionValidator.validatePropertyAllowedValues(Type.INTEGER.toString(),
                                                                                                           allowedValues.toArray(new String[allowedValues.size()]))));
    }

}
