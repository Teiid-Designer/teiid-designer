/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.visitor;

import java.util.ArrayList;
import java.util.List;
import org.teiid.designer.query.sql.IValueIteratorProviderCollectorVisitor;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.ISubqueryContainer;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.visitor.ValueIteratorProviderCollectorVisitor;
import org.teiid772.sql.impl.SyntaxFactory;

/**
 *
 */
public class ValueIteratorProviderCollectorVisitorImpl implements IValueIteratorProviderCollectorVisitor {

    private final SyntaxFactory factory = new SyntaxFactory();
    
    @Override
    public List<ISubqueryContainer<?>> getValueIteratorProviders(ILanguageObject obj) {
        LanguageObject languageObject = factory.convert(obj);
        List<SubqueryContainer> providers = ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(languageObject);
        
        List<ISubqueryContainer<?>> wrapList = new ArrayList<ISubqueryContainer<?>>();
        
        if (providers != null) {
            for (SubqueryContainer provider : providers) {
                LanguageObject providerObject = (LanguageObject) provider;
                wrapList.add((ISubqueryContainer) factory.createLanguageObject(providerObject));
            }
        }
        
        return wrapList;
    }

}
