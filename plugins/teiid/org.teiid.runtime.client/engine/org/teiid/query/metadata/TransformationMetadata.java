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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import org.teiid.adminapi.impl.DataPolicyMetadata;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.core.types.BlobImpl;
import org.teiid.core.types.ClobImpl;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.InputStreamFactory;
import org.teiid.core.types.SQLXMLImpl;
import org.teiid.core.util.ArgCheck;
import org.teiid.core.util.LRUCache;
import org.teiid.core.util.ObjectConverterUtil;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.AbstractMetadataRecord;
import org.teiid.metadata.BaseColumn.NullType;
import org.teiid.metadata.Column;
import org.teiid.metadata.Column.SearchType;
import org.teiid.metadata.ColumnSet;
import org.teiid.metadata.ForeignKey;
import org.teiid.metadata.FunctionParameter;
import org.teiid.metadata.KeyRecord;
import org.teiid.metadata.Procedure;
import org.teiid.metadata.ProcedureParameter;
import org.teiid.metadata.ProcedureParameter.Type;
import org.teiid.metadata.Schema;
import org.teiid.metadata.Table;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.function.FunctionTree;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.mapping.xml.MappingDocument;
import org.teiid.query.mapping.xml.MappingLoader;
import org.teiid.query.mapping.xml.MappingNode;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.lang.ObjectTable;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;


/**
 * Teiid's implementation of the QueryMetadataInterface that reads columns, groups, models etc.
 * from the metadata object model.
 */
public class TransformationMetadata extends BasicQueryMetadata implements Serializable {
	
	public static final String ALLOWED_LANGUAGES = "allowed-languages"; //$NON-NLS-1$

	private static final class LiveQueryNode extends QueryNode {
		Procedure p;
		private LiveQueryNode(Procedure p) {
			super(null);
			this.p = p;
		}

		public String getQuery() {
			return p.getQueryPlan();
		}
	}
	
	private static final class LiveTableQueryNode extends QueryNode {
		Table t;
		private LiveTableQueryNode(Table t) {
			super(null);
			this.t = t;
		}

		public String getQuery() {
			return t.getSelectTransformation();
		}
	}

	private final class VirtualFileInputStreamFactory extends
			InputStreamFactory {
		private final VDBResources.Resource r;

		private VirtualFileInputStreamFactory(VDBResources.Resource r) {
			this.r = r;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return r.openStream();
		}
		
		@Override
		public long getLength() {
			return r.getSize();
		}
		
		@Override
		public StorageMode getStorageMode() {
			return StorageMode.PERSISTENT;
		}
	}

	private static final long serialVersionUID = 1058627332954475287L;
	
	/** Delimiter character used when specifying fully qualified entity names */
    public static final char DELIMITER_CHAR = StringUtil.Constants.DOT_CHAR;
    public static final String DELIMITER_STRING = String.valueOf(DELIMITER_CHAR);
    
    // error message cached to avoid i18n lookup each time
    public static String NOT_EXISTS_MESSAGE = StringUtil.Constants.SPACE+Messages.getString(Messages.TransformationMetadata.doesNotExist);

    public static Properties EMPTY_PROPS = new Properties();

    private final TeiidParser teiidParser;
    private final CompositeMetadataStore store;
    private DataTypeManagerService dataTypeManager;
    private Map<String, VDBResources.Resource> vdbEntries;
    private FunctionLibrary functionLibrary;
    private VDBMetaData vdbMetaData;
    private ScriptEngineManager scriptEngineManager;
    private Map<String, ScriptEngineFactory> scriptEngineFactories = Collections.synchronizedMap(new HashMap<String, ScriptEngineFactory>());
    private Set<String> importedModels;
    private Set<String> allowedLanguages;
    private Map<String, DataPolicyMetadata> policies = new TreeMap<String, DataPolicyMetadata>(String.CASE_INSENSITIVE_ORDER);
    @Since(Version.TEIID_8_5)
    private boolean useOutputNames = true;
    
    /*
     * TODO: move caching to jboss cache structure
     */
    private Map<String, Object> metadataCache = Collections.synchronizedMap(new LRUCache<String, Object>(250));
    private Map<String, Object> groupInfoCache = Collections.synchronizedMap(new LRUCache<String, Object>(250));
    private Map<String, Collection<Table>> partialNameToFullNameCache = Collections.synchronizedMap(new LRUCache<String, Collection<Table>>(1000));
    private Map<String, Collection<StoredProcedureInfo>> procedureCache = Collections.synchronizedMap(new LRUCache<String, Collection<StoredProcedureInfo>>(200));

    @Since(Version.TEIID_8_12_4)
    private boolean widenComparisonToString = true;

