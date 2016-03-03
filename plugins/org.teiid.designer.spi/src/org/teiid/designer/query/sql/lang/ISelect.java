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

/**
 *
 */
public interface ISelect<E extends ILanguageObject, LV extends ILanguageVisitor> 
    extends ILanguageObject<LV> {

    /**
     * Returns an ordered list of the symbols in the select.
     * 
     * @return list of SelectSymbol in SELECT
     */
    List<E> getSymbols();

    /**
     * Sets an ordere collection of the symbols in the select.
     * 
     * @param symbols collection of SelectSymbol in SELECT
     */
    void setSymbols(Collection<? extends E> symbols);
    
    /**
     * Add a symbol to this select
     * 
     * @param expression
     */
    void addSymbol(E expression);

    /**
     * Is the select a 'SELECT *'
     * 
     * @return true if a select wildcard
     */
    boolean isStar();

    /**
     * Checks whether the select is distinct
     * 
     * @return True if select is distinct
     */
    boolean isDistinct();
      
    /**
     * Set whether select is distinct.
     * 
     * @param isDistinct True if SELECT is distinct
     */
    void setDistinct(boolean isDistinct);
}
