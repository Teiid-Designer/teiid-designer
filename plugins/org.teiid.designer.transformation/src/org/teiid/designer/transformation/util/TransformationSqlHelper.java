/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metadata.runtime.ColumnRecordImpl;
import org.teiid.designer.core.metadata.runtime.TableRecordImpl;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnSetAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.core.query.QueryValidator;
import org.teiid.designer.core.query.SetQueryUtil;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.metadata.runtime.ColumnRecord;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metadata.runtime.ProcedureParameterRecord;
import org.teiid.designer.metamodels.function.Function;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.metamodels.transformation.SqlAlias;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.metadata.IMetadataID;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.IElementCollectorVisitor;
import org.teiid.designer.query.sql.IGroupCollectorVisitor;
import org.teiid.designer.query.sql.IGroupsUsedByElementsVisitor;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.IReferenceCollectorVisitor;
import org.teiid.designer.query.sql.ISQLConstants;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IOrderByItem;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.query.sql.lang.util.CommandHelper;
import org.teiid.designer.query.sql.proc.IBlock;
import org.teiid.designer.query.sql.proc.ICommandStatement;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IMultipleElementSymbol;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.designer.query.sql.symbol.ISymbol;
import org.teiid.designer.transformation.PreferenceConstants;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.aspects.sql.InputParameterSqlAspect;
import org.teiid.designer.transformation.metadata.TransformationMetadataFactory;
import org.teiid.designer.transformation.validation.SqlTransformationResult;
import org.teiid.designer.transformation.validation.TransformationValidator;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeName;
import org.teiid.designer.udf.IFunctionDescriptor;
import org.teiid.designer.udf.IFunctionForm;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.udf.IFunctionLibrary.FunctionName;
import org.teiid.designer.udf.UdfManager;


/**
 * TransformationSqlHelper This class is responsible for handling sql validation, changes, etc.
 *
 * @since 8.0
 */
public class TransformationSqlHelper implements ISQLConstants {

    private static final TransformationSqlHelper INSTANCE = new TransformationSqlHelper();

    // Metadata Resolver
    // private static QueryMetadataInterface resolver = TransformationPlugin.getDefault().getTransformationMetadata();

    private static final String NEW_CONVERSION_NAME = "conversion"; //$NON-NLS-1$

    /**
     * Get the SqlTransformationHelper instance for this VM.
     * 
     * @return the singleton instance for this VM; never null
     */
    public static TransformationSqlHelper getInstance() {
        return INSTANCE;
    }
    
    /**
     * Convenience method for retrieving the query service
     * 
     * @return instance of {@link IQueryService}
     */
    public static IQueryService getQueryService() {
        return ModelerCore.getTeiidQueryService();
    }
    
    /**
     * Convenience method for retrieving a query factory
     * 
     * @return instance of {@link IQueryFactory}
     */
    public static IQueryFactory getQueryFactory() {
        return getQueryService().createQueryFactory();
    }

    /**
     * Determine if a group can be added to the current transformation SQL SELECT.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return 'true' if the SQL can be updated, 'false' if not.
     */
    public static boolean canAddGroupToSelectSql( EObject transMappingRoot ) {
        boolean canUpdate = false;
        // -----------------------------------------------------------------
        // Check whether a group can be added to the transformation SQL
        // -----------------------------------------------------------------
        if (TransformationHelper.isValidQuery(transMappingRoot) || TransformationHelper.isValidSetQuery(transMappingRoot)
            || TransformationHelper.isSelectFromString(transMappingRoot) || TransformationHelper.isEmptySelect(transMappingRoot)) {
            canUpdate = true;
        }
        return canUpdate;
    }

    /**
     * Determine if a group can be removed from the current transformation SQL SELECT.
     * 
     * @param transMappingRoot the transformation mapping root
     * @return 'true' if the SQL can be updated, 'false' if not.
     */
    public static boolean canRemoveGroupFromSelectSql( EObject transMappingRoot ) {
        boolean canUpdate = false;
        // -----------------------------------------------------------------
        // Check whether a group can be added to the transformation SQL
        // -----------------------------------------------------------------
        if (TransformationHelper.isValidQuery(transMappingRoot) || TransformationHelper.isEmptySelect(transMappingRoot)) {
            canUpdate = true;
        }
        return canUpdate;
    }

    /**
     * Update All SQL (SELECT,UPDATE,INSERT,DELETE) for the transformation when a source group is added.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlAliasGroup the SqlAlias group to add
     * @param addElemsToSelect 'true' if group elements are to be added to the select, 'false' if not.
     * @param source the txn event Source
     */
    public static void updateAllSqlOnSqlAliasGroupAdded( EObject transMappingRoot, // NO_UCD
                                                         EObject sqlAliasGroup,
                                                         boolean addElemsToSelect,
                                                         Object source ) {
        // modTODO: only the SELECT is modified currently - may extend to include other SQL(?)
        updateSelectSqlOnSqlAliasGroupAdded(transMappingRoot, sqlAliasGroup, addElemsToSelect, source);
    }

    /**
     * Update All SQL (SELECT,UPDATE,INSERT,DELETE) for the transformation when source groups are added.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlAliasGroups the List of SqlAlias groups to add
     * @param addElemsToSelect 'true' if group elements are to be added to the select, 'false' if not.
     * @param source the txn event Source
     */
    public static void updateAllSqlOnSqlAliasGroupsAdded( EObject transMappingRoot,
                                                          List sqlAliasGroups,
                                                          boolean addElemsToSelect,
                                                          Object source ) {
        // modTODO: only the SELECT is modified currently - may extend to include other SQL(?)
        updateSelectSqlOnSqlAliasGroupsAdded(transMappingRoot, sqlAliasGroups, addElemsToSelect, source);
    }

    /**
     * Update All SQL (SELECT,UPDATE,INSERT,DELETE) for the transformation when source groups are removed.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sourceEObjs the source groups removed
     */
    public static void updateAllSqlOnSqlAliasGroupsRemoved( EObject transMappingRoot,
                                                            List sqlAliasGroups,
                                                            boolean removeElemsFromSelect,
                                                            Object source ) {
        // modTODO: only the SELECT is modified currently - may extend to include other SQL(?)
        updateSelectSqlOnSqlAliasGroupsRemoved(transMappingRoot, sqlAliasGroups, removeElemsFromSelect, source);
    }

    /**
     * Update All SQL (SELECT,UPDATE,INSERT,DELETE) for the transformation when target elements are removed.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param elementEObjs the target elements removed
     */
    public static void updateAllSqlOnElementsRemoved( EObject transMappingRoot,
                                                      List elementEObjs,
                                                      Object source ) {
        // TODO: only the SELECT, GROUP BY and ORDER BY are modified currently - may extend to include other SQL(?)
        updateSqlOnElementsRemoved(transMappingRoot, elementEObjs, source);
    }

