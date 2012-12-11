/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.visitor;

import java.util.Collection;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.IResolverVisitor;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid82.sql.impl.CrossQueryMetadata;
import org.teiid82.sql.impl.SyntaxFactory;

/**
 *
 */
public class ResolverVisitorImpl implements IResolverVisitor {

    private final SyntaxFactory factory = new SyntaxFactory();
    
    @Override
    public void setProperty(String propertyName, Object value) {
        if (SHORT_NAME.equals(propertyName) && value instanceof Boolean)
            ResolverVisitor.setFindShortName((Boolean) value);
    }

    @Override
    public void resolveLanguageObject(ILanguageObject obj,
                                      IQueryMetadataInterface metadata) throws Exception {
        LanguageObject languageObject = factory.convert(obj);
        CrossQueryMetadata dMetadata = new CrossQueryMetadata(metadata);
        ResolverVisitor.resolveLanguageObject(languageObject, dMetadata);
    }

    @Override
    public void resolveLanguageObject(ILanguageObject obj,
                                      Collection<IGroupSymbol> groups,
                                      IQueryMetadataInterface metadata) throws Exception {
        LanguageObject languageObject = factory.convert(obj);
        Collection<GroupSymbol> groupImpls = factory.unwrap(groups);
        CrossQueryMetadata dMetadata = new CrossQueryMetadata(metadata);
        ResolverVisitor.resolveLanguageObject(languageObject, groupImpls, dMetadata);
    }

}
