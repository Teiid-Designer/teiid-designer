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
                    errorMsg = NLS.bind(Messages.valueContainsSpecialCharactersValidationMsg, propertyName);
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

    public static ValidationStatus validateDescription( String description ) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    public static ValidationStatus validateMetaclassName( String metaclassName ) {
        String errorMsg = emptyCheck(Messages.metaclassName, metaclassName);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            // check for invalid characters
            for (char c : metaclassName.toCharArray()) {
                if ((c != '.') && !Character.isJavaIdentifierPart(c)) {
                    errorMsg = Messages.metaclassNameHasInvalidCharactersValidationMsg;
                    break;
                }
            }
        }

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    /**
     * Makes sure there is at least one metaclass name and no duplicate names.
     * 
     * @param metaclassNames the collection of metaclass names in the model extension definition (can be <code>null</code>)
     * @param validateEachName indicates if each name should validate using {@link #validateMetaclassName(String)}
     * @return
     */
    public static ValidationStatus validateMetaclassNames( String[] metaclassNames,
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
                    ValidationStatus status = validateMetaclassName(metaclassName);

                    if (status.isError()) {
                        return status;
                    }
                }
            }
        }

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    public static ValidationStatus validateMetamodelUri( String metamodelUri,
                                                         Collection<String> extendableMetamodelUris ) {
        String errorMsg = uriCheck(Messages.metamodelUri, metamodelUri);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            if ((extendableMetamodelUris == null) || !extendableMetamodelUris.contains(metamodelUri)) {
                errorMsg = NLS.bind(Messages.metamodelUriNotExtendableValidationMsg, metamodelUri);
            }
        }

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    public static ValidationStatus validateModelType( String supportedModelType,
                                                      Set<String> validModelTypes ) {
        String errorMsg = emptyCheck(Messages.modelType, supportedModelType);

        // invalid to have a null or empty model type
        if (!CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.createErrorMessage(errorMsg);
        }

        // make sure a valid value
        if ((validModelTypes != null) && !validModelTypes.isEmpty() && validModelTypes.contains(supportedModelType)) {
            return ValidationStatus.OK_STATUS;
        }

        // not a valid model type
        return ValidationStatus.createErrorMessage(NLS.bind(Messages.invalidModelType, supportedModelType));
    }

    public static ValidationStatus validateModelTypes( Collection<String> supportedModelTypes,
                                                       Set<String> validModelTypes ) {
        // ok to have null or empty collection of model types
        if ((supportedModelTypes == null) || supportedModelTypes.isEmpty()) {
            return ValidationStatus.OK_STATUS;
        }

        // make sure each model type is valid
        for (String modelType : supportedModelTypes) {
            ValidationStatus status = validateModelType(modelType, validModelTypes);

            if (status.isError()) {
                return status;
            }
        }

        // make sure no duplicates
        if (new HashSet(supportedModelTypes).size() != supportedModelTypes.size()) {
            return ValidationStatus.createErrorMessage(Messages.duplicateModelType);
        }

        // all model types are valid
        return ValidationStatus.OK_STATUS;
    }

    public static ValidationStatus validateNamespacePrefix( String namespacePrefix,
                                                            Collection<String> existingNamespacePrefixes ) {
        String errorMsg = containsSpacesCheck(Messages.namespacePrefix, namespacePrefix);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = containsSpecialCharactersCheck(Messages.namespacePrefix, namespacePrefix);

            if (CoreStringUtil.isEmpty(errorMsg) && (existingNamespacePrefixes != null)
                    && existingNamespacePrefixes.contains(namespacePrefix)) {
                return ValidationStatus.createWarningMessage(NLS.bind(Messages.namespacePrefixExistsValidationMsg, namespacePrefix));
            }
        }

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    public static ValidationStatus validateNamespaceUri( String namespaceUri,
                                                         Collection<String> existingNamespaceUris ) {
        String errorMsg = uriCheck(Messages.namespaceUri, namespaceUri);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            if ((existingNamespaceUris != null) && existingNamespaceUris.contains(namespaceUri)) {
                return ValidationStatus.createWarningMessage(NLS.bind(Messages.namespaceUriExistsValidationMsg, namespaceUri));
            }
        }

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    public static ValidationStatus validatePropertyAdvancedAttribute( boolean proposedValue ) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    public static ValidationStatus validatePropertyAllowedValue( String runtimeType,
                                                                 String allowedValue ) {
        String errorMsg = emptyCheck(Messages.allowedValue, allowedValue);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            // if no runtime type and not empty assume valid
            if (!validatePropertyRuntimeType(runtimeType).isError()) {
                // make sure value is valid for type
                errorMsg = Utils.isValidValue(Utils.convertRuntimeType(runtimeType), allowedValue, true, null);
            }
        }

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    /**
     * @param runtimeType the runtime type (cannot be <code>null</code>)
     * @param allowedValues the allowed values (can be <code>null</code>)
     * @return <code>null</code> if all values are valid based on the runtime type
     */
    public static ValidationStatus validatePropertyAllowedValues( String runtimeType,
                                                                  String[] allowedValues ) {
        // valid not to have allowed values
        if (ArrayUtil.isNullOrEmpty(allowedValues)) {
            return ValidationStatus.OK_STATUS;
        }

        for (String allowedValue : allowedValues) {
            // need to get rid of first occurrence of allowedValue in order to see if there is a duplicate
            ValidationStatus status = validatePropertyAllowedValue(runtimeType, allowedValue);

            // value is not valid for type
            if (status.isError()) {
                return status;
            }

            // make sure there are no duplicates
            List<String> temp = new ArrayList<String>(Arrays.asList(allowedValues));
            temp.remove(allowedValue);

            for (Object value : temp) {
                if (value.equals(allowedValue)) {
                    return ValidationStatus.createErrorMessage(NLS.bind(Messages.duplicateAllowedValue, allowedValue));
                }
            }
        }

        // valid
        return ValidationStatus.OK_STATUS;
    }

    public static ValidationStatus validatePropertyDefaultValue( String runtimeType,
                                                                 String defaultValue,
                                                                 String[] allowedValues ) {
        // only validate if there is a runtime type
        ValidationStatus status = validatePropertyRuntimeType(runtimeType);

        if (status.isError()) {
            // allow any value if there is no runtime type
            return ValidationStatus.OK_STATUS;
        }

        // have a good runtime type
        String errorMsg = Utils.isValidValue(Utils.convertRuntimeType(runtimeType), defaultValue, true, allowedValues);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    @SuppressWarnings("unused")
    public static ValidationStatus validatePropertyDefinition( String simpleId,
                                                               String runtimeType,
                                                               boolean requiresDefaultValue,
                                                               String defaultValue,
                                                               boolean requiresFixedValue,
                                                               String fixedValue,
                                                               Collection<Translation> descriptions,
                                                               Collection<Translation> displayNames,
                                                               String[] allowedValues ) {
        // return the first error severity or the next most severity
        ValidationStatus status = null;
        ValidationStatus tempStatus = null;

        SIMPLE_ID: {
            status = validatePropertySimpleId(simpleId);

            if (status.isError()) {
                return status;
            }
        }

        RUNTIME_TYPE: {
            tempStatus = validatePropertyRuntimeType(runtimeType);

            if (tempStatus.isError()) {
                return tempStatus;
            }

            if (tempStatus.compareTo(status) < 0) {
                status = tempStatus;
            }
        }

        DEFAULT_VALUE: {
            if (requiresDefaultValue) {
                tempStatus = validatePropertyDefaultValue(runtimeType, defaultValue, allowedValues);

                if (tempStatus.isError()) {
                    return tempStatus;
                }

                if (tempStatus.compareTo(status) < 0) {
                    status = tempStatus;
                }
            }
        }

        FIXED_VALUE: {
            if (requiresFixedValue) {
                tempStatus = validatePropertyFixedValue(runtimeType, fixedValue, allowedValues);

                if (tempStatus.isError()) {
                    return tempStatus;
                }

                if (tempStatus.compareTo(status) < 0) {
                    status = tempStatus;
                }
            }
        }

        DESCRIPTIONS: {
            tempStatus = validateTranslations(Messages.propertyDescription, descriptions, true);

            if (tempStatus.isError()) {
                return tempStatus;
            }

            if (tempStatus.compareTo(status) < 0) {
                status = tempStatus;
            }
        }

        DISPLAY_NAMES: {
            tempStatus = validateTranslations(Messages.propertyDisplayName, displayNames, true);

            if (tempStatus.isError()) {
                return tempStatus;
            }

            if (tempStatus.compareTo(status) < 0) {
                status = tempStatus;
            }
        }

        ALLOWED_VALUES: {
            tempStatus = validatePropertyAllowedValues(runtimeType, allowedValues);

            if (tempStatus.isError()) {
                return tempStatus;
            }

            if (tempStatus.compareTo(status) < 0) {
                status = tempStatus;
            }
        }

        return status;
    }

    public static ValidationStatus
            validatePropertyDefinitions( Map<String, Collection<ModelExtensionPropertyDefinition>> medPropDefns ) {
        ValidationStatus status = null;

        for (String metaclassName : medPropDefns.keySet()) {
            status = validateMetaclassName(metaclassName);

            if (!status.isError()) {
                // make sure metaclass has at least one property
                Collection<ModelExtensionPropertyDefinition> props = medPropDefns.get(metaclassName);

                if ((props == null) || props.isEmpty()) {
                    return ValidationStatus.createErrorMessage(NLS.bind(Messages.extendedMetaclassHasNoPropertiesValidationMsg,
                                                                        metaclassName));
                }

                for (Collection<ModelExtensionPropertyDefinition> propDefns : medPropDefns.values()) {
                    Set<String> ids = new HashSet<String>();

                    for (ModelExtensionPropertyDefinition propDefn : propDefns) {
                        // check for duplicates
                        if (!ids.add(propDefn.getSimpleId())) {
                            return ValidationStatus.createErrorMessage(NLS.bind(Messages.duplicatePropertyIdValidatinMsg,
                                                                                propDefn.getSimpleId()));
                        }

                        status = validatePropertyDefinition(propDefn.getSimpleId(), propDefn.getRuntimeType(),
                                                            !CoreStringUtil.isEmpty(propDefn.getDefaultValue()),
                                                            propDefn.getDefaultValue(),
                                                            !CoreStringUtil.isEmpty(propDefn.getFixedValue()),
                                                            propDefn.getFixedValue(), propDefn.getDescriptions(),
                                                            propDefn.getDisplayNames(), propDefn.getAllowedValues());

                        if (status.isError()) {
                            return status;
                        }
                    }
                }
            }
        }

        return ValidationStatus.OK_STATUS;
    }

    public static ValidationStatus validatePropertyFixedValue( String runtimeType,
                                                               String fixedValue,
                                                               String[] allowedValues ) {
        // only validate if there is a runtime type
        ValidationStatus status = validatePropertyRuntimeType(runtimeType);

        if (status.isError()) {
            // allow any value if there is no runtime type
            return ValidationStatus.OK_STATUS;
        }

        // have a good runtime type
        String errorMsg = Utils.isValidValue(Utils.convertRuntimeType(runtimeType), fixedValue, true, allowedValues);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    public static ValidationStatus validatePropertyIndexedAttribute( boolean proposedValue ) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    public static ValidationStatus validatePropertyMaskedAttribute( boolean proposedValue ) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    public static ValidationStatus validatePropertyRequiredAttribute( boolean proposedValue ) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    public static ValidationStatus validatePropertyRuntimeType( String runtimeType ) {
        String errorMsg = null;

        try {
            ModelExtensionPropertyDefinition.Utils.convertRuntimeType(runtimeType);
        } catch (IllegalArgumentException e) {
            errorMsg = e.getLocalizedMessage();
        }

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    public static ValidationStatus validatePropertySimpleId( String proposedValue ) {
        String errorMsg = containsOnlyIdCharactersCheck(Messages.propertySimpleId, proposedValue);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    public static ValidationStatus validateTranslation( Locale locale,
                                                        String text ) {
        ValidationStatus status = validateTranslationLocale(locale);

        if (!status.isError()) {
            return validateTranslationText(text);
        }

        return status;
    }

    public static ValidationStatus validateTranslationLocale( Locale locale ) {
        if (locale == null) {
            return ValidationStatus.createErrorMessage(Messages.localeMissingValidationMsg);
        }

        return ValidationStatus.OK_STATUS;
    }

    public static ValidationStatus validateTranslations( String translationType,
                                                         Collection<Translation> translations,
                                                         boolean validateEachTranslation ) {
        if ((translations == null) || translations.isEmpty()) {
            return ValidationStatus.OK_STATUS;
        }

        String errorMsg = null;
        Set<Locale> locales = new HashSet<Locale>(translations.size());

        for (Translation translation : translations) {
            errorMsg = nullCheck(translationType, translation);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                break;
            }

            if (validateEachTranslation) {
                ValidationStatus status = validateTranslation(translation.getLocale(), translation.getTranslation());

                if (status.isError()) {
                    return status;
                }
            }

            locales.add(translation.getLocale());
        }

        // duplicates check
        if (CoreStringUtil.isEmpty(errorMsg)) {
            if (translations.size() != locales.size()) {
                return ValidationStatus.createErrorMessage(NLS.bind(Messages.duplicateTranslationLocaleValidationMsg,
                                                                    translationType));
            }
        }

        return ValidationStatus.OK_STATUS;
    }

    public static ValidationStatus validateTranslationText( String text ) {
        String errorMsg = emptyCheck(Messages.translation, text);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            // see if only spaces
            errorMsg = emptyCheck(Messages.translation, text.trim());
        }

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    public static ValidationStatus validateVersion( String version ) {
        String errorMsg = containsSpacesCheck(Messages.version, version);

        if (!CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.createErrorMessage(errorMsg);
        }

        int newVersion = -1;

        try {
            newVersion = Integer.parseInt(version);
        } catch (Exception e) {
            return ValidationStatus.createErrorMessage(NLS.bind(Messages.versionIsNotAnIntegerValidationMsg, version));
        }

        if (newVersion < ModelExtensionDefinitionHeader.DEFAULT_VERSION) {
            return ValidationStatus.createErrorMessage(NLS.bind(Messages.versionLessThanDefaultValidationMsg,
                                                                ModelExtensionDefinitionHeader.DEFAULT_VERSION));
        }

        // good value
        return ValidationStatus.OK_STATUS;
    }

    /**
     * Don't allow construction.
     */
    private ModelExtensionDefinitionValidator() {
        // nothing to do
    }

}
