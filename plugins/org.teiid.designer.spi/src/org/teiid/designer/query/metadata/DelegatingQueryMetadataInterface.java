/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.script.ScriptEngine;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.xml.IMappingNode;

/**
 *
 */
public class DelegatingQueryMetadataInterface implements IQueryMetadataInterface {

    private final IQueryMetadataInterface delegate;

    /**
     * @param delegate
     */
    public DelegatingQueryMetadataInterface(IQueryMetadataInterface delegate) {
        this.delegate = delegate;
    }

    @Override
    public ITeiidServerVersion getTeiidVersion() {
        return this.delegate.getTeiidVersion();
    }

    @Override
    public Object getElementID(String elementName) throws Exception {
        return this.delegate.getElementID(elementName);
    }

    @Override
    public Object getGroupID(String groupName) throws Exception {
        return this.delegate.getGroupID(groupName);
    }

    @Override
    public Collection getGroupsForPartialName(String partialGroupName) throws Exception {
        return this.delegate.getGroupsForPartialName(partialGroupName);
    }

    @Override
    public Object getModelID(Object groupOrElementID) throws Exception {
        return this.delegate.getModelID(groupOrElementID);
    }

    @Override
    public String getFullName(Object metadataID) throws Exception {
        return this.delegate.getFullName(metadataID);
    }

    @Override
    public String getName(Object metadataID) throws Exception {
        return this.delegate.getName(metadataID);
    }

    @Override
    public List getElementIDsInGroupID(Object groupID) throws Exception {
        return this.delegate.getElementIDsInGroupID(groupID);
    }

    @Override
    public Object getGroupIDForElementID(Object elementID) throws Exception {
        return this.delegate.getGroupIDForElementID(elementID);
    }

    @Override
    public IStoredProcedureInfo getStoredProcedureInfoForProcedure(String fullyQualifiedProcedureName) throws Exception {
        return this.delegate.getStoredProcedureInfoForProcedure(fullyQualifiedProcedureName);
    }

    @Override
    public String getElementType(Object elementID) throws Exception {
        return this.delegate.getElementType(elementID);
    }

    @Override
    public String getDefaultValue(Object elementID) throws Exception {
        return this.delegate.getDefaultValue(elementID);
    }

    @Override
    public Object getMinimumValue(Object elementID) throws Exception {
        return this.delegate.getMinimumValue(elementID);
    }

    @Override
    public Object getMaximumValue(Object elementID) throws Exception {
        return this.delegate.getMaximumValue(elementID);
    }

    @Override
    public int getPosition(Object elementID) throws Exception {
        return this.delegate.getPosition(elementID);
    }

    @Override
    public int getPrecision(Object elementID) throws Exception {
        return this.delegate.getPrecision(elementID);
    }

    @Override
    public int getScale(Object elementID) throws Exception {
        return this.delegate.getScale(elementID);
    }

    @Override
    public int getRadix(Object elementID) throws Exception {
        return this.delegate.getRadix(elementID);
    }

    @Override
    public String getFormat(Object elementID) throws Exception {
        return this.delegate.getFormat(elementID);
    }

    @Override
    public float getDistinctValues(Object elementID) throws Exception {
        return this.delegate.getDistinctValues(elementID);
    }

    @Override
    public float getNullValues(Object elementID) throws Exception {
        return this.delegate.getNullValues(elementID);
    }

    @Override
    public boolean isVirtualGroup(Object groupID) throws Exception {
        return this.delegate.isVirtualGroup(groupID);
    }

    @Override
    public boolean isVirtualModel(Object modelID) throws Exception {
        return this.delegate.isVirtualModel(modelID);
    }

    @Override
    public IQueryNode getVirtualPlan(Object groupID) throws Exception {
        return this.delegate.getVirtualPlan(groupID);
    }

    @Override
    public String getInsertPlan(Object groupID) throws Exception {
        return this.delegate.getInsertPlan(groupID);
    }

