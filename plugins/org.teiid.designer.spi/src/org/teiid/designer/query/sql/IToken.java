/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

/**
 *
 */
public interface IToken {

    /**
     * @return the offset
     */
    int getOffset();

    /**
     * @return the text of the token
     */
    String getText();

    /**
     * @return is this an id
     */
    boolean isId();

}
