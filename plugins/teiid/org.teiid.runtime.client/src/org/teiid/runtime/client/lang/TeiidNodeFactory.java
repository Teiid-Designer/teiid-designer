/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang;

import org.teiid.runtime.client.lang.ast.AggregateSymbol;
import org.teiid.runtime.client.lang.ast.AliasSymbol;
import org.teiid.runtime.client.lang.ast.Alter;
import org.teiid.runtime.client.lang.ast.ArrayTable;
import org.teiid.runtime.client.lang.ast.AssignmentStatement;
import org.teiid.runtime.client.lang.ast.BetweenCriteria;
import org.teiid.runtime.client.lang.ast.Block;
import org.teiid.runtime.client.lang.ast.BranchingStatement;
import org.teiid.runtime.client.lang.ast.CaseExpression;
import org.teiid.runtime.client.lang.ast.Command;
import org.teiid.runtime.client.lang.ast.CommandStatement;
import org.teiid.runtime.client.lang.ast.CompareCriteria;
import org.teiid.runtime.client.lang.ast.CompoundCriteria;
import org.teiid.runtime.client.lang.ast.Constant;
import org.teiid.runtime.client.lang.ast.CreateProcedureCommand;
import org.teiid.runtime.client.lang.ast.Criteria;
import org.teiid.runtime.client.lang.ast.DeclareStatement;
import org.teiid.runtime.client.lang.ast.Delete;
import org.teiid.runtime.client.lang.ast.DerivedColumn;
import org.teiid.runtime.client.lang.ast.Drop;
import org.teiid.runtime.client.lang.ast.DynamicCommand;
import org.teiid.runtime.client.lang.ast.ElementSymbol;
import org.teiid.runtime.client.lang.ast.ExistsCriteria;
import org.teiid.runtime.client.lang.ast.ExpressionCriteria;
import org.teiid.runtime.client.lang.ast.From;
import org.teiid.runtime.client.lang.ast.FromClause;
import org.teiid.runtime.client.lang.ast.Function;
import org.teiid.runtime.client.lang.ast.GroupBy;
import org.teiid.runtime.client.lang.ast.GroupSymbol;
import org.teiid.runtime.client.lang.ast.IfStatement;
import org.teiid.runtime.client.lang.ast.Insert;
import org.teiid.runtime.client.lang.ast.Into;
import org.teiid.runtime.client.lang.ast.IsNullCriteria;
import org.teiid.runtime.client.lang.ast.JSONObject;
import org.teiid.runtime.client.lang.ast.JoinPredicate;
import org.teiid.runtime.client.lang.ast.JoinType;
import org.teiid.runtime.client.lang.ast.LanguageObject;
import org.teiid.runtime.client.lang.ast.Limit;
import org.teiid.runtime.client.lang.ast.LoopStatement;
import org.teiid.runtime.client.lang.ast.MatchCriteria;
import org.teiid.runtime.client.lang.ast.MultipleElementSymbol;
import org.teiid.runtime.client.lang.ast.NotCriteria;
import org.teiid.runtime.client.lang.ast.ObjectColumn;
import org.teiid.runtime.client.lang.ast.ObjectTable;
import org.teiid.runtime.client.lang.ast.Option;
import org.teiid.runtime.client.lang.ast.OrderBy;
import org.teiid.runtime.client.lang.ast.OrderByItem;
import org.teiid.runtime.client.lang.ast.ProjectedColumn;
import org.teiid.runtime.client.lang.ast.Query;
import org.teiid.runtime.client.lang.ast.QueryCommand;
import org.teiid.runtime.client.lang.ast.QueryString;
import org.teiid.runtime.client.lang.ast.Reference;
import org.teiid.runtime.client.lang.ast.ReturnStatement;
import org.teiid.runtime.client.lang.ast.ScalarSubquery;
import org.teiid.runtime.client.lang.ast.SearchedCaseExpression;
import org.teiid.runtime.client.lang.ast.Select;
import org.teiid.runtime.client.lang.ast.SetClause;
import org.teiid.runtime.client.lang.ast.SetClauseList;
import org.teiid.runtime.client.lang.ast.SetCriteria;
import org.teiid.runtime.client.lang.ast.Statement;
import org.teiid.runtime.client.lang.ast.StoredProcedure;
import org.teiid.runtime.client.lang.ast.SubqueryCompareCriteria;
import org.teiid.runtime.client.lang.ast.SubqueryFromClause;
import org.teiid.runtime.client.lang.ast.SubquerySetCriteria;
import org.teiid.runtime.client.lang.ast.Teiid8ParserTreeConstants;
import org.teiid.runtime.client.lang.ast.TextColumn;
import org.teiid.runtime.client.lang.ast.TextLine;
import org.teiid.runtime.client.lang.ast.TextTable;
import org.teiid.runtime.client.lang.ast.TriggerAction;
import org.teiid.runtime.client.lang.ast.UnaryFromClause;
import org.teiid.runtime.client.lang.ast.Update;
import org.teiid.runtime.client.lang.ast.WhileStatement;
import org.teiid.runtime.client.lang.ast.WindowFunction;
import org.teiid.runtime.client.lang.ast.WindowSpecification;
import org.teiid.runtime.client.lang.ast.WithQueryCommand;
import org.teiid.runtime.client.lang.ast.XMLAttributes;
import org.teiid.runtime.client.lang.ast.XMLColumn;
import org.teiid.runtime.client.lang.ast.XMLElement;
import org.teiid.runtime.client.lang.ast.XMLForest;
import org.teiid.runtime.client.lang.ast.XMLNamespaces;
import org.teiid.runtime.client.lang.ast.XMLParse;
import org.teiid.runtime.client.lang.ast.XMLQuery;
import org.teiid.runtime.client.lang.ast.XMLSerialize;
import org.teiid.runtime.client.lang.ast.XMLTable;
import org.teiid.runtime.client.lang.ast.v8.Aggregate8Symbol;
import org.teiid.runtime.client.lang.ast.v8.Window8Function;
import org.teiid.runtime.client.lang.parser.TeiidParser;
import org.teiid.runtime.client.lang.parser.v8.Teiid8Parser;

