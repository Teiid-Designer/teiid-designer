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
public interface IExistsCriteria<LV extends ILanguageVisitor, C extends ICommand>
    extends IPredicateCriteria<LV>, ISubqueryContainer<C> {

    /**
     * Has this been negated
     * 
     * @return true if negated
     */
    boolean isNegated();
    
    /**
     * Inverse the negation
     * 
     * @param value
     */
    void setNegated(boolean value);

}
