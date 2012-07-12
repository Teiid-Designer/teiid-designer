/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.core.index;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;



/** 
 * @since 4.2
 */
public abstract class AbstractIndexSelector implements IndexSelector {

    private boolean isValid = true;
    /** 
     * 
     * @since 4.2
     */
    public AbstractIndexSelector() {
        super();
    }

    /** 
     * @see org.teiid.designer.core.index.IndexSelector#getIndexes()
     * @since 4.2
     */
    public abstract Index[] getIndexes() throws IOException;

    
    
    /** 
     * @see org.teiid.designer.core.index.IndexSelector#getFilePaths()
     * @since 4.2
     */
    public String[] getFilePaths() {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.index.IndexSelector#getFileContentsAsString(java.util.List)
     * @since 4.2
     */
    public List getFileContentsAsString(final List paths) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.core.index.IndexSelector#getFileContentAsString(java.lang.String)
     * @since 4.2
     */
    public String getFileContentAsString(final String path) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.index.IndexSelector#getFileContent(java.lang.String, java.lang.String[], java.lang.String[])
     * @since 4.2
     */
    public InputStream getFileContent(final String path, final String[] tokens, final String[] tokenReplacements) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.index.IndexSelector#getFileSize(java.lang.String)
     * @since 4.2
     */
    public long getFileSize(String path) {
        return 0;
    }
    
    /** 
     * @see org.teiid.designer.core.index.IndexSelector#getFile(java.lang.String)
     * @since 4.2
     */
    public File getFile(String path) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.index.IndexSelector#getFileContent(java.lang.String)
     * @since 4.2
     */
    public InputStream getFileContent(final String path) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.index.IndexSelector#isValid()
     * @since 4.2
     */
    public boolean isValid() {
        return this.isValid;
    }
    /** 
     * @see org.teiid.designer.core.index.IndexSelector#setValid(boolean)
     * @since 4.2
     */
    public void setValid(boolean valid) {
        this.isValid = valid;
    }
}
