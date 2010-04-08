/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.ResourceNameUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ResourceValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelStatusImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * ModelFileExtensionRule
 */
public class ModelFileExtensionRule implements ResourceValidationRule {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.validation.ResourceValidationRule#validate(org.eclipse.emf.ecore.resource.Resource, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate( final Resource resource,
                          final ValidationContext context ) {
        CoreArgCheck.isNotNull(resource);
        CoreArgCheck.isNotNull(context);

        // Run this rule only on file based resources
        final URI uri = resource.getURI();
        if (uri != null && uri.isFile()) {

            // Case 6581 - First Check the modelname for validity
            String name = ModelerCore.getModelEditor().getModelName(resource);
            // Decode any uri encoding
            String decodedName = URI.decode(name);
            final ValidationResultImpl vresult = new ValidationResultImpl(decodedName);
            if (!isReservedInvalidModelName(decodedName)) {
                CoreValidationRulesUtil.validateStringNameChars(vresult, decodedName, null);
            }
            if (!vresult.hasProblems()) {
                if (!name.equals(decodedName) && !isReservedInvalidModelName(name)) {
                    CoreValidationRulesUtil.validateStringNameChars(vresult, name, null);
                    if (vresult.hasProblems()) {
                        context.addResult(vresult);
                    }
                }
            } else {
                context.addResult(vresult);
            }

            final IStatus status = validate(uri.lastSegment());
            if (!status.isOK()) {
                final ValidationResult result = new ValidationResultImpl(resource);
                final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, status.getMessage());
                result.addProblem(problem);
                context.addResult(result);
            }
        }
    }

    /**
     * Return an IStatus indicating if the specified file name is considered to have a valid file extension. The extension is
     * considered invalid if it matches one of the well-known file extensions for the modeler (e.g. ".xmi", ".xsd", ".vdb") but
     * the extension is not lower-case. There are a number of places in the modeler and console code that assumes lower-case
     * extensions. Because of this, a mixed or upper-case extension could produce problems which is the reason for the validation
     * rule. This method will return an OK status if the specified file name has no extension, is not one of the well-known
     * extensions, is a well-known extension with the correct case.
     * 
     * @param fileNameWithExtension
     * @return
     * @since 4.3
     */
    public static IStatus validate( final String fileNameWithExtension ) {
        CoreArgCheck.isNotNull(fileNameWithExtension);
        CoreArgCheck.isNotZeroLength(fileNameWithExtension);

        String actualExtension = null;
        int beginIndex = fileNameWithExtension.lastIndexOf('.') + 1;
        if (beginIndex > 0 && beginIndex < fileNameWithExtension.length()) {
            actualExtension = fileNameWithExtension.substring(beginIndex);
        }

        String expectedExtension = getMatchingKnownExtension(actualExtension);

        // If this resource extension does not match any of the well-known extensions then return
        if (expectedExtension == null) {
            return ModelStatusImpl.VERIFIED_OK;
        }

        // If the extension is a case-sensitive match for one of the well-known extensions then return
        if (expectedExtension.equals(actualExtension)) {
            return ModelStatusImpl.VERIFIED_OK;
        }

        // If the extension is a case-insensitive match for one of the well-known extensions then validation error
        if (expectedExtension.equalsIgnoreCase(actualExtension)) {
            final int endIndex = fileNameWithExtension.length() - actualExtension.length();
            final String expectedFileName = fileNameWithExtension.substring(0, endIndex) + expectedExtension;

            final Object[] params = new Object[] {fileNameWithExtension, expectedFileName};
            final String msg = ModelerCore.Util.getString("ModelerCore.file_extension_not_correct_case_please_rename_file", params); //$NON-NLS-1$

            return new Status(IStatus.WARNING, ModelerCore.PLUGIN_ID, -1, msg, null);
        }
        return ModelStatusImpl.VERIFIED_OK;
    }

    // Case 6581 - added since we have some internal modelNames that are invalid (contain dashes).
    // This method is used to detect whether a modelname is one of the invalid internal names,
    // and bypass the name validation for it.
    private static boolean isReservedInvalidModelName( String name ) {
        boolean isReservedInvalid = false;
        if (ResourceNameUtil.XMLSCHEMA_INSTANCE_NAME.equals(name) || ResourceNameUtil.SIMPLEDATATYPES_INSTANCE_NAME.equals(name)) {
            isReservedInvalid = true;
        }
        return isReservedInvalid;
    }

    private static String getMatchingKnownExtension( final String extension ) {
        // Return the "expected" extension by performing a case-insensitive match against all well-known model file extensions
        if (ModelUtil.EXTENSION_XML.equalsIgnoreCase(extension)) {
            return ModelUtil.EXTENSION_XML;

        } else if (ModelUtil.EXTENSION_XMI.equalsIgnoreCase(extension)) {
            return ModelUtil.EXTENSION_XMI;

        } else if (ModelUtil.EXTENSION_XSD.equalsIgnoreCase(extension)) {
            return ModelUtil.EXTENSION_XSD;

        } else if (ModelUtil.EXTENSION_VDB.equalsIgnoreCase(extension)) {
            return ModelUtil.EXTENSION_VDB;

        }
        return null;
    }
}
