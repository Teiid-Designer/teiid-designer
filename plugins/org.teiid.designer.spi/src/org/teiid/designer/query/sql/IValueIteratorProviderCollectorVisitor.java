/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql;

import java.util.List;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.ISubqueryContainer;

/**
 *
 */
public interface IValueIteratorProviderCollectorVisitor {

    /**
     * Get the ValueIteratorProvider instances from obj
     * 
     * @param obj Language object
     * 
     * @return List of found ValueIteratorProvider
     */
    List<ISubqueryContainer<?>> getValueIteratorProviders(ILanguageObject obj);
}
