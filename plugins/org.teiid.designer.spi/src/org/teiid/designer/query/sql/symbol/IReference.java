/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;

import org.teiid.designer.query.sql.lang.IExpression;

/**
 *
 */
public interface IReference extends IExpression {

    /**
     * @return
     */
    boolean isPositional();

    /**
     * @return
     */
    IElementSymbol getExpression();

}
