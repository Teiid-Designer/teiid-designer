/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.List;
import org.teiid.designer.query.sql.symbol.IElementSymbol;


/**
 *
 */
public interface IOrderBy extends ILanguageObject {

    /** Constant for the ascending value */
    public static final boolean ASC = true;

    /** Constant for the descending value */
    public static final boolean DESC = false;
    
    /**
     * Returns the number of elements in ORDER BY.
     * 
     * @return Number of variables in ORDER BY
     */
    int getVariableCount();

    /**
     * Get the order by items
     * 
     * @return list of order by items
     */
    List<IOrderByItem> getOrderByItems();
    
    /**
     * Adds a new variable to the list of order by elements.
     * 
     * @param expression to add
     */
    void addVariable(IExpression expression);

    /**
     * Adds a new variable to the list of order by elements
     * 
     * @param element
     * @param orderType
     */
    void addVariable(IElementSymbol element, boolean orderType);
}
