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

package com.metamatrix.modeler.relationship;

import java.util.List;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.search.ISearchEngine;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;

/**
 * The RelationshipSearch interface provides a mechanism to search for {@link Relationship} instances
 * that satisfy a number of criteria.  Possible criteria include:
 * <ul>
 *  <li>The {@link Relationship#getType() type} that Relationship instances must have; this
 *      may be a specific {@link RelationshipType} instance, may be {@link #ANY_RELATIONSHIP_TYPE any type},
 *      or may require that there be {@link #NO_RELATIONSHIP_TYPE no type}.  If a specific
 *      RelationshipType is required, then optionally the criteria may restrict it to be that
 *      exact type, or may instead allow any types that are direct or indirect
 *      {@link RelationshipType#getSubType() subtypes} of the supplied type.
 *      This criteria is set using {@link #setRelationshipTypeCriteria(RelationshipType, boolean)}.</li>
 *  <li>The scope of Relationship models that are to be searched.  If this criteria is used,
 *      then only the {@link Relationship} instances in the supplied models will be considered.
 *      This criteria is set using {@link #setRelationshipModelScope(List)}.</li>
 *  <li></li>
 *  <li></li>
 * </ul>
 */
public interface RelationshipSearch extends ISearchEngine {
    
    // =========================================================================
    //                      Constants
    // =========================================================================
    
    public static final String NAME_PATTERN_ANY_STRING = "*"; //$NON-NLS-1$
    public static final String NAME_PATTERN_ANY_CHAR = "?"; //$NON-NLS-1$
    public static final String NAME_PATTERN_ESCAPE_CHAR = "\\"; //$NON-NLS-1$
    
    /**
     * The default value for the {@link #getNameCriteria() name criteria}.  By default, any
     * name pattern satisfies the criteria (or there is no name criteria).
     * @see #getNameCriteria()
     * @see #isNameCriteriaCaseSensitive()
     * @see #setNameCriteria(String, boolean)
     */
    public static final String DEFAULT_NAME_CRITERIA = NAME_PATTERN_ANY_STRING;
    
    /**
     * The default value for whether the {@link #getNameCriteria() name criteria} is case sensitive.
     * By default, name criteria is case sensitive.
     * @see #getNameCriteria()
     * @see #isNameCriteriaCaseSensitive()
     * @see #setNameCriteria(String, boolean)
     */
    public static final boolean DEFAULT_NAME_CASE_SENSITIVE = true;
    
    /**
     * The default value for whether subtypes should satisfy the 
     * {@link #getRelationshipTypeCriteria() type criteria}.  By default, subtypes are to be included.
     * @see #setRelationshipTypeCriteria(RelationshipType, boolean)
     * @see #isIncludeSubtypes()
     */
    public static final boolean DEFAULT_INCLUDE_SUBTYPES = true;
    
    /**
     * The singleton {@link RelationshipType} instance that denotes that {@link Relationship#getType() type}
     * should be null on {@link Relationship} instances returned from the search.
     * @see #setRelationshipTypeCriteria(RelationshipType, boolean)
     * @see #getRelationshipTypeCriteria()
     */
    public static final RelationshipType NO_RELATIONSHIP_TYPE = RelationshipFactory.eINSTANCE.createRelationshipType();

    /**
     * The singleton {@link RelationshipType} instance that denotes that {@link Relationship#getType() type}
     * isn't involved in the search criteria.
     * @see #setRelationshipTypeCriteria(RelationshipType, boolean)
     * @see #getRelationshipTypeCriteria()
     */
    public static final RelationshipType ANY_RELATIONSHIP_TYPE = RelationshipFactory.eINSTANCE.createRelationshipType();


    // =========================================================================
    //                      Search Criteria
    // =========================================================================
    
    /**
     * Convenience method to obtain the ModelWorkspace object for the search.
     * @return the model workspace; never null
     * @see #getRelationshipModelScope()
     * @see #getParticipantsCriteria()
     * @see #setRelationshipModelScope(List)
     * @see #setParticipantsCriteria(List)
     */
    public ModelWorkspace getModelWorkspace();
    
    /**
     * Get the search criteria for the participants.  If any 
     * {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem} are supplied, only relationships
     * that have at least one participant in those ModelWorkspaceItems will be found.
     * If the list of models is empty or is the {@link com.metamatrix.modeler.core.workspace.ModelWorkspace},
     * this criteria is not used in the search.
     * @return the list of {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem}
     * instances in which must exist at least one participant in the relationships; never null,
     * but may be empty if this criteria is not be used
     */
    public List getParticipantsCriteria();

    /**
     * Set the search criteria for the participants.  If any 
     * {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem} are supplied, only relationships
     * that have at least one participant in those ModelWorkspaceItems will be found.
     * If the list of models is null is the {@link com.metamatrix.modeler.core.workspace.ModelWorkspace},
     * this criteria is not used in the search.
     * @param modelWorkspaceItems the list of {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem}
     * instances; If the list is not set or a null value is set, the whole workspace is searched,
     * if an empty list is set then the search results in no results.
     */
    public void setParticipantsCriteria( List modelWorkspaceItems );

