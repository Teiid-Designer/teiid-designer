/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl.visitor;

import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import org.teiid82.sql.impl.LanguageObjectImpl;

/**
 *
 */
public class SQLStringVisitorImpl implements ISQLStringVisitor {

    @Override
    public String getSQLString(ILanguageObject languageObject) {
        if(languageObject==null) return ""; //$NON-NLS-1$
        LanguageObjectImpl languageObjectImpl = (LanguageObjectImpl) languageObject;
        return SQLStringVisitor.getSQLString(languageObjectImpl.getDelegate());
    }
    
    @Override
    public void visit(ILanguageObject languageObject) {
    }

}
