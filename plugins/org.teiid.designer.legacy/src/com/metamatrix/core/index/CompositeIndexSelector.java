/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.index;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.teiid.core.util.ArgCheck;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.index.IndexSelector;

/**
 * CompositeIndexSelector, this selector returns indexes if aggrgates from all the 
 * selectors it is constructed from.
 */
public class CompositeIndexSelector extends AbstractIndexSelector {

    private List indexSelectors;

    public CompositeIndexSelector(List indexSelectors) {
        this.indexSelectors = indexSelectors;
    }

    /*
     * @see com.metamatrix.modeler.core.index.IndexSelector#getIndexes()
     */
    @Override
    public Index[] getIndexes() throws IOException {
        List indexes = new ArrayList();
        for(final Iterator selectIter = indexSelectors.iterator();selectIter.hasNext();) {
            IndexSelector indexSelector = (IndexSelector) selectIter.next();
            if(indexSelector !=  null) {
                for(int i=0; i < indexSelector.getIndexes().length; i++) {
                    indexes.add(indexSelector.getIndexes()[i]);     
                }
            }
        }

        return (Index[]) indexes.toArray(new Index[0]);
    }
    
    /** 
     * @see com.metamatrix.modeler.core.index.IndexSelector#getFilePaths()
     * @since 4.2
     */
    @Override
    public String[] getFilePaths() {
        List paths = new ArrayList();
        for(final Iterator selectIter = indexSelectors.iterator();selectIter.hasNext();) {
            IndexSelector indexSelector = (IndexSelector) selectIter.next();
            if(indexSelector !=  null) {
                for(int i=0; i < indexSelector.getFilePaths().length; i++) {
                    paths.add(indexSelector.getFilePaths()[i]);     
                }
            }
        }

        return (String[]) paths.toArray(new String[0]);        
    }

    /**
     * Read the contents of the files at the specefied paths in the index directory
     * and return the contents as String in a collection.
     */
    @Override
    public List getFileContentsAsString(List paths) {
        ArgCheck.isNotEmpty(paths);
        for(final Iterator indexIter = this.indexSelectors.iterator();indexIter.hasNext();) {
            IndexSelector selector = (IndexSelector) indexIter.next();
            if(selector instanceof RuntimeIndexSelector) {
                RuntimeIndexSelector runtimeSelector = (RuntimeIndexSelector) selector;
                List contents = runtimeSelector.getFileContentsAsString(paths);
                if(!contents.isEmpty()) {
                    return contents;
                }
            }
        }

        return Collections.EMPTY_LIST;
    }

    /** 
     * @see com.metamatrix.modeler.core.index.IndexSelector#getFileContent(java.lang.String)
     * @since 4.2
     */
    @Override
    public InputStream getFileContent(String path) {
        ArgCheck.isNotNull(path);
        for(final Iterator indexIter = this.indexSelectors.iterator();indexIter.hasNext();) {
            IndexSelector selector = (IndexSelector) indexIter.next();
            if(selector instanceof RuntimeIndexSelector) {
                RuntimeIndexSelector runtimeSelector = (RuntimeIndexSelector) selector;
                InputStream contents = runtimeSelector.getFileContent(path);
                if(contents != null) {
                    return contents;
                }
            }
        }
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.index.IndexSelector#getFile(java.lang.String)
     * @since 4.2
     */
    @Override
    public File getFile(String path) {
        ArgCheck.isNotNull(path);
        for(final Iterator indexIter = this.indexSelectors.iterator();indexIter.hasNext();) {
            IndexSelector selector = (IndexSelector) indexIter.next();
            if(selector instanceof RuntimeIndexSelector) {
                RuntimeIndexSelector runtimeSelector = (RuntimeIndexSelector) selector;
                File file = runtimeSelector.getFile(path);
                if(file != null) {
                    return file;
                }
            }
        }
        return null;
    }    
    
    /** 
     * @see com.metamatrix.modeler.core.index.IndexSelector#getFileContentAsString(java.lang.String)
     * @since 4.2
     */
    @Override
    public String getFileContentAsString(String path) {
        ArgCheck.isNotNull(path);
        for(final Iterator indexIter = this.indexSelectors.iterator();indexIter.hasNext();) {
            IndexSelector selector = (IndexSelector) indexIter.next();
            if(selector instanceof RuntimeIndexSelector) {
                RuntimeIndexSelector runtimeSelector = (RuntimeIndexSelector) selector;
                String contents = runtimeSelector.getFileContentAsString(path);
                if(contents != null) {
                    return contents;
                }
            }
        }
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.index.IndexSelector#getFileSize(java.lang.String)
     * @since 4.2
     */
    @Override
    public long getFileSize(String path) {
        ArgCheck.isNotNull(path);
        for(final Iterator indexIter = this.indexSelectors.iterator();indexIter.hasNext();) {
            IndexSelector selector = (IndexSelector) indexIter.next();
            if(selector instanceof RuntimeIndexSelector) {
                RuntimeIndexSelector runtimeSelector = (RuntimeIndexSelector) selector;
                long length = runtimeSelector.getFileSize(path);
                if(length != 0) {
                    return length;
                }
            }
        }
        return 0;
    }

    /** 
     * @see com.metamatrix.modeler.core.index.IndexSelector#getFileContent(java.lang.String, java.lang.String[], java.lang.String[])
     * @since 4.2
     */
    @Override
    public InputStream getFileContent(final String path, final String[] tokens, final String[] tokenReplacements) {
        ArgCheck.isNotNull(path);
        for(final Iterator indexIter = this.indexSelectors.iterator();indexIter.hasNext();) {
            IndexSelector selector = (IndexSelector) indexIter.next();
            if(selector instanceof RuntimeIndexSelector) {
                RuntimeIndexSelector runtimeSelector = (RuntimeIndexSelector) selector;
                InputStream contents = runtimeSelector.getFileContent(path, tokens, tokenReplacements);
                if(contents != null) {
                    return contents;
                }
            }
        }
        return null;
    }

    public List getIndexSelectors() {
        return this.indexSelectors;
    }

}
