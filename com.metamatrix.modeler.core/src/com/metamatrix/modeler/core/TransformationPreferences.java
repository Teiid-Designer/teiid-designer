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
     * Sets the default upper recursion limit
     * 
     * @param the new default upper recursion limit
     */
    public void setUpperRecursionLimit(int val);
    
    /**
     * Get all the default upper recursion limit property
     * @return default string length.
     */
    public boolean getRemoveDuplicateAttibutes();
    
    /**
     * Sets the default upper recursion limit
     * 
     * @param the new default upper recursion limit
     */
    public void setRemoveDuplicateAttibutes(boolean val);
    
}
