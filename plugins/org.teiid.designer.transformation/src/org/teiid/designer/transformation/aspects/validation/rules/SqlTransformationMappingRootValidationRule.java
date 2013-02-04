/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.validation.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingRoot;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ValidationPreferences;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metadata.runtime.TableRecord;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.function.ScalarFunction;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.transformation.SqlTransformation;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TreeMappingRoot;
import org.teiid.designer.metamodels.transformation.impl.MappingClassImpl;
import org.teiid.designer.metamodels.transformation.impl.MappingClassSetImpl;
import org.teiid.designer.metamodels.webservice.Operation;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.metamodels.xml.XmlRoot;
import org.teiid.designer.metamodels.xml.impl.XmlDocumentImpl;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.ICommandCollectorVisitor;
import org.teiid.designer.query.sql.IElementCollectorVisitor;
import org.teiid.designer.query.sql.IFunctionCollectorVisitor;
import org.teiid.designer.query.sql.IGroupCollectorVisitor;
import org.teiid.designer.query.sql.IPredicateCollectorVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.metadata.TransformationMetadata;
import org.teiid.designer.transformation.util.AttributeMappingHelper;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.validation.SqlTransformationResult;
import org.teiid.designer.transformation.validation.TransformationValidationResult;
import org.teiid.designer.transformation.validation.TransformationValidator;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.udf.IFunctionLibrary.FunctionName;
import org.teiid.designer.udf.UdfManager;


/**
 * SqlTransformationMappingRootValidationRule
 *
 * @since 8.0
 */
public class SqlTransformationMappingRootValidationRule implements ObjectValidationRule {

    private final IGroupCollectorVisitor groupCollectorVisitor;
    
    /**
     * 
     */
    public SqlTransformationMappingRootValidationRule() {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        groupCollectorVisitor = queryService.getGroupCollectorVisitor(true);
    }
    
    
    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate( final EObject eObject,
                          final ValidationContext context ) {
        CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, eObject);
        SqlTransformationMappingRoot transRoot = (SqlTransformationMappingRoot)eObject;

        // create a validation result for the virtual group
        ValidationResult validationResult = new ValidationResultImpl(transRoot, transRoot.getTarget());

        // validate the mapping root, to see if it has one output and at least one input
        validateMapping(transRoot, validationResult);

        // validate sources nd targets on the mapping root
        validateSourcesAndTargets(transRoot, validationResult);

        // validate the mapping inputs checking for circular dependencies
        validMappingInputDependencies(transRoot, validationResult);

        // check the updatability of the virtual group in relation to its physical couterparts
        // also check updateability in relation to update/insert/delete transform definition
        validateUpdatability(transRoot, validationResult);

        // valid sqltransformation in the mapping root
        final TransformationValidator validator = new TransformationValidator(transRoot, context, true, true);
        validateSqlTransformation(transRoot, validationResult, validator, context);

        // if target is web service (Operation), validate input document's root element does not have a mapping class
        validateInputDocumentForWebService(transRoot, validationResult);

