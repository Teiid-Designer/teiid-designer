/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.search;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.modeler.core.search.runtime.SearchRecord;

public interface ISearchEngine {

    /**
     * Determine whether this search engine has sufficient information to perform the search.
     * 
     * @return the status (never <code>null</code>)
     * @see #execute(IProgressMonitor)
     * @since 6.0.0
     */
    IStatus canExecute();

    /**
     * Perform the search. If the execution fails, there will be no {@link #getResults() results}. Any existing search results are
     * cleared when this method is invoked.
     * 
     * @param monitor the progress monitor (may be <code>null</code>)
     * @return the status of the search (never <code>null</code>)
     * @since 6.0.0
     */
    IStatus execute( IProgressMonitor monitor );

    /**
     * The results from the last executed search.
     * 
     * @return the result records (never <code>null</code>)
     * @since 6.0.0
     */
    List<SearchRecord> getResults();

    /**
     * @return a textual representation of the search criteria or <code>null</code> if the search criteria is not executable
     * @since 6.0.0
     */
    String getSearchCriteria();
}
