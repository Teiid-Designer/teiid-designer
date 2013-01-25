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
public interface IExpression<LV extends ILanguageVisitor> extends ILanguageObject<LV> {

    /**
     * Get the return type of this expression. 
     * @return Java class may be null prior to being resolved
     */
    <T> Class<T> getType();

}
