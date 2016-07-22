/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.script.ScriptEngine;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.xml.IMappingNode;
import org.teiid.query.sql.symbol.Expression;


/**
 * Basic Query Metadata Wrapper
 */
public class BasicQueryMetadataWrapper implements IQueryMetadataInterface {
	
	protected IQueryMetadataInterface actualMetadata;
	protected IQueryMetadataInterface designTimeMetadata;
	protected boolean designTime;

	/**
	 * @param actualMetadata
	 */
	public BasicQueryMetadataWrapper(IQueryMetadataInterface actualMetadata) {
		this.actualMetadata = actualMetadata;
	}

	@Override
	public ITeiidServerVersion getTeiidVersion() {
	    return actualMetadata.getTeiidVersion();
	}

	@Override
    public boolean elementSupports(Object elementID, int elementConstant)
			throws Exception {
		return actualMetadata.elementSupports(elementID, elementConstant);
	}

	@Override
    public Collection getAccessPatternsInGroup(Object groupID)
			throws Exception {
		return actualMetadata.getAccessPatternsInGroup(groupID);
	}

	@Override
    public byte[] getBinaryVDBResource(String resourcePath)
			throws Exception {
		return actualMetadata.getBinaryVDBResource(resourcePath);
	}

	@Override
	public float getCardinality(Object groupID)
			throws Exception {
		return actualMetadata.getCardinality(groupID);
	}

	@Override
    public String getCharacterVDBResource(String resourcePath)
			throws Exception {
		return actualMetadata.getCharacterVDBResource(resourcePath);
	}

	@Override
    public String getDefaultValue(Object elementID)
			throws Exception {
		return actualMetadata.getDefaultValue(elementID);
	}

	@Override
    public String getDeletePlan(Object groupID)
			throws Exception {
		return actualMetadata.getDeletePlan(groupID);
	}

	@Override
	public float getDistinctValues(Object elementID)
			throws Exception {
		return actualMetadata.getDistinctValues(elementID);
	}

	@Override
    public Object getElementID(String elementName)
			throws Exception {
		return actualMetadata.getElementID(elementName);
	}

	@Override
    public List getElementIDsInAccessPattern(Object accessPattern)
			throws Exception {
		return actualMetadata.getElementIDsInAccessPattern(accessPattern);
	}

	@Override
    public List getElementIDsInGroupID(Object groupID)
			throws Exception {
		return actualMetadata.getElementIDsInGroupID(groupID);
	}

	@Override
    public List getElementIDsInIndex(Object index)
			throws Exception {
		return actualMetadata.getElementIDsInIndex(index);
	}

	@Override
    public List getElementIDsInKey(Object key)
			throws Exception {
		return actualMetadata.getElementIDsInKey(key);
	}

	@Override
    public int getElementLength(Object elementID)
			throws Exception {
		return actualMetadata.getElementLength(elementID);
	}

	@Override
    public String getElementType(Object elementID)
			throws Exception {
		return actualMetadata.getElementType(elementID);
	}

	@Override
    public Properties getExtensionProperties(Object metadataID)
			throws Exception {
		return actualMetadata.getExtensionProperties(metadataID);
	}

	@Override
    public Collection getForeignKeysInGroup(Object groupID)
			throws Exception {
		return actualMetadata.getForeignKeysInGroup(groupID);
	}

	@Override
    public String getFullName(Object metadataID)
			throws Exception {
		return actualMetadata.getFullName(metadataID);
	}

	@Override
    public Object getGroupID(String groupName)
			throws Exception {
		return actualMetadata.getGroupID(groupName);
	}

	@Override
    public Object getGroupIDForElementID(Object elementID)
			throws Exception {
		return actualMetadata.getGroupIDForElementID(elementID);
	}

	@Override
    public Collection getGroupsForPartialName(String partialGroupName)
			throws Exception {
		return actualMetadata.getGroupsForPartialName(partialGroupName);
	}

	@Override
    public Collection getIndexesInGroup(Object groupID)
			throws Exception {
		return actualMetadata.getIndexesInGroup(groupID);
	}

	@Override
    public String getInsertPlan(Object groupID)
			throws Exception {
		return actualMetadata.getInsertPlan(groupID);
	}

