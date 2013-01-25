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
public interface ICompoundCriteria<C extends ICriteria, LV extends ILanguageVisitor> 
    extends ILogicalCriteria<LV> {

    /** Constant indicating the logical "or" of two or more criteria. */
    int OR = 1;

    /** Constant indicating the logical "and" of two or more criteria.*/
    int AND = 0;
        
    /**
     * Returns the list of criteria.
     * 
     * @return List of {@link ICriteria}
     */
    List<C> getCriteria();
    
    /**
     * Get the number of {@link ICriteria}
     * 
     * @return count of criteria
     */
    int getCriteriaCount();

    /**
     * Get the {@link ICriteria} at given index
     * 
     * @param index
     * 
     * @return criteria
     */
    C getCriteria(int index);

    /**
     * Add a criteria
     * 
     * @param criteria
     */
    void addCriteria(C criteria);
    
    /**
     * Get the logical operator
     * 
     * @return int of either AND or OR
     */
    int getOperator();

}
