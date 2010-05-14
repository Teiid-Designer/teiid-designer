/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship.ui.search;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.ui.search.MetadataSearchQuery;
import com.metamatrix.modeler.ui.search.MetadataSearchResult;

/**
 * The <code>RelationshipSearchResult</code> class is the result object for a relationship search. A
 * {@link RelationshipSearchQuery} is used to construct a result instance. Then {@link MetadataMatch}es are added later.
 * 
 * @since 6.0
 */
public class RelationshipSearchResult extends MetadataSearchResult {

    /**
     * Constructs a result for the specified query. Relationship matches must be added to the result using
     * {@link #setMatches(List)}.
     * 
     * @param the query used to construct the result
     * @since 6.0.0
     */
    public RelationshipSearchResult( RelationshipSearchQuery query ) {
        super(query);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.search.MetadataSearchResult#getLabel()
     */
    @Override
    public String getLabel() {
        return UiConstants.Util.getString(I18nUtil.getPropertyPrefix(RelationshipSearchResult.class) + "result.msg", //$NON-NLS-1$ 
                                          new Object[] {((MetadataSearchQuery)getQuery()).getSearchCriteria(), getMatchCount()});
    }
}