    /**
     * Update ISelect SQL for the transformation when a source group is added.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlAliasGroup the SqlAlias group to add
     * @param addElemsToSelect 'true' if group elements are to be added to the select, 'false' if not.
     * @param source the txn event Source
     */
    public static void updateSelectSqlOnSqlAliasGroupAdded( EObject transMappingRoot,
                                                            EObject sqlAliasGroup,
                                                            boolean addElemsToSelect,
                                                            Object source ) {
        if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isTable(sqlAliasGroup)) {
            // ------------------------------------------------------------------------------
            // Add the new group to the IFrom clause and its attributes to the select clause
            // WILL NOT CHANGE UNLESS ITS A PARSABLE QUERY. (DONT CHANGE SETQUERY)
            // -----------------------------------------------------------------
            if (TransformationHelper.isParsableQuery(transMappingRoot) || TransformationHelper.isEmptySelect(transMappingRoot)) {
                // Add the source Group to the IQuery IFrom Clause
                if (sqlAliasGroup != null) {
                    List groups = new ArrayList(1);
                    groups.add(sqlAliasGroup);
                    final TransformationValidator validator = new TransformationValidator(
                                                                                          (SqlTransformationMappingRoot)transMappingRoot,
                                                                                          false);
                    addSqlAliasGroupsToSelectStatement(transMappingRoot, groups, addElemsToSelect, source, validator);
                }
            }
        }
    }

    /**
     * Update ISelect SQL for the transformation when a source group is added.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sqlAliasGroups the SqlAlias groups to add
     * @param addElemsToSelect 'true' if group elements are to be added to the select, 'false' if not.
     * @param source the txn event Source
     */
    public static void updateSelectSqlOnSqlAliasGroupsAdded( EObject transMappingRoot,
                                                             List sqlAliasGroups,
                                                             boolean addElemsToSelect,
                                                             Object source ) {
        // ------------------------------------------------------------------------------
        // Add the new group to the IFrom clause and its attributes to the select clause
        // WILL NOT CHANGE UNLESS ITS A PARSABLE QUERY. (DONT CHANGE SETQUERY)
        // -----------------------------------------------------------------
        if (TransformationHelper.isParsableQuery(transMappingRoot) || TransformationHelper.isEmptySelect(transMappingRoot)
            || TransformationHelper.isSelectFromString(transMappingRoot)) {
            // Add Sources to the SELECT statement
            final TransformationValidator validator = new TransformationValidator((SqlTransformationMappingRoot)transMappingRoot,
                                                                                  false);
            addSqlAliasGroupsToSelectStatement(transMappingRoot, sqlAliasGroups, addElemsToSelect, source, validator);
        }
    }

    /**
     * Update ISelect SQL for the transformation when source groups are removed.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sources the source groups removed
     */
    public static void updateSelectSqlOnSqlAliasGroupsRemoved( EObject transMappingRoot,
                                                               List sqlAliasGroups,
                                                               boolean removeElemsFromSelect,
                                                               Object source ) {
        // ------------------------------------------------------------------------------
        // Remove the new group from the IFrom clause and its attributes
        // from the select clause
        // WILL NOT CHANGE UNLESS ITS A PARSABLE QUERY. (DONT CHANGE SETQUERY)
        // -----------------------------------------------------------------
        if (TransformationHelper.isParsableQuery(transMappingRoot)) {
            // if source is null, use this Helper as the source
            if (source == null) {
                source = getInstance();
            }
            // Remove Sources from the SELECT statement
            final TransformationValidator validator = new TransformationValidator((SqlTransformationMappingRoot)transMappingRoot,
                                                                                  false);
            removeSqlAliasGroupsFromSelectStatement(transMappingRoot, sqlAliasGroups, removeElemsFromSelect, source, validator);
        }
    }

    /**
     * Update ISelect SQL for the transformation when target elements are removed.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param elementEObjs the elements to remove from the SQL
     */
    public static void updateSqlOnElementsRemoved( EObject transMappingRoot,
                                                   List elementEObjs,
                                                   Object source ) {
        // ------------------------------------------------------------------------------
        // Remove the elements from the select clause
        // WILL NOT CHANGE UNLESS ITS A PARSABLE QUERY. (DONT CHANGE SETQUERY)
        // -----------------------------------------------------------------
        if (TransformationHelper.isParsableQuery(transMappingRoot)) {
            // if source is null, use this Helper as the source
            if (source == null) {
                source = getInstance();
            }
            // Remove Sources from the SELECT statement
            removeElementsFromStatement(transMappingRoot, elementEObjs, source);
        }
    }

    /**
     * Update UNION ISelect SQL for the transformation when source groups are added.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sourceGroups the source groups being added
     * @param addElemsToSelect 'true' if group elements are to be added to the select, 'false' if not.
     * @param source the txn event Source
     */
    public static void updateUnionSelectOnGroupsAdded( EObject transMappingRoot,
                                                       List sourceGroups,
                                                       boolean useAll,
                                                       Object txnSource ) {
        // ------------------------------------------------------------------------------
        // Modify the current SELECT SQL, adding the list of source Groups as separate
        // UNION queries.
        // WILL NOT CHANGE SQL UNLESS ITS A PARSABLE QUERY.
        // ------------------------------------------------------------------------------
        if (TransformationHelper.isParsableQuery(transMappingRoot) || TransformationHelper.isParsableSetQuery(transMappingRoot)
            || TransformationHelper.isEmptySelect(transMappingRoot)) {
            ICommand command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            if (command != null) {
                if (command instanceof IQueryCommand) {
                    ISetQuery newQuery = createSetQueryAddUnionSources((IQueryCommand)command, sourceGroups, useAll);
                    // Set the new MetaObject property
                    TransformationHelper.setSelectSqlString(transMappingRoot, newQuery.toString(), false, txnSource);
                }
            } else {
                ISetQuery newQuery = createSetQueryAddUnionSources(null, sourceGroups, useAll);
                // Set the new MetaObject property
                TransformationHelper.setSelectSqlString(transMappingRoot, newQuery.toString(), false, txnSource);
            }
        }
    }

    /**
     * Update UNION ISelect SQL for the transformation, adding the source Groups to the desired UNION segment.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sourceGroups the source groups being added
     * @param nSegment the index of the segment to add the source groups to
     * @param source the txn event Source
     */
    public static void updateUnionSelectAddGroupsToSegment( EObject transMappingRoot,
                                                            List sourceGroups,
                                                            int nSegmentIndex,
                                                            Object txnSource ) {
        // ----------------------------------------------------------------------------------------------
        // Modify the current SELECT SQL, adding the list of source Groups to the desired UNION segment
        // WILL NOT CHANGE SQL UNLESS ITS A PARSABLE UNION QUERY.
        // ----------------------------------------------------------------------------------------------
        if (TransformationHelper.isParsableSetQuery(transMappingRoot)) {
            ICommand command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            if (command != null && command instanceof ISetQuery) {
                ISetQuery newSetQuery = (ISetQuery)command.clone();
                List queries = ((ISetQuery)command).getQueryCommands();
                IQueryCommand queryCommand = (IQueryCommand)queries.get(nSegmentIndex);
                if (queryCommand instanceof IQuery) {
                    IQuery query = (IQuery)queryCommand;
                    IQuery newQuery = createQueryAddGroupsToFrom(query, sourceGroups);
                    SetQueryUtil.setQueryAtIndex(newSetQuery, nSegmentIndex, newQuery);
                    // Set the new MetaObject property
                    TransformationHelper.setSelectSqlString(transMappingRoot, newSetQuery.toString(), false, txnSource);
                }
            }
        }
    }

    /**
     * Add sources to the SELECT SQL - add groups to FROM and its elements to SELECT
     * 
     * @param transMappingRoot the transformation mapping root object.
     * @param groups the list of group objects to add.
     */
    private static void addSqlAliasGroupsToSelectStatement( EObject transMappingRoot,
                                                            List sqlAliasGroups,
                                                            boolean addElemsToSelect,
                                                            Object txnSource,
                                                            TransformationValidator validator ) {
        if (transMappingRoot == null || sqlAliasGroups == null) return;

        // If txnSource is null, then use this Helper as the txnSource
        if (txnSource == null) {
            txnSource = getInstance();
        }

        boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);
        ICommand command = SqlMappingRootCache.getSelectCommand(transMappingRoot);

        boolean doAutoExpandSelect = TransformationPlugin.getDefault().getPreferences().getBoolean(PreferenceConstants.AUTO_EXPAND_SELECT, PreferenceConstants.AUTO_EXPAND_SELECT_DEFAULT);
        if( !doAutoExpandSelect && addElemsToSelect ) {
        	doAutoExpandSelect = addElemsToSelect;
        }
        
        // --------------------------------------------------------
        // If query is resolvable, work with the LanguageObjects
        // --------------------------------------------------------
        if (isValid && command instanceof IQuery) {
            IQuery query = (IQuery)command;

            // Add new Groups to the IQuery IFrom Clause
            IQuery newQuery = createQueryAddSqlAliasGroups(query,
                                                          sqlAliasGroups,
                                                          doAutoExpandSelect,
                                                          QueryValidator.SELECT_TRNS,
                                                          validator);

            // Set the new mappingRoot SQL
            TransformationHelper.setSelectSqlString(transMappingRoot, newQuery.toString(), false, txnSource);
            // --------------------------------------------------------
            // Handle group additions for 'SELECT * FROM' or Empty SQL
            // --------------------------------------------------------
        } else if (TransformationHelper.isEmptySelect(transMappingRoot)) {

            // Add Groups to the IQuery IFrom Clause
            Object targetGrp = TransformationHelper.getTransformationLinkTarget(transMappingRoot);

            // If target is not a procedure, create default query
            if (!TransformationHelper.isSqlProcedure(targetGrp)) {
                // Create empty SELECT * FROM query
                IQuery qry = createDefaultQuery(null);
                qry = createQueryAddSqlAliasGroups(qry, sqlAliasGroups, doAutoExpandSelect, QueryValidator.SELECT_TRNS, validator);
                // Set the new mappingRoot SQL
                TransformationHelper.setSelectSqlString(transMappingRoot, qry.toString(), false, txnSource);
                // Target is a procedure
            } else {
                // One Group added
                if (sqlAliasGroups.size() == 1) {
                    SqlAlias sqlAlias = (SqlAlias)sqlAliasGroups.get(0);
                    // get aliasedObject
                    EObject aliasedEObject = sqlAlias.getAliasedObject();
                    // EObject is Procedure
                    if (TransformationHelper.isSqlProcedure(aliasedEObject)) {
                        // Create StoredProcedure
                        IStoredProcedure proc = createStoredProc(sqlAlias);
                        if (proc != null) {
                            ICreateProcedureCommand cCommand = createVirtualProcCommmandForCommand(proc);
                            // Set the new mappingRoot SQL
                            TransformationHelper.setSelectSqlString(transMappingRoot, cCommand.toString(), false, txnSource);
                        }
                    } else if( TransformationHelper.isSqlTable(aliasedEObject)) {
                        // Create empty SELECT * FROM query
                        IQuery qry = createDefaultQuery(null);
                        // Add the sqlAliases
                        qry = createQueryAddSqlAliasGroups(qry,
                                                           sqlAliasGroups,
                                                           doAutoExpandSelect,
                                                           QueryValidator.SELECT_TRNS,
                                                           validator);

                        ICreateProcedureCommand cCommand = createVirtualProcCommmandForCommand(qry);
                        // Set the new mappingRoot SQL
                        TransformationHelper.setSelectSqlString(transMappingRoot, cCommand.toString(), false, txnSource);
                    }
                    // Multiple Groups added
                } else {
                    // Create empty SELECT * FROM query
                    IQuery qry = createDefaultQuery(null);
                    qry = createQueryAddSqlAliasGroups(qry,
                                                       sqlAliasGroups,
                                                       doAutoExpandSelect,
                                                       QueryValidator.SELECT_TRNS,
                                                       validator);

                    ICreateProcedureCommand cCommand = createVirtualProcCommmandForCommand(qry);
                    // Set the new mappingRoot SQL
                    TransformationHelper.setSelectSqlString(transMappingRoot, cCommand.toString(), false, txnSource);
                }
            }
            // --------------------------------------------------------------
            // Handle group additions for 'SELECT xxxxx FROM' or Empty SQL
            // --------------------------------------------------------------
        } else if (TransformationHelper.isSelectFromString(transMappingRoot)) {
            // Get the current SQL
            StringBuffer sb = new StringBuffer(TransformationHelper.getSelectSqlString(transMappingRoot));
            sb.append(SPACE);

            // Make FromClauses from the groups passed in, add to existing SQL
            List clausesToAdd = createFromClauses(sqlAliasGroups);

            // Add new FROM clauses to existing SQL
            Iterator iter = clausesToAdd.iterator();
            while (iter.hasNext()) {
                sb.append(iter.next().toString());
                if (iter.hasNext()) {
                    sb.append(COMMA + SPACE);
                }
            }
            // Set the new mappingRoot SQL
            TransformationHelper.setSelectSqlString(transMappingRoot, sb.toString(), false, txnSource);
        }
    }

    /**
     * create a CreateUpdateProcedureCommand from a Command
     * 
     * @param command the Command
     * @return the CreateUpdateProcedureCommand
     */
    public static ICreateProcedureCommand createVirtualProcCommmandForCommand( ICommand command ) {
        IBlock block = getQueryFactory().createBlock();

        ICommandStatement cmdStmt = getQueryFactory().createCommandStatement(command);
        block.addStatement(cmdStmt);

        // Create the CreateUpdateProcedureCommand
        ICreateProcedureCommand cCommand = getQueryFactory().createCreateProcedureCommand(block);

        return cCommand;
    }

    /**
     * add the group elements to the supplied query SELECT SQL
     * 
     * @param query the query LanguageObject
     * @param addedGroups the list of groups whose elements to add to the ISelect Clause
     */
    private static IQuery createQueryAddSqlAliasGroupElemsToSelect( IQuery resolvedQuery,
                                                                   List addedSqlAliasGroups ) {
        IQuery result = null;
        // If the IQuery is a SELECT *, not necessary to add
        if (!isSelectStar(resolvedQuery.getSelect())) {
            result = (IQuery)resolvedQuery.clone();

            // Get the current query ISelect Clause
            ISelect select = resolvedQuery.getSelect();
            List currentSelectSymbols = select.getSymbols();

            // Get the new Group Elements
            List newElementSymbols = createElemSymbols(addedSqlAliasGroups);

            // List of new ISelect Symbols to create
            List selectSymbols = new ArrayList();
            selectSymbols.addAll(currentSelectSymbols);
            selectSymbols.addAll(newElementSymbols);

            List newSelectSymbols = renameConflictingSymbols(selectSymbols);

            // Replace the ISelect with newSymbols, default to SELECT * if no symbols
            if (newSelectSymbols.size() == 0) {
                newSelectSymbols.add(getQueryFactory().createMultipleElementSymbol());
                select.setSymbols(newSelectSymbols);
            } else {
                select.setSymbols(newSelectSymbols);
            }

            result.setSelect(select);
        }
        return result;
    }

    /**
     * add list of Groups to the IQuery FROM clause
     * 
     * @param query the query LanguageObject
     * @param addGroupList the List of groups to add to the query.
     * @param isResolvable flag indicating whether the query is resolvable
     */
    private static IQuery createQueryAddSqlAliasGroupsToFrom( IQuery resolvedQuery,
                                                             List sqlAliasGroups ) {
        IQuery result = null;

        if (resolvedQuery != null && sqlAliasGroups != null) {
            // IQuery result - Clone the input
            result = (IQuery)resolvedQuery.clone();

            // --------------------------------------------------------------------
            // Get the list of groups (AliasedMetaObjects) from the command
            // --------------------------------------------------------------------
            // Get the current query IFrom Clause
            IFrom from = resolvedQuery.getFrom();
            // Defect 19499: The query doesnt necessarily have a FROM clause (eg "SELECT 1 AS Col1, 2 AS Col2")
            IFrom newFrom;
            if (from != null) {
                newFrom = (IFrom)from.clone();
            } else {
                newFrom = getQueryFactory().createFrom();
            }

            // Make GroupSymbols from the groups passed in
            List clausesToAdd = createFromClauses(sqlAliasGroups);

            Iterator iter = clausesToAdd.iterator();
            while (iter.hasNext()) {
                IFromClause nextFromClause = (IFromClause)iter.next();
                if (!newFrom.containsGroup(((IUnaryFromClause)nextFromClause).getGroup())) {
                    newFrom.addClause(nextFromClause);
                }
            }
            result.setFrom(newFrom);
        }
        return result;
    }

    /**
     * add list of Groups EObjects to the IQuery FROM clause
     * 
     * @param query the query LanguageObject
     * @param addGroupList the List of groups to add to the query.
     * @return the modified query
     */
    private static IQuery createQueryAddGroupsToFrom( IQuery query,
                                                     List grpEObjs ) {
        IQuery result = null;

        if (query != null && grpEObjs != null) {
            // IQuery result - Clone the input
            result = (IQuery)query.clone();

            // --------------------------------------------------------------------
            // Get the current list of groups from the command
            // --------------------------------------------------------------------
            // Get the current query IFrom Clause
            IFrom from = query.getFrom();
            IFrom newFrom;
            if (from != null) {
                newFrom = (IFrom)from.clone();
            } else {
                newFrom = getQueryFactory().createFrom();
            }

            // Make FromClauses for the groups passed in
            for (int i = 0; i < grpEObjs.size(); i++) {
                EObject grpEObj = (EObject)grpEObjs.get(i);
                IFromClause fClause = createFromClause(grpEObj);
                newFrom.addClause(fClause);
            }
            result.setFrom(newFrom);
        }
        return result;
    }

    /**
     * add list of Groups to the IQuery FROM clause
     * 
     * @param query the query LanguageObject
     * @param addGroupList the List of groups to add to the query.
     */
    private static IQuery createQueryAddSqlAliasGroups( IQuery resolvedQuery,
                                                       List sqlAliasGroups,
                                                       boolean addGroupElemsToSelect,
                                                       int cmdType,
                                                       TransformationValidator validator ) {
        IQuery result = null;
        if (resolvedQuery != null && sqlAliasGroups != null) {
            // Add the new groups to the IQuery FROM clause
            result = createQueryAddSqlAliasGroupsToFrom(resolvedQuery, sqlAliasGroups);

            // Parse the newSQL
            SqlTransformationResult parserResult = TransformationValidator.parseSQL(result.toString());
            IQuery resultQuery = (IQuery)parserResult.getCommand();
            // Attempt to Resolve and Validate after adding groups
            boolean isResolvable = false;
            boolean isValid = false;
            // If parsable, Resolve
            if (resultQuery != null) {
                SqlTransformationResult resolverResult = validator.resolveCommand(resultQuery, cmdType);
                isResolvable = resolverResult.isResolvable();
                if (isResolvable) {
                    SqlTransformationResult validationResult = validator.validateCommand(resultQuery, cmdType);
                    isValid = validationResult.isValidatable();
                }
            }

            // Now add the Group Elements to the SELECT if desired
            if (isValid) {
                ISelect select = resultQuery.getSelect();
                if (isSelectStar(select)) {
                    result = createQueryFixNameConflicts(resultQuery, addGroupElemsToSelect);
                } else {
                    if (addGroupElemsToSelect) {
                        result = createQueryAddSqlAliasGroupElemsToSelect(resultQuery, sqlAliasGroups);
                    } else {
                        result = resultQuery;
                    }
                }
            }
        }
        return result;
    }

    /**
     * add list of Groups to a IQuery or SetQuery, appending the additional commands to the end.
     * 
     * @param queryCommand the incoming query command (either a IQuery or a SetQuery)
     * @param unionSourceGroups the List of groups to add as union sources
     * @param useAll 'true' if adding UNION ALL, 'false' if adding UNION
     * @return the new union (SetQuery)
     */
    private static ISetQuery createSetQueryAddUnionSources( IQueryCommand queryCommand,
                                                           List unionSourceGrps,
                                                           boolean useAll ) {
        ISetQuery result = null;
        
        result = getQueryFactory().createSetQuery(ISetQuery.Operation.UNION);
        result.setAll(useAll);
        Iterator iter = null;
        
        if (queryCommand != null) {
        	// Set the left query to the current command
            result.setLeftQuery((IQueryCommand)queryCommand.clone());

            iter = unionSourceGrps.iterator();
        } else {
        	// Case where there is NO initial query
            iter = unionSourceGrps.iterator();
            
            // Create a default query with the first source groups (left and right)
            if (iter.hasNext()) {
                EObject sourceGroup = (EObject)iter.next();
                IQuery qry = createDefaultQuery(sourceGroup);
                result.setLeftQuery(qry);
                if (iter.hasNext()) {
                    sourceGroup = (EObject)iter.next();
                    IQueryCommand right = createDefaultQuery(sourceGroup);                    
                    result.setRightQuery(right);
                }
            }

            // If there are MORE than 2 sources, go ahead and create a New query command and set it as the Left query
            if (iter.hasNext()) {
                ISetQuery unionResult = getQueryFactory().createSetQuery(ISetQuery.Operation.UNION);
                unionResult.setAll(useAll);
                unionResult.setLeftQuery(result);
                result = unionResult;
           }
        }
        
        // Add new queries for each of the source groups
        while (iter.hasNext()) {
            EObject sourceGroup = (EObject)iter.next();
            IQuery qry = createDefaultQuery(sourceGroup);
            result.setRightQuery(qry);
            if (iter.hasNext()) {
                IQueryCommand left = result;
                result = getQueryFactory().createSetQuery(ISetQuery.Operation.UNION);
                result.setAll(useAll);
                result.setLeftQuery(left);
            }
        }        
        return result;
    }

    /**
     * Remove sources from the SELECT SQL - remove groups from FROM and its elements from SELECT
     * 
     * @param transMappingRoot the transformation mapping root object.
     * @param groups the list of group objects to remove.
     */
    private static void removeSqlAliasGroupsFromSelectStatement( EObject transMappingRoot,
                                                                 List sqlAliasGroups,
                                                                 boolean removeElemsFromSelect,
                                                                 Object txnSource,
                                                                 TransformationValidator validator ) {
        if (transMappingRoot == null || sqlAliasGroups == null) return;

        // If txnSource is null, then use this Helper as the txnSource
        if (txnSource == null) {
            txnSource = getInstance();
        }

        boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);
        ICommand command = SqlMappingRootCache.getSelectCommand(transMappingRoot);

        // If query is resolvable, work with the LanguageObjects
        if (isValid && command instanceof IQuery) {
            IQuery query = (IQuery)command;
            IQuery newQuery = createQueryRemoveSqlAliasGroups(query,
                                                             sqlAliasGroups,
                                                             removeElemsFromSelect,
                                                             QueryValidator.SELECT_TRNS,
                                                             validator);

            // Set the new MetaObject property
            TransformationHelper.setSelectSqlString(transMappingRoot, newQuery.toString(), false, txnSource);
        }
    }

    /**
     * Remove elements from the SELECT SQL
     * 
     * @param transMappingRoot the transformation mapping root object.
     * @param elementEObjs the list of element EObjects to remove.
     */
    private static void removeElementsFromStatement( EObject transMappingRoot,
                                                     List elementEObjs,
                                                     Object txnSource ) {
        if (transMappingRoot == null || elementEObjs == null) return;

        // If txnSource is null, then use this Helper as the txnSource
        if (txnSource == null) {
            txnSource = getInstance();
        }

        boolean isValid = SqlMappingRootCache.isSelectValid(transMappingRoot);
        ICommand command = SqlMappingRootCache.getSelectCommand(transMappingRoot);

        // If query is resolvable, work with the LanguageObjects
        if (isValid && command instanceof IQuery) {
            IQuery query = (IQuery)command;

            // Remove Elems from the Query
            IQuery newQuery = createQueryRemoveElems(query, elementEObjs);

            // Set the new MetaObject property
            if (newQuery != null) {
                TransformationHelper.setSelectSqlString(transMappingRoot, newQuery.toString(), false, txnSource);
            }
        }
    }

    /**
     * removes the group elements from the supplied query
     * 
     * @param query the query LanguageObject
     * @param removeGroupList the list of groups whose elemets to remove from the ISelect Clause
     * @param isResolvable flag indicating whether the query is resolvable
     */
    private static IQuery createQueryRemoveSqlAliasGroupElemsFromSelect( IQuery resolvedQuery,
                                                                        List removeSqlAliasGroups ) {
        IQuery result = null;
        // If the IQuery is not a SELECT *, ask whether to delete Group attributes from SELECT
        if (hasSqlAliasGroupAttributes(resolvedQuery, removeSqlAliasGroups)) {
            result = (IQuery)resolvedQuery.clone();
            List aliasGroupSymbols = createGroupSymbols(removeSqlAliasGroups);
            // Get the current query ISelect Clause
            ISelect select = resolvedQuery.getSelect();
            List currentSelectSymbols = select.getSymbols();

            IGroupsUsedByElementsVisitor groupsUsedByElementsVisitor = getQueryService().getGroupsUsedByElementsVisitor();
            
            // List of new ISelect Symbols to create
            List newSelectSymbols = new ArrayList(currentSelectSymbols.size());

            Iterator iter = currentSelectSymbols.iterator();
            while (iter.hasNext()) {
            	IExpression selectSymbol = (IExpression)iter.next();
                // Get all Groups referenced by symbol
                Collection symbolGroups = groupsUsedByElementsVisitor.findGroups(selectSymbol);
                Iterator symbolGroupIter = symbolGroups.iterator();
                boolean removeSymbol = false;
                while (symbolGroupIter.hasNext()) {
                    IGroupSymbol symbGroup = (IGroupSymbol)symbolGroupIter.next();

                    Iterator removeGroupIter = aliasGroupSymbols.iterator();
                    while (removeGroupIter.hasNext()) {
                        IGroupSymbol removeGroupSymbol = (IGroupSymbol)removeGroupIter.next();
                        if (symbGroup.equals(removeGroupSymbol)) {
                            removeSymbol = true;
                            break;
                        }
                    }
                    if (removeSymbol) {
                        break;
                    }
                }
                if (!removeSymbol) {
                    newSelectSymbols.add(selectSymbol);
                }
            }
            // Replace the SELECT with new Symbols, default to SELECT * if no symbols left
            if (newSelectSymbols.size() == 0) {
                newSelectSymbols.add(getQueryFactory().createMultipleElementSymbol());
                select.setSymbols(newSelectSymbols);
            } else {
                select.setSymbols(newSelectSymbols);
            }
            result.setSelect(select);
        }
        return result;
    }

    /**
     * removes the elements from the supplied query
     * 
     * @param query the query LanguageObject
     * @param removeElements the list of elements to remove from the ISelect Clause
     * @param isResolvable flag indicating whether the query is resolvable
     */
    protected static IQuery createQueryRemoveElems( IQuery resolvedQuery,
                                                   List removeElements ) {
        IQuery result = resolvedQuery;

        List removeNames = new ArrayList(removeElements.size());
        Iterator iter = removeElements.iterator();
        while (iter.hasNext()) {
            Object remElem = iter.next();
            if ((remElem instanceof EObject)
                && org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isColumn((EObject)remElem)) {
                SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect((EObject)remElem);
                removeNames.add(columnAspect.getName((EObject)remElem));
            }
        }

        // If the IQuery is not a SELECT *, ask whether to delete Group attributes from SELECT
        if (hasSqlElemSymbols(resolvedQuery, removeElements)) {
            // Get the names of the Elements to remove
            result = (IQuery)resolvedQuery.clone();
            // Get the current query ISelect Clause
            ISelect select = resolvedQuery.getSelect();
            
            List<IExpression> currentSelectSymbols = CommandHelper.getProjectedSymbols(resolvedQuery);

            // List of new ISelect Symbols to create
            List newSelectSymbols = removeSymbols(currentSelectSymbols, removeNames);

            // Replace the SELECT with new Symbols, default to SELECT FROM if no symbols left
            select.setSymbols(newSelectSymbols);
            result.setSelect(select);
        }

        if (result.getGroupBy() != null) {
            // Remove any symbols from the Group By that exist in the removeElements list
            IGroupBy groupBy = result.getGroupBy();
            List currentGroupBySymbols = groupBy.getSymbols();
            List newGroupBySymbols = removeSymbols(currentGroupBySymbols, removeNames);
            groupBy.getSymbols().clear();
            groupBy.getSymbols().addAll(newGroupBySymbols);

            // If there are no symbols left, remove the group by
            if (groupBy.getCount() == 0) {
                result.setGroupBy(null);
            } else {
                result.setGroupBy(groupBy);
            }
        }

        if (result.getOrderBy() != null) {
            // Remove any symbols from the Order By that exist in the removeElements list
            IOrderBy orderBy = result.getOrderBy();
            final Iterator<IOrderByItem> iter2 = orderBy.getOrderByItems().iterator();
            while (iter2.hasNext()) {
                final IOrderByItem next = iter2.next();
                final String name = TransformationSqlHelper.getSingleElementSymbolShortName(next.getSymbol(), false);
                if (removeNames.contains(name)) {
                    iter2.remove();
                }
            }

            // If there are no symbols left remove the order by
            if (orderBy.getVariableCount() == 0) {
                result.setOrderBy(null);
            } else {
                result.setOrderBy(orderBy);
            }
        }
        return result;
    }

    // TODO: COULD BE PROBLEMATTIC!!! OrderBy and GroupBy can hold IExpressions
    private static List removeSymbols( List currentSymbols,
                                       List symbolNamesToRemove ) {
        if (currentSymbols == null || symbolNamesToRemove == null) {
            return Collections.EMPTY_LIST;
        }

        final List result = new ArrayList(currentSymbols.size());
        final Iterator iter = currentSymbols.iterator();
        while (iter.hasNext()) {
            final Object next = iter.next();
            if (next instanceof IExpression) {
            	IExpression seSymbol = (IExpression)next;
                String symName = TransformationSqlHelper.getSingleElementSymbolShortName(seSymbol, false);
                if (!symbolNamesToRemove.contains(symName)) {
                    result.add(seSymbol);
                }
            }
        }

        return result;
    }

    /**
     * removes the groups from the supplied query
     * 
     * @param query the query LanguageObject
     * @param removedGroups the list of groups to remove from the IFrom Clause
     * @param isResolvable flag indicating whether the query is resolvable
     */
    private static IQuery createQueryRemoveSqlAliasGroupsFromFrom( IQuery resolvedQuery,
                                                                  List removedSqlAliasGrps ) {
        IQuery result = null;
        if (resolvedQuery != null) {
            result = (IQuery)resolvedQuery.clone();
            // Get the current query IFrom Clause
            IFrom from = resolvedQuery.getFrom();
            List<IFromClause> currentFromClauses = from.getClauses();

            // List of new IFrom Clauses to create
            List<IFromClause> newFromClauses = new ArrayList(currentFromClauses.size());

            // Go thru the current IFrom Clauses and see if they need to be removed
            Iterator iter = currentFromClauses.iterator();
            while (iter.hasNext()) {
                IFromClause fromClause = (IFromClause)iter.next();
                boolean removeIt = false;
                Iterator removeSqlAliasGrpIter = removedSqlAliasGrps.iterator();
                while (removeSqlAliasGrpIter.hasNext()) {
                    EObject removeGroupEObj = (EObject)removeSqlAliasGrpIter.next();
                    // UnaryFromClause
                    if (fromClause instanceof IUnaryFromClause) {
                        IGroupSymbol gSymbol = ((IUnaryFromClause)fromClause).getGroup();

                        IGroupSymbol removeGroupSymbol = createGroupSymbol(removeGroupEObj);
                        if (gSymbol != null && gSymbol.equals(removeGroupSymbol)) {
                            removeIt = true;
                            break;
                        }
                        // SubqueryFromClause
                    } else if (fromClause instanceof ISubqueryFromClause) {
                        ISubqueryFromClause sqf = (ISubqueryFromClause)fromClause;
                        if (removeGroupEObj instanceof SqlAlias) {
                            if (isMatch(sqf, (SqlAlias)removeGroupEObj)) {
                                removeIt = true;
                                break;
                            }
                        }
                    }
                }
                if (!removeIt) {
                    newFromClauses.add(fromClause);
                }
            }
            // Replace the IFrom with new Clauses
            from.setClauses(newFromClauses);
            result.setFrom(from);
        }
        return result;
    }

    // Determine if the SubqueryFromClause and SqlAlias match
    private static boolean isMatch( ISubqueryFromClause subqueryFrom,
                                   SqlAlias sqlAlias ) {
        boolean isMatch = false;
        if (subqueryFrom != null && sqlAlias != null) {
            ICommand fromClauseQuery = subqueryFrom.getCommand();
            String fromClauseName = subqueryFrom.getName();
            // If StoredProcedure, add group directly to groups list
            if (fromClauseQuery instanceof IStoredProcedure) {
                IStoredProcedure fromClauseProc = (IStoredProcedure)fromClauseQuery;
                String fromClauseProcName = fromClauseProc.getProcedureCallableName();
                String sqlAliasName = sqlAlias.getAlias();
                EObject sqlAliasEObj = sqlAlias.getAliasedObject();
                // Check alias names
                if (fromClauseName != null && fromClauseName.equalsIgnoreCase(sqlAliasName)) {
                    if (TransformationHelper.isSqlProcedure(sqlAliasEObj)) {
                        IStoredProcedureInfo procInfo = getProcInfo(TransformationHelper.getSqlEObjectFullName(sqlAliasEObj),
                                                                   sqlAliasEObj);
                        String sqlAliasProcName = procInfo.getProcedureCallableName();
                        if (fromClauseProcName != null && fromClauseProcName.equalsIgnoreCase(sqlAliasProcName)) {
                            isMatch = true;
                        }
                    }
                }
            }
        }
        return isMatch;
    }

    /**
     * Remove list of Groups and it's elements from the Query
     * 
     * @param query the query LanguageObject
     * @param removeGroupList the List of groups to remove from the query.
     */
    private static IQuery createQueryRemoveSqlAliasGroups( IQuery resolvedQuery,
                                                          List sqlAliasGroups,
                                                          boolean removeGroupElemsFromSelect,
                                                          int cmdType,
                                                          TransformationValidator validator ) {
        IQuery result = null;
        if (resolvedQuery != null && sqlAliasGroups != null) {
            // If the IQuery is not a SELECT *, ask whether to delete Group attributes from SELECT
            if (!isSelectStar(resolvedQuery.getSelect()) && hasSqlAliasGroupAttributes(resolvedQuery, sqlAliasGroups)) {
                if (removeGroupElemsFromSelect) {
                    // Remove the group elements from the IQuery SELECT
                    result = createQueryRemoveSqlAliasGroupElemsFromSelect(resolvedQuery, sqlAliasGroups);
                } else {
                    result = resolvedQuery;
                }
            }

            // Null return means that no changes were made
            IQuery resultQuery = null;
            boolean isValid = false;
            if (result != null) {
                // Parse the newSQL
                SqlTransformationResult parserResult = TransformationValidator.parseSQL(result.toString());
                resultQuery = (IQuery)parserResult.getCommand();
                // Attempt to Resolve and Validate after adding groups
                boolean isResolvable = false;
                // If parsable, Resolve
                SqlTransformationResult resolverResult = validator.resolveCommand(resultQuery, cmdType);
                isResolvable = resolverResult.isResolvable();
                if (isResolvable) {
                    SqlTransformationResult validationResult = validator.validateCommand(resultQuery, cmdType);
                    isValid = validationResult.isValidatable();
                }
            } else {
                resultQuery = resolvedQuery;
                isValid = true;
            }

            // Now remove the Groups from the From
            if (isValid) {
                result = createQueryRemoveSqlAliasGroupsFromFrom(resolvedQuery, sqlAliasGroups);
            }

        }
        return result;
    }

    /**
     * This method rebuilds the IQuery Language Object in the event of a projected Symbol name conflict.
     * @param resolvedQuery 
     * @param addGroupElemsToSelect 
     * 
     * @return the modified IQuery language object
     */
    public static IQuery createQueryFixNameConflicts( IQuery resolvedQuery , boolean addGroupElemsToSelect) {
        IQuery modifiedQuery = null;
        if (resolvedQuery != null) {
            // Modified IQuery Result
            modifiedQuery = (IQuery)resolvedQuery.clone();

            if (hasProjectedSymbolNameConflict(resolvedQuery)) {
                // Get the current symbols for the Query
                List currentSymbols = resolvedQuery.getSelect().getSymbols();

                // Fix name conflicts
                List newSymbols = renameConflictingSymbols(currentSymbols);

                // Reset the ISelect Symbols
                ISelect newSelect = getQueryFactory().createSelect(newSymbols);
                modifiedQuery.setSelect(newSelect);
            } else if( addGroupElemsToSelect ) {
            	List currentSelectSymbols = resolvedQuery.getSelect().getSymbols();
                if (currentSelectSymbols.size() == 1) {
                	IExpression singleSelectSymbol = (IExpression)currentSelectSymbols.get(0);
                    if (singleSelectSymbol instanceof IMultipleElementSymbol) {
                    	List<IElementSymbol> elementSymbols = ((IMultipleElementSymbol)singleSelectSymbol).getElementSymbols();
                    	// Reset the ISelect Symbols
                        ISelect newSelect = getQueryFactory().createSelect(elementSymbols);
                        modifiedQuery.setSelect(newSelect);
                    }
                }
            }
        }
        return modifiedQuery;
    }

    /**
     * This method creates a default query from the supplied source. If the supplied sourceGroup is null, the resulting query is
     * "SELECT * FROM"
     * 
     * @param source the source for the query, can be a table or procedure
     * @return the IQuery language object result
     */
    public static IQuery createDefaultQuery( EObject source ) {
        // create SELECT *
        ISelect newSelect = getQueryFactory().createSelect();
        newSelect.addSymbol(getQueryFactory().createMultipleElementSymbol());

        // Create FROM
        IFrom newFrom = getQueryFactory().createFrom();
        // Add group to FROM
        if (source != null) {
            IFromClause clause = createFromClause(source);
            if (clause != null) {
                newFrom.addClause(clause);
            }
        }

        // Create IQuery and set SELECT and FROM
        IQuery query = getQueryFactory().createQuery();
        query.setSelect(newSelect);
        query.setFrom(newFrom);

        return query;
    }

    /**
     * Determine if the supplied command has a name conflict (has duplicate short names).
     * 
     * @param command the ICommand language object
     * @return 'true' if there is a name conflict, 'false' if not.
     */
    public static boolean hasProjectedSymbolNameConflict( ICommand<IExpression, ILanguageVisitor> command ) {
        boolean hasConflict = false;
        if (command != null) {
            // maintain collection of short names
            Collection attrNames = new ArrayList();
            // Look for duplicate short names
            for (IExpression seSymbol : CommandHelper.getProjectedSymbols(command)) {
                String name = TransformationSqlHelper.getSingleElementSymbolShortName(seSymbol, false);
                // Construct unique name using symbol name and previous names
                String uniqueName = getUniqueName(name, attrNames);
                // If the short name required modification, there's a conflict.
                if (!uniqueName.equals(name)) {
                    hasConflict = true;
                    break;
                }
                attrNames.add(uniqueName);
            }
        }
        return hasConflict;
    }

    // Return a List of the Shortened Names of the Projected Symbols
    public static List<String> getProjectedSymbolNames( ICommand command ) {
        if (command == null) return Collections.EMPTY_LIST;
        // ---------------------------------------------------
        // Get the List of ProjectedSymbols from the command
        // ---------------------------------------------------
        List<IExpression> projectedSymbols = CommandHelper.getProjectedSymbols(command);
        // -------------------------------
        // Build the List of symbolNames
        // -------------------------------
        List symbolNames = null;
        if (projectedSymbols.isEmpty()) {
            symbolNames = Collections.EMPTY_LIST;
        } else {
            symbolNames = new ArrayList(projectedSymbols.size());
            // Populate the list with the symbol names
            for (IExpression symbol : projectedSymbols) {
                String shortName = getSingleElementSymbolShortName(symbol, false);
                if (shortName != null) {
                    symbolNames.add(shortName);
                }
            }
        }
        return symbolNames;
    }

    /**
     * Get the short name for a SingleElementSymbol. Includes logic to handle the case of implicit Functions, to hide the function
     * name. For IExpressionSymbols, the showExpression flag can be set 'true' to show the IExpression text or 'false' to show the
     * default name
     * 
     * @param symbol the SingleElementSymbol to get the short name
     * @param showExpression flag to determine if full IExpression text is returned, or default name
     * @return the short name for the supplied symbol
     */
    public static String getSingleElementSymbolShortName( IExpression symbol,
                                                          boolean showExpression ) {
        String symbolName = BLANK;
        
        if (symbol != null) {
            // Handle IExpressionSymbols - if Implicit Function, the function name is hidden
            if (symbol instanceof IExpressionSymbol) {
                // get the IExpression
                IExpression expr = ((IExpressionSymbol)symbol).getExpression();
                // IExpression is a Function, look for implicit function
                if (expr != null && expr instanceof IFunction) {
                    // if implicit function, use the arg symbol name, otherwise use function name
                    IFunction func = (IFunction)expr;
                    if (func.isImplicit()) {
                        IElementCollectorVisitor elementCollectorVisitor = getQueryService().getElementCollectorVisitor(true);
                        Collection elementSymbols = elementCollectorVisitor.findElements(func);
                        if (elementSymbols.size() == 1) {
                            IElementSymbol element = (IElementSymbol)elementSymbols.iterator().next();
                            symbolName = element.getShortName();
                        } else {
                            symbolName = (showExpression) ? symbol.toString() : getQueryService().getSymbolShortName(symbol);
                        }
                        // Not an implicit function
                    } else {
                        symbolName = (showExpression) ? symbol.toString() : getQueryService().getSymbolShortName(symbol);
                    }
                    // Expression not a Function
                } else {
                    symbolName = (showExpression) ? symbol.toString() : getQueryService().getSymbolShortName(symbol);
                }
            } else if (symbol instanceof IConstant) {
                symbolName = (showExpression) ? symbol.toString() : getQueryService().getSymbolShortName(symbol);
            // Not IExpression symbol, use short name
            } else {
                symbolName = getQueryService().getSymbolShortName(symbol);
            }
        }
        return symbolName;
    }

    // Return a List of Shortened QuerySelect symbol names, renaming them to make unique
    public static List<String> getProjectedSymbolUniqueNames( ICommand command ) {
        List uniqueNames = new ArrayList();
        List selectNames = getProjectedSymbolNames(command);

        Iterator iter = selectNames.iterator();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            if (!uniqueNames.contains(name)) {
                uniqueNames.add(name);
            } else {
                String uniqueName = getUniqueName(name, uniqueNames);
                uniqueNames.add(uniqueName);
            }
        }
        return uniqueNames;
    }

    /**
     * Get list of all IN or INOUT parameters from a Command
     * 
     * @param command the command languageObject
     * @return the list of SPParameters that are IN or INOUT
     */
    public static List getProcedureInputParams( ICommand command ) {
        if (command == null) return Collections.EMPTY_LIST;

        ArrayList inputParams = new ArrayList();

        // Add InputParameters for supplied command (if it's a StoredProcedure)
        if (command instanceof IStoredProcedure) {
            List procInParams = ((IStoredProcedure)command).getInputParameters();
            inputParams.addAll(procInParams);
        }

        return inputParams;
    }

    /**
     * Create a Map of unique name (key) to a EObject (value) from the supplied StoredProcedure. If a projected symbol is an
     * IElementSymbol which is resolved to an EObject, the EObject will be added to the Map.
     * 
     * @param command the supplied IStoredProcedure object.
     * @return map of unique name to EObject
     */
    public static Map getProcInputParamEObjects( IStoredProcedure storedProc ) { // NO_UCD
        Map symbolEObjMap = new HashMap();

        // Add the Procedure Input Parameters to the Map
        // (for now adding null for the EObject)
        if (storedProc != null) {
            // Get the list of InputParameters
            List inputParams = storedProc.getInputParameters();
            // add the name/Parameter to the map
            Iterator iter = inputParams.iterator();
            while (iter.hasNext()) {
                ISPParameter param = (ISPParameter)iter.next();
                IElementSymbol symbol = param.getParameterSymbol();
                String name = symbol.getShortName();
                Object eObj = getElementSymbolEObject(symbol);
                if (eObj != null) {
                    symbolEObjMap.put(name, eObj);
                }
            }
        }
        return symbolEObjMap;
    }

    /**
     * this gets a new, unique name given a string and an existing collection The name will be incremented by adding "x", where x
     * is an integer, until a unique name is found
     * 
     * @param name the String to make unique
     * @param collection the existing collection to compare against
     * @return the unique name
     */
    public static String getUniqueName( String name,
                                        Collection collection ) {
        if (collection == null) {
            collection = Collections.EMPTY_SET;
        }
        String result = name;
        int incr = 1;
        boolean nameIsInCollection = false;
        do {
            nameIsInCollection = false;
            // Perform a case-insensitive check against the collection
            for (Iterator i = collection.iterator(); i.hasNext();) {
                if (result.equalsIgnoreCase((String)i.next())) {
                    nameIsInCollection = true;
                    result = name + "_" + incr; //$NON-NLS-1$
                    incr++;
                    break;
                }
            }
        } while (nameIsInCollection);
        return result;
    }

    /**
     * Create a Map of unique name (key) to a "type" object (value). The type object may be a Datatype, if the IElementSymbol is
     * resolved. Else the type object will be a Class type.
     * 
     * @param command the supplied ICommand object.
     * @return map of unique name to type Object
     */
    public static Map getProjectedSymbolUniqueTypes( ICommand command ) {
        if (command != null) {
            Map symbolTypeMap = new HashMap();
            // Get the list of SELECT symbols
            List<IExpression> symbols = CommandHelper.getProjectedSymbols(command);
            
            // Populate the list with the symbol names
            for (IExpression symbol : symbols) {
                String name = TransformationSqlHelper.getSingleElementSymbolShortName(symbol, false);

                Object typeObj = getElementSymbolType(symbol);

                // There may be duplicates in the elementMap (self-joins,etc)
                // If so, the name is made unique first...
                Set currentNames = (symbolTypeMap.size() != 0) ? symbolTypeMap.keySet() : Collections.EMPTY_SET;
                String uniqueName = getUniqueName(name, currentNames);
                symbolTypeMap.put(uniqueName, typeObj);
            }
            return symbolTypeMap;
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * Create a Map of unique name (key) to a "type" object (value). The type object may be a Datatype, if the IElementSymbol is
     * resolved. Else the type object will be a Class type. Also get the Procedure InputParameter types as well.
     * 
     * @param command the supplied ICommand object.
     * @return map of unique name to type Object
     */
    public static Map getProjectedSymbolAndProcInputUniqueTypes( ICommand command ) {
        if (command != null) {
            // Get name-type map for the projected symbols
            Map symbolTypeMap = getProjectedSymbolUniqueTypes(command);
            // ------------------------------------------------
            // Add InputParameter names and types to the Map
            // ------------------------------------------------
            List inputParams = getProcedureInputParams(command);
            // add the name/Parameter to the map
            Iterator iter = inputParams.iterator();
            while (iter.hasNext()) {
                ISPParameter param = (ISPParameter)iter.next();
                IElementSymbol symbol = param.getParameterSymbol();
                String name = symbol.getShortName();
                Object eObj = getElementSymbolType(symbol);
                symbolTypeMap.put(name, eObj);
            }
            return symbolTypeMap;
        }
        return Collections.EMPTY_MAP;
    }

    /**
     * Create a Map of unique name (key) to a EObject (value) from the supplied Command. If a projected symbol is and
     * IElementSymbol which is resolved to an EObject, the EObject will be added to the Map.
     * 
     * @param command the supplied ICommand object.
     * @return map of unique name to length
     */
    public static Map getProjectedSymbolEObjects( ICommand command ) {
        Map symbolEObjMap = new HashMap();
        if (command != null) {
            // Get the list of SELECT symbols
            List<IExpression> symbols = CommandHelper.getProjectedSymbols(command);
            
            // Populate the list with the symbol names
            for (IExpression symbol : symbols) {
                String name = TransformationSqlHelper.getSingleElementSymbolShortName(symbol, false);
                Object eObj = null;
                if (symbol instanceof IElementSymbol) {
                    eObj = getElementSymbolEObject((IElementSymbol)symbol);
                }

                // There may be duplicates in the elementMap (self-joins,etc)
                // If so, the name is made unique first...
                Set currentNames = (symbolEObjMap.size() != 0) ? symbolEObjMap.keySet() : Collections.EMPTY_SET;
                String uniqueName = getUniqueName(name, currentNames);

                // If the EObject is not null (ElementSymbol which has been resolved), add to Map
                if (eObj != null) {
                    symbolEObjMap.put(uniqueName, eObj);
                }
            }
        }
        return symbolEObjMap;
    }

    /**
     * Create a Map of unique name (key) to a EObject (value) from the supplied Command. If a projected symbol is and
     * IElementSymbol which is resolved to an EObject, the EObject will be added to the Map.
     * 
     * @param command the supplied ICommand object.
     * @return map of unique name to length
     */
    public static Map getProjectedSymbolAndProcInputEObjects( ICommand command ) {
        // Get the projected Symbol - EObject Map
        Map projectedSymbolEObjMap = getProjectedSymbolEObjects(command);

        // Add the Procedure Input Parameters to the Map
        // (for now adding null for the EObject)
        if (command != null) {
            // Get the list of InputParameters
            List inputParams = getProcedureInputParams(command);
            // add the name/Parameter to the map
            Iterator iter = inputParams.iterator();
            while (iter.hasNext()) {
                ISPParameter param = (ISPParameter)iter.next();
                IElementSymbol symbol = param.getParameterSymbol();
                String name = symbol.getShortName();
                Object eObj = getElementSymbolEObject(symbol);
                if (eObj != null) {
                    projectedSymbolEObjMap.put(name, eObj);
                }
            }
        }
        return projectedSymbolEObjMap;
    }

    /**
     * Create a Map of unique name (key) to a length (value). If a projected symbol's type is a String Datatype, then the length
     * will be set. Otherwise the length is set to -1 in th map, to indicate a non-string Datatype.
     * 
     * @param command the supplied ICommand object.
     * @param hasXMLDocSource 'true' if the command has XML Document source
     * @return map of unique name to length
     */
    public static Map getProjectedSymbolLengths( ICommand command,
                                                 boolean hasXMLDocSource ) {
        Map symbolLengthMap = new HashMap();

        if (command != null) {
            // Get the list of SELECT symbols
            List<IExpression> symbols = CommandHelper.getProjectedSymbols(command);
            // Populate the list with the symbol names
            for (IExpression symbol : symbols) {
                String name = TransformationSqlHelper.getSingleElementSymbolShortName(symbol, false);

                Object typeObj = getElementSymbolType(symbol);
                boolean xmlDocSourceCase = hasXMLDocSource && (command instanceof ICreateProcedureCommand);

                // There may be duplicates in the elementMap (self-joins,etc)
                // If so, the name is made unique first...
                Set currentNames = (symbolLengthMap.size() != 0) ? symbolLengthMap.keySet() : Collections.EMPTY_SET;
                String uniqueName = getUniqueName(name, currentNames);

                // Put the type length in the symbolLengthMap
                updateTypeLengthMap(symbolLengthMap, uniqueName, typeObj, symbol, xmlDocSourceCase);
            }
        }
        return symbolLengthMap;
    }

    /**
     * Create a Map of unique name (key) to a length (value). If a projected symbol's type is a String Datatype, then the length
     * will be set. Otherwise the length is set to -1 in th map, to indicate a non-string Datatype.
     * 
     * @param command the supplied ICommand object.
     * @param hasXMLDocSource 'true' if the command has XML Document source
     * @return map of unique name to length
     */
    public static Map getProjectedSymbolAndProcInputLengths( ICommand command,
                                                             boolean hasXMLDocSource ) {
        // Get the map of projected Symbol names to lengths
        Map symbolLengthMap = getProjectedSymbolLengths(command, hasXMLDocSource);

        if (command != null) {
            // Get the list of InputParameters
            List inputParams = getProcedureInputParams(command);
            // Add parameter lengths to the mapp
            Iterator paramIter = inputParams.iterator();
            while (paramIter.hasNext()) {
                ISPParameter param = (ISPParameter)paramIter.next();
                IElementSymbol symbol = param.getParameterSymbol();
                String name = symbol.getShortName();

                Object typeObj = getElementSymbolType(symbol);
                boolean xmlDocSourceCase = hasXMLDocSource && (command instanceof ICreateProcedureCommand);

                // Put the type length in the symbolLengthMap
                updateTypeLengthMap(symbolLengthMap, name, typeObj, symbol, xmlDocSourceCase);
            }
        }
        return symbolLengthMap;
    }

    /**
     * Update the map with the supplied name and the length for the supplied typeObj
     * 
     * @param theMap the Map to update
     * @param name the name of the map entry
     * @param typeObj the type object to get the length
     * @return the ordered list of query SELECT element types
     */
    private static void updateTypeLengthMap( Map theMap,
                                             String name,
                                             Object typeObj,
                                             IExpression symbol,
                                             boolean xmlDocSourceCase ) {
        // If the type is Datatype and it's a String Datatype, put the length in the map
        // Otherwise put in a -1
        if (typeObj != null && typeObj instanceof XSDSimpleTypeDefinition) {
            String dtName = ((XSDSimpleTypeDefinition)typeObj).getName();
            if ((DatatypeConstants.BuiltInNames.STRING.equals(dtName) || DatatypeConstants.BuiltInNames.CHAR.equals(dtName))
                && ModelerCore.getWorkspaceDatatypeManager().isBuiltInDatatype((XSDSimpleTypeDefinition)typeObj)) {
                int length = getElementSymbolLength(symbol);
                theMap.put(name, new Integer(length));
            } else {
                theMap.put(name, new Integer(-1));
            }
        } else if (typeObj != null) {
            // Handles NullType
            if (typeObj instanceof Class) {
                // XML Document source is special case - set length to Integer.MAX_VALUE
                if (xmlDocSourceCase) {
                    theMap.put(name, new Integer(Integer.MAX_VALUE));
                } else {
                    if (String.class.equals(typeObj)) {
                        int stringLength = getSymbolLength(symbol);
                        if (stringLength > 0) {
                            theMap.put(name, new Integer(stringLength));
                        } else {
                            theMap.put(name, new Integer(ModelerCore.getTransformationPreferences().getDefaultStringLength()));
                        }
                    } else if (Character.class.equals(typeObj)) { 
                        theMap.put(name, new Integer(1));
                    } else {
                        theMap.put(name, new Integer(-1));
                    }
                }
            }
        } else {
            theMap.put(name, new Integer(-1));
        }
    }

    /**
     * Determine if the symbol is a concat, substring, or decode function
     * 
     * @param SingleElementSymbol
     * @return IFunction null if not found
     * @since 4.2.2
     */
    private static IFunction isStringFunction( IExpression symbol ) {
        IExpressionSymbol expressionSymbol = null;
        IFunction function = null;

        if (symbol instanceof IAliasSymbol && ((IAliasSymbol)symbol).getSymbol() instanceof IExpressionSymbol) {
            expressionSymbol = (IExpressionSymbol)((IAliasSymbol)symbol).getSymbol();
        } else if (symbol instanceof IExpressionSymbol) {
            expressionSymbol = (IExpressionSymbol)symbol;
        }
        if (expressionSymbol != null && expressionSymbol.getExpression() instanceof IFunction) {
            function = (IFunction)expressionSymbol.getExpression();
            IFunctionLibrary functionLibrary = UdfManager.getInstance().getFunctionLibrary();
            
            if (function.getName().equalsIgnoreCase(functionLibrary.getFunctionName(FunctionName.CONCAT))
                || function.getName().equalsIgnoreCase(functionLibrary.getFunctionName(FunctionName.CONCAT_OPERATOR))) {
                return function;
            } else if (isDecodeOrSubString(function)) {
                return function;
            } else {
                return null;
            }
        }

        return function;
    }

    /**
     * Helper method to determine if the function is decodeString or subString
     * 
     * @param IFunction function
     * @return boolean
     * @since 4.2.2
     */
    private static boolean isDecodeOrSubString( IFunction function ) {
        IFunctionLibrary functionLibrary = UdfManager.getInstance().getFunctionLibrary();
        if (function.getName().equalsIgnoreCase(functionLibrary.getFunctionName(FunctionName.DECODESTRING))
            || function.getName().equalsIgnoreCase(functionLibrary.getFunctionName(FunctionName.SUBSTRING))) {
            return true;
        }
        return false;

    }

    /**
     * Determine the concatenated string lengths
     * 
     * @param IFunction
     * @return int length of 2 concatenated string columns
     * @since 4.2.2
     */
    private static int concatSymbolLength( IExpression exprObject ) {
        IExpression[] args = null;
        IElementSymbol elSymbol = null;
        int stringLength = 0;

        if (exprObject != null && exprObject instanceof IFunction) {
            IFunction myFunc = (IFunction)exprObject;
            if (isDecodeOrSubString(myFunc)) {
                return stringLength += getMaxStringLength(myFunc);
            } else if (myFunc.getName().equalsIgnoreCase("chr")) { //$NON-NLS-1$
                return stringLength += 1;
            }

            args = myFunc.getArgs();
            for (int i = 0; i < args.length; i++) {
                IExpression symbol = args[i];

                if (symbol != null && symbol instanceof IFunction) {
                    stringLength += concatSymbolLength(symbol);
                }

                if (symbol instanceof IElementSymbol) {
                    elSymbol = (IElementSymbol)symbol;
                    Object mID = elSymbol.getMetadataID();
                    if (mID != null && mID instanceof ColumnRecord) {
                        int length = ((ColumnRecord)mID).getLength();
                        stringLength += length;
                    } else if (mID != null && mID instanceof ProcedureParameterRecord) {
                        int length = ((ProcedureParameterRecord)mID).getLength();
                        stringLength += length;
                    } else {
                        stringLength += ModelerCore.getTransformationPreferences().getDefaultStringLength();
                    }
                }
                if (symbol instanceof IConstant) {
                    IConstant constant = (IConstant)args[i];
                    Object value = constant.getValue();
                    if (value != null && value instanceof String) {
                        stringLength += ((String)value).length();
                    } else {
                        stringLength += ModelerCore.getTransformationPreferences().getDefaultStringLength();
                    }
                }
            }
        } else if (exprObject instanceof IElementSymbol) {
            elSymbol = (IElementSymbol)exprObject;
            Object mID = elSymbol.getMetadataID();
            if (mID != null && mID instanceof ColumnRecord) {
                int length = ((ColumnRecord)mID).getLength();
                stringLength += length;
            } else if (mID != null && mID instanceof ProcedureParameterRecord) {
                int length = ((ProcedureParameterRecord)mID).getLength();
                stringLength += length;
            } else {
                stringLength += ModelerCore.getTransformationPreferences().getDefaultStringLength();
            }
        } else if (exprObject instanceof IConstant) {
            IConstant constant = (IConstant)exprObject;
            Object value = constant.getValue();
            if (value != null && value instanceof String) {
                stringLength += ((String)value).length();
            } else {
                stringLength += ModelerCore.getTransformationPreferences().getDefaultStringLength();
            }
        }
        return stringLength += 0;
    }

    /**
     * Determines the length of possible nested string function using recursion on concatSymbolLength
     * 
     * @return int length of concatenated IExpression
     * @since 4.2.2
     */
    public static int getSymbolLength( IExpression symbol ) {
        int stringLength = 0;

        IFunction function = null;

        if ((function = isStringFunction(symbol)) != null) {
            if (!isDecodeOrSubString(function)) {
                IExpression[] args = function.getArgs();
                for (int i = 0; i < args.length; i++) {
                    IExpression exprSymbol = args[i];

                    if (exprSymbol != null && exprSymbol instanceof IFunction) {
                        stringLength += concatSymbolLength(exprSymbol);
                    }
                    if (exprSymbol instanceof IElementSymbol) {
                        stringLength += concatSymbolLength(exprSymbol);
                    }
                    if (exprSymbol instanceof IConstant) {
                        stringLength += concatSymbolLength(exprSymbol);
                    }
                }
            } else if (isDecodeOrSubString(function)) {
                stringLength += getMaxStringLength(function);
            }
        } else {
            stringLength += 0;
        }
        return stringLength;
    }

    /**
     * Helper method to determine the max length of given substring/decodeString length. No matter if it is the 2 or 3 param
     * substring function always can obtain the max length from first argument of function
     * 
     * @param IFunction
     * @return int max length of function's first param
     * @since 4.2.2
     */
    private static int getMaxStringLength( IFunction function ) {
        IExpression[] args = function.getArgs();
        IExpression exprSymbol = args[0];
        
        IFunctionLibrary functionLibrary = UdfManager.getInstance().getFunctionLibrary();
        if (function.getName().equalsIgnoreCase(functionLibrary.getFunctionName(FunctionName.DECODESTRING))) {
            return getDecodeLength(function);
        }
        return concatSymbolLength(exprSymbol);
    }

    /**
     * Helper method to determine the max length of decodeString. Either the column size will be max or one of the string lengths
     * from decode part
     * 
     * @param IFunction
     * @return int max length of column size or max decode string length
     * @since 4.2.2
     */
    private static int getDecodeLength( IFunction function ) {
        IExpression[] args = function.getArgs();
        IExpression exprSymbol = null;

        int maxLength = 0;
        exprSymbol = args[0];
        if (exprSymbol instanceof IElementSymbol) {
            IElementSymbol elmSymbol = (IElementSymbol)exprSymbol;
            Object mID = elmSymbol.getMetadataID();
            if (mID != null && mID instanceof ColumnRecord) {
                int length = ((ColumnRecord)mID).getLength();
                maxLength = length;
            } else if (mID != null && mID instanceof ProcedureParameterRecord) {
                int length = ((ProcedureParameterRecord)mID).getLength();
                maxLength = length;
            } else {
                maxLength = ModelerCore.getTransformationPreferences().getDefaultStringLength();
            }
        }
        exprSymbol = args[1];
        if (exprSymbol instanceof IConstant) {
            IConstant constSym = (IConstant)exprSymbol;
            Object constObj = constSym.getValue();
            if (constObj != null && constObj instanceof String) {
                String decodes = (String)constObj;

                String delimiter = ","; //$NON-NLS-1$
                if (args.length == 3) {
                    exprSymbol = args[2];
                    constSym = (IConstant)exprSymbol;
                    constObj = constSym.getValue();
                    if (constObj != null && constObj instanceof String) {
                        delimiter = (String)constObj;
                    }
                }
                StringTokenizer strTok = new StringTokenizer(decodes, delimiter);
                while (strTok.hasMoreTokens()) {
                    String word = strTok.nextToken().trim();
                    if (word.length() > maxLength) {
                        maxLength = word.length();
                    }
                }
            }
        }

        return maxLength;

    }

    /**
     * Get the ordered list of query select element Types from a ICommand The "type" will be one of three things (1) null - When
     * the target attribute is unmatched (2) Datatype - When the target attribute is bound to a symbol which resolves to a
     * MetaObject (3) java class type - When the target attribute is bound to an IExpression
     * 
     * @param comman the ICommand language object
     * @return the ordered list of query SELECT element types
     */
    public static List getProjectedSymbolTypes( ICommand command ) { // NO_UCD
        List selectTypes = new ArrayList();
        if (command != null) {
            // Get the list of SELECT symbols
            List<IExpression> symbols = CommandHelper.getProjectedSymbols(command);
            for (IExpression symbol : symbols) {
                selectTypes.add(getElementSymbolType(symbol));
            }
        }
        return selectTypes;
    }

    // Return a Map of renamed symbols - new name (key) and SelectSymbol (value)
    public static Map getRenamedSymbolsMap( List<IExpression> symbols ) {
        Map renameMap = new HashMap();
        // Make sure all the symbols are SingleElementSymbols
        List seSymbols = new ArrayList();
        Iterator symbolIter = symbols.iterator();
        while (symbolIter.hasNext()) {
        	IExpression sSymbol = (IExpression)symbolIter.next();
            if (sSymbol instanceof IMultipleElementSymbol) {
                List meSymbols = ((IMultipleElementSymbol)sSymbol).getElementSymbols();
                if (meSymbols != null) {
                    Iterator meIter = meSymbols.iterator();
                    while (meIter.hasNext()) {
                        IElementSymbol eSymbol = (IElementSymbol)meIter.next();
                        if (eSymbol != null) {
                            seSymbols.add(eSymbol);
                        }
                    }
                }
            } else if (sSymbol != null) {
                seSymbols.add(sSymbol);
            }
        }
        Collection elementNames = new ArrayList();
        // Populate the list with the symbol names
        symbolIter = seSymbols.iterator();
        while (symbolIter.hasNext()) {
        	IExpression seSymbol = (IExpression)symbolIter.next();
            String name = TransformationSqlHelper.getSingleElementSymbolShortName(seSymbol, false);
            String uniqueName = name;

            IExpression underlyingSymbol = seSymbol;

            // If this is an alias, set the symbol to be the underlying object
            if (seSymbol instanceof IAliasSymbol) {
                underlyingSymbol = ((IAliasSymbol)seSymbol).getSymbol();
            }
            if (underlyingSymbol instanceof IElementSymbol) {
                // Set MetaObject ref in Map for SingleElementSymbol
                IElementSymbol eSymbol = (IElementSymbol)underlyingSymbol;
                // There may be duplicates in the elementMap (self-joins,etc)
                // If so, the name is made unique first...
                uniqueName = getUniqueName(name, elementNames);
                // Ignore TempMetadataID
                Object idObj = eSymbol.getMetadataID();
                if (idObj != null && idObj instanceof MetadataRecord) {
                    elementNames.add(uniqueName);
                }
            } else if (underlyingSymbol instanceof IExpressionSymbol) {
                // There may be duplicates in the elementMap (self-joins,etc)
                // If so, the name is made unique first...
                uniqueName = getUniqueName(name, elementNames);
                elementNames.add(uniqueName);
            }
            if (!uniqueName.equals(name)) {
                renameMap.put(uniqueName, seSymbol);
            }
        }
        return renameMap;
    }

    /**
     * Create a List of FROM clauses, given a list of SqlAlias Objects
     * 
     * @param sqlAliasObjs the supplied list of SqlAlias Objects
     * @return the generated list of from clauses
     */
    private static List createFromClauses( List sqlAliases ) {
        List result = new ArrayList(sqlAliases.size());

        Iterator iter = sqlAliases.iterator();
        while (iter.hasNext()) {
            SqlAlias sqlAlias = (SqlAlias)iter.next();
            // Create unary from clause
            IGroupSymbol gSymbol = createGroupSymbol(sqlAlias);
            result.add(getQueryFactory().createUnaryFromClause(gSymbol));
        }
        return result;
    }

    /**
     * Create a FROM clause, given a Table, Procedure or SqlAlias
     * 
     * @param eObject the supplied EObject
     * @return the generated FROM clause
     */
    private static IFromClause createFromClause( EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);

        IFromClause fromClause = getQueryFactory().createUnaryFromClause(createGroupSymbol(eObject));

        return fromClause;
    }

    /**
     * Create a List of GroupSymbols from a List of groups - EOjects or SqlAlias objects.
     * 
     * @param groupObjs the supplied list of group EObjects
     * @return the generated list of corresponding GroupSymbols
     */
    private static List createGroupSymbols( List groupEObjs ) {
        List result = new ArrayList(groupEObjs.size());

        Iterator iter = groupEObjs.iterator();
        while (iter.hasNext()) {
            EObject eObj = (EObject)iter.next();
            IGroupSymbol gSymbol = createGroupSymbol(eObj);
            if (gSymbol != null) {
                result.add(gSymbol);
            }
        }
        return result;
    }

    /**
     * Create a IStoredProcedure from a Procedure EObject or SqlAlias. The aliasedObject of the SqlAlias must be a procedure.
     * 
     * @param eObj the Procedure or SqlAlias object
     * @return the generated StoredProcedure
     */
    public static IStoredProcedure createStoredProc( EObject eObj ) {
        CoreArgCheck.isNotNull(eObj);

        IStoredProcedure storedProc = null;

        // If the supplied EObject is SqlAlias, get Alias name and aliased Object
        if (eObj instanceof SqlAlias) {
            SqlAlias sqlAlias = (SqlAlias)eObj;
            eObj = sqlAlias.getAliasedObject();
        }

        if (TransformationHelper.isSqlProcedure(eObj)) {
            // Get procedure name
            SqlProcedureAspect procedureAspect = (SqlProcedureAspect)AspectManager.getSqlAspect(eObj);
            String procFullName = procedureAspect.getFullName(eObj);

            // Create StoredProc and set ID/name
            storedProc = getQueryFactory().createStoredProcedure();
            storedProc.setProcedureName(procFullName);
            storedProc.setDisplayNamedParameters(true);
            // Get the Parameter attributes from the group
            List procParams = procedureAspect.getParameters(eObj);

            // Create List of SPParams from the attributes (IN, IN_OUT)
            List spParams = createSPParams(procParams);
            Iterator iter = spParams.iterator();

            while (iter.hasNext()) {
                storedProc.setParameter((ISPParameter)iter.next());
            }

            EObject results = (EObject)procedureAspect.getResult(eObj);
            if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureResultSet(results)) {
                SqlColumnSetAspect rsAspect = (SqlColumnSetAspect)org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(results);
                List rsCols = rsAspect.getColumns(results);
                if (rsCols.size() > 0) {
                    // it doesn't matter at this point what the columns are, just that is has a resultset
                    ISPParameter param = getQueryFactory().createSPParameter(spParams.size(), ISPParameter.ParameterInfo.RESULT_SET, "RESULT"); //$NON-NLS-1$
                    param.addResultSetColumn("RESULT", String.class, "RESULT"); //$NON-NLS-1$ //$NON-NLS-2$
                    storedProc.setParameter(param);
                }
            }
        }

        return storedProc;
    }

    /**
     * Create a IGroupSymbol from a group EOject or SqlAlias
     * 
     * @param groupEObj the supplied group EObject
     * @return the corresponding GroupSymbols
     */
    public static IGroupSymbol createGroupSymbol( EObject groupEObj ) {
        IGroupSymbol gSymbol = null;

        String aliasName = null;
        // If the supplied EObject is SqlAlias, get Alias name and aliased Object
        if (groupEObj instanceof SqlAlias) {
            SqlAlias sqlAlias = (SqlAlias)groupEObj;
            aliasName = sqlAlias.getAlias();
            groupEObj = sqlAlias.getAliasedObject();
        }
        // Create the IGroupSymbol using the EObject and alias (if any)
        if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isTable(groupEObj)) {
            SqlTableAspect tableAspect = (SqlTableAspect)AspectManager.getSqlAspect(groupEObj);
            boolean hasResource = groupEObj.eResource() != null ? true : false;
            String tableFullName = tableAspect.getName(groupEObj);
            if (hasResource) {
            	tableFullName = tableAspect.getFullName(groupEObj);
            }
            String tableShortName = tableAspect.getName(groupEObj);
            // Get MetadataID for fullName
            Object groupID = null;
            if (hasResource) {
                groupID = getGroupID(tableFullName, groupEObj);
            }
            if (aliasName != null && !aliasName.equalsIgnoreCase(tableShortName)) {
                gSymbol = getQueryFactory().createGroupSymbol(aliasName, tableFullName);
            } else {
                gSymbol = getQueryFactory().createGroupSymbol(tableFullName);
            }
            // Set the MetadataID if it was found
            if (groupID != null) {
                gSymbol.setMetadataID(groupID);
            }
        } else if (groupEObj instanceof InputSet) {
            // InputSet iSet = (InputSet)groupEObj;
            String inputSetFullName = "InputSet"; //$NON-NLS-1$
            String inputSetShortName = "InputSet"; //$NON-NLS-1$
            if (aliasName != null && !aliasName.equalsIgnoreCase(inputSetShortName)) {
                gSymbol = getQueryFactory().createGroupSymbol(aliasName, inputSetFullName);
            } else {
                gSymbol = getQueryFactory().createGroupSymbol(inputSetFullName);
            }
        } else if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(groupEObj)) {
            SqlProcedureAspect procAspect = (SqlProcedureAspect)AspectManager.getSqlAspect(groupEObj);
            String name = procAspect.getFullName(groupEObj);
            String shortName = procAspect.getName(groupEObj);
            if (aliasName != null && !aliasName.equalsIgnoreCase(shortName)) {
                gSymbol = getQueryFactory().createGroupSymbol(aliasName, name);
            } else {
                gSymbol = getQueryFactory().createGroupSymbol(name);
            }
        }

        return gSymbol;
    }

    /**
     * Get the ElementID for the supplied Element FullName. Returns the ElementID, or null if it wasnt found.
     * 
     * @param elementFullName the fullName of the element
     * @param elmntObj the eObject of the element
     * @return the Object ID
     */
    private static Object getElementID( String elementFullName,
                                        EObject elmntObj ) {
        Object elemID = null;
        // try {
        SqlAspect sqlAspect = AspectManager.getSqlAspect(elmntObj);
        CoreArgCheck.isInstanceOf(SqlColumnAspect.class, sqlAspect);
        elemID = new ColumnRecordImpl((SqlColumnAspect)sqlAspect, elmntObj);

        return elemID;
    }

    /**
     * Get the GroupID for the supplied Group FullName. Returns the GroupID, or null if it wasnt found.
     * 
     * @param groupFullName the fullName of the group
     * @param grpObj the eObject of the group
     * @return the Object ID
     */
    private static Object getGroupID( String groupFullName,
                                      EObject grpObj ) {
        Object groupID = null;
        SqlAspect sqlAspect = AspectManager.getSqlAspect(grpObj);
        CoreArgCheck.isInstanceOf(SqlTableAspect.class, sqlAspect);
        groupID = new TableRecordImpl((SqlTableAspect)sqlAspect, grpObj);

        return groupID;
    }

    /**
     * Get the GroupID for the supplied Group FullName. Returns the GroupID, or null if it wasnt found.
     * 
     * @param procFullName the fullName of the procedure
     * @param procObj the eObject of the procedure
     * @return the Object ID
     */
    private static IStoredProcedureInfo getProcInfo( String procFullName,
                                                    EObject procObj ) {
        IStoredProcedureInfo procInfo = null;
        try {
            IQueryMetadataInterface resolver = TransformationMetadataFactory.getInstance().getModelerMetadata(procObj);
            procInfo = resolver.getStoredProcedureInfoForProcedure(procFullName);
        } catch (Exception e) {
            String message = TransformationPlugin.Util.getString("TransformationSqlHelper.groupIDNotFoundError", //$NON-NLS-1$
                                                                 procFullName);
            TransformationPlugin.Util.log(IStatus.WARNING, e, message);
        }
        return procInfo;
    }

    /**
     * Create a List of ElementSymbols for all of the element Eobjects in the supplied List of group EOjects.
     * 
     * @param groupObjs the supplied list of group EObjects
     * @return the generated list of corresponding SingleElementSymbols
     */
    private static List createElemSymbols( List sqlAliasGroups ) {
        List result = new ArrayList();

        Iterator iter = sqlAliasGroups.iterator();
        while (iter.hasNext()) {
            SqlAlias groupSqlAlias = (SqlAlias)iter.next();
            result.addAll(createElemSymbols(groupSqlAlias));
        }
        return result;
    }

    /**
     * Create a List of ElementSymbols for the supplied group EOject.
     * 
     * @param groupEObj the supplied group EObject
     * @return the generated list of corresponding SingleElementSymbols
     */
    public static List createElemSymbols( SqlAlias groupSqlAlias ) {
        List result = new ArrayList();

        // Create GroupSymbol
        IGroupSymbol groupSymbol = createGroupSymbol(groupSqlAlias);

        // Get the aliased EObject
        EObject groupEObj = groupSqlAlias.getAliasedObject();

        // Create the Symbol using the EObject and alias (if any)
        if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isTable(groupEObj)) {
            SqlTableAspect tableAspect = (SqlTableAspect)AspectManager.getSqlAspect(groupEObj);
            List columns = tableAspect.getColumns(groupEObj);
            Iterator columnIter = columns.iterator();
            IExpression seSymbol = null;
            while (columnIter.hasNext()) {
                EObject columnEObj = (EObject)columnIter.next();
                seSymbol = createElemSymbol(columnEObj, groupSymbol);
                result.add(seSymbol);
            }
        } else if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(groupEObj)) {
            SqlProcedureAspect procAspect = (SqlProcedureAspect)AspectManager.getSqlAspect(groupEObj);

            EObject procResultEObj = (EObject)procAspect.getResult(groupEObj);

            List allColumns = new ArrayList();

            if (procResultEObj != null) {
                SqlColumnSetAspect procResultAspect = (SqlColumnSetAspect)AspectManager.getSqlAspect(procResultEObj);
                allColumns.addAll(procResultAspect.getColumns(procResultEObj));
            }

            allColumns.addAll(TransformationHelper.getInParameters(groupEObj));
            allColumns.addAll(TransformationHelper.getOutAndReturnParameters(groupEObj));

            List inout = TransformationHelper.getInoutParameters(groupEObj);

            allColumns.addAll(inout);

            for (Iterator i = allColumns.iterator(); i.hasNext();) {
                EObject elemEObj = (EObject)i.next();

                String type = TransformationHelper.getRuntimeType(elemEObj);
                
                IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
                final Class clazz = service.getDataTypeClass(type);

                String paramName = TransformationHelper.getSqlEObjectName(elemEObj);

                IElementSymbol symbol = getQueryFactory().createElementSymbol(groupSymbol.getName() + ISymbol.SEPARATOR + paramName);
                symbol.setType(clazz);
                result.add(symbol);

                if (inout.contains(elemEObj)) {
                    symbol = getQueryFactory().createElementSymbol(groupSymbol.getName() + ISymbol.SEPARATOR + paramName + "_IN"); //$NON-NLS-1$
                    symbol.setType(clazz);
                    result.add(symbol);
                }
            }
        }

        return result;
    }

    /**
     * Create an IElementSymbol from a group EOject or SqlAlias
     * 
     * @param groupEObj the supplied group EObject
     * @return the corresponding GroupSymbols
     */
    public static IExpression createElemSymbol( EObject elemEObj,
                                                        IGroupSymbol parentGroupSymbol ) {
    	IExpression seSymbol = null;
    	IDataTypeManagerService dataTypeService = ModelerCore.getTeiidDataTypeManagerService();
    	
    	Class<?> nullClass = dataTypeService.getDefaultDataClass(DataTypeName.NULL);
    	
        // Get name for the supplied group
        String tableName = parentGroupSymbol.getName();
        if (tableName == null) {
            tableName = ISQLConstants.BLANK;
        }

        // If the supplied EObject is SqlAlias, get Alias name and aliased Object
        String columnAliasName = null;
        if (elemEObj instanceof SqlAlias) {
            SqlAlias columnAlias = (SqlAlias)elemEObj;
            columnAliasName = columnAlias.getAlias();
            elemEObj = columnAlias.getAliasedObject();
        }

        // check for input parameter before column as an input parameter and column
        // implement the same aspect, our logic to determine entitty is based on the aspect type
        if (TransformationHelper.isSqlInputParameter(elemEObj)) {
            InputParameterSqlAspect aspect = (InputParameterSqlAspect)AspectManager.getSqlAspect(elemEObj);

            String fullName = tableName + "." + aspect.getName(elemEObj); //$NON-NLS-1$          

            IElementSymbol element = getQueryFactory().createElementSymbol(fullName);
            element.setGroupSymbol(parentGroupSymbol);
            final String rtType = aspect.getRuntimeType(elemEObj);
            if (rtType != null) {
                final Class clazz = dataTypeService.getDataTypeClass(rtType);
                element.setMetadataID(getQueryFactory().createMetadataID(fullName.toUpperCase(), clazz));
                element.setType(clazz);
            } else {
                element.setMetadataID(getQueryFactory().createMetadataID(fullName.toUpperCase(), nullClass));
                element.setType(nullClass);
            }
            seSymbol = element;
            // Create ElementSymbols using eObj and alias (if any)
            // Use the tableName and column short name to create the ElementSymbol
        } else if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(elemEObj)) {
            SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(elemEObj);
            String colShortName = columnAspect.getName(elemEObj);
            String colUUID = TransformationHelper.getSqlEObjectUUID(elemEObj);
            // Get MetadataID for uuid
            Object elemID = getElementID(colUUID, elemEObj);
            if (columnAliasName != null) {
                IElementSymbol elemSymbol = getQueryFactory().createElementSymbol(tableName + "." + colShortName); //$NON-NLS-1$
                elemSymbol.setGroupSymbol(parentGroupSymbol);
                // Set the MetadataID if it was found
                if (elemID != null) {
                    elemSymbol.setMetadataID(elemID);
                }
                final String rtType = columnAspect.getRuntimeType(elemEObj);
                if (rtType != null) {
                    final Class clazz = dataTypeService.getDataTypeClass(rtType);
                    elemSymbol.setType(clazz);
                } else {
                    elemSymbol.setType(nullClass);
                }
                seSymbol = getQueryFactory().createAliasSymbol(columnAliasName, elemSymbol);
            } else {
                IElementSymbol elemSymbol = getQueryFactory().createElementSymbol(tableName + "." + colShortName); //$NON-NLS-1$
                elemSymbol.setGroupSymbol(parentGroupSymbol);
                // Set the MetadataID if it was found
                if (elemID != null) {
                    elemSymbol.setMetadataID(elemID);
                }
                final String rtType = columnAspect.getRuntimeType(elemEObj);
                if (rtType != null) {
                    final Class clazz = dataTypeService.getDataTypeClass(rtType);
                    elemSymbol.setType(clazz);
                } else {
                    elemSymbol.setType(nullClass);
                }
                seSymbol = elemSymbol;
            }
        } else if (TransformationHelper.isSqlProcedureParameter(elemEObj)) {
            SqlProcedureParameterAspect aspect = (SqlProcedureParameterAspect)AspectManager.getSqlAspect(elemEObj);
            String paramName = TransformationHelper.getSqlEObjectFullName(elemEObj);

            IElementSymbol element = getQueryFactory().createElementSymbol(paramName);
            element.setGroupSymbol(parentGroupSymbol);
            if (aspect != null) {
                final String rtType = aspect.getRuntimeType(elemEObj);
                if (rtType != null) {
                    final Class clazz = dataTypeService.getDataTypeClass(rtType);
                    element.setMetadataID(getQueryFactory().createMetadataID(paramName.toUpperCase(), clazz));
                    element.setType(clazz);
                } else {
                    element.setMetadataID(getQueryFactory().createMetadataID(paramName.toUpperCase(), nullClass));
                    element.setType(nullClass);
                }
            } else {
                element.setMetadataID(getQueryFactory().createMetadataID(paramName.toUpperCase(), nullClass));
                element.setType(nullClass);
            }
            seSymbol = element;
        }
        return seSymbol;
    }

    /**
     * Resolve any name conflicts with the supplied symbols List. Return the corrected symbols list.
     * 
     * @param seSymbols the supplied list of SingleElementSymbols
     * @return the corrected list of SingleElementSymbols
     */
    public static List renameConflictingSymbols( List seSymbols ) {
        // List of corrected symbols to be returned
        List newSymbols = new ArrayList();

        // Get the map of symbols which require rename
        // name (key) to SingleElementSymbol (value)
        Map renamedSymbolsMap = getRenamedSymbolsMap(seSymbols);

        // If the map has entry, there is name conflict
        if (renamedSymbolsMap.size() != 0) {

            Map workingRenSymMap = new HashMap(renamedSymbolsMap);

            // Iterate through the current ISelect symbols
            // If any are MultipleElementSymbols, replace them with the SingleElementSymbols
            // if they contain a renamed symbol
            // Once a symbol is renamed, the Map entry is removed so duplicates dont get renamed
            for (int i = 0; i < seSymbols.size(); i++) {
            	IExpression currentSelectSymbol = (IExpression)seSymbols.get(i);
                // ---------------------------------------------------------------
                // Current Symbol is MultiElement - expand it if necessary
                // ---------------------------------------------------------------
                if (currentSelectSymbol instanceof IMultipleElementSymbol) {
                    // Check whether the MultiSymbol needs expanding
                    boolean shouldExpand = shouldExpand((IMultipleElementSymbol)currentSelectSymbol, workingRenSymMap);
                    // If the MultiSymbol needs expanding, expand it with renamed symbol
                    if (shouldExpand) {
                        // Get SingleElementSybmols for this MultiSymbol
                        List multiElemSymbols = ((IMultipleElementSymbol)currentSelectSymbol).getElementSymbols();
                        Iterator iter = multiElemSymbols.iterator();
                        // Go thru all SingleElement symbols, and rename if necessary
                        while (iter.hasNext()) {
                        	IExpression renamedSymbol = renameSymbolUsingMap((IExpression)iter.next(),
                                                                                     workingRenSymMap);
                            newSymbols.add(renamedSymbol);
                        }
                        // This MultiSymbol didnt need expanding, just add it back as is
                    } else {
                        newSymbols.add(currentSelectSymbol);
                    }
                    // ----------------------------------------------------------------
                    // Current Symbol is SingleElementSymbol - rename it if necessary
                    // ----------------------------------------------------------------
                } else if (currentSelectSymbol != null && currentSelectSymbol instanceof IExpression) {
                	IExpression renamedSymbol = renameSymbolUsingMap((IExpression)currentSelectSymbol,
                                                                             workingRenSymMap);
                    newSymbols.add(renamedSymbol);
                }
            }
        } else {
            newSymbols.addAll(seSymbols);
        }

        return newSymbols;
    }

    /**
     * Determine if the MultiElementSymbol should be expanded, based on the supplied map. The map contains newName (key) to
     * SingleElementSymbol (value)
     */
    private static boolean shouldExpand( IMultipleElementSymbol multiElemSymbol,
                                         Map renamedSymbolsMap ) {
        boolean shouldExpand = false;

        // Get SingleElementSymbols for this MultiElement
        List multiElemSymbols = multiElemSymbol.getElementSymbols();

        // Get Symbols which require rename
        Collection renamedSymbols = renamedSymbolsMap.values();

        // If any of the renamed symbols is "within" the MultiSymbol, then needs expanding
        Iterator iter = renamedSymbols.iterator();
        while (iter.hasNext()) {
            if (multiElemSymbols.contains(iter.next())) {
                shouldExpand = true;
                break;
            }
        }
        return shouldExpand;
    }

    /**
     * method to rename a IExpression. The IExpression is checked against the renamedSymbolsMap. If the symbol is
     * in the renamedSymbolsMap, set the name to the new value. Otherwise, return the original symbol.
     * 
     * @param seSymbol the IExpression to check.
     * @param renamedSymbolsMap the map of renamed symbols
     * @return the renamed IExpression (renamed only if necessary)
     */
    private static IExpression renameSymbolUsingMap( IExpression seSymbol,
                                                             Map renamedSymbolsMap ) {
        // Default is just going to return what comes in
    	IExpression resultSymbol = seSymbol;
        if (seSymbol != null) {
            Iterator renamedIter = renamedSymbolsMap.keySet().iterator();
            // Check the Map. If seSymbol is in the Map, rename it to the new name.
            while (renamedIter.hasNext()) {
                // newName key
                String newName = (String)renamedIter.next();
                // get corresponding symbol
                IExpression renamedSymbol = (IExpression)renamedSymbolsMap.get(newName);
                // If seSymbol is in the Map, rename it and quit.
                if (renamedSymbol.equals(seSymbol)) {
                    if (seSymbol instanceof IAliasSymbol) {
                        ((IAliasSymbol)seSymbol).setShortName(newName);
                        resultSymbol = seSymbol;
                    } else {
                        resultSymbol = getQueryFactory().createAliasSymbol(newName, seSymbol);
                    }
                    // Remove renamed from the Map so duplicates dont get renamed
                    renamedSymbolsMap.remove(newName);
                    break;
                }
            }
        }
        return resultSymbol;
    }

    private static List createSPParams( List procParams ) {
        List spparams = new ArrayList(procParams.size());
        
        int index = 0;
        for (int i = 0; i < procParams.size(); i++) {
            EObject paramObject = (EObject)procParams.get(i);
            SqlAspect sqlAspect = org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(paramObject);
            if (sqlAspect instanceof SqlProcedureParameterAspect) {
                SqlProcedureParameterAspect paramAspect = (SqlProcedureParameterAspect)sqlAspect;
                int direction = paramAspect.getType(paramObject);
                String name = paramAspect.getName(paramObject);
                switch (direction) {
                    case MetadataConstants.PARAMETER_TYPES.IN_PARM:
                        ISPParameter spparam1 = getQueryFactory().createSPParameter(index, getQueryFactory().createElementSymbol(name));
                        spparam1.setName(name);
                        spparam1.setParameterType(ISPParameter.ParameterInfo.IN);
                        spparams.add(spparam1);
                        index++;
                        break;
                    case MetadataConstants.PARAMETER_TYPES.INOUT_PARM:
                        ISPParameter spparam2 = getQueryFactory().createSPParameter(index, getQueryFactory().createElementSymbol(name));
                        spparam2.setName(name);
                        spparam2.setParameterType(ISPParameter.ParameterInfo.INOUT);
                        spparams.add(spparam2);
                        index++;
                        break;
                    case MetadataConstants.PARAMETER_TYPES.OUT_PARM:
                        ISPParameter spparam3 = getQueryFactory().createSPParameter(index, getQueryFactory().createElementSymbol(name));
                        spparam3.setName(name);
                        spparam3.setParameterType(ISPParameter.ParameterInfo.OUT);
                        spparams.add(spparam3);
                        index++;
                        break;
                    case MetadataConstants.PARAMETER_TYPES.RETURN_VALUE:
                        ISPParameter spparam4 = getQueryFactory().createSPParameter(index, getQueryFactory().createElementSymbol(name));
                        spparam4.setName(name);
                        spparam4.setParameterType(ISPParameter.ParameterInfo.RETURN_VALUE);
                        spparams.add(spparam4);
                        index++;
                        break;
                }
            }
        }
        return spparams;
    }

    // Get Short name for GroupSymbol
    public static String getGroupSymbolShortName( IGroupSymbol gSymbol ) {
        String shortName = null;
        if (gSymbol != null) {
            String symbolDefn = gSymbol.getDefinition();
            String symbolName = gSymbol.getName();
            // If an alias is being used, use it
            if (symbolDefn != null) {
                shortName = symbolName;
            } else {
                EObject eObj = getGroupSymbolEObject(gSymbol);
                shortName = TransformationHelper.getSqlEObjectName(eObj);
            }
        }
        return shortName;
    }

    /**
     * Get a list of all the GroupSymbols in the supplied command.
     * 
     * @param command the ICommand language object
     * @return the List of GroupSymbols
     */
    public static Collection<IGroupSymbol> getGroupSymbols( ICommand command ) {
        IGroupCollectorVisitor groupCollectorVisitor = getQueryService().getGroupCollectorVisitor(false);
        
        // All groups - including duplicates
        Collection allGrps = groupCollectorVisitor.findGroupsIgnoreInlineViews(command);
        // New group for result - no duplicates
        Collection<IGroupSymbol> result = new ArrayList<IGroupSymbol>(allGrps.size());
        Iterator iter = allGrps.iterator();
        while (iter.hasNext()) {
            IGroupSymbol gSymbol = (IGroupSymbol)iter.next();
            if (!containsGroupSymbol(result, gSymbol)) {
                result.add(gSymbol);
            }
        }
        return result;
    }

    /**
     * Determine if the supplied symbols list contains the supplied GroupSymbol. Both the IGroupSymbol canonical name and
     * definitions are compared. This method required because the GroupSymbol.equals will not work - it assumes that both
     * GroupSymbols were obtained from the same "queryFrame". This is not always the case, such as UNION queries.
     * 
     * @param symbols the List of GroupSymbols
     * @param gSymbol the groupSymbol to compare vs the symbols list
     * @return 'true' if the list already contains the supplied symbol, 'false' if not.
     */
    public static boolean containsGroupSymbol( Collection symbols,
                                               IGroupSymbol gSymbol ) {
        boolean result = false;
        // Name and Definition of the Symbol being tested
        String gSymbName = gSymbol.getName();
        String gSymbDefn = gSymbol.getDefinition();
        
        // Check the list for a matching symbol
        Iterator iter = symbols.iterator();
        while (iter.hasNext()) {
            IGroupSymbol listSymbol = (IGroupSymbol)iter.next();
            String lSymbName = listSymbol.getName();
            String lSymbDefn = listSymbol.getDefinition();
            // If the symbol definitions are both null, neither are aliased - just compare the canonical names
            if (lSymbDefn == null && gSymbDefn == null) {
                if (lSymbName.equals(gSymbName)) {
                    // match found, set true and break
                    result = true;
                    break;
                }
                // If neither of the symbol definitions are null, both are aliased - aliases and definitions must both match
            } else if (lSymbDefn != null && gSymbDefn != null) {
                if (lSymbDefn.equalsIgnoreCase(gSymbDefn) && lSymbName.equals(gSymbName)) {
                    // match found, set true and break
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @param symbols
     * @param eSymbol
     * @return
     * @since 5.0
     */
    public static boolean containsElementSymbol( Collection symbols,
    		IExpression eSymbol ) {
        EObject referencedColumn = getSingleElementSymbolEObject(eSymbol);
        Iterator iter = symbols.iterator();
        while (iter.hasNext()) {
        	IExpression next = (IExpression)iter.next();
            if (eSymbol == next) {
                return true;
            }
            
            if (getQueryService().getSymbolName(eSymbol).equalsIgnoreCase(getQueryService().getSymbolName(next) )){
                // If Names are same, do one last check if the EObjects (column refs) are same
                if (referencedColumn != null) {
                    EObject nextReferencedEObject = getSingleElementSymbolEObject(next);
                    if (nextReferencedEObject != null) {
                        if (nextReferencedEObject == referencedColumn) {
                            return true;
                        }
                    }
                }
                return true;
            }
        }

        return false;
    }

    /**
     * @param singleSymbol
     * @return
     * @since 5.0
     */
    public static EObject getSingleElementSymbolEObject( IExpression singleSymbol ) {
        EObject referencedColumn = null;
        if (singleSymbol instanceof IAliasSymbol) {
        	IExpression theSymbol = ((IAliasSymbol)singleSymbol).getSymbol();
            if (theSymbol instanceof IElementSymbol) {
                referencedColumn = TransformationSqlHelper.getElementSymbolEObject((IElementSymbol)theSymbol);
            }
        } else if (singleSymbol instanceof IElementSymbol) {
            referencedColumn = TransformationSqlHelper.getElementSymbolEObject((IElementSymbol)singleSymbol);
        }
        return referencedColumn;
    }

    /**
     * Get the number of Reference LanguageObjects in the supplied command.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param type the command type
     * @return int the number of references.
     */
    public static int getReferenceCount( Object transMappingRoot,
                                         int type ) {
        // Get the ICommand for the supplied Type
        ICommand command = TransformationHelper.getCommand(transMappingRoot, type);

        int refCount = 0;
        if (command != null) {
            // Get the list of References
            IReferenceCollectorVisitor referenceCollectorVisitor = getQueryService().getReferenceCollectorVisitor();
            List<IReference> refs = referenceCollectorVisitor.findReferences(command);
            refCount = refs.size();
        }

        return refCount;
    }

    /**
     * Get the EObject the the symbol is resolved to - null if not resolved
     * 
     * @param symbol the ElementSymbol
     * @return the symbol EObject
     */
    public static EObject getElementSymbolEObject( IElementSymbol symbol ) {
        EObject result = null;
        if (symbol != null) {
            Object elemObj = symbol.getMetadataID();
            if (elemObj != null) {
                if (elemObj instanceof MetadataRecord) {
                    result = (EObject)((MetadataRecord)elemObj).getEObject();
                } else if (TransformationHelper.isSqlColumn(elemObj)) {
                    result = (EObject)elemObj;
                }
            }
        }
        return result;
    }

    /**
     * Get the EObject the the symbol is resolved to - null if not resolved
     * 
     * @param symbol the ElementSymbol
     * @param command the ICommand that the IElementSymbol comes from
     * @return the symbol EObject, null if not found
     */
    public static EObject getElementSymbolEObject( IElementSymbol symbol,
                                                   ICommand command ) {
        EObject result = null;
        if (symbol != null) {
            Object elemObj = symbol.getMetadataID();
            if (elemObj instanceof IMetadataID) {
                elemObj = ((IMetadataID)elemObj).getOriginalMetadataID();
            }
            if (elemObj instanceof MetadataRecord) {
                result = (EObject)((MetadataRecord)elemObj).getEObject();
            }
        }
        return result;
    }

    /**
     * Get the SingleElementSymbol type. The "type" will be one of three things (1) null - When the SingleElementSymbol is not
     * resolve / no type (2) Datatype - SingleElementSymbol is not resolved - Datatype of the SqlColumn (3) java class type -
     * SingleElementSymbol is IExpression, etc, type is Class
     * 
     * @param symbol the SingleElementSymbol
     * @return the symbols type
     */
    public static Object getElementSymbolType( IExpression symbol ) {
        Object datatype = null;
        // If this is an alias, set the symbol to be the aliased symbol
        if (symbol instanceof IAliasSymbol) {
            symbol = ((IAliasSymbol)symbol).getSymbol();
        }

        // -------------------------------------
        // Element Symbol
        // -------------------------------------
        if (symbol instanceof IElementSymbol) {
            IElementSymbol eSymbol = (IElementSymbol)symbol;
            // get MetadataID from resolved symbol
            Object idObj = eSymbol.getMetadataID();
            // MetadataRecord = get resolved EObject datatype
            if (idObj instanceof MetadataRecord) {
                EObject recordEObj = (EObject)((MetadataRecord)idObj).getEObject();
                if (recordEObj != null && TransformationHelper.isSqlColumn(recordEObj)) {
                    SqlColumnAspect columnAspect = ((SqlColumnAspect)AspectManager.getSqlAspect(recordEObj));
                    datatype = columnAspect.getDatatype(recordEObj);
                }
                // TempMetadataID - get type
            } else if (idObj instanceof IMetadataID) {
                datatype = ((IMetadataID)idObj).getType();
                // MetadataID is null, get symbol datatype
            } else if (idObj == null) {
                datatype = eSymbol.getType();
            }
        // -------------------------------------
        // IExpression
        // -------------------------------------
        } else {
        	Object tmpType = null;
        	if( symbol instanceof IFunction) {
        		// need to get the functions's return parameter and find it's type
        		// If there is no RESULT parameter, then return object;
        		// Look in current project for source model containing the function method
        		IFunction function = (IFunction)symbol;
        		
        		String name = function.getName();
        		// Find datatype for function
        		
        		tmpType = UdfManager.getInstance().getDataType(name);
        		if( tmpType != null ) {
        			datatype = tmpType;
        		}
        	} else {
        		datatype = symbol.getType();
        	}
        }
        return datatype;
    }

    /**
     * Get the SingleElementSymbol length. If symbol is not resolved or not a SqlColumnAspect, returns -1.
     * 
     * @param symbol the SingleElementSymbol
     * @return the symbols type length
     */
    public static int getElementSymbolLength( IExpression symbol ) {
        int length = -1;
        // If this is an alias, set the symbol to be the aliased symbol
        if (symbol instanceof IAliasSymbol) {
            symbol = ((IAliasSymbol)symbol).getSymbol();
        }

        // -------------------------------------
        // Element Symbol
        // -------------------------------------
        if (symbol instanceof IElementSymbol) {
            IElementSymbol eSymbol = (IElementSymbol)symbol;
            EObject eObj = getElementSymbolEObject(eSymbol);
            if (eObj != null) {
                length = TransformationHelper.getSqlColumnLength(eObj);
            }
        }
        return length;
    }

    /**
     * Get the List of EObjects that the ElementSymbols are resolved to.
     * 
     * @param elemSymbols the collection of ElementSymbols
     * @param command the ICommand that the elemSymbols come from
     * @return the List of EObjects
     */
    public static List<EObject> getElementSymbolEObjects( Collection elemSymbols,
                                                 ICommand command ) {
        List<EObject> result = Collections.EMPTY_LIST;
        if (elemSymbols != null) {
            result = new ArrayList(elemSymbols.size());
            Iterator iter = elemSymbols.iterator();
            while (iter.hasNext()) {
                IElementSymbol eSymbol = (IElementSymbol)iter.next();
                EObject elemEObj = getElementSymbolEObject(eSymbol, command);
                if (elemEObj != null) {
                    result.add(elemEObj);
                }
            }
        }
        return result;
    }

    /**
     * Get the EObject that the IGroupSymbol is resolved to.
     * 
     * @param symbol the GroupSymbol
     * @return the EObject that the IGroupSymbol is resolved to.
     */
    public static EObject getGroupSymbolEObject( IGroupSymbol symbol ) {
        EObject result = null;
        if (symbol != null) {
            Object groupObj = symbol.getMetadataID();
            if (groupObj != null) {
                if (symbol.isProcedure() && groupObj instanceof IMetadataID) {
                    IMetadataID tid = (IMetadataID)groupObj;
                    groupObj = tid.getOriginalMetadataID();
                }

                if (groupObj instanceof MetadataRecord) {
                    result = (EObject)((MetadataRecord)groupObj).getEObject();
                } else if (TransformationHelper.isSqlProcedureResultSet(groupObj)) {
                    result = (EObject)groupObj;
                }
            }
        }
        return result;
    }

    /**
     * Get the List of EObjects that the GroupSymbols are resolved to.
     * 
     * @param groupSymbols the collection of GroupSymbols
     * @return the List of EObjects
     */
    public static List getGroupSymbolEObjects( Collection groupSymbols ) {
        List result = Collections.EMPTY_LIST;
        if (groupSymbols != null) {
            result = new ArrayList(groupSymbols.size());
            Iterator iter = groupSymbols.iterator();
            while (iter.hasNext()) {
                IGroupSymbol gSymbol = (IGroupSymbol)iter.next();
                EObject grpEObj = getGroupSymbolEObject(gSymbol);
                if (grpEObj != null) {
                    result.add(grpEObj);
                }
            }
        }
        return result;
    }

    /**
     * Get the EObject that the IStoredProcedure is resolved to.
     * 
     * @param symbol the StoredProcedure
     * @return the EObject that the IStoredProcedure is resolved to.
     */
    public static EObject getStoredProcedureEObject( IStoredProcedure storedProc ) {
        EObject result = null;
        if (storedProc != null) {
            Object procID = storedProc.getProcedureID();
            if (procID != null && procID instanceof MetadataRecord) {
                result = (EObject)((MetadataRecord)procID).getEObject();
            }
        }
        return result;
    }

    /**
     * methods to check whether the supplied query's SELECT clause has any select symbols that reference any of the groups in the
     * supplied groupList
     * 
     * @param select the query SELECT clause
     * @param removeGroupList the List of Groups being removed
     * @return 'true' if the ISelect clause contains any attributes belonging to the groupList groups
     */
    public static boolean hasSqlAliasGroupAttributes( IQuery query,
                                                      List sqlAliasGroups ) {
        boolean result = false;
        // Create List of GroupSymbols corresponding to supplied SqlAliases
        List aliasGroupSymbols = createGroupSymbols(sqlAliasGroups);

        if (!isSelectStar(query.getSelect())) {
            
            IGroupsUsedByElementsVisitor groupsUsedByElementsVisitor = getQueryService().getGroupsUsedByElementsVisitor();
            IElementCollectorVisitor elementCollectorVisitor = getQueryService().getElementCollectorVisitor(true);
            
            // Get all ElementSymbols referenced in the select
            Collection selectElements = elementCollectorVisitor.findElements(query.getSelect());
            // Determine if any of the elements groups is in the remove list
            Iterator iter = selectElements.iterator();
            while (iter.hasNext()) {
                IElementSymbol selectElem = (IElementSymbol)iter.next();
                // Get all Groups referenced by the ElementSymbol
                Collection symbolGroups = groupsUsedByElementsVisitor.findGroups(selectElem);
                Iterator symbolGroupIter = symbolGroups.iterator();
                while (symbolGroupIter.hasNext()) {
                    IGroupSymbol groupSymbol = (IGroupSymbol)symbolGroupIter.next();
                    if (aliasGroupSymbols.contains(groupSymbol)) {
                        result = true;
                        break;
                    }
                }
                if (result) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * methods to check whether the supplied query's SELECT clause has a name that matches any of the supplied element EObjects
     * 
     * @param query the supplied query command
     * @param elemEObjs the List of Element EObjects
     * @return 'true' if any of the query projected symbols names matches the name of the supplied list of element EObjects
     */
    public static boolean hasSqlElemSymbols( IQuery query,
                                             List elemEObjs ) {
        boolean result = false;
        List projSymbolNames = getProjectedSymbolNames(query);

        // Determine if any of the supplied sqlColumns is in the projected symbols
        Iterator iter = elemEObjs.iterator();
        while (iter.hasNext()) {
            Object elem = iter.next();
            if ((elem instanceof EObject)
                && org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isColumn((EObject)elem)) {
                SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect((EObject)elem);
                if (projSymbolNames.contains(columnAspect.getName((EObject)elem))) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * methods to check whether the supplied SELECT clause is a SELECT *
     * 
     * @param select the query SELECT clause
     * @return 'true' if the ISelect clause is a SELECT *
     */
    public static boolean isSelectStar( ISelect select ) {
        boolean result = false;
        List currentSelectSymbols = select.getSymbols();
        if (currentSelectSymbols.size() == 1) {
        	IExpression singleSelectSymbol = (IExpression)currentSelectSymbols.get(0);
            if (singleSelectSymbol instanceof IMultipleElementSymbol) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Convert an IExpressionSymbol to the specified type
     * 
     * @param exprSymbol the original IExpressionSymbol
     * @param newTypeName the type to convert the original IExpressionSymbol to
     * @return the converted IExpression Symbol
     */
    public static IExpressionSymbol convert( IExpressionSymbol exprSymbol,
                                            String newTypeName ) {
        IExpressionSymbol newExpressionSymbol = (IExpressionSymbol)exprSymbol.clone();

        // Get the original IExpressionSymbol Type
        IExpression expr = exprSymbol.getExpression();
        // if ( expr == null ) {
        // // AggregateSymbols can contain null IExpressions - if so, use the symbol itself
        // expr = exprSymbol;
        // }
        //        
        Class originalTypeClass = expr.getType();
        
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        String originalTypeName = service.getDataTypeName(originalTypeClass);

        // If the desired Type is different from the original Type, do convert
        if (!originalTypeName.equalsIgnoreCase(newTypeName)) {
            // If the supplied IExpressionSymbol is a Convert Function, try to reuse it and change the type
            if (isConvertFunction(exprSymbol)) {
                IExpression convExpr = getConvertedExpr(exprSymbol);
                Class convExprTypeClass = convExpr.getType();
                String convExprTypeName = service.getDataTypeName(convExprTypeClass);
                // Check whether there is a conversion
                boolean isExplicit = service.isExplicitConversion(convExprTypeName, newTypeName);
                boolean isImplicit = service.isImplicitConversion(convExprTypeName, newTypeName);
                // If theres a conversion, go ahead and use it
                if (isExplicit || isImplicit) {
                    IFunction func = getConversion(convExprTypeName, newTypeName, convExpr);
                    newExpressionSymbol.setExpression(func);
                } else {
                    IFunction convertFunction = getConversion(originalTypeName, newTypeName, expr);
                    newExpressionSymbol.setExpression(convertFunction);
                }
            } else {
                IFunction convertFunction = getConversion(originalTypeName, newTypeName, expr);
                newExpressionSymbol.setExpression(convertFunction);
            }
        }
        return newExpressionSymbol;
    }

    /**
     * Convert an ElementSymbol to the specified type. An alias symbol is created with the supplied aliasName. If the supplied
     * aliasName is null, the original element symbol name is used as the alias.
     * 
     * @param elementSymbol the original ElementSymbol
     * @param newTypeName the type to convert the original IElementSymbol to
     * @param aliasName if not null, this is the aliasName, otherwise use elementSymbolName
     * @return the converted element Symbol
     */
    public static IAliasSymbol convert( IElementSymbol elementSymbol,
                                       String newTypeName,
                                       String aliasName ) {
        Class originalTypeClass = elementSymbol.getType();
        
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        String originalTypeName = service.getDataTypeName(originalTypeClass);

        IFunction convertFunction = getConversion(originalTypeName, newTypeName, elementSymbol);

        IExpressionSymbol exprSymbol = getQueryFactory().createExpressionSymbol(NEW_CONVERSION_NAME, convertFunction);
        IAliasSymbol newSymbol = null;
        if (aliasName != null) {
            newSymbol = getQueryFactory().createAliasSymbol(aliasName, exprSymbol);
        } else {
            newSymbol = getQueryFactory().createAliasSymbol(elementSymbol.getShortName(), exprSymbol);
        }
        return newSymbol;
    }

    public static IFunction getConversion( String originalTypeName,
                                          String newTypeName,
                                          IExpression expression ) {
        
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        IFunctionLibrary functionLibrary = UdfManager.getInstance().getFunctionLibrary();
        
        Class originalType = service.getDataTypeClass(originalTypeName);

        IFunctionLibrary<IFunctionForm, IFunctionDescriptor> library = UdfManager.getInstance().getFunctionLibrary();
        Class<?> stringDataClass = service.getDefaultDataClass(DataTypeName.STRING);
        IFunctionDescriptor fd = library.findFunction(functionLibrary.getFunctionName(FunctionName.CONVERT), 
                                                      new Class[] {originalType, stringDataClass});

        List<IExpression> expressions = Arrays.asList(
            expression, getQueryFactory().createConstant(newTypeName));
        
        IFunction conversion = getQueryFactory().createFunction(fd.getName(), expressions);
        conversion.setType(service.getDataTypeClass(newTypeName));
        conversion.setFunctionDescriptor(fd);

        return conversion;
    }

    public static boolean isConvertFunction( IExpressionSymbol exprSymbol ) {
        IExpression expr = exprSymbol.getExpression();
        if (expr != null && expr instanceof IFunction) {
            String fName = ((IFunction)expr).getName();
            if (fName.equalsIgnoreCase(ISQLConstants.CONVERT)) {
                return true;
            }
        }
        return false;
    }

    public static IExpression getConvertedExpr( IExpressionSymbol exprSymbol ) {
        if (isConvertFunction(exprSymbol)) {
            IExpression expr = exprSymbol.getExpression();
            IExpression fExp = ((IFunction)expr).getArg(0);
            return fExp;
            // if(fExp instanceof SingleElementSymbol) {
            // return (SingleElementSymbol)fExp;
            // }
        }
        return null;
    }

    public static IAliasSymbol convertElementSymbol( IElementSymbol symbol,
                                                    String targetTypeStr,
                                                    String aliasName ) {
        return convert(symbol, targetTypeStr, aliasName);
    }

    public static IExpressionSymbol convertExpressionSymbol( IExpressionSymbol symbol,
                                                            String targetTypeStr ) {
        // Handle case where the IExpression is already a Convert Function
        if (isConvertFunction(symbol)) {
            IExpression cExpr = getConvertedExpr(symbol);
            if (cExpr != null && cExpr instanceof IExpression) {
            	IExpression seSymbol = (IExpression)cExpr;
            	
            	IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
                String seSymbolTypeStr = service.getDataTypeName(seSymbol.getType());
                // Check whether there is a conversion from the underlying symbol to the attribute type
                boolean isExplicitConv = service.isExplicitConversion(seSymbolTypeStr, targetTypeStr);
                boolean isImplicitConv = service.isImplicitConversion(seSymbolTypeStr, targetTypeStr);
                if (isImplicitConv || isExplicitConv) {
                    if (seSymbol instanceof IExpressionSymbol) {
                        return convertExpressionSymbol((IExpressionSymbol)seSymbol, targetTypeStr);
                    } else if (seSymbol instanceof IElementSymbol) {
                        IAliasSymbol aSymbol = convertElementSymbol((IElementSymbol)seSymbol, targetTypeStr, null);
                        IExpression aseSymbol = aSymbol.getSymbol();
                        if (aseSymbol instanceof IExpressionSymbol) {
                            return (IExpressionSymbol)aseSymbol;
                        }
                    }
                } else {
                    return convert(symbol, targetTypeStr);
                }
            }
        } else {
            return convert(symbol, targetTypeStr);
        }
        return null;
    }
}
