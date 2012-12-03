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
public interface IJoinPredicate extends IFromClause {

    /**
     * Enumeration of join types
     */
    enum JoinType {
        /** Represents an inner join:  a INNER JOIN b */
        JOIN_INNER,

        /** Represents a right outer join:  a RIGHT OUTER JOIN b */
        JOIN_RIGHT_OUTER,

        /** Represents a left outer join:  a LEFT OUTER JOIN b */
        JOIN_LEFT_OUTER,

        /** Represents a full outer join:  a FULL OUTER JOIN b */
        JOIN_FULL_OUTER,

        /** Represents a cross join:  a CROSS JOIN b */
        JOIN_CROSS,

        /** Represents a union join:  a UNION JOIN b - not used after rewrite */
        JOIN_UNION,

        /** internal SEMI Join type */
        JOIN_SEMI,

        /** internal ANTI SEMI Join type */
        JOIN_ANTI_SEMI
    }
   
    /**
     * Get left clause
     * 
     * @return Left clause
     */
    IFromClause getLeftClause();
    
    /**
     * Set left clause 
     * 
     * @param fromClause Left clause to set
     */
    void setLeftClause(IFromClause fromClause);
   
    /**
     * Get right clause
     * 
     * @return Right clause
     */
    IFromClause getRightClause();
    
    /**
     * Set right clause 
     * 
     * @param fromClause Right clause to set
     */
    void setRightClause(IFromClause fromClause);
    
    
}
