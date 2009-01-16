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

import java.util.List;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.search.ISearchEngine;
import com.metamatrix.modeler.core.search.runtime.RelationshipRecord;
import com.metamatrix.modeler.core.search.runtime.SearchRecord;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.ui.search.MetadataMatchInfo;
import com.metamatrix.modeler.ui.search.MetadataSearchQuery;
import com.metamatrix.modeler.ui.search.MetadataSearchResult;

/**
 * A <code>RelationshipSearchQuery</code> executes the search using the search engine passed in at construction and creates the {
 * {@link RelationshipSearchResult}.
 * 
 * @since 6.0.0
 */
public class RelationshipSearchQuery extends MetadataSearchQuery {

    /**
     * Constructs a relationship search query.
     * 
     * @param search the search engine
     * @since 6.0.0
     */
    public RelationshipSearchQuery( ISearchEngine searchEngine ) {
        super(searchEngine);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.search.MetadataSearchQuery#constructEmptyResults()
     */
    @Override
    protected MetadataSearchResult constructEmptyResults() {
        return new RelationshipSearchResult(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.search.MetadataSearchQuery#getLabel()
     */
    @Override
    public String getLabel() {
        return UiConstants.Util.getString(I18nUtil.getPropertyPrefix(RelationshipSearchQuery.class) + "query.msg"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.ui.search.MetadataSearchQuery#processSearchEngineResults(java.util.List)
     */
    @Override
    protected void processSearchEngineResults( List<SearchRecord> records ) {
        RelationshipSearchResult result = (RelationshipSearchResult)getSearchResult();

        for (SearchRecord record : records) {
            RelationshipRecord relRec = (RelationshipRecord)record;
            RelationshipMatch match = new RelationshipMatch(new MetadataMatchInfo(relRec.getResourcePath(), result), relRec);
            result.addMatch(match);
        }
    }
}
