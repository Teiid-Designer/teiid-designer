/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.api.exception.query.QueryMetadataException;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.TeiidComponentException;
import org.teiid.core.TeiidRuntimeException;
import org.teiid.core.types.DataTypeManager;
import org.teiid.query.metadata.QueryMetadataInterface;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.visitor.ReferenceCollectorVisitor;
import org.teiid.query.validator.UpdateValidator;
import org.teiid.query.validator.Validator;
import org.teiid.query.validator.ValidatorFailure;
import org.teiid.query.validator.ValidatorReport;
import org.teiid.query.validator.UpdateValidator.UpdateType;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnSetAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.internal.core.index.ModelResourceIndexSelector;
import com.metamatrix.modeler.internal.core.index.NullIndexSelector;
import com.metamatrix.modeler.internal.core.index.TargetLocationIndexSelector;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.modeler.transformation.metadata.QueryMetadataContext;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFacade;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFactory;
import com.metamatrix.modeler.transformation.metadata.VdbMetadata;

/**
 * TransformationValidator Static methods for doing Validation on the transformation.
 */
public class TransformationValidator implements QueryValidator {

    private static final boolean DEFAULT_USE_CACHING = true;

    /** Key used when setting the metadata context in the validation context. */
    public static final String QUERY_METADATA_INTERFACE = TransformationValidator.class.getSimpleName()
                                                          + ".QUERY_METADATA_INTERFACE"; //$NON-NLS-1$

    // toolkit metadata
    private QueryMetadataInterface metadata;
    private final boolean restrictSearch;
    private final SqlTransformationMappingRoot mappingRoot;
    private final EObject targetGroup;
    private final ValidationContext validationContext;
    // private String indexFilePath = IndexUtil.INDEX_PATH;
    private static final String XML_URI = "http://www.metamatrix.com/metamodels/XmlDocument"; //$NON-NLS-1$

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Constructor for TransformationValidator
     * 
     * @param eObject The EObject used to contruct metadata instance used by the validator
     */
    public TransformationValidator( final SqlTransformationMappingRoot eObject ) {
        this(eObject, null, DEFAULT_USE_CACHING, false);
    }

    /**
     * Constructor for TransformationValidator
     * 
     * @param eObject The EObject used to contruct metadata instance used by the validator
     * @param useCaching A boolean indicating if metadata caching should be enabled.
     */
    public TransformationValidator( final SqlTransformationMappingRoot eObject,
                                    final boolean useCaching ) {
        this(eObject, null, useCaching, false);
    }

    /**
     * Constructor for TransformationValidator
     * 
     * @param eObject The EObject used to contruct metadata instance used by the validator
     * @param useCaching A boolean indicating if metadata caching should be enabled.
     * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports or if the whole workspace
     *        needs to be searched
     */
    public TransformationValidator( final SqlTransformationMappingRoot eObject,
                                    final boolean useCaching,
                                    final boolean restrictSearch ) {
        this(eObject, null, useCaching, restrictSearch);
    }

