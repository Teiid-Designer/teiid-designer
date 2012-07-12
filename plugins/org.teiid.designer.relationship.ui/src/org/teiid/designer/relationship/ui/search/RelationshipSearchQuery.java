/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.search;

import java.util.List;

import org.teiid.core.util.I18nUtil;
import org.teiid.designer.core.search.ISearchEngine;
import org.teiid.designer.core.search.runtime.RelationshipRecord;
import org.teiid.designer.core.search.runtime.SearchRecord;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.ui.search.MetadataMatchInfo;
import org.teiid.designer.ui.search.MetadataSearchQuery;
import org.teiid.designer.ui.search.MetadataSearchResult;


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
     * @see org.teiid.designer.ui.search.MetadataSearchQuery#constructEmptyResults()
     */
    @Override
    protected MetadataSearchResult constructEmptyResults() {
        return new RelationshipSearchResult(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.search.MetadataSearchQuery#getLabel()
     */
    @Override
    public String getLabel() {
        return UiConstants.Util.getString(I18nUtil.getPropertyPrefix(RelationshipSearchQuery.class) + "query.msg"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.search.MetadataSearchQuery#processSearchEngineResults(java.util.List)
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
