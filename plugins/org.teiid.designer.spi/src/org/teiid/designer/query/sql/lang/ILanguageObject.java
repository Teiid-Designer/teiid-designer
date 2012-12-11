/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 *
 */
public interface ILanguageObject {

    /**
     * Clone this language object
     * 
     * @return a copy of this language object
     */
    ILanguageObject clone();

    /**
     * @param visitor
     */
    void acceptVisitor(ILanguageVisitor visitor);
    
    /**
     * Is this object a function
     */
    boolean isFunction();
    
    /**
     * Is this object an expression
     */
    boolean isExpression();

}
