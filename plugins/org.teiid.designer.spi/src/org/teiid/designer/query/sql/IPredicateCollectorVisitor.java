/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import java.util.Collection;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.ILanguageObject;

/**
 *
 */
public interface IPredicateCollectorVisitor<LO extends ILanguageObject, C extends ICriteria> {
    
    /**
     * Get the predicates from obj
     * 
     * @param obj Language object
     * 
     * @return collection of criteria 
     */
    Collection<C> findPredicates(LO obj);

}
