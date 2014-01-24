/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.Collection;
import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;


/**
 *
 */
public interface IInsert<ES extends IElementSymbol,
                                           E extends IExpression, 
                                           G extends IGroupSymbol, 
                                           Q extends IQueryCommand,
                                           LV extends ILanguageVisitor> extends ICommand<E, LV> {

    /**
     * Returns the group being inserted into
     * 
     * @return Group being inserted into
     */
    G getGroup();
    
    /**
     * Set the group for this insert statement
     * 
     * @param group Group to be inserted into
     */
    void setGroup(G group);
    
    /**
     * Return an ordered List of variables, may be null if no columns were specified
     * 
     * @return List of {@link IElementSymbol}
     */
    List<ES> getVariables();
    
    /**
     * Add a variable to end of list
     * 
     * @param symbol Variable to add to the list
     */
    void addVariable(ES symbol);
    
    /**
     * Add a collection of variables to end of list
     * 
     * @param symbols Variables to add to the list - collection of ElementSymbol
     */
    void addVariables(Collection<ES> symbols);
    
    /**
     * Returns a list of values to insert
     * to be inserted.
     * 
     * @return List of {@link IExpression}s
     */
    List<E> getValues();
    
    /**
     * Sets the values to be inserted.
     * 
     * @param values List of {@link IExpression}s
     */
    void setValues(List<E> values);
    
    /**
     * Set a collection of variables that replace the existing variables
     * 
     * @param vars Variables to be set on this object (ElementSymbols)
     */
    void setVariables(Collection<ES> vars);

    /**
     * Get the query expression
     * 
     * @return query expression
     */
    Q getQueryExpression();
    
}