    @Override
    public String getUpdatePlan(Object groupID) throws Exception {
        return this.delegate.getUpdatePlan(groupID);
    }

    @Override
    public String getDeletePlan(Object groupID) throws Exception {
        return this.delegate.getDeletePlan(groupID);
    }

    @Override
    public boolean modelSupports(Object modelID, int modelConstant) throws Exception {
        return this.delegate.modelSupports(modelID, modelConstant);
    }

    @Override
    public boolean groupSupports(Object groupID, int groupConstant) throws Exception {
        return this.delegate.groupSupports(groupID, groupConstant);
    }

    @Override
    public boolean elementSupports(Object elementID, int elementConstant) throws Exception {
        return this.delegate.elementSupports(elementID, elementConstant);
    }

    @Override
    public Properties getExtensionProperties(Object metadataID) throws Exception {
        return this.delegate.getExtensionProperties(metadataID);
    }

    @Override
    public int getMaxSetSize(Object modelID) throws Exception {
        return this.delegate.getMaxSetSize(modelID);
    }

    @Override
    public Collection getIndexesInGroup(Object groupID) throws Exception {
        return this.delegate.getIndexesInGroup(groupID);
    }

    @Override
    public Collection getUniqueKeysInGroup(Object groupID) throws Exception {
        return this.delegate.getUniqueKeysInGroup(groupID);
    }

    @Override
    public Collection getForeignKeysInGroup(Object groupID) throws Exception {
        return this.delegate.getForeignKeysInGroup(groupID);
    }

    @Override
    public Object getPrimaryKeyIDForForeignKeyID(Object foreignKeyID) throws Exception {
        return this.delegate.getPrimaryKeyIDForForeignKeyID(foreignKeyID);
    }

    @Override
    public Collection getAccessPatternsInGroup(Object groupID) throws Exception {
        return this.delegate.getAccessPatternsInGroup(groupID);
    }

    @Override
    public List getElementIDsInIndex(Object index) throws Exception {
        return this.delegate.getElementIDsInIndex(index);
    }

    @Override
    public List getElementIDsInKey(Object key) throws Exception {
        return this.delegate.getElementIDsInKey(key);
    }

    @Override
    public List getElementIDsInAccessPattern(Object accessPattern) throws Exception {
        return this.delegate.getElementIDsInAccessPattern(accessPattern);
    }

    @Override
    public boolean isXMLGroup(Object groupID) throws Exception {
        return this.delegate.isXMLGroup(groupID);
    }

    @Override
    public IMappingNode getMappingNode(Object groupID) throws Exception {
        return this.delegate.getMappingNode(groupID);
    }

    @Override
    public String getVirtualDatabaseName() throws Exception {
        return this.delegate.getVirtualDatabaseName();
    }

    @Override
    public Collection getXMLTempGroups(Object groupID) throws Exception {
        return this.delegate.getXMLTempGroups(groupID);
    }

    @Override
    public float getCardinality(Object groupID) throws Exception {
        return this.delegate.getCardinality(groupID);
    }

    @Override
    public List getXMLSchemas(Object groupID) throws Exception {
        return this.delegate.getXMLSchemas(groupID);
    }

    @Override
    public String getNameInSource(Object metadataID) throws Exception {
        return this.delegate.getNameInSource(metadataID);
    }

    @Override
    public int getElementLength(Object elementID) throws Exception {
        return this.delegate.getElementLength(elementID);
    }

    @Override
    public boolean hasMaterialization(Object groupID) throws Exception {
        return this.delegate.hasMaterialization(groupID);
    }

    @Override
    public Object getMaterialization(Object groupID) throws Exception {
        return this.delegate.getMaterialization(groupID);
    }

    @Override
    public Object getMaterializationStage(Object groupID) throws Exception {
        return this.delegate.getMaterializationStage(groupID);
    }

