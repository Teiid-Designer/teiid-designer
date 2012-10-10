/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.validation.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;


/**
 * CoreValidationRulesUtil
 *
 * @since 8.0
 */
public class CoreValidationRulesUtil {
    
    /**
     * Validate the given string against a maximum length and create any problems.
     * @param maxLength
     * @param stringToValidate
     * @param ValidationResult to add problems for
     */
    public static void validateStringLength(final ValidationResult result, final int maxLength, final String stringToValidate) {
        CoreArgCheck.isNotNull(stringToValidate);
        CoreArgCheck.isNotNull(result);
        
        final StringNameValidator validator = new StringNameValidator(maxLength);
        final String reasonInvalid = validator.checkNameLength(stringToValidate);
        if ( reasonInvalid != null ) {
            // create validation problem and add it to the result
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, reasonInvalid);
            result.addProblem(problem);
        }
    }

    /**
     * Validate the given string against a maximum length and create any problems.
     * @param maxLength
     * @param stringToValidate
     * @param ValidationResult to add problems for
     */
    public static void validateStringLength(final ValidationResult result, final String stringToValidate) {
        CoreArgCheck.isNotNull(stringToValidate);
        CoreArgCheck.isNotNull(result);
        
        final StringNameValidator validator = new StringNameValidator();
        final String reasonInvalid = validator.checkNameLength(stringToValidate);
        if ( reasonInvalid != null ) {
            // create validation problem and add it to the result
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, reasonInvalid);
            result.addProblem(problem);
        }
    }    

    /**
     * Validate the given string against a maximum length and create any problems.
     * @param ValidationResult to add problems for
     * @param stringToValidate
     * @param any invalid characters to check for
     * @param invalidCharacterSeverity the severity to use if there are invalid characters; must be
     * one of the {@link IStatus} severity codes.
     */
    public static void validateStringNameChars(final ValidationResult result, final String stringToValidate, final char[] validChars, final int invalidCharacterSeverity) {
        CoreArgCheck.isNotNull(stringToValidate);                
        CoreArgCheck.isNotNull(result);
        final StringNameValidator validator = new StringNameValidator(validChars);
        validateStringNameChars(result, stringToValidate, validator, invalidCharacterSeverity);
    }
    
    /**
     * Validate the given string against a maximum length and create any problems.
     * @param ValidationResult to add problems for
     * @param stringToValidate
     * @param a string validator
     * @param invalidCharacterSeverity the severity to use if there are invalid characters; must be
     * one of the {@link IStatus} severity codes.
     */
    public static void validateStringNameChars(final ValidationResult result, final String stringToValidate, StringNameValidator validator, final int invalidCharacterSeverity) {
        CoreArgCheck.isNotNull(stringToValidate);                
        CoreArgCheck.isNotNull(result);

        final String reasonInvalid = validator.checkNameCharacters(stringToValidate);
        if ( reasonInvalid != null ) {
            // create validation problem and add it to the result
            int severity = invalidCharacterSeverity;
            if ( severity != IStatus.ERROR && severity != IStatus.INFO && severity != IStatus.OK && severity != IStatus.WARNING ) {
                severity = IStatus.ERROR;
            }
            ValidationProblem problem  = new ValidationProblemImpl(0, severity, reasonInvalid);
            result.addProblem(problem);
        }
    }

    /**
     * Validate the given string against a maximum length and create any problems.
     * @param stringToValidate
     * @param any invalid characters to check for
     * @param ValidationResult to add problems for
     */
    public static void validateStringNameChars(final ValidationResult result, final String stringToValidate, final char[] validChars) {
        validateStringNameChars(result,stringToValidate,validChars,IStatus.ERROR);
    }

	/**
	 * Validate the given string against a maximum length and create any problems.
	 * @param stringToValidate
	 * @param any invalid characters to check for
	 * @param ValidationResult to add problems for
	 */
	public static void validateStringName(final ValidationResult result, final int maxLength, final String stringToValidate, final char[] invalidChars) {
		CoreArgCheck.isNotNull(stringToValidate);                
		CoreArgCheck.isNotNull(result);
		final StringNameValidator validator = new StringNameValidator(maxLength, invalidChars);
		final String reasonInvalid = validator.checkValidName(stringToValidate);
		if ( reasonInvalid != null ) {
			// create validation problem and add it to the result
			ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, reasonInvalid);
			result.addProblem(problem);
		}
	}


	/**
	 * Recursively check the names of the siblings against each other names and create
	 * validation errors if we find siblings with same case insensitive name.
	 * @param context The validation context to which we add results
	 * @param siblings The siblings EObjects
	 * @param nameFeatureID the ID of the feature that represents the name feature
	 */
	public static void validateUniqueness(final ValidationContext context, final List siblings, final int nameFeatureID) {
		final StringNameValidator validator = new StringNameValidator();
		validateUniqueness(context, validator, siblings, nameFeatureID);
	}
    
    public static void validateUniqueness(final ValidationContext context, final StringNameValidator validator, final List siblings, final int nameFeatureID) {

        Map objectCountMap = validator.getDuplicateNamesMap(siblings,nameFeatureID);

        // if there is at least one match, create a problem
        if(objectCountMap.size() > 0) {
            Iterator keyIter = objectCountMap.keySet().iterator();
            while(keyIter.hasNext()) {
                EObject eObject = (EObject) keyIter.next();
                final EStructuralFeature eFeature = eObject.eClass().getEStructuralFeature(nameFeatureID);
                final String name = (String) eObject.eGet(eFeature);
                Integer count = (Integer)objectCountMap.get(eObject);
                ValidationResult result = new ValidationResultImpl(eObject);
                String msg = (validator.isCaseSensitive() ? ModelerCore.Util.getString("StringNameValidator.sameNameCaseSensitive", name, count) //$NON-NLS-1$
                                                          : ModelerCore.Util.getString("StringNameValidator.The_name_{0}_is_the_same_as_{1}_other_objects_under_the_same_parent",name, count)); //$NON-NLS-1$
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                result.addProblem(problem);
                context.addResult(result);
            }
        }
    }
    
	/**
	 * Recursively check the names of the features against each other names and return
	 * statuses containing errors.
	 * @param features The list of {@link org.eclipse.emf.ecore.EStructuralFeature} objects
	 */
	public static Collection validateUniqueness(final List features) {
		final StringNameValidator validator = new StringNameValidator();
		Map objectCountMap = validator.getDuplicateNamesMap(features);

		// if there is at least one match, create a status
		if(objectCountMap.size() > 0) {
			Collection statusList = new ArrayList(objectCountMap.size());
			Iterator keyIter = objectCountMap.keySet().iterator();
			while(keyIter.hasNext()) {
				EObject eObject = (EObject) keyIter.next();
				final String name = ModelerCore.getModelEditor().getName(eObject);
				Integer count = (Integer)objectCountMap.get(eObject);
				// create a new status and update the list
				IStatus status = new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, 0, ModelerCore.Util.getString("CoreValidationRulesUtil.The_name_of_feature_{0}_is_the_same_as_name_of_{1}_other_features._1", name, count), null); //$NON-NLS-1$
				statusList.add(status);
			}
			return statusList;
		}

		return Collections.EMPTY_LIST;
	}

    /**
     * Get the valid name for the given string, all invalid chars are replaced with an '_'
     * character, if there is an invalid character at the beginning it is removed from the string. If the
     * name is not unique among its siblings integers are added to the end of the name until it is unique.
     * @param name The name that should be unique within its container
     * @param any invalid character to check for, in addition to the default invalid chars
     * @param maximum length of the valid string
     * @return a valid string for this invalid string.
     */
    public static String getValidString(final String name, final char[] invalidChars, final int maxLength) {
        final StringNameValidator validator = new StringNameValidator(maxLength,invalidChars);
        return validator.createValidName(name);
    }

//    
//    private static String getUniqueString(String name, Collection siblingNames) {
//        Iterator siblingIter = siblingNames.iterator();
//        while(siblingIter.hasNext()) {
//            String sibName = (String) siblingIter.next();
//            if(sibName.equalsIgnoreCase(name)) {
//                char lastChar = name.charAt(name.length()-1);
//                int intValue = 0;                
//                if(Character.isDigit(lastChar)) {
//                    intValue = Character.getNumericValue(lastChar);
//                    name = name.substring(0, name.length()-1) + intValue;
//                } else {
//                     intValue++;
//                     name = name + intValue;                     
//                }
//                name = getUniqueString(name, siblingNames);
//            }
//        }
//
//        return name;
//    }

}
