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

package org.teiid.query.resolver.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.api.exception.query.UnresolvedSymbolDescription;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IQueryMetadataInterface.SupportConstants;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.IJoinType.Types;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.BaseColumn;
import org.teiid.metadata.ForeignKey;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.metadata.GroupInfo;
import org.teiid.query.metadata.TempMetadataAdapter;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.metadata.TempMetadataStore;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.SubqueryContainer;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.CaseExpression;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.DerivedColumn;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.SearchedCaseExpression;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.util.SymbolMap;
import org.teiid.query.sql.visitor.ElementCollectorVisitor;
import org.teiid.query.sql.visitor.GroupsUsedByElementsVisitor;
import org.teiid.query.sql.visitor.ValueIteratorProviderCollectorVisitor;
import org.teiid.runtime.client.Messages;


/**
 * Utilities used during resolution
 */
public class ResolverUtil {

    public static class ResolvedLookup {
		private GroupSymbol group;
		private ElementSymbol keyElement;
		private ElementSymbol returnElement;
		
		void setGroup(GroupSymbol group) {
			this.group = group;
		}
		public GroupSymbol getGroup() {
			return group;
		}
		void setKeyElement(ElementSymbol keyElement) {
			this.keyElement = keyElement;
		}
		public ElementSymbol getKeyElement() {
			return keyElement;
		}
		void setReturnElement(ElementSymbol returnElement) {
			this.returnElement = returnElement;
		}
		public ElementSymbol getReturnElement() {
			return returnElement;
		}
	}

	// Can't construct
    private ResolverUtil() {}

    /*
     *                        Type Conversion Utilities
     */

    /**
     * Gets the most specific type to which all the given types have an implicit
     * conversion. The method decides a common type as follows:
     * <ul>
     *   <li>If one or more of the given types is a candidate, then this method
     *       will return the candidate that occurs first in the given array.
     *       This is why the order of the names in the array is important. </li>
     *   <li>Otherwise, if none of them is a candidate, this method will attempt
     *       to find a common type to which all of them can be implicitly
     *       converted.</li>
     *   <li>Otherwise this method is unable to find a common type to which all
     *       the given types can be implicitly converted, and therefore returns
     *       a null.</li>
     * </ul>
     * @param teiidVersion
     * @param typeNames an ordered array of unique type names.
     * @return a type name to which all the given types can be converted
     */
    @SuppressWarnings("null")
	public static String getCommonType(ITeiidServerVersion teiidVersion, String[] typeNames) {
        if (typeNames == null || typeNames.length == 0) {
            return null;
        }
        if (typeNames.length == 1) {
        	return typeNames[0];
        }
        LinkedHashSet<String> commonConversions = null;
        Set<String> types = new LinkedHashSet<String>();
        Set<String> conversions = null;
        boolean first = true;
        for (int i = 0; i < typeNames.length && (first || !commonConversions.isEmpty()); i++) {
        	String string = typeNames[i];
			if (string == null) {
				return null;
			}
			if (DataTypeManagerService.DefaultDataTypes.NULL.getId().equals(string) || !types.add(string)) {
				continue;
			}
			if (first) {
				commonConversions = new LinkedHashSet<String>();
		        // A type can be implicitly converted to itself, so we put the implicit
		        // conversions as well as the original type in the working list of
		        // conversions.
		        commonConversions.add(string);
		        DataTypeManagerService.getInstance(teiidVersion).getImplicitConversions(string, commonConversions);
		        first = false;
			} else {
				if (conversions == null) {
					conversions = new HashSet<String>();
				}
				DataTypeManagerService.getInstance(teiidVersion).getImplicitConversions(string, conversions);
	            conversions.add(string);
	            // Equivalent to set intersection
	            commonConversions.retainAll(conversions);
	            conversions.clear();
			}

		}
        // If there is only one type, then simply return it
        if (types.size() == 1) {
            return types.iterator().next();
        }
        if (types.isEmpty()) {
        	return DataTypeManagerService.DefaultDataTypes.NULL.getId();
        }
        for (String string : types) {
            if (commonConversions.contains(string)) {
                return string;
            }
        }
    	commonConversions.remove(DefaultDataTypes.STRING.getId());
        commonConversions.remove(DefaultDataTypes.OBJECT.getId());
        if (!commonConversions.isEmpty()) {
            return commonConversions.iterator().next(); 
        }
        return null;
    }

    /**
     * Gets whether there exists an implicit conversion from the source type to
     * the target type
     * @param teiidVersion 
     * @param fromType
     * @param toType
     * @return true if there exists an implicit conversion from the
     * <code>fromType</code> to the <code>toType</code>.
     */
    public static boolean canImplicitlyConvert(ITeiidServerVersion teiidVersion, String fromType, String toType) {
        if (fromType.equals(toType)) return true;
        return DataTypeManagerService.getInstance(teiidVersion).isImplicitConversion(fromType, toType);
    }

    /**
     * Replaces a sourceExpression with a conversion of the source expression
     * to the target type. If the source type and target type are the same,
     * this method does nothing.
     * @param sourceExpression
     * @param targetTypeName
     * @return
     * @throws Exception 
     */
    public static Expression convertExpression(Expression sourceExpression, String targetTypeName, IQueryMetadataInterface metadata) throws Exception {
        return convertExpression(sourceExpression,
                                 DataTypeManagerService.getInstance(sourceExpression.getTeiidVersion()).getDataTypeName(sourceExpression.getType()),
                                 targetTypeName, metadata);
    }

