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
