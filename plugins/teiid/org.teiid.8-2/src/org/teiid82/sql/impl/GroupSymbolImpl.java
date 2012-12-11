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
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 *
 */
public class GroupSymbolImpl extends SymbolImpl implements IGroupSymbol {

    /**
     * @param groupSymbol
     */
    public GroupSymbolImpl(GroupSymbol groupSymbol) {
        super(groupSymbol);
    }
    
    @Override
    public GroupSymbol getDelegate() {
        return (GroupSymbol) delegate;
    }
    
    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public IGroupSymbol clone() {
        return new GroupSymbolImpl(getDelegate().clone());
    }
    
    @Override
    public void setName(String newName) {
        getDelegate().setName(newName);
    }

    @Override
    public String getDefinition() {
        return getDelegate().getDefinition();
    }

    @Override
    public void setDefinition(String definition) {
        getDelegate().setDefinition(definition);
    }

    @Override
    public boolean isProcedure() {
        return getDelegate().isProcedure();
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
