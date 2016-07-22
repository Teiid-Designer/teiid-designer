/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.validator;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.teiid.api.exception.query.QueryValidatorException;
import org.teiid.core.types.ArrayImpl;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryMetadataInterface.SupportConstants;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.ISetQuery.Operation;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.language.SQLConstants;
import org.teiid.metadata.AggregateAttributes;
import org.teiid.metadata.Table;
import org.teiid.query.eval.Evaluator;
import org.teiid.query.function.FunctionMethods;
import org.teiid.query.function.source.XMLSystemFunctions;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.resolver.ProcedureContainerResolver;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Alter;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.AlterView;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Create;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.Drop;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.HasCriteria;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.IsDistinctCriteria;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.Labeled;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NamespaceItem;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.ObjectColumn;
import org.teiid.query.sql.lang.ObjectTable;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.SetClauseList;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.TargetedCommand;
import org.teiid.query.sql.lang.TextColumn;
import org.teiid.query.sql.lang.TextTable;
import org.teiid.query.sql.lang.TranslateCriteria;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.lang.WithQueryCommand;
import org.teiid.query.sql.lang.XMLColumn;
import org.teiid.query.sql.lang.XMLTable;
import org.teiid.query.sql.navigator.PreOrderNavigator;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement;
import org.teiid.query.sql.proc.BranchingStatement.BranchingMode;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.WhileStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.DerivedColumn;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.JSONObject;
import org.teiid.query.sql.symbol.QueryString;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.Reference.Constraint;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.TextLine;
import org.teiid.query.sql.symbol.WindowFunction;
import org.teiid.query.sql.symbol.XMLAttributes;
import org.teiid.query.sql.symbol.XMLCast;
import org.teiid.query.sql.symbol.XMLElement;
import org.teiid.query.sql.symbol.XMLExists;
import org.teiid.query.sql.symbol.XMLForest;
import org.teiid.query.sql.symbol.XMLNamespaces;
import org.teiid.query.sql.symbol.XMLParse;
import org.teiid.query.sql.symbol.XMLQuery;
import org.teiid.query.sql.symbol.XMLSerialize;
import org.teiid.query.sql.visitor.AggregateSymbolCollectorVisitor;
import org.teiid.query.sql.visitor.AggregateSymbolCollectorVisitor.AggregateStopNavigator;
import org.teiid.query.sql.visitor.ElementCollectorVisitor;
import org.teiid.query.sql.visitor.EvaluatableVisitor;
import org.teiid.query.sql.visitor.FunctionCollectorVisitor;
import org.teiid.query.sql.visitor.GroupCollectorVisitor;
import org.teiid.query.sql.visitor.GroupsUsedByElementsVisitor;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import org.teiid.query.sql.visitor.ValueIteratorProviderCollectorVisitor;
import org.teiid.query.validator.UpdateValidator.UpdateInfo;
import org.teiid.query.xquery.saxon.SaxonXQueryExpression;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;
import org.teiid.translator.SourceSystemFunctions;
import net.sf.saxon.om.Name11Checker;
import net.sf.saxon.om.QNameException;

/**
 *
 */
public class ValidationVisitor extends AbstractValidationVisitor {

    private static final class PositiveIntegerConstraint implements Reference.Constraint {
    	
    	private Messages.ValidationVisitor msgKey;
    	
    	public PositiveIntegerConstraint(Messages.ValidationVisitor enumKey) {
    		this.msgKey = enumKey;
		}

		@Override
        public void validate(Object value) throws Exception {
			if (value == null || ((Integer)value).intValue() < 0) {
				 throw new TeiidClientException(Messages.getString(msgKey));
			}
		}
	}

    public static final Constraint LIMIT_CONSTRAINT = new PositiveIntegerConstraint(Messages.ValidationVisitor.badlimit2);

	// State during validation
    private boolean isXML = false;	// only used for Query commands
    
    private boolean inQuery;

	// update procedure being validated
	private ICreateProcedureCommand<Block, GroupSymbol, Expression, LanguageVisitor> createProc;

	private final DataTypeManagerService dataTypeManager;

	/**
     * @param teiidVersion
     */
    public ValidationVisitor(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
        dataTypeManager = DataTypeManagerService.getInstance(teiidVersion);
    }

	@Override
    public void reset() {
        super.reset();
        this.isXML = false;
        this.inQuery = false;

        if (getTeiidVersion().isGreaterThanOrEqualTo(Version.TEIID_8_0))
            this.createProc = null;
    }

    // ############### Visitor methods for language objects ##################
    
//    public void visit(BatchedUpdateCommand obj) {
//        List<Command> commands = obj.getUpdateCommands();
//        Command command = null;
//        int type = 0;
//        for (int i = 0; i < commands.size(); i++) {
//            command = commands.get(i);
//            type = command.getType();
//            if (type != Command.TYPE_INSERT &&
//                type != Command.TYPE_UPDATE &&
//                type != Command.TYPE_DELETE &&
//                type != Command.TYPE_QUERY) { // SELECT INTO command
//                handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_batch_command),command);
//            } else if (type == Command.TYPE_QUERY) {
//                Into into = ((Query)command).getInto();
//                if (into == null) {
//                    handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_batch_command),command);
//                }
//            }
//        }
//    }

	@Override
    public void visit(Delete obj) {
    	validateNoXMLUpdates(obj);
        GroupSymbol group = obj.getGroup();
        validateGroupSupportsUpdate(group);
        if (obj.getUpdateInfo() != null && obj.getUpdateInfo().isInherentDelete()) {
        	validateUpdate(obj, ICommand.TYPE_DELETE, obj.getUpdateInfo());
        }
    }

    @Override
    public void visit(GroupBy obj) {
    	// Get list of all group by IDs
        List<Expression> groupBySymbols = obj.getSymbols();
        validateSortable(groupBySymbols);
        for (Expression expr : groupBySymbols) {
            if (!ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(expr).isEmpty() || expr instanceof Constant || expr instanceof Reference) {
            	handleValidationError(Messages.getString(Messages.ValidationVisitor.groupby_subquery, expr), expr);
            }
		}
    }
    
