/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;

/**
 *
 */
public class LanguageObjectImpl implements ILanguageObject {

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
    public LanguageObject getDelegate() {
        return delegate;
    }
    
    @Override
    public ILanguageObject clone() {
        return new LanguageObjectImpl((LanguageObject) delegate.clone());
    }
    
    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
    
    @Override
    public boolean isFunction() {
        return delegate instanceof Function;
    }
    
    @Override
    public boolean isExpression() {
        return delegate instanceof Expression;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.delegate == null) ? 0 : this.delegate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (getClass() != obj.getClass())
            return false;
        
        LanguageObjectImpl other = (LanguageObjectImpl)obj;
        
        if (this.delegate == null) {
            if (other.delegate != null)
                return false;
        } else if (!this.delegate.equals(other.delegate))
            return false;
        
        return true;
    }
    
    

    
}
