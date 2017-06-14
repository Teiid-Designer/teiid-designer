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
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.id.UUID;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.metadata.runtime.ColumnRecord;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.util.CommandHelper;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.util.AttributeMappingHelper;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.transformation.util.TransformationSqlHelper;
import org.teiid.designer.transformation.validation.TransformationValidator;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeName;


/**
 * This rule compares the number of projected symbols of the sql transformation with the number of mappings. Also compare the
 * datatypes of the projected symbols with the datatypes of the outputs of the mappings.
 * 
 * @since 8.0
 */
public class ProjectSymbolsValidationHelper {

    /**
     * Compare the project symbols on the command with the mappings and target attributes.
     * 
     * @param command The command for project symbols
     * @param transRoot The mapping root for the transformation
     * @param validationResult The validation result that gets updated with problems.
     * @since 4.3
     */
    public void validateProjectedSymbols( final ICommand command,
                                          final SqlTransformationMappingRoot transRoot,
                                          final ValidationResult validationResult ) {
        List projSymbols = CommandHelper.getProjectedSymbols(command);
        applyMappingValidationRules(projSymbols, transRoot, validationResult);
    }

    /**
     * Apply validation rules specific to Procedure mappings when input parameters are present. In this case exra columns are
     * added to the target group to provide mappings for the input parameter to allow for the paremeter values to be passed to the
     * paremeter execution at runtime. Source of a transformation is a procedure and the target is a table, mapping class or a
     * procedure.
     * 
     * @param projSymbols The list or projected symbols from the command.
     * @param transRoot The mappingroot whose source is a procedure
     * @param validationResult The validation result that gets updated with problems.
     * @since 4.3
     */
    public void applyMappingValidationRules( final List projSymbols,
                                             final SqlTransformationMappingRoot transRoot,
                                             final ValidationResult validationResult ) {

        // list of target attributes
        // create new collection to prevent modification of EList
        List attributes = new ArrayList(TransformationValidator.getOutputColumns(transRoot));

        // list of all nexted mappings
        // create new collection to prevent modification of EList
        Collection mappings = new ArrayList(transRoot.getNested());

        // compare project symbols with nested mappings
        checkMappingsForProjectSymbols(transRoot, mappings, projSymbols, validationResult);

        // compare project symbols with target attributes
        checkTargetAttributesForProjectSymbols(transRoot, attributes, projSymbols, validationResult);

        // compare project symbols and target attribues
        compareProjectSymbolsAndTargetColumns(transRoot, projSymbols, attributes, validationResult);
    }

    /**
     * There should be one nested mapping per projected symbol these are column level mappings, each mapping may/not have a input
     * but it should have exactly one output(virtual group attribute) this is verified in TransformationMappingValidationRule
     * 
     * @param mappings
     * @param projSymbols
     * @since 4.3
     */
    public void checkMappingsForProjectSymbols( final SqlTransformationMappingRoot transRoot,
                                                Collection mappings,
                                                Collection projSymbols,
                                                final ValidationResult validationResult ) {
        EObject target = transRoot.getTarget();
        String problemMsg = null;
        if (projSymbols.size() > mappings.size()) {
            problemMsg = TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_number_of_columns/elements_in_{0}_are_less_than_the_number_defined_in_the_sql_transformation._1", TransformationHelper.getSqlEObjectName(target)); //$NON-NLS-1$
        } else if (projSymbols.size() < mappings.size()) {
            problemMsg = TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_number_of_columns/elements_in_{0}_are_greater_than_the_number_defined_in_the_sql_transformation._2", TransformationHelper.getSqlEObjectName(target)); //$NON-NLS-1$
        }
        // create validation problem and additional to the results
        if (problemMsg != null) {
            ValidationProblem typeProblem = new ValidationProblemImpl(0, IStatus.ERROR, problemMsg);
            validationResult.addProblem(typeProblem);
        }
    }

