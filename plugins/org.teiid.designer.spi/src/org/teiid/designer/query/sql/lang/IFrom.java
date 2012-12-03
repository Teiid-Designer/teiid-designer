/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.List;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;


/**
 *
 */
public interface IFrom extends ILanguageObject {

    /** 
     * Get all the clauses in FROM
     * 
     * @return List of {@link IFromClause}
     */
    List<IFromClause> getClauses();
    
    /** 
     * Set all the clauses
     * 
     * @param clauses List of {@link IFromClause}
     */
    void setClauses(List<IFromClause> clauses);

    /**
     * Add a clause to the FROM
     * 
     * @param clause Add a clause to the FROM
     */
    void addClause(IFromClause clause);
    
    /**
     * Adds a new group to the list 
     * (it will be wrapped in a UnaryFromClause)
     * 
     * @param group Group to add
     */
    void addGroup(IGroupSymbol group);

    /**
     * Returns an ordered list of the groups in all sub-clauses.
     * 
     * @return List of {@link IGroupSymbol}
     */
    List<? extends IGroupSymbol> getGroups();
    
    /**
     * Checks if a group is in the From
     * 
     * @param group Group to check for
     * 
     * @return True if the From contains the group
     */
    boolean containsGroup(IGroupSymbol group);

}
