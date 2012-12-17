/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.visitor;

import java.util.Collection;
import org.teiid.designer.query.sql.IGroupCollectorVisitor;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.visitor.GroupCollectorVisitor;
import org.teiid772.sql.impl.SyntaxFactory;

/**
 *
 */
public class GroupCollectorVisitorImpl implements IGroupCollectorVisitor {

    private final SyntaxFactory factory = new SyntaxFactory();
    
    @Override
    public Collection<IGroupSymbol> getGroups(ILanguageObject obj, boolean removeDuplicates) {
        LanguageObject languageObject = factory.convert(obj);
        Collection<GroupSymbol> groupImpls = GroupCollectorVisitor.getGroups(languageObject, removeDuplicates);
        return factory.wrap(groupImpls);
    }

    @Override
    public Collection<IGroupSymbol> getGroupsIgnoreInlineViews(ILanguageObject obj, boolean removeDuplicates) {
        LanguageObject languageObject = factory.convert(obj);
        Collection<GroupSymbol> groupImpls = GroupCollectorVisitor.getGroupsIgnoreInlineViews(languageObject, removeDuplicates);
        return factory.wrap(groupImpls);
    }
}
