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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;


/**
 * This class provides a caching mechanism for Dimension's workspace to locate and re-use Index files without closing and re-opening
 * the files. Without caching, Indexes are opened and closed for each query otherwise, creating a substantial unneeded performance
 * penalty.
 * @since 5.0
 */
public class ModelWorkspaceIndexManager {

    private HashMap currentIndexes;
    private boolean doCache = true;
    /**
     *
     * @since 5.0
     */
    public ModelWorkspaceIndexManager() {
        super();
        currentIndexes = new HashMap();
    }

    /**
     * Creates an index for the specified file & resource and adds it to the cache if doCache == TRUE.
     * @param indexFileName
     * @param fullPathIndexName
     * @param resourceFileName
     * @param reuseExistingFile
     * @return the Index
     * @since 5.0
     */
    public Index addIndex(String indexFileName, String fullPathIndexName, String resourceFileName, boolean reuseExistingFile) {

        Index newIndex = null;

        try {
            if( resourceFileName != null ) {
                //System.out.println("  ModelWorkspaceIndexManager.addIndex()  Created Index for File = " + resourceFileName  + "  INDEX FILE = " + indexFileName);
                newIndex = new Index(fullPathIndexName, resourceFileName, reuseExistingFile);
            } else {
                newIndex =  new Index(fullPathIndexName, reuseExistingFile);
            }
            if( doCache ) {
                newIndex.setDoCache(true);
            }
        } catch (IOException theException) {
            ModelerCore.Util.log(IStatus.ERROR, theException, "ModelWorkspaceIndexManager.addIndex() error creating Index() for file: " + fullPathIndexName); //$NON-NLS-1$
        } finally {
            if( newIndex != null && doCache ) {
                //System.out.println("  ModelWorkspaceIndexManager.addIndex()  Index File = " + indexFileName);
                currentIndexes.put(indexFileName, newIndex);
            }
        }

        return newIndex;
    }

    /**
     * Finds the existing Index, or creates one if it doesn't exist.
     * @param indexFileName
     * @param fullPathIndexName
     * @param resourceFileName
     * @return
     * @since 5.0
     */
    public Index getIndex(String indexFileName, String fullPathIndexName, String resourceFileName) {
        if( doCache ) {
            Index cachedIndex = (Index)currentIndexes.get(indexFileName);
            if( cachedIndex != null ) {
                return cachedIndex;
            }

            return addIndex(indexFileName, fullPathIndexName, resourceFileName, true);
        }
        // If we don't cache, go ahead and create a new Index when needed.
        Index newIndex = null;

        try {
            newIndex = new Index(fullPathIndexName,resourceFileName, true);
        } catch (IOException theException) {
            ModelerCore.Util.log(IStatus.ERROR, theException, "ModelWorkspaceIndexManager.addIndex() error creating Index() for file: " + fullPathIndexName); //$NON-NLS-1$
        }
        return newIndex;
    }

    /**
     * Finds the existing Index, or creates one if it doesn't exist.
     * @param indexFileName
     * @param fullPathIndexName
     * @return
     * @since 5.0
     */
    public Index getIndex(String indexFileName, String fullPathIndexName) {
        if( doCache ) {
            Index cachedIndex = (Index)currentIndexes.get(indexFileName);
            if( cachedIndex != null ) {
                return cachedIndex;
            }

            return addIndex(indexFileName, fullPathIndexName, null, false);
        }
        // If we don't cache, go ahead and create a new Index when needed.
        Index newIndex = null;

        try {
            newIndex = new Index(fullPathIndexName, true);
        } catch (IOException theException) {
            ModelerCore.Util.log(IStatus.ERROR, theException, "ModelWorkspaceIndexManager.addIndex() error creating Index() for file: " + fullPathIndexName); //$NON-NLS-1$
        }
        return newIndex;
    }

    /**
     * returns an Index for the input index file name
     * @param indexFileName
     * @return Index - may be null if index file does not exist on file system and is not cached by this manager
     * @since 5.0
     */
    public Index getExistingIndex(String indexFileName) {
        Index cachedIndex = (Index)currentIndexes.get(indexFileName);
        return cachedIndex;
    }

    /**
     * Get list of existing indexes corresponding to the input index files
     * @param indexFiles
     * @return Index array
     * @since 5.0
     */
    public Index[] getExistingIndexes(File[] indexFiles) {
        ArrayList tmp = new ArrayList();

        for(int i=0; i<indexFiles.length; i++ ) {
            //String fullPath = indexFiles[i].getAbsolutePath();
            String fileName = indexFiles[i].getName();

            Index nextIndex = getExistingIndex(fileName);
            if( nextIndex != null ) {
                tmp.add(nextIndex);
            }
        }

        Index[] indexes = new Index[tmp.size()];
        tmp.toArray(indexes);

        return indexes;
    }

