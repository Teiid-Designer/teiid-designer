/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.transformation.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.script.ScriptEngine;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.LRUCache;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.metadata.runtime.ColumnRecord;
import org.teiid.designer.metadata.runtime.ColumnSetRecord.ColumnSetRecordProperties;
import org.teiid.designer.metadata.runtime.ForeignKeyRecord.ForeignKeyRecordProperties;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metadata.runtime.MetadataRecord.MetadataRecordProperties;
import org.teiid.designer.metadata.runtime.ProcedureRecord.ProcedureRecordProperties;
import org.teiid.designer.metadata.runtime.TableRecord;
import org.teiid.designer.metadata.runtime.TableRecord.TableRecordProperties;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.xml.IMappingNode;

/**
 * Modelers implementation of QueryMetadataInterface that reads columns, groups, modeles etc. index files for various metadata
 * properties. TransformationMetadataFacade should only be used when the metadata is read only. It is used in the modeler with in
 * the context of validating a Query(when the metadata is read only).
 *
 * @since 8.0
 */
public class TransformationMetadataFacade implements IQueryMetadataInterface {

    /**
     * Default amount of space in the cache
     */
    public static final int DEFAULT_SPACELIMIT = 4000;

    /**
     * Partial name cache is useful when the user executes same kind of partial name queries many times, number of such queries
     * will not be many so limiting this cachesize to 100.
     */
    public static final int DEFAULT_SPACELIMIT_PARTIAL_NAME_CACHE = 100;
    
    private final TransformationMetadata metadata;
    private final Map<String, Object> nameToIdCache;
    private final Map<Object, MetadataRecord> idToRecordCache;
    private final Map<String, String> partialNameToFullNameCache;

    /**
     * @param delegate
     */
    public TransformationMetadataFacade( final TransformationMetadata delegate ) {
        this(delegate, DEFAULT_SPACELIMIT);
    }

    /**
     * @param delegate
     * @param cacheSize
     */
    public TransformationMetadataFacade( final TransformationMetadata delegate,
                                         int cacheSize ) {
        CoreArgCheck.isNotNull(delegate);
        this.metadata = delegate;
        this.nameToIdCache = Collections.synchronizedMap(new LRUCache<String, Object>(cacheSize));
        this.idToRecordCache = Collections.synchronizedMap(new LRUCache<Object, MetadataRecord>(cacheSize));
        this.partialNameToFullNameCache = Collections.synchronizedMap(new LRUCache<String, String>(
                                                                                                   DEFAULT_SPACELIMIT_PARTIAL_NAME_CACHE));
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    @Override
    public ITeiidServerVersion getTeiidVersion() {
        return ModelerCore.getTeiidServerVersion();
    }

    @Override
	public Object getElementID( final String elementName ) throws Exception {
        // Check the cache first ...
        MetadataRecord record = getRecordByName(elementName, IndexConstants.RECORD_TYPE.COLUMN);

        // If not found in the cache then retrieve it from the index
        if (record == null) {
            record = (MetadataRecord)this.metadata.getElementID(elementName);
            // Update the cache ...
            if (record != null) {
                updateNameToIdCache(elementName, IndexConstants.RECORD_TYPE.COLUMN, record.getUUID());
                updateIdToRecordCache(record.getUUID(), record);
            }
        }
        return record;
    }

    @Override
	public Object getGroupID( final String groupName ) throws Exception {
        // Check the cache first ...
        MetadataRecord record = getRecordByName(groupName, IndexConstants.RECORD_TYPE.TABLE);

        // If not found in the cache then Sretrieve it from the index
        if (record == null) {
            record = (MetadataRecord)this.metadata.getGroupID(groupName);
            // Update the cache ...
            if (record != null) {
                updateNameToIdCache(groupName, IndexConstants.RECORD_TYPE.TABLE, record.getUUID());
                updateIdToRecordCache(record.getUUID(), record);
            }
        }
        return record;
    }

    @Override
	public Collection getGroupsForPartialName( final String partialGroupName )
        throws Exception {
        // Check the cache first ...
        String fullName = getFullNameByPartialName(partialGroupName, IndexConstants.RECORD_TYPE.TABLE);

        // If not found in the cache then retrieve it from the index
        if (fullName == null) {
            synchronized (partialNameToFullNameCache) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                fullName = getFullNameByPartialName(partialGroupName, IndexConstants.RECORD_TYPE.TABLE);
                if (fullName == null) {
                    // search for the records that match the partial name
                    Collection partialNameRecords = this.metadata.getGroupsForPartialName(partialGroupName);
                    // Update the cache only if there is one matching record...otherwise its a failure case (ambiguous)
                    if (partialNameRecords != null && partialNameRecords.size() == 1) {
                        updatePartialNameToFullName(partialGroupName,
                                                    (String)partialNameRecords.iterator().next(),
                                                    IndexConstants.RECORD_TYPE.TABLE);
                    }
                    return partialNameRecords;
                }
            }
        }
        Collection partialNameRecords = new ArrayList(1);
        partialNameRecords.add(fullName);
        return partialNameRecords;
    }