    /**
     * There should be one target attribute per projected symbol.
     * 
     * @param mappings
     * @param projSymbols
     * @since 4.3
     */
    public void checkTargetAttributesForProjectSymbols( final SqlTransformationMappingRoot transRoot,
                                                        Collection attributes,
                                                        Collection projSymbols,
                                                        final ValidationResult validationResult ) {
        EObject target = transRoot.getTarget();
        String problemMsg = null;
        if (projSymbols.size() > attributes.size()) {
            problemMsg = TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_number_of_columns/elements_in_{0}_are_less_than_the_number_defined_in_the_sql_transformation._1", TransformationHelper.getSqlEObjectName(target)); //$NON-NLS-1$
        } else if (projSymbols.size() < attributes.size()) {
            problemMsg = TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_number_of_columns/elements_in_{0}_are_greater_than_the_number_defined_in_the_sql_transformation._2", TransformationHelper.getSqlEObjectName(target)); //$NON-NLS-1$
        }
        // create validation problem and additional to the results
        if (problemMsg != null) {
            ValidationProblem typeProblem = new ValidationProblemImpl(0, IStatus.ERROR, problemMsg);
            validationResult.addProblem(typeProblem);
        }
    }

    /**
     * Compare the project symbols from a SqlTransformation query to the columns on the target table or procedure resultset. The
     * names and datatypes of columns in the target should match those in the sql transformation query. The order of columns in
     * the target should be same as the order of project symbols from the sql.
     * 
     * @param transRoot Mapping root whose nested mappings are validated.
     * @param projSymbols The list of project symbols from the sql transformation
     * @param validationResult The validation result that gets updated with problems.
     * @since 4.3
     */
    public void compareProjectSymbolsAndTargetColumns( final SqlTransformationMappingRoot transRoot,
                                                       final List projSymbols,
                                                       final List targetColumns,
                                                       final ValidationResult validationResult ) {

        // get the target to the transformation and list of
        // output columns on it
        EObject target = transRoot.getTarget();
        final Iterator colIter = targetColumns.iterator();
        final Iterator projIter = projSymbols.iterator();
        while (projIter.hasNext() && colIter.hasNext()) {
            boolean foundMatch = false;
            EObject outputColumn = (EObject)colIter.next();
            String outputColumnName = TransformationHelper.getSqlColumnName(outputColumn);
            IExpression singleElementSymbol = (IExpression)projIter.next();
            String symbolName = AttributeMappingHelper.getSymbolShortName(singleElementSymbol);
            if (outputColumnName.equalsIgnoreCase(symbolName)) {
                foundMatch = true;
            } else if (symbolName != null && symbolName.toLowerCase().startsWith(UUID.PROTOCOL)) {
                // If the metadataID for the symbol is a TempMetadataID and
                // not a MetadataRecord instance then the symbol name may be
                // in the form of a UUID. Try to resolve this UUID to an EObject
                // so that we can ultimately get the user name of this symbol.
                // Fix for defect 17764
                String uuid = symbolName.toLowerCase();
                Container cntr = ModelerCore.getContainer(outputColumn);
                if (cntr != null) {
                    Object obj = cntr.getEObjectFinder().find(uuid);
                    if (obj instanceof EObject) {
                        symbolName = TransformationHelper.getSqlColumnName((EObject)obj);
                        if (outputColumnName.equalsIgnoreCase(symbolName)) {
                            foundMatch = true;
                        }
                    }
                }
            }
            // -----------------------------------------------------------------
            // Check #1 - column name in virtual table does not match any symbol
            // -----------------------------------------------------------------
            if (!foundMatch) {
                // create validation problem and additional to the results
                ValidationProblem typeProblem = new ValidationProblemImpl(
                                                                          0,
                                                                          IStatus.ERROR,
                                                                          TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_target_attribute_matches_no_symbol_in_the_query_{0}._1", outputColumnName)); //$NON-NLS-1$
                validationResult.addProblem(typeProblem);
                return;
            }

            // ------------------------------------------------------------------------------
            // Check #2 - column datatype does not match the symbol datatype
            // ------------------------------------------------------------------------------
            IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
            Class sourceType = singleElementSymbol.getType();
            

            
            String problemMsg = null;
            // check only if the source is a valid type
            SqlColumnAspect columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(outputColumn);
            if (sourceType != null && sourceType != service.getDefaultDataClass(DataTypeName.NULL)) { 
                EObject datatype = columnAspect.getDatatype(outputColumn);
                SqlDatatypeAspect typeAspect = datatype != null ? (SqlDatatypeAspect)AspectManager.getSqlAspect(datatype) : null;
                if (typeAspect != null) {
                    Class targetType = service.getDataTypeClass(typeAspect.getRuntimeTypeName(datatype));
                    
                    boolean isAliasedSymbolsMatch = false;
                    if (singleElementSymbol instanceof IAliasSymbol) {
                    	IExpression symbol = ((IAliasSymbol)singleElementSymbol).getSymbol();
                    	if( symbol instanceof IFunction) {
        	            	Object dType = TransformationSqlHelper.getElementSymbolType(symbol);
        	            	if( dType instanceof EObject ) {
                    			String attTypeName = ModelerCore.getModelEditor().getName((EObject)dType);
                    			String symTypeName = ModelerCore.getModelEditor().getName(datatype);
        	            		isAliasedSymbolsMatch =  attTypeName.equalsIgnoreCase(symTypeName);
        	            	}
                    	}
                    }
                    
                    if (!isAliasedSymbolsMatch && !sourceType.equals(targetType)) {
                        problemMsg = TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_datatype_type_of_the_column_{0}_does_not_match_the_source_column_type._1", //$NON-NLS-1$
                                                                         new Object[] {outputColumnName,
                                                                             service.getDataTypeName(targetType),
                                                                             service.getDataTypeName(sourceType)});
                        // create validation problem and additional to the results
                        ValidationProblem typeProblem = new ValidationProblemImpl(0, IStatus.ERROR, problemMsg);
                        validationResult.addProblem(typeProblem);
                        continue;
                    }
                } else {
                    problemMsg = TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_datatype_type_of_the_column_{0}_is_not_set_or_cannot_be_resolved_in_the_workspace._1", outputColumnName); //$NON-NLS-1$
                    // create validation problem and additional to the results
                    ValidationProblem typeProblem = new ValidationProblemImpl(0, IStatus.ERROR, problemMsg);
                    validationResult.addProblem(typeProblem);
                    continue;
                }
            }

            // -------------------------------------------------------------------------
            // Check #3 - A "nullable" field in a physical group should also be nullable
            // in its corresponding virtual group
            // -------------------------------------------------------------------------
            // The target attribute may not be a MappingClass column (fix for defect 10917)
            if (!(target instanceof MappingClass)) {
                if ((singleElementSymbol instanceof IElementSymbol) && (outputColumn != null)) {
                    final IElementSymbol eSymbol = (IElementSymbol)singleElementSymbol;
                    final Object metadataID = eSymbol.getMetadataID();
                    if ((metadataID != null) && (metadataID instanceof ColumnRecord)) {
                        ColumnRecord colRecord = (ColumnRecord)metadataID;
                        int symbolNullType = colRecord.getNullType();
                        columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(outputColumn);
                        int columnNullType = columnAspect.getNullType(outputColumn);
                        if (symbolNullType != columnNullType) {
                            problemMsg = TransformationPlugin.Util.getString("SqlTransformationMappingRootValidationRule.The_Nullable_value_of_virtual_group_attribute_{0}_doesn____t_match_that_of_the_attribute_it_mapps_to_in_query_transform._1", outputColumnName); //$NON-NLS-1$
                            // create validation problem and additional to the results
                            ValidationProblem typeProblem = new ValidationProblemImpl(0, IStatus.WARNING, problemMsg);
                            validationResult.addProblem(typeProblem);
                            continue;
                        }
                    }
                }
            }
        }
    }
}