    /**
     * Finds the existing Index, or creates one if it doesn't exist.
     * @param indexFileName
     * @param fullPathIndexName
     * @param resourceFileName
     * @return
     * @since 5.0
     */
    public Index getNewIndex(String indexFileName, String fullPathIndexName, String resourceFileName) {
        if( doCache ) {
            Index cachedIndex = (Index)currentIndexes.get(indexFileName);
            if( cachedIndex != null ) {
                disposeIndex(cachedIndex);
            }

            return addIndex(indexFileName, fullPathIndexName, null, false);
        }
        // If we don't cache, go ahead and create a new Index when needed.
        Index newIndex = null;

        try {
            newIndex = new Index(fullPathIndexName,resourceFileName, true);
        } catch (IOException theException) {
            ModelerCore.Util.log(IStatus.ERROR, theException, "ModelWorkspaceIndexManager.addIndex() error creating Index() for file: " + fullPathIndexName); //$NON-NLS-1$
        }

        return newIndex;
    }

    /**
     * Finds the existing Index, or creates one if it doesn't exist.
     * @param indexFileName
     * @param fullPathIndexName
     * @return
     * @since 5.0
     */
    public Index getNewIndex(String indexFileName, String fullPathIndexName) {
        if( doCache ) {
            Index cachedIndex = (Index)currentIndexes.get(indexFileName);
            if( cachedIndex != null ) {
                disposeIndex(cachedIndex);
            }

            return addIndex(indexFileName, fullPathIndexName, null, false);
        }
        // If we don't cache, go ahead and create a new Index when needed.
        Index newIndex = null;

        try {
            newIndex = new Index(fullPathIndexName, true);
        } catch (IOException theException) {
            ModelerCore.Util.log(IStatus.ERROR, theException, "ModelWorkspaceIndexManager.addIndex() error creating Index() for file: " + fullPathIndexName); //$NON-NLS-1$
        }
        return newIndex;
    }

    /**
     * Removes the index file with the provided name after the index is disposed. This results in the Index file being deleted from
     * the file system. The deleted index is finally removed from the index cache.
     * @param indexFileName
     * @since 5.0
     */
    public void disposeIndex(String indexFileName) {
        Index existingIndex = (Index)currentIndexes.get(indexFileName);
        if( existingIndex != null ) {
            //System.out.println("  ModelWorkspaceIndexManager.disposeIndex()  Index File = " + indexFileName);
            currentIndexes.remove(indexFileName);
            existingIndex.dispose();
        }
    }

    /**
     * Removes the specified index file. This results in the Index file being deleted from
     * the file system. The deleted index is finally removed from the index cache.
     * @param index
     * @since 5.0
     */
    public void disposeIndex(Index index) {
        this.disposeIndex(index.getIndexFile().getName());
    }

    /**
     *  Convenience method to close all index files and clear the cache.
     *  This method does not delete the indexes from the file system. (see removeAll())
     *
     * @since 5.0
     */
    public void clear() {
        for(Iterator iter = currentIndexes.values().iterator(); iter.hasNext(); ) {
            Index nextIndex = (Index)iter.next();
            if( nextIndex != null ) {
                nextIndex.close();
            }
        }
        currentIndexes.clear();
    }

    /**
     * Clears the cache, closes all Indexes and deletes them from the file system.
     *
     * @since 5.0
     */
    public void disposeAll() {
        Collection copyOfIndexes = new ArrayList(currentIndexes.values());
        for(Iterator iter = copyOfIndexes.iterator(); iter.hasNext(); ) {
            Index nextIndex = (Index)iter.next();
            if( nextIndex != null ) {
                this.disposeIndex(nextIndex);
            }
        }
        currentIndexes.clear();
    }

    public void closeIndex(Index index) {
        // Close the index
        index.close();
        // Remove it (if it exists) from the cache
        currentIndexes.remove(index.getIndexFile().getName());
    }


    /**
     * Clears the cache, closes all Indexes and deletes them from the file system.
     *
     * @since 5.0
     */
    public void closeAll() {
        Collection copyOfIndexes = new ArrayList(currentIndexes.values());
        for(Iterator iter = copyOfIndexes.iterator(); iter.hasNext(); ) {
            Index nextIndex = (Index)iter.next();
            if( nextIndex != null ) {
                // Close the index
                nextIndex.close();
                // Remove it (if it exists) from the cache
                currentIndexes.remove(nextIndex.getIndexFile().getName());
            }
        }
    }
}
