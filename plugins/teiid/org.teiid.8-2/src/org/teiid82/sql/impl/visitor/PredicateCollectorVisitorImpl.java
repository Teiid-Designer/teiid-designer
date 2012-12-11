/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.visitor;

import java.util.Collection;
import org.teiid.designer.query.sql.IPredicateCollectorVisitor;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.visitor.PredicateCollectorVisitor;
import org.teiid82.sql.impl.SyntaxFactory;

/**
 *
 */
public class PredicateCollectorVisitorImpl implements IPredicateCollectorVisitor {

    private final SyntaxFactory factory = new SyntaxFactory();
    
    @Override
    public Collection<ICriteria> getPredicates(ILanguageObject obj) {
        LanguageObject languageObject = factory.convert(obj);
        Collection<Criteria> predicates = PredicateCollectorVisitor.getPredicates(languageObject);
        return factory.wrap(predicates);
    }

}
