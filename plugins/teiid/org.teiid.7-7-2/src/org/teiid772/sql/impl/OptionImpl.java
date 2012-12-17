/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl;

import java.util.Collection;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.query.sql.lang.Option;

/**
 *
 */
public class OptionImpl extends LanguageObjectImpl implements IOption {

    /**
     * @param option
     */
    public OptionImpl(Option option) {
        super(option);
    }
    
    @Override
    public Option getDelegate() {
        return (Option) delegate;
    }
    
    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public IOption clone() {
        return new OptionImpl((Option) getDelegate().clone());
    }
    
    @Override
    public boolean isNoCache() {
        return getDelegate().isNoCache();
    }

    @Override
    public Collection<String> getNoCacheGroups() {
        return getDelegate().getNoCacheGroups();
    }

    @Override
    public Collection<String> getDependentGroups() {
        return getDelegate().getDependentGroups();
    }

    @Override
    public Collection<String> getNotDependentGroups() {
        return getDelegate().getNotDependentGroups();
    }
}
