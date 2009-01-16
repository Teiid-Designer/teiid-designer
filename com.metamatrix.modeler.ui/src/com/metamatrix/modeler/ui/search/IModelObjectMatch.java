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
