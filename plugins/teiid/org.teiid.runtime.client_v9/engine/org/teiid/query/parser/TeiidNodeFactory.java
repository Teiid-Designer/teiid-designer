/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.parser;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.lang.AlterProcedure;
import org.teiid.query.sql.lang.AlterTrigger;
import org.teiid.query.sql.lang.AlterView;
import org.teiid.query.sql.lang.ArrayTable;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Create;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.Drop;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.ExpressionCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.IsDistinctCriteria;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.ObjectColumn;
import org.teiid.query.sql.lang.ObjectTable;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.ProjectedColumn;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.SetClauseList;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.Teiid9ParserTreeConstants;
import org.teiid.query.sql.lang.TextColumn;
import org.teiid.query.sql.lang.TextTable;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.lang.WithQueryCommand;
import org.teiid.query.sql.lang.XMLColumn;
import org.teiid.query.sql.lang.XMLTable;
import org.teiid.query.sql.lang.v9.Alter9Procedure;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.ExceptionExpression;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.RaiseStatement;
import org.teiid.query.sql.proc.ReturnStatement;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.proc.WhileStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.Array;
import org.teiid.query.sql.symbol.CaseExpression;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.DerivedColumn;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.JSONObject;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.QueryString;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.SearchedCaseExpression;
import org.teiid.query.sql.symbol.TextLine;
import org.teiid.query.sql.symbol.WindowFunction;
import org.teiid.query.sql.symbol.WindowSpecification;
import org.teiid.query.sql.symbol.XMLAttributes;
import org.teiid.query.sql.symbol.XMLCast;
import org.teiid.query.sql.symbol.XMLElement;
import org.teiid.query.sql.symbol.XMLExists;
import org.teiid.query.sql.symbol.XMLForest;
import org.teiid.query.sql.symbol.XMLNamespaces;
import org.teiid.query.sql.symbol.XMLParse;
import org.teiid.query.sql.symbol.XMLQuery;
import org.teiid.query.sql.symbol.XMLSerialize;
import org.teiid.query.sql.symbol.v9.Aggregate9Symbol;
import org.teiid.query.sql.symbol.v9.Window8Function;
import org.teiid.runtime.client.Messages;

/**
 * Factory for creating parser nodes
 */
public class TeiidNodeFactory {

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

    private static boolean isTeiid9Version(ITeiidServerVersion teiidVersion) {
    	return teiidVersion.getMajor().equals(ITeiidServerVersion.NINE);
    }

    /**
     * Convenience method for creating new AST Node
     *
     * @param teiidVersion
     * @param nodeType
     * @return new AST Node
     */
    public static <T extends LanguageObject> T createASTNode(ITeiidServerVersion teiidVersion, ASTNodes nodeType) {
    	return TeiidNodeFactory.getInstance().create(teiidVersion, nodeType);
    }

    /**
     * Create a parser node for the node with the given common node name
     * @see teiidVersion#createASTNode(ASTNodes)
     *
     * @param teiidVersion
     * @param nodeType
     *
     * @return node applicable to the given parser
     */
    public <T extends LanguageObject> T create(ITeiidServerVersion teiidVersion, ASTNodes nodeType) {
        
        if (isTeiid9Version(teiidVersion)) {
            for (int i = 0; i < Teiid9ParserTreeConstants.jjtNodeName.length; ++i) {
                String constantName = Teiid9ParserTreeConstants.jjtNodeName[i];
                if (! constantName.equalsIgnoreCase(nodeType.getName()))
                    continue;

                return create(teiidVersion, i);
            }
        }

        throw new IllegalArgumentException(Messages.getString(Messages.TeiidParser.invalidNodeType, nodeType.getName(), teiidVersion));
    }

    /**
     * Create a parser node for the given node type
     *
     * @param teiidVersion
     * @param nodeType
     *
     * @return node applicable to the given parser
     */
    public <T extends LanguageObject> T create(ITeiidServerVersion teiidVersion, int nodeType) {
        if (isTeiid9Version(teiidVersion))
            return createV9(teiidVersion, nodeType);

        throw new IllegalArgumentException(Messages.getString(Messages.TeiidParser.invalidNodeType, nodeType, teiidVersion));
    }

    /* ################## Framework for generating the remainder of this class #################### */