	@Override
    public IMappingNode getMappingNode(Object groupID)
			throws Exception {
		return actualMetadata.getMappingNode(groupID);
	}

	@Override
    public Object getMaterialization(Object groupID)
			throws Exception {
		return actualMetadata.getMaterialization(groupID);
	}

	@Override
    public Object getMaterializationStage(Object groupID)
			throws Exception {
		return actualMetadata.getMaterializationStage(groupID);
	}

	@Override
    public Object getMaximumValue(Object elementID)
			throws Exception {
		return actualMetadata.getMaximumValue(elementID);
	}

	@Override
    public int getMaxSetSize(Object modelID)
			throws Exception {
		return actualMetadata.getMaxSetSize(modelID);
	}

	@Override
    public Object getMinimumValue(Object elementID)
			throws Exception {
		return actualMetadata.getMinimumValue(elementID);
	}

	@Override
    public Object getModelID(Object groupOrElementID)
			throws Exception {
		return actualMetadata.getModelID(groupOrElementID);
	}

	@Override
    public String getNameInSource(Object metadataID)
			throws Exception {
		return actualMetadata.getNameInSource(metadataID);
	}

	@Override
    public String getNativeType(Object elementID)
			throws Exception {
		return actualMetadata.getNativeType(elementID);
	}

	@Override
	public float getNullValues(Object elementID)
			throws Exception {
		return actualMetadata.getNullValues(elementID);
	}

	@Override
    public int getPosition(Object elementID)
			throws Exception {
		return actualMetadata.getPosition(elementID);
	}

	@Override
    public int getPrecision(Object elementID)
			throws Exception {
		return actualMetadata.getPrecision(elementID);
	}

	@Override
    public Object getPrimaryKeyIDForForeignKeyID(Object foreignKeyID)
			throws Exception {
		return actualMetadata.getPrimaryKeyIDForForeignKeyID(foreignKeyID);
	}

	@Override
    public int getRadix(Object elementID) throws Exception {
		return actualMetadata.getRadix(elementID);
	}
	
	@Override
    public String getFormat(Object elementID) throws Exception {
		return actualMetadata.getFormat(elementID);
	}   	

	@Override
    public int getScale(Object elementID) throws Exception {
		return actualMetadata.getScale(elementID);
	}

	@Override
    public IStoredProcedureInfo getStoredProcedureInfoForProcedure(
			String fullyQualifiedProcedureName)
			throws Exception {
		return actualMetadata
				.getStoredProcedureInfoForProcedure(fullyQualifiedProcedureName);
	}

	@Override
    public Collection getUniqueKeysInGroup(Object groupID)
			throws Exception {
		return actualMetadata.getUniqueKeysInGroup(groupID);
	}

	@Override
    public String getUpdatePlan(Object groupID)
			throws Exception {
		return actualMetadata.getUpdatePlan(groupID);
	}

	@Override
    public String[] getVDBResourcePaths() throws Exception {
		return actualMetadata.getVDBResourcePaths();
	}

	@Override
    public String getVirtualDatabaseName() throws Exception {
		return actualMetadata.getVirtualDatabaseName();
	}

	@Override
    public IQueryNode getVirtualPlan(Object groupID)
			throws Exception {
		return actualMetadata.getVirtualPlan(groupID);
	}

	@Override
    public List getXMLSchemas(Object groupID)
			throws Exception {
		return actualMetadata.getXMLSchemas(groupID);
	}

	@Override
    public Collection getXMLTempGroups(Object groupID)
			throws Exception {
		return actualMetadata.getXMLTempGroups(groupID);
	}

	@Override
    public boolean groupSupports(Object groupID, int groupConstant)
			throws Exception {
		return actualMetadata.groupSupports(groupID, groupConstant);
	}

	@Override
    public boolean hasMaterialization(Object groupID)
			throws Exception {
		return actualMetadata.hasMaterialization(groupID);
	}

	@Override
    public boolean isProcedure(Object groupID)
			throws Exception {
		return actualMetadata.isProcedure(groupID);
	}

	@Override
    public boolean isTemporaryTable(Object groupID)
			throws Exception {
		return actualMetadata.isTemporaryTable(groupID);
	}

