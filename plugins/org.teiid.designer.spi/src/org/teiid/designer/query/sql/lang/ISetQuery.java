/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.List;

/**
 *
 */
public interface ISetQuery extends IQueryCommand {

    /**
     * Enumerator of types of operation
     */
    public enum Operation {
        /** Represents UNION of two queries */
        UNION,
        /** Represents intersection of two queries */
        INTERSECT,
        /** Represents set difference of two queries */
        EXCEPT
    }
    
    /**
     * Is an all query
     * 
     * @return true if all
     */
    boolean isAll();
    
    /**
     * Set flag that this is an all query
     * 
     * @param value
     */
    void setAll(boolean value);

    /**
     * Get operation for this set
     * 
     * @return Operation as defined in this class
     */
    Operation getOperation();

    /**
     * Get left side of the query
     * 
     * @return left part of query
     */
    IQueryCommand getLeftQuery();

    /**
     * Set the left side of the query
     * 
     * @param query
     */
    void setLeftQuery(IQueryCommand query);

    /**
     * Get right side of the query
     * 
     * @return right part of query
     */
    IQueryCommand getRightQuery();

    /**
     * Set the right side of the query
     * 
     * @param query
     */
    void setRightQuery(IQueryCommand query);

    /**
     * @return the left and right queries as a list.  This list cannot be modified.
     */
    List<IQueryCommand> getQueryCommands();

}
