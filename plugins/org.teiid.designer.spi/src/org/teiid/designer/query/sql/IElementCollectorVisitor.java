/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import java.util.Collection;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IElementSymbol;

/**
 *
 */
public interface IElementCollectorVisitor<LO extends ILanguageObject, ES extends IElementSymbol> {

    /**
     * Helper to quickly get the elements from obj in a collection.  The
     * removeDuplicates flag affects whether duplicate elements will be
     * filtered out.
     * 
     * @param obj Language object
     * @return Collection of {@link IElementSymbol}
     */
    Collection<? super ES> findElements(LO obj);
    
    /**
     * Helper to quickly get the elements from obj in a collection.  The
     * removeDuplicates flag affects whether duplicate elements will be
     * filtered out.
     * 
     * @param obj Language object
     * @param useDeepIteration indicates whether or not to iterate into nested
     *                 subqueries of the query 
     * 
     * @return Collection of {@link IElementSymbol}
     */
    Collection<? super ES> findElements(LO obj, boolean useDeepIteration);
    
    /**
     * Helper to quickly get the elements from obj in a collection.  The
     * removeDuplicates flag affects whether duplicate elements will be
     * filtered out.
     * 
     * @param obj Language object
     * @param useDeepIteration indicates whether or not to iterate into nested
     *                 subqueries of the query 
     * @param aggsOnly
     * 
     * @return Collection of {@link IElementSymbol}
     */
    Collection<? super ES> findElements(LO obj, boolean useDeepIteration, boolean aggsOnly);
}
