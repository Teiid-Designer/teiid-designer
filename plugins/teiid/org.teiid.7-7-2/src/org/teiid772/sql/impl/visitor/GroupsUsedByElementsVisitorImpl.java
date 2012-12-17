/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.visitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.teiid.designer.query.sql.IGroupsUsedByElementsVisitor;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.visitor.GroupsUsedByElementsVisitor;
import org.teiid772.sql.impl.SyntaxFactory;

/**
 *
 */
public class GroupsUsedByElementsVisitorImpl implements IGroupsUsedByElementsVisitor {

    private final SyntaxFactory factory = new SyntaxFactory();

    @Override
    public Set<IGroupSymbol> getGroups(ILanguageObject obj) {
        LanguageObject languageObject = factory.convert(obj);
        Set<GroupSymbol> groupImpls = GroupsUsedByElementsVisitor.getGroups(languageObject);
        List<IGroupSymbol> wrapList = factory.wrap(groupImpls);
        return new HashSet<IGroupSymbol>(wrapList);
    }
    
}
