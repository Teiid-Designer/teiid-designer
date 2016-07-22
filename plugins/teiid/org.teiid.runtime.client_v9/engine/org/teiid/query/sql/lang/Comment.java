/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.teiid.designer.query.sql.IToken;
import org.teiid.designer.query.sql.lang.IComment;
import org.teiid.query.parser.AbstractToken;
import org.teiid.runtime.client.admin.StringConstants;

/**
 *
 */
public class Comment implements IComment, StringConstants {

    private String text = EMPTY_STRING;

    private int line = -1;

    private int column = -1;

    private int offset = -1;

    private boolean isMultiLine = true;

    private List<? extends IToken> preTokens = null;

    /**
     * Default Constructor
     */
    public Comment() {}

    /**
     * Convenience Constructor for tests
     * @param text
     * @param offset
     */
    public Comment(String text, int offset) {
        this.text = text;
        this.offset = offset;
    }

    /**
     * @return the text
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the line
     */
    @Override
    public int getLine() {
        return this.line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * @return the column
     */
    @Override
    public int getColumn() {
        return this.column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(int column) {
        this.column = column;
    }

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
     * @return the preTokens
     */
    @Override
    public List<? extends IToken> getPreTokens() {
        if (this.preTokens == null)
            return Collections.emptyList();

        return this.preTokens;
    }

    /**
     * @param preTokens the preTokens to set
     */
    public void setPreTokens(List<? extends AbstractToken> preTokens) {
        this.preTokens = new LinkedList(preTokens);
    }

    /**
     * @return the isMultiLine
     */
    @Override
    public boolean isMultiLine() {
        return this.isMultiLine;
    }

    /**
     * @param isMultiLine the isMultiLine to set
     */
    public void setMultiLine(boolean isMultiLine) {
        this.isMultiLine = isMultiLine;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.column;
        result = prime * result + (this.isMultiLine ? 1231 : 1237);
        result = prime * result + this.line;
        result = prime * result + this.offset;
        result = prime * result + ((this.preTokens == null) ? 0 : this.preTokens.hashCode());
        result = prime * result + ((this.text == null) ? 0 : this.text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Comment other = (Comment)obj;
        if (this.column != other.column)
            return false;
        if (this.isMultiLine != other.isMultiLine)
            return false;
        if (this.line != other.line)
            return false;
        if (this.offset != other.offset)
            return false;
        if (this.preTokens == null) {
            if (other.preTokens != null)
                return false;
        } else if (!this.preTokens.equals(other.preTokens))
            return false;
        if (this.text == null) {
            if (other.text != null)
                return false;
        } else if (!this.text.equals(other.text))
            return false;
        return true;
    }

    @Override
    @SuppressWarnings( "nls" )
    public String toString() {
        StringBuffer txt = new StringBuffer("Comment [text=" + this.text + ", line=" + this.line + ", column=" + this.column
                                            + ", offset=" + this.offset + ", isMultiLine=" + this.isMultiLine);

        txt.append(", preTokens=");
        if (this.preTokens == null || this.preTokens.isEmpty())
            txt.append("[]");
        else {
            Iterator<? extends IToken> iterator = this.preTokens.iterator();
            while (iterator.hasNext()) {
                IToken token = iterator.next();
                txt.append(token.getText());
                txt.append(SPACE).append(AT).append(SPACE);
                txt.append(token.getOffset());
                if (iterator.hasNext())
                    txt.append(COMMA).append(SPACE);
            }
        }
        txt.append("]");

        return txt.toString();
    }
}
