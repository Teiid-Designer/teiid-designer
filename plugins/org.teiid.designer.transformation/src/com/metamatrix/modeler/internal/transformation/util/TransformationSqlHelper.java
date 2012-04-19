/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import java.util.ArrayList;
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
import org.teiid.api.exception.query.QueryMetadataException;
import org.teiid.core.TeiidComponentException;
import org.teiid.core.types.DataTypeManager;
import org.teiid.designer.udf.UdfManager;
import org.teiid.language.SQLConstants;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.metadata.QueryMetadataInterface;
import org.teiid.query.metadata.StoredProcedureInfo;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.SetQuery.Operation;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.SelectSymbol;
import org.teiid.query.sql.symbol.SingleElementSymbol;
import org.teiid.query.sql.visitor.ElementCollectorVisitor;
import org.teiid.query.sql.visitor.GroupCollectorVisitor;
import org.teiid.query.sql.visitor.GroupsUsedByElementsVisitor;
import org.teiid.query.sql.visitor.ReferenceCollectorVisitor;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metadata.runtime.ColumnRecord;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord;
import com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.query.SetQueryUtil;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.internal.core.metadata.runtime.ColumnRecordImpl;
import com.metamatrix.modeler.internal.core.metadata.runtime.TableRecordImpl;
import com.metamatrix.modeler.transformation.PreferenceConstants;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.modeler.transformation.aspects.sql.InputParameterSqlAspect;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFactory;
import com.metamatrix.modeler.transformation.validation.SqlTransformationResult;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;

/**
 * TransformationSqlHelper This class is responsible for handling sql validation, changes, etc.
 */
public class TransformationSqlHelper implements SqlConstants {

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
     * Update All SQL (SELECT,UPDATE,INSERT,DELETE) for the transformation when a source group is removed.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sourceEObj the source group removed
     */
    public static void updateAllSqlOnSqlAliasGroupRemoved( EObject transMappingRoot, // NO_UCD
                                                           EObject sqlAliasGroup,
                                                           boolean removeElemsFromSelect,
                                                           Object source ) {
        // modTODO: only the SELECT is modified currently - may extend to include other SQL(?)
        updateSelectSqlOnSqlAliasGroupRemoved(transMappingRoot, sqlAliasGroup, removeElemsFromSelect, source);
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
     * Update Select SQL for the transformation when a source group is added.
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
        if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable(sqlAliasGroup)) {
            // ------------------------------------------------------------------------------
            // Add the new group to the From clause and its attributes to the select clause
            // WILL NOT CHANGE UNLESS ITS A PARSABLE QUERY. (DONT CHANGE SETQUERY)
            // -----------------------------------------------------------------
            if (TransformationHelper.isParsableQuery(transMappingRoot) || TransformationHelper.isEmptySelect(transMappingRoot)) {
                // Add the source Group to the Query From Clause
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
     * Update Select SQL for the transformation when a source group is added.
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
        // Add the new group to the From clause and its attributes to the select clause
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
     * Update Select SQL for the transformation when a source group is removed.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param source the source group removed
     */
    public static void updateSelectSqlOnSqlAliasGroupRemoved( EObject transMappingRoot,
                                                              EObject sqlAliasGroup,
                                                              boolean removeElemsFromSelect,
                                                              Object source ) {
        if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable(sqlAliasGroup)) {
            // if source is null, use this Helper as the source
            if (source == null) {
                source = getInstance();
            }
            // ------------------------------------------------------------------------------
            // Remove the new group from the From clause and its attributes
            // from the select clause
            // WILL NOT CHANGE UNLESS ITS A PARSABLE QUERY. (DONT CHANGE SETQUERY)
            // -----------------------------------------------------------------
            if (TransformationHelper.isParsableQuery(transMappingRoot) || TransformationHelper.isEmptySelect(transMappingRoot)) {
                // Create AliasedMetaObject for Link End
                // AliasedMetaObject aliasedGroupMO = createAliasedMO(newLinkMO);
                // Remove the source Groups from the Query From Clause
                if (sqlAliasGroup != null) {
                    List groups = new ArrayList(1);
                    groups.add(sqlAliasGroup);
                    final TransformationValidator validator = new TransformationValidator(
                                                                                          (SqlTransformationMappingRoot)transMappingRoot,
                                                                                          false);
                    removeSqlAliasGroupsFromSelectStatement(transMappingRoot, groups, removeElemsFromSelect, source, validator);
                }
            }
        }
    }

    /**
     * Update Select SQL for the transformation when source groups are removed.
     * 
     * @param transMappingRoot the transformation mapping root
     * @param sources the source groups removed
     */
    public static void updateSelectSqlOnSqlAliasGroupsRemoved( EObject transMappingRoot,
                                                               List sqlAliasGroups,
                                                               boolean removeElemsFromSelect,
                                                               Object source ) {
        // ------------------------------------------------------------------------------
        // Remove the new group from the From clause and its attributes
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
     * Update Select SQL for the transformation when target elements are removed.
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
     * Update UNION Select SQL for the transformation when source groups are added.
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
            Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            if (command != null) {
                if (command instanceof QueryCommand) {
                    SetQuery newQuery = createSetQueryAddUnionSources((QueryCommand)command, sourceGroups, useAll);
                    // Set the new MetaObject property
                    TransformationHelper.setSelectSqlString(transMappingRoot, newQuery.toString(), false, txnSource);
                }
            } else {
                SetQuery newQuery = createSetQueryAddUnionSources(null, sourceGroups, useAll);
                // Set the new MetaObject property
                TransformationHelper.setSelectSqlString(transMappingRoot, newQuery.toString(), false, txnSource);
            }
        }
    }

    /**
     * Update UNION Select SQL for the transformation, adding the source Groups to the desired UNION segment.
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
            Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);
            if (command != null && command instanceof SetQuery) {
                SetQuery newSetQuery = (SetQuery)command.clone();
                List queries = ((SetQuery)command).getQueryCommands();
                QueryCommand queryCommand = (QueryCommand)queries.get(nSegmentIndex);
                if (queryCommand instanceof Query) {
                    Query query = (Query)queryCommand;
                    Query newQuery = createQueryAddGroupsToFrom(query, sourceGroups);
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
        Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);

        boolean doAutoExpandSelect = TransformationPlugin.getDefault().getPreferences().getBoolean(PreferenceConstants.AUTO_EXPAND_SELECT, PreferenceConstants.AUTO_EXPAND_SELECT_DEFAULT);
        if( !doAutoExpandSelect && addElemsToSelect ) {
        	doAutoExpandSelect = addElemsToSelect;
        }
        
        // --------------------------------------------------------
        // If query is resolvable, work with the LanguageObjects
        // --------------------------------------------------------
        if (isValid && command instanceof Query) {
            Query query = (Query)command;

            // Add new Groups to the Query From Clause
            Query newQuery = createQueryAddSqlAliasGroups(query,
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

            // Add Groups to the Query From Clause
            Object targetGrp = TransformationHelper.getTransformationLinkTarget(transMappingRoot);

            // If target is not a procedure, create default query
            if (!TransformationHelper.isSqlProcedure(targetGrp)) {
                // Create empty SELECT * FROM query
                Query qry = createDefaultQuery(null);
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
                        StoredProcedure proc = createStoredProc(sqlAlias);
                        if (proc != null) {
                            CreateUpdateProcedureCommand cCommand = createVirtualProcCommmandForCommand(proc);
                            // Set the new mappingRoot SQL
                            TransformationHelper.setSelectSqlString(transMappingRoot, cCommand.toString(), false, txnSource);
                        }
                    } else if( TransformationHelper.isSqlTable(aliasedEObject)) {
                        // Create empty SELECT * FROM query
                        Query qry = createDefaultQuery(null);
                        // Add the sqlAliases
                        qry = createQueryAddSqlAliasGroups(qry,
                                                           sqlAliasGroups,
                                                           doAutoExpandSelect,
                                                           QueryValidator.SELECT_TRNS,
                                                           validator);

                        CreateUpdateProcedureCommand cCommand = createVirtualProcCommmandForCommand(qry);
                        // Set the new mappingRoot SQL
                        TransformationHelper.setSelectSqlString(transMappingRoot, cCommand.toString(), false, txnSource);
                    }
                    // Multiple Groups added
                } else {
                    // Create empty SELECT * FROM query
                    Query qry = createDefaultQuery(null);
                    qry = createQueryAddSqlAliasGroups(qry,
                                                       sqlAliasGroups,
                                                       doAutoExpandSelect,
                                                       QueryValidator.SELECT_TRNS,
                                                       validator);

                    CreateUpdateProcedureCommand cCommand = createVirtualProcCommmandForCommand(qry);
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
    public static CreateUpdateProcedureCommand createVirtualProcCommmandForCommand( Command command ) {
        Block block = new Block();

        CommandStatement cmdStmt = new CommandStatement(command);

        block.addStatement(cmdStmt);

        // Create the CreateUpdateProcedureCommand
        CreateUpdateProcedureCommand cCommand = new CreateUpdateProcedureCommand(block);
        // Virtual procedure
        cCommand.setUpdateProcedure(false);
        return cCommand;
    }

    /**
     * add the group elements to the supplied query SELECT SQL
     * 
     * @param query the query LanguageObject
     * @param addedGroups the list of groups whose elements to add to the Select Clause
     */
    private static Query createQueryAddSqlAliasGroupElemsToSelect( Query resolvedQuery,
                                                                   List addedSqlAliasGroups ) {
        Query result = null;
        // If the Query is a SELECT *, not necessary to add
        if (!isSelectStar(resolvedQuery.getSelect())) {
            result = (Query)resolvedQuery.clone();

            // Get the current query Select Clause
            Select select = resolvedQuery.getSelect();
            List currentSelectSymbols = select.getSymbols();

            // Get the new Group Elements
            List newElementSymbols = createElemSymbols(addedSqlAliasGroups);

            // List of new Select Symbols to create
            List selectSymbols = new ArrayList();
            selectSymbols.addAll(currentSelectSymbols);
            selectSymbols.addAll(newElementSymbols);

            List newSelectSymbols = renameConflictingSymbols(selectSymbols);

            // Replace the Select with newSymbols, default to SELECT * if no symbols
            if (newSelectSymbols.size() == 0) {
                newSelectSymbols.add(new MultipleElementSymbol());
                select.setSymbols(newSelectSymbols);
            } else {
                select.setSymbols(newSelectSymbols);
            }

            result.setSelect(select);
        }
        return result;
    }

