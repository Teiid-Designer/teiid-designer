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
public interface ICompoundCriteria extends ICriteria {

    /**
     * Logical operators for comparing criteria
     */
    public enum LogicalOperator {
        /** Constant indicating the logical "or" of two or more criteria. */
        OR(1),

        /** Constant indicating the logical "and" of two or more criteria.*/
        AND(0);
        
        private int index;

        LogicalOperator(int index) {
            this.index = index;
        }
        
        /**
         * @return the enumerator index
         */
        public int index() {
            return index;
        }
    }
    
    /**
     * Returns the list of criteria.
     * 
     * @return List of {@link ICriteria}
     */
    List<ICriteria> getCriteria();
    
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
    ICriteria getCriteria(int index);

    /**
     * Add a criteria
     * 
     * @param criteria
     */
    void addCriteria(ICriteria criteria);
    
    /**
     * Get the logical operator
     * 
     * @return enum from {@link LogicalOperator}
     */
    LogicalOperator getOperator();

}
