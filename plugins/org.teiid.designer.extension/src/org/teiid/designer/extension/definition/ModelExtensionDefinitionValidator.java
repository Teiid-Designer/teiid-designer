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
import org.teiid.core.designer.util.ArrayUtil;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.Utils;
import org.teiid.designer.extension.properties.Translation;


/**
 * Validates the properties of a {@link ModelExtensionDefinition}.
 *
 * @since 8.0
 */
public final class ModelExtensionDefinitionValidator {

    private static MedStatus addStatus(final MedStatus currentStatus,
                                       final MedStatus statusToAdd) {
        if (statusToAdd.isOk()) {
            return currentStatus;
        }

        MultiValidationStatus multi = null;

        if (currentStatus.isMulti()) {
            multi = (MultiValidationStatus)currentStatus;
        } else {
            multi = MultiValidationStatus.create(currentStatus);
        }

        multi.add(statusToAdd);
        return multi;
    }

    private static String containsOnlyIdCharactersCheck( final String propertyName,
                                                         final String value ) {
        if (!CoreStringUtil.isEmpty(value)) {
            for (char c : value.toCharArray()) {
                if (!Character.isLetterOrDigit(c) && (c != '_') && (c != '-')) {
                    return NLS.bind(Messages.invalidPropertyId, value);
                }
            }
        }

        return null;
    }

    private static String containsSpecialCharactersCheck(final String propertyName,
                                                         final String value) {
        if (!CoreStringUtil.isEmpty(value)) {
            for (char c : value.toCharArray()) {
                if (!Character.isLetterOrDigit(c)) {
                    return NLS.bind(Messages.valueContainsSpecialCharactersValidationMsg, propertyName);
                }
            }
        }

        return null;
    }

    private static String containsSpacesCheck(final String propertyName,
                                              final String value) {
        if (!CoreStringUtil.isEmpty(value) && value.contains(CoreStringUtil.Constants.SPACE)) {
            return NLS.bind(Messages.valueContainsSpacesValidationMsg, propertyName);
        }

        return null;
    }

    private static String emptyCheck( final String propertyName,
                                      final String value ) {
        if (CoreStringUtil.isEmpty(value)) {
            return NLS.bind(Messages.propertyIsEmptyValidationMsg, propertyName);
        }

        // value is valid
        return null;
    }

    private static String nullCheck(final String name,
                                    final Object object) {
        if (object == null) {
            return NLS.bind(Messages.objectIsNullValidationMsg, name);
        }

        // value is valid
        return null;
    }

    private static String uriCheck(final String propertyName,
                                   final String value) {
        String errorMsg = emptyCheck(propertyName, value);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = containsSpacesCheck(propertyName, value);

            if (CoreStringUtil.isEmpty(errorMsg)) {
                try {
                    URI.create(value);
                } catch (Exception e) {
                    errorMsg = NLS.bind(Messages.uriInvalidValidationMsg, propertyName);
                }
            }
        }

