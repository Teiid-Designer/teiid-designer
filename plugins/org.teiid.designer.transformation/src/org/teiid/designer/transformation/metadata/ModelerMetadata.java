/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.ModelerCoreRuntimeException;
import org.teiid.core.designer.TeiidDesignerException;
import org.teiid.core.designer.id.ObjectID;
import org.teiid.core.designer.id.UUID;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.index.IEntryResult;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.index.ModelResourceIndexSelector;
import org.teiid.designer.core.index.ModelWorkspaceIndexSelector;
import org.teiid.designer.core.metadata.runtime.AnnotationRecordImpl;
import org.teiid.designer.core.metadata.runtime.ColumnRecordImpl;
import org.teiid.designer.core.metadata.runtime.ColumnSetRecordImpl;
import org.teiid.designer.core.metadata.runtime.DatatypeRecordImpl;
import org.teiid.designer.core.metadata.runtime.ForeignKeyRecordImpl;
import org.teiid.designer.core.metadata.runtime.ModelRecordImpl;
import org.teiid.designer.core.metadata.runtime.ProcedureParameterRecordImpl;
import org.teiid.designer.core.metadata.runtime.ProcedureRecordImpl;
import org.teiid.designer.core.metadata.runtime.TableRecordImpl;
import org.teiid.designer.core.metadata.runtime.TransformationRecordImpl;
import org.teiid.designer.core.metadata.runtime.VdbRecordImpl;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAnnotationAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlForeignKeyAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlVdbAspect;
import org.teiid.designer.core.metamodel.util.AbstractNameFinder;
import org.teiid.designer.core.metamodel.util.ColumnNameFinder;
import org.teiid.designer.core.metamodel.util.DatatypeNameFinder;
import org.teiid.designer.core.metamodel.util.ModelNameFinder;
import org.teiid.designer.core.metamodel.util.ProcedureNameFinder;
import org.teiid.designer.core.metamodel.util.TableNameFinder;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.resource.MMXmiResource;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.util.ModelVisitor;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metadata.runtime.TransformationRecord;
import org.teiid.designer.metadata.runtime.impl.RecordFactory;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.util.UuidUtil;

/**
 * Metadata implementation used by model workspace to resolve queries.
 *
 * @since 8.0
 */
public class ModelerMetadata extends TransformationMetadata {

    private static Map systemModelByNameMap = Collections.EMPTY_MAP;
    static {
        if (ModelerCore.getPlugin() != null) {
            Resource[] systemModels = ModelerCore.getSystemVdbResources();
            systemModelByNameMap = new HashMap(systemModels.length);
            for (int i = 0; i != systemModels.length; ++i) {
                String name = systemModels[i].getURI().trimFileExtension().lastSegment();
                systemModelByNameMap.put(name.toUpperCase(), systemModels[i]);
            }
        }
    }

