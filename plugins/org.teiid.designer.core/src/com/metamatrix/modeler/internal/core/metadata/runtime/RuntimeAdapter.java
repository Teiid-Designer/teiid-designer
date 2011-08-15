/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.core.id.ObjectID;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.internal.core.index.WordEntry;
import com.metamatrix.metadata.runtime.impl.RecordFactory;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlForeignKeyAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationInfo;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlUniqueKeyAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;

/**
 * RuntimeAdapter
 */
public class RuntimeAdapter extends RecordFactory {

    private static HashMap metaClassUriMap = new HashMap();

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * Create the {@link com.metamatrix.internal.core.index.impl.WordEntry} instance(s) to be used as the index file record(s) for
     * this SqlAspect instance. The word entries are added to the list provided by the calling method.
     * 
     * @param sqlAspect
     * @param modelPath
     * @param wordEntries the list to which WordEntry instances are added
     * @param addAllWords boolean indicating if certain types of indexes can be skipped based on type of indexer call this.
     */
    public static void addIndexWord( final Object eObject,
                                     final IndexingContext context,
                                     final String modelPath,
                                     final Collection wordEntries ) {
        addIndexWord(eObject, context, modelPath, wordEntries, true);
    }

    /**
     * Create the {@link com.metamatrix.internal.core.index.impl.WordEntry} instance(s) to be used as the index file record. The
     * word entries are added to the list provided by the calling method.
     * 
     * @param filePath The path to the file in the vdb
     * @param wordEntries the list to which WordEntry instances are added
     * @param addAllWords boolean indicating if certain types of indexes can be skipped based on type of indexer call this.
     */
    public static void addFileIndexWord( final String filePath,
                                         final Collection wordEntries ) {
        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(50);

        // append record header
        sb.append(IndexConstants.RECORD_TYPE.FILE);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the max set size
        sb.append(filePath);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        WordEntry wordEntry = new WordEntry(sb.toString().toCharArray());
        wordEntries.add(wordEntry);
    }

