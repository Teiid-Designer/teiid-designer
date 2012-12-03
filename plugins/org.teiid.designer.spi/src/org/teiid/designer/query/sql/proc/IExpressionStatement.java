/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.proc;

import org.teiid.designer.query.sql.lang.IExpression;

/**
 *
 */
public interface IExpressionStatement extends IStatement {

    /**
     * Get the statement's expression
     * 
     * @return the expression
     */
    IExpression getExpression();
}
