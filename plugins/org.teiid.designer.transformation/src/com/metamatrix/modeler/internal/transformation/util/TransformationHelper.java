/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.emf.mapping.MappingRoot;
import org.teiid.core.TeiidComponentException;
import org.teiid.api.exception.query.QueryMetadataException;
import org.teiid.core.util.SqlUtil;
import com.metamatrix.common.xmi.XMIHeader;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationMapping;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.XQueryTransformation;
import com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metadata.runtime.ProcedureRecord;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlResultSetAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelFileUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFactory;
import org.teiid.query.metadata.QueryMetadataInterface;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.navigator.PreOrderNavigator;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.SingleElementSymbol;
import org.teiid.query.sql.util.UpdateProcedureGenerator;
import org.teiid.query.sql.visitor.ElementCollectorVisitor;
import org.teiid.query.validator.UpdateValidationVisitor;
import org.teiid.query.validator.ValidatorReport;

/**
 * TransformationHelper This class contains helper methods for getting "properties" from TransformationMappings
 */
public class TransformationHelper implements SqlConstants {
    public static final String THIS_CLASS = "TransformationHelper"; //$NON-NLS-1$
    private static final boolean IS_UNDOABLE = true;
    private static final boolean IS_SIGNIFICANT = true;
    private static final boolean NOT_SIGNIFICANT = false;
    private static final boolean IS_UUID = true;
    private static final boolean IS_NOT_UUID = false;

    //    private static final String SELECT_TRNS_STRING = "SELECT"; //$NON-NLS-1$
    //    private static final String INSERT_TRNS_STRING = "INSERT"; //$NON-NLS-1$
    //    private static final String UPDATE_TRNS_STRING = "UPDATE"; //$NON-NLS-1$
    //    private static final String DELETE_TRNS_STRING = "DELETE"; //$NON-NLS-1$

    //public static final String DEFAULT_SELECT = "SELECT * FROM"; //$NON-NLS-1$

    private static TransformationFactory transformationFactory = TransformationFactory.eINSTANCE;
    private static final String CHANGE_SELECT_TXN_DESCRIPTION = TransformationPlugin.Util.getString("TransformationHelper.changeSelectSqlTxnDescription"); //$NON-NLS-1$
    private static final String CHANGE_INSERT_TXN_DESCRIPTION = TransformationPlugin.Util.getString("TransformationHelper.changeInsertSqlTxnDescription"); //$NON-NLS-1$
    private static final String CHANGE_UPDATE_TXN_DESCRIPTION = TransformationPlugin.Util.getString("TransformationHelper.changeUpdateSqlTxnDescription"); //$NON-NLS-1$
    private static final String CHANGE_DELETE_TXN_DESCRIPTION = TransformationPlugin.Util.getString("TransformationHelper.changeDeleteSqlTxnDescription"); //$NON-NLS-1$

    private static final String ADD_SRC_ALIAS_TXN_DESCRIPTION = TransformationPlugin.Util.getString("TransformationHelper.addSrcAliasTxnDescription"); //$NON-NLS-1$
    private static final String REMOVE_SRC_ALIAS_TXN_DESCRIPTION = TransformationPlugin.Util.getString("TransformationHelper.removeSrcAliasTxnDescription"); //$NON-NLS-1$

    private static final String NULL_OR_INVALID_TARGET = "TransformationHelper.getTransformationMappingRoot:null or invalid target."; //$NON-NLS-1$

    private static final String XML_SERVICE_URI = "XmlService"; //$NON-NLS-1$

