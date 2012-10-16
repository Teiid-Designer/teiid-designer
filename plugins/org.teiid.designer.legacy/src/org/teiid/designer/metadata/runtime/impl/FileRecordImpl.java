/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime.impl;

import java.io.File;
import java.io.InputStream;
import org.teiid.core.util.EquivalenceUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.HashCodeUtil;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.workspace.ModelFileUtil;
import org.teiid.designer.metadata.runtime.FileRecord;

/**
 * @since 8.0
 */
public class FileRecordImpl extends AbstractMetadataRecord implements FileRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private String pathInVdb;
    private String[] tokens;
    private String[] tokenReplacements;
    private IndexSelector selector;

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#getPathInVdb()
     * @since 4.2
     */
    @Override
	public String getPathInVdb() {
        return this.pathInVdb;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#isBinary()
     * @since 4.2
     */
    @Override
	public boolean getBinary() {
        if (this.pathInVdb != null && this.pathInVdb.endsWith(IndexConstants.INDEX_EXT)) {
            return true;
        }
        return false;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#getContent()
     * @since 4.2
     */
    @Override
	public InputStream getContent() {
        if (this.tokens != null) {
            return this.selector.getFileContent(getPathInVdb(), this.tokens, this.tokenReplacements);
        }
        return this.selector.getFileContent(getPathInVdb());
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#getFileLength()
     * @since 4.2
     */
    public long getFileLength() {
        return this.selector.getFileSize(getPathInVdb());
    }

    /**
     * @param pathInVdb The pathInVdb to set.
     * @since 4.2
     */
    public void setPathInVdb( final String pathInVdb ) {
        this.pathInVdb = pathInVdb;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#getTokenReplacementString1()
     * @since 4.2
     */
    @Override
	public String[] getTokens() {
        return this.tokens;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#getTokenReplacements()
     * @since 4.2
     */
    @Override
	public String[] getTokenReplacements() {
        return this.tokenReplacements;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#setTokens(java.lang.String[])
     * @since 4.2
     */
    @Override
	public void setTokens( final String[] tokens ) {
        this.tokens = tokens;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#setTokenReplacementString2(java.lang.String[])
     * @since 4.2
     */
    @Override
	public void setTokenReplacements( final String[] tokenReplacements ) {
        this.tokenReplacements = tokenReplacements;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#setIndexSelector(org.teiid.designer.core.index.IndexSelector)
     * @since 4.2
     */
    @Override
	public void setIndexSelector( final IndexSelector selector ) {
        this.selector = selector;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#getFileRecord()
     * @since 4.2
     */
    @Override
	public FileRecord getFileRecord() {
        return this;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getModelName()
     * @since 4.2
     */
    @Override
    public String getModelName() {
        if (isModelFile()) {
            return FileUtils.getBaseFileNameWithoutExtension(this.pathInVdb);
        }
        return null;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#isIndexFile()
     * @since 4.2
     */
    @Override
	public boolean isIndexFile() {
        if (this.pathInVdb != null && this.pathInVdb.endsWith(IndexConstants.INDEX_EXT)) {
            return true;
        }
        return false;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.FileRecord#getModelName()
     * @since 4.2
     */
    @Override
	public boolean isModelFile() {
        if (this.pathInVdb != null) {
            File fileInVdb = this.selector.getFile(this.pathInVdb);
            return ModelFileUtil.isModelFile(fileInVdb);
        }
        return false;
    }

    /**
     * Compare two records for equality.
     */
    @Override
    public boolean equals( Object obj ) {

        if (obj == this) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        FileRecord other = (FileRecord)obj;

        if (!EquivalenceUtil.areEqual(this.getPathInVdb(), other.getPathInVdb())) {
            return false;
        }
        if (!EquivalenceUtil.areEqual(this.getTokens(), other.getTokens())) {
            return false;
        }
        if (!EquivalenceUtil.areEqual(this.getTokenReplacements(), other.getTokenReplacements())) {
            return false;
        }

        return true;
    }

    /**
     * Get hashcode for From. WARNING: The hash code relies on the variables in the record, so changing the variables will change
     * the hash code, causing a select to be lost in a hash structure. Do not hash a record if you plan to change it.
     */
    @Override
    public int hashCode() {
        int myHash = 0;
        if (this.pathInVdb != null) {
            myHash = HashCodeUtil.hashCode(myHash, this.pathInVdb);
        }
        if (this.tokens != null) {
            myHash = HashCodeUtil.hashCode(myHash, (Object[])this.tokens);
        }
        if (this.tokenReplacements != null) {
            myHash = HashCodeUtil.hashCode(myHash, (Object[])this.tokenReplacements);
        }

        return myHash;
    }

    @Override
    public String toString() {
        return this.getPathInVdb();
    }
}
