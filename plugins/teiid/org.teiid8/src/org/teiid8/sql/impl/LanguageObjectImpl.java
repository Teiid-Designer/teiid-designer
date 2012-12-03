/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid8.sql.impl;

import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.query.sql.LanguageObject;

/**
 *
 */
public abstract class LanguageObjectImpl implements ILanguageObject {

    protected final LanguageObject delegate;
    
    private SyntaxFactory factory = new SyntaxFactory();

    protected LanguageObjectImpl(LanguageObject languageObject) {
        this.delegate = languageObject;
    }
    
    protected SyntaxFactory getFactory() {
        return factory;
    }
    
    /**
     * Get the underlying language object
     * 
     * @return language object
     */
    public abstract LanguageObject getDelegate();
    
    @Override
    public abstract ILanguageObject clone();

}
