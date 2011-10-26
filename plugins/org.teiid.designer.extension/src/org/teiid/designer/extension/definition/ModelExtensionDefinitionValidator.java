/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.Utils;
import org.teiid.designer.extension.properties.Translation;

import com.metamatrix.core.util.ArrayUtil;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * Validates the properties of a {@link ModelExtensionDefinition}.
 */
public final class ModelExtensionDefinitionValidator {

    private static String containsOnlyIdCharactersCheck( String propertyName,
                                                         String value ) {
        String errorMsg = emptyCheck(propertyName, value);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            for (char c : value.toCharArray()) {
                if (!Character.isLetterOrDigit(c) && (c != '_') && (c != '-')) {
                    errorMsg = NLS.bind(Messages.invalidPropertyId, value);
                    break;
                }
            }
        }

        return errorMsg;
    }

    private static String containsSpecialCharactersCheck( String propertyName,
                                                          String value ) {
        String errorMsg = emptyCheck(propertyName, value);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            for (char c : value.toCharArray()) {
                if (!Character.isLetterOrDigit(c)) {
                    errorMsg = NLS.bind(Messages.valueContainsSpacesValidationMsg, propertyName);
                }
            }
        }

        return errorMsg;
    }

    private static String containsSpacesCheck( String propertyName,
                                               String value ) {
        String errorMsg = emptyCheck(propertyName, value);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            if (value.contains(CoreStringUtil.Constants.SPACE)) {
                errorMsg = NLS.bind(Messages.valueContainsSpacesValidationMsg, propertyName);
            }
        }

        return errorMsg;
    }

    private static String emptyCheck( String propertyName,
                                      String value ) {
        if (CoreStringUtil.isEmpty(value)) {
            return NLS.bind(Messages.propertyIsEmptyValidationMsg, propertyName);
        }

        // value is valid
        return null;
    }

    private static String nullCheck( String name,
                                     Object object ) {
        if (object == null) {
            return NLS.bind(Messages.objectIsNullValidationMsg, name);
        }

        // value is valid
        return null;
    }

    private static String uriCheck( String propertyName,
                                    String value ) {
        String errorMsg = containsSpacesCheck(propertyName, value);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            try {
                URI.create(value);
            } catch (Exception e) {
                errorMsg = NLS.bind(Messages.uriInvalidValidationMsg, propertyName);
            }
        }

        return errorMsg;
    }

    public static String validateDescription( String description ) {
        // any value is valid
        return null;
    }

    public static String validateMetaclassName( String metaclassName ) {
        String errorMsg = emptyCheck(Messages.metaclassName, metaclassName);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            // check for invalid characters
            for (char c : metaclassName.toCharArray()) {
                if ((c != '.') && !Character.isJavaIdentifierPart(c)) {
                    errorMsg = Messages.metaclassNameHasInvalidCharactersValidationMsg;
                }
            }
        }

        return errorMsg;
    }

    /**
     * Makes sure there is at least one metaclass name and no duplicate names.
     * 
     * @param metaclassNames the collection of metaclass names in the model extension definition (can be <code>null</code>)
     * @param validateEachName indicates if each name should validate using {@link #validateMetaclassName(String)}
     * @return
     */
    public static String validateMetaclassNames( String[] metaclassNames,
                                                 boolean validateEachName ) {
        String errorMsg = null;

        if ((metaclassNames == null) || (metaclassNames.length == 0)) {
            errorMsg = Messages.medHasNoMetaclassesValidationMsg;
        } else {
            // make sure no duplicates
            if (metaclassNames.length != new HashSet<String>(Arrays.asList(metaclassNames)).size()) {
                errorMsg = Messages.medHasDuplicateMetaclassesValidationMsg;
            }

            if (validateEachName) {
                for (String metaclassName : metaclassNames) {
                    errorMsg = validateMetaclassName(metaclassName);

                    if (!CoreStringUtil.isEmpty(errorMsg)) {
                        break;
                    }
                }
            }
        }

        return errorMsg;
    }

    public static String validateMetamodelUri( String metamodelUri,
                                               Collection<String> extendableMetamodelUris ) {
        String errorMsg = uriCheck(Messages.metamodelUri, metamodelUri);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            if ((extendableMetamodelUris == null) || !extendableMetamodelUris.contains(metamodelUri)) {
                errorMsg = NLS.bind(Messages.metamodelUriNotExtendableValidationMsg, metamodelUri);
            }
        }

        return errorMsg;
    }

    public static String validateNamespacePrefix( String namespacePrefix,
                                                  Collection<String> existingNamespacePrefixes ) {
        String errorMsg = containsSpacesCheck(Messages.namespacePrefix, namespacePrefix);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = containsSpecialCharactersCheck(Messages.namespacePrefix, namespacePrefix);

            if (CoreStringUtil.isEmpty(errorMsg) && (existingNamespacePrefixes != null)
                    && existingNamespacePrefixes.contains(namespacePrefix)) {
                errorMsg = NLS.bind(Messages.namespacePrefixExistsValidationMsg, namespacePrefix);
            }
        }

        return errorMsg;
    }

    public static String validateNamespaceUri( String namespaceUri,
                                               Collection<String> existingNamespaceUris ) {
        String errorMsg = uriCheck(Messages.namespaceUri, namespaceUri);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            if ((existingNamespaceUris != null) && existingNamespaceUris.contains(namespaceUri)) {
                errorMsg = NLS.bind(Messages.namespaceUriExistsValidationMsg, namespaceUri);
            }
        }

        return errorMsg;
    }

    public static String validatePropertyAdvancedAttribute( boolean proposedValue ) {
        return null; // always valid
    }

    public static String validatePropertyAllowedValue( String runtimeType,
                                                       String allowedValue ) {
        String errorMsg = emptyCheck(Messages.allowedValue, allowedValue);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            // if no runtime type and not empty assume valid
            if (CoreStringUtil.isEmpty(validatePropertyRuntimeType(runtimeType))) {
                // make sure value is valid for type
                errorMsg = Utils.isValidValue(Utils.convertRuntimeType(runtimeType), allowedValue, true, null);
            }
        }

        return errorMsg;
    }

    /**
     * @param runtimeType the runtime type (cannot be <code>null</code>)
     * @param allowedValues the allowed values (can be <code>null</code>)
     * @return <code>null</code> if all values are valid based on the runtime type
     */
    public static String validatePropertyAllowedValues( String runtimeType,
                                                        String[] allowedValues ) {
        // valid not to have allowed values
        if (ArrayUtil.isNullOrEmpty(allowedValues)) {
            return null;
        }

        for (String allowedValue : allowedValues) {
            // need to get rid of first occurrence of allowedValue in order to see if there is a duplicate
            String errorMsg = validatePropertyAllowedValue(runtimeType, allowedValue);

            // value is not valid for type
            if (!CoreStringUtil.isEmpty(errorMsg)) {
                return errorMsg;
            }

            // make sure there are no duplicates
            List<String> temp = new ArrayList<String>(Arrays.asList(allowedValues));
            temp.remove(allowedValue);

            for (Object value : temp) {
                if (value.equals(allowedValue)) {
                    return NLS.bind(Messages.duplicateAllowedValue, allowedValue);
                }
            }
        }

        // valid
        return null;
    }

    public static String validatePropertyDefaultValue( String runtimeType,
                                                       String defaultValue,
                                                       String[] allowedValues ) {
        // only validate if there is a runtime type
        if (CoreStringUtil.isEmpty(validatePropertyRuntimeType(runtimeType))) {
            return Utils.isValidValue(Utils.convertRuntimeType(runtimeType), defaultValue, true, allowedValues);
        }

        return null;
    }

    public static String validatePropertyDefinition( String namespacePrefix,
                                                     String id,
                                                     String runtimeType,
                                                     String defaultValue,
                                                     String fixedValue,
                                                     Collection<Translation> descriptions,
                                                     Collection<Translation> displayNames,
                                                     String[] allowedValues ) {
        String errorMsg = validateNamespacePrefix(namespacePrefix, Collections.<String> emptyList());

        if (!CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = validatePropertyRuntimeType(runtimeType);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                errorMsg = validatePropertyDefaultValue(runtimeType, defaultValue, allowedValues);

                if (!CoreStringUtil.isEmpty(errorMsg)) {
                    errorMsg = validatePropertyFixedValue(runtimeType, fixedValue);

                    if (!CoreStringUtil.isEmpty(errorMsg)) {
                        errorMsg = validateTranslations(Messages.propertyDescription, descriptions, true);

                        if (!CoreStringUtil.isEmpty(errorMsg)) {
                            errorMsg = validateTranslations(Messages.propertyDisplayName, displayNames, true);

                            if (!CoreStringUtil.isEmpty(errorMsg)) {
                                errorMsg = validatePropertyAllowedValues(runtimeType, allowedValues);
                            }
                        }
                    }
                }
            }
        }

        return errorMsg;
    }

    public static String validatePropertyDefinitions( Map<String, Collection<ModelExtensionPropertyDefinition>> medPropDefns ) {
        String errorMsg = null;

        MED_PROP_DEFNS: {
            for (String metaclassName : medPropDefns.keySet()) {
                errorMsg = validateMetaclassName(metaclassName);

                if (CoreStringUtil.isEmpty(errorMsg)) {
                    // make sure metaclass has at least one property
                    Collection<ModelExtensionPropertyDefinition> props = medPropDefns.get(metaclassName);

                    if ((props == null) || props.isEmpty()) {
                        errorMsg = NLS.bind(Messages.extendedMetaclassHasNoPropertiesValidationMsg, metaclassName);
                        break MED_PROP_DEFNS;
                    }

                    for (Collection<ModelExtensionPropertyDefinition> propDefns : medPropDefns.values()) {
                        Set<String> ids = new HashSet<String>();

                        for (ModelExtensionPropertyDefinition propDefn : propDefns) {
                            // check for duplicates
                            if (!ids.add(propDefn.getSimpleId())) {
                                errorMsg = NLS.bind(Messages.duplicatePropertyIdValidatinMsg, propDefn.getSimpleId());
                                break MED_PROP_DEFNS;
                            }

                            errorMsg = validatePropertyDefinition(propDefn.getNamespacePrefix(), propDefn.getSimpleId(),
                                                                  propDefn.getRuntimeType(), propDefn.getDefaultValue(),
                                                                  propDefn.getFixedValue(), propDefn.getDescriptions(),
                                                                  propDefn.getDisplayNames(), propDefn.getAllowedValues());

                            if (!CoreStringUtil.isEmpty(errorMsg)) {
                                break MED_PROP_DEFNS;
                            }
                        }
                    }
                }
            }
        }

        return errorMsg;
    }

    public static String validatePropertyFixedValue( String runtimeType,
                                                     String fixedValue ) {
        // only validate if there is a runtime type
        if (CoreStringUtil.isEmpty(validatePropertyRuntimeType(runtimeType))) {
            return Utils.isValidValue(Utils.convertRuntimeType(runtimeType), fixedValue, true, null);
        }

        return null;
    }

    public static String validatePropertyIndexedAttribute( boolean proposedValue ) {
        return null; // always valid
    }

    public static String validatePropertyMaskedAttribute( boolean proposedValue ) {
        return null; // always valid
    }

    public static String validatePropertyRequiredAttribute( boolean proposedValue ) {
        return null; // always valid
    }

    public static String validatePropertyRuntimeType( String runtimeType ) {
        String errorMsg = null;

        try {
            ModelExtensionPropertyDefinition.Utils.convertRuntimeType(runtimeType);
        } catch (IllegalArgumentException e) {
            errorMsg = e.getLocalizedMessage();
        }

        return errorMsg;
    }

    public static String validatePropertySimpleId( String proposedValue,
                                                   Collection<String> existingPropIds ) {
        String errorMsg = containsOnlyIdCharactersCheck(Messages.propertySimpleId, proposedValue);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            if ((existingPropIds != null) && existingPropIds.contains(proposedValue)) {
                errorMsg = NLS.bind(Messages.duplicatePropertyIdValidatinMsg, proposedValue);
            }
        }

        return errorMsg;
    }

    public static String validateTranslation( Locale locale,
                                              String text ) {
        String errorMsg = validateTranslationLocale(locale);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = validateTranslationText(text);
        }

        return errorMsg;
    }

    public static String validateTranslationLocale( Locale locale ) {
        if (locale == null) {
            return Messages.localeMissingValidationMsg;
        }

        return null;
    }

    public static String validateTranslations( String translationType,
                                               Collection<Translation> translations,
                                               boolean validateEachTranslation ) {
        if ((translations == null) || translations.isEmpty()) {
            return null;
        }

        String errorMsg = null;
        Set<Locale> locales = new HashSet<Locale>(translations.size());

        for (Translation translation : translations) {
            errorMsg = nullCheck(translationType, translation);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                break;
            }

            if (validateEachTranslation) {
                errorMsg = validateTranslation(translation.getLocale(), translation.getTranslation());

                if (!CoreStringUtil.isEmpty(errorMsg)) {
                    break;
                }
            }

            locales.add(translation.getLocale());
        }

        // duplicates check
        if (CoreStringUtil.isEmpty(errorMsg)) {
            if (translations.size() != locales.size()) {
                errorMsg = NLS.bind(Messages.duplicateTranslationLocaleValidationMsg, translationType);
            }
        }

        return errorMsg;
    }

    public static String validateTranslationText( String text ) {
        String errorMsg = emptyCheck(Messages.translation, text);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            // see if only spaces
            errorMsg = emptyCheck(Messages.translation, text.trim());
        }

        return errorMsg;
    }

    public static String validateVersion( String version ) {
        String msg = containsSpacesCheck(Messages.version, version);

        if (!CoreStringUtil.isEmpty(msg)) {
            return msg;
        }

        int newVersion = -1;

        try {
            newVersion = Integer.parseInt(version);
        } catch (Exception e) {
            return NLS.bind(Messages.versionIsNotAnIntegerValidationMsg, version);
        }

        if (newVersion < ModelExtensionDefinitionHeader.DEFAULT_VERSION) {
            return NLS.bind(Messages.versionLessThanDefaultValidationMsg, ModelExtensionDefinitionHeader.DEFAULT_VERSION);
        }

        // good value
        return null;
    }

    /**
     * Don't allow construction.
     */
    private ModelExtensionDefinitionValidator() {
        // nothing to do
    }

}
