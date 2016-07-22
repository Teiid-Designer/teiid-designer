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
import java.util.List;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.resolver.CommandResolver;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.runtime.client.Messages;


public class SetQueryResolver extends CommandResolver {

    /**
     * @param queryResolver
     */
    public SetQueryResolver(QueryResolver queryResolver) {
        super(queryResolver);
    }

    /**
     * @see org.teiid.query.resolver.CommandResolver#resolveCommand(org.teiid.query.sql.lang.Command, TempMetadataAdapter, boolean)
     */
    public void resolveCommand(Command command, TempMetadataAdapter metadata, boolean resolveNullLiterals)
        throws Exception {

        SetQuery setQuery = (SetQuery) command;
        
        SimpleQueryResolver simpleQueryResolver = new SimpleQueryResolver(getQueryResolver());
        simpleQueryResolver.resolveWith(metadata, setQuery);

        QueryCommand firstCommand = setQuery.getLeftQuery();
        QueryCommand rightCommand = setQuery.getRightQuery();

        getQueryResolver().setChildMetadata(firstCommand, setQuery);
        getQueryResolver().resolveCommand(firstCommand, metadata.getMetadata(), false);

        resolveSetQuery(metadata, resolveNullLiterals, setQuery, firstCommand, rightCommand);
    }

    public void resolveSetQuery(TempMetadataAdapter metadata,
                                boolean resolveNullLiterals, SetQuery setQuery,
                                QueryCommand firstCommand, QueryCommand rightCommand)
                                throws Exception {    
        List<Expression> firstProject = firstCommand.getProjectedSymbols();
        List<Class<?>> firstProjectTypes = new ArrayList<Class<?>>();
        for (Expression symbol : firstProject) {
            firstProjectTypes.add(symbol.getType());
        }

        getQueryResolver().setChildMetadata(rightCommand, setQuery);
        getQueryResolver().resolveCommand(rightCommand, metadata.getMetadata(), false);

        if (firstProject.size() != rightCommand.getProjectedSymbols().size()) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30147, setQuery.getOperation()));
        }
        checkSymbolTypes(firstProjectTypes, rightCommand.getProjectedSymbols());
        
        if (resolveNullLiterals) {
            for (int i = 0; i < firstProjectTypes.size(); i++) {
                Class<?> clazz = firstProjectTypes.get(i);
                
                if (DataTypeManagerService.DefaultDataTypes.NULL.getTypeClass().equals(clazz)) {
                    firstProjectTypes.set(i, DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass());
                }
            }
        }

        setQuery.setProjectedTypes(firstProjectTypes, metadata.getMetadata());
        
        // ORDER BY clause
        if(setQuery.getOrderBy() != null) {
            //order by elements must use the short name of the projected symbols
            ResolverUtil.resolveOrderBy(setQuery.getOrderBy(), setQuery, metadata);
        } 

        setProjectedTypes(setQuery, firstProjectTypes, metadata.getMetadata());
        
        if (setQuery.getLimit() != null) {
            ResolverUtil.resolveLimit(setQuery.getLimit());
        }
        
        setQuery.setTemporaryMetadata(firstCommand.getTemporaryMetadata().clone());
    }

    private void setProjectedTypes(SetQuery setQuery,
                                   List<Class<?>> firstProjectTypes, IQueryMetadataInterface metadata) throws Exception {
        for (QueryCommand subCommand : setQuery.getQueryCommands()) {
            if (!(subCommand instanceof SetQuery)) {
                continue;
            }
            SetQuery child = (SetQuery)subCommand;
            List projectedSymbols = child.getProjectedSymbols();
            if (child.getOrderBy() != null) {
                for (int j = 0; j < projectedSymbols.size(); j++) {
                    Expression ses = (Expression)projectedSymbols.get(j);
                    Class<?> targetType = firstProjectTypes.get(j);
                    if (ses.getType() != targetType && orderByContainsVariable(child.getOrderBy(), ses, j)) {
                        DataTypeManagerService dataTypeManager = getDataTypeManager();
                        String sourceTypeName = dataTypeManager.getDataTypeName(ses.getType());
                        String targetTypeName = dataTypeManager.getDataTypeName(targetType);
                        throw new QueryResolverException(Messages.getString(Messages.QueryResolver.type_conversion,
                                                                                    new Object[] {ses, sourceTypeName, targetTypeName}));
                    }
                }
            }
            child.setProjectedTypes(firstProjectTypes, metadata);
            setProjectedTypes(child, firstProjectTypes, metadata);
        }
    }
    
    /**
     * Checks if a variable is in the ORDER BY
     * @param position 0-based index of the variable
     * @return True if the ORDER BY contains the element
     */
    public static boolean orderByContainsVariable(OrderBy orderBy, Expression ses, int position) {
    	for (OrderByItem item : orderBy.getOrderByItems()) {
			if (item.getExpressionPosition() == position) {
				return true;
			}
		}
        return false;
    }
    
	private void checkSymbolTypes(List firstProjectTypes, List projSymbols) {
        for(int j=0; j<projSymbols.size(); j++){
            Class firstProjType = (Class)firstProjectTypes.get(j);
    		Expression projSymbol = (Expression)projSymbols.get(j);
            Class projType = projSymbol.getType();
            
            if(firstProjType.equals(projType)){
                continue;
            }
            DataTypeManagerService dataTypeManager = getDataTypeManager();

            String sourceType = dataTypeManager.getDataTypeName(firstProjType);
            String targetType = dataTypeManager.getDataTypeName(projType);
            
            String commonType = ResolverUtil.getCommonType(getTeiidVersion(), new String[] {sourceType, targetType});
            
            if (commonType == null) {
            	commonType = DataTypeManagerService.DefaultDataTypes.OBJECT.getId();
            }
            
            firstProjectTypes.set(j, dataTypeManager.getDataTypeClass(commonType));
        }
	}
}