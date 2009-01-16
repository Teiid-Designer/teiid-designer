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

package com.metamatrix.modeler.ui.search;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.search.ISearchEngine;
import com.metamatrix.modeler.core.search.runtime.ResourceObjectRecord;
import com.metamatrix.modeler.core.search.runtime.SearchRecord;
import com.metamatrix.modeler.ui.UiConstants;

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
