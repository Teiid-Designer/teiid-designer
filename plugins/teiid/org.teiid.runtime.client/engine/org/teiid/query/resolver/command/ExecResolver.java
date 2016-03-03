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

package org.teiid.query.resolver.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.metadata.TempMetadataStore;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.resolver.ProcedureContainerResolver;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.GroupContext;
import org.teiid.query.sql.lang.ProcedureContainer;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.symbol.Array;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.visitor.ValueIteratorProviderCollectorVisitor;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;


/**
 */
public class ExecResolver extends ProcedureContainerResolver {
	
    /**
     * @param queryResolver
     */
    public ExecResolver(QueryResolver queryResolver) {
        super(queryResolver);
    }

    /**
     * @param metadata
     * @param storedProcedureCommand
     * @param storedProcedureInfo
     * @param oldParams
     * @param namedParameters
     * @throws TeiidClientException
     * @throws TeiidComponentException
     * @throws QueryMetadataException
     */
    private void findCommand7Metadata(IQueryMetadataInterface metadata, StoredProcedure storedProcedureCommand, IStoredProcedureInfo storedProcedureInfo, Collection<SPParameter> oldParams, boolean namedParameters)
        throws Exception {
        // Cache original input parameter expressions.  Depending on whether
        // the procedure was parsed with named or unnamed parameters, the keys
        // for this map will either be the String names of the parameters or
        // the Integer indices, as entered in the user query
        Map<Object, Expression> inputExpressions = new HashMap<Object, Expression>();
        int adjustIndex = 0;
        for (SPParameter param : oldParams) {
            if(param.getExpression() == null) {
                if (param.getParameterType() == SPParameter.RESULT_SET) {
                    adjustIndex--;  //If this was already resolved, just pretend the result set param doesn't exist
                }
                continue;
            }
            if (namedParameters && param.getParameterType() != SPParameter.RETURN_VALUE) {
                if (inputExpressions.put(param.getName().toUpperCase(), param.getExpression()) != null) {
                    throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30138, param.getName().toUpperCase()));
                }
            } else {
                inputExpressions.put(param.getIndex() + adjustIndex, param.getExpression());
            }
        }

        storedProcedureCommand.clearParameters();
        int origInputs = inputExpressions.size();
        /*
         * Take the values set from the stored procedure implementation, and match up with the
         * types of parameter it is from the metadata and then reset the newly joined parameters
         * into the stored procedure command.  If it is a result set get those columns and place
         * them into the stored procedure command as well.
         */
        List<SPParameter> metadataParams = storedProcedureInfo.getParameters();
        List<SPParameter> clonedMetadataParams = new ArrayList<SPParameter>(metadataParams.size());
        int inputParams = 0;
        int outParams = 0;
        boolean hasReturnValue = false;
        for (SPParameter metadataParameter : metadataParams) {
            if( (metadataParameter.getParameterType()==ISPParameter.ParameterInfo.IN.index()) ||
                (metadataParameter.getParameterType()==ISPParameter.ParameterInfo.INOUT.index())){

                inputParams++;
            } else if (metadataParameter.getParameterType() == ISPParameter.ParameterInfo.OUT.index()) {
                outParams++;
            } else if (metadataParameter.getParameterType() == ISPParameter.ParameterInfo.RETURN_VALUE.index()) {
                hasReturnValue = true;
            }
            SPParameter clonedParam = metadataParameter.clone();
            clonedMetadataParams.add(clonedParam);
            storedProcedureCommand.setParameter(clonedParam);
        }
        
        if (storedProcedureCommand.isCalledWithReturn() && !hasReturnValue) {
            throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30139, storedProcedureCommand.getGroup()));
        }

        if(!namedParameters && (inputParams > inputExpressions.size())) {
            throw new TeiidClientException(Messages.getString(Messages.ERR.ERR_015_008_0007, inputParams, origInputs, storedProcedureCommand.getGroup()));
        }
        
        // Walk through the resolved parameters and set the expressions from the
        // input parameters
        int exprIndex = 1;
        HashSet<String> expected = new HashSet<String>();
        if (storedProcedureCommand.isCalledWithReturn() && hasReturnValue) {
            for (SPParameter param : clonedMetadataParams) {
                if (param.getParameterType() == SPParameter.RETURN_VALUE) {
                    Expression expr = inputExpressions.remove(exprIndex++);
                    param.setExpression(expr);
                }
            }
        }
        for (SPParameter param : clonedMetadataParams) {
            if(param.getParameterType() == SPParameter.RESULT_SET || param.getParameterType() == SPParameter.RETURN_VALUE) {
                continue;
            }
            if (namedParameters) {
                String nameKey = param.getParameterSymbol().getShortCanonicalName();
                Expression expr = inputExpressions.remove(nameKey);
                // With named parameters, have to check on optional params and default values
                if (expr == null && param.getParameterType() != ISPParameter.ParameterInfo.OUT.index()) {
                    expr = ResolverUtil.getDefault(param.getParameterSymbol(), metadata);
                    param.setUsingDefault(true);
                    expected.add(nameKey);
                } 
                param.setExpression(expr);                    
            } else {
                if(param.getParameterType() == SPParameter.OUT) {
                    continue;
                }
                Expression expr = inputExpressions.remove(exprIndex++);
                param.setExpression(expr);
            }
        }
        
        // Check for leftovers, i.e. params entered by user w/ wrong/unknown names
        if (!inputExpressions.isEmpty()) {
            if (namedParameters) {
                throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30141, inputExpressions.keySet(), expected));
            }
            throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID31113, inputParams, origInputs, storedProcedureCommand.getGroup().toString()));
        }
    }

    private void findCommand8Metadata(IQueryMetadataInterface metadata, StoredProcedure storedProcedureCommand, IStoredProcedureInfo storedProcedureInfo, Collection<SPParameter> oldParams, boolean namedParameters)
        throws Exception {

        // Cache original input parameter expressions.  Depending on whether
        // the procedure was parsed with named or unnamed parameters, the keys
        // for this map will either be the String names of the parameters or
        // the Integer indices, as entered in the user query
        Map<Integer, Expression> positionalExpressions = new TreeMap<Integer, Expression>();
        Map<String, Expression> namedExpressions = new TreeMap<String, Expression>(String.CASE_INSENSITIVE_ORDER);
        int adjustIndex = 0;
        for (SPParameter param : oldParams) {
            if(param.getExpression() == null) {
                if (param.getParameterType() == SPParameter.RESULT_SET) {
                    adjustIndex--;  //If this was already resolved, just pretend the result set param doesn't exist
                }
                continue;
            }
            if (namedParameters && param.getParameterType() != SPParameter.RETURN_VALUE) {
                if (namedExpressions.put(param.getParameterSymbol().getShortName(), param.getExpression()) != null) {
                     throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30138, param.getName()));
                }
            } else {
                positionalExpressions.put(param.getIndex() + adjustIndex, param.getExpression());
            }
        }

        storedProcedureCommand.clearParameters();
        int origInputs = positionalExpressions.size() + namedExpressions.size();
        /*
         * Take the values set from the stored procedure implementation, and match up with the
         * types of parameter it is from the metadata and then reset the newly joined parameters
         * into the stored procedure command.  If it is a result set get those columns and place
         * them into the stored procedure command as well.
         */
        List<SPParameter> metadataParams = storedProcedureInfo.getParameters();
        List<SPParameter> clonedMetadataParams = new ArrayList<SPParameter>(metadataParams.size());
        int inputParams = 0;
        int optionalParams = 0;
        int outParams = 0;
        boolean hasReturnValue = false;
        boolean optional = false;
        boolean varargs = false;
        for (int i = 0; i < metadataParams.size(); i++) {
            SPParameter metadataParameter = metadataParams.get(i);
            if( (metadataParameter.getParameterType()==ISPParameter.ParameterInfo.IN.index()) ||
                (metadataParameter.getParameterType()==ISPParameter.ParameterInfo.INOUT.index())){
                if (ResolverUtil.hasDefault(metadataParameter.getMetadataID(), metadata) || metadataParameter.isVarArg()) {
                    optional = true;
                    optionalParams++;
                } else {
                    inputParams++;
                    if (optional) {
                        optional = false;
                        inputParams += optionalParams;
                        optionalParams = 0;
                    }
                }
                if (metadataParameter.isVarArg()) {
                    varargs = true;
                }
            } else if (metadataParameter.getParameterType() == ISPParameter.ParameterInfo.OUT.index()) {
                outParams++;
                /*
                 * TODO: it would consistent to do the following, but it is a breaking change for procedures that have intermixed out params with in.
                 * we may need to revisit this later
                 */
                //optional = true;
                //optionalParams++;
            } else if (metadataParameter.getParameterType() == ISPParameter.ParameterInfo.RETURN_VALUE.index()) {
                hasReturnValue = true;
            }
            SPParameter clonedParam = metadataParameter.clone();
            clonedMetadataParams.add(clonedParam);
            storedProcedureCommand.setParameter(clonedParam);
        }
        
        if (storedProcedureCommand.isCalledWithReturn() && !hasReturnValue) {
             throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30139, storedProcedureCommand.getGroup()));
        }

        if(!namedParameters && (inputParams > positionalExpressions.size()) ) {
             throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30140, inputParams, inputParams + optionalParams + (varargs?"+":""), origInputs, storedProcedureCommand.getGroup())); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        // Walk through the resolved parameters and set the expressions from the
        // input parameters
        int exprIndex = 1;
        HashSet<String> expected = new HashSet<String>();
        if (storedProcedureCommand.isCalledWithReturn() && hasReturnValue) {
            for (SPParameter param : clonedMetadataParams) {
                if (param.getParameterType() == SPParameter.RETURN_VALUE) {
                    Expression expr = positionalExpressions.remove(exprIndex++);
                    param.setExpression(expr);
                    break;
                }
            }
        }
        for (SPParameter param : clonedMetadataParams) {
            if(param.getParameterType() == SPParameter.RESULT_SET || param.getParameterType() == SPParameter.RETURN_VALUE) {
                continue;
            }
            if (namedParameters) {
                String nameKey = param.getParameterSymbol().getShortName();
                Expression expr = namedExpressions.remove(nameKey);
                // With named parameters, have to check on optional params and default values
                if (expr == null) {
                    if (param.getParameterType() != ISPParameter.ParameterInfo.OUT.index()) {
                        param.setUsingDefault(true);
                        expected.add(nameKey);
                        if (!param.isVarArg()) {
                            expr = ResolverUtil.getDefault(param.getParameterSymbol(), metadata);
                        } else {
                            //zero length array
                            List<Expression> exprs = new ArrayList<Expression>(0);
                            Array array = create(ASTNodes.ARRAY); 
                            array.setExpressions(exprs);
                            array.setImplicit(true);
                            array.setType(param.getClassType());
                            expr = array;
                        }
                    }
                } 
                param.setExpression(expr);                    
            } else {
                Expression expr = positionalExpressions.remove(exprIndex++);
                if(param.getParameterType() == SPParameter.OUT) {
                    if (expr != null) {
                        boolean isRef = expr instanceof Reference;
                        if (!isRef || exprIndex <= inputParams + 1) {
                            //for backwards compatibility, this should be treated instead as an input
                            exprIndex--;
                            positionalExpressions.put(exprIndex, expr);
                        } else if (isRef) {
                            //mimics the hack that was in PreparedStatementRequest.
                            Reference ref = (Reference)expr;
                            ref.setOptional(true); //may be an out
                            /*
                             * Note that there is a corner case here with out parameters intermixed with optional parameters
                             * there's not a good way around this.
                             */
                        }
                    }
                    continue;
                }
                if (expr == null) {
                    if (!param.isVarArg()) {
                        expr = ResolverUtil.getDefault(param.getParameterSymbol(), metadata);
                    }
                    param.setUsingDefault(true);
                } 
                if (param.isVarArg()) {
                    List<Expression> exprs = new ArrayList<Expression>(positionalExpressions.size() + 1);
                    if (expr != null) {
                        exprs.add(expr);
                    }
                    exprs.addAll(positionalExpressions.values());
                    positionalExpressions.clear();
                    Array array = create(ASTNodes.ARRAY); 
                    array.setExpressions(exprs);
                    array.setImplicit(true);
                    expr = array;
                }
                param.setExpression(expr);
            }
        }
        
        // Check for leftovers, i.e. params entered by user w/ wrong/unknown names
        if (!namedExpressions.isEmpty()) {
             throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30141, namedExpressions.keySet(), expected));
        }
        if (!positionalExpressions.isEmpty()) {
             throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID31113, positionalExpressions.size(), origInputs, storedProcedureCommand.getGroup().toString()));
        }
    }

    /**
     * @see org.teiid.query.resolver.CommandResolver#findCommandMetadata(org.teiid.query.sql.lang.Command,
     * org.teiid.query.metadata.QueryMetadataInterface)
     */
    private void findCommandMetadata(Command command, TempMetadataStore discoveredMetadata, IQueryMetadataInterface metadata)
        throws Exception {

        StoredProcedure storedProcedureCommand = (StoredProcedure) command;
        
        IStoredProcedureInfo storedProcedureInfo = null;
        try {
        	storedProcedureInfo = metadata.getStoredProcedureInfoForProcedure(storedProcedureCommand.getProcedureName());
        } catch (Exception e) {
        	String[] parts = storedProcedureCommand.getProcedureName().split("\\.", 2); //$NON-NLS-1$
	    	if (parts.length > 1 && parts[0].equalsIgnoreCase(metadata.getVirtualDatabaseName())) {
	            try {
	            	storedProcedureInfo = metadata.getStoredProcedureInfoForProcedure(parts[1]);
	            	storedProcedureCommand.setProcedureName(parts[1]);
	            } catch(Exception e1) {
	            } 
	        }
	    	if (storedProcedureInfo == null) {
	    		throw e;
	    	}
        }

        storedProcedureCommand.setUpdateCount(storedProcedureInfo.getUpdateCount());
        storedProcedureCommand.setModelID(storedProcedureInfo.getModelID());
        storedProcedureCommand.setProcedureID(storedProcedureInfo.getProcedureID());
        storedProcedureCommand.setProcedureCallableName(storedProcedureInfo.getProcedureCallableName());

        // Get old parameters as they may have expressions set on them - collect
        // those expressions to copy later into the resolved parameters
        Collection<SPParameter> oldParams = storedProcedureCommand.getParameters();

        boolean namedParameters = storedProcedureCommand.isDisplayNamedParameters();
        
        // If parameter count is zero, then for the purposes of this method treat that
        // as if named parameters were used.  Even though the StoredProcedure was not
        // parsed that way, the user may have entered no parameters with the intention
        // of relying on all default values of all optional parameters.
        if (oldParams.size() == 0 || (oldParams.size() == 1 && storedProcedureCommand.isCalledWithReturn())) {
        	storedProcedureCommand.setDisplayNamedParameters(true);
            namedParameters = true;
        }
        
        if (getTeiidVersion().isLessThan(Version.TEIID_8_0))
            findCommand7Metadata(metadata, storedProcedureCommand, storedProcedureInfo, oldParams, namedParameters);
        else
            findCommand8Metadata(metadata, storedProcedureCommand, storedProcedureInfo, oldParams, namedParameters);
        
        // Create temporary metadata that defines a group based on either the stored proc
        // name or the stored query name - this will be used later during planning
        String procName = storedProcedureCommand.getProcedureName();
        List tempElements = storedProcedureCommand.getProjectedSymbols();
        boolean isVirtual = storedProcedureInfo.getQueryPlan() != null;
        discoveredMetadata.addTempGroup(procName, tempElements, isVirtual);

        // Resolve tempElements against new metadata
        GroupSymbol procGroup = getTeiidParser().createASTNode(ASTNodes.GROUP_SYMBOL);
        procGroup.setName(storedProcedureInfo.getProcedureCallableName());
        procGroup.setProcedure(true);
        TempMetadataID tid = discoveredMetadata.getTempGroupID(procName);
        tid.setOriginalMetadataID(storedProcedureCommand.getProcedureID());
        procGroup.setMetadataID(tid);
        storedProcedureCommand.setGroup(procGroup);
    }
    
    /** 
     * @see org.teiid.query.resolver.ProcedureContainerResolver#resolveProceduralCommand(org.teiid.query.sql.lang.Command, org.teiid.query.metadata.TempMetadataAdapter)
     */
    @Override
    public void resolveProceduralCommand(Command command, TempMetadataAdapter metadata) 
        throws Exception {

        findCommandMetadata(command, metadata.getMetadataStore(), metadata);
        
        //Resolve expressions on input parameters
        StoredProcedure storedProcedureCommand = (StoredProcedure) command;
        GroupContext externalGroups = storedProcedureCommand.getExternalGroupContexts();
        for (SPParameter param : storedProcedureCommand.getParameters()) {
            Expression expr = param.getExpression();
            if(expr == null) {
            	continue;
            }
            for (SubqueryContainer<?> container : ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(expr)) {
                getQueryResolver().setChildMetadata(container.getCommand(), command);
                
                getQueryResolver().resolveCommand(container.getCommand(), metadata.getMetadata());
            }
            try {
            	ResolverVisitor visitor = new ResolverVisitor(expr.getTeiidVersion());
            	visitor.resolveLanguageObject(expr, null, externalGroups, metadata);
            } catch (Exception e) {
            	if (!checkForArray(param, expr)) {
            		throw e;
            	}
            	continue;
            }
            Class<?> paramType = param.getClassType();

            ResolverUtil.setDesiredType(expr, paramType, storedProcedureCommand);
            
            // Compare type of parameter expression against parameter type
            // and add implicit conversion if necessary
            Class<?> exprType = expr.getType();
            if(paramType == null || exprType == null) {
                 throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30143, storedProcedureCommand.getProcedureName(), param.getName()));
            }
            String tgtType = getDataTypeManager().getDataTypeName(paramType);
            String srcType = getDataTypeManager().getDataTypeName(exprType);
            Expression result = null;
                            
            if (param.getParameterType() == SPParameter.RETURN_VALUE || param.getParameterType() == SPParameter.OUT) {
            	if (!ResolverUtil.canImplicitlyConvert(getTeiidVersion(), tgtType, srcType)) {
            		 throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30144, param.getParameterSymbol(), tgtType, srcType));
            	}
            } else {
                try {
                    result = ResolverUtil.convertExpression(expr, tgtType, metadata);
                } catch (Exception e) {
                     throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID30145, new Object[] { param.getParameterSymbol(), srcType, tgtType}));
                }                                                       
                param.setExpression(result);
            }
        }
    }

    /**
     * The param resolving always constructs an array, which is 
     * not appropriate if passing an array directly
     * @return 
     */
	private boolean checkForArray(SPParameter param, Expression expr) {
		if (!param.isVarArg() || !(expr instanceof Array)) {
			return false;
		}
		Array array = (Array)expr;
		if (array.getExpressions().size() == 1) {
			Expression first = array.getExpressions().get(0);
			if (first.getType() != null && first.getType() == array.getType()) {
				param.setExpression(first);
				return true;
			} 
		}
		return false;
	}
    
    @Override
    protected void resolveGroup(TempMetadataAdapter metadata,
                                ProcedureContainer procCommand) throws Exception {
        //Do nothing
    }

    /** 
     * @throws Exception 
     * @see org.teiid.query.resolver.ProcedureContainerResolver#getPlan(org.teiid.query.metadata.QueryMetadataInterface, org.teiid.query.sql.symbol.GroupSymbol)
     */
    @Override
    protected String getPlan(IQueryMetadataInterface metadata,
                             GroupSymbol group) throws Exception {
        IStoredProcedureInfo<SPParameter, QueryNode> storedProcedureInfo = metadata.getStoredProcedureInfoForProcedure(group.getName());
        
        //if there is a query plan associated with the procedure, get it.
        QueryNode plan = storedProcedureInfo.getQueryPlan();
        
        if (plan.getQuery() == null) {
             throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30146, group));
        }
        
        return plan.getQuery();
    }
}
