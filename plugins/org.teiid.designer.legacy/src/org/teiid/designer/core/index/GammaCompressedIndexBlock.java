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
import org.teiid.core.designer.util.StringUtilities;

/**
 * Uses prefix coding on words, and gamma coding of document numbers differences.
 *
 * @since 8.0
 */
public class GammaCompressedIndexBlock extends IndexBlock {
	CodeByteStream writeCodeStream= new CodeByteStream();
	CodeByteStream readCodeStream;
	char[] prevWord= null;
	int offset= 0;

	public GammaCompressedIndexBlock(int blockSize) {
		super(blockSize);
		readCodeStream= new CodeByteStream(field.buffer());
	}
	/**
	 * @see IndexBlock#addEntry
	 */
	@Override
    public boolean addEntry(WordEntry entry) {
		writeCodeStream.reset();
		encodeEntry(entry, prevWord, writeCodeStream);
		if (offset + writeCodeStream.byteLength() > this.blockSize - 2) {
			return false;
		}
		byte[] bytes= writeCodeStream.toByteArray();
		field.put(offset, bytes);
		offset += bytes.length;
		prevWord= entry.getWord();
		return true;
	}
	protected void encodeEntry(WordEntry entry, char[] prevWord, CodeByteStream codeStream) {
		char[] word= entry.getWord();
		int prefixLen= prevWord == null ? 0 : Math.min(StringUtilities.prefixLength(prevWord, word), 255);
		codeStream.writeByte(prefixLen);
		codeStream.writeUTF(word, prefixLen, word.length);
		int n= entry.getNumRefs();
		codeStream.writeGamma(n);
		int prevRef= 0;
		for (int i= 0; i < n; ++i) {
			int ref= entry.getRef(i);
			if (ref <= prevRef)
				throw new IllegalArgumentException();
			codeStream.writeGamma(ref - prevRef);
			prevRef= ref;
		}
	}
	/**
	 * @see IndexBlock#flush
	 */
	@Override
    public void flush() {
		if (offset > 0) {
			field.putInt2(offset, 0);
			offset= 0;
			prevWord= null;
		}
	}
	/**
	 * @see IndexBlock#isEmpty
	 */
	@Override
    public boolean isEmpty() {
		return offset == 0;
	}
	/**
	 * @see IndexBlock#nextEntry
	 */
	@Override
    public boolean nextEntry(WordEntry entry) {
		try {
			readCodeStream.reset(field.buffer(), offset);
			int prefixLength= readCodeStream.readByte();
			char[] word= readCodeStream.readUTF();
			if (prevWord != null && prefixLength > 0) {
				char[] temp= new char[prefixLength + word.length];
				System.arraycopy(prevWord, 0, temp, 0, Math.min(prefixLength, prevWord.length));
				System.arraycopy(word, 0, temp, Math.min(prefixLength, prevWord.length), word.length);
				word= temp;
			}
			if (word.length == 0) {
				return false;
			}
			entry.reset(word);
			int n= readCodeStream.readGamma();
			int prevRef= 0;
			for (int i= 0; i < n; ++i) {
				int ref= prevRef + readCodeStream.readGamma();
				if (ref < prevRef)
					throw new InternalError();
				entry.addRef(ref);
				prevRef= ref;
			}
			offset= readCodeStream.byteLength();
			prevWord= word;
			return true;
		} catch (UTFDataFormatException e) {
			return false;
		}
	}
	/**
	 * @see IndexBlock#reset
	 */
	@Override
    public void reset() {
		super.reset();
		offset= 0;
		prevWord= null;
	}
}
