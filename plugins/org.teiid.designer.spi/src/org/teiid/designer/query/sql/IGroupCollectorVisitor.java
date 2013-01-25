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
import org.teiid.designer.query.sql.symbol.IGroupSymbol;

/**
 *
 */
public interface IGroupCollectorVisitor<LO extends ILanguageObject, GS extends IGroupSymbol> {

    /**
     * Get the groups from obj in a collection.  The
     * removeDuplicates flag affects whether duplicate groups will be
     * filtered out.
     * 
     * @param obj Language object
     * @return Collection of {@link IGroupSymbol}
     */
    Collection<GS> findGroups(LO obj);
    
    /**
     * Get the groups from obj in a collection.  The 
     * removeDuplicates flag affects whether duplicate groups will be 
     * filtered out.
     * 
     * @param obj Language object
     * @return Collection of {@link IGroupSymbol}
     */
    Collection<GS> findGroupsIgnoreInlineViews(LO obj);
}
