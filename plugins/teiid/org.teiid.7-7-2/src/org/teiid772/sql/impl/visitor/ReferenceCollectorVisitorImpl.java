/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl.visitor;

import java.util.List;
import org.teiid.designer.query.sql.IReferenceCollectorVisitor;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.visitor.ReferenceCollectorVisitor;
import org.teiid772.sql.impl.SyntaxFactory;

/**
 *
 */
public class ReferenceCollectorVisitorImpl implements IReferenceCollectorVisitor {

    private final SyntaxFactory factory = new SyntaxFactory();
    
    @Override
    public List<IReference> getReferences(ILanguageObject obj) {
        LanguageObject languageObject = factory.convert(obj);
        List<Reference> references = ReferenceCollectorVisitor.getReferences(languageObject);
        return factory.wrap(references);
    }

}
