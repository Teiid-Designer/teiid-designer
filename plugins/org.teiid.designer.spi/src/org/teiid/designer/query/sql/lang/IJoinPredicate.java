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
public interface IJoinPredicate<T extends IFromClause, LV extends ILanguageVisitor> extends IFromClause<LV> {

    /**
     * Get left clause
     * 
     * @return Left clause
     */
    T getLeftClause();
    
    /**
     * Set left clause 
     * 
     * @param fromClause Left clause to set
     */
    void setLeftClause(T fromClause);
   
    /**
     * Get right clause
     * 
     * @return Right clause
     */
    T getRightClause();
    
    /**
     * Set right clause 
     * 
     * @param fromClause Right clause to set
     */
    void setRightClause(T fromClause);
    
    
}