    /**
     * add list of Groups to the Query FROM clause
     * 
     * @param query the query LanguageObject
     * @param addGroupList the List of groups to add to the query.
     * @param isResolvable flag indicating whether the query is resolvable
     */
    private static Query createQueryAddSqlAliasGroupsToFrom( Query resolvedQuery,
                                                             List sqlAliasGroups ) {
        Query result = null;

        if (resolvedQuery != null && sqlAliasGroups != null) {
            // Query result - Clone the input
            result = (Query)resolvedQuery.clone();

            // --------------------------------------------------------------------
            // Get the list of groups (AliasedMetaObjects) from the command
            // --------------------------------------------------------------------
            // Get the current query From Clause
            From from = resolvedQuery.getFrom();
            // Defect 19499: The query doesnt necessarily have a FROM clause (eg "SELECT 1 AS Col1, 2 AS Col2")
            From newFrom;
            if (from != null) {
                newFrom = (From)from.clone();
            } else {
                newFrom = new From();
            }

            // Get all of the current GroupSymbols for the query
            List currentGroupSymbols = new ArrayList();
            GroupCollectorVisitor.getGroupsIgnoreInlineViews(resolvedQuery, currentGroupSymbols);

            // Make GroupSymbols from the groups passed in
            List clausesToAdd = createFromClauses(sqlAliasGroups);

            Iterator iter = clausesToAdd.iterator();
            while (iter.hasNext()) {
                FromClause nextFromClause = (FromClause)iter.next();
                if (!newFrom.containsGroup(((UnaryFromClause)nextFromClause).getGroup())) {
                    newFrom.addClause(nextFromClause);
                }
            }
            result.setFrom(newFrom);
        }
        return result;
    }

    /**
     * add list of Groups EObjects to the Query FROM clause
     * 
     * @param query the query LanguageObject
     * @param addGroupList the List of groups to add to the query.
     * @return the modified query
     */
    private static Query createQueryAddGroupsToFrom( Query query,
                                                     List grpEObjs ) {
        Query result = null;

        if (query != null && grpEObjs != null) {
            // Query result - Clone the input
            result = (Query)query.clone();

            // --------------------------------------------------------------------
            // Get the current list of groups from the command
            // --------------------------------------------------------------------
            // Get the current query From Clause
            From from = query.getFrom();
            From newFrom;
            if (from != null) {
                newFrom = (From)from.clone();
            } else {
                newFrom = new From();
            }

            // Make FromClauses for the groups passed in
            for (int i = 0; i < grpEObjs.size(); i++) {
                EObject grpEObj = (EObject)grpEObjs.get(i);
                FromClause fClause = createFromClause(grpEObj);
                newFrom.addClause(fClause);
            }
            result.setFrom(newFrom);
        }
        return result;
    }

