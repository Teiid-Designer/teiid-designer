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

package org.teiid.query.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.language.SQLConstants;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.metadata.TempMetadataID.Type;
import org.teiid.query.metadata.TempMetadataStore;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.parser.TeiidNodeFactory;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.GroupContext;
import org.teiid.query.sql.lang.ProcedureContainer;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.validator.UpdateValidator.UpdateInfo;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;


public abstract class ProcedureContainerResolver extends CommandResolver {

    /**
     * @param queryResolver
     */
    public ProcedureContainerResolver(QueryResolver queryResolver) {
        super(queryResolver);
    }

    public abstract void resolveProceduralCommand(Command command,
                                                  TempMetadataAdapter metadata) throws Exception;

    /**
     * Expand a command by finding and attaching all subcommands to the command.  If
     * some initial resolution must be done for this to be accomplished, that is ok, 
     * but it should be kept to a minimum.
     * @param procCcommand The command to expand
     * @param metadata Metadata access
     * 
     * @throws Exception
     */
    public Command expandCommand(ProcedureContainer procCommand, IQueryMetadataInterface metadata)
    throws Exception {
        
        // Resolve group so we can tell whether it is an update procedure
        GroupSymbol group = procCommand.getGroup();

        Command subCommand = null;
        
        String plan = getPlan(metadata, procCommand);
        
        if (plan == null) {
            return null;
        }
        
        QueryParser parser = getQueryResolver().getQueryParser();
        try {
            subCommand = parser.parseProcedure(plan, !(procCommand instanceof StoredProcedure));
        } catch(Exception e) {
             throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID30060, group, procCommand.getClass().getSimpleName()));
        }
        
        return subCommand;
    }

    /** 
     * For a given resolver, this returns the unparsed command.
     * 
     * @param metadata
     * @param group
     * @return
     * @throws Exception
     * @throws Exception
     */
    protected abstract String getPlan(IQueryMetadataInterface metadata,
                           GroupSymbol group) throws Exception;
        
	private static void addChanging(ITeiidServerVersion teiidVersion, TempMetadataStore discoveredMetadata,
			GroupContext externalGroups, List<ElementSymbol> elements) {
		List<ElementSymbol> changingElements = new ArrayList<ElementSymbol>(elements.size());
        for(int i=0; i<elements.size(); i++) {
            ElementSymbol virtualElmnt = elements.get(i);
            ElementSymbol changeElement = virtualElmnt.clone();
            changeElement.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
            changingElements.add(changeElement);
        }

        addScalarGroup(teiidVersion, ProcedureReservedWords.CHANGING, discoveredMetadata, externalGroups, changingElements, false);
	}
        
    /** 
     * @see org.teiid.query.resolver.CommandResolver#resolveCommand(org.teiid.query.sql.lang.Command, org.teiid.query.metadata.TempMetadataAdapter, boolean)
     */
    public void resolveCommand(Command command, TempMetadataAdapter metadata, boolean resolveNullLiterals) 
        throws Exception {
        
        ProcedureContainer procCommand = (ProcedureContainer)command;
        
        resolveGroup(metadata, procCommand);
        
        resolveProceduralCommand(procCommand, metadata);
        
        //getPlan(metadata, procCommand);
    }

	private String getPlan(IQueryMetadataInterface metadata, ProcedureContainer procCommand)
			throws Exception {
		if(!procCommand.getGroup().isTempTable() && metadata.isVirtualGroup(procCommand.getGroup().getMetadataID())) {
            String plan = getPlan(metadata, procCommand.getGroup());
            if (plan == null && !metadata.isProcedure(procCommand.getGroup().getMetadataID())) {
            	int type = procCommand.getType();
            	//force validation
            	getUpdateInfo(procCommand.getGroup(), metadata, type, true);
            }
            return plan;
        }
		return null;
	}
	
	public UpdateInfo getUpdateInfo(GroupSymbol group, IQueryMetadataInterface metadata, int type, boolean validate) throws Exception {
		UpdateInfo info = getUpdateInfo(group, metadata);
		
		if (info == null) {
			return null;
		}
    	if (validate) {
    		String error = validateUpdateInfo(group, type, info);
    		if (error != null) {
    			throw new QueryResolverException(error);
    		}
    	}
    	return info;
	}

	public static String validateUpdateInfo(GroupSymbol group, int type, UpdateInfo info) {
		String error = info.getDeleteValidationError();
		String name = "Delete"; //$NON-NLS-1$
		if (type == ICommand.TYPE_UPDATE) {
			error = info.getUpdateValidationError();
			name = "Update"; //$NON-NLS-1$
		} else if (type == ICommand.TYPE_INSERT) {
			error = info.getInsertValidationError();
			name = "Insert"; //$NON-NLS-1$
		}
		if (error != null) {
			return Messages.gs(Messages.TEIID.TEIID30061, group, name, error);
		}
		return null;
	}

	public UpdateInfo getUpdateInfo(GroupSymbol group,
			IQueryMetadataInterface metadata) throws Exception {
		if (!getQueryResolver().isView(group, metadata)) {
			return null;
		}
		try {
			return getQueryResolver().resolveView(group, metadata.getVirtualPlan(group.getMetadataID()), SQLConstants.Reserved.SELECT, metadata).getUpdateInfo();
		} catch (Exception e) {
			 throw new QueryResolverException(e);
		}
	}
	
    /** 
     * @param metadata
     * @param procCommand
     * @throws Exception
     * @throws Exception
     */
    protected void resolveGroup(TempMetadataAdapter metadata,
                              ProcedureContainer procCommand) throws Exception {
        // Resolve group so we can tell whether it is an update procedure
        GroupSymbol group = procCommand.getGroup();
        ResolverUtil.resolveGroup(group, metadata);
        if (!group.isTempTable()) {
        	procCommand.setUpdateInfo(getUpdateInfo(group, metadata, procCommand.getType(), false));
        }
    }

    public static GroupSymbol addScalarGroup(ITeiidServerVersion teiidVersion, String name, TempMetadataStore metadata, GroupContext externalGroups, List<? extends Expression> symbols) {
    	return addScalarGroup(teiidVersion, name, metadata, externalGroups, symbols, true);
    }
    
	public static GroupSymbol addScalarGroup(ITeiidServerVersion teiidVersion, String name, TempMetadataStore metadata, GroupContext externalGroups, List<? extends Expression> symbols, boolean updatable) {
		boolean[] updateArray = new boolean[symbols.size()];
		if (updatable) {
			Arrays.fill(updateArray, true);
		}
		return addScalarGroup(teiidVersion, name, metadata, externalGroups, symbols, updateArray);
	}
	
	public static GroupSymbol addScalarGroup(ITeiidServerVersion teiidVersion, String name, TempMetadataStore metadata, GroupContext externalGroups, List<? extends Expression> symbols, boolean[] updatable) {
	    GroupSymbol variables = TeiidNodeFactory.createASTNode(teiidVersion, ASTNodes.GROUP_SYMBOL);
		variables.setName(name);
	    externalGroups.addGroup(variables);
	    TempMetadataID tid = metadata.addTempGroup(name, symbols);
	    tid.setMetadataType(Type.SCALAR);
	    int i = 0;
	    for (TempMetadataID cid : tid.getElements()) {
			cid.setMetadataType(Type.SCALAR);
			cid.setUpdatable(updatable[i++]);
		}
	    variables.setMetadataID(tid);
	    return variables;
	}
	
	/**
	 * Set the appropriate "external" metadata for the given command
	 * @param queryResolver
	 * @param currentCommand 
	 * @param container 
	 * @param type 
	 * @param metadata 
	 * @param inferProcedureResultSetColumns 
	 * @throws Exception 
	 */
	public static void findChildCommandMetadata(QueryResolver queryResolver, Command currentCommand,
			GroupSymbol container, int type, IQueryMetadataInterface metadata, boolean inferProcedureResultSetColumns)
			throws Exception {
	    ITeiidServerVersion teiidVersion = queryResolver.getTeiidVersion();
		//find the childMetadata using a clean metadata store
	    TempMetadataStore childMetadata = new TempMetadataStore();
	    TempMetadataAdapter tma = new TempMetadataAdapter(metadata, childMetadata);
	    GroupContext externalGroups = new GroupContext();

		if (currentCommand instanceof TriggerAction) {
			TriggerAction ta = (TriggerAction)currentCommand;
			ta.setView(container);
		    //TODO: it seems easier to just inline the handling here rather than have each of the resolvers check for trigger actions
		    List<ElementSymbol> viewElements = ResolverUtil.resolveElementsInGroup(ta.getView(), metadata);
		    if (type == ICommand.TYPE_UPDATE || type == ICommand.TYPE_INSERT) {
		    	addChanging(teiidVersion, tma.getMetadataStore(), externalGroups, viewElements);
		    	addScalarGroup(teiidVersion, SQLConstants.Reserved.NEW, tma.getMetadataStore(), externalGroups, viewElements, false);
		    }
		    if (type == ICommand.TYPE_UPDATE || type == ICommand.TYPE_DELETE) {
		    	addScalarGroup(teiidVersion, SQLConstants.Reserved.OLD, tma.getMetadataStore(), externalGroups, viewElements, false);
		    }
		} else if (currentCommand instanceof CreateProcedureCommand) {
			CreateProcedureCommand cupc = (CreateProcedureCommand)currentCommand;
			cupc.setVirtualGroup(container);

			if (type == ICommand.TYPE_STORED_PROCEDURE) {
				IStoredProcedureInfo<ISPParameter, IQueryNode> info = metadata.getStoredProcedureInfoForProcedure(container.getName());
		        // Create temporary metadata that defines a group based on either the stored proc
		        // name or the stored query name - this will be used later during planning
		        String procName = info.getProcedureCallableName();

		        // Look through parameters to find input elements - these become child metadata
		        List<ElementSymbol> tempElements = new ArrayList<ElementSymbol>(info.getParameters().size());
		        boolean[] updatable = new boolean[info.getParameters().size()];
		        int i = 0;
		        List<ElementSymbol> rsColumns = Collections.emptyList();
		        for (ISPParameter param : info.getParameters()) {
		            if(param.getParameterType() != ISPParameter.ParameterInfo.RESULT_SET.index()) {
		                ElementSymbol symbol = (ElementSymbol) param.getParameterSymbol();
		                tempElements.add(symbol);
		                updatable[i++] = param.getParameterType() != ISPParameter.ParameterInfo.IN.index();  
		                if (param.getParameterType() == ISPParameter.ParameterInfo.RETURN_VALUE.index()) {
		                	cupc.setReturnVariable(symbol);
		                }
		            } else {
		            	rsColumns = param.getResultSetColumns();
		            }
		        }
		        if (inferProcedureResultSetColumns) {
		        	rsColumns = null;
		        }
		        GroupSymbol gs = addScalarGroup(teiidVersion, procName, childMetadata, externalGroups, tempElements, updatable);
		        if (cupc.getReturnVariable() != null) {
		        	ResolverVisitor visitor = new ResolverVisitor(teiidVersion);
		        	visitor.resolveLanguageObject(cupc.getReturnVariable(), Arrays.asList(gs), metadata);
		        }
		        cupc.setResultSetColumns(rsColumns);
		        //the relational planner will override this with the appropriate value
		        cupc.setProjectedSymbols(rsColumns);
			} else {
    			cupc.setUpdateType(type);
			}
		}

	    queryResolver.setChildMetadata(currentCommand, childMetadata, externalGroups);
	}

}
