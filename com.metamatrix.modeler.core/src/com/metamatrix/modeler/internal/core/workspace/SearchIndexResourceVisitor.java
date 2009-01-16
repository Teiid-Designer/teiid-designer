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

package com.metamatrix.modeler.internal.core.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import com.metamatrix.modeler.internal.core.index.IndexUtil;

/**
 * Visitor that collects all associated {@link org.eclipse.core.resources.IResource}s. Index names and index files are also
 * collected.
 * 
 * @since 5.0.1
 */
public class SearchIndexResourceVisitor implements IResourceVisitor {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private Collection indexNames = new HashSet();

    private List resources = new ArrayList();

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Obtains the index files of all indexes associated with this visitor. The result will be empty if the resource hasn't been
     * visited yet.
     * 
     * @return the index files (never <code>null</code>)
     * @since 5.0.1
     */
    public File[] getIndexFiles() {
        File[] result = null;
        File[] files = new File(IndexUtil.INDEX_PATH).listFiles(); // all index files

        if (files.length != 0) {
            List temp = new ArrayList(files.length);

            for (int ndx = files.length; --ndx >= 0;) {
                File file = files[ndx];

                if (IndexUtil.isIndexFile(file) && this.indexNames.contains(file.getName())) {
                    temp.add(file);
                }
            }

            if (!temp.isEmpty()) {
                temp.toArray(result = new File[temp.size()]);
            } else {
                result = new File[0];
            }
        } else {
            result = new File[0];
        }

        return result;
    }

    /**
     * Obtains the file names of all indexes associated with this visitor. The result will be empty if the visitor hasn't visited
     * the resource yet.
     * 
     * @return the file names of all indexes (never <code>null</code>)
     * @since 5.0.1
     */
    public Collection getIndexNames() {
        return this.indexNames;
    }

    /**
     * Obtains the {@link IResource}s associated with this visitor. The result will be empty if the visitor hasn't visited the
     * resource yet.
     * 
     * @return the resources (never <code>null</code>)
     * @since 5.0.1
     */
    public List getResources() {
        return this.resources;
    }

    private boolean isIncludedResource( final IResource theResource ) {
        if ((theResource == null) || !theResource.exists()) {
            return false;
        }

        if (ModelUtil.isModelFile(theResource) || ModelUtil.isXsdFile(theResource) || ModelUtil.isVdbArchiveFile(theResource)) {
            return true;
        }

        return false;
    }

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
     * @since 5.0.1
     */
    public boolean visit( IResource theResource ) {
        if (isIncludedResource(theResource)) {
            this.resources.add(theResource);
            this.indexNames.add(IndexUtil.getRuntimeIndexFileName(theResource));
            this.indexNames.add(IndexUtil.getSearchIndexFileName(theResource));
        }

        return true;
    }

}