    /**
     * Replaces a sourceExpression with a conversion of the source expression
     * to the target type. If the source type and target type are the same,
     * this method does nothing.
     * @param sourceExpression
     * @param sourceTypeName
     * @param targetTypeName
     * @return
     * @throws Exception 
     */
    public static Expression convertExpression(Expression sourceExpression, String sourceTypeName, String targetTypeName, IQueryMetadataInterface metadata) throws Exception {
        if (sourceTypeName.equals(targetTypeName)) {
            return sourceExpression;
        }
        
        if(canImplicitlyConvert(sourceExpression.getTeiidVersion(), sourceTypeName, targetTypeName)) {
            return getConversion(sourceExpression, sourceTypeName, targetTypeName, true, (FunctionLibrary) metadata.getFunctionLibrary());
        }

        if (sourceExpression instanceof Constant) {
            Expression result = convertConstant(sourceTypeName, targetTypeName, (Constant)sourceExpression);
            if (result != null) {
                if (result.getType() == DataTypeManagerService.DefaultDataTypes.TIMESTAMP.getTypeClass()) {
                    return result;
                }
                return getConversion(sourceExpression, sourceTypeName, targetTypeName, true, (FunctionLibrary) metadata.getFunctionLibrary());
            }
        }

        //Expression is wrong type and can't convert
         throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30082, new Object[] {targetTypeName, sourceExpression, sourceTypeName}));
    }

    public static Constant convertConstant(String sourceTypeName,
                                           String targetTypeName,
                                           Constant constant) {
        if (!DataTypeManagerService.getInstance(constant.getTeiidVersion()).isTransformable(sourceTypeName, targetTypeName)) {
        	return null;
        }

        try {
	        //try to get the converted constant, if this fails then it is not in a valid format
	        Constant result = getProperlyTypedConstant(constant.getValue(),
	                                                   DataTypeManagerService.getInstance(constant.getTeiidVersion()).getDataTypeClass(targetTypeName),
	                                                   constant.getTeiidParser());

	        if (DataTypeManagerService.DefaultDataTypes.STRING.getId().equals(sourceTypeName)) {
	        	if (DataTypeManagerService.DefaultDataTypes.CHAR.getId().equals(targetTypeName)) {
	        		String value = (String)constant.getValue();
	        		if (value != null && value.length() != 1) {
	        			return null;
	        		}
	        	}
	        	return result;
	        }
	        
	        //for non-strings, ensure that the conversion is consistent
	        if (!DataTypeManagerService.getInstance(constant.getTeiidVersion()).isTransformable(targetTypeName, sourceTypeName)) {
	        	return null;
	        }
        
	        if (!(constant.getValue() instanceof Comparable)) {
	        	return null; //this is the case for xml constants
	        }
	        
	        Constant reverse = getProperlyTypedConstant(result.getValue(), constant.getType(), constant.getTeiidParser());
	        
	        if (((Comparable)constant.getValue()).compareTo(reverse.getValue()) == 0) {
	            return result;
	        }
        } catch (QueryResolverException e) {
        	
        }
            
        return null;
    }

    public static Function getConversion(Expression sourceExpression,
                                            String sourceTypeName,
                                            String targetTypeName,
                                            boolean implicit, FunctionLibrary library) {
        DataTypeManagerService dataTypeManagerService = DataTypeManagerService.getInstance(sourceExpression.getTeiidVersion());
        Class<?> srcType = dataTypeManagerService.getDataTypeClass(sourceTypeName);
        Class<?> targetType = dataTypeManagerService.getDataTypeClass(targetTypeName);

        try {
            setDesiredType(sourceExpression, targetType, sourceExpression);
        } catch (Exception e) {
            // Ignore exception
        }

        FunctionDescriptor fd = library.findTypedConversionFunction(srcType, dataTypeManagerService.getDataTypeClass(targetTypeName));

        Constant constant = sourceExpression.getTeiidParser().createASTNode(ASTNodes.CONSTANT);
        constant.setValue(targetTypeName);
        TeiidParser teiidParser = sourceExpression.getTeiidParser();
        Function conversion = teiidParser.createASTNode(ASTNodes.FUNCTION);
        conversion.setName(fd.getName());
        conversion.setArgs(new Expression[] { sourceExpression, constant });
        conversion.setType(dataTypeManagerService.getDataTypeClass(targetTypeName));
        conversion.setFunctionDescriptor(fd);
        if (implicit) {
        	conversion.makeImplicit();
        }

        return conversion;
    }

    public static void setDesiredType(List<DerivedColumn> passing, LanguageObject obj) throws Exception {
    	setDesiredType(passing, obj, DataTypeManagerService.DefaultDataTypes.XML.getTypeClass());
    }
    
    public static void setDesiredType(List<DerivedColumn> passing, LanguageObject obj, Class<?> type) throws Exception {
		for (DerivedColumn dc : passing) {
	    	setDesiredType(dc.getExpression(), type, obj);
		}
	}

    /**
     * Utility to set the type of an expression if it is a Reference and has a null type.
     * @param expression the expression to test
     * @param targetType the target type, if the expression's type is null.
     * @throws Exception if unable to set the reference type to the target type.
     */
    public static void setDesiredType(Expression expression, Class<?> targetType, LanguageObject surroundingExpression) throws Exception {
        if (expression instanceof Reference) {
        	Reference ref = (Reference)expression;
        	if (ref.isPositional() && ref.getType() == null) {
	        	if (targetType == null) {
	        		 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30083, surroundingExpression));
	        	}
	            ref.setType(targetType);
        	}
        } else if (expression instanceof Function) {
        	Function f = (Function)expression;
        	if (f.getType() == null) {
	        	f.setType(targetType);
        	}
        }
    }
    
	/**
	 * Attempt to resolve the order by throws Exception if the
	 * symbol is not of SingleElementSymbol type
	 * 
	 * @param orderBy
	 * @param fromClauseGroups
	 *            groups of the FROM clause of the query (for resolving
	 *            ambiguous unqualified element names), or empty List if a Set
	 *            Query Order By is being resolved
	 * @param knownElements
	 *            resolved elements from SELECT clause, which are only ones
	 *            allowed to be in ORDER BY
	 * @param metadata
	 *            IQueryMetadataInterface
	 */
    public static void resolveOrderBy(OrderBy orderBy, QueryCommand command, TempMetadataAdapter metadata)
        throws Exception {

        ITeiidServerVersion teiidVersion = orderBy.getTeiidVersion();
    	List<Expression> knownElements = command.getProjectedQuery().getSelect().getProjectedSymbols();
    	
    	boolean isSimpleQuery = false;
    	List<GroupSymbol> fromClauseGroups = Collections.emptyList();
        GroupBy groupBy = null;
        if (command instanceof Query) {
        	Query query = (Query)command;
        	isSimpleQuery = !query.getSelect().isDistinct() && !query.hasAggregates();
        	if (query.getFrom() != null) {
        		fromClauseGroups = query.getFrom().getGroups();
        	}
        	if (!query.getSelect().isDistinct() && teiidVersion.isGreaterThanOrEqualTo(Version.TEIID_8_10)) {
                groupBy = query.getGroupBy();
            }
        }

        // Cached state, if needed
        String[] knownShortNames = new String[knownElements.size()];
        List<Expression> expressions = new ArrayList<Expression>(knownElements.size());

        for(int i=0; i<knownElements.size(); i++) {
            Expression knownSymbol = knownElements.get(i);
            expressions.add(SymbolMap.getExpression(knownSymbol));
            if (knownSymbol instanceof ElementSymbol || knownSymbol instanceof AliasSymbol) {
                String name = ((Symbol)knownSymbol).getShortName();
                
                knownShortNames[i] = name;
            }
        }

        for (int i = 0; i < orderBy.getVariableCount(); i++) {
        	Expression sortKey = orderBy.getVariable(i);
        	if (sortKey instanceof ElementSymbol) {
        		ElementSymbol symbol = (ElementSymbol)sortKey;
        		String groupPart = null;
        		if (symbol.getGroupSymbol() != null) {
        			groupPart = symbol.getGroupSymbol().getName();
        		}
        		String symbolName = symbol.getName();
        		String shortName = symbol.getShortName();
        		if (groupPart == null) {
        			int position = -1;
    				Expression matchedSymbol = null;
    				// walk the SELECT col short names, looking for a match on the current ORDER BY 'short name'
    				for(int j=0; j<knownShortNames.length; j++) {
    					if( !shortName.equalsIgnoreCase( knownShortNames[j] )) {
    						continue;
    					}
    			        // if we already have a matched symbol, matching again here means it is duplicate/ambiguous
    			        if(matchedSymbol != null) {
    			        	if (!matchedSymbol.equals(knownElements.get(j))) {
    			        		 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30084, symbolName));
    			        	}
    			        	continue;
    			        }
    			        matchedSymbol = knownElements.get(j);
    			        position = j;
    				}
    				if (matchedSymbol != null) {
    				    TempMetadataID tempMetadataID = new TempMetadataID(symbol.getName(), matchedSymbol.getType());
    				    symbol.setMetadataID(tempMetadataID);
    				    symbol.setType(matchedSymbol.getType());
    				}
                    if (position != -1) {
                        orderBy.setExpressionPosition(i, position);
                        continue;
                    }
        		}
        	} else if (sortKey instanceof ExpressionSymbol && orderBy.getTeiidVersion().isLessThan(Version.TEIID_8_0)) {
        	 // check for legacy positional
                ExpressionSymbol es = (ExpressionSymbol)sortKey;
        	    if (es.getExpression() instanceof Constant) {
                    Constant c = (Constant)es.getExpression();
                    setExpressionPosition(orderBy, knownElements, i, c);
                    continue;
        	    }
        	} else if (sortKey instanceof Constant) {
        		// check for legacy positional
        		Constant c = (Constant)sortKey;
    		    setExpressionPosition(orderBy, knownElements, i, c);
    		    continue;
        	}
        	//handle order by expressions        	
        	if (command instanceof SetQuery) {
    			 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30086, sortKey));
    		}

        	//resolve subqueries
            for (SubqueryContainer container : ValueIteratorProviderCollectorVisitor.getValueIteratorProviders(sortKey)) {
                Command c = container.getCommand();
                QueryResolver queryResolver = new QueryResolver(command.getTeiidVersion());
                queryResolver.setChildMetadata(c, command);
                c.pushNewResolvingContext(fromClauseGroups);

                queryResolver = new QueryResolver(command.getTeiidVersion());
                queryResolver.resolveCommand(c, metadata.getMetadata(), false);
            }

        	ResolverVisitor visitor = new ResolverVisitor(command.getTeiidVersion());
        	for (ElementSymbol symbol : ElementCollectorVisitor.getElements(sortKey, false)) {
        		try {
        	    	visitor.resolveLanguageObject(symbol, fromClauseGroups, command.getExternalGroupContexts(), metadata);
        	    } catch(Exception e) {
        	    	 throw new QueryResolverException(e, Messages.gs(Messages.TEIID.TEIID30087, symbol.getName()) );
        	    } 
			}
            visitor.resolveLanguageObject(sortKey, metadata);
            
            int index = expressions.indexOf(SymbolMap.getExpression(sortKey));
            // if unrelated and not a simple query - that is more than just a grouping, throw an exception
            if (index == -1 && !isSimpleQuery) {
                if (teiidVersion.isGreaterThanOrEqualTo(Version.TEIID_8_10) && groupBy == null)
                    throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30088, sortKey));
                else
                    throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30088, sortKey));
        	}  
        	orderBy.setExpressionPosition(i, index);
        }
    }

    /**
     * @param orderBy
     * @param knownElements
     * @param i
     * @param c
     * @throws QueryResolverException
     */
    private static void setExpressionPosition(OrderBy orderBy, List<Expression> knownElements, int i, Constant c)
        throws QueryResolverException {
        int elementOrder = Integer.valueOf(c.getValue().toString()).intValue();
        // adjust for the 1 based index.
        if (elementOrder > knownElements.size() || elementOrder < 1) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30085, c));
        }
        orderBy.setExpressionPosition(i, elementOrder - 1);
    }
    
    /** 
     * Get the default value for the parameter, which could be null
     * if the parameter is set to NULLABLE.  If no default is available,
     * a Exception will be thrown.
     * 
     * @param symbol ElementSymbol retrieved from metadata, fully-resolved
     * @param metadata IQueryMetadataInterface
     * @return expr param (if it is non-null) or default value (if there is one)
     * or null Constant (if parameter is optional and therefore allows this)
     * @throws Exception if expr is null, parameter is required and no
     * default value is defined
     * @throws Exception for error retrieving metadata
     * @throws Exception
     * @since 4.3
     */
	public static Expression getDefault(ElementSymbol symbol, IQueryMetadataInterface metadata) throws Exception {
        //Check if there is a default value, if so use it
		Object mid = symbol.getMetadataID();
    	Class<?> type = symbol.getType();
    	ITeiidServerVersion teiidVersion = symbol.getTeiidVersion();
        String defaultValue = metadata.getDefaultValue(mid);
        
        if (defaultValue == null && !metadata.elementSupports(mid, SupportConstants.Element.NULL)) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30089, symbol.getOutputName()));
        }

        if (teiidVersion.isGreaterThanOrEqualTo(Version.TEIID_8_12_4) && "expression".equalsIgnoreCase(metadata.getExtensionProperty(mid,  BaseColumn.DEFAULT_HANDLING, false))) { //$NON-NLS-1$
            Expression ex = null;
            
            try {
                QueryParser parser = new QueryParser(teiidVersion);
                ex = parser.parseExpression(defaultValue);
            } catch (Exception e) {
                //TODO: also validate this at load time
                throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31170, symbol));
            }
            ValueIteratorProviderCollectorVisitor visitor = new ValueIteratorProviderCollectorVisitor(teiidVersion);
            List<SubqueryContainer> subqueries = visitor.getValueIteratorProviders(ex);
            ResolverVisitor resolverVisitor = new ResolverVisitor(teiidVersion);
            resolverVisitor.resolveLanguageObject(ex, metadata);
            for (SubqueryContainer<?> container : subqueries) {
                QueryResolver queryResolver = new QueryResolver(teiidVersion);
                queryResolver.resolveCommand(container.getCommand(), metadata);
            }
            return ResolverUtil.convertExpression(ex, DataTypeManagerService.getInstance(teiidVersion).getDataTypeName(type), metadata);
        }

        return getProperlyTypedConstant(defaultValue, type, symbol.getTeiidParser());
	}    
	
	public static boolean hasDefault(Object mid, IQueryMetadataInterface metadata) throws Exception {
        Object defaultValue = metadata.getDefaultValue(mid);
        
        return defaultValue != null || metadata.elementSupports(mid, SupportConstants.Element.NULL);
	}
    
    /** 
     * Construct a Constant with proper type, given the String default
     * value for the parameter and the parameter type.  Throw a
     * Exception if the String can't be transformed.
     * @param defaultValue either null or a String
     * @param parameterType modeled type of parameter (MetaMatrix runtime type)
     * @param teiidParser parser for creation of the new AST Constant node
     * @return Constant with proper type and default value Object of proper Class.  Will
     * be null Constant if defaultValue is null.
     * @throws Exception if TransformationException is encountered
     * @since 4.3
     */
    public static Constant getProperlyTypedConstant(Object defaultValue, Class<?> parameterType, TeiidParser teiidParser)
        throws QueryResolverException{
        ITeiidServerVersion version = teiidParser.getVersion();
        DataTypeManagerService dataTypeManager = DataTypeManagerService.getInstance(version);

        try {            
            Object newValue = dataTypeManager.transformValue(defaultValue, parameterType);
            Constant constant = teiidParser.createASTNode(ASTNodes.CONSTANT);
            constant.setValue(newValue);
            constant.setType(parameterType);
            return constant;
        } catch (Exception e) {
            if (version.isGreaterThanOrEqualTo(Version.TEIID_8_12_4)) {
                //timestamp literals also allow date format
                if (parameterType == DataTypeManagerService.DefaultDataTypes.TIMESTAMP.getTypeClass()) {
                    try {
                        Object newValue = dataTypeManager.transformValue(defaultValue,
                                                                         DataTypeManagerService.DefaultDataTypes.DATE);
                        Constant constant = teiidParser.createASTNode(ASTNodes.CONSTANT);
                        constant.setValue(new Timestamp(((Date)newValue).getTime()));
                        constant.setType(parameterType);
                        return constant;
                    } catch (Exception e1) {
                    }
                }
            }
             throw new QueryResolverException(e, Messages.gs(Messages.TEIID.TEIID30090, defaultValue, defaultValue.getClass(), parameterType));
        }
    }

    /**
     * Returns the resolved elements in the given group.  This method has the side effect of caching the resolved elements on the group object.
     * The resolved elements may not contain non-selectable columns depending on the metadata first used for resolving.
     * 
     */
    public static List<ElementSymbol> resolveElementsInGroup(GroupSymbol group, IQueryMetadataInterface metadata)
    throws Exception {
        return new ArrayList<ElementSymbol>(getGroupInfo(group, metadata).getSymbolList());
    }
    
    public static void clearGroupInfo(GroupSymbol group, IQueryMetadataInterface metadata) throws Exception {
    	metadata.addToMetadataCache(group.getMetadataID(), GroupInfo.CACHE_PREFIX + group.getName(), null);
    }
    
	static GroupInfo getGroupInfo(GroupSymbol group,
			IQueryMetadataInterface metadata)
			throws Exception {
		String key = GroupInfo.CACHE_PREFIX + group.getName();
		GroupInfo groupInfo = (GroupInfo)metadata.getFromMetadataCache(group.getMetadataID(), key);
    	
        if (groupInfo == null) {
        	group = group.clone();
            // get all elements from the metadata
            List elementIDs = metadata.getElementIDsInGroupID(group.getMetadataID());

    		LinkedHashMap<Object, ElementSymbol> symbols = new LinkedHashMap<Object, ElementSymbol>(elementIDs.size());

    		TeiidParser parser = group.getTeiidParser();
            for (Object elementID : elementIDs) {
            	String elementName = metadata.getName(elementID);
            	String elementType = metadata.getElementType(elementID);
            	Class<?> elementTypeClass = DataTypeManagerService.getInstance(group.getTeiidVersion()).getDataTypeClass(elementType);

                // Form an element symbol from the ID
            	ElementSymbol element = parser.createASTNode(ASTNodes.ELEMENT_SYMBOL);
            	element.setShortName(elementName);
            	element.setGroupSymbol(group);
                element.setMetadataID(elementID);
                element.setType(elementTypeClass);

                symbols.put(elementID, element);
            }

            groupInfo = new GroupInfo(symbols);
            metadata.addToMetadataCache(group.getMetadataID(), key, groupInfo);
        }
		return groupInfo;
	}
    
    /**
     * When access patterns are flattened, they are an approximation the user
     * may need to enter as criteria.
     *  
     * @param metadata
     * @param groups
     * @param flatten
     * @return
     * @throws Exception
     */
	public static List getAccessPatternElementsInGroups(final IQueryMetadataInterface metadata, Collection groups, boolean flatten) throws Exception {
		List accessPatterns = null;
		Iterator i = groups.iterator();
		while (i.hasNext()){
		    
		    GroupSymbol group = (GroupSymbol)i.next();
		    
		    //Check this group for access pattern(s).
		    Collection accessPatternIDs = metadata.getAccessPatternsInGroup(group.getMetadataID());
		    if (accessPatternIDs != null && accessPatternIDs.size() > 0){
		        Iterator j = accessPatternIDs.iterator();
		        if (accessPatterns == null){
		            accessPatterns = new ArrayList();
		        }
		        while (j.hasNext()) {
		        	List elements = metadata.getElementIDsInAccessPattern(j.next());
		        	GroupInfo groupInfo = getGroupInfo(group, metadata);
		        	List result = new ArrayList(elements.size());
		        	for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
		    			Object id = iterator.next();
		    			ElementSymbol symbol = groupInfo.getSymbol(id);
		    			assert symbol != null;
		    			result.add(symbol);
		    		}
		        	if (flatten) {
		        		accessPatterns.addAll(result);
		        	} else {
		        		accessPatterns.add(new AccessPattern(result));
		        	}
		        }
		    }
		}
                
		return accessPatterns;
	}

    public static void resolveLimit(Limit limit) throws Exception {
        if (limit.getOffset() != null) {
            setDesiredType(limit.getOffset(), DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(), limit);
        }
        setDesiredType(limit.getRowLimit(), DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(), limit);
    }
    
    public static void resolveImplicitTempGroup(TempMetadataAdapter metadata, GroupSymbol symbol, List symbols) 
        throws Exception {
        
        if (symbol.isImplicitTempGroupSymbol()) {
            if (metadata.getMetadataStore().getTempElementElementIDs(symbol.getName())==null) {
                addTempGroup(metadata, symbol, symbols, true);
            }
            ResolverUtil.resolveGroup(symbol, metadata);
        }
    }

    public static TempMetadataID addTempGroup(TempMetadataAdapter metadata,
                                    GroupSymbol symbol,
                                    List<? extends Expression> symbols, boolean tempTable) throws Exception {
        Set<String> names = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        for (Expression ses : symbols) {
            if (!names.add(Symbol.getShortName(ses))) {
                 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30091, symbol, Symbol.getShortName(ses)));
            }
        }
        
        if (tempTable) {
            resolveNullLiterals(symbols);
        }
        TempMetadataStore store = metadata.getMetadataStore();
        return store.addTempGroup(symbol.getName(), symbols, !tempTable, tempTable);
    }
    
    public static TempMetadataID addTempTable(TempMetadataAdapter metadata,
                                     GroupSymbol symbol,
                                     List<? extends Expression> symbols) throws Exception {
        return addTempGroup(metadata, symbol, symbols, true);
    }

    /** 
     * Look for a null literal in the SELECT clause and set it's type to STRING.  This ensures that 
     * the result set metadata retrieved for this query will be properly set to something other than
     * the internal NullType.  Added for defect 15437.
     * 
     * @param select The select clause
     * @since 4.2
     */
    public static void resolveNullLiterals(List symbols) {
        for (int i = 0; i < symbols.size(); i++) {
            Expression selectSymbol = (Expression) symbols.get(i);
            
            setTypeIfNull(selectSymbol, DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass());
        }
    }

	public static void setTypeIfNull(Expression symbol,
			Class<?> replacement) {
		if(!DataTypeManagerService.DefaultDataTypes.NULL.getTypeClass().equals(symbol.getType()) && symbol.getType() != null) {
            return;
        }
		symbol = SymbolMap.getExpression(symbol);
	    if(symbol instanceof Constant) {                	
	    	((Constant)symbol).setType(replacement);
	    } else if (symbol instanceof CaseExpression) {
	        ((CaseExpression)symbol).setType(replacement);
	    } else if (symbol instanceof SearchedCaseExpression) {
	        ((SearchedCaseExpression)symbol).setType(replacement);
	    } else if (symbol instanceof ScalarSubquery) {
	        ((ScalarSubquery)symbol).setType(replacement);                                        
	    } else if(symbol instanceof ElementSymbol) {
		    ElementSymbol elementSymbol = (ElementSymbol)symbol;
	        elementSymbol.setType(replacement);
		} else {
	    	try {
				ResolverUtil.setDesiredType(symbol, replacement, symbol);
			} catch (Exception e) {
				//cannot happen
			}
		}  
	}
    
    /**
     *  
     * @param groupContext
     * @param groups
     * @param metadata
     * @return the List of groups that match the given groupContext out of the supplied collection
     * @throws Exception
     */
    public static List<GroupSymbol> findMatchingGroups(String groupContext,
                            Collection<GroupSymbol> groups,
                            IQueryMetadataInterface metadata) throws Exception {

        if (groups == null) {
            return null;
        }

        LinkedList<GroupSymbol> matchedGroups = new LinkedList<GroupSymbol>();

        if (groupContext == null) {
            matchedGroups.addAll(groups);
        } else {
        	for (GroupSymbol group : groups) {
                String fullName = group.getName();
                if (nameMatchesGroup(groupContext, matchedGroups, group, fullName)) {
                    if (groupContext.length() == fullName.length()) {
                        return matchedGroups;
                    }
                    continue;
                }

                // don't try to vdb qualify temp metadata
                if (group.getMetadataID() instanceof TempMetadataID) {
                    continue;
                }

                String actualVdbName = metadata.getVirtualDatabaseName();

                if (actualVdbName != null) {
                    fullName = actualVdbName + Symbol.SEPARATOR + fullName;
                    if (nameMatchesGroup(groupContext, matchedGroups, group, fullName)
                        && groupContext.length() == fullName.length()) {
                        return matchedGroups;
                    }
                }
            }
        }

        return matchedGroups;
    }

    
    public static boolean nameMatchesGroup(String groupContext,
                                            String fullName) {
        //if there is a name match, make sure that it is the full name or a proper qualifier
        if (StringUtil.endsWithIgnoreCase(fullName, groupContext)) {
            int matchIndex = fullName.length() - groupContext.length();
            if (matchIndex == 0 || fullName.charAt(matchIndex - 1) == '.') {
                return true;
            }
        }
        return false;
    }
    
    private static boolean nameMatchesGroup(String groupContext,
                                            LinkedList<GroupSymbol> matchedGroups,
                                            GroupSymbol group,
                                            String fullName) {
        if (nameMatchesGroup(groupContext, fullName)) {
            matchedGroups.add(group);
            return true;
        }
        return false;
    }

	/**
	 * Check the type of the (left) expression and the type of the single
	 * projected symbol of the subquery.  If they are not the same, try to find
	 * an implicit conversion from the former type to the latter type, and wrap
	 * the left expression in that conversion function; otherwise throw an
	 * Exception.
	 * @param expression the Expression on one side of the predicate criteria
	 * @param crit the SubqueryContainer containing the subquery Command of the other
	 * side of the predicate criteria
	 * @return implicit conversion Function, or null if none is necessary
	 * @throws Exception if a conversion is necessary but none can
	 * be found
	 */
	static Expression resolveSubqueryPredicateCriteria(Expression expression, SubqueryContainer crit, IQueryMetadataInterface metadata)
		throws Exception {
	
		// Check that type of the expression is same as the type of the
		// single projected symbol of the subquery
		Class<?> exprType = expression.getType();
		if(exprType == null) {
	         throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30075, expression));
		}
		String exprTypeName = DataTypeManagerService.getInstance(expression.getTeiidVersion()).getDataTypeName(exprType);
	
		Collection<Expression> projectedSymbols = crit.getCommand().getProjectedSymbols();
		if (projectedSymbols.size() != 1){
	         throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30093, crit.getCommand()));
		}
		Class<?> subqueryType = projectedSymbols.iterator().next().getType();
		String subqueryTypeName = DataTypeManagerService.getInstance(expression.getTeiidVersion()).getDataTypeName(subqueryType);
		Expression result = null;
	    try {
	        result = convertExpression(expression, exprTypeName, subqueryTypeName, metadata);
	    } catch (QueryResolverException qre) {
	         throw new QueryResolverException(qre, Messages.gs(Messages.TEIID.TEIID30094, crit));
	    }
	    return result;
	}

	public static ResolvedLookup resolveLookup(Function lookup, IQueryMetadataInterface metadata) throws Exception {
		Expression[] args = lookup.getArgs();
		TeiidParser parser = lookup.getTeiidParser();

		ResolvedLookup result = new ResolvedLookup();
	    // Special code to handle setting return type of the lookup function to match the type of the return element
	    if( !(args[0] instanceof Constant) || !(args[1] instanceof Constant) || !(args[2] instanceof Constant)) {
		     throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30095));
	    }
        // If code table name in lookup function refers to temp group throw exception
		GroupSymbol groupSym = parser.createASTNode(ASTNodes.GROUP_SYMBOL);
		groupSym.setName((String) ((Constant)args[0]).getValue());
		try {
			groupSym.setMetadataID(metadata.getGroupID((String) ((Constant)args[0]).getValue()));
			if (groupSym.getMetadataID() instanceof TempMetadataID) {
				 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30096, ((Constant)args[0]).getValue()));
			}
		} catch(QueryResolverException e) {
			 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30097, ((Constant)args[0]).getValue()));
		}
		result.setGroup(groupSym);
		
		List<GroupSymbol> groups = Arrays.asList(groupSym);
		
		String returnElementName = (String) ((Constant)args[0]).getValue() + "." + (String) ((Constant)args[1]).getValue(); //$NON-NLS-1$
		ElementSymbol returnElement = parser.createASTNode(ASTNodes.ELEMENT_SYMBOL);
		returnElement.setName(returnElementName);
		ResolverVisitor visitor = new ResolverVisitor(parser.getVersion());
        try {
            visitor.resolveLanguageObject(returnElement, groups, metadata);
        } catch(Exception e) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30098, returnElementName));
        }
		result.setReturnElement(returnElement);
        
        String keyElementName = (String) ((Constant)args[0]).getValue() + "." + (String) ((Constant)args[2]).getValue(); //$NON-NLS-1$
        ElementSymbol keyElement = parser.createASTNode(ASTNodes.ELEMENT_SYMBOL);
        keyElement.setName(keyElementName);
        try {
            visitor.resolveLanguageObject(keyElement, groups, metadata);
        } catch(Exception e) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30099, keyElementName));
        }
		result.setKeyElement(keyElement);
		args[3] = convertExpression(args[3], DataTypeManagerService.getInstance(lookup.getTeiidVersion()).getDataTypeName(keyElement.getType()), metadata);
		return result;
	}

	private static QueryResolverException handleUnresolvedGroup(GroupSymbol symbol, String description) {
		UnresolvedSymbolDescription usd = new UnresolvedSymbolDescription(symbol.toString(), description);
		QueryResolverException e = new QueryResolverException(usd.getDescription()+": "+usd.getSymbol()); //$NON-NLS-1$
        e.setUnresolvedSymbols(Arrays.asList(usd));
        return e;
	}

	public static void resolveGroup(GroupSymbol symbol, IQueryMetadataInterface metadata)
	    throws Exception {
	
	    if (symbol.getMetadataID() != null){
	        return;
	    }
	
	    // determine the "metadataID" part of the symbol to look up
	    String potentialID = symbol.getNonCorrelationName();
	    
	    String name = symbol.getName();
	    String definition = symbol.getDefinition();
	
	    Object groupID = null;
	    try {
	        // get valid GroupID for possibleID - this may throw exceptions if group is invalid
	        groupID = metadata.getGroupID(potentialID);
	    } catch(Exception e) {
	        // didn't find this group ID
	    } 
	
	    // If that didn't work, try to strip a vdb name from potentialID
	    if(groupID == null) {
	    	String[] parts = potentialID.split("\\.", 2); //$NON-NLS-1$
	    	if (parts.length > 1 && parts[0].equalsIgnoreCase(metadata.getVirtualDatabaseName())) {
	            try {
	                groupID = metadata.getGroupID(parts[1]);
	            } catch(Exception e) {
	                // ignore - just didn't find it
	            } 
	            if(groupID != null) {
	            	potentialID = parts[1];
	            }
	        }
	    }
	
	    // the group could be partially qualified,  verify that this group exists
	    // and there is only one group that matches the given partial name
	    if(groupID == null) {
	    	Collection groupNames = null;
	    	try {
	        	groupNames = metadata.getGroupsForPartialName(potentialID);
	        } catch(Exception e) {
	            // ignore - just didn't find it
	        } 
	
	        if(groupNames != null) {
	            int matches = groupNames.size();
	            if(matches == 1) {
	            	potentialID = (String) groupNames.iterator().next();
			        try {
			            // get valid GroupID for possibleID - this may throw exceptions if group is invalid
			            groupID = metadata.getGroupID(potentialID);
			        } catch(Exception e) {
			            // didn't find this group ID
			        } 
	            } else if(matches > 1) {
	                throw handleUnresolvedGroup(symbol, Messages.getString(Messages.ERR.ERR_015_008_0055));
	            }
	        }
	    }
	    
	    if (groupID == null || metadata.isProcedure(groupID)) {
		    //try procedure relational resolving
	        try {
	            IStoredProcedureInfo storedProcedureInfo = metadata.getStoredProcedureInfoForProcedure(potentialID);
	            symbol.setProcedure(true);
	            groupID = storedProcedureInfo.getProcedureID();
	        } catch(Exception e) {
	            // just ignore
	        } 
	    }
	    
	    if(groupID == null) {
	        throw handleUnresolvedGroup(symbol, Messages.getString(Messages.ERR.ERR_015_008_0056));
	    }
	    // set real metadata ID in the symbol
	    symbol.setMetadataID(groupID);
	    potentialID = metadata.getFullName(groupID);
        if(symbol.getDefinition() == null) {
            symbol.setName(potentialID);
        } else {
            symbol.setDefinition(potentialID);
        }
	    try {
	        if (!symbol.isProcedure()) {
	            symbol.setIsTempTable(metadata.isTemporaryTable(groupID));
	        }
	    } catch(Exception e) {
	        // should not come here
	    } 
	    
	    if (metadata.useOutputName()) {
	    	symbol.setOutputDefinition(definition);
	    	symbol.setOutputName(name);
		}
	}
	
	public static void findKeyPreserved(Query query, Set<GroupSymbol> keyPreservingGroups, IQueryMetadataInterface metadata)
	throws Exception {
		if (query.getFrom() == null) {
			return;
		}
		if (query.getFrom().getClauses().size() == 1) {
			findKeyPreserved(query.getFrom().getClauses().get(0), keyPreservingGroups, metadata);
			return;
		}
		//non-ansi join
		Set<GroupSymbol> groups = new HashSet<GroupSymbol>(query.getFrom().getGroups());
		for (GroupSymbol groupSymbol : groups) {
			if (metadata.getUniqueKeysInGroup(groupSymbol.getMetadataID()).isEmpty()) {
				return;
			}
		}
		LinkedList<Expression> leftExpressions = new LinkedList<Expression>();
		LinkedList<Expression> rightExpressions = new LinkedList<Expression>();
		for (Criteria crit : Criteria.separateCriteriaByAnd(query.getCriteria())) {
			if (!(crit instanceof CompareCriteria)) {
				continue;
			}
			CompareCriteria cc = (CompareCriteria)crit;
			if (cc.getOperator() != CompareCriteria.EQ) {
				continue;
			}
			if (cc.getLeftExpression() instanceof ElementSymbol && cc.getRightExpression() instanceof ElementSymbol) {
				ElementSymbol left = (ElementSymbol)cc.getLeftExpression();
				ElementSymbol right = (ElementSymbol)cc.getRightExpression();
				int compare = left.getGroupSymbol().compareTo(right.getGroupSymbol());
				if (compare > 0) {
					leftExpressions.add(left);
					rightExpressions.add(right);
				} else if (compare != 0) {
					leftExpressions.add(right);
					rightExpressions.add(left);
				}
			}
		}
		HashMap<List<GroupSymbol>, List<HashSet<Object>>> crits = createGroupMap(leftExpressions, rightExpressions);
		HashSet<GroupSymbol> tempSet = new HashSet<GroupSymbol>();
		HashSet<GroupSymbol> nonKeyPreserved = new HashSet<GroupSymbol>();
		for (GroupSymbol group : groups) {
			LinkedHashSet<GroupSymbol> visited = new LinkedHashSet<GroupSymbol>();
			LinkedList<GroupSymbol> toVisit = new LinkedList<GroupSymbol>();
			toVisit.add(group);
			while (!toVisit.isEmpty()) {
				GroupSymbol visiting = toVisit.removeLast();
				if (!visited.add(visiting) || nonKeyPreserved.contains(visiting)) {
					continue;
				}
				if (keyPreservingGroups.contains(visiting)) {
					visited.addAll(groups);
					break;
				}
				toVisit.addAll(findKeyPreserved(tempSet, Collections.singleton(visiting), crits, true, metadata, groups));
				toVisit.addAll(findKeyPreserved(tempSet, Collections.singleton(visiting), crits, false, metadata, groups));
			}
			if (visited.containsAll(groups)) {
				keyPreservingGroups.add(group);
			} else {
				nonKeyPreserved.add(group);
			}
		}
	}

	/**
	 * Taken originally from RuleChooseJoinStrategy
	 *
	 * @param leftGroups
	 * @param rightGroups
	 * @param leftExpressions
	 * @param rightExpressions
	 * @param crits
	 * @param nonEquiJoinCriteria
	 */
    private static void separateCriteria(Collection<GroupSymbol> leftGroups, Collection<GroupSymbol> rightGroups, List<Expression> leftExpressions, List<Expression> rightExpressions, List<Criteria> crits, List<Criteria> nonEquiJoinCriteria) {
        for (Criteria theCrit : crits) {
            Set<GroupSymbol> critGroups = GroupsUsedByElementsVisitor.getGroups(theCrit);

            if (leftGroups.containsAll(critGroups) || rightGroups.containsAll(critGroups)) {
                nonEquiJoinCriteria.add(theCrit);
                continue;
            }

            if (!(theCrit instanceof CompareCriteria)) {
                nonEquiJoinCriteria.add(theCrit);
                continue;
            }

            CompareCriteria crit = (CompareCriteria)theCrit;
            if (crit.getOperator() != CompareCriteria.EQ) {
                nonEquiJoinCriteria.add(theCrit);
                continue;
            }

            Expression leftExpr = crit.getLeftExpression();
            Expression rightExpr = crit.getRightExpression();

            Set<GroupSymbol> leftExprGroups = GroupsUsedByElementsVisitor.getGroups(leftExpr);
            Set<GroupSymbol> rightExprGroups = GroupsUsedByElementsVisitor.getGroups(rightExpr);

            if (leftGroups.isEmpty() || rightGroups.isEmpty()) {
                nonEquiJoinCriteria.add(theCrit);
            } else if (leftGroups.containsAll(leftExprGroups) && rightGroups.containsAll(rightExprGroups)) {
                leftExpressions.add(leftExpr);
                rightExpressions.add(rightExpr);
            } else if (rightGroups.containsAll(leftExprGroups) && leftGroups.containsAll(rightExprGroups)) {
                leftExpressions.add(rightExpr);
                rightExpressions.add(leftExpr);
            } else {
                nonEquiJoinCriteria.add(theCrit);
            }
        }
    }
	
	public static void findKeyPreserved(FromClause clause, Set<GroupSymbol> keyPreservingGroups, IQueryMetadataInterface metadata)
	throws Exception {
		if (clause instanceof UnaryFromClause) {
			UnaryFromClause ufc = (UnaryFromClause)clause;
		    
			if (!metadata.getUniqueKeysInGroup(ufc.getGroup().getMetadataID()).isEmpty()) {
				keyPreservingGroups.add(ufc.getGroup());
			}
		} 
		if (clause instanceof JoinPredicate) {
			JoinPredicate jp = (JoinPredicate)clause;
			JoinType joinType = jp.getJoinType();
			if (joinType.getKind().equals(Types.JOIN_CROSS) || joinType.getKind().equals(Types.JOIN_FULL_OUTER)) {
				return;
			}
			HashSet<GroupSymbol> leftPk = new HashSet<GroupSymbol>();
			findKeyPreserved(jp.getLeftClause(), leftPk, metadata);
			HashSet<GroupSymbol> rightPk = new HashSet<GroupSymbol>();
			findKeyPreserved(jp.getRightClause(), rightPk, metadata);
			
			if (leftPk.isEmpty() && rightPk.isEmpty()) {
				return;
			}
			
			HashSet<GroupSymbol> leftGroups = new HashSet<GroupSymbol>();
			HashSet<GroupSymbol> rightGroups = new HashSet<GroupSymbol>();
			jp.getLeftClause().collectGroups(leftGroups);
			jp.getRightClause().collectGroups(rightGroups);
			
			LinkedList<Expression> leftExpressions = new LinkedList<Expression>();
			LinkedList<Expression> rightExpressions = new LinkedList<Expression>();
			separateCriteria(leftGroups, rightGroups, leftExpressions, rightExpressions, jp.getJoinCriteria(), new LinkedList<Criteria>());
		    
			HashMap<List<GroupSymbol>, List<HashSet<Object>>> crits = createGroupMap(leftExpressions, rightExpressions);
			if (!leftPk.isEmpty() && (joinType.getKind().equals(Types.JOIN_INNER) || joinType.getKind().equals(Types.JOIN_LEFT_OUTER))) {
				findKeyPreserved(keyPreservingGroups, leftPk, crits, true, metadata, rightPk);
			} 
			if (!rightPk.isEmpty() && (joinType.getKind().equals(Types.JOIN_INNER) || joinType.getKind().equals(Types.JOIN_RIGHT_OUTER))) {
				findKeyPreserved(keyPreservingGroups, rightPk, crits, false, metadata, leftPk);
			}
		}
	}

	private static HashMap<List<GroupSymbol>, List<HashSet<Object>>> createGroupMap(
			LinkedList<Expression> leftExpressions,
			LinkedList<Expression> rightExpressions) {
		HashMap<List<GroupSymbol>, List<HashSet<Object>>> crits = new HashMap<List<GroupSymbol>, List<HashSet<Object>>>();
		
		for (int i = 0; i < leftExpressions.size(); i++) {
			Expression lexpr = leftExpressions.get(i);
			Expression rexpr = rightExpressions.get(i);
			if (!(lexpr instanceof ElementSymbol) || !(rexpr instanceof ElementSymbol)) {
				continue;
			}
			ElementSymbol les = (ElementSymbol)lexpr;
			ElementSymbol res = (ElementSymbol)rexpr;
			List<GroupSymbol> tbls = Arrays.asList(les.getGroupSymbol(), res.getGroupSymbol());
			List<HashSet<Object>> ids = crits.get(tbls);
			if (ids == null) {
				ids = Arrays.asList(new HashSet<Object>(), new HashSet<Object>());
				crits.put(tbls, ids);
			}
			ids.get(0).add(les.getMetadataID());
			ids.get(1).add(res.getMetadataID());
		}
		return crits;
	}

	/**
	 * Taken from RuleRaiseAccess
	 */
    private static boolean matchesForeignKey(IQueryMetadataInterface metadata, Collection<Object> leftIds, Collection<Object> rightIds, GroupSymbol leftGroup, boolean exact)
        throws Exception {
        Collection fks = metadata.getForeignKeysInGroup(leftGroup.getMetadataID());
        for (Object fk : fks) {
            String allow = metadata.getExtensionProperty(fk, ForeignKey.ALLOW_JOIN, false);
            if (allow != null && !Boolean.valueOf(allow)) {
                continue;
            }
            List fkColumns = metadata.getElementIDsInKey(fk);
            if ((exact && leftIds.size() != fkColumns.size()) || !leftIds.containsAll(fkColumns)) {
                continue;
            }
            Object pk = metadata.getPrimaryKeyIDForForeignKeyID(fk);
            List pkColumns = metadata.getElementIDsInKey(pk);
            if ((!exact || rightIds.size() == pkColumns.size()) && rightIds.containsAll(pkColumns)) {
                return true;
            }
        }
        return false;
    }

	static private HashSet<GroupSymbol> findKeyPreserved(Set<GroupSymbol> keyPreservingGroups,
		Set<GroupSymbol> pk,
		HashMap<List<GroupSymbol>, List<HashSet<Object>>> crits, boolean left, IQueryMetadataInterface metadata, Set<GroupSymbol> otherGroups)
		throws Exception {
		HashSet<GroupSymbol> result = new HashSet<GroupSymbol>();
		for (GroupSymbol gs : pk) {
			for (Map.Entry<List<GroupSymbol>, List<HashSet<Object>>> entry : crits.entrySet()) {
				if (!entry.getKey().get(left?0:1).equals(gs) || !otherGroups.contains(entry.getKey().get(left?1:0))) {
					continue;
				}
				if (matchesForeignKey(metadata, entry.getValue().get(left?0:1), entry.getValue().get(left?1:0), gs, false)) {
					keyPreservingGroups.add(gs);
					result.add(entry.getKey().get(left?1:0));
				}
			}
		}
		return result;
	}

	/**
	 * This method will convert all elements in a command to their fully qualified name.
	 * @param command Command to convert
	 */
	public static void fullyQualifyElements(Command command) {
	    Collection<ElementSymbol> elements = ElementCollectorVisitor.getElements(command, false, true);
	    for (ElementSymbol element : elements) {
	        element.setDisplayFullyQualified(true);
	    }
	}
    
}