    /**
     * Constructor for TransformationValidator
     * 
     * @param eObject The EObject used to contruct metadata instance used by the validator
     * @param context The ValidationContext to use
     * @param useCaching A boolean indicating if metadata caching should be enabled.
     * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports or if the whole workspace
     *        needs to be searched
     */
    public TransformationValidator( final SqlTransformationMappingRoot eObject,
                                    final ValidationContext context,
                                    final boolean useCaching,
                                    final boolean restrictSearch ) {
        this.restrictSearch = restrictSearch;
        this.mappingRoot = eObject;
        this.targetGroup = eObject.getTarget();
        this.validationContext = context;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * This method does a validation on all of the transformatin SQL Strings (SELECT, INSERT, UPDATE, DELETE). The result of the
     * validation is returned as a TransformationValidationResult object.
     * 
     * @param transformation the SqlTransformation
     * @return the TransformationValidationResult object
     */
    public TransformationValidationResult validateTransformation() {
        // If this is a XML Document SOURCE model, return
        EmfResource emfResource = (EmfResource)this.mappingRoot.eResource();
        if (emfResource.getModelAnnotation() != null) {
            ModelType type = emfResource.getModelAnnotation().getModelType();
            String stringURI = emfResource.getModelAnnotation().getPrimaryMetamodelUri();
            if (type.equals(ModelType.LOGICAL_LITERAL) && XML_URI.equals(stringURI)) {
                return new TransformationValidationResult();
            }
        }

        // validate the SQLTransformation on the mapping root
        // get the UUID form of the SqlTransformation
        SqlTransformation sqlTrans = (SqlTransformation)this.mappingRoot.getHelper();

        // get the path to the virtual group
        String objectPath = TransformationHelper.getSqlEObjectPath(this.targetGroup);

        // If no sql transform exists ...
        if (sqlTrans == null) {
            String msg = TransformationPlugin.Util.getString("TransformationValidator.Error_in_the_Sql_tranformation_for_1", objectPath); //$NON-NLS-1$
            List statuses = new ArrayList(1);
            statuses.add(new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, msg, null));
            return new TransformationValidationResult(statuses);
        }

        // create a transformation result and update it with various command results
        TransformationValidationResult transformResult = new TransformationValidationResult();

        // validate the select sql on the mapping root
        SqlTransformationResult selectStatus = SqlMappingRootCache.getSqlTransformationStatus(this.mappingRoot,
                                                                                              QueryValidator.SELECT_TRNS,
                                                                                              restrictSearch,
                                                                                              this.validationContext);
        if (selectStatus != null) {
            transformResult.setSelectResult(selectStatus);
        } else {
            String msg = TransformationPlugin.Util.getString("TransformationValidator.Found_problems_validating_transformation_defining_{0},_re-validate_in_the_transformation_editor._1", objectPath); //$NON-NLS-1$
            List statuses = new ArrayList(1);
            statuses.add(new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, msg, null));
            return new TransformationValidationResult(statuses);
        }

        /* Defect 11415
         * Workspace query validation would try to resolve UUID versions of the queries initially.
         * We need to use UUID queries because they reflect the most current picture of the objects
         * in the modeler(Example: If an entity is renamed the user query on the MappingRoot would
         * point to the older entity name but the UUID of the entity would remain the same.), also
         * UUID queries get updated with changes in the worspace.
         *
         * Defect: 11627
         * Whenever a UUID query is not resolvable, we convert the UUID query to user form and then
         * try to validate it. There are cases where a UUID query would be not be resolvable but a
         * user form of it would be, example: In a query that has one of its groups defined by a sub query
         * If the user types in the query below in the TransforMationEditor and validate
         * Select a, b, c from (select a, b, c from table) as x
         * The element on the outer query get resolved to TempMetadataID and do not have UUIDs defined,
         * so the UUID version of the query that gets saved on the MappingRoot would look like
         * Select a, b, c from (select UUID, UUID, UUID from UUID) as x
         * This query would not be resolvable, since the TempGroup x has 3 elements whose names are UUID's
         * but do not have a,b,c defined on it, when this query is converted back to user version the query
         * would be resolvable again.
         *
         * We do the same thing when validating Insert, Update and Delete transformations.
         * This logic is in the SqlMappingRootCache
         */

        // -----------------------------------------------------------
        // SELECT is Valid - check the remaining SQL (if allowed)
        // Update, Insert and Delete transforms exist only for Tables.
        // The transforma need to be validated only if the target is updatable.
        // -----------------------------------------------------------
        if (transformResult.isValid()
            && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isUpdatableGroup(this.targetGroup)) {

            // -----------------------------------------------------------
            // Validate the INSERT String
            // -----------------------------------------------------------
            if (sqlTrans.isInsertAllowed()) {
                transformResult.setInsertResult(SqlMappingRootCache.getSqlTransformationStatus(mappingRoot,
                                                                                               QueryValidator.INSERT_TRNS,
                                                                                               restrictSearch,
                                                                                               this.validationContext));
                transformResult.setInsertAllowed(true);
            }
            // -----------------------------------------------------------
            // Validate the UPDATE String
            // -----------------------------------------------------------
            if (sqlTrans.isUpdateAllowed()) {
                transformResult.setUpdateResult(SqlMappingRootCache.getSqlTransformationStatus(mappingRoot,
                                                                                               QueryValidator.UPDATE_TRNS,
                                                                                               restrictSearch,
                                                                                               this.validationContext));
                transformResult.setUpdateAllowed(true);
            }
            // -----------------------------------------------------------
            // Validate the DELETE String
            // -----------------------------------------------------------
            if (sqlTrans.isDeleteAllowed()) {
                transformResult.setDeleteResult(SqlMappingRootCache.getSqlTransformationStatus(mappingRoot,
                                                                                               QueryValidator.DELETE_TRNS,
                                                                                               restrictSearch,
                                                                                               this.validationContext));
                transformResult.setDeleteAllowed(true);
            }
        }

