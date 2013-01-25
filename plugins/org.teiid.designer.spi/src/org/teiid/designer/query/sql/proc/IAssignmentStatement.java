/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.proc;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.symbol.IElementSymbol;

/**
 *
 */
public interface IAssignmentStatement<E extends IExpression, LV extends ILanguageVisitor>
    extends IStatement<LV>, IExpressionStatement<E> {

    /**
     * Get the expression giving the value that is assigned to the variable.
     * 
     * @return An <code>Expression</code> with the value
     */
    IElementSymbol getVariable();
    
    /**
     * Get the value of the statement
     */
    E getValue();
    
    /**
     * Set the value of the statement
     * 
     * @param value
     */
    void setValue(E value);
}
