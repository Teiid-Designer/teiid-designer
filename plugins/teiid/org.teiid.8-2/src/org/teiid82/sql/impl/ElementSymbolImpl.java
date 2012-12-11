/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.metadata.IMetadataID;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 *
 */
public class ElementSymbolImpl extends SymbolImpl implements IElementSymbol {

    /**
     * @param elementSymbol
     */
    public ElementSymbolImpl(ElementSymbol elementSymbol) {
        super(elementSymbol);
    }

    @Override
    public ElementSymbol getDelegate() {
        return (ElementSymbol) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ElementSymbolImpl clone() {
        return new ElementSymbolImpl(getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
    }

    @Override
    public void setType(Class<?> targetType) {
        getDelegate().setType(targetType);
    }
    
    @Override
    public IGroupSymbol getGroupSymbol() {
        return getFactory().convert(getDelegate().getGroupSymbol());
    }

    @Override
    public void setGroupSymbol(IGroupSymbol groupSymbol) {
        GroupSymbol groupSymbolImpl = getFactory().convert(groupSymbol);
        getDelegate().setGroupSymbol(groupSymbolImpl);
    }

    @Override
    public boolean isExternalReference() {
        return getDelegate().isExternalReference();
    }

    @Override
    public void setDisplayFullyQualified(boolean value) {
        getDelegate().setDisplayFullyQualified(value);
    }

    @Override
    public ESDisplayMode getDisplayMode() {
        String enumName = getDelegate().getDisplayMode().name();
        return ESDisplayMode.valueOf(enumName);
    }

    @Override
    public Object getMetadataID() {
        Object metadataID = getDelegate().getMetadataID();
        if (metadataID instanceof TempMetadataID) {
            return new MetadataIDImpl((TempMetadataID) metadataID);
        }
        
        return metadataID;
    }

    @Override
    public void setMetadataID(Object metadataID) {
        if (metadataID instanceof IMetadataID) {
            MetadataIDImpl metadataIDImpl = (MetadataIDImpl) metadataID;
            metadataID = metadataIDImpl.getDelegate();
        }
        
        getDelegate().setMetadataID(metadataID);
    }
}