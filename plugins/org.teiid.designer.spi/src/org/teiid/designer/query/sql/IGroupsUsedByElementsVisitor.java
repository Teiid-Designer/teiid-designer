/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import java.util.Collection;
import java.util.Set;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;

/**
 *
 */
public interface IGroupsUsedByElementsVisitor<LO extends ILanguageObject, GS extends IGroupSymbol> {

    /**
     * Find the groups containd in the given object.
     * 
     * Duplicates are removed.
     * 
     * @param object Language object
     * 
     * @return Collection of {@link IGroupSymbol}
     */
    Set<GS> findGroups(LO object);
    
    
    /**
     * Find the groups contains in the collection of given objects.
     * 
     * Duplicates are removed.
     * 
     * @param objects Collection of language objects
     * 
     * @return Collection of {@link IGroupSymbol}
     */
    <T extends LO> Set<GS> findGroups(Collection<T> objects);
}
