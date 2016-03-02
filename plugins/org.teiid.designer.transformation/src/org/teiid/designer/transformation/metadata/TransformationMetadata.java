/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.transformation.metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.script.ScriptEngine;
import org.teiid.core.designer.TeiidDesignerException;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.core.designer.id.UUID;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.ModelType;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.CompositeIndexSelector;
import org.teiid.designer.core.index.IEntryResult;
import org.teiid.designer.core.index.Index;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.index.RuntimeIndexSelector;
import org.teiid.designer.core.index.SimpleIndexUtil;
import org.teiid.designer.metadata.runtime.ColumnRecord;
import org.teiid.designer.metadata.runtime.ColumnRecordComparator;
import org.teiid.designer.metadata.runtime.ColumnSetRecord;
import org.teiid.designer.metadata.runtime.DatatypeRecord;
import org.teiid.designer.metadata.runtime.ForeignKeyRecord;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metadata.runtime.ModelRecord;
import org.teiid.designer.metadata.runtime.ProcedureParameterRecord;
import org.teiid.designer.metadata.runtime.ProcedureRecord;
import org.teiid.designer.metadata.runtime.PropertyRecord;
import org.teiid.designer.metadata.runtime.TableRecord;
import org.teiid.designer.metadata.runtime.TransformationRecord;
import org.teiid.designer.metadata.runtime.VdbRecord;
import org.teiid.designer.metadata.runtime.impl.RecordFactory;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.udf.UdfManager;
import org.teiid.designer.xml.IMappingDocumentFactory;
import org.teiid.designer.xml.IMappingNode;

/**
 * Modelers implementation of QueryMetadataInterface that reads columns, groups, models etc. index files for various metadata
 * properties.
 *
 * @since 8.0
 */
public class TransformationMetadata implements IQueryMetadataInterface {

    // Fix Me: The following constants come from org.teiid.designer.metamodels.relational.NullableType
    private static int NULLABLE = 1;
    private static int NULLABLE_UNKNOWN = 2;
    // Fix Me: The following constants come from org.teiid.designer.metamodels.relational.SearchabilityType
    private static int SEARCHABLE = 0;
    private static int ALL_EXCEPT_LIKE = 1;
    private static int LIKE_ONLY = 2;

    /** Delimiter character used when specifying fully qualified entity names */
    public static final char DELIMITER_CHAR = IndexConstants.NAME_DELIM_CHAR;
    protected static final String DELIMITER_STRING = CoreStringUtil.Constants.EMPTY_STRING + IndexConstants.NAME_DELIM_CHAR;

    private static ColumnRecordComparator columnComparator = new ColumnRecordComparator();

    // error message cached to avaid i18n lookup each time
    private static String NOT_EXISTS_MESSAGE = CoreStringUtil.Constants.SPACE
                                              + TransformationPlugin.Util.getString("TransformationMetadata.does_not_exist._1"); //$NON-NLS-1$

    // context object all the info needed for metadata lookup
    private final QueryMetadataContext context;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * TransformationMetadata constructor
     * 
     * @param context Object containing the info needed to lookup metadta.
     */
    protected TransformationMetadata(final QueryMetadataContext context) {
        CoreArgCheck.isNotNull(context);
        this.context = context;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    @Override
    public ITeiidServerVersion getTeiidVersion() {
        return ModelerCore.getTeiidServerVersion();
    }

    @Override
    public Object getElementID(final String elementName) throws Exception {
        CoreArgCheck.isNotEmpty(elementName);

        // elementfull names always contain atlest 3 segments(modelname.groupName.elementName)
        if (CoreStringUtil.startsWithIgnoreCase(elementName, UUID.PROTOCOL)
            || CoreStringUtil.getTokens(elementName, DELIMITER_STRING).size() >= 3) {
            // Query the index files
            return getRecordByType(elementName, IndexConstants.RECORD_TYPE.COLUMN);
        }
        throw new Exception(elementName + NOT_EXISTS_MESSAGE);
    }

    @Override
    public Object getGroupID(final String groupName) throws Exception {
        CoreArgCheck.isNotEmpty(groupName);

        // groupfull names always contain atlest 2 segments(modelname.groupName)
        if (CoreStringUtil.startsWithIgnoreCase(groupName, UUID.PROTOCOL)
            || CoreStringUtil.getTokens(groupName, DELIMITER_STRING).size() >= 2) {
            // Query the index files
            return getRecordByType(groupName, IndexConstants.RECORD_TYPE.TABLE);
        }
        throw new Exception(groupName + NOT_EXISTS_MESSAGE);
    }

    @Override
    public Collection getGroupsForPartialName(final String partialGroupName) throws Exception {
        CoreArgCheck.isNotEmpty(partialGroupName);

        Collection tableRecords = null;

        String partialName = partialGroupName;
        // if it the group is a UUID
        if (!CoreStringUtil.startsWithIgnoreCase(partialGroupName, UUID.PROTOCOL)) {
            // Prepend a "." so only match full part names
            partialName = DELIMITER_CHAR + partialGroupName;
        }

        // Query the index files
        tableRecords = findMetadataRecords(IndexConstants.RECORD_TYPE.TABLE, partialName, true);

        // Extract the fully qualified names to return
        final Collection tableNames = new ArrayList(tableRecords.size());
        for (Iterator recordIter = tableRecords.iterator(); recordIter.hasNext();) {
            // get the table record for this result
            TableRecord tableRecord = (TableRecord)recordIter.next();
            tableNames.add(getFullName(tableRecord));
        }
        return tableNames;
    }

    @Override
    public Object getModelID(final Object groupOrElementID) throws Exception {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, groupOrElementID);
        MetadataRecord metadataRecord = (MetadataRecord)groupOrElementID;

        // get modelName
        String modelName = metadataRecord.getModelName();

        // Query the index files
        return getRecordByType(modelName, IndexConstants.RECORD_TYPE.MODEL);
    }

