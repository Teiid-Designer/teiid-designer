/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.parser;

import org.teiid.designer.query.sql.IToken;

/**
 *
 */
public abstract class AbstractToken implements IToken {

    private String text;

    private int offset;

    boolean doNotTrack = false;

    boolean id = false;

    /**
     * @return the offset
     */
    @Override
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
     * @return the text of the token
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the doNotTrack
     */
    public boolean doNotTrack() {
        return this.doNotTrack;
    }

    /**
     * @param doNotTrack the doNotTrack to set
     */
    public void setDoNotTrack(boolean doNotTrack) {
        this.doNotTrack = doNotTrack;
    }

    /**
     * @return if this is an id
     */
    @Override
    public boolean isId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(boolean id) {
        this.id = id;
    }
}
