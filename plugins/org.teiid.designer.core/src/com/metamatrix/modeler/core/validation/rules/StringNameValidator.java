/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;

/**
 * StringNameValidator
 */
public class StringNameValidator {

    public static final char UNDERSCORE_CHARACTER = '_';
    public static final char[] DEFAULT_VALID_NON_LETTER_OR_DIGIT_CHARS = {UNDERSCORE_CHARACTER};
    public static final char DEFAULT_REPLACEMENT_CHARACTER = UNDERSCORE_CHARACTER;
    public static final int MAXIMUM_LENGTH = Integer.MAX_VALUE;
    public static final int DEFAULT_MAXIMUM_LENGTH = 255;
    public static final int DEFAULT_MINIMUM_LENGTH = 1;
    public static final boolean DEFAULT_CASE_SENSITIVE_NAME_COMPARISON = false;
    private static final Integer INTEGER_ONE = new Integer(1);

    private final int maximumLength;
    private final int minimumLength;
    private final boolean caseSensitive;
    private final char replacementCharacter;
    private final char[] validNonLetterOrDigitChars;
    
    private final ExistingNames existingNames;

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator( final int minLength,
                                final int maxLength,
                                final boolean caseSensitive,
                                final char replacementCharacter,
                                final char[] validNonLetterOrDigitChars ) {
        super();
        this.minimumLength = minLength < 0 ? DEFAULT_MINIMUM_LENGTH : minLength;
        this.maximumLength = maxLength < 0 ? MAXIMUM_LENGTH : maxLength;
        this.caseSensitive = caseSensitive;
        this.replacementCharacter = replacementCharacter;
        if (this.minimumLength > this.maximumLength) {
            final String msg = ModelerCore.Util.getString("StringNameValidator.The_minimum_length_may_not_exceed_the_maximum_length"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (validNonLetterOrDigitChars == null) {
        	this.validNonLetterOrDigitChars = DEFAULT_VALID_NON_LETTER_OR_DIGIT_CHARS;
        } else {
	        this.validNonLetterOrDigitChars = validNonLetterOrDigitChars;
	        Arrays.sort(this.validNonLetterOrDigitChars);
        }

        this.existingNames = new ExistingNames(this.caseSensitive);
    }

    /**
     * @param name the name to add to the existing name collection (should not be <code>null</code>)
     */
    public boolean addExistingName(String name) {
        return this.existingNames.add(name);
    }

    /**
     * Clears the existing name collection.
     */
    public void clearExistingNames() {
        this.existingNames.clear();
    }

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator( final boolean caseSensitive ) {
        this(DEFAULT_MINIMUM_LENGTH, DEFAULT_MAXIMUM_LENGTH, caseSensitive, DEFAULT_REPLACEMENT_CHARACTER, null);
    }

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator( final char[] validNonLetterOrDigitChars ) {
        this(DEFAULT_MINIMUM_LENGTH, DEFAULT_MAXIMUM_LENGTH, DEFAULT_CASE_SENSITIVE_NAME_COMPARISON,
             DEFAULT_REPLACEMENT_CHARACTER, validNonLetterOrDigitChars);
    }

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator( final int maxLength,
                                final char[] validNonLetterOrDigitChars ) {
        this(DEFAULT_MINIMUM_LENGTH, maxLength, DEFAULT_CASE_SENSITIVE_NAME_COMPARISON, DEFAULT_REPLACEMENT_CHARACTER,
        		validNonLetterOrDigitChars);
    }

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator( final int minLength,
                                final int maxLength,
                                final char[] validNonLetterOrDigitChars ) {
        this(minLength, maxLength, DEFAULT_CASE_SENSITIVE_NAME_COMPARISON, DEFAULT_REPLACEMENT_CHARACTER, validNonLetterOrDigitChars);
    }

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator( final int minLength,
                                final int maxLength,
                                final boolean caseSensitive,
                                final char replacementCharacter ) {
        this(minLength, maxLength, caseSensitive, replacementCharacter, null);
    }

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator( final int minLength,
                                final int maxLength,
                                final boolean caseSensitive ) {
        this(minLength, maxLength, caseSensitive, DEFAULT_REPLACEMENT_CHARACTER, null);
    }

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator( final int minLength,
                                final int maxLength ) {
        this(minLength, maxLength, DEFAULT_CASE_SENSITIVE_NAME_COMPARISON, DEFAULT_REPLACEMENT_CHARACTER, null);
    }

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator( final int maxLength ) {
        this(DEFAULT_MINIMUM_LENGTH, maxLength, DEFAULT_CASE_SENSITIVE_NAME_COMPARISON, DEFAULT_REPLACEMENT_CHARACTER, null);
    }

    /**
     * Construct an instance of StringNameValidator.
     */
    public StringNameValidator() {
        this(DEFAULT_MINIMUM_LENGTH, DEFAULT_MAXIMUM_LENGTH, DEFAULT_CASE_SENSITIVE_NAME_COMPARISON,
             DEFAULT_REPLACEMENT_CHARACTER, null);
    }

    /**
     * @return
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * @return
     */
    public int getMaximumLength() {
        return maximumLength;
    }

    /**
     * @return
     */
    public int getMinimumLength() {
        return minimumLength;
    }

    /**
     * @return
     */
    public char getReplacementCharacter() {
        return replacementCharacter;
    }

    /**
     * Check whether the name length is between {@link #getMinimumLength()} and {@link #getMaximumLength()} (inclusive).
     * 
     * @param name the name to check; may not be null
     * @return a message stating what is wrong with the name, or null if the name is considered valid
     */
    public String checkNameLength( final String name ) {
        CoreArgCheck.isNotNull(name);
        final int strLength = name.length();
        if (strLength < getMinimumLength()) {
            final Object[] params = new Object[] {new Integer(getMinimumLength())};
            final String msg = ModelerCore.Util.getString("StringNameValidator.MinLengthFailure", params); //$NON-NLS-1$
            return msg;
            // check the entity name length agaist a desired value
        } else if (strLength > getMaximumLength()) {
            final Object[] params = new Object[] {new Integer(strLength), new Integer(getMaximumLength())};
            final String msg = ModelerCore.Util.getString("StringNameValidator.The_name_length_({0})_is_longer_than_allowed_({1})", params); //$NON-NLS-1$
            return msg;
        }

        // Valid, so return no error message
        return null;
    }

    /**
     * Check whether the characters in the name are considered valid. The first character must be an alphabetic character;
     * remaining characters must be either alphabetic characters, digits or the underscore ('_') character. Any characters that
     * are in the {@link #getInvalidCharacters() invalid set} will also fail validation
     * 
     * @param name the name to be checked; may not be null
     * @return a message stating what is wrong with the name, or null if the name is considered valid
     */
    public String checkNameCharacters( final String name ) {
        CoreArgCheck.isNotNull(name);

        // Go through the string and ensure that each character is valid ...
        
        int length = name.length();
        
        if (length == 0) {
        	return null;
        }
        
        char c = name.charAt(0);

        String msg = isValidInitialChar(c);
        
        if (msg != null) {
        	return msg;
        }
        
        for (int index = 1; index < length; index++) {
        	c = name.charAt(index);
            msg = isValidChar(c, index);
            if (msg != null) {
            	return msg;
            }
        }

        // Valid, so return no error message
        return null;
    }

	protected String isValidChar(char c, int index) {
		if ( isValidInitialChar(c) != null ) {
			if (!Character.isDigit(c) && !isValidNonLetterOrDigit(c) ) {
			    final Object[] params = new Object[] {new Character(c), new Integer(index), getValidNonLetterOrDigitMessageSuffix()};
			    return ModelerCore.Util.getString("StringNameValidator.The_character___{0}___(at_position_{1})_is_not_allowed;_only_alphabetic,_digit_or_underscore", params); //$NON-NLS-1$
			} 
		} 
		return null;
	}
    
    protected String isValidInitialChar(char c) {
    	if (!Character.isLetter(c)) {
            final Object[] params = new Object[] {new Character(c)};
            final String msg = ModelerCore.Util.getString("StringNameValidator.The_first_character_of_the_name_({0})_must_be_an_alphabetic_character", params); //$NON-NLS-1$
            return msg;
        }
        return null;
    }
    
    
    /**
     * Allows additional non-letter or non-digit characters to be valid. Subclasses should override this method to add
     * additional valid characters.
     * @param c
     * @return true if valid character
     */
    public boolean isValidNonLetterOrDigit(char c) {
    	return Arrays.binarySearch(validNonLetterOrDigitChars, c) >= 0;
    }
    
    public String getValidNonLetterOrDigitMessageSuffix() {
    	return ModelerCore.Util.getString("StringNameValidator.or_other_valid_characters"); //$NON-NLS-1$
    }

    /**
     * This method returns whether the specified name consists of valid characters, and does <i>not include whether it is valid in
     * its namespace</i>. The following are the rules that are used by this method: <li>The name may not be null</li> <li>The name
     * may not be zero-length</li> <li>The name may not have a length greater than 255 characters</li> <li>The first character
     * must be an alphabetic character; remaining characters must be either alphabetic characters, digits or the underscore ('_')
     * character</li>
     * 
     * @param newName the name being considered
     * @return true if the name is a valid name (excluding context-sensitive naming rules), or false otherwise.
     */
    public boolean isValidName( final String name ) {
        final String reasonInvalid = checkValidName(name);
        if (reasonInvalid != null) {
            return false;
        }
        return true;
    }

    /**
     * This method returns whether the specified name consists of valid characters, and does <i>not include whether it is valid in
     * its namespace</i>. The following are the rules that are used by this method:
     * <ul>
     * <li>The name may not be null</li>
     * <li>The name may must have a length that is equal to or greater than {@link #getMinimumLength()}</li>
     * <li>The name may must have a length that is equal to or less than {@link #getMaximumLength()}</li>
     * <li>The first character must be an alphabetic character; remaining characters must be either alphabetic characters, digits
     * or the underscore ('_') character</li>
     * <li>The name may not contain {@link #getInvalidCharacters() invalid characters}</li>
     * </ul>
     * 
     * @param name the name being considered
     * @return a message which is a validation error, null if the name is valid.
     */
    public String checkValidName( final String name ) {
        // The name may not be null
        if (name == null) {
            final String msg = ModelerCore.Util.getString("StringNameValidator.The_name_may_not_be_null"); //$NON-NLS-1$
            return msg;
        }

        // Check the length of the name ...
        // the length is being seperately checked by a differrent method
        // which is invoked by String length rule, need not be checked twice.
        final String lengthMsg = checkNameLength(name);
        if (lengthMsg != null) {
            return lengthMsg;
        }

        // Check the characters in the name ...
        final String contentMsg = checkNameCharacters(name);
        if (contentMsg != null) {
            return contentMsg;
        }

        // If it passed all of the tests ...
        return null;
    }

    /**
     * Checks if the given name is a unique EObject within its container.
     * 
     * @param name The name that should be unique within its container
     * @param eObject The EObject to validate
     * @param nameFeatureID the ID of the feature that represents the name feature
     * @return a message stating what is wrong with the name, or null if the name is considered valid
     */
    public String checkUniqueness( final String name,
                                   final EObject eObject,
                                   final List siblings,
                                   final int nameFeatureID ) {

        CoreArgCheck.isNotNull(name);
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isNotNull(siblings);

        // get the metamodel URI for the eObject
        final String eObjUri = eObject.eClass().getEPackage().getNsURI();

        // check all the contents of the container
        final Iterator iter = siblings.iterator();
        int matchCntr = 0;
        while (iter.hasNext()) {
            final EObject sibling = (EObject)iter.next();
            // Process this sibling EXCEPT if the same object
            if (sibling != eObject) {
                final EClass siblingClass = sibling.eClass();
                final String siblingURI = siblingClass.getEPackage().getNsURI();
                // if sibling and th eObject belong to the same metamodel
                if (eObjUri.equals(siblingURI)) {
                    final EStructuralFeature eFeature = siblingClass.getEStructuralFeature(nameFeatureID);
                    // If the specified feature exists for this EObject
                    if (eFeature != null) {
                        final String siblingName = (String)sibling.eGet(eFeature);
                        // Increment the counter whenever a child has the specified name
                        if (siblingName != null) {
                            if ((isCaseSensitive() && siblingName.equals(name))
                                || (!isCaseSensitive() && siblingName.equalsIgnoreCase(name))) {
                                matchCntr++;
                            }
                        }
                    }
                }
            }
        }

        // if there is at least one match, create a problem
        if (matchCntr != 0) {
            final Object params = new Object[] {name, new Integer(matchCntr)};
            final String msg = ModelerCore.Util.getString("StringNameValidator.The_name_{0}_is_the_same_as_{1}_other_objects_under_the_same_parent", params); //$NON-NLS-1$
            return msg;
        }

        // No duplicates ...
        return null;
    }

    /**
     * Get a map of sibling to the number of other siblings its name would match.
     * 
     * @param siblings List of all the siblings
     * @param nameFeatureID the ID of the feature that represents the name feature
     */
    public Map getDuplicateNamesMap( final List siblings,
                                     final int nameFeatureID ) {
        CoreArgCheck.isNotNull(siblings);

        // ---------------------------------------------
        // Defect 22095 - needed to improve validation performance
        // Old method was finding the "Name" values inside the for() loops.
        // This is expensive and didn't need to be that way.
        // Basically the change does everything BUT do a check on Metamodel URI
        // OLD CODE = if(siblingURI1.equals(siblingURI2)) but Dennis said we shouldn't need it because the nameFeatureID is being
        // passed in.
        // ---------------------------------------------

        // Let's create a Map of sibling names to EObject for only those names that have this feature
        Map siblingNameMap = getSiblingsNameFeatureMap(siblings, nameFeatureID);

        Map objectCountMap = new HashMap();

        List siblingEObjects = new ArrayList(siblingNameMap.keySet());

        // check all the contents of the container
        int siblingSize = siblingEObjects.size();

        for (int i = 0; i < siblingSize; i++) {
            final EObject siblingA = (EObject)siblingEObjects.get(i);
            final String siblingNameA = (String)siblingNameMap.get(siblingA);
            for (int j = i + 1; j < siblingSize; j++) {
                final EObject siblingB = (EObject)siblingEObjects.get(j);
                final String siblingNameB = (String)siblingNameMap.get(siblingB);
                if ((isCaseSensitive() && siblingNameA.equals(siblingNameB))
                    || (!isCaseSensitive() && siblingNameA.equalsIgnoreCase(siblingNameB))) {

                    Integer matchCnt1 = (Integer)objectCountMap.get(siblingA);
                    Integer matchCnt2 = (Integer)objectCountMap.get(siblingB);

                    matchCnt1 = matchCnt1 == null ? INTEGER_ONE : new Integer(matchCnt1.intValue() + 1);
                    matchCnt2 = matchCnt2 == null ? INTEGER_ONE : new Integer(matchCnt2.intValue() + 1);
                    objectCountMap.put(siblingA, matchCnt1);
                    objectCountMap.put(siblingB, matchCnt2);
                }
            }
        }

        return objectCountMap;
    }

    private Map getSiblingsNameFeatureMap( final List siblings,
                                           final int nameFeatureID ) {
        int siblingSize = siblings.size();
        Map siblingNameMap = new HashMap();

        for (int i = 0; i < siblingSize; i++) {
            final EObject siblingEObject = (EObject)siblings.get(i);
            final EClass siblingClass = siblingEObject.eClass();
            final EStructuralFeature eFeature = siblingClass.getEStructuralFeature(nameFeatureID);
            // check if feature exists
            if (eFeature != null) {
                final Object featureValue = siblingEObject.eGet(eFeature);
                // check it the feature value is a string
                if (featureValue != null && featureValue instanceof String) {
                    siblingNameMap.put(siblingEObject, featureValue);
                }
            }
        }
        return siblingNameMap;
    }

    /**
     * Get a map of feature to the number of other features its name would match.
     * 
     * @param features List of all the features
     */
    public Map getDuplicateNamesMap( final List features ) {

        CoreArgCheck.isNotNull(features);

        Map objectCountMap = new HashMap();

        // check all the contents of the container
        int featuresSize = features.size();

        for (int i = 0; i < featuresSize; i++) {
            final EStructuralFeature eFeature1 = (EStructuralFeature)features.get(i);
            // check if feature exists
            if (eFeature1 != null) {
                final String featureName1 = eFeature1.getName();
                for (int j = i + 1; j < featuresSize; j++) {
                    final EStructuralFeature eFeature2 = (EStructuralFeature)features.get(j);
                    // check if feature exists
                    if (eFeature2 != null) {
                        final String featureName2 = eFeature2.getName();
                        // Increment the matchcounter whenever another feature has the specified name
                        if (featureName2 != null) {
                            if ((isCaseSensitive() && featureName1.equals(featureName2))
                                || (!isCaseSensitive() && featureName1.equalsIgnoreCase(featureName2))) {

                                Integer matchCnt1 = (Integer)objectCountMap.get(eFeature1);
                                Integer matchCnt2 = (Integer)objectCountMap.get(eFeature2);

                                matchCnt1 = matchCnt1 == null ? INTEGER_ONE : new Integer(matchCnt1.intValue() + 1);
                                matchCnt2 = matchCnt2 == null ? INTEGER_ONE : new Integer(matchCnt2.intValue() + 1);
                                objectCountMap.put(eFeature1, matchCnt1);
                                objectCountMap.put(eFeature2, matchCnt2);
                            }
                        }
                    }
                }
            }
        }

        return objectCountMap;
    }

    /**
     * This method modifies the value of name property of an model entity if it is invalid, and makes it valid. Currently, it
     * checks whether the entity's is fixed , contains invalid characters, or conflicts with a sibling's name. The name value
     * before the change is set as the alias property.
     * <p>
     * This is equivalent to calling {@link #createValidName(String, char[], int, boolean) createValidName(name,null,255,false)}.
     * </p>
     * 
     * @param name the name; may not be null
     * @return the new name, or null if the name was already valid (i.e., would be unchanged by this method)
     */
    public String createValidName( final String name ) {
        return createValidName(name, false);
    }

    /**
     * This method modifies the value of name property of an model entity if it is invalid, and makes it valid. Currently, it
     * checks whether the entity's is fixed , contains invalid characters, or conflicts with a sibling's name. The name value
     * before the change is set as the alias property.
     * <p>
     * This is equivalent to calling {@link #createValidName(String, char[], int, boolean)
     * createValidName(name,null,255,performValidityCheck)}.
     * </p>
     * 
     * @param name the name; may not be null
     * @param performValidityCheck true if validity checking should be performed, or false if the validity checking should be
     *        skipped
     * @return the new name, or null if the name was already valid (i.e., would be unchanged by this method)
     */
    public String createValidName( final String name,
                                   final boolean performValidityCheck ) {
        CoreArgCheck.isNotNull(name);

        // Otherwise, the name is presumed to be invalid ...
        StringBuffer newName = new StringBuffer(name.length());
        boolean changed = false;
        int initLength = name.length();
        final int maxLength = Math.min(initLength, getMaximumLength());

        int actualLength = 0;
        if (initLength > 0) {
	        // Go through the string and ensure that each character is valid ...
	        boolean foundInitialChar = false;
	        int index = 0;
	        for( char nextChar : name.toCharArray()) {
	        	index++;
	        	if( !foundInitialChar ) {
		        	String msg = isValidInitialChar(nextChar);
		            
		    	    if (msg == null) {
		    	    	foundInitialChar = true;
		    	    	changed = true;
		    	    	actualLength++;
		        		newName.append(nextChar);
		        	} else {
		        		changed = true;
		        	}
	        	} else {
	        		if( actualLength < maxLength ) {
	                    String msg = isValidChar(nextChar, index);
	                    if (msg != null) {
	               	    	changed = true;
	               	    	actualLength++;
	                    	newName.append(this.getReplacementCharacter());
	                    } else {
	                    	actualLength++;
	                    	newName.append(nextChar);
	                    }
	        		} else {
	        			break;
	        		}
	        	}
	        }
        }
        
        
//        if (length > 0) {
//        	char c = name.charAt(0);
//
//	        String msg = isValidInitialChar(c);
//        
//    	    if (msg != null) {
//    	    	changed = true;
//        		newName.setCharAt(0, this.getReplacementCharacter());
//        	}
//    	    for (int index = 1; index < maxLength; index++) {
//            	c = name.charAt(index);
//                msg = isValidChar(c, index);
//                if (msg != null) {
//           	    	changed = true;
//                	newName.setCharAt(index, this.getReplacementCharacter());
//                }
//            }
//        }

        while (newName.length() < getMinimumLength()) {
            changed = true;
        	newName.append(this.getReplacementCharacter());
        }
        
        if (newName.length() > maxLength) {
        	changed = true;
        	newName.delete(maxLength, newName.length());
        }
        
        if (changed) {
        	return newName.toString();
        }

        // Valid, so return no error message
        return null;
    }

    /**
     * Create a valid name that is does not match the supplied set of "existing" names.
     * 
     * @param name the name to be made valid; may not be null
     * @return the new name, or null if the name was already valid (i.e., would be unchanged by this method)
     */
    public String createValidUniqueName( final String name ) {
        String result = null;

        // Create a valid name ...
        String validName = createValidName(name);
        if (validName == null) validName = name; // Already was valid ...
        else result = validName;

        // Create a unique name ...
        final String uniqueName = createUniqueName(validName);
        if (uniqueName != null) {
            // Was not unique ...
            result = uniqueName;
        }
        // Return the result; may be null if name was already valid and unique
        return result;
    }

    /**
     * Create a name that is does not match the supplied set of "existing" names.
     * 
     * @param name the name to be made valid; may not be null
     * @return the new name, or null if the name was already unique (i.e., would be unchanged by this method)
     */
    public String createUniqueName(final String name) {
        CoreArgCheck.isNotNull(name);

        // Compute the counter at which we have to start taking away characters ...
        final int roomForCounterChars = Math.max(0, this.getMaximumLength() - name.length());
        final int counterToStartRemoving = ((int)Math.pow(10, roomForCounterChars)) - 1;

        String theName = name;
        boolean changed = false;
        int counter = 0;
        while (true) {
            if (addExistingName(theName)) {
                // It is unique, so return ...
                return (changed ? theName : null);
            }

            // The name is not unique, so compute a new one ...
            ++counter;

            // First check the length ...
            final int length = name.length();
            if (counter > counterToStartRemoving) {
                // Must make room for the counter ...
                int numCharsToRemove = 0;
                if (counter - counterToStartRemoving < 10) {
                    numCharsToRemove = 1;
                } else if (counter - counterToStartRemoving < 100) {
                    numCharsToRemove = 2;
                } else if (counter - counterToStartRemoving < 1000) {
                    numCharsToRemove = 3;
                } else if (counter - counterToStartRemoving < 10000) {
                    numCharsToRemove = 4;
                } else if (counter - counterToStartRemoving < 100000) {
                    numCharsToRemove = 5;
                } else {
                    numCharsToRemove = length + 1; // will force a failure
                }
                // See if there are enough characters to remove ...
                if (length > numCharsToRemove) {
                    theName = name.substring(0, length - numCharsToRemove) + counter;
                    changed = true;
                } else {
                    final Object[] params = new Object[] {name};
                    final String msg = ModelerCore.Util.getString("StringNameValidator.Unable_to_make_the_name_{0}_unique_within_the_limits_of_the_maximum_length", params); //$NON-NLS-1$
                    throw new ModelerCoreRuntimeException(msg);
                }
            } else {
                // Simply append the counter ...
                changed = true;
                theName = name + counter;
            }
        }
    }

    class Node implements Comparable {

        final char content;
        boolean marker;
        private TreeMap<Character, Node> kids;

        Node( char c ) {
            this.content = c;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo( Object obj ) {
            Node that = (Node)obj;

            if (this.content == that.content) {
                return 0;
            }

            return (this.content - that.content);
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( Object obj ) {
            if (this == obj) {
                return true;
            }

            if ((obj == null) || !getClass().equals(obj.getClass())) {
                return false;
            }

            return (this.content == ((Node)obj).content);
        }

        void clearChildren() {
            if (this.kids != null) {
                this.kids.clear();
            }
        }

        TreeMap getChildren() {
            if (this.kids == null) {
                this.kids = new TreeMap<Character, Node>();
            }

            return this.kids;
        }

        boolean hasChildren() {
            return ((this.kids != null) && !this.kids.isEmpty());
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return Character.valueOf(this.content).hashCode();
        }

        Node subNode( char c ) {
            if (this.kids == null) {
                return null;
            }

            return this.kids.get(c);
        }
    }

    private class ExistingNames {

        private final boolean caseSensitive;
        private Node root;

        public ExistingNames( boolean caseSensitive ) {
            this.caseSensitive = caseSensitive;
            this.root = new Node(' ');
        }

        public boolean add( String name ) {
            boolean added = false;

            if (!this.caseSensitive) {
                name = name.toUpperCase();
            }

            Node current = this.root;

            for (char c : name.toCharArray()) {
                Node child = current.subNode(c);

                if (child == null) {
                    Node newKid = new Node(c);
                    current.getChildren().put(c, newKid);
                    current = newKid;
                    added = true;
                } else {
                    current = child;
                }
            }

            if (added || (current.marker == false)) {
                // set marker to indicate end of a name
                current.marker = true;
                added = true;
            }

            return added;
        }

        private void buildNodeNameList( String name,
                                        Node node,
                                        StringBuilder txt ) {
            // add if node is an existing name
            if (node.marker) {
                txt.append(name).append(',');
            }

            // process children
            for (Object obj : node.getChildren().values()) {
                Node kid = (Node)obj;
                buildNodeNameList(name + kid.content, kid, txt);
            }
        }

        public void clear() {
            this.root.clearChildren();
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder txt = new StringBuilder();
            buildNodeNameList("", this.root, txt); //$NON-NLS-1$
            return txt.substring(0, txt.length() - 1).toString(); // remove last comma
        }
    }

    //    
    // private static String getUniqueString(String name, Collection siblingNames) {
    // Iterator siblingIter = siblingNames.iterator();
    // while(siblingIter.hasNext()) {
    // String sibName = (String) siblingIter.next();
    // if(sibName.equalsIgnoreCase(name)) {
    // char lastChar = name.charAt(name.length()-1);
    // int intValue = 0;
    // if(Character.isDigit(lastChar)) {
    // intValue = Character.getNumericValue(lastChar);
    // name = name.substring(0, name.length()-1) + intValue;
    // } else {
    // intValue++;
    // name = name + intValue;
    // }
    // name = getUniqueString(name, siblingNames);
    // }
    // }
    //
    // return name;
    // }
}