    @Override
	public Object getModelID( final Object groupOrElementID ) throws Exception {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, groupOrElementID);

        MetadataRecord record = (MetadataRecord)groupOrElementID;

        Object modelRecord = record.getPropertyValue(MetadataRecordProperties.MODEL_FOR_RECORD);
        if (modelRecord == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                modelRecord = record.getPropertyValue(MetadataRecordProperties.MODEL_FOR_RECORD);
                if (modelRecord == null) {
                    modelRecord = this.metadata.getModelID(groupOrElementID);
                    record.setPropertyValue(MetadataRecordProperties.MODEL_FOR_RECORD, modelRecord);
                }
            }
        }

        return modelRecord;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.metadata.QueryMetadataInterface#getElementIDsInGroupID(java.lang.Object)
     */
    @Override
	public List getElementIDsInGroupID( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        MetadataRecord record = (MetadataRecord)groupID;

        List elementIDs = (List)record.getPropertyValue(TableRecordProperties.ELEMENTS_IN_GROUP);
        if (elementIDs == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                elementIDs = (List)record.getPropertyValue(TableRecordProperties.ELEMENTS_IN_GROUP);
                if (elementIDs == null) {
                    elementIDs = this.metadata.getElementIDsInGroupID(groupID);
                    if (elementIDs != null) {
                        record.setPropertyValue(TableRecordProperties.ELEMENTS_IN_GROUP, elementIDs);
                        Iterator elemntIter = elementIDs.iterator();
                        while (elemntIter.hasNext()) {
                            MetadataRecord columnRecord = (MetadataRecord)elemntIter.next();
                            // Update the cache ...
                            if (columnRecord != null) {
                                updateNameToIdCache(columnRecord.getFullName(),
                                                    columnRecord.getRecordType(),
                                                    columnRecord.getUUID());
                                updateIdToRecordCache(columnRecord.getUUID(), columnRecord);
                            }
                        }
                    }
                }
            }
        }

        return elementIDs;
    }

    @Override
	public Object getGroupIDForElementID( final Object elementID ) throws Exception {
        CoreArgCheck.isInstanceOf(ColumnRecord.class, elementID);
        ColumnRecord columnRecord = (ColumnRecord)elementID;

        String tableUUID = columnRecord.getParentUUID();

        // Check the cache first ...
        MetadataRecord record = getRecordByID(tableUUID);

        // If not found in the cache then retrieve it from the index
        if (record == null) {
            record = (MetadataRecord)this.metadata.getGroupID(tableUUID);
            // Update the cache ...
            if (record != null) {
                updateNameToIdCache(record.getFullName(), record.getRecordType(), record.getUUID());
                updateIdToRecordCache(record.getUUID(), record);
            }
        }
        return record;
    }

    @Override
	public IStoredProcedureInfo getStoredProcedureInfoForProcedure( final String fullyQualifiedProcedureName )
        throws Exception {

        IStoredProcedureInfo procInfo = null;

        // Check the cache first ...
        MetadataRecord record = getRecordByName(fullyQualifiedProcedureName, IndexConstants.RECORD_TYPE.CALLABLE);

        // If not found in the cache then retrieve it from the index
        if (record == null) {
            // lookup the indexes for the record
            procInfo = this.metadata.getStoredProcedureInfoForProcedure(fullyQualifiedProcedureName);
            if (procInfo != null) {
                // a record should always be found on the procInfo
                record = (MetadataRecord)procInfo.getProcedureID();
                // update the cache on the record with the procIndo object
                record.setPropertyValue(ProcedureRecordProperties.STORED_PROC_INFO_FOR_RECORD, procInfo);
                // Update the cache ... with procedure info
                updateNameToIdCache(fullyQualifiedProcedureName, IndexConstants.RECORD_TYPE.CALLABLE, record.getUUID());
                updateIdToRecordCache(record.getUUID(), record);
            }
        }

        // found record
        if (procInfo == null && record != null) {
            // if the record is found it should have been update with the procInfo object
            procInfo = (IStoredProcedureInfo)record.getPropertyValue(ProcedureRecordProperties.STORED_PROC_INFO_FOR_RECORD);
            // this should never occur but if procInfo cannot be found on the record
            if (procInfo == null) {
                procInfo = this.metadata.getStoredProcedureInfoForProcedure(fullyQualifiedProcedureName);
                record.setPropertyValue(ProcedureRecordProperties.STORED_PROC_INFO_FOR_RECORD, procInfo);
            }
        }

        return procInfo;
    }

    @Override
	public IQueryNode getVirtualPlan( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;

        IQueryNode queryPlan = (IQueryNode) tableRecord.getPropertyValue(TableRecordProperties.QUERY_PLAN);
        if (queryPlan == null) {
            synchronized (tableRecord) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                queryPlan = (IQueryNode) tableRecord.getPropertyValue(TableRecordProperties.QUERY_PLAN);
                if (queryPlan == null) {
                    queryPlan = this.metadata.getVirtualPlan(groupID);
                    tableRecord.setPropertyValue(TableRecordProperties.QUERY_PLAN, queryPlan);
                }
            }
        }

        return queryPlan;
    }

    @Override
	public String getInsertPlan( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;
        String insertPlan = (String)tableRecord.getPropertyValue(TableRecordProperties.INSERT_PLAN);
        if (insertPlan == null) {
            // look up the cache again, might have been updated by
            // the thread that just released the lock
            synchronized (tableRecord) {
                insertPlan = (String)tableRecord.getPropertyValue(TableRecordProperties.INSERT_PLAN);
                if (insertPlan == null) {
                    insertPlan = this.metadata.getInsertPlan(groupID);
                    tableRecord.setPropertyValue(TableRecordProperties.INSERT_PLAN, insertPlan);
                }
            }
        }

        return insertPlan;
    }

    @Override
	public String getUpdatePlan( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;
        String updatePlan = (String)tableRecord.getPropertyValue(TableRecordProperties.UPDATE_PLAN);
        if (updatePlan == null) {
            synchronized (tableRecord) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                updatePlan = (String)tableRecord.getPropertyValue(TableRecordProperties.UPDATE_PLAN);
                if (updatePlan == null) {
                    updatePlan = this.metadata.getUpdatePlan(groupID);
                    tableRecord.setPropertyValue(TableRecordProperties.UPDATE_PLAN, updatePlan);
                }
            }
        }

        return updatePlan;
    }

    @Override
	public String getDeletePlan( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;
        String deletePlan = (String)tableRecord.getPropertyValue(TableRecordProperties.DELETE_PLAN);
        if (deletePlan == null) {
            synchronized (tableRecord) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                deletePlan = (String)tableRecord.getPropertyValue(TableRecordProperties.DELETE_PLAN);
                if (deletePlan == null) {
                    deletePlan = this.metadata.getDeletePlan(groupID);
                    tableRecord.setPropertyValue(TableRecordProperties.DELETE_PLAN, deletePlan);
                }
            }
        }

        return deletePlan;

    }

    @Override
	public Collection getIndexesInGroup( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        MetadataRecord record = (MetadataRecord)groupID;
        Collection indexes = (Collection)record.getPropertyValue(TableRecordProperties.INDEXES_IN_GROUP);
        if (indexes == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                indexes = (Collection)record.getPropertyValue(TableRecordProperties.INDEXES_IN_GROUP);
                if (indexes == null) {
                    indexes = this.metadata.getIndexesInGroup(groupID);
                    record.setPropertyValue(TableRecordProperties.INDEXES_IN_GROUP, indexes);
                }
            }
        }

        return indexes;
    }

    @Override
	public Collection getUniqueKeysInGroup( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        MetadataRecord record = (MetadataRecord)groupID;
        Collection uks = (Collection)record.getPropertyValue(TableRecordProperties.UNIQUEKEYS_IN_GROUP);
        if (uks == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                uks = (Collection)record.getPropertyValue(TableRecordProperties.UNIQUEKEYS_IN_GROUP);
                if (uks == null) {
                    uks = this.metadata.getUniqueKeysInGroup(groupID);
                    record.setPropertyValue(TableRecordProperties.UNIQUEKEYS_IN_GROUP, uks);
                }
            }
        }

        return uks;
    }

    @Override
	public Collection getForeignKeysInGroup( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        MetadataRecord record = (MetadataRecord)groupID;
        Collection fks = (Collection)record.getPropertyValue(TableRecordProperties.FOREIGNKEYS_IN_GROUP);
        if (fks == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                fks = (Collection)record.getPropertyValue(TableRecordProperties.FOREIGNKEYS_IN_GROUP);
                if (fks == null) {
                    fks = this.metadata.getForeignKeysInGroup(groupID);
                    record.setPropertyValue(TableRecordProperties.FOREIGNKEYS_IN_GROUP, fks);
                }
            }
        }

        return fks;
    }

    @Override
	public Object getPrimaryKeyIDForForeignKeyID( final Object foreignKeyID )
        throws Exception {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, foreignKeyID);

        MetadataRecord keyRecord = (MetadataRecord)foreignKeyID;
        Object primaryKey = keyRecord.getPropertyValue(ForeignKeyRecordProperties.PRIMARY_KEY_FOR_FK);
        if (primaryKey == null) {
            synchronized (keyRecord) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                primaryKey = keyRecord.getPropertyValue(ForeignKeyRecordProperties.PRIMARY_KEY_FOR_FK);
                if (primaryKey == null) {
                    primaryKey = this.metadata.getPrimaryKeyIDForForeignKeyID(foreignKeyID);
                    keyRecord.setPropertyValue(ForeignKeyRecordProperties.PRIMARY_KEY_FOR_FK, primaryKey);
                }
            }
        }

        return primaryKey;
    }

    @Override
    public Collection getAccessPatternsInGroup( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, groupID);

        MetadataRecord record = (MetadataRecord)groupID;
        Collection accPatterns = (Collection)record.getPropertyValue(TableRecordProperties.ACCESS_PTTRNS_IN_GROUP);
        if (accPatterns == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                accPatterns = (Collection)record.getPropertyValue(TableRecordProperties.ACCESS_PTTRNS_IN_GROUP);
                if (accPatterns == null) {
                    accPatterns = this.metadata.getAccessPatternsInGroup(groupID);
                    record.setPropertyValue(TableRecordProperties.ACCESS_PTTRNS_IN_GROUP, accPatterns);
                }
            }
        }

        return accPatterns;
    }

    @Override
    public List getElementIDsInIndex( final Object index ) throws Exception {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, index);

        MetadataRecord record = (MetadataRecord)index;

        List elementIDs = (List)record.getPropertyValue(ColumnSetRecordProperties.ELEMENTS_IN_INDEX);
        if (elementIDs == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                elementIDs = (List)record.getPropertyValue(ColumnSetRecordProperties.ELEMENTS_IN_INDEX);
                if (elementIDs == null) {
                    elementIDs = this.metadata.getElementIDsInIndex(index);
                    if (elementIDs != null) {
                        record.setPropertyValue(ColumnSetRecordProperties.ELEMENTS_IN_INDEX, elementIDs);
                        for (Iterator elemntIter = elementIDs.iterator(); elemntIter.hasNext();) {
                            MetadataRecord columnRecord = (MetadataRecord)elemntIter.next();
                            // Update the cache ...
                            if (columnRecord != null) {
                                updateNameToIdCache(columnRecord.getFullName(),
                                                    columnRecord.getRecordType(),
                                                    columnRecord.getUUID());
                                updateIdToRecordCache(columnRecord.getUUID(), columnRecord);
                            }
                        }
                    }
                }
            }
        }

        return elementIDs;
    }

    @Override
    public List getElementIDsInKey( final Object key ) throws Exception {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, key);

        MetadataRecord record = (MetadataRecord)key;

        List elementIDs = (List)record.getPropertyValue(ColumnSetRecordProperties.ELEMENTS_IN_KEY);
        if (elementIDs == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                elementIDs = (List)record.getPropertyValue(ColumnSetRecordProperties.ELEMENTS_IN_KEY);
                if (elementIDs == null) {
                    elementIDs = this.metadata.getElementIDsInKey(key);
                    if (elementIDs != null) {
                        record.setPropertyValue(ColumnSetRecordProperties.ELEMENTS_IN_KEY, elementIDs);
                        for (Iterator elemntIter = elementIDs.iterator(); elemntIter.hasNext();) {
                            MetadataRecord columnRecord = (MetadataRecord)elemntIter.next();
                            // Update the cache ...
                            if (columnRecord != null) {
                                updateNameToIdCache(columnRecord.getFullName(),
                                                    columnRecord.getRecordType(),
                                                    columnRecord.getUUID());
                                updateIdToRecordCache(columnRecord.getUUID(), columnRecord);
                            }
                        }
                    }
                }
            }
        }

        return elementIDs;
    }

    @Override
    public List getElementIDsInAccessPattern( final Object accessPattern ) throws Exception {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, accessPattern);

        MetadataRecord record = (MetadataRecord)accessPattern;

        List elementIDs = (List)record.getPropertyValue(ColumnSetRecordProperties.ELEMENTS_IN_ACCESS_PTTRN);
        if (elementIDs == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                elementIDs = (List)record.getPropertyValue(ColumnSetRecordProperties.ELEMENTS_IN_ACCESS_PTTRN);
                if (elementIDs == null) {
                    elementIDs = this.metadata.getElementIDsInAccessPattern(accessPattern);
                    if (elementIDs != null) {
                        record.setPropertyValue(ColumnSetRecordProperties.ELEMENTS_IN_ACCESS_PTTRN, elementIDs);
                        for (Iterator elemntIter = elementIDs.iterator(); elemntIter.hasNext();) {
                            MetadataRecord columnRecord = (MetadataRecord)elemntIter.next();
                            // Update the cache ...
                            if (columnRecord != null) {
                                updateNameToIdCache(columnRecord.getFullName(),
                                                    columnRecord.getRecordType(),
                                                    columnRecord.getUUID());
                                updateIdToRecordCache(columnRecord.getUUID(), columnRecord);
                            }
                        }
                    }
                }
            }
        }

        return elementIDs;
    }

    @Override
    public IMappingNode getMappingNode( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        MetadataRecord record = (MetadataRecord)groupID;

        IMappingNode mappingNode = (IMappingNode)record.getPropertyValue(TableRecordProperties.MAPPING_NODE_FOR_RECORD);
        if (mappingNode == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                mappingNode = (IMappingNode)record.getPropertyValue(TableRecordProperties.MAPPING_NODE_FOR_RECORD);
                if (mappingNode == null) {
                    mappingNode = this.metadata.getMappingNode(groupID);
                    record.setPropertyValue(TableRecordProperties.MAPPING_NODE_FOR_RECORD, mappingNode);
                }
            }
        }

        return mappingNode.clone();
    }

    @Override
    public Collection<Object> getXMLTempGroups( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        MetadataRecord record = (MetadataRecord)groupID;

        Collection tempGroups = (Collection)record.getPropertyValue(TableRecordProperties.TEMPORARY_GROUPS_FOR_DOCUMENT);
        if (tempGroups == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                tempGroups = (Collection)record.getPropertyValue(TableRecordProperties.TEMPORARY_GROUPS_FOR_DOCUMENT);
                if (tempGroups == null) {
                    tempGroups = this.metadata.getXMLTempGroups(groupID);
                    record.setPropertyValue(TableRecordProperties.TEMPORARY_GROUPS_FOR_DOCUMENT, tempGroups);
                }
            }
        }

        return tempGroups;
    }

    @Override
    public List<Object> getXMLSchemas( final Object groupID ) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        MetadataRecord record = (MetadataRecord)groupID;

        List schemas = (List)record.getPropertyValue(TableRecordProperties.SCHEMAS_FOR_DOCUMENT);
        if (schemas == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                schemas = (List)record.getPropertyValue(TableRecordProperties.SCHEMAS_FOR_DOCUMENT);
                if (schemas == null) {
                    schemas = this.metadata.getXMLSchemas(groupID);
                    record.setPropertyValue(TableRecordProperties.SCHEMAS_FOR_DOCUMENT, schemas);
                }
            }
        }

        return schemas;
    }

    @Override
    public Properties getExtensionProperties( final Object metadataID ) throws Exception {

        CoreArgCheck.isInstanceOf(MetadataRecord.class, metadataID);

        MetadataRecord record = (MetadataRecord)metadataID;

        Properties extentions = (Properties)record.getPropertyValue(MetadataRecordProperties.EXTENSIONS_FOR_RECORD);
        if (extentions == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                extentions = (Properties)record.getPropertyValue(MetadataRecordProperties.EXTENSIONS_FOR_RECORD);
                if (extentions == null) {
                    extentions = this.metadata.getExtensionProperties(metadataID);
                    record.setPropertyValue(MetadataRecordProperties.EXTENSIONS_FOR_RECORD, extentions);
                }
            }
        }

        return extentions;
    }




    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * Return the IndexSelector reference
     * 
     * @return delegate
     */
    public TransformationMetadata getDelegate() {
        return this.metadata;
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    private MetadataRecord getRecordByName( final String fullname,
                                            final char recordType ) {
        CoreArgCheck.isNotZeroLength(fullname);

        // Check the cache for the identifier corresponding to this name ...
        Object id = this.nameToIdCache.get(getLookupKey(fullname, recordType));

        // If the identifier was found then check the cache for the record object for this identifier ...
        if (id != null) {
            return getRecordByID(id);
        }
        return null;
    }

    private String getFullNameByPartialName( final String partialName,
                                             final char recordType ) {
        CoreArgCheck.isNotZeroLength(partialName);

        // Check the cache for the identifier corresponding to this partialname ...
        return this.partialNameToFullNameCache.get(getLookupKey(partialName, recordType));
    }

    private MetadataRecord getRecordByID( final Object id ) {
        CoreArgCheck.isNotNull(id);
        return this.idToRecordCache.get(id);
    }

    private void updateNameToIdCache( final String fullName,
                                      final char recordType,
                                      final Object id ) {
        if (!CoreStringUtil.isEmpty(fullName) && id != null) {
            this.nameToIdCache.put(getLookupKey(fullName, recordType), id);
        }
    }

    private void updateIdToRecordCache( final Object id,
                                        final MetadataRecord record ) {
        if (id != null && record != null) {
            this.idToRecordCache.put(id, record);
        }
    }

    private void updatePartialNameToFullName( final String partialName,
                                              final String fullName,
                                              final char recordType ) {
        if (!CoreStringUtil.isEmpty(partialName) && !CoreStringUtil.isEmpty(fullName)) {
            this.partialNameToFullNameCache.put(getLookupKey(partialName, recordType), fullName);
        }
    }

    private String getLookupKey( final String name,
                                 final char recordType ) {
        return name.toUpperCase() + recordType;
    }

    @Override
    public String getFullName(Object metadataID) throws Exception {
        return null;
    }

    @Override
    public String getName(Object metadataID) throws Exception {
        return null;
    }

    @Override
    public String getElementType(Object elementID) throws Exception {
        return null;
    }

    @Override
    public Object getDefaultValue(Object elementID) throws Exception {
        return null;
    }

    @Override
    public Object getMinimumValue(Object elementID) throws Exception {
        return null;
    }

    @Override
    public Object getMaximumValue(Object elementID) throws Exception {
        return null;
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
    public int getScale(Object elementID) throws Exception {
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
    public float getDistinctValues(Object elementID) throws Exception {
        return 0;
    }

    @Override
    public float getNullValues(Object elementID) throws Exception {
        return 0;
    }

    @Override
    public boolean isVirtualGroup(Object groupID) throws Exception {
        return false;
    }

    @Override
    public boolean isVirtualModel(Object modelID) throws Exception {
        return false;
    }

    @Override
    public boolean modelSupports(Object modelID,
                                 int modelConstant) throws Exception {
        return false;
    }

    @Override
    public boolean groupSupports(Object groupID,
                                 int groupConstant) throws Exception {
        return false;
    }

    @Override
    public boolean elementSupports(Object elementID,
                                   int elementConstant) throws Exception {
        return false;
    }

    @Override
    public int getMaxSetSize(Object modelID) throws Exception {
        return 0;
    }

    @Override
    public boolean isXMLGroup(Object groupID) throws Exception {
        return false;
    }

    @Override
    public String getVirtualDatabaseName() throws Exception {
        return null;
    }

    @Override
    public float getCardinality(Object groupID) throws Exception {
        return 0;
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
    public boolean hasMaterialization(Object groupID) throws Exception {
        return false;
    }

    @Override
    public Object getMaterialization(Object groupID) throws Exception {
        return null;
    }

    @Override
    public Object getMaterializationStage(Object groupID) throws Exception {
        return null;
    }

    @Override
    public String getNativeType(Object elementID) throws Exception {
        return null;
    }

    @Override
    public boolean isProcedure(Object groupID) throws Exception {
        return false;
    }

    @Override
    public boolean hasProcedure(String procedureName) {
        return false;
    }

    @Override
    public String[] getVDBResourcePaths() throws Exception {
        return null;
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
    public String getCharacterVDBResource(String resourcePath) throws Exception {
        return null;
    }

    @Override
    public byte[] getBinaryVDBResource(String resourcePath) throws Exception {
        return null;
    }

    @Override
    public Object getPrimaryKey(Object metadataID) {
        return null;
    }

    @Override
    public IFunctionLibrary getFunctionLibrary() {
        return null;
    }

    @Override
    public boolean isTemporaryTable(Object groupID) throws Exception {
        return false;
    }

    @Override
    public Object addToMetadataCache(Object metadataID, String key, Object value) throws Exception {
        return null;
    }

    @Override
    public Object getFromMetadataCache(Object metadataID, String key) throws Exception {
        return null;
    }

    @Override
    public boolean isScalarGroup(Object groupID) throws Exception {
        return false;
    }

    @Override
    public boolean isMultiSource(Object modelId) throws Exception {
        return false;
    }

    @Override
    public boolean isMultiSourceElement(Object elementId) throws Exception {
        return false;
    }

    @Override
    public IQueryMetadataInterface getDesignTimeMetadata() {
        return null;
    }

    @Override
    public IQueryMetadataInterface getSessionMetadata() {
        return null;
    }

    @Override
    public Set getImportedModels() {
        return null;
    }

    @Override
    public ScriptEngine getScriptEngine(String langauge) throws Exception {
        return null;
    }

    @Override
    public boolean isVariadic(Object metadataID) {
        return false;
    }

    @Override
    public Map getFunctionBasedExpressions(Object metadataID) {
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
    public String getExtensionProperty(Object metadataID, String key, boolean checkUnqualified) {
        return null;
    }

    @Override
    public boolean findShortName() {
        return false;
    }

    @Override
    public boolean useOutputName() {
        return false;
    }
}
