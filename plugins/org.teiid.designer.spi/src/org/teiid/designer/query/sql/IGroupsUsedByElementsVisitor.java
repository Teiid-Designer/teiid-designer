/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import java.util.Set;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;

/**
 *
 */
public interface IGroupsUsedByElementsVisitor {

    /**
     * Helper to quickly get the groups from obj in a collection.  Duplicates
     * are removed.
     * 
     * @param obj Language object
     * 
     * @return Collection of {@link IGroupSymbol}
     */
    Set<IGroupSymbol> getGroups(ILanguageObject obj);
}
