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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.search.ui.text.Match;

/**
 * @since 6.0.0
 */
public class MetadataMatchInfo {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The path of this info object's associated resource.
     * 
     * @since 6.0.0
     */
    private final String resourcePath;

    /**
     * The result containing the search result matches.
     * 
     * @since 6.0.0
     */
    private final MetadataSearchResult result;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs an info object with an associated resource at the specified path.
     * 
     * @param resourcePath the path of the resource
     * @param result the search result
     * @since 6.0.0
     */
    public MetadataMatchInfo( String resourcePath,
                              MetadataSearchResult result ) {
        this.resourcePath = resourcePath;
        this.result = result;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof MetadataMatchInfo) {
            return getResourcePath().equals(((MetadataMatchInfo)obj).getResourcePath());
        }

        return false;
    }

    /**
     * @return the count of matches for the associated resource
     * @since 6.0.0
     */
    public int getMatchCount() {
        return this.result.getMatchCount(this);
    }
    
    /**
     * @return the matches contained in this info object (never <code>null</code>)
     * @since 6.0.0
     */
    public Match[] getMatches() {
        return this.result.getMatches(this);
    }

    /**
     * @return the associated resource where the matches were found
     * @since 6.0.0
     */
    public IResource getResource() {
        return ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromOSString(getResourcePath()));
    }

    /**
     * @return the resource path
     * @since 6.0.0
     */
    public String getResourcePath() {
        return this.resourcePath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getResourcePath().hashCode();
    }
}
