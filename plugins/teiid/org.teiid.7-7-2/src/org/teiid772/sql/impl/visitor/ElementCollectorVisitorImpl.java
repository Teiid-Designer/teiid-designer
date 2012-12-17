/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.visitor;

import java.util.Collection;
import org.teiid.designer.query.sql.IElementCollectorVisitor;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.visitor.ElementCollectorVisitor;
import org.teiid772.sql.impl.SyntaxFactory;

/**
 *
 */
public class ElementCollectorVisitorImpl implements IElementCollectorVisitor {

    private final SyntaxFactory factory = new SyntaxFactory();
    
    @Override
    public Collection<IElementSymbol> getElements(ILanguageObject obj,
                                                  boolean removeDuplicates) {
        LanguageObject languageObject = factory.convert(obj);
        Collection<ElementSymbol> elements = ElementCollectorVisitor.getElements(languageObject, removeDuplicates);
        return factory.wrap(elements);
    }

    @Override
    public Collection<IElementSymbol> getElements(ILanguageObject obj,
                                                  boolean removeDuplicates,
                                                  boolean useDeepIteration) {
        LanguageObject languageObject = factory.convert(obj);
        Collection<ElementSymbol> elements = ElementCollectorVisitor.getElements(languageObject, removeDuplicates, useDeepIteration);
        return factory.wrap(elements);
    }

    @Override
    public Collection<IElementSymbol> getElements(ILanguageObject obj,
                                                  boolean removeDuplicates,
                                                  boolean useDeepIteration,
                                                  boolean aggsOnly) {
        LanguageObject languageObject = factory.convert(obj);
        Collection<ElementSymbol> elements = ElementCollectorVisitor.getElements(languageObject, removeDuplicates, useDeepIteration, aggsOnly);
        return factory.wrap(elements);
    }

}
