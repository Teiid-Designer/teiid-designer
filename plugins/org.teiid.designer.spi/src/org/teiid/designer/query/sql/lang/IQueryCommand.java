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
public interface IQueryCommand<O extends IOrderBy, Q extends IQuery, E extends IExpression, LV extends ILanguageVisitor>
    extends ICommand<E, LV> {

    /**
     * Get the order by clause for the query.
     * 
     * @return order by clause
     */
    O getOrderBy();
    
    /**
     * Set the order by clause for the query.
     * 
     * @param orderBy New order by clause
     */
    void setOrderBy(O orderBy);
    
    /**
     * Get the query
     * 
     * @return query
     */
    Q getProjectedQuery();
}