	@Override
    public boolean isVirtualGroup(Object groupID)
			throws Exception {
		return actualMetadata.isVirtualGroup(groupID);
	}

	@Override
    public boolean isVirtualModel(Object modelID)
			throws Exception {
		return actualMetadata.isVirtualModel(modelID);
	}

	@Override
    public boolean isXMLGroup(Object groupID)
			throws Exception {
		return actualMetadata.isXMLGroup(groupID);
	}

	@Override
    public boolean modelSupports(Object modelID, int modelConstant)
			throws Exception {
		return actualMetadata.modelSupports(modelID, modelConstant);
	}

	@Override
    public Object addToMetadataCache(Object metadataID, String key, Object value)
			throws Exception {
		return actualMetadata.addToMetadataCache(metadataID, key, value);
	}

	@Override
    public Object getFromMetadataCache(Object metadataID, String key)
			throws Exception {
		return actualMetadata.getFromMetadataCache(metadataID, key);
	}

	@Override
    public boolean isScalarGroup(Object groupID)
			throws Exception {
		return actualMetadata.isScalarGroup(groupID);
	}

	@Override
	public IFunctionLibrary getFunctionLibrary() {
		return actualMetadata.getFunctionLibrary();
	}
	
	@Override
	public Object getPrimaryKey(Object metadataID) {
		return actualMetadata.getPrimaryKey(metadataID);
	}
	
	@Override
	public boolean isMultiSource(Object modelId) throws Exception {
		return actualMetadata.isMultiSource(modelId);
	}
	
	@Override
	public boolean isMultiSourceElement(Object elementId) throws Exception {
		return actualMetadata.isMultiSourceElement(elementId);
	}
	
	@Override
	public IQueryMetadataInterface getDesignTimeMetadata() {
		if (designTime) {
			return this;
		}
		if (designTimeMetadata == null) {
    		designTimeMetadata = createDesignTimeMetadata();
    		if (designTimeMetadata instanceof BasicQueryMetadataWrapper) {
    			((BasicQueryMetadataWrapper)designTimeMetadata).designTime = true;
    		}
    	}
		return designTimeMetadata;
	}
	
	protected IQueryMetadataInterface createDesignTimeMetadata() {
		return actualMetadata.getDesignTimeMetadata();
	}
	
	@Override
	public boolean hasProcedure(String name) throws Exception {
		return actualMetadata.hasProcedure(name);
	}
	
	@Override
	public String getName(Object metadataID) throws Exception {
		return actualMetadata.getName(metadataID);
	}
	
	@Override
	public IQueryMetadataInterface getSessionMetadata() {
		return actualMetadata.getSessionMetadata();
	}
	
	@Override
	public Set<String> getImportedModels() {
		return actualMetadata.getImportedModels();
	}
	
	@Override
	public ScriptEngine getScriptEngine(String language) throws Exception {
		return actualMetadata.getScriptEngine(language);
	}
	
	@Override
	public boolean isVariadic(Object metadataID) {
		return actualMetadata.isVariadic(metadataID);
	}
	
	@Override
	public Map<Expression, Integer> getFunctionBasedExpressions(Object metadataID) {
		return actualMetadata.getFunctionBasedExpressions(metadataID);
	}

	@Override
	public boolean isPseudo(Object elementId) {
		return actualMetadata.isPseudo(elementId);
	}
	
	@Override
	public Object getModelID(String modelName) throws Exception {
		return actualMetadata.getModelID(modelName);
	}
	
	@Override
	public String getExtensionProperty(Object metadataID, String key,
			boolean checkUnqualified) {
		return actualMetadata.getExtensionProperty(metadataID, key, checkUnqualified);
	}

	@Override
	public boolean findShortName() {
		return actualMetadata.findShortName();
	}
	
	@Override
	public boolean useOutputName() {
		return actualMetadata.useOutputName();
	}

    @Override
    public String getModeledType(Object elementID) throws Exception {
        return null;
    }

    @Override
    public String getModeledBaseType(Object elementID) throws Exception {
        return null;
    }

    @Override
    public String getModeledPrimitiveType(Object elementID) throws Exception {
        return null;
    }

    @Override
    @Since(Version.TEIID_8_12_4)
    public boolean widenComparisonToString() {
        return actualMetadata.widenComparisonToString();
    }
}
