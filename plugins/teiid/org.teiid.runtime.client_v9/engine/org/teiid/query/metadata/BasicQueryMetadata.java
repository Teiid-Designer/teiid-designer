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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.script.ScriptEngine;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.xml.IMappingNode;
import org.teiid.query.eval.TeiidScriptEngine;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.sql.lang.ObjectTable;
import org.teiid.query.sql.symbol.Expression;


/**
 * This is an abstract implementation of the metadata interface.  It can 
 * be subclassed to create test implementations or partial implementations.
 */
public class BasicQueryMetadata implements IQueryMetadataInterface {
	
    private final ITeiidServerVersion teiidVersion;

    /**
     * Constructor for AbstractQueryMetadata.
     * @param teiidVersion
     */
    public BasicQueryMetadata(ITeiidServerVersion teiidVersion) {
        super();
        this.teiidVersion = teiidVersion;
    }

    @Override
    public ITeiidServerVersion getTeiidVersion() {
        return teiidVersion;
    }

    /**
     * @see IQueryMetadataInterface#getElementID(String)
     */
    @Override
    public Object getElementID(String elementName)
        throws Exception {
        return null;
    }

    /**
     * @see IQueryMetadataInterface#getGroupID(String)
     */
    @Override
    public Object getGroupID(String groupName)
        throws Exception {
        return null;
    }
    
    /**
     * @see IQueryMetadataInterface#getGroupID(String)
     */
    @Override
    public Collection getGroupsForPartialName(String partialGroupName)
        throws Exception {
		return Collections.EMPTY_LIST;        	
    }

    /**
     * @see IQueryMetadataInterface#getModelID(Object)
     */
    @Override
    public Object getModelID(Object groupOrElementID)
        throws Exception {
        return null;
    }

    /**
     * @see IQueryMetadataInterface#getFullName(Object)
     */
    @Override
    public String getFullName(Object metadataID)
        throws Exception {
        return null;
    }