        // add the result to the context
        context.addResult(validationResult);
    }

    private void validateInputDocumentForWebService( final SqlTransformationMappingRoot transRoot,
                                                     final ValidationResult validationResult ) {
        if (validationResult.isFatalObject(transRoot)) {
            return;
        }

        EObject target = transRoot.getTarget();
        if (!(target instanceof Operation)) {
            return;
        }

        List sources = transRoot.getInputs();
        for (final Iterator iter = sources.iterator(); iter.hasNext();) {
            EObject source = (EObject)iter.next();
            if (source instanceof XmlDocument) {
                XmlDocument xmlDoc = (XmlDocument)source;
                XmlRoot rootElement = xmlDoc.getRoot();

                ModelContents mdlContents = ((EmfResource)source.eResource()).getModelContents();

                Iterator contentIter = mdlContents.getTransformations(source).iterator();
                if (!contentIter.hasNext()) {
                    return;
                }

                // get the mapping root associated with the transformation
                while (contentIter.hasNext()) {
                    MappingRoot mappingRoot = (MappingRoot)contentIter.next();
                    if (mappingRoot != null && mappingRoot instanceof TreeMappingRoot) {
                        Iterator outputRootElementsIter = mappingRoot.getOutputs().iterator();
                        while (outputRootElementsIter.hasNext()) {
                            if (rootElement.equals(outputRootElementsIter.next())) {
                                ValidationProblem problem = new ValidationProblemImpl(
                                                                                      0,
                                                                                      IStatus.WARNING,
                                                                                      TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.xml_doc_mapped_at_root_for_web_service")); //$NON-NLS-1$
                                validationResult.addProblem(problem);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if the target of a mapping root can accept the sources on the sql transformation and also if the sources on the
     * transformation can be added to for the given target.
     * @param transRoot the mapping root (cannot be <code>null</code>)
     * @param validationResult the result to add any validation errors to (cannot be <code>null</code>)
     * 
     * @since 4.3
     */
    public void validateSourcesAndTargets( final SqlTransformationMappingRoot transRoot,
                                           final ValidationResult validationResult ) {
        EObject target = transRoot.getTarget();
        CoreArgCheck.isNotNull(target);
        final Container container = ModelerCore.getContainer(transRoot);
        if (container == null) {
            return;
        }
        // if the target is a proxy try to resolve
        if (target.eIsProxy()) {
            target = EcoreUtil.resolve(target, container);
            // still does not resolve, just return there will be other
            // validation errors
            if (target.eIsProxy()) {
                return;
            }
        }
        SqlAspect targetSqlAspect = org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(target);
        if (targetSqlAspect == null
            || !(targetSqlAspect instanceof SqlTableAspect || targetSqlAspect instanceof SqlProcedureAspect)) {
            ValidationProblem typeProblem = new ValidationProblemImpl(
                                                                      0,
                                                                      IStatus.ERROR,
                                                                      TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.no_valid_target")); //$NON-NLS-1$
            validationResult.addProblem(typeProblem);
            return;
        }
        List sources = transRoot.getInputs();
        for (final Iterator iter = sources.iterator(); iter.hasNext();) {
            EObject source = (EObject)iter.next();
            // if the target is a proxy try to resolve
            if (source.eIsProxy()) {
                source = EcoreUtil.resolve(source, container);
                // still does not resolve, just return there will be other
                // validation errors
                if (source.eIsProxy()) {
                    return;
                }
            }
            SqlAspect sourceSqlAspect = org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(source);
            
            boolean isFunction = source instanceof ScalarFunction;

            if( !isFunction && (sourceSqlAspect == null || 
            		!(sourceSqlAspect instanceof SqlTableAspect || sourceSqlAspect instanceof SqlProcedureAspect)) ) {
                ValidationProblem typeProblem = new ValidationProblemImpl(
                                                                          0,
                                                                          IStatus.ERROR,
                                                                          TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.no_valid_source")); //$NON-NLS-1$
                validationResult.addProblem(typeProblem);
                return;
            }
            
            if( !isFunction ) {
            	boolean isValidSource = true;
	            if (targetSqlAspect instanceof SqlTableAspect) {
	                isValidSource = ((SqlTableAspect)targetSqlAspect).canAcceptTransformationSource(target, source);
	            } else if (targetSqlAspect instanceof SqlProcedureAspect) {
	                isValidSource = ((SqlProcedureAspect)targetSqlAspect).canAcceptTransformationSource(target, source);
	            }
	            if (!isValidSource) {
	                String targetName = targetSqlAspect.getName(target);
	                String sourceName = sourceSqlAspect.getName(source);
	                ValidationProblem typeProblem = new ValidationProblemImpl(
	                                                                          0,
	                                                                          IStatus.ERROR,
	                                                                          TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.invalid_source_for_target", sourceName, targetName)); //$NON-NLS-1$
	                validationResult.addProblem(typeProblem);
	                return;
	            }

	            boolean isValidTarget = true;
	            if (sourceSqlAspect instanceof SqlTableAspect) {
	                isValidTarget = ((SqlTableAspect)sourceSqlAspect).canBeTransformationSource(source, target);
	            } else if (sourceSqlAspect instanceof SqlProcedureAspect) {
	                isValidTarget = ((SqlProcedureAspect)sourceSqlAspect).canBeTransformationSource(source, target);
	            }
	            if (!isValidTarget) {
	                String targetName = targetSqlAspect.getName(target);
	                String sourceName = sourceSqlAspect.getName(source);
	                ValidationProblem typeProblem = new ValidationProblemImpl(
	                                                                          0,
	                                                                          IStatus.ERROR,
	                                                                          TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.invalid_target_for_source", targetName, sourceName)); //$NON-NLS-1$
	                validationResult.addProblem(typeProblem);
	                return;
	            }
            }
        }
    }

    /**
     * Check the inputs to the transform for circular dependencies to the tranformation target 1) ERROR -> If an input, in the
     * chain of tranformation inputs, is the tranformation target
     */
    private void validMappingInputDependencies( final SqlTransformationMappingRoot transRoot,
                                                final ValidationResult validationResult ) {
        if (validationResult.isFatalObject(transRoot)) {
            return;
        }
        final Collection inputs = this.getMappingInputs(transRoot, true);
        final EObject target = transRoot.getTarget();
        for (Iterator iter = inputs.iterator(); iter.hasNext();) {
            EObject source = (EObject)iter.next();
            if (source != null && source == target) {
                Object[] params = new Object[] {ModelerCore.getModelEditor().getModelRelativePathIncludingModel(source)};
                String msg = TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.A_circular_dependency_exists_between_this_tranformation_and_the_source_group_0_1", params); //$NON-NLS-1$
                ValidationProblem typeProblem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                validationResult.addProblem(typeProblem);
            }
        }
    }

    /**
     * Check the number of source groups and the number of target groups to the transformation mapping root. 1) ERROR -> If the
     * transformation has more or less than one target.
     */
    private void validateMapping( final SqlTransformationMappingRoot transRoot,
                                  final ValidationResult validationResult ) {
        if (validationResult.isFatalObject(transRoot)) {
            return;
        }
        Collection outputs = transRoot.getOutputs();
        if (outputs.size() < 1) {
            String modelName = ModelerCore.getModelEditor().getModelName(transRoot).toString();
            ValidationProblem problem = new ValidationProblemImpl(
                                                                  0,
                                                                  IStatus.ERROR,
                                                                  TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.Sql_transformation_in_the_model_{0},_has_no_target_tables/groups._1", modelName)); //$NON-NLS-1$
            validationResult.addProblem(problem);
        } else if (outputs.size() > 1) {
            String modelName = ModelerCore.getModelEditor().getModelName(transRoot).toString();
            ValidationProblem problem = new ValidationProblemImpl(
                                                                  0,
                                                                  IStatus.ERROR,
                                                                  TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.Sql_transformation_in_the_model_{0},_cannot_not_have_multiple_target_tables/groups._2", modelName)); //$NON-NLS-1$
            validationResult.addProblem(problem);
        } else {
            EObject output = (EObject)outputs.iterator().next();
            for (final Iterator inputIter = transRoot.getInputs().iterator(); inputIter.hasNext();) {
                if (output == inputIter.next()) {
                    String msg = TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_virtual_group_{0}_cannot_be_involved_as_an_input_to_the_transformation_defining_it._1", TransformationHelper.getSqlEObjectName(output)); //$NON-NLS-1$
                    ValidationProblem failureProblem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                    validationResult.addProblem(failureProblem);
                }
            }
        }
    }

    /**
     * Validate parameters, resultSet and the sql that defines a update procedure. 1) Error -> INSERT procedure is trying to
     * insert against the same virtual group 2) Error -> UPDATE procedure is trying to update against the same virtual group 3)
     * Error -> DELETE procedure is trying to delete against the same virtual group 4) Warn -> If INSERT procedure does not
     * contain a single INSERT ICommand. 5) Warn -> If HAS/TRANSLATE criteria constructs are used in INSERT procedures. 6) Warn ->
     * If UPDATE procedure does not contain a single UPDATE ICommand. 7) Warn -> If DELETE procedure does not contain a single
     * DELETE ICommand.
     */
    private void validateUpdateProcedures( final TransformationValidationResult transformResult,
                                           final SqlTransformationMappingRoot transRoot,
                                           final ValidationResult validationResult ) {
        if (validationResult.isFatalObject(transRoot)) {
            return;
        }
        EObject target = transRoot.getTarget();
        if (!(target instanceof Table) || !((Table)target).isSupportsUpdate()) {
            return;
        }

        // Adding changes to check if "User Default" is checked for each update procedure
        // IF NOT, then we don't want to validate this SQL and create Problems.
        SqlTransformation transform = (SqlTransformation)transRoot.getHelper();
        
        if (!transform.isInsertSqlDefault() && transformResult.hasInsertResult()) {
            // validate insert procedure
            ICommand insertCommand = transformResult.getInsertResult().getCommand();
            if (insertCommand != null) {
                // check if any of the commands are of type insert
                validateSubCommands(insertCommand, ICommand.TYPE_INSERT, transRoot, validationResult);
            }
        }

        if (!transform.isUpdateSqlDefault() && transformResult.hasUpdateResult()) {
            // validate update procedure
            ICommand updateCommand = transformResult.getUpdateResult().getCommand();
            if (updateCommand != null) {
                // check if any of the commands are of type update
                validateSubCommands(updateCommand, ICommand.TYPE_UPDATE, transRoot, validationResult);
            }
        }

        if (!transform.isDeleteSqlDefault() && transformResult.hasDeleteResult()) {
            // validate delete procedure
            ICommand deleteCommand = transformResult.getDeleteResult().getCommand();
            if (deleteCommand != null) {
                // check if any of the commands are of type delete
                validateSubCommands(deleteCommand, ICommand.TYPE_DELETE, transRoot, validationResult);
            }
        }
    }

    /**
     * Validate the subcommands.
     * 
     * @param superCmd The super command to validate
     * @param subCmdType Pass the subcommand type to check something in the subcommand in the context of the super ICommand.
     * @param transRoot The mapping root of the transformation
     */
    private void validateSubCommands( final ICommand command,
                                      final int subCmdType,
                                      final SqlTransformationMappingRoot transRoot,
                                      final ValidationResult validationResult ) {
        ValidationProblem typeProblem = null;

        // flag sets to true, if desired type of cmd is found for
        // the super command
        boolean foundDesiredSubCmd = false;
        // get all the sub commands and iterate through them
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        ICommandCollectorVisitor commandCollectorVisitor = queryService.getCommandCollectorVisitor();
        final Collection commands = commandCollectorVisitor.findCommands(command);
        // get the target for the transformation
        EObject target = transRoot.getTarget();
        for (final Iterator cmdIter = commands.iterator(); cmdIter.hasNext();) {
            ICommand subCommand = (ICommand)cmdIter.next();
            int currentCmdType = subCommand.getType();
            // if subcommand type is samme as that expected for super command
            if (currentCmdType == subCmdType) {
                foundDesiredSubCmd = true;
            }
            switch (currentCmdType) {
                case ICommand.TYPE_QUERY:
                    if (subCommand instanceof IQuery) {
                        validateQuery((IQuery)subCommand, transRoot, validationResult);
                    }
                    break;
                case ICommand.TYPE_INSERT:
                    if (containsTarget(subCommand, target)) {
                        // create validation problem and addition to the results
                        typeProblem = new ValidationProblemImpl(
                                                                0,
                                                                IStatus.ERROR,
                                                                TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_insert_procedure_for_the_virtualGroup_{0},_is_trying_to_execute_an_insert_against_itself._1", TransformationHelper.getSqlEObjectName(target))); //$NON-NLS-1$
                    }
                    break;
                case ICommand.TYPE_UPDATE:
                    if (containsTarget(subCommand, target)) {
                        // create validation problem and addition to the results
                        typeProblem = new ValidationProblemImpl(
                                                                0,
                                                                IStatus.ERROR,
                                                                TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_update_procedure_for_the_virtualGroup_{0},_is_trying_to_execute_an_update_against_itself._2", TransformationHelper.getSqlEObjectName(target))); //$NON-NLS-1$    				    
                    }
                    break;
                case ICommand.TYPE_DELETE:
                    if (containsTarget(subCommand, target)) {
                        // create validation problem and addition to the results
                        typeProblem = new ValidationProblemImpl(
                                                                0,
                                                                IStatus.ERROR,
                                                                TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_delete_procedure_for_the_virtualGroup_{0},_is_trying_to_execute_an_delete_against_itself._3", TransformationHelper.getSqlEObjectName(target))); //$NON-NLS-1$    				    
                    }
                    break;
                default:
                    break;
            }
            // create validation problem and addition to the results
            validationResult.addProblem(typeProblem);

            // abort we found an error
            if (typeProblem != null && typeProblem.getSeverity() == IStatus.ERROR) {
                return;
            }
            // for each of the subcommand, validate its subcommands
            validateSubCommands(subCommand, ICommand.TYPE_UNKNOWN, transRoot, validationResult);
        }

        // if we do care abpout the subcommand type for the super command
        // or desired subcommand is not found for the super command
        if (subCmdType != ICommand.TYPE_UNKNOWN && !foundDesiredSubCmd) {
            switch (subCmdType) {
                case ICommand.TYPE_INSERT:
                    // create validation problem and addition to the results
                    typeProblem = new ValidationProblemImpl(
                                                            0,
                                                            IStatus.WARNING,
                                                            TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_insert_procedure_for_the_virtualGroup_{0}_does____not_execute_an_insert._1", TransformationHelper.getSqlEObjectName(target))); //$NON-NLS-1$
                    break;
                case ICommand.TYPE_UPDATE:
                    // create validation problem and addition to the results
                    typeProblem = new ValidationProblemImpl(
                                                            0,
                                                            IStatus.WARNING,
                                                            TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_update_procedure_for_the_virtualGroup_{0}_does____not_execute_an_update._2", TransformationHelper.getSqlEObjectName(target))); //$NON-NLS-1$
                    break;
                case ICommand.TYPE_DELETE:
                    // create validation problem and addition to the results
                    typeProblem = new ValidationProblemImpl(
                                                            0,
                                                            IStatus.WARNING,
                                                            TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_delete_procedure_for_the_virtualGroup_{0}_does____not_execute_an_delete._3", TransformationHelper.getSqlEObjectName(target))); //$NON-NLS-1$
                    break;
                default:
                    break;

            }
            validationResult.addProblem(typeProblem);
        }
    }

    /**
     * Check if any of the target group represented by the EObject is used in the given ICommand.
     */
    private boolean containsTarget( final ICommand command,
                                    final EObject target ) {
        String targetName = TransformationHelper.getSqlEObjectFullName(target);
        String targetUUID = TransformationHelper.getSqlEObjectUUID(target);
        for (final Iterator grpIter = groupCollectorVisitor.findGroups(command).iterator(); grpIter.hasNext();) {
            IGroupSymbol group = (IGroupSymbol)grpIter.next();
            if (group.getName().equalsIgnoreCase(targetUUID) || group.getName().equalsIgnoreCase(targetName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Validate parameters, resultSet and the sql that defines a virtual procedure. 1) Error -> If Procedure does not return a
     * Result. 2) Error -> If the procedure defines parameters other than IN parameters. 3) Warn -> If the procedure's parameters
     * are not used in the sql defining the procedure 4) Error -> If the command defining a procedure is an update procedure. 5)
     * Error - > If the command defining proc is an update, the proc should have a result with one column of type int.
     */
    private void validateVirtualProcedures( final ICommand command,
                                            final SqlTransformationMappingRoot transRoot,
                                            final ValidationResult validationResult ) {
        if (validationResult.isFatalObject(transRoot)) {
            return;
        }
        EObject target = transRoot.getTarget();
        if (!(target instanceof Procedure)) {
            return;
        }

        // target of the mapping root is a procedure
        Procedure procTrgt = (Procedure)target;
        // command type
        int cmdType = command.getType();
        // get the resultSet on the procedure
        ProcedureResult procResult = procTrgt.getResult();
        if (procResult == null && cmdType != ICommand.TYPE_UPDATE_PROCEDURE) {
            // create validation problem and additional to the results
            ValidationProblem typeProblem = new ValidationProblemImpl(
                                                                      0,
                                                                      IStatus.ERROR,
                                                                      TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.Virtual_stored_procedures_should_always_return_a_resultSet._1")); //$NON-NLS-1$
            validationResult.addProblem(typeProblem);
            return;
        }

        // Check whether any of the parameters are used in the SQL transformation ...
        // 1. Accumulate the short names of the elements in the SQL ...
        // final boolean useDeepIteration = true; // fix for defect 10879
        final Collection elements = getElementsIncludeParameters(command);
        final Collection upperSymbolNames = new HashSet(elements.size());
        for (final Iterator elmntIter = elements.iterator(); elmntIter.hasNext();) {
            final IElementSymbol symbol = (IElementSymbol)elmntIter.next();
            final String symbolUpperName = AttributeMappingHelper.getSymbolFullName(symbol).toUpperCase();
            upperSymbolNames.add(symbolUpperName);
        }

        // 2. Iterate over the parameters and verify they are used ...
        // iterate over the parameters
        Collection parameters = procTrgt.getParameters();
        // set to true if there are no parameters
        boolean paramUsed = parameters.isEmpty();
        for (final Iterator paramIter = parameters.iterator(); paramIter.hasNext();) {
            ProcedureParameter param = (ProcedureParameter)paramIter.next();

            // check if any of the parameters are used in the SQL transform defining the
            // procedure
            // Check the parameter name; only need to do this if no parameters have been used
            if (!paramUsed) {
                // Get the SQL aspect for the parameter ...
                final SqlAspect sqlAspect = AspectManager.getSqlAspect(param);
                if (sqlAspect != null) {
                    final String paramName = sqlAspect.getFullName(param);
                    if (paramName != null) {
                        final String paramUpperName = paramName.toUpperCase();
                        if (upperSymbolNames.contains(paramUpperName)) {
                            paramUsed = true;
                        }
                    }
                }
            }
        }

        if (!paramUsed) {
            // create validation problem and additional to the results
            ValidationProblem typeProblem = new ValidationProblemImpl(
                                                                      0,
                                                                      IStatus.WARNING,
                                                                      TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.Sql_Transform_defining_the_virtual_procedure_{0}_does_not_use_any_of_the_parameters_defined_on_the_procedure._1", procTrgt.getName())); //$NON-NLS-1$
            validationResult.addProblem(typeProblem);
        }

        // If it is update command then result should have one column of type 'int'
        if (cmdType == ICommand.TYPE_INSERT || cmdType == ICommand.TYPE_UPDATE || cmdType == ICommand.TYPE_DELETE) {
            Collection columns = procResult.getColumns();
            String typeName = null;
            if (columns.size() == 1) {
                Column column = (Column)columns.iterator().next();
                EObject columnType = column.getType();
                if (columnType == null) {
                    // there is already a validation error
                    return;
                }
                final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column, true);
                typeName = dtMgr.getName(columnType);
            }

            if (typeName == null || !typeName.equals(DatatypeConstants.BuiltInNames.INT)) {
                // create validation problem and additional to the results
                ValidationProblem typeProblem = new ValidationProblemImpl(
                                                                          0,
                                                                          IStatus.ERROR,
                                                                          TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.Virtual_stored_procedures_defined_by_an_Insert,_Update_or_Delete_statement_must_define_return_a_resultSet_with_one_column_of_type_int._4")); //$NON-NLS-1$
                validationResult.addProblem(typeProblem);
            }
        }
    }

    private Collection getElementsIncludeParameters( ILanguageObject obj ) {
        if (obj == null) {
            return Collections.EMPTY_LIST;
        }
        
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IElementCollectorVisitor elementCollectorVisitor = queryService.getElementCollectorVisitor(true);
        return elementCollectorVisitor.findElements(obj, true);
    }

    /**
     * Check if the virtual group is updatable, atleast one of its source groups that define the virtual group is updatable. 1)
     * ERROR - > If the virtual group is updatable, then its transformation should include atleast one updatable source.
     */
    private void validateUpdatability( final SqlTransformationMappingRoot transRoot,
                                       final ValidationResult validationResult ) {
        if (validationResult.isFatalObject(transRoot)) {
            return;
        }
        Collection inputs = transRoot.getInputs();
        Collection outputs = transRoot.getOutputs();
        if (outputs.isEmpty() || inputs.isEmpty()) {
            return;
        }
        EObject targetObj = (EObject)outputs.iterator().next();

        if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isUpdatableGroup(targetObj)) {
            boolean updatable = false;
            for (final Iterator inIter = inputs.iterator(); inIter.hasNext();) {
                EObject sourceObj = (EObject)inIter.next();
                if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isUpdatableGroup(sourceObj)
                    || org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(sourceObj)) {
                    updatable = true;
                    break;
                }
            }

            if (!updatable) {
                ValidationProblem problem = new ValidationProblemImpl(
                                                                      0,
                                                                      IStatus.WARNING,
                                                                      TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_transformation_defining_an_updatable_virtual_group_should_be_include_atleast_one_updatable_source_group._1", TransformationHelper.getSqlEObjectName(targetObj))); //$NON-NLS-1$
                validationResult.addProblem(problem);
            }
        }
    }

    /**
     * Validates the SqlTransformation by doing parsing/resolution/validation of the sql query on the transformation. If the query
     * is found to be valid additional validation checks are applied on the query which would result is warnings. 1) ERROR -> If
     * the sql defining the transformation, ie not parsable, resolvable or validatable. 2) ERROR -> Updateable virtual group,
     * allows insert/update/delete but does not specify transformation
     */
    private void validateSqlTransformation( final SqlTransformationMappingRoot transRoot,
                                            final ValidationResult validationResult,
                                            final TransformationValidator validator,
                                            final ValidationContext context ) {
        if (validationResult.isFatalObject(transRoot)) {
            return;
        }

        TransformationValidationResult transformResult = validator.validateTransformation();
        // get target group/procedure
        EObject target = transRoot.getTarget();

        // validate further only if transformation is valid
        if (!transformResult.isValid()) {
            
            //If the target is virtual procedure, and isFunction - we dont need a transformation - so ignore transformation problems
            if(target instanceof Procedure) {
                if( ((Procedure)target).isFunction() ) {
                    return;
                }
            }
            
            Collection statuses = null;
            // if has a invalid select collect status
            SqlTransformationResult selectResult = transformResult.getSelectResult();
            if (selectResult != null && !selectResult.isValidatable()) {
                Collection selectStatuses = selectResult.getStatusList();
                if (selectStatuses != null) statuses = selectStatuses;
            }
            // if has a invalid insert collect status
            SqlTransformationResult insertResult = transformResult.getInsertResult();
            if (insertResult != null && !insertResult.isValidatable()) {
                Collection insertStatuses = insertResult.getStatusList();
                if (insertStatuses != null) {
                    if (statuses == null) {
                        statuses = insertStatuses;
                    } else {
                        statuses.addAll(insertStatuses);
                    }
                }
            }
            // if has a invalid update collect status
            SqlTransformationResult updateResult = transformResult.getUpdateResult();
            if (updateResult != null && !updateResult.isValidatable()) {
                Collection updateStatuses = updateResult.getStatusList();
                if (updateStatuses != null) {
                    if (statuses == null) {
                        statuses = updateStatuses;
                    } else {
                        statuses.addAll(updateStatuses);
                    }
                }
            }
            // if has a invalid delete collect status
            SqlTransformationResult deleteResult = transformResult.getDeleteResult();
            if (deleteResult != null && !deleteResult.isValidatable()) {
                Collection deleteStatuses = deleteResult.getStatusList();
                if (deleteStatuses != null) {
                    if (statuses == null) {
                        statuses = deleteStatuses;
                    } else {
                        statuses.addAll(deleteStatuses);
                    }
                }
            }
            // collect statuses on tranformation result
            Collection transformStatuses = transformResult.getStatusList();
            if (transformStatuses != null) {
                if (statuses == null) {
                    statuses = transformStatuses;
                } else {
                    statuses.addAll(transformStatuses);
                }
            }
            // create validation problems for all validation statuses
            if (statuses != null) {
                for (final Iterator statusIter = statuses.iterator(); statusIter.hasNext();) {
                    // create validation problem and add it to the results
                    ValidationProblem failureProblem = new ValidationProblemImpl((IStatus)statusIter.next());
                    validationResult.addProblem(failureProblem);
                }
            }
            return;
        } else if (org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper.isUpdatableGroup(target)) {
            // no Insert/Update/Delete transform but the table is updatable

            // furthur validation checks on the update procedure.
            validateUpdateProcedures(transformResult, transRoot, validationResult);
        }

        SqlTransformationResult selectResult = transformResult.getSelectResult();
        // apply validation furtur validation checks on the valid Select query
        if (selectResult != null) {
            ICommand command = selectResult.getCommand();
            if (command != null) {
                if (command instanceof IQuery) {
                    validateQuery((IQuery)command, transRoot, validationResult);
                }
                validateSubCommands(command, ICommand.TYPE_UNKNOWN, transRoot, validationResult);
            }

            if (!validationResult.isFatalObject(transRoot)) {
                // compare the projected symbols from command with the columns
                ProjectSymbolsValidationHelper projRule = new ProjectSymbolsValidationHelper();
                projRule.validateProjectedSymbols(command, transRoot, validationResult);

                // validate params, resultSet and sql on a virtual procedure.
                validateVirtualProcedures(command, transRoot, validationResult);

                // validate mapping class transformation
                MappingClassTransformationValidationHelper rule = new MappingClassTransformationValidationHelper();
                rule.validate(command, transRoot, validationResult);
            }

            // if there is a string function (SUBSTRING, LOCATE, and INSERT), warn the user that it is one based
            int pref = context.getPreferenceStatus(ValidationPreferences.CORE_STRING_FUNCTIONS_ONE_BASED, IStatus.WARNING);
            if (pref != IStatus.OK) {
                IQueryService queryService = ModelerCore.getTeiidQueryService();
                IFunctionCollectorVisitor functionCollectorVisitor = queryService.getFunctionCollectorVisitor(true);
                Collection<IFunction> functions = functionCollectorVisitor.findFunctions(command, true);
                
                Iterator iter = functions.iterator();
                while (iter.hasNext()) {
                    IFunction function = (IFunction)iter.next();
                    String functionName = function.getName().toUpperCase();
                    if ("SUBSTRING".equals(functionName) || "LOCATE".equals(functionName) || "INSERT".equals(functionName)) {//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        ValidationProblem stringFunctionWarning = new ValidationProblemImpl(
                                                                                            0,
                                                                                            pref,
                                                                                            TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.STRING_BASED_FUNCTION_ONE_BASED")); //$NON-NLS-1$
                        stringFunctionWarning.setHasPreference(context.hasPreferences());
                        validationResult.addProblem(stringFunctionWarning);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Apply additional validation checks on query in the command defining the transform. 1) WARNING - > If there is a cross join
     * involved in the sql. 2) WARNING -> IF there is a join involved between elements of differrent types.
     */
    private void validateQuery( final IQuery query,
                                final SqlTransformationMappingRoot transRoot,
                                final ValidationResult validationResult ) {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        IElementCollectorVisitor elementCollectorVisitor = queryService.getElementCollectorVisitor(true);
        IPredicateCollectorVisitor predicateCollectorVisitor = queryService.getPredicateCollectorVisitor();
        
        // apply additional validation checks for queries
        Collection predicates = predicateCollectorVisitor.findPredicates(query);
        Collection groups = groupCollectorVisitor.findGroups(query);
        if (predicates.isEmpty() && groups.size() > 1) {
            // There are no predicates but there are groups
            // (this is the trivial case)
            ValidationProblem warningProblem = new ValidationProblemImpl(
                                                                         0,
                                                                         IStatus.WARNING,
                                                                         TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.6", getNamesForGroupSymbols(groups))); //$NON-NLS-1$
            validationResult.addProblem(warningProblem);
            // added to append xml document parent info.
            appendDocumentLocation(transRoot, validationResult);
        } else if (groups.size() > 1) {
            // Make sure that all the groups appear in at least one join predicate,
            // (note there might be predicates that are not join predicates).
            // Otherwise warn of a possible cross join.
            // A join predicate must be of the form "GroupA.ElementA = GroupB.ElementB".
            // collection groups that are joined
            List allJoins = new ArrayList();
            IFunctionLibrary functionLibrary = UdfManager.getInstance().getFunctionLibrary();
            
            for (final Iterator predicateIter = predicates.iterator(); predicateIter.hasNext();) {
                Object predicate = predicateIter.next();
                if (predicate instanceof ICompareCriteria) {
                    ICompareCriteria compare = (ICompareCriteria)predicate;
                    // collect all the groups involved in this join
                    Collection groupsInJoin = new HashSet();
                    for (final Iterator elementIter = elementCollectorVisitor.findElements(compare).iterator(); elementIter.hasNext();) {
                        IElementSymbol element = (IElementSymbol)elementIter.next();
                        IGroupSymbol group = element.getGroupSymbol();
                        if (group == null && element.isExternalReference()) {
                            String elementFullName = element.getName();
                            int grpIndex = elementFullName.indexOf(element.getShortName());
                            String groupName = elementFullName.substring(0, grpIndex - 1);
                            group = factory.createGroupSymbol(groupName);
                        }
                        if (group != null) {
                            groupsInJoin.add(group);
                        }
                    }
                    // only if multiple groups are involved is it a join
                    if (groupsInJoin.size() > 1) {
                        allJoins.add(groupsInJoin);
                    }
                    if (compare.getOperator() == ICompareCriteria.EQ) {
                        IExpression leftExpression = compare.getLeftExpression();
                        IExpression rightExpression = compare.getRightExpression();
                        // in case of CAST/CONVERT functions get the elementsymbol in the function
                        // and compare types
                        if (leftExpression != null && leftExpression instanceof IFunction) {
                            IFunction leftFunction = (IFunction)leftExpression;
                            String descriptorName = leftFunction.getFunctionDescriptor().getName();
                            if (leftFunction.isImplicit()
                                && descriptorName != null
                                && (descriptorName.equals(functionLibrary.getFunctionName(FunctionName.CONVERT)) || 
                                                          descriptorName.equals(functionLibrary.getFunctionName(FunctionName.CAST)))) {

                                leftExpression = leftFunction.getArg(0);
                            }
                        }
                        if (rightExpression != null && rightExpression instanceof IFunction) {
                            IFunction rightFunction = (IFunction)rightExpression;
                            String descriptorName = rightFunction.getFunctionDescriptor().getName();
                            if (rightFunction.isImplicit()
                                && descriptorName != null
                                && (descriptorName.equals(functionLibrary.getFunctionName(FunctionName.CONVERT)) || 
                                                          descriptorName.equals(functionLibrary.getFunctionName(FunctionName.CAST)))) {

                                rightExpression = rightFunction.getArg(0);
                            }
                        }
                        
                        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();

                        if (leftExpression instanceof IElementSymbol && rightExpression instanceof IElementSymbol) {
                            Class leftDataType = leftExpression.getType();
                            Class rightDataType = rightExpression.getType();
                            if (!leftDataType.equals(rightDataType)) {
                                Object[] params = new Object[] {compare, service.getDataTypeName(leftDataType),
                                    service.getDataTypeName(rightDataType)};

                                ValidationProblem warningProblem = new ValidationProblemImpl(
                                                                                             0,
                                                                                             IStatus.WARNING,
                                                                                             TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.Join_type_mismatch_in_crit", params)); //$NON-NLS-1$
                                validationResult.addProblem(warningProblem);
                                // added to append xml document parent info.
                                appendDocumentLocation(transRoot, validationResult);
                            }
                        }
                    }
                }
            }

            // based on transitive property check if the groups in the various
            // joins are joined i.e if a join b and b join c then a is joined to c or a,b,c are joined
            // accumulated the groups thus joined in a collection

            // collection of all the groups joined
            Collection groupsJoined = new HashSet();
            if (allJoins.size() == 1) {
                groupsJoined.addAll((Collection)allJoins.get(0));
            } else {
                for (int i = 0; i < allJoins.size(); i++) {
                    Collection join1 = (Collection)allJoins.get(i);
                    for (int j = i + 1; j < allJoins.size(); j++) {
                        Collection join2 = (Collection)allJoins.get(j);
                        // check if groups in join2 are in join1
                        for (final Iterator grpIter = join2.iterator(); grpIter.hasNext();) {
                            IGroupSymbol joinGrp = (IGroupSymbol)grpIter.next();
                            // all groups in join1 and join2 are joined together
                            if (join1.contains(joinGrp)) {
                                groupsJoined.addAll(join1);
                                groupsJoined.addAll(join2);
                                break;
                            }
                        }
                    }
                }
            }

            // check if existing joins have all groups involved in the query
            if (!groupsJoined.containsAll(groups)) { // there are still unjoined groups
                groups.removeAll(groupsJoined);
                ValidationProblem warningProblem = new ValidationProblemImpl(
                                                                             0,
                                                                             IStatus.WARNING,
                                                                             TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.6", getNamesForGroupSymbols(groups))); //$NON-NLS-1$
                validationResult.addProblem(warningProblem);
                // added to append xml document parent info.
                appendDocumentLocation(transRoot, validationResult);
            }
        }

        // validate the option if any
        validateOption(query, transRoot, validationResult);
    }

    /**
     * This method is added to append the xml document model parent node info in case of a warning accures. MyDefect 155885
     * 
     * @since 4.2
     */
    private void appendDocumentLocation( final SqlTransformationMappingRoot transRoot,
                                         final ValidationResult validationResult ) {
        String docName = getDocumentName(transRoot);
        if (docName != null) {
            validationResult.setLocationPath(docName + "/" + validationResult.getLocationPath()); //$NON-NLS-1$
        }
    }

    /**
     * This method provide the xml document parent node name.
     * 
     * @since 4.2
     */
    private String getDocumentName( final SqlTransformationMappingRoot transRoot ) {
        String docName = null;
        Object mappingClassImpl;
        Object mappingClassSetImpl;
        Object xmlDocumentImpl;

        try {
            mappingClassImpl = transRoot.getTarget();
            if (mappingClassImpl instanceof MappingClassImpl) {
                mappingClassSetImpl = ((MappingClassImpl)mappingClassImpl).eContainer();
                if (mappingClassSetImpl instanceof MappingClassSetImpl) {
                    xmlDocumentImpl = ((MappingClassSetImpl)mappingClassSetImpl).getTarget();
                    if (xmlDocumentImpl instanceof XmlDocumentImpl) {
                        docName = ((XmlDocumentImpl)xmlDocumentImpl).getName();
                    }
                }
            }
        } catch (Exception ex) {
            TransformationPlugin.Util.log(IStatus.WARNING, ex, ex.getMessage());
        }

        return docName;
    }

    private Collection getNamesForGroupSymbols( final Collection groups ) {
        Collection groupNames = new HashSet(groups.size());
        for (final Iterator iter = groups.iterator(); iter.hasNext();) {
            IGroupSymbol grpSyb = (IGroupSymbol)iter.next();
            Object metadataID = grpSyb.getMetadataID();
            if (metadataID != null && metadataID instanceof MetadataRecord) {
                groupNames.add(((MetadataRecord)metadataID).getFullName());
            } else {
                groupNames.add(grpSyb.getName());
            }
        }
        return groupNames;
    }

    /**
     * Validate that the groups used in OPTION MAKEDEP and NOCACHE clause are physical groups used in the transformation or any of
     * the tranformations it depends on.
     * 
     * @since 4.2
     */
    private void validateOption( final ICommand command,
                                 final SqlTransformationMappingRoot root,
                                 final ValidationResult result ) {
        IOption option = command.getOption();
        if (option == null) {
            return;
        }

        // Validate the Option dependent groups
        validateDepOptionGroups(option.getDependentGroups(), root, result);
        // Validate the Option not dependent groups
        validateDepOptionGroups(option.getNotDependentGroups(), root, result);

        // Validate the Option NoCache groups
        validateOptionNoCacheGrps(command, root, result);
    }

    /**
     * Validate that the groups used in OPTION MAKEDEP/MAKENOTDEP clause are physical groups used in the transformation or any of
     * the tranformations it depends on. 1) Warning -> Group name in MAKEDEP/MAKENOTDEP clause not fully qualified indicating it
     * may be alias.
     * 
     * @since 4.2
     */
    private void validateDepOptionGroups( final Collection<String> groupNames,
                                          final SqlTransformationMappingRoot root,
                                          final ValidationResult result ) {
        if (groupNames == null || groupNames.isEmpty()) {
            return;
        }

        for (String groupName : groupNames) {
            // check if the group name specified is a fully qualified name
            // if it is not a fully qualified name, its
            // probably an alias name
            if (groupName.indexOf(TransformationMetadata.DELIMITER_CHAR) < 0) {
                ValidationProblem warningProblem = new ValidationProblemImpl(
                                                                             0,
                                                                             IStatus.WARNING,
                                                                             TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.0", groupName)); //$NON-NLS-1$
                result.addProblem(warningProblem);
                // warn an alias is being used
                continue;
            }

            /*
             * As part of Case 5595 I am removing the code that used to generate an error
             * when the group in a MAKEDEP clause was a virtual group.
             *  
             */
        }
    }

    /**
     * Validate that the groups used in OPTION NOCACHE caluse are physical groups used in the transformation or any of the
     * tranformations it depends on. 1) Warning -> Group name in NOCACHE clause not fully qualified indicating it may be alias. 2)
     * Error -> Group used in the NOCACHE clause is a virtual group 3) Error -> Could not find the group in NOCACHE clause in any
     * dependent transformation
     * 
     * @since 4.2
     */
    private void validateOptionNoCacheGrps( final ICommand command,
                                            final SqlTransformationMappingRoot root,
                                            final ValidationResult result ) {

        IOption option = command.getOption();
        if (option == null || !option.isNoCache()) {
            return;
        }
        Collection groups = groupCollectorVisitor.findGroups(command);
        // names of groups specified in NO cACHE clause
        Collection<String> noCacheGroups = option.getNoCacheGroups();
        boolean hasMaterializedGroups = false;
        if (noCacheGroups != null) {
            for (String groupName : noCacheGroups) {
                boolean foundMaterializedMatch = false;
                // collections of short and group names that match the
                // names of noCacheGroup
                Collection shortNamesMatched = new LinkedList();
                Collection partialNamesMatched = new LinkedList();
                Collection aliasNamesMatched = new LinkedList();
                // compare the name against against the names of materialized virtual groups
                for (final Iterator grpIter = groups.iterator(); grpIter.hasNext();) {
                    IGroupSymbol grpSymbol = (IGroupSymbol)grpIter.next();
                    Object metadataID = grpSymbol.getMetadataID();
                    if (metadataID != null && metadataID instanceof TableRecord) {
                        TableRecord record = (TableRecord)metadataID;
                        String tableFullName = record.getFullName();
                        String tableShortName = record.getName();
                        String aliasName = grpSymbol.getDefinition() != null ? grpSymbol.getName() : null;
                        boolean isMaterialized = record.isMaterialized();
                        if (isMaterialized) {
                            hasMaterializedGroups = true;
                        }
                        // if full name match found, break out and continue
                        // with the other groups in the NO OPTION clause
                        if (isMaterialized && groupName.equalsIgnoreCase(tableFullName)) {
                            foundMaterializedMatch = true;
                            break;
                        } else if (groupName.equalsIgnoreCase(tableShortName)) {
                            if (!shortNamesMatched.contains(groupName.toUpperCase())) {
                                shortNamesMatched.add(groupName.toUpperCase());
                                if (isMaterialized) {
                                    foundMaterializedMatch = true;
                                }
                            } else {
                                // ambiguous case
                                ValidationProblem warningProblem = new ValidationProblemImpl(
                                                                                             0,
                                                                                             IStatus.ERROR,
                                                                                             TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.7", groupName)); //$NON-NLS-1$
                                result.addProblem(warningProblem);
                                return;
                            }
                        } else if (aliasName != null && groupName.equalsIgnoreCase(aliasName)) {
                            if (!aliasNamesMatched.contains(groupName.toUpperCase())) {
                                aliasNamesMatched.add(groupName.toUpperCase());
                                if (isMaterialized) {
                                    foundMaterializedMatch = true;
                                }
                            } else {
                                // ambiguous case
                                ValidationProblem warningProblem = new ValidationProblemImpl(
                                                                                             0,
                                                                                             IStatus.ERROR,
                                                                                             TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.8", groupName)); //$NON-NLS-1$
                                result.addProblem(warningProblem);
                                return;
                            }
                        } else if (CoreStringUtil.endsWithIgnoreCase(tableFullName, groupName)) {
                            if (!partialNamesMatched.contains(tableFullName.toUpperCase())) {
                                partialNamesMatched.add(tableFullName.toUpperCase());
                                if (isMaterialized) {
                                    foundMaterializedMatch = true;
                                }
                            } else {
                                // ambiguous case
                                ValidationProblem warningProblem = new ValidationProblemImpl(
                                                                                             0,
                                                                                             IStatus.ERROR,
                                                                                             TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.11", groupName)); //$NON-NLS-1$
                                result.addProblem(warningProblem);
                                return;
                            }
                        }
                    }
                }

                if (!foundMaterializedMatch) {
                    // group name does not match any materialized group names
                    ValidationProblem warningProblem = new ValidationProblemImpl(
                                                                                 0,
                                                                                 IStatus.ERROR,
                                                                                 TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.9", groupName)); //$NON-NLS-1$
                    result.addProblem(warningProblem);
                    return;
                }
            }
        }

        if (!hasMaterializedGroups && (noCacheGroups == null || noCacheGroups.isEmpty())) {
            ValidationProblem warningProblem = new ValidationProblemImpl(
                                                                         0,
                                                                         IStatus.WARNING,
                                                                         TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.10")); //$NON-NLS-1$
            result.addProblem(warningProblem);
            return;
        }
    }

    // / HELPER METHODS

    private Collection getMappingInputs( final SqlTransformationMappingRoot root,
                                         final boolean recursive ) {
        if (root != null) {
            Collection result = new HashSet();
            Collection visitedMappings = new HashSet();
            addInputsToCollection(root, result, visitedMappings, recursive);
            return result;
        }
        return Collections.EMPTY_SET;
    }

    private void addInputsToCollection( final Mapping mapping,
                                        final Collection result,
                                        final Collection visitedMappings,
                                        final boolean recursive ) {
        if (mapping != null) {
            // Make sure we do not visit the same Mapping more than once
            if (visitedMappings.contains(mapping)) {
                return;
            }
            visitedMappings.add(mapping);
            result.addAll(mapping.getInputs());

            if (!recursive) {
                return;
            }
            // Iterate over the transformation inputs looking for inputs from virtual models
            for (final Iterator iter = mapping.getInputs().iterator(); iter.hasNext();) {
                final EObject input = (EObject)iter.next();
                final Container container = ModelerCore.getContainer(mapping);
                final Resource resource = ModelerCore.getModelEditor().findResource(container, input);

                if (resource instanceof EmfResource) {
                    final EmfResource emfResource = (EmfResource)resource;
                    final ModelAnnotation model = emfResource.getModelAnnotation();

                    // If the input to the transformation is from a virtual model then
                    // we need to accumulate its inputs ...
                    if (model != null && model.getModelType() == ModelType.VIRTUAL_LITERAL) {
                        final ModelContents contents = emfResource.getModelContents();
                        final Collection xforms = contents.getTransformations(input);
                        for (final Iterator iterator = xforms.iterator(); iterator.hasNext();) {
                            final TransformationMappingRoot mappingRoot = (TransformationMappingRoot)iterator.next();
                            addInputsToCollection(mappingRoot, result, visitedMappings, recursive);
                        }
                    }
                }
            }
        }
    }

}