    /**
     * add list of Groups to the Query FROM clause
     * 
     * @param query the query LanguageObject
     * @param addGroupList the List of groups to add to the query.
     */
    private static Query createQueryAddSqlAliasGroups( Query resolvedQuery,
                                                       List sqlAliasGroups,
                                                       boolean addGroupElemsToSelect,
                                                       int cmdType,
                                                       TransformationValidator validator ) {
        Query result = null;
        if (resolvedQuery != null && sqlAliasGroups != null) {
            // Add the new groups to the Query FROM clause
            result = createQueryAddSqlAliasGroupsToFrom(resolvedQuery, sqlAliasGroups);

            // Parse the newSQL
            SqlTransformationResult parserResult = TransformationValidator.parseSQL(result.toString());
            Query resultQuery = (Query)parserResult.getCommand();
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
                Select select = resultQuery.getSelect();
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
     * add list of Groups to a Query or SetQuery, appending the additional commands to the end.
     * 
     * @param queryCommand the incoming query command (either a Query or a SetQuery)
     * @param unionSourceGroups the List of groups to add as union sources
     * @param useAll 'true' if adding UNION ALL, 'false' if adding UNION
     * @return the new union (SetQuery)
     */
    private static SetQuery createSetQueryAddUnionSources( QueryCommand queryCommand,
                                                           List unionSourceGrps,
                                                           boolean useAll ) {
        SetQuery result = null;
        result = new SetQuery(Operation.UNION);
        result.setAll(useAll);
        Iterator iter = null;
        
        if (queryCommand != null) {
        	// Set the left query to the current command
            result.setLeftQuery((QueryCommand)queryCommand.clone());

            iter = unionSourceGrps.iterator();
        } else {
        	// Case where there is NO initial query
            iter = unionSourceGrps.iterator();
            
            // Create a default query with the first source groups (left and right)
            if (iter.hasNext()) {
                EObject sourceGroup = (EObject)iter.next();
                Query qry = createDefaultQuery(sourceGroup);
                result.setLeftQuery(qry);
                if (iter.hasNext()) {
                    sourceGroup = (EObject)iter.next();
                    QueryCommand right = createDefaultQuery(sourceGroup);                    
                    result.setRightQuery(right);
                }
            }

            // If there are MORE than 2 sources, go ahead and create a New query command and set it as the Left query
            if (iter.hasNext()) {
                SetQuery unionResult = new SetQuery(Operation.UNION);
                unionResult.setAll(useAll);
                unionResult.setLeftQuery(result);
                result = unionResult;
           }
        }
        
        // Add new queries for each of the source groups
        while (iter.hasNext()) {
            EObject sourceGroup = (EObject)iter.next();
            Query qry = createDefaultQuery(sourceGroup);
            result.setRightQuery(qry);
            if (iter.hasNext()) {
                QueryCommand left = result;
                result = new SetQuery(Operation.UNION);
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
        Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);

        // If query is resolvable, work with the LanguageObjects
        if (isValid && command instanceof Query) {
            Query query = (Query)command;

            // DeOptimize the Query First
            // if(isResolvable) {
            // elementSymbolOptimizer.deoptimize(query);
            // }

            // Remove Groups and Group Elems from the Query
            // Object targetGrp = TransformationHelper.getTransformationLinkTarget(transMappingRoot);
            Query newQuery = createQueryRemoveSqlAliasGroups(query,
                                                             sqlAliasGroups,
                                                             removeElemsFromSelect,
                                                             QueryValidator.SELECT_TRNS,
                                                             validator);

            // Set the new MetaObject property
            TransformationHelper.setSelectSqlString(transMappingRoot, newQuery.toString(), false, txnSource);
        } else {
            rebuildQueryRemovingSqlAliasGroups(transMappingRoot, sqlAliasGroups);
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
        Command command = SqlMappingRootCache.getSelectCommand(transMappingRoot);

        // If query is resolvable, work with the LanguageObjects
        if (isValid && command instanceof Query) {
            Query query = (Query)command;

            // DeOptimize the Query First
            // if(isResolvable) {
            // elementSymbolOptimizer.deoptimize(query);
            // }

            // Remove Elems from the Query
            Query newQuery = createQueryRemoveElems(query, elementEObjs);

            // Set the new MetaObject property
            if (newQuery != null) {
                TransformationHelper.setSelectSqlString(transMappingRoot, newQuery.toString(), false, txnSource);
            }
        }
    }

    /**
     * rebuilds the Query, removing the specified group (if it exists)
     * 
     * @param queryMO the query MetaObject
     * @param aliasedGroupMO the group MetaObject to omit from the query
     */
    private static void rebuildQueryRemovingSqlAliasGroups( EObject transMappingRoot,
                                                            List sqlAliasGroups ) {
        if (transMappingRoot == null || sqlAliasGroups == null) return;

        // // List of all the query source links
        // Collection queryLinks = getQueryLinkEditor().getSourceLinks(queryMO);
        //        
        // // New From
        // From from = new From();
        //        
        // Iterator iter = queryLinks.iterator();
        // while(iter.hasNext()) {
        // MetaObject linkMO = (MetaObject)iter.next();
        // AliasedMetaObject aliasedCurrentGroupMO = createAliasedMO(linkMO);
        // Iterator removeGroupIter = aliasedGroupMOs.iterator();
        // while(removeGroupIter.hasNext()) {
        // AliasedMetaObject aliasedGroupMO = (AliasedMetaObject)removeGroupIter.next();
        // // If the link end object is the group to remove, dont add it.
        // if( aliasedCurrentGroupMO!=null && !aliasedCurrentGroupMO.equals(aliasedGroupMO) ) {
        // from.addClause( createFromClauseForGroup(aliasedCurrentGroupMO) );
        // }
        // }
        // }
        // //--------------------------------------------------
        // // Replace the FROM clause. This triggers Change
        // // Event on the QueryOperationDefinition
        // //--------------------------------------------------
        // replaceSQLFromClause(queryMO,from.toString());
    }

    // private static void replaceSQLFromClause(Object transMappingRoot, String newFrom) {
    // // The current SQL string
    // String sqlString = TransformationHelper.getSelectSqlString(transMappingRoot);
    // // Replace its FROM clause
    // if(sqlString!=null) {
    // sqlString = SqlStringUtil.replaceFrom(sqlString,newFrom);
    // }
    // // Set the new MetaObject property
    // TransformationHelper.setSelectSqlString(transMappingRoot,sqlString);
    // }

    /**
     * removes the group elements from the supplied query
     * 
     * @param query the query LanguageObject
     * @param removeGroupList the list of groups whose elemets to remove from the Select Clause
     * @param isResolvable flag indicating whether the query is resolvable
     */
    private static Query createQueryRemoveSqlAliasGroupElemsFromSelect( Query resolvedQuery,
                                                                        List removeSqlAliasGroups ) {
        Query result = null;
        // If the Query is not a SELECT *, ask whether to delete Group attributes from SELECT
        if (hasSqlAliasGroupAttributes(resolvedQuery, removeSqlAliasGroups)) {
            result = (Query)resolvedQuery.clone();
            List aliasGroupSymbols = createGroupSymbols(removeSqlAliasGroups);
            // Get the current query Select Clause
            Select select = resolvedQuery.getSelect();
            List currentSelectSymbols = select.getSymbols();

            // List of new Select Symbols to create
            List newSelectSymbols = new ArrayList(currentSelectSymbols.size());

            Iterator iter = currentSelectSymbols.iterator();
            while (iter.hasNext()) {
                SelectSymbol selectSymbol = (SelectSymbol)iter.next();
                // Get all Groups referenced by symbol
                Collection symbolGroups = GroupsUsedByElementsVisitor.getGroups(selectSymbol);
                Iterator symbolGroupIter = symbolGroups.iterator();
                boolean removeSymbol = false;
                while (symbolGroupIter.hasNext()) {
                    GroupSymbol symbGroup = (GroupSymbol)symbolGroupIter.next();

                    Iterator removeGroupIter = aliasGroupSymbols.iterator();
                    while (removeGroupIter.hasNext()) {
                        GroupSymbol removeGroupSymbol = (GroupSymbol)removeGroupIter.next();
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
                newSelectSymbols.add(new MultipleElementSymbol());
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
     * @param removeElements the list of elements to remove from the Select Clause
     * @param isResolvable flag indicating whether the query is resolvable
     */
    protected static Query createQueryRemoveElems( Query resolvedQuery,
                                                   List removeElements ) {
        Query result = resolvedQuery;

        List removeNames = new ArrayList(removeElements.size());
        Iterator iter = removeElements.iterator();
        while (iter.hasNext()) {
            Object remElem = iter.next();
            if ((remElem instanceof EObject)
                && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn((EObject)remElem)) {
                SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect((EObject)remElem);
                removeNames.add(columnAspect.getName((EObject)remElem));
            }
        }

        // If the Query is not a SELECT *, ask whether to delete Group attributes from SELECT
        if (hasSqlElemSymbols(resolvedQuery, removeElements)) {
            // Get the names of the Elements to remove
            result = (Query)resolvedQuery.clone();
            // Get the current query Select Clause
            Select select = resolvedQuery.getSelect();
            List currentSelectSymbols = resolvedQuery.getProjectedSymbols();

            // List of new Select Symbols to create
            List newSelectSymbols = removeSymbols(currentSelectSymbols, removeNames);

            // Replace the SELECT with new Symbols, default to SELECT FROM if no symbols left
            select.setSymbols(newSelectSymbols);
            result.setSelect(select);
        }

        if (result.getGroupBy() != null) {
            // Remove any symbols from the Group By that exist in the removeElements list
            GroupBy groupBy = result.getGroupBy();
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
            OrderBy orderBy = result.getOrderBy();
            final Iterator<OrderByItem> iter2 = orderBy.getOrderByItems().iterator();
            while (iter2.hasNext()) {
                final OrderByItem next = iter2.next();
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

    // TODO: COULD BE PROBLEMATTIC!!! OrderBy and GroupBy can hold expressions
    private static List removeSymbols( List currentSymbols,
                                       List symbolNamesToRemove ) {
        if (currentSymbols == null || symbolNamesToRemove == null) {
            return Collections.EMPTY_LIST;
        }

        final List result = new ArrayList(currentSymbols.size());
        final Iterator iter = currentSymbols.iterator();
        while (iter.hasNext()) {
            final Object next = iter.next();
            if (next instanceof SingleElementSymbol) {
                SingleElementSymbol seSymbol = (SingleElementSymbol)next;
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
     * @param removedGroups the list of groups to remove from the From Clause
     * @param isResolvable flag indicating whether the query is resolvable
     */
    private static Query createQueryRemoveSqlAliasGroupsFromFrom( Query resolvedQuery,
                                                                  List removedSqlAliasGrps ) {
        Query result = null;
        if (resolvedQuery != null) {
            result = (Query)resolvedQuery.clone();
            // Get the current query From Clause
            From from = resolvedQuery.getFrom();
            List currentFromClauses = from.getClauses();

            // List of new From Clauses to create
            List newFromClauses = new ArrayList(currentFromClauses.size());

            // Go thru the current From Clauses and see if they need to be removed
            Iterator iter = currentFromClauses.iterator();
            while (iter.hasNext()) {
                FromClause fromClause = (FromClause)iter.next();
                boolean removeIt = false;
                Iterator removeSqlAliasGrpIter = removedSqlAliasGrps.iterator();
                while (removeSqlAliasGrpIter.hasNext()) {
                    EObject removeGroupEObj = (EObject)removeSqlAliasGrpIter.next();
                    // UnaryFromClause
                    if (fromClause instanceof UnaryFromClause) {
                        GroupSymbol gSymbol = ((UnaryFromClause)fromClause).getGroup();

                        GroupSymbol removeGroupSymbol = createGroupSymbol(removeGroupEObj);
                        if (gSymbol != null && gSymbol.equals(removeGroupSymbol)) {
                            removeIt = true;
                            break;
                        }
                        // SubqueryFromClause
                    } else if (fromClause instanceof SubqueryFromClause) {
                        SubqueryFromClause sqf = (SubqueryFromClause)fromClause;
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
            // Replace the From with new Clauses
            from.setClauses(newFromClauses);
            result.setFrom(from);
        }
        return result;
    }

    // Determine if the SubqueryFromClause and SqlAlias match
    public static boolean isMatch( SubqueryFromClause subqueryFrom,
                                   SqlAlias sqlAlias ) {
        boolean isMatch = false;
        if (subqueryFrom != null && sqlAlias != null) {
            Command fromClauseQuery = subqueryFrom.getCommand();
            String fromClauseName = subqueryFrom.getName();
            // If StoredProcedure, add group directly to groups list
            if (fromClauseQuery instanceof StoredProcedure) {
                StoredProcedure fromClauseProc = (StoredProcedure)fromClauseQuery;
                String fromClauseProcName = fromClauseProc.getProcedureCallableName();
                String sqlAliasName = sqlAlias.getAlias();
                EObject sqlAliasEObj = sqlAlias.getAliasedObject();
                // Check alias names
                if (fromClauseName != null && fromClauseName.equalsIgnoreCase(sqlAliasName)) {
                    if (TransformationHelper.isSqlProcedure(sqlAliasEObj)) {
                        StoredProcedureInfo procInfo = getProcInfo(TransformationHelper.getSqlEObjectFullName(sqlAliasEObj),
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

    // Determine if the StoredProcedure and SqlAlias match
    public static boolean isMatch( StoredProcedure storedProc, // NO_UCD
                                   SqlAlias sqlAlias ) {
        boolean isMatch = false;
        if (storedProc != null && sqlAlias != null) {
            String procName = storedProc.getProcedureCallableName();
            String sqlAliasName = sqlAlias.getAlias();
            EObject sqlAliasEObj = sqlAlias.getAliasedObject();
            // Check alias names
            if (procName != null && procName.equalsIgnoreCase(sqlAliasName)) {
                if (TransformationHelper.isSqlProcedure(sqlAliasEObj)) {
                    StoredProcedureInfo procInfo = getProcInfo(TransformationHelper.getSqlEObjectFullName(sqlAliasEObj),
                                                               sqlAliasEObj);
                    String sqlAliasProcName = procInfo.getProcedureCallableName();
                    if (procName.equalsIgnoreCase(sqlAliasProcName)) {
                        isMatch = true;
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
    private static Query createQueryRemoveSqlAliasGroups( Query resolvedQuery,
                                                          List sqlAliasGroups,
                                                          boolean removeGroupElemsFromSelect,
                                                          int cmdType,
                                                          TransformationValidator validator ) {
        Query result = null;
        if (resolvedQuery != null && sqlAliasGroups != null) {
            // If the Query is not a SELECT *, ask whether to delete Group attributes from SELECT
            if (!isSelectStar(resolvedQuery.getSelect()) && hasSqlAliasGroupAttributes(resolvedQuery, sqlAliasGroups)) {
                if (removeGroupElemsFromSelect) {
                    // Remove the group elements from the Query SELECT
                    result = createQueryRemoveSqlAliasGroupElemsFromSelect(resolvedQuery, sqlAliasGroups);
                } else {
                    result = resolvedQuery;
                }
            }

            // Null return means that no changes were made
            Query resultQuery = null;
            boolean isValid = false;
            if (result != null) {
                // Parse the newSQL
                SqlTransformationResult parserResult = TransformationValidator.parseSQL(result.toString());
                resultQuery = (Query)parserResult.getCommand();
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
     * This method rebuilds the Query Language Object in the event of a projected Symbol name conflict.
     * 
     * @param queryCommand the query Command object
     * @return the modified Query language object
     */
    public static Query createQueryFixNameConflicts( Query resolvedQuery , boolean addGroupElemsToSelect) {
        Query modifiedQuery = null;
        if (resolvedQuery != null) {
            // Modified Query Result
            modifiedQuery = (Query)resolvedQuery.clone();

            if (hasProjectedSymbolNameConflict(resolvedQuery)) {
                // Get the current symbols for the Query
                List currentSymbols = resolvedQuery.getSelect().getSymbols();

                // Fix name conflicts
                List newSymbols = renameConflictingSymbols(currentSymbols);

                // Reset the Select Symbols
                Select newSelect = new Select(newSymbols);
                modifiedQuery.setSelect(newSelect);
            } else if( addGroupElemsToSelect ) {
            	List currentSelectSymbols = resolvedQuery.getSelect().getSymbols();
                if (currentSelectSymbols.size() == 1) {
                    SelectSymbol singleSelectSymbol = (SelectSymbol)currentSelectSymbols.get(0);
                    if (singleSelectSymbol instanceof MultipleElementSymbol) {
                    	List<ElementSymbol> elementSymbols = ((MultipleElementSymbol)singleSelectSymbol).getElementSymbols();
                    	// Reset the Select Symbols
                        Select newSelect = new Select(elementSymbols);
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
     * @return the Query language object result
     */
    public static Query createDefaultQuery( EObject source ) {
        // create SELECT *
        Select newSelect = new Select();
        newSelect.addSymbol(new MultipleElementSymbol());

        // Create FROM
        From newFrom = new From();
        // Add group to FROM
        if (source != null) {
            FromClause clause = createFromClause(source);
            if (clause != null) {
                newFrom.addClause(clause);
            }
        }

        // Create Query and set SELECT and FROM
        Query query = new Query();
        query.setSelect(newSelect);
        query.setFrom(newFrom);

        return query;
    }

    /**
     * Determine if the supplied command has a name conflict (has duplicate short names).
     * 
     * @param command the Command language object
     * @return 'true' if there is a name conflict, 'false' if not.
     */
    public static boolean hasProjectedSymbolNameConflict( Command command ) {
        boolean hasConflict = false;
        if (command != null) {
            // maintain collection of short names
            Collection attrNames = new ArrayList();
            // Get the list of Projected symbols
            List symbols = command.getProjectedSymbols();
            // Look for duplicate short names
            Iterator symbolIter = symbols.iterator();
            while (symbolIter.hasNext()) {
                SingleElementSymbol seSymbol = (SingleElementSymbol)symbolIter.next();
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
    public static List getProjectedSymbolNames( Command command ) {
        if (command == null) return Collections.EMPTY_LIST;
        // ---------------------------------------------------
        // Get the List of ProjectedSymbols from the command
        // ---------------------------------------------------
        List projectedSymbols = command.getProjectedSymbols();

        // -------------------------------
        // Build the List of symbolNames
        // -------------------------------
        List symbolNames = null;
        if (projectedSymbols == null || projectedSymbols.isEmpty()) {
            symbolNames = Collections.EMPTY_LIST;
        } else {
            symbolNames = new ArrayList(projectedSymbols.size());
            // Populate the list with the symbol names
            Iterator symbolIter = projectedSymbols.iterator();
            while (symbolIter.hasNext()) {
                SingleElementSymbol symbol = (SingleElementSymbol)symbolIter.next();
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
     * name. For expressionSymbols, the showExpression flag can be set 'true' to show the expression text or 'false' to show the
     * default name
     * 
     * @param symbol the SingleElementSymbol to get the short name
     * @param showExpression flag to determine if full expression text is returned, or default name
     * @return the short name for the supplied symbol
     */
    public static String getSingleElementSymbolShortName( SingleElementSymbol symbol,
                                                          boolean showExpression ) {
        String symbolName = BLANK;
        if (symbol != null) {
            // Handle ExpressionSymbols - if Implicit Function, the function name is hidden
            if (symbol instanceof ExpressionSymbol) {
                // get the expression
                Expression expr = ((ExpressionSymbol)symbol).getExpression();
                // Expression is a Function, look for implicit function
                if (expr instanceof Function) {
                    // if implicit function, use the arg symbol name, otherwise use function name
                    Function func = (Function)expr;
                    if (func.isImplicit()) {
                        Collection elementSymbols = ElementCollectorVisitor.getElements(func, true);
                        if (elementSymbols.size() == 1) {
                            ElementSymbol element = (ElementSymbol)elementSymbols.iterator().next();
                            symbolName = element.getShortName();
                        } else {
                            symbolName = (showExpression) ? symbol.toString() : symbol.getShortName();
                        }
                        // Not an implicit function
                    } else {
                        symbolName = (showExpression) ? symbol.toString() : symbol.getShortName();
                    }
                    // Expression not a Function
                } else {
                    symbolName = (showExpression) ? symbol.toString() : symbol.getShortName();
                }
                // Not expression symbol, use short name
            } else {
                symbolName = symbol.getShortName();
            }
        }
        return symbolName;
    }

    // Return a List of Shortened QuerySelect symbol names, renaming them to make unique
    public static List getProjectedSymbolUniqueNames( Command command ) {
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
    public static List getProcedureInputParams( Command command ) {
        if (command == null) return Collections.EMPTY_LIST;

        ArrayList inputParams = new ArrayList();

        // Add InputParameters for supplied command (if it's a StoredProcedure)
        if (command instanceof StoredProcedure) {
            List procInParams = ((StoredProcedure)command).getInputParameters();
            inputParams.addAll(procInParams);
        }

        return inputParams;
    }

    /**
     * Create a Map of unique name (key) to a EObject (value) from the supplied StoredProcedure. If a projected symbol is an
     * ElementSymbol which is resolved to an EObject, the EObject will be added to the Map.
     * 
     * @param command the supplied StoredProcedure object.
     * @return map of unique name to EObject
     */
    public static Map getProcInputParamEObjects( StoredProcedure storedProc ) { // NO_UCD
        Map symbolEObjMap = new HashMap();

        // Add the Procedure Input Parameters to the Map
        // (for now adding null for the EObject)
        if (storedProc != null) {
            // Get the list of InputParameters
            List inputParams = storedProc.getInputParameters();
            // add the name/Parameter to the map
            Iterator iter = inputParams.iterator();
            while (iter.hasNext()) {
                SPParameter param = (SPParameter)iter.next();
                ElementSymbol symbol = param.getParameterSymbol();
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
     * Create a Map of unique name (key) to a "type" object (value). The type object may be a Datatype, if the ElementSymbol is
     * resolved. Else the type object will be a Class type.
     * 
     * @param command the supplied Command object.
     * @return map of unique name to type Object
     */
    public static Map getProjectedSymbolUniqueTypes( Command command ) {
        if (command != null) {
            Map symbolTypeMap = new HashMap();
            // Get the list of SELECT symbols
            List symbols = command.getProjectedSymbols();
            // Populate the list with the symbol names
            for (Iterator symbolIter = symbols.iterator(); symbolIter.hasNext();) {
                SingleElementSymbol symbol = (SingleElementSymbol)symbolIter.next();
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
     * Create a Map of unique name (key) to a "type" object (value). The type object may be a Datatype, if the ElementSymbol is
     * resolved. Else the type object will be a Class type. Also get the Procedure InputParameter types as well.
     * 
     * @param command the supplied Command object.
     * @return map of unique name to type Object
     */
    public static Map getProjectedSymbolAndProcInputUniqueTypes( Command command ) {
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
                SPParameter param = (SPParameter)iter.next();
                ElementSymbol symbol = param.getParameterSymbol();
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
     * ElementSymbol which is resolved to an EObject, the EObject will be added to the Map.
     * 
     * @param command the supplied Command object.
     * @return map of unique name to length
     */
    public static Map getProjectedSymbolEObjects( Command command ) {
        Map symbolEObjMap = new HashMap();
        if (command != null) {
            // Get the list of SELECT symbols
            List symbols = command.getProjectedSymbols();
            // Populate the list with the symbol names
            Iterator symbolIter = symbols.iterator();
            while (symbolIter.hasNext()) {
                SingleElementSymbol symbol = (SingleElementSymbol)symbolIter.next();
                String name = TransformationSqlHelper.getSingleElementSymbolShortName(symbol, false);
                Object eObj = null;
                if (symbol instanceof ElementSymbol) {
                    eObj = getElementSymbolEObject((ElementSymbol)symbol);
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
     * ElementSymbol which is resolved to an EObject, the EObject will be added to the Map.
     * 
     * @param command the supplied Command object.
     * @return map of unique name to length
     */
    public static Map getProjectedSymbolAndProcInputEObjects( Command command ) {
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
                SPParameter param = (SPParameter)iter.next();
                ElementSymbol symbol = param.getParameterSymbol();
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
     * @param command the supplied Command object.
     * @param hasXMLDocSource 'true' if the command has XML Document source
     * @return map of unique name to length
     */
    public static Map getProjectedSymbolLengths( Command command,
                                                 boolean hasXMLDocSource ) {
        Map symbolLengthMap = new HashMap();

        if (command != null) {
            // Get the list of SELECT symbols
            List symbols = command.getProjectedSymbols();
            // Populate the list with the symbol names
            Iterator symbolIter = symbols.iterator();
            while (symbolIter.hasNext()) {
                SingleElementSymbol symbol = (SingleElementSymbol)symbolIter.next();
                String name = TransformationSqlHelper.getSingleElementSymbolShortName(symbol, false);

                Object typeObj = getElementSymbolType(symbol);
                boolean xmlDocSourceCase = hasXMLDocSource && (command instanceof CreateUpdateProcedureCommand);

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
     * @param command the supplied Command object.
     * @param hasXMLDocSource 'true' if the command has XML Document source
     * @return map of unique name to length
     */
    public static Map getProjectedSymbolAndProcInputLengths( Command command,
                                                             boolean hasXMLDocSource ) {
        // Get the map of projected Symbol names to lengths
        Map symbolLengthMap = getProjectedSymbolLengths(command, hasXMLDocSource);

        if (command != null) {
            // Get the list of InputParameters
            List inputParams = getProcedureInputParams(command);
            // Add parameter lengths to the mapp
            Iterator paramIter = inputParams.iterator();
            while (paramIter.hasNext()) {
                SPParameter param = (SPParameter)paramIter.next();
                ElementSymbol symbol = param.getParameterSymbol();
                String name = symbol.getShortName();

                Object typeObj = getElementSymbolType(symbol);
                boolean xmlDocSourceCase = hasXMLDocSource && (command instanceof CreateUpdateProcedureCommand);

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
                                             SingleElementSymbol symbol,
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
                    if (String.class.equals(((Class)typeObj))) {//$NON-NLS-1$
                        int stringLength = getSymbolLength(symbol);
                        if (stringLength > 0) {
                            theMap.put(name, new Integer(stringLength));
                        } else {
                            theMap.put(name, new Integer(ModelerCore.getTransformationPreferences().getDefaultStringLength()));
                        }
                    } else if (Character.class.equals((Class) typeObj)) { //$NON-NLS-1$
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
     * @return Function null if not found
     * @since 4.2.2
     */
    private static Function isStringFunction( SingleElementSymbol symbol ) {
        ExpressionSymbol expressionSymbol = null;
        Function function = null;

        if (symbol instanceof AliasSymbol && ((AliasSymbol)symbol).getSymbol() instanceof ExpressionSymbol) {
            expressionSymbol = (ExpressionSymbol)((AliasSymbol)symbol).getSymbol();
        } else if (symbol instanceof ExpressionSymbol) {
            expressionSymbol = (ExpressionSymbol)symbol;
        }
        if (expressionSymbol != null && expressionSymbol.getExpression() instanceof Function) {
            function = (Function)expressionSymbol.getExpression();
            if (function.getName().equalsIgnoreCase(FunctionLibrary.CONCAT)
                || function.getName().equalsIgnoreCase(FunctionLibrary.CONCAT_OPERATOR)) {
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
     * @param Function function
     * @return boolean
     * @since 4.2.2
     */
    private static boolean isDecodeOrSubString( Function function ) {
        if (function.getName().equalsIgnoreCase(FunctionLibrary.DECODESTRING)
            || function.getName().equalsIgnoreCase(FunctionLibrary.SUBSTRING)) {
            return true;
        }
        return false;

    }

    /**
     * Determine the concatenated string lengths
     * 
     * @param Function
     * @return int length of 2 concatenated string columns
     * @since 4.2.2
     */
    private static int concatSymbolLength( Expression exprObject ) {
        Expression[] args = null;
        ElementSymbol elSymbol = null;
        int stringLength = 0;

        if (exprObject instanceof Function) {
            Function myFunc = (Function)exprObject;
            if (isDecodeOrSubString(myFunc)) {
                return stringLength += getMaxStringLength(myFunc);
            } else if (myFunc.getName().equalsIgnoreCase("chr")) { //$NON-NLS-1$
                return stringLength += 1;
            }

            args = myFunc.getArgs();
            for (int i = 0; i < args.length; i++) {
                Expression symbol = args[i];

                if (symbol instanceof Function) {
                    stringLength += concatSymbolLength(symbol);
                }

                if (symbol instanceof ElementSymbol) {
                    elSymbol = (ElementSymbol)symbol;
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
                if (symbol instanceof Constant) {
                    Constant constant = (Constant)args[i];
                    Object value = constant.getValue();
                    if (value != null && value instanceof String) {
                        stringLength += ((String)value).length();
                    } else {
                        stringLength += ModelerCore.getTransformationPreferences().getDefaultStringLength();
                    }
                }
            }
        } else if (exprObject instanceof ElementSymbol) {
            elSymbol = (ElementSymbol)exprObject;
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
        } else if (exprObject instanceof Constant) {
            Constant constant = (Constant)exprObject;
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
     * @return int length of concatenated expression
     * @since 4.2.2
     */
    public static int getSymbolLength( SingleElementSymbol symbol ) {
        int stringLength = 0;

        Function function = null;

        if ((function = isStringFunction(symbol)) != null) {
            if (!isDecodeOrSubString(function)) {
                Expression[] args = function.getArgs();
                for (int i = 0; i < args.length; i++) {
                    Expression exprSymbol = args[i];

                    if (exprSymbol instanceof Function) {
                        stringLength += concatSymbolLength(exprSymbol);
                    }
                    if (exprSymbol instanceof ElementSymbol) {
                        stringLength += concatSymbolLength(exprSymbol);
                    }
                    if (exprSymbol instanceof Constant) {
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
     * @param Function
     * @return int max length of function's first param
     * @since 4.2.2
     */
    private static int getMaxStringLength( Function function ) {
        Expression[] args = function.getArgs();
        Expression exprSymbol = args[0];
        if (function.getName().equalsIgnoreCase(FunctionLibrary.DECODESTRING)) {
            return getDecodeLength(function);
        }
        return concatSymbolLength(exprSymbol);
    }

    /**
     * Helper method to determine the max length of decodeString. Either the column size will be max or one of the string lengths
     * from decode part
     * 
     * @param Function
     * @return int max length of column size or max decode string length
     * @since 4.2.2
     */
    private static int getDecodeLength( Function function ) {
        Expression[] args = function.getArgs();
        Expression exprSymbol = null;

        int maxLength = 0;
        exprSymbol = args[0];
        if (exprSymbol instanceof ElementSymbol) {
            ElementSymbol elmSymbol = (ElementSymbol)exprSymbol;
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
        if (exprSymbol instanceof Constant) {
            Constant constSym = (Constant)exprSymbol;
            Object constObj = constSym.getValue();
            if (constObj != null && constObj instanceof String) {
                String decodes = (String)constObj;

                String delimiter = ","; //$NON-NLS-1$
                if (args.length == 3) {
                    exprSymbol = args[2];
                    constSym = (Constant)exprSymbol;
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
     * Get the ordered list of query select element Types from a Command The "type" will be one of three things (1) null - When
     * the target attribute is unmatched (2) Datatype - When the target attribute is bound to a symbol which resolves to a
     * MetaObject (3) java class type - When the target attribute is bound to an expression
     * 
     * @param comman the Command language object
     * @return the ordered list of query SELECT element types
     */
    public static List getProjectedSymbolTypes( Command command ) { // NO_UCD
        List selectTypes = new ArrayList();
        if (command != null) {
            // Get the list of SELECT symbols
            List symbols = command.getProjectedSymbols();
            Iterator iter = symbols.iterator();
            while (iter.hasNext()) {
                SingleElementSymbol symbol = (SingleElementSymbol)iter.next();
                selectTypes.add(getElementSymbolType(symbol));
            }
        }
        return selectTypes;
    }

    // Return a Map of renamed symbols - new name (key) and SelectSymbol (value)
    public static Map getRenamedSymbolsMap( List symbols ) {
        Map renameMap = new HashMap();
        // Make sure all the symbols are SingleElementSymbols
        List seSymbols = new ArrayList();
        Iterator symbolIter = symbols.iterator();
        while (symbolIter.hasNext()) {
            SelectSymbol sSymbol = (SelectSymbol)symbolIter.next();
            if (sSymbol instanceof MultipleElementSymbol) {
                List meSymbols = ((MultipleElementSymbol)sSymbol).getElementSymbols();
                if (meSymbols != null) {
                    Iterator meIter = meSymbols.iterator();
                    while (meIter.hasNext()) {
                        ElementSymbol eSymbol = (ElementSymbol)meIter.next();
                        if (eSymbol != null) {
                            seSymbols.add(eSymbol);
                        }
                    }
                }
            } else if (sSymbol instanceof SingleElementSymbol) {
                seSymbols.add(sSymbol);
            }
        }
        Collection elementNames = new ArrayList();
        // Populate the list with the symbol names
        symbolIter = seSymbols.iterator();
        while (symbolIter.hasNext()) {
            SingleElementSymbol seSymbol = (SingleElementSymbol)symbolIter.next();
            String name = TransformationSqlHelper.getSingleElementSymbolShortName(seSymbol, false);
            String uniqueName = name;

            SingleElementSymbol underlyingSymbol = seSymbol;

            // If this is an alias, set the symbol to be the underlying object
            if (seSymbol instanceof AliasSymbol) {
                underlyingSymbol = ((AliasSymbol)seSymbol).getSymbol();
            }
            if (underlyingSymbol instanceof ElementSymbol) {
                // Set MetaObject ref in Map for SingleElementSymbol
                ElementSymbol eSymbol = (ElementSymbol)underlyingSymbol;
                // There may be duplicates in the elementMap (self-joins,etc)
                // If so, the name is made unique first...
                uniqueName = getUniqueName(name, elementNames);
                // Ignore TempMetadataID
                Object idObj = eSymbol.getMetadataID();
                if (idObj != null && idObj instanceof MetadataRecord) {
                    elementNames.add(uniqueName);
                }
            } else if (underlyingSymbol instanceof ExpressionSymbol) {
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
            GroupSymbol gSymbol = createGroupSymbol(sqlAlias);
            result.add(new UnaryFromClause(gSymbol));
        }
        return result;
    }

    /**
     * Create a FROM clause, given a Table, Procedure or SqlAlias
     * 
     * @param eObject the supplied EObject
     * @return the generated FROM clause
     */
    private static FromClause createFromClause( EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);

        FromClause fromClause = new UnaryFromClause(createGroupSymbol(eObject));

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
            GroupSymbol gSymbol = createGroupSymbol(eObj);
            if (gSymbol != null) {
                result.add(gSymbol);
            }
        }
        return result;
    }

    /**
     * Create a StoredProcedure from a Procedure EObject or SqlAlias. The aliasedObject of the SqlAlias must be a procedure.
     * 
     * @param eObj the Procedure or SqlAlias object
     * @return the generated StoredProcedure
     */
    public static StoredProcedure createStoredProc( EObject eObj ) {
        CoreArgCheck.isNotNull(eObj);

        StoredProcedure storedProc = null;

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
            storedProc = new StoredProcedure();
            storedProc.setProcedureName(procFullName);
            storedProc.setDisplayNamedParameters(true);
            // Get the Parameter attributes from the group
            List procParams = procedureAspect.getParameters(eObj);

            // Create List of SPParams from the attributes (IN, IN_OUT)
            List spParams = createSPParams(procParams);
            Iterator iter = spParams.iterator();

            while (iter.hasNext()) {
                storedProc.setParameter((SPParameter)iter.next());
            }

            EObject results = (EObject)procedureAspect.getResult(eObj);
            if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedureResultSet(results)) {
                SqlColumnSetAspect rsAspect = (SqlColumnSetAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(results);
                List rsCols = rsAspect.getColumns(results);
                if (rsCols.size() > 0) {
                    // it doesn't matter at this point what the columns are, just that is has a resultset
                    SPParameter param = new SPParameter(spParams.size(), SPParameter.RESULT_SET, "RESULT"); //$NON-NLS-1$
                    param.addResultSetColumn("RESULT", String.class, "RESULT"); //$NON-NLS-1$ //$NON-NLS-2$
                    storedProc.setParameter(param);
                }
            }
        }

        return storedProc;
    }

    /**
     * Create a GroupSymbol from a group EOject or SqlAlias
     * 
     * @param groupEObj the supplied group EObject
     * @return the corresponding GroupSymbols
     */
    public static GroupSymbol createGroupSymbol( EObject groupEObj ) {
        GroupSymbol gSymbol = null;

        String aliasName = null;
        // If the supplied EObject is SqlAlias, get Alias name and aliased Object
        if (groupEObj instanceof SqlAlias) {
            SqlAlias sqlAlias = (SqlAlias)groupEObj;
            aliasName = sqlAlias.getAlias();
            groupEObj = sqlAlias.getAliasedObject();
        }
        // Create the GroupSymbol using the EObject and alias (if any)
        if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable(groupEObj)) {
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
                gSymbol = new GroupSymbol(aliasName, tableFullName);
            } else {
                gSymbol = new GroupSymbol(tableFullName);
            }
            // Set the MetadataID if it was found
            if (groupID != null) {
                gSymbol.setMetadataID(groupID);
            }
        } else if (groupEObj instanceof InputSet) {
            // InputSet iSet = (InputSet)groupEObj;
            String inputSetFullName = "InputSet"; //$NON-NLS-1$
            String inputSetShortName = "InputSet"; //$NON-NLS-1$
            // SqlTableAspect tableAspect = (SqlTableAspect)SqlAspectManager.getSqlAspect(groupEObj);
            // String inputSetFullName = tableAspect.getFullName(groupEObj);
            // String inputSetShortName = tableAspect.getName(groupEObj);
            if (aliasName != null && !aliasName.equalsIgnoreCase(inputSetShortName)) {
                gSymbol = new GroupSymbol(aliasName, inputSetFullName);
            } else {
                gSymbol = new GroupSymbol(inputSetFullName);
            }
        } else if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(groupEObj)) {
            SqlProcedureAspect procAspect = (SqlProcedureAspect)AspectManager.getSqlAspect(groupEObj);
            String name = procAspect.getFullName(groupEObj);
            String shortName = procAspect.getName(groupEObj);
            if (aliasName != null && !aliasName.equalsIgnoreCase(shortName)) {
                gSymbol = new GroupSymbol(aliasName, name);
            } else {
                gSymbol = new GroupSymbol(name);
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
        // Defect 17972: The following call was done many times and opened/closed model files to peek at
        // the Header (isModelFile()) Dennis F. Suggested replacing with the above code
        // QueryMetadataInterface resolver = TransformationMetadataFactory.getInstance().getModelerMetadata(elmntObj);
        // elemID = resolver.getElementID(elementFullName);
        // } catch (QueryMetadataException e) {
        //            String message = TransformationPlugin.Util.getString("TransformationSqlHelper.elementIDNotFoundError",     //$NON-NLS-1$
        // elementFullName);
        // TransformationPlugin.Util.log(IStatus.WARNING, e, message);
        // } catch (MetaMatrixComponentException e) {
        //            String message = TransformationPlugin.Util.getString("TransformationSqlHelper.elementIDNotFoundError",     //$NON-NLS-1$
        // elementFullName);
        // TransformationPlugin.Util.log(IStatus.WARNING, e, message);
        // }
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
        // try {
        // Defect 17972: The following call was done many times and opened/closed model files to peek at
        // the Header (isModelFile()) Dennis F. Suggested replacing with the above code
        // QueryMetadataInterface resolver = TransformationMetadataFactory.getInstance().getModelerMetadata(grpObj);
        // groupID = recom.metamatrixsolver.getGroupID(groupFullName);
        SqlAspect sqlAspect = AspectManager.getSqlAspect(grpObj);
        CoreArgCheck.isInstanceOf(SqlTableAspect.class, sqlAspect);
        groupID = new TableRecordImpl((SqlTableAspect)sqlAspect, grpObj);
        // } catch (QueryMetadataException e) {
        //            String message = TransformationPlugin.Util.getString("TransformationSqlHelper.groupIDNotFoundError",     //$NON-NLS-1$
        // groupFullName);
        // TransformationPlugin.Util.log(IStatus.WARNING, e, message);
        // } catch (MetaMatrixComponentException e) {
        //            String message = TransformationPlugin.Util.getString("TransformationSqlHelper.groupIDNotFoundError",     //$NON-NLS-1$
        // groupFullName);
        // TransformationPlugin.Util.log(IStatus.WARNING, e, message);
        // }
        return groupID;
    }

    /**
     * Get the GroupID for the supplied Group FullName. Returns the GroupID, or null if it wasnt found.
     * 
     * @param procFullName the fullName of the procedure
     * @param procObj the eObject of the procedure
     * @return the Object ID
     */
    private static StoredProcedureInfo getProcInfo( String procFullName,
                                                    EObject procObj ) {
        StoredProcedureInfo procInfo = null;
        try {
            QueryMetadataInterface resolver = TransformationMetadataFactory.getInstance().getModelerMetadata(procObj);
            procInfo = resolver.getStoredProcedureInfoForProcedure(procFullName);
        } catch (QueryMetadataException e) {
            String message = TransformationPlugin.Util.getString("TransformationSqlHelper.groupIDNotFoundError", //$NON-NLS-1$
                                                                 procFullName);
            TransformationPlugin.Util.log(IStatus.WARNING, e, message);
        } catch (TeiidComponentException e) {
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
        GroupSymbol groupSymbol = createGroupSymbol(groupSqlAlias);

        // Get the aliased EObject
        EObject groupEObj = groupSqlAlias.getAliasedObject();

        // Create the Symbol using the EObject and alias (if any)
        if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable(groupEObj)) {
            SqlTableAspect tableAspect = (SqlTableAspect)AspectManager.getSqlAspect(groupEObj);
            List columns = tableAspect.getColumns(groupEObj);
            Iterator columnIter = columns.iterator();
            SingleElementSymbol seSymbol = null;
            while (columnIter.hasNext()) {
                EObject columnEObj = (EObject)columnIter.next();
                seSymbol = createElemSymbol(columnEObj, groupSymbol);
                result.add(seSymbol);
            }
        } else if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(groupEObj)) {
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
                final Class clazz = DataTypeManager.getDataTypeClass(type);

                String paramName = TransformationHelper.getSqlEObjectName(elemEObj);

                ElementSymbol symbol = new ElementSymbol(groupSymbol.getName() + SingleElementSymbol.SEPARATOR + paramName);
                symbol.setType(clazz);
                result.add(symbol);

                if (inout.contains(elemEObj)) {
                    symbol = new ElementSymbol(groupSymbol.getName() + SingleElementSymbol.SEPARATOR + paramName + "_IN"); //$NON-NLS-1$
                    symbol.setType(clazz);
                    result.add(symbol);
                }
            }
        }

        return result;
    }

    /**
     * Create an ElementSymbol from a group EOject or SqlAlias
     * 
     * @param groupEObj the supplied group EObject
     * @return the corresponding GroupSymbols
     */
    public static SingleElementSymbol createElemSymbol( EObject elemEObj,
                                                        GroupSymbol parentGroupSymbol ) {
        SingleElementSymbol seSymbol = null;

        // Get name for the supplied group
        String tableName = parentGroupSymbol.getName();
        if (tableName == null) {
            tableName = SqlConstants.BLANK;
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

            ElementSymbol element = new ElementSymbol(fullName);
            element.setGroupSymbol(parentGroupSymbol);
            final String rtType = aspect.getRuntimeType(elemEObj);
            if (rtType != null) {
                final Class clazz = DataTypeManager.getDataTypeClass(rtType);
                element.setMetadataID(new TempMetadataID(fullName.toUpperCase(), clazz));
                element.setType(clazz);
            } else {
                element.setMetadataID(new TempMetadataID(fullName.toUpperCase(), DataTypeManager.DefaultDataClasses.NULL));
                element.setType(DataTypeManager.DefaultDataClasses.NULL);
            }
            seSymbol = element;
            // Create ElementSymbols using eObj and alias (if any)
            // Use the tableName and column short name to create the ElementSymbol
        } else if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(elemEObj)) {
            SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(elemEObj);
            String colShortName = columnAspect.getName(elemEObj);
            String colUUID = TransformationHelper.getSqlEObjectUUID(elemEObj);
            // Get MetadataID for uuid
            Object elemID = getElementID(colUUID, elemEObj);
            if (columnAliasName != null) {
                ElementSymbol elemSymbol = new ElementSymbol(tableName + "." + colShortName); //$NON-NLS-1$
                elemSymbol.setGroupSymbol(parentGroupSymbol);
                // Set the MetadataID if it was found
                if (elemID != null) {
                    elemSymbol.setMetadataID(elemID);
                }
                final String rtType = columnAspect.getRuntimeType(elemEObj);
                if (rtType != null) {
                    final Class clazz = DataTypeManager.getDataTypeClass(rtType);
                    elemSymbol.setType(clazz);
                } else {
                    elemSymbol.setType(DataTypeManager.DefaultDataClasses.NULL);
                }
                seSymbol = new AliasSymbol(columnAliasName, elemSymbol);
            } else {
                ElementSymbol elemSymbol = new ElementSymbol(tableName + "." + colShortName); //$NON-NLS-1$
                elemSymbol.setGroupSymbol(parentGroupSymbol);
                // Set the MetadataID if it was found
                if (elemID != null) {
                    elemSymbol.setMetadataID(elemID);
                }
                final String rtType = columnAspect.getRuntimeType(elemEObj);
                if (rtType != null) {
                    final Class clazz = DataTypeManager.getDataTypeClass(rtType);
                    elemSymbol.setType(clazz);
                } else {
                    elemSymbol.setType(DataTypeManager.DefaultDataClasses.NULL);
                }
                seSymbol = elemSymbol;
            }
        } else if (TransformationHelper.isSqlProcedureParameter(elemEObj)) {
            SqlProcedureParameterAspect aspect = (SqlProcedureParameterAspect)AspectManager.getSqlAspect(elemEObj);
            String paramName = TransformationHelper.getSqlEObjectFullName(elemEObj);

            ElementSymbol element = new ElementSymbol(paramName);
            element.setGroupSymbol(parentGroupSymbol);
            if (aspect != null) {
                final String rtType = aspect.getRuntimeType(elemEObj);
                if (rtType != null) {
                    final Class clazz = DataTypeManager.getDataTypeClass(rtType);
                    element.setMetadataID(new TempMetadataID(paramName.toUpperCase(), clazz));
                    element.setType(clazz);
                } else {
                    element.setMetadataID(new TempMetadataID(paramName.toUpperCase(), DataTypeManager.DefaultDataClasses.NULL));
                    element.setType(DataTypeManager.DefaultDataClasses.NULL);
                }
            } else {
                element.setMetadataID(new TempMetadataID(paramName.toUpperCase(), DataTypeManager.DefaultDataClasses.NULL));
                element.setType(DataTypeManager.DefaultDataClasses.NULL);
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

            // Iterate through the current Select symbols
            // If any are MultipleElementSymbols, replace them with the SingleElementSymbols
            // if they contain a renamed symbol
            // Once a symbol is renamed, the Map entry is removed so duplicates dont get renamed
            for (int i = 0; i < seSymbols.size(); i++) {
                SelectSymbol currentSelectSymbol = (SelectSymbol)seSymbols.get(i);
                // ---------------------------------------------------------------
                // Current Symbol is MultiElement - expand it if necessary
                // ---------------------------------------------------------------
                if (currentSelectSymbol instanceof MultipleElementSymbol) {
                    // Check whether the MultiSymbol needs expanding
                    boolean shouldExpand = shouldExpand((MultipleElementSymbol)currentSelectSymbol, workingRenSymMap);
                    // If the MultiSymbol needs expanding, expand it with renamed symbol
                    if (shouldExpand) {
                        // Get SingleElementSybmols for this MultiSymbol
                        List multiElemSymbols = ((MultipleElementSymbol)currentSelectSymbol).getElementSymbols();
                        Iterator iter = multiElemSymbols.iterator();
                        // Go thru all SingleElement symbols, and rename if necessary
                        while (iter.hasNext()) {
                            SingleElementSymbol renamedSymbol = renameSymbolUsingMap((SingleElementSymbol)iter.next(),
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
                } else if (currentSelectSymbol instanceof SingleElementSymbol) {
                    SingleElementSymbol renamedSymbol = renameSymbolUsingMap((SingleElementSymbol)currentSelectSymbol,
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
    private static boolean shouldExpand( MultipleElementSymbol multiElemSymbol,
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
     * method to rename a SingleElementSymbol. The SingleElementSymbol is checked against the renamedSymbolsMap. If the symbol is
     * in the renamedSymbolsMap, set the name to the new value. Otherwise, return the original symbol.
     * 
     * @param seSymbol the SingleElementSymbol to check.
     * @param renamedSymbolsMap the map of renamed symbols
     * @return the renamed SingleElementSymbol (renamed only if necessary)
     */
    private static SingleElementSymbol renameSymbolUsingMap( SingleElementSymbol seSymbol,
                                                             Map renamedSymbolsMap ) {
        // Default is just going to return what comes in
        SingleElementSymbol resultSymbol = seSymbol;
        if (seSymbol != null) {
            Iterator renamedIter = renamedSymbolsMap.keySet().iterator();
            // Check the Map. If seSymbol is in the Map, rename it to the new name.
            while (renamedIter.hasNext()) {
                // newName key
                String newName = (String)renamedIter.next();
                // get corresponding symbol
                SingleElementSymbol renamedSymbol = (SingleElementSymbol)renamedSymbolsMap.get(newName);
                // If seSymbol is in the Map, rename it and quit.
                if (renamedSymbol.equals(seSymbol)) {
                    if (seSymbol instanceof AliasSymbol) {
                        seSymbol.setShortName(newName);
                        resultSymbol = seSymbol;
                    } else {
                        resultSymbol = new AliasSymbol(newName, seSymbol);
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
            SqlAspect sqlAspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(paramObject);
            if (sqlAspect instanceof SqlProcedureParameterAspect) {
                SqlProcedureParameterAspect paramAspect = (SqlProcedureParameterAspect)sqlAspect;
                int direction = paramAspect.getType(paramObject);
                String name = paramAspect.getName(paramObject);
                switch (direction) {
                    case MetadataConstants.PARAMETER_TYPES.IN_PARM:
                        SPParameter spparam1 = new SPParameter(index, new ElementSymbol(name));
                        spparam1.setName(name);
                        spparam1.setParameterType(SPParameter.IN);
                        spparams.add(spparam1);
                        index++;
                        break;
                    case MetadataConstants.PARAMETER_TYPES.INOUT_PARM:
                        SPParameter spparam2 = new SPParameter(index, new ElementSymbol(name));
                        spparam2.setName(name);
                        spparam2.setParameterType(SPParameter.INOUT);
                        spparams.add(spparam2);
                        index++;
                        break;
                    case MetadataConstants.PARAMETER_TYPES.OUT_PARM:
                        SPParameter spparam3 = new SPParameter(index, new ElementSymbol(name));
                        spparam3.setName(name);
                        spparam3.setParameterType(SPParameter.OUT);
                        spparams.add(spparam3);
                        index++;
                        break;
                    case MetadataConstants.PARAMETER_TYPES.RETURN_VALUE:
                        SPParameter spparam4 = new SPParameter(index, new ElementSymbol(name));
                        spparam4.setName(name);
                        spparam4.setParameterType(SPParameter.RETURN_VALUE);
                        spparams.add(spparam4);
                        index++;
                        break;
                }
            }
        }
        return spparams;
    }

    // Get Short name for GroupSymbol
    public static String getGroupSymbolShortName( GroupSymbol gSymbol ) {
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
     * @param command the Command language object
     * @return the List of GroupSymbols
     */
    public static Collection getGroupSymbols( Command command ) {
        // All groups - including duplicates
        Collection allGrps = GroupCollectorVisitor.getGroupsIgnoreInlineViews(command, false);
        // New group for result - no duplicates
        Collection result = new ArrayList(allGrps.size());
        Iterator iter = allGrps.iterator();
        while (iter.hasNext()) {
            GroupSymbol gSymbol = (GroupSymbol)iter.next();
            if (!containsGroupSymbol(result, gSymbol)) {
                result.add(gSymbol);
            }
        }
        return result;
    }

    /**
     * Determine if the supplied symbols list contains the supplied GroupSymbol. Both the GroupSymbol canonical name and
     * definitions are compared. This method required because the GroupSymbol.equals will not work - it assumes that both
     * GroupSymbols were obtained from the same "queryFrame". This is not always the case, such as UNION queries.
     * 
     * @param symbols the List of GroupSymbols
     * @param gSymbol the groupSymbol to compare vs the symbols list
     * @return 'true' if the list already contains the supplied symbol, 'false' if not.
     */
    public static boolean containsGroupSymbol( Collection symbols,
                                               GroupSymbol gSymbol ) {
        boolean result = false;
        // Get the supplied GroupSymbol name and definition
        String gSymbName = gSymbol.getCanonicalName();
        String gSymbDefn = gSymbol.getDefinition();
        // Check the list for a matching symbol
        Iterator iter = symbols.iterator();
        while (iter.hasNext()) {
            GroupSymbol listSymbol = (GroupSymbol)iter.next();
            String lSymbName = listSymbol.getCanonicalName();
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
                                                 SingleElementSymbol eSymbol ) {
        EObject referencedColumn = getSingleElementSymbolEObject(eSymbol);
        Iterator iter = symbols.iterator();
        while (iter.hasNext()) {
            SingleElementSymbol next = (SingleElementSymbol)iter.next();
            if (eSymbol == next) {
                return true;
            }
            if (eSymbol.getName().equalsIgnoreCase(next.getName())) {
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
    public static EObject getSingleElementSymbolEObject( SingleElementSymbol singleSymbol ) {
        EObject referencedColumn = null;
        if (singleSymbol instanceof AliasSymbol) {
            SingleElementSymbol theSymbol = ((AliasSymbol)singleSymbol).getSymbol();
            if (theSymbol instanceof ElementSymbol) {
                referencedColumn = TransformationSqlHelper.getElementSymbolEObject((ElementSymbol)theSymbol);
            }
        } else if (singleSymbol instanceof ElementSymbol) {
            referencedColumn = TransformationSqlHelper.getElementSymbolEObject((ElementSymbol)singleSymbol);
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
        // Get the Command for the supplied Type
        Command command = TransformationHelper.getCommand(transMappingRoot, type);

        int refCount = 0;
        if (command != null) {
            // Get the list of References
            List<Reference> refs = ReferenceCollectorVisitor.getReferences(command);
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
    public static EObject getElementSymbolEObject( ElementSymbol symbol ) {
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
     * @param command the Command that the ElementSymbol comes from
     * @return the symbol EObject, null if not found
     */
    public static EObject getElementSymbolEObject( ElementSymbol symbol,
                                                   Command command ) {
        EObject result = null;
        if (symbol != null) {
            Object elemObj = symbol.getMetadataID();
            if (elemObj instanceof TempMetadataID) {
                elemObj = ((TempMetadataID)elemObj).getOriginalMetadataID();
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
     * SingleElementSymbol is Expression, etc, type is Class
     * 
     * @param symbol the SingleElementSymbol
     * @return the symbols type
     */
    public static Object getElementSymbolType( SingleElementSymbol symbol ) {
        Object datatype = null;
        // If this is an alias, set the symbol to be the aliased symbol
        if (symbol instanceof AliasSymbol) {
            symbol = ((AliasSymbol)symbol).getSymbol();
        }

        // -------------------------------------
        // Element Symbol
        // -------------------------------------
        if (symbol instanceof ElementSymbol) {
            ElementSymbol eSymbol = (ElementSymbol)symbol;
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
            } else if (idObj instanceof TempMetadataID) {
                datatype = ((TempMetadataID)idObj).getType();
                // MetadataID is null, get symbol datatype
            } else if (idObj == null) {
                datatype = eSymbol.getType();
            }
            // -------------------------------------
            // Expression Symbol
            // -------------------------------------
        } else if (symbol instanceof ExpressionSymbol) {
            ExpressionSymbol exSymbol = (ExpressionSymbol)symbol;
            datatype = exSymbol.getType();
        }
        return datatype;
    }

    /**
     * Get the SingleElementSymbol length. If symbol is not resolved or not a SqlColumnAspect, returns -1.
     * 
     * @param symbol the SingleElementSymbol
     * @return the symbols type length
     */
    public static int getElementSymbolLength( SingleElementSymbol symbol ) {
        int length = -1;
        // If this is an alias, set the symbol to be the aliased symbol
        if (symbol instanceof AliasSymbol) {
            symbol = ((AliasSymbol)symbol).getSymbol();
        }

        // -------------------------------------
        // Element Symbol
        // -------------------------------------
        if (symbol instanceof ElementSymbol) {
            ElementSymbol eSymbol = (ElementSymbol)symbol;
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
     * @param command the Command that the elemSymbols come from
     * @return the List of EObjects
     */
    public static List getElementSymbolEObjects( Collection elemSymbols,
                                                 Command command ) {
        List result = Collections.EMPTY_LIST;
        if (elemSymbols != null) {
            result = new ArrayList(elemSymbols.size());
            Iterator iter = elemSymbols.iterator();
            while (iter.hasNext()) {
                ElementSymbol eSymbol = (ElementSymbol)iter.next();
                EObject elemEObj = getElementSymbolEObject(eSymbol, command);
                if (elemEObj != null) {
                    result.add(elemEObj);
                }
            }
        }
        return result;
    }

    /**
     * Get the EObject that the GroupSymbol is resolved to.
     * 
     * @param symbol the GroupSymbol
     * @return the EObject that the GroupSymbol is resolved to.
     */
    public static EObject getGroupSymbolEObject( GroupSymbol symbol ) {
        EObject result = null;
        if (symbol != null) {
            Object groupObj = symbol.getMetadataID();
            if (groupObj != null) {
                if (symbol.isProcedure()) {
                    TempMetadataID tid = (TempMetadataID)groupObj;
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
                GroupSymbol gSymbol = (GroupSymbol)iter.next();
                EObject grpEObj = getGroupSymbolEObject(gSymbol);
                if (grpEObj != null) {
                    result.add(grpEObj);
                }
            }
        }
        return result;
    }

    /**
     * Get the EObject that the StoredProcedure is resolved to.
     * 
     * @param symbol the StoredProcedure
     * @return the EObject that the StoredProcedure is resolved to.
     */
    public static EObject getStoredProcedureEObject( StoredProcedure storedProc ) {
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
     * @return 'true' if the Select clause contains any attributes belonging to the groupList groups
     */
    public static boolean hasSqlAliasGroupAttributes( Query query,
                                                      List sqlAliasGroups ) {
        boolean result = false;
        // Create List of GroupSymbols corresponding to supplied SqlAliases
        List aliasGroupSymbols = createGroupSymbols(sqlAliasGroups);

        if (!isSelectStar(query.getSelect())) {
            // Get all ElementSymbols referenced in the select
            Collection selectElements = ElementCollectorVisitor.getElements(query.getSelect(), true);
            // Determine if any of the elements groups is in the remove list
            Iterator iter = selectElements.iterator();
            while (iter.hasNext()) {
                ElementSymbol selectElem = (ElementSymbol)iter.next();
                // Get all Groups referenced by the ElementSymbol
                Collection symbolGroups = GroupsUsedByElementsVisitor.getGroups(selectElem);
                Iterator symbolGroupIter = symbolGroups.iterator();
                while (symbolGroupIter.hasNext()) {
                    GroupSymbol groupSymbol = (GroupSymbol)symbolGroupIter.next();
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
    public static boolean hasSqlElemSymbols( Query query,
                                             List elemEObjs ) {
        boolean result = false;
        List projSymbolNames = getProjectedSymbolNames(query);

        // Determine if any of the supplied sqlColumns is in the projected symbols
        Iterator iter = elemEObjs.iterator();
        while (iter.hasNext()) {
            Object elem = iter.next();
            if ((elem instanceof EObject)
                && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn((EObject)elem)) {
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
     * @return 'true' if the Select clause is a SELECT *
     */
    public static boolean isSelectStar( Select select ) {
        boolean result = false;
        List currentSelectSymbols = select.getSymbols();
        if (currentSelectSymbols.size() == 1) {
            SelectSymbol singleSelectSymbol = (SelectSymbol)currentSelectSymbols.get(0);
            if (singleSelectSymbol instanceof MultipleElementSymbol) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Convert an ExpressionSymbol to the specified type
     * 
     * @param exprSymbol the original ExpressionSymbol
     * @param newTypeName the type to convert the original ExpressionSymbol to
     * @return the converted expression Symbol
     */
    public static ExpressionSymbol convert( ExpressionSymbol exprSymbol,
                                            String newTypeName ) {
        ExpressionSymbol newExpressionSymbol = (ExpressionSymbol)exprSymbol.clone();

        // Get the original ExpressionSymbol Type
        Expression expr = exprSymbol.getExpression();
        // if ( expr == null ) {
        // // AggregateSymbols can contain null expressions - if so, use the symbol itself
        // expr = exprSymbol;
        // }
        //        
        Class originalTypeClass = expr.getType();
        String originalTypeName = DataTypeManager.getDataTypeName(originalTypeClass);

        // If the desired Type is different from the original Type, do convert
        if (!originalTypeName.equalsIgnoreCase(newTypeName)) {
            // If the supplied ExpressionSymbol is a Convert Function, try to reuse it and change the type
            if (isConvertFunction(exprSymbol)) {
                Expression convExpr = getConvertedExpr(exprSymbol);
                Class convExprTypeClass = convExpr.getType();
                String convExprTypeName = DataTypeManager.getDataTypeName(convExprTypeClass);
                // Check whether there is a conversion
                boolean isExplicit = DataTypeManager.isExplicitConversion(convExprTypeName, newTypeName);
                boolean isImplicit = DataTypeManager.isImplicitConversion(convExprTypeName, newTypeName);
                // If theres a conversion, go ahead and use it
                if (isExplicit || isImplicit) {
                    Function func = getConversion(convExprTypeName, newTypeName, convExpr);
                    newExpressionSymbol.setExpression(func);
                } else {
                    Function convertFunction = getConversion(originalTypeName, newTypeName, expr);
                    newExpressionSymbol.setExpression(convertFunction);
                }
            } else {
                Function convertFunction = getConversion(originalTypeName, newTypeName, expr);
                newExpressionSymbol.setExpression(convertFunction);
            }
        }
        return newExpressionSymbol;
    }

    /**
     * Convert an Elementymbol to the specified type. An alias symbol is created with the supplied aliasName. If the supplied
     * aliasName is null, the original element symbol name is used as the alias.
     * 
     * @param elementSymbol the original ElementSymbol
     * @param newTypeName the type to convert the original ElementSymbol to
     * @param aliasName if not null, this is the aliasName, otherwise use elementSymbolName
     * @return the converted element Symbol
     */
    public static AliasSymbol convert( ElementSymbol elementSymbol,
                                       String newTypeName,
                                       String aliasName ) {
        Class originalTypeClass = elementSymbol.getType();
        String originalTypeName = DataTypeManager.getDataTypeName(originalTypeClass);

        Function convertFunction = getConversion(originalTypeName, newTypeName, elementSymbol);

        ExpressionSymbol exprSymbol = new ExpressionSymbol(NEW_CONVERSION_NAME, convertFunction);
        AliasSymbol newSymbol = null;
        if (aliasName != null) {
            newSymbol = new AliasSymbol(aliasName, exprSymbol);
        } else {
            newSymbol = new AliasSymbol(elementSymbol.getShortName(), exprSymbol);
        }
        return newSymbol;
    }

    public static Function getConversion( String originalTypeName,
                                          String newTypeName,
                                          Expression expression ) {
        Class originalType = DataTypeManager.getDataTypeClass(originalTypeName);

        FunctionLibrary library = UdfManager.INSTANCE.getFunctionLibrary();
        FunctionDescriptor fd = library.findFunction(FunctionLibrary.CONVERT, new Class[] {originalType,
            DataTypeManager.DefaultDataClasses.STRING});

        Function conversion = new Function(fd.getName(), new Expression[] {expression, new Constant(newTypeName)});
        conversion.setType(DataTypeManager.getDataTypeClass(newTypeName));
        conversion.setFunctionDescriptor(fd);

        return conversion;
    }

    public static boolean isConvertFunction( ExpressionSymbol exprSymbol ) {
        Expression expr = exprSymbol.getExpression();
        if (expr instanceof Function) {
            String fName = ((Function)expr).getName();
            if (fName.equalsIgnoreCase(SQLConstants.Reserved.CONVERT)) {
                return true;
            }
        }
        return false;
    }

    public static Expression getConvertedExpr( ExpressionSymbol exprSymbol ) {
        if (isConvertFunction(exprSymbol)) {
            Expression expr = exprSymbol.getExpression();
            Expression fExp = ((Function)expr).getArg(0);
            return fExp;
            // if(fExp instanceof SingleElementSymbol) {
            // return (SingleElementSymbol)fExp;
            // }
        }
        return null;
    }

    public static AliasSymbol convertElementSymbol( ElementSymbol symbol,
                                                    String targetTypeStr,
                                                    String aliasName ) {
        return convert(symbol, targetTypeStr, aliasName);
    }

    public static ExpressionSymbol convertExpressionSymbol( ExpressionSymbol symbol,
                                                            String targetTypeStr ) {
        // Handle case where the Expression is already a Convert Function
        if (isConvertFunction(symbol)) {
            Expression cExpr = getConvertedExpr(symbol);
            if (cExpr instanceof SingleElementSymbol) {
                SingleElementSymbol seSymbol = (SingleElementSymbol)cExpr;
                String seSymbolTypeStr = DataTypeManager.getDataTypeName(seSymbol.getType());
                // Check whether there is a conversion from the underlying symbol to the attribute type
                boolean isExplicitConv = DataTypeManager.isExplicitConversion(seSymbolTypeStr, targetTypeStr);
                boolean isImplicitConv = DataTypeManager.isImplicitConversion(seSymbolTypeStr, targetTypeStr);
                if (isImplicitConv || isExplicitConv) {
                    if (seSymbol instanceof ExpressionSymbol) {
                        return convertExpressionSymbol((ExpressionSymbol)seSymbol, targetTypeStr);
                    } else if (seSymbol instanceof ElementSymbol) {
                        AliasSymbol aSymbol = convertElementSymbol((ElementSymbol)seSymbol, targetTypeStr, null);
                        SingleElementSymbol aseSymbol = aSymbol.getSymbol();
                        if (aseSymbol instanceof ExpressionSymbol) {
                            return (ExpressionSymbol)aseSymbol;
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
