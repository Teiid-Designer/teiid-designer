/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.query.internal.ui.sqleditor.component;

import java.util.Arrays;
import java.util.List;

import com.metamatrix.query.function.FunctionLibrary;
import com.metamatrix.query.sql.ReservedWords;
import com.metamatrix.query.sql.lang.*;
import com.metamatrix.query.sql.proc.*;
import com.metamatrix.query.sql.symbol.*;

/**
 * The <code>DisplayNodeFactory</code> class is the Factory used to create all different
 * types of DisplayNodes.
 */
public class DisplayNodeFactory {
    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$

    private static final List RESERVED_WORDS = Arrays.asList(ReservedWords.ALL_WORDS);
    private static final String[] SEPARATORS = new String[11];
    private static final String[] FUNCTION_NAMES = new String[2];
    private static final String[] JOIN_TYPE_NAMES = new String[5];

	static {
       SEPARATORS[0] = " ";   //$NON-NLS-1$
       SEPARATORS[1] = " \n"; //$NON-NLS-1$
       SEPARATORS[2] = "\n";  //$NON-NLS-1$
       SEPARATORS[3] = " \t"; //$NON-NLS-1$
       SEPARATORS[4] = "\t";  //$NON-NLS-1$
       SEPARATORS[5] = ", ";  //$NON-NLS-1$
       SEPARATORS[6] = ",";   //$NON-NLS-1$
       SEPARATORS[7] = "(";   //$NON-NLS-1$
       SEPARATORS[8] = ")";   //$NON-NLS-1$
       SEPARATORS[9] = " (";  //$NON-NLS-1$
       SEPARATORS[10] = ") "; //$NON-NLS-1$
       FUNCTION_NAMES[0] = FunctionLibrary.CAST.toUpperCase();
       FUNCTION_NAMES[1] = FunctionLibrary.CONVERT.toUpperCase();
       JOIN_TYPE_NAMES[0] = ReservedWords.INNER+SPACE+ReservedWords.JOIN;
       JOIN_TYPE_NAMES[1] = ReservedWords.CROSS+SPACE+ReservedWords.JOIN;
       JOIN_TYPE_NAMES[2] = ReservedWords.LEFT+SPACE+ReservedWords.OUTER+SPACE+ReservedWords.JOIN;
       JOIN_TYPE_NAMES[3] = ReservedWords.RIGHT+SPACE+ReservedWords.OUTER+SPACE+ReservedWords.JOIN;
       JOIN_TYPE_NAMES[4] = ReservedWords.FULL+SPACE+ReservedWords.OUTER+SPACE+ReservedWords.JOIN;
    }

    private static final List SEPARATOR_WORDS = Arrays.asList(SEPARATORS);
    private static final List FUNCTION_NAME_WORDS = Arrays.asList(FUNCTION_NAMES);
    private static final List JOIN_TYPE_WORDS = Arrays.asList(JOIN_TYPE_NAMES);

    //************************************************
    // Public Methods
    //************************************************