    /**
     * Get the MappingHelper from a SqlTransformationMappingRoot. If one doesn't exist, it is created.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the mapping helper
     */
    public static MappingHelper getMappingHelper( Object transMappingRoot ) {
        MappingHelper helper = null;
        if (transMappingRoot != null && isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get the Mapping Helper from the MappingRoot
            SqlTransformationMappingRoot sqlTransMappingRoot = (SqlTransformationMappingRoot)transMappingRoot;
            helper = sqlTransMappingRoot.getHelper();

            // If helper is null, create one
            if (helper == null) {
                // Need to wrap these in transaction
                boolean requiresStart = ModelerCore.startTxn(false, true, "Create Mapping Helper", transMappingRoot); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Create Primary SqlTransformation
                    if (!isReadOnly(sqlTransMappingRoot)) {
                        helper = transformationFactory.createSqlTransformation();
                        sqlTransMappingRoot.setHelper(helper);
                        // Create Nested SqlTransformation for User SQL
                        createNestedUserSqlTransformation(helper);
                    }
                    succeeded = true;
                } finally {
                    if (requiresStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        } else if (transMappingRoot instanceof XQueryTransformationMappingRoot) {
            // Get the Mapping Helper from the MappingRoot
            XQueryTransformationMappingRoot xQueryTransMappingRoot = (XQueryTransformationMappingRoot)transMappingRoot;
            helper = xQueryTransMappingRoot.getHelper();

            // If helper is null, create one
            if (helper == null) {
                // Need to wrap these in transaction
                boolean requiresStart = ModelerCore.startTxn(false, true, "Create XQuery Mapping Helper", transMappingRoot); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Create Primary XQueryTransformation
                    if (!isReadOnly(xQueryTransMappingRoot)) {
                        helper = transformationFactory.createXQueryTransformation();
                        xQueryTransMappingRoot.setHelper(helper);
                        // Create Nested SqlTransformation for User SQL
                        // createNestedUserSqlTransformation(helper);
                    }
                    succeeded = true;
                } finally {
                    if (requiresStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        return helper;
    }

    /**
     * Get the User SqlTransformation from a SqlTransformationMappingRoot. This is the nested SqlTransformation that is used to
     * store the "user" (or non-uuid) SQL strings.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the mapping helper
     */
    public static SqlTransformation getUserSqlTransformation( Object transMappingRoot ) {
        SqlTransformation nestedSqlTrans = null;

        // This will create the nested SqlTransformation (if it doesnt exist)
        MappingHelper helper = getMappingHelper(transMappingRoot);

        // Should be non-null, but check anyway
        if (helper != null) {
            // Get the nested Helpers, find User SqlTransformation
            EList nestedList = helper.getNested();
            Iterator iter = nestedList.iterator();
            while (iter.hasNext()) {
                EObject eObj = (EObject)iter.next();
                if (eObj != null && eObj instanceof SqlTransformation) {
                    nestedSqlTrans = (SqlTransformation)eObj;
                    break;
                }
            }
            // If the User SqlTransformation wasnt found, create one
            if (nestedSqlTrans == null && !isReadOnly(helper)) {
                nestedSqlTrans = createNestedUserSqlTransformation(helper);
            }
        }
        return nestedSqlTrans;
    }

    /**
     * Create a Nested SqlTransformation within the primary SqlTransformation. Purpose of the nested SqlTransformation object is
     * to maintain "user" Sql Strings (non-uuid versions)
     * 
     * @param sqlTransformation the primary SqlTransformation mapping helper
     * @return the created SqlTransformation
     */
    public static SqlTransformation createNestedUserSqlTransformation( MappingHelper sqlTransformation ) {
        // Create Nested SqlTransformation for User SQL
        SqlTransformation userSqlTrans = transformationFactory.createSqlTransformation();
        userSqlTrans.setNestedIn(sqlTransformation);
        if (sqlTransformation instanceof SqlTransformation) {
            final SqlTransformation sqlTran = (SqlTransformation)sqlTransformation;
            final String selectSql = sqlTran.getSelectSql();

            // Convert existing UUID based SQL Strings to UserSql Strings
            // and set them on the new SqlTransformation
            Container container = ModelerCore.getContainer(sqlTransformation);
            if (container != null) {
                if (selectSql != null) {
                    final String userSql = SqlConverter.convertUUIDsToFullNames(selectSql, container);
                    userSqlTrans.setSelectSql(userSql);
                }

                final String insertSql = sqlTran.getInsertSql();
                if (insertSql != null) {
                    final String userSql = SqlConverter.convertUUIDsToFullNames(insertSql, container);
                    userSqlTrans.setInsertSql(userSql);
                }

                final String updateSql = sqlTran.getUpdateSql();
                if (updateSql != null) {
                    final String userSql = SqlConverter.convertUUIDsToFullNames(updateSql, container);
                    userSqlTrans.setUpdateSql(userSql);
                }

                final String deleteSql = sqlTran.getDeleteSql();
                if (deleteSql != null) {
                    final String userSql = SqlConverter.convertUUIDsToFullNames(deleteSql, container);
                    userSqlTrans.setDeleteSql(userSql);
                }
            }

        }

        return userSqlTrans;
    }

    /**
     * Get the Mapping from a MappingHelper.
     * 
     * @param transMappingHelper the transformation mapping helper
     * @return the mapping
     */
    public static Mapping getMappingRoot( MappingHelper transMappingHelper ) {
        Mapping mapping = null;
        if (transMappingHelper != null) {
            // Get the Mapping from the MappingHelper
            mapping = transMappingHelper.getMapper();
        }
        return mapping;
    }

    /**
     * Create the Transformation using the supplied virtual target group and source group. The resulting SELECT SQL for the
     * transformation is "SELECT * FROM SourceGroup". If the transformation already exists, it is reset.
     * 
     * @param virtualTarget the Virtual target group for the transformation
     * @param sourceGroup the source group for the transformation
     * @return the transformation mapping root
     */
    public static EObject createTransformation( EObject virtualTarget,
                                                EObject sourceGroup ) {
        EObject transMappingRoot = null;
        if (virtualTarget != null && !isReadOnly(virtualTarget)) {

            // This creates new mapping root if it doesnt already exist
            transMappingRoot = getTransformationMappingRoot(virtualTarget);

            String name = getSqlEObjectFullName(sourceGroup);
            addSqlAlias(transMappingRoot, sourceGroup, name, false, virtualTarget);

            if (transMappingRoot != null) {
                final Object txnSource = null;
                // If the source is a StoredProcedure, create Virtual Procedure
                if (TransformationHelper.isSqlProcedure(sourceGroup)) {
                    // Create StoredProcedure
                    StoredProcedure proc = TransformationSqlHelper.createStoredProc(sourceGroup);
                    if (proc != null) {
                        CreateUpdateProcedureCommand cCommand = TransformationSqlHelper.createVirtualProcCommmandForCommand(proc);
                        // Set the SQL STring on the transformation ...
                        setSelectSqlString(transMappingRoot, cCommand.toString(), false, txnSource);
                    }
                } else {
                    // Create the query node for the default query ...
                    Query query = TransformationSqlHelper.createDefaultQuery(sourceGroup);
                    // Set the SQL STring on the transformation ...
                    setSelectSqlString(transMappingRoot, query.toString(), true, txnSource);
                }

                // If target and source allowsUpdate is true, create the default insert/update/delete sql
                Object target = TransformationHelper.getTransformationTarget(transMappingRoot);
                if (TransformationHelper.isVirtualSqlTable(target) && TransformationHelper.isSqlTable(sourceGroup)) {
                    SqlTableAspect tableAspect = (SqlTableAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)target);
                    if (tableAspect != null && tableAspect.supportsUpdate((EObject)target)
                        && tableAspect.supportsUpdate(sourceGroup)) {
                        TransformationHelper.refreshUpdateStrings(transMappingRoot, true, txnSource);
                    }
                }

                // reconcile mappings
                TransformationMappingHelper.reconcileMappingsOnSqlChange(transMappingRoot, txnSource);
            }

        }
        return transMappingRoot;
    }

    /**
     * Get if the given object is an updatable table
     * 
     * @param target The eObject which may be a table
     * @return boolean indicating that a table is updatable
     * @since 4.3
     */
    public static boolean isTableThatSupportsUpdate( EObject target ) {
        CoreArgCheck.isNotNull(target);

        SqlAspect sqlAspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(target);
        if (sqlAspect != null && sqlAspect instanceof SqlTableAspect) {
            SqlTableAspect tableAspect = (SqlTableAspect)sqlAspect;
            if (tableAspect.supportsUpdate(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create the Transformation using the supplied virtual target group and SELECT SQL text. If the transformation already
     * exists, the SQL is set to the provided string.
     * 
     * @param virtualTarget the Virtual target group for the transformation
     * @param selectSQL the SELECT SQL text for the transformation
     * @return the transformation mapping root
     */
    public static EObject createTransformation( EObject virtualTarget,
                                                String selectSQL ) {
        EObject transMappingRoot = null;
        if (virtualTarget != null) {
            // This creates new mapping root if it doesnt already exist
            transMappingRoot = getTransformationMappingRoot(virtualTarget);

            // Set the supplied SELECT SQL on transformation
            if (transMappingRoot != null && !isReadOnly(transMappingRoot)) {
                final Object txnSource = null;
                setSelectSqlString(transMappingRoot, selectSQL, true, txnSource);
                TransformationMappingHelper.reconcileMappingsOnSqlChange(transMappingRoot, txnSource);
            }
        }
        return transMappingRoot;
    }

    public static String getDefaultSqlSelectString( Object transMappingRoot ) {
        return BLANK;
        // EObject target = getTransformationTarget(transMappingRoot);
        // String selectString = DEFAULT_SELECT;
        // if (isSqlProcedure(target) || isSqlProcedureResultSet(target)) {
        // selectString = BLANK;
        // }
        //
        // return selectString;
    }

    public static String getSelectSqlString( Object transMappingRoot ) {
        return getSqlString(transMappingRoot, QueryValidator.SELECT_TRNS);
    }

    public static String getInsertSqlString( Object transMappingRoot ) {
        return getSqlString(transMappingRoot, QueryValidator.INSERT_TRNS);
    }

    public static String getUpdateSqlString( Object transMappingRoot ) {
        return getSqlString(transMappingRoot, QueryValidator.UPDATE_TRNS);
    }

    public static String getDeleteSqlString( Object transMappingRoot ) {
        return getSqlString(transMappingRoot, QueryValidator.DELETE_TRNS);
    }

    /**
     * Get the SQL for the specified type
     * 
     * @param transMappingRoot the transformation mappingRoot
     * @param cmdType the commandType to get the current SQL String
     * @return the SQL String
     */
    public static String getSqlString( Object transMappingRoot,
                                       int cmdType ) {
        return SqlMappingRootCache.getSqlString(transMappingRoot, cmdType);
    }

    /**
     * Get the UUID SQL for the specified type. Does not go to cache - goes directly to mappingRoot.
     * 
     * @param transMappingRoot the transformation mappingRoot
     * @param cmdType the commandType to get the current UUID SQL String
     * @return the UUID SQL String
     */
    public static String getUUIDSqlString( Object transMappingRoot,
                                           int cmdType ) {
        String uuidString = null;
        switch (cmdType) {
            case QueryValidator.SELECT_TRNS:
                uuidString = getSelectSqlUUIDString(transMappingRoot);
                break;
            case QueryValidator.INSERT_TRNS:
                uuidString = getInsertSqlUUIDString(transMappingRoot);
                break;
            case QueryValidator.UPDATE_TRNS:
                uuidString = getUpdateSqlUUIDString(transMappingRoot);
                break;
            case QueryValidator.DELETE_TRNS:
                uuidString = getDeleteSqlUUIDString(transMappingRoot);
                break;
            default:
                break;
        }
        return uuidString;
    }

    /**
     * Set the SQL String for the provided type on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlString the SQL String
     * @param sqlType the SQL type (SqlConstants.SELECT_TRNS, INSERT_TRNS, UPDATE_TRNS, DELETE_TRNS)
     */
    public static boolean setSelectSqlString( Object transMappingRoot,
                                              String sqlString,
                                              boolean isSignificant,
                                              Object txnSource ) {
        return setSqlString(transMappingRoot, sqlString, QueryValidator.SELECT_TRNS, isSignificant, txnSource);
    }

    public static boolean setInsertSqlString( Object transMappingRoot,
                                              String sqlString,
                                              boolean isSignificant,
                                              Object txnSource ) {
        return setSqlString(transMappingRoot, sqlString, QueryValidator.INSERT_TRNS, isSignificant, txnSource);
    }

    public static boolean setUpdateSqlString( Object transMappingRoot,
                                              String sqlString,
                                              boolean isSignificant,
                                              Object txnSource ) {
        return setSqlString(transMappingRoot, sqlString, QueryValidator.UPDATE_TRNS, isSignificant, txnSource);
    }

    public static boolean setDeleteSqlString( Object transMappingRoot,
                                              String sqlString,
                                              boolean isSignificant,
                                              Object txnSource ) {
        return setSqlString(transMappingRoot, sqlString, QueryValidator.DELETE_TRNS, isSignificant, txnSource);
    }

    /**
     * Set the SQL properties for the specified type for a QueryOperationDefinition MetaObject, given a SQL string. The sqlString
     * argument is assumed not to contain uuids. The sqlString provided will be saved to the MetaObjects sql (string) property.
     * The sqlString will also be converted to uuid form and saved to the (statement) property.
     * 
     * @param queryMO the QueryOperationDefinition object
     * @param sqlString the SQL string.
     * @param type the statement type (SELECT, INSERT, UPDATE, DELETE)
     * @param txnSource the source to use for the transaction.
     * @param txnName the transaction name (for undo/redo display).
     * @param isSignificant the isSignificant flag, for undoable edits.
     */
    public static boolean setSqlString( Object transMappingRoot,
                                        String sqlString,
                                        int cmdType,
                                        boolean isSignificant,
                                        Object txnSource ) {
        boolean changed = false;

        if (!isReadOnly((EObject)transMappingRoot)) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_SELECT_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                // Convert Symbols to UUIDs First
                String newUUIDSQL = SqlConverter.convertToUID(sqlString, (EObject)transMappingRoot, cmdType);
                boolean hasCachedStatus = SqlMappingRootCache.containsStatus((EObject)transMappingRoot, cmdType);

                // Switch based on type (SELECT, INSERT, UPDATE, DELETE)
                switch (cmdType) {
                    case QueryValidator.SELECT_TRNS:
                        // Set the mapping root sql strings
                        changed = setSelectSqlUserString(transMappingRoot, sqlString, isSignificant, txnSource);
                        boolean uuidSelectChanged = setSelectSqlUUIDString(transMappingRoot, newUUIDSQL, isSignificant, txnSource);
                        changed = changed || uuidSelectChanged;
                        // Invalid cache if the sql has changed
                        if (hasCachedStatus) {
                            if (SqlMappingRootCache.isSqlDifferent(transMappingRoot, cmdType, sqlString, newUUIDSQL)) {
                                // invalidate cached status
                                SqlMappingRootCache.invalidateSelectStatus(transMappingRoot, true, txnSource);
                                // refresh Update, Insert, Delete if necessary
                                refreshUpdateStrings(transMappingRoot, isSignificant, txnSource);
                                changed = true;
                            }
                        }
                        break;
                    case QueryValidator.INSERT_TRNS:
                        // Set the mapping root sql strings
                        changed = setInsertSqlUserString(transMappingRoot, sqlString, isSignificant, txnSource);
                        boolean uuidInsertChanged = setInsertSqlUUIDString(transMappingRoot, newUUIDSQL, isSignificant, txnSource);
                        changed = changed || uuidInsertChanged;
                        // Invalid cache if the user string changed, or current status is uuid status
                        if (changed || hasCachedStatus) {
                            if (SqlMappingRootCache.isSqlDifferent(transMappingRoot, cmdType, sqlString, newUUIDSQL)) {
                                // invalidate cached status
                                SqlMappingRootCache.invalidateInsertStatus(transMappingRoot, true, txnSource);
                                changed = true;
                            }
                        }
                        break;
                    case QueryValidator.UPDATE_TRNS:
                        // Set the mapping root sql strings
                        changed = setUpdateSqlUserString(transMappingRoot, sqlString, isSignificant, txnSource);
                        boolean uuidUpdateChanged = setUpdateSqlUUIDString(transMappingRoot, newUUIDSQL, isSignificant, txnSource);
                        changed = changed || uuidUpdateChanged;
                        // Invalid cache if the user string changed, or current status is uuid status
                        if (changed || hasCachedStatus) {
                            if (SqlMappingRootCache.isSqlDifferent(transMappingRoot, cmdType, sqlString, newUUIDSQL)) {
                                // invalidate cached status
                                SqlMappingRootCache.invalidateUpdateStatus(transMappingRoot, true, txnSource);
                                changed = true;
                            }
                        }
                        break;
                    case QueryValidator.DELETE_TRNS:
                        // Set the mapping root sql strings
                        changed = setDeleteSqlUserString(transMappingRoot, sqlString, isSignificant, txnSource);
                        boolean uuidDeleteChanged = setDeleteSqlUUIDString(transMappingRoot, newUUIDSQL, isSignificant, txnSource);
                        changed = changed || uuidDeleteChanged;
                        // Invalid cache if the user string changed, or current status is uuid status
                        if (changed || hasCachedStatus) {
                            if (SqlMappingRootCache.isSqlDifferent(transMappingRoot, cmdType, sqlString, newUUIDSQL)) {
                                // invalidate cached status
                                SqlMappingRootCache.invalidateDeleteStatus(transMappingRoot, true, txnSource);
                                changed = true;
                            }
                        }
                        break;
                    default:
                        break;
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        changed = false;
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        return changed;
    }

    /**
     * refresh all of the update strings (UPDATE, INSERT, DELETE) based on the current SELECT.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param eObj the EObject to alias
     * @param aliasName the alias name
     */
    public static void refreshUpdateStrings( Object transMappingRoot,
                                             boolean isSignificant,
                                             Object txnSource ) {
        // If this mappingRoot allows insert, and default is being used, reset it
        if (isInsertAllowed(transMappingRoot) && isInsertSqlDefault((EObject)transMappingRoot)) {
            String generatedProc = getGeneratedProcedureStr(transMappingRoot, QueryValidator.INSERT_TRNS);
            String generatedProcUID = SqlConverter.convertToUID(generatedProc,
                                                                (EObject)transMappingRoot,
                                                                QueryValidator.INSERT_TRNS);
            String currentInsertUserStr = getInsertSqlUserString(transMappingRoot);
            String currentInsertUUIDStr = getInsertSqlUUIDString(transMappingRoot);
            if ((generatedProc == null && currentInsertUserStr != null)
                || (generatedProc != null && !generatedProc.equalsIgnoreCase(currentInsertUserStr))) {
                setInsertSqlUserString(transMappingRoot, generatedProc, isSignificant, txnSource);
            }
            if ((generatedProcUID == null && currentInsertUUIDStr != null)
                || (generatedProcUID != null && !generatedProcUID.equalsIgnoreCase(currentInsertUUIDStr))) {
                setInsertSqlUUIDString(transMappingRoot, generatedProcUID, isSignificant, txnSource);
            }
        }
        // If this mappingRoot allows update, and default is being used, reset it
        if (isUpdateAllowed(transMappingRoot) && isUpdateSqlDefault((EObject)transMappingRoot)) {
            String generatedProc = getGeneratedProcedureStr(transMappingRoot, QueryValidator.UPDATE_TRNS);
            String generatedProcUID = SqlConverter.convertToUID(generatedProc,
                                                                (EObject)transMappingRoot,
                                                                QueryValidator.UPDATE_TRNS);
            String currentUpdateUserStr = getUpdateSqlUserString(transMappingRoot);
            String currentUpdateUUIDStr = getUpdateSqlUUIDString(transMappingRoot);
            if ((generatedProc == null && currentUpdateUserStr != null)
                || (generatedProc != null && !generatedProc.equalsIgnoreCase(currentUpdateUserStr))) {
                setUpdateSqlUserString(transMappingRoot, generatedProc, isSignificant, txnSource);
            }
            if ((generatedProcUID == null && currentUpdateUUIDStr != null)
                || (generatedProcUID != null && !generatedProcUID.equalsIgnoreCase(currentUpdateUUIDStr))) {
                setUpdateSqlUUIDString(transMappingRoot, generatedProcUID, isSignificant, txnSource);
            }
        }
        // If this mappingRoot allows delete, and default is being used, reset it
        if (isDeleteAllowed(transMappingRoot) && isDeleteSqlDefault((EObject)transMappingRoot)) {
            String generatedProc = getGeneratedProcedureStr(transMappingRoot, QueryValidator.DELETE_TRNS);
            String generatedProcUID = SqlConverter.convertToUID(generatedProc,
                                                                (EObject)transMappingRoot,
                                                                QueryValidator.DELETE_TRNS);
            String currentDeleteUserStr = getDeleteSqlUserString(transMappingRoot);
            String currentDeleteUUIDStr = getDeleteSqlUUIDString(transMappingRoot);
            if ((generatedProc == null && currentDeleteUserStr != null)
                || (generatedProc != null && !generatedProc.equalsIgnoreCase(currentDeleteUserStr))) {
                setDeleteSqlUserString(transMappingRoot, generatedProc, isSignificant, txnSource);
            }
            if ((generatedProcUID == null && currentDeleteUUIDStr != null)
                || (generatedProcUID != null && !generatedProcUID.equalsIgnoreCase(currentDeleteUUIDStr))) {
                setDeleteSqlUUIDString(transMappingRoot, generatedProcUID, isSignificant, txnSource);
            }
        }
    }

    /**
     * Create a SQL alias for a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param eObj the EObject to alias
     * @param aliasName the alias name
     */
    public static SqlAlias createSqlAlias( Object transMappingRoot,
                                           EObject eObj,
                                           String aliasName ) {
        CoreArgCheck.isNotNull(eObj);
        CoreArgCheck.isNotNull(aliasName);

        SqlAlias sqlAlias = null;
        MappingHelper helper = getMappingHelper(transMappingRoot);
        if (helper != null && helper instanceof SqlTransformation) {
            sqlAlias = transformationFactory.createSqlAlias();
            sqlAlias.setAliasedObject(eObj);
            sqlAlias.setAlias(aliasName);
        }
        return sqlAlias;
    }

    /**
     * Create a SQL alias for a SqlTransformationMappingRoot and adds it to the SqlAlias list.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param eObj the EObject to alias
     * @param aliasName the alias name
     * @return 'true' if the operation was successful, 'false' if not.
     */
    public static boolean addSqlAlias( Object mappingRoot,
                                       EObject eObj,
                                       String aliasName,
                                       boolean isSignificant,
                                       Object txnSource ) {
        boolean wasAdded = false;

        MappingHelper helper = getMappingHelper(mappingRoot);
        SqlTransformationMappingRoot transMappingRoot = null;
        if (isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly(transMappingRoot)) {

            // --------------------------------------------
            // If alias doesnt already exist, add is ok
            // --------------------------------------------
            if (isValidSource(transMappingRoot, eObj) && !containsSqlAliasName(transMappingRoot, aliasName, eObj)) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, ADD_SRC_ALIAS_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    // Get Current MappingRoot inputs
                    List inputEObjects = transMappingRoot.getInputs();

                    // If inputEObject doesnt contain EObject, add it to the mapping
                    if (!inputEObjects.contains(eObj)) {
                        addValueToEList(transMappingRoot, eObj, transMappingRoot.getInputs());
                    }

                    // Add the SqlAlias to the SqlTransformation
                    SqlAlias newAlias = createSqlAlias(transMappingRoot, eObj, aliasName);
                    SqlTransformation sqlTrans = (SqlTransformation)helper;
                    addValueToEList(sqlTrans, newAlias, sqlTrans.getAliases());
                    succeeded = true;
                } finally {// if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                wasAdded = true;
            }
        }
        return wasAdded;
    }

    /**
     * Add a SQL alias to the SqlAlias List for a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlAlias the SqlAlias to be added
     * @return 'true' if the operation was successful, 'false' if not.
     */
    public static boolean addSqlAlias( Object mappingRoot,
                                       SqlAlias sqlAlias,
                                       boolean isSignificant,
                                       Object txnSource ) {
        boolean wasAdded = false;

        MappingHelper helper = getMappingHelper(mappingRoot);
        SqlTransformationMappingRoot transMappingRoot = null;
        if (isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly(transMappingRoot)) {

            // --------------------------------------------
            // If alias doesnt already exist, add is ok
            // --------------------------------------------
            if (isValidSource(transMappingRoot, sqlAlias.getAliasedObject())
                && !containsSqlAliasName(transMappingRoot, sqlAlias.getAlias(), sqlAlias.getAliasedObject())) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, ADD_SRC_ALIAS_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    // Get Current MappingRoot inputs
                    List inputEObjects = transMappingRoot.getInputs();

                    // If inputEObject doesnt contain EObject, add it to the mapping
                    if (!inputEObjects.contains(sqlAlias.getAliasedObject())) {
                        addValueToEList(transMappingRoot, sqlAlias.getAliasedObject(), transMappingRoot.getInputs());
                    }

                    // Add the SqlAlias to the SqlTransformation
                    SqlTransformation sqlTrans = (SqlTransformation)helper;
                    addValueToEList(sqlTrans, sqlAlias, sqlTrans.getAliases());
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                wasAdded = true;
            }
        }

        return wasAdded;
    }

    /**
     * Remove an Input source from a SqlTransformationMappingRoot.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param eObj the EObject to remove
     */
    public static void removeSourceAndAliases( Object mappingRoot, // NO_UCD
                                               EObject sourceEObj,
                                               boolean isSignificant,
                                               Object txnSource ) {

        if (mappingRoot != null && isSqlTransformationMappingRoot(mappingRoot) && !isReadOnly((EObject)mappingRoot)) {
            SqlTransformationMappingRoot transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;

            // Get the current transformation sources
            List sources = getSourceEObjects(transMappingRoot);

            // If the current inputs contain the requested eObj, remove it
            if (sources.contains(sourceEObj)) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, "Remove Source", txnSource); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    // Delete the Aliases
                    List aliases = getSqlAliasesForSource(mappingRoot, sourceEObj);
                    if (!aliases.isEmpty()) {
                        try {
                            ModelerCore.getModelEditor().delete(aliases);
                        } catch (ModelerCoreException e) {
                            String message = TransformationPlugin.Util.getString("TransformationHelper.removeTransSourceAliasError", //$NON-NLS-1$
                                                                                 transMappingRoot.toString());
                            TransformationPlugin.Util.log(IStatus.ERROR, e, message);
                        }
                    }
                    // remove the source
                    removeValueFromEList(transMappingRoot, sourceEObj, transMappingRoot.getInputs());
                    removeModelImportForSourceObject((EmfResource)transMappingRoot.eResource(), sourceEObj);
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the datatype for the supplied sqlColumn eobject.
     * 
     * @param object the supplied EObject
     * @return the Datatype
     */
    public static EObject getSqlColumnDatatype( EObject eObject ) {
        EObject datatype = null;
        if (isSqlColumn(eObject)) {
            SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(eObject);
            datatype = columnAspect.getDatatype(eObject);
        }
        return datatype;
    }

    /**
     * Get the length for the supplied sqlColumn eobject.
     * 
     * @param object the supplied EObject
     * @return the length
     */
    public static int getSqlColumnLength( EObject eObject ) {
        int length = -1;
        if (isSqlColumn(eObject)) {
            SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(eObject);
            length = columnAspect.getLength(eObject);
        }
        return length;
    }

    /**
     * Get the runtime type for the supplied sqlColumn eobject.
     * 
     * @param object the supplied EObject
     * @return the runtime type
     */
    public static String getSqlColumnRuntimeType( EObject eObject ) {
        String runtimeType = null;
        if (isSqlColumn(eObject)) {
            SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(eObject);
            runtimeType = columnAspect.getRuntimeType(eObject);
        }
        return runtimeType;
    }

    /**
     * Get the runtime type for the supplied procedure parameter eobject.
     * 
     * @param object the supplied EObject
     * @return the runtime type
     */
    public static String getProcedureParameterRuntimeType( EObject eObject ) {
        String runtimeType = null;
        if (isSqlProcedureParameter(eObject)) {
            SqlProcedureParameterAspect columnAspect = (SqlProcedureParameterAspect)AspectManager.getSqlAspect(eObject);
            runtimeType = columnAspect.getRuntimeType(eObject);
        }
        return runtimeType;
    }

    public static String getRuntimeType( EObject eObject ) {
        String result = getSqlColumnRuntimeType(eObject);
        if (result != null) {
            return result;
        }
        result = getProcedureParameterRuntimeType(eObject);
        if (result != null) {
            return result;
        }
        return result;
    }

    /**
     * Set the datatype for the supplied sqlColumn eobject.
     * 
     * @param eObject the supplied EObject
     * @param datatype the Datatype
     */
    public static void setSqlColumnDatatype( EObject eObject,
                                             EObject datatype,
                                             Object txnSource ) {
        if (isSqlColumn(eObject) && !isReadOnly(eObject)) {
            SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(eObject);

            if (columnAspect.canSetDatatype()) {
                boolean requiredStart = ModelerCore.startTxn(IS_SIGNIFICANT, IS_UNDOABLE, "Set Column Datatype", txnSource); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    columnAspect.setDatatype(eObject, datatype);
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
    }

    /**
     * Set the length for the supplied sqlColumn eobject.
     * 
     * @param eObject the supplied EObject
     * @param length the new length
     */
    public static void setSqlColumnLength( EObject eObject,
                                           int length,
                                           Object txnSource ) {
        if (isSqlColumn(eObject) && !isReadOnly(eObject)) {
            SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(eObject);

            if (columnAspect.canSetLength()) {
                boolean requiredStart = ModelerCore.startTxn(IS_SIGNIFICANT, IS_UNDOABLE, "Set Column Length", txnSource); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    columnAspect.setLength(eObject, length);
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
    }

    /**
     * Set the name for the supplied sqlColumn eobject.
     * 
     * @param eObject the supplied EObject
     * @param name the new name
     */
    public static void setSqlColumnName( EObject eObject,
                                         String name,
                                         Object txnSource ) {
        if (isSqlColumn(eObject) && !isReadOnly(eObject)) {
            boolean requiredStart = ModelerCore.startTxn(IS_SIGNIFICANT, IS_UNDOABLE, "Set Column Name", txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                ModelerCore.getModelEditor().rename(eObject, name);
                succeeded = true;
            } catch (ModelerCoreException e) {
                String message = TransformationPlugin.Util.getString("TransformationHelper.sqlColumnRenameError", //$NON-NLS-1$
                                                                     eObject.toString());
                TransformationPlugin.Util.log(IStatus.ERROR, e, message);
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * transfer SqlColumn properties from the source to the target.
     * 
     * @param eObject the supplied EObject
     * @param datatype the Datatype
     */
    public static void transferSqlColumnProperties( EObject targetEObj,
                                                    EObject sourceEObj,
                                                    Object txnSource ) {
        if (isSqlColumn(targetEObj) && isSqlColumn(sourceEObj) && !isReadOnly(targetEObj)) {

            boolean requiredStart = ModelerCore.startTxn(IS_SIGNIFICANT, IS_UNDOABLE, "Transfer Column Props", txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(targetEObj);
                columnAspect.updateObject(targetEObj, sourceEObj);
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        } else if (isSqlColumn(targetEObj) && isSqlProcedureParameter(sourceEObj) && !isReadOnly(targetEObj)) {
            boolean requiredStart = ModelerCore.startTxn(IS_SIGNIFICANT, IS_UNDOABLE, "Transfer Column Props", txnSource); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                Column tgtColumn = (Column)targetEObj;
                MetamodelAspect aspect = AspectManager.getSqlAspect(sourceEObj);
                CoreArgCheck.isInstanceOf(SqlProcedureParameterAspect.class, aspect);
                final ProcedureParameter param = (ProcedureParameter)sourceEObj;
                // set all the properties by looking up the sql aspect
                tgtColumn.setLength(param.getLength());
                tgtColumn.setPrecision(param.getPrecision());
                tgtColumn.setRadix(param.getRadix());
                tgtColumn.setPrecision(param.getPrecision());
                tgtColumn.setScale(param.getScale());
                tgtColumn.setType(param.getType());
                tgtColumn.setDefaultValue(param.getDefaultValue());

                // set the nulltype for the target
                tgtColumn.setNullable(param.getNullable());
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * Get the name the supplied object sql eobject.
     * 
     * @param object the supplied Object
     * @return the name
     */
    public static String getSqlEObjectName( EObject eObject ) {
        String returnString = "NULL"; //$NON-NLS-1$
        SqlAspect aspect = AspectManager.getSqlAspect(eObject);
        if (aspect != null) {
            returnString = aspect.getName(eObject);
        }
        return returnString;
    }

    /**
     * Get the name the supplied object sql eobject.
     * 
     * @param object the supplied Object
     * @return the name
     */
    public static String getSqlEObjectFullName( EObject eObject ) {
        String returnString = "NULL"; //$NON-NLS-1$
        SqlAspect aspect = AspectManager.getSqlAspect(eObject);
        if (aspect != null) {
            returnString = aspect.getFullName(eObject);
        }
        return returnString;
    }

    /**
     * Get the name the supplied object sql eobject.
     * 
     * @param object the supplied Object
     * @return the name
     */
    public static String getSqlEObjectUUID( EObject eObject ) {
        String returnString = "NULL"; //$NON-NLS-1$
        SqlAspect aspect = AspectManager.getSqlAspect(eObject);
        if (aspect != null) {
            returnString = aspect.getObjectID(eObject).toString();
        }
        return returnString;
    }

    /**
     * Get the path the supplied object sql eobject.
     * 
     * @param object the supplied Object
     * @return the name
     */
    public static String getSqlEObjectPath( EObject eObject ) {
        String returnString = "NULL"; //$NON-NLS-1$
        SqlAspect aspect = AspectManager.getSqlAspect(eObject);
        if (aspect != null) {
            returnString = aspect.getPath(eObject).toString();
        }
        return returnString;
    }

    /**
     * Get the name the supplied object sql eobject.
     * 
     * @param object the supplied Object
     * @return the name
     */
    public static String getSqlColumnName( EObject eObject ) {
        String returnString = "NULL"; //$NON-NLS-1$
        if (isSqlColumn(eObject)) {
            if (isXmlDocument(eObject)) {
                returnString = "xml"; //$NON-NLS-1$
            } else {
                SqlAspect aspect = AspectManager.getSqlAspect(eObject);
                if (aspect != null) {
                    returnString = aspect.getName(eObject);
                }
            }
        }
        return returnString;
    }

    /**
     * Get the parameters for the provided Procedure EObject.
     * 
     * @param object the supplied Procedure Object
     * @return the parameters
     */
    public static List getProcedureParameters( EObject eObject ) {
        List params = null;
        if (TransformationHelper.isSqlProcedure(eObject)) {
            // Get procedure parameters
            SqlProcedureAspect procedureAspect = (SqlProcedureAspect)AspectManager.getSqlAspect(eObject);
            params = procedureAspect.getParameters(eObject);
        }
        if (params == null) {
            params = Collections.EMPTY_LIST;
        }
        return params;
    }

    /**
     * Get the parameters of type INOUT for the provided Procedure EObject.
     * 
     * @param object the supplied Procedure Object
     * @return the parameters of type INOUT
     */
    public static List getInoutParameters( EObject eObject ) {
        List params = getProcedureParameters(eObject);
        List result = new ArrayList(params.size());
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            Object param = iter.next();
            if (TransformationHelper.isSqlProcedureParameter(param)) {
                SqlProcedureParameterAspect parameterAspect = (SqlProcedureParameterAspect)AspectManager.getSqlAspect((EObject)param);
                int type = parameterAspect.getType((EObject)param);
                if (type == MetadataConstants.PARAMETER_TYPES.INOUT_PARM) {
                    result.add(param);
                }
            }
        }
        return result;
    }

    /**
     * Get the parameters of type IN for the provided Procedure EObject.
     * 
     * @param object the supplied Procedure Object
     * @return the parameters of type IN
     */
    public static List getInParameters( EObject eObject ) {
        List params = getProcedureParameters(eObject);
        List result = new ArrayList(params.size());
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            Object param = iter.next();
            if (TransformationHelper.isSqlProcedureParameter(param)) {
                SqlProcedureParameterAspect parameterAspect = (SqlProcedureParameterAspect)AspectManager.getSqlAspect((EObject)param);
                int type = parameterAspect.getType((EObject)param);
                if (type == MetadataConstants.PARAMETER_TYPES.IN_PARM) {
                    result.add(param);
                }
            }
        }
        return result;
    }

    /**
     * Get the parameters of type IN and INOUT for the provided Procedure EObject.
     * 
     * @param object the supplied Procedure Object
     * @return the parameters of type IN or INOUT
     */
    public static List getInAndInoutParameters( EObject eObject ) {
        List params = getProcedureParameters(eObject);
        List result = new ArrayList(params.size());
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            Object param = iter.next();
            if (TransformationHelper.isSqlProcedureParameter(param)) {
                SqlProcedureParameterAspect parameterAspect = (SqlProcedureParameterAspect)AspectManager.getSqlAspect((EObject)param);
                int type = parameterAspect.getType((EObject)param);
                if (type == MetadataConstants.PARAMETER_TYPES.IN_PARM || type == MetadataConstants.PARAMETER_TYPES.INOUT_PARM) {
                    result.add(param);
                }
            }
        }
        return result;
    }

    /**
     * Get the parameters of type OUT, and RETURN for the provided Procedure EObject.
     * 
     * @param object the supplied Procedure Object
     * @return the parameters of type OUT, or RETURN
     */
    public static List getOutAndReturnParameters( EObject eObject ) {
        List params = getProcedureParameters(eObject);
        List result = new ArrayList(params.size());
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            Object param = iter.next();
            if (TransformationHelper.isSqlProcedureParameter(param)) {
                SqlProcedureParameterAspect parameterAspect = (SqlProcedureParameterAspect)AspectManager.getSqlAspect((EObject)param);
                int type = parameterAspect.getType((EObject)param);
                if (type == MetadataConstants.PARAMETER_TYPES.OUT_PARM || type == MetadataConstants.PARAMETER_TYPES.RETURN_VALUE) {
                    result.add(param);
                }
            }
        }
        return result;
    }

    /**
     * Remove a SQL alias from the SqlAlias List for a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlAlias the SqlAlias to be removed
     * @return
     */
    public static boolean removeSourceAlias( Object mappingRoot,
                                             EObject eObj,
                                             String aliasName,
                                             boolean isSignificant,
                                             Object txnSource ) {
        boolean wasRemoved = false;

        MappingHelper helper = getMappingHelper(mappingRoot);
        SqlTransformationMappingRoot transMappingRoot = null;
        if (isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly(transMappingRoot)) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, REMOVE_SRC_ALIAS_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                // Remove alias matching the supplied aliasName, if exists
                removeSourceAliasMatchingName(transMappingRoot, eObj, aliasName);

                // See if there are any Aliases left after removal
                List aliases = getSqlAliasesForSource(transMappingRoot, eObj);
                // If the last alias was removed, also remove the Input from the mappingRoot
                if (aliases.size() == 0) {
                    // Get Current MappingRoot inputs
                    List inputEObjects = transMappingRoot.getInputs();

                    // If inputEObject list contains the EObject, remove it from the mapping
                    if (inputEObjects.contains(eObj)) {
                        removeValueFromEList(transMappingRoot, eObj, transMappingRoot.getInputs());
                        // inputEObjects.remove(eObj);
                        removeModelImportForSourceObject((EmfResource)transMappingRoot.eResource(), eObj);
                    }
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }

            wasRemoved = true;
        }

        return wasRemoved;
    }

    /**
     * Remove SQL aliases SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlAlias the SqlAlias to be removed
     * @return
     */
    public static boolean removeSourceAliases( Object mappingRoot,
                                               EObject source,
                                               List sqlAliases,
                                               boolean isSignificant,
                                               Object txnSource ) {
        boolean wasRemoved = false;

        MappingHelper helper = getMappingHelper(mappingRoot);
        SqlTransformationMappingRoot transMappingRoot = null;
        if (isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly(transMappingRoot)) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, REMOVE_SRC_ALIAS_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                // Ensure that all the SqlAliases reference the supplied source
                Iterator iter = sqlAliases.iterator();
                List aliasesToRemove = new ArrayList(sqlAliases.size());
                while (iter.hasNext()) {
                    Object nextObj = iter.next();
                    if (nextObj instanceof SqlAlias) {
                        EObject aliasedObj = ((SqlAlias)nextObj).getAliasedObject();
                        if (aliasedObj != null && aliasedObj.equals(source) && !aliasesToRemove.contains(nextObj)) {
                            aliasesToRemove.add(nextObj);
                        }
                    }
                }

                // If number of Aliases being remove is same as all aliases for source,
                // remove the source also
                List allSourceAliases = getSqlAliasesForSource(transMappingRoot, source);
                boolean removeSource = false;
                if (aliasesToRemove.size() == allSourceAliases.size()) {
                    removeSource = true;
                }

                // Remove the SqlAliases
                SqlTransformation sqlTrans = (SqlTransformation)helper;
                Iterator removeIter = aliasesToRemove.iterator();
                while (removeIter.hasNext()) {
                    EObject removeObj = (EObject)removeIter.next();
                    removeValueFromEList(sqlTrans, removeObj, sqlTrans.getAliases());
                    removeModelImportForSourceObject((EmfResource)transMappingRoot.eResource(), removeObj);
                }

                // If necessary, remove the input
                if (removeSource) {
                    removeValueFromEList(transMappingRoot, source, transMappingRoot.getInputs());
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }

            wasRemoved = true;
        }

        return wasRemoved;
    }

    /**
     * Clear all SQL aliases and input sources from the SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @return
     */
    public static boolean removeAllSourcesAndAliases( Object mappingRoot,
                                                      boolean isSignificant,
                                                      Object txnSource ) {
        boolean wasCleared = false;

        MappingHelper helper = getMappingHelper(mappingRoot);
        SqlTransformationMappingRoot transMappingRoot = null;
        if (isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
        }

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly(transMappingRoot)) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, REMOVE_SRC_ALIAS_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                // if Target is WS Operation, then set the XML DOcument attribute to NULL
                clearXmlDocumentAsSource(transMappingRoot, true, txnSource);
                // Delete the Aliases
                SqlTransformation sqlTrans = (SqlTransformation)helper;
                List aliasList = new ArrayList(((SqlTransformation)helper).getAliases());
                Iterator aliasIter = aliasList.iterator();
                while (aliasIter.hasNext()) {
                    removeValueFromEList(sqlTrans, aliasIter.next(), sqlTrans.getAliases());
                }
                // Delete the sources
                List inputList = new ArrayList(transMappingRoot.getInputs());
                Iterator inputIter = inputList.iterator();
                while (inputIter.hasNext()) {
                    EObject removeObj = (EObject)inputIter.next();
                    // remove the source
                    removeValueFromEList(transMappingRoot, removeObj, transMappingRoot.getInputs());
                    removeModelImportForSourceObject((EmfResource)transMappingRoot.eResource(), removeObj);
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }

            wasCleared = true;
        }
        return wasCleared;
    }

    /**
     * This method sets additional properties on a Web Services's Operation's Output object In particular, it calls
     * setXmlDocument() and setContentViaElement()
     * 
     * @param transformation
     * @param xmlDocument
     * @param txnSource
     * @since 5.0
     */
    public static void clearXmlDocumentAsSource( final SqlTransformationMappingRoot transformation,
                                                 final boolean forceClearContentViaElement,
                                                 final Object txnSource ) {
        // Ensure root's target is Web Service Operation object
        Object target = transformation.getTarget();
        if (!(target instanceof Operation)) {
            return;
        }
        Operation operation = (Operation)target;

        // Ensure there is only one new value and it's an XML document
        if (operation.getOutput() != null) {
            boolean requiredStart = false;
            boolean succeeded = false;
            try {
                // -------------------------------------------------
                // Let's wrap this in a transaction!!!
                // will result in only one transaction?
                // -------------------------------------------------

                requiredStart = ModelerCore.startTxn(false, false, "Set Output Property Values", txnSource); //$NON-NLS-1$$

                // call setXmlDocument() on the Output
                if (operation.getOutput().getXmlDocument() != null) {
                    operation.getOutput().setXmlDocument(null);
                }
                // setContenntViaElement() on the Output using the document's underlying XSD Element
                if (operation.getOutput().getContentElement() != null && forceClearContentViaElement) {
                    operation.getOutput().setContentElement(null);
                }

                succeeded = true;
            } catch (Exception ex) {
                TransformationPlugin.Util.log(IStatus.ERROR, ex, ex.getClass().getName()
                                                                 + ":" + THIS_CLASS + ".clearXmlDocumentAsSource()"); //$NON-NLS-1$  //$NON-NLS-2$
            } finally {
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    public static boolean clearTransformation( Object mappingRoot,
                                               boolean isSignificant,
                                               Object txnSource,
                                               boolean removeAttributes ) {
        boolean wasCleared = false;
        EObject targetEObject = null;

        MappingHelper helper = getMappingHelper(mappingRoot);
        SqlTransformationMappingRoot transMappingRoot = null;
        if (isSqlTransformationMappingRoot(mappingRoot)) {
            transMappingRoot = (SqlTransformationMappingRoot)mappingRoot;
            targetEObject = transMappingRoot.getTarget();
        }

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly(transMappingRoot)) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, REMOVE_SRC_ALIAS_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                removeAllSourcesAndAliases(transMappingRoot, isSignificant, txnSource);
                String defaultSelect = getDefaultSqlSelectString(transMappingRoot);

                TransformationHelper.setSelectSqlString(mappingRoot, defaultSelect, isSignificant, txnSource);
                TransformationHelper.setInsertSqlString(mappingRoot, null, isSignificant, txnSource);
                TransformationHelper.setUpdateSqlString(mappingRoot, null, isSignificant, txnSource);
                TransformationHelper.setDeleteSqlString(mappingRoot, null, isSignificant, txnSource);

                AttributeMappingHelper.clearAttributeMappingInputs(mappingRoot, txnSource);

                if (removeAttributes) {
                    List attributes = new ArrayList(targetEObject.eContents());
                    // Iterator iter = attributes.iterator();
                    // while(iter.hasNext()) {
                    try {
                        ModelerCore.getModelEditor().delete(attributes);
                    } catch (ModelerCoreException e) {
                        String message = TransformationPlugin.Util.getString("TransformationHelper.removeTargetAttributeError", //$NON-NLS-1$
                                                                             ModelerCore.getModelEditor().getName(targetEObject));
                        TransformationPlugin.Util.log(IStatus.ERROR, e, message);
                    }
                    // }
                    AttributeMappingHelper.clearAttributeMappings(mappingRoot, txnSource);

                }

                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }

            wasCleared = true;
        }

        return wasCleared;
    }

    /**
     * Determine if the SqlTransformationMappingRoot already has an alias with the same name as the supplied alias
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlAlias the SqlAlias
     * @return 'true' if the mappingRoot contains the supplied SqlAlias, 'false' if not.
     */
    public static boolean containsSqlAliasName( Object transMappingRoot, // NO_UCD
                                                String desiredName ) {
        boolean contains = false;
        // Check whether the current alias contain any with this name
        List allSqlAliases = getAllSqlAliases(transMappingRoot);
        Iterator iter = allSqlAliases.iterator();
        while (iter.hasNext()) {
            String aliasName = ((SqlAlias)iter.next()).getAlias();
            if (aliasName != null && aliasName.equalsIgnoreCase(desiredName)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * Determine if the SqlTransformationMappingRoot already has an alias with the same name as the supplied alias
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlAlias the SqlAlias
     * @return 'true' if the mappingRoot contains the supplied SqlAlias, 'false' if not.
     */
    public static boolean containsSqlAliasName( Object transMappingRoot,
                                                String desiredName,
                                                Object sourceObject ) {
        boolean contains = false;
        // Check whether the current alias contain any with this name from the source
        List allSqlAliases = getAllSqlAliases(transMappingRoot);
        Iterator iter = allSqlAliases.iterator();
        while (iter.hasNext()) {
            SqlAlias sqlAlias = (SqlAlias)iter.next();
            String aliasName = sqlAlias.getAlias();

            if (aliasName != null && aliasName.equalsIgnoreCase(desiredName)) {
                // Check the eObject
                EObject eObj = sqlAlias.getAliasedObject();
                if (sourceObject == eObj) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    /**
     * This is a private method that only removes the SqlAlias for an input (if it can be found)
     * 
     * @param transMappingRoot the transformation mapping root
     * @param eObj the input EObject
     * @param aliasName the name of the alias to be removed
     */
    private static void removeSourceAliasMatchingName( EObject transMappingRoot,
                                                       EObject eObj,
                                                       String aliasName ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);
        if (helper != null && helper instanceof SqlTransformation && !isReadOnly(helper)) {
            SqlTransformation sqlTrans = (SqlTransformation)helper;
            List aliases = sqlTrans.getAliases();
            // Remove the alias if it matches
            Object aliasToRemove = null;
            Iterator iter = aliases.iterator();
            while (iter.hasNext()) {
                SqlAlias sqlAlias = (SqlAlias)iter.next();
                EObject aEObj = sqlAlias.getAliasedObject();
                String aName = sqlAlias.getAlias();
                if (aEObj != null && aName != null && aEObj.equals(eObj) && aName.equalsIgnoreCase(aliasName)) {
                    aliasToRemove = sqlAlias;
                    break;
                }
            }
            if (aliasToRemove != null) {
                removeValueFromEList(sqlTrans, aliasToRemove, sqlTrans.getAliases());
            }
        }
    }

    /**
     * Remove transformation inputs that arent referenced by a SqlAlias. Also, remove SqlAliases that dont refer to something in
     * the input list. Purpose of this method is to clean up any orphaned objects.
     * 
     * @param transMappingRoot the transformation mapping root
     */
    public static void reconcileInputsAndAliases( EObject transMappingRoot ) {
        if (isSqlTransformationMappingRoot(transMappingRoot)) {
            SqlTransformationMappingRoot tmRoot = (SqlTransformationMappingRoot)transMappingRoot;
            // Get List of All SqlAliases for mapping root
            List allAliases = getAllSqlAliases(tmRoot);

            // Get List of Unique EObjects aliased by SqlAliases
            List allAliasedEObjs = getAllAliasedEObjs(tmRoot);

            // Get Current MappingRoot inputs
            List allInputs = tmRoot.getInputs();

            // If any of the inputs are not referenced by a SqlAlias, add them to badInputList
            List badInputList = new ArrayList(allInputs.size());
            Iterator inputIter = allInputs.iterator();
            while (inputIter.hasNext()) {
                Object input = inputIter.next();
                if (!allAliasedEObjs.contains(input)) {
                    badInputList.add(input);
                }
            }

            // If 'badInputs' were found, remove them from the input list
            if (!badInputList.isEmpty()) {
                Iterator badIter = badInputList.iterator();
                // Delete the sources
                while (badIter.hasNext()) {
                    EObject removeObj = (EObject)badIter.next();
                    // remove the source
                    removeValueFromEList(tmRoot, removeObj, tmRoot.getInputs());
                    removeModelImportForSourceObject((EmfResource)tmRoot.eResource(), removeObj);
                }
            }

            // If any of the SqlAliases has a reference to something other than the input
            // EObjects, that's bad also
            List badAliasList = new ArrayList();
            Iterator aliasIter = allAliases.iterator();
            while (aliasIter.hasNext()) {
                SqlAlias sqlAlias = (SqlAlias)aliasIter.next();
                EObject aliasedEObj = sqlAlias.getAliasedObject();
                if (aliasedEObj != null && !allInputs.contains(aliasedEObj)) {
                    badAliasList.add(sqlAlias);
                }
            }

            // If 'badAliases' were found, remove them from alias list
            Iterator badAliasIter = badAliasList.iterator();
            while (badAliasIter.hasNext()) {
                MappingHelper helper = getMappingHelper(tmRoot);
                if (helper instanceof SqlTransformation) {
                    SqlTransformation sqlTrans = (SqlTransformation)helper;
                    removeValueFromEList(sqlTrans, badAliasIter.next(), sqlTrans.getAliases());
                }
            }

        }
    }

    /**
     * Get all SqlTransformationMappingRoots for the supplied resource
     * 
     * @param resource the supplied resource
     * @return the list of mappingRoots
     */
    public static List getAllTransformations( final Resource resource ) {
        CoreArgCheck.isNotNull(resource);
        List transformations = new ArrayList();
        List contents = resource.getContents();
        Iterator cIter = contents.iterator();
        while (cIter.hasNext()) {
            Object obj = cIter.next();
            if (obj instanceof TransformationContainer) {
                List mappings = ((TransformationContainer)obj).getTransformationMappings();
                transformations.addAll(mappings);
                break;
            }
        }

        return transformations;
    }

    /**
     * Return the current SqlAlias List for a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     */
    public static List getAllSqlAliases( Object transMappingRoot ) {
        List allAliases = null;

        MappingHelper helper = getMappingHelper(transMappingRoot);
        if (helper != null && helper instanceof SqlTransformation) {
            allAliases = ((SqlTransformation)helper).getAliases();
        }

        if (allAliases == null) {
            allAliases = Collections.EMPTY_LIST;
        }

        return allAliases;
    }

    /**
     * Get the List of aliased EObjects. This gets the SqlAlias List and constructs the list of unique EObjects from it.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the list of unique EObjects from the aliases
     */
    public static List getAllAliasedEObjs( Object transMappingRoot ) {
        List uniqueEObjList = new ArrayList();
        List allAliases = getAllSqlAliases(transMappingRoot);
        Iterator iter = allAliases.iterator();
        while (iter.hasNext()) {
            SqlAlias sqlAlias = (SqlAlias)iter.next();
            EObject aliasedEObj = sqlAlias.getAliasedObject();
            if (aliasedEObj != null && !uniqueEObjList.contains(aliasedEObj)) {
                uniqueEObjList.add(aliasedEObj);
            }
        }
        return uniqueEObjList;
    }

    /**
     * Return a partial SqlAlias List for a SqlTransformationMappingRoot that reference a specific eObject
     * 
     * @param transMappingRoot the transformation mapping root
     * @param eObj the EObject to find matched SqlAlias objects
     */
    public static List getSqlAliasesForSource( Object transMappingRoot,
                                               EObject eObj ) {
        List matchingAliases = new ArrayList();
        List badAliasList = new ArrayList();

        Iterator iter = getAllSqlAliases(transMappingRoot).iterator();

        SqlAlias nextAlias = null;
        while (iter.hasNext()) {
            nextAlias = (SqlAlias)iter.next();
            EObject aliasedObject = nextAlias.getAliasedObject();
            if (aliasedObject != null) {
                if (aliasedObject.equals(eObj)) {
                    matchingAliases.add(nextAlias);
                }
            } else {
                String message = "[TransformationHelper.getSqlAliasesForSource()] WARNING:  SqlAlias has no aliased object."; //$NON-NLS-1$
                TransformationPlugin.Util.log(IStatus.WARNING, message);
                // Add the Bad aliases (those with null aliasedObject) to a list for removal.
                badAliasList.add(nextAlias);
            }
        }

        // Remove Bad aliases
        if (badAliasList.size() > 0) {
            int nBad = badAliasList.size();
            for (int i = (nBad - 1); i >= 0; i--) {
                SqlAlias alias = (SqlAlias)badAliasList.get(i);
                if (transMappingRoot != null) {
                    MappingHelper helper = getMappingHelper(transMappingRoot);
                    if (helper != null && helper instanceof SqlTransformation) {
                        SqlTransformation sqlTrans = (SqlTransformation)helper;
                        removeValueFromEList(sqlTrans, alias, sqlTrans.getAliases());
                    }
                }
            }
        }

        return matchingAliases;
    }

    /**
     * Determine if the supplied object is a SqlTransformation
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlTransformation, 'false' if not
     */
    public static boolean isSqlTransformation( Object obj ) {
        return (obj != null && obj instanceof SqlTransformation);
    }

    /**
     * Determine if the supplied object is a XQueryTransformation
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlTransformation, 'false' if not
     */
    public static boolean isXQueryTransformation( Object obj ) {
        return (obj instanceof XQueryTransformation);
    }

    /**
     * Determine if the supplied object is a SqlTable
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlTable, 'false' if not
     */
    public static boolean isSqlTable( Object obj ) {
        boolean isTable = false;
        if (obj != null && obj instanceof EObject) {
            isTable = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable((EObject)obj);
        }
        return isTable;
    }

    /**
     * Determine if the supplied object is a SqlInputSet
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlInputSet, 'false' if not
     */
    public static boolean isSqlInputSet( Object obj ) {
        boolean isInputSet = false;
        if (obj != null && obj instanceof EObject) {
            isInputSet = SqlAspectHelper.isInputSet((EObject)obj);
        }
        return isInputSet;
    }

    /**
     * Determine if the supplied object is a xml doc
     * 
     * @param obj the object to test
     * @return 'true' if object is a xml doc, 'false' if not
     */
    public static boolean isXmlDocument( Object obj ) {
        boolean isTable = false;
        if (obj != null && obj instanceof EObject) {
            isTable = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isXmlDocument((EObject)obj);
        }
        return isTable;
    }

    /**
     * Determine if the supplied object is a SqlProcedure
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlProcedure, 'false' if not
     */
    public static boolean isSqlProcedure( Object obj ) {
        boolean isProcedure = false;
        if (obj != null && obj instanceof EObject) {
            isProcedure = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure((EObject)obj);
        }
        return isProcedure;
    }

    /**
     * Determine if the supplied object is a XQueryProcedure
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlProcedure, 'false' if not
     * @since 5.0.1
     */
    public static boolean isXQueryProcedure( Object obj ) {
        boolean result = false;
        if (obj != null && obj instanceof EObject) {
            boolean isProcedure = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure((EObject)obj);
            if (isProcedure) {
                // SWJ: this is a hack, but there's no time to build a whole XQuery Aspect framework
                final String mmUri = ((EObject)obj).eClass().getEPackage().getNsURI();
                if (mmUri != null && mmUri.endsWith(XML_SERVICE_URI)) {
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Determine if the supplied object is a Virtual SqlProcedure
     * 
     * @param obj the object to test
     * @return 'true' if object is a Virtual SqlProcedure, 'false' if not
     */
    public static boolean isSqlVirtualProcedure( Object obj ) {
        boolean isVirtualProcedure = false;
        if (obj != null && obj instanceof EObject) {
            if (isSqlProcedure(obj) && isVirtual(obj)) {
                isVirtualProcedure = true;
            }
        }
        return isVirtualProcedure;
    }

    /**
     * Determine if the supplied object is a SqlProcedureParameter
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlProcedure, 'false' if not
     */
    public static boolean isSqlProcedureParameter( Object obj ) {
        boolean isProcedure = false;
        if (obj != null && obj instanceof EObject) {
            isProcedure = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureParameter((EObject)obj);
        }
        return isProcedure;
    }

    /**
     * Determine if the supplied object is a SqlProcedureParameter
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlProcedure, 'false' if not
     */
    public static boolean isSqlProcedureResultSet( Object obj ) {
        boolean isProcedure = false;
        if (obj != null && obj instanceof EObject) {
            isProcedure = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureResultSet((EObject)obj);
        }
        return isProcedure;
    }

    /**
     * Determine if the supplied object is a SqlProcedureParameter
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlProcedure, 'false' if not
     */
    public static boolean isSqlInputParameter( Object obj ) {
        boolean isProcedure = false;
        if (obj != null && obj instanceof EObject) {
            isProcedure = SqlAspectHelper.isInputParameter((EObject)obj);
        }
        return isProcedure;
    }

    /**
     * Determine if the supplied object is a SqlColumnSet
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlColumnSet, 'false' if not
     */
    public static boolean isSqlColumnSet( Object obj ) {
        boolean isTable = false;
        if (obj != null && obj instanceof EObject) {
            isTable = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumnSet((EObject)obj);
        }
        return isTable;
    }

    /**
     * Determine if the supplied object is a SqlColumn
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlColumn, 'false' if not
     */
    public static boolean isSqlColumn( Object obj ) {
        boolean isColumn = false;
        if (obj != null && obj instanceof EObject) {
            isColumn = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn((EObject)obj);
        }
        return isColumn;
    }

    /**
     * Determine if the supplied object is a non-selectable SqlColumnAspect
     * 
     * @param eObj the supplied object
     * @return 'true' if the object is a non-selectable column, 'false' otherwise
     */
    public static boolean isNonSelectableSqlColumn( Object obj ) { // NO_UCD
        boolean isNonSelectCol = false;
        if (obj != null && obj instanceof EObject) {
            SqlAspect sqlAspect = AspectManager.getSqlAspect((EObject)obj);
            if (sqlAspect instanceof SqlColumnAspect) {
                SqlColumnAspect colAspect = (SqlColumnAspect)sqlAspect;
                if (!colAspect.isSelectable((EObject)obj)) {
                    isNonSelectCol = true;
                }
            }
        }
        return isNonSelectCol;
    }

    /**
     * Determine if the supplied object is a virtual SqlTable
     * 
     * @param obj the object to test
     * @return 'true' if object is a virtual SqlTable, 'false' if not
     */
    public static boolean isVirtualSqlTable( Object obj ) {
        boolean isVirtualTable = false;
        if (obj != null && obj instanceof EObject) {
            boolean isTable = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable((EObject)obj);
            if (isTable) {
                isVirtualTable = isVirtual(obj);
            }
        }
        return isVirtualTable;
    }

    /**
     * Determine if the supplied object is a valid Sql Transformation Target Currently, these are (1) Virtual SqlTables and (2)
     * Procedures
     * 
     * @param obj the object to test
     * @return 'true' if object is a valid Sql Transformation Target, 'false' if not
     */
    public static boolean isValidSqlTransformationTarget( Object obj ) {
        boolean isValidTarget = false;
        if (obj != null && obj instanceof EObject) {
            isValidTarget = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isValidSqlTransformationTarget((EObject)obj);
            // SWJ rolled back this change because it causes Defect 22737 and break virtual procedures too
            // if( isValidTarget ) {
            // isValidTarget = TransformationHelper.isVirtualSqlTable(obj);
            // }
        }
        return isValidTarget;
    }

    /**
     * Determine if the supplied object is a valid Sql Transformation Target Currently, these are (1) Virtual SqlTables (2)
     * XMLDocuments and (2) Procedures
     * 
     * @param obj the object to test
     * @return 'true' if object is a valid Sql Transformation Target, 'false' if not
     */
    public static boolean isValidTransformationTarget( Object obj ) {
        boolean isValidTarget = false;
        if (obj != null && obj instanceof EObject) {
            isValidTarget = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isValidTransformationTarget((EObject)obj);
        }
        return isValidTarget;
    }

    /**
     * Method to determin if the supplied EObject is a valid source. Must be a Sql Table or Procedure
     * 
     * @param eObject the supplied EObject
     * @return true if the supplied EObject is a valid source, false if not.
     */
    public static boolean isValidSource( Object transformationRoot,
                                         Object obj ) {
        boolean isValid = false;

        if (transformationRoot != null && TransformationHelper.isSqlTransformationMappingRoot(transformationRoot)) {
            if ((TransformationHelper.isSqlTable(obj) && !TransformationHelper.isSqlInputSet(obj) && !TransformationHelper.isOperation(obj))
                || (TransformationHelper.isSqlProcedure(obj) && !TransformationHelper.isOperation(obj))) {
                // We need to check to make sure that the eObject is not the target
                EObject target = ((SqlTransformationMappingRoot)transformationRoot).getTarget();
                if (target != null && !target.equals(obj)) isValid = true;
            }
        }
        return isValid;
    }

    /**
     * Return the virtual model state of the specified model object.
     * 
     * @param eObject
     * @return true if model object is in virtual model.
     */
    public static boolean isVirtual( Object obj ) {
        if (obj != null && obj instanceof EObject) {
            EObject eObject = (EObject)obj;
            final Resource resource = eObject.eResource();
            if (resource instanceof EmfResource) {
                return ModelType.VIRTUAL_LITERAL.equals(((EmfResource)resource).getModelAnnotation().getModelType());
            } else if (resource == null && eObject.eIsProxy()) {
                URI theUri = ((InternalEObject)eObject).eProxyURI().trimFragment();
                if (theUri.isFile()) {
                    File newFile = new File(theUri.toFileString());
                    XMIHeader header = ModelFileUtil.getXmiHeader(newFile);
                    if (header != null && ModelType.VIRTUAL_LITERAL.equals(ModelType.get(header.getModelType()))) return true;
                }
            }
        }
        return false;
    }

    /**
     * Return the virtual model state of the specified model object.
     * 
     * @param eObject
     * @return true if model object is in virtual model.
     */
    public static boolean isPhysical( Object obj ) {
        if (obj != null && obj instanceof EObject) {
            EObject eObject = (EObject)obj;
            final Resource resource = eObject.eResource();
            if (resource instanceof EmfResource) {
                return ModelType.PHYSICAL_LITERAL.equals(((EmfResource)resource).getModelAnnotation().getModelType());
            } else if (resource == null && eObject.eIsProxy()) {
                URI theUri = ((InternalEObject)eObject).eProxyURI().trimFragment();
                if (theUri.isFile()) {
                    File newFile = new File(theUri.toFileString());
                    XMIHeader header = ModelFileUtil.getXmiHeader(newFile);
                    if (header != null && ModelType.PHYSICAL_LITERAL.equals(ModelType.get(header.getModelType()))) return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine if the supplied object is a MappingClass
     * 
     * @param obj the object to test
     * @return 'true' if object is a MappingClass, 'false' if not
     */
    public static boolean isMappingClass( Object obj ) {
        return (obj != null && obj instanceof MappingClass && !(obj instanceof StagingTable));
    }

    /**
     * Determine if the supplied object is a StagingTable
     * 
     * @param obj the object to test
     * @return 'true' if object is a StagingTable, 'false' if not
     */
    public static boolean isStagingTable( Object obj ) {
        return (obj != null && obj instanceof StagingTable);
    }

    /**
     * Determine if the supplied object is a MappingClass
     * 
     * @param obj the object to test
     * @return 'true' if object is a MappingClass, 'false' if not
     */
    public static boolean isOperation( Object obj ) {
        return (obj != null && obj instanceof Operation);
    }

    /**
     * Determine if the supplied notification is a table 'supportsUpdate' feature change.
     * 
     * @param notification the notification to test.
     * @return 'true' if notification is table 'supportsUpdate' change, 'false' if not.
     */
    public static boolean isSupportsUpdateTableChangeNotification( Notification notification ) {
        if (NotificationUtilities.isChanged(notification)) {
            if (notification.getFeature() instanceof EStructuralFeature) {
                EStructuralFeature esf = (EStructuralFeature)notification.getFeature();
                if (esf.getFeatureID() == RelationalPackage.TABLE__SUPPORTS_UPDATE) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean supportsUpdate( EObject transMappingRoot,
                                          EObject sourceTableEObject ) {
        // bmlTODO: Add logic to check if Update SQL contains the symbol for the input sourceTableEObject
        return getUpdateSqlUUIDString(transMappingRoot) != null && isUpdateAllowed(transMappingRoot);
    }

    public static boolean supportsInsert( EObject transMappingRoot,
                                          EObject sourceTableEObject ) {
        // bmlTODO: Add logic to check if Insert SQL contains the symbol for the input sourceTableEObject
        return getInsertSqlUUIDString(transMappingRoot) != null && isInsertAllowed(transMappingRoot);
    }

    public static boolean supportsDelete( EObject transMappingRoot,
                                          EObject sourceTableEObject ) {
        // bmlTODO: Add logic to check if Delete SQL contains the symbol for the input sourceTableEObject
        return getDeleteSqlUUIDString(transMappingRoot) != null && isDeleteAllowed(transMappingRoot);
    }

    public static boolean isAllowed( Object transMappingRoot,
                                     int cmdType ) {
        boolean result = false;
        if (cmdType == QueryValidator.SELECT_TRNS) {
            result = true;
        } else if (cmdType == QueryValidator.INSERT_TRNS) {
            result = isInsertAllowed(transMappingRoot);
        } else if (cmdType == QueryValidator.UPDATE_TRNS) {
            result = isUpdateAllowed(transMappingRoot);
        } else if (cmdType == QueryValidator.DELETE_TRNS) {
            result = isDeleteAllowed(transMappingRoot);
        }
        return result;
    }

    public static boolean isUpdateSqlDefault( EObject transMappingRoot ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);
        if (helper != null && helper instanceof SqlTransformation) {
            return ((SqlTransformation)helper).isUpdateSqlDefault();
        }
        return false;
    }

    public static boolean isInsertSqlDefault( EObject transMappingRoot ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);
        if (helper != null && helper instanceof SqlTransformation) {
            return ((SqlTransformation)helper).isInsertSqlDefault();
        }
        return false;
    }

    public static boolean isDeleteSqlDefault( EObject transMappingRoot ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);
        if (helper != null && helper instanceof SqlTransformation) {
            return ((SqlTransformation)helper).isDeleteSqlDefault();
        }
        return false;
    }

    public static boolean isSqlDefault( EObject transMappingRoot, // NO_UCD
                                        int cmdType ) {
        boolean result = false;
        if (cmdType == QueryValidator.SELECT_TRNS) {
            result = true;
        } else if (cmdType == QueryValidator.INSERT_TRNS) {
            result = isInsertSqlDefault(transMappingRoot);
        } else if (cmdType == QueryValidator.UPDATE_TRNS) {
            result = isUpdateSqlDefault(transMappingRoot);
        } else if (cmdType == QueryValidator.DELETE_TRNS) {
            result = isDeleteSqlDefault(transMappingRoot);
        }
        return result;
    }

    /**
     * Set the update 'isSqlDefault' flag on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param isUpdateDefault the 'isSqlDefault' boolean
     */
    public static void setUpdateSqlDefault( Object transMappingRoot,
                                            boolean isUpdateDefault,
                                            boolean isSignificant,
                                            Object txnSource ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly((EObject)transMappingRoot)) {
            // Also set UserTransformation Flags
            SqlTransformation userTrans = getUserSqlTransformation(transMappingRoot);
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_UPDATE_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                ((SqlTransformation)helper).setUpdateSqlDefault(isUpdateDefault);
                // Set on UserTransformation also
                if (userTrans != null) {
                    userTrans.setUpdateSqlDefault(isUpdateDefault);
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * Set the insert 'isSqlDefault' flag on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param isInsertDefault the 'isSqlDefault' boolean
     */
    public static void setInsertSqlDefault( Object transMappingRoot,
                                            boolean isInsertDefault,
                                            boolean isSignificant,
                                            Object txnSource ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly((EObject)transMappingRoot)) {
            // Also set UserTransformation Flags
            SqlTransformation userTrans = getUserSqlTransformation(transMappingRoot);
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_INSERT_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                ((SqlTransformation)helper).setInsertSqlDefault(isInsertDefault);
                // Set on UserTransformation also
                if (userTrans != null) {
                    userTrans.setInsertSqlDefault(isInsertDefault);
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * Set the delete 'isSqlDefault' flag on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param isDeleteDefault the 'isSqlDefault' boolean
     */
    public static void setDeleteSqlDefault( Object transMappingRoot,
                                            boolean isDeleteDefault,
                                            boolean isSignificant,
                                            Object txnSource ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly((EObject)transMappingRoot)) {
            // Also set UserTransformation Flags
            SqlTransformation userTrans = getUserSqlTransformation(transMappingRoot);
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_DELETE_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                ((SqlTransformation)helper).setDeleteSqlDefault(isDeleteDefault);
                // Set on UserTransformation also
                if (userTrans != null) {
                    userTrans.setDeleteSqlDefault(isDeleteDefault);
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    public static boolean tableSupportsUpdate( EObject targetTableEObject ) {
        boolean tableSupportsUpdate = false;
        if (isVirtualSqlTable(targetTableEObject)) {
            // Get the SUID from the Target Virtual Group.
            SqlTableAspect aspect = (SqlTableAspect)AspectManager.getSqlAspect(targetTableEObject);
            if (aspect.supportsUpdate(targetTableEObject)) {
                tableSupportsUpdate = true;
            }
        }
        return tableSupportsUpdate;
    }

    public static boolean tableIsMaterialized( EObject targetTableEObject ) {
        boolean isMaterialized = false;
        if (isVirtualSqlTable(targetTableEObject)) {
            // Get the SUID from the Target Virtual Group.
            SqlTableAspect aspect = (SqlTableAspect)AspectManager.getSqlAspect(targetTableEObject);
            if (aspect.isMaterialized(targetTableEObject)) {
                isMaterialized = true;
            }
        }
        return isMaterialized;
    }

    public static void setTableSupportsUpdate( EObject targetTableEObject,
                                               boolean supportsUpdate ) {
        if (isVirtualSqlTable(targetTableEObject)) {
            // Get the SUID from the Target Virtual Group.
            SqlTableAspect aspect = (SqlTableAspect)AspectManager.getSqlAspect(targetTableEObject);
            if (aspect != null) {
                aspect.setSupportsUpdate(targetTableEObject, supportsUpdate);
            }
        }
    }

    public static void setTableColumnsSupportsUpdate( Object targetTableObject,
                                                      boolean supportsUpdate ) {
        if (isVirtualSqlTable(targetTableObject)) {
            EObject tableEObject = (EObject)targetTableObject;
            // Get the SUID from the Target Virtual Group.
            SqlTableAspect aspect = (SqlTableAspect)AspectManager.getSqlAspect(tableEObject);
            if (aspect != null) {
                List columns = aspect.getColumns(tableEObject);
                Iterator iter = columns.iterator();
                while (iter.hasNext()) {
                    EObject colEObj = (EObject)iter.next();
                    if (colEObj != null && colEObj instanceof Column) {
                        ((Column)colEObj).setUpdateable(supportsUpdate);
                    }
                }
            }
        }
    }

    /**
     * Set the 'supports update' flag on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param supportsUpdate the 'supports update' boolean
     */
    public static void setSupportsUpdate( Object transMappingRoot,
                                          boolean supportsUpdate,
                                          boolean isSignificant,
                                          Object txnSource ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly((EObject)transMappingRoot)) {
            // Also set UserTransformation Flags
            SqlTransformation userTrans = getUserSqlTransformation(transMappingRoot);
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_UPDATE_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                ((SqlTransformation)helper).setUpdateAllowed(supportsUpdate);
                // Set on UserTransformation also
                if (userTrans != null) {
                    userTrans.setUpdateAllowed(supportsUpdate);
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * Set the 'supports insert' flag on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param supportsUpdate the 'supports insert' boolean
     */
    public static void setSupportsInsert( Object transMappingRoot,
                                          boolean supportsInsert,
                                          boolean isSignificant,
                                          Object txnSource ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly((EObject)transMappingRoot)) {
            // Also set UserTransformation Flags
            SqlTransformation userTrans = getUserSqlTransformation(transMappingRoot);
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_INSERT_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                ((SqlTransformation)helper).setInsertAllowed(supportsInsert);
                // Set on UserTransformation also
                if (userTrans != null) {
                    userTrans.setInsertAllowed(supportsInsert);
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    /**
     * Set the 'supports delete' flag on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param supportsUpdate the 'supports delete' boolean
     */
    public static void setSupportsDelete( Object transMappingRoot,
                                          boolean supportsDelete,
                                          boolean isSignificant,
                                          Object txnSource ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);

        if (helper != null && helper instanceof SqlTransformation && !isReadOnly((EObject)transMappingRoot)) {
            // Also set UserTransformation Flags
            SqlTransformation userTrans = getUserSqlTransformation(transMappingRoot);
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_DELETE_TXN_DESCRIPTION, txnSource);
            boolean succeeded = false;
            try {
                ((SqlTransformation)helper).setDeleteAllowed(supportsDelete);
                // Set on UserTransformation also
                if (userTrans != null) {
                    userTrans.setDeleteAllowed(supportsDelete);
                }
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }

    public synchronized static EObject getTransformationMappingRoot( EObject targetVirtualGroupEObject,
                                                                     boolean makeSignificant,
                                                                     boolean makeUndoable ) {
        // Throw exception if supplied target is null or invalid
        if (!TransformationHelper.isValidSqlTransformationTarget(targetVirtualGroupEObject)) {
            throw new IllegalArgumentException(NULL_OR_INVALID_TARGET);
        }

        EObject transformEObject = null;
        List allTransforms = null;

        ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(targetVirtualGroupEObject);
        if (modelContents != null) {
            allTransforms = modelContents.getTransformations(targetVirtualGroupEObject);
        }
        if (!allTransforms.isEmpty()) {
            // Should only be one....
            Object nextObj = allTransforms.iterator().next();
            if (isSqlTransformationMappingRoot(nextObj)) transformEObject = (EObject)nextObj;
            else if (isXQueryTransformationMappingRoot(nextObj)) transformEObject = (EObject)nextObj;
        } else if (!isReadOnly(targetVirtualGroupEObject)) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(makeSignificant,
                                                         makeUndoable,
                                                         "Create Transformation Mapping Root", targetVirtualGroupEObject); //$NON-NLS-1$
            boolean succeeded = false;
            // Create transformation Object
            try {
                // Create XQuery T-Root if the model is an XML Service View model
                if (isXQueryProcedure(targetVirtualGroupEObject)) {
                    transformEObject = ModelResourceContainerFactory.createNewXQueryTransformationMappingRoot(targetVirtualGroupEObject,
                                                                                                              targetVirtualGroupEObject.eResource());
                } else {
                    transformEObject = ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(targetVirtualGroupEObject,
                                                                                                           targetVirtualGroupEObject.eResource());
                }
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        return transformEObject;
    }

    public synchronized static EObject getTransformationMappingRoot( EObject targetVirtualGroupEObject ) {
        return getTransformationMappingRoot(targetVirtualGroupEObject, NOT_SIGNIFICANT, IS_UNDOABLE);

    }

    public synchronized static EObject getMappingRoot( EObject targetVirtualGroupEObject,
                                                       boolean makeSignificant,
                                                       boolean makeUndoable ) {
        // Throw exception if supplied target is null or invalid
        if (!TransformationHelper.isValidTransformationTarget(targetVirtualGroupEObject)) {
            throw new IllegalArgumentException(NULL_OR_INVALID_TARGET);
        }

        EObject transformEObject = null;
        List allTransforms = null;

        ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(targetVirtualGroupEObject);
        if (modelContents != null) {
            allTransforms = modelContents.getTransformations(targetVirtualGroupEObject);
        }
        if (!allTransforms.isEmpty()) {
            // Should only be one....
            Object nextObj = allTransforms.iterator().next();
            if (isSqlTransformationMappingRoot(nextObj)) transformEObject = (EObject)nextObj;
        } else if (!isReadOnly(targetVirtualGroupEObject)) {
            // start txn if not already in txn
            boolean requiredStart = ModelerCore.startTxn(makeSignificant,
                                                         makeUndoable,
                                                         "Create Transformation Mapping Root", targetVirtualGroupEObject); //$NON-NLS-1$
            boolean succeeded = false;
            // Create transformation Object
            try {
                if (isXmlDocument(targetVirtualGroupEObject)) {
                    transformEObject = ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(targetVirtualGroupEObject,
                                                                                                           targetVirtualGroupEObject.eResource());
                } else {
                    transformEObject = ModelResourceContainerFactory.createNewSqlTransformationMappingRoot(targetVirtualGroupEObject,
                                                                                                           targetVirtualGroupEObject.eResource());
                }
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        return transformEObject;
    }

    public synchronized static EObject getMappingRoot( EObject targetVirtualGroupEObject ) {
        return getMappingRoot(targetVirtualGroupEObject, NOT_SIGNIFICANT, IS_UNDOABLE);
    }

    public synchronized static boolean hasMappingRoot( EObject targetVirtualGroupEObject ) {
        // Throw exception if supplied target is null or invalid
        if (!TransformationHelper.isValidTransformationTarget(targetVirtualGroupEObject)) {
            throw new IllegalArgumentException(NULL_OR_INVALID_TARGET);
        }

        List allTransforms = null;

        ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(targetVirtualGroupEObject);
        if (modelContents != null) {
            allTransforms = modelContents.getTransformations(targetVirtualGroupEObject);
        }
        if (!allTransforms.isEmpty()) {
            return true;
        }

        return false;
    }

    public synchronized static boolean hasSqlTransformationMappingRoot( EObject targetVirtualGroupEObject ) {
        // Throw exception if supplied target is null or invalid
        if (!TransformationHelper.isValidTransformationTarget(targetVirtualGroupEObject)) {
            throw new IllegalArgumentException(NULL_OR_INVALID_TARGET);
        }

        List allTransforms = null;

        ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(targetVirtualGroupEObject);
        if (modelContents != null) {
            allTransforms = modelContents.getTransformations(targetVirtualGroupEObject);
        }
        if (!allTransforms.isEmpty()) {
            // Should only be one....
            Object nextObj = allTransforms.iterator().next();
            if (isSqlTransformationMappingRoot(nextObj)) {
                return true;
            }
        }

        return false;
    }

    public synchronized static boolean hasXQueryTransformationMappingRoot( EObject targetVirtualGroupEObject ) {
        // Throw exception if supplied target is null or invalid
        if (!TransformationHelper.isValidTransformationTarget(targetVirtualGroupEObject)) {
            throw new IllegalArgumentException(NULL_OR_INVALID_TARGET);
        }

        List allTransforms = null;

        ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(targetVirtualGroupEObject);
        if (modelContents != null) {
            allTransforms = modelContents.getTransformations(targetVirtualGroupEObject);
        }
        if (!allTransforms.isEmpty()) {
            // Should only be one....
            Object nextObj = allTransforms.iterator().next();
            if (isXQueryTransformationMappingRoot(nextObj)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Indicates if the given <code>EObject</code> is contained within a read-only resource.
     * 
     * @param theEObject the object being checked
     * @return <code>true</code> if the object is read-only; <code>false</code> otherwise.
     */
    public static boolean isReadOnly( EObject theEObject ) {
        // consider it read-only until proven otherwise
        boolean result = true;
        if (theEObject != null) {
            ModelResource modelResource = ModelerCore.getModelEditor().findModelResource(theEObject);
            if (modelResource != null) {
                result = ModelUtil.isIResourceReadOnly(modelResource.getResource());
            } else {
                // outside workspace
                result = false;
            }
        }
        return result;
    }

    /**
     * Get the ExternalMetadataMap for the supplied transformation MappingRoot.
     * 
     * @param transMappingRoot the transformation MappingRoot
     * @return the external map to use when resolving
     */
    public static Map getExternalMetadataMap( Object transMappingRoot, // NO_UCD
                                              int cmdType ) {
        return SqlMappingRootCache.getExternalMetadataMap(transMappingRoot, cmdType);
    }

    /**
     * Method to return the Map of External Metadata for a createUpdateProcedure.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the map of external metadata to use when resolving the query.
     */
    public static Map getExternalMapForCreateUpdateProcedure( EObject transMappingRoot, // NO_UCD
                                                              int cmdType ) {
        return SqlMappingRootCache.getExternalMapForCreateUpdateProc(transMappingRoot, cmdType);
    }

    /**
     * Get the ExternalMetadataMap for the supplied target group.
     * 
     * @param targetGroup the target group of the transformation
     * @return the external map to use when resolving
     */
    public static Map getExternalMetadataMap( final Command command,
                                              final SqlTransformationMappingRoot mappingRoot,
                                              final QueryMetadataInterface metadata )
        throws TeiidComponentException, QueryMetadataException {
        if (mappingRoot != null) {
            final Object targetGroup = mappingRoot.getTarget();
            // If the transformation has mappingClass target, use InputSet as External Metadata
            if (targetGroup instanceof MappingClass) {
                Map externalMetadata = new HashMap();
                EObject inputSet = ((MappingClass)targetGroup).getInputSet();
                if (inputSet != null) {
                    externalMetadata.putAll(getExternalMetadataMapForInputSet((InputSet)inputSet));
                }

                externalMetadata.putAll(getExternalMetadataMapForMappingClass((MappingClass)targetGroup, metadata));

                return externalMetadata;
            } else if (targetGroup instanceof Table) {
                // Generate External Map for CreateUpdateProcedureCommand
                if (command != null && command.getType() == Command.TYPE_UPDATE_PROCEDURE) {
                    return getExternalMetadataMapForUpdateProcedure((Table)targetGroup, metadata);
                }

                // If the transformation has source procedures, Generate External Map from Target Group AccessPatterns
                Map externalMetadata = new HashMap();
                externalMetadata.putAll(getExternalMetadataMapForTable((Table)targetGroup, metadata));
                return externalMetadata;
            } else if (targetGroup instanceof Procedure) {
                // Stored procedure
                return getStoredProcedureExternalMetadataMap((Procedure)targetGroup, metadata);
            } else if (targetGroup instanceof Operation) {
                // Web service operation
                return getOperationExternalMetadataMap((Operation)targetGroup, metadata);
            }
        }

        return Collections.EMPTY_MAP;
    }

    /**
     * Return the external metadata for a mapping class to procedure mapping
     * 
     * @param mc - The MappingClass
     * @param proc - GroupSymbol for the Procedure
     * @param metadata - QMI to use for metadata retrieval
     * @return the map of metadata
     * @throws MetaMatrixComponentException
     * @throws QueryMetadataException
     * @since 4.3
     */
    public static Map getExternalMetadataMapForMappingClass( final MappingClass mc,
                                                             final QueryMetadataInterface metadata ) {
        // Create external Metadata Map
        HashMap externalMetadata = new HashMap();

        return externalMetadata;
    }

    /**
     * Return the List of ResultSet Parameters from the supplied StoredProcedure
     * 
     * @param proc the StoredProcedure
     * @return the List of ResultSet Parameters
     */
    public static List getProcedureResultSetParameters( final StoredProcedure proc ) { // NO_UCD
        List resultParams = Collections.EMPTY_LIST;
        if (proc != null && proc.getProcedureID() instanceof ProcedureRecord) {
            ProcedureRecord record = (ProcedureRecord)proc.getProcedureID();
            Object resultSetID = record.getResultSetID();
            final String uriString = record.getResourcePath() + DatatypeConstants.URI_REFERENCE_DELIMITER + resultSetID;
            EObject resultSetEObj = getEObjectByURI(uriString);
            if (TransformationHelper.isSqlProcedureResultSet(resultSetEObj)) {
                MetamodelAspect aspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(resultSetEObj);
                if (aspect != null && aspect instanceof SqlColumnSetAspect) {
                    resultParams = ((SqlColumnSetAspect)aspect).getColumns(resultSetEObj);
                }
            }
        }
        return resultParams;
    }

    /**
     * Return the EObject instance using the specified URI string. This is a helper method that attempts to retrieve the model
     * entity by calling ModelerCore.getModelContainer().getEObject(uri,false); The URI strings is expected to be of the form
     * "resourcePath#uuid"
     * 
     * @param eObjectURI
     * @return
     * @since 4.1
     */
    public static EObject getEObjectByURI( final String eObjectURI ) {
        if (eObjectURI != null) {
            final URI uri = URI.createURI(eObjectURI);
            try {
                return ModelerCore.getModelContainer().getEObject(uri, true);
            } catch (CoreException err) {
                TransformationPlugin.Util.log(err);
            }

        }
        return null;
    }

    /**
     * Method to return the Map of External Metadata for a procedure.
     * 
     * @param targetGroup the virtual group for this procedure
     * @return the map of external metadata to use when resolving the query.
     */
    public static Map getExternalMetadataMapForTable( final Table targetGroup,
                                                      final QueryMetadataInterface metadata )
        throws TeiidComponentException, QueryMetadataException {
        if (targetGroup == null) {
            return Collections.EMPTY_MAP;
        }
        String targetGroupFullName = TransformationHelper.getSqlEObjectFullName(targetGroup);
        String targetGroupUUID = TransformationHelper.getSqlEObjectUUID(targetGroup);
        GroupSymbol gSymbol = new GroupSymbol(targetGroupFullName);
        Object groupID = metadata.getGroupID(targetGroupUUID);
        if (groupID != null) {
            gSymbol.setMetadataID(groupID);
        }

        // If no external metadata map was defined, set to empty map
        return Collections.EMPTY_MAP;
    }

    /**
     * Method to return the Map of External Metadata for a procedure.
     * 
     * @param targetGroup the virtual group for this procedure
     * @return the map of external metadata to use when resolving the query.
     */
    public static Map getExternalMetadataMapForUpdateProcedure( final Table targetGroup,
                                                                final QueryMetadataInterface metadata )
        throws TeiidComponentException, QueryMetadataException {
        if (targetGroup != null) {
            String targetGroupFullName = TransformationHelper.getSqlEObjectFullName(targetGroup);
            String targetGroupUUID = TransformationHelper.getSqlEObjectUUID(targetGroup);
            GroupSymbol gSymbol = new GroupSymbol(targetGroupFullName);
            Object groupID = metadata.getGroupID(targetGroupUUID);
            if (groupID != null) {
                gSymbol.setMetadataID(groupID);
                return ExternalMetadataUtil.getProcedureExternalMetadata(gSymbol, metadata);
            }
        }

        // If no external metadata map was defined, set to empty map
        return Collections.EMPTY_MAP;
    }

    /**
     * Method to return the Map of External Metadata for an InputSet.
     * 
     * @param inputSet the InputSet
     * @return the map of external metadata to use when resolving the query.
     */
    public static Map getExternalMetadataMapForInputSet( final InputSet inputSet ) {
        Map externalMetadata = new HashMap();

        // GroupSymbol (name form) for InputSet
        GroupSymbol inputSetSymbol = new GroupSymbol(ProcedureReservedWords.INPUT);

        List inputParams = inputSet.getInputParameters();

        // Create ElementSymbols for each InputParameter
        List elements = new ArrayList();
        Iterator inputParamIter1 = inputParams.iterator();
        while (inputParamIter1.hasNext()) {
            InputParameter inputParam = (InputParameter)inputParamIter1.next();
            // Create ElementSymbol from the InputParameter
            SingleElementSymbol element = TransformationSqlHelper.createElemSymbol(inputParam, inputSetSymbol);
            if (element != null) {
                elements.add(element);
            }
        }

        // populate the map
        if (elements.size() > 0) {
            externalMetadata.put(inputSetSymbol, elements);
        }

        // GroupSymbol (uuid form) for InputSet
        String uuid = ModelerCore.getObjectIdString(inputSet);
        GroupSymbol inputSetUuidSymbol = new GroupSymbol(uuid);

        // collect element symbols
        List elementSymbols = new ArrayList(elements.size());
        Iterator inputParamIter2 = inputParams.iterator();
        while (inputParamIter2.hasNext()) {
            InputParameter inputParam = (InputParameter)inputParamIter2.next();
            // Create ElementSymbol from the InputParameter
            SingleElementSymbol element = TransformationSqlHelper.createElemSymbol(inputParam, inputSetUuidSymbol);
            if (element != null) {
                elementSymbols.add(element);
            }
        }

        // populate the map
        if (elementSymbols.size() > 0) {
            externalMetadata.put(inputSetUuidSymbol, elementSymbols);
        }

        return externalMetadata;
    }

    /**
     * Method to return the Map of External Metadata for a source Procedure.
     * 
     * @param procedure the Procedure
     * @return the map of external metadata to use when resolving the query.
     */
    public static Map getExternalMetadataMapForProcedure( final Procedure procedure ) { // NO_UCD
        Map externalMetadata = new HashMap();

        // GroupSymbol (name form) for InputSet
        GroupSymbol procedureSymbol = new GroupSymbol(procedure.getName());

        List params = procedure.getParameters();

        // Create ElementSymbols for each InputParameter
        List elements = new ArrayList();
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            ProcedureParameter param = (ProcedureParameter)iter.next();
            if (param.getDirection() == DirectionKind.IN_LITERAL || param.getDirection() == DirectionKind.INOUT_LITERAL) {
                // Create ElementSymbol from the ProcedureParameter
                SingleElementSymbol element = TransformationSqlHelper.createElemSymbol(param, procedureSymbol);
                if (element != null) {
                    elements.add(element);
                }
            }
        }

        // populate the map
        if (elements.size() > 0) {
            externalMetadata.put(procedureSymbol, elements);
        }

        // GroupSymbol (uuid form) for the Procedure
        String uuid = ModelerCore.getObjectIdString(procedure);
        GroupSymbol procedureUuidSymbol = new GroupSymbol(uuid);

        // collect element symbols
        List elementSymbols = new ArrayList(elements.size());
        for (Iterator iter = params.iterator(); iter.hasNext();) {
            ProcedureParameter param = (ProcedureParameter)iter.next();
            if (param.getDirection() == DirectionKind.IN_LITERAL) {
                // Create ElementSymbol from the ProcedureParameter
                SingleElementSymbol element = TransformationSqlHelper.createElemSymbol(param, procedureUuidSymbol);
                if (element != null) {
                    elementSymbols.add(element);
                }
            }
        }

        // populate the map
        if (elementSymbols.size() > 0) {
            externalMetadata.put(procedureUuidSymbol, elementSymbols);
        }

        return externalMetadata;
    }

    /**
     * Method to return the Map of External Metadata for the supplied stored procedure. If the query has no external metadata, an
     * empty map is returned.
     * 
     * @param targetProcedure the virtual procedure for this transform
     * @return the map of external metadata to use when resolving the query.
     */
    public static Map getStoredProcedureExternalMetadataMap( final Procedure targetProcedure,
                                                             final QueryMetadataInterface metadata )
        throws TeiidComponentException, QueryMetadataException {
        if (targetProcedure != null) {
            String targetProcFullName = TransformationHelper.getSqlEObjectFullName(targetProcedure);
            GroupSymbol gSymbol = new GroupSymbol(targetProcFullName);
            return ExternalMetadataUtil.getStoredProcedureExternalMetadata(gSymbol, metadata);
        }
        // If no external metadata map was defined, set to empty map
        return Collections.EMPTY_MAP;
    }

    /**
     * Method to return the Map of External Metadata for the supplied stored procedure. If the query has no external metadata, an
     * empty map is returned.
     * 
     * @param targetOperation the virtual web service operation for this transform
     * @return the map of external metadata to use when resolving the query.
     */
    public static Map getOperationExternalMetadataMap( final Operation targetOperation,
                                                       final QueryMetadataInterface metadata )
        throws TeiidComponentException, QueryMetadataException {
        if (targetOperation != null) {
            String targetProcFullName = TransformationHelper.getSqlEObjectFullName(targetOperation);
            GroupSymbol gSymbol = new GroupSymbol(targetProcFullName);
            return ExternalMetadataUtil.getStoredProcedureExternalMetadata(gSymbol, metadata);
        }
        // If no external metadata map was defined, set to empty map
        return Collections.EMPTY_MAP;
    }

    public static boolean isTransformationMappingRoot( Object object ) {
        return (object != null && object instanceof TransformationMappingRoot);
    }

    public static boolean isTransformationMapping( Object object ) {
        return (object != null && object instanceof TransformationMapping);
    }

    public static boolean isMapping( Object object ) {
        return (object != null && object instanceof Mapping);
    }

    /* 
     * Method to determine whether the Notification source is the SqlTransformationMappingRoot
     * @param object the object to test
     * @return 'true' if the source is the SqlTransformationMappingRoot, 'false' if not.
     */
    public static boolean isSqlTransformationMappingRoot( Object object ) {
        return (object != null && object instanceof SqlTransformationMappingRoot);
    }

    /* 
     * Method to determine whether the Notification source is the SqlTransformationMappingRoot
     * @param object the object to test
     * @return 'true' if the source is the SqlTransformationMappingRoot, 'false' if not.
     */
    public static boolean isXQueryTransformationMappingRoot( Object object ) {
        return (object instanceof XQueryTransformationMappingRoot);
    }

    public static boolean isTransformationObject( Object object ) {
        boolean result = false;

        if (object instanceof EObject) {
            if (isTransformationMapping(object) || isTransformationMappingRoot(object) || isSqlTransformation(object)
                || isSqlTransformationMappingRoot(object) || isXQueryTransformation(object)
                || isXQueryTransformationMappingRoot(object)) result = true;
        }

        return result;
    }

    /* 
     * Determine whether the target group of the supplied MappingRoot is readonly (locked)
     * @param object the object to test
     * @return 'true' if the transformation target is locked, 'false' if not.
     */
    public static boolean isTargetGroupLocked( Object object ) {
        boolean isReadOnly = false;
        // supplied object is SqlTransformationMappingRoot
        if (isSqlTransformationMappingRoot(object)) {
            isReadOnly = ((SqlTransformationMappingRoot)object).isOutputReadOnly();
            // supplied object is Virtual Table, get it's mapping root first
        } else if (isVirtualSqlTable(object)) {
            EObject mappingRoot = getTransformationMappingRoot((EObject)object);
            if (mappingRoot instanceof SqlTransformationMappingRoot) {
                isReadOnly = ((SqlTransformationMappingRoot)mappingRoot).isOutputReadOnly();
            }
        }
        return isReadOnly;
    }

    /**
     * Get the source EObjects for the supplied transformation mapping root.
     * 
     * @param object the supplied transformation mapping root.
     * @return the List of Input EObject sources
     */
    public static List getSourceEObjects( final EObject mappingRoot ) {
        List sources = null;
        if (mappingRoot != null && isSqlTransformationMappingRoot(mappingRoot)) {
            sources = ((SqlTransformationMappingRoot)mappingRoot).getInputs();
        }

        if (sources == null) {
            sources = Collections.EMPTY_LIST;
        }

        return sources;
    }

    /**
     * Get the EObject that the SqlTransformationMappingRoot is linked to. This is different than the transformation target in the
     * case of VirtualProcedures.
     * 
     * @param mappingRoot the SqlTransformationMappingRoot
     * @return the virtual target
     */
    public static EObject getTransformationLinkTarget( final EObject mappingRoot ) {
        EObject target = null;
        if (isSqlTransformationMappingRoot(mappingRoot)) {
            target = ((SqlTransformationMappingRoot)mappingRoot).getTarget();
        } else if (isXQueryTransformationMappingRoot(mappingRoot)) {
            target = ((XQueryTransformationMappingRoot)mappingRoot).getTarget();
        }
        return target;
    }

    /**
     * Get the target for the supplied SqlTransformationMappingRoot. If the mappingRoot target is a procedure, the resultSet is
     * the transformation target. If the procedure does not already have a result Set, one is created.
     * 
     * @param mappingRoot the SqlTransformationMappingRoot
     * @return the transformation target
     */
    public static EObject getTransformationTarget( final Object mappingRoot ) {
        EObject result = null;
        if (mappingRoot != null && isSqlTransformationMappingRoot(mappingRoot)) {
            EObject rootTarget = ((SqlTransformationMappingRoot)mappingRoot).getTarget();
            if (isSqlProcedure(rootTarget)) {
                SqlProcedureAspect procAspect = (SqlProcedureAspect)AspectManager.getSqlAspect(rootTarget);
                result = (EObject)procAspect.getResult(rootTarget);
                // If procedure doesn't have resultSet yet, create one
                if (result == null) {
                    result = createProcResultSet(rootTarget);
                }
            } else {
                result = rootTarget;
            }
        }
        return result;
    }

    /**
     * Get the linked sources for the supplied SqlTransformationMappingRoot.
     * 
     * @param mappingRoot the SqlTransformationMappingRoot
     * @return the List of transformation sources
     */
    public static List getTransformationSources( final Object mappingRoot ) {
        List inputs = Collections.EMPTY_LIST;
        if (mappingRoot != null && isSqlTransformationMappingRoot(mappingRoot)) {
            inputs = ((SqlTransformationMappingRoot)mappingRoot).getInputs();
        }
        return inputs;
    }

    /**
     * Get the target EObject attributes for the supplied SqlTransformationMappingRoot.
     * 
     * @param mappingRoot the SqlTransformationMappingRoot
     * @return the virtual target
     */
    public static List getTransformationTargetAttributes( final EObject mappingRoot ) {
        EObject virtualTarget = getTransformationTarget(mappingRoot);
        return getTargetAttributes(virtualTarget);
    }

    /**
     * Get the target EObject attributes for the supplied table
     * 
     * @param virtualTarget the Table
     * @return the list of attributes in the table
     */
    public static List getTargetAttributes( final EObject virtualTarget ) {
        List result = null;
        if (isVirtualSqlTable(virtualTarget)) {
            result = getTableColumns(virtualTarget);
        } else if (isSqlColumnSet(virtualTarget)) {
            result = getColumnSetColumns(virtualTarget);
        }
        if (result == null || result.isEmpty()) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /**
     * Get the EObject columns for the supplied sqlTable.
     * 
     * @param sqlTable the sql table
     * @return the columns for the sqlTable
     */
    public static List getTableColumns( final EObject sqlTable ) {
        List columns = new ArrayList();
        if (sqlTable != null && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable(sqlTable)) {
            SqlTableAspect tableAspect = (SqlTableAspect)AspectManager.getSqlAspect(sqlTable);
            // Get Table Columns and add to result List
            columns.addAll(tableAspect.getColumns(sqlTable));
        }
        if (columns.isEmpty()) return Collections.EMPTY_LIST;
        return columns;
    }

    /**
     * Get the EObject columns for the supplied sqlColumnSet.
     * 
     * @param sqlColumnSet the sql ColumnSet
     * @return the columns for the ColumnSet
     */
    public static List getColumnSetColumns( final EObject sqlColumnSet ) {
        List columns = new ArrayList();
        if (sqlColumnSet != null && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumnSet(sqlColumnSet)) {
            SqlColumnSetAspect columnSetAspect = (SqlColumnSetAspect)AspectManager.getSqlAspect(sqlColumnSet);
            // Get Table Columns and add to result List
            List columnList = columnSetAspect.getColumns(sqlColumnSet);
            if (columnList != null && !columnList.isEmpty()) columns.addAll(columnSetAspect.getColumns(sqlColumnSet));
        }
        if (columns.isEmpty()) return Collections.EMPTY_LIST;
        return columns;
    }

    /**
     * Get the command for the provided command type (SELECT,INSERT,UPDATE,DELETE)
     * 
     * @param transMappingRoot the transformation mappingRoot
     * @param cmdType the commandType to get the current SQL String
     * @return the Command for the specified type
     */
    public static Command getCommand( Object transMappingRoot,
                                      int cmdType ) {
        return SqlMappingRootCache.getCommand(transMappingRoot, cmdType);
    }

    /**
     * Method to determine whether the command for the provided command type (SELECT,INSERT,UPDATE,DELETE) is parsable.
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the query valid, or false if not.
     */
    public static boolean isParsable( Object transMappingRoot,
                                      int cmdType ) {
        return SqlMappingRootCache.isParsable(transMappingRoot, cmdType);
    }

    /**
     * Method to determine whether the command for the provided command type (SELECT,INSERT,UPDATE,DELETE) is resolvable.
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the query valid, or false if not.
     */
    public static boolean isResolvable( Object transMappingRoot,
                                        int cmdType ) {
        return SqlMappingRootCache.isResolvable(transMappingRoot, cmdType);
    }

    /**
     * Method to determine whether the command for the provided command type (SELECT,INSERT,UPDATE,DELETE) is valid.
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the query valid, or false if not.
     */
    public static boolean isValid( Object transMappingRoot,
                                   int cmdType ) {
        return SqlMappingRootCache.isValid(transMappingRoot, cmdType);
    }

    /**
     * Method to determine whether the command for the provided command type (SELECT,INSERT,UPDATE,DELETE) references the supplied
     * sourceGroup
     * 
     * @param transMappingRoot the transformation mapping root.
     * @param sourceGroup the sourceGroup to test
     * @param cmdType the provided command type
     * @return true if the transformation references the source Group, or false if not.
     */
    public static boolean hasSourceGroup( Object transMappingRoot, // NO_UCD
                                          Object sourceGroup,
                                          int cmdType ) {
        return SqlMappingRootCache.hasSourceGroup(transMappingRoot, sourceGroup, cmdType);
    }

    /**
     * Method to invalidate any cached mapping root transformations that have any of the supplied sourceGroups as a source.
     * 
     * @param sourceGroups the Set of sourceGroups to test
     */
    public static void invalidateCachedRootsWithSourceGroups( Set sourceGroups ) {
        SqlMappingRootCache.invalidateRootsWithSourceGroups(sourceGroups);
    }

    /**
     * Method to invalidate any cached mapping root transformations that have any of the supplied groups as a target.
     * 
     * @param groups the Set of groups to test
     */
    public static void invalidateCachedRootsWithTargetGroups( Set groups ) {
        SqlMappingRootCache.invalidateRootsWithTargetGroups(groups);
    }

    /**
     * Method to determine whether the command for the provided command type (SELECT,INSERT,UPDATE,DELETE) has a compatible target
     * group.
     * 
     * @param transMappingRoot the transformation mapping root.
     * @param cmdType the sql command type (SELECT,INSERT,UPDATE,DELETE)
     * @return true if the target group is valid, or false if not.
     */
    public static boolean isTargetValid( Object transMappingRoot,
                                         int cmdType ) {
        return SqlMappingRootCache.isTargetValid(transMappingRoot, cmdType);
    }

    /**
     * Method to get the target Valid status for a transformation. Is null if the target is valid.
     * 
     * @param transMappingRoot the transformation mapping root.
     * @param cmdType the sql command type (SELECT,INSERT,UPDATE,DELETE)
     * @return the target Group valid status.
     */
    public static IStatus getTargetValidStatus( Object transMappingRoot,
                                                int cmdType ) {
        return SqlMappingRootCache.getTargetValidStatus(transMappingRoot, cmdType);
    }

    /**
     * Method to determine whether a QueryObject has valid (resolvable) SQL statement. Also check that the Object is a Query (NOT
     * a SetQuery)
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the query is a valid Query object, or false if not.
     */
    public static boolean isValidQuery( Object transMappingRoot ) {
        boolean result = false;
        if (SqlMappingRootCache.isSelectValid(transMappingRoot)) {
            Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            if (command instanceof Query) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Method to determine whether a QueryObject has valid (resolvable) SQL statement. Also check that the Object is an
     * {@link CreateUpdateProcedureCommand}.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return <code>true</code> if the query is a valid update procedure
     */
    public static boolean isValidUpdateProcedure( Object transMappingRoot ) {
        boolean result = false;

        if (SqlMappingRootCache.isSelectValid(transMappingRoot)) {
            Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);

            if (command instanceof CreateUpdateProcedureCommand) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Method to determine whether a QueryObject has valid (resolvable) SQL statement. Also check that the Object is a SetQuery
     * (NOT a Query)
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the query is a valid Query object, or false if not.
     */
    public static boolean isValidSetQuery( Object transMappingRoot ) {
        boolean result = false;
        if (SqlMappingRootCache.isSelectValid(transMappingRoot)) {
            Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            if (command instanceof SetQuery) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Method to determine whether a QueryObject has parsable Query SQL statement.
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the query is parsable, or false if not.
     */
    public static boolean isParsableQuery( Object transMappingRoot ) {
        boolean result = false;
        if (SqlMappingRootCache.isSelectParsable(transMappingRoot)) {
            Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            if (command instanceof Query) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Method to determine whether a QueryObject has parsable SetQuery SQL statement.
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the query is parsable, or false if not.
     */
    public static boolean isParsableSetQuery( Object transMappingRoot ) {
        boolean result = false;
        if (SqlMappingRootCache.isSelectParsable(transMappingRoot)) {
            Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            if (command instanceof SetQuery) {
                result = true;
            }
        }
        return result;
    }

    // /**
    // * Method to determine if the transformation SELECT is the default SELECT
    // *
    // * @param transMappingRoot the transformation mapping root.
    // * @return true if the Select SQL is the initial default, false if not.
    // */
    // public static boolean isInitialSelect( Object transMappingRoot ) {
    // String sqlString = TransformationHelper.getSelectSqlString(transMappingRoot);
    // return isDefaultSelect(sqlString);
    // }

    // /**
    // * Method to determine if the supplied SQL is the initial default
    // *
    // * @param sqlString the SQL string.
    // * @return true if the supplied SQL is the default select, false if not.
    // */
    // public static boolean isDefaultSelect( String sqlString ) {
    // if (sqlString == null) return false;
    //
    // StringBuffer sb = new StringBuffer(sqlString);
    // SqlStringUtil.replaceAll(sb, CR, BLANK);
    // SqlStringUtil.replaceAll(sb, TAB, BLANK);
    // SqlStringUtil.replaceAll(sb, DBL_SPACE, SPACE);
    // String str = sb.toString();
    // if (str != null && str.trim().equalsIgnoreCase(DEFAULT_SELECT)) {
    // return true;
    // }
    // return false;
    // }

    /**
     * Method to determine if the transformation SELECT is a 'SELECT xxx FROM' String
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the Select SQL is a 'SELECT xxx FROM' string, false if not.
     */
    public static boolean isSelectFromString( Object transMappingRoot ) {
        String sqlString = TransformationHelper.getSelectSqlString(transMappingRoot);
        return isSelectFromString(sqlString);
    }

    /**
     * Method to determine if the supplied SQL is a 'SELECT xxx FROM' String. If the supplied sqlString starts with 'SELECT' and
     * ends with 'FROM', this method returns true.
     * 
     * @param sqlString the SQL string.
     * @return true if the supplied SQL is a 'SELECT xxx FROM' String, false if not.
     */
    public static boolean isSelectFromString( String sqlString ) {
        boolean isSelectFrom = false;

        if (sqlString != null) {
            StringBuffer sb = new StringBuffer(sqlString);
            SqlStringUtil.replaceAll(sb, CR, BLANK);
            SqlStringUtil.replaceAll(sb, TAB, BLANK);
            SqlStringUtil.replaceAll(sb, DBL_SPACE, SPACE);
            String str = sb.toString();
            if (str != null) {
                String trimmedSQL = str.trim().toUpperCase();
                if (trimmedSQL.startsWith(SELECT) && trimmedSQL.endsWith(FROM)) {
                    isSelectFrom = true;
                }
            }
        }

        return isSelectFrom;
    }

    /**
     * Method to determine if the transformation SELECT is null or contains an "empty" string
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the Select SQL is empty, false if not.
     */
    public static boolean isEmptySelect( Object transMappingRoot ) {
        String sqlString = TransformationHelper.getSelectSqlString(transMappingRoot);
        return isEmptySelect(sqlString);
    }

    /**
     * Method to determine if the supplied SQL is null or is an "empty" string
     * 
     * @param sqlString the SQL string
     * @return true if the SQL is empty, false if not.
     */
    public static boolean isEmptySelect( String sqlString ) {
        boolean result = false;
        if (sqlString == null) {
            result = true;
        } else {
            StringBuffer sb = new StringBuffer(sqlString);
            SqlStringUtil.replaceAll(sb, CR, BLANK);
            SqlStringUtil.replaceAll(sb, TAB, BLANK);
            String str = sb.toString();
            if (str != null && str.trim().length() == 0) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Method to determine if the reconciler can be used on the supplied Query MetaObject. The query must have a validatable SQL
     * SELECT string, and it must also be a queryCommand (Query or SetQuery) for the reconciler to be able to handle it.
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the query is a valid for reconciler, or false if not.
     */
    public static boolean canUseReconciler( Object transMappingRoot ) {
        boolean canUse = false;
        boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);
        Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
        if (command != null) {
            // If the command is a query and is validatable, can reconcile
            if (isValid && command instanceof Query) {
                canUse = true;
                // If the command is a SetQuery, not required to pass validation.
                // There will be further checks in the editor on the specific segment to reconcile
            } else if (command instanceof SetQuery) {
                canUse = true;
            }
        }
        return canUse;
    }

    /**
     * Method to determine whether the command for the provided command type (SELECT,INSERT,UPDATE,DELETE) is parsable.
     * 
     * @param transMappingRoot the transformation mapping root.
     * @return true if the query valid, or false if not.
     */
    public static boolean isUnionCommand( Object transMappingRoot ) {
        boolean isUnion = false;
        Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
        if (command != null && command instanceof SetQuery) {
            isUnion = true;
        }
        return isUnion;
    }

    /**
     * Determine whether Delete is allowed on this mapping root.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return
     */
    public static boolean isDeleteAllowed( Object transMappingRoot ) {
        boolean result = false;

        EObject target = getTransformationTarget(transMappingRoot);
        // If the target supports updates, then check further
        if (tableSupportsUpdate(target)) {
            // Now check if the transformation supports delete
            MappingHelper helper = getMappingHelper(transMappingRoot);
            if (helper != null && helper instanceof SqlTransformation) {
                result = ((SqlTransformation)helper).isDeleteAllowed();
            }
        }
        return result;
    }

    /**
     * Determine whether Insert is allowed on this mapping root.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return
     */
    public static boolean isInsertAllowed( Object transMappingRoot ) {
        boolean result = false;

        EObject target = getTransformationTarget(transMappingRoot);
        // If the target supports updates, then check further
        if (tableSupportsUpdate(target)) {
            // Now check if the transformation supports insert
            MappingHelper helper = getMappingHelper(transMappingRoot);
            if (helper != null && helper instanceof SqlTransformation) {
                result = ((SqlTransformation)helper).isInsertAllowed();
            }
        }
        return result;
    }

    /**
     * Determine whether Update is allowed on this mapping root.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return
     */
    public static boolean isUpdateAllowed( Object transMappingRoot ) {
        boolean result = false;

        EObject target = getTransformationTarget(transMappingRoot);
        // If the target supports updates, then check further
        if (tableSupportsUpdate(target)) {
            // Now check if the transformation supports update
            MappingHelper helper = getMappingHelper(transMappingRoot);
            if (helper != null && helper instanceof SqlTransformation) {
                result = ((SqlTransformation)helper).isUpdateAllowed();
            }
        }
        return result;
    }

    /**
     * Get the generated procedure text string for a given SELECT query command
     * 
     * @param transMappingRoot the transformation mapping root
     * @param type the type of the statement to generate the procedure for
     * @return the generated procedure text string
     */
    public static String getGeneratedProcedureStr( Object transMappingRoot,
                                                   int type ) {
        // Get the generated procedure (if possible)
        CreateUpdateProcedureCommand generatedProc = getGeneratedProcedure(transMappingRoot, type);

        // If non-null, return the string
        if (generatedProc != null) {
            return generatedProc.toString();
        }
        return null;
    }

    /**
     * Get the generated procedure for a given SELECT query command
     * 
     * @param transMappingRoot the transformation mapping root
     * @param type the type of the statement to generate the procedure for
     * @return the generated procedure
     */
    public static CreateUpdateProcedureCommand getGeneratedProcedure( Object transMappingRoot,
                                                                      int type ) {
        CreateUpdateProcedureCommand generatedProc = null;

        if (transMappingRoot != null && isSqlTransformationMappingRoot(transMappingRoot)) {

            EObject targetGroup = getTransformationTarget(transMappingRoot);

            if (targetGroup == null) {
                return null;
            }

            boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);

            // Get the corresponding procedure type
            int procType = 0;
            if (type == QueryValidator.INSERT_TRNS) {
                procType = UpdateProcedureGenerator.INSERT_PROCEDURE;
            } else if (type == QueryValidator.UPDATE_TRNS) {
                procType = UpdateProcedureGenerator.UPDATE_PROCEDURE;
            } else if (type == QueryValidator.DELETE_TRNS) {
                procType = UpdateProcedureGenerator.DELETE_PROCEDURE;
            }

            // Get the generated procedure from generator utility
            if (isValid
                && (procType == UpdateProcedureGenerator.INSERT_PROCEDURE
                    || procType == UpdateProcedureGenerator.UPDATE_PROCEDURE || procType == UpdateProcedureGenerator.DELETE_PROCEDURE)) {
                String virtualTargetFullName = getSqlEObjectFullName(targetGroup);
                Command selectCommand = SqlMappingRootCache.getSelectCommand(transMappingRoot);
                // Can only generate procedures for Query Commands
                if (selectCommand != null && selectCommand instanceof Query) {
                    try {
                        QueryMetadataInterface resolver = TransformationMetadataFactory.getInstance().getModelerMetadata(targetGroup);
                        generatedProc = UpdateProcedureGenerator.createProcedure(procType,
                                                                                 virtualTargetFullName,
                                                                                 selectCommand,
                                                                                 resolver);
                    } catch (TeiidComponentException e) {
                        // Exception leaves generatedProc null
                        String message = "[TransformationHelper.getGeneratedProcedure()] INFO:  Couldnt generate procedure\n"; //$NON-NLS-1$
                        TransformationPlugin.Util.log(IStatus.INFO, message + e.getMessage());
                    }
                }
            }
        }
        return generatedProc;
    }

    /**
     * Get the generated procedure errors for a query
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the procedure generation errors
     */
    public static String getProcedureGenerationErrorMsg( Object transMappingRoot ) {
        String errorMsg = null;

        if (transMappingRoot != null && isSqlTransformationMappingRoot(transMappingRoot)) {

            EObject targetGroup = getTransformationTarget(transMappingRoot);

            if (targetGroup == null) {
                return null;
            }

            boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);

            // If SELECT statement is valid, attempt to get validation error message
            if (isValid) {
                Command selectCommand = SqlMappingRootCache.getSelectCommand(transMappingRoot);
                QueryMetadataInterface resolver = TransformationMetadataFactory.getInstance().getModelerMetadata(targetGroup);
                // validate that a procedure can be generated
                UpdateValidationVisitor updateVisitor = new UpdateValidationVisitor(resolver);
                PreOrderNavigator.doVisit(selectCommand, updateVisitor);
                ValidatorReport report = updateVisitor.getReport();
                if (report.hasItems()) {
                    errorMsg = report.getFailureMessage();
                }
            }
        }

        return errorMsg;
    }

    /**
     * For the provided TransformationMappingRoot, get all of the Source Attributes for the supplied Target Attribute.
     * 
     * @param targetAttr the target attribute
     * @param transMappingRoot the transformationMappingRoot
     * @return the collection of source attributes that are mapped to the target attribute
     */
    public static Collection getSourceAttributesForTargetAttr( Object targetAttr,
                                                               Object transMappingRoot ) {
        Collection sourceAttributes = null;
        if (transMappingRoot != null && TransformationHelper.isTransformationMappingRoot(transMappingRoot)) {
            Command selectCommand = TransformationHelper.getCommand(transMappingRoot, QueryValidator.SELECT_TRNS);
            // Handle non-Union queries. Get source attributes from mappings
            if (!(selectCommand instanceof SetQuery)) {
                sourceAttributes = new ArrayList();
                // Get the Attribute Mapping List
                EList attrMappings = ((MappingRoot)transMappingRoot).getNested();
                // Find the attribute mapping that has target attribute as output
                Iterator iter = attrMappings.iterator();
                while (iter.hasNext()) {
                    Mapping mapping = (Mapping)iter.next();
                    List outputs = mapping.getOutputs();
                    if (outputs != null && !outputs.isEmpty()) {
                        EObject output = (EObject)outputs.get(0);
                        if (output != null && output.equals(targetAttr)) {
                            sourceAttributes.addAll(mapping.getInputs());
                            break;
                        }
                    }
                }
                // Handle Union queries differently - (need to extract each query in the union)
            } else {
                sourceAttributes = getUnionSourceAttributesForTargetAttr(targetAttr, (SetQuery)selectCommand);
            }
        }
        if (sourceAttributes == null) {
            sourceAttributes = Collections.EMPTY_LIST;
        }

        return sourceAttributes;
    }

    /**
     * Get source Attributes from a SetQuery for the supplied target Attribute.
     * 
     * @param targetAttr the target attribute
     * @param unionQry the SetQuery (UNION) to extract sources from
     * @return the collection of source attributes
     */
    private static Collection getUnionSourceAttributesForTargetAttr( Object targetAttr,
                                                                     SetQuery unionQry ) {
        List sourceAttributes = new ArrayList();
        if (unionQry != null && unionQry.getOperation() == org.teiid.query.sql.lang.SetQuery.Operation.UNION
            && unionQry.isResolved()) {
            List projSymbolNames = TransformationSqlHelper.getProjectedSymbolNames(unionQry);
            // Find index of the symbol that matches the target attribute name
            int index = -1;
            if (TransformationHelper.isSqlColumn(targetAttr)) {
                String attrName = TransformationHelper.getSqlColumnName((EObject)targetAttr);
                // Find the matching symbol and set the index
                for (int i = 0; i < projSymbolNames.size(); i++) {
                    String symbolName = (String)projSymbolNames.get(i);
                    if (symbolName != null && symbolName.equalsIgnoreCase(attrName)) {
                        index = i;
                        break;
                    }
                }
            }
            // Get all of the component queries of the UNION and get the corresponding source attributes
            if (index != -1) {
                List queries = unionQry.getQueryCommands();
                Iterator qIter = queries.iterator();
                while (qIter.hasNext()) {
                    QueryCommand query = (QueryCommand)qIter.next();
                    List projSymbols = query.getProjectedSymbols();
                    // number of project symbols in each query in the union should be same as
                    // number of project symbols on the union, if the query is properly reconciled
                    // but there is no gaurentee so check the index in the proj of union to the proj
                    // of qury
                    if (index < projSymbols.size()) {
                        SingleElementSymbol seSymbol = (SingleElementSymbol)projSymbols.get(index);
                        // Get the ElementSymbols / corresponding EObjs
                        Collection elemSymbols = ElementCollectorVisitor.getElements(seSymbol, true);
                        Collection elemEObjs = TransformationSqlHelper.getElementSymbolEObjects(elemSymbols, query);
                        sourceAttributes.addAll(elemEObjs);
                    }
                }
            }
        }
        return sourceAttributes;
    }

    /**
     * For the provided TransformationMappingRoot, get all of the Target Attributes for the supplied Source Attribute.
     * 
     * @param sourceAttr the source attribute
     * @param transMappingRoot the transformationMappingRoot
     * @return the collection of source attributes that are mapped to the target attribute
     */
    public static Collection getTargetAttributesForSourceAttr( Object sourceAttr,
                                                               Object transMappingRoot ) {
        Collection targetAttributes = null;
        if (transMappingRoot != null && TransformationHelper.isTransformationMappingRoot(transMappingRoot)) {
            targetAttributes = new ArrayList();
            // Get the Attribute Mapping List
            EList attrMappings = ((MappingRoot)transMappingRoot).getNested();
            // Check each attribute mapping to see if it has source attribute as input
            Iterator iter = attrMappings.iterator();
            while (iter.hasNext()) {
                Mapping mapping = (Mapping)iter.next();
                List inputs = mapping.getInputs();
                if (inputs != null && !inputs.isEmpty() && inputs.contains(sourceAttr)) {
                    targetAttributes.addAll(mapping.getOutputs());
                }
            }
        }
        if (targetAttributes == null) {
            targetAttributes = Collections.EMPTY_LIST;
        }

        return targetAttributes;
    }

    /**
     * Determine if supplied sql User String is different than the current user string on the specified mapping root.
     * 
     * @return 'true' if supplied string is different, 'false' if not.
     */
    public static boolean isUserSqlDifferent( String sql,
                                              Object sqlMappingRoot,
                                              int cmdType ) {
        // set true by default
        boolean isDifferent = true;
        // get the current mapping root user String for the specified command
        String currentUserSql = null;
        switch (cmdType) {
            case QueryValidator.SELECT_TRNS:
                currentUserSql = getSelectSqlUserString(sqlMappingRoot);
                break;
            case QueryValidator.INSERT_TRNS:
                currentUserSql = getInsertSqlUserString(sqlMappingRoot);
                break;
            case QueryValidator.UPDATE_TRNS:
                currentUserSql = getUpdateSqlUserString(sqlMappingRoot);
                break;
            case QueryValidator.DELETE_TRNS:
                currentUserSql = getDeleteSqlUserString(sqlMappingRoot);
                break;
            default:
                break;
        }
        // Supplied SQL null, check whether currentSql also null
        if (sql == null) {
            if (currentUserSql == null) {
                isDifferent = false;
            }
            // Supplied SQL non-null, check whether currentSql is the same
            // --------------------------------------------------------------
            // NOTE: Cannot use equalsIgnoreCase here - the user may have
            // changed only a string literal in the sql - use equals
            // --------------------------------------------------------------
        } else if (sql.equals(currentUserSql)) {
            isDifferent = false;
        }
        return isDifferent;
    }

    // ----------------------------------------------------------
    // Private helper methods
    // ----------------------------------------------------------

    /**
     * Get the SQL Select User (text) String, given a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Select String
     */
    private static String getSelectSqlUserString( Object transMappingRoot ) {
        SqlTransformation sqlTrans = getUserSqlTransformation(transMappingRoot);
        String result = null;
        if (sqlTrans != null) {
            result = sqlTrans.getSelectSql();
        }
        return result;
    }

    /**
     * Get the SQL Insert User (text) String, given a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Insert String
     */
    private static String getInsertSqlUserString( Object transMappingRoot ) {
        SqlTransformation sqlTrans = getUserSqlTransformation(transMappingRoot);
        String result = null;
        if (sqlTrans != null) {
            result = sqlTrans.getInsertSql();
        }
        return result;
    }

    /**
     * Get the SQL Update User (text) String, given a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Update String
     */
    private static String getUpdateSqlUserString( Object transMappingRoot ) {
        SqlTransformation sqlTrans = getUserSqlTransformation(transMappingRoot);
        String result = null;
        if (sqlTrans != null) {
            result = sqlTrans.getUpdateSql();
        }
        return result;
    }

    /**
     * Get the SQL Delete User (text) String, given a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Delete String
     */
    private static String getDeleteSqlUserString( Object transMappingRoot ) {
        SqlTransformation sqlTrans = getUserSqlTransformation(transMappingRoot);
        String result = null;
        if (sqlTrans != null) {
            result = sqlTrans.getDeleteSql();
        }
        return result;
    }

    /**
     * Get the SQL Select String, given a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Select String
     */
    private static String getSelectSqlUUIDString( Object transMappingRoot ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);
        String result = null;
        if (helper != null && helper instanceof SqlTransformation) {
            result = ((SqlTransformation)helper).getSelectSql();
        }
        return result;
    }

    /**
     * Get the SQL Insert String, given a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Insert String
     */
    private static String getInsertSqlUUIDString( Object transMappingRoot ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);
        String result = null;
        if (helper != null && helper instanceof SqlTransformation) {
            result = ((SqlTransformation)helper).getInsertSql();
        }
        return result;
    }

    /**
     * Get the SQL Update String, given a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Update String
     */
    private static String getUpdateSqlUUIDString( Object transMappingRoot ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);
        String result = null;
        if (helper != null && helper instanceof SqlTransformation) {
            result = ((SqlTransformation)helper).getUpdateSql();
        }
        return result;
    }

    /**
     * Get the SQL Delete String, given a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the SQL Delete String
     */
    private static String getDeleteSqlUUIDString( Object transMappingRoot ) {
        MappingHelper helper = getMappingHelper(transMappingRoot);
        String result = null;
        if (helper != null && helper instanceof SqlTransformation) {
            result = ((SqlTransformation)helper).getDeleteSql();
        }
        return result;
    }

    /**
     * Set the SQL Select String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param selectString the SQL Select String
     */
    private static boolean setSelectSqlUUIDString( Object transMappingRoot,
                                                   String selectString,
                                                   boolean isSignificant,
                                                   Object txnSource ) {
        boolean changed = false;

        if (isSqlDifferent(transMappingRoot, QueryValidator.SELECT_TRNS, selectString, IS_UUID)) {
            MappingHelper helper = getMappingHelper(transMappingRoot);

            if (helper != null && helper instanceof SqlTransformation) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_SELECT_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    ((SqlTransformation)helper).setSelectSql(selectString);
                    changed = true;
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            changed = false;
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        // if( !changed )
        // System.out.println("  >> T-Helper tried to set UUID SQL with SAME SQL.  Command type = " + SELECT_TRNS_STRING);
        return changed;
    }

    /**
     * Set the SQL Insert String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param insertString the SQL Insert String
     */
    private static boolean setInsertSqlUUIDString( Object transMappingRoot,
                                                   String insertString,
                                                   boolean isSignificant,
                                                   Object txnSource ) {
        boolean changed = false;

        if (isSqlDifferent(transMappingRoot, QueryValidator.INSERT_TRNS, insertString, IS_UUID)) {
            MappingHelper helper = getMappingHelper(transMappingRoot);

            if (helper != null && helper instanceof SqlTransformation) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_INSERT_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    ((SqlTransformation)helper).setInsertSql(insertString);
                    changed = true;
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            changed = false;
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        // if( !changed )
        // System.out.println("  >> T-Helper tried to set UUID SQL with SAME SQL.  Command type = " + INSERT_TRNS_STRING);
        return changed;
    }

    /**
     * Set the SQL Update String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param updateString the SQL Update String
     */
    private static boolean setUpdateSqlUUIDString( Object transMappingRoot,
                                                   String updateString,
                                                   boolean isSignificant,
                                                   Object txnSource ) {
        boolean changed = false;

        if (isSqlDifferent(transMappingRoot, QueryValidator.UPDATE_TRNS, updateString, IS_UUID)) {
            MappingHelper helper = getMappingHelper(transMappingRoot);

            if (helper != null && helper instanceof SqlTransformation) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_UPDATE_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    ((SqlTransformation)helper).setUpdateSql(updateString);
                    changed = true;
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            changed = false;
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        // if( !changed )
        // System.out.println("  >> T-Helper tried to set UUID SQL with SAME SQL.  Command type = " + UPDATE_TRNS_STRING);
        return changed;
    }

    /**
     * Set the SQL Delete String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param deleteString the SQL Delete String
     */
    private static boolean setDeleteSqlUUIDString( Object transMappingRoot,
                                                   String deleteString,
                                                   boolean isSignificant,
                                                   Object txnSource ) {
        boolean changed = false;

        if (isSqlDifferent(transMappingRoot, QueryValidator.DELETE_TRNS, deleteString, IS_UUID)) {
            MappingHelper helper = getMappingHelper(transMappingRoot);

            if (helper != null && helper instanceof SqlTransformation) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_DELETE_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    if (((SqlTransformation)helper).getDeleteSql() == null) changed = true;

                    ((SqlTransformation)helper).setDeleteSql(deleteString);
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            changed = false;
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        // if( !changed )
        // System.out.println("  >> T-Helper tried to set UUID SQL with SAME SQL.  Command type = " + DELETE_TRNS_STRING);
        return changed;
    }

    /**
     * Set the SQL Select String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param selectString the SQL Select String
     */
    public static boolean setSelectSqlUserString( Object transMappingRoot,
                                                  String selectString,
                                                  boolean isSignificant,
                                                  Object txnSource ) {
        return setSelectSqlUserString(transMappingRoot, selectString, true, isSignificant, txnSource);
    }

    /**
     * Set the SQL Select String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param selectString the SQL Select String
     */
    public static boolean setSelectSqlUserString( Object transMappingRoot,
                                                  String selectString,
                                                  boolean checkIfDifferent,
                                                  boolean isSignificant,
                                                  Object txnSource ) {
        boolean changed = false;

        boolean setString = false;
        if (checkIfDifferent) {
            setString = isSqlDifferent(transMappingRoot, QueryValidator.SELECT_TRNS, selectString, IS_NOT_UUID);
        } else {
            setString = true;
        }

        if (setString) {
            SqlTransformation userSqlTrans = getUserSqlTransformation(transMappingRoot);

            if (userSqlTrans != null) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_SELECT_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    userSqlTrans.setSelectSql(SqlUtil.normalize(selectString));
                    changed = true;
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();

                        } else {
                            changed = false;
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        // if( !changed )
        // System.out.println("  >> T-Helper tried to set Stnd SQL with SAME SQL.  Command type = " + SELECT_TRNS_STRING);
        return changed;
    }

    /**
     * Set the SQL Insert String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param insertString the SQL Insert String
     */
    public static boolean setInsertSqlUserString( Object transMappingRoot,
                                                  String insertString,
                                                  boolean isSignificant,
                                                  Object txnSource ) {
        return setInsertSqlUserString(transMappingRoot, insertString, true, isSignificant, txnSource);
    }

    /**
     * Set the SQL Insert String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param insertString the SQL Insert String
     */
    public static boolean setInsertSqlUserString( Object transMappingRoot,
                                                  String insertString,
                                                  boolean checkIfDifferent,
                                                  boolean isSignificant,
                                                  Object txnSource ) {
        boolean changed = false;

        boolean setString = false;
        if (checkIfDifferent) {
            setString = isSqlDifferent(transMappingRoot, QueryValidator.INSERT_TRNS, insertString, IS_NOT_UUID);
        } else {
            setString = true;
        }

        if (setString) {
            SqlTransformation userSqlTrans = getUserSqlTransformation(transMappingRoot);

            if (userSqlTrans != null) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_INSERT_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    userSqlTrans.setInsertSql(SqlUtil.normalize(insertString));
                    changed = true;
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            changed = false;
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        // if( !changed )
        // System.out.println("  >> T-Helper tried to set Stnd SQL with SAME SQL.  Command type = " + INSERT_TRNS_STRING);
        return changed;
    }

    /**
     * Set the SQL Update String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param updateString the SQL Update String
     */
    public static boolean setUpdateSqlUserString( Object transMappingRoot,
                                                  String updateString,
                                                  boolean isSignificant,
                                                  Object txnSource ) {
        return setUpdateSqlUserString(transMappingRoot, updateString, true, isSignificant, txnSource);
    }

    /**
     * Set the SQL Update String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param updateString the SQL Update String
     */
    public static boolean setUpdateSqlUserString( Object transMappingRoot,
                                                  String updateString,
                                                  boolean checkIfDifferent,
                                                  boolean isSignificant,
                                                  Object txnSource ) {
        boolean changed = false;

        boolean setString = false;
        if (checkIfDifferent) {
            setString = isSqlDifferent(transMappingRoot, QueryValidator.UPDATE_TRNS, updateString, IS_NOT_UUID);
        } else {
            setString = true;
        }

        if (setString) {
            SqlTransformation userSqlTrans = getUserSqlTransformation(transMappingRoot);

            if (userSqlTrans != null) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_UPDATE_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    userSqlTrans.setUpdateSql(SqlUtil.normalize(updateString));
                    changed = true;
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            changed = false;
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        // if( !changed )
        // System.out.println("  >> T-Helper tried to set Stnd SQL with SAME SQL.  Command type = " + UPDATE_TRNS_STRING);
        return changed;
    }

    /**
     * Set the SQL Delete String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param deleteString the SQL Delete String
     */
    public static boolean setDeleteSqlUserString( Object transMappingRoot,
                                                  String deleteString,
                                                  boolean isSignificant,
                                                  Object txnSource ) {
        return setDeleteSqlUserString(transMappingRoot, deleteString, true, isSignificant, txnSource);
    }

    /**
     * Set the SQL Delete String on a SqlTransformationMappingRoot
     * 
     * @param transMappingRoot the transformation mapping root
     * @param deleteString the SQL Delete String
     */
    public static boolean setDeleteSqlUserString( Object transMappingRoot,
                                                  String deleteString,
                                                  boolean checkIfDifferent,
                                                  boolean isSignificant,
                                                  Object txnSource ) {
        boolean changed = false;

        boolean setString = false;
        if (checkIfDifferent) {
            setString = isSqlDifferent(transMappingRoot, QueryValidator.DELETE_TRNS, deleteString, IS_NOT_UUID);
        } else {
            setString = true;
        }

        if (setString) {
            SqlTransformation userSqlTrans = getUserSqlTransformation(transMappingRoot);

            if (userSqlTrans != null) {
                // start txn if not already in txn
                boolean requiredStart = ModelerCore.startTxn(isSignificant, IS_UNDOABLE, CHANGE_DELETE_TXN_DESCRIPTION, txnSource);
                boolean succeeded = false;
                try {
                    userSqlTrans.setDeleteSql(SqlUtil.normalize(deleteString));
                    succeeded = true;
                } finally {
                    // if we started the txn, commit it.
                    if (requiredStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
        // if( !changed )
        // System.out.println("  >> T-Helper tried to set Stnd SQL with SAME SQL.  Command type = " + DELETE_TRNS_STRING);
        return changed;
    }

    /**
     * Determine if either the supplied user SQL is different than the current string value.
     * 
     * @param transMappingRoot the transformation MappingRoot
     * @param cmdType the command type (SELECT, INSERT, UPDATE, DELETE)
     * @param sqlString the user SQL to compare to the cache
     * @param isUUID the value used to check either the user sql string or UUID sql string values
     * @return 'true' if the strings are different, 'false' otherwise.
     */
    public static boolean isSqlDifferent( final Object transMappingRoot,
                                          final int cmdType,
                                          final String sqlString,
                                          boolean isUUID ) {
        boolean isDifferent = true;
        if (transMappingRoot != null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            if (isUUID) {
                String currentUUIDSql = getUUIDSqlString(transMappingRoot, cmdType);
                isDifferent = stringsDifferent(SqlUtil.normalize(currentUUIDSql), SqlUtil.normalize(sqlString));
            } else {
                String currentSql = getSqlString(transMappingRoot, cmdType);
                isDifferent = stringsDifferent(SqlUtil.normalize(currentSql), SqlUtil.normalize(sqlString));
            }
        }
        return isDifferent;
    }

    /**
     * determine if the supplied sql Strings are different
     * 
     * @param newSql the new SQL String
     * @param oldSql the old SQL String
     * @return 'true' if strings differ, 'false' if same
     */
    private static boolean stringsDifferent( String newSql,
                                             String oldSql ) {
        boolean isDifferent = true;
        if (newSql == null) {
            if (oldSql == null) {
                isDifferent = false;
            }
        } else if (oldSql != null) {
            StringBuffer newSb = new StringBuffer(newSql.trim());
            StringBuffer oldSb = new StringBuffer(oldSql.trim());
            CoreStringUtil.replaceAll(newSb, CR, BLANK);
            CoreStringUtil.replaceAll(newSb, TAB, BLANK);
            String newSbString = CoreStringUtil.collapseWhitespace(newSb.toString());
            CoreStringUtil.replaceAll(oldSb, CR, BLANK);
            CoreStringUtil.replaceAll(oldSb, TAB, BLANK);
            String oldSbString = CoreStringUtil.collapseWhitespace(oldSb.toString());
            if (newSbString != null && newSbString.equals(oldSbString)) {
                isDifferent = false;
            }
        }
        return isDifferent;
    }

    /**
     * create a ResultSet for a Procedure if one doesnt already exist.
     * 
     * @param procEObj the Procedure EObject
     * @return the ResultSet EObject created, null if none created.
     */
    public static EObject createProcResultSet( EObject procEObj ) {
        EObject resultSet = null;
        if (isSqlProcedure(procEObj) && !isReadOnly(procEObj)) {
            // start Txn
            boolean requiredStart = ModelerCore.startTxn(IS_SIGNIFICANT, IS_UNDOABLE, "Create Result Set", procEObj); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                SqlProcedureAspect procAspect = (SqlProcedureAspect)AspectManager.getSqlAspect(procEObj);
                EObject currentResultSet = (EObject)procAspect.getResult(procEObj);
                if (currentResultSet == null) {
                    // Get the descriptor for ResultSet from the supplied Procedure
                    org.eclipse.emf.common.command.Command resultSetDesc = getProcResultSetDescriptor(procEObj);
                    // Create a new Attribute with the specified name
                    resultSet = ModelerCore.getModelEditor().createNewChildFromCommand(procEObj, resultSetDesc);
                }
                succeeded = true;
            } catch (ModelerCoreException e) {
                String message = TransformationPlugin.Util.getString("TransformationHelper.createProcResultSetError", //$NON-NLS-1$
                                                                     procEObj.toString());
                TransformationPlugin.Util.log(IStatus.ERROR, e, message);
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
        return resultSet;
    }

    /**
     * Get a ResultSet Descriptor, given a Procedure EObj
     * 
     * @param procEObj the Procedure EObject
     * @return the ResultSet descriptor, null if none found
     */
    private static org.eclipse.emf.common.command.Command getProcResultSetDescriptor( EObject procEObject ) {
        org.eclipse.emf.common.command.Command resultSetDescriptor = null;
        // ------------------------------------------------
        // Get the Descriptor for ColumnSetAspect
        // ------------------------------------------------
        // Get the valid descriptors that can be added under the targetEObject
        Collection descriptors = null;
        try {
            descriptors = ModelerCore.getModelEditor().getNewChildCommands(procEObject);
        } catch (ModelerCoreException e) {
            String message = TransformationPlugin.Util.getString("TransformationHelper.getProcResultSetDescriptorError", //$NON-NLS-1$
                                                                 procEObject.toString());
            TransformationPlugin.Util.log(IStatus.ERROR, e, message);
            return null;
        }

        // Use the first ColumnSetAspect found
        Iterator iter = descriptors.iterator();
        while (iter.hasNext()) {
            resultSetDescriptor = (org.eclipse.emf.common.command.Command)iter.next();
            EObject eObj = (EObject)resultSetDescriptor.getResult().iterator().next();
            // If the descriptor is a ColumnSetAspect, stop
            if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumnSet(eObj)) {
                break;
            }
        }
        return resultSetDescriptor;
    }

    /**
     * create a ResultSet for a Procedure if one doesnt already exist.
     * 
     * @param procEObj the Procedure EObject
     * @return the ResultSet EObject created, null if none created.
     */
    public static EObject createProcResultSetColumn( EObject procResultEObj ) {
        EObject resultSet = null;
        if (isSqlProcedureResultSet(procResultEObj) && !isReadOnly(procResultEObj)) {
            // start Txn
            boolean requiredStart = ModelerCore.startTxn(IS_SIGNIFICANT, IS_UNDOABLE, "Create Result Set", procResultEObj); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                SqlColumnSetAspect procResultAspect = (SqlColumnSetAspect)AspectManager.getSqlAspect(procResultEObj);
                List columns = procResultAspect.getColumns(procResultEObj);
                if (columns.isEmpty()) {
                    // Get the descriptor for ResultSet from the supplied Procedure
                    org.eclipse.emf.common.command.Command resultSetDesc = getProcResultSetColumnDescriptor(procResultEObj);
                    // Create a new Attribute with the specified name
                    resultSet = ModelerCore.getModelEditor().createNewChildFromCommand(procResultEObj, resultSetDesc);
                }
                succeeded = true;
            } catch (ModelerCoreException e) {
                String message = TransformationPlugin.Util.getString("TransformationHelper.createProcResultSetError", //$NON-NLS-1$
                                                                     procResultEObj.toString());
                TransformationPlugin.Util.log(IStatus.ERROR, e, message);
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
        return resultSet;
    }

    /**
     * Get a ResultSet Descriptor, given a Procedure EObj
     * 
     * @param procEObj the Procedure EObject
     * @return the ResultSet descriptor, null if none found
     */
    private static org.eclipse.emf.common.command.Command getProcResultSetColumnDescriptor( EObject procResultEObject ) {
        org.eclipse.emf.common.command.Command resultSetColumnDescriptor = null;
        // ------------------------------------------------
        // Get the Descriptor for ColumnSetAspect
        // ------------------------------------------------
        // Get the valid descriptors that can be added under the targetEObject
        Collection descriptors = null;
        try {
            descriptors = ModelerCore.getModelEditor().getNewChildCommands(procResultEObject);
        } catch (ModelerCoreException e) {
            String message = TransformationPlugin.Util.getString("TransformationHelper.getProcResultSetDescriptorError", //$NON-NLS-1$
                                                                 procResultEObject.toString());
            TransformationPlugin.Util.log(IStatus.ERROR, e, message);
            return null;
        }

        // Use the first ColumnSetAspect found
        Iterator iter = descriptors.iterator();
        while (iter.hasNext()) {
            resultSetColumnDescriptor = (org.eclipse.emf.common.command.Command)iter.next();
            EObject eObj = (EObject)resultSetColumnDescriptor.getResult().iterator().next();
            // If the descriptor is a ColumnAspect, stop
            if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(eObj)) {
                break;
            }
        }
        return resultSetColumnDescriptor;
    }

    private static void removeModelImportForSourceObject( final EmfResource targetResource,
                                                          final EObject sourceObj ) {
        if (targetResource != null && sourceObj != null) {
            EObject source = sourceObj;
            if (sourceObj instanceof SqlAlias) {
                source = ((SqlAlias)sourceObj).getAliasedObject();
            }

            // Check if the source EObject is in a different resource than the target
            List externalResources = new ArrayList();
            Resource sourceResource = source.eResource();
            if (sourceResource != targetResource && !externalResources.contains(sourceResource)) {
                externalResources.add(sourceResource);
            }

            // Check if any of the contents of the source EObject is in a
            // different resource than the target
            for (Iterator iter = source.eAllContents(); iter.hasNext();) {
                EObject eObj = (EObject)iter.next();
                sourceResource = eObj.eResource();
                if (sourceResource != targetResource && !externalResources.contains(sourceResource)) {
                    externalResources.add(sourceResource);
                }
            }

            // Attempt to remove any ModelImports for resources within the
            // external resource list. A ModelImport will only be removed if
            // no other EObject instances within the target resource reference
            // that external resource.
            for (Iterator iter = externalResources.iterator(); iter.hasNext();) {
                Resource externalResource = (Resource)iter.next();
                try {
                    ModelerCore.getModelEditor().removeModelImport(targetResource, externalResource);
                } catch (ModelerCoreException e) {
                    final String msg = TransformationPlugin.Util.getString("TransformationHelper.Error_removing_Model_Import_from_{0}_1", targetResource.getURI()); //$NON-NLS-1$
                    TransformationPlugin.Util.log(IStatus.ERROR, e, msg);
                }
            }
        }

    }

    /**
     * helper method for adding an object to an EList - uses ModelEditor
     * 
     * @param eListOwner the object that contains the elist
     * @param value the value to add to the EList
     * @param eList the EList
     */
    private static void addValueToEList( EObject eListOwner,
                                         Object value,
                                         EList eList ) {
        try {
            ModelerCore.getModelEditor().addValue(eListOwner, value, eList);
        } catch (ModelerCoreException e) {
            TransformationPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    /**
     * helper method for removing an object from an EList - uses ModelEditor
     * 
     * @param eListOwner the object that contains the elist
     * @param value the value to remove from the EList
     * @param eList the EList
     */
    private static void removeValueFromEList( EObject eListOwner,
                                              Object value,
                                              EList eList ) {
        try {
            ModelerCore.getModelEditor().removeValue(eListOwner, value, eList);
        } catch (ModelerCoreException e) {
            TransformationPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    public static void printTransformation( EObject someEObject ) { // NO_UCD
        SqlTransformationMappingRoot mappingRoot = null;
        if (someEObject instanceof TransformationMappingRoot) {
            if (isSqlTransformationMappingRoot(someEObject)) {
                mappingRoot = (SqlTransformationMappingRoot)someEObject;
            }
        }

        if (mappingRoot != null) {
            TransformationPlugin.Util.log(IStatus.ERROR, " --------------- SQL TRANSFORMATION ------------------"); //$NON-NLS-1$
            // ------------------------------------------------
            // Print the transformation Target
            // ------------------------------------------------
            EObject target = getTransformationTarget(mappingRoot);
            String targetName = "null"; //$NON-NLS-1$
            if (target != null) {
                targetName = getSqlEObjectFullName(target);
            }
            TransformationPlugin.Util.log(IStatus.ERROR, " Virtual Group Target = " + targetName); //$NON-NLS-1$  
            TransformationPlugin.Util.log(IStatus.ERROR, ""); //$NON-NLS-1$
            // ------------------------------------------------
            // Print the transformation Source Groups
            // ------------------------------------------------
            int nSources = mappingRoot.getInputs().size();
            TransformationPlugin.Util.log(IStatus.ERROR, " # transformation Sources = " + nSources); //$NON-NLS-1$
            if (nSources > 0) {
                List inputs = mappingRoot.getInputs();
                for (int i = 0; i < nSources; i++) {
                    EObject input = (EObject)inputs.get(i);
                    String inputName = "null"; //$NON-NLS-1$
                    if (input != null) {
                        inputName = getSqlEObjectFullName(input);
                    }
                    TransformationPlugin.Util.log(IStatus.ERROR, " Source " + (i + 1) + ": " + inputName); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            TransformationPlugin.Util.log(IStatus.ERROR, ""); //$NON-NLS-1$
            // ------------------------------------------------
            // Print the transformation SQL Text
            // ------------------------------------------------
            MappingHelper helper = getMappingHelper(mappingRoot);
            if (helper != null && helper instanceof SqlTransformation) {
                TransformationPlugin.Util.log(IStatus.ERROR, " === SQL (UUID Form) ==="); //$NON-NLS-1$
                SqlTransformation sHelper = (SqlTransformation)helper;
                TransformationPlugin.Util.log(IStatus.ERROR, "     SELECT SQL (UUID) = " + sHelper.getSelectSql()); //$NON-NLS-1$
                TransformationPlugin.Util.log(IStatus.ERROR, "     INSERT SQL (UUID) = " + sHelper.getInsertSql()); //$NON-NLS-1$
                TransformationPlugin.Util.log(IStatus.ERROR, "     UPDATE SQL (UUID) = " + sHelper.getUpdateSql()); //$NON-NLS-1$
                TransformationPlugin.Util.log(IStatus.ERROR, "     DELETE SQL (UUID) = " + sHelper.getDeleteSql()); //$NON-NLS-1$
                TransformationPlugin.Util.log(IStatus.ERROR, ""); //$NON-NLS-1$
                TransformationPlugin.Util.log(IStatus.ERROR, " === SQL TEXT Form) ==="); //$NON-NLS-1$
                MappingHelper subHelper = getUserSqlTransformation(mappingRoot);
                if (subHelper != null && subHelper instanceof SqlTransformation) {
                    TransformationPlugin.Util.log(IStatus.ERROR,
                                                  "     SELECT SQL (TEXT) = " + ((SqlTransformation)subHelper).getSelectSql()); //$NON-NLS-1$
                    TransformationPlugin.Util.log(IStatus.ERROR,
                                                  "     INSERT SQL (TEXT) = " + ((SqlTransformation)subHelper).getInsertSql()); //$NON-NLS-1$
                    TransformationPlugin.Util.log(IStatus.ERROR,
                                                  "     UPDATE SQL (TEXT) = " + ((SqlTransformation)subHelper).getUpdateSql()); //$NON-NLS-1$
                    TransformationPlugin.Util.log(IStatus.ERROR,
                                                  "     DELETE SQL (TEXT) = " + ((SqlTransformation)subHelper).getDeleteSql()); //$NON-NLS-1$
                    TransformationPlugin.Util.log(IStatus.ERROR, ""); //$NON-NLS-1$
                }
            }
            // ------------------------------------------------
            // Print the transformation Attribute Mappings
            // ------------------------------------------------
            List attributeMappings = AttributeMappingHelper.getAttributeMappings(mappingRoot);
            int nMappings = attributeMappings.size();
            TransformationPlugin.Util.log(IStatus.ERROR, " # Attribute Mappings = " + nMappings); //$NON-NLS-1$
            if (nMappings > 0) {
                Mapping nextMapping = null;
                for (int i = 0; i < nMappings; i++) {
                    nextMapping = (Mapping)attributeMappings.get(i);
                    List outputs = nextMapping.getOutputs();
                    List inputs = nextMapping.getInputs();
                    int nInputs = inputs.size();
                    int nOutputs = outputs.size();
                    // -------------------
                    // Output Attribute
                    // -------------------
                    TransformationPlugin.Util.log(IStatus.ERROR, "     Attribute Mapping " + (i + 1)); //$NON-NLS-1$
                    if (nOutputs > 0) {
                        EObject output = (EObject)outputs.get(0);
                        String outputName = "null"; //$NON-NLS-1$
                        if (output != null) {
                            outputName = getSqlEObjectFullName(output);
                        }
                        TransformationPlugin.Util.log(IStatus.ERROR, "       Attr Mapping Output: " + outputName); //$NON-NLS-1$
                    } else {
                        TransformationPlugin.Util.log(IStatus.ERROR, "       No Attr Mapping Outputs"); //$NON-NLS-1$
                    }
                    // -------------------
                    // Input Attributes
                    // -------------------
                    if (nInputs > 0) {
                        EObject nextEObject = null;
                        for (int iInput = 0; iInput < nInputs; iInput++) {
                            nextEObject = (EObject)inputs.get(iInput);
                            String inputName = getSqlEObjectFullName(nextEObject);
                            TransformationPlugin.Util.log(IStatus.ERROR,
                                                          "       Attr Mapping Input " + (iInput + 1) + ": " + inputName); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    } else {
                        TransformationPlugin.Util.log(IStatus.ERROR, "       No Attr Mapping Inputs"); //$NON-NLS-1$
                    }
                }
            }

            TransformationPlugin.Util.log(IStatus.INFO, " ---------------------------------------------------\n"); //$NON-NLS-1$
        }
    }

    /**
     * Return the procedure that produces this result set.
     * 
     * @param obj the object to test
     * @return 'true' if object is a SqlProcedure, 'false' if not
     */
    public static Object getSqlProcedureForResultSet( Object obj ) {
        if (obj != null && obj instanceof EObject) {
            MetamodelAspect aspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)obj);
            if (aspect != null && aspect instanceof SqlResultSetAspect) {
                return ((SqlResultSetAspect)aspect).getProcedure((EObject)obj);
            }
        }

        return null;
    }
}
