/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.teiid.api.exception.query.QueryMetadataException;
import org.teiid.core.TeiidComponentException;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.metadata.BasicQueryMetadata;
import org.teiid.query.metadata.StoredProcedureInfo;

/**
 *
 */
public class CrossQueryMetadata extends BasicQueryMetadata {

    private final IQueryMetadataInterface spi;
    
    private final SyntaxFactory factory = new SyntaxFactory();
    
    /**
     * @param spi
     */
    public CrossQueryMetadata(IQueryMetadataInterface spi) {
        this.spi = spi;
    }

    @Override
    public Object getElementID(String elementName) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getElementID(elementName);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getGroupID(String groupName) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getGroupID(groupName);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection getGroupsForPartialName(String partialGroupName) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getGroupsForPartialName(partialGroupName);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getModelID(Object groupOrElementID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getModelID(groupOrElementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getFullName(Object metadataID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getFullName(metadataID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public List getElementIDsInGroupID(Object groupID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getElementIDsInGroupID(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getGroupIDForElementID(Object elementID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getGroupIDForElementID(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public StoredProcedureInfo getStoredProcedureInfoForProcedure(String fullyQualifiedProcedureName)
        throws TeiidComponentException, QueryMetadataException {
        try {
            StoredProcedureInfo storedProcedureInfo = factory.convert(spi.getStoredProcedureInfoForProcedure(fullyQualifiedProcedureName));
            return storedProcedureInfo;
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getElementType(Object elementID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getElementType(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getDefaultValue(Object elementID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getDefaultValue(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getMinimumValue(Object elementID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getMinimumValue(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getMaximumValue(Object elementID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getMaximumValue(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean isVirtualGroup(Object groupID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.isVirtualGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean isVirtualModel(Object modelID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.isVirtualModel(modelID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public QueryNode getVirtualPlan(Object groupID) throws TeiidComponentException, QueryMetadataException {
        try {
            Object virtualPlan = spi.getVirtualPlan(groupID);
            if (virtualPlan instanceof String) {
                return new QueryNode((String) virtualPlan);
            }
            
            return null;
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getInsertPlan(Object groupID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getInsertPlan(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getUpdatePlan(Object groupID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getUpdatePlan(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getDeletePlan(Object groupID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getDeletePlan(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Properties getExtensionProperties(Object metadataID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getExtensionProperties(metadataID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection getIndexesInGroup(Object groupID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getIndexesInGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection getUniqueKeysInGroup(Object groupID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getUniqueKeysInGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection getForeignKeysInGroup(Object groupID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getForeignKeysInGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getPrimaryKeyIDForForeignKeyID(Object foreignKeyID) throws TeiidComponentException, QueryMetadataException {
        try {
            return spi.getPrimaryKeyIDForForeignKeyID(foreignKeyID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public FunctionLibrary getFunctionLibrary() {
        IFunctionLibrary functionLibrary = spi.getFunctionLibrary();
        FunctionLibraryImpl functionLibraryImpl = (FunctionLibraryImpl) functionLibrary;
        return functionLibraryImpl.getDelegate();
    }    
}