    @Override
    public String getNativeType(Object elementID) throws Exception {
        return this.delegate.getNativeType(elementID);
    }

    @Override
    public boolean isProcedure(Object groupID) throws Exception {
        return this.delegate.isProcedure(groupID);
    }

    @Override
    public boolean hasProcedure(String procedureName) throws Exception {
        return this.delegate.hasProcedure(procedureName);
    }

    @Override
    public String[] getVDBResourcePaths() throws Exception {
        return this.delegate.getVDBResourcePaths();
    }

    @Override
    public String getModeledType(Object elementID) throws Exception {
        return this.delegate.getModeledType(elementID);
    }

    @Override
    public String getModeledBaseType(Object elementID) throws Exception {
        return this.delegate.getModeledBaseType(elementID);
    }

    @Override
    public String getModeledPrimitiveType(Object elementID) throws Exception {
        return this.delegate.getModeledPrimitiveType(elementID);
    }

    @Override
    public String getCharacterVDBResource(String resourcePath) throws Exception {
        return this.delegate.getCharacterVDBResource(resourcePath);
    }

    @Override
    public byte[] getBinaryVDBResource(String resourcePath) throws Exception {
        return this.delegate.getBinaryVDBResource(resourcePath);
    }

    @Override
    public Object getPrimaryKey(Object metadataID) {
        return this.delegate.getPrimaryKey(metadataID);
    }

    @Override
    public IFunctionLibrary getFunctionLibrary() {
        return this.delegate.getFunctionLibrary();
    }

    @Override
    public boolean isTemporaryTable(Object groupID) throws Exception {
        return this.delegate.isTemporaryTable(groupID);
    }

    @Override
    public Object addToMetadataCache(Object metadataID, String key, Object value) throws Exception {
        return this.delegate.addToMetadataCache(metadataID, key, value);
    }

    @Override
    public Object getFromMetadataCache(Object metadataID, String key) throws Exception {
        return this.delegate.getFromMetadataCache(metadataID, key);
    }

    @Override
    public boolean isScalarGroup(Object groupID) throws Exception {
        return this.delegate.isScalarGroup(groupID);
    }

    @Override
    public boolean isMultiSource(Object modelId) throws Exception {
        return this.delegate.isMultiSource(modelId);
    }

    @Override
    public boolean isMultiSourceElement(Object elementId) throws Exception {
        return this.delegate.isMultiSourceElement(elementId);
    }

    @Override
    public IQueryMetadataInterface getDesignTimeMetadata() {
        return this.delegate.getDesignTimeMetadata();
    }

    @Override
    public IQueryMetadataInterface getSessionMetadata() {
        return this.delegate.getSessionMetadata();
    }

    @Override
    public Set getImportedModels() {
        return this.delegate.getImportedModels();
    }

    @Override
    public ScriptEngine getScriptEngine(String language) throws Exception {
        return this.delegate.getScriptEngine(language);
    }

    @Override
    public boolean isVariadic(Object metadataID) {
        return this.delegate.isVariadic(metadataID);
    }

    @Override
    public Map getFunctionBasedExpressions(Object metadataID) {
        return this.delegate.getFunctionBasedExpressions(metadataID);
    }

    @Override
    public boolean isPseudo(Object elementId) {
        return this.delegate.isPseudo(elementId);
    }

    @Override
    public Object getModelID(String modelName) throws Exception {
        return this.delegate.getModelID(modelName);
    }

    @Override
    public String getExtensionProperty(Object metadataID, String key, boolean checkUnqualified) {
        return this.delegate.getExtensionProperty(metadataID, key, checkUnqualified);
    }

    @Override
    public boolean useOutputName() {
        return this.delegate.useOutputName();
    }

    @Override
    public boolean findShortName() {
        return this.delegate.findShortName();
    }

    @Override
    public boolean widenComparisonToString() {
        return this.delegate.widenComparisonToString();
    }

}