    /**
     * @see IQueryMetadataInterface#getElementIDsInGroupID(Object)
     */
    @Override
    public List getElementIDsInGroupID(Object groupID)
        throws Exception {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see IQueryMetadataInterface#getGroupIDForElementID(Object)
     */
    @Override
    public Object getGroupIDForElementID(Object elementID)
        throws Exception {
        return null;
    }

    /**
     * @see IQueryMetadataInterface#getStoredProcedureInfoForProcedure(String)
     */
    @Override
    public StoredProcedureInfo getStoredProcedureInfoForProcedure(String fullyQualifiedProcedureName)
        throws Exception {
        return null;
    }

    /**
     * @see IQueryMetadataInterface#getElementType(Object)
     */
    @Override
    public String getElementType(Object elementID)
        throws Exception {
        return null;
    }

    @Override
    public String getDefaultValue(Object elementID)
        throws Exception {
        return null;
	}    

    @Override
    public Object getMaximumValue(Object elementID) throws Exception {
        return null;
    }

    @Override
    public Object getMinimumValue(Object elementID) throws Exception {
        return null;
    }
    
    /** 
     * @see IQueryMetadataInterface#getDistinctValues(java.lang.Object)
     * @since 4.3
     */
    @Override
    public float getDistinctValues(Object elementID) throws Exception {
        return -1;
    }
    /** 
     * @see IQueryMetadataInterface#getNullValues(java.lang.Object)
     * @since 4.3
     */
    @Override
    public float getNullValues(Object elementID) throws Exception {
        return -1;
    }
    
    @Override
    public int getPosition(Object elementID) throws Exception {
        return 0;
    }
    
    @Override
    public int getPrecision(Object elementID) throws Exception {
        return 0;
    }
    
    @Override
    public int getRadix(Object elementID) throws Exception {
        return 0;
    }

	@Override
	public String getFormat(Object elementID) throws Exception {
		return null;
	}    
    
    @Override
    public int getScale(Object elementID) throws Exception {
        return 0;
    }
    

    /**
     * @see IQueryMetadataInterface#isVirtualGroup(Object)
     */
    @Override
    public boolean isVirtualGroup(Object groupID)
        throws Exception {
        return false;
    }

    /** 
     * @see IQueryMetadataInterface#hasMaterialization(java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean hasMaterialization(Object groupID) 
        throws Exception {
        return false;
    }
    
    /** 
     * @see IQueryMetadataInterface#getMaterialization(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Object getMaterialization(Object groupID) 
        throws Exception {
        return null;
    }
    
    /** 
     * @see IQueryMetadataInterface#getMaterializationStage(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Object getMaterializationStage(Object groupID) 
        throws Exception {
        return null;
    }
    
    /**
     * @see IQueryMetadataInterface#isVirtualModel(Object)
     */
    @Override
    public boolean isVirtualModel(Object modelID)
        throws Exception {
        return false;
    }

    /**
     * @see IQueryMetadataInterface#getVirtualPlan(Object)
     */
    @Override
    public QueryNode getVirtualPlan(Object groupID)
        throws Exception {
        return null;
    }
    
	/**
	 * Get procedure defining the insert plan for this group.
	 * @param groupID Group
	 * @return A string giving the procedure for inserts.
	 */
    @Override
    public String getInsertPlan(Object groupID)
        throws Exception {
        return null;
    }
        
	/**
	 * Get procedure defining the update plan for this group.
	 * @param groupID Group
	 * @return A string giving the procedure for inserts.
	 */
    @Override
    public String getUpdatePlan(Object groupID)
        throws Exception {
        return null;
    }
        
	/**
	 * Get procedure defining the delete plan for this group.
	 * @param groupID Group
	 * @return A string giving the procedure for inserts.
	 */
    @Override
    public String getDeletePlan(Object groupID)
        throws Exception {
        return null;
    }

    /**
     * @see IQueryMetadataInterface#modelSupports(Object, int)
     */
    @Override
    public boolean modelSupports(Object modelID, int modelConstant)
        throws Exception {
        return false;
    }

    /**
     * @see IQueryMetadataInterface#groupSupports(Object, int)
     */
    @Override
    public boolean groupSupports(Object groupID, int groupConstant)
        throws Exception {
        return false;
    }

    /**
     * @see IQueryMetadataInterface#elementSupports(Object, int)
     */
    @Override
    public boolean elementSupports(Object elementID, int elementConstant)
        throws Exception {
        return false;
    }

    /**
     * @see IQueryMetadataInterface#getMaxSetSize(Object)
     */
    @Override
    public int getMaxSetSize(Object modelID)
        throws Exception {
        return 0;
    }


    /**
     * @see IQueryMetadataInterface#getIndexesInGroup(java.lang.Object)
     */
    @Override
    public Collection getIndexesInGroup(Object groupID)
        throws Exception {
        return Collections.EMPTY_SET;
    }

    /**
     * @see IQueryMetadataInterface#getUniqueKeysInGroup(Object)
     */
    @Override
    public Collection getUniqueKeysInGroup(Object groupID)
        throws Exception {
        return Collections.EMPTY_SET;
    }

    /**
     * @see IQueryMetadataInterface#getForeignKeysInGroup(Object)
     */
    @Override
    public Collection getForeignKeysInGroup(Object groupID)
        throws Exception {
        return Collections.EMPTY_SET;
    }

    /**
     * @see IQueryMetadataInterface#getPrimaryKeyIDForForeignKeyID(Object)
     */
    @Override
    public Object getPrimaryKeyIDForForeignKeyID(Object foreignKeyID)
        throws Exception{
        return null;
    }

    /**
     * @see IQueryMetadataInterface#getElementIDsInKey(Object)
     */
    @Override
    public List getElementIDsInKey(Object key)
        throws Exception {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see IQueryMetadataInterface#getElementIDsInIndex(java.lang.Object)
     */
    @Override
    public List getElementIDsInIndex(Object index)
        throws Exception {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see IQueryMetadataInterface#getAccessPatternsInGroup(Object)
     */
    @Override
    public Collection getAccessPatternsInGroup(Object groupID)
        throws Exception {
        return Collections.EMPTY_SET;
    }

    /**
     * @see IQueryMetadataInterface#getElementIDsInAccessPattern(Object)
     */
    @Override
    public List getElementIDsInAccessPattern(Object accessPattern)
        throws Exception {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see IQueryMetadataInterface#isXMLGroup(Object)
     */
    @Override
    public boolean isXMLGroup(Object groupID)
        throws Exception {
        return false;
    }

    /**
     * @see IQueryMetadataInterface#getMappingNode(Object)
     */
    @Override
    public IMappingNode getMappingNode(Object groupID)
        throws Exception {
        return null;
    }
    
    /**
     * @see IQueryMetadataInterface#getVirtualDatabaseName()
     */
    @Override
    public String getVirtualDatabaseName() 
        throws Exception {
            
        return null;
    }
    
    @Override
    public Collection getXMLTempGroups(Object groupID) 
        throws Exception{
    	
    	return Collections.EMPTY_SET;    	
    }
    
    @Override
    public float getCardinality(Object groupID) 
    	throws Exception{
    		
    	return IQueryMetadataInterface.UNKNOWN_CARDINALITY;
    }

    @Override
    public List getXMLSchemas(Object groupID) throws Exception {
        return null;
    }

    @Override
    public String getNameInSource(Object metadataID) throws Exception {
        return null;
    }

    @Override
    public int getElementLength(Object elementID) throws Exception {
        return 0;
    }

    @Override
    public Properties getExtensionProperties(Object metadataID)
        throws Exception {
        return null;
    }

    @Override
    public String getNativeType(Object elementID) throws Exception {
        return null;
    }

	@Override
    public boolean isProcedure(Object elementID) throws Exception {
		return false;
	}
    
    @Override
    public byte[] getBinaryVDBResource(String resourcePath) throws Exception {
        return null;
    }

    @Override
    public String getCharacterVDBResource(String resourcePath) throws Exception {
        return null;
    }

    @Override
    public String[] getVDBResourcePaths() throws Exception {
        return null;
    }
    
    /** 
     * @see IQueryMetadataInterface#getModeledType(java.lang.Object)
     * @since 5.0
     */
    @Override
    public String getModeledType(Object elementID) throws Exception {
        return null;
    }
    
    /** 
     * @see IQueryMetadataInterface#getModeledBaseType(java.lang.Object)
     * @since 5.0
     */
    @Override
    public String getModeledBaseType(Object elementID) throws Exception {
        return null;
    }

    /** 
     * @see IQueryMetadataInterface#getModeledPrimitiveType(java.lang.Object)
     * @since 5.0
     */
    @Override
    public String getModeledPrimitiveType(Object elementID) throws Exception {
        return null;
    }
   
    @Override
    public boolean isTemporaryTable(Object groupID)
        throws Exception {
        return false;
    }

	@Override
    public Object addToMetadataCache(Object metadataID, String key, Object value)
			throws Exception {
		return null;
	}

	@Override
    public Object getFromMetadataCache(Object metadataID, String key)
			throws Exception {
		return null;
	}

	@Override
    public boolean isScalarGroup(Object groupID)
			throws Exception {
		return false;
	}

	@Override
	public IFunctionLibrary getFunctionLibrary() {
		return null;
	}
	
	@Override
	public Object getPrimaryKey(Object metadataID) {
		return null;
	}
	
	@Override
	public boolean isMultiSource(Object modelId) {
		return false;
	}
	
	@Override
	public boolean isMultiSourceElement(Object elementId) {
		return false;
	}
	
	@Override
	public IQueryMetadataInterface getDesignTimeMetadata() {
		return this;
	}
	
	@Override
	public boolean hasProcedure(String name) throws Exception {
		return false;
	}
	
	@Override
	public String getName(Object metadataID) throws Exception {
		return null;
	}
	
	@Override
	public IQueryMetadataInterface getSessionMetadata() {
		return null;
	}
	
	@Override
	public Set<String> getImportedModels() {
		return Collections.emptySet();
	}

	@Override
	public ScriptEngine getScriptEngine(String language) throws Exception {
		if (language == null || ObjectTable.DEFAULT_LANGUAGE.equals(language)) {
			return new TeiidScriptEngine();
		}
		return getScriptEngineDirect(language);
	}
	
	/**
	 * 
	 * @param language
	 * @return script engine directly associated with language
	 * @throws Exception
	 */
	public ScriptEngine getScriptEngineDirect(String language) throws Exception {
		return null;
	}
	
	@Override
	public boolean isVariadic(Object metadataID) {
		return false;
	}
	
	@Override
	public Map<Expression, Integer> getFunctionBasedExpressions(Object metadataID) {
		return null;
	}

	@Override
	public boolean isPseudo(Object elementId) {
		return false;
	}
	
	@Override
	public Object getModelID(String modelName) throws Exception {
		return null;
	}
	
	@Override
	public String getExtensionProperty(Object metadataID, String key,
			boolean checkUnqualified) {
		return null;
	}

	@Override
    public boolean findShortName() {
        return false;
    }
    
    @Override
    public boolean useOutputName() {
        return true;
    }

    @Override
    @Since(Version.TEIID_8_12_4)
    public boolean widenComparisonToString() {
        return true;
    }
}
