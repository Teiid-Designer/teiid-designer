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

package com.metamatrix.modeler.core.search.commands;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;

import com.metamatrix.modeler.core.index.IndexSelector;

/**
 * This interface is used to find typed objects based on datatype criteria.
 */
public interface FindObjectCommand extends SearchCommand {

    /**
     * Return a collection of record
     * objects that are found on this command execution. 
     * @return a collection of records
     */
    Collection getRecordInfo();
    
    /**
     * Set the IndexSelector that will be used to obtain models that will be searched.
     * @param selector the index selector that should be used, or null if the 
     * {@link com.metamatrix.modeler.internal.core.index.ModelWorkspaceIndexSelector} should be used
     */
    void setIndexSelector( IndexSelector selector );

    /**
     * Set the metamodel class to use in the search. 
     * @param metaClass
     * @since 4.1
     */
    public void setMetaClass(EClass metaClass);
    
    /**
     * Set the property matching criteria for the search.  The pattern may be null, zero-length
     * or {@link #NAME_PATTERN_ANY_STRING "*"} if the text is not to be used in the criteria.  If the text
     * is to be evaluated against the criteria, then the pattern should consist of combinations of literal
     * characters to be match, the {@link #NAME_PATTERN_ANY_STRING "*"} for matching any number of characters,
     * the {@link #NAME_PATTERN_ANY_CHAR "?"} for matching any single character, and
     * {@link #NAME_PATTERN_ESCAPE_CHAR "\"} for escaping the "*", "?" or "\" characters.
     * @param featureName the feature name for the search
     * @param textPattern the text matching pattern
     * @param containsPattern true if the specified pattern is to be matched, or false if matches are to be excluded
     * in the results 
     * @see #isFeatureCriteriaCaseSensitive()
     * @see #getFeatureCriteria()
     */
    public void setFeatureCriteria( String featureName, String textPattern, boolean containsPattern );

}
