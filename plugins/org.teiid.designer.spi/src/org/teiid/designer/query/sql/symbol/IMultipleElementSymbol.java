/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;

import java.util.List;
import org.teiid.designer.query.sql.lang.IExpression;

/**
 *
 */
public interface IMultipleElementSymbol extends IExpression {

    /**
     * Get the element symbols referred to by this multiple element symbol
     * 
     * @return List of {@link ElementSymbol}s, may be null
     */
    List<IElementSymbol> getElementSymbols();

}
