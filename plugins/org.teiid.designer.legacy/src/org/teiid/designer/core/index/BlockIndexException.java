/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.index;

import java.io.IOException;

/**
 * An exception thrown when an error occurs write a block in an index
 */
public class BlockIndexException extends IOException {

    private static final long serialVersionUID = 1L;

    private String word;

    /**
     * @param word
     * @param errorMsg
     */
    public BlockIndexException(String word, String errorMsg) {
        super(errorMsg);
        this.word = word;
    }

    /**
     * @return the word
     */
    public String getWord() {
        return this.word;
    }

}
