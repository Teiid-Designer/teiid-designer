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
        assertTrue(ModelExtensionDefinitionValidator.validateDescription(CoreStringUtil.Constants.EMPTY_STRING).isOk());
    }

    @Test
    public void interiorSpacesAsDescriptionShouldBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateDescription(INTERIOR_SPACES).isOk());
    }

    @Test
    public void specialCharactersAsDescriptionShouldBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateDescription(INTERIOR_SPACES).isOk());
    }

    @Test
    public void nullDescriptionShouldBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateDescription(null).isOk());
    }

    @Test
    public void specialCharactersAsMetaclassNameShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetaclassName(SPECIAL_CHARS).isError());
    }

    @Test
    public void interiorSpacesAsMetaclassNameShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetaclassName(INTERIOR_SPACES).isError());
    }

    @Test
    public void allSpacesAsMetaclassNameShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetaclassName(ALL_SPACES).isError());
    }

    @Test
    public void emptyMetaclassNameShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetaclassName(CoreStringUtil.Constants.EMPTY_STRING).isError());
    }

    @Test
    public void nullMetaclassNameShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetaclassName(null).isError());
    }

    @Test
    public void interiorSpacesAsMetamodelUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetamodelUri(INTERIOR_SPACES, null).isError());
    }

    @Test
    public void specialCharactersAsMetamodelUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetamodelUri(SPECIAL_CHARS, null).isError());
    }

    @Test
    public void allSpacesAsMetamodelUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetamodelUri(ALL_SPACES, null).isError());
    }

    @Test
    public void emptyMetamodelUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetamodelUri(CoreStringUtil.Constants.EMPTY_STRING, null).isError());
    }

    @Test
    public void nullMetamodelUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateMetamodelUri(null, null).isError());
    }

    @Test
    public void interiorSpacesAsNamespacePrefixShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateNamespacePrefix(INTERIOR_SPACES, null).isError());
    }

    @Test
    public void specialCharactersAsNamespacePrefixShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateNamespacePrefix(SPECIAL_CHARS, null).isError());
    }

    @Test
    public void emptyNamespacePrefixShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateNamespacePrefix(CoreStringUtil.Constants.EMPTY_STRING, null).isError());
    }

    @Test
    public void nullNamespacePrefixShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateNamespacePrefix(null, null).isError());
    }

    @Test
    public void interiorSpacesAsNamespaceUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateNamespaceUri(INTERIOR_SPACES, null).isError());
    }

    @Test
    public void specialCharactersAsNamespaceUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateNamespaceUri(SPECIAL_CHARS, null).isError());
    }

    @Test
    public void allSpacesAsNamespaceUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateNamespaceUri(ALL_SPACES, null).isError());
    }

    @Test
    public void emptyNamespaceUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateNamespaceUri(CoreStringUtil.Constants.EMPTY_STRING, null).isError());
    }

    @Test
    public void nullNamespaceUriShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateNamespaceUri(null, null).isError());
    }

    @Test
    public void allSpacesAsVersionShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateVersion(ALL_SPACES).isError());
    }

    @Test
    public void emptyVersionShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateVersion(CoreStringUtil.Constants.EMPTY_STRING).isError());
    }

    @Test
    public void nullVersionShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateVersion(null).isError());
    }

    @Test
    public void allSpacesAsVersionValueShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateVersion(ALL_SPACES).isError());
    }

    @Test
    public void nonIntegerAsVersionValueShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateVersion("abc").isError()); //$NON-NLS-1$
    }

    @Test
    public void negativeIntegerAsVersionValueShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateVersion("-5").isError()); //$NON-NLS-1$
    }

    @Test
    public void positiveIntegerAsVersionValueShouldBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateVersion("5").isOk()); //$NON-NLS-1$
    }

    @Test
    public void zeroAsVersionValueShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateVersion("0").isError()); //$NON-NLS-1$
    }

    @Test
    public void allSpacesAsTranslationTextShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateTranslationText(ALL_SPACES).isError());
    }

    @Test
    public void emptyTranslationTextShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateTranslationText(CoreStringUtil.Constants.EMPTY_STRING).isError());
    }

    @Test
    public void nullTranslationTextShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateTranslationText(null).isError());
    }

    @Test
    public void nullTranslationTLocaleShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validateTranslationLocale(null).isError());
    }

    @Test
    public void emptyPropertyFixedValueShouldBeValidWhenNoRuntimeType() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyFixedValue(null, CoreStringUtil.Constants.EMPTY_STRING, null)
                                                    .isOk());
    }

    @Test
    public void emptyPropertyFixedValueShouldNotBeValidIfThereIsARuntimeType() {
        assertFalse(ModelExtensionDefinitionValidator.validatePropertyFixedValue(Type.STRING.toString(),
                                                                                 CoreStringUtil.Constants.EMPTY_STRING, null)
                                                     .isOk());
    }

    @Test
    public void nullPropertyFixedValueShouldBeValidWhenNoRuntimeType() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyFixedValue(null, null, null).isOk());
    }

    @Test
    public void nullPropertyFixedValueShouldNotBeValidIfThereIsARuntimeType() {
        assertFalse(ModelExtensionDefinitionValidator.validatePropertyFixedValue(Type.STRING.toString(), null, null).isOk());
    }

    @Test
    public void emptyPropertyDefaultValueShouldBeValidWhenThereIsNoRuntimeType() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyDefaultValue(null, CoreStringUtil.Constants.EMPTY_STRING, null)
                                                    .isOk());
    }

    @Test
    public void emptyPropertyDefaultValueShouldNotBeValidWhenThereIsARuntimeType() {
        assertFalse(ModelExtensionDefinitionValidator.validatePropertyDefaultValue(Type.STRING.toString(),
                                                                                   CoreStringUtil.Constants.EMPTY_STRING, null)
                                                     .isOk());
    }

    @Test
    public void nullPropertyDefaultValueShouldBeValidThereIsNoRuntimeType() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyDefaultValue(null, null, null).isOk());
    }

    @Test
    public void emptyPropertySimpleIdShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertySimpleId(CoreStringUtil.Constants.EMPTY_STRING, null)
                                                    .isError());
    }

    @Test
    public void nullPropertySimpleIdShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertySimpleId(null, null).isError());
    }

    @Test
    public void allSpacesAsPropertySimpleIdShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertySimpleId(ALL_SPACES, null).isError());
    }

    @Test
    public void interiorSpacesAsPropertySimpleIdShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertySimpleId(INTERIOR_SPACES, null).isError());
    }

    @Test
    public void specialCharactersAsPropertySimpleIdShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertySimpleId(SPECIAL_CHARS, null).isError());
    }

    @Test
    public void allSpacesAsRuntimeTypeShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyRuntimeType(ALL_SPACES).isError());
    }

    @Test
    public void emptyRuntimeTypeShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyRuntimeType(CoreStringUtil.Constants.EMPTY_STRING).isError());
    }

    @Test
    public void nullRuntimeTypeShouldNotBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyRuntimeType(null).isError());
    }

    @Test
    public void allEnumeratedRuntimeTypesShouldBeValid() {
        for (Type type : Type.values()) {
            assertTrue(ModelExtensionDefinitionValidator.validatePropertyRuntimeType(type.toString()).isOk());
        }
    }

    @Test
    public void propertyAdvancededAttributeShouldAlwaysBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyAdvancedAttribute(true).isOk());
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyAdvancedAttribute(false).isOk());
    }

    @Test
    public void propertyIndexedAttributeShouldAlwaysBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyIndexedAttribute(true).isOk());
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyIndexedAttribute(false).isOk());
    }

    @Test
    public void propertyMaskedAttributeShouldAlwaysBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyMaskedAttribute(true).isOk());
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyMaskedAttribute(false).isOk());
    }

    @Test
    public void propertyRequiredAttributeShouldAlwaysBeValid() {
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyRequiredAttribute(true).isOk());
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyRequiredAttribute(false).isOk());
    }

    @Test
    public void translationsWithDuplicatesShouldNotBeValid() {
        Collection<Translation> translations = Constants.Utils.getTranslationsWithDuplicates();
        assertTrue(ModelExtensionDefinitionValidator.validateTranslations(CoreStringUtil.Constants.EMPTY_STRING, translations,
                                                                          false).isError());
    }

    @Test
    public void allowedValuesWithDuplicatesShouldNotBeValid() {
        Collection<String> allowedValues = Constants.Utils.getStringAllowedValuesWithDuplicates();
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyAllowedValues(Type.STRING.toString(),
                                                                                   allowedValues.toArray(new String[allowedValues.size()]))
                                                    .isError());
    }

    @Test
    public void allowedValuesWithWrongTypeShouldNotBeValid() {
        Collection<String> allowedValues = Constants.Utils.getStringAllowedValues();
        assertTrue(ModelExtensionDefinitionValidator.validatePropertyAllowedValues(Type.INTEGER.toString(),
                                                                                   allowedValues.toArray(new String[allowedValues.size()]))
                                                    .isError());
    }

}
