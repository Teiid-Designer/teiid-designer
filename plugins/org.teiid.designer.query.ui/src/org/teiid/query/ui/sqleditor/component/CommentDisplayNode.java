/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.ui.sqleditor.component;

import java.util.List;
import org.teiid.designer.query.sql.IToken;
import org.teiid.designer.query.sql.lang.IComment;

/**
 *
 */
public class CommentDisplayNode extends TextDisplayNode {

    private final IComment comment;

    /**
     * @param parentNode
     * @param comment
     */
    public CommentDisplayNode(DisplayNode parentNode, IComment comment) {
        super(parentNode, comment.getText());
        this.comment = comment;
    }

    /**
     * @return comment offset
     */
    public int getOffset() {
        return comment.getOffset();
    }

    /**
     * @return list of preceding tokens
     */
    public List<? extends IToken> getPreTokens() {
        return comment.getPreTokens();
    }

    /**
     * @return is the comment a / * ... * \
     */
    public boolean isMultiLine() {
        return comment.isMultiLine();
    }
}
