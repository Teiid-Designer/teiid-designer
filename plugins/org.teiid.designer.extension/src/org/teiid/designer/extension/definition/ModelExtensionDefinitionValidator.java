/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.net.URI;

import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.CoreStringUtil;

/**
 * Validates the properties of a {@link ModelExtensionDefinition}.
 */
public final class ModelExtensionDefinitionValidator {

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

    public static String validateMetamodelUri( String metamodelUri ) {
        return uriCheck(Messages.metamodelUri, metamodelUri);
    }

    public static String validateMetamodelUri( String metamodelUri,
                                               ModelExtensionRegistry registry ) {
        String errorMsg = validateMetamodelUri(metamodelUri);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            if (!registry.isExtendable(metamodelUri)) {
                errorMsg = NLS.bind(Messages.metamodelUriNotExtendableValidationMsg, metamodelUri);
            }
        }

        return errorMsg;
    }

    public static String validateNamespacePrefix( String namespacePrefix ) {
        return containsSpacesCheck(Messages.namespacePrefix, namespacePrefix);
    }

    public static String validateNamespacePrefix( String namespacePrefix,
                                                  ModelExtensionRegistry registry ) {
        String errorMsg = validateNamespacePrefix(namespacePrefix);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            if (registry.isNamespacePrefixRegistered(namespacePrefix)) {
                errorMsg = NLS.bind(Messages.namespacePrefixExistsValidationMsg, namespacePrefix);
            }
        }

        return errorMsg;
    }

    public static String validateNamespaceUri( String namespaceUri ) {
        return uriCheck(Messages.namespaceUri, namespaceUri);
    }

    public static String validateNamespaceUri( String namespaceUri,
                                               ModelExtensionRegistry registry ) {
        String errorMsg = validateNamespaceUri(namespaceUri);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            if (registry.isNamespaceUriRegistered(namespaceUri)) {
                errorMsg = NLS.bind(Messages.namespaceUriExistsValidationMsg, namespaceUri);
            }
        }

        return errorMsg;
    }

    public static String validateResourcePath( String resourcePath ) {
        String propertyName = Messages.resourcePath;
        String msg = emptyCheck(propertyName, resourcePath);

        if (!CoreStringUtil.isEmpty(msg)) {
            // TODO check to see if file exists
            return msg;
        }

        return null;
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

    public static String validate( ModelExtensionDefinition med ) {
        String errorMsg = validateResourcePath(med.getResourcePath());

        if (CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = validateNamespacePrefix(med.getNamespacePrefix());

            if (CoreStringUtil.isEmpty(errorMsg)) {
                errorMsg = validateNamespaceUri(med.getNamespaceUri());

                if (CoreStringUtil.isEmpty(errorMsg)) {
                    errorMsg = validateMetamodelUri(med.getMetamodelUri());

                    if (CoreStringUtil.isEmpty(errorMsg)) {
                        errorMsg = validateVersion(String.valueOf(med.getVersion()));

                        if (CoreStringUtil.isEmpty(errorMsg)) {
                            errorMsg = validateDescription(med.getDescription());
                        }
                    }
                }
            }
        }

        return errorMsg;
    }

    public static String validate( ModelExtensionDefinition med,
                                   ModelExtensionRegistry registry ) {
        String errorMsg = validate(med);

        if (CoreStringUtil.isEmpty(errorMsg)) {
            errorMsg = validateNamespacePrefix(med.getNamespacePrefix(), registry);

            if (CoreStringUtil.isEmpty(errorMsg)) {
                errorMsg = validateNamespaceUri(med.getNamespaceUri(), registry);

                if (CoreStringUtil.isEmpty(errorMsg)) {
                    errorMsg = validateMetamodelUri(med.getMetamodelUri(), registry);
                }
            }
        }

        return errorMsg;
    }

}
