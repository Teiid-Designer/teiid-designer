/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;


/**
 *
 */
public interface IFrom<F extends IFromClause, 
                                        G extends IGroupSymbol,
                                        LV extends ILanguageVisitor> extends ILanguageObject<LV> {

    /** 
     * Get all the clauses in FROM
     * 
     * @return List of {@link IFromClause}
     */
    List<F> getClauses();
    
    /** 
     * Set all the clauses
     * 
     * @param clauses List of {@link IFromClause}
     */
    void setClauses(List<? extends F> clauses);

    /**
     * Add a clause to the FROM
     * 
     * @param clause Add a clause to the FROM
     */
    void addClause(F clause);
    
    /**
     * Adds a new group to the list 
     * (it will be wrapped in a UnaryFromClause)
     * 
     * @param group Group to add
     */
    void addGroup(G group);

    /**
     * Returns an ordered list of the groups in all sub-clauses.
     * 
     * @return List of {@link IGroupSymbol}
     */
    List<? extends G> getGroups();
    
    /**
     * Checks if a group is in the From
     * 
     * @param group Group to check for
     * 
     * @return True if the From contains the group
     */
    boolean containsGroup(G group);

}
