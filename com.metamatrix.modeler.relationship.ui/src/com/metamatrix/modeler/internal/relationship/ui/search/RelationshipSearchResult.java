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