    public static DisplayNode createDisplayNode( DisplayNode parentNode, Object obj) {
        //------------------------------------------------------
        // If an ExpressionSymbol, the object is the Expression
        //------------------------------------------------------
        if(obj instanceof ExpressionSymbol) {
            if(obj instanceof AggregateSymbol) {
                AggregateSymbolDisplayNode node =
                  new AggregateSymbolDisplayNode( parentNode, (AggregateSymbol)obj );
                return node;
            }
            obj = ((ExpressionSymbol)obj).getExpression();
        }
        //---------------------------------------------------------------------
        // Commands
        //---------------------------------------------------------------------
        if(obj instanceof Query) {
            QueryDisplayNode node = new QueryDisplayNode( parentNode, (Query)obj );
            return node;
        } else if(obj instanceof SetQuery) {
            SetQueryDisplayNode node = new SetQueryDisplayNode( parentNode, (SetQuery)obj );
            return node;
        } else if(obj instanceof Insert) {
            InsertDisplayNode node = new InsertDisplayNode( parentNode, (Insert)obj );
            return node;
        } else if(obj instanceof Update) {
            UpdateDisplayNode node = new UpdateDisplayNode( parentNode, (Update)obj );
            return node;
        } else if(obj instanceof Delete) {
            DeleteDisplayNode node = new DeleteDisplayNode( parentNode, (Delete)obj );
            return node;
        } else if(obj instanceof StoredProcedure) {
            StoredProcedureDisplayNode node = new StoredProcedureDisplayNode( parentNode, (StoredProcedure)obj );
            return node;
        } else if(obj instanceof CreateUpdateProcedureCommand) {
            CreateUpdateProcedureDisplayNode node = new CreateUpdateProcedureDisplayNode( parentNode, (CreateUpdateProcedureCommand)obj );
            return node;
        } else if(obj instanceof DynamicCommand) {
            DynamicCommandDisplayNode node = new DynamicCommandDisplayNode( parentNode, (DynamicCommand)obj );
            return node;
        } else if(obj instanceof Create) {
            CreateCommandDisplayNode node = new CreateCommandDisplayNode( parentNode, (Create)obj );
            return node;
        } else if(obj instanceof Drop) {
            DropCommandDisplayNode node = new DropCommandDisplayNode( parentNode, (Drop)obj );
            return node;
        //---------------------------------------------------------------------
        // Select, From, GroupBy, OrderBy, Option
        //---------------------------------------------------------------------
        } else if(obj instanceof Select) {
            SelectDisplayNode node = new SelectDisplayNode( parentNode, (Select)obj );
            return node;
        } else if(obj instanceof From) {
            FromDisplayNode node = new FromDisplayNode( parentNode, (From)obj );
            return node;
        } else if(obj instanceof GroupBy) {
            GroupByDisplayNode node = new GroupByDisplayNode( parentNode, (GroupBy)obj );
            return node;
        } else if(obj instanceof OrderBy) {
            OrderByDisplayNode node = new OrderByDisplayNode( parentNode, (OrderBy)obj );
            return node;
        } else if(obj instanceof Limit) {
            LimitDisplayNode node = new LimitDisplayNode( parentNode, (Limit)obj );
            return node;
        } else if(obj instanceof Option) {
            OptionDisplayNode node = new OptionDisplayNode( parentNode, (Option)obj );
            return node;
        } else if(obj instanceof Into) {
            IntoDisplayNode node = new IntoDisplayNode( parentNode, (Into)obj );
            return node;
        //---------------------------------------------------------------------
        // Keywords, Separators, Unknown Strings
        //---------------------------------------------------------------------
        } else if (obj instanceof String) {
            String text = (String)obj;
            if (RESERVED_WORDS.contains(text) || JOIN_TYPE_WORDS.contains(text.toUpperCase())) {
                return new KeywordDisplayNode(parentNode, text);
            }
            if (FUNCTION_NAME_WORDS.contains(text.toUpperCase())) {
                return new FunctionNameDisplayNode(parentNode, text);
            }
            if (SEPARATOR_WORDS.contains(text)) {
                return new SeparatorDisplayNode(parentNode, text);
            }
            return new UnknownDisplayNode(parentNode, text);
        //---------------------------------------------------------------------
        // FromClause Parts
        //---------------------------------------------------------------------
        } else if(obj instanceof SubqueryFromClause) {
            SubqueryFromClauseDisplayNode node = new SubqueryFromClauseDisplayNode( parentNode, (SubqueryFromClause)obj );
            return node;
        } else if(obj instanceof UnaryFromClause) {
            UnaryFromClauseDisplayNode node = new UnaryFromClauseDisplayNode( parentNode, (UnaryFromClause)obj );
            return node;
        } else if(obj instanceof JoinPredicate) {
            JoinPredicateDisplayNode node = new JoinPredicateDisplayNode( parentNode, (JoinPredicate)obj );
            return node;
        //---------------------------------------------------------------------
        // Criteria Nodes
        //---------------------------------------------------------------------
        } else if(obj instanceof CompareCriteria) {
            CompareCriteriaDisplayNode node = new CompareCriteriaDisplayNode( parentNode, (CompareCriteria)obj );
            return node;
        } else if(obj instanceof MatchCriteria) {
            MatchCriteriaDisplayNode node = new MatchCriteriaDisplayNode( parentNode, (MatchCriteria)obj );
            return node;
        } else if(obj instanceof SetCriteria) {
            SetCriteriaDisplayNode node = new SetCriteriaDisplayNode( parentNode, (SetCriteria)obj );
            return node;
        } else if(obj instanceof IsNullCriteria) {
            IsNullCriteriaDisplayNode node = new IsNullCriteriaDisplayNode( parentNode, (IsNullCriteria)obj );
            return node;
        } else if(obj instanceof CompoundCriteria) {
            CompoundCriteriaDisplayNode node = new CompoundCriteriaDisplayNode( parentNode, (CompoundCriteria)obj );
            return node;
        } else if(obj instanceof NotCriteria) {
            NotCriteriaDisplayNode node = new NotCriteriaDisplayNode( parentNode, (NotCriteria)obj );
            return node;
        } else if(obj instanceof BetweenCriteria) {
            BetweenCriteriaDisplayNode node = new BetweenCriteriaDisplayNode( parentNode, (BetweenCriteria)obj );
            return node;
        } else if(obj instanceof SubquerySetCriteria) {
            SubquerySetCriteriaDisplayNode node = new SubquerySetCriteriaDisplayNode( parentNode, (SubquerySetCriteria)obj );
            return node;
        } else if(obj instanceof ExistsCriteria) {
            ExistsCriteriaDisplayNode node = new ExistsCriteriaDisplayNode( parentNode, (ExistsCriteria)obj );
            return node;
        } else if(obj instanceof SubqueryCompareCriteria) {
            SubqueryCompareCriteriaDisplayNode node = new SubqueryCompareCriteriaDisplayNode( parentNode, (SubqueryCompareCriteria)obj );
            return node;
        //---------------------------------------------------------------------
        // Constant, Function, Expression Nodes
        //---------------------------------------------------------------------
        } else if(obj instanceof Constant) {
            ConstantDisplayNode node = new ConstantDisplayNode( parentNode, (Constant)obj );
            return node;
        } else if(obj instanceof Function) {
            FunctionDisplayNode node = new FunctionDisplayNode( parentNode, (Function)obj );
            return node;
        } else if(obj instanceof CaseExpression) {
            CaseExpressionDisplayNode node = new CaseExpressionDisplayNode( parentNode, (CaseExpression)obj );
            return node;
        } else if(obj instanceof SearchedCaseExpression) {
            SearchedCaseExpressionDisplayNode node = new SearchedCaseExpressionDisplayNode( parentNode, (SearchedCaseExpression)obj );
            return node;
        //---------------------------------------------------------------------
        // Symbol Nodes
        //---------------------------------------------------------------------
        } else if(obj instanceof AliasSymbol) {
            AliasSymbolDisplayNode node = new AliasSymbolDisplayNode( parentNode, (AliasSymbol)obj );
            return node;
        } else if(obj instanceof ElementSymbol) {
            ElementSymbolDisplayNode node = new ElementSymbolDisplayNode( parentNode, (ElementSymbol)obj );
            return node;
        } else if(obj instanceof GroupSymbol) {
            GroupSymbolDisplayNode node = new GroupSymbolDisplayNode( parentNode, (GroupSymbol)obj );
            return node;
        } else if(obj instanceof AllSymbol) {
            SymbolDisplayNode node = new SymbolDisplayNode( parentNode, (AllSymbol)obj );
            return node;
        } else if(obj instanceof AllInGroupSymbol) {
            SymbolDisplayNode node = new SymbolDisplayNode( parentNode, (AllInGroupSymbol)obj );
            return node;
        } else if(obj instanceof Reference) {
            ReferenceDisplayNode node = new ReferenceDisplayNode( parentNode, (Reference)obj );
            return node;
        } else if(obj instanceof ScalarSubquery) {
            ScalarSubqueryDisplayNode node = new ScalarSubqueryDisplayNode( parentNode, (ScalarSubquery)obj );
            return node;
        //---------------------------------------------------------------------
        // Procedure LanguageObjects
        //---------------------------------------------------------------------
        } else if(obj instanceof DeclareStatement) {
            DeclareStatementDisplayNode node = new DeclareStatementDisplayNode( parentNode, (DeclareStatement)obj );
            return node;
        } else if(obj instanceof AssignmentStatement) {
            AssignmentStatementDisplayNode node = new AssignmentStatementDisplayNode( parentNode, (AssignmentStatement)obj );
            return node;
        } else if(obj instanceof Block) {
            BlockDisplayNode node = new BlockDisplayNode( parentNode, (Block)obj );
            return node;
        } else if(obj instanceof CommandStatement) {
            CommandStatementDisplayNode node = new CommandStatementDisplayNode( parentNode, (CommandStatement)obj );
            return node;
        } else if(obj instanceof IfStatement) {
            IfStatementDisplayNode node = new IfStatementDisplayNode( parentNode, (IfStatement)obj );
            return node;
        } else if(obj instanceof BreakStatement) {
            BreakStatementDisplayNode node = new BreakStatementDisplayNode( parentNode, (BreakStatement)obj );
            return node;
        } else if(obj instanceof LoopStatement) {
            LoopStatementDisplayNode node = new LoopStatementDisplayNode( parentNode, (LoopStatement)obj );
            return node;
        } else if(obj instanceof WhileStatement) {
            WhileStatementDisplayNode node = new WhileStatementDisplayNode( parentNode, (WhileStatement)obj );
            return node;
        } else if(obj instanceof ContinueStatement) {
            ContinueStatementDisplayNode node = new ContinueStatementDisplayNode( parentNode, (ContinueStatement)obj );
            return node;
        } else if(obj instanceof TranslateCriteria) {
            TranslateCriteriaDisplayNode node = new TranslateCriteriaDisplayNode( parentNode, (TranslateCriteria)obj );
            return node;
        } else if(obj instanceof HasCriteria) {
            HasCriteriaDisplayNode node = new HasCriteriaDisplayNode( parentNode, (HasCriteria)obj );
            return node;
        } else if(obj instanceof CriteriaSelector) {
            CriteriaSelectorDisplayNode node = new CriteriaSelectorDisplayNode( parentNode, (CriteriaSelector)obj );
            return node;
        //---------------------------------------------------------------------
        // All Others are Unknown Nodes
        //---------------------------------------------------------------------
        } else {
            String unknownText = UNDEFINED;
            if(obj!=null) {
                String objText = obj.toString();
                if(objText.trim().length()>0) {
                    unknownText = objText;
                }
            }
            UnknownDisplayNode node = new UnknownDisplayNode( parentNode, unknownText );
            return node;
        }
    }

    public static DisplayNode createHavingDisplayNode( DisplayNode parentNode, Criteria criteria) {
        return new HavingDisplayNode( parentNode, criteria );
    }
    
    public static DisplayNode createWhereDisplayNode( DisplayNode parentNode, Criteria criteria) {
        return new WhereDisplayNode( parentNode, criteria );
    }

    public static DisplayNode createFunctionNameDisplayNode( DisplayNode parentNode, String name) {
        return new FunctionNameDisplayNode(parentNode,name);
    }

    public static DisplayNode createUnknownQueryDisplayNode( DisplayNode parentNode, String name) {
        return new UnknownQueryDisplayNode( parentNode, name );
    }

}