    @Override
    public void visit(GroupSymbol obj) {
    	try {
			if (this.getMetadata().isScalarGroup(obj.getMetadataID())) {
			    handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_scalar_group_reference, obj),obj);    		
			}
		} catch (Exception e) {
			handleException(e);
		}
    }

    @Override
    public void visit(Insert obj) {
        validateNoXMLUpdates(obj);
        validateGroupSupportsUpdate(obj.getGroup());
        validateInsert(obj);
        
        try {
			if (obj.isMerge()) {
				Collection keys = getMetadata().getUniqueKeysInGroup(obj.getGroup().getMetadataID());
				if (keys.isEmpty()) {
					handleValidationError(Messages.gs(Messages.TEIID.TEIID31132, obj.getGroup()), obj);
				} else {
					Set<Object> keyCols = new LinkedHashSet<Object>(getMetadata().getElementIDsInKey(keys.iterator().next()));
					for (ElementSymbol es : obj.getVariables()) {
						keyCols.remove(es.getMetadataID());
					}
					if (!keyCols.isEmpty()) {
						handleValidationError(Messages.gs(Messages.TEIID.TEIID31133, obj.getGroup(), obj.getVariables()), obj);
					}
				}
			}
		} catch (Exception e1) {
			handleException(e1);
		}
        
        if (obj.getQueryExpression() != null) {
        	validateMultisourceInsert(obj.getGroup());
        }
        if (obj.getUpdateInfo() != null && obj.getUpdateInfo().isInherentInsert()) {
        	validateUpdate(obj, ICommand.TYPE_INSERT, obj.getUpdateInfo());
        	try {
				if (obj.getUpdateInfo().findInsertUpdateMapping(obj, false) == null) {
					handleValidationError(Messages.gs(Messages.TEIID.TEIID30376, obj.getVariables()), obj);
				}
			} catch (Exception e) {
				handleValidationError(e.getMessage(), obj);
			}
        }
    }

    @Override
    public void visit(OrderByItem obj) {
    	validateSortable(obj.getSymbol());

    	if (obj.getExpressionPosition() < 0 && isTeiid89OrGreater()) { // Added for Teiid 8.9
            for (SubqueryContainer subquery : ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(obj)) {
                for (ElementSymbol es : ElementCollectorVisitor.getElements(obj, true, true)) {
                    if (es.isExternalReference()) {
                        handleValidationError(Messages.gs(Messages.TEIID.TEIID31156, subquery), obj);
                    }
                }
            }
        }
    }
    
    @Override
    public void visit(Query obj) {
        validateHasProjectedSymbols(obj);
        if(isXMLCommand(obj)) {
            //no temp table (Select Into) allowed
            if(obj.getInto() != null){
                handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0069),obj);
            }

        	this.isXML = true;
	        validateXMLQuery(obj);
        } else {
        	this.inQuery = true;
            validateAggregates(obj);

            //if it is select with no from, should not have ScalarSubQuery
            if(obj.getSelect() != null && obj.getFrom() == null){
                if(!ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(obj.getSelect()).isEmpty()){
                    handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0067),obj);
                }
            }
            
            if (obj.getInto() != null) {
                validateSelectInto(obj);
            }                        
        }
    }
	
	@Override
    public void visit(Select obj) {
        validateSelectElements(obj);
        if(obj.isDistinct()) {
            validateSortable(obj.getProjectedSymbols());
        }
    }

	@Override
    public void visit(SubquerySetCriteria obj) {
		validateSubquery(obj);
		if (isNonComparable(obj.getExpression())) {
			handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0027, obj, getDataTypeManager().getDataTypeName(obj.getExpression().getType())), obj);
    	}
        this.validateRowLimitFunctionNotInInvalidCriteria(obj);
        
		Collection<Expression> projSymbols = obj.getCommand().getProjectedSymbols();

		//Subcommand should have one projected symbol (query with one expression
		//in SELECT or stored procedure execution that returns a single value).
		if(projSymbols.size() != 1) {
			handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0011),obj);
		}
	}
	
	@Override
	public void visit(XMLSerialize obj) {
		if (obj.getEncoding() != null ) {
        	try {
				Charset.forName(obj.getEncoding());
        	} catch (IllegalArgumentException e) {
        		handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_encoding, obj.getEncoding()), obj);
        	}
			if ((obj.getType() != DataTypeManagerService.DefaultDataTypes.BLOB.getTypeClass() && obj.getType() != DataTypeManagerService.DefaultDataTypes.VARBINARY.getTypeClass())) {
				handleValidationError(Messages.getString(Messages.ValidationVisitor.encoding_for_binary), obj);
			}
		}
	}

    @Override
    public void visit(SetQuery obj) {
        validateHasProjectedSymbols(obj);
        validateSetQuery(obj);
    }
    
    @Override
    public void visit(Update obj) {
        validateNoXMLUpdates(obj);
        validateGroupSupportsUpdate(obj.getGroup());
        validateUpdate(obj);
    }

    @Override
    public void visit(Into obj) {
        GroupSymbol target = obj.getGroup();
        validateGroupSupportsUpdate(target);
        validateMultisourceInsert(obj.getGroup());
    }

	private void validateMultisourceInsert(GroupSymbol group) {
		try {
			if (getMetadata().isMultiSource(getMetadata().getModelID(group.getMetadataID()))) {
				handleValidationError(Messages.getString(Messages.ValidationVisitor.multisource_insert, group), group);
			}
        } catch (Exception e) {
			handleException(e);
		}
	}

    @Override
    public void visit(Function obj) {
    	if(IFunctionLibrary.FunctionName.LOOKUP.equalsIgnoreCase(obj.getName())) {
    		try {
				ResolverUtil.ResolvedLookup resolvedLookup = ResolverUtil.resolveLookup(obj, getMetadata());
				if(ValidationVisitor.isNonComparable(resolvedLookup.getKeyElement())) {
		            handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_lookup_key, resolvedLookup.getKeyElement(), getDataTypeManager().getDataTypeName(resolvedLookup.getKeyElement().getType())), resolvedLookup.getKeyElement());            
		        }
			} catch (Exception e) {
				handleException(e, obj);
			}
    	} else if (IFunctionLibrary.FunctionName.CONTEXT.equalsIgnoreCase(obj.getName())) {
            if(!isXML) {
                // can't use this pseudo-function in non-XML queries
                handleValidationError(Messages.getString(Messages.ValidationVisitor.The_context_function_cannot_be_used_in_a_non_XML_command), obj);
            } else {
                if (!(obj.getArg(0) instanceof ElementSymbol)){
                    handleValidationError(Messages.getString(Messages.ERR.ERR_015_004_0036), obj); 
                }
                
                for (Iterator<Function> functions = FunctionCollectorVisitor.getFunctions(obj.getArg(1), false).iterator(); functions.hasNext();) {
                    Function function = functions.next();
                    
                    if (IFunctionLibrary.FunctionName.CONTEXT.equalsIgnoreCase(function.getName())) {
                        handleValidationError(Messages.getString(Messages.ValidationVisitor.Context_function_nested), obj);
                    }
                }
            }
    	} else if (IFunctionLibrary.FunctionName.ROWLIMIT.equalsIgnoreCase(obj.getName()) ||
    	            IFunctionLibrary.FunctionName.ROWLIMITEXCEPTION.equalsIgnoreCase(obj.getName())) {
            if(isXML) {
                if (!(obj.getArg(0) instanceof ElementSymbol)) {
                    // Arg must be an element symbol
                    handleValidationError(Messages.getString(Messages.ValidationVisitor.rowlimit2), obj);
                }
            } else {
                // can't use this pseudo-function in non-XML queries
                handleValidationError(Messages.getString(Messages.ValidationVisitor.The_rowlimit_function_cannot_be_used_in_a_non_XML_command), obj);
            }
        } else if(obj.getName().equalsIgnoreCase(SourceSystemFunctions.XPATHVALUE)) {
	        // Validate the xpath value is valid
	        if(obj.getArgs()[1] instanceof Constant) {
	            Constant xpathConst = (Constant) obj.getArgs()[1];
                try {
                    XMLSystemFunctions.validateXpath((String)xpathConst.getValue());
                } catch(Exception e) {
                	handleValidationError(Messages.getString(Messages.QueryResolver.invalid_xpath, e.getMessage()), obj);
                }
	        }
        } else if(obj.getName().equalsIgnoreCase(SourceSystemFunctions.TO_BYTES) || obj.getName().equalsIgnoreCase(SourceSystemFunctions.TO_CHARS)) {
        	try {
        		FunctionMethods.getCharset((String)((Constant)obj.getArg(1)).getValue());
        	} catch (IllegalArgumentException e) {
        		handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_encoding, obj.getArg(1)), obj);
        	}
        } else if (obj.isAggregate()) {
        	handleValidationError(Messages.getString(Messages.ValidationVisitor.user_defined_aggregate_as_function, obj, obj.getName()), obj);
        } else if (IFunctionLibrary.FunctionName.JSONARRAY.equalsIgnoreCase(obj.getName())) {
        	Expression[] args = obj.getArgs();
        	for (Expression expression : args) {
        		validateJSONValue(obj, expression);
			}
        }
    }

    // ############### Visitor methods for stored procedure lang objects ##################

    public void visit(AssignmentStatement obj) {
    	
    	ElementSymbol variable = obj.getVariable();

    	validateAssignment(obj, variable);
    }
    
    @Override
    public void visit(CommandStatement obj) {
        if (getTeiidVersion().isLessThan(Version.TEIID_8_0))
            visit7(obj);
        else
            visit8(obj);
    }
    
    private void visit7(CommandStatement obj) {
    	if (obj.getCommand() instanceof StoredProcedure) {
    		StoredProcedure proc = (StoredProcedure)obj.getCommand();
    		for (SPParameter param : proc.getParameters()) {
				if ((param.getParameterType() == SPParameter.RETURN_VALUE 
						|| param.getParameterType() == SPParameter.OUT) && param.getExpression() instanceof ElementSymbol) {
					validateAssignment(obj, (ElementSymbol)param.getExpression());
				}
			}
    	}
    }
    
    @Override
    public void visit(StoredProcedure obj) {
		for (SPParameter param : obj.getInputParameters()) {
			try {
                if (!getMetadata().elementSupports(param.getMetadataID(), SupportConstants.Element.NULL) && EvaluatableVisitor.isFullyEvaluatable(param.getExpression(), true)) {
                    try {
	                    // If nextValue is an expression, evaluate it before checking for null
	                    Object evaluatedValue = Evaluator.assess(param.getExpression());
	                    if(evaluatedValue == null) {
	                        handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0055, param.getParameterSymbol()), param.getParameterSymbol());
	                    } else if (evaluatedValue instanceof ArrayImpl && getMetadata().isVariadic(param.getMetadataID())) {
	            			ArrayImpl av = (ArrayImpl)evaluatedValue;
	            			for (Object o : av.getValues()) {
	            				if (o == null) {
	            					handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0055, param.getParameterSymbol()), param.getParameterSymbol());
	            				}
	            			}
	            		}
	                } catch(Exception e) {
	                    //ignore for now, we don't have the context which could be the problem
	                }
	            }
            } catch (Exception e) {
            	handleException(e);
            }
		}
    }

	private void validateAssignment(LanguageObject obj,
			ElementSymbol variable) {
		String groupName = variable.getGroupSymbol().getCanonicalName();
		//This will actually get detected by the resolver, since we inject an automatic declaration.
    	if(groupName.equals(ProcedureReservedWords.CHANGING) || groupName.equals(ProcedureReservedWords.INPUTS)) {
			handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0012, ProcedureReservedWords.INPUTS, ProcedureReservedWords.CHANGING), obj);
		}
	}
    
    @Override
    public void visit(ScalarSubquery obj) {
    	validateSubquery(obj);
        Collection<Expression> projSymbols = obj.getCommand().getProjectedSymbols();

        //Scalar subquery should have one projected symbol (query with one expression
        //in SELECT or stored procedure execution that returns a single value).
        if(projSymbols.size() != 1) {
        	handleValidationError(Messages.getString(Messages.ERR.ERR_015_008_0032, obj.getCommand()), obj.getCommand());
        }
    }

    /**
     * Validate that the command assigns a value to the ROWS_UPDATED variable 
     * @param obj
     * @since 4.2
     */
    @Removed(Version.TEIID_8_0)
    protected void validateContainsRowsUpdatedVariable(CreateUpdateProcedureCommand obj) {
        final Collection<ElementSymbol> assignVars = new ArrayList<ElementSymbol>();
       // Use visitor to find assignment statements
        LanguageVisitor visitor = new LanguageVisitor(getTeiidVersion()) {
            @Override
            public void visit(AssignmentStatement stmt) {
                assignVars.add(stmt.getVariable());
            }
        };
        PreOrderNavigator.doVisit(obj, visitor);
        boolean foundVar = false;
        for (ElementSymbol variable : assignVars) {
            if(variable.getShortName().equalsIgnoreCase(ProcedureReservedWords.ROWS_UPDATED)) {
                foundVar = true;
                break;
            }
        }
        if(!foundVar) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0016, ProcedureReservedWords.ROWS_UPDATED), obj);
        }
    }

    @Override
	@Removed(Version.TEIID_8_0)
    public void visit(CreateUpdateProcedureCommand obj) {
        if(!obj.isUpdateProcedure()){
            
            //check that the procedure does not contain references to itself
            if (GroupCollectorVisitor.getGroups(obj,true).contains(obj.getVirtualGroup())) {
                handleValidationError(Messages.getString(Messages.ValidationVisitor.Procedure_has_group_self_reference),obj);
            }
            
            return;
        }

        // set the state to validate this procedure
        this.createProc = obj;
        validateContainsRowsUpdatedVariable(obj);
    }

    @Override
    public void visit(CreateProcedureCommand obj) {
        //check that the procedure does not contain references to itself
    	if (obj.getUpdateType() == ICommand.TYPE_UNKNOWN) {
	        if (GroupCollectorVisitor.getGroups(obj,true).contains(obj.getVirtualGroup())) {
	        	handleValidationError(Messages.getString(Messages.ValidationVisitor.Procedure_has_group_self_reference),obj);
	        }
	        if (obj.getResultSetColumns() != null) {
	        	//some unit tests bypass setting the columns
		        this.createProc = obj;
	        }
    	}
    }

    private boolean isUpdateProcedure() {
        if (this.createProc == null)
            return false;

        if (!(this.createProc instanceof CreateUpdateProcedureCommand))
            return false;

        return ((CreateUpdateProcedureCommand) this.createProc).isUpdateProcedure();
    }

    @Override
    @Removed(Version.TEIID_8_0)
    public void visit(HasCriteria obj) {
        if (! isUpdateProcedure()) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0019), obj);
        }
    }
    
    @Override
    @Removed(Version.TEIID_8_0)
    public void visit(TranslateCriteria obj) {
        if (! isUpdateProcedure()) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0019), obj);
        }
        if(obj.hasTranslations()) {
            Collection selectElmnts = null;
            if(obj.getSelector().hasElements()) {
                selectElmnts = obj.getSelector().getElements();
            }
            Iterator critIter = obj.getTranslations().iterator();
            while(critIter.hasNext()) {
                CompareCriteria transCrit = (CompareCriteria) critIter.next();
                Collection<ElementSymbol> leftElmnts = ElementCollectorVisitor.getElements(transCrit.getLeftExpression(), true);
                // there is always only one element
                ElementSymbol leftExpr = leftElmnts.iterator().next();

                if(selectElmnts != null && !selectElmnts.contains(leftExpr)) {
                    handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0021), leftExpr);
                }
            }
        }

        // additional validation checks
        validateTranslateCriteria(obj);
    }

    @Override
    public void visit(CompoundCriteria obj) {
        // Validate use of 'rowlimit' or 'rowlimitexception' pseudo-function - each occurrence must be in a single
        // CompareCriteria which is entirely it's own conjunct (not OR'ed with anything else)
        if (isXML) {
            // Collect all occurrances of rowlimit and rowlimitexception functions
            List<Function> rowLimitFunctions = new ArrayList<Function>();
            FunctionCollectorVisitor visitor = new FunctionCollectorVisitor(getTeiidVersion(), rowLimitFunctions, IFunctionLibrary.FunctionName.ROWLIMIT.text());
            PreOrderNavigator.doVisit(obj, visitor); 
            visitor = new FunctionCollectorVisitor(getTeiidVersion(), rowLimitFunctions, IFunctionLibrary.FunctionName.ROWLIMITEXCEPTION.text());
            PreOrderNavigator.doVisit(obj, visitor);
            final int functionCount = rowLimitFunctions.size();
            if (functionCount > 0) {
                
                // Verify each use of rowlimit function is in a compare criteria that is 
                // entirely it's own conjunct
                Iterator<Criteria> conjunctIter = Criteria.separateCriteriaByAnd(obj).iterator();            
                
                int i = 0;
                while (conjunctIter.hasNext() && i<functionCount ) {
                    Object conjunct = conjunctIter.next();
                    if (conjunct instanceof CompareCriteria) {
                        CompareCriteria crit = (CompareCriteria)conjunct;
                        if ((rowLimitFunctions.contains(crit.getLeftExpression()) && !rowLimitFunctions.contains(crit.getRightExpression())) || 
                            (rowLimitFunctions.contains(crit.getRightExpression()) && !rowLimitFunctions.contains(crit.getLeftExpression()))) {
                        	i++;
                        }
                    }
                }
                if (i<functionCount) {
                    handleValidationError(Messages.getString(Messages.ValidationVisitor.rowlimit3), obj);
                }
            }
        }
        
    }

    // ######################### Validation methods #########################

    /**
     * A valid translated expression is not an <code>AggregateSymbol</code> and
     * does not include elements not present on the groups of the command using
     * the translated criteria.
     */
    @Removed(Version.TEIID_8_0)
    protected void validateTranslateCriteria(TranslateCriteria obj) {
        if(this.currentCommand == null) {
            return;
        }
        if (! isUpdateProcedure()) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0019), obj);
        }

        CreateUpdateProcedureCommand updateProc = (CreateUpdateProcedureCommand) this.createProc;
        Map<ElementSymbol, Expression> symbolMap = updateProc.getSymbolMap();
        Command userCommand = updateProc.getUserCommand();
        // modeler validation
        if(userCommand == null) {
            return;
        }
        Criteria userCrit = null;
        int userCmdType = userCommand.getType();
        switch(userCmdType) {
            case ICommand.TYPE_DELETE:
                userCrit = ((Delete)userCommand).getCriteria();
                break;
            case ICommand.TYPE_UPDATE:
                userCrit = ((Update)userCommand).getCriteria();
                break;
            default:
                break;
        }
        // nothing to validate if there is no user criteria
        if(userCrit == null) {
            return;
        }

        Collection<ElementSymbol> transleElmnts = ElementCollectorVisitor.getElements(obj, true);
        Collection<GroupSymbol> groups = GroupCollectorVisitor.getGroups(this.currentCommand, true);
        Operator selectType = obj.getSelector().getSelectorType();

        for (Criteria predCrit : Criteria.separateCriteriaByAnd(userCrit)) {
            if(selectType != Operator.NO_TYPE) {
                if(predCrit instanceof CompareCriteria) {
                    CompareCriteria ccCrit = (CompareCriteria) predCrit;
                    if(selectType.getIndex() != ccCrit.getOperator()) {
                        continue;
                    }
                } else if(predCrit instanceof MatchCriteria) {
                    if(selectType != Operator.LIKE) {
                        continue;
                    }
                } else if(predCrit instanceof IsNullCriteria) {
                    if(selectType != Operator.IS_NULL) {
                        continue;
                    }
                } else if(predCrit instanceof SetCriteria) {
                    if(selectType != Operator.IN) {
                        continue;
                    }
                } else if(predCrit instanceof BetweenCriteria) {
                    if(selectType != Operator.BETWEEN) {
                        continue;
                    }
                }
            }
            Iterator<ElementSymbol> critEmlntIter = ElementCollectorVisitor.getElements(predCrit, true).iterator();
            // collect all elements elements on the criteria map to
            while(critEmlntIter.hasNext()) {
                ElementSymbol criteriaElement = critEmlntIter.next();
                if(transleElmnts.contains(criteriaElement)) {
                    Expression mappedExpr = symbolMap.get(criteriaElement);
                    if(mappedExpr instanceof AggregateSymbol) {
                        handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0022, criteriaElement), criteriaElement);
                    }

                    if (!groups.containsAll(GroupsUsedByElementsVisitor.getGroups(mappedExpr))) {
                        handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0023, criteriaElement), criteriaElement);
                    }
                }
            }
        }
    }

    protected void validateSelectElements(Select obj) {
    	if(isXML) {
    		return;
    	}

        Collection<ElementSymbol> elements = ElementCollectorVisitor.getElements(obj, true);
        
        Collection<ElementSymbol> cantSelect = validateElementsSupport(
            elements,
            SupportConstants.Element.SELECT );

		if(cantSelect != null) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0024, cantSelect), cantSelect);
		}
    }

    protected void validateHasProjectedSymbols(Command obj) {
        if(obj.getProjectedSymbols().size() == 0) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0025), obj);
        }
    }

    /**
     * Validate that no elements of type OBJECT are in a SELECT DISTINCT or
     * and ORDER BY.
     * @param symbols List of SingleElementSymbol
     */
    protected void validateSortable(List<? extends Expression> symbols) {
    	for (Expression expression : symbols) {
            validateSortable(expression);
        }
    }

	private void validateSortable(Expression symbol) {
		if (isNonComparable(symbol)) {
		    handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0026, symbol, getDataTypeManager().getDataTypeName(symbol.getType())), symbol);
		}
	}

    /**
     * @param symbol
     * @return whether symbol type is non-comparable
     */
    public static boolean isNonComparable(Expression symbol) {
        DataTypeManagerService dataTypeManager = DataTypeManagerService.getInstance(symbol.getTeiidVersion());
        return dataTypeManager.isNonComparable(dataTypeManager.getDataTypeName(symbol.getType()));
    }

	/**
	 * This method can be used to validate Update commands cannot be
	 * executed against XML documents.
	 */
    protected void validateNoXMLUpdates(Command obj) {
     	if(isXMLCommand(obj)) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0029), obj);
     	}
    }

	/**
	 * This method can be used to validate commands used in the stored
	 * procedure languge cannot be executed against XML documents.
	 */
    protected void validateNoXMLProcedures(Command obj) {
     	if(isXMLCommand(obj)) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0030), obj);
     	}
    }

    private void validateXMLQuery(Query obj) {
        if(obj.getGroupBy() != null) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0031), obj);
        }
        if(obj.getHaving() != null) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0032), obj);
        }
        if(obj.getLimit() != null) {
            handleValidationError(Messages.getString(Messages.ValidationVisitor.limit_not_valid_for_xml), obj);
        }
        if (obj.getOrderBy() != null) {
        	OrderBy orderBy = obj.getOrderBy();
        	for (OrderByItem item : orderBy.getOrderByItems()) {
				if (!(item.getSymbol() instanceof ElementSymbol)) {
					handleValidationError(Messages.getString(Messages.ValidationVisitor.orderby_expression_xml), obj);
				}
			}
         }
    }
    
    protected void validateGroupSupportsUpdate(GroupSymbol groupSymbol) {
    	try {
	    	if(! getMetadata().groupSupports(groupSymbol.getMetadataID(), SupportConstants.Group.UPDATE)) {
	            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0033, 
	                                                     SQLStringVisitor.getSQLString(groupSymbol)), groupSymbol);
	        }
	    } catch (Exception e) {
	        handleException(e, groupSymbol);
	    }
    }
    
    protected void validateSetQuery(SetQuery query) {
        // Walk through sub queries - validate each one separately and
        // also check the columns of each for comparability
        for (QueryCommand subQuery : query.getQueryCommands()) {
            if(isXMLCommand(subQuery)) {
                handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0034), query);
            }
            if (subQuery instanceof Query && ((Query)subQuery).getInto() != null) {
            	handleValidationError(Messages.getString(Messages.ValidationVisitor.union_insert), query);
            }
        }
        
        if (!query.isAll() || query.getOperation() == Operation.EXCEPT || query.getOperation() == Operation.INTERSECT) {
            validateSortable(query.getProjectedSymbols());
        }
        
        if (query.isAll() && (query.getOperation() == Operation.EXCEPT || query.getOperation() == Operation.INTERSECT)) {
            handleValidationError(Messages.getString(Messages.ValidationVisitor.excpet_intersect_all), query);
        }
    }

    private void validateAggregates(Query query) {
        Select select = query.getSelect();
        GroupBy groupBy = query.getGroupBy();
        Criteria having = query.getHaving();
        validateNoAggsInClause(groupBy);
        List<GroupSymbol> correlationGroups = null;
        validateNoAggsInClause(query.getCriteria());
        if (query.getFrom() == null) {
        	validateNoAggsInClause(select);
        	validateNoAggsInClause(query.getOrderBy());
        } else {
        	validateNoAggsInClause(query.getFrom());
        	correlationGroups = query.getFrom().getGroups();
        }
        
        Set<Expression> groupSymbols = null;
        boolean hasAgg = false;
        if (groupBy != null) {
            groupSymbols = new HashSet<Expression>(groupBy.getSymbols());
            hasAgg = true;
        }
        LinkedHashSet<Expression> invalid = new LinkedHashSet<Expression>();
        LinkedHashSet<Expression> invalidWindowFunctions = new LinkedHashSet<Expression>();
        LinkedList<AggregateSymbol> aggs = new LinkedList<AggregateSymbol>();
        if (having != null) {
            validateCorrelatedReferences(query, correlationGroups, groupSymbols, having, invalid);
        	AggregateSymbolCollectorVisitor.getAggregates(having, aggs, invalid, null, invalidWindowFunctions, groupSymbols);
        	hasAgg = true;
        }
        if (isTeiid810OrGreater()) {
            if (groupBy != null && query.getOrderBy() != null) {
                Set<Expression> exanded = new HashSet<Expression>(groupSymbols);
                exanded.addAll(select.getProjectedSymbols());
                for (OrderByItem item : query.getOrderBy().getOrderByItems()) {
                    if (item.isUnrelated()) {
                        AggregateSymbolCollectorVisitor.getAggregates(item.getSymbol(), aggs, invalid, null, invalidWindowFunctions, exanded);
                    }
                }
            }
        }
        for (Expression symbol : select.getProjectedSymbols()) {
        	if (hasAgg || !aggs.isEmpty()) {
        		validateCorrelatedReferences(query, correlationGroups, groupSymbols, symbol, invalid);
        	}
        	AggregateSymbolCollectorVisitor.getAggregates(symbol, aggs, invalid, null, null, groupSymbols);                                            
        }
        if ((!aggs.isEmpty() || hasAgg) && !invalid.isEmpty()) {
    		handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0037, invalid), invalid);
        }
        if (!invalidWindowFunctions.isEmpty()) {
        	handleValidationError(Messages.getString(Messages.TeiidParser.window_only_top_level, invalidWindowFunctions), invalidWindowFunctions);
        }
    }

    /**
     * This validation is more convoluted than needed since it is being run before rewrite/planning.
     * Ideally we would already have correlated references set on the subqueries.
     */
	private void validateCorrelatedReferences(Query query,
			final List<GroupSymbol> correlationGroups, final Set<Expression> groupingSymbols, LanguageObject object, LinkedHashSet<Expression> invalid) {
		if (query.getFrom() == null) {
			return;
		}
		ElementCollectorVisitor ecv = new ElementCollectorVisitor(getTeiidVersion(), invalid) {
			@Override
            public void visit(ElementSymbol obj) {
				if (obj.isExternalReference() && correlationGroups.contains(obj.getGroupSymbol())
						 && (groupingSymbols == null || !groupingSymbols.contains(obj))) {
					super.visit(obj);
				}
			}
		};
		AggregateStopNavigator asn = new AggregateStopNavigator(ecv, groupingSymbols);
		object.acceptVisitor(asn);
	}

	private void validateNoAggsInClause(LanguageObject clause) {
		if (clause == null) {
        	return;
        }
		LinkedHashSet<Expression> aggs = new LinkedHashSet<Expression>();
		AggregateSymbolCollectorVisitor.getAggregates(clause, aggs, null, null, aggs, null);
		if (!aggs.isEmpty()) {
			handleValidationError(Messages.getString(Messages.TeiidParser.Aggregate_only_top_level, aggs), aggs);
		}
	}
    
    protected void validateInsert(Insert obj) {
        Collection<ElementSymbol> vars = obj.getVariables();
        Iterator<ElementSymbol> varIter = vars.iterator();
        Collection values = obj.getValues();
        Iterator valIter = values.iterator();
        GroupSymbol insertGroup = obj.getGroup();
        try {
            boolean multiSource = getMetadata().isMultiSource(getMetadata().getModelID(insertGroup.getMetadataID()));
            // Validate that all elements in variable list are updatable
        	for (ElementSymbol insertElem : vars) {
                if(! getMetadata().elementSupports(insertElem.getMetadataID(), SupportConstants.Element.UPDATE)) {
                    handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0052, insertElem), insertElem);
                }
                if (multiSource && getMetadata().isMultiSourceElement(insertElem.getMetadataID())) {
                	multiSource = false;
                }
            }
        	if (multiSource) {
        		validateMultisourceInsert(insertGroup);
        	}

            // Get elements in the group.
    		Collection<ElementSymbol> insertElmnts = new LinkedList<ElementSymbol>(ResolverUtil.resolveElementsInGroup(insertGroup, getMetadata()));

    		// remove all elements specified in insert to get the ignored elements
    		insertElmnts.removeAll(vars);

    		for (ElementSymbol nextElmnt : insertElmnts) {
				if(!getMetadata().elementSupports(nextElmnt.getMetadataID(), SupportConstants.Element.DEFAULT_VALUE) &&
					!getMetadata().elementSupports(nextElmnt.getMetadataID(), SupportConstants.Element.NULL) &&
                    !getMetadata().elementSupports(nextElmnt.getMetadataID(), SupportConstants.Element.AUTO_INCREMENT)) {
		                handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0053, new Object[] {insertGroup, nextElmnt}), nextElmnt);
				}
			}

            //check to see if the elements support nulls in metadata,
            // if any of the value present in the insert are null
            while(valIter.hasNext() && varIter.hasNext()) {
                Expression nextValue = (Expression) valIter.next();
                ElementSymbol nextVar = varIter.next();
                if (EvaluatableVisitor.isFullyEvaluatable(nextValue, true)) {
                    try {
                        // If nextValue is an expression, evaluate it before checking for null
                        Object evaluatedValue = Evaluator.assess(nextValue);
                        if(evaluatedValue == null && ! getMetadata().elementSupports(nextVar.getMetadataID(), SupportConstants.Element.NULL)) {
                            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0055, nextVar), nextVar);
                        }
                    } catch(Exception e) {
                        //ignore for now, we don't have the context which could be the problem
                    }
                }
            }// end of while
        } catch(Exception e) {
            handleException(e, obj);
        } 
    }
    
    protected void validateSetClauseList(SetClauseList list) {
    	Set<ElementSymbol> dups = new HashSet<ElementSymbol>();
	    HashSet<ElementSymbol> changeVars = new HashSet<ElementSymbol>();
	    for (SetClause clause : list.getClauses()) {
	    	ElementSymbol elementID = clause.getSymbol();
	        if (!changeVars.add(elementID)) {
	        	dups.add(elementID);
	        }
		}
	    if(!dups.isEmpty()) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0062, dups), dups);
	    }
    }
    
    protected void validateUpdate(Update update) {
        try {
            UpdateInfo info = update.getUpdateInfo();

            // list of elements that are being updated
		    for (SetClause entry : update.getChangeList().getClauses()) {
        	    ElementSymbol elementID = entry.getSymbol();

                // Check that left side element is updatable
                if(! getMetadata().elementSupports(elementID.getMetadataID(), SupportConstants.Element.UPDATE)) {
                    handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0059, elementID), elementID);
                }
                
                Object metadataID = elementID.getMetadataID();
                if (getMetadata().isMultiSourceElement(metadataID)){
                	handleValidationError(Messages.getString(Messages.ValidationVisitor.multi_source_update_not_allowed, elementID), elementID);
                }

			    // Check that right expression is a constant and is non-null
                Expression value = entry.getValue();    
                if (EvaluatableVisitor.isFullyEvaluatable(value, true)) {
                    try {
                        Constant c = getTeiidParser().createASTNode(ASTNodes.CONSTANT);
                        c.setValue(Evaluator.assess(value));
                        value = c;
                    } catch (Exception err) {
                    }
                }
                
                if(value instanceof Constant) {
    			    // If value is null, check that element supports this as a nullable column
                    if(((Constant)value).getValue() == null && ! getMetadata().elementSupports(elementID.getMetadataID(), SupportConstants.Element.NULL)) {
                        handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0060, SQLStringVisitor.getSQLString(elementID)), elementID);
                    }// end of if
                } 
		    }
            if (info != null && info.isInherentUpdate()) {
            	validateUpdate(update, ICommand.TYPE_UPDATE, info);
            	Set<ElementSymbol> updateCols = update.getChangeList().getClauseMap().keySet();
            	if (!info.hasValidUpdateMapping(updateCols)) {
            		handleValidationError(Messages.gs(Messages.TEIID.TEIID30376, updateCols), update);
            	}
            }
        } catch(Exception e) {
            handleException(e, update);
        }
        
        validateSetClauseList(update.getChangeList());
    }

	private void validateUpdate(TargetedCommand update, int type, UpdateInfo info) {
		String error = ProcedureContainerResolver.validateUpdateInfo(update.getGroup(), type, info);
		if (error != null) {
			handleValidationError(error, update.getGroup());
		}
	}
    
    /**
     * Validates SELECT INTO queries.
     * @param query
     * @since 4.2
     */
    protected void validateSelectInto(Query query) {
        List<Expression> symbols = query.getSelect().getProjectedSymbols();
        GroupSymbol intoGroup = query.getInto().getGroup();
        validateInto(query, symbols, intoGroup);
    }

    private void validateInto(LanguageObject query,
                                List<Expression> symbols,
                                GroupSymbol intoGroup) {
        try {
            List elementIDs = getMetadata().getElementIDsInGroupID(intoGroup.getMetadataID());
            
            // Check if there are too many elements in the SELECT clause
            if (symbols.size() != elementIDs.size()) {
                handleValidationError(Messages.getString(Messages.ValidationVisitor.select_into_wrong_elements, new Object[] {new Integer(elementIDs.size()), new Integer(symbols.size())}), query);
                return;
            }

            for (int symbolNum = 0; symbolNum < symbols.size(); symbolNum++) {
                Expression symbol = symbols.get(symbolNum);
                Object elementID = elementIDs.get(symbolNum);
                // Check if supports updates
                if (!getMetadata().elementSupports(elementID, SupportConstants.Element.UPDATE)) {
                    handleValidationError(Messages.getString(Messages.ValidationVisitor.element_updates_not_allowed, getMetadata().getFullName(elementID)), intoGroup);
                }

                Class<?> symbolType = symbol.getType();
                String symbolTypeName = dataTypeManager.getDataTypeName(symbolType);
                String targetTypeName = getMetadata().getElementType(elementID);
                if (symbolTypeName.equals(targetTypeName)) {
                    continue;
                }
                if (!dataTypeManager.isImplicitConversion(symbolTypeName, targetTypeName)) { // If there's no implicit conversion between the two
                    Object[] params = new Object [] {symbolTypeName, targetTypeName, new Integer(symbolNum + 1), query};
                    handleValidationError(Messages.getString(Messages.ValidationVisitor.select_into_no_implicit_conversion, params), query);
                    continue;
                }
            }
        } catch (Exception e) {
            handleException(e, query);
        } 
    }
    
    private void validateRowLimitFunctionNotInInvalidCriteria(Criteria obj) {
        // Collect all occurrances of rowlimit and rowlimitexception functions
        List<Function> rowLimitFunctions = new ArrayList<Function>();
        FunctionCollectorVisitor visitor = new FunctionCollectorVisitor(getTeiidVersion(), rowLimitFunctions, IFunctionLibrary.FunctionName.ROWLIMIT.text());
        PreOrderNavigator.doVisit(obj, visitor);      
        visitor = new FunctionCollectorVisitor(getTeiidVersion(), rowLimitFunctions, IFunctionLibrary.FunctionName.ROWLIMITEXCEPTION.text());
        PreOrderNavigator.doVisit(obj, visitor); 
        if (rowLimitFunctions.size() > 0) {
            handleValidationError(Messages.getString(Messages.ValidationVisitor.rowlimit3), obj);
        }
    }
    
    /** 
     * @see LanguageVisitor#visit(org.teiid.query.sql.lang.BetweenCriteria)
     * @since 4.3
     */
    @Override
    public void visit(BetweenCriteria obj) {
    	if (isNonComparable(obj.getExpression())) {
    		handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0027, obj, getDataTypeManager().getDataTypeName(obj.getExpression().getType())), obj);   		
    	}
        this.validateRowLimitFunctionNotInInvalidCriteria(obj);
    }

    /** 
     * @see LanguageVisitor#visit(org.teiid.query.sql.lang.IsNullCriteria)
     * @since 4.3
     */
    @Override
    public void visit(IsNullCriteria obj) {
        this.validateRowLimitFunctionNotInInvalidCriteria(obj);
    }

    @Override
    @Since(Version.TEIID_8_12_4)
    public void visit(IsDistinctCriteria isDistinctCriteria) {
        try {
            IQueryMetadataInterface metadata = getMetadata();
            if (!metadata.isScalarGroup(isDistinctCriteria.getLeftRowValue().getMetadataID())) {
                handleValidationError(Messages.gs(Messages.TEIID.TEIID31171, isDistinctCriteria.getLeftRowValue()), isDistinctCriteria.getLeftRowValue()); 
            }
            if (!metadata.isScalarGroup(isDistinctCriteria.getRightRowValue().getMetadataID())) {
                handleValidationError(Messages.gs(Messages.TEIID.TEIID31171, isDistinctCriteria.getRightRowValue()), isDistinctCriteria.getRightRowValue()); 
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    /** 
     * @see LanguageVisitor#visit(org.teiid.query.sql.lang.MatchCriteria)
     * @since 4.3
     */
    @Override
    public void visit(MatchCriteria obj) {
        this.validateRowLimitFunctionNotInInvalidCriteria(obj);
    }

    /** 
     * @see LanguageVisitor#visit(org.teiid.query.sql.lang.NotCriteria)
     * @since 4.3
     */
    @Override
    public void visit(NotCriteria obj) {
        this.validateRowLimitFunctionNotInInvalidCriteria(obj);
    }

    /** 
     * @see LanguageVisitor#visit(org.teiid.query.sql.lang.SetCriteria)
     * @since 4.3
     */
    @Override
    public void visit(SetCriteria obj) {
    	if (isNonComparable(obj.getExpression())) {
    		handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0027, obj, getDataTypeManager().getDataTypeName(obj.getExpression().getType())), obj);	
    	}
        this.validateRowLimitFunctionNotInInvalidCriteria(obj);
    }

    /** 
     * @see LanguageVisitor#visit(org.teiid.query.sql.lang.SubqueryCompareCriteria)
     * @since 4.3
     */
    @Override
    public void visit(SubqueryCompareCriteria obj) {
    	validateSubquery(obj);
    	if (isNonComparable(obj.getLeftExpression())) {
    		handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0027, obj, getDataTypeManager().getDataTypeName(obj.getLeftExpression().getType())), obj);
    	}
        this.validateRowLimitFunctionNotInInvalidCriteria(obj);
    }
    
    @Override
    public void visit(Option obj) {
        List<String> dep = obj.getDependentGroups();
        List<String> notDep = obj.getNotDependentGroups();
        if (dep != null && !dep.isEmpty()
            && notDep != null && !notDep.isEmpty()) {
            String groupName = null;
            String notDepGroup = null;
            for (Iterator<String> i = dep.iterator(); i.hasNext();) {
                groupName = i.next();
                for (Iterator<String> j = notDep.iterator(); j.hasNext();) {
                    notDepGroup = j.next();
                    if (notDepGroup.equalsIgnoreCase(groupName)) {
                        handleValidationError(Messages.getString(Messages.ValidationVisitor.group_in_both_dep, groupName), obj);
                        return;
                    }
                }
            }
        }
    }
    
    /** 
     * @see LanguageVisitor#visit(org.teiid.query.sql.lang.DynamicCommand)
     */
    @Override
    public void visit(DynamicCommand obj) {
        if (obj.getIntoGroup() != null) {
            validateInto(obj, obj.getAsColumns(), obj.getIntoGroup());
        }
        if (obj.getUsing() != null) {
        	validateSetClauseList(obj.getUsing());
        }
    }
    
    @Override
    public void visit(Create obj) {
    	if (!obj.getPrimaryKey().isEmpty()) {
    		validateSortable(obj.getPrimaryKey());
    	}
    	if (obj.getTableMetadata() != null) {
    		Table t = obj.getTableMetadata();
    		if (!t.getForeignKeys().isEmpty()) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.temp_fk, obj.getTable()), obj);
    		}
    	}
    }
    
    /** 
     * @see LanguageVisitor#visit(org.teiid.query.sql.lang.Drop)
     */
    @Override
    public void visit(Drop drop) {
        if (!drop.getTable().isTempTable()) {
            handleValidationError(Messages.getString(Messages.ValidationVisitor.drop_of_nontemptable, drop.getTable()), drop);
        }
        try {
			if (getMetadata().isVirtualGroup(drop.getTable().getMetadataID())) {
				handleValidationError(Messages.getString(Messages.ValidationVisitor.drop_of_globaltemptable, drop.getTable()), drop);
			}
		} catch (Exception e) {
			handleException(e);
		}
    }
    
    @Override
    public void visit(CompareCriteria obj) {
    	if (isNonComparable(obj.getLeftExpression())) {
    		handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0027, obj, getDataTypeManager().getDataTypeName(obj.getLeftExpression().getType())), obj);  		
    	}
    	
        // Validate use of 'rowlimit' and 'rowlimitexception' pseudo-functions - they cannot be nested within another
        // function, and their operands must be a nonnegative integers

        // Collect all occurrences of rowlimit function
        List rowLimitFunctions = new ArrayList();
        FunctionCollectorVisitor visitor = new FunctionCollectorVisitor(getTeiidVersion(), rowLimitFunctions, IFunctionLibrary.FunctionName.ROWLIMIT.text());
        PreOrderNavigator.doVisit(obj, visitor);   
        visitor = new FunctionCollectorVisitor(getTeiidVersion(), rowLimitFunctions, IFunctionLibrary.FunctionName.ROWLIMITEXCEPTION.text());
        PreOrderNavigator.doVisit(obj, visitor);            
        final int functionCount = rowLimitFunctions.size();
        if (functionCount > 0) {
            Function function = null;
            Expression expr = null;
            if (obj.getLeftExpression() instanceof Function) {
                Function leftExpr = (Function)obj.getLeftExpression();

                if (IFunctionLibrary.FunctionName.ROWLIMIT.equalsIgnoreCase(leftExpr.getName()) ||
                    IFunctionLibrary.FunctionName.ROWLIMITEXCEPTION.equalsIgnoreCase(leftExpr.getName())) {
                    function = leftExpr;
                    expr = obj.getRightExpression();
                }
            } 
            if (function == null && obj.getRightExpression() instanceof Function) {
                Function rightExpr = (Function)obj.getRightExpression();
                
                if (IFunctionLibrary.FunctionName.ROWLIMIT.equalsIgnoreCase(rightExpr.getName()) ||
                IFunctionLibrary.FunctionName.ROWLIMITEXCEPTION.equalsIgnoreCase(rightExpr.getName())) {
                    function = rightExpr;
                    expr = obj.getLeftExpression();
                }
            }
            if (function == null) {
                // must be nested, which is invalid
                handleValidationError(Messages.getString(Messages.ValidationVisitor.rowlimit0), obj);
            } else {
                if (expr instanceof Constant) {
                    Constant constant = (Constant)expr;
                    if (constant.getValue() instanceof Integer) {
                        Integer integer = (Integer)constant.getValue();
                        if (integer.intValue() < 0) {
                            handleValidationError(Messages.getString(Messages.ValidationVisitor.rowlimit1), obj);
                        }
                    } else {
                        handleValidationError(Messages.getString(Messages.ValidationVisitor.rowlimit1), obj);
                    }
                } else if (expr instanceof Reference) {
                	((Reference)expr).setConstraint(new PositiveIntegerConstraint(Messages.ValidationVisitor.rowlimit1));
                } else {
                    handleValidationError(Messages.getString(Messages.ValidationVisitor.rowlimit1), obj);
                }
            }                 
        }
    }
    
    @Override
    public void visit(Limit obj) {
        validateLimitExpression(obj, obj.getOffset());
        validateLimitExpression(obj, obj.getRowLimit());
    }

	private void validateLimitExpression(Limit obj, Expression limitExpr) {
		if (limitExpr != null) {
	        if (limitExpr instanceof Constant) {
	            Integer limit = (Integer)((Constant)limitExpr).getValue();
	            if (limit.intValue() < 0) {
	                handleValidationError(Messages.getString(Messages.ValidationVisitor.badlimit2), obj);
	            }
	        } else if (limitExpr instanceof Reference) {
	        	((Reference)limitExpr).setConstraint(LIMIT_CONSTRAINT); 
	        } else if (!EvaluatableVisitor.willBecomeConstant(limitExpr)) {
	        	handleValidationError(Messages.getString(Messages.ValidationVisitor.badlimit1), obj);
	        }
        }
	}
    
    @Override
    public void visit(XMLForest obj) {
    	validateDerivedColumnNames(obj, obj.getArgs());
    	for (DerivedColumn dc : obj.getArgs()) {
			if (dc.getAlias() == null) {
				continue;
			}
			validateQName(obj, dc.getAlias());
			validateXMLContentTypes(dc.getExpression(), obj);
		}
    }
    
    @Override
	@Since(Version.TEIID_8_0)
    public void visit(JSONObject obj) {
    	for (DerivedColumn dc : obj.getArgs()) {
    		validateJSONValue(obj, dc.getExpression());
		}
    }
    
    @Override
    public void visit(WindowFunction windowFunction) {
    	AggregateSymbol.Type type = windowFunction.getFunction().getAggregateFunction();
    	switch (type) {
    	case RANK:
    	case DENSE_RANK:
    	case ROW_NUMBER:
    		if (windowFunction.getWindowSpecification().getOrderBy() == null) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.ranking_requires_order_by, windowFunction), windowFunction);
    		}
    		break;
    	case TEXTAGG:
    	case ARRAY_AGG:
    	case JSONARRAY_AGG:
    	case XMLAGG:
    	case STRING_AGG:
    		if (windowFunction.getWindowSpecification().getOrderBy() != null) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.window_order_by, windowFunction), windowFunction);
            }
    		break;
    	default:
    	    break;
    	}
    	validateNoSubqueriesOrOuterReferences(windowFunction);
        if (windowFunction.getFunction().getOrderBy() != null || (windowFunction.getFunction().isDistinct() && windowFunction.getWindowSpecification().getOrderBy() != null)) {
        	handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0042, new Object[] {windowFunction.getFunction(), windowFunction}), windowFunction);
        }
        if (windowFunction.getWindowSpecification().getPartition() != null) {
        	validateSortable(windowFunction.getWindowSpecification().getPartition());
        }
    }
    
    @Override
    public void visit(AggregateSymbol obj) {
    	if (!inQuery) {
    		handleValidationError(Messages.getString(Messages.TeiidParser.Aggregate_only_top_level, obj), obj);
    		return;
    	}
    	if (obj.getAggregateFunction() == AggregateSymbol.Type.USER_DEFINED) {
    		AggregateAttributes aa = obj.getFunctionDescriptor().getMethod().getAggregateAttributes();
    		if (!aa.allowsDistinct() && obj.isDistinct()) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.uda_not_allowed, "DISTINCT", obj), obj); //$NON-NLS-1$
    		}
    		if (!aa.allowsOrderBy() && obj.getOrderBy() != null) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.uda_not_allowed, "ORDER BY", obj), obj); //$NON-NLS-1$
    		}
    		if (aa.isAnalytic() && !obj.isWindowed()) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.uda_analytic, obj), obj);  
    		}
    	}
    	if (obj.getCondition() != null) {
    		Expression condition = obj.getCondition();
    		validateNoSubqueriesOrOuterReferences(condition);
    	}

        Expression aggExp = null;
        if (getTeiidVersion().isLessThan(Version.TEIID_8_0)) {
            aggExp = obj.getExpression();
            validateNoNestedAggs(aggExp, false);
        } else {
            Expression[] aggExps = obj.getArgs();
            for (Expression expression : aggExps) {
                boolean windowed = isLessThanTeiid8124() ? false : obj.isWindowed();
                validateNoNestedAggs(expression, windowed);
            }
            if (aggExps.length > 0)
                aggExp = aggExps[0];
        }

        validateNoNestedAggs(obj.getOrderBy(), false);
        validateNoNestedAggs(obj.getCondition(), false);
        
        // Verify data type of aggregate expression
        IAggregateSymbol.Type aggregateFunction = obj.getAggregateFunction();
        if((aggregateFunction == IAggregateSymbol.Type.SUM || aggregateFunction == IAggregateSymbol.Type.AVG) && obj.getType() == null) {
            handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0041, new Object[] {aggregateFunction, obj}), obj);
        } else if (obj.getType() != DataTypeManagerService.DefaultDataTypes.NULL.getTypeClass()) {
        	if (aggregateFunction == IAggregateSymbol.Type.XMLAGG && aggExp.getType() != DataTypeManagerService.DefaultDataTypes.XML.getTypeClass()) {
        		handleValidationError(Messages.getString(Messages.ValidationVisitor.non_xml, new Object[] {aggregateFunction, obj}), obj);
        	} else if (obj.isBoolean() && aggExp.getType() != DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass()) {
        		handleValidationError(Messages.getString(Messages.ValidationVisitor.non_boolean, new Object[] {aggregateFunction, obj}), obj);
        	} else if (aggregateFunction == IAggregateSymbol.Type.JSONARRAY_AGG) {
				validateJSONValue(obj, aggExp);
        	} else if (obj.getType() == null) {
                handleValidationError(Messages.getString(Messages.ValidationVisitor.aggregate_type, obj), obj); //$NON-NLS-1$
            }
        }
        if((obj.isDistinct() ||
            aggregateFunction == IAggregateSymbol.Type.MIN ||
            aggregateFunction == IAggregateSymbol.Type.MAX) &&
            dataTypeManager.isNonComparable(dataTypeManager.getDataTypeName(aggExp.getType()))) {
    		handleValidationError(Messages.getString(Messages.ValidationVisitor.non_comparable, new Object[] {aggregateFunction, obj}), obj);
        }
        if(obj.isEnhancedNumeric()) {
        	if (!Number.class.isAssignableFrom(aggExp.getType())) {
        		handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0041, new Object[] {aggregateFunction, obj}), obj);
        	}
        	if (obj.isDistinct()) {
        		handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_distinct, new Object[] {aggregateFunction, obj}), obj);
        	}
        }
        if (obj.isDistinct() && obj.getOrderBy() != null && isTeiid8124OrGreater()) {
            HashSet<Expression> args = new HashSet<Expression>(Arrays.asList(obj.getArgs()));
            for (OrderByItem item : obj.getOrderBy().getOrderByItems()) {
                if (!args.contains(item.getSymbol())) {
                    handleValidationError(Messages.getString(Messages.ValidationVisitor.distinct_orderby_agg, obj), obj); //$NON-NLS-1$
                    break;
                }
            }
        }
    	if (obj.getAggregateFunction() != IAggregateSymbol.Type.TEXTAGG) {
    		return;
    	}
    	TextLine tl = (TextLine)aggExp;
    	if (tl.isIncludeHeader()) {
    		validateDerivedColumnNames(obj, tl.getExpressions());
    	}
    	for (DerivedColumn dc : tl.getExpressions()) {
    	    if (isLessThanTeiid8124()) {
    	        validateXMLContentTypes(dc.getExpression(), obj);
    	        continue;
    	    }

			Expression expression = dc.getExpression();
            if (expression.getType() == DefaultDataTypes.OBJECT.getTypeClass()
                    || expression.getType() == null
                    || expression.getType().isArray()
                    || expression.getType() == DefaultDataTypes.VARBINARY.getTypeClass()
                    || expression.getType() == DefaultDataTypes.BLOB.getTypeClass()) {
                handleValidationError(Messages.getString(Messages.ValidationVisitor.text_content_type, expression), obj); //$NON-NLS-1$
            }
		}
    	validateTextOptions(obj, tl.getDelimiter(), tl.getQuote(), '\n');
    	if (tl.getEncoding() != null) {
    		try {
    			Charset.forName(tl.getEncoding());
    		} catch (IllegalArgumentException e) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_encoding, tl.getEncoding()), obj);
    		}
    	}
    }

	@Since(Version.TEIID_8_0)
	private void validateJSONValue(LanguageObject obj, Expression expr) {
		if (expr.getType() != DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass()
		    && !dataTypeManager.isTransformable(
		                                               expr.getType(),
		                                               DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass())) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_json_value, expr, obj), obj);
		}
	}

	private void validateNoSubqueriesOrOuterReferences(Expression expr) {
		if (!ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(expr).isEmpty()) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.filter_subquery, expr), expr);
		}
		for (ElementSymbol es : ElementCollectorVisitor.getElements(expr, false)) {
			if (es.isExternalReference()) {
				handleValidationError(Messages.getString(Messages.ValidationVisitor.filter_subquery, es), es);
			}
		}
	}
    
	private void validateNoNestedAggs(LanguageObject aggExp, boolean windowOnly) {
		// Check for any nested aggregates (which are not allowed)
        if(aggExp != null) {
        	HashSet<Expression> nestedAggs = new LinkedHashSet<Expression>();
        	AggregateSymbolCollectorVisitor.getAggregates(aggExp, windowOnly?null:nestedAggs, null, null, nestedAggs, null);
            if(!nestedAggs.isEmpty()) {
                handleValidationError(Messages.getString(Messages.ERR.ERR_015_012_0039, nestedAggs), nestedAggs);
            }
        }
	}
    
	private String[] validateQName(LanguageObject obj, String name) {
		try {
			return Name11Checker.getInstance().getQNameParts(name);
		} catch (QNameException e) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.xml_invalid_qname, name), obj);
		}
		return null;
	}

	private void validateDerivedColumnNames(LanguageObject obj, List<DerivedColumn> cols) {
		for (DerivedColumn dc : cols) {
    		if (dc.getAlias() == null && !(dc.getExpression() instanceof ElementSymbol)) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.expression_requires_name), obj);
        	} 
		}
	}
    
    @Override
    public void visit(XMLAttributes obj) {
    	validateDerivedColumnNames(obj, obj.getArgs());
    	for (DerivedColumn dc : obj.getArgs()) {
			if (dc.getAlias() == null) {
				continue;
			}
			if ("xmlns".equals(dc.getAlias())) { //$NON-NLS-1$
				handleValidationError(Messages.getString(Messages.ValidationVisitor.xml_attributes_reserved), obj);
			}
			String[] parts = validateQName(obj, dc.getAlias());
			if (parts == null) {
				continue;
			}
			if ("xmlns".equals(parts[0])) { //$NON-NLS-1$
				handleValidationError(Messages.getString(Messages.ValidationVisitor.xml_attributes_reserved, dc.getAlias()), obj);
			}
		}
    }
    
    @Override
    public void visit(XMLElement obj) {
    	for (Expression expression : obj.getContent()) {
    		validateXMLContentTypes(expression, obj);
    	}
    	validateQName(obj, obj.getName());
    }
    
    /**
     * @param expression
     * @param parent
     */
    public void validateXMLContentTypes(Expression expression, LanguageObject parent) {
        if (isTeiid8124OrGreater()) {
            if (expression.getType() == DefaultDataTypes.OBJECT.getTypeClass() || expression.getType() == null || expression.getType().isArray()) {
                handleValidationError(Messages.getString(Messages.ValidationVisitor.xml_content_type, expression), parent);
            }
            return;
        }

		if (expression.getType() == DefaultDataTypes.OBJECT.getTypeClass() || expression.getType() == DefaultDataTypes.BLOB.getTypeClass()) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.xml_content_type, expression), parent);
		}
    }
    
    @Override
    public void visit(QueryString obj) {
    	validateDerivedColumnNames(obj, obj.getArgs());
    }
    
    @Override
    public void visit(XMLTable obj) {
    	List<DerivedColumn> passing = obj.getPassing();
    	validatePassing(obj, obj.getXQueryExpression(), passing);
    	boolean hasOrdinal = false;
    	for (XMLColumn xc : obj.getColumns()) {
			if (!xc.isOrdinal()) {
				if (xc.getDefaultExpression() != null && !EvaluatableVisitor.isFullyEvaluatable(xc.getDefaultExpression(), false)) {
					handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_default, xc.getDefaultExpression()), obj);
				}
				continue;
			}
			if (hasOrdinal) {
				handleValidationError(Messages.getString(Messages.ValidationVisitor.one_ordinal), obj);
				break;
			}
			hasOrdinal = true;
		}
    }
    
    @Override
	@Since(Version.TEIID_8_0)
    public void visit(ObjectTable obj) {
    	List<DerivedColumn> passing = obj.getPassing();
    	TreeSet<String> names = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    	for (DerivedColumn dc : passing) {
    		if (dc.getAlias() == null) {
				handleValidationError(Messages.getString(Messages.ValidationVisitor.context_item_not_allowed), obj);
        	} else if (!names.add(dc.getAlias())) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.duplicate_passing, dc.getAlias()), obj);
        	}
		}
    	Compilable scriptCompiler = null;
    	try {
			ScriptEngine engine = this.getMetadata().getScriptEngine(obj.getScriptingLanguage());
			obj.setScriptEngine(engine);
			if (engine instanceof Compilable) {
				scriptCompiler = (Compilable)engine;
				engine.put(ScriptEngine.FILENAME, SQLConstants.NonReserved.OBJECTTABLE);
				obj.setCompiledScript(scriptCompiler.compile(obj.getRowScript()));
			}
		} catch (Exception e) {
			handleValidationError(e.getMessage(), obj);
		}

    	for (ObjectColumn xc : obj.getColumns()) {
    		if (scriptCompiler != null) {
    			try {
					xc.setCompiledScript(scriptCompiler.compile(xc.getPath()));
				} catch (ScriptException e) {
					handleValidationError(Messages.gs(Messages.TEIID.TEIID31110, xc.getPath(), e.getMessage()), obj); //$NON-NLS
				}
    		}
			if (xc.getDefaultExpression() != null && !EvaluatableVisitor.isFullyEvaluatable(xc.getDefaultExpression(), false)) {
				handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_default, xc.getDefaultExpression()), obj);
			}
		}
    }
    
    @Override
    public void visit(XMLQuery obj) {
    	validatePassing(obj, obj.getXQueryExpression(), obj.getPassing());
    }

    @Since(Version.TEIID_8_10)
    @Override
    public void visit(XMLExists obj) {
        validatePassing(obj, obj.getXmlQuery().getXQueryExpression(), obj.getXmlQuery().getPassing());
    }

    @Since(Version.TEIID_8_10)
    @Override
    public void visit(XMLCast obj) {
        if (obj.getExpression().getType() != DefaultDataTypes.XML.getTypeClass()
            && obj.getType() != DefaultDataTypes.XML.getTypeClass()) {
            handleValidationError(Messages.getString(Messages.ValidationVisitor.xmlcast_types, obj), obj);
        }
    }

    private void validatePassing(LanguageObject obj, SaxonXQueryExpression xqe, List<DerivedColumn> passing) {
		boolean context = false;
    	boolean hadError = false;
    	TreeSet<String> names = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    	for (DerivedColumn dc : passing) {
    		if (dc.getAlias() == null) {
    			Class<?> type = dc.getExpression().getType();
    			if (type != DataTypeManagerService.DefaultDataTypes.XML.getTypeClass()) {
    				handleValidationError(Messages.getString(Messages.ValidationVisitor.context_item_type), obj);
    			}
    			if (context && !hadError) {
    				handleValidationError(Messages.getString(Messages.ValidationVisitor.passing_requires_name), obj);
    				hadError = true;
    			}
    			context = true;
        	} else { 
        		validateXMLContentTypes(dc.getExpression(), obj);
        		if (!names.add(dc.getAlias())) {
        			handleValidationError(Messages.getString(Messages.ValidationVisitor.duplicate_passing, dc.getAlias()), obj);
        		}
        	}
		}
    	if (xqe.usesContextItem() && !context) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.context_required), obj);    		
    	}
	}
    
    @Override
    public void visit(XMLNamespaces obj) {
    	boolean hasDefault = false;
    	for (NamespaceItem item : obj.getNamespaceItems()) {
			if (item.getPrefix() != null) {
				if (item.getPrefix().equals("xml") || item.getPrefix().equals("xmlns")) { //$NON-NLS-1$ //$NON-NLS-2$
					handleValidationError(Messages.getString(Messages.ValidationVisitor.xml_namespaces_reserved), obj);
				} else if (!Name11Checker.getInstance().isValidNCName(item.getPrefix())) {
					handleValidationError(Messages.getString(Messages.ValidationVisitor.xml_namespaces_invalid, item.getPrefix()), obj);
				}
				if (item.getUri().length() == 0) {
					handleValidationError(Messages.getString(Messages.ValidationVisitor.xml_namespaces_null_uri), obj);
				}
				continue;
			}
			if (hasDefault) {
				handleValidationError(Messages.getString(Messages.ValidationVisitor.xml_namespaces), obj);
				break;
			}
			hasDefault = true;
		}
    }
    
    @Override
    public void visit(TextTable obj) {
    	boolean widthSet = false;
    	Character delimiter = null;
    	Character quote = null;
    	boolean usingSelector = false;
    	for (TextColumn column : obj.getColumns()) {
    	    if (column.isOrdinal())
                continue;

			if (column.getWidth() != null) {
				widthSet = true;
				if (column.getWidth() < 0) {
					handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_negative), obj);
				}
			} else if (widthSet) {
    			handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_invalid_width), obj);
			}
			if (column.getSelector() != null) {
				usingSelector = true;
				if (obj.getSelector() != null && obj.getSelector().equals(column.getSelector())) {
					handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_selector_required), obj);
				}
			}
        	if (column.getPosition() != null && column.getPosition() < 0) {
	    		handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_negative), obj);
	    	}
		}
    	if (widthSet) {
    		if (obj.getDelimiter() != null || obj.getHeader() != null || obj.getQuote() != null || usingSelector) {
        		handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_width), obj);
    		}
    	} else {
        	if (obj.getHeader() != null && obj.getHeader() < 0) {
	    		handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_negative), obj);
	    	}
        	if (!obj.isUsingRowDelimiter()) {
        		handleValidationError(Messages.getString(Messages.ValidationVisitor.fixed_option), obj);
        	}
    		delimiter = obj.getDelimiter();
    		quote = obj.getQuote();
			validateTextOptions(obj, delimiter, quote, obj.getRowDelimiter());
    	}
    	if (obj.getSkip() != null && obj.getSkip() < 0) {
    		handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_negative), obj);
    	}
    	if (usingSelector && obj.getSelector() == null) {
    		handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_selector_required), obj);
    	}
    }

	private void validateTextOptions(LanguageObject obj, Character delimiter, Character quote, Character newLine) {
		if (quote == null) {
			quote = '"';
		} 
		if (delimiter == null) {
			delimiter = ',';
		}
		if (newLine == null) {
            newLine = '\n';
        }
		if (quote.equals(delimiter)) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_delimiter), obj);
		}
		if (quote.equals(newLine) || delimiter.equals(newLine)) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.text_table_newline), obj);
		}
	}
    
    @Override
    public void visit(XMLParse obj) {
    	if (obj.getExpression().getType() == DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass())
    	    return;

    	if(obj.getExpression().getType() == DataTypeManagerService.DefaultDataTypes.CLOB.getTypeClass())
    	    return;

    	if(obj.getExpression().getType() == DataTypeManagerService.DefaultDataTypes.BLOB.getTypeClass())
    	    return;

    	if (isTeiid811OrGreater()) {
    	    if(obj.getExpression().getType() == DataTypeManagerService.DefaultDataTypes.VARBINARY.getTypeClass())
    	        return;
    	}

    	handleValidationError(Messages.getString(Messages.ValidationVisitor.xmlparse_type), obj);
    }
    
    @Override
    public void visit(ExistsCriteria obj) {
    	validateSubquery(obj);
    }
    
    @Override
    public void visit(SubqueryFromClause obj) {
    	validateSubquery(obj);
    }
    
    @Override
    public void visit(LoopStatement obj) {
    	validateSubquery(obj);
    }
    
    @Override
    public void visit(WithQueryCommand obj) {
    	validateSubquery(obj);
    }
    
    @Override
    public void visit(AlterView obj) {
    	try {
    	    QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
            queryResolver.validateProjectedSymbols(obj.getTarget(), getMetadata(), obj.getDefinition());
			Validator.validate(obj.getDefinition(), getMetadata(), this);
			validateAlterTarget(obj);
		} catch (QueryValidatorException e) {
            handleValidationError(e.getMessage(), obj.getDefinition());
        } catch (Exception e) {
            handleException(e);
        }
    }

	private void validateAlterTarget(Alter<?> obj) {
		if (getMetadata().getImportedModels().contains(obj.getTarget().getSchema())) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_alter, obj.getTarget()), obj.getTarget());
		}
	}

    @Override
    public void visit(AlterProcedure obj) {
    	GroupSymbol gs = obj.getTarget();
    	validateAlterTarget(obj);
    	try {
	    	if (!gs.isProcedure() || !getMetadata().isVirtualModel(getMetadata().getModelID(gs.getMetadataID()))) {
	    		handleValidationError(Messages.getString(Messages.ValidationVisitor.not_a_procedure, gs), gs);
	    		return;
	    	}
	    	Validator.validate(obj.getDefinition(), getMetadata(), this);
	    	QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
	    	IStoredProcedureInfo<ISPParameter, IQueryNode> info = getMetadata().getStoredProcedureInfoForProcedure(gs.getName());
	    	for (ISPParameter param : info.getParameters()) {
	    		if (param.getParameterType() == SPParameter.RESULT_SET) {
	    	    	queryResolver.validateProjectedSymbols(gs, param.getResultSetColumns(), obj.getDefinition().getProjectedSymbols());
	    	    	break;
	    		}
	    	}
    	} catch (QueryValidatorException e) {
            Command command = obj.getDefinition();
            if (command instanceof CreateUpdateProcedureCommand)
                handleValidationError(e.getMessage(), ((CreateUpdateProcedureCommand) command).getBlock());
            else if (command instanceof CreateProcedureCommand)
                handleValidationError(e.getMessage(), ((CreateProcedureCommand) command).getBlock());
        } catch (Exception e) {
            handleException(e);
        }
    }
    
    @Override
    public void visit(Block obj) {
    	if (obj.getLabel() == null) {
    		return;
    	}
		for (LanguageObject lo : stack) {
			if (lo instanceof Labeled) {
				Labeled labeled = (Labeled)lo;
	    		if (obj.getLabel().equalsIgnoreCase(labeled.getLabel())) {
	    			handleValidationError(Messages.getString(Messages.ValidationVisitor.duplicate_block_label, obj.getLabel()), obj);
	    		}
			}
		}
    }

    private void visit8(CommandStatement obj) {
    	if (this.createProc == null || this.createProc.getResultSetColumns().isEmpty() || !obj.isReturnable() || !obj.getCommand().returnsResultSet()) {
    		return;
    	}
		List<? extends Expression> symbols = obj.getCommand().getResultSetColumns();
		if (symbols == null && obj.getCommand() instanceof DynamicCommand) {
			DynamicCommand cmd = (DynamicCommand)obj.getCommand();
			cmd.setAsColumns(this.createProc.getResultSetColumns());
			return;
		}
		try {
		    QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
            queryResolver.validateProjectedSymbols(createProc.getVirtualGroup(), createProc.getResultSetColumns(), symbols);
		} catch (Exception e) {
			handleValidationError(Messages.gs(Messages.TEIID.TEIID31121, createProc.getVirtualGroup(), obj, e.getMessage()), obj);
		}
    }
    
    @Override
    public void visit(BranchingStatement obj) {
		boolean matchedLabel = false;
		boolean inLoop = false;
		for (LanguageObject lo : stack) {
			if (lo instanceof LoopStatement || lo instanceof WhileStatement) {
				inLoop = true;
				if (obj.getLabel() == null) {
					break;
				}
				matchedLabel |= obj.getLabel().equalsIgnoreCase(((Labeled)lo).getLabel());
			} else if (obj.getLabel() != null && lo instanceof Block && obj.getLabel().equalsIgnoreCase(((Block)lo).getLabel())) {
				matchedLabel = true;
				if (obj.getMode() != BranchingMode.LEAVE) {
					handleValidationError(Messages.getString(Messages.ValidationVisitor.invalid_label, obj.getLabel()), obj);
				}
			}
		}
		if (obj.getMode() != BranchingMode.LEAVE && !inLoop) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.no_loop), obj);
		}
		if (obj.getLabel() != null && !matchedLabel) {
			handleValidationError(Messages.getString(Messages.ValidationVisitor.unknown_block_label, obj.getLabel()), obj);
		}
    }
    
    @Override
    public void visit(AlterTrigger obj) {
    	validateAlterTarget(obj);
    	validateGroupSupportsUpdate(obj.getTarget());
		try {
			if (obj.getDefinition() != null) {
				Validator.validate(obj.getDefinition(), getMetadata(), this);
			}			
		} catch (Exception e) {
			handleException(e);
		}
    }

    //TODO: it may be simpler to catch this in the parser
    private void validateSubquery(SubqueryContainer<?> subQuery) {
    	if (subQuery.getCommand() instanceof Query && ((Query)subQuery.getCommand()).getInto() != null) {
        	handleValidationError(Messages.getString(Messages.ValidationVisitor.subquery_insert), subQuery.getCommand());
        }
    }
    
}
