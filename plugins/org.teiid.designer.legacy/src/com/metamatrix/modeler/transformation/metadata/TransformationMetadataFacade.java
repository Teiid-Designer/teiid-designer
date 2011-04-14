/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.transformation.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.teiid.api.exception.query.QueryMetadataException;
import org.teiid.core.TeiidComponentException;
import org.teiid.core.util.ArgCheck;
import org.teiid.core.util.Assertion;
import org.teiid.core.util.LRUCache;
import org.teiid.core.util.StringUtil;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.mapping.xml.MappingDocument;
import org.teiid.query.mapping.xml.MappingNode;
import org.teiid.query.metadata.BasicQueryMetadataWrapper;
import org.teiid.query.metadata.GroupInfo;
import org.teiid.query.metadata.StoredProcedureInfo;
import com.metamatrix.core.util.AssertionUtil;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.ColumnRecord;
import com.metamatrix.modeler.core.metadata.runtime.ColumnSetRecord.ColumnSetRecordProperties;
import com.metamatrix.modeler.core.metadata.runtime.ForeignKeyRecord.ForeignKeyRecordProperties;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord.MetadataRecordProperties;
import com.metamatrix.modeler.core.metadata.runtime.ProcedureRecord.ProcedureRecordProperties;
import com.metamatrix.modeler.core.metadata.runtime.TableRecord;
import com.metamatrix.modeler.core.metadata.runtime.TableRecord.TableRecordProperties;

/**
 * Modelers implementation of QueryMetadataInterface that reads columns, groups, modeles etc. index files for various metadata
 * properties. TransformationMetadataFacade should only be used when the metadata is read only. It is used in the modeler with in
 * the context of validating a Query(when the metadata is read only).
 */
public class TransformationMetadataFacade extends BasicQueryMetadataWrapper {

    /**
     * Default amount of space in the cache
     */
    public static final int DEFAULT_SPACELIMIT = 4000;

    /**
     * Partial name cache is useful when the user executes same kind of partial name queries many times, number of such queries
     * will not be many so limiting this cachesize to 100.
     */
    public static final int DEFAULT_SPACELIMIT_PARTIAL_NAME_CACHE = 100;

    private static final int GROUP_INFO_CACHE_SIZE = 500;

    private final TransformationMetadata metadata;
    private final Map<String, Object> nameToIdCache;
    private final Map<Object, MetadataRecord> idToRecordCache;
    private final Map<String, String> partialNameToFullNameCache;
    private final Map groupInfoCache = Collections.synchronizedMap(new LRUCache(GROUP_INFO_CACHE_SIZE));

    public TransformationMetadataFacade( final TransformationMetadata delegate ) {
        this(delegate, DEFAULT_SPACELIMIT);
    }