/**
 * Factory for creating parser nodes
 */
public class TeiidNodeFactory {

    /**
     * Names of nodes frequently created outside of the parsers
     * For use with {@link TeiidNodeFactory#create(TeiidParser, CommonNodes)}
     */
    public enum CommonNodes {

        /**
         * Element Symbol
         */
        ELEMENT_SYMBOL("ElementSymbol"), //$NON-NLS-1$
    
        /**
         * Group Symbol
         */
        GROUP_SYMBOL("GroupSymbol"), //$NON-NLS-1$

        /**
         * Multiple Element Symbol
         */
        MUTLIPLE_ELEMENT_SYMBOL("MultipleElementSymbol"), //$NON-NLS-1$

        /**
         * Command Statement
         */
        COMMAND_STATEMENT("CommonStatement"),  //$NON-NLS-1$

        /**
         * Select
         */
        SELECT("Select"),   //$NON-NLS-1$

        /**
         * UnaryFromClause
         */
        UNARY_FROM_CLAUSE("unaryFromClause"),   //$NON-NLS-1$

        /**
         * SubqueryFromClause
         */
        SUBQUERY_FROM_CLAUSE("SubqueryFromClause"),   //$NON-NLS-1$

        /**
         * From
         */
        FROM("From"),  //$NON-NLS-1$

        /**
         * Query
         */
        QUERY("Query"), //$NON-NLS-1$

        /**
         * Scaler Subquery
         */
        SCALAR_SUBQUERY("ScalarSubquery"),  //$NON-NLS-1$

        /**
         * Function
         */
        FUNCTION("Function"),  //$NON-NLS-1$

        /**
         * MatchCritieria
         */
        MATCH_CRITERIA("MatchCriteria"), //$NON-NLS-1$

        /**
         * Expression Symbol
         */
        EXPRESSION_SYMBOL("ExpressionSymbol"), //$NON-NLS-1$
        
        /**
         * XML Column
         */
        XML_COLUMN("XMLColumn");  //$NON-NLS-1$

        private String name;

        CommonNodes(String name) {
            this.name = name;
        }

        /**
         * @return Name of this common node
         */
        public String getName() {
            return name;
        }
    }

    private static TeiidNodeFactory instance;

    /**
     * Singleton instance of this factory
     *
     * @return teiidNodeFactory
     */
    public static TeiidNodeFactory getInstance() {
        if (instance == null) instance = new TeiidNodeFactory();

        return instance;
    }

    private static boolean isTeiid7Parser(TeiidParser teiidParser) {
//        return teiidParser instanceof Teiid7Parser;
        return false;
    }