    @Override
    public String getFullName(final Object metadataID) {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, metadataID);
        MetadataRecord metadataRecord = (MetadataRecord)metadataID;
        return metadataRecord.getFullName();
    }

    @Override
    public List getElementIDsInGroupID(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;

        // Query the index files
        final String groupName = tableRecord.getFullName();
        final String groupUUID = tableRecord.getUUID();
        CoreArgCheck.isNotNull(groupUUID);
        final Collection results = findChildRecords(tableRecord, IndexConstants.RECORD_TYPE.COLUMN);
        if (results.isEmpty()) {
            throw new Exception(
                                             TransformationPlugin.Util.getString("TransformationMetadata.Group(0}_does_not_have_elements", groupName)); //$NON-NLS-1$
        }

        List columnRecords = new ArrayList(results);

        // Sort the column records according to their positions
        Collections.sort(columnRecords, columnComparator);
        return columnRecords;
    }

    @Override
    public Object getGroupIDForElementID(final Object elementID) throws Exception {
        if (elementID instanceof ColumnRecord) {
            ColumnRecord columnRecord = (ColumnRecord)elementID;
            String tableUUID = columnRecord.getParentUUID();
            return this.getGroupID(tableUUID);
        } else if (elementID instanceof ProcedureParameterRecord) {
            ProcedureParameterRecord columnRecord = (ProcedureParameterRecord)elementID;
            String tableUUID = columnRecord.getParentUUID();
            return this.getGroupID(tableUUID);
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public IStoredProcedureInfo getStoredProcedureInfoForProcedure(final String procedureName)
        throws Exception {
        IStoredProcedureInfo result = getStoredProcInfoDirect(procedureName);
        if (result == null) {
            throw new Exception(procedureName + NOT_EXISTS_MESSAGE);
        }
        return result;
    }
    
    @Override
    public boolean hasProcedure(String name) {
        try {
            return getStoredProcInfoDirect(name) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to convert the parameter type returned from a ProcedureParameterRecord to the parameter type expected by
     * StoredProcedureInfo
     * 
     * @param parameterType
     * @return
     */
    private ISPParameter.ParameterInfo convertParamRecordTypeToStoredProcedureType(final int parameterType) {
        switch (parameterType) {
            case MetadataConstants.PARAMETER_TYPES.IN_PARM:
                return ISPParameter.ParameterInfo.IN;
            case MetadataConstants.PARAMETER_TYPES.OUT_PARM:
                return ISPParameter.ParameterInfo.OUT;
            case MetadataConstants.PARAMETER_TYPES.INOUT_PARM:
                return ISPParameter.ParameterInfo.INOUT;
            case MetadataConstants.PARAMETER_TYPES.RETURN_VALUE:
                return ISPParameter.ParameterInfo.RETURN_VALUE;
            case MetadataConstants.PARAMETER_TYPES.RESULT_SET:
                return ISPParameter.ParameterInfo.RESULT_SET;
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public String getElementType(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getRuntimeType();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return ((ProcedureParameterRecord)elementID).getRuntimeType();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public Object getDefaultValue(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getDefaultValue();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return ((ProcedureParameterRecord)elementID).getDefaultValue();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public Object getMinimumValue(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getMinValue();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return null;
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public Object getMaximumValue(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getMaxValue();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return null;
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public boolean isVirtualGroup(final Object groupID) {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        return ((TableRecord)groupID).isVirtual();
    }

    @Override
    public boolean isProcedure(final Object groupID) {
        if (groupID instanceof ProcedureRecord) {
            return true;
        }
        if (groupID instanceof TableRecord) {
            return false;
        }
        throw createInvalidRecordTypeException(groupID);
    }

    @Override
    public boolean isVirtualModel(final Object modelID) {
        CoreArgCheck.isInstanceOf(ModelRecord.class, modelID);
        ModelRecord modelRecord = (ModelRecord)modelID;
        return (modelRecord.getModelType() == ModelType.VIRTUAL);
    }

    @Override
    public IQueryNode getVirtualPlan(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;
        final String groupName = tableRecord.getFullName();
        if (tableRecord.isVirtual()) {
            // Query the index files
            Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.SELECT_TRANSFORM, groupName, false);
            // this group may be an xml document
            if (results.isEmpty()) {
                results = findMetadataRecords(IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM, groupName, false);
            }
            int resultSize = results.size();
            if (resultSize == 1) {
                // get the transform record for this result
                final TransformationRecord transformRecord = (TransformationRecord)results.iterator().next();

                String transQuery = transformRecord.getTransformation();
                IQueryService queryService = ModelerCore.getTeiidQueryService();
                IQueryFactory factory = queryService.createQueryFactory();
                IQueryNode queryNode = factory.createQueryNode(transQuery);

                // get any bindings and add them onto the query node
                List bindings = transformRecord.getBindings();
                if (bindings != null) {
                    for (Iterator bindIter = bindings.iterator(); bindIter.hasNext();) {
                        queryNode.addBinding((String)bindIter.next());
                    }
                }
                return queryNode;
            }
            // no transfomation available
            if (resultSize == 0) {
                throw new Exception(
                                                 TransformationPlugin.Util.getString("TransformationMetadata.Could_not_find_query_plan_for_the_group__5") + groupName); //$NON-NLS-1$
            }
            // there should be only one result entry for a fully qualified name
            if (resultSize > 1) {
                throw new Exception(
                                                          TransformationPlugin.Util.getString("TransformationMetadata.GroupID_ambiguous_there_are_multiple_virtual_plans_available_for_this_groupID__1") + groupName); //$NON-NLS-1$
            }
        }
        throw new Exception(
                                         TransformationPlugin.Util.getString("TransformationMetadata.QueryPlan_could_not_be_found_for_physical_group__6") + groupName); //$NON-NLS-1$
    }

    @Override
    public String getInsertPlan(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;
        final String groupName = tableRecord.getFullName();
        if (tableRecord.isVirtual()) {
            // Query the index files
            Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.INSERT_TRANSFORM, groupName, false);
            int resultSize = results.size();
            if (resultSize == 1) {
                // get the transform record for this result
                final TransformationRecord transformRecord = (TransformationRecord)results.iterator().next();
                return transformRecord.getTransformation();
            }
            // no transfomation available
            if (resultSize == 0) {
                return null;
            }
            // there should be only one result entry for a fully qualified name
            if (resultSize > 1) {
                throw new Exception(
                                                          TransformationPlugin.Util.getString("TransformationMetadata.GroupID_ambiguous_there_are_multiple_insert_plans_available_for_this_groupID__2") + groupName); //$NON-NLS-1$
            }
        }
        throw new Exception(
                                         TransformationPlugin.Util.getString("TransformationMetadata.InsertPlan_could_not_be_found_for_physical_group__8") + groupName); //$NON-NLS-1$
    }

    @Override
    public String getUpdatePlan(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;
        final String groupName = tableRecord.getFullName();
        if (tableRecord.isVirtual()) {
            // Query the index files
            Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM, groupName, false);
            int resultSize = results.size();
            if (resultSize == 1) {
                // get the transform record for this result
                final TransformationRecord transformRecord = (TransformationRecord)results.iterator().next();
                return transformRecord.getTransformation();
            }
            // no transfomation available
            if (resultSize == 0) {
                return null;
            }
            // there should be only one result entry for a fully qualified name
            if (resultSize > 1) {
                throw new Exception(
                                                          TransformationPlugin.Util.getString("TransformationMetadata.GroupID_ambiguous_there_are_multiple_update_plans_available_for_this_groupID__3") + groupName); //$NON-NLS-1$
            }
        }

        throw new Exception(
                                         TransformationPlugin.Util.getString("TransformationMetadata.InsertPlan_could_not_be_found_for_physical_group__10") + groupName); //$NON-NLS-1$
    }

    @Override
    public String getDeletePlan(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;
        final String groupName = tableRecord.getFullName();
        if (tableRecord.isVirtual()) {
            // Query the index files
            Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.DELETE_TRANSFORM, groupName, false);
            int resultSize = results.size();
            if (resultSize == 1) {
                // get the transform record for this result
                final TransformationRecord transformRecord = (TransformationRecord)results.iterator().next();
                return transformRecord.getTransformation();
            }
            // no transfomation available
            if (resultSize == 0) {
                return null;
            }
            // there should be only one result entry for a fully qualified name
            if (resultSize > 1) {
                throw new Exception(
                                                          TransformationPlugin.Util.getString("TransformationMetadata.GroupID_ambiguous_there_are_multiple_delete_plans_available_for_this_groupID__4") + groupName); //$NON-NLS-1$
            }
        }
        throw new Exception(
                                         TransformationPlugin.Util.getString("TransformationMetadata.DeletePlan_could_not_be_found_for_physical_group__12") + groupName); //$NON-NLS-1$
    }

    @Override
    public boolean modelSupports(final Object modelID,
                                 final int modelConstant) {
        CoreArgCheck.isInstanceOf(ModelRecord.class, modelID);

        switch (modelConstant) {
            default:
                throw new UnsupportedOperationException(
                                                        TransformationPlugin.Util.getString("TransformationMetadata.Unknown_support_constant___12") + modelConstant); //$NON-NLS-1$
        }
    }

    @Override
    public boolean groupSupports(final Object groupID,
                                 final int groupConstant) {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;

        switch (groupConstant) {
            case SupportConstants.Group.UPDATE:
                return tableRecord.supportsUpdate();
            default:
                throw new UnsupportedOperationException(
                                                        TransformationPlugin.Util.getString("TransformationMetadata.Unknown_support_constant___12") + groupConstant); //$NON-NLS-1$
        }
    }

    @Override
    public boolean elementSupports(final Object elementID,
                                   final int elementConstant) {

        if (elementID instanceof ColumnRecord) {
            ColumnRecord columnRecord = (ColumnRecord)elementID;
            switch (elementConstant) {
                case SupportConstants.Element.NULL:
                    int ntype1 = columnRecord.getNullType();
                    return (ntype1 == NULLABLE);
                case SupportConstants.Element.NULL_UNKNOWN:
                    int ntype2 = columnRecord.getNullType();
                    return (ntype2 == NULLABLE_UNKNOWN);
                case SupportConstants.Element.SEARCHABLE_COMPARE:
                    int stype1 = columnRecord.getSearchType();
                    return (stype1 == SEARCHABLE || stype1 == ALL_EXCEPT_LIKE);
                case SupportConstants.Element.SEARCHABLE_LIKE:
                    int stype2 = columnRecord.getSearchType();
                    return (stype2 == SEARCHABLE || stype2 == LIKE_ONLY);
                case SupportConstants.Element.SELECT:
                    return columnRecord.isSelectable();
                case SupportConstants.Element.UPDATE:
                    return columnRecord.isUpdatable();
                case SupportConstants.Element.DEFAULT_VALUE:
                    Object defaultValue = columnRecord.getDefaultValue();
                    if (defaultValue == null) {
                        return false;
                    }
                    return true;
                case SupportConstants.Element.AUTO_INCREMENT:
                    return columnRecord.isAutoIncrementable();
                case SupportConstants.Element.CASE_SENSITIVE:
                    return columnRecord.isCaseSensitive();
                case SupportConstants.Element.SIGNED:
                    return columnRecord.isSigned();
                default:
                    throw new UnsupportedOperationException(
                                                            TransformationPlugin.Util.getString("TransformationMetadata.Unknown_support_constant___12") + elementConstant); //$NON-NLS-1$
            }
        } else if (elementID instanceof ProcedureParameterRecord) {
            ProcedureParameterRecord columnRecord = (ProcedureParameterRecord)elementID;
            switch (elementConstant) {
                case SupportConstants.Element.NULL:
                    int ntype1 = columnRecord.getNullType();
                    return (ntype1 == NULLABLE);
                case SupportConstants.Element.NULL_UNKNOWN:
                    int ntype2 = columnRecord.getNullType();
                    return (ntype2 == NULLABLE_UNKNOWN);
                case SupportConstants.Element.SEARCHABLE_COMPARE:
                case SupportConstants.Element.SEARCHABLE_LIKE:
                    return false;
                case SupportConstants.Element.SELECT:

                    if (columnRecord.getType() == MetadataConstants.PARAMETER_TYPES.IN_PARM) {
                        return false;
                    }

                    return true;
                case SupportConstants.Element.UPDATE:
                    return false;
                case SupportConstants.Element.DEFAULT_VALUE:
                    Object defaultValue = columnRecord.getDefaultValue();
                    if (defaultValue == null) {
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
                    throw new UnsupportedOperationException(
                                                            TransformationPlugin.Util.getString("TransformationMetadata.Unknown_support_constant___12") + elementConstant); //$NON-NLS-1$
            }

        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    private IllegalArgumentException createInvalidRecordTypeException(Object elementID) {
        return new IllegalArgumentException(
                                            TransformationPlugin.Util.getString("TransformationMetadata.Invalid_type", elementID.getClass().getName())); //$NON-NLS-1$
    }

    @Override
    public int getMaxSetSize(final Object modelID) {
        CoreArgCheck.isInstanceOf(ModelRecord.class, modelID);
        return ((ModelRecord)modelID).getMaxSetSize();
    }

    @Override
    public Collection getIndexesInGroup(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;

        final String groupUUID = tableRecord.getUUID();
        CoreArgCheck.isNotNull(groupUUID);

        // get the indexIDs
        Collection indexIDs = tableRecord.getIndexIDs();
        Collection indexRecords = new HashSet(indexIDs.size());
        // find a index record for each ID
        for (Iterator indexIter = tableRecord.getIndexIDs().iterator(); indexIter.hasNext();) {
            String indexID = (String)indexIter.next();
            // Query the index files
            final Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.INDEX, indexID, false);
            if (results.size() != 1) {
                if (results.isEmpty()) {
                    throw new Exception(
                                                     TransformationPlugin.Util.getString("TransformationMetadata.No_metadata_info_available_for_the_index_with_UUID_{0}._1", indexID)); //$NON-NLS-1$
                }
                throw new Exception(
                                                 TransformationPlugin.Util.getString("TransformationMetadata.Ambigous_index_with_UUID_{0},_found_multiple_indexes_with_the_given_UUID._2", indexID)); //$NON-NLS-1$
            }
            indexRecords.addAll(results);
        }

        return indexRecords;
    }

    @Override
    public Collection getUniqueKeysInGroup(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;

        final String groupUUID = tableRecord.getUUID();
        CoreArgCheck.isNotNull(groupUUID);
        // Query the index files
        // find all unique keys
        return findChildRecords(tableRecord, IndexConstants.RECORD_TYPE.UNIQUE_KEY);
    }

    @Override
    public Collection getForeignKeysInGroup(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;

        // Query the index files
        final String groupUUID = tableRecord.getUUID();
        CoreArgCheck.isNotNull(groupUUID);

        return findChildRecords(tableRecord, IndexConstants.RECORD_TYPE.FOREIGN_KEY);
    }

    @Override
    public Object getPrimaryKeyIDForForeignKeyID(final Object foreignKeyID)
        throws Exception {
        CoreArgCheck.isInstanceOf(ForeignKeyRecord.class, foreignKeyID);
        ForeignKeyRecord fkRecord = (ForeignKeyRecord)foreignKeyID;

        String uuid = (String)fkRecord.getUniqueKeyID();
        return this.getRecordByType(uuid, IndexConstants.RECORD_TYPE.PRIMARY_KEY);
    }

    @Override
    public Collection getAccessPatternsInGroup(final Object groupID)
        throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;

        // Query the index files
        final String groupUUID = tableRecord.getUUID();
        CoreArgCheck.isNotNull(groupUUID);
        return findChildRecords(tableRecord, IndexConstants.RECORD_TYPE.ACCESS_PATTERN);
    }

    @Override
    public List getElementIDsInIndex(final Object index) throws Exception {
        CoreArgCheck.isInstanceOf(ColumnSetRecord.class, index);
        ColumnSetRecord indexRecord = (ColumnSetRecord)index;

        boolean recordMatch = (indexRecord.getRecordType() == IndexConstants.RECORD_TYPE.INDEX);

        if (!recordMatch) {
            throw new Exception(
                                             TransformationPlugin.Util.getString("TransformationMetadata.The_metadataID_passed_does_not_match_a_index_record._1")); //$NON-NLS-1$
        }

        List uuids = indexRecord.getColumnIDs();
        List columnRecords = new ArrayList(uuids.size());

        for (Iterator uuidIter = uuids.iterator(); uuidIter.hasNext();) {
            String uuid = (String)uuidIter.next();
            columnRecords.add(this.getElementID(uuid));
        }

        return columnRecords;
    }

    @Override
    public List getElementIDsInKey(final Object key) throws Exception {
        CoreArgCheck.isInstanceOf(ColumnSetRecord.class, key);
        ColumnSetRecord keyRecord = (ColumnSetRecord)key;

        boolean recordMatch = (keyRecord.getRecordType() == IndexConstants.RECORD_TYPE.FOREIGN_KEY
                               || keyRecord.getRecordType() == IndexConstants.RECORD_TYPE.PRIMARY_KEY || keyRecord.getRecordType() == IndexConstants.RECORD_TYPE.UNIQUE_KEY);
        if (!recordMatch) {
            throw new Exception(
                                             TransformationPlugin.Util.getString("TransformationMetadata.Expected_id_of_the_type_key_record_as_the_argument_2")); //$NON-NLS-1$
        }

        List uuids = keyRecord.getColumnIDs();

        // Get the table record for this key
        final String groupUUID = keyRecord.getParentUUID();
        CoreArgCheck.isNotNull(groupUUID);
        final TableRecord tableRecord = (TableRecord)this.getGroupID(groupUUID);

        // Query the index files
        final Collection results = findChildRecordsForColumns(tableRecord, IndexConstants.RECORD_TYPE.COLUMN, uuids);
        if (results.isEmpty()) {
            throw new Exception(tableRecord.getFullName() + NOT_EXISTS_MESSAGE);
        }

        return new ArrayList(results);
    }

    @Override
    public List getElementIDsInAccessPattern(final Object accessPattern)
        throws Exception {
        CoreArgCheck.isInstanceOf(ColumnSetRecord.class, accessPattern);
        ColumnSetRecord accessRecord = (ColumnSetRecord)accessPattern;

        boolean recordMatch = (accessRecord.getRecordType() == IndexConstants.RECORD_TYPE.ACCESS_PATTERN);
        if (!recordMatch) {
            throw new Exception(
                                             TransformationPlugin.Util.getString("TransformationMetadata.Expected_id_of_the_type_accesspattern_record_as_the_argument_3")); //$NON-NLS-1$
        }

        List uuids = accessRecord.getColumnIDs();

        // Get the table record for this key
        final String groupUUID = accessRecord.getParentUUID();
        CoreArgCheck.isNotNull(groupUUID);
        final TableRecord tableRecord = (TableRecord)this.getGroupID(groupUUID);

        // Query the index files
        final Collection results = findChildRecordsForColumns(tableRecord, IndexConstants.RECORD_TYPE.COLUMN, uuids);
        if (results.isEmpty()) {
            throw new Exception(tableRecord.getFullName() + NOT_EXISTS_MESSAGE);
        }

        return new ArrayList(results);
    }

    @Override
    public boolean isXMLGroup(final Object groupID) {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;
        if (tableRecord.getTableType() == MetadataConstants.TABLE_TYPES.DOCUMENT_TYPE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasMaterialization(final Object groupID) {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;
        return tableRecord.isMaterialized();
    }

    @Override
    public Object getMaterialization(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;
        if (tableRecord.isMaterialized()) {
            String uuid = (String)tableRecord.getMaterializedTableID();
            return this.getGroupID(uuid);
        }
        return null;
    }

    @Override
    public Object getMaterializationStage(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;
        if (tableRecord.isMaterialized()) {
            String uuid = (String)tableRecord.getMaterializedStageTableID();
            return this.getGroupID(uuid);
        }
        return null;
    }

    @Override
    public IMappingNode getMappingNode(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);

        TableRecord tableRecord = (TableRecord)groupID;
        final String groupName = tableRecord.getFullName();
        if (tableRecord.isVirtual()) {
            // get the transform record for this group
            TransformationRecord transformRecord = null;
            // Query the index files
            Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM, groupName, false);
            int resultSize = results.size();
            if (resultSize == 1) {
                // get the columnset record for this result
                transformRecord = (TransformationRecord)results.iterator().next();
            } else {
                if (resultSize == 0) {
                    throw new Exception(
                                                     TransformationPlugin.Util.getString("TransformationMetadata.Could_not_find_transformation_record_for_the_group__1") + groupName); //$NON-NLS-1$
                }
                // there should be only one for a fully qualified elementName
                if (resultSize > 1) {
                    throw new Exception(
                                                              TransformationPlugin.Util.getString("TransformationMetadata.Multiple_transformation_records_found_for_the_group___1") + groupName); //$NON-NLS-1$
                }
            }
            // get mappin transform
            String document = transformRecord.getTransformation();
            InputStream inputStream = new ByteArrayInputStream(document.getBytes());
            IMappingNode mappingDoc = null;
          
            try {
                IQueryService queryService = ModelerCore.getTeiidQueryService();
                IMappingDocumentFactory factory = queryService.getMappingDocumentFactory();
                mappingDoc = factory.loadMappingDocument(inputStream, groupName);
                return mappingDoc;
            } catch (Exception e) {
                throw new TeiidDesignerException(
                                                      e,
                                                      TransformationPlugin.Util.getString("TransformationMetadata.Error_trying_to_read_virtual_document_{0},_with_body__n{1}_1", groupName, mappingDoc)); //$NON-NLS-1$
            } finally {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }

        return null;
    }

    @Override
    public String getVirtualDatabaseName() {
        // Query the index files
        try {
            final VdbRecord vdbRecord = (VdbRecord)this.getRecordByType(null, IndexConstants.RECORD_TYPE.VDB_ARCHIVE);
            return vdbRecord.getName();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<Object> getXMLTempGroups(final Object groupID) throws Exception {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;

        int tableType = tableRecord.getTableType();
        if (tableType == MetadataConstants.TABLE_TYPES.DOCUMENT_TYPE) {
            // Query the index files
            final Collection results = findChildRecordsWithoutFiltering(tableRecord, IndexConstants.RECORD_TYPE.TABLE);
            if (!results.isEmpty()) {
                Collection tempGroups = new HashSet(results.size());
                for (Iterator resultIter = results.iterator(); resultIter.hasNext();) {
                    TableRecord record = (TableRecord)resultIter.next();
                    if (record.getTableType() == MetadataConstants.TABLE_TYPES.XML_STAGING_TABLE_TYPE) {
                        tempGroups.add(record);
                    }
                }
                return tempGroups;
            }
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public float getCardinality(final Object groupID) {
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        return ((TableRecord)groupID).getCardinality();
    }

    @Override
    public List getXMLSchemas(final Object groupID) throws Exception {

        if (!(getIndexSelector() instanceof CompositeIndexSelector || getIndexSelector() instanceof RuntimeIndexSelector)) {
            return Collections.EMPTY_LIST;
        }
        CoreArgCheck.isInstanceOf(TableRecord.class, groupID);
        TableRecord tableRecord = (TableRecord)groupID;

        // lookup transformation record for the group
        String groupName = tableRecord.getFullName();
        TransformationRecord transformRecord = null;

        // Query the index files
        Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM, groupName, false);
        int resultSize = results.size();
        if (resultSize == 1) {
            // get the columnset record for this result
            transformRecord = (TransformationRecord)results.iterator().next();
        } else {
            if (resultSize == 0) {
                throw new Exception(
                                                 TransformationPlugin.Util.getString("TransformationMetadata.Could_not_find_transformation_record_for_the_group__1") + groupName); //$NON-NLS-1$
            }
            // there should be only one for a fully qualified elementName
            if (resultSize > 1) {
                throw new Exception(
                                                          TransformationPlugin.Util.getString("TransformationMetadata.Multiple_transformation_records_found_for_the_group___1") + groupName); //$NON-NLS-1$
            }
        }

        // get the schema Paths
        List<String> schemaPaths = transformRecord.getSchemaPaths();

        List<String> fullPaths = new LinkedList<String>();

        for (String string : schemaPaths) {
            fullPaths.add(string);
        }

        // get schema contents
        List schemas = getIndexSelector().getFileContentsAsString(fullPaths);
        if (schemas == null || schemas.isEmpty()) {
            schemas = getIndexSelector().getFileContentsAsString(schemaPaths);
            if (schemas == null || schemas.isEmpty()) {
                throw new Exception(
                                                          TransformationPlugin.Util.getString("TransformationMetadata.Error_trying_to_read_schemas_for_the_document/table____1") + groupName); //$NON-NLS-1$
            }
        }

        return schemas;
    }

    @Override
    public String getNameInSource(final Object metadataID) {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, metadataID);
        return ((MetadataRecord)metadataID).getNameInSource();
    }

    @Override
    public int getElementLength(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getLength();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return ((ProcedureParameterRecord)elementID).getLength();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public int getPosition(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getPosition();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return ((ProcedureParameterRecord)elementID).getPosition();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public int getPrecision(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getPrecision();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return ((ProcedureParameterRecord)elementID).getPrecision();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public int getRadix(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getRadix();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return ((ProcedureParameterRecord)elementID).getRadix();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public String getFormat(Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getFormat();
        }
        throw createInvalidRecordTypeException(elementID);
    }

    @Override
    public int getScale(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getScale();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return ((ProcedureParameterRecord)elementID).getScale();
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public float getDistinctValues(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getDistinctValues();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return -1;
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public float getNullValues(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getNullValues();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return -1;
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public String getNativeType(final Object elementID) {
        if (elementID instanceof ColumnRecord) {
            return ((ColumnRecord)elementID).getNativeType();
        } else if (elementID instanceof ProcedureParameterRecord) {
            return null;
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    @Override
    public Properties getExtensionProperties(final Object metadataID)
        throws Exception {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, metadataID);
        MetadataRecord metadataRecord = (MetadataRecord)metadataID;

        Properties extProps = new Properties();

        // find the entities properties records
        String uuid = metadataRecord.getUUID();
        String prefixString = getUUIDPrefixPattern(IndexConstants.RECORD_TYPE.PROPERTY, uuid);

        Index[] indexes = getIndexes(IndexConstants.RECORD_TYPE.PROPERTY, getIndexSelector());
        IEntryResult[] results = queryIndex(indexes, prefixString.toCharArray(), true, true);

        if (results != null && results.length > 0) {
            // get the property records for this result
            for (int i = 0; i < results.length; i++) {
                if (results[i] != null) {
                    PropertyRecord record = (PropertyRecord)findMetadataRecord(results[i]);
                    if (record != null) {
                        extProps.setProperty(record.getPropertyName(), record.getPropertyValue());
                    }
                }
            }
        }

        return extProps;
    }

    @Override
    public byte[] getBinaryVDBResource(String resourcePath) throws Exception {
        String content = this.getCharacterVDBResource(resourcePath);
        if (content != null) {
            return content.getBytes();
        }
        return null;
    }

    @Override
    public String getCharacterVDBResource(String resourcePath) throws Exception {
        IndexSelector selector = this.getIndexSelector();
        // make sure the selector is initialized
        try {
            selector.getIndexes();
        } catch (IOException e) {
            throw new TeiidDesignerException(
                                             e,
                                             TransformationPlugin.Util.getString("TransformationMetadata.error_intialize_selector")); //$NON-NLS-1$
        }
        // look for the resource in only the first available indexSelector
        // built in assumption is that first selector is always for the vdb logged in
        if (selector instanceof CompositeIndexSelector) {
            CompositeIndexSelector compSelector = (CompositeIndexSelector)selector;
            List selectors = compSelector.getIndexSelectors();
            if (selectors.size() > 0) {
                IndexSelector firstSelector = (IndexSelector)selectors.get(0);
                return firstSelector.getFileContentAsString(resourcePath);
            }
        }
        return selector.getFileContentAsString(resourcePath);
    }

    @Override
    public String[] getVDBResourcePaths() throws Exception {
        IndexSelector selector = this.getIndexSelector();
        // make sure the selector is initialized
        try {
            selector.getIndexes();
        } catch (IOException e) {
            throw new TeiidDesignerException(
                                             e,
                                             TransformationPlugin.Util.getString("TransformationMetadata.error_intialize_selector")); //$NON-NLS-1$
        }
        // look for the resource in only the first available indexSelector
        // built in assumption is that first selector is always for the vdb logged in
        if (selector instanceof CompositeIndexSelector) {
            CompositeIndexSelector compSelector = (CompositeIndexSelector)selector;
            List selectors = compSelector.getIndexSelectors();
            if (selectors.size() > 0) {
                IndexSelector firstSelector = (IndexSelector)selectors.get(0);

                return firstSelector.getFilePaths();
            }
        }
        return selector.getFilePaths();
    }

    @Override
    public String getModeledType(final Object elementID) throws Exception {
        DatatypeRecord record = getDatatypeRecord(elementID);
        if (record != null) {
            return record.getDatatypeID();
        }
        return null;
    }

    @Override
    public String getModeledBaseType(final Object elementID) throws Exception {
        DatatypeRecord record = getDatatypeRecord(elementID);
        if (record != null) {
            return record.getBasetypeID();
        }
        return null;
    }

    @Override
    public String getModeledPrimitiveType(final Object elementID) throws Exception {
        DatatypeRecord record = getDatatypeRecord(elementID);
        if (record != null) {
            return record.getPrimitiveTypeID();
        }
        return null;
    }
    
    @Override
    public Object getPrimaryKey(Object metadataID) {
        CoreArgCheck.isInstanceOf(TableRecord.class, metadataID);
        TableRecord tableRecord = (TableRecord)metadataID;

        final String groupUUID = tableRecord.getUUID();
        CoreArgCheck.isNotNull(groupUUID);

        Collection pk;
        try {
            pk = findChildRecords(tableRecord, IndexConstants.RECORD_TYPE.PRIMARY_KEY);
        } catch (Exception e) {
            throw new TeiidDesignerRuntimeException(e);
        }
        if (pk.size() > 1) {
            throw new TeiidDesignerRuntimeException("Multiple primary keys for table"); //$NON-NLS-1$
        }
        return pk.iterator().next();
    }

    @Override
    public String getName(Object metadataID) {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, metadataID);
        MetadataRecord metadataRecord = (MetadataRecord)metadataID;
        return metadataRecord.getName();
    }
    
    @Override
    public IFunctionLibrary getFunctionLibrary() {
        return UdfManager.getInstance().getFunctionLibrary();
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    protected DatatypeRecord getDatatypeRecord(final Object elementID)
        throws Exception {
        if (elementID instanceof ColumnRecord) {
            String uuid = ((ColumnRecord)elementID).getDatatypeUUID();
            if (!CoreStringUtil.isEmpty(uuid)) {
                // Query the index files
                Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.DATATYPE, uuid, false);
                int resultSize = results.size();
                if (resultSize == 1) {
                    // get the datatype record for this result
                    return (DatatypeRecord)results.iterator().next();
                } else if (resultSize == 0) {
                    // there should be only one for the UUID
                    throw new Exception(uuid + NOT_EXISTS_MESSAGE);
                } else {
                    // there should be only one for the UUID
                    throw new Exception(TransformationPlugin.Util.getString("TransformationMetadata.0", uuid)); //$NON-NLS-1$
                }
            }
            return null;
        } else if (elementID instanceof ProcedureParameterRecord) {
            String uuid = ((ProcedureParameterRecord)elementID).getDatatypeUUID();
            if (!CoreStringUtil.isEmpty(uuid)) {
                // Query the index files
                Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.DATATYPE, uuid, false);
                int resultSize = results.size();
                if (resultSize == 1) {
                    // get the datatype record for this result
                    return (DatatypeRecord)results.iterator().next();
                } else if (resultSize == 0) {
                    // there should be only one for the UUID
                    throw new Exception(uuid + NOT_EXISTS_MESSAGE);
                } else {
                    // there should be only one for the UUID
                    throw new Exception(TransformationPlugin.Util.getString("TransformationMetadata.0", uuid)); //$NON-NLS-1$
                }
            }
            return null;
        } else {
            throw createInvalidRecordTypeException(elementID);
        }
    }

    /**
     * Return the array of MtkIndex instances representing temporary indexes
     * 
     * @param selector
     * @return
     * @throws TeiidComponentException
     */
    protected Index[] getIndexes(final char recordType,
                                 final IndexSelector selector) throws Exception {
        try {
            return selector.getIndexes();
        } catch (IOException e) {
            throw new TeiidDesignerException(
                                             e,
                                             TransformationPlugin.Util.getString("TransformationMetadata.Error_trying_to_obtain_index_file_using_IndexSelector_1", selector)); //$NON-NLS-1$
        }
    }

    /**
     * Return the pattern match string that could be used to match a UUID in a datatype index record. The RECORD_TYPE.DATATYPE
     * records contain a header portion of the form: recordType|datatypeID|basetypeID|fullName|objectID|nameInSource|...
     * 
     * @param uuid The UUID for which the pattern match string is to be constructed.
     * @return The pattern match string of the form: recordType|*|*|*|uuid|*
     */
    protected String getDatatypeUUIDMatchPattern(final String uuid) {
        CoreArgCheck.isNotNull(uuid);
        String uuidString = uuid;
        if (CoreStringUtil.startsWithIgnoreCase(uuid, UUID.PROTOCOL)) {
            uuidString = uuid.toLowerCase();
        }
        // construct the pattern string
        String patternStr = "" //$NON-NLS-1$
                            + IndexConstants.RECORD_TYPE.DATATYPE // recordType
                            + IndexConstants.RECORD_STRING.RECORD_DELIMITER + IndexConstants.RECORD_STRING.MATCH_CHAR // datatypeID
                            + IndexConstants.RECORD_STRING.RECORD_DELIMITER + IndexConstants.RECORD_STRING.MATCH_CHAR // basetypeID
                            + IndexConstants.RECORD_STRING.RECORD_DELIMITER + IndexConstants.RECORD_STRING.MATCH_CHAR // fullName
                            + IndexConstants.RECORD_STRING.RECORD_DELIMITER + uuidString // objectID
                            + IndexConstants.RECORD_STRING.RECORD_DELIMITER + IndexConstants.RECORD_STRING.MATCH_CHAR;
        return patternStr;
    }

    /**
     * Return the pattern match string that could be used to match a UUID in an index record. All index records contain a header
     * portion of the form: recordType|pathInModel|UUID|nameInSource|parentObjectID|
     * 
     * @param uuid The UUID for which the pattern match string is to be constructed.
     * @return The pattern match string of the form: recordType|*|uuid|*
     */
    protected String getUUIDMatchPattern(final char recordType,
                                         final String uuid) {
        CoreArgCheck.isNotNull(uuid);
        String uuidString = uuid;
        if (CoreStringUtil.startsWithIgnoreCase(uuid, UUID.PROTOCOL)) {
            uuidString = uuid.toLowerCase();
        }
        // construct the pattern string
        String patternStr = "" //$NON-NLS-1$
                            + recordType
                            + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                            + IndexConstants.RECORD_STRING.MATCH_CHAR
                            + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                            + uuidString
                            + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                            + IndexConstants.RECORD_STRING.MATCH_CHAR;
        return patternStr;
    }

    /**
     * Return the pattern match string that could be used to match a partially/fully qualified entity name in an index record. All
     * index records contain a header portion of the form: recordType|pathInModel|UUID|nameInSource|parentObjectID|
     * 
     * @param name The partially/fully qualified name for which the pattern match string is to be constructed.
     * @return The pattern match string of the form: recordType|*name|*
     */
    protected String getMatchPattern(final char recordType,
                                     final String name) {
        CoreArgCheck.isNotNull(name);

        // construct the pattern string
        String patternStr = "" //$NON-NLS-1$
                            + recordType
                            + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                            + IndexConstants.RECORD_STRING.MATCH_CHAR;
        if (name != null) {
            patternStr = patternStr + name.trim().toUpperCase() + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                         + IndexConstants.RECORD_STRING.MATCH_CHAR;
        }
        return patternStr;
    }

    /**
     * Return the prefix match string that could be used to exactly match a fully qualified entity name in an index record. All
     * index records contain a header portion of the form: recordType|pathInModel|UUID|nameInSource|parentObjectID|
     * 
     * @param name The fully qualified name for which the prefix match string is to be constructed.
     * @return The pattern match string of the form: recordType|name|
     */
    protected String getPrefixPattern(final char recordType,
                                      final String name) {

        // construct the pattern string
        String patternStr = "" //$NON-NLS-1$
                            + recordType + IndexConstants.RECORD_STRING.RECORD_DELIMITER;
        if (name != null) {
            patternStr = patternStr + name.trim().toUpperCase() + IndexConstants.RECORD_STRING.RECORD_DELIMITER;
        }

        return patternStr;
    }

    /**
     * Return the prefix match string that could be used to exactly match a uuid in an index record.
     * 
     * @param uuid The uuid for which the prefix match string is to be constructed.
     * @return The pattern match string of the form: recordType|uuid|
     */
    protected String getUUIDPrefixPattern(final char recordType,
                                          final String uuid) {

        // construct the pattern string
        String patternStr = "" //$NON-NLS-1$
                            + recordType + IndexConstants.RECORD_STRING.RECORD_DELIMITER;
        if (uuid != null) {
            patternStr = patternStr + uuid.trim() + IndexConstants.RECORD_STRING.RECORD_DELIMITER;
        }

        return patternStr;
    }

    /**
     * Return the prefix match string that could be used to exactly match a fully qualified entity name in an index record. All
     * index records contain a header portion of the form: recordType|pathInModel|UUID|nameInSource|parentObjectID|
     * 
     * @param name The fully qualified name for which the prefix match string is to be constructed.
     * @return The pattern match string of the form: recordType|name|
     */
    protected String getParentPrefixPattern(final char recordType,
                                            final String name) {

        // construct the pattern string
        String patternStr = "" //$NON-NLS-1$
                            + recordType + IndexConstants.RECORD_STRING.RECORD_DELIMITER;
        if (name != null) {
            patternStr = patternStr + name.trim().toUpperCase() + DELIMITER_CHAR;
        }

        return patternStr;
    }

    /**
     * Get a MetadataRecord object given a entityName/UUID.
     * 
     * @param entityName String representing an entity, may be null(vdbs)
     * @param recordType The record type for the entity
     * @return A MetadataRecord object for a given entityName/UUID
     * @throws Exception
     */
    protected MetadataRecord getRecordByType(final String entityName,
                                             final char recordType) throws Exception {

        // Query the index files
        Collection results = findMetadataRecords(recordType, entityName, false);
        int resultSize = results.size();
        if (resultSize == 1) {
            // get the columnset record for this result
            return (MetadataRecord)results.iterator().next();
        } else if (resultSize == 0) {
            // there should be only one for the UUID
            throw new Exception(entityName + NOT_EXISTS_MESSAGE);
        } else {
            // there should be only one for the UUID
            throw new Exception(TransformationPlugin.Util.getString("TransformationMetadata.0", entityName)); //$NON-NLS-1$
        }
    }

    /**
     * Remove any MetadataRecord instances that do not match the specified uuid Due to the pattern matching used to query index
     * files if an index record matched the specified uuid string anywhere in that record it would be returned in the results (for
     * example, if the parent ObjectID in the index record matched the specified uuid).
     * 
     * @param uuid
     * @param records
     * @since 4.2
     */
    protected void filterMetadataRecordForUUID(final String uuid,
                                               Collection records) {
        if (uuid != null && records != null) {
            for (final Iterator iter = records.iterator(); iter.hasNext();) {
                final MetadataRecord record = (MetadataRecord)iter.next();
                if (record == null || !uuid.equals(record.getUUID())) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * Return the IndexSelector reference
     * 
     * @return
     */
    protected IndexSelector getIndexSelector() {
        return this.context.getIndexSelector();
    }

    /**
     * Return the QueryMetadataContext reference
     * 
     * @return
     */
    protected QueryMetadataContext getContext() {
        return this.context;
    }

    /**
     * Return all index file records that match the specified entity name, filtering by matching on parent uuid
     * 
     * @param parentRecord
     * @param childRecordType the type of the child to seek uuids
     * @param uuids to filter just the objects we want
     * @return columnRecords
     * @throws Exception
     */
    protected Collection findChildRecords(final MetadataRecord parentRecord,
                                          final char childRecordType) throws Exception {
        IEntryResult[] results = queryIndexByParentPath(childRecordType, parentRecord.getFullName());
        Collection records = findMetadataRecords(results);

        // if uniquekey records are being returned, also return primary key records,
        // as primary keys are unique keys
        if (childRecordType == IndexConstants.RECORD_TYPE.UNIQUE_KEY) {
            Collection primarKeyRecords = findMetadataRecords(queryIndexByParentPath(IndexConstants.RECORD_TYPE.PRIMARY_KEY,
                                                                                     parentRecord.getFullName()));
            records.addAll(primarKeyRecords);
        }

        // jh Case 5092: Moved filtering down into this method (findChildRecords()).
        // (It used to be done in each method that calls this one.)

        // filtering records (records with different parents could begin with same name)
        final String groupUUID = parentRecord.getUUID();

        List filteredRecords = new ArrayList(records.size());

        for (Iterator resultsIter = records.iterator(); resultsIter.hasNext();) {
            MetadataRecord record = (MetadataRecord)resultsIter.next();
            String parentUUID = record.getParentUUID();

            if (parentUUID != null && parentUUID.equalsIgnoreCase(groupUUID)) {
                filteredRecords.add(record);
            }
        }

        return filteredRecords;
    }

    /**
     * Return all index file records that match the specified entity name, filtering by matching on the child uuids
     * 
     * @param parentRecord
     * @param childRecordType the type of the child to seek uuids
     * @param uuids to filter just the objects we want
     * @return columnRecords
     * @throws Exception
     */
    protected Collection findChildRecordsForColumns(final MetadataRecord parentRecord,
                                                    final char childRecordType,
                                                    final List uuids) throws Exception {
        IEntryResult[] results = queryIndexByParentPath(childRecordType, parentRecord.getFullName());
        Collection records = findMetadataRecords(results);

        // if uniquekey records are being returned, also return primary key records,
        // as primary keys are unique keys
        if (childRecordType == IndexConstants.RECORD_TYPE.UNIQUE_KEY) {
            Collection primarKeyRecords = findMetadataRecords(queryIndexByParentPath(IndexConstants.RECORD_TYPE.PRIMARY_KEY,
                                                                                     parentRecord.getFullName()));
            records.addAll(primarKeyRecords);
        }

        List columnRecords = new ArrayList(uuids.size());

        // filtering recods (records with differrent parents could
        // begin with same name)
        for (Iterator resultIter = records.iterator(); resultIter.hasNext();) {
            MetadataRecord record = (MetadataRecord)resultIter.next();
            if (record != null && uuids.contains(record.getUUID())) {
                columnRecords.add(record);
            }
        }
        return columnRecords;
    }

    /**
     * Return all index file records that match the specified entity name, without filtering
     * 
     * @param parentRecord
     * @param childRecordType the type of the child to seek
     * @return records
     * @throws Exception
     */
    protected Collection findChildRecordsWithoutFiltering(final MetadataRecord parentRecord,
                                                          final char childRecordType) throws Exception {
        IEntryResult[] results = queryIndexByParentPath(childRecordType, parentRecord.getFullName());
        Collection records = findMetadataRecords(results);

        // if uniquekey records are being returned, also return primary key records,
        // as primary keys are unique keys
        if (childRecordType == IndexConstants.RECORD_TYPE.UNIQUE_KEY) {
            Collection primarKeyRecords = findMetadataRecords(queryIndexByParentPath(IndexConstants.RECORD_TYPE.PRIMARY_KEY,
                                                                                     parentRecord.getFullName()));
            records.addAll(primarKeyRecords);
        }

        return records;
    }

    /**
     * Return all index file records that match the specified entity name
     * 
     * @param indexName
     * @param entityName the name to match
     * @param isPartialName true if the entity name is a partially qualified
     * @return results
     * @throws Exception
     */
    protected Collection findMetadataRecords(final IEntryResult[] results) {
        return RecordFactory.getMetadataRecord(results, null);
    }

    protected MetadataRecord findMetadataRecord(final IEntryResult result) {
        return RecordFactory.getMetadataRecord(result, null);
    }

    /**
     * Return all index file records that match the specified entity name
     * 
     * @param indexName
     * @param entityName the name to match
     * @param isPartialName true if the entity name is a partially qualified
     * @return results
     * @throws Exception 
     */
    protected Collection findMetadataRecords(final char recordType,
                                             final String entityName,
                                             final boolean isPartialName) throws Exception {

        IEntryResult[] results = queryIndex(recordType, entityName, isPartialName);
        Collection records = findMetadataRecords(results);

        if (CoreStringUtil.startsWithIgnoreCase(entityName, UUID.PROTOCOL)) {
            // Filter out ColumnRecord instances that do not match the specified uuid.
            // Due to the pattern matching used to query index files if an index record
            // matched the specified uuid string anywhere in that record it would be returned
            // in the results (for example, if the parent ObjectID in the index record
            // matched the specified uuid).
            this.filterMetadataRecordForUUID(entityName, records);
        }
        return records;
    }

    /**
     * Return all index file records that match the specified entity name
     * 
     * @param indexName
     * @param entityName the name to match
     * @param isPartialName true if the entity name is a partially qualified
     * @return results
     * @throws Exception
     */
    protected IEntryResult[] queryIndex(final char recordType,
                                        final String entityName,
                                        final boolean isPartialName) throws Exception {

        IEntryResult[] results = null;
        Index[] indexes = getIndexes(recordType, getIndexSelector());

        // Query based on UUID
        if (CoreStringUtil.startsWithIgnoreCase(entityName, UUID.PROTOCOL)) {
            String patternString = null;
            if (recordType == IndexConstants.RECORD_TYPE.DATATYPE) {
                patternString = getDatatypeUUIDMatchPattern(entityName);
            } else {
                patternString = getUUIDMatchPattern(recordType, entityName);
            }
            results = queryIndex(indexes, patternString.toCharArray(), false, true);
        }

        // Query based on partially qualified name
        else if (isPartialName) {
            String patternString = getMatchPattern(recordType, entityName);
            results = queryIndex(indexes, patternString.toCharArray(), false, false);
        }

        // Query based on fully qualified name
        else {
            String prefixString = getPrefixPattern(recordType, entityName);
            results = queryIndex(indexes, prefixString.toCharArray(), true, true);
        }

        return results;
    }

    /**
     * Return all index file records that match the specified record pattern.
     * 
     * @param indexes the array of MtkIndex instances to query
     * @param pattern
     * @return results
     * @throws Exception
     */
    protected IEntryResult[] queryIndex(final Index[] indexes,
                                        final char[] pattern,
                                        boolean isPrefix,
                                        boolean returnFirstMatch) throws Exception {
        return SimpleIndexUtil.queryIndex(indexes, pattern, isPrefix, returnFirstMatch);
    }

    /**
     * Return all index file records that match the specified record pattern.
     * 
     * @param indexes the array of MtkIndex instances to query
     * @param pattern
     * @return results
     * @throws Exception
     */
    protected IEntryResult[] queryIndex(final Index[] indexes,
                                        final char[] pattern,
                                        boolean isPrefix,
                                        boolean isCaseSensitive,
                                        boolean returnFirstMatch) throws Exception {
        return SimpleIndexUtil.queryIndex(null, indexes, pattern, isPrefix, isCaseSensitive, returnFirstMatch);
    }

    // ==================================================================================
    // P A C K A G E M E T H O D S
    // ==================================================================================

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    /**
     * Looks up procedure plan in the transformations index for a given procedure.
     */
    private String getProcedurePlan(final String procedureName) throws Exception {
        CoreArgCheck.isNotEmpty(procedureName);

        // Query the index files
        Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.PROC_TRANSFORM, procedureName, false);
        int resultSize = results.size();
        if (resultSize == 1) {
            // get the transform record for this result
            final TransformationRecord transformRecord = (TransformationRecord)results.iterator().next();
            return transformRecord.getTransformation();
        }
        // there should be only one result entry for a fully qualified name
        if (resultSize > 1) {
            throw new Exception(
                                                      TransformationPlugin.Util.getString("TransformationMetadata.Procedure_ambiguous_there_are_multiple_procedure_plans_available_for_this_name___4") + procedureName); //$NON-NLS-1$
        }

        // no transfomation available, this may not be a virtual procedure
        return null;
    }

    /**
     * Helper method to get back an array of ColumnRecords given a list of UUIDs.
     */
    private ColumnRecord[] getColumnRecordsForUUIDs(final List uuids)
        throws Exception {

        ColumnRecord[] columnRecords = new ColumnRecord[uuids.size()];

        for (int i = 0; i < uuids.size(); i++) {
            String colUUID = (String)uuids.get(i);
            columnRecords[i] = (ColumnRecord)this.getElementID(colUUID);
        }

        return columnRecords;
    }

    /**
     * Return all index file records that match the specified entity name
     * 
     * @param indexName
     * @param entityName the name to match
     * @param isPartialName true if the entity name is a partially qualified
     * @return results
     * @throws Exception
     */
    private IEntryResult[] queryIndexByParentPath(final char recordType,
                                                  final String parentFullName) throws Exception {

        // Query based on fully qualified name
        String prefixString = getParentPrefixPattern(recordType, parentFullName);

        // Query the model index files
        Index[] indexes = getIndexes(recordType, getIndexSelector());
        IEntryResult[] results = queryIndex(indexes, prefixString.toCharArray(), true, false);

        return results;
    }
    
    private IStoredProcedureInfo getStoredProcInfoDirect(final String procedureName) throws Exception {

        CoreArgCheck.isNotEmpty(procedureName);

        ProcedureRecord procRecord = null;

        IDataTypeManagerService dataTypeService = ModelerCore.getTeiidDataTypeManagerService();
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();

        // procedure full names always contain atlest 2 segments(modelname.procedureName)
        if (CoreStringUtil.startsWithIgnoreCase(procedureName, UUID.PROTOCOL)
            || CoreStringUtil.getTokens(procedureName, DELIMITER_STRING).size() >= 2) {

            final Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.CALLABLE, procedureName, false);

            int resultSize = results.size();
            if (resultSize == 1) {
                // get the columnset record for this result
                procRecord = (ProcedureRecord)results.iterator().next();
            } else if (resultSize == 0) {
                if (CoreStringUtil.startsWithIgnoreCase(procedureName, UUID.PROTOCOL)) {
                    return null;
                }
            } else {
                // there should be only one for the full name
                throw new Exception(TransformationPlugin.Util.getString("TransformationMetadata.0", procedureName)); //$NON-NLS-1$
            }
        }

        if (procRecord == null) {

            String partialName = DELIMITER_CHAR + procedureName;

            final Collection results = findMetadataRecords(IndexConstants.RECORD_TYPE.CALLABLE, partialName, true);

            int resultSize = results.size();
            if (resultSize == 1) {
                // get the columnset record for this result
                procRecord = (ProcedureRecord)results.iterator().next();
            } else if (resultSize == 0) {
                throw new Exception(procedureName + NOT_EXISTS_MESSAGE);
            } else {
                // there should be only one for the UUID
                throw new Exception(TransformationPlugin.Util.getString("TransformationMetadata.0", procedureName)); //$NON-NLS-1$
            }
        }

        String procedureFullName = procRecord.getFullName();

        // create the storedProcedure info object that would hold procedure's metadata
        IStoredProcedureInfo procInfo = factory.createStoredProcedureInfo();
        procInfo.setProcedureCallableName(procedureFullName);
        procInfo.setProcedureID(procRecord);

        // modelID for the procedure
        MetadataRecord modelRecord = (MetadataRecord)this.getModelID(procRecord);
        procInfo.setModelID(modelRecord);

        // get the parameter metadata info
        for (Iterator paramIter = procRecord.getParameterIDs().iterator(); paramIter.hasNext();) {
            String paramID = (String)paramIter.next();
            ProcedureParameterRecord paramRecord = (ProcedureParameterRecord)this.getRecordByType(paramID,
                                                                                                  IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER);
            String runtimeType = paramRecord.getRuntimeType();
            ISPParameter.ParameterInfo direction = this.convertParamRecordTypeToStoredProcedureType(paramRecord.getType());
            // create a parameter and add it to the procedure object
            ISPParameter spParam = factory.createSPParameter(paramRecord.getPosition(), direction, paramRecord.getFullName());
            spParam.setMetadataID(paramRecord);
            spParam.setClassType(dataTypeService.getDataTypeClass(runtimeType));
            procInfo.addParameter(spParam);
        }

        // if the procedure returns a resultSet, obtain resultSet metadata
        String resultID = (String)procRecord.getResultSetID();
        if (resultID != null) {
            try {
                ColumnSetRecord resultRecord = (ColumnSetRecord)this.getRecordByType(resultID,
                                                                                     IndexConstants.RECORD_TYPE.RESULT_SET);
                // resultSet is the last parameter in the procedure
                int lastParamIndex = procInfo.getParameters().size() + 1;

                ISPParameter param = factory.createSPParameter(lastParamIndex,
                                                               ISPParameter.ParameterInfo.RESULT_SET,
                                                               resultRecord.getFullName());
                param.setClassType(java.sql.ResultSet.class);
                param.setMetadataID(resultRecord);

                ColumnRecord[] columnRecords = getColumnRecordsForUUIDs(resultRecord.getColumnIDs());

                for (int i = 0; i < columnRecords.length; i++) {
                    String colType = columnRecords[i].getRuntimeType();
                    param.addResultSetColumn(columnRecords[i].getFullName(),
                                             dataTypeService.getDataTypeClass(colType),
                                             columnRecords[i]);
                }

                procInfo.addParameter(param);
            } catch (Exception e) {
                // it is ok to fail here. it will happen when a
                // virtual stored procedure is created from a
                // physical stored procedrue without a result set
                // TODO: find a better fix for this
            }
        }

        // if this is a virtual procedure get the procedure plan
        if (procRecord.isVirtual()) {
            String procedurePlan = getProcedurePlan(procedureFullName);
            if (procedurePlan != null) {
                IQueryNode queryNode = factory.createQueryNode(procedurePlan);
                procInfo.setQueryPlan(queryNode);
            }
        }

        // subtract 1, to match up with the server
        procInfo.setUpdateCount(procRecord.getUpdateCount() - 1);

        return procInfo;
    }

    /***
     * Methods added below to support implementation in teiid runtime client
     */

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
        return this;
    }

    @Override
    public IQueryMetadataInterface getSessionMetadata() {
        return null;
    }

    @Override
    public Set getImportedModels() {
        return Collections.emptySet();
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