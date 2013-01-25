/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import org.teiid.designer.query.sql.ILanguageVisitor;






/**
 *
 */
public interface ISubqueryFromClause<LV extends ILanguageVisitor, C extends ICommand>
    extends IFromClause<LV>, ISubqueryContainer<C> {

    /**
     * Get name of this clause.
     * 
     * @return Name of clause
     */
    String getName();
    
    /** 
     * Reset the alias for this subquery from clause and it's pseudo-GroupSymbol.  
     * WARNING: this will modify the hashCode and equals semantics and will cause this object
     * to be lost if currently in a HashMap or HashSet.
     * 
     * @param name New name
     */
    void setName(String name);
}
