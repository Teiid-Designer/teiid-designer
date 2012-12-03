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
public interface IGroupBy extends ILanguageObject {

    /**
     * Returns the number of symbols in the GROUP BY
     * 
     * @return Count of the number of symbols in GROUP BY
     */
    int getCount();
    
    /**
     * Returns an ordered list of the symbols in the GROUP BY
     * 
     * @return List of {@link IElementSymbol}s
     */
    List<IExpression> getSymbols();
    
    /**
     * Adds a new symbol to the list of symbols
     * .
     * @param symbol Symbol to add to GROUP BY
     */
    void addSymbol(IExpression symbol);
}
