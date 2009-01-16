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

package com.metamatrix.modeler.core.search;

import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;

/**
 * The MetadataSearch interface provides a mechanism to search for {@link EObject} instances
 * that satisfy a number of criteria.  Possible criteria include:
 * <ul>
 *  <li>Metaclass representing the EClass of the EObject
 *  <li>Datatype if the EObject has a type feature referencing XSDSimpleTypeDefinition instances
 *  <li>RuntimeType if the EObject has a type feature referencing XSDSimpleTypeDefinition instances
 *  <li>Specific EObject features such as name, UUID, or description.
 * </ul>
 */
public interface MetadataSearch extends ISearchEngine {
    
    // =========================================================================
    //                      Constants
    // =========================================================================
    
    public static final String TEXT_PATTERN_ANY_STRING  = IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
    public static final String TEXT_PATTERN_ANY_CHAR    = IndexConstants.RECORD_STRING.SINGLE_CHAR_MATCH_STRING;
    public static final String TEXT_PATTERN_ESCAPE_CHAR = "\\"; //$NON-NLS-1$
    
    public static final String NAME_SEARCH_FEATURE        = ModelerCore.Util.getString("MetadataSearch.AllowableNameFeature"); //$NON-NLS-1$
    public static final String DESCRIPTION_SEARCH_FEATURE = ModelerCore.Util.getString("MetadataSearch.AllowableDescriptionFeature"); //$NON-NLS-1$
    public static final String UUID_SEARCH_FEATURE        = ModelerCore.Util.getString("MetadataSearch.AllowableUuidFeature"); //$NON-NLS-1$
    public static final String OBJECT_URI_FEATURE         = ModelerCore.Util.getString("MetadataSearch.AllowableObjectUriFeature"); //$NON-NLS-1$
    public static final String[] ALLOWABLE_SEARCH_FEATURES = new String[] {NAME_SEARCH_FEATURE,
                                                                           DESCRIPTION_SEARCH_FEATURE,
                                                                           UUID_SEARCH_FEATURE,
                                                                           OBJECT_URI_FEATURE};
    
    /**
     * The default value for whether the property search results should include only
     * results that match the specified pattern or exclude results that match the specified
     * pattern.
     * By default, only results that match to specified pattern are included.
     */
    public static final boolean DEFAULT_CONTAINS_PATTERN = true;
    
    /**
     * The default value for whether subtypes of simple datatypes should be included in the search
     * By default, subtypes are to be included.
     */
    public static final boolean DEFAULT_INCLUDE_SUBTYPES = false;


    // =========================================================================
    //                      Search Criteria
    // =========================================================================
    
    /**
     * Convenience method to obtain the ModelWorkspace object for the search.
     * @return the model workspace; never null
     */
    public ModelWorkspace getModelWorkspace();
    
    /**
     * Return the specific metamodel class to use in the search or null
     * if any metamodel class is allowed. 
     * @return
     * @since 4.1
     */
    public EClass getMetaClass();
    
    /**
     * Return true if the specified metamodel class is a typed class
     * meaning that EClass contains EStructuralFeatures that reference
     * datatypes.
     * @return
     * @since 4.1
     */
    public boolean isTypedMetaClass(EClass metaClass);
    
    /**
     * Set the metamodel class to use in the search. 
     * @param metaClass
     * @since 4.1
     */
    public void setMetaClass(EClass metaClass);
    
    /**
     * Return the array of <code>EObject</code> instances representing
     * all datatypes found in the workspace.
     * @return
     */
    public EObject[] getDatatypes();
    
    /**
     * Return the array of <code>String</code> instances representing
     * all runtime types supported by the server.
     * @return
     */
    public String[] getRuntimeTypes();
    
    /**
     * Return the array of <code>String</code> instances representing
     * the features or properties whose textual value can be searched.
     * @return
     */
    public String[] getFeaturesNames();
    
    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.core.search.ISearchEngine#getSearchCriteria()
     */
    String getSearchCriteria();
    
    /**
     * Return the name feature of the feature to be used for executing the search. 
     * @return
     * @since 4.1
     */
    public String getSelectedFeatureName();

    /**
     * Get the property matching pattern for the search.  The pattern may be null, zero-length
     * or {@link #TEXT_PATTERN_ANY_STRING "*"} if the text is not to be used in the criteria.  If the text
     * is to be evaluated against the criteria, then the pattern should consist of combinations of literal
     * characters to be match, the {@link #NAME_PATTERN_ANY_STRING "*"} for matching any number of characters,
     * the {@link #NAME_PATTERN_ANY_CHAR "?"} for matching any single character, and
     * {@link #NAME_PATTERN_ESCAPE_CHAR "\"} for escaping the "*", "?" or "\" characters.
     * @return the text pattern; may be null or zero-length if there is no criteria
     * @see #setFeatureCriteria(String, boolean)
     */
    public String getFeatureCriteria();

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
     * @see #getFeatureCriteria()
     */
    public void setFeatureCriteria( String featureName, String textPattern, boolean containsPattern );
    
    /**
     * Return the EObject representing the simple datatype that must be matched 
     * @return
     * @since 4.1
     */
    public EObject getDatatype();
    
    /**
     * Return whether the datatype search should return EObjects that reference a datatype
     * that is a subtype of the specified type.
     * @return true if subtypes of the are to be included, or false if only datatypes that 
     * exactly reference the specified type are to be found.
     * @see #setDatatype(EObject, boolean)
     * @see #getDatatype()
     */
    public boolean isIncludeSubtypes();
    
    /**
     * Set the EObject representing the simple datatype that must be matched 
     * @param datatype
     * @param includeSubtypes
     * @since 4.1
     */
    public void setDatatype( EObject datatype, boolean includeSubtypes );
    
    /**
     * Return the runtime type that must be matched 
     * @return
     * @since 4.1
     */
    public String getRuntimeType();
    
    /**
     * Set the runtime type that must be matched. 
     * @param runtimeType
     * @since 4.1
     */
    public void setRuntimeType( String runtimeType );
    
    /**
     * Get the scope of models that should be searched.  If no 
     * {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem} are supplied or the list
     * includes the whole {@link com.metamatrix.modeler.core.workspace.ModelWorkspace}, then all
     * models in the workspace will be searched.  Otherwise, only those 
     * {@link com.metamatrix.modeler.core.workspace.ModelResource ModelResources} that are specified or below
     * the specified <code>modelWorkspaceItems</code> will be searched.
     * @return the list of {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem}
     * instances that define the search scope; may not be null but may be empty
     */
    public List getModelScope();

    /**
     * Set the scope of models that should be searched.  If no 
     * {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem} are supplied or the list
     * includes the whole {@link com.metamatrix.modeler.core.workspace.ModelWorkspace}, then all
     * model in the workspace will be searched.  Otherwise, only those 
     * {@link com.metamatrix.modeler.core.workspace.ModelResource ModelResources} that are specified or below
     * the specified <code>modelWorkspaceItems</code> will be searched.
     * @param modelWorkspaceItems the list of {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem}
     * instances that define the search scope; may not be null but may be empty
     */
    public void setModelScope( List modelWorkspaceItems );
}
