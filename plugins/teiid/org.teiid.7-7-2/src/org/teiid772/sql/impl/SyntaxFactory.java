/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.metadata.IMetadataID;
import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria.LogicalOperator;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.ISPParameter.ParameterInfo;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.ISetQuery.Operation;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.designer.query.sql.lang.ISubqueryCompareCriteria;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.query.sql.lang.IUpdate;
import org.teiid.designer.query.sql.proc.IAssignmentStatement;
import org.teiid.designer.query.sql.proc.IBlock;
import org.teiid.designer.query.sql.proc.ICommandStatement;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.proc.IDeclareStatement;
import org.teiid.designer.query.sql.proc.IRaiseStatement;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IMultipleElementSymbol;
import org.teiid.designer.query.sql.symbol.IReference;
import org.teiid.designer.query.sql.symbol.IScalarSubquery;
import org.teiid.designer.udf.IFunctionDescriptor;
import org.teiid.designer.udf.IFunctionForm;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionForm;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.metadata.StoredProcedureInfo;
import org.teiid.query.metadata.TempMetadataID;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.RaiseErrorStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.SelectSymbol;
import org.teiid.query.sql.symbol.SingleElementSymbol;

/**
 *
 */
public class SyntaxFactory implements IQueryFactory {

    private Object createObject(Object object) {
        String className = object.getClass().getSimpleName();
        String classImplName = className + "Impl"; //$NON-NLS-1$
        String qualifiedName = this.getClass().getPackage().getName() + "." + classImplName; //$NON-NLS-1$
        
        try {
            Class<?> objectClass = this.getClass().getClassLoader().loadClass(qualifiedName);
            Constructor<?> constructor = objectClass.getConstructor(object.getClass());
            return constructor.newInstance(object);
        } catch (Exception ex) {
            
            // No specific class so try and wrap in a generic expression or language object
            if (object instanceof Expression) {
                return new ExpressionImpl((Expression) object);
            }
            else if (object instanceof LanguageObject) {
                return new LanguageObjectImpl((LanguageObject) object);
            } 
            else {
                throw new RuntimeException(ex);
            }
        }
    }
    
    /**
     * Create implementation wrapper object around the
     * given language object
     * 
     * @param languageObject
     * 
     * @return instance of {@link ILanguageObject}
     */
    public ILanguageObject createLanguageObject(LanguageObject languageObject) {
        if (languageObject == null)
            return null;
        
        return (ILanguageObject) createObject(languageObject);
    }
    
    /**
     * Create implementation wrapper object around the
     * given expression
     * 
     * @param expression
     * 
     * @return instance of {@link IExpression}
     */
    public IExpression createExpression(Expression expression) {
        if (expression == null)
            return null;
        
        return (IExpression) createObject(expression);
    }
    
    /**
     * Create implementation wrapper object around the
     * given function form
     * 
     * @param functionForm
     * 
     * @return instance of {@link IFunctionForm}
     */
    public IFunctionForm createFunctionForm(FunctionForm functionForm) {
        if (functionForm == null)
            return null;
        
        return (IFunctionForm) createObject(functionForm);
    }
    
    /**
     * Create implementation wrapper object around the
     * given function descriptor
     *
     * @param functionDescriptor
     * 
     * @return instance of {@link IFunctionDescriptor}
     */
    public IFunctionDescriptor createFunctionDescriptor(FunctionDescriptor functionDescriptor) {
        if (functionDescriptor == null)
            return null;
        
        return (IFunctionDescriptor) createObject(functionDescriptor);
    }
    
    /**
     * Convert the language wrapper object to its delegate
     * 
     * @param languageObject
     * 
     * @return the delegate object
     */
    public <T extends LanguageObject> T convert(ILanguageObject languageObject) {
        if (languageObject == null)
            return null;
        
        LanguageObjectImpl languageObjectImpl = (LanguageObjectImpl) languageObject;
        return (T) languageObjectImpl.getDelegate();
    }
    
    /**
     * Convert the delegate object by wrapping it
     * 
     * @param languageObject
     * 
     * @return the getFactory().wrapped object
     */
    public <T extends ILanguageObject> T convert(LanguageObject languageObject) {
        return (T) createLanguageObject(languageObject);
    }
    
    /**
     * Convert the delegate object by getFactory().wrapping it
     * 
     * @param expression
     * 
     * @return the getFactory().wrapped expression
     */
    public <T extends ILanguageObject> T convert(Expression expression) {
        if (expression == null)
            return null;
        
        return (T) createExpression(expression);
    }
    
