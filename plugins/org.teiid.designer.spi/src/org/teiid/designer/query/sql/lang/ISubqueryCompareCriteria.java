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
public interface ISubqueryCompareCriteria<LV extends ILanguageVisitor, C extends ICommand>
    extends IPredicateCriteria<LV>, ISubqueryContainer<C>{

    /** "Some" predicate quantifier (equivalent to "Any") */
    public static final int SOME = 2;

    /** "Any" predicate quantifier (equivalent to "Some") */
    public static final int ANY = 3;

    /** "All" predicate quantifier */
    public static final int ALL = 4;
   
}
