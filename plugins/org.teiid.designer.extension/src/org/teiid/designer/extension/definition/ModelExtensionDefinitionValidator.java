/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
     * @param metaclassNames the collection of metaclass names in the model extension definition
     * @param validateEachName indicates if each name should validate using {@link #validateMetaclassName(String)}
     * @return
     */
    public static String validateMetaclassNames( Collection<String> metaclassNames,
                                                 boolean validateEachName ) {
        String errorMsg = null;

        if ((metaclassNames == null) || metaclassNames.isEmpty()) {
            errorMsg = Messages.medHasNoMetaclassesValidationMsg;
        } else {
            // make sure no duplicates
            if (metaclassNames.size() != new HashSet<String>(metaclassNames).size()) {
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
            if ((existingNamespacePrefixes != null) && existingNamespacePrefixes.contains(namespacePrefix)) {
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
                                                     String description,
                                                     String displayName,
                                                     String[] allowedValues ) {
        // namespace prefix
        String errorMsg = validateNamespacePrefix(namespacePrefix, Collections.<String> emptyList());

        // runtime type
        if (!CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = validatePropertyRuntimeType(runtimeType);

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                errorMsg = validatePropertyDefaultValue(runtimeType, defaultValue, allowedValues);

                if (!CoreStringUtil.isEmpty(errorMsg)) {
                    errorMsg = validatePropertyFixedValue(runtimeType, fixedValue);

                    if (!CoreStringUtil.isEmpty(errorMsg)) {
                        // TODO fix this
                        // errorMsg = validateTranslation(description);
                        //
                        // if (!CoreStringUtil.isEmpty(errorMsg)) {
                        // errorMsg = validateTranslation(displayName, null);
                        //
                        if (!CoreStringUtil.isEmpty(errorMsg)) {
                            errorMsg = validatePropertyAllowedValues(runtimeType, allowedValues);
                        }
                        // }
                    }
                }
            }
        }

        return errorMsg;
    }

    public static String validatePropertyDefinitions( Map<String, Map<String, ModelExtensionPropertyDefinition>> medPropDefns ) {
        String errorMsg = null;

        MED_PROP_DEFNS: {
            for (String metaclassName : medPropDefns.keySet()) {
                errorMsg = validateMetaclassName(metaclassName);

                if (CoreStringUtil.isEmpty(errorMsg)) {
                    // make sure metaclass has at least one property
                    Map<String, ModelExtensionPropertyDefinition> props = medPropDefns.get(metaclassName);

                    if ((props == null) || props.isEmpty()) {
                        errorMsg = NLS.bind(Messages.extendedMetaclassHasNoPropertiesValidationMsg, metaclassName);
                        break MED_PROP_DEFNS;
                    }

                    for (Map<String, ModelExtensionPropertyDefinition> propDefns : medPropDefns.values()) {
                        Set<String> ids = new HashSet<String>();

                        for (ModelExtensionPropertyDefinition propDefn : propDefns.values()) {
                            // check for duplicates
                            if (!ids.add(propDefn.getSimpleId())) {
                                errorMsg = NLS.bind(Messages.duplicatePropertyIdValidatinMsg, propDefn.getSimpleId());
                                break MED_PROP_DEFNS;
                            }

                            errorMsg = validatePropertyDefinition(propDefn.getNamespacePrefix(), propDefn.getSimpleId(),
                                                                  propDefn.getRuntimeType(), propDefn.getDefaultValue(),
                                                                  propDefn.getFixedValue(), propDefn.getDescription(),
                                                                  propDefn.getDisplayName(), propDefn.getAllowedValues());

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

    public static String validateResourcePath( String resourcePath ) {
        String propertyName = Messages.resourcePath;
        String errorMsg = emptyCheck(propertyName, resourcePath);

        if (!CoreStringUtil.isEmpty(errorMsg)) {
            File file = new File(resourcePath);

            if (!file.exists()) {
                errorMsg = NLS.bind(Messages.medFileDoesNotExistValidationMsg, resourcePath);
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
                                               Collection<Translation> translations ) {
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

            errorMsg = validateTranslation(translation.getLocale(), translation.getTranslation());

            if (!CoreStringUtil.isEmpty(errorMsg)) {
                break;
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
        return emptyCheck(Messages.translation, text);
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

    private String description;

    private String descriptionError;
    private ModelExtensionDefinition medBeingEdited;

    private final Collection<String> metaclasses;
    private String metaclassesError;

    private String metamodelUri;
    private String metamodelUriError;

    private String namespacePrefix;
    private String namespacePrefixError;

    private String namespaceUri;
    private String namespaceUriError;

    private final Map<String, Map<String, ModelExtensionPropertyDefinition>> properties;
    private String propertiesError;

    private String resourcePath;
    private String resourcePathError;

    private int version = -1;
    private String versionError;

    public ModelExtensionDefinitionValidator() {
        this.metaclasses = new ArrayList<String>();
        this.properties = new HashMap<String, Map<String, ModelExtensionPropertyDefinition>>();
    }

    public ModelExtensionDefinitionValidator( ModelExtensionDefinition med ) {
        this();

        if (med != null) {
            this.medBeingEdited = med;
            this.resourcePath = med.getResourcePath();
            this.description = med.getDescription();
            this.metamodelUri = med.getMetamodelUri();
            this.namespacePrefix = med.getNamespacePrefix();
            this.namespaceUri = med.getNamespaceUri();
            this.version = med.getVersion();

            for (String metaclassName : med.getExtendedMetaclasses()) {
                this.metaclasses.add(metaclassName);
                Map<String, ModelExtensionPropertyDefinition> propDefns = this.properties.get(metaclassName);

                if (propDefns == null) {
                    propDefns = new HashMap<String, ModelExtensionPropertyDefinition>();
                    this.properties.put(metaclassName, propDefns);
                }

                for (ModelExtensionPropertyDefinition propDefn : med.getPropertyDefinitions(metaclassName)) {
                    propDefns.put(propDefn.getId(), propDefn);
                }
            }
        }
    }

    public String addMetaclassName( String metaclassName ) {
        this.metaclassesError = validateMetaclassName(metaclassName);
        this.metaclasses.add(metaclassName);

        if (CoreStringUtil.isEmpty(this.metaclassesError)) {
            this.metaclassesError = validateMetaclassNames(this.metaclasses, false);
        }

        return this.metaclassesError;
    }

    public String addPropertyDescription( Translation description ) {
        // TODO need to add
        return validateTranslation(description.getLocale(), description.getTranslation());
    }

    public String addPropertyDisplayName( Translation displayName ) {
        // TODO need to add
        return validateTranslation(displayName.getLocale(), displayName.getTranslation());
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    public String[] getExtendedMetaclasses() {
        return this.metaclasses.toArray(new String[this.metaclasses.size()]);
    }

    /**
     * @return the metamodel URI
     */
    public String getMetamodelUri() {
        return this.metamodelUri;
    }

    /**
     * @return the namespace prefix
     */
    public String getNamespacePrefix() {
        return this.namespacePrefix;
    }

    /**
     * @return the namespace URI
     */
    public String getNamespaceUri() {
        return this.namespaceUri;
    }

    public ModelExtensionPropertyDefinition[] getPropertyDefinitions( String metaclassName ) {
        Map<String, ModelExtensionPropertyDefinition> map = this.properties.get(metaclassName);

        if (map == null) {
            return new ModelExtensionPropertyDefinition[0];
        }

        Collection<ModelExtensionPropertyDefinition> propDefns = map.values();
        return propDefns.toArray(new ModelExtensionPropertyDefinition[propDefns.size()]);
    }

    /**
     * @return resourcePath
     */
    public String getResourcePath() {
        return this.resourcePath;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return this.version;
    }

    public boolean isEditMode() {
        return (this.medBeingEdited != null);
    }

    public String removeMetaclassName( String metaclassName ) {
        this.metaclasses.remove(metaclassName);
        this.properties.remove(metaclassName);
        this.metaclassesError = validateMetaclassNames(this.metaclasses, false);

        if (CoreStringUtil.isEmpty(this.metaclassesError)) {
            this.metaclassesError = validatePropertyDefinitions(this.properties);
        }

        return this.metaclassesError;
    }

    public String removePropertyDescription( Translation description ) {
        // TODO need to remove
        return null; // no validation required
    }

    public String removePropertyDisplayName( Translation displayName ) {
        // TODO need to remove
        return null; // no validation required
    }

    /**
     * @param the new description
     * @return the validation error message or <code>null</code> if new value is valid
     */
    public String setDescription( String description ) {
        this.description = description;
        this.descriptionError = validateDescription(description);
        return this.descriptionError;
    }

    /**
     * @param metamodelUri the new metamodel URI
     * @return the validation error message or <code>null</code> if new value is valid
     */
    public String setMetamodelUri( String metamodelUri,
                                   Collection<String> existingMetamodelUris ) {
        this.metamodelUri = metamodelUri;
        this.metamodelUriError = validateMetamodelUri(metamodelUri, existingMetamodelUris);
        return this.metamodelUriError;
    }

    /**
     * @param namespacePrefix the new namespace prefix
     * @return the validation error message or <code>null</code> if new value is valid
     */
    public String setNamespacePrefix( String namespacePrefix,
                                      Collection<String> existingNamespacePrefixes ) {
        this.namespacePrefix = namespacePrefix;
        this.namespacePrefixError = validateNamespacePrefix(namespacePrefix, existingNamespacePrefixes);
        return this.namespacePrefixError;
    }

    /**
     * @param namespaceUri the new namespace URI
     * @return the validation error message or <code>null</code> if new value is valid
     */
    public String setNamespaceUri( String namespaceUri,
                                   Collection<String> existingNamespaceUris ) {
        this.namespaceUri = namespaceUri;
        this.namespaceUriError = validateNamespaceUri(namespaceUri, existingNamespaceUris);
        return this.namespaceUriError;
    }

    /**
     * @param resourcePath Sets resourcePath to the specified value.
     * @return the validation error message or <code>null</code> if new value is valid
     */
    public String setResourcePath( String path ) {
        this.resourcePath = path;
        this.resourcePathError = validateResourcePath(path);
        return this.resourcePathError;
    }

    /**
     * @param version the new version
     * @return the validation error message or <code>null</code> if new value is valid
     */
    public String setVersion( int version ) {
        this.version = version;
        this.versionError = validateVersion(String.valueOf(version));
        return this.versionError;
    }

    public Collection<String> validate( Collection<String> extendableMetamodelUris,
                                        Collection<String> existingNamespacePrefixes,
                                        Collection<String> existingNamespaceUris ) {
        Collection<String> errors = new ArrayList<String>();

        // description
        this.descriptionError = validateDescription(this.description);

        if (!CoreStringUtil.isEmpty(this.descriptionError)) {
            errors.add(this.descriptionError);
        }

        // metamodel URI
        this.metamodelUriError = validateMetamodelUri(this.metamodelUri, extendableMetamodelUris);

        if (!CoreStringUtil.isEmpty(this.metamodelUriError)) {
            errors.add(this.metamodelUriError);
        }

        // namespace prefix
        this.namespacePrefixError = validateNamespacePrefix(this.namespacePrefix, existingNamespacePrefixes);

        if (!CoreStringUtil.isEmpty(this.namespacePrefixError)) {
            errors.add(this.namespacePrefixError);
        }

        // namespace URI
        this.namespaceUriError = validateNamespaceUri(this.namespaceUri, existingNamespaceUris);

        if (!CoreStringUtil.isEmpty(this.namespaceUriError)) {
            errors.add(this.namespaceUriError);
        }

        // resource resourcePath
        this.resourcePathError = validateResourcePath(this.resourcePath);

        if (!CoreStringUtil.isEmpty(this.resourcePathError)) {
            errors.add(this.resourcePathError);
        }

        // extended metaclasses
        this.metaclassesError = validateMetaclassNames(this.metaclasses, true);

        if (!CoreStringUtil.isEmpty(this.metaclassesError)) {
            errors.add(this.metaclassesError);
        }

        // properties
        this.propertiesError = validatePropertyDefinitions(this.properties);

        if (!CoreStringUtil.isEmpty(this.propertiesError)) {
            errors.add(this.propertiesError);
        }

        return errors;
    }

}