    /**
     * TransformationMetadata constructor
     * @param teiidParser
     * @param vdbMetadata
     * @param store
     * @param vdbEntries
     * @param systemFunctions 
     * @param functionTrees
     */
    public TransformationMetadata(TeiidParser teiidParser, VDBMetaData vdbMetadata, final CompositeMetadataStore store, Map<String, VDBResources.Resource> vdbEntries, FunctionTree systemFunctions, Collection<FunctionTree> functionTrees) {
        super(teiidParser.getVersion());
    	ArgCheck.isNotNull(store);
    	this.teiidParser = teiidParser;
    	this.vdbMetaData = vdbMetadata;
    	if (this.vdbMetaData !=null) {
    		this.scriptEngineManager = vdbMetadata.getAttachment(ScriptEngineManager.class);
    		this.importedModels = this.vdbMetaData.getImportedModels();
    		this.allowedLanguages = StringUtil.valueOf(vdbMetadata.getPropertyValue(ALLOWED_LANGUAGES), Set.class); 
    		if (this.allowedLanguages == null) {
    			this.allowedLanguages = Collections.emptySet();
    		}
    		for (DataPolicyMetadata policy : vdbMetadata.getDataPolicyMap().values()) {
    			policy = policy.clone();
    			policies.put(policy.getName(), policy);
    		}
    		store.processGrants(policies);
    	} else {
    		this.importedModels = Collections.emptySet();
    	}
        this.store = store;
        if (vdbEntries == null) {
        	this.vdbEntries = Collections.emptyMap();
        } else {
        	this.vdbEntries = vdbEntries;
        }
        if (functionTrees == null) {
        	this.functionLibrary = new FunctionLibrary(teiidParser.getVersion(), systemFunctions);
        } else {
            this.functionLibrary = new FunctionLibrary(teiidParser.getVersion(), systemFunctions, functionTrees.toArray(new FunctionTree[functionTrees.size()]));
        }
    }

    private TransformationMetadata(TeiidParser teiidParser, final CompositeMetadataStore store, FunctionLibrary functionLibrary) {
        super(teiidParser.getVersion());
        this.teiidParser = teiidParser;
        this.store = store;
    	this.vdbEntries = Collections.emptyMap();
        this.functionLibrary = functionLibrary;
    }

    public TeiidParser getTeiidParser() {
        return teiidParser;
    }

    public ITeiidServerVersion getTeiidVersion() {
        return teiidParser.getVersion();
    }