    private static boolean isTeiid8Parser(TeiidParser teiidParser) {
        return teiidParser instanceof Teiid8Parser;
    }

    /**
     * Create a parser node for the node with the given common node name
     * @see TeiidParser#createCommonNode(CommonNodes)
     *
     * @param teiidParser
     * @param nodeType
     *
     * @return node applicable to the given parser
     */
    public <T extends LanguageObject> T create(TeiidParser teiidParser, CommonNodes nodeType) {
        
        if (isTeiid8Parser(teiidParser)) {
            for (int i = 0; i < Teiid8ParserTreeConstants.jjtNodeName.length; ++i) {
                String constantName = Teiid8ParserTreeConstants.jjtNodeName[i];
                if (! constantName.equalsIgnoreCase(nodeType.getName()))
                    continue;

                return create(teiidParser, i);
            }
        }

        throw new IllegalStateException();
    }

    /**
     * Create a parser node for the given node type
     *
     * @param teiidParser
     * @param nodeType
     *
     * @return node applicable to the given parser
     */
    public <T extends LanguageObject> T create(TeiidParser teiidParser, int nodeType) {
        if (isTeiid8Parser(teiidParser)) {
            return create((Teiid8Parser)teiidParser, nodeType);
            //        } else if (isTeiid7Parser(teiidParser) {

        }

        throw new IllegalArgumentException();
    }
    