    /**
     * UngetFactory().wrap the collection of getFactory().wrapper object for its delegates
     * 
     * @param objects
     * 
     * @return collection of delegate objects
     */
    public <T extends ILanguageObject, S extends LanguageObject> List<S> unwrap(Collection<T> objects) {
        List<S> languageObjects = new ArrayList<S>();
        
        if (objects != null) {
            for (T languageObject : objects) {
                languageObjects.add((S) convert(languageObject));
            }
        }
        
        return languageObjects;
    }
    
    /**
     * Wrap the collection of delegate object
     * 
     * @param delegateList
     * 
     * @return collection of getFactory().wrapped objects
     */
    public <T extends ILanguageObject, S extends LanguageObject> List<T> wrap(Collection<S> delegateList) {
        List<T> wrapList = new ArrayList<T>();
        
        if (delegateList != null) {
            for (S delegateItem : delegateList) {
                wrapList.add((T) createLanguageObject(delegateItem));
            }
        }
        
        return wrapList;
    }
    
    /**
     * Convert the enumeration join type to the teiid 8 class {@link JoinType}
     * 
     * @param joinType
     * 
     * @return instance of {@link JoinType}
     */
    public JoinType convert(IJoinPredicate.JoinType joinType) {
        JoinType dJoinType = null;
        switch (joinType) {
            case JOIN_INNER:
                dJoinType = JoinType.JOIN_INNER;
                break;
            case JOIN_RIGHT_OUTER:
                dJoinType = JoinType.JOIN_RIGHT_OUTER;
                break;
            case JOIN_LEFT_OUTER:
                dJoinType = JoinType.JOIN_LEFT_OUTER;
                break;
            case JOIN_FULL_OUTER:
                dJoinType = JoinType.JOIN_FULL_OUTER;
                break;
            case JOIN_CROSS:
                dJoinType = JoinType.JOIN_CROSS;
                break;
            case JOIN_UNION:
                dJoinType = JoinType.JOIN_UNION;
                break;
            case JOIN_SEMI:
                dJoinType = JoinType.JOIN_SEMI;
                break;
            case JOIN_ANTI_SEMI:
                dJoinType = JoinType.JOIN_ANTI_SEMI;
                break;
        }
        return dJoinType;
    }
    
    /**
     * Convert the stored procedure info wrapper object to its delegate
     * 
     * @param storedProcedureInfo
     * 
     * @return the delegate object
     */
    public StoredProcedureInfo convert(IStoredProcedureInfo storedProcedureInfo) {
        StoredProcedureInfoImpl storedProcedureInfoImpl = (StoredProcedureInfoImpl) storedProcedureInfo;
        return storedProcedureInfoImpl.getDelegate();
    }

    @Override
    public IFunction createFunction(String name,
                                    IExpression[] arguments) {
        List<IExpression> argsList;
        if (arguments == null) {
            argsList = new ArrayList<IExpression>();
        } else {
            argsList = Arrays.asList(arguments);
        }
        
        List<Expression> dargs = unwrap(argsList);
        Function function = new Function(name, dargs.toArray(new Expression[0]));
        return new FunctionImpl(function);
    }

    @Override
    public IAggregateSymbol createAggregateSymbol(String functionName,
                                                  IAggregateSymbol.AggregateType functionType,
                                                  boolean isDistinct,
                                                  IExpression expression) {
        Expression dExpression = convert(expression);
        return convert(new AggregateSymbol(functionName, functionType.name(), isDistinct, dExpression));
    }

    @Override
    public IElementSymbol createElementSymbol(String name) {
        return convert(new ElementSymbol(name));
    }

    @Override
    public IElementSymbol createElementSymbol(String name,
                                              boolean displayFullyQualified) {
        return convert(new ElementSymbol(name, displayFullyQualified));
    }

    @Override
    public IAliasSymbol createAliasSymbol(String name,
                                          IExpression expression) {
        SingleElementSymbol dExpression = convert(expression);
        return convert(new AliasSymbol(name, dExpression));
    }

    @Override
    public IGroupSymbol createGroupSymbol(String name) {
        return convert(new GroupSymbol(name));
    }

    @Override
    public IGroupSymbol createGroupSymbol(String name,
                                          String definition) {
        return convert(new GroupSymbol(name, definition));
    }

    @Override
    public IExpressionSymbol createExpressionSymbol(String name,
                                                    IExpression expression) {
        Expression dExpression = convert(expression);
        return convert(new ExpressionSymbol(name, dExpression));
    }

    @Override
    public IMultipleElementSymbol createMultipleElementSymbol() {
        return convert(new MultipleElementSymbol());
    }

    @Override
    public IConstant createConstant(Object value) {
        return convert(new Constant(value));
    }

    @Override
    public IDeclareStatement createDeclareStatement(IElementSymbol variable,
                                                    String valueType) {
        ElementSymbol symbol = convert(variable);
        return convert(new DeclareStatement(symbol, valueType));
    }

