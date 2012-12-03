/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;


/**
 *
 */
public interface IQueryCommand extends ICommand {

    /**
     * Get the order by clause for the query.
     * 
     * @return order by clause
     */
    IOrderBy getOrderBy();
    
    /**
     * Set the order by clause for the query.
     * 
     * @param orderBy New order by clause
     */
    void setOrderBy(IOrderBy orderBy);
    
    /**
     * Get the query
     * 
     * @return query
     */
    IQuery getProjectedQuery();
}
