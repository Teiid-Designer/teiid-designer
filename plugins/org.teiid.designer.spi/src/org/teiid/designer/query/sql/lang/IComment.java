/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.List;
import org.teiid.designer.query.sql.IToken;

/**
 *
 */
public interface IComment {

    /**
     * @return the text
     */
    String getText();

    /**
     * @return the line
     */
    int getLine();

    /**
     * @return the column
     */
    int getColumn();

    /**
     * @return the offset
     */
    int getOffset();

    /**
     * @return the isMultiLine
     */
    boolean isMultiLine();

    /**
     * @return the comment's preceding tokens
     */
    List<? extends IToken> getPreTokens();
}