    private Container container;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * ModelerMetadata constructor
     * 
     * @param context Object containing the info needed to lookup metadta.
     */
    ModelerMetadata( QueryMetadataContext context,
                     Container container ) {
        super(context);
        this.container = container;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /* 
     * @see org.teiid.query.metadata.QueryMetadataInterface#getExtensionProperties(java.lang.Object)
     */
    @Override
    public Properties getExtensionProperties( Object metadataID ) {
        CoreArgCheck.isInstanceOf(MetadataRecord.class, metadataID);
        MetadataRecord metadataRecord = (MetadataRecord)metadataID;

        String uuid = metadataRecord.getUUID();
        EObject eObj = lookupEObject(uuid);

        Properties extProps = new Properties();

        // Create get annotation for the EObject and lookup properties
        if (eObj != null && eObj.eResource() != null && eObj.eResource() instanceof EmfResource) {
            EmfResource emfResource = (EmfResource)eObj.eResource();
            ModelContents contents = new ModelContents(emfResource);
            Annotation annotation = contents.getAnnotation(eObj);
            if (annotation != null) {
                Iterator entryIter = annotation.getTags().entrySet().iterator();
                while (entryIter.hasNext()) {
                    Map.Entry entry = (Map.Entry)entryIter.next();
                    extProps.setProperty((String)entry.getKey(), (String)entry.getValue());
                }
            }
        }

        return extProps;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Get the short element from the given full name
     * 
     * @param fullElementName
     * @return
     */
    protected String getShortElementName(final String fullElementName) {
        CoreArgCheck.isNotEmpty(fullElementName);
        if (UuidUtil.isStringifiedUUID(fullElementName)) {
            return UuidUtil.stripPrefixFromUUID(fullElementName);
        }
        int index = fullElementName.lastIndexOf(DELIMITER_CHAR);
        if (index >= 0) {
            return fullElementName.substring(index + 1);
        }
        return fullElementName;
    }
    
    /**
     * Return all metadata records for the entity that matches the given entity name and is of the type specified by the record
     * type.
     * 
     * @param recordType
     * @param entityName the name to match
     * @param isPartialName true if the entity name is a partially qualified
     * @throws Exception
     */
    @Override
    protected Collection findMetadataRecords( final char recordType,
                                              final String entityName,
                                              final boolean isPartialName ) throws Exception {

        Collection eObjects = new ArrayList();

        String uuid = null;
        if (CoreStringUtil.startsWithIgnoreCase(entityName, UUID.PROTOCOL)) {
            uuid = entityName.toLowerCase();
        } else {
            String shortName = getShortElementName(entityName);
            if (CoreStringUtil.startsWithIgnoreCase(shortName, UUID.PROTOCOL)) {
                uuid = shortName.toLowerCase();
            }
        }
        // if it the element is a UUID
        if (uuid != null) {
            EObject eObj = lookupEObject(uuid);
            if (eObj != null) {
                // 12/31/03 (LLP) : fix for 10825. Prevent NPE when column has been deleted.
                if (eObj.eContainer() != null || eObj.eResource() != null) {
                    eObjects.add(eObj);
                }
            }
        }

        // no eObjects found, could be cause the name is a "user string" or Eobject for UUID could
        // not be found in any of open resources
        if (eObjects.isEmpty()) {

            Collection sysObjects = findSystemMetadataRecords(recordType, entityName, isPartialName);

            if (!sysObjects.isEmpty()) {
                return sysObjects;
            }

            // model name is first token
            List tokens = CoreStringUtil.getTokens(entityName, DELIMITER_STRING);
            String firstSegment = (String)tokens.get(0);

            // check if a modelResource exists on the index selector,
            // if so there are indexes to be queried
            if (eObjects.isEmpty() && getIndexSelector() instanceof ModelResourceIndexSelector) {
                if (ModelerCore.DEBUG_QUERY_RESOLUTION) {
                    final String debugMsg = TransformationPlugin.Util.getString("ModelerMetadata.Resolving_entity_{0}_using_index_files_1", entityName); //$NON-NLS-1$
                    TransformationPlugin.Util.log(IStatus.INFO, debugMsg);
                }
                ModelResourceIndexSelector resourceSelector = (ModelResourceIndexSelector)getIndexSelector();
                Object modelResource = ModelerCore.getModelWorkspace().findModelResource(resourceSelector.getResource());
                if (modelResource != null) {
                    // look up the index files instead of navigating the resources.
                    Collection records = super.findMetadataRecords(recordType, entityName, isPartialName);
                    if (!super.getContext().isRestrictedSearch() && records.isEmpty()) {
                        // if cant find query all files, there may have been no imports added
                        // some one is trying to resolved a query containing groups for which there are no imports
                        IndexSelector workspaceSelector = new ModelWorkspaceIndexSelector();
                        super.getContext().setIndexSelector(workspaceSelector);
                        // look up the index files for the whole workspace instead of looking at imported resources
                        records = super.findMetadataRecords(recordType, entityName, isPartialName);
                        // set back the resource selector for subsequent metadata lookup
                        super.getContext().setIndexSelector(resourceSelector);
                    }
                    if (ModelerCore.DEBUG_QUERY_RESOLUTION) {
                        final Object[] params = new Object[] {Integer.toString(records.size()), entityName};
                        final String debugMsg = TransformationPlugin.Util.getString("ModelerMetadata.Found_{0}_records_for_the_entity_{1}_1", params); //$NON-NLS-1$
                        ModelerCore.Util.log(IStatus.INFO, debugMsg);
                    }
                    if (!records.isEmpty()) {
                        return records;
                    }
                    if (uuid != null) {
                        ResourceSet[] resourceSets = this.getContainer().getExternalResourceSets();
                        for (int i = 0; i < resourceSets.length; i++) {
                            ResourceSet currentResourceSet = resourceSets[i];
                            if (currentResourceSet != null && currentResourceSet instanceof Container) {
                                Container externalContainer = (Container)currentResourceSet;
                                EObject externalEObj = (EObject)externalContainer.getEObjectFinder().find(uuid);
                                if (externalEObj != null) {
                                    // 12/31/03 (LLP) : fix for 10825. Prevent NPE when column has been deleted.
                                    if (externalEObj.eContainer() != null || externalEObj.eResource() != null) {
                                        eObjects.add(externalEObj);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (ModelerCore.DEBUG_QUERY_RESOLUTION) {
                final String debugMsg = TransformationPlugin.Util.getString("ModelerMetadata.Resolving_entity_{0}_by_navigating_the_workspace_1", entityName); //$NON-NLS-1$
                TransformationPlugin.Util.log(IStatus.INFO, debugMsg);
            }

            // Look up the EObject by path assuming the first path segement
            // is the model name and the remaining segments are the path within
            // the model
            // find all resources for model name
            Iterator resourceIter = this.findResourcesByName(firstSegment).iterator();
            while (resourceIter.hasNext()) {
                Resource resource = (Resource)resourceIter.next();
                // find EObjects in each resource
                if (resource != null) {
                    Collection entities = findEntitiesByName(resource, entityName, recordType, isPartialName);
                    for (Iterator iter = entities.iterator(); iter.hasNext();) {
                        EObject eObj = (EObject)iter.next();
                        if (eObj != null && (eObj.eContainer() != null || eObj.eResource() != null)) {
                            eObjects.add(eObj);
                        }
                    }
                }
            }
        }

        // find metadata records for the Eobjects collected
        if (!eObjects.isEmpty()) {
            Collection records = createMetadataRecords(recordType, eObjects);
            if (ModelerCore.DEBUG_QUERY_RESOLUTION) {
                final Object[] params = new Object[] {Integer.toString(records.size()), entityName};
                final String debugMsg = TransformationPlugin.Util.getString("ModelerMetadata.Found_{0}_records_for_the_entity_{1}_1", params); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.INFO, debugMsg);
            }
            return records;
        }
        return Collections.EMPTY_LIST;
    }

    protected boolean isSystemModelName( final String firstSegment ) {

        // If the string is a UUID then it is not the name of one of our system models
        if (CoreStringUtil.startsWithIgnoreCase(firstSegment, UUID.PROTOCOL)) {
            return false;
        }

        // Check if the model name is one of the system model names
        if (systemModelByNameMap.containsKey(firstSegment.toUpperCase())) {
            return true;
        }
        return false;
    }

    protected Collection findSystemMetadataRecords( final char recordType,
                                                    final String entityName,
                                                    final boolean isPartialName ) throws Exception {
        Collection eObjects = new ArrayList();

        List tokens = CoreStringUtil.getTokens(entityName, DELIMITER_STRING);
        String firstSegment = (String)tokens.get(0);

        if (!isSystemModelName(firstSegment)) {
            return eObjects;
        }

        Object value = systemModelByNameMap.get(firstSegment.toUpperCase());

        // If the entity name is the name of one of our system models, we need to verify
        // that no model in the workspace
        // we match a System resource in the external container make sure there is
        // no workspace resource we should choose instead
        if (value != null && value instanceof EmfResource) {
            ObjectID eResourceUuid = ((EmfResource)value).getUuid();
            Resource wsEResource = this.getContainer().getResourceFinder().findByUUID(eResourceUuid, false);
            if (wsEResource != null) {
                value = wsEResource;
            }
        }

        Collection entities = findEntitiesByName(value, entityName, recordType, isPartialName);
        for (Iterator iter = entities.iterator(); iter.hasNext();) {
            EObject eObj = (EObject)iter.next();
            if (eObj != null && (eObj.eContainer() != null || eObj.eResource() != null)) {
                eObjects.add(eObj);
            }
        }
        Collection records = createMetadataRecords(recordType, eObjects);
        if (ModelerCore.DEBUG_QUERY_RESOLUTION) {
            final Object[] params = new Object[] {Integer.toString(records.size()), entityName};
            final String debugMsg = TransformationPlugin.Util.getString("ModelerMetadata.Found_{0}_records_for_the_entity_{1}_1", params); //$NON-NLS-1$
            ModelerCore.Util.log(IStatus.INFO, debugMsg);
        }
        return records;

    }

    /**
     * Create the metadataRecord for the given EObject.
     * 
     * @param recordType The record type for the expected MetadataRecord
     * @param eObjects The collection of EObject instances whose records are returned
     * @return The metadataRecords for the eObjects
     * @throws Exception
     */
    protected Collection createMetadataRecords( final char recordType,
                                                final Collection eObjects ) throws Exception {
        if (eObjects != null && !eObjects.isEmpty()) {
            Collection records = new ArrayList(eObjects.size());
            for (Iterator eObjIter = eObjects.iterator(); eObjIter.hasNext();) {
                MetadataRecord record = createMetadataRecord(recordType, (EObject)eObjIter.next());
                if (record != null) {
                    records.add(record);
                }
            }
            return records;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Create the metadataRecord for the given EObject.
     * 
     * @param recordType The record type for the expected MetadataRecord
     * @param eObject The EObject whose record is returned
     * @return The metadataRecord for the eObject
     * @throws Exception
     */
    protected MetadataRecord createMetadataRecord( final char recordType,
                                                   final EObject eObject ) throws Exception {
        MetadataRecord record = null;
        SqlAspect sqlAspect = AspectManager.getSqlAspect(eObject);
        if (!sqlAspect.isQueryable(eObject)) {
            return null;
        }
        switch (recordType) {
            case IndexConstants.RECORD_TYPE.MODEL:
                if (sqlAspect instanceof SqlModelAspect) {
                    record = new ModelRecordImpl((SqlModelAspect)sqlAspect, eObject);
                }
                break;
            case IndexConstants.RECORD_TYPE.TABLE:
                if (sqlAspect instanceof SqlTableAspect) {
                    record = new TableRecordImpl((SqlTableAspect)sqlAspect, eObject);
                }
                break;
            case IndexConstants.RECORD_TYPE.CALLABLE:
                if (sqlAspect instanceof SqlProcedureAspect) {
                    record = new ProcedureRecordImpl((SqlProcedureAspect)sqlAspect, eObject);
                }
                break;
            case IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER:
                if (sqlAspect instanceof SqlProcedureParameterAspect) {
                    record = new ProcedureParameterRecordImpl((SqlProcedureParameterAspect)sqlAspect, eObject);
                }
                break;
            case IndexConstants.RECORD_TYPE.COLUMN:
                if (sqlAspect instanceof SqlColumnAspect) {
                    record = new ColumnRecordImpl((SqlColumnAspect)sqlAspect, eObject);
                }
                break;
            case IndexConstants.RECORD_TYPE.RESULT_SET:
            case IndexConstants.RECORD_TYPE.INDEX:
            case IndexConstants.RECORD_TYPE.ACCESS_PATTERN:
            case IndexConstants.RECORD_TYPE.PRIMARY_KEY:
            case IndexConstants.RECORD_TYPE.UNIQUE_KEY:
                if (sqlAspect instanceof SqlColumnSetAspect) {
                    record = new ColumnSetRecordImpl((SqlColumnSetAspect)sqlAspect, eObject, recordType);
                }
                break;
            case IndexConstants.RECORD_TYPE.FOREIGN_KEY:
                if (sqlAspect instanceof SqlForeignKeyAspect) {
                    record = new ForeignKeyRecordImpl((SqlForeignKeyAspect)sqlAspect, eObject);
                }
                break;
            case IndexConstants.RECORD_TYPE.SELECT_TRANSFORM:
            case IndexConstants.RECORD_TYPE.INSERT_TRANSFORM:
            case IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM:
            case IndexConstants.RECORD_TYPE.DELETE_TRANSFORM:
            case IndexConstants.RECORD_TYPE.PROC_TRANSFORM:
            case IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM:
                record = getTransforMationRecord(sqlAspect, eObject, recordType);
                break;
            case IndexConstants.RECORD_TYPE.DATATYPE:
                if (sqlAspect instanceof SqlDatatypeAspect) {
                    record = new DatatypeRecordImpl((SqlDatatypeAspect)sqlAspect, eObject);
                }
                break;
            case IndexConstants.RECORD_TYPE.VDB_ARCHIVE:
                if (sqlAspect instanceof SqlVdbAspect) {
                    record = new VdbRecordImpl((SqlVdbAspect)sqlAspect, eObject);
                }
                break;
            case IndexConstants.RECORD_TYPE.ANNOTATION:
                if (sqlAspect instanceof SqlAnnotationAspect) {
                    record = new AnnotationRecordImpl((SqlAnnotationAspect)sqlAspect, eObject);
                }
                break;
            case IndexConstants.RECORD_TYPE.PROPERTY:
            case IndexConstants.RECORD_TYPE.JOIN_DESCRIPTOR:
                return null;
            default:
                throw new TeiidDesignerException(
                                                       TransformationPlugin.Util.getString("TransformationMetadata.No_known_index_file_type_associated_with_the_recordType_1", new Character(recordType))); //$NON-NLS-1$
        }

        return record;
    }

    private TransformationRecord getTransforMationRecord( final SqlAspect sqlAspect,
                                                          final EObject eObject,
                                                          final char recordType ) {
        if (sqlAspect instanceof SqlTransformationAspect) {
            switch (recordType) {
                case IndexConstants.RECORD_TYPE.SELECT_TRANSFORM:
                    return new TransformationRecordImpl((SqlTransformationAspect)sqlAspect, eObject,
                                                        SqlTransformationAspect.Types.SELECT);
                case IndexConstants.RECORD_TYPE.INSERT_TRANSFORM:
                    return new TransformationRecordImpl((SqlTransformationAspect)sqlAspect, eObject,
                                                        SqlTransformationAspect.Types.INSERT);
                case IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM:
                    return new TransformationRecordImpl((SqlTransformationAspect)sqlAspect, eObject,
                                                        SqlTransformationAspect.Types.UPDATE);
                case IndexConstants.RECORD_TYPE.DELETE_TRANSFORM:
                    return new TransformationRecordImpl((SqlTransformationAspect)sqlAspect, eObject,
                                                        SqlTransformationAspect.Types.DELETE);
                case IndexConstants.RECORD_TYPE.PROC_TRANSFORM:
                    return new TransformationRecordImpl((SqlTransformationAspect)sqlAspect, eObject,
                                                        SqlTransformationAspect.Types.PROCEDURE);
                case IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM:
                    return new TransformationRecordImpl((SqlTransformationAspect)sqlAspect, eObject,
                                                        SqlTransformationAspect.Types.MAPPING);
                default:
                    throw new ModelerCoreRuntimeException(
                                                          TransformationPlugin.Util.getString("TransformationMetadata.No_known_index_file_type_associated_with_the_recordType_1", new Character(recordType))); //$NON-NLS-1$				
            }
        }

        return null;
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
    @Override
    protected Collection findChildRecords( final MetadataRecord parentRecord,
                                           final char childRecordType ) throws Exception {
        CoreArgCheck.isNotNull(parentRecord);
        // find the eObject on the parent record
        EObject parentObj = (EObject)parentRecord.getEObject();
        // if not preset look up by uuid
        if (parentObj == null) {
            String uuid = parentRecord.getUUID();
            parentObj = lookupEObject(uuid);
        }
        if (parentObj != null) {
            SqlAspect sqlAspect = AspectManager.getSqlAspect(parentObj);
            if (sqlAspect instanceof SqlTableAspect) {
                SqlTableAspect tableAspect = (SqlTableAspect)sqlAspect;
                switch (childRecordType) {
                    case IndexConstants.RECORD_TYPE.COLUMN:
                        Collection columns = tableAspect.getColumns(parentObj);
                        return findMetadataRecords(childRecordType, columns);
                    case IndexConstants.RECORD_TYPE.FOREIGN_KEY:
                        Collection foreignKeys = tableAspect.getForeignKeys(parentObj);
                        return findMetadataRecords(childRecordType, foreignKeys);
                    case IndexConstants.RECORD_TYPE.UNIQUE_KEY:
                        Collection uniqueKeys = tableAspect.getUniqueKeys(parentObj);
                        return findMetadataRecords(childRecordType, uniqueKeys);
                    case IndexConstants.RECORD_TYPE.PRIMARY_KEY:
                        EObject primaryKey = (EObject)tableAspect.getPrimaryKey(parentObj);
                        MetadataRecord record = createMetadataRecord(childRecordType, primaryKey);
                        if (record != null) {
                            Collection records = new ArrayList(1);
                            records.add(record);
                            return records;
                        }
                        break;
                    case IndexConstants.RECORD_TYPE.ACCESS_PATTERN:
                        Collection accPtterns = tableAspect.getAccessPatterns(parentObj);
                        return findMetadataRecords(childRecordType, accPtterns);
                    case IndexConstants.RECORD_TYPE.INDEX:
                        Collection indexes = tableAspect.getIndexes(parentObj);
                        return findMetadataRecords(childRecordType, indexes);
                }
            } else if (sqlAspect instanceof SqlProcedureAspect) {
                SqlProcedureAspect procAspect = (SqlProcedureAspect)sqlAspect;
                switch (childRecordType) {
                    case IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER:
                        Collection params = procAspect.getParameters(parentObj);
                        return findMetadataRecords(childRecordType, params);
                    case IndexConstants.RECORD_TYPE.RESULT_SET:
                        EObject result = (EObject)procAspect.getResult(parentObj);
                        MetadataRecord record = createMetadataRecord(childRecordType, result);
                        if (record != null) {
                            Collection records = new ArrayList(1);
                            records.add(record);
                            return records;
                        }
                        break;
                }
            } else if (sqlAspect instanceof SqlColumnSetAspect) {
                SqlColumnSetAspect colSetAspect = (SqlColumnSetAspect)sqlAspect;
                switch (childRecordType) {
                    case IndexConstants.RECORD_TYPE.COLUMN:
                        Collection column = colSetAspect.getColumns(parentObj);
                        return findMetadataRecords(childRecordType, column);
                    default:
                        break;
                }
            }
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    protected Collection findMetadataRecords( final IEntryResult[] results ) {
        return RecordFactory.getMetadataRecord(results, getContainer().getEObjectFinder());
    }

    @Override
    protected MetadataRecord findMetadataRecord( final IEntryResult result ) {
        return RecordFactory.getMetadataRecord(result, getContainer().getEObjectFinder());
    }

    /**
     * Return the resources in whose context we are looking up metadta.
     * 
     * @since 4.2
     */
    protected final Collection getResources() {
        return super.getContext().getResources();
    }

    /**
     * Return the conatainer in whose context we are looking up metadta.
     * 
     * @since 4.2
     */
    protected final Container getContainer() {
        return container;
    }

    /**
     * Find the EObject having the specified UUID using the ObjectManager for the lookup. If an EObject with this UUID cannot be
     * found then null is returned.
     */
    protected EObject lookupEObject( final String uuid ) {
        CoreArgCheck.isNotEmpty(uuid);

        // Before searching by UUID make sure all resources associated with this QMI are loaded
        if (this.getResources() != null) {
            for (Iterator iter = this.getResources().iterator(); iter.hasNext();) {
                Resource r = (Resource)iter.next();
                if (!r.isLoaded()) {
                    try {
                        r.load(Collections.EMPTY_MAP);
                    } catch (IOException e) {
                        TransformationPlugin.Util.log(IStatus.ERROR, e.getLocalizedMessage());
                    }
                }
            }
        }

        // Go to the Container ...
        EObject eObject = null;

        if (this.getContainer() != null) {
            eObject = (EObject)this.getContainer().getEObjectFinder().find(uuid);

            if (eObject != null) {
                // get the resource on the object
                Resource resource = eObject.eResource();
                // check if this is among the resources is scope for this QMI
                if (this.getResources() != null) {
                    Container cntr = ModelerCore.getContainer(resource);
                    // If the resource exists in the same Container as the one associated with this QMI
                    // but the resource is not in the scope of resources then return null
                    if (cntr == this.getContainer() && !this.getResources().contains(resource)) {
                        return null;
                    }
                }
            }
            return eObject;
        }

        // We are in a non-container environment
        Iterator rsrs = this.getResources().iterator();
        while (rsrs.hasNext()) {
            Resource rsrc = (Resource)rsrs.next();
            if (rsrc instanceof MMXmiResource) {
                eObject = ((MMXmiResource)rsrc).getEObject(uuid);
                if (eObject != null) {
                    return eObject;
                }
            } else if (rsrc instanceof XSDResourceImpl) {
                eObject = ((XSDResourceImpl)rsrc).getEObject(uuid);
                if (eObject != null) {
                    return eObject;
                }
            }
        }

        return eObject;
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    /**
     * Find the metadataRecord for the given EObjects.
     * 
     * @param recordType The record type for the expected MetadataRecord
     * @param eObjects The collection of EObject whose record is returned
     * @return The metadataRecord for the eObject
     * @throws Exception
     */
    private Collection findMetadataRecords( final char recordType,
                                            final Collection eObjects ) throws Exception {
        Collection records = new ArrayList(eObjects.size());
        for (Iterator eObjIter = eObjects.iterator(); eObjIter.hasNext();) {
            EObject eObj = (EObject)eObjIter.next();
            MetadataRecord record = createMetadataRecord(recordType, eObj);
            if (record != null) {
                records.add(record);
            }
        }
        return records;
    }

    /**
     * Return a collection EMF resource matching the specified model name.
     * 
     * @param modelName The name of the model whose resource/s are returned
     * @return The collection of EMD resources
     */
    private Collection findResourcesByName( final String modelName ) {
        CoreArgCheck.isNotEmpty(modelName);

        // get the collection of resources to check
        Collection rsrcs = new ArrayList((this.getResources() != null ? this.getResources() : getContainer().getResources()));

        // Add the system models to the collection if not already there
        Resource[] systemModels = ModelerCore.getSystemVdbResources();
        for (int i = 0; i != systemModels.length; ++i) {
            Resource systemModel = systemModels[i];
            if (!rsrcs.contains(systemModel)) {
                rsrcs.add(systemModel);
            }
        }

        // get the editor to get the name of the resources
        ModelEditor modelEditor = ModelerCore.getModelEditor();
        // find all the resource the match the model name
        Collection resources = new ArrayList(1);
        // get the index selector to limit the resource search
        IndexSelector selector = this.getIndexSelector();
        if (selector != null && selector instanceof ModelResourceIndexSelector) {
            // the selector has reference to the resource and references to its imports
            ModelResourceIndexSelector resourceSelector = (ModelResourceIndexSelector)selector;
            // try finding the entity in the selectors resource
            Resource modelResource = resourceSelector.getResource();
            String resourceName = modelEditor.getModelName(modelResource);
            if (resourceName.equalsIgnoreCase(modelName)) {
                resources.add(modelResource);
            } else {
                // check if any if any of the imported resources have the same name
                Iterator importIter = resourceSelector.getModelImports().iterator();
                while (importIter.hasNext()) {
                    ModelImport modelImport = (ModelImport)importIter.next();
                    String importName = modelImport.getName();
                    if (importName.equalsIgnoreCase(modelName)) {
                        // compare the import path of the model to the path in the resource
                        String importPath = modelImport.getPath();
                        for (Iterator resourceIter = rsrcs.iterator(); resourceIter.hasNext();) {
                            Resource resource = (Resource)resourceIter.next();
                            String resourceUri = URI.decode(resource.getURI().toString());
                            if (CoreStringUtil.endsWithIgnoreCase(resourceUri, importPath)) {
                                resources.add(resource);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            // find the resource that matches the model name
            for (Iterator resourceIter = rsrcs.iterator(); resourceIter.hasNext();) {
                Resource resource = (Resource)resourceIter.next();
                String resourceName = modelEditor.getModelName(resource);
                if (resourceName.equalsIgnoreCase(modelName)) {
                    resources.add(resource);
                    break;
                }
            }
        }
        if (!resources.isEmpty()) {
            return resources;
        }
        // all open resources as model could not be found
        return rsrcs;
    }

    private Collection findEntitiesByName( final Object container,
                                           final String entityName,
                                           final char recordType,
                                           final boolean isPartialName ) {
        CoreArgCheck.isNotEmpty(entityName);

        AbstractNameFinder finder = null;
        switch (recordType) {
            case IndexConstants.RECORD_TYPE.MODEL:
                finder = new ModelNameFinder(entityName, isPartialName);
                break;
            case IndexConstants.RECORD_TYPE.TABLE:
                finder = new TableNameFinder(entityName, isPartialName);
                break;
            case IndexConstants.RECORD_TYPE.CALLABLE:
                finder = new ProcedureNameFinder(entityName, isPartialName);
                break;
            case IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER:
            case IndexConstants.RECORD_TYPE.COLUMN:
                finder = new ColumnNameFinder(entityName, isPartialName);
                break;
            case IndexConstants.RECORD_TYPE.RESULT_SET:
            case IndexConstants.RECORD_TYPE.INDEX:
            case IndexConstants.RECORD_TYPE.ACCESS_PATTERN:
            case IndexConstants.RECORD_TYPE.PRIMARY_KEY:
            case IndexConstants.RECORD_TYPE.UNIQUE_KEY:
            case IndexConstants.RECORD_TYPE.FOREIGN_KEY:
                finder = new ColumnNameFinder(entityName, isPartialName);
                break;
            case IndexConstants.RECORD_TYPE.SELECT_TRANSFORM:
            case IndexConstants.RECORD_TYPE.INSERT_TRANSFORM:
            case IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM:
            case IndexConstants.RECORD_TYPE.DELETE_TRANSFORM:
            case IndexConstants.RECORD_TYPE.PROC_TRANSFORM:
            case IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM:
                return getTransforMationsForTable(container, entityName, recordType, isPartialName);
            case IndexConstants.RECORD_TYPE.DATATYPE:
                // case IndexConstants.RECORD_TYPE.DATATYPE_ELEMENT:
                // case IndexConstants.RECORD_TYPE.DATATYPE_FACET:
                finder = new DatatypeNameFinder(entityName, isPartialName);
                break;
            case IndexConstants.RECORD_TYPE.VDB_ARCHIVE:
            case IndexConstants.RECORD_TYPE.ANNOTATION:
            case IndexConstants.RECORD_TYPE.PROPERTY:
            case IndexConstants.RECORD_TYPE.JOIN_DESCRIPTOR:
                break;
            default:
                throw new ModelerCoreRuntimeException(
                                                      TransformationPlugin.Util.getString("TransformationMetadata.No_known_index_file_type_associated_with_the_recordType_1", new Character(recordType))); //$NON-NLS-1$
        }

        if (finder != null) {
            executeVisitor(container, finder, ModelVisitorProcessor.DEPTH_INFINITE);
            return finder.getMatchingEObjects();
        }

        return Collections.EMPTY_LIST;
    }

    private static void executeVisitor( final Object container,
                                        final ModelVisitor visitor,
                                        final int depth ) {
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        try {
            if (container instanceof Resource) {
                processor.walk((Resource)container, depth);
            } else if (container instanceof EObject) {
                processor.walk((EObject)container, depth);
            }
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    private Collection getTransforMationsForTable( final Object container,
                                                   final String entityName,
                                                   final char recordType,
                                                   final boolean isPartialName ) {
        AbstractNameFinder finder = null;
        switch (recordType) {
            case IndexConstants.RECORD_TYPE.SELECT_TRANSFORM:
            case IndexConstants.RECORD_TYPE.INSERT_TRANSFORM:
            case IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM:
            case IndexConstants.RECORD_TYPE.DELETE_TRANSFORM:
            case IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM:
                finder = new TableNameFinder(entityName, isPartialName);
                break;
            case IndexConstants.RECORD_TYPE.PROC_TRANSFORM:
                finder = new ProcedureNameFinder(entityName, isPartialName);
                break;
            default:
                throw new ModelerCoreRuntimeException(
                                                      TransformationPlugin.Util.getString("TransformationMetadata.No_known_index_file_type_associated_with_the_recordType_1", new Character(recordType))); //$NON-NLS-1$			
        }

        executeVisitor(container, finder, ModelVisitorProcessor.DEPTH_INFINITE);
        Collection matches = finder.getMatchingEObjects();
        Collection transforms = new ArrayList(matches.size());
        for (Iterator targetIter = matches.iterator(); targetIter.hasNext();) {
            EObject targetObj = (EObject)targetIter.next();
            if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isValidTransformationTarget(targetObj)) {
                EObject mappinRoot = TransformationHelper.getMappingRoot(targetObj);
                transforms.add(mappinRoot);
            }
        }

        return transforms;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.transformation.metadata.TransformationMetadata#findChildRecordsForColumns(org.teiid.designer.core.metadata.runtime.MetadataRecord, char, java.util.List)
     */
    @Override
    protected Collection findChildRecordsForColumns( MetadataRecord parentRecord,
                                                     char childRecordType,
                                                     List uuids ) throws Exception {
        Collection kids = findChildRecords(parentRecord, childRecordType);
        ArrayList<MetadataRecord> result = new ArrayList<MetadataRecord>();
        for (Object o : kids) {
            MetadataRecord mr = (MetadataRecord)o;
            if (uuids.contains(mr.getUUID())) {
                result.add(mr);
            }
        }
        return result;
    }
}
