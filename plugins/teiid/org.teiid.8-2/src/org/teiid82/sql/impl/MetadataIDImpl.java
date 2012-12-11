/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl;

import java.util.ArrayList;
import java.util.List;
import org.teiid.designer.query.metadata.IMetadataID;
import org.teiid.query.metadata.TempMetadataID;

/**
 *
 */
public class MetadataIDImpl implements IMetadataID {

    private TempMetadataID tempMetadataID;

    /**
     * @param tempMetadataID
     */
    public MetadataIDImpl(TempMetadataID tempMetadataID) {
        this.tempMetadataID = tempMetadataID;
    }

    /**
     * @return delegate
     */
    public TempMetadataID getDelegate() {
        return tempMetadataID;
    }
    
    @Override
    public String getID() {
        return tempMetadataID.getID();
    }

    @Override
    public Object getOriginalMetadataID() {
        return tempMetadataID.getOriginalMetadataID();
    }

    @Override
    public List<IMetadataID> getElements() {
        List<IMetadataID> elements = new ArrayList<IMetadataID>();
        for(TempMetadataID element : tempMetadataID.getElements()) {
            elements.add(new MetadataIDImpl(element));
        }
        
        return elements;
    }

    @Override
    public Class<?> getType() {
        return tempMetadataID.getClass();
    }

}