    @Override
    public ICommandStatement createCommandStatement(ICommand command) {
        Command dCommand = convert(command);
        return convert(new CommandStatement(dCommand));
    }

    @Override
    public IRaiseStatement createRaiseStatement(IExpression expression) {
        Expression dExpression = convert(expression);
        return convert(new RaiseErrorStatement(dExpression));
    }

    @Override
    public IQuery createQuery() {
        return convert(new Query());
    }

    @Override
    public ISetQuery createSetQuery(Operation operation,
                                    boolean all,
                                    IQueryCommand leftQuery,
                                    IQueryCommand rightQuery) {
        QueryCommand dLCommand = convert(leftQuery);
        QueryCommand dRCommand = convert(rightQuery);
        
        SetQuery.Operation dOperation = SetQuery.Operation.valueOf(operation.name());
        SetQuery setQuery = new SetQuery(dOperation, all, dLCommand, dRCommand);
        return convert(setQuery);
    }

    @Override
    public ISetQuery createSetQuery(Operation operation) {
        SetQuery.Operation dOperation = SetQuery.Operation.valueOf(operation.name());
        return convert(new SetQuery(dOperation));
    }

    @Override
    public ICompareCriteria createCompareCriteria() {
        return convert(new CompareCriteria());
    }

    @Override
    public ICompareCriteria createCompareCriteria(IExpression expression1,
                                                  int operator,
                                                  IExpression expression2) {
        Expression dExpression1 = convert(expression1);
        Expression dExpression2 = convert(expression2);
        return convert(new CompareCriteria(dExpression1, operator, dExpression2));
    }

    @Override
    public IIsNullCriteria createIsNullCriteria() {
        return convert(new IsNullCriteria());
    }

    @Override
    public IIsNullCriteria createIsNullCriteria(IExpression expression) {
        Expression dExpression = convert(expression);
        return convert(new IsNullCriteria(dExpression));
    }

    @Override
    public INotCriteria createNotCriteria() {
        return convert(new NotCriteria());
    }

    @Override
    public INotCriteria createNotCriteria(ICriteria criteria) {
        Criteria dCriteria = convert(criteria);
        return convert(new NotCriteria(dCriteria));
    }

    @Override
    public IMatchCriteria createMatchCriteria() {
        return convert(new MatchCriteria());
    }

    @Override
    public ISetCriteria createSetCriteria() {
        return convert(new SetCriteria());
    }

    @Override
    public ISubquerySetCriteria createSubquerySetCriteria() {
        return convert(new SubquerySetCriteria());
    }

    @Override
    public ISubquerySetCriteria createSubquerySetCriteria(IExpression expression,
                                                          IQueryCommand command) {
        Expression dExpression = convert(expression);
        QueryCommand dCommand = convert(command);
        return convert(new SubquerySetCriteria(dExpression, dCommand));
    }

    @Override
    public ISubqueryCompareCriteria createSubqueryCompareCriteria(IExpression leftExpression,
                                                                  IQueryCommand command,
                                                                  int operator,
                                                                  int predicateQuantifier) {
        Expression dLeftExpression = convert(leftExpression);
        QueryCommand dCommand = convert(command);
        return convert(new SubqueryCompareCriteria(dLeftExpression, dCommand, operator, predicateQuantifier));
    }

    @Override
    public IScalarSubquery createScalarSubquery(IQueryCommand queryCommand) {
        QueryCommand dCommand = convert(queryCommand);
        return convert(new ScalarSubquery(dCommand));
    }

    @Override
    public IBetweenCriteria createBetweenCriteria(IElementSymbol elementSymbol,
                                                  IConstant constant1,
                                                  IConstant constant2) {
        ElementSymbol dSymbol = convert(elementSymbol);
        Constant dConstant1 = convert(constant1);
        Constant dConstant2 = convert(constant2);
        return convert(new BetweenCriteria(dSymbol, dConstant1, dConstant2));
    }

    @Override
    public ICompoundCriteria createCompoundCriteria(LogicalOperator operator,
                                                    ICriteria... criteria) {
        List<Criteria> criteriaList = new ArrayList<Criteria>();
        if (criteria != null) {
            for (ICriteria c : criteria) {
                criteriaList.add((Criteria) convert(c));
            }
        }
        
        return convert(new CompoundCriteria(operator.index(), criteriaList));
    }

    @Override
    public IExistsCriteria createExistsCriteria(IQueryCommand queryCommand) {
        QueryCommand dCommand = convert(queryCommand);
        return convert(new ExistsCriteria(dCommand));
    }

    @Override
    public IBlock createBlock() {
        return convert(new Block());
    }