    public TransformationMetadataFacade( final TransformationMetadata delegate,
                                         int cacheSize ) {
    	super(delegate);
        ArgCheck.isNotNull(delegate);
        this.metadata = delegate;
        this.nameToIdCache = Collections.synchronizedMap(new LRUCache<String, Object>(cacheSize));
        this.idToRecordCache = Collections.synchronizedMap(new LRUCache<Object, MetadataRecord>(cacheSize));
        this.partialNameToFullNameCache = Collections.synchronizedMap(new LRUCache<String, String>(
                                                                                                   DEFAULT_SPACELIMIT_PARTIAL_NAME_CACHE));
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getElementID(java.lang.String)
     */
    @Override
	public Object getElementID( final String elementName ) throws TeiidComponentException, QueryMetadataException {
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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getGroupID(java.lang.String)
     */
    @Override
	public Object getGroupID( final String groupName ) throws TeiidComponentException, QueryMetadataException {
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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getGroupsForPartialName(java.lang.String)
     */
    @Override
	public Collection getGroupsForPartialName( final String partialGroupName )
        throws TeiidComponentException, QueryMetadataException {
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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getModelID(java.lang.Object)
     */
    @Override
	public Object getModelID( final Object groupOrElementID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(MetadataRecord.class, groupOrElementID);

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
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getElementIDsInGroupID(java.lang.Object)
     */
    @Override
	public List getElementIDsInGroupID( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getGroupIDForElementID(java.lang.Object)
     */
    @Override
	public Object getGroupIDForElementID( final Object elementID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(ColumnRecord.class, elementID);
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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getStoredProcedureInfoForProcedure(java.lang.String)
     */
    @Override
	public StoredProcedureInfo getStoredProcedureInfoForProcedure( final String fullyQualifiedProcedureName )
        throws TeiidComponentException, QueryMetadataException {

        StoredProcedureInfo procInfo = null;

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
            procInfo = (StoredProcedureInfo)record.getPropertyValue(ProcedureRecordProperties.STORED_PROC_INFO_FOR_RECORD);
            // this should never occur but if procInfo cannot be found on the record
            if (procInfo == null) {
                procInfo = this.metadata.getStoredProcedureInfoForProcedure(fullyQualifiedProcedureName);
                record.setPropertyValue(ProcedureRecordProperties.STORED_PROC_INFO_FOR_RECORD, procInfo);
            }
        }

        return procInfo;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getVirtualPlan(java.lang.Object)
     */
    @Override
	public QueryNode getVirtualPlan( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;

        QueryNode queryPlan = (QueryNode)tableRecord.getPropertyValue(TableRecordProperties.QUERY_PLAN);
        if (queryPlan == null) {
            synchronized (tableRecord) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                queryPlan = (QueryNode)tableRecord.getPropertyValue(TableRecordProperties.QUERY_PLAN);
                if (queryPlan == null) {
                    queryPlan = this.metadata.getVirtualPlan(groupID);
                    tableRecord.setPropertyValue(TableRecordProperties.QUERY_PLAN, queryPlan);
                }
            }
        }

        return queryPlan;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getInsertPlan(java.lang.Object)
     */
    @Override
	public String getInsertPlan( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getUpdatePlan(java.lang.Object)
     */
    @Override
	public String getUpdatePlan( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getDeletePlan(java.lang.Object)
     */
    @Override
	public String getDeletePlan( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

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



    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getIndexesInGroup(java.lang.Object)
     */
    @Override
	public Collection getIndexesInGroup( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getUniqueKeysInGroup(java.lang.Object)
     */
    @Override
	public Collection getUniqueKeysInGroup( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getForeignKeysInGroup(java.lang.Object)
     */
    @Override
	public Collection getForeignKeysInGroup( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getPrimaryKeyIDForForeignKeyID(java.lang.Object)
     */
    @Override
	public Object getPrimaryKeyIDForForeignKeyID( final Object foreignKeyID )
        throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(MetadataRecord.class, foreignKeyID);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getAccessPatternsInGroup(java.lang.Object)
     */
    @Override
	public Collection getAccessPatternsInGroup( final Object groupID )
        throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(MetadataRecord.class, groupID);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getElementIDsInIndex(java.lang.Object)
     */
    @Override
	public List getElementIDsInIndex( final Object index ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(MetadataRecord.class, index);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getElementIDsInKey(java.lang.Object)
     */
    @Override
	public List getElementIDsInKey( final Object key ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(MetadataRecord.class, key);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getElementIDsInAccessPattern(java.lang.Object)
     */
    @Override
	public List getElementIDsInAccessPattern( final Object accessPattern )
        throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(MetadataRecord.class, accessPattern);

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

    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getMappingNode(java.lang.Object)
     */
    @Override
	public MappingNode getMappingNode( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

        MetadataRecord record = (MetadataRecord)groupID;

        MappingDocument mappingNode = (MappingDocument)record.getPropertyValue(TableRecordProperties.MAPPING_NODE_FOR_RECORD);
        if (mappingNode == null) {
            synchronized (record) {
                // look up the cache again, might have been updated by
                // the thread that just released the lock
                mappingNode = (MappingDocument)record.getPropertyValue(TableRecordProperties.MAPPING_NODE_FOR_RECORD);
                if (mappingNode == null) {
                    mappingNode = (MappingDocument)this.metadata.getMappingNode(groupID);
                    record.setPropertyValue(TableRecordProperties.MAPPING_NODE_FOR_RECORD, mappingNode);
                }
            }
        }

        return (MappingNode)mappingNode.clone();
    }


    /* (non-Javadoc)
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getXMLTempGroups(java.lang.Object)
     */
    @Override
	public Collection getXMLTempGroups( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

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
	public List getXMLSchemas( final Object groupID ) throws TeiidComponentException, QueryMetadataException {
        ArgCheck.isInstanceOf(TableRecord.class, groupID);

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


    /* 
     * @see com.metamatrix.query.metadata.QueryMetadataInterface#getExtensionProperties(java.lang.Object)
     */
    @Override
	public Properties getExtensionProperties( final Object metadataID )
        throws TeiidComponentException, QueryMetadataException {

        ArgCheck.isInstanceOf(MetadataRecord.class, metadataID);

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
     * @return
     */
    public TransformationMetadata getDelegate() {
        return this.metadata;
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    private MetadataRecord getRecordByName( final String fullname,
                                            final char recordType ) {
        AssertionUtil.isNotZeroLength(fullname);

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
        AssertionUtil.isNotZeroLength(partialName);

        // Check the cache for the identifier corresponding to this partialname ...
        return this.partialNameToFullNameCache.get(getLookupKey(partialName, recordType));
    }

    private MetadataRecord getRecordByID( final Object id ) {
        Assertion.isNotNull(id);
        return this.idToRecordCache.get(id);
    }

    private void updateNameToIdCache( final String fullName,
                                      final char recordType,
                                      final Object id ) {
        if (!StringUtil.isEmpty(fullName) && id != null) {
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
        if (!StringUtil.isEmpty(partialName) && !StringUtil.isEmpty(fullName)) {
            this.partialNameToFullNameCache.put(getLookupKey(partialName, recordType), fullName);
        }
    }

    private String getLookupKey( final String name,
                                 final char recordType ) {
        return name.toUpperCase() + recordType;
    }

    @Override
    public Object addToMetadataCache( Object metadataID,
                                      String key,
                                      Object value ) {
        ArgCheck.isInstanceOf(MetadataRecord.class, metadataID);
        if (key.startsWith(GroupInfo.CACHE_PREFIX)) {
            return this.groupInfoCache.put(metadataID + "/" + key, value); //$NON-NLS-1$
        }
        MetadataRecord record = (MetadataRecord)metadataID;
        synchronized (record) {
            Object result = record.getPropertyValue(key);
            record.setPropertyValue(key, value);
            return result;
        }
    }

    @Override
    public Object getFromMetadataCache( Object metadataID,
                                        String key ) {
        ArgCheck.isInstanceOf(MetadataRecord.class, metadataID);
        if (key.startsWith(GroupInfo.CACHE_PREFIX)) {
            return this.groupInfoCache.get(metadataID + "/" + key); //$NON-NLS-1$
        }
        MetadataRecord record = (MetadataRecord)metadataID;
        synchronized (record) {
            return record.getPropertyValue(key);
        }
    }


}
