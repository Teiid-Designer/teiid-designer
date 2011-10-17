/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    public static String definitionFileNotFoundInFilesystem;
    public static String definitionFileNotFoundInWorkspace;
    public static String definitionSchemaFileNotFoundInFilesystem;
    public static String definitionSchemaFileNotFoundInWorkspace;
    public static String descriptorToolTip;
    public static String duplicateAllowedValue;
    public static String emptyPropertyValue;
    public static String errorProcessingDefinitionFile;
    public static String errorProcessingExtensionPoint;
    public static String errorProcessingModelExtension;
    public static String extensionPropertyNotAdded;
    public static String incorrectModelExtensionAssistantClass;
    public static String invalidBooleanAllowedValue;
    public static String invalidDefinitionFileNewVersion;
    public static String invalidDefinitionFileVersion;
    public static String invalidPropertyId;
    public static String invalidPropertyValueForType;
    public static String invalidRuntimeType;
    public static String missingDefinitionPath;
    public static String modelExtensionAssistantNotFound;
    public static String modelExtensionAssistantSetIdCalled;
    public static String namespacePrefixAlreadyRegistered;
    public static String namespacePrefixNotFound;
    public static String namespaceUriAlreadyRegistered;
    public static String invalidMetamodelUriExtension;
    public static String problemConstructingModelExtensionAssistantClass;
    public static String unknownPropertyType;
    public static String unknownRegistryQueryType;
    public static String valueDoesNotMatchAnAllowedValue;

    public static String allowedValue;
    public static String description;
    public static String metaclassName;
    public static String metamodelUri;
    public static String namespacePrefix;
    public static String namespaceUri;
    public static String propertyDescription;
    public static String propertyDisplayName;
    public static String propertySimpleId;
    public static String resourcePath;
    public static String translation;
    public static String version;

    public static String duplicatePropertyIdValidatinMsg;
    public static String duplicateTranslationLocaleValidationMsg;
    public static String extendedMetaclassHasNoPropertiesValidationMsg;
    public static String localeMissingValidationMsg;
    public static String objectIsNullValidationMsg;
    public static String medFileDoesNotExistValidationMsg;
    public static String medHasNoMetaclassesValidationMsg;
    public static String medHasDuplicateMetaclassesValidationMsg;
    public static String metaclassNameHasInvalidCharactersValidationMsg;
    public static String metamodelUriNotExtendableValidationMsg;
    public static String missingRuntimeTypeValidationMsg;
    public static String namespacePrefixExistsValidationMsg;
    public static String namespaceUriExistsValidationMsg;
    public static String propertyIsEmptyValidationMsg;
    public static String translationMissingValidationMsg;
    public static String valueContainsSpacesValidationMsg;
    public static String uriInvalidValidationMsg;
    public static String versionIsNotAnIntegerValidationMsg;
    public static String versionLessThanDefaultValidationMsg;
    public static String appendPropertyId;

    public static String translationToString;

    static {
        NLS.initializeMessages("org.teiid.designer.extension.messages", Messages.class); //$NON-NLS-1$
    }
}
