/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.core.validation.rules;

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

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * CoreValidationRulesUtil
 */
public class CoreValidationRulesUtil {
    
    /**
     * Validate the given string against a maximum length and create any problems.
     * @param maxLength
     * @param stringToValidate
     * @param ValidationResult to add problems for
     */
    public static void validateStringLength(final ValidationResult result, final int maxLength, final String stringToValidate) {
        ArgCheck.isNotNull(stringToValidate);
        ArgCheck.isNotNull(result);
        
        final StringNameValidator validator = new StringNameValidator(maxLength);
        final String reasonInvalid = validator.checkNameLength(stringToValidate);
        if ( reasonInvalid != null ) {
            // create validation problem and addit to the resuly
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
        ArgCheck.isNotNull(stringToValidate);
        ArgCheck.isNotNull(result);
        
        final StringNameValidator validator = new StringNameValidator();
        final String reasonInvalid = validator.checkNameLength(stringToValidate);
        if ( reasonInvalid != null ) {
            // create validation problem and addit to the resuly
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, reasonInvalid);
            result.addProblem(problem);
        }
    }    

    /**
     * Validate the given string against a maximum length and create any problems.
     * @param stringToValidate
     * @param any invalid charachters to check for
     * @param ValidationResult to add problems for
     * @param invalidCharacterSeverity the severity to use if there are invalid characters; must be
     * one of the {@link IStatus} severity codes.
     */
    public static void validateStringNameChars(final ValidationResult result, final String stringToValidate, final char[] invalidChars, final int invalidCharacterSeverity) {
        ArgCheck.isNotNull(stringToValidate);                
        ArgCheck.isNotNull(result);
        final StringNameValidator validator = new StringNameValidator(invalidChars);
        final String reasonInvalid = validator.checkNameCharacters(stringToValidate);
        if ( reasonInvalid != null ) {
            // create validation problem and addit to the resuly
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
     * @param any invalid charachters to check for
     * @param ValidationResult to add problems for
     */
    public static void validateStringNameChars(final ValidationResult result, final String stringToValidate, final char[] invalidChars) {
        validateStringNameChars(result,stringToValidate,invalidChars,IStatus.ERROR);
    }

	/**
	 * Validate the given string against a maximum length and create any problems.
	 * @param stringToValidate
	 * @param any invalid charachters to check for
	 * @param ValidationResult to add problems for
	 */
	public static void validateStringName(final ValidationResult result, final int maxLength, final String stringToValidate, final char[] invalidChars) {
		ArgCheck.isNotNull(stringToValidate);                
		ArgCheck.isNotNull(result);
		final StringNameValidator validator = new StringNameValidator(maxLength, invalidChars);
		final String reasonInvalid = validator.checkValidName(stringToValidate);
		if ( reasonInvalid != null ) {
			// create validation problem and addit to the resuly
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
     * charachter, if there is an invalid charachter at the begining it is removed from the string. If the
     * name is not unique among its siblings integers are added to the end of the name until it is unique.
     * @param name The name that should be unique within its container
     * @param any invalid charachters to check for, in addition to the default invalid chars
     * @param maximum legth of the valid string
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