    private static final String DOT = StringUtil.Constants.DOT;
    private static final String UNDERSCORE = "_"; //$NON-NLS-1$
    private static final String OPEN_BRACKET = "("; //$NON-NLS-1$
    private static final String CLOSE_BRACKET = ")"; //$NON-NLS-1$
    private static final String SPEECH_MARK = "\""; //$NON-NLS-1$
    private static final String SEMI_COLON = ";"; //$NON-NLS-1$
    private static final String COMMA = ","; //$NON-NLS-1$
    private static final String PREFIX = "JJT"; //$NON-NLS-1$
    private static final String NEWLINE = "\n"; //$NON-NLS-1$
    private static final String TAB = "\t"; //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String NON_NLS = "//$NON-NLS-1$"; //$NON-NLS-1$
    private static final String PACKAGE_NAME = "org.teiid.query.sql.lang"; //$NON-NLS-1$
    private static final String CONSTANT_CLASS_PREFIX = "Teiid"; //$NON-NLS-1$
    private static final String CONSTANT_CLASS_POSTFIX = "ParserTreeConstants"; //$NON-NLS-1$
    private static final String NODENAME_FIELD = "jjtNodeName"; //$NON-NLS-1$
    private static final String VOID = "VOID"; //$NON-NLS-1$
    private static final String[] COMPONENT_METHOD_EXCLUSIONS = { "AggregateSymbol", "WindowFunction", "AlterProcedure" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    private static final Map<String, String> AST_NODE_ANNOTATIONS = new HashMap<String, String>();

    private Map<String, String> indexNodeNames(Class<?> constantClass) throws NoSuchFieldException, IllegalAccessException {
        /*
         * Find the jjtNodeName declarations and index the
         * values in a map with their lower case names as keys.
         * Use this to convert the constants into their respective
         * camel case method names.
         */
        Field nodeNameField = constantClass.getField(NODENAME_FIELD);
        Object nodeNameObj = nodeNameField.get(null);
        String[] nodeNameFields = (String[]) nodeNameObj;
        Map<String, String> nodeNameMap = new HashMap<String, String>();
        for (String nodeName : nodeNameFields) {
            nodeNameMap.put(nodeName.toLowerCase(), nodeName);
        }

        return nodeNameMap;
    }

    private String createASTNodesEnumDeclaration() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("/**" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * Names of AST nodes to allow creation outside of the parsers" + NEWLINE); //$NON-NLS-1$
        buffer.append(" *" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * @generated" +  NEWLINE); //$NON-NLS-1$
        buffer.append(" */" + NEWLINE); //$NON-NLS-1$
        buffer.append("public enum ASTNodes {" + NEWLINE); //$NON-NLS-1$

        return buffer.toString();
    }

    private String createASTNodeEnumValue(String typeName) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(TAB + "/**" + NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + " * " + typeName + NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + " * @generated" +  NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + " */" + NEWLINE); //$NON-NLS-1$

        String annotation = AST_NODE_ANNOTATIONS.get(typeName);
        if (annotation != null)
            buffer.append(TAB + annotation + NEWLINE);

        buffer.append(TAB);
        for (int i = 0; i < typeName.length(); ++i) {
            Character c = typeName.charAt(i);

            // Avoid issues with sequences of capitals such as XMLSerialise
            Character c1 = null;
            if ((i + 1) < typeName.length())
                c1 = typeName.charAt(i + 1);

            if (i > 0 && Character.isUpperCase(c) && ! (c1 != null && Character.isUpperCase(c1)))
                buffer.append(UNDERSCORE);

            buffer.append(Character.toUpperCase(c));
        }
        buffer.append(OPEN_BRACKET + SPEECH_MARK);
        buffer.append(typeName);
        buffer.append(SPEECH_MARK + CLOSE_BRACKET);
        buffer.append(COMMA + SPACE + NON_NLS + NEWLINE);

        return buffer.toString();
    }

    private String createASTNodesEnumMethods() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(TAB + "private String name" + SEMI_COLON + NEWLINE); //$NON-NLS-1$
        buffer.append(NEWLINE);
        buffer.append(TAB + "ASTNodes(String name) {" + NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + TAB + "this.name = name" + SEMI_COLON + NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + "}" + NEWLINE); //$NON-NLS-1$
        buffer.append(NEWLINE);
        buffer.append(TAB + "/**" + NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + " * @return Name of this common node" + NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + " */" + NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + "public String getName() {" + NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + TAB + "return name" + SEMI_COLON + NEWLINE); //$NON-NLS-1$
        buffer.append(TAB + "}" + NEWLINE); //$NON-NLS-1$
        buffer.append("}" + NEWLINE); //$NON-NLS-1$

        return buffer.toString();
    }

