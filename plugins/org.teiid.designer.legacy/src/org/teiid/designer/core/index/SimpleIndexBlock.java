/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     MetaMatrix, Inc - repackaging and updates for use as a metadata store
 *******************************************************************************/
package org.teiid.designer.core.index;

import java.io.UTFDataFormatException;
import java.util.Arrays;

/**
 * Does no compression of words, and uses 4-byte ints for file numbers and number of files.
 *
 * @since 8.0
 */
public class SimpleIndexBlock extends IndexBlock {

    /**
     * @param blockSize
     */
    public SimpleIndexBlock(int blockSize) {
        super(blockSize);
    }

    /**
     * @see IndexBlock#addEntry
     */
    @Override
    public boolean addEntry(WordEntry entry) {
        char[] word = entry.getWord();
        int n = entry.getNumRefs();
        int sizeEstimate = 2 + word.length * 3 + 4 + n * 4;
        int offset = getOffset();
        if (offset + sizeEstimate > this.blockSize - 2)
            return false;
        offset += field.putUTF(offset, word);
        field.putInt4(offset, n);
        offset += 4;
        for (int i = 0; i < n; ++i) {
            field.putInt4(offset, entry.getRef(i));
            offset += 4;
        }
        setOffset(offset);
        return true;
    }

    /**
     * @param word
     * @return {@link WordEntry}
     */
    public WordEntry findEntry(char[] word) {
        try {
            int offset = 0;
            int byteLen;
            while ((byteLen = field.getUInt2(offset)) != 0) {
                char[] tempWord = field.getUTF(offset);
                offset += byteLen + 2;
                if (Arrays.equals(tempWord, word)) {
                    WordEntry entry = new WordEntry(word);
                    int n = field.getInt4(offset);
                    offset += 4;
                    for (int i = 0; i < n; ++i) {
                        int ref = field.getInt4(offset);
                        offset += 4;
                        entry.addRef(ref);
                    }
                    return entry;
                }
                int n = field.getInt4(offset);
                offset += 4 + 4 * n;
            }
            return null;
        } catch (UTFDataFormatException e) {
            return null;
        }
    }

    /**
     * @see IndexBlock#flush
     */
    @Override
    public void flush() {
        if (getOffset() > 0) {
            field.putInt2(getOffset(), 0);
            setOffset(0);
        }
    }

    /**
     * @see IndexBlock#nextEntry
     */
    @Override
    public boolean nextEntry(WordEntry entry) {
        try {
            int offset = getOffset();
            int byteLen = field.getUInt2(offset);
            if (byteLen == 0)
                return false;
            char[] word = field.getUTF(offset);
            offset += byteLen + 2;
            entry.reset(word);
            int n = field.getInt4(offset);
            offset += 4;
            for (int i = 0; i < n; ++i) {
                int ref = field.getInt4(offset);
                offset += 4;
                entry.addRef(ref);
            }
            setOffset(offset);
            return true;
        } catch (UTFDataFormatException e) {
            return false;
        }
    }
}