        return errorMsg;
    }

    /**
     * @param description the description being validated (can be <code>null</code> or empty)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateDescription(final String description) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    /**
     * @param med the MED being validated (cannot be <code>null</code>)
     * @param existingNamespacePrefixes the namespace prefixes defined in the MED (can be <code>null</code>)
     * @param existingNamespaceUris the namespace URIs defined in the MED (can be <code>null</code>) 
     * @param extendableMetamodelUris the valid metamodel URIs that can be extended (can be <code>null</code>)
     * @param validModelTypes the valid model types (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateMed(final ModelExtensionDefinition med,
                                        final Collection<String> existingNamespacePrefixes,
                                        final Collection<String> existingNamespaceUris,
                                        final Collection<String> extendableMetamodelUris,
                                        final Set<String> validModelTypes) {

    	boolean builtIn = med.isBuiltIn();
    	if(!builtIn) {
    		MedStatus status = validateMedHeader(med.getHeader(),
    				existingNamespacePrefixes,
    				existingNamespaceUris,
    				extendableMetamodelUris,
    				validModelTypes);
    		status = addStatus(status, validateMetaclassNames(med.getExtendedMetaclasses(), true));
    		return addStatus(status, validatePropertyDefinitions(med.getPropertyDefinitions()));
    	} else {
    		return ValidationStatus.OK_STATUS;
    	}
    }

    /**
     * @param header the MED header being validated (cannot be <code>null</code>)
     * @param existingNamespacePrefixes the namespace prefixes defined in the MED (can be <code>null</code>)
     * @param existingNamespaceUris the namespace URIs defined in the MED (can be <code>null</code>) 
     * @param extendableMetamodelUris the valid metamodel URIs that can be extended (can be <code>null</code>)
     * @param validModelTypes the valid model types (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateMedHeader(final ModelExtensionDefinitionHeader header,
                                              final Collection<String> existingNamespacePrefixes,
                                              final Collection<String> existingNamespaceUris,
                                              final Collection<String> extendableMetamodelUris,
                                              final Set<String> validModelTypes) {
        MedStatus status = validateNamespacePrefix(header.getNamespacePrefix(), existingNamespacePrefixes);
        status = addStatus(status, validateNamespaceUri(header.getNamespaceUri(), existingNamespaceUris));
        status = addStatus(status, validateMetamodelUri(header.getMetamodelUri(), extendableMetamodelUris));
        status = addStatus(status, validateModelTypes(header.getSupportedModelTypes(), validModelTypes));
        status = addStatus(status, validateDescription(header.getDescription()));
        return addStatus(status, validateVersion(Integer.toString(header.getVersion())));
    }

    /**
     * @param metaclassName the metaclass name being validated (can be <code>null</code> or empty)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateMetaclassName(final String metaclassName) {
        String errorMsg = emptyCheck(Messages.metaclassName, metaclassName);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            // check for invalid characters
            for (final char c : metaclassName.toCharArray()) {
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
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateMetaclassNames(final String[] metaclassNames,
                                                   final boolean validateEachName) {
        if ((metaclassNames == null) || (metaclassNames.length == 0)) {
            return ValidationStatus.createErrorMessage(Messages.medHasNoMetaclassesValidationMsg);
        }

        MedStatus status = ValidationStatus.OK_STATUS;

        // make sure no duplicates
        if (metaclassNames.length != new HashSet<String>(Arrays.asList(metaclassNames)).size()) {
            status = MultiValidationStatus.create(ValidationStatus.createErrorMessage(Messages.medHasDuplicateMetaclassesValidationMsg));
        }

        if (validateEachName) {
            for (final String metaclassName : metaclassNames) {
                status = addStatus(status, validateMetaclassName(metaclassName));
            }
        }

        return status;
    }

    /**
     * @param metamodelUri the URI being validated (can be <code>null</code> or empty)
     * @param extendableMetamodelUris the allowed URIs that can be extended (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateMetamodelUri(final String metamodelUri,
                                                 final Collection<String> extendableMetamodelUris) {
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

    /**
     * @param supportedModelType the model type being checked (can be <code>null</code> or empty)
     * @param validModelTypes the valid model types (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateModelType(final String supportedModelType,
                                              final Set<String> validModelTypes) {
        { // empty check
            final String errorMsg = emptyCheck(Messages.modelType, supportedModelType);

            // invalid to have a null or empty model type
            if (!CoreStringUtil.isEmpty(errorMsg)) {
                return ValidationStatus.createErrorMessage(errorMsg);
            }
        }

        // make sure a valid value
        if ((validModelTypes != null) && !validModelTypes.isEmpty() && validModelTypes.contains(supportedModelType)) {
            return ValidationStatus.OK_STATUS;
        }

        // not a valid model type
        return ValidationStatus.createErrorMessage(NLS.bind(Messages.invalidModelType, supportedModelType));
    }

    /**
     * @param supportedModelTypes the model types being checked (can be <code>null</code>)
     * @param validModelTypes the valid model types (can be <code>null</code> if )
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateModelTypes(final Collection<String> supportedModelTypes,
                                               final Set<String> validModelTypes) {
        // ok to have null or empty collection of model types
        if ((supportedModelTypes == null) || supportedModelTypes.isEmpty()) {
            return ValidationStatus.OK_STATUS;
        }

        MedStatus status = ValidationStatus.OK_STATUS;

        // make sure each model type is valid
        for (final String modelType : supportedModelTypes) {
            status = addStatus(status, validateModelType(modelType, validModelTypes));
        }

        // make sure no duplicates
        if (new HashSet(supportedModelTypes).size() != supportedModelTypes.size()) {
            status = addStatus(status, ValidationStatus.createErrorMessage(Messages.duplicateModelType));
        }

        return status;
    }

    /**
     * @param namespacePrefix the namespace prefix being checked (can be <code>null</code> or empty)
     * @param existingNamespacePrefixes the namespace prefixes defined in the MED (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateNamespacePrefix(final String namespacePrefix,
                                                    final Collection<String> existingNamespacePrefixes) {
        { // empty check
            final String errorMsg = emptyCheck(Messages.namespacePrefix, namespacePrefix);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                return ValidationStatus.createErrorMessage(errorMsg);
            }
        }

        MedStatus status = ValidationStatus.OK_STATUS;

        { // spaces check
            final String errorMsg = containsSpacesCheck(Messages.namespacePrefix, namespacePrefix);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                status = addStatus(status, ValidationStatus.createErrorMessage(errorMsg));
            }
        }

        { // special char check
            final String errorMsg = containsSpecialCharactersCheck(Messages.namespacePrefix, namespacePrefix);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                status = addStatus(status, ValidationStatus.createErrorMessage(errorMsg));
            }
        }

        { // exists check
            if ((existingNamespacePrefixes != null) && existingNamespacePrefixes.contains(namespacePrefix)) {
                status = addStatus(status,
                                   ValidationStatus.createWarningMessage(NLS.bind(Messages.namespacePrefixExistsValidationMsg,
                                                                                  namespacePrefix)));
            }
        }

        return status;
    }

    /**
     * @param namespaceUri the namespace URI being checked (can be <code>null</code> or empty)
     * @param existingNamespaceUris the namespace URIs defined in the MED (can be <code>null</code>) 
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateNamespaceUri(final String namespaceUri,
                                                 final Collection<String> existingNamespaceUris) {
        MedStatus status = ValidationStatus.OK_STATUS;
        final String errorMsg = uriCheck(Messages.namespaceUri, namespaceUri);

        if (!CoreStringUtil.isEmpty(errorMsg)) {
            status = addStatus(status, ValidationStatus.createErrorMessage(errorMsg));
        }

        if ((existingNamespaceUris != null) && existingNamespaceUris.contains(namespaceUri)) {
            status = addStatus(status, ValidationStatus.createWarningMessage(NLS.bind(Messages.namespaceUriExistsValidationMsg,
                                                                                      namespaceUri)));
        }

        return status;
    }

    /**
     * @param proposedValue the value of the advanced attribute
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyAdvancedAttribute(final boolean proposedValue) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    /**
     * @param runtimeType the runtime type of the property (can be <code>null</code> or empty)
     * @param allowedValue the value being checked (can be <code>null</code> or empty)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyAllowedValue(final String runtimeType,
                                                         final String allowedValue) {
        { // empty check
            final String errorMsg = emptyCheck(Messages.allowedValue, allowedValue);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                return ValidationStatus.createErrorMessage(errorMsg);
            }
        }

        // when non-empty and no or invalid runtime type assume valid
        if (CoreStringUtil.isEmpty(runtimeType) || validatePropertyRuntimeType(runtimeType).isError()) {
            return ValidationStatus.OK_STATUS;
        }

        // make sure value is valid for type
        final String errorMsg = Utils.isValidValue(Messages.propertyAllowedValue,
                                                   Utils.convertRuntimeType(runtimeType),
                                                   allowedValue,
                                                   true,
                                                   null);

        if (!CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.createErrorMessage(errorMsg);
        }

        return ValidationStatus.OK_STATUS;
    }

    /**
     * @param runtimeType the runtime type (can be <code>null</code> or empty)
     * @param allowedValues the allowed values (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyAllowedValues(final String runtimeType,
                                                          final String[] allowedValues) {
        // valid not to have allowed values
        if (ArrayUtil.isNullOrEmpty(allowedValues)) {
            return ValidationStatus.OK_STATUS;
        }

        MedStatus status = ValidationStatus.OK_STATUS;

        for (final String allowedValue : allowedValues) {
            status = addStatus(status, validatePropertyAllowedValue(runtimeType, allowedValue));

            // need to get rid of first occurrence of allowedValue in order to see if there is a duplicate
            final List<String> temp = new ArrayList<String>(Arrays.asList(allowedValues));
            temp.remove(allowedValue);

            for (final Object value : temp) {
                if (value.equals(allowedValue)) {
                    status = addStatus(status,
                                       ValidationStatus.createErrorMessage(NLS.bind(Messages.duplicateAllowedValue, allowedValue)));
                }
            }
        }

        return status;
    }

    /**
     * @param runtimeType the runtime type (can be <code>null</code> or empty)
     * @param defaultValue the value being checked (can be <code>null</code> or empty)
     * @param allowedValues the allowed values (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyDefaultValue(final String runtimeType,
                                                         final String defaultValue,
                                                         final String[] allowedValues) {
        // only validate if there is a valid runtime type
        if (validatePropertyRuntimeType(runtimeType).isError()) {
            return ValidationStatus.OK_STATUS;
        }

        // have a good runtime type
        final String errorMsg = Utils.isValidValue(Messages.propertyDefaultValue,
                                                   Utils.convertRuntimeType(runtimeType),
                                                   defaultValue,
                                                   true,
                                                   allowedValues);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    /**
     * @param simpleId the unqualified property name (can be <code>null</code> or empty)
     * @param runtimeType the property type (can be <code>null</code> or empty)
     * @param requiresDefaultValue <code>true</code> if the property requires a default value
     * @param defaultValue the default value (can be <code>null</code> or empty)
     * @param requiresFixedValue <code>true</code> if the property requires a fixed value
     * @param fixedValue the fixed value (can be <code>null</code> or empty)
     * @param descriptions the property description translations (can be <code>null</code>)
     * @param displayNames the property display name translations (can be <code>null</code>)
     * @param allowedValues the property allowed values (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyDefinition(final String simpleId,
                                                       final String runtimeType,
                                                       final boolean requiresDefaultValue,
                                                       final String defaultValue,
                                                       final boolean requiresFixedValue,
                                                       final String fixedValue,
                                                       final Collection<Translation> descriptions,
                                                       final Collection<Translation> displayNames,
                                                       final String[] allowedValues) {
        MedStatus status = ValidationStatus.OK_STATUS;
        status = addStatus(status, validatePropertySimpleId(simpleId));
        status = addStatus(status, validatePropertyRuntimeType(runtimeType));

        if (requiresDefaultValue) {
            status = addStatus(status, validatePropertyDefaultValue(runtimeType, defaultValue, allowedValues));
        }

        if (requiresFixedValue) {
            status = addStatus(status, validatePropertyFixedValue(runtimeType, fixedValue, allowedValues));
        }

        status = addStatus(status, validateTranslations(Messages.propertyDescription, descriptions, true));
        status = addStatus(status, validateTranslations(Messages.propertyDisplayName, displayNames, true));
        status = addStatus(status, validatePropertyAllowedValues(runtimeType, allowedValues));

        return status;
    }

    /**
     * @param medPropDefns the property definitions being validated (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyDefinitions(final Map<String, Collection<ModelExtensionPropertyDefinition>> medPropDefns) {
        MedStatus status = ValidationStatus.OK_STATUS;

        for (final String metaclassName : medPropDefns.keySet()) {
            // only validate if there is a good metaclass
            if (validateMetaclassName(metaclassName).isError()) {
                continue;
            }

            // make sure metaclass has at least one property
            final Collection<ModelExtensionPropertyDefinition> props = medPropDefns.get(metaclassName);

            if ((props == null) || props.isEmpty()) {
                status = addStatus(status,
                                   ValidationStatus.createErrorMessage(NLS.bind(Messages.extendedMetaclassHasNoPropertiesValidationMsg,
                                                                                metaclassName)));
            } else {
                for (final Collection<ModelExtensionPropertyDefinition> propDefns : medPropDefns.values()) {
                    final Set<String> ids = new HashSet<String>();

                    for (final ModelExtensionPropertyDefinition propDefn : propDefns) {
                        // check for duplicates
                        if (!ids.add(propDefn.getSimpleId())) {
                            status = addStatus(status,
                                               ValidationStatus.createErrorMessage(NLS.bind(Messages.duplicatePropertyIdValidatinMsg,
                                                                                            propDefn.getSimpleId())));
                        }

                        status = addStatus(status,
                                           validatePropertyDefinition(propDefn.getSimpleId(),
                                                                      propDefn.getRuntimeType(),
                                                                      !CoreStringUtil.isEmpty(propDefn.getDefaultValue()),
                                                                      propDefn.getDefaultValue(),
                                                                      !CoreStringUtil.isEmpty(propDefn.getFixedValue()),
                                                                      propDefn.getFixedValue(),
                                                                      propDefn.getDescriptions(),
                                                                      propDefn.getDisplayNames(),
                                                                      propDefn.getAllowedValues()));
                    }
                }
            }
        }

        return status;
    }

    /**
     * @param runtimeType the property type (can be <code>null</code> or empty)
     * @param fixedValue the value being checked (can be <code>null</code> or empty)
     * @param allowedValues the allowed values (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyFixedValue(final String runtimeType,
                                                       final String fixedValue,
                                                       final String[] allowedValues) {
        // only validate if there is a runtime type
        if (validatePropertyRuntimeType(runtimeType).isError()) {
            return ValidationStatus.OK_STATUS;
        }

        // have a good runtime type
        final String errorMsg = Utils.isValidValue(Messages.propertyFixedValue,
                                                   Utils.convertRuntimeType(runtimeType),
                                                   fixedValue,
                                                   true,
                                                   allowedValues);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    /**
     * @param proposedValue the value of the indexed attribute
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyIndexedAttribute( final boolean proposedValue ) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    /**
     * @param proposedValue the value of the masked attribute
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyMaskedAttribute( final boolean proposedValue ) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    /**
     * @param proposedValue the value of the required attribute
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyRequiredAttribute(final boolean proposedValue) {
        return ValidationStatus.OK_STATUS; // any value is valid
    }

    /**
     * @param runtimeType property type being checked (can be <code>null</code> or empty)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertyRuntimeType(final String runtimeType) {
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

    /**
     * @param proposedValue the unqualified property name (can be <code>null</code> or empty)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validatePropertySimpleId(final String proposedValue) {
        { // empty check
            final String errorMsg = emptyCheck(Messages.propertySimpleId, proposedValue);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                return ValidationStatus.createErrorMessage(errorMsg);
            }
        }

        String errorMsg = containsOnlyIdCharactersCheck(Messages.propertySimpleId, proposedValue);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            return ValidationStatus.OK_STATUS;
        }

        return ValidationStatus.createErrorMessage(errorMsg);
    }

    /**
     * @param locale the translation locale (can be <code>null</code>)
     * @param text the translated text (can be <code>null</code> or empty)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateTranslation(final Locale locale,
                                                final String text) {
        MedStatus status = validateTranslationLocale(locale);
        status = addStatus(status, validateTranslationText(text));

        return status;
    }

    /**
     * @param locale the locale being checked (can be <code>null</code>)
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateTranslationLocale( final Locale locale ) {
        if (locale == null) {
            return ValidationStatus.createErrorMessage(Messages.localeMissingValidationMsg);
        }

        return ValidationStatus.OK_STATUS;
    }

    /**
     * @param translationType the translation type (can not be <code>null</code> or empty)
     * @param translations the translations being checked (can be <code>null</code>)
     * @param validateEachTranslation <code>true</code> if every translation should be validated
     * @return the validation status (never <code>null</code>)
     */
    public static MedStatus validateTranslations(final String translationType,
                                                 final Collection<Translation> translations,
                                                 final boolean validateEachTranslation) {
        if ((translations == null) || translations.isEmpty()) {
            return ValidationStatus.OK_STATUS;
        }

        MedStatus status = ValidationStatus.OK_STATUS;
        final Set<Locale> locales = new HashSet<Locale>(translations.size());

        for (final Translation translation : translations) {
            final String errorMsg = nullCheck(translationType, translation);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                status = addStatus(status, ValidationStatus.createErrorMessage(errorMsg));
                break;
            }

            if (validateEachTranslation) {
                status = addStatus(status, validateTranslation(translation.getLocale(), translation.getTranslation()));
            }

            // duplicates check
            if (!locales.add(translation.getLocale())) {
                status = addStatus(status,
                                   ValidationStatus.createErrorMessage(NLS.bind(Messages.duplicateTranslationLocaleValidationMsg,
                                                                                translationType)));
            }
        }

        return status;
    }

    /**
     * @param text the text being checked (can be <code>null</code> or empty)
     * @return the validation status (never <code>null</code>)
     */
    public static ValidationStatus validateTranslationText(final String text) {
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

    /**
     * @param version the version being checked (can be <code>null</code> or empty)
     * @return the validation status (never <code>null</code>)
     */
    public static ValidationStatus validateVersion(final String version) {
        { // empty check
            final String errorMsg = emptyCheck(Messages.version, version);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                return ValidationStatus.createErrorMessage(errorMsg);
            }
        }

        { // spaces check
            String errorMsg = containsSpacesCheck(Messages.version, version);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                return ValidationStatus.createErrorMessage(errorMsg);
            }
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