    private String createMethodDeclaration(int serverVersion) {
        StringBuffer buffer = new StringBuffer();

        buffer.append("/**" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * Create a version " + serverVersion + " teiid parser node for the given node type." + NEWLINE);  //$NON-NLS-1$//$NON-NLS-2$
        buffer.append(" *" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * @generated" + NEWLINE); //$NON-NLS-1$
        buffer.append(" *" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * @param teiidVersion" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * @param nodeType" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * @return version " +serverVersion + " teiid parser node" + NEWLINE); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append(" */" + NEWLINE); //$NON-NLS-1$
        buffer.append("private <T extends LanguageObject> T createV" + serverVersion + "(ITeiidServerVersion teiidVersion, int nodeType) {" + NEWLINE); //$NON-NLS-1$ //$NON-NLS-2$

        return buffer.toString();
    }

    private String createSwitchCase(String astIdentifier, String typeName, String constantClassName) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\t\tcase " + constantClassName + DOT + astIdentifier + ":" + NEWLINE); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("\t\t\treturn (T) create" + typeName + "(teiidVersion, nodeType)" + SEMI_COLON + NEWLINE); //$NON-NLS-1$ //$NON-NLS-2$
        
        return buffer.toString();
    }

    private String createComponentMethod(String typeName) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("/**" + NEWLINE); //$NON-NLS-1$
        buffer.append(" *" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * @generated" + NEWLINE); //$NON-NLS-1$
        buffer.append(" *" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * @param teiidVersion" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * @param nodeType" + NEWLINE); //$NON-NLS-1$
        buffer.append(" * @return" + NEWLINE); //$NON-NLS-1$
        buffer.append(" */" + NEWLINE); //$NON-NLS-1$
        buffer.append("private " + typeName + " create" + typeName + "(ITeiidServerVersion teiidVersion, int nodeType) {" + NEWLINE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buffer.append(TAB + "return new " + typeName + "(teiidVersion, nodeType)" + SEMI_COLON + NEWLINE); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("}" + NEWLINE); //$NON-NLS-1$

        return buffer.toString();
    }
    
    private void generate(int... serverVersions) throws Exception {
        Set<String> componentMethods = new LinkedHashSet<String>();
        Set<String> astNodesEnum = new LinkedHashSet<String>();
        StringBuffer createMethodBuffer = new StringBuffer();
        List<String> componentMethodExclusions = Arrays.asList(COMPONENT_METHOD_EXCLUSIONS);

        astNodesEnum.add(createASTNodesEnumDeclaration());

        for (int serverVersion : serverVersions) {
            String constantClassName = CONSTANT_CLASS_PREFIX + serverVersion + CONSTANT_CLASS_POSTFIX;
            Class<?> constantClass = Class.forName(PACKAGE_NAME + DOT + constantClassName);

            /* Index node names against their camelcase equivalents */
            Map<String, String> nodeNameIndex = indexNodeNames(constantClass);

            createMethodBuffer.append(createMethodDeclaration(serverVersion));

            // Create switch statement
            createMethodBuffer.append(TAB + "switch (nodeType) {" + NEWLINE); //$NON-NLS-1$

            for (Field field : constantClass.getFields()) {
                String fieldName = field.getName();
                if (! fieldName.startsWith(PREFIX))
                    continue;

                if (fieldName.equalsIgnoreCase(PREFIX + VOID))
                    continue;
                
                String astName = fieldName.substring(PREFIX.length());
                String typeName = nodeNameIndex.get(astName.toLowerCase());
                
                // Append to main create's switch statement
                createMethodBuffer.append(createSwitchCase(fieldName, typeName, constantClassName));

                // Create component method if not already created
                if (! componentMethodExclusions.contains(typeName))
                    componentMethods.add(createComponentMethod(typeName));

                // Create AST Node enum if not already created
                astNodesEnum.add(createASTNodeEnumValue(typeName));
            }

            // Complete switch statement
            createMethodBuffer.append(TAB + TAB + "default:" + NEWLINE); //$NON-NLS-1$
            createMethodBuffer.append(TAB + TAB + TAB);
            createMethodBuffer.append("throw new IllegalArgumentException(" //$NON-NLS-1$
                                                                + "Messages.getString(Messages.TeiidParser.invalidNodeType, " //$NON-NLS-1$
                                                                + "nodeType, teiidVersion))"); //$NON-NLS-1$
            createMethodBuffer.append(SEMI_COLON + NEWLINE);
            createMethodBuffer.append(TAB + "}" + NEWLINE); //$NON-NLS-1$
            createMethodBuffer.append("}" + NEWLINE + NEWLINE); //$NON-NLS-1$
        }

        // Replace the last enum value's comma with a semi-colon
        Iterator<String> iter = astNodesEnum.iterator();
        String lastValue = null;
        while(iter.hasNext()) {
            lastValue = iter.next();
        }
        astNodesEnum.remove(lastValue);
        lastValue = lastValue.replace(COMMA, SEMI_COLON);
        astNodesEnum.add(lastValue);

        // Complete AST Node Enum
        astNodesEnum.add(createASTNodesEnumMethods());

        for (String value : astNodesEnum) {
            System.out.println(value);
        }

        for (String method : componentMethods) {
            System.out.println(method);
        }

        System.out.println(createMethodBuffer.toString());
    }
    
    /**
     * Execute to auto-generate the factory methods based on the
     * TeiidnTreeParserConstants interfaces.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        TeiidNodeFactory factory = new TeiidNodeFactory();
        factory.generate(9);
    }

    /**
     * Method used by the generated parsers for constructing nodes
     *
     * Note.
     * This tells the parser what the current node is, which is used for recording
     * comments on the language object tree.
     *
     * @param teiidParser
     * @param nodeType
     *
     * @return created language object
     */
    public static LanguageObject jjtCreate(TeiidParserSPI teiidParser, int nodeType) {
        LanguageObject langObject = getInstance().create(teiidParser.getVersion(), nodeType);
        teiidParser.setCurrentNode(langObject);
        return langObject;
    }

    /**
     * Method used by the generated parsers for constructing nodes
     *
     * Note.
     * This tells the parser what the current node is, which is used for recording
     * comments on the language object tree. 
     *             
     *
     * @param teiidParser
     * @param nodeType
     *
     * @return created language object
     */
    public static <T extends LanguageObject> T jjtCreate(TeiidParserSPI teiidParser, ASTNodes nodeType) {
        T langObject = getInstance().create(teiidParser.getVersion(), nodeType);
        teiidParser.setCurrentNode(langObject);
        return langObject;
    }

    private WindowFunction createWindowFunction(ITeiidServerVersion teiidVersion, int nodeType) {
        if (isTeiid9Version(teiidVersion))
            return new Window8Function(teiidVersion, nodeType);

        throw new IllegalArgumentException(Messages.getString(Messages.TeiidParser.invalidNodeType, nodeType, teiidVersion));
    }

    /**
     * @param teiidVersion
     * @param nodeType
     * @return
     */
    private AggregateSymbol createAggregateSymbol(ITeiidServerVersion teiidVersion, int nodeType) {
        if (isTeiid9Version(teiidVersion))
            return new Aggregate9Symbol(teiidVersion, nodeType);
        
        throw new IllegalArgumentException(Messages.getString(Messages.TeiidParser.invalidNodeType, nodeType, teiidVersion));
    }

   /**
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private AlterProcedure createAlterProcedure(ITeiidServerVersion teiidVersion, int nodeType) {
       if (isTeiid9Version(teiidVersion))
           return new Alter9Procedure(teiidVersion, nodeType);

       throw new IllegalArgumentException(Messages.getString(Messages.TeiidParser.invalidNodeType, nodeType, teiidVersion));
   }

    /*
     * ############### Methods below auto-generated by executing this class as a java application ##############
     */
   /**
    * Names of AST nodes to allow creation outside of the parsers
    *
    * @generated
    */
   public enum ASTNodes {

       /**
        * TriggerAction
        * @generated
        */
       TRIGGER_ACTION("TriggerAction"), //$NON-NLS-1$

       /**
        * Drop
        * @generated
        */
       DROP("Drop"), //$NON-NLS-1$

       /**
        * Create
        * @generated
        */
       CREATE("Create"), //$NON-NLS-1$

       /**
        * RaiseStatement
        * @generated
        */
       RAISE_STATEMENT("RaiseStatement"), //$NON-NLS-1$

       /**
        * ExceptionExpression
        * @generated
        */
       EXCEPTION_EXPRESSION("ExceptionExpression"), //$NON-NLS-1$

       /**
        * BranchingStatement
        * @generated
        */
       BRANCHING_STATEMENT("BranchingStatement"), //$NON-NLS-1$

       /**
        * ReturnStatement
        * @generated
        */
       RETURN_STATEMENT("ReturnStatement"), //$NON-NLS-1$

       /**
        * WhileStatement
        * @generated
        */
       WHILE_STATEMENT("WhileStatement"), //$NON-NLS-1$

       /**
        * LoopStatement
        * @generated
        */
       LOOP_STATEMENT("LoopStatement"), //$NON-NLS-1$

       /**
        * IfStatement
        * @generated
        */
       IF_STATEMENT("IfStatement"), //$NON-NLS-1$

       /**
        * DeclareStatement
        * @generated
        */
       DECLARE_STATEMENT("DeclareStatement"), //$NON-NLS-1$

       /**
        * CommandStatement
        * @generated
        */
       COMMAND_STATEMENT("CommandStatement"), //$NON-NLS-1$

       /**
        * CreateProcedureCommand
        * @generated
        */
       CREATE_PROCEDURE_COMMAND("CreateProcedureCommand"), //$NON-NLS-1$

       /**
        * DynamicCommand
        * @generated
        */
       DYNAMIC_COMMAND("DynamicCommand"), //$NON-NLS-1$

       /**
        * SetClauseList
        * @generated
        */
       SET_CLAUSE_LIST("SetClauseList"), //$NON-NLS-1$

       /**
        * SetClause
        * @generated
        */
       SET_CLAUSE("SetClause"), //$NON-NLS-1$

       /**
        * ProjectedColumn
        * @generated
        */
       PROJECTED_COLUMN("ProjectedColumn"), //$NON-NLS-1$

       /**
        * StoredProcedure
        * @generated
        */
       STORED_PROCEDURE("StoredProcedure"), //$NON-NLS-1$

       /**
        * Insert
        * @generated
        */
       INSERT("Insert"), //$NON-NLS-1$

       /**
        * Update
        * @generated
        */
       UPDATE("Update"), //$NON-NLS-1$

       /**
        * Delete
        * @generated
        */
       DELETE("Delete"), //$NON-NLS-1$

       /**
        * WithQueryCommand
        * @generated
        */
       WITH_QUERY_COMMAND("WithQueryCommand"), //$NON-NLS-1$

       /**
        * SetQuery
        * @generated
        */
       SET_QUERY("SetQuery"), //$NON-NLS-1$

       /**
        * Query
        * @generated
        */
       QUERY("Query"), //$NON-NLS-1$

       /**
        * Into
        * @generated
        */
       INTO("Into"), //$NON-NLS-1$

       /**
        * Select
        * @generated
        */
       SELECT("Select"), //$NON-NLS-1$

       /**
        * ExpressionSymbol
        * @generated
        */
       EXPRESSION_SYMBOL("ExpressionSymbol"), //$NON-NLS-1$

       /**
        * DerivedColumn
        * @generated
        */
       DERIVED_COLUMN("DerivedColumn"), //$NON-NLS-1$

       /**
        * MultipleElementSymbol
        * @generated
        */
       MULTIPLE_ELEMENT_SYMBOL("MultipleElementSymbol"), //$NON-NLS-1$

       /**
        * From
        * @generated
        */
       FROM("From"), //$NON-NLS-1$

       /**
        * JoinPredicate
        * @generated
        */
       JOIN_PREDICATE("JoinPredicate"), //$NON-NLS-1$

       /**
        * JoinType
        * @generated
        */
       JOIN_TYPE("JoinType"), //$NON-NLS-1$

       /**
        * XMLSerialize
        * @generated
        */
       XML_SERIALIZE("XMLSerialize"), //$NON-NLS-1$

       /**
        * ArrayTable
        * @generated
        */
       ARRAY_TABLE("ArrayTable"), //$NON-NLS-1$

       /**
        * TextTable
        * @generated
        */
       TEXT_TABLE("TextTable"), //$NON-NLS-1$

       /**
        * TextColumn
        * @generated
        */
       TEXT_COLUMN("TextColumn"), //$NON-NLS-1$

       /**
        * XMLExists
        * @generated
        */
       XML_EXISTS("XMLExists"), //$NON-NLS-1$

       /**
        * XMLQuery
        * @generated
        */
       XML_QUERY("XMLQuery"), //$NON-NLS-1$

       /**
        * ObjectTable
        * @generated
        */
       OBJECT_TABLE("ObjectTable"), //$NON-NLS-1$

       /**
        * ObjectColumn
        * @generated
        */
       OBJECT_COLUMN("ObjectColumn"), //$NON-NLS-1$

       /**
        * XMLTable
        * @generated
        */
       XML_TABLE("XMLTable"), //$NON-NLS-1$

       /**
        * XMLColumn
        * @generated
        */
       XML_COLUMN("XMLColumn"), //$NON-NLS-1$

       /**
        * SubqueryFromClause
        * @generated
        */
       SUBQUERY_FROM_CLAUSE("SubqueryFromClause"), //$NON-NLS-1$

       /**
        * UnaryFromClause
        * @generated
        */
       UNARY_FROM_CLAUSE("UnaryFromClause"), //$NON-NLS-1$

       /**
        * Criteria
        * @generated
        */
       CRITERIA("Criteria"), //$NON-NLS-1$

       /**
        * CompoundCriteria
        * @generated
        */
       COMPOUND_CRITERIA("CompoundCriteria"), //$NON-NLS-1$

       /**
        * NotCriteria
        * @generated
        */
       NOT_CRITERIA("NotCriteria"), //$NON-NLS-1$

       /**
        * CompareCriteria
        * @generated
        */
       COMPARE_CRITERIA("CompareCriteria"), //$NON-NLS-1$

       /**
        * SubqueryCompareCriteria
        * @generated
        */
       SUBQUERY_COMPARE_CRITERIA("SubqueryCompareCriteria"), //$NON-NLS-1$

       /**
        * MatchCriteria
        * @generated
        */
       MATCH_CRITERIA("MatchCriteria"), //$NON-NLS-1$

       /**
        * BetweenCriteria
        * @generated
        */
       BETWEEN_CRITERIA("BetweenCriteria"), //$NON-NLS-1$

       /**
        * IsNullCriteria
        * @generated
        */
       IS_NULL_CRITERIA("IsNullCriteria"), //$NON-NLS-1$

       /**
        * SubquerySetCriteria
        * @generated
        */
       SUBQUERY_SET_CRITERIA("SubquerySetCriteria"), //$NON-NLS-1$

       /**
        * SetCriteria
        * @generated
        */
       SET_CRITERIA("SetCriteria"), //$NON-NLS-1$

       /**
        * ExistsCriteria
        * @generated
        */
       EXISTS_CRITERIA("ExistsCriteria"), //$NON-NLS-1$

       /**
        * GroupBy
        * @generated
        */
       GROUP_BY("GroupBy"), //$NON-NLS-1$

       /**
        * OrderBy
        * @generated
        */
       ORDER_BY("OrderBy"), //$NON-NLS-1$

       /**
        * OrderByItem
        * @generated
        */
       ORDER_BY_ITEM("OrderByItem"), //$NON-NLS-1$

       /**
        * Limit
        * @generated
        */
       LIMIT("Limit"), //$NON-NLS-1$

       /**
        * Option
        * @generated
        */
       OPTION("Option"), //$NON-NLS-1$

       /**
        * Reference
        * @generated
        */
       REFERENCE("Reference"), //$NON-NLS-1$

       /**
        * CaseExpression
        * @generated
        */
       CASE_EXPRESSION("CaseExpression"), //$NON-NLS-1$

       /**
        * SearchedCaseExpression
        * @generated
        */
       SEARCHED_CASE_EXPRESSION("SearchedCaseExpression"), //$NON-NLS-1$

       /**
        * Function
        * @generated
        */
       FUNCTION("Function"), //$NON-NLS-1$

       /**
        * XMLParse
        * @generated
        */
       XML_PARSE("XMLParse"), //$NON-NLS-1$

       /**
        * QueryString
        * @generated
        */
       QUERY_STRING("QueryString"), //$NON-NLS-1$

       /**
        * XMLElement
        * @generated
        */
       XML_ELEMENT("XMLElement"), //$NON-NLS-1$

       /**
        * XMLAttributes
        * @generated
        */
       XML_ATTRIBUTES("XMLAttributes"), //$NON-NLS-1$

       /**
        * JSONObject
        * @generated
        */
       JSON_OBJECT("JSONObject"), //$NON-NLS-1$

       /**
        * XMLForest
        * @generated
        */
       XML_FOREST("XMLForest"), //$NON-NLS-1$

       /**
        * XMLNamespaces
        * @generated
        */
       XML_NAMESPACES("XMLNamespaces"), //$NON-NLS-1$

       /**
        * AssignmentStatement
        * @generated
        */
       ASSIGNMENT_STATEMENT("AssignmentStatement"), //$NON-NLS-1$

       /**
        * ScalarSubquery
        * @generated
        */
       SCALAR_SUBQUERY("ScalarSubquery"), //$NON-NLS-1$

       /**
        * GroupSymbol
        * @generated
        */
       GROUP_SYMBOL("GroupSymbol"), //$NON-NLS-1$

       /**
        * Constant
        * @generated
        */
       CONSTANT("Constant"), //$NON-NLS-1$

       /**
        * ElementSymbol
        * @generated
        */
       ELEMENT_SYMBOL("ElementSymbol"), //$NON-NLS-1$

       /**
        * Block
        * @generated
        */
       BLOCK("Block"), //$NON-NLS-1$

       /**
        * ExpressionCriteria
        * @generated
        */
       EXPRESSION_CRITERIA("ExpressionCriteria"), //$NON-NLS-1$

       /**
        * AliasSymbol
        * @generated
        */
       ALIAS_SYMBOL("AliasSymbol"), //$NON-NLS-1$

       /**
        * AggregateSymbol
        * @generated
        */
       AGGREGATE_SYMBOL("AggregateSymbol"), //$NON-NLS-1$

       /**
        * WindowFunction
        * @generated
        */
       WINDOW_FUNCTION("WindowFunction"), //$NON-NLS-1$

       /**
        * WindowSpecification
        * @generated
        */
       WINDOW_SPECIFICATION("WindowSpecification"), //$NON-NLS-1$

       /**
        * TextLine
        * @generated
        */
       TEXT_LINE("TextLine"), //$NON-NLS-1$

       /**
        * AlterTrigger
        * @generated
        */
       ALTER_TRIGGER("AlterTrigger"), //$NON-NLS-1$

       /**
        * AlterProcedure
        * @generated
        */
       ALTER_PROCEDURE("AlterProcedure"), //$NON-NLS-1$

       /**
        * AlterView
        * @generated
        */
       ALTER_VIEW("AlterView"), //$NON-NLS-1$

       /**
        * Array
        * @generated
        */
       ARRAY("Array"), //$NON-NLS-1$

       /**
        * XMLCast
        * @generated
        */
       XML_CAST("XMLCast"), //$NON-NLS-1$

       /**
        * IsDistinctCriteria
        * @generated
        */
       IS_DISTINCT_CRITERIA("IsDistinctCriteria"); //$NON-NLS-1$

       private String name;

       ASTNodes(String name) {
           this.name = name;
       }

       /**
        * @return Name of this common node
        */
       public String getName() {
           return name;
       }
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private TriggerAction createTriggerAction(ITeiidServerVersion teiidVersion, int nodeType) {
       return new TriggerAction(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Drop createDrop(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Drop(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Create createCreate(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Create(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private RaiseStatement createRaiseStatement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new RaiseStatement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ExceptionExpression createExceptionExpression(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ExceptionExpression(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private BranchingStatement createBranchingStatement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new BranchingStatement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ReturnStatement createReturnStatement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ReturnStatement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private WhileStatement createWhileStatement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new WhileStatement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private LoopStatement createLoopStatement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new LoopStatement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private IfStatement createIfStatement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new IfStatement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private DeclareStatement createDeclareStatement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new DeclareStatement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private CommandStatement createCommandStatement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new CommandStatement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private CreateProcedureCommand createCreateProcedureCommand(ITeiidServerVersion teiidVersion, int nodeType) {
       return new CreateProcedureCommand(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private DynamicCommand createDynamicCommand(ITeiidServerVersion teiidVersion, int nodeType) {
       return new DynamicCommand(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private SetClauseList createSetClauseList(ITeiidServerVersion teiidVersion, int nodeType) {
       return new SetClauseList(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private SetClause createSetClause(ITeiidServerVersion teiidVersion, int nodeType) {
       return new SetClause(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ProjectedColumn createProjectedColumn(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ProjectedColumn(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private StoredProcedure createStoredProcedure(ITeiidServerVersion teiidVersion, int nodeType) {
       return new StoredProcedure(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Insert createInsert(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Insert(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Update createUpdate(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Update(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Delete createDelete(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Delete(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private WithQueryCommand createWithQueryCommand(ITeiidServerVersion teiidVersion, int nodeType) {
       return new WithQueryCommand(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private SetQuery createSetQuery(ITeiidServerVersion teiidVersion, int nodeType) {
       return new SetQuery(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Query createQuery(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Query(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Into createInto(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Into(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Select createSelect(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Select(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ExpressionSymbol createExpressionSymbol(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ExpressionSymbol(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private DerivedColumn createDerivedColumn(ITeiidServerVersion teiidVersion, int nodeType) {
       return new DerivedColumn(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private MultipleElementSymbol createMultipleElementSymbol(ITeiidServerVersion teiidVersion, int nodeType) {
       return new MultipleElementSymbol(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private From createFrom(ITeiidServerVersion teiidVersion, int nodeType) {
       return new From(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private JoinPredicate createJoinPredicate(ITeiidServerVersion teiidVersion, int nodeType) {
       return new JoinPredicate(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private JoinType createJoinType(ITeiidServerVersion teiidVersion, int nodeType) {
       return new JoinType(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLSerialize createXMLSerialize(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLSerialize(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ArrayTable createArrayTable(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ArrayTable(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private TextTable createTextTable(ITeiidServerVersion teiidVersion, int nodeType) {
       return new TextTable(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private TextColumn createTextColumn(ITeiidServerVersion teiidVersion, int nodeType) {
       return new TextColumn(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLExists createXMLExists(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLExists(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLQuery createXMLQuery(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLQuery(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ObjectTable createObjectTable(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ObjectTable(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ObjectColumn createObjectColumn(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ObjectColumn(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLTable createXMLTable(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLTable(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLColumn createXMLColumn(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLColumn(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private SubqueryFromClause createSubqueryFromClause(ITeiidServerVersion teiidVersion, int nodeType) {
       return new SubqueryFromClause(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private UnaryFromClause createUnaryFromClause(ITeiidServerVersion teiidVersion, int nodeType) {
       return new UnaryFromClause(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Criteria createCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Criteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private CompoundCriteria createCompoundCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new CompoundCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private NotCriteria createNotCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new NotCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private CompareCriteria createCompareCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new CompareCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private SubqueryCompareCriteria createSubqueryCompareCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new SubqueryCompareCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private MatchCriteria createMatchCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new MatchCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private BetweenCriteria createBetweenCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new BetweenCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private IsNullCriteria createIsNullCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new IsNullCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private SubquerySetCriteria createSubquerySetCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new SubquerySetCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private SetCriteria createSetCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new SetCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ExistsCriteria createExistsCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ExistsCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private GroupBy createGroupBy(ITeiidServerVersion teiidVersion, int nodeType) {
       return new GroupBy(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private OrderBy createOrderBy(ITeiidServerVersion teiidVersion, int nodeType) {
       return new OrderBy(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private OrderByItem createOrderByItem(ITeiidServerVersion teiidVersion, int nodeType) {
       return new OrderByItem(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Limit createLimit(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Limit(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Option createOption(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Option(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Reference createReference(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Reference(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private CaseExpression createCaseExpression(ITeiidServerVersion teiidVersion, int nodeType) {
       return new CaseExpression(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private SearchedCaseExpression createSearchedCaseExpression(ITeiidServerVersion teiidVersion, int nodeType) {
       return new SearchedCaseExpression(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Function createFunction(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Function(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLParse createXMLParse(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLParse(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private QueryString createQueryString(ITeiidServerVersion teiidVersion, int nodeType) {
       return new QueryString(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLElement createXMLElement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLElement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLAttributes createXMLAttributes(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLAttributes(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private JSONObject createJSONObject(ITeiidServerVersion teiidVersion, int nodeType) {
       return new JSONObject(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLForest createXMLForest(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLForest(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLNamespaces createXMLNamespaces(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLNamespaces(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private AssignmentStatement createAssignmentStatement(ITeiidServerVersion teiidVersion, int nodeType) {
       return new AssignmentStatement(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ScalarSubquery createScalarSubquery(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ScalarSubquery(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private GroupSymbol createGroupSymbol(ITeiidServerVersion teiidVersion, int nodeType) {
       return new GroupSymbol(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Constant createConstant(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Constant(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ElementSymbol createElementSymbol(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ElementSymbol(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Block createBlock(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Block(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private ExpressionCriteria createExpressionCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new ExpressionCriteria(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private AliasSymbol createAliasSymbol(ITeiidServerVersion teiidVersion, int nodeType) {
       return new AliasSymbol(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private WindowSpecification createWindowSpecification(ITeiidServerVersion teiidVersion, int nodeType) {
       return new WindowSpecification(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private TextLine createTextLine(ITeiidServerVersion teiidVersion, int nodeType) {
       return new TextLine(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private AlterTrigger createAlterTrigger(ITeiidServerVersion teiidVersion, int nodeType) {
       return new AlterTrigger(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private AlterView createAlterView(ITeiidServerVersion teiidVersion, int nodeType) {
       return new AlterView(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private Array createArray(ITeiidServerVersion teiidVersion, int nodeType) {
       return new Array(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private XMLCast createXMLCast(ITeiidServerVersion teiidVersion, int nodeType) {
       return new XMLCast(teiidVersion, nodeType);
   }

   /**
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return
    */
   private IsDistinctCriteria createIsDistinctCriteria(ITeiidServerVersion teiidVersion, int nodeType) {
       return new IsDistinctCriteria(teiidVersion, nodeType);
   }

   /**
    * Create a version 9 teiid parser node for the given node type.
    *
    * @generated
    *
    * @param teiidVersion
    * @param nodeType
    * @return version 9 teiid parser node
    */
   private <T extends LanguageObject> T createV9(ITeiidServerVersion teiidVersion, int nodeType) {
       switch (nodeType) {
           case Teiid9ParserTreeConstants.JJTTRIGGERACTION:
               return (T) createTriggerAction(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTDROP:
               return (T) createDrop(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTCREATE:
               return (T) createCreate(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTRAISESTATEMENT:
               return (T) createRaiseStatement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTEXCEPTIONEXPRESSION:
               return (T) createExceptionExpression(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTBRANCHINGSTATEMENT:
               return (T) createBranchingStatement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTRETURNSTATEMENT:
               return (T) createReturnStatement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTWHILESTATEMENT:
               return (T) createWhileStatement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTLOOPSTATEMENT:
               return (T) createLoopStatement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTIFSTATEMENT:
               return (T) createIfStatement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTDECLARESTATEMENT:
               return (T) createDeclareStatement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTCOMMANDSTATEMENT:
               return (T) createCommandStatement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTCREATEPROCEDURECOMMAND:
               return (T) createCreateProcedureCommand(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTDYNAMICCOMMAND:
               return (T) createDynamicCommand(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSETCLAUSELIST:
               return (T) createSetClauseList(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSETCLAUSE:
               return (T) createSetClause(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTPROJECTEDCOLUMN:
               return (T) createProjectedColumn(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSTOREDPROCEDURE:
               return (T) createStoredProcedure(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTINSERT:
               return (T) createInsert(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTUPDATE:
               return (T) createUpdate(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTDELETE:
               return (T) createDelete(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTWITHQUERYCOMMAND:
               return (T) createWithQueryCommand(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSETQUERY:
               return (T) createSetQuery(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTQUERY:
               return (T) createQuery(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTINTO:
               return (T) createInto(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSELECT:
               return (T) createSelect(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTEXPRESSIONSYMBOL:
               return (T) createExpressionSymbol(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTDERIVEDCOLUMN:
               return (T) createDerivedColumn(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTMULTIPLEELEMENTSYMBOL:
               return (T) createMultipleElementSymbol(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTFROM:
               return (T) createFrom(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTJOINPREDICATE:
               return (T) createJoinPredicate(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTJOINTYPE:
               return (T) createJoinType(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLSERIALIZE:
               return (T) createXMLSerialize(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTARRAYTABLE:
               return (T) createArrayTable(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTTEXTTABLE:
               return (T) createTextTable(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTTEXTCOLUMN:
               return (T) createTextColumn(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLEXISTS:
               return (T) createXMLExists(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLQUERY:
               return (T) createXMLQuery(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTOBJECTTABLE:
               return (T) createObjectTable(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTOBJECTCOLUMN:
               return (T) createObjectColumn(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLTABLE:
               return (T) createXMLTable(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLCOLUMN:
               return (T) createXMLColumn(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSUBQUERYFROMCLAUSE:
               return (T) createSubqueryFromClause(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTUNARYFROMCLAUSE:
               return (T) createUnaryFromClause(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTCRITERIA:
               return (T) createCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTCOMPOUNDCRITERIA:
               return (T) createCompoundCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTNOTCRITERIA:
               return (T) createNotCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTCOMPARECRITERIA:
               return (T) createCompareCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSUBQUERYCOMPARECRITERIA:
               return (T) createSubqueryCompareCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTMATCHCRITERIA:
               return (T) createMatchCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTBETWEENCRITERIA:
               return (T) createBetweenCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTISNULLCRITERIA:
               return (T) createIsNullCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSUBQUERYSETCRITERIA:
               return (T) createSubquerySetCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSETCRITERIA:
               return (T) createSetCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTEXISTSCRITERIA:
               return (T) createExistsCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTGROUPBY:
               return (T) createGroupBy(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTORDERBY:
               return (T) createOrderBy(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTORDERBYITEM:
               return (T) createOrderByItem(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTLIMIT:
               return (T) createLimit(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTOPTION:
               return (T) createOption(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTREFERENCE:
               return (T) createReference(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTCASEEXPRESSION:
               return (T) createCaseExpression(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSEARCHEDCASEEXPRESSION:
               return (T) createSearchedCaseExpression(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTFUNCTION:
               return (T) createFunction(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLPARSE:
               return (T) createXMLParse(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTQUERYSTRING:
               return (T) createQueryString(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLELEMENT:
               return (T) createXMLElement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLATTRIBUTES:
               return (T) createXMLAttributes(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTJSONOBJECT:
               return (T) createJSONObject(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLFOREST:
               return (T) createXMLForest(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLNAMESPACES:
               return (T) createXMLNamespaces(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTASSIGNMENTSTATEMENT:
               return (T) createAssignmentStatement(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTSCALARSUBQUERY:
               return (T) createScalarSubquery(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTGROUPSYMBOL:
               return (T) createGroupSymbol(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTCONSTANT:
               return (T) createConstant(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTELEMENTSYMBOL:
               return (T) createElementSymbol(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTBLOCK:
               return (T) createBlock(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTEXPRESSIONCRITERIA:
               return (T) createExpressionCriteria(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTALIASSYMBOL:
               return (T) createAliasSymbol(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTAGGREGATESYMBOL:
               return (T) createAggregateSymbol(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTWINDOWFUNCTION:
               return (T) createWindowFunction(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTWINDOWSPECIFICATION:
               return (T) createWindowSpecification(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTTEXTLINE:
               return (T) createTextLine(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTALTERTRIGGER:
               return (T) createAlterTrigger(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTALTERPROCEDURE:
               return (T) createAlterProcedure(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTALTERVIEW:
               return (T) createAlterView(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTARRAY:
               return (T) createArray(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTXMLCAST:
               return (T) createXMLCast(teiidVersion, nodeType);
           case Teiid9ParserTreeConstants.JJTISDISTINCTCRITERIA:
               return (T) createIsDistinctCriteria(teiidVersion, nodeType);
           default:
               throw new IllegalArgumentException(Messages.getString(Messages.TeiidParser.invalidNodeType, nodeType, teiidVersion));
       }
   }
}