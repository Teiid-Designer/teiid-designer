/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import com.metamatrix.core.index.AbstractIndexSelector;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.internal.core.index.Index;

/**
 * @since 4.2
 */
public class TargetLocationIndexSelector extends AbstractIndexSelector {

    public static final Index[] EMPTY_INDEX_ARRAY = new Index[0];

    private final String indexLocation;

    private Index[] indexes;

    /**
     * @since 4.2
     */
    public TargetLocationIndexSelector( final String indexLocation ) {
        CoreArgCheck.isNotNull(indexLocation);
        this.indexLocation = indexLocation;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.index.IndexSelector#getIndexes()
     */
    @Override
    public Index[] getIndexes() throws IOException {
        if (this.indexes == null) {
            File indexDir = new File(indexLocation);
            FilenameFilter fileFilter = getIndexFileFilter();
            File[] files = null;
            if (fileFilter != null) {
                files = indexDir.listFiles(fileFilter);
            } else {
                files = indexDir.listFiles();
            }
            ArrayList tmp = new ArrayList();

            for (int i = 0; i < files.length; i++) {
                final File indexFile = files[i];
                if (IndexUtil.indexFileExists(indexFile)) {
                    tmp.add(new Index(indexFile.getAbsolutePath(), true));
                }
            }

            this.indexes = new Index[tmp.size()];
            tmp.toArray(this.indexes);
        }

        return this.indexes;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(100);
        sb.append("TargetLocationIndexSelector ["); //$NON-NLS-1$
        Index[] indexes = EMPTY_INDEX_ARRAY;
        try {
            indexes = getIndexes();
        } catch (IOException e) {
            // do nothing
        }
        for (int i = 0; i < indexes.length; i++) {
            if (i > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(indexes[i].getIndexFile());
        }
        sb.append("]"); //$NON-NLS-1$
        return sb.toString();
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    protected void setIndexes( final Index[] indexes ) {
        this.indexes = indexes;
    }

    protected FilenameFilter getIndexFileFilter() {
        return null;
    }
}