    /**
     * Create the {@link com.metamatrix.internal.core.index.impl.WordEntry} instance(s) to be used as the index file record(s) for
     * this SqlAspect instance. The word entries are added to the list provided by the calling method.
     * 
     * @param sqlAspect
     * @param modelPath
     * @param wordEntries the list to which WordEntry instances are added
     * @param addAllWords boolean indicating if certain types of indexes can be skipped based on type of indexer call this.
     */
    public static void addIndexWord( final Object eObject,
                                     final IndexingContext context,
                                     final String modelPath,
                                     final Collection wordEntries,
                                     final boolean addAllWords ) {
        CoreArgCheck.isInstanceOf(EObject.class, eObject);
        SqlAspect sqlAspect = AspectManager.getSqlAspect((EObject)eObject);
        if (sqlAspect == null || !sqlAspect.isQueryable((EObject)eObject)) {
            return;
        }

        if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.COLUMN)) {
            addColumnWord((SqlColumnAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
        }
        if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.TABLE)) {
            addTableWord((SqlTableAspect)sqlAspect, (EObject)eObject, context, modelPath, wordEntries);
        }
        if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.DATATYPE)) {
            addDatatypeWord((SqlDatatypeAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
        }
        if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.CALLABLE)) {
            addCallableWord((SqlProcedureAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
        }
        if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER)) {
            addCallableParameterWord((SqlProcedureParameterAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
        }
        if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.MODEL)) {
            addModelWord((SqlModelAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
        }

        // these words are not needed for modeler indexing.
        if (addAllWords) {
            if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.UNIQUE_KEY)
                || sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.PRIMARY_KEY)) {
                addUniqueKeyWord((SqlUniqueKeyAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
            }
            if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.INDEX)
                || sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.ACCESS_PATTERN)
                || sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.RESULT_SET)) {
                addColumnSetWord((SqlColumnSetAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
            }
            if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.FOREIGN_KEY)) {
                addForeignKeyWord((SqlForeignKeyAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
            }
            if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.SELECT_TRANSFORM)
                || sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.INSERT_TRANSFORM)
                || sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM)
                || sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.DELETE_TRANSFORM)
                || sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.PROC_TRANSFORM)
                || sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM)) {
                addTransformationWords((SqlTransformationAspect)sqlAspect, (EObject)eObject, context, modelPath, wordEntries);
            }
            if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.VDB_ARCHIVE)) {
                addVdbWord((SqlVdbAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
            }
            if (sqlAspect.isRecordType(IndexConstants.RECORD_TYPE.ANNOTATION)) {
                addAnnotationWord((SqlAnnotationAspect)sqlAspect, (EObject)eObject, modelPath, wordEntries);
            }
            // add an extension property word for each EObject
            addPropertyWord(sqlAspect, (EObject)eObject, modelPath, wordEntries);
        }

    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a model. This resulting WordEntry
     * is of the form: header|maxSetSize|boolean values|footer|
     */
    public static void addModelWord( final SqlModelAspect aspect,
                                     final EObject eObject,
                                     final String modelPath,
                                     final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        // final EObject container = (eObject != null ? eObject.eContainer() : null);
        final String parentObjectID = null; // getObjectIdString(container);
        // The path returned from the aspect will be of the form Model/ModelAnnotation. We
        // only want the first segment for the model path.
        String fullName = aspect.getName(eObject);

        addModelWord(objectID,
                     fullName,
                     aspect.getNameInSource(eObject),
                     parentObjectID,
                     aspect.getMaxSetSize(eObject),
                     aspect.getModelType(eObject),
                     aspect.isVisible(eObject),
                     aspect.supportsDistinct(eObject),
                     aspect.supportsJoin(eObject),
                     aspect.supportsOrderBy(eObject),
                     aspect.supportsOuterJoin(eObject),
                     aspect.supportsWhereAll(eObject),
                     aspect.getPrimaryMetamodelUri(eObject),
                     modelPath,
                     wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a model. This resulting WordEntry
     * is of the form: header|maxSetSize|modelType|primaryMetamodelUri|boolean values|footer|
     */
    static void addModelWord( final String objectID,
                              final String fullName,
                              final String nameInSource,
                              final String parentObjectID,
                              final int maxSetSize,
                              final int modelType,
                              final boolean isVisible,
                              final boolean supportsDistinct,
                              final boolean supportsJoin,
                              final boolean supportsOrderBy,
                              final boolean supportsOuterJoin,
                              final boolean supportsWhereAll,
                              final String primaryMetamodelUri,
                              final String modelPath,
                              final Collection wordEntries ) {

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        appendWordHeader(IndexConstants.RECORD_TYPE.MODEL, objectID, fullName, nameInSource, parentObjectID, sb);

        // Append the max set size
        sb.append(maxSetSize);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the model type
        sb.append(modelType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the primary metamodel Uri
        sb.append(primaryMetamodelUri);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the supports flags
        appendBoolean(isVisible, sb);
        appendBoolean(supportsDistinct, sb);
        appendBoolean(supportsJoin, sb);
        appendBoolean(supportsOrderBy, sb);
        appendBoolean(supportsOuterJoin, sb);
        appendBoolean(supportsWhereAll, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, fullName, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a VDB archive. This resulting
     * WordEntry is of the form: header|version|identifer|producerName|producerVersion|provider|timeLastChanged|
     * timeLastProduced|modelIDs|description|footer|
     */
    public static void addVdbWord( final SqlVdbAspect aspect,
                                   final EObject eObject,
                                   final String modelPath,
                                   final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        final String parentObjectID = null; // getObjectIdString(container);
        final String fullName = aspect.getFullName(eObject);
        final String name = aspect.getName(eObject);

        addVdbWord(objectID,
                   fullName,
                   aspect.getNameInSource(eObject),
                   parentObjectID,
                   aspect.getVersion(eObject),
                   aspect.getIdentifier(eObject),
                   aspect.getDescription(eObject),
                   aspect.getProducerName(eObject),
                   aspect.getProducerVersion(eObject),
                   aspect.getProvider(eObject),
                   aspect.getTimeLastChanged(eObject),
                   aspect.getTimeLastProduced(eObject),
                   aspect.getModelIDs(eObject),
                   modelPath,
                   name,
                   wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a VDB archive. This resulting
     * WordEntry is of the form: header|version|identifer|producerName|producerVersion|provider|timeLastChanged|
     * timeLastProduced|modelIDs|description|footer|
     */
    static void addVdbWord( final String objectID,
                            final String fullName,
                            final String nameInSource,
                            final String parentObjectID,
                            final String version,
                            final String identifier,
                            final String description,
                            final String producerName,
                            final String producerVersion,
                            final String provider,
                            final String timeLastChanged,
                            final String timeLastProduced,
                            final List modelIDs,
                            final String modelPath,
                            final String name,
                            final Collection wordEntries ) {

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        appendWordHeader(IndexConstants.RECORD_TYPE.VDB_ARCHIVE, objectID, fullName, nameInSource, parentObjectID, sb);

        // Append the version
        appendObject(version, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the identifier
        appendObject(identifier, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the producerName
        appendObject(producerName, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the producerVersion
        appendObject(producerVersion, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the provider
        appendObject(provider, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the timeLastChanged
        appendObject(timeLastChanged, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the timeLastProduced
        appendObject(timeLastProduced, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the modelIDs
        appendIDs(modelIDs, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the description
        appendObject(description, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a transformation. This resulting
     * WordEntry is of the form: recordType|upperFullName|objectID|fullName|nameInSource|parentObjectID|
     * transformationType|transformedObjectID|transformation|bindingNames|schemaPaths|footer|
     */
    public static void addTransformationWords( final SqlTransformationAspect aspect,
                                               final EObject eObject,
                                               final IndexingContext context,
                                               final String modelPath,
                                               final Collection wordEntries ) {

        final EObject virtualTable = (EObject)aspect.getTransformedObject(eObject);
        if (virtualTable == null) {
            return;
        }
        if (context != null) {
            if (context.hasTransformation(virtualTable)) {
                return;
            }
            context.addTargetTransform(virtualTable, eObject);
        }

        final String upperName = aspect.getFullName(eObject).toUpperCase();
        final String transformedObjectID = getObjectIdString(virtualTable);
        final String transformationObjectID = getObjectIdString(eObject);
        final String name = aspect.getName(eObject);

        String[] types = aspect.getTransformationTypes(eObject);

        if (types != null) {
            for (int i = 0; i < types.length; i++) {
                // do not add words for transformations that are not allowed
                if (types[i].equals(SqlTransformationAspect.Types.INSERT) && !aspect.isInsertAllowed(eObject)) {
                    continue;
                }
                if (types[i].equals(SqlTransformationAspect.Types.UPDATE) && !aspect.isUpdateAllowed(eObject)) {
                    continue;
                }
                if (types[i].equals(SqlTransformationAspect.Types.DELETE) && !aspect.isDeleteAllowed(eObject)) {
                    continue;
                }
                SqlTransformationInfo transInfo = aspect.getTransformationInfo(eObject, context, types[i]);
                if (transInfo != null && !CoreStringUtil.isEmpty(transInfo.getSqlTransform())) {
                    String sqlTransform = transInfo.getSqlTransform();
                    List bindingNames = transInfo.getBindings();
                    List schemaPaths = transInfo.getSchemaPaths();
                    addTransformationWord(upperName,
                                          types[i],
                                          transformationObjectID,
                                          transformedObjectID,
                                          sqlTransform,
                                          bindingNames,
                                          schemaPaths,
                                          modelPath,
                                          name,
                                          wordEntries);
                }
            }
        }
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a transformation. This resulting
     * WordEntry is of the form: recordType|upperFullName|objectID|fullName|nameInSource|parentObjectID|
     * transformationType|transformedObjectID|transformation|bindingNames|schemaPaths|footer|
     */
    static void addTransformationWord( final String upperName,
                                       final String tranformType,
                                       final String transformationObjectID,
                                       final String transformedObjectID,
                                       final String transformation,
                                       final List bindings,
                                       final List schemaPaths,
                                       final String modelPath,
                                       final String name,
                                       final Collection wordEntries ) {

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

        // Append the record type
        if (tranformType.equals(SqlTransformationAspect.Types.SELECT)) {
            sb.append(IndexConstants.RECORD_TYPE.SELECT_TRANSFORM);
        } else if (tranformType.equals(SqlTransformationAspect.Types.INSERT)) {
            sb.append(IndexConstants.RECORD_TYPE.INSERT_TRANSFORM);
        } else if (tranformType.equals(SqlTransformationAspect.Types.UPDATE)) {
            sb.append(IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM);
        } else if (tranformType.equals(SqlTransformationAspect.Types.DELETE)) {
            sb.append(IndexConstants.RECORD_TYPE.DELETE_TRANSFORM);
        } else if (tranformType.equals(SqlTransformationAspect.Types.PROCEDURE)) {
            sb.append(IndexConstants.RECORD_TYPE.PROC_TRANSFORM);
        } else if (tranformType.equals(SqlTransformationAspect.Types.MAPPING)) {
            sb.append(IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM);
        }

        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        // append fully qualified name of the virtual group
        appendObject(upperName, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        // Append the transformed object ID
        sb.append(transformedObjectID);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        // Append the transformed object ID
        sb.append(transformationObjectID);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        // append the sql transformation
        appendObject(transformation, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        // append the binding Names
        appendStrings(bindings, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        // append the schemaPaths
        appendStrings(schemaPaths, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(transformedObjectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a model. This resulting WordEntry
     * is of the form: header|isFunction|parameterIDs|resultSetID|footer|
     */
    public static void addCallableWord( final SqlProcedureAspect aspect,
                                        final EObject eObject,
                                        final String modelPath,
                                        final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        // final EObject container = (eObject != null ? eObject.eContainer() : null);
        final String parentObjectID = null; // getObjectIdString(container);
        final String fullName = aspect.getFullName(eObject);
        final String name = aspect.getName(eObject);

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        appendWordHeader(IndexConstants.RECORD_TYPE.CALLABLE,
                         objectID,
                         fullName,
                         aspect.getNameInSource(eObject),
                         parentObjectID,
                         sb);

        // append booleans
        // append boolean indicating if the procedure is a function
        appendBoolean(aspect.isFunction(eObject), sb);
        // append boolean indicating if the procedure is virtual
        appendBoolean(aspect.isVirtual(eObject), sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUIDs of the parameter references
        appendIDs(aspect.getParameters(eObject), sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUID of the resultSet
        appendID(aspect.getResult(eObject), sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append procedure update count
        sb.append(aspect.getUpdateCount(eObject));
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a model. This resulting WordEntry
     * is of the form: header|defaultValue|dataType|length|radix|scale|nullable|precision|position|paramType|isOptional|footer|
     */
    public static void addCallableParameterWord( final SqlProcedureParameterAspect aspect,
                                                 final EObject eObject,
                                                 final String modelPath,
                                                 final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        final String parentObjectID = getObjectIdString(aspect.getParentObjectID(eObject));
        // final EObject container = (eObject != null ? eObject.eContainer() : null);
        // final String parentObjectID = getObjectIdString(container);
        final String fullName = aspect.getFullName(eObject);
        final String name = aspect.getName(eObject);

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        appendWordHeader(IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER,
                         objectID,
                         fullName,
                         aspect.getNameInSource(eObject),
                         parentObjectID,
                         sb);

        // Append the defaultvalue of the parameter
        appendObject(aspect.getDefaultValue(eObject), sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the datatye of the parameter
        appendObject(aspect.getRuntimeType(eObject), sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the datatye uuid of the parameter
        appendObject(aspect.getDatatypeObjectID(eObject), sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the length
        sb.append(aspect.getLength(eObject));
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the radix
        sb.append(aspect.getRadix(eObject));
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the scale
        sb.append(aspect.getScale(eObject));
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the nullability
        sb.append(aspect.getNullType(eObject));
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the precision
        sb.append(aspect.getPrecision(eObject));
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the position
        sb.append(aspect.getPosition(eObject));
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the parameter type
        sb.append(aspect.getType(eObject));
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the parameter isOptional value
        appendBoolean(aspect.isOptional(eObject), sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a table. This resulting WordEntry
     * is of the form: header|cardinality|boolean
     * values|columnIDs|primaryKeyID|foreignKeyIDs|indexIDs|uniqueKeyIDs|accessPatternIDs|materializedTableIDs|footer|
     */
    public static void addTableWord( final SqlTableAspect aspect,
                                     final EObject eObject,
                                     final IndexingContext context,
                                     final String modelPath,
                                     final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        final String name = aspect.getName(eObject);
        final String parentObjectID = null; // getObjectIdString(container);
        final String fullName = aspect.getFullName(eObject);
        final String primaryKeyID = getObjectIdString(aspect.getPrimaryKey(eObject));
        String materializedTableID = null;
        final boolean isMaterialized = aspect.isMaterialized(eObject);

        // Append the UUIDs of the materialized table references
        if (isMaterialized ) {
        	// Check for an annotation for this EObject
        	materializedTableID = aspect.getMaterializedTableId(eObject);
        }

        addTableWord(objectID,
                     fullName,
                     aspect.getNameInSource(eObject),
                     parentObjectID,
                     aspect.getCardinality(eObject),
                     aspect.getTableType(eObject),
                     aspect.isVirtual(eObject),
                     aspect.isSystem(eObject),
                     isMaterialized,
                     aspect.supportsUpdate(eObject),
                     primaryKeyID,
                     aspect.getColumns(eObject),
                     aspect.getForeignKeys(eObject),
                     aspect.getIndexes(eObject),
                     aspect.getUniqueKeys(eObject),
                     aspect.getAccessPatterns(eObject),
                     materializedTableID,
                     modelPath,
                     name,
                     wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a table. This resulting WordEntry
     * is of the form: header|cardinality|boolean
     * values|columnIDs|primaryKeyID|foreignKeyIDs|indexIDs|uniqueKeyIDs|accessPatternIDs
     * |materializedTableID|materializedStageTableID|footer|
     */
    static void addTableWord( final String objectID,
                              final String fullName,
                              final String nameInSource,
                              final String parentObjectID,
                              final int cardinality,
                              final int tableType,
                              final boolean isVirtual,
                              final boolean isSystem,
                              final boolean isMaterialized,
                              final boolean supportsUpdate,
                              final String primaryKeyID,
                              final List columnIDs,
                              final Collection foreignKeyIDs,
                              final Collection indexIDs,
                              final Collection uniqueKeyIDs,
                              final Collection accessPatternIDs,
                              final String materializedTableID,
                              final String modelPath,
                              final String name,
                              final Collection wordEntries ) {

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        appendWordHeader(IndexConstants.RECORD_TYPE.TABLE, objectID, fullName, nameInSource, parentObjectID, sb);

        // Append the cardinality
        sb.append(cardinality);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the tableType
        sb.append(tableType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the supports flags
        appendBoolean(isVirtual, sb);
        appendBoolean(isSystem, sb);
        appendBoolean(supportsUpdate, sb);
        appendBoolean(isMaterialized, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUIDs of the column references
        appendIDs(Collections.EMPTY_LIST, sb);
        // DFF 01/06/04 - Deprecated the TableRecord.getColumnIDs method as a
        // partial fix for defect 10861. Storing the list of column UUIDs may
        // create an index record longer than the allowable INDEX_RECORD_BLOCK_SIZE.
        // Retrieving the list of columns owned by table can be achieved by
        // querying for columns with the fully qualified table as the parent name.
        // appendIDs(columnIDs,sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUID of the primary key
        appendID(primaryKeyID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUIDs of the foreign key references
        appendIDs(foreignKeyIDs, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUIDs of the index references
        appendIDs(indexIDs, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUIDs of the unique key references
        appendIDs(uniqueKeyIDs, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUIDs of the access pattern references
        appendIDs(accessPatternIDs, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUIDs of the materialized table reference
        appendID(materializedTableID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUIDs of the materialized stage table reference
        appendID(null, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a column. This resulting WordEntry
     * is of the form: header|boolean values|nullType|searchType|
     * length|scale|precision|charOctetLength|radix|distinctValues|nullValues
     * |minValue|maxValue|format|datatype|defaultValue|footer|
     */
    public static void addColumnWord( final SqlColumnAspect aspect,
                                      final EObject eObject,
                                      final String modelPath,
                                      final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        final String parentObjectID = getObjectIdString(aspect.getParentObjectID(eObject));
        final String name = aspect.getName(eObject);
        final String fullName = aspect.getFullName(eObject);
        final String minValue = (aspect.getMinValue(eObject) != null ? aspect.getMinValue(eObject).toString() : null);
        final String maxValue = (aspect.getMaxValue(eObject) != null ? aspect.getMaxValue(eObject).toString() : null);
        final String defaultValue = (aspect.getDefaultValue(eObject) != null ? aspect.getDefaultValue(eObject).toString() : null);

        addColumnWord(objectID,
                      fullName,
                      aspect.getNameInSource(eObject),
                      parentObjectID,
                      aspect.isSelectable(eObject),
                      aspect.isUpdatable(eObject),
                      aspect.getNullType(eObject),
                      aspect.isAutoIncrementable(eObject),
                      aspect.isCaseSensitive(eObject),
                      aspect.isSigned(eObject),
                      aspect.isCurrency(eObject),
                      aspect.isFixedLength(eObject),
                      aspect.isTranformationInputParameter(eObject),
                      aspect.getSearchType(eObject),
                      aspect.getLength(eObject),
                      aspect.getScale(eObject),
                      aspect.getPrecision(eObject),
                      aspect.getCharOctetLength(eObject),
                      aspect.getRadix(eObject),
                      aspect.getDistinctValues(eObject),
                      aspect.getNullValues(eObject),
                      minValue,
                      maxValue,
                      aspect.getFormat(eObject),
                      aspect.getRuntimeType(eObject),
                      aspect.getNativeType(eObject),
                      aspect.getDatatypeObjectID(eObject),
                      defaultValue,
                      aspect.getPosition(eObject),
                      modelPath,
                      name,
                      wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a column. This resulting WordEntry
     * is of the form: header|boolean values|nullType|searchType|
     * length|scale|precision|charOctetLength|radix|distinctValues|nullValues
     * |minValue|maxValue|format|datatype|nativeType|defaultValue|footer|
     */
    static void addColumnWord( final String objectID,
                               final String fullName,
                               final String nameInSource,
                               final String parentObjectID,
                               final boolean isSelectable,
                               final boolean isUpdatable,
                               final int nullType,
                               final boolean isAutoIncrementable,
                               final boolean isCaseSensitive,
                               final boolean isSigned,
                               final boolean isCurrency,
                               final boolean isFixedLength,
                               final boolean isTranformationInputParameter,
                               final int searchType,
                               final int length,
                               final int scale,
                               final int precision,
                               final int charOctetLength,
                               final int radix,
                               final int distinctValues,
                               final int nullValues,
                               final String minValue,
                               final String maxValue,
                               final String format,
                               final String runtimeType,
                               final String nativeType,
                               final String datatypeObjectID,
                               final String defaultValue,
                               final int position,
                               final String modelPath,
                               final String name,
                               final Collection wordEntries ) {

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        appendWordHeader(IndexConstants.RECORD_TYPE.COLUMN, objectID, fullName, nameInSource, parentObjectID, sb);

        // Append the supports flags
        appendBoolean(isSelectable, sb);
        appendBoolean(isUpdatable, sb);
        appendBoolean(isAutoIncrementable, sb);
        appendBoolean(isCaseSensitive, sb);
        appendBoolean(isSigned, sb);
        appendBoolean(isCurrency, sb);
        appendBoolean(isFixedLength, sb);
        appendBoolean(isTranformationInputParameter, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the nullType
        sb.append(nullType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the searchType
        sb.append(searchType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the length
        sb.append(length);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the scale
        sb.append(scale);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the precision
        sb.append(precision);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the position
        sb.append(position);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the charOctetLength
        sb.append(charOctetLength);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the radix
        sb.append(radix);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the distinctValues
        sb.append(distinctValues);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the nullValues
        sb.append(nullValues);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the minValue
        appendObject(minValue, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the maxValue
        appendObject(maxValue, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the format
        appendObject(format, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the runtime type
        appendObject(runtimeType, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the native type
        appendObject(nativeType, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the ObjectID of the datatype
        appendObject(datatypeObjectID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the default value
        appendObject(defaultValue, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a index, access pattern or
     * resultSet. This resulting WordEntry is of the form: header|columnIDs|footer|
     */
    public static void addColumnSetWord( final SqlColumnSetAspect aspect,
                                         final EObject eObject,
                                         final String modelPath,
                                         final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        final String parentObjectID = getObjectIdString(aspect.getParentObjectID(eObject));
        // final EObject container = (eObject != null ? eObject.eContainer() : null);
        // final String parentObjectID = getObjectIdString(container);
        final String fullName = aspect.getFullName(eObject);
        final String name = aspect.getName(eObject);

        char recordType = IndexConstants.RECORD_TYPE.INDEX;
        if (aspect.isRecordType(IndexConstants.RECORD_TYPE.ACCESS_PATTERN)) {
            recordType = IndexConstants.RECORD_TYPE.ACCESS_PATTERN;
        }
        if (aspect.isRecordType(IndexConstants.RECORD_TYPE.RESULT_SET)) {
            recordType = IndexConstants.RECORD_TYPE.RESULT_SET;
        }

        addColumnSetWord(recordType,
                         objectID,
                         fullName,
                         aspect.getNameInSource(eObject),
                         parentObjectID,
                         aspect.getColumns(eObject),
                         modelPath,
                         name,
                         wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a primaryKey, uniqueKey, index, or
     * access pattern. This resulting WordEntry is of the form: header|columnIDs|footer|
     */
    static void addColumnSetWord( final char recordType,
                                  final String objectID,
                                  final String fullName,
                                  final String nameInSource,
                                  final String parentObjectID,
                                  final List columnIDs,
                                  final String modelPath,
                                  final String name,
                                  final Collection wordEntries ) {

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        appendWordHeader(recordType, objectID, fullName, nameInSource, parentObjectID, sb);

        // Append the UUIDs of the column references
        appendIDs(columnIDs, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a uniqueKey. This resulting
     * WordEntry is of the form: header|columnIDs|foreignKeyIDs|footer|
     */
    public static void addUniqueKeyWord( final SqlUniqueKeyAspect aspect,
                                         final EObject eObject,
                                         final String modelPath,
                                         final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        final String parentObjectID = getObjectIdString(aspect.getParentObjectID(eObject));
        final String name = aspect.getName(eObject);
        final String fullName = aspect.getFullName(eObject);
        char recordType = IndexConstants.RECORD_TYPE.PRIMARY_KEY;
        if (aspect.isRecordType(IndexConstants.RECORD_TYPE.UNIQUE_KEY)) {
            recordType = IndexConstants.RECORD_TYPE.UNIQUE_KEY;
        }
        addUniqueKeyWord(recordType,
                         objectID,
                         fullName,
                         aspect.getNameInSource(eObject),
                         parentObjectID,
                         aspect.getColumns(eObject),
                         aspect.getForeignKeys(eObject),
                         modelPath,
                         name,
                         wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a uniqueKey. This resulting
     * WordEntry is of the form: header|columnIDs|foreignKeyIDs|footer|
     */
    static void addUniqueKeyWord( final char recordType,
                                  final String objectID,
                                  final String fullName,
                                  final String nameInSource,
                                  final String parentObjectID,
                                  final List columnIDs,
                                  final List foreignKeyIDs,
                                  final String modelPath,
                                  final String name,
                                  final Collection wordEntries ) {

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());

        appendWordHeader(recordType, objectID, fullName, nameInSource, parentObjectID, sb);

        // Append the UUIDs of the column references
        appendIDs(columnIDs, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUID of the unique key
        appendIDs(foreignKeyIDs, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a foreignKey. This resulting
     * WordEntry is of the form: header|columnIDs|uniqueKeyID|footer|
     */
    public static void addForeignKeyWord( final SqlForeignKeyAspect aspect,
                                          final EObject eObject,
                                          final String modelPath,
                                          final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        final String parentObjectID = getObjectIdString(aspect.getParentObjectID(eObject));
        final String name = aspect.getName(eObject);
        final String fullName = aspect.getFullName(eObject);
        final String uniqueKeyID = (aspect.getUniqueKey(eObject) != null ? getObjectIdString(aspect.getUniqueKey(eObject)) : null);

        addForeignKeyWord(objectID,
                          fullName,
                          aspect.getNameInSource(eObject),
                          parentObjectID,
                          aspect.getColumns(eObject),
                          uniqueKeyID,
                          modelPath,
                          name,
                          wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a foreignKey. This resulting
     * WordEntry is of the form: header|columnIDs|uniqueKeyID|footer|
     */
    static void addForeignKeyWord( final String objectID,
                                   final String fullName,
                                   final String nameInSource,
                                   final String parentObjectID,
                                   final List columnIDs,
                                   final String uniqueKeyID,
                                   final String modelPath,
                                   final String name,
                                   final Collection wordEntries ) {

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        appendWordHeader(IndexConstants.RECORD_TYPE.FOREIGN_KEY, objectID, fullName, nameInSource, parentObjectID, sb);

        // Append the UUIDs of the column references
        appendIDs(columnIDs, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the UUID of the unique key
        appendObject(uniqueKeyID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a datatype. This resulting
     * WordEntry is of the form: recordType|datatypeID|basetypeID|fullName|objectID|nameInSource|varietyType|varietyProps|
     * runtimeTypeName|javaClassName|type|searchType|nullType|booleanValues|length|precisionLength|
     * scale|radix|primitiveTypeID|footer|
     */
    public static void addDatatypeWord( final SqlDatatypeAspect aspect,
                                        final EObject eObject,
                                        final String modelPath,
                                        final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        final EObject container = (eObject != null ? eObject.eContainer() : null);
        final String parentObjectID = getObjectIdString(container);
        final String fullName = aspect.getFullName(eObject);
        final String name = aspect.getName(eObject);

        // System.out.println(aspect.getDatatypeID(eObject)+", "+aspect.getBasetypeID(eObject)+", "+aspect.getPrimitiveTypeID(eObject));

        addDatatypeWord(objectID,
                        fullName,
                        aspect.getNameInSource(eObject),
                        parentObjectID,
                        aspect.getLength(eObject),
                        aspect.getPrecisionLength(eObject),
                        aspect.getScale(eObject),
                        aspect.getRadix(eObject),
                        aspect.isSigned(eObject),
                        aspect.isAutoIncrement(eObject),
                        aspect.isCaseSensitive(eObject),
                        aspect.getType(eObject),
                        aspect.getSearchType(eObject),
                        aspect.getNullType(eObject),
                        aspect.getJavaClassName(eObject),
                        aspect.getRuntimeTypeName(eObject),
                        aspect.getDatatypeID(eObject),
                        aspect.getBasetypeID(eObject),
                        aspect.getPrimitiveTypeID(eObject),
                        aspect.getVarietyType(eObject),
                        aspect.getVarietyProps(eObject),
                        modelPath,
                        name,
                        wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a datatype. This resulting
     * WordEntry is of the form: recordType|datatypeID|basetypeID|fullName|objectID|nameInSource|varietyType|varietyProps|
     * runtimeTypeName|javaClassName|type|searchType|nullType|booleanValues|length|precisionLength|
     * scale|radix|primitiveTypeID|footer|
     */
    static void addDatatypeWord( final String objectID,
                                 final String fullName,
                                 final String nameInSource,
                                 final String parentObjectID,
                                 final int length,
                                 final int precisionLength,
                                 final int scale,
                                 final int radix,
                                 final boolean isSigned,
                                 final boolean isAutoIncrement,
                                 final boolean isCaseSensitive,
                                 final short type,
                                 final short searchType,
                                 final short nullType,
                                 final String javaClassName,
                                 final String runtimeTypeName,
                                 final String datatypeID,
                                 final String baseTypeID,
                                 final String primitiveTypeID,
                                 final short varietyType,
                                 final List varietyProps,
                                 final String modelPath,
                                 final String name,
                                 final Collection wordEntries ) {

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        sb.append(IndexConstants.RECORD_TYPE.DATATYPE);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the datatype and basetype identifiers
        appendObject(datatypeID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(baseTypeID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the fullName/objectID/nameInSource
        appendObject(fullName, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(objectID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(nameInSource, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the variety type and its properties
        sb.append(varietyType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendIDs(varietyProps, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the runtime type and java class names
        appendObject(runtimeTypeName, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(javaClassName, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the datatype type
        sb.append(type);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the search type
        sb.append(searchType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the null type
        sb.append(nullType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the boolean flags
        appendBoolean(isSigned, sb);
        appendBoolean(isAutoIncrement, sb);
        appendBoolean(isCaseSensitive, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the length
        sb.append(length);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the precision length
        sb.append(precisionLength);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the scale
        sb.append(scale);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the radix
        sb.append(radix);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the primitive type identifier
        appendObject(primitiveTypeID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a annotation. This resulting
     * WordEntry is of the form: recordType|objectID|propertyName|value|isExtention|footer|
     */
    public static void addPropertyWord( final SqlAspect sqlAspect,
                                        final EObject eObject,
                                        final String modelPath,
                                        final Collection wordEntries ) {

        EObject extObject = null;
        try {
            ModelEditor editor = ModelerCore.getModelEditor();
            extObject = editor.getExtension(eObject);
        } catch (Exception e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        Collection propertyNames = new LinkedList();
        String objectID = getObjectIdString(eObject);
        String name = sqlAspect.getName(eObject);
        if (extObject != null) {
            final EClass eClass = extObject.eClass();
            for (final Iterator featureIter = eClass.getEAttributes().iterator(); featureIter.hasNext();) {
                final EStructuralFeature feature = (EStructuralFeature)featureIter.next();
                Object key = feature.getName();
                Object value = extObject.eGet(feature);

                if (key == null || value == null) {
                    continue;
                }

                String propName = key.toString();
                String propValue = value.toString();

                if (CoreStringUtil.isEmpty(propName) || CoreStringUtil.isEmpty(propValue)) {
                    continue;
                }

                if (feature.isMany()) {
                    EList valueList = (EList)value;
                    if (valueList.isEmpty()) {
                        continue;
                    }
                    for (final Iterator valueIter = eClass.getEAttributes().iterator(); valueIter.hasNext();) {
                        value = valueIter.next();
                        if (value != null && CoreStringUtil.isEmpty(value.toString())) {
                            // add property word
                            addPropertyWord(objectID, name, propName, value.toString(), true, modelPath, wordEntries);
                        }
                    }
                } else {
                    // add property word
                    addPropertyWord(objectID, name, propName, propValue, true, modelPath, wordEntries);
                }
                // collect properties added
                propertyNames.add(propName);
            }
        }
         
        // Now we look for extension properties
        ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        String metaclassName = eObject.getClass().getName();

        for (ModelExtensionAssistant assistant : registry.getModelExtensionAssistants(metaclassName)) {
            try {
                // gets the current value and if missing in EObject the default property value
                Properties extensionProperties = assistant.getPropertyValues(eObject);

                for (String propName : extensionProperties.stringPropertyNames()) {
                    ModelExtensionPropertyDefinition propDefn = registry.getPropertyDefinition(metaclassName, propName);

                    // make sure the property should be indexed
                    if (!propDefn.shouldBeIndexed()) {
                        continue;
                    }

                    String propValue = extensionProperties.getProperty(propName);

                    if (CoreStringUtil.isEmpty(propValue)) {
                        continue;
                    }

                    if (!propertyNames.contains(propName)) {
                        addPropertyWord(objectID, name, propName, propValue, true, modelPath, wordEntries);
                        propertyNames.add(propName);
                    }
                }
            } catch (Exception e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a annotation. This resulting
     * WordEntry is of the form: recordType|objectID|propertyName|value|isExtention|footer|
     */
    public static void addPropertyWord( final String objectID,
                                        final String name,
                                        final String propName,
                                        final String propValue,
                                        final boolean isExtention,
                                        final String modelPath,
                                        final Collection wordEntries ) {

        if (CoreStringUtil.isEmpty(propName) || CoreStringUtil.isEmpty(propValue)) {
            return;
        }

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(30);

        // Append the property type
        sb.append(IndexConstants.RECORD_TYPE.PROPERTY);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the objectID to the record
        appendObject(objectID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the property key
        appendObject(propName, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the property value
        appendObject(propValue, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the is extention value
        appendBoolean(isExtention, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a annotation. This resulting
     * WordEntry is of the form: header|description|footer|
     */
    public static void addAnnotationWord( final SqlAnnotationAspect aspect,
                                          final EObject eObject,
                                          final String modelPath,
                                          final Collection wordEntries ) {

        final String objectID = getObjectIdString(aspect.getObjectID(eObject));
        final String parentObjectID = null; // getObjectIdString(aspect.getParentObjectID(eObject));
        final String fullName = aspect.getFullName(eObject);
        final String name = aspect.getName(eObject);

        // If the target of the annotation does not get output to an index file
        // then the annotation should not get output. We can determine whether
        // the target is getting output by checking if there is a SqlAspect for it.
        if (eObject instanceof Annotation) {
            final Annotation annotation = (Annotation)eObject;
            final EObject target = annotation.getAnnotatedObject();
            if (target != null) {
                SqlAspect sqlAspect = AspectManager.getSqlAspect(target);
                if (sqlAspect != null && !sqlAspect.isQueryable(target)) {
                    return;
                }
            }
        }

        addAnnotationWord(objectID,
                          fullName,
                          aspect.getNameInSource(eObject),
                          parentObjectID,
                          aspect.getDescription(eObject),
                          modelPath,
                          name,
                          wordEntries);
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a annotation. This resulting
     * WordEntry is of the form: header|description|footer|
     */
    static void addAnnotationWord( final String objectID,
                                   final String fullName,
                                   final String nameInSource,
                                   final String parentObjectID,
                                   final String description,
                                   final String modelPath,
                                   final String name,
                                   final Collection wordEntries ) {

        // Do not create the annotation word if there is no description to store
        if (CoreStringUtil.isEmpty(description)) {
            return;
        }

        // Construct a string containing the runtime metadata
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        appendWordHeader(IndexConstants.RECORD_TYPE.ANNOTATION, objectID, fullName, nameInSource, parentObjectID, sb);

        // Append the description
        appendObject(description, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        // Append the footer
        appendWordFooter(modelPath, name, sb);

        addNewWordEntryToList(objectID, sb, wordEntries);
    }

    /**
     * Split the specified WordEntry into multiple WordEntry instances if it exceeds the allowable INDEX_RECORD_BLOCK_SIZE. If the
     * entry length does not exceed INDEX_RECORD_BLOCK_SIZE then the original entry will be the only item returned in the list.
     * 
     * @param wordEntry
     * @param blockSize
     */
    public static List splitWordEntry( final String objectID,
                                       final WordEntry wordEntry,
                                       final int blockSize ) {
        CoreArgCheck.isNotNull(objectID);
        CoreArgCheck.isNotNull(wordEntry);

        String entryStr = wordEntry.toString();
        int length = entryStr.length();

        // If the WordEntry is empty ...
        if (length == 0) {
            return Collections.EMPTY_LIST;
        }

        // If the WordEntry size fits within a single block ...
        // (length != blockSize because the char[blockSize] is
        // reserved for a special continuation character. It is
        // the presence or absence of this character that indicates
        // whether the WordEntry is continued onto the next entry).
        List result = new ArrayList(9);
        if (length < blockSize) {
            result.add(wordEntry);
            return result;
        }

        // Ensure that the specified block size is large enough for
        // for the continuation header, trailer, and at least 1 character
        // from the WordEntry
        CoreArgCheck.isTrue(blockSize >= (objectID.length() + 8), "Block size " + blockSize + " is too small"); //$NON-NLS-1$ //$NON-NLS-2$
        char[] origEntry = entryStr.toCharArray();

        // Split the WordEntry into multiple entries ...
        int segCount = 1;
        char recordType = origEntry[0];
        StringBuffer sb = new StringBuffer(blockSize);
        for (int i = 0; i < origEntry.length; i++) {
            char c = origEntry[i];
            sb.append(c);
            if ((sb.length() == (blockSize - 1)) && (i < origEntry.length - 1)) {
                appendContinuationTrailer(blockSize, sb);
                WordEntry partialEntry = new WordEntry(sb.toString().toCharArray());
                result.add(partialEntry);

                sb.setLength(0);
                appendContinuationHeader(recordType, objectID, segCount, sb);
                segCount++;
            }
        }
        // Add in the last segment ...
        if (sb.length() > 0) {
            WordEntry partialEntry = new WordEntry(sb.toString().toCharArray());
            result.add(partialEntry);
        }

        return result;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Append the index record version information to the StringBuffer. The version information consists of 3 characters, the
     * first being a special marker character followed by two integer value characters for a version range of [0-99]. Version
     * values less than 10 will be preceeded by a 0 character.
     * 
     * @param indexVersion
     * @param sb
     * @since 4.2
     */
    protected static void appendIndexVersion( final int indexVersion,
                                              final StringBuffer sb ) {
        // The range of index versions is [0,99]
        CoreArgCheck.isTrue(indexVersion > -1 && indexVersion < 100, "Index version " + indexVersion + " out of range. (0 - 99)"); //$NON-NLS-1$ //$NON-NLS-2$

        sb.append(IndexConstants.RECORD_STRING.INDEX_VERSION_MARKER);
        if (indexVersion < 10) {
            sb.append(Integer.toString(0));
            sb.append(Integer.toString(indexVersion));
        } else {
            sb.append(Integer.toString(indexVersion));
        }
    }

    protected static void appendObject( final Object obj,
                                        final StringBuffer sb ) {
        if (obj != null) {
            // MyDefect : 18119 decoded %20 white space.
            String objectString = obj.toString();
            if (obj instanceof URI) {
                objectString = URI.decode(objectString);
            }

            if (objectString.length() == 0) {
                sb.append(IndexConstants.RECORD_STRING.SPACE);
            }

            sb.append(objectString);
        } else {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
        }
    }

    protected static void appendBoolean( final boolean b,
                                         final StringBuffer sb ) {
        if (b) {
            sb.append(IndexConstants.RECORD_STRING.TRUE);
        } else {
            sb.append(IndexConstants.RECORD_STRING.FALSE);
        }
    }

    /**
     * Concatenate the identifiers for each object in the collection
     */
    protected static void appendStrings( final Collection objs,
                                         final StringBuffer sb ) {
        if (objs == null || objs.isEmpty()) {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
            return;
        }
        // Remove any null or empty strings
        final List tmp = new ArrayList(objs);
        for (Iterator iter = tmp.iterator(); iter.hasNext();) {
            String obj = (String)iter.next();
            if (obj == null || obj.trim().length() == 0) {
                iter.remove();
            }
        }
        // Return if the final list is empty
        if (tmp.isEmpty()) {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
            return;
        }
        // Append the remaining strings
        for (Iterator iter = tmp.iterator(); iter.hasNext();) {
            String obj = (String)iter.next();
            sb.append(obj);
            if (iter.hasNext()) {
                sb.append(IndexConstants.RECORD_STRING.LIST_DELIMITER);
            }
        }
    }

    /**
     * Concatenate the identifiers for each object in the collection
     */
    protected static void appendStrings( final Map objs,
                                         final int indexVersionNumber,
                                         final StringBuffer sb ) {
        if (objs == null || objs.isEmpty()) {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
            return;
        }

        for (Iterator entryIter = objs.entrySet().iterator(); entryIter.hasNext();) {
            Map.Entry mapEntry = (Map.Entry)entryIter.next();
            if (mapEntry == null) {
                continue;
            }
            Object key = mapEntry.getKey();
            Object value = mapEntry.getValue();

            if (key == null || value == null) {
                sb.append(IndexConstants.RECORD_STRING.SPACE);
                if (entryIter.hasNext()) {
                    sb.append(IndexConstants.RECORD_STRING.LIST_DELIMITER);
                }
                continue;
            }

            // add prop values to the string
            sb.append(key.toString());
            sb.append(IndexConstants.RECORD_STRING.PROP_DELIMITER);
            sb.append(value.toString());
            if (entryIter.hasNext()) {
                sb.append(IndexConstants.RECORD_STRING.LIST_DELIMITER);
            }
        }
    }

    /**
     * Concatenate the identifiers for each object in the collection
     */
    protected static void appendIDs( final Collection objs,
                                     final StringBuffer sb ) {
        if (objs == null || objs.isEmpty()) {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
            return;
        }
        // Remove any null or empty strings
        final List tmp = new ArrayList(objs);
        for (Iterator iter = tmp.iterator(); iter.hasNext();) {
            String obj = getObjectIdString(iter.next());
            if (obj == null || obj.trim().length() == 0) {
                iter.remove();
            }
        }
        // Return if the final list is empty
        if (tmp.isEmpty()) {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
            return;
        }
        // Append the remaining strings
        for (Iterator iter = tmp.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            appendID(obj, sb);
            if (iter.hasNext()) {
                sb.append(IndexConstants.RECORD_STRING.LIST_DELIMITER);
            }
        }
    }

    protected static void appendID( final Object obj,
                                    final StringBuffer sb ) {
        String id = getObjectIdString(obj);
        if (id == null || id.length() == 0) {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
        } else {
            sb.append(id);
        }
    }

    protected static String getObjectIdString( final Object obj ) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof EClass) {
            return null;
        } else if (obj instanceof EObject) {
            ModelEditorImpl modelEditor = (ModelEditorImpl)ModelerCore.getModelEditor();
            return modelEditor.getSearchIndexObjectID((EObject)obj);
        } else if (obj instanceof ObjectID) {
            return obj.toString();
        } else if (obj instanceof String) {
            return (String)obj;
        }
        return null;
    }

    /**
     * Concatenate the identifiers for each object in the collection
     */
    protected static void appendURIs( final Collection objs,
                                      final int indexVersionNumber,
                                      final StringBuffer sb ) {
        if (objs == null || objs.isEmpty()) {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
            return;
        }
        // Remove any null or empty strings
        final List tmp = new ArrayList(objs);
        for (Iterator iter = tmp.iterator(); iter.hasNext();) {
            String obj = getObjectIdString(iter.next());
            if (obj == null || obj.trim().length() == 0) {
                iter.remove();
            }
        }
        // Return if the final list is empty
        if (tmp.isEmpty()) {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
            return;
        }
        // Append the remaining strings
        for (Iterator iter = tmp.iterator(); iter.hasNext();) {
            Object obj = iter.next();
            appendURI(obj, sb, false);
            if (iter.hasNext()) {
                sb.append(IndexConstants.RECORD_STRING.LIST_DELIMITER);
            }
        }
    }

    protected static void appendURI( final Object obj,
                                     final StringBuffer sb,
                                     final boolean isMetaClass ) {
        String id = getObjectURIString(obj, isMetaClass);
        if (id == null || id.length() == 0) {
            sb.append(IndexConstants.RECORD_STRING.SPACE);
        } else {
            sb.append(id);
        }
    }

    protected static String getObjectURIString( final Object obj,
                                                final boolean isMetaClass ) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof EObject) {
            // If metaclass, look in cache first
            if (isMetaClass) {
                String cachedUri = (String)metaClassUriMap.get(((EClass)obj).getName());
                if (cachedUri != null) {
                    return cachedUri;
                }
            }
            // If haven't returned, create a new URI
            EObject eObj = (EObject)obj;
            String theUri = ModelerCore.getModelEditor().getUri(eObj).toString();
            if (isMetaClass) {
                // Add new URI to cache
                metaClassUriMap.put(((EClass)obj).getName(), theUri);
            }
            return theUri;
        } else if (obj instanceof URI) {
            return obj.toString();
        } else if (obj instanceof String) {
            return (String)obj;
        }
        return null;
    }

    protected static Properties getProperties( final String values,
                                               final int indexVersionNumber ) {
        final char listDelimiter = getListDelimiter(indexVersionNumber);
        final char propDelimiter = getPropDelimiter(indexVersionNumber);
        return getProperties(values, listDelimiter, propDelimiter);
    }

    protected static Properties getProperties( final String values,
                                               final char listDelimiter,
                                               final char propDelimiter ) {
        Properties props = new Properties();
        if (CoreStringUtil.isEmpty(values)) {
            return props;
        }
        if (values.length() == 1 && values.charAt(0) == IndexConstants.RECORD_STRING.SPACE) {
            return props;
        }
        final List tokens = CoreStringUtil.split(values, String.valueOf(listDelimiter));
        for (Iterator iter = tokens.iterator(); iter.hasNext();) {
            String token = (String)iter.next();
            if (token != null) {
                final List propTokens = CoreStringUtil.split(token, String.valueOf(propDelimiter));
                if (propTokens.size() == 2) {
                    props.put(propTokens.get(0), propTokens.get(1));
                }
            }
        }
        return props;
    }

    protected static char getPropDelimiter( final int indexVersionNumber ) {
        if (indexVersionNumber < DELIMITER_INDEX_VERSION) {
            return IndexConstants.RECORD_STRING.PROP_DELIMITER_OLD;
        }
        return IndexConstants.RECORD_STRING.PROP_DELIMITER;
    }

    /**
     * Add new WordEntry instance to the specified list. If the size of the WordEntry exceeds the allowable block size then the
     * entry is split into multiple entries and each one added to the list.
     * 
     * @param objectID
     * @param sb
     * @param wordEntries
     */
    protected static void addNewWordEntryToList( final String objectID,
                                                 final StringBuffer sb,
                                                 final Collection wordEntries ) {
        String word = sb.toString().trim();
        if (!CoreStringUtil.isEmpty(word)) {
            // ModelerCore.Util.log("        >> RA.addNewWord..() Word = " + word);
            WordEntry wordEntry = new WordEntry(word.toCharArray());
            if (word.length() < INDEX_RECORD_BLOCK_SIZE) {
                wordEntries.add(wordEntry);
                return;
            }
            List splitEntries = splitWordEntry(objectID, wordEntry, INDEX_RECORD_BLOCK_SIZE);
            for (Iterator iter = splitEntries.iterator(); iter.hasNext();) {
                WordEntry entry = (WordEntry)iter.next();
                wordEntries.add(entry);
            }
        }
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    /**
     * Create the initial "header" portion of an index record. This header is common to all record types and is of the form:
     * recordType|upperFullName|objectID|fullName|nameInSource|parentObjectID
     */
    private static void appendWordHeader( final char recordType,
                                          final String objectID,
                                          final String fullName,
                                          final String nameInSource,
                                          final String parentObjectID,
                                          final StringBuffer sb ) {
        sb.append(recordType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        String upperName = (fullName != null ? fullName.toUpperCase() : null);
        appendObject(upperName, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(objectID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(fullName, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(nameInSource, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(parentObjectID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
    }

    /**
     * Create the initial "footer" portion of an index record. This footer is common to all record types and is of the form:
     * modelPath|name
     */
    private static void appendWordFooter( final String modelPath,
                                          final String name,
                                          final StringBuffer sb ) {
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(modelPath, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(name, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendIndexVersion(getCurrentIndexVersionNumber(), sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
    }

    /**
     * Create the continuation "header" portion of an index record. This header is used to prefix a record that is a continuation
     * of another record and is of the form: RECORD_CONTINUATION|objectID|segmentCount|
     */
    private static void appendContinuationHeader( final char recordType,
                                                  final String objectID,
                                                  final int segCount,
                                                  final StringBuffer sb ) {
        sb.append(IndexConstants.RECORD_TYPE.RECORD_CONTINUATION);
        sb.append(recordType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        appendObject(objectID, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        sb.append(segCount);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
    }

    /**
     * Pad the string buffer with spaces out to blockSize -1 and then add the RECORD_CONTINUATION at the blockSize index.
     */
    private static void appendContinuationTrailer( final int blockSize,
                                                   final StringBuffer sb ) {
        int blanksToAdd = blockSize - sb.length() - 1;
        CoreArgCheck.isTrue(blanksToAdd >= 0, "Blanks to add must be >= 0"); //$NON-NLS-1$
        for (int i = 0; i < blanksToAdd; i++) {
            sb.append(' ');
        }
        sb.append(IndexConstants.RECORD_TYPE.RECORD_CONTINUATION);
    }

    // ==================================================================================
    // T E S T M E T H O D S
    // ==================================================================================

    public static WordEntry createTestWordEntry( final char recordType,
                                                 final int numFields,
                                                 final String fieldValue ) {
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        sb.append(recordType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        for (int i = 0; i < numFields; i++) {
            appendObject(fieldValue, sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        }

        appendIndexVersion(getCurrentIndexVersionNumber(), sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        return new WordEntry(sb.toString().toCharArray());
    }

    public static WordEntry createTestWordEntry( final char recordType,
                                                 final int numFields,
                                                 final Collection fieldValue ) {
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        sb.append(recordType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        int indexVersion = getCurrentIndexVersionNumber();
        for (int i = 0; i < numFields; i++) {
            appendStrings(fieldValue, sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        }

        appendIndexVersion(indexVersion, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        return new WordEntry(sb.toString().toCharArray());
    }

    public static WordEntry createTestWordEntry( final char recordType,
                                                 final int numFields,
                                                 final Map fieldValue ) {
        final StringBuffer sb = new StringBuffer(getIniitalBufferSize());
        sb.append(recordType);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        int indexVersion = getCurrentIndexVersionNumber();
        for (int i = 0; i < numFields; i++) {
            appendStrings(fieldValue, indexVersion, sb);
            sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);
        }

        appendIndexVersion(indexVersion, sb);
        sb.append(IndexConstants.RECORD_STRING.RECORD_DELIMITER);

        return new WordEntry(sb.toString().toCharArray());
    }

    /*
     * Defect 22774 - added this getter to centralize the initial buffer size and upped it from 100 to 500 chars. Most index strings
     * are larger than 100 chars.
     */
    protected static int getIniitalBufferSize() {
        return 500;
    }

}