    /**
     * Create a version 8 teiid parser node for the given node type.
     *
     * @param teiidParser
     * @param nodeType
     *
     * @return version 8 teiid parser node
     */
    private <T extends LanguageObject> T create(Teiid8Parser teiidParser, int nodeType) {
        switch (nodeType) {
            case Teiid8ParserTreeConstants.JJTCOMMAND:
                return (T) createCommand(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTALTER:
                return (T) createAlter(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTTRIGGERACTION:
                return (T) createTriggerAction(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTDROP:
                return (T) createDrop(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTRAISESTATEMENT:
                return (T) createAssignmentStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSTATEMENT:
                return (T) createStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTBRANCHINGSTATEMENT:
                return (T) createBranchingStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTRETURNSTATEMENT:
                return (T) createReturnStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTWHILESTATEMENT:
                return (T) createWhileStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTLOOPSTATEMENT:
                return (T) createLoopStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTIFSTATEMENT:
                return (T) createIfStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTDECLARESTATEMENT:
                return (T) createDeclareStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTCOMMANDSTATEMENT:
                return (T) createCommandStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTCREATEPROCEDURECOMMAND:
                return (T) createCreateProcedureCommand(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTDYNAMICCOMMAND:
                return (T) createDynamicCommand(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSETCLAUSELIST:
                return (T) createSetClauseList(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSETCLAUSE:
                return (T) createSetClause(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTPROJECTEDCOLUMN:
                return (T) createProjectedColumn(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSTOREDPROCEDURE:
                return (T) createStoredProcedure(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTINSERT:
                return (T) createInsert(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTUPDATE:
                return (T) createUpdate(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTDELETE:
                return (T) createDelete(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTQUERYCOMMAND:
                return (T) createQueryCommand(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTWITHQUERYCOMMAND:
                return (T) createWithQueryCommand(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTQUERY:
                return (T) createQuery(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTINTO:
                return (T) createInto(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSELECT:
                return (T) createSelect(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTDERIVEDCOLUMN:
                return (T) createDerivedColumn(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTMULTIPLEELEMENTSYMBOL:
                return (T) createMultipleElementSymbol(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTFROM:
                return (T) createFrom(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTFROMCLAUSE:
                return (T) createFromClause(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTJOINPREDICATE:
                return (T) createJoinPredicate(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTJOINTYPE:
                return (T) createJoinType(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTXMLSERIALIZE:
                return (T) createXMLSerialize(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTARRAYTABLE:
                return (T) createArrayTable(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTTEXTTABLE:
                return (T) createTextTable(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTTEXTCOLUMN:
                return (T) createTextColumn(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTXMLQUERY:
                return (T) createXMLQuery(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTOBJECTTABLE:
                return (T) createObjectTable(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTOBJECTCOLUMN:
                return (T) createObjectColumn(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTXMLTABLE:
                return (T) createXMLTable(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTXMLCOLUMN:
                return (T) createXMLColumn(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSUBQUERYFROMCLAUSE:
                return (T) createSubQueryFromClause(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTUNARYFROMCLAUSE:
                return (T) createUnaryFromClause(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTCRITERIA:
                return (T) createCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTCOMPOUNDCRITERIA:
                return (T) createCompoundCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTNOTCRITERIA:
                return (T) createNotCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTCOMPARECRITERIA:
                return (T) createCompareCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSUBQUERYCOMPARECRITERIA:
                return (T) createSubqueryCompareCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTMATCHCRITERIA:
                return (T) createMatchCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTBETWEENCRITERIA:
                return (T) createBetweenCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTISNULLCRITERIA:
                return (T) createIsNullCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSUBQUERYSETCRITERIA:
                return (T) createSubquerySetCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSETCRITERIA:
                return (T) createSetCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTEXISTSCRITERIA:
                return (T) createExistsCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTGROUPBY:
                return (T) createGroupBy(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTORDERBY:
                return (T) createOrderBy(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTORDERBYITEM:
                return (T) createOrderByItem(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTLIMIT:
                return (T) createLimit(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTOPTION:
                return (T) createOption(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTREFERENCE:
                return (T) createReference(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTCASEEXPRESSION:
                return (T) createCaseExpression(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSEARCHEDCASEEXPRESSION:
                return (T) createSearchedCaseExpression(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTFUNCTION:
                return (T) createFunction(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTXMLPARSE:
                return (T) createXMLParse(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTQUERYSTRING:
                return (T) createQueryString(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTXMLELEMENT:
                return (T) createXMLElement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTXMLATTRIBUTES:
                return (T) createXMLAttributes(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTJSONOBJECT:
                return (T) createJSONObject(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTXMLFOREST:
                return (T) createXMLForest(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTXMLNAMESPACES:
                return (T) createXMLNamespaces(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTASSIGNMENTSTATEMENT:
                return (T) createAssignmentStatement(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTSCALARSUBQUERY:
                return (T) createScalarSubquery(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTGROUPSYMBOL:
                return (T) createGroupSymbol(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTCONSTANT:
                return (T) createConstant(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTELEMENTSYMBOL:
                return (T) createElementSymbol(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTBLOCK:
                return (T) createBlock(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTEXPRESSIONCRITERIA:
                return (T) createExpressionCriteria(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTALIASSYMBOL:
                return (T) createAliasSymbol(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTAGGREGATESYMBOL:
                return (T) createAggregateSymbol(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTWINDOWFUNCTION:
                return (T) createWindowFunction(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTWINDOWSPECIFICATION:
                return (T) createWindowSpecification(teiidParser, nodeType);
            case Teiid8ParserTreeConstants.JJTTEXTLINE:
                return (T) createTextLine(teiidParser, nodeType);
                
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private TextLine createTextLine(Teiid8Parser teiidParser, int nodeType) {
        return new TextLine(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private WindowSpecification createWindowSpecification(Teiid8Parser teiidParser, int nodeType) {
        return new WindowSpecification(teiidParser, nodeType);
    }

    private WindowFunction createWindowFunction(TeiidParser teiidParser, int nodeType) {
        if (isTeiid8Parser(teiidParser))
            return new Window8Function((Teiid8Parser) teiidParser, nodeType);
//      else if (isTeiid7Parser(teiidParser)
//      return new Window7Function((Teiid7Parser) teiidParser, nodeType);
        throw new IllegalStateException();
    }
    
    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private AggregateSymbol createAggregateSymbol(TeiidParser teiidParser, int nodeType) {
        if (isTeiid8Parser(teiidParser))
            return new Aggregate8Symbol((Teiid8Parser) teiidParser, nodeType);
//        else if (isTeiid7Parser(teiidParser)
//            return new Aggregate7Symbol((Teiid7Parser) teiidParser, nodeType);
        
        throw new IllegalStateException();
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private AliasSymbol createAliasSymbol(TeiidParser teiidParser, int nodeType) {
        return new AliasSymbol(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private ExpressionCriteria createExpressionCriteria(TeiidParser teiidParser, int nodeType) {
        return new ExpressionCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Block createBlock(TeiidParser teiidParser, int nodeType) {
        return new Block(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private ElementSymbol createElementSymbol(TeiidParser teiidParser, int nodeType) {
        return new ElementSymbol(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Constant createConstant(TeiidParser teiidParser, int nodeType) {
        return new Constant(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private GroupSymbol createGroupSymbol(TeiidParser teiidParser, int nodeType) {
        return new GroupSymbol(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private ScalarSubquery createScalarSubquery(TeiidParser teiidParser, int nodeType) {
        return new ScalarSubquery(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private XMLNamespaces createXMLNamespaces(TeiidParser teiidParser, int nodeType) {
        return new XMLNamespaces(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private XMLForest createXMLForest(TeiidParser teiidParser, int nodeType) {
        return new XMLForest(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private JSONObject createJSONObject(TeiidParser teiidParser, int nodeType) {
        return new JSONObject(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private XMLAttributes createXMLAttributes(TeiidParser teiidParser, int nodeType) {
        return new XMLAttributes(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private XMLElement createXMLElement(TeiidParser teiidParser, int nodeType) {
        return new XMLElement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private QueryString createQueryString(TeiidParser teiidParser, int nodeType) {
        return new QueryString(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private XMLParse createXMLParse(TeiidParser teiidParser, int nodeType) {
        return new XMLParse(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Function createFunction(TeiidParser teiidParser, int nodeType) {
        return new Function(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private SearchedCaseExpression createSearchedCaseExpression(TeiidParser teiidParser, int nodeType) {
        return new SearchedCaseExpression(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private CaseExpression createCaseExpression(TeiidParser teiidParser, int nodeType) {
        return new CaseExpression(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Reference createReference(TeiidParser teiidParser, int nodeType) {
        return new Reference(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Option createOption(TeiidParser teiidParser, int nodeType) {
        return new Option(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Limit createLimit(TeiidParser teiidParser, int nodeType) {
        return new Limit(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private OrderByItem createOrderByItem(TeiidParser teiidParser, int nodeType) {
        return new OrderByItem(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private OrderBy createOrderBy(TeiidParser teiidParser, int nodeType) {
        return new OrderBy(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private GroupBy createGroupBy(TeiidParser teiidParser, int nodeType) {
        return new GroupBy(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private ExistsCriteria createExistsCriteria(TeiidParser teiidParser, int nodeType) {
        return new ExistsCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private SetCriteria createSetCriteria(TeiidParser teiidParser, int nodeType) {
        return new SetCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private SubquerySetCriteria createSubquerySetCriteria(TeiidParser teiidParser, int nodeType) {
        return new SubquerySetCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private IsNullCriteria createIsNullCriteria(TeiidParser teiidParser, int nodeType) {
        return new IsNullCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private BetweenCriteria createBetweenCriteria(TeiidParser teiidParser, int nodeType) {
        return new BetweenCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private MatchCriteria createMatchCriteria(TeiidParser teiidParser, int nodeType) {
        return new MatchCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private SubqueryCompareCriteria createSubqueryCompareCriteria(TeiidParser teiidParser, int nodeType) {
        return new SubqueryCompareCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private CompareCriteria createCompareCriteria(TeiidParser teiidParser, int nodeType) {
        return new CompareCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private NotCriteria createNotCriteria(TeiidParser teiidParser, int nodeType) {
        return new NotCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private CompoundCriteria createCompoundCriteria(TeiidParser teiidParser, int nodeType) {
        return new CompoundCriteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Criteria createCriteria(TeiidParser teiidParser, int nodeType) {
        return new Criteria(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private UnaryFromClause createUnaryFromClause(TeiidParser teiidParser, int nodeType) {
        return new UnaryFromClause(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private SubqueryFromClause createSubQueryFromClause(TeiidParser teiidParser, int nodeType) {
        return new SubqueryFromClause(teiidParser, nodeType);
    }

    private XMLColumn createXMLColumn(TeiidParser teiidParser, int nodeType) {
        return new XMLColumn(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private XMLTable createXMLTable(TeiidParser teiidParser, int nodeType) {
        return new XMLTable(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private ObjectColumn createObjectColumn(TeiidParser teiidParser, int nodeType) {
        return new ObjectColumn(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private ObjectTable createObjectTable(TeiidParser teiidParser, int nodeType) {
        return new ObjectTable(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private XMLQuery createXMLQuery(TeiidParser teiidParser, int nodeType) {
        return new XMLQuery(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private TextColumn createTextColumn(TeiidParser teiidParser, int nodeType) {
        return new TextColumn(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private TextTable createTextTable(TeiidParser teiidParser, int nodeType) {
        return new TextTable(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private ArrayTable createArrayTable(TeiidParser teiidParser, int nodeType) {
        return new ArrayTable(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private XMLSerialize createXMLSerialize(TeiidParser teiidParser, int nodeType) {
        return new XMLSerialize(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private JoinType createJoinType(TeiidParser teiidParser, int nodeType) {
        return new JoinType(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private JoinPredicate createJoinPredicate(TeiidParser teiidParser, int nodeType) {
        return new JoinPredicate(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private FromClause createFromClause(TeiidParser teiidParser, int nodeType) {
        return new FromClause(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private From createFrom(TeiidParser teiidParser, int nodeType) {
        return new From(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private MultipleElementSymbol createMultipleElementSymbol(TeiidParser teiidParser, int nodeType) {
        return new MultipleElementSymbol(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private DerivedColumn createDerivedColumn(TeiidParser teiidParser, int nodeType) {
        return new DerivedColumn(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Select createSelect(TeiidParser teiidParser, int nodeType) {
        return new Select(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Into createInto(TeiidParser teiidParser, int nodeType) {
        return new Into(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Query createQuery(TeiidParser teiidParser, int nodeType) {
        return new Query(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private WithQueryCommand createWithQueryCommand(TeiidParser teiidParser, int nodeType) {
        return new WithQueryCommand(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private QueryCommand createQueryCommand(TeiidParser teiidParser, int nodeType) {
        return new QueryCommand(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Delete createDelete(TeiidParser teiidParser, int nodeType) {
        return new Delete(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Update createUpdate(TeiidParser teiidParser, int nodeType) {
        return new Update(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Insert createInsert(TeiidParser teiidParser, int nodeType) {
        return new Insert(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private StoredProcedure createStoredProcedure(TeiidParser teiidParser, int nodeType) {
        return new StoredProcedure(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private ProjectedColumn createProjectedColumn(TeiidParser teiidParser, int nodeType) {
        return new ProjectedColumn(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private SetClause createSetClause(TeiidParser teiidParser, int nodeType) {
        return new SetClause(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private SetClauseList createSetClauseList(TeiidParser teiidParser, int nodeType) {
        return new SetClauseList(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private DynamicCommand createDynamicCommand(TeiidParser teiidParser, int nodeType) {
        return new DynamicCommand(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private CreateProcedureCommand createCreateProcedureCommand(TeiidParser teiidParser, int nodeType) {
        return new CreateProcedureCommand(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private CommandStatement createCommandStatement(TeiidParser teiidParser, int nodeType) {
        return new CommandStatement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private DeclareStatement createDeclareStatement(TeiidParser teiidParser, int nodeType) {
        return new DeclareStatement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private IfStatement createIfStatement(TeiidParser teiidParser, int nodeType) {
        return new IfStatement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private LoopStatement createLoopStatement(TeiidParser teiidParser, int nodeType) {
        return new LoopStatement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private WhileStatement createWhileStatement(TeiidParser teiidParser, int nodeType) {
        return new WhileStatement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private ReturnStatement createReturnStatement(TeiidParser teiidParser, int nodeType) {
        return new ReturnStatement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private BranchingStatement createBranchingStatement(TeiidParser teiidParser, int nodeType) {
        return new BranchingStatement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Statement createStatement(TeiidParser teiidParser, int nodeType) {
        return new Statement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private AssignmentStatement createAssignmentStatement(TeiidParser teiidParser, int nodeType) {
        return new AssignmentStatement(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Drop createDrop(TeiidParser teiidParser, int nodeType) {
        return new Drop(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private TriggerAction createTriggerAction(TeiidParser teiidParser, int nodeType) {
        return new TriggerAction(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Alter createAlter(TeiidParser teiidParser, int nodeType) {
        return new Alter(teiidParser, nodeType);
    }

    /**
     * @param teiidParser
     * @param nodeType
     * @return
     */
    private Command createCommand(Teiid8Parser teiidParser, int nodeType) {
        return new Command(teiidParser, nodeType);
    }

    /**
     * Method used by the generated parsers for constructing nodes
     *
     * @param teiidParser
     * @param nodeType
     *
     * @return created language object
     */
    public static LanguageObject jjtCreate(TeiidParser teiidParser, int nodeType) {
        return getInstance().create(teiidParser, nodeType);
    }
}