    @Override
    public ICreateProcedureCommand createCreateProcedureCommand(IBlock block) {
        Block dBlock = convert(block);
        return convert(new CreateUpdateProcedureCommand(dBlock));
    }

    @Override
    public IAssignmentStatement createAssignmentStatement(IElementSymbol elementSymbol,
                                                          IExpression expression) {
        ElementSymbol dSymbol = convert(elementSymbol);
        Expression dExpression = convert(expression);
        return convert(new AssignmentStatement(dSymbol, dExpression));
    }

    @Override
    public IAssignmentStatement createAssignmentStatement(IElementSymbol elementSymbol,
                                                          IQueryCommand queryCommand) {
        ElementSymbol dSymbol = convert(elementSymbol);
        QueryCommand dCommand = convert(queryCommand);
        return convert(new AssignmentStatement(dSymbol, dCommand));
    }

    @Override
    public ISelect createSelect() {
        return convert(new Select());
    }

    @Override
    public ISelect createSelect(List<? extends IExpression> symbols) {
        List<SelectSymbol> dSymbols = unwrap(symbols);
        return convert(new Select(dSymbols));
    }

    @Override
    public IFrom createFrom() {
        return convert(new From());
    }

    @Override
    public IFrom createFrom(List<? extends IFromClause> fromClauses) {
        List<FromClause> dFromClauses = unwrap(fromClauses);
        return convert(new From(dFromClauses));
    }

    @Override
    public IUnaryFromClause createUnaryFromClause(IGroupSymbol symbol) {
        GroupSymbol dSymbol = convert(symbol);
        return convert(new UnaryFromClause(dSymbol));
    }

    @Override
    public ISubqueryFromClause createSubqueryFromClause(String name,
                                                        ICommand command) {
        QueryCommand dCommand = convert(command);
        return convert(new SubqueryFromClause(name, dCommand));
    }

    @Override
    public IJoinPredicate createJoinPredicate(IFromClause leftClause,
                                              IFromClause rightClause,
                                              IJoinPredicate.JoinType joinType) {
        FromClause dLeftClause = convert(leftClause);
        FromClause dRightClause = convert(rightClause);
        JoinType dJoinType = convert(joinType);
        
        JoinPredicate joinPredicate = new JoinPredicate(dLeftClause, dRightClause, dJoinType);
        return convert(joinPredicate);
    }

    @Override
    public IJoinPredicate createJoinPredicate(IFromClause leftClause,
                                              IFromClause rightClause,
                                              IJoinPredicate.JoinType joinType,
                                              List<ICriteria> criteria) {
        
        FromClause dLeftClause = convert(leftClause);
        FromClause dRightClause = convert(rightClause);
        JoinType dJoinType = convert(joinType);
        List<Criteria> criteriaList = unwrap(criteria);
        
        JoinPredicate joinPredicate = new JoinPredicate(dLeftClause, dRightClause, dJoinType, criteriaList);
        return convert(joinPredicate);
    }

    @Override
    public IGroupBy createGroupBy() {
        return convert(new GroupBy());
    }

    @Override
    public IOrderBy createOrderBy() {
        return convert(new OrderBy());
    }

    @Override
    public IOption createOption() {
        return convert(new Option());
    }

    @Override
    public IUpdate createUpdate() {
        return new UpdateImpl(new Update());
    }

    @Override
    public IDelete createDelete() {
        return new DeleteImpl(new Delete());
    }

    @Override
    public IInsert createInsert() {
        return new InsertImpl(new Insert());
    }

    @Override
    public IStoredProcedure createStoredProcedure() {
        return new StoredProcedureImpl(new StoredProcedure());
    }

    @Override
    public ISPParameter createSPParameter(int index,
                                          IExpression expression) {
        Expression dExpression = convert(expression);
        SPParameter spParameter = new SPParameter(index, dExpression);
        return new SPParameterImpl(spParameter);
    }

    @Override
    public ISPParameter createSPParameter(int index,
                                          ParameterInfo parameterType,
                                          String name) {
        
        SPParameter spParameter = new SPParameter(index, parameterType.index(), name);
        return new SPParameterImpl(spParameter);
    }

    @Override
    public IReference createReference(int index) {
        return convert(new Reference(index));
    }

    @Override
    public IMetadataID createMetadataID(String id, Class<?> clazz) {
        return new MetadataIDImpl(new TempMetadataID(id, clazz));
    }

    @Override
    public IStoredProcedureInfo createStoredProcedureInfo() {
        return new StoredProcedureInfoImpl(new StoredProcedureInfo());
    }

    @Override
    public IQueryNode createQueryNode(String queryPlan) {
        return new QueryNodeImpl(new QueryNode(queryPlan));
    }
    
}
