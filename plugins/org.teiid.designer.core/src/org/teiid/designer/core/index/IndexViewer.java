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
import java.util.ArrayList;
import java.util.Collection;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.index.BlocksIndexInput;
import org.teiid.designer.core.index.Index;
import org.teiid.designer.core.index.IndexInput;
import org.teiid.designer.core.util.StringUtilities;


/**
 * IndexViewer
 *
 * @since 8.0
 */
public class IndexViewer {

    private static final char[] FORMAT_CHARS = {'\n', '\r', '\t'};

    /**
     * Returns the contents of the specified index file as a byte[]. Line separator characters are added after every WordEntry
     * found in the file.
     */
    public static byte[] getByteContent( final File file ) throws IOException {
        CoreArgCheck.isNotNull(file);
        CoreArgCheck.isTrue(IndexUtil.isIndexFile(file), "File is not index file"); //$NON-NLS-1$
        CoreArgCheck.isTrue(IndexUtil.indexFileExists(file.getAbsolutePath()),
                            "Index File " + file.getAbsolutePath() + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$

        String content = getStringContent(file);
        return (content != null ? content.getBytes() : new byte[0]);
    }

    /**
     * Returns the contents of the specified index file as a string. Line separator characters are added after every WordEntry
     * found in the file.
     */
    public static String getStringContent( final File file ) throws IOException {
        CoreArgCheck.isNotNull(file);
        CoreArgCheck.isTrue(IndexUtil.isIndexFile(file), "File is not index file"); //$NON-NLS-1$
        CoreArgCheck.isTrue(IndexUtil.indexFileExists(file.getAbsolutePath()),
                            "Index File " + file.getAbsolutePath() + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$

        IndexInput input = null;
        try {
            // Stringify the contents ...
            input = new BlocksIndexInput(file);
            input.open();
            StringBuffer buffer = new StringBuffer();
            while (input.hasMoreWords()) {
                final String word = String.valueOf(input.getCurrentWordEntry().getWord());
                buffer.append(StringUtilities.removeChars(word, IndexViewer.FORMAT_CHARS));
                buffer.append(CoreStringUtil.LINE_SEPARATOR);
                input.moveToNextWordEntry();
            }
            return buffer.toString();
        } catch (Throwable t) {
            return CoreStringUtil.getStackTrace(t);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        // return ObjectConverterUtil.convertFileToString(file);
    }

    /**
     * Returns the collection of {@link char[]} instances representing the contents of the specified index file.
     */
    public static Collection getWords( final String filePath ) throws IOException {
        CoreArgCheck.isNotNull(filePath);
        CoreArgCheck.isTrue(IndexUtil.isIndexFile(filePath), "File is not index file"); //$NON-NLS-1$
        CoreArgCheck.isTrue(IndexUtil.indexFileExists(filePath), "Index File " + filePath + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$

        IndexInput input = null;
        try {
            // Stringify the contents ...
            input = new BlocksIndexInput(new File(filePath));
            input.open();
            Collection words = new ArrayList();
            while (input.hasMoreWords()) {
                words.add(input.getCurrentWordEntry().getWord());
                input.moveToNextWordEntry();
            }
            return words;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    /**
     * Returns the collection of {@link char[]} instances representing the contents of the specified index file.
     */
    public static Collection getWords( final File file ) throws IOException {
        CoreArgCheck.isNotNull(file);
        CoreArgCheck.isTrue(IndexUtil.isIndexFile(file), "File is not index file"); //$NON-NLS-1$
        CoreArgCheck.isTrue(IndexUtil.indexFileExists(file.getAbsolutePath()),
                            "Index File " + file.getAbsolutePath() + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$

        IndexInput input = null;
        try {
            // Stringify the contents ...
            input = new BlocksIndexInput(file);
            input.open();
            Collection words = new ArrayList();
            while (input.hasMoreWords()) {
                words.add(input.getCurrentWordEntry().getWord());
                input.moveToNextWordEntry();
            }
            return words;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    /**
     * 
     */
    public static void clearIndexFileContents( final String filePath ) throws IOException {
        Index index = new Index(filePath, null, true);
        index.empty();
    }

    /**
     * 
     */
    public static String getStringContent( final Index index ) throws IOException {
        CoreArgCheck.isNotNull(index);
        CoreArgCheck.isNotNull(index.getIndexFile());
        CoreArgCheck.isTrue(IndexUtil.isIndexFile(index.getIndexFile()), "File is not index file"); //$NON-NLS-1$
        CoreArgCheck.isTrue(IndexUtil.indexFileExists(index.getIndexFile().getAbsolutePath()),
                            "Index File " + index.getIndexFile().getAbsolutePath() + " does not exist"); //$NON-NLS-1$ //$NON-NLS-2$

        return getStringContent(index.getIndexFile());
    }

}
