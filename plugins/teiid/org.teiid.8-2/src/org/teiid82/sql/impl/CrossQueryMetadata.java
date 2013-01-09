/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.teiid.api.exception.query.QueryMetadataException;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.mapping.xml.MappingNode;
import org.teiid.query.metadata.BasicQueryMetadata;
import org.teiid.query.metadata.StoredProcedureInfo;
import org.teiid82.sql.impl.xml.MappingDocumentFactory;

/**
 *
 */
public class CrossQueryMetadata extends BasicQueryMetadata {

    private final IQueryMetadataInterface spi;

    private final SyntaxFactory factory = new SyntaxFactory();
    
    private final MappingDocumentFactory mappingFactory = new MappingDocumentFactory();

    /**
     * @param spi
     */
    public CrossQueryMetadata(IQueryMetadataInterface spi) {
        this.spi = spi;
    }

    @Override
    public Object getElementID(String elementName) throws QueryMetadataException {
        try {
            return spi.getElementID(elementName);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getGroupID(String groupName) throws QueryMetadataException {
        try {
            return spi.getGroupID(groupName);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection getGroupsForPartialName(String partialGroupName) throws QueryMetadataException {
        try {
            return spi.getGroupsForPartialName(partialGroupName);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getModelID(Object groupOrElementID) throws QueryMetadataException {
        try {
            return spi.getModelID(groupOrElementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getFullName(Object metadataID) throws QueryMetadataException {
        try {
            return spi.getFullName(metadataID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getName(Object metadataID) throws QueryMetadataException {
        try {
            return spi.getName(metadataID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public List getElementIDsInGroupID(Object groupID) throws QueryMetadataException {
        try {
            return spi.getElementIDsInGroupID(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getGroupIDForElementID(Object elementID) throws QueryMetadataException {
        try {
            return spi.getGroupIDForElementID(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public StoredProcedureInfo getStoredProcedureInfoForProcedure(String fullyQualifiedProcedureName)
        throws QueryMetadataException {
        try {
            StoredProcedureInfo storedProcedureInfo = factory.convert(spi.getStoredProcedureInfoForProcedure(fullyQualifiedProcedureName));
            return storedProcedureInfo;
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getElementType(Object elementID) throws QueryMetadataException {
        try {
            return spi.getElementType(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getDefaultValue(Object elementID) throws QueryMetadataException {
        try {
            return spi.getDefaultValue(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getMinimumValue(Object elementID) throws QueryMetadataException {
        try {
            return spi.getMinimumValue(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getMaximumValue(Object elementID) throws QueryMetadataException {
        try {
            return spi.getMaximumValue(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public int getPosition(Object elementID) throws QueryMetadataException {
        try {
            return spi.getPosition(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public int getPrecision(Object elementID) throws QueryMetadataException {
        try {
            return spi.getPrecision(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public int getScale(Object elementID) throws QueryMetadataException {
        try {
            return spi.getScale(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public int getRadix(Object elementID) throws QueryMetadataException {
        try {
            return spi.getRadix(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getFormat(Object elementID) throws QueryMetadataException {
        try {
            return spi.getFormat(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public int getDistinctValues(Object elementID) throws QueryMetadataException {
        try {
            return spi.getDistinctValues(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public int getNullValues(Object elementID) throws QueryMetadataException {
        try {
            return spi.getNullValues(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean isVirtualGroup(Object groupID) throws QueryMetadataException {
        try {
            return spi.isVirtualGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean isVirtualModel(Object modelID) throws QueryMetadataException {
        try {
            return spi.isVirtualModel(modelID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public QueryNode getVirtualPlan(Object groupID) throws QueryMetadataException {
        try {
            IQueryNode queryNode = spi.getVirtualPlan(groupID);
            return ((QueryNodeImpl) queryNode).getDelegate();
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getInsertPlan(Object groupID) throws QueryMetadataException {
        try {
            return spi.getInsertPlan(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getUpdatePlan(Object groupID) throws QueryMetadataException {
        try {
            return spi.getUpdatePlan(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getDeletePlan(Object groupID) throws QueryMetadataException {
        try {
            return spi.getDeletePlan(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean modelSupports(Object modelID, int modelConstant) throws QueryMetadataException {
        try {
            return spi.modelSupports(modelID, modelConstant);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean groupSupports(Object groupID, int groupConstant) throws QueryMetadataException {
        try {
            return spi.groupSupports(groupID, groupConstant);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean elementSupports(Object elementID, int elementConstant) throws QueryMetadataException {
        try {
            return spi.elementSupports(elementID, elementConstant);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Properties getExtensionProperties(Object metadataID) throws QueryMetadataException {
        try {
            return spi.getExtensionProperties(metadataID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public int getMaxSetSize(Object modelID) throws QueryMetadataException {
        try {
            return spi.getMaxSetSize(modelID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection getIndexesInGroup(Object groupID) throws QueryMetadataException {
        try {
            return spi.getIndexesInGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection getUniqueKeysInGroup(Object groupID) throws QueryMetadataException {
        try {
            return spi.getUniqueKeysInGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection getForeignKeysInGroup(Object groupID) throws QueryMetadataException {
        try {
            return spi.getForeignKeysInGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getPrimaryKeyIDForForeignKeyID(Object foreignKeyID) throws QueryMetadataException {
        try {
            return spi.getPrimaryKeyIDForForeignKeyID(foreignKeyID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection getAccessPatternsInGroup(Object groupID) throws QueryMetadataException {
        try {
            return spi.getAccessPatternsInGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public List getElementIDsInIndex(Object index) throws QueryMetadataException {
        try {
            return spi.getElementIDsInIndex(index);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public List getElementIDsInKey(Object key) throws QueryMetadataException {
        try {
            return spi.getElementIDsInKey(key);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public List getElementIDsInAccessPattern(Object accessPattern) throws QueryMetadataException {
        try {
            return spi.getElementIDsInAccessPattern(accessPattern);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean isXMLGroup(Object groupID) throws QueryMetadataException {
        try {
            return spi.isXMLGroup(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public MappingNode getMappingNode(Object groupID) throws QueryMetadataException {
        try {
            return mappingFactory.convert(spi.getMappingNode(groupID));
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getVirtualDatabaseName() throws QueryMetadataException {
        try {
            return spi.getVirtualDatabaseName();
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Collection<Object> getXMLTempGroups(Object groupID) throws QueryMetadataException {
        try {
            return spi.getXMLTempGroups(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public int getCardinality(Object groupID) throws QueryMetadataException {
        try {
            return spi.getCardinality(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public List getXMLSchemas(Object groupID) throws QueryMetadataException {
        try {
            return spi.getXMLSchemas(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getNameInSource(Object metadataID) throws QueryMetadataException {
        try {
            return spi.getNameInSource(metadataID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public int getElementLength(Object elementID) throws QueryMetadataException {
        try {
            return spi.getElementLength(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean hasMaterialization(Object groupID) throws QueryMetadataException {
        try {
            return spi.hasMaterialization(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getMaterialization(Object groupID) throws QueryMetadataException {
        try {
            return spi.getMaterialization(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getMaterializationStage(Object groupID) throws QueryMetadataException {
        try {
            return spi.getMaterializationStage(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getNativeType(Object elementID) throws QueryMetadataException {
        try {
            return spi.getNativeType(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean isProcedure(Object groupID) throws QueryMetadataException {
        try {
            return spi.isProcedure(groupID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public boolean hasProcedure(String procedureName) {
        return spi.hasProcedure(procedureName);
    }

    @Override
    public String[] getVDBResourcePaths() throws QueryMetadataException {
        try {
            return spi.getVDBResourcePaths();
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getModeledType(Object elementID) throws QueryMetadataException {
        try {
            return spi.getModeledType(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getModeledBaseType(Object elementID) throws QueryMetadataException {
        try {
            return spi.getModeledBaseType(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getModeledPrimitiveType(Object elementID) throws QueryMetadataException {
        try {
            return spi.getModeledPrimitiveType(elementID);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public String getCharacterVDBResource(String resourcePath) throws QueryMetadataException {
        try {
            return spi.getCharacterVDBResource(resourcePath);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public byte[] getBinaryVDBResource(String resourcePath) throws QueryMetadataException {
        try {
            return spi.getBinaryVDBResource(resourcePath);
        } catch (Exception ex) {
            throw new QueryMetadataException(ex.getMessage());
        }
    }

    @Override
    public Object getPrimaryKey(Object metadataID) {
        return spi.getPrimaryKey(metadataID);
    }

    @Override
    public FunctionLibrary getFunctionLibrary() {
        IFunctionLibrary functionLibrary = spi.getFunctionLibrary();
        FunctionLibraryImpl functionLibraryImpl = (FunctionLibraryImpl)functionLibrary;
        return functionLibraryImpl.getDelegate();
    }

}
