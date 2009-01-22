/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
