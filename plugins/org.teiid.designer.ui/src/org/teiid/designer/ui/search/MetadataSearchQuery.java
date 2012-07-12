/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.search;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.core.search.ISearchEngine;
import org.teiid.designer.core.search.runtime.ResourceObjectRecord;
import org.teiid.designer.core.search.runtime.SearchRecord;
import org.teiid.designer.ui.UiConstants;


/**
 * A <code>MetadataSearchQuery</code> executes the search using the search engine passed in at construction and creates the {
 * {@link MetadataSearchResult}.
 * 
 * @since 6.0.0
 */
public class MetadataSearchQuery implements ISearchQuery, UiConstants {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The search engine that executes the search.
     * 
     * @since 6.0.0
     */
    private ISearchEngine searchEngine;

    /**
     * The search results.
     * 
     * @since 6.0.0
     */
    private MetadataSearchResult result;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs a metadata search query.
     * 
     * @param search the search engine
     * @since 6.0.0
     */
    public MetadataSearchQuery( ISearchEngine searchEngine ) {
        this.searchEngine = searchEngine;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchQuery#canRerun()
     */
    @Override
    public boolean canRerun() {
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
     */
    @Override
    public boolean canRunInBackground() {
        return true;
    }

    /**
     * @return a results object that the matches will be added to
     * @since 6.0.0
     */
    protected MetadataSearchResult constructEmptyResults() {
        return new MetadataSearchResult(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchQuery#getLabel()
     */
    @Override
    public String getLabel() {
        return Util.getString(I18nUtil.getPropertyPrefix(MetadataSearchPage.class) + "query.msg"); //$NON-NLS-1$ 
    }

    /**
     * @return the search criteria in a textual form
     * @since 6.0.0
     */
    public String getSearchCriteria() {
        return this.searchEngine.getSearchCriteria();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
     */
    @Override
    public ISearchResult getSearchResult() {
        if (this.result == null) {
            this.result = constructEmptyResults();
        }

        return this.result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus run( IProgressMonitor monitor ) throws OperationCanceledException {
        IStatus status = this.searchEngine.canExecute();

        if (status.isOK()) {
            status = this.searchEngine.execute(monitor);
            processSearchEngineResults(this.searchEngine.getResults());
        }

        return status;
    }

    protected void processSearchEngineResults( List<SearchRecord> records ) {
        MetadataSearchResult result = (MetadataSearchResult)getSearchResult();

        for (SearchRecord record : records) {
            ResourceObjectRecord ror = (ResourceObjectRecord)record;
            MetadataMatch match = new MetadataMatch(new MetadataMatchInfo(ror.getResourcePath(), result), ror);
            result.addMatch(match);
        }
    }
}
