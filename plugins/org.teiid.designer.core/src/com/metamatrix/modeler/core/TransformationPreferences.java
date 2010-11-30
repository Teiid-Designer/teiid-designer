/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

/**
 * TransformationPreferences
 */
public interface TransformationPreferences {

    /**
     * Preference name for transformation default string length
     */
    String DEFAULT_STRING_LENGTH_KEY = "transformationPreferences.defaultStringLength"; //$NON-NLS-1$
    
    /**
     * Preference name for transformation default string length
     */
    String UPPER_RECURSION_LIMIT_KEY = "transformationPreferences.upperRecursionLimit"; //$NON-NLS-1$
    
    /**
     * Preference name for mapping remove duplicate attributes on merge
     */
    String REMOVE_DUPLICATE_ATTRIBUTES_KEY = "transformationPreferences.removeDuplicateAttributes"; //$NON-NLS-1$
    
    /**
     * Get all the default string length property
     * @return default string length.
     */
    public int getDefaultStringLength();
    
    /**
     * @return the default value of the default length of a string
     */
    int getDefaultStringLengthDefault();
    
    /**
     * Sets the default string length
     * 
     * @param the new default upper recursion limit
     */
    public void setDefaultStringLength(int val);

    /**
     * Get all the default upper recursion limit property
     * @return default string length.
     */
    public int getUpperRecursionLimit();
    
    /**
     * @return the upper recursion limit default value
     */
    int getUpperRecursionLimitDefault();
    
    /**
     * Sets the default upper recursion limit
     * 
     * @param the new default upper recursion limit
     */
    public void setUpperRecursionLimit(int val);
    
    /**
     * @return <code>true</code> if duplicate attributes should be removed
     */
    public boolean getRemoveDuplicateAttibutes();
    
    /**
     * @return <code>true</code> if the default value is to remove duplicate attributes
     */
    boolean getRemoveDuplicateAttibutesDefault();
    
    /**
     * Sets the default upper recursion limit
     * 
     * @param the new default upper recursion limit
     */
    public void setRemoveDuplicateAttibutes(boolean val);
    
}
