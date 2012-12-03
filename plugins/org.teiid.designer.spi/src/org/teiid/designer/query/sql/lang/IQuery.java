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
public interface IQuery extends IQueryCommand {

    /**
     * Get the select clause for the query.
     * 
     * @return SELECT clause
     */
    ISelect getSelect();

    /**
     * Set the select clause for the query.
     * 
     * @param select SELECT clause
     */
    void setSelect(ISelect select);
    
    /**
     * Get the from clause for the query.
     * 
     * @return FROM clause
     */
    IFrom getFrom();
    
    /**
     * Set the from clause for the query.
     * 
     * @param from FROM clause
     */
    void setFrom( IFrom from);
    
    /**
     * Get the into clause for the query
     * 
     * @return INTO clause
     */
    IInto getInto();
    
    /**
     * Set the into clause for the query.
     * 
     * @param into INTO clause
     */
    void setInto( IInto into);
    
    /**
     * Get the criteria clause for the query.
     * 
     * @return WHERE clause
     */
    ICriteria getCriteria();
    
    /**
     * Set the where clause
     * 
     * @param where
     */
    void setCriteria(ICriteria where);


    /**
     * Get the having clause for the query.
     * 
     * @return HAVING clause
     */
    ICriteria getHaving();

    /**
     * Set the having clause
     * 
     * @param having
     */
    void setHaving(ICompareCriteria having);
    
    /**
     * Get the group by
     * 
     * @return GROUPBY clause
     */
    IGroupBy getGroupBy();

    /**
     * Set the group by clause
     * 
     * @param groupBy
     */
    void setGroupBy(IGroupBy groupBy);
}