    /**
     * @return the dataTypeManager
     */
    public DataTypeManagerService getDataTypeManager() {
        if (dataTypeManager == null)
            dataTypeManager = DataTypeManagerService.getInstance(getTeiidVersion());

        return this.dataTypeManager;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    public Column getElementID(final String elementName) throws Exception {
    	int columnIndex = elementName.lastIndexOf(TransformationMetadata.DELIMITER_STRING);
		if (columnIndex == -1) {
			 throw new TeiidClientException(elementName+TransformationMetadata.NOT_EXISTS_MESSAGE);
		}
		Table table = this.store.findGroup(elementName.substring(0, columnIndex));
		String shortElementName = elementName.substring(columnIndex + 1);
		return getColumn(elementName, table, shortElementName);
    }

	public static Column getColumn(final String elementName, Table table,
			String shortElementName) throws Exception {
		Column c = table.getColumnByName(shortElementName);
		if (c != null) {
			return c;
		}
         throw new TeiidClientException(elementName+TransformationMetadata.NOT_EXISTS_MESSAGE);
	}

    public Table getGroupID(String groupName) throws Exception {
        if (getTeiidVersion().isLessThan(Version.TEIID_8_0))
            groupName = groupName.toUpperCase();

        return getMetadataStore().findGroup(groupName);
    }

    public Collection<String> getGroupsForPartialName(final String partialGroupName)
        throws Exception {
		ArgCheck.isNotEmpty(partialGroupName);

		Collection<Table> matches = this.partialNameToFullNameCache.get(partialGroupName);
		
		if (matches == null) {
	        matches = getMetadataStore().getGroupsForPartialName(partialGroupName);
	        
        	this.partialNameToFullNameCache.put(partialGroupName, matches);
		}
		
		if (matches.isEmpty()) {
			return Collections.emptyList();
		}
		
		Collection<String> filteredResult = new ArrayList<String>(matches.size());
		for (Table table : matches) {
			if (vdbMetaData == null || vdbMetaData.isVisible(table.getParent().getName())) {
	        	filteredResult.add(table.getFullName());
	        }
		}
		return filteredResult;
    }

    public Object getModelID(final Object groupOrElementID) throws Exception {
        AbstractMetadataRecord metadataRecord = (AbstractMetadataRecord) groupOrElementID;
        AbstractMetadataRecord parent = metadataRecord.getParent();
        if (parent instanceof Schema) {
        	return parent;
        }
        if (parent == null) {
        	throw createInvalidRecordTypeException(groupOrElementID);
        }
        parent = parent.getParent();
        if (parent instanceof Schema) {
        	return parent;
        }
    	throw createInvalidRecordTypeException(groupOrElementID);
    }

    public String getFullName(final Object metadataID) throws Exception {
        AbstractMetadataRecord metadataRecord = (AbstractMetadataRecord) metadataID;
        if (metadataRecord instanceof Column) {
        	Column c = (Column)metadataRecord;
        	if (c.getParent() != null && c.getParent().getParent() instanceof Procedure) {
        		return c.getParent().getParent().getFullName() + '.' + c.getName();
        	}
        }
        return metadataRecord.getFullName();
    }
    
    @Override
    public String getName(Object metadataID) throws Exception,
    		TeiidClientException {
        AbstractMetadataRecord metadataRecord = (AbstractMetadataRecord) metadataID;
        return metadataRecord.getName();
    }

    public List<Column> getElementIDsInGroupID(final Object groupID) throws Exception {
    	List<Column> columns = ((Table)groupID).getColumns();
    	if (columns == null || columns.isEmpty()) {
    		throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID31071, ((Table)groupID).getName()));
    	}
    	return columns;
    }

    public Object getGroupIDForElementID(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            Column columnRecord = (Column) elementID;
            AbstractMetadataRecord parent = columnRecord.getParent();
            if (parent instanceof Table) {
            	return parent;
            }
            if (parent instanceof ColumnSet) {
            	parent = ((ColumnSet<?>)parent).getParent();
            	if (parent instanceof Procedure) {
            		return parent;
            	}
            }
        } 
        if(elementID instanceof ProcedureParameter) {
        	ProcedureParameter columnRecord = (ProcedureParameter) elementID;
            return columnRecord.getParent();
        }
        throw createInvalidRecordTypeException(elementID);
    }
    
    public boolean hasProcedure(String name) throws Exception {
    	try {
    		return getStoredProcInfoDirect(name) != null;
    	} catch (TeiidClientException e) {
    		return true;
    	}
    }

    public StoredProcedureInfo getStoredProcedureInfoForProcedure(final String name)
        throws Exception {
        StoredProcedureInfo result = getStoredProcInfoDirect(name);
        
		if (result == null) {
			 throw new TeiidClientException(name+NOT_EXISTS_MESSAGE);
		}
    	
        return result;
    }

	private StoredProcedureInfo getStoredProcInfoDirect(
			final String name)
			throws Exception {
		ArgCheck.isNotEmpty(name);
        String canonicalName = name.toUpperCase();
        Collection<StoredProcedureInfo> results = this.procedureCache.get(canonicalName);
        
        if (results == null) {
        	Collection<Procedure> procRecords = getMetadataStore().getStoredProcedure(canonicalName);
        	if (procRecords.isEmpty()) {
        		return null;
        	}
        	results = new ArrayList<StoredProcedureInfo>(procRecords.size());
        	for (Procedure procRecord : procRecords) {
                String procedureFullName = procRecord.getFullName();

                // create the storedProcedure info object that would hold procedure's metadata
                StoredProcedureInfo procInfo = new StoredProcedureInfo();
                procInfo.setProcedureCallableName(procedureFullName);
                procInfo.setProcedureID(procRecord);

                // modelID for the procedure
                procInfo.setModelID(procRecord.getParent());

                // get the parameter metadata info
                for (ProcedureParameter paramRecord : procRecord.getParameters()) {
                    String runtimeType = paramRecord.getRuntimeType();
                    int direction = this.convertParamRecordTypeToStoredProcedureType(paramRecord.getType());
                    // create a parameter and add it to the procedure object
                    SPParameter spParam = new SPParameter(getTeiidParser(), paramRecord.getPosition(), direction, paramRecord.getFullName());
                    spParam.setMetadataID(paramRecord);
                    spParam.setClassType(getDataTypeManager().getDataTypeClass(runtimeType));
                    if (paramRecord.isVarArg()) {
                    	spParam.setVarArg(true);
                    	spParam.setClassType(getDataTypeManager().getDataType(spParam.getClassType()).getTypeArrayClass());
                    }
                    procInfo.addParameter(spParam);
                }

                // if the procedure returns a resultSet, obtain resultSet metadata
                if(procRecord.getResultSet() != null) {
                    ColumnSet<Procedure> resultRecord = procRecord.getResultSet();
                    // resultSet is the last parameter in the procedure
                    int lastParamIndex = procInfo.getParameters().size() + 1;
                    SPParameter param = new SPParameter(getTeiidParser(), lastParamIndex, SPParameter.RESULT_SET, resultRecord.getFullName());
                    param.setClassType(java.sql.ResultSet.class);
                    param.setMetadataID(resultRecord);

                    for (Column columnRecord : resultRecord.getColumns()) {
                        String colType = columnRecord.getRuntimeType();
                        param.addResultSetColumn(columnRecord.getFullName(), getDataTypeManager().getDataTypeClass(colType), columnRecord);
                    }

                    procInfo.addParameter(param);            
                }

                // if this is a virtual procedure get the procedure plan
                if(procRecord.isVirtual()) {
                    QueryNode queryNode = new LiveQueryNode(procRecord);
                    procInfo.setQueryPlan(queryNode);
                }
                
                //subtract 1, to match up with the server
                procInfo.setUpdateCount(procRecord.getUpdateCount() -1);
				results.add(procInfo);
			}
        	this.procedureCache.put(canonicalName, results);        	
        }
        
        StoredProcedureInfo result = null;
        
        for (StoredProcedureInfo storedProcedureInfo : results) {
        	Schema schema = (Schema)storedProcedureInfo.getModelID();
	        if(name.equalsIgnoreCase(storedProcedureInfo.getProcedureCallableName()) || vdbMetaData == null || vdbMetaData.isVisible(schema.getName())){
	        	if (result != null) {
	    			 throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30358, name));
	    		}
	        	result = storedProcedureInfo;
	        }
		}
		return result;
	}
    
    /**
     * Method to convert the parameter type returned from a ProcedureParameterRecord
     * to the parameter type expected by StoredProcedureInfo
     * @param parameterType
     * @return
     */
    private int convertParamRecordTypeToStoredProcedureType(final ProcedureParameter.Type parameterType) {
        switch (parameterType) {
            case In : return SPParameter.IN;
            case Out : return SPParameter.OUT;
            case InOut : return SPParameter.INOUT;
            case ReturnValue : return SPParameter.RETURN_VALUE;
            default : 
                return -1;
        }
    }

    public String getElementType(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getRuntimeType();            
        } else if(elementID instanceof ProcedureParameter){
            return ((ProcedureParameter) elementID).getRuntimeType();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    public String getDefaultValue(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getDefaultValue();            
        } else if(elementID instanceof ProcedureParameter){
            return ((ProcedureParameter) elementID).getDefaultValue();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    public Object getMinimumValue(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getMinimumValue();            
        } else if(elementID instanceof ProcedureParameter){
            return null;
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    public Object getMaximumValue(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getMaximumValue();            
        } else if(elementID instanceof ProcedureParameter){
            return null;
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    public boolean isVirtualGroup(final Object groupID) throws Exception {
    	if (groupID instanceof Table) {
    		return ((Table) groupID).isVirtual();
    	}
    	if (groupID instanceof Procedure) {
    		return ((Procedure) groupID).isVirtual();
    	}
    	throw createInvalidRecordTypeException(groupID);
    }

    public boolean isProcedure(final Object groupID) throws Exception {
    	if(groupID instanceof Procedure) {
            return true;            
        } 
    	if(groupID instanceof Table){
            return false;
        } 
    	throw createInvalidRecordTypeException(groupID);
    }

    public boolean isVirtualModel(final Object modelID) throws Exception {
        Schema modelRecord = (Schema) modelID;
        return !modelRecord.isPhysical();
    }

    public QueryNode getVirtualPlan(final Object groupID) throws Exception {
        Table tableRecord = (Table) groupID;
        if (!tableRecord.isVirtual()) {
             throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30359, tableRecord.getFullName(), "Query")); //$NON-NLS-1$
        }
        LiveTableQueryNode queryNode = new LiveTableQueryNode(tableRecord);

        // get any bindings and add them onto the query node
        List<String> bindings = tableRecord.getBindings();
        if(bindings != null) {
            for(Iterator<String> bindIter = bindings.iterator();bindIter.hasNext();) {
                queryNode.addBinding(bindIter.next());
            }
        }

        return queryNode;
    }

    public String getInsertPlan(final Object groupID) throws Exception {
        Table tableRecordImpl = (Table)groupID;
        if (!tableRecordImpl.isVirtual()) {
             throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30359, tableRecordImpl.getFullName(), "Insert")); //$NON-NLS-1$
        }
        return tableRecordImpl.isInsertPlanEnabled()?tableRecordImpl.getInsertPlan():null;
    }

    public String getUpdatePlan(final Object groupID) throws Exception {
        Table tableRecordImpl = (Table)groupID;
        if (!tableRecordImpl.isVirtual()) {
        	throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30359, tableRecordImpl.getFullName(), "Update")); //$NON-NLS-1$
        }
        return tableRecordImpl.isUpdatePlanEnabled()?tableRecordImpl.getUpdatePlan():null;
    }

    public String getDeletePlan(final Object groupID) throws Exception {
        Table tableRecordImpl = (Table)groupID;
        if (!tableRecordImpl.isVirtual()) {
        	throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30359, tableRecordImpl.getFullName(), "Delete")); //$NON-NLS-1$
        }
        return tableRecordImpl.isDeletePlanEnabled()?tableRecordImpl.getDeletePlan():null;
    }

    public boolean modelSupports(final Object modelID, final int modelConstant)
        throws Exception {
        switch(modelConstant) {
            default:
                throw new UnsupportedOperationException(Messages.getString(Messages.TransformationMetadata.unknownSupportConstant12) + modelConstant);
        }        
    }

    public boolean groupSupports(final Object groupID, final int groupConstant)
        throws Exception {
        Table tableRecord = (Table) groupID;

        switch(groupConstant) {
            case SupportConstants.Group.UPDATE:
                return tableRecord.supportsUpdate();
            default:
                throw new UnsupportedOperationException(Messages.getString(Messages.TransformationMetadata.unknownSupportConstant12) + groupConstant);
        }
    }

    public boolean elementSupports(final Object elementID, final int elementConstant)
        throws Exception {
        
        if(elementID instanceof Column) {
            Column columnRecord = (Column) elementID;            
            switch(elementConstant) {
                case SupportConstants.Element.NULL:
                    return columnRecord.getNullType() == NullType.Nullable;
                case SupportConstants.Element.NULL_UNKNOWN:
                    return columnRecord.getNullType() == NullType.Unknown;
                case SupportConstants.Element.SEARCHABLE_COMPARE:
                    return (columnRecord.getSearchType() == SearchType.Searchable || columnRecord.getSearchType() == SearchType.All_Except_Like);
                case SupportConstants.Element.SEARCHABLE_LIKE:
                	return (columnRecord.getSearchType() == SearchType.Searchable || columnRecord.getSearchType() == SearchType.Like_Only);
                case SupportConstants.Element.SEARCHABLE_EQUALITY:
                    if (getTeiidVersion().isLessThan(Version.TEIID_8_12_4))
                        return false;

                    return (columnRecord.getSearchType() == SearchType.Equality_Only || columnRecord.getSearchType() == SearchType.Searchable || columnRecord.getSearchType() == SearchType.All_Except_Like);
                case SupportConstants.Element.SELECT:
                    return columnRecord.isSelectable();
                case SupportConstants.Element.UPDATE:
                    return columnRecord.isUpdatable();
                case SupportConstants.Element.DEFAULT_VALUE:
                    Object defaultValue = columnRecord.getDefaultValue();
                    if(defaultValue == null) {
                        return false;
                    }
                    return true;
                case SupportConstants.Element.AUTO_INCREMENT:
                    return columnRecord.isAutoIncremented();
                case SupportConstants.Element.CASE_SENSITIVE:
                    return columnRecord.isCaseSensitive();
                case SupportConstants.Element.SIGNED:
                    return columnRecord.isSigned();
                default:
                    throw new UnsupportedOperationException(Messages.getString(Messages.TransformationMetadata.unknownSupportConstant12) + elementConstant);
            }
        } else if(elementID instanceof ProcedureParameter) {
            ProcedureParameter columnRecord = (ProcedureParameter) elementID;            
            switch(elementConstant) {
                case SupportConstants.Element.NULL:
                	return columnRecord.getNullType() == NullType.Nullable;
                case SupportConstants.Element.NULL_UNKNOWN:
                	return columnRecord.getNullType() == NullType.Unknown;
                case SupportConstants.Element.SEARCHABLE_COMPARE:
                case SupportConstants.Element.SEARCHABLE_LIKE:
                    return false;
                case SupportConstants.Element.SELECT:
                    return columnRecord.getType() != Type.In;
                case SupportConstants.Element.UPDATE:
                    return false;
                case SupportConstants.Element.DEFAULT_VALUE:
                    Object defaultValue = columnRecord.getDefaultValue();
                    if(defaultValue == null) {
                        return false;
                    }
                    return true;
                case SupportConstants.Element.AUTO_INCREMENT:
                    return false;
                case SupportConstants.Element.CASE_SENSITIVE:
                    return false;
                case SupportConstants.Element.SIGNED:
                    return true;
                default:
                    throw new UnsupportedOperationException(Messages.getString(Messages.TransformationMetadata.unknownSupportConstant12) + elementConstant); 
            }
            
        } else {            
            throw createInvalidRecordTypeException(elementID);
        }
    }
    
    private IllegalArgumentException createInvalidRecordTypeException(Object elementID) {
        return new IllegalArgumentException(Messages.getString(Messages.TransformationMetadata.invalidType, elementID!=null?elementID.getClass().getName():null));
    }

    public int getMaxSetSize(final Object modelID) throws Exception {
        return 0;
    }

    public Collection<KeyRecord> getIndexesInGroup(final Object groupID) throws Exception {
        return ((Table)groupID).getIndexes();
    }

    public Collection<KeyRecord> getUniqueKeysInGroup(final Object groupID)
        throws Exception {
    	Table tableRecordImpl = (Table)groupID;
    	ArrayList<KeyRecord> result = new ArrayList<KeyRecord>(tableRecordImpl.getUniqueKeys());
    	if (tableRecordImpl.getPrimaryKey() != null) {
	    	result.add(tableRecordImpl.getPrimaryKey());
    	}
    	for (KeyRecord key : tableRecordImpl.getIndexes()) {
			if (key.getType() == KeyRecord.Type.Unique) {
				result.add(key);
			}
		}
    	return result;
    }

    public Collection<ForeignKey> getForeignKeysInGroup(final Object groupID)
        throws Exception {
    	return ((Table)groupID).getForeignKeys();
    }

    public Object getPrimaryKeyIDForForeignKeyID(final Object foreignKeyID)
        throws Exception {
        ForeignKey fkRecord = (ForeignKey) foreignKeyID;
        return fkRecord.getPrimaryKey();
    }

    public Collection<KeyRecord> getAccessPatternsInGroup(final Object groupID)
        throws Exception {
    	return ((Table)groupID).getAccessPatterns();
    }

    public List<Column> getElementIDsInIndex(final Object index) throws Exception {
    	return ((ColumnSet<?>)index).getColumns();
    }

    public List<Column> getElementIDsInKey(final Object key) throws Exception {
        return ((ColumnSet<?>)key).getColumns();
    }

    public List<Column> getElementIDsInAccessPattern(final Object accessPattern)
        throws Exception {
        return ((ColumnSet<?>)accessPattern).getColumns();
    }

    public boolean isXMLGroup(final Object groupID) throws Exception {
        Table tableRecord = (Table) groupID;
        return tableRecord.getTableType() == Table.Type.Document;
    }

    /** 
     * @see org.teiid.query.metadata.QueryMetadataInterface#hasMaterialization(java.lang.Object)
     * @since 4.2
     */
    public boolean hasMaterialization(final Object groupID) throws Exception,
                                                      TeiidClientException {
        Table tableRecord = (Table) groupID;
        return tableRecord.isMaterialized();
    }

    /** 
     * @see org.teiid.query.metadata.QueryMetadataInterface#getMaterialization(java.lang.Object)
     * @since 4.2
     */
    public Object getMaterialization(final Object groupID) throws Exception,
                                                    TeiidClientException {
        Table tableRecord = (Table) groupID;
        if(tableRecord.isMaterialized()) {
	        return tableRecord.getMaterializedTable();
        }
        return null;
    }

    /** 
     * @see org.teiid.query.metadata.QueryMetadataInterface#getMaterializationStage(java.lang.Object)
     * @since 4.2
     */
    public Object getMaterializationStage(final Object groupID) throws Exception,
                                                         TeiidClientException {
        Table tableRecord = (Table) groupID;
        if(tableRecord.isMaterialized()) {
	        return tableRecord.getMaterializedStageTable();
        }
        return null;
    }

    public MappingNode getMappingNode(final Object groupID) throws Exception {
        Table tableRecord = (Table) groupID;
        
        MappingDocument mappingDoc = (MappingDocument) getFromMetadataCache(groupID, "xml-doc"); //$NON-NLS-1$
        
        if (mappingDoc != null) {
        	return mappingDoc;
        }
        
		final String groupName = tableRecord.getFullName();
        if(tableRecord.isVirtual()) {
            // get mapping transform
            String document = tableRecord.getSelectTransformation();            
            InputStream inputStream = new ByteArrayInputStream(document.getBytes());
            MappingLoader reader = new MappingLoader(getTeiidParser());
            try{
                mappingDoc = reader.loadDocument(inputStream);
                mappingDoc.setName(groupName);
            } catch (Exception e){
                 throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID30363, groupName, mappingDoc));
            } finally {
            	try {
					inputStream.close();
            	} catch(Exception e) {}
            }
            addToMetadataCache(groupID, "xml-doc", mappingDoc); //$NON-NLS-1$
            return mappingDoc;
        }

        return null;
    }

    /**
     * @see org.teiid.query.metadata.QueryMetadataInterface#getVirtualDatabaseName()
     */
    public String getVirtualDatabaseName() throws Exception {
    	if (vdbMetaData == null) {
    		return null;
    	}
    	return vdbMetaData.getName();
    }

    public int getVirtualDatabaseVersion() {
    	if (vdbMetaData == null) {
    		return 0;
    	}
    	return vdbMetaData.getVersion();
    }
    
    public VDBMetaData getVdbMetaData() {
		return vdbMetaData;
	}
    
    /**
     * @see org.teiid.query.metadata.QueryMetadataInterface#getXMLTempGroups(java.lang.Object)
     */
    public Collection<Table> getXMLTempGroups(final Object groupID) throws Exception {
        Table tableRecord = (Table) groupID;

        if(tableRecord.getTableType() == Table.Type.Document) {
            return this.store.getXMLTempGroups(tableRecord);
        }
        return Collections.emptySet();
    }

    public float getCardinality(final Object groupID) throws Exception {
        return ((Table) groupID).getCardinalityAsFloat();
    }

    public List<SQLXMLImpl> getXMLSchemas(final Object groupID) throws Exception {
        Table tableRecord = (Table) groupID;

        // lookup transformation record for the group
        String groupName = tableRecord.getFullName();

        // get the schema Paths
        List<String> schemaPaths = tableRecord.getSchemaPaths();
        
        List<SQLXMLImpl> schemas = new LinkedList<SQLXMLImpl>();
        if (schemaPaths == null) {
        	return schemas;
        }
        String path = getParentPath(tableRecord.getResourcePath());
        for (String string : schemaPaths) {
        	String parentPath = path;
        	boolean relative = false;
        	while (string.startsWith("../")) { //$NON-NLS-1$
        		relative = true;
        		string = string.substring(3);
        		parentPath = getParentPath(parentPath);
        	}
        	SQLXMLImpl schema = null;
        	if (!relative) {
        		schema = getVDBResourceAsSQLXML(string);
        	}
        	if (schema == null) {
        		if (!parentPath.endsWith("/")) { //$NON-NLS-1$
        			parentPath += "/"; //$NON-NLS-1$
        		}
        		schema = getVDBResourceAsSQLXML(parentPath + string);
        	}
        	
        	if (schema == null) {
        		 throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30364,groupName));
        	}
        	schemas.add(schema);
        }
        
        return schemas;
    }

	private String getParentPath(String path) {
		if (path == null) {
			return ""; //$NON-NLS-1$
		}
		int index = path.lastIndexOf('/');
        if (index > 0) {
        	path = path.substring(0, index);
        } else {
        	path = ""; //$NON-NLS-1$
        }
		return path;
	}

    public String getNameInSource(final Object metadataID) throws Exception {
        return ((AbstractMetadataRecord) metadataID).getNameInSource();
    }

    public int getElementLength(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getLength();            
        } else if(elementID instanceof ProcedureParameter){
            return ((ProcedureParameter) elementID).getLength();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    public int getPosition(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getPosition();
        } else if(elementID instanceof ProcedureParameter) {
            return ((ProcedureParameter) elementID).getPosition();            
        } else {
            throw createInvalidRecordTypeException(elementID);            
        }
    }
    
    public int getPrecision(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getPrecision();
        } else if(elementID instanceof ProcedureParameter) {
            return ((ProcedureParameter) elementID).getPrecision();            
        } else {
            throw createInvalidRecordTypeException(elementID);            
        }
    }
    
    public int getRadix(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getRadix();
        } else if(elementID instanceof ProcedureParameter) {
            return ((ProcedureParameter) elementID).getRadix();            
        } else {
            throw createInvalidRecordTypeException(elementID);            
        }
    }
    
	public String getFormat(Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getFormat();
        } 
        throw createInvalidRecordTypeException(elementID);            
	}       
    
    public int getScale(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getScale();
        } else if(elementID instanceof ProcedureParameter) {
            return ((ProcedureParameter) elementID).getScale();            
        } else {
            throw createInvalidRecordTypeException(elementID);            
        }
    }

    public float getDistinctValues(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getDistinctValuesAsFloat();
        } else if(elementID instanceof ProcedureParameter) {
            return -1;            
        } else {
            throw createInvalidRecordTypeException(elementID);            
        }
    }

    public float getNullValues(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getNullValuesAsFloat();
        } else if(elementID instanceof ProcedureParameter) {
            return -1;            
        } else {
            throw createInvalidRecordTypeException(elementID);            
        }
    }

    public String getNativeType(final Object elementID) throws Exception {
        if(elementID instanceof Column) {
            return ((Column) elementID).getNativeType();
        } else if(elementID instanceof ProcedureParameter) {
            return null;            
        } else {
            throw createInvalidRecordTypeException(elementID);            
        }
    }

    public Properties getExtensionProperties(final Object metadataID) throws Exception {
        AbstractMetadataRecord metadataRecord = (AbstractMetadataRecord) metadataID;
        Map<String, String> result = metadataRecord.getProperties();
        if (result == null) {
        	return EMPTY_PROPS;
        }
        Properties p = new Properties();
        p.putAll(result);
        return p;
    }
    
    @Override
    public String getExtensionProperty(Object metadataID, String key, boolean checkUnqualified) {
        return ((AbstractMetadataRecord)metadataID).getProperty(key, checkUnqualified);
    }

    /** 
     * @see org.teiid.query.metadata.BasicQueryMetadata#getBinaryVDBResource(java.lang.String)
     * @since 4.3
     */
    public byte[] getBinaryVDBResource(String resourcePath) throws Exception {
    	final VDBResources.Resource f = getFile(resourcePath);
    	if (f == null) {
    		return null;
    	}
		try {
			return ObjectConverterUtil.convertToByteArray(f.openStream());
		} catch (IOException e) {
			 throw new TeiidClientException(e);
		}
    }
    
    public ClobImpl getVDBResourceAsClob(String resourcePath) {
    	final VDBResources.Resource f = getFile(resourcePath);
    	if (f == null) {
    		return null;
    	}
		return new ClobImpl(new VirtualFileInputStreamFactory(f), -1);
    }
    
    public SQLXMLImpl getVDBResourceAsSQLXML(String resourcePath) {
    	final VDBResources.Resource f = getFile(resourcePath);
    	if (f == null) {
    		return null;
    	}
		return new SQLXMLImpl(new VirtualFileInputStreamFactory(f));
    }
    
    public BlobImpl getVDBResourceAsBlob(String resourcePath) {
    	final VDBResources.Resource f = getFile(resourcePath);
    	if (f == null) {
    		return null;
    	}
    	return new BlobImpl(new VirtualFileInputStreamFactory(f));
    }
    
    private VDBResources.Resource getFile(String resourcePath) {
    	if (resourcePath == null) {
    		return null;
    	}
    	return this.vdbEntries.get(resourcePath);
    }

    /** 
     * @see org.teiid.query.metadata.BasicQueryMetadata#getCharacterVDBResource(java.lang.String)
     * @since 4.3
     */
    public String getCharacterVDBResource(String resourcePath) throws Exception {
    	try {
    		byte[] bytes = getBinaryVDBResource(resourcePath);
    		if (bytes == null) {
    			return null;
    		}
			return ObjectConverterUtil.convertToString(new ByteArrayInputStream(bytes));
		} catch (IOException e) {
			 throw new TeiidClientException(e);
		}
    }
    
    public CompositeMetadataStore getMetadataStore() {
    	return this.store;
    }

    /** 
     * @see org.teiid.query.metadata.BasicQueryMetadata#getVDBResourcePaths()
     * @since 4.3
     */
    public String[] getVDBResourcePaths() throws Exception {
    	LinkedList<String> paths = new LinkedList<String>();
    	for (Map.Entry<String, VDBResources.Resource> entry : this.vdbEntries.entrySet()) {
			paths.add(entry.getKey());
    	}
    	return paths.toArray(new String[paths.size()]);
    }
    
	@Override
	public Object addToMetadataCache(Object metadataID, String key, Object value) {
        boolean groupInfo = key.startsWith(GroupInfo.CACHE_PREFIX);
        key = getCacheKey(key, (AbstractMetadataRecord)metadataID);
        if (groupInfo) {
        	return this.groupInfoCache.put(key, value); 
        }
    	return this.metadataCache.put(key, value); 
	}

	@Override
	public Object getFromMetadataCache(Object metadataID, String key)
			throws Exception {
        boolean groupInfo = key.startsWith(GroupInfo.CACHE_PREFIX);
        key = getCacheKey(key, (AbstractMetadataRecord)metadataID);
        if (groupInfo) {
        	return this.groupInfoCache.get(key); 
        }
    	return this.metadataCache.get(key);
	}

	private String getCacheKey(String key, AbstractMetadataRecord record) {
		return record.getUUID() + "/" + key; //$NON-NLS-1$
	}

	@Override
	public FunctionLibrary getFunctionLibrary() {
		return this.functionLibrary;
	}
	
	@Override
	public Object getPrimaryKey(Object metadataID) {
		Table table = (Table)metadataID;
		return table.getPrimaryKey();
	}
	
	@Override
	public IQueryMetadataInterface getDesignTimeMetadata() {
		TransformationMetadata tm = new TransformationMetadata(getTeiidParser(), store, functionLibrary);
		tm.groupInfoCache = this.groupInfoCache;
		tm.metadataCache = this.metadataCache;
		tm.partialNameToFullNameCache = this.partialNameToFullNameCache;
		tm.procedureCache = this.procedureCache; 
		tm.scriptEngineManager = this.scriptEngineManager;
		tm.importedModels = this.importedModels;
		tm.allowedLanguages = this.allowedLanguages;

		if (getTeiidVersion().isGreaterThanOrEqualTo(Version.TEIID_8_12_4))
		    tm.widenComparisonToString = this.widenComparisonToString;

		return tm;
	}
	
	@Override
	public Set<String> getImportedModels() {
		return this.importedModels;
	}
	
	@Override
	public ScriptEngine getScriptEngineDirect(String language)
			throws Exception {
		if (this.scriptEngineManager == null) {
			this.scriptEngineManager = new ScriptEngineManager();
		}
		ScriptEngine engine = null;
		if (allowedLanguages == null || allowedLanguages.contains(language)) {
			/*
			 * because of state caching in the engine, we'll return a new instance for each
			 * usage.  we can pool if needed and add a returnEngine method 
			 */
			ScriptEngineFactory sef = this.scriptEngineFactories.get(language);
			if (sef != null) {
				try {
					engine = sef.getScriptEngine();
					engine.setBindings(scriptEngineManager.getBindings(), ScriptContext.ENGINE_SCOPE);
				} catch (Exception e) {
					//just swallow the exception to mimic the jsr behavior
				}
			}
			engine = this.scriptEngineManager.getEngineByName(language);
		}
		if (engine == null) {
			Set<String> names = new LinkedHashSet<String>();
			for (ScriptEngineFactory factory : this.scriptEngineManager.getEngineFactories()) {
				names.addAll(factory.getNames());
			}
			if (allowedLanguages != null) {
				names.retainAll(allowedLanguages);
			}
			names.add(ObjectTable.DEFAULT_LANGUAGE);
			throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID31109, language, names));
		}
		this.scriptEngineFactories.put(language, engine.getFactory());
		return engine;
	}
	
	@Override
	public boolean isVariadic(Object metadataID) {
		if (metadataID instanceof ProcedureParameter) {
			return ((ProcedureParameter)metadataID).isVarArg();
		}
		if (metadataID instanceof FunctionParameter) {
			return ((FunctionParameter)metadataID).isVarArg();
		}
		return false;
	}
	
	@Override
	public Schema getModelID(String modelName) throws Exception {
		Schema s = this.getMetadataStore().getSchema(modelName);
		if (s == null) {
			throw new TeiidClientException(modelName+TransformationMetadata.NOT_EXISTS_MESSAGE);
		}
		return s;
	}
	
	public Map<String, DataPolicyMetadata> getPolicies() {
		return policies;
	}
	
	@Override
	public boolean useOutputName() {
		return useOutputNames;
	}
	
	public void setUseOutputNames(boolean useOutputNames) {
		this.useOutputNames = useOutputNames;
	}

	@Override
    public boolean widenComparisonToString() {
        return widenComparisonToString;
    }
    
    public void setWidenComparisonToString(boolean widenComparisonToString) {
        if (getTeiidVersion().isLessThan(Version.TEIID_8_12_4))
            return;

        this.widenComparisonToString = widenComparisonToString;
    }
}