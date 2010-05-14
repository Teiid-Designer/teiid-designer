/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.search;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.search.runtime.SearchRecord;

public interface IModelObjectMatch {

    /**
     * @return the EObject that was matched (may be <code>null</code>)
     * @since 6.0.0
     */
    EObject getEObject();

    /**
     * @return a short description of the match for the search result view (never <code>null</code>)
     * @since 6.0.0
     */
    String getMatchDescription();

    /**
     * @return the workspace path of the resource containing the match (never <code>null</code>)
     * @since 6.0.0
     */
    String getResourcePath();

    /**
     * @return the search record obtained from the {@link com.metamatrix.modeler.core.search.ISearchEngine}
     * @since 6.0.0
     */
    SearchRecord getSearchRecord();
}
