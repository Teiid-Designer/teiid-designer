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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.api.exception.query.UnresolvedSymbolDescription;
import org.teiid.core.CoreConstants;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.util.ArgCheck;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.IResolverVisitor;
import org.teiid.designer.query.sql.symbol.IElementSymbol.DisplayMode;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.function.FunctionLibrary.ConversionResult;
import org.teiid.query.metadata.GroupInfo;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.ExpressionCriteria;
import org.teiid.query.sql.lang.GroupContext;
import org.teiid.query.sql.lang.IsDistinctCriteria;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.navigator.PostOrderNavigator;
import org.teiid.query.sql.proc.ExceptionExpression;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.Array;
import org.teiid.query.sql.symbol.CaseExpression;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.DerivedColumn;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.QueryString;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.SearchedCaseExpression;
import org.teiid.query.sql.symbol.XMLExists;
import org.teiid.query.sql.symbol.XMLQuery;
import org.teiid.query.sql.symbol.XMLSerialize;
import org.teiid.query.sql.symbol.v7.Aggregate7Symbol;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;


public class ResolverVisitor extends LanguageVisitor
    implements IResolverVisitor<LanguageObject, GroupSymbol> {
    
    public static final String TEIID_PASS_THROUGH_TYPE = "teiid:pass-through-type"; //$NON-NLS-1$

	private static final String SYS_PREFIX = CoreConstants.SYSTEM_MODEL + '.';

	@Removed(Version.TEIID_8_5)
    private ThreadLocal<Boolean> determinePartialName = new ThreadLocal<Boolean>() {
    	@Override
        protected Boolean initialValue() {
    		return false;
    	}
    };

    private Collection<GroupSymbol> groups;
    private GroupContext externalContext;
    protected IQueryMetadataInterface metadata;
    private Exception componentException;
    private Exception resolverException;
    private Map<Function, Exception> unresolvedFunctions;
    private boolean findShortName;
    private List<ElementSymbol> matches = new ArrayList<ElementSymbol>(2);
    private List<GroupSymbol> groupMatches = new ArrayList<GroupSymbol>(2);
	@Since(Version.TEIID_8_6)
    private boolean hasUserDefinedAggregate;
    
    /**
     * Constructor for ResolverVisitor.
     * 
     * External groups are ordered from inner to outer most
     * @param teiidVersion
     */
    public ResolverVisitor(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    /**
     * Constructor for ResolverVisitor.
     *
     * @param teiidVersion
     * @param metadata
     * @param groups
     * @param externalContext
     */
    public ResolverVisitor(ITeiidServerVersion teiidVersion, IQueryMetadataInterface metadata, Collection<GroupSymbol> internalGroups, GroupContext externalContext) {
        this(teiidVersion);
		this.groups = internalGroups;
        this.externalContext = externalContext;
        this.metadata = metadata;
        setFindShortName(metadata);
    }

    private void setFindShortName(IQueryMetadataInterface metadata) {
        if (getTeiidVersion().isGreaterThanOrEqualTo(Version.TEIID_8_5))
            this.findShortName = metadata.findShortName();
        else
            this.findShortName = determinePartialName.get();
    }
    
	/**
	 * @param groups
	 */
	public void setGroups(Collection<GroupSymbol> groups) {
		this.groups = groups;
	}

    @Override
    public void visit(ElementSymbol obj) {
        try {
            resolveElementSymbol(obj);
        } catch(Exception e) {
            handleException(handleUnresolvedElement(obj, e.getMessage()));
        }
    }

    private QueryResolverException handleUnresolvedElement(ElementSymbol symbol, String description) {
    	UnresolvedSymbolDescription usd = new UnresolvedSymbolDescription(symbol.toString(), description);
    	QueryResolverException e = new QueryResolverException(usd.getDescription());
        e.setUnresolvedSymbols(Arrays.asList(usd));
        return e;
    }

    private void resolveElementSymbol(ElementSymbol elementSymbol)
        throws Exception {

        // already resolved
        if(elementSymbol.getMetadataID() != null) {
        	return;
        }
        
        // look up group and element parts of the potentialID
        String groupContext = null;
        if (elementSymbol.getGroupSymbol() != null) {
        	groupContext = elementSymbol.getGroupSymbol().getName();
        }
        String elementShortName = elementSymbol.getShortName();
        if (groupContext != null) {
            groupContext = elementSymbol.getGroupSymbol().getName();
        	try {
				if (findShortName && internalResolveElementSymbol(elementSymbol, null, elementShortName, groupContext)) {
		    		elementSymbol.setDisplayMode(DisplayMode.SHORT_OUTPUT_NAME);
		    		return;
				}
			} catch (Exception e) {
				//ignore
			}
        }
        
        internalResolveElementSymbol(elementSymbol, groupContext, elementShortName, null);
   }

	private boolean internalResolveElementSymbol(ElementSymbol elementSymbol,
			String groupContext, String shortCanonicalName, String expectedGroupContext)
			throws Exception {
		boolean isExternal = false;
        boolean groupMatched = false;
        
        GroupContext root = null;
        
        if (groups != null || externalContext != null) {
            if (groups != null) {
                root = new GroupContext(externalContext, groups);
            }
            if (root == null) {
                isExternal = true;
                root = externalContext;
            }
        } else {
            try {
                LinkedList<GroupSymbol> matchedGroups = new LinkedList<GroupSymbol>();
                
                if (groupContext != null) {
                    //assume that this is fully qualified
                    Object groupID = this.metadata.getGroupID(groupContext);
                    // No groups specified, so any group is valid
                    GroupSymbol groupSymbol = getTeiidParser().createASTNode(ASTNodes.GROUP_SYMBOL);
                    groupSymbol.setName(groupContext);
                    groupSymbol.setMetadataID(groupID);
                    matchedGroups.add(groupSymbol);
                }
                
                root = new GroupContext(null, matchedGroups);
            } catch(Exception e) {
                // ignore 
            }
        }
        
        matches.clear();
        groupMatches.clear();
        while (root != null) {
            Collection<GroupSymbol> matchedGroups = ResolverUtil.findMatchingGroups(groupContext, root.getGroups(), metadata);
            if (matchedGroups != null && !matchedGroups.isEmpty()) {
                groupMatched = true;
                    
                resolveAgainstGroups(shortCanonicalName, matchedGroups);
                
                if (matches.size() > 1) {
            	    throw handleUnresolvedElement(elementSymbol, Messages.gs(Messages.TEIID.TEIID31117, elementSymbol, groupMatches));
                }
                
                if (matches.size() == 1) {
                    break;
                }
            }
            
            root = root.getParent();
            isExternal = true;
        }
        
        if (matches.isEmpty()) {
            if (groupMatched) {
                throw handleUnresolvedElement(elementSymbol, Messages.gs(Messages.TEIID.TEIID31118, elementSymbol)); 
            }
            throw handleUnresolvedElement(elementSymbol, Messages.gs(Messages.TEIID.TEIID31119, elementSymbol)); 
        }
        //copy the match information
        ElementSymbol resolvedSymbol = matches.get(0);
        GroupSymbol resolvedGroup = groupMatches.get(0);
        String oldName = elementSymbol.getOutputName();
        if (expectedGroupContext != null && !ResolverUtil.nameMatchesGroup(expectedGroupContext, resolvedGroup.getName())) {
        	return false;
        }
        elementSymbol.setIsExternalReference(isExternal);
        elementSymbol.setType(resolvedSymbol.getType());
        elementSymbol.setMetadataID(resolvedSymbol.getMetadataID());
        elementSymbol.setGroupSymbol(resolvedGroup);
        elementSymbol.setShortName(resolvedSymbol.getShortName());
        if (metadata.useOutputName()) {
        	elementSymbol.setOutputName(oldName);
        }
        return true;
	}
    
    private void resolveAgainstGroups(String elementShortName,
                                      Collection<GroupSymbol> matchedGroups) throws Exception {
    	for (GroupSymbol group : matchedGroups) {
            GroupInfo groupInfo = ResolverUtil.getGroupInfo(group, metadata);
            
            ElementSymbol result = groupInfo.getSymbol(elementShortName);
            if (result != null) {
            	matches.add(result);
            	groupMatches.add(group);
            }
        }
    }
        
    @Override
    public void visit(BetweenCriteria obj) {
        try {
            resolveBetweenCriteria(obj);
        } catch(Exception e) {
            handleException(e);
        }
    }

    @Override
    public void visit(CompareCriteria obj) {
        try {
            resolveCompareCriteria(obj);
        } catch(Exception e) {
            handleException(e);
        }
    }

    @Override
    public void visit(MatchCriteria obj) {
        try {
            resolveMatchCriteria(obj);
        } catch(Exception e) {
            handleException(e);
        }
    }

    @Override
    public void visit(SetCriteria obj) {
        try {
            resolveSetCriteria(obj);
        } catch(Exception e) {
            handleException(e);
        }
    }

    @Override
    public void visit(SubqueryCompareCriteria obj) {
        try {
            obj.setLeftExpression(ResolverUtil.resolveSubqueryPredicateCriteria(obj.getLeftExpression(), obj, metadata));
        } catch(Exception e) {
            handleException(e);
        }
    }

    @Override
    public void visit(SubquerySetCriteria obj) {
        try {
            obj.setExpression(ResolverUtil.resolveSubqueryPredicateCriteria(obj.getExpression(), obj, metadata));
        } catch(Exception e) {
            handleException(e);
        }
    }

    @Override
    public void visit(IsNullCriteria obj) {
        try {
        	setDesiredType(obj.getExpression(), DefaultDataTypes.OBJECT.getTypeClass(), obj);
        } catch(Exception e) {
            handleException(e);
        }
    }

    @Override
    public void visit(IsDistinctCriteria isDistinctCriteria) {
        try {
            ResolverUtil.resolveGroup(isDistinctCriteria.getLeftRowValue(), metadata);
            ResolverUtil.resolveGroup(isDistinctCriteria.getRightRowValue(), metadata);
        } catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public void visit(Function obj) {
        try {
            resolveFunction(obj, (FunctionLibrary) this.metadata.getFunctionLibrary());
			if (obj.isAggregate() && isTeiidVersionOrGreater(Version.TEIID_8_6)) {
            	hasUserDefinedAggregate = true;
            }
        } catch(Exception e) {
            String msg = e.getMessage();
        	if (msg != null && (msg.contains(Messages.TEIID.TEIID30069.name()) || msg.contains(Messages.TEIID.TEIID30067.name()))) {
	        	if (unresolvedFunctions == null) {
	        		unresolvedFunctions = new LinkedHashMap<Function, Exception>();
	        	}
	        	unresolvedFunctions.put(obj, e);
        	} else {
        		handleException(e);
        	}
        }
    }
    
    @Override
    public void visit(Array array) {
    	try {
	    	if (array.getComponentType() != null) {
	    		String type = getDataTypeManager().getDataTypeName(array.getComponentType());
	    		for (int i = 0; i < array.getExpressions().size(); i++) {
	    			Expression expr = array.getExpressions().get(i);
	    			setDesiredType(expr, array.getComponentType(), array);
	    			if (array.getComponentType() != DefaultDataTypes.OBJECT.getTypeClass()) {
	    				array.getExpressions().set(i, ResolverUtil.convertExpression(expr, type, metadata));
	    			}
	    		}
	    	} else {
                Class<?> type = null;
                for (int i = 0; i < array.getExpressions().size(); i++) {
                    Expression expr = array.getExpressions().get(i);
                    Class<?> baseType = expr.getType();
                    while (baseType != null && baseType.isArray()) {
                        baseType = baseType.getComponentType();
                    }
                    if (baseType != DefaultDataTypes.NULL.getTypeClass()) {
                        if (type == null) {
                            type = expr.getType();
                        } else if (type != expr.getType()) {
                            type = DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass();
                        }
                    }
                }
                if (type == null) {
                    type = DefaultDataTypes.NULL.getTypeClass();
                }
                array.setComponentType(type);
            }
    	} catch (Exception e) {
    		handleException(e);
    	}
    }

    @Override
    public void visit(CaseExpression obj) {
        try {
            resolveCaseExpression(obj);
        } catch(Exception e) {
            handleException(e);
        }
    }
    
    @Override
    public void visit(SearchedCaseExpression obj) {
        try {
            resolveSearchedCaseExpression(obj);
        } catch(Exception e) {
            handleException(e);
        }
    }
    
    @Override
    public void visit(SetClause obj) {
    	String type = getDataTypeManager().getDataTypeName(obj.getSymbol().getType());
    	try {
    		setDesiredType(obj.getValue(), obj.getSymbol().getType(), obj);
            obj.setValue(ResolverUtil.convertExpression(obj.getValue(), type, metadata));                    
        } catch(Exception e) {
            handleException(new QueryResolverException(e, Messages.getString(Messages.QueryResolver.setClauseResolvingError, new Object[] {obj.getValue(), obj.getSymbol(), type})));
        } 
    }
    
    @Override
    public void visit(XMLSerialize obj) {
    	try {
			obj.setExpression(ResolverUtil.convertExpression(obj.getExpression(), DefaultDataTypes.XML.getId(), metadata));
		} catch (Exception e) {
			handleException(new QueryResolverException(e, Messages.getString(Messages.QueryResolver.xmlSerializeResolvingError, obj)));
		}
    }
    
    @Override
    public void visit(XMLQuery obj) {
    	try {
	    	ResolverUtil.setDesiredType(obj.getPassing(), obj);
			obj.compileXqueryExpression();
		} catch (Exception e) {
			handleException(e); 
		}
    }

    @Since(Version.TEIID_8_10)
    @Override
    public void visit(XMLExists obj) {
        visit(obj.getXmlQuery());
    }

    @Override
    public void visit(QueryString obj) {
    	try {
			obj.setPath(ResolverUtil.convertExpression(obj.getPath(), DefaultDataTypes.STRING.getId(), metadata));
			for (DerivedColumn col : obj.getArgs()) {
				col.setExpression(ResolverUtil.convertExpression(col.getExpression(), DefaultDataTypes.STRING.getId(), metadata));
			}
		} catch (Exception e) {
			handleException(new QueryResolverException(e, Messages.getString(Messages.QueryResolver.xmlQueryResolvingError, obj)));
		}
    }
    
    @Override
    public void visit(ExpressionCriteria obj) {
		try {
			obj.setExpression(ResolverUtil.convertExpression(obj.getExpression(), DefaultDataTypes.BOOLEAN.getId(), metadata));
		} catch (Exception e) {
			handleException(e);
		}
    }
    
    @Override
    public void visit(ExceptionExpression obj) {
    	try {
    		if (obj.getErrorCode() != null) {
    			obj.setErrorCode(ResolverUtil.convertExpression(obj.getErrorCode(), DefaultDataTypes.INTEGER.getId(), metadata));
    		}
			obj.setMessage(ResolverUtil.convertExpression(obj.getMessage(), DefaultDataTypes.STRING.getId(), metadata));
			if (obj.getSqlState() != null) {
				obj.setSqlState(ResolverUtil.convertExpression(obj.getSqlState(), DefaultDataTypes.STRING.getId(), metadata));
			}
			checkException(obj.getParent());
		} catch (Exception e) {
			handleException(e);
		}
    }

	public static void checkException(Expression obj)
			throws QueryResolverException {
		if (obj == null || obj instanceof ExceptionExpression) {
			return;
		}
		if (obj instanceof ElementSymbol) {
			ElementSymbol es = (ElementSymbol)obj;
			if (!(es.getMetadataID() instanceof TempMetadataID)) {
				throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31120, obj));
			}
			TempMetadataID tid = (TempMetadataID)es.getMetadataID();
			if (tid.getType() != Exception.class) {
				throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31120, obj));
			}
		} else if (obj instanceof Constant) {
			Constant c = (Constant)obj;
			if (!(c.getValue() instanceof Exception)) {
				throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31120, obj));
			}
		} else {
			throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31120, obj));
		}
	}
    
    @Override
    public void visit(AggregateSymbol obj) {
    	if (obj.getCondition() != null) {
			try {
				obj.setCondition(ResolverUtil.convertExpression(obj.getCondition(), DefaultDataTypes.BOOLEAN.getId(), metadata));
			} catch (Exception e) {
				handleException(e);
			}
    	}

    	if (obj instanceof Aggregate7Symbol)
    	    return;

    	/* Following does not apply to 7.7.x aggregate symbols */

    	switch (obj.getAggregateFunction()) {
    	case USER_DEFINED:
    		visit((Function)obj);
    		break;
    	case STRING_AGG:
    		try {
	    		if (obj.getArgs().length != 2) {
	    			throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31140, obj));
	    		}
	    		if (obj.getType() == null) {
					Expression arg = obj.getArg(0);
					Expression arg1 = obj.getArg(1);
					Class<?> type = null;
					if (isBinary(arg)) {
						setDesiredType(arg1, DefaultDataTypes.BLOB.getTypeClass(), obj);
						if (isBinary(arg1)) {
							type = DefaultDataTypes.BLOB.getTypeClass();
						}
					} else if (isCharacter(arg, false)) {
						setDesiredType(arg1, DefaultDataTypes.CLOB.getTypeClass(), obj);
						if (isCharacter(arg1, false)) {
							type = DefaultDataTypes.CLOB.getTypeClass();
						}
					} else if (arg.getType() == null) {
						if (isBinary(arg1)) {
							setDesiredType(arg, DefaultDataTypes.BLOB.getTypeClass(), obj);
							if (isBinary(arg)) {
								type = DefaultDataTypes.BLOB.getTypeClass();
							}
						} else if (isCharacter(arg1, false)) {
							setDesiredType(arg, DefaultDataTypes.CLOB.getTypeClass(), obj);
							if (isCharacter(arg, false)) {
								type = DefaultDataTypes.CLOB.getTypeClass();
							}
						}
					}
					if (type == null) {
						throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31141, obj));
					}
	    			obj.setType(type);
	    		}
    		} catch (Exception e) {
				handleException(e);
			}
    		break;
    	}
    }

	private boolean isCharacter(Expression arg, boolean includeChar) {
	    Class<?> type = arg.getType();
        return isCharacter(type, includeChar);
	}

	static boolean isCharacter(Class<?> type, boolean includeChar) {
        return type == DefaultDataTypes.STRING.getTypeClass()
                || type == DefaultDataTypes.CLOB.getTypeClass()
        || (includeChar && type == DefaultDataTypes.CHAR.getTypeClass());
    }

	private boolean isBinary(Expression arg) {
		return arg.getType() == DefaultDataTypes.VARBINARY.getTypeClass()
				|| arg.getType() == DefaultDataTypes.BLOB.getTypeClass();
	}

    public Exception getComponentException() {
        return this.componentException;
    }

    public Exception getResolverException() {
        return this.resolverException;
    }

    void handleException(Exception e) {
        this.componentException = e;

        // Abort the validation process
        setAbort(true);
    }

	public void throwException(boolean includeUnresolvedFunctions)
			throws Exception {
		if(getComponentException() != null) {
            throw getComponentException();
        }

        if(getResolverException() != null) {
            throw getResolverException();
        }
        
        if (includeUnresolvedFunctions 
        		&& unresolvedFunctions != null && !unresolvedFunctions.isEmpty()) {
        	throw unresolvedFunctions.values().iterator().next();
        }
	}

	/**
	 * Resolve function such that all functions are resolved and type-safe.
	 */
	void resolveFunction(Function function, FunctionLibrary library)
	    throws Exception {
	
	    // Check whether this function is already resolved
	    if(function.getFunctionDescriptor() != null) {
	        return;
	    }
	
	    // Look up types for all args
	    boolean hasArgWithoutType = false;
	    Expression[] args = function.getArgs();
	    Class<?>[] types = new Class[args.length];
	    for(int i=0; i<args.length; i++) {
	        types[i] = args[i].getType();
	        if(types[i] == null) {
	        	if(!(args[i] instanceof Reference)){
	                 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30067, new Object[] {args[i], function}));
	        	}
	            hasArgWithoutType = true;
	        }
	    }
	
	    //special case handling for convert of an untyped reference
	    if (FunctionLibrary.isConvert(function) && hasArgWithoutType) {
	        Constant constant = (Constant)function.getArg(1);
	        Class<?> type = getDataTypeManager().getDataTypeClass((String)constant.getValue());
	
	        setDesiredType(function.getArg(0), type, function);
	        types[0] = type;
	        hasArgWithoutType = false;
	    }
	
	    // Attempt to get exact match of function for this signature
	    List<FunctionDescriptor> fds;
        try {
            fds = findWithImplicitConversions(library, function, args, types, hasArgWithoutType);

            if (fds.isEmpty()) {
                if (!library.hasFunctionMethod(function.getName(), args.length)) {
                    // Unknown function form
                    throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30068, function));
                }
                // Known function form - but without type information
                if (hasArgWithoutType) {
                    throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30069, function));
                }
                // Known function form - unable to find implicit conversions
                throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30070, function));
            }
            if (fds.size() > 1) {
                throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31150, function));
            }
        } catch (Exception e) {
            if (e instanceof QueryResolverException)
                throw e;

            // Known function form - but without type information
            if (hasArgWithoutType) {
                 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30069, function));
            }
            throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31150, function));
        }

        FunctionDescriptor fd = fds.get(0);
	    if (fd.getMethod().isVarArgs() 
	    		&& fd.getTypes().length == types.length 
	    		&& library.isVarArgArrayParam(fd.getMethod(), types, types.length - 1, fd.getTypes()[types.length - 1])) {
	    	fd = fd.clone();
	    	fd.setCalledWithVarArgArrayParam(true);
	    }
	    
	    if(fd.isSystemFunction(IFunctionLibrary.FunctionName.CONVERT) || fd.isSystemFunction(IFunctionLibrary.FunctionName.CAST)) {
	        String dataType = (String) ((Constant)args[1]).getValue();
	        Class<?> dataTypeClass = getDataTypeManager().getDataTypeClass(dataType);
	        fd = library.findTypedConversionFunction(args[0].getType(), dataTypeClass);
	
	        // Verify that the type conversion from src to type is even valid
	        Class<?> srcTypeClass = args[0].getType();
	        if(srcTypeClass != null && dataTypeClass != null &&
	           !srcTypeClass.equals(dataTypeClass) &&
	           !getDataTypeManager().isTransformable(srcTypeClass, dataTypeClass)) {
	
	             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30071, new Object[] {getDataTypeManager().getDataTypeName(srcTypeClass), dataType}));
	        }
	    } else if(fd.isSystemFunction(IFunctionLibrary.FunctionName.LOOKUP)) {
			ResolverUtil.ResolvedLookup lookup = ResolverUtil.resolveLookup(function, metadata);
			fd = library.copyFunctionChangeReturnType(fd, lookup.getReturnElement().getType());
	    } else if (fd.isSystemFunction(IFunctionLibrary.FunctionName.ARRAY_GET) && args[0].getType().isArray()) {
	        if (args[0].getType() != null && args[0].getType().isArray()) {
	    		//hack to use typed array values
				fd = library.copyFunctionChangeReturnType(fd, args[0].getType().getComponentType());
	    	} else {
	    		if (function.getType() != null) {
	    			setDesiredType(args[0], function.getType(), function);
	    		}
	    		if (args[0].getType() != DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass()) {
	    			throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31145, getDataTypeManager().getDataTypeName(args[0].getType()), function));
	    		}
	    	}
	    } else if (Boolean.valueOf(fd.getMethod().getProperty(TEIID_PASS_THROUGH_TYPE, false))) {
	    	//hack largely to support pg
	    	fd = library.copyFunctionChangeReturnType(fd, args[0].getType());
	    }
	
	    function.setFunctionDescriptor(fd);
	    function.setType(fd.getReturnType());
	    if (CoreConstants.SYSTEM_MODEL.equals(fd.getSchema()) && StringUtil.startsWithIgnoreCase(function.getName(), SYS_PREFIX)) {
	    	function.setName(function.getName().substring(SYS_PREFIX.length()));
	    }
	}

	/**
	 * Find possible matches based on implicit conversions of the arguments.
	 * NOTE: This method has the side-effect of explicitly inserting conversions into the function arguments,
	 * and thereby changing the structure of the function call.
	 * @param library
	 * @param function
	 * @param types
	 * @return
	 * @throws Exception 
	 * @since 4.3
	 */
	private List<FunctionDescriptor> findWithImplicitConversions(FunctionLibrary library, Function function, Expression[] args, Class<?>[] types, boolean hasArgWithoutType) throws Exception {
	    
	    // Try to find implicit conversion path to still perform this function
	    ConversionResult cr = null;
	    try {
	        cr = library.determineNecessaryConversions(function.getName(), function.getType(), args, types, hasArgWithoutType);
	    } catch (Exception ex) {
	        if (getTeiidVersion().isLessThan(TeiidServerVersion.Version.TEIID_8_9))
	            return Collections.emptyList();
	        else
	            throw ex;
	    }

        if (cr.method == null && getTeiidVersion().isGreaterThanOrEqualTo(TeiidServerVersion.Version.TEIID_8_9)) {
			return Collections.emptyList();
		}
		Class<?>[] newSignature = types;
	    
	    if(cr.needsConverion) {
	        FunctionDescriptor[] conversions = library.getConverts(cr.method, types);
		    newSignature = new Class[conversions.length];
		    // Insert new conversion functions as necessary, while building new signature
		    for(int i=0; i<conversions.length; i++) {
		        
		        Class<?> newType = types[i];
		        
		        if(conversions[i] != null) {
		            newType = conversions[i].getReturnType();
		            
		            setDesiredType(args[i], newType, function);
		                                
		            //only currently typed expressions need conversions
		            if (types[i] != null && newType != DefaultDataTypes.OBJECT.getTypeClass()) {
	                      //directly resolve constants
                        if (args[i] instanceof Constant && newType == DefaultDataTypes.TIMESTAMP.getTypeClass()) {
                            args[i] = ResolverUtil.getProperlyTypedConstant(((Constant)args[i]).getValue(), newType, getTeiidParser());
                        } else {
                            function.insertConversion(i, conversions[i]);
                        }
		            }
		        } 

		        newSignature[i] = newType;
		    }
	    }

	    // Now resolve using the new signature to get the function's descriptor
	    String name = cr.method != null && cr.method.getFullName() != null ? cr.method.getFullName() : function.getName();
	    return library.findAllFunctions(name, newSignature);
	}

	/**
	 * Resolves criteria "a BETWEEN b AND c". If type conversions are necessary,
	 * this method attempts the following implicit conversions:
	 * <br/>
	 * <ol type="1" start="1">
	 *   <li>convert the lower and upper expressions to the criteria expression's type, or</li>
	 *   <li>convert the criteria and upper expressions to the lower expression's type, or</li>
	 *   <li>convert the criteria and lower expressions to the upper expression's type, or</li>
	 *   <li>convert all expressions to a common type to which all three expressions' types can be implicitly converted.</li>
	 * </ol>
	 * @param criteria
	 * @throws Exception
	 * @throws Exception 
	 * @throws Exception
	 */
	void resolveBetweenCriteria(BetweenCriteria criteria)
	    throws Exception {
	
	    Expression exp = criteria.getExpression();
	    Expression lower = criteria.getLowerExpression();
	    Expression upper = criteria.getUpperExpression();
	
	    // invariants: none of the expressions is an aggregate symbol
	    setDesiredType(exp,
	                                   (lower.getType() == null)
	                                        ? upper.getType()
	                                        : lower.getType(), criteria);
	    // invariants: exp.getType() != null
	    setDesiredType(lower, exp.getType(), criteria);
	    setDesiredType(upper, exp.getType(), criteria);
	    // invariants: none of the types is null

	    if (exp.getType().equals(lower.getType()) && exp.getType().equals(upper.getType())) {
	        return;
	    }

	    String expTypeName = getDataTypeManager().getDataTypeName(exp.getType());
	    String lowerTypeName = getDataTypeManager().getDataTypeName(lower.getType());
	    String upperTypeName = getDataTypeManager().getDataTypeName(upper.getType());

	    //check if all types are the same, or if there is a common type
        String[] types = new String[2];
        types[0] = lowerTypeName;
        types[1] = upperTypeName;
        Class<?> type = null;
        
        String commonType = ResolverUtil.getCommonType(getTeiidVersion(), types);
	    if (commonType != null) {
	        type = getDataTypeManager().getDataTypeClass(commonType);
	    }

	    boolean exprChar = isCharacter(exp, true);
        
        if (exp.getType() != DefaultDataTypes.NULL.getTypeClass()) {
            boolean success = true;
            // try to apply cast
            // Apply cast and replace current value
            if (!exprChar || metadata.widenComparisonToString() || isCharacter(lower, true)) { 
                try {
                    criteria.setLowerExpression(ResolverUtil.convertExpression(lower, lowerTypeName, expTypeName, metadata) );
                    lower = criteria.getLowerExpression();
                    lowerTypeName = getDataTypeManager().getDataTypeName(lower.getType());
                } catch (QueryResolverException e) {
                    if (lower instanceof Constant && isCharacter(lower, true) && !metadata.widenComparisonToString()) {
                        throw e;
                    }
                    if (type == null) {
                        type = lower.getType();
                    }
                    success = false;
                }
            } else {
                success = false;
            }
            // try to apply cast
            // Apply cast and replace current value
            if (!exprChar || metadata.widenComparisonToString() || isCharacter(upper, true)) {
                try {
                    criteria.setUpperExpression(ResolverUtil.convertExpression(upper, upperTypeName, expTypeName, metadata) );
                    upper = criteria.getUpperExpression();
                    upperTypeName = getDataTypeManager().getDataTypeName(upper.getType());
                } catch (QueryResolverException e) {
                    if (lower instanceof Constant && isCharacter(lower, true) && !metadata.widenComparisonToString()) {
                        throw e;
                    }
                    if (type == null) {
                        type = upper.getType();
                    }
                    success = false;
                }
            } else {
                success = false;
            }
            if (success) {
                return;
            }
        }
    
        // If no convert found for first element, check whether everything in the
        // set is the same and the convert can be placed on the left side
        if (type == null) {
	        // Couldn't find a common type to implicitly convert to
	         throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30072, expTypeName, lowerTypeName, criteria));
	    }

     // Is there a possible conversion from left to right?
        String typeName = getDataTypeManager().getDataTypeName(type);
        
        if (!isCharacter(type, true) || metadata.widenComparisonToString() || exp.getType() == DefaultDataTypes.NULL.getTypeClass()) {
            criteria.setExpression(ResolverUtil.convertExpression(exp, expTypeName, typeName, metadata));
        } else if (type != exp.getType()) {
            throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31172, criteria));
        }
        
        if(lower.getType() != type) {
            if (!metadata.widenComparisonToString() && exprChar ^ isCharacter(lower, true)) {
                throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31172, criteria));
            }
            criteria.setLowerExpression(ResolverUtil.convertExpression(lower, lowerTypeName, typeName, metadata));
        }
        if(upper.getType() != type) {
            if (!metadata.widenComparisonToString() && exprChar ^ isCharacter(lower, true)) {
                throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31172, criteria));
            }
            criteria.setUpperExpression(ResolverUtil.convertExpression(upper, upperTypeName, typeName, metadata));
        }

	    // invariants: exp.getType() == lower.getType() == upper.getType()
	}

	void resolveCompareCriteria(CompareCriteria ccrit)
		throws Exception {
	
		Expression leftExpression = ccrit.getLeftExpression();
		Expression rightExpression = ccrit.getRightExpression();
	
		// Check typing between expressions
	    setDesiredType(leftExpression, rightExpression.getType(), ccrit);
	    setDesiredType(rightExpression, leftExpression.getType(), ccrit);
	
		if(leftExpression.getType() == rightExpression.getType()) {
			return;
		}
	
		// Try to apply an implicit conversion from one side to the other
		String leftTypeName = getDataTypeManager().getDataTypeName(leftExpression.getType());
		String rightTypeName = getDataTypeManager().getDataTypeName(rightExpression.getType());
	
		if (leftExpression.getType() == DefaultDataTypes.NULL.getTypeClass()) {
            ccrit.setLeftExpression(ResolverUtil.convertExpression(leftExpression, leftTypeName, rightTypeName, metadata) );
            return;
        }
        if (rightExpression.getType() == DefaultDataTypes.NULL.getTypeClass()) {
            ccrit.setRightExpression(ResolverUtil.convertExpression(rightExpression, rightTypeName, leftTypeName, metadata) );
            return;
        }
        
        boolean leftChar = isCharacter(leftExpression, true);
        boolean rightChar = isCharacter(rightExpression, true);

	    // Special cases when right expression is a constant
	    if(rightExpression instanceof Constant && !leftChar) {
	        // Auto-convert constant string on right to expected type on left
	        try {
	            ccrit.setRightExpression(ResolverUtil.convertExpression(rightExpression, rightTypeName, leftTypeName, metadata));
	            return;
	        } catch (Exception qre) {
	            if (rightChar && !metadata.widenComparisonToString()) {
                    throw qre;
                }
	        }
	    }
	    
	    // Special cases when left expression is a constant
	    if(leftExpression instanceof Constant && !rightChar) {
	        // Auto-convert constant string on left to expected type on right
	        try {
	            ccrit.setLeftExpression(ResolverUtil.convertExpression(leftExpression, leftTypeName, rightTypeName, metadata));
	            return;                                           
	        } catch (Exception qre) {
	            if (leftChar && !metadata.widenComparisonToString()) {
                    throw qre;
                }
	        }
	    }
	
	    // Try to apply a conversion generically

	    if ((rightChar ^ leftChar) && !metadata.widenComparisonToString()) {
            throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31172, ccrit));
        }

	    if(ResolverUtil.canImplicitlyConvert(getTeiidVersion(), leftTypeName, rightTypeName)) {
			ccrit.setLeftExpression(ResolverUtil.convertExpression(leftExpression, leftTypeName, rightTypeName, metadata) );
			return;
		}
	
		if(ResolverUtil.canImplicitlyConvert(getTeiidVersion(), rightTypeName, leftTypeName)) {
			ccrit.setRightExpression(ResolverUtil.convertExpression(rightExpression, rightTypeName, leftTypeName, metadata) );
			return;
	    }
	
		String commonType = ResolverUtil.getCommonType(getTeiidVersion(), new String[] {leftTypeName, rightTypeName});
		
		if (commonType == null) {
	        // Neither are aggs, but types can't be reconciled
	         throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30072, new Object[] { leftTypeName, rightTypeName, ccrit }));
		}
		ccrit.setLeftExpression(ResolverUtil.convertExpression(leftExpression, leftTypeName, commonType, metadata) );
		ccrit.setRightExpression(ResolverUtil.convertExpression(rightExpression, rightTypeName, commonType, metadata) );
	}

	void resolveMatchCriteria(MatchCriteria mcrit)
	    throws Exception {
	
	    setDesiredType(mcrit.getLeftExpression(), mcrit.getRightExpression().getType(), mcrit);
	    mcrit.setLeftExpression(resolveMatchCriteriaExpression(mcrit, mcrit.getLeftExpression()));
	
	    setDesiredType(mcrit.getRightExpression(), mcrit.getLeftExpression().getType(), mcrit);
	    mcrit.setRightExpression(resolveMatchCriteriaExpression(mcrit, mcrit.getRightExpression()));
	}

	/**
	 * Checks one side of a LIKE Criteria; implicitly converts to a String or CLOB if necessary.
	 * @param mcrit the Match Criteria
	 * @param expr either left or right expression
	 * @return either 'expr' itself, or a new implicit type conversion wrapping expr
	 * @throws Exception if no implicit type conversion is available
	 */
	Expression resolveMatchCriteriaExpression(MatchCriteria mcrit, Expression expr)
	throws Exception {
	    // Check left expression == string or CLOB
	    String type = getDataTypeManager().getDataTypeName(expr.getType());
	    Expression result = expr;
	    if(type != null) {
	        if (!isCharacter(expr, false)) {
	                
	            if(ResolverUtil.canImplicitlyConvert(getTeiidVersion(), type, DefaultDataTypes.STRING.getId())) {
	
	                result = ResolverUtil.convertExpression(expr, type, DefaultDataTypes.STRING.getId(), metadata);
	                
	            } else if (ResolverUtil.canImplicitlyConvert(getTeiidVersion(), type, DefaultDataTypes.CLOB.getId())){
	                    
	                result = ResolverUtil.convertExpression(expr, type, DefaultDataTypes.CLOB.getId(), metadata);
	
	            } else {
	                 throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30074, mcrit));
	            }
	        }
	    }
	    return result;
	}

	void resolveSetCriteria(SetCriteria scrit)
	    throws Exception {
	
	    // Check that each of the values are the same type as expression
	    Class<?> exprType = scrit.getExpression().getType();
	    if(exprType == null) {
	         throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30075, scrit.getExpression()));
	    }
	
	  //check if all types are the same, or if there is a common type
        boolean same = true;
        Iterator valIter = scrit.getValues().iterator();
        String[] types = new String[scrit.getValues().size()];
        int i = 0;
        Class<?> type = null;
        while(valIter.hasNext()) {
            Expression value = (Expression) valIter.next();
            if (value.getType() != exprType) {
                same = false;
            }
            types[i++] = getDataTypeManager().getDataTypeName(value.getType());
            type = value.getType();
        }
        if (same && type == exprType) {
            return;
        }
        
        if (!same) {
            String commonType = ResolverUtil.getCommonType(getTeiidVersion(), types);
            if (commonType != null) {
                type = getDataTypeManager().getDataTypeClass(commonType);
            } else {
                type = null;
            }
        }

	    String exprTypeName = getDataTypeManager().getDataTypeName(exprType);
	    boolean attemptConvert = !isCharacter(exprType, true) || metadata.widenComparisonToString();
        
        List<Expression> newVals = new ArrayList<Expression>(scrit.getValues().size());
        if (scrit.getExpression().getType() != DefaultDataTypes.NULL.getTypeClass()) {
            valIter = scrit.getValues().iterator();
            while(valIter.hasNext()) {
                Expression value = (Expression) valIter.next();
                setDesiredType(value, exprType, scrit);
                if(value.getType() != exprType) {
                    String valTypeName = getDataTypeManager().getDataTypeName(value.getType());
                    // try to apply cast
                    // Apply cast and replace current value
                    if (attemptConvert || isCharacter(value.getType(), true)) {
                        try {
                            newVals.add(ResolverUtil.convertExpression(value, valTypeName, exprTypeName, metadata) );
                        } catch (QueryResolverException e) {
                            if (value instanceof Constant && isCharacter(value, true) && !metadata.widenComparisonToString()) {
                                throw e;
                            }
                            if (type == null) {
                                type = value.getType();
                            }
                            break;
                        }
                    }
                } else {
                    newVals.add(value);
                }
            }
            if (newVals.size() == scrit.getValues().size()) {
                scrit.setValues(newVals);
                return;
            }
        }
    
        // If no convert found for first element, check whether everything in the
        // set is the same and the convert can be placed on the left side
        if (type == null) {
             throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30077, scrit));
        }
        
        // Is there a possible conversion from left to right?
        String setTypeName = getDataTypeManager().getDataTypeName(type);
        
        if (!isCharacter(type, true) || metadata.widenComparisonToString() || scrit.getExpression().getType() == DefaultDataTypes.NULL.getTypeClass()) {
            scrit.setExpression(ResolverUtil.convertExpression(scrit.getExpression(), exprTypeName, setTypeName, metadata));
        } else if (type != scrit.getExpression().getType()) {
            throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31172, scrit));
        }
        
        boolean exprChar = isCharacter(scrit.getExpression(), true);

        newVals.clear();
        valIter = scrit.getValues().iterator();
        while(valIter.hasNext()) {
            Expression value = (Expression) valIter.next();
            if(value.getType() == null) {
                throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30075, value));
            } else if(value.getType() != type) {
                if (!metadata.widenComparisonToString() && exprChar ^ isCharacter(value, true)) {
                    throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31172, scrit));
                }
                value = ResolverUtil.convertExpression(value, setTypeName, metadata);
            }
            newVals.add(value);
        }

        scrit.setValues(newVals);
	}

	void resolveCaseExpression(CaseExpression obj) throws Exception {
	    // If already resolved, do nothing
	    if (obj.getType() != null) {
	        return;
	    }
	    final int whenCount = obj.getWhenCount();
	    Expression expr = obj.getExpression();
	
	    Class<?> whenType = null;
	    Class<?> thenType = null;
	    // Get the WHEN and THEN types, and get a candidate type for each (for the next step)
	    for (int i = 0; i < whenCount; i++) {
	        if (whenType == null) {
	            whenType = obj.getWhenExpression(i).getType();
	        }
	        if (thenType == null) {
	            thenType = obj.getThenExpression(i).getType();
	        }
	    }
	
	    Expression elseExpr = obj.getElseExpression();
	    if (elseExpr != null) {
	        if (thenType == null) {
	            thenType = elseExpr.getType();
	        }
	    }
	    // Invariant: All the expressions contained in the obj are resolved (except References)
	
	    // 2. Attempt to set the target types of all contained expressions,
	    //    and collect their type names for the next step
	    ArrayList<String> whenTypeNames = new ArrayList<String>(whenCount + 1);
	    ArrayList<String> thenTypeNames = new ArrayList<String>(whenCount + 1);
	    setDesiredType(expr, whenType, obj);
	    // Add the expression's type to the WHEN types
	    whenTypeNames.add(getDataTypeManager().getDataTypeName(expr.getType()));
	    Expression when = null;
	    Expression then = null;
	    // Set the types of the WHEN and THEN parts
	    boolean whenNotChar = false;
	    for (int i = 0; i < whenCount; i++) {
	        when = obj.getWhenExpression(i);
	        then = obj.getThenExpression(i);
	
	        setDesiredType(when, expr.getType(), obj);
	        setDesiredType(then, thenType, obj);
	
	        if (!whenTypeNames.contains(getDataTypeManager().getDataTypeName(when.getType()))) {
	            whenTypeNames.add(getDataTypeManager().getDataTypeName(when.getType()));
	        }
	        if (!isCharacter(when.getType(), true)) {
                whenNotChar = true;
            }
	        if (!thenTypeNames.contains(getDataTypeManager().getDataTypeName(then.getType()))) {
	            thenTypeNames.add(getDataTypeManager().getDataTypeName(then.getType()));
	        }
	    }
	    // Set the type of the else expression
	    if (elseExpr != null) {
	        setDesiredType(elseExpr, thenType, obj);
	        if (!thenTypeNames.contains(getDataTypeManager().getDataTypeName(elseExpr.getType()))) {
	            thenTypeNames.add(getDataTypeManager().getDataTypeName(elseExpr.getType()));
	        }
	    }
	
	    // Invariants: all the expressions' types are non-null
	
	    // 3. Perform implicit type conversions
	    String whenTypeName = ResolverUtil.getCommonType(getTeiidVersion(), whenTypeNames.toArray(new String[whenTypeNames.size()]));
	    if (whenTypeName == null) {
	         throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30079, "WHEN", obj));//$NON-NLS-1$
	    }
	    if (!metadata.widenComparisonToString() && whenNotChar && isCharacter(getDataTypeManager().getDataTypeClass(whenTypeName), true)) {
            throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID31172, obj));
        }
	    String thenTypeName = ResolverUtil.getCommonType(getTeiidVersion(), thenTypeNames.toArray(new String[thenTypeNames.size()]));
	    if (thenTypeName == null) {
	         throw new QueryResolverException(Messages.gs(Messages.TEIID.TEIID30079, "THEN/ELSE", obj));//$NON-NLS-1$
	    }
	    obj.setExpression(ResolverUtil.convertExpression(obj.getExpression(), whenTypeName, metadata));
	    ArrayList<Expression> whens = new ArrayList<Expression>(whenCount);
	    ArrayList<Expression> thens = new ArrayList<Expression>(whenCount);
	    for (int i = 0; i < whenCount; i++) {
	        whens.add(ResolverUtil.convertExpression(obj.getWhenExpression(i), whenTypeName, metadata));
	        thens.add(ResolverUtil.convertExpression(obj.getThenExpression(i), thenTypeName, metadata));
	    }
	    obj.setWhen(whens, thens);
	    if (elseExpr != null) {
	        obj.setElseExpression(ResolverUtil.convertExpression(elseExpr, thenTypeName, metadata));
	    }
	    // Set this CASE expression's type to the common THEN type, and we're done.
	    obj.setType(getDataTypeManager().getDataTypeClass(thenTypeName));
	}

	private void setDesiredType(Expression obj, Class<?> type, LanguageObject surrounding) throws Exception {
		ResolverUtil.setDesiredType(obj, type, surrounding);
		//second pass resolving for functions
		if (!(obj instanceof Function)) {
			return;
		}
		if (unresolvedFunctions != null) {
			Function f = (Function)obj;
			if (f.getFunctionDescriptor() != null) {
				return;
			}
        	unresolvedFunctions.remove(obj);
			obj.acceptVisitor(this);
			Exception e = unresolvedFunctions.get(obj);
			if (e != null) {
				throw e;
			}
		}
	}

	void resolveSearchedCaseExpression(SearchedCaseExpression obj) throws Exception {
	    // If already resolved, do nothing
	    if (obj.getType() != null) {
	        return;
	    }
	    final int whenCount = obj.getWhenCount();
	    // 1. Call recursively to resolve any contained CASE expressions
	
	    Class<?> thenType = null;
	    // Get the WHEN and THEN types, and get a candidate type for each (for the next step)
	    for (int i = 0; i < whenCount; i++) {
	        if (thenType == null) {
	            thenType = obj.getThenExpression(i).getType();
	        }
	    }
	
	    Expression elseExpr = obj.getElseExpression();
	    if (elseExpr != null) {
	        if (thenType == null) {
	            thenType = elseExpr.getType();
	        }
	    }
	    // Invariant: All the expressions contained in the obj are resolved (except References)
	
	    // 2. Attempt to set the target types of all contained expressions,
	    //    and collect their type names for the next step
	    ArrayList<String> thenTypeNames = new ArrayList<String>(whenCount + 1);
	    Expression then = null;
	    // Set the types of the WHEN and THEN parts
	    for (int i = 0; i < whenCount; i++) {
	        then = obj.getThenExpression(i);
	        setDesiredType(then, thenType, obj);
            thenTypeNames.add(getDataTypeManager().getDataTypeName(then.getType()));
	    }
	    // Set the type of the else expression
	    if (elseExpr != null) {
	        setDesiredType(elseExpr, thenType, obj);
            thenTypeNames.add(getDataTypeManager().getDataTypeName(elseExpr.getType()));
	    }
	
	    // Invariants: all the expressions' types are non-null
	
	    // 3. Perform implicit type conversions
	    String thenTypeName = ResolverUtil.getCommonType(getTeiidVersion(), thenTypeNames.toArray(new String[thenTypeNames.size()]));
	    if (thenTypeName == null) {
	         throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID30079, "THEN/ELSE", obj)); //$NON-NLS-1$
	    }
	    ArrayList<Expression> thens = new ArrayList<Expression>(whenCount);
	    for (int i = 0; i < whenCount; i++) {
	        thens.add(ResolverUtil.convertExpression(obj.getThenExpression(i), thenTypeName, metadata));
	    }
	    obj.setWhen(obj.getWhen(), thens);
	    if (elseExpr != null) {
	        obj.setElseExpression(ResolverUtil.convertExpression(elseExpr, thenTypeName, metadata));
	    }
	    // Set this CASE expression's type to the common THEN type, and we're done.
	    obj.setType(getDataTypeManager().getDataTypeClass(thenTypeName));
	}
	
    @Override
    public void resolveLanguageObject(LanguageObject obj, IQueryMetadataInterface metadata)
    throws Exception {
	    resolveLanguageObject(obj, null, metadata);
	}
	
	@Override
    public void resolveLanguageObject(LanguageObject obj, Collection<GroupSymbol> groups, IQueryMetadataInterface metadata)
	    throws Exception {
	    resolveLanguageObject(obj, groups, null, metadata);
	}
	
	/**
	 * @param obj
	 * @param groups
	 * @param externalContext
	 * @param metadata
	 * @throws Exception
	 */
	public void resolveLanguageObject(LanguageObject obj, Collection<GroupSymbol> groups, GroupContext externalContext, IQueryMetadataInterface metadata)
	    throws Exception {
	
	    if(obj == null) {
	        return;
	    }

	    ArgCheck.isTrue(obj.getTeiidVersion().compareTo(getTeiidVersion()), "version of visitor should match version of object"); //$NON-NLS-1$

	    setGroups(groups);
        this.externalContext = externalContext;
        this.metadata = metadata;
        setFindShortName(metadata);

	    // Resolve elements, deal with errors
	    PostOrderNavigator.doVisit(obj, this);
	    this.throwException(true);
	}

	@Since(Version.TEIID_8_6)
	public boolean hasUserDefinedAggregate() {
		return hasUserDefinedAggregate;
	}

	@Deprecated
    @Override
    public void setProperty(String propertyName, Object value) {
        /* No longer required. To be removed on removal of deprecated client plugins */
    }
}