    /**
     * Get the name matching pattern for the search.  The pattern may be null, zero-length
     * or {@link #NAME_PATTERN_ANY_STRING "*"} if the name is not to be used in the criteria.  If the name
     * is to be evaluated against the criteria, then the pattern should consist of combinations of literal
     * characters to be match, the {@link #NAME_PATTERN_ANY_STRING "*"} for matching any number of characters,
     * the {@link #NAME_PATTERN_ANY_CHAR "?"} for matching any single character, and
     * {@link #NAME_PATTERN_ESCAPE_CHAR "\"} for escaping the "*", "?" or "\" characters.
     * @return the name pattern; may be null or zero-length if there is no criteria
     * @see #isNameCriteriaCaseSensitive()
     * @see #setNameCriteria(String, boolean)
     */
    public String getNameCriteria();
    
    /**
     * Return whether the {@link #getNameCriteria() name criteria pattern} is case sensitive.
     * @return true if the name pattern is case sensitive, or false if case insensitive.
     * @see #setNameCriteria(String, boolean)
     * @see #getNameCriteria()
     */
    public boolean isNameCriteriaCaseSensitive();

    /**
     * Set the search criteria for the name of the relationships.  The pattern may be null, zero-length
     * or {@link #NAME_PATTERN_ANY_STRING "*"} if the name is not to be used in the criteria.  If the name
     * is to be evaluated against the criteria, then the pattern should consist of combinations of literal
     * characters to be match, the {@link #NAME_PATTERN_ANY_STRING "*"} for matching any number of characters,
     * the {@link #NAME_PATTERN_ANY_CHAR "?"} for matching any single character, and
     * {@link #NAME_PATTERN_ESCAPE_CHAR "\"} for escaping the "*", "?" or "\" characters.
     * @param namePattern the name matching pattern
     * @param caseSensitive true if the name is to be match case sensitively, or false if the criteria
     * is case insensitive
     * @see #isNameCriteriaCaseSensitive()
     * @see #getNameCriteria()
     */
    public void setNameCriteria( String namePattern, boolean caseSensitive );

    /**
     * Get the RelationshipType that is currently to be matched when searching.  May be 
     * {@link #ANY_RELATIONSHIP_TYPE any type} (specifying that the type is not used as criteria),
     * {@link #NO_RELATIONSHIP_TYPE no type} (specifying that the relationship should have no type),
     * or null if the type hasn't been set (resulting in {@link #canExecute() failure to execute}).
     * @return the RelationshipType instance
     * @see #setRelationshipTypeCriteria(RelationshipType, boolean)
     * @see #isIncludeSubtypes()
     */
    public RelationshipType getRelationshipTypeCriteria();
    
    /**
     * Return whether the search should return relationships with a {@link Relationship#getType() type}
     * that is a subtype of the {@link #getRelationshipTypeCriteria() type}.
     * @return true if subtypes of the {@link #getRelationshipTypeCriteria() type} are to be included,
     * or false if only relationships that exactly reference the {@link #getRelationshipTypeCriteria() type}
     * are to be found.
     * @see #setRelationshipTypeCriteria(RelationshipType, boolean)
     * @see #getRelationshipTypeCriteria()
     */
    public boolean isIncludeSubtypes();

    /**
     * Set the RelationshipType that for the search criteria.  May be 
     * {@link #ANY_RELATIONSHIP_TYPE any type} (specifying that the type is not used as criteria),
     * or {@link #NO_RELATIONSHIP_TYPE no type} (specifying that the relationship should have no type).
     * @param requiredType the RelationshipType instance that is to be the type criteria; should be
     * {@link #ANY_RELATIONSHIP_TYPE} if there is to be no criteria on the type, or null if the type
     * still has to be set (resulting in {@link #canExecute() failure to execute}).
     *  (equivalent to passing {@link #ANY_RELATIONSHIP_TYPE}
     * @param includeSubtypes false if relationships returned will have a {@link Relationship#getType() type}
     * that is exactly the supplied type, or true if relationships returned will have a 
     * {@link Relationship#getType() type} that is a (direct or indirect) subtype of the supplied type;
     * has no effect if <code>null</code> or {@link #ANY_RELATIONSHIP_TYPE} is supplied as the 
     * <code>requiredType</code>
     * @see #getRelationshipTypeCriteria()
     * @see #isIncludeSubtypes()
     */
    public void setRelationshipTypeCriteria( RelationshipType requiredType, boolean includeSubtypes);

    /**
     * Get the scope of Relationship models that should be searched.  If no 
     * {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem} are supplied or the list
     * includes the whole {@link com.metamatrix.modeler.core.workspace.ModelWorkspace}, then all Relationship
     * model in the workspace will be searched.  Otherwise, only those 
     * {@link com.metamatrix.modeler.core.workspace.ModelResource ModelResources} that are specified or below
     * the specified <code>modelWorkspaceItems</code> will be searched.
     * @return the list of {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem}
     * instances that define the search scope; may not be null but may be empty
     */
    public List getRelationshipModelScope();

    /**
     * Set the scope of Relationship models that should be searched.  If no 
     * {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem} are supplied or the list
     * includes the whole {@link com.metamatrix.modeler.core.workspace.ModelWorkspace}, then all Relationship
     * model in the workspace will be searched.  Otherwise, only those 
     * {@link com.metamatrix.modeler.core.workspace.ModelResource ModelResources} that are specified or below
     * the specified <code>modelWorkspaceItems</code> will be searched.
     * @param modelWorkspaceItems the list of {@link com.metamatrix.modeler.core.workspace.ModelWorkspaceItem}
     * instances that define the search scope; may not be null but may be empty
     */
    public void setRelationshipModelScope( List modelWorkspaceItems );
}
