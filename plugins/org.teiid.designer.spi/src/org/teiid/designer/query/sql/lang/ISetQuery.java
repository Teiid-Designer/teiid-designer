/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 *
 */
public interface ISetQuery<QC extends IQueryCommand, 
                                                O extends IOrderBy,
                                                Q extends IQuery,
                                                E extends IExpression, 
                                                LV extends ILanguageVisitor> extends IQueryCommand<O, Q, E, LV> {

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
    QC getLeftQuery();

    /**
     * Set the left side of the query
     * 
     * @param query
     */
    void setLeftQuery(QC query);

    /**
     * Get right side of the query
     * 
     * @return right part of query
     */
    QC getRightQuery();

    /**
     * Set the right side of the query
     * 
     * @param query
     */
    void setRightQuery(QC query);

    /**
     * @return the left and right queries as a list.  This list cannot be modified.
     */
    List<QC> getQueryCommands();

}