        // Return results
        return transformResult;
    }

    public boolean isValidRoot() {
        return mappingRoot != null && mappingRoot.eResource() != null;
    }

    /**
     * This method attempts a complete validation on the supplied SQL string. The result of the validation is returned as a
     * CommandValidationResult object.
     * 
     * @param sql the SQL String
     * @return the CommandValidationResult object
     */
    public QueryValidationResult validateSql( final String sql,
                                              final int transformType,
                                              final boolean cacheResult ) {
        if (!isValidRoot()) {
            return null;
        }
        SqlTransformationResult commandValidationResult = null;
        
        switch( transformType ) {
	        case QueryValidator.INSERT_TRNS: {
	        	if( ((SqlTransformation)this.mappingRoot.getHelper()).isInsertSqlDefault()) {
	        		IStatus status = new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID, 0,  "\"Use Default\" option is turned on for Insert procedure.", null); //$NON-NLS-1$
	        		Command command = null;
	        		if( sql != null && sql.length() > 0 ) {
	        			SqlTransformationResult parsedResult = parseSQL(sql);
	        			command = parsedResult.getCommand();
	        		}
	        		commandValidationResult = new SqlTransformationResult(command, status);
	        		commandValidationResult.setSqlString(sql);
	        		return commandValidationResult;
	        	}
	        } break;
	        case QueryValidator.UPDATE_TRNS: {
	        	if( ((SqlTransformation)this.mappingRoot.getHelper()).isUpdateSqlDefault()) {
	        		IStatus status = new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID, 0,  "\"Use Default\" option is turned on for UPDATE procedure.", null); //$NON-NLS-1$
	        		Command command = null;
	        		if( sql != null && sql.length() > 0 ) {
	        			SqlTransformationResult parsedResult = parseSQL(sql);
	        			command = parsedResult.getCommand();
	        		}
	        		commandValidationResult = new SqlTransformationResult(command, status);
	        		commandValidationResult.setSqlString(sql);
	        		return commandValidationResult;
	        	}
	        } break;
	        case QueryValidator.DELETE_TRNS: {
	        	if( ((SqlTransformation)this.mappingRoot.getHelper()).isDeleteSqlDefault()) {
	        		IStatus status = new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID, 0, "\"Use Default\" option is turned on for DELETE procedure.", null); //$NON-NLS-1$
	        		Command command = null;
	        		if( sql != null && sql.length() > 0 ) {
	        			SqlTransformationResult parsedResult = parseSQL(sql);
	        			command = parsedResult.getCommand();
	        		}
	        		commandValidationResult = new SqlTransformationResult(command, status);
	        		commandValidationResult.setSqlString(sql);
	        		return commandValidationResult;
	        	}
	        } break;
	    }
        
        commandValidationResult = parseSQL(sql);
        

        
        
        if (commandValidationResult.isParsable() && transformType != UNKNOWN_TRNS) {
            Command command = commandValidationResult.getCommand();

            // resolve command
            commandValidationResult = resolveCommand(command, transformType);
            // aTODO commandValidationResult = resolveCommand(command, transformType, this.targetGroup);
            if (commandValidationResult.isResolvable()) {
                // validate command
                commandValidationResult = validateCommand(command, transformType);
                commandValidationResult.setResolvable(true);
            }
        }

        // set other info
        commandValidationResult.setSqlString(sql);
        commandValidationResult.setSourceGroups(this.mappingRoot.getInputs());

        // cache the result if needed
        if (cacheResult) {
            SqlMappingRootCache.setStatus(this.mappingRoot, transformType, commandValidationResult);
        }

        return commandValidationResult;
    }

    /**
     * This method attempts to parse the supplied SQL string. The result is returned as a SqlTransformationResult object.
     * 
     * @param sqlString the SQL to parse
     * @return the SqlTransformationResult object
     */
    public static SqlTransformationResult parseSQL( final String sqlString ) {
        Command command = null;
        IStatus status = null;
        if (sqlString == null || sqlString.trim().isEmpty()) {
            String msg = TransformationPlugin.Util.getString("TransformationValidator.emptySQLMessage"); //$NON-NLS-1$
            status = new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, msg, null);
        } else {
            try {
                // QueryParser is not thread-safe, get new parser each time
                QueryParser parser = new QueryParser();
                command = parser.parseDesignerCommand(sqlString);
            } catch (Exception e) {
                status = new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e);
            }
        }

        final SqlTransformationResult result = new SqlTransformationResult(command, status);
        if (status != null && !status.isOK()) {
            result.setParsable(false);
        }

        return result;
    }

    /**
     * This method attempts to resolve the supplied Command language object. The result is returned as a SqlTransformationResult
     * object.
     * 
     * @param command the Command languageObject
     * @param externalMetadata the externalMetadata required to resolve the command
     * @return the SqlTransformationResult object
     */
    public SqlTransformationResult resolveCommand( final Command command,
                                                   final int transformType ) {
        IStatus status = checkCommandType(command, transformType, this.targetGroup);
        if (status != null && status.getSeverity() != IStatus.OK) {
            SqlTransformationResult resolverResult = new SqlTransformationResult(command, status);
            resolverResult.setTargetValidStatus(status);
            return resolverResult;
        }

        Map externalMetadata = Collections.EMPTY_MAP;
        try {
            // look up external metadata
            externalMetadata = TransformationHelper.getExternalMetadataMap(command, mappingRoot, getQueryMetadata(), transformType);
        } catch (QueryMetadataException e) {
            IStatus errorStatus = new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e);
            return new SqlTransformationResult(command, errorStatus);
        } catch (TeiidComponentException e) {
            IStatus errorStatus = new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e);
            return new SqlTransformationResult(command, errorStatus);
        }
        // resolve the command
        return resolveCommand(command, externalMetadata);
    }

    /**
     * This method does a validation on the supplied Command language object. The result is returned as a SqlTransformationResult
     * object.
     * 
     * @param command the Command languageObject
     * @return the SqlTransformationResult object
     */
    public SqlTransformationResult validateCommand( final Command command, final int cmdType) {
        CoreArgCheck.isNotNull(command);
        Collection<IStatus> statusList = null;
        Collection<IStatus> updateStatusList = null;
        try {
            // Validate
            ValidatorReport report = Validator.validate(command, getQueryMetadata());
            // If Validation report has nothing, command is valid
            if (!report.hasItems()) {
                // If no report items - validation is successful
                // Check sources (target can't be a source)
                statusList = validateSources(command, statusList);
                // validate references
                statusList = validateReferences(command, statusList);

            } else {
                statusList = createStatusList(report);
            }
            // handle exception
        } catch (TeiidComponentException e) {
            // Add exception to the problems list
            statusList = new ArrayList<IStatus>(1);
            statusList.add(new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e));
        }

        boolean validateAndResolve = false;
        
        if (statusList == null || statusList.isEmpty()) {
        	statusList = Collections.EMPTY_LIST;
        	validateAndResolve = true;
        }
        
        // If Select SQL is OK, now we need to check if any of the update SQL are using "Default" and run
        // additional validation
        
        boolean allowsUpdates = TransformationHelper.tableSupportsUpdate(targetGroup);
        
        if( validateAndResolve && cmdType == QueryValidator.SELECT_TRNS && allowsUpdates) {
        	updateStatusList = new ArrayList();
        	UpdateType insertType = UpdateType.INSTEAD_OF;
        	UpdateType updateType = UpdateType.INSTEAD_OF;
        	UpdateType deleteType = UpdateType.INSTEAD_OF;
        	SqlTransformation transformation = (SqlTransformation)mappingRoot.getHelper();
        	boolean doUpdateValidation = false;
        	if( transformation.isInsertSqlDefault() ) {
        		insertType = UpdateType.INHERENT;
        		doUpdateValidation = true;
        	}
        	if( transformation.isUpdateSqlDefault() ) {
        		updateType = UpdateType.INHERENT;
        		doUpdateValidation = true;
        	} 
        	if( transformation.isDeleteSqlDefault() ) {
        		deleteType = UpdateType.INHERENT;
        		doUpdateValidation = true;
        	} 
        	if( doUpdateValidation ) {
	        	UpdateValidator updateValidator = new UpdateValidator(metadata, insertType, updateType, deleteType );
	        	List<ElementSymbol> elemSymbols = null;
	        	
	        	try {
	        		elemSymbols = getProjectedSymbols();
	        		if( !elemSymbols.isEmpty() ) {
	        			updateValidator.validate(command, elemSymbols);
	        		}
	        	} catch (TeiidComponentException e) {
	                // Add exception to the problems list
	        		updateStatusList = new ArrayList<IStatus>(1);
	                updateStatusList.add(new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e));
	            }
	
	            if (updateStatusList == null || updateStatusList.isEmpty()) {
	            	updateStatusList = new ArrayList<IStatus>();
	            	if( insertType == UpdateType.INHERENT) {
	            		updateStatusList.addAll(getReportStatusList(updateValidator.getInsertReport(), INSERT_SQL_PROBLEM));
	            	}
	            	if( updateType == UpdateType.INHERENT) {
	            		updateStatusList.addAll(getReportStatusList(updateValidator.getUpdateReport(), UPDATE_SQL_PROBLEM));
	            	}
	            	if( deleteType == UpdateType.INHERENT) {
	            		updateStatusList.addAll(getReportStatusList(updateValidator.getDeleteReport(), DELETE_SQL_PROBLEM));
	            	}
	            	
	            	updateStatusList.addAll(getReportStatusList(updateValidator.getReport(), ALL_UPDATE_SQL_PROBLEM));
	            }
        	}
        }

        SqlTransformationResult result = new SqlTransformationResult(command, statusList, updateStatusList);

        result.setValidatable(validateAndResolve);
        result.setResolvable(validateAndResolve);
        
        return result;
    }

    public QueryMetadataInterface getQueryMetadata() {
        return getDirectQueryMetadata();

    }

    /**
     * Return the {@link com.metamatrix.query.metadata.QueryMetadataInterface} instance to use for query validation and
     * resolution.
     */
    private QueryMetadataInterface getDirectQueryMetadata() {
        if (this.metadata == null && this.mappingRoot.eResource() != null) {
            TransformationMetadataFactory factory = TransformationMetadataFactory.getInstance();
            final boolean useServerMetadata = (this.validationContext != null && this.validationContext.useServerIndexes());

            // Validating within the modeler workspace
            if (!useServerMetadata) {
                // create a indexSelector for this resource and instantiate transformation validator
                ModelResourceIndexSelector selector = new ModelResourceIndexSelector(this.mappingRoot.eResource());
                QueryMetadataContext queryContext = new QueryMetadataContext(selector);
                queryContext.setRestrictedSearch(this.restrictSearch);
                Container container = null;
                if (this.validationContext != null) {
                    container = this.validationContext.getResourceContainer();
                    if (!this.validationContext.useIndexesToResolve()) {
                        queryContext.setIndexSelector(new NullIndexSelector());
                    }
                    queryContext.setResources(Arrays.asList(this.validationContext.getResourcesInScope()));
                } else {
                    try {
                        container = ModelerCore.getModelContainer();
                        // set the resource scope (all model resources in open model projects)
                        try {
                            ModelWorkspace workspace = ModelerCore.getModelWorkspace();
                            queryContext.setResources(Arrays.asList(workspace.getEmfResources()));
                        } catch (RuntimeException e) {
                            // If we are running in a non-workspace environement, just use the resource from the given
                            // mappingRoot's resource set
                            queryContext.setResources(this.mappingRoot.eResource().getResourceSet().getResources());
                        }
                    } catch (CoreException e) {
                        TransformationPlugin.Util.log(e);
                    }
                }
                // defect 16567 - we cannot use caching since it prevents the MetadataRecords
                // from being updated with new values based on model changes.
                // this.metadata = factory.createCachingModelerMetadata(this.mappingRoot, this.restrictSearch);
                this.metadata = factory.getModelerMetadata(queryContext, container);
                // Validating within the vdb(server) context
            } else {
                CoreArgCheck.isNotNull(this.validationContext);
                IndexSelector selector = null;
                if (this.validationContext.useIndexesToResolve()) {
                    if (this.validationContext.getData(QUERY_METADATA_INTERFACE) != null) {
                        this.metadata = (QueryMetadataInterface)this.validationContext.getData(QUERY_METADATA_INTERFACE);
                        // make sure the type is what we think it should be.
                        if (!(this.metadata instanceof VdbMetadata)
                            && ((this.metadata instanceof TransformationMetadataFacade) && !(((TransformationMetadataFacade)this.metadata).getDelegate() instanceof VdbMetadata))) {
                            throw new TeiidRuntimeException(
                                                            TransformationPlugin.Util.getString("TransformationValidator.QMI_of_unexpected_type")); //$NON-NLS-1$
                        }
                    } else {
                        // The TargetLocationIndexSelector will gather all index files under a specified directory location.
                        selector = new TargetLocationIndexSelector(this.validationContext.getIndexLocation());
                    }
                } else {
                    selector = new NullIndexSelector();
                }

                if (this.metadata == null) {
                    QueryMetadataContext queryContext = new QueryMetadataContext(selector);
                    queryContext.setResources(Arrays.asList(this.validationContext.getResourcesInScope()));
                    queryContext.setRestrictedSearch(this.restrictSearch);
                    this.metadata = factory.getVdbMetadata(queryContext, this.validationContext.getResourceContainer());
                    this.validationContext.setData(QUERY_METADATA_INTERFACE, this.metadata);
                }
            }
        }
        return this.metadata;
    }

    /**
     * Check if the command could be used to define the given target in the transformation.
     * 
     * @param command the Command languageObject
     * @param transformType The type of transformation.
     * @param targetGroup The traget virtual group or procedure.
     * @return The status indicating if the command is valid.
     */
    public static IStatus checkCommandType( final Command command,
                                            final int transformType,
                                            final Object targetGroup ) {
        CoreArgCheck.isNotNull(command);
        CoreArgCheck.isNotNull(targetGroup);

        int cmdType = command.getType();
        switch (transformType) {
            case QueryValidator.SELECT_TRNS:
                if (targetGroup instanceof Table) {
                    if (cmdType != Command.TYPE_QUERY && cmdType != Command.TYPE_STORED_PROCEDURE) {
                        // create validation problem and addition to the results
                        String msg = TransformationPlugin.Util.getString("TransformationValidator.Query_defining_a_virtual_group_can_only_be_of_type_Select_or_Exec._1"); //$NON-NLS-1$
                        return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, msg, null);
                    }
                } else if (targetGroup instanceof Procedure) {
                    if (!(cmdType == Command.TYPE_UPDATE_PROCEDURE && !((CreateUpdateProcedureCommand)command).isUpdateProcedure())) {
                        // create validation problem and addition to the results
                        String msg = TransformationPlugin.Util.getString("TransformationValidator.Query_defining_a_virtual_procedure_can_only_be_of_type_Virtual_procedure._2"); //$NON-NLS-1$
                        return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, msg, null);
                    }
                } else if (targetGroup instanceof Operation) {
                    if (cmdType != Command.TYPE_UPDATE_PROCEDURE) {
                        // create validation problem and addition to the results
                        String msg = TransformationPlugin.Util.getString("TransformationValidator.Query_defining_an_operation_can_only_be_of_type_Virtual_procedure._1"); //$NON-NLS-1$
                        return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, msg, null);
                    }
                }
                break;
            case QueryValidator.INSERT_TRNS:
            case QueryValidator.UPDATE_TRNS:
            case QueryValidator.DELETE_TRNS:
                if (cmdType != Command.TYPE_TRIGGER_ACTION && !(cmdType == Command.TYPE_UPDATE_PROCEDURE && ((CreateUpdateProcedureCommand)command).isUpdateProcedure())) {
                    // create validation problem and addition to the results
                    String msg = TransformationPlugin.Util.getString("TransformationValidator.Only_update_procedures_are_allowed_in_the_INSERT/UPDATE/DELETE_tabs._1"); //$NON-NLS-1$
                    return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, msg, null);
                }
                break;
            default:
                // create validation problem transformation type is illegal
                String msg = TransformationPlugin.Util.getString("TransformationValidator.Invalid_transformation_type,_only_allowed_transformations_are_select,_insert,_update,_delete_transforms._1"); //$NON-NLS-1$
                return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, msg, null);
        }
        return null;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Private method for creating a List of Status objects from a ValidatorReport
     * 
     * @param report the ValidatorReport
     * @return the List of Status
     */
    private List<IStatus> createStatusList( final ValidatorReport report ) {
        if (report != null && report.hasItems()) {
        	Collection<ValidatorFailure> items = report.getItems();
            List<IStatus> statusList = new ArrayList<IStatus>(items.size());
            for (ValidatorFailure item : items) {
            	IStatus status = new Status(
            	item.getStatus() == org.teiid.query.validator.ValidatorFailure.Status.ERROR ? IStatus.ERROR : IStatus.WARNING,
            			TransformationPlugin.PLUGIN_ID, 0, item.toString(), null);
                statusList.add(status);
            }
            return statusList;
        }
        return Collections.emptyList();
    }

    protected SqlTransformationResult resolveCommand( final Command command,
                                                      final Map externalMetadata ) {
        IStatus status = null;

        CoreArgCheck.isNotNull(command);
        String commandSQL = command.toString();
        // ------------------------------------------------------------
        // Resolve the Command
        // ------------------------------------------------------------
        try {
            // Attempt to resolve the command
            final QueryMetadataInterface metadata = getQueryMetadata();
            QueryResolver.buildExternalGroups(externalMetadata, command);
            QueryResolver.resolveCommand(command, metadata, true);
            // If unsuccessful, an exception is thrown

            if (command instanceof CreateUpdateProcedureCommand
            		&& ((CreateUpdateProcedureCommand)command).getResultsCommand() instanceof DynamicCommand
            		&& !((DynamicCommand)((CreateUpdateProcedureCommand)command).getResultsCommand()).isAsClauseSet()) {
            	List<ElementSymbol> projectedSymbols = getProjectedSymbols();
            	((CreateUpdateProcedureCommand)command).setProjectedSymbols(projectedSymbols);
            }
        } catch (TeiidComponentException e) {
            // create status
            status = new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        } catch (QueryResolverException e) {
            // create status
            status = new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }

        if (status != null && status.getSeverity() == IStatus.ERROR) {
            return new SqlTransformationResult(parseSQL(commandSQL).getCommand(), status);
        }

        SqlTransformationResult resolverResult = new SqlTransformationResult(command, status);
        // set the external metadata on the resolverResult
        resolverResult.setExternalMetadataMap(externalMetadata);
        // set resolvable
        if (status == null) {
            resolverResult.setResolvable(true);
        }

        return resolverResult;
    }

    /**
     * Get the output columns for the target of the given transformation mapping root.
     * 
     * @param transRoot The mappingroot objects wholse targets columns are returned
     * @return The list of columns on the target.
     * @since 4.3
     */
    public static List getOutputColumns( final SqlTransformationMappingRoot transRoot ) {
        EObject target = transRoot.getTarget();
        List outputColumns = null;
        SqlAspect sqlAspect = AspectManager.getSqlAspect(target);
        if (sqlAspect instanceof SqlTableAspect) {
            outputColumns = ((SqlTableAspect)sqlAspect).getColumns(target);
        } else if (sqlAspect instanceof SqlProcedureAspect) {
            SqlProcedureAspect procAspect = (SqlProcedureAspect)sqlAspect;
            EObject resultSet = (EObject)procAspect.getResult(target);
            if (resultSet != null) {
                SqlColumnSetAspect resultAspect = (SqlColumnSetAspect)AspectManager.getSqlAspect(resultSet);
                outputColumns = resultAspect.getColumns(resultSet);
            }
        }
        return outputColumns;
    }
    
    private List<ElementSymbol> getProjectedSymbols() throws QueryMetadataException, TeiidComponentException {
        List<EObject> outputColumns = getOutputColumns(this.mappingRoot);
        List<ElementSymbol> projectedSymbols = new ArrayList<ElementSymbol>(outputColumns.size());
        for (EObject outputColumn : outputColumns) {
            String outputColumnName = TransformationHelper.getSqlColumnName(outputColumn);
            SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(outputColumn);
            EObject datatype = columnAspect.getDatatype(outputColumn);
            SqlDatatypeAspect typeAspect = datatype != null ? (SqlDatatypeAspect)AspectManager.getSqlAspect(datatype) : null;
            Class<?> targetType = null;
            if (typeAspect != null) {
                targetType = DataTypeManager.getDataTypeClass(typeAspect.getRuntimeTypeName(datatype));
            }
            ElementSymbol column = new ElementSymbol(outputColumnName);
            column.setType(targetType);
            column.setMetadataID(getQueryMetadata().getElementID(columnAspect.getFullName(outputColumn)));
            projectedSymbols.add(column);
        }
        
        return projectedSymbols;
    }
    
    private List<IStatus> getReportStatusList(ValidatorReport report, int errorCode) {
    	Collection<ValidatorFailure> items = report.getItems();
    	
    	List<IStatus> statusList = new ArrayList<IStatus>(items.size());
    	for( ValidatorFailure item : items ) {
    		int statusID = IStatus.WARNING;
    		if( item.getStatus() == ValidatorFailure.Status.ERROR ) {
    			statusID = IStatus.ERROR; 
    		}
    		IStatus status = new Status(statusID, TransformationPlugin.PLUGIN_ID, errorCode, item.toString(), null);
    		statusList.add(status);
    	}
    	
    	return statusList;
    }

    /**
     * Check if the command has any references or ?. If so error out.
     * 
     * @param command the Command languageObject
     * @param statuslist to which an error can be added
     * @return statuslist The updated status list
     */
    private Collection<IStatus> validateReferences( final Command command,
                                                    Collection<IStatus> statusList ) {
        Collection references = ReferenceCollectorVisitor.getReferences(command);
        if (!references.isEmpty()) {
            statusList = statusList != null ? statusList : new ArrayList<IStatus>(1);
            IStatus status = new Status(
                                        IStatus.ERROR,
                                        TransformationPlugin.PLUGIN_ID,
                                        0,
                                        TransformationPlugin.Util.getString("TransformationValidator.The_transformation_contains_Reference_Symbols_(_),_which_is_not_allowed,_replace___with_a_constant,_element,_or_parameter_name._1"), null); //$NON-NLS-1$
            statusList.add(status);
        }

        return statusList;
    }

    /**
     * Check if the command has any references or ?. If so error out.
     * 
     * @param command the Command languageObject
     * @param statuslist to which an error can be added
     * @return statuslist The updated status list
     */
    private Collection<IStatus> validateSources( final Command command,
                                                 Collection<IStatus> statusList ) {
        if (isTargetASourceInCommand(command)) {
            statusList = statusList != null ? statusList : new ArrayList<IStatus>(1);
            String message = TransformationPlugin.Util.getString("TransformationValidator.errorTargetIsSourceMsg", ModelerCore.getModelEditor().getName(targetGroup)); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 0, message, null);
            statusList.add(status);
        }

        return statusList;
    }

    protected boolean isTargetASourceInCommand( Command command ) {
        boolean result = false;
        Collection sourceSymbols = TransformationSqlHelper.getGroupSymbols(command);
        List sourceEObjects = TransformationSqlHelper.getGroupSymbolEObjects(sourceSymbols);
        if (!sourceEObjects.isEmpty() && mappingRoot != null) {
            EObject tRootTarget = mappingRoot.getTarget();
            Iterator iter = sourceEObjects.iterator();
            Object nextObj = null;
            while (iter.hasNext() && !result) {
                nextObj = iter.next();
                if (nextObj.equals(tRootTarget)) result = true;
            }
        }

        return result;
    }

	@Override
	public EObject getTransformationRoot() {
		// TODO Auto-generated method stub
		return this.mappingRoot;
	}
    
}
