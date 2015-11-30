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

import java.util.Arrays;
import org.teiid.core.designer.util.CharOperation;

/**
 * An indexBlock stores wordEntries.
 *
 * @since 8.0
 */

public abstract class IndexBlock extends Block {

    private int offset= 0;

	public IndexBlock(int blockSize) {
		super(blockSize);
	}

	/**
     * @return the offset
     */
    public int getOffset() {
        return this.offset;
    }

	/**
     * @param offset the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * @return whether the block is empty or not (if it doesn't contain any wordEntry).
     */
    public boolean isEmpty() {
        return getOffset() == 0;
    }

	/**
	 * Adds the given wordEntry to the indexBlock.
	 */

	public abstract boolean addEntry(WordEntry entry);
	/**
	 * @see Block#clear()
	 */
	@Override
    public void clear() {
		reset();
		super.clear();
	}
	public WordEntry findEntryPrefixedBy(char[] word, boolean isCaseSensitive) {
		reset();
		WordEntry entry= new WordEntry();
		while (nextEntry(entry)) {
			if (CharOperation.prefixEquals(entry.getWord(), word, isCaseSensitive)) {
				return entry;
			}
		}
		return null;
	}
	public WordEntry findExactEntry(char[] word) {
		reset();
		WordEntry entry= new WordEntry();
		while (nextEntry(entry)) {
			if (Arrays.equals(entry.getWord(), word)) {
				return entry;
			}
		}
		return null;
	}

	/**
	 * Finds the next wordEntry and stores it in the given entry.
	 */
	public abstract boolean nextEntry(WordEntry entry);

	public void reset() {
	    setOffset(0);
	}
}
