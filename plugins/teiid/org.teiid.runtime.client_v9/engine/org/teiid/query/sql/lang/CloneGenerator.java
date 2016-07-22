/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.teiid.core.util.StringUtil;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.ExceptionExpression;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.RaiseErrorStatement;
import org.teiid.query.sql.proc.RaiseStatement;
import org.teiid.query.sql.proc.ReturnStatement;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.proc.WhileStatement;
import org.teiid.query.sql.symbol.AliasSymbol;
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
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.symbol.TextLine;
import org.teiid.query.sql.symbol.WindowSpecification;
import org.teiid.query.sql.symbol.XMLAttributes;
import org.teiid.query.sql.symbol.XMLElement;
import org.teiid.query.sql.symbol.XMLForest;
import org.teiid.query.sql.symbol.XMLNamespaces;
import org.teiid.query.sql.symbol.XMLParse;
import org.teiid.query.sql.symbol.XMLQuery;
import org.teiid.query.sql.symbol.XMLSerialize;
import org.teiid.query.sql.symbol.v9.Aggregate9Symbol;
import org.teiid.query.sql.symbol.v9.Window8Function;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class CloneGenerator {

    private static final String SEMI_COLON = ";";

    private static final String CLONE = "clone";

    private static final String RETURN = "return";

    private static final String CONSTRUCTOR_PARAMS = "(this.parser, this.id)";

    private static final String NEW = "new";

    private static final String EQUALS = "=";

    private static final String OVERRIDE = "@Override";

    private static final String TAB = "    ";

    private static final String OPEN_BRACE = "{";

    private static final String END_BRACE = "}";

    private static final String NEWLINE = "\n";

    private static final String PUBLIC = "public";

    private static final String SPACE = " ";

    private static final String CLONE_METHOD = "clone()";

    private static final String GET = "get";

    private static final String IS = "is";

    private static final String DOT = StringUtil.Constants.DOT;

    private static final String OPEN_BRACKET = "(";

    private static final String CLOSE_BRACKET = ")";

    private static final String IF = "if";

    private static final String NOT_NULL = "!= null";

    private static final String DIRECTORY = "/home/phantomjinx/programming/java/tdesigner/git/plugins/teiid/org.teiid.runtime.client/engine/";

    private static final String JAVA = ".java";
    
    private static final List<Class<?>> CLASS_LIST = new ArrayList<Class<?>>();

    private static final List<Class<?>> NON_CLONE_CLASSES = new ArrayList<Class<?>>();
    
    private final Map<Class<?>, File> classCache = new HashMap<Class<?>, File>();

    static {
        NON_CLONE_CLASSES.add(Boolean.class);
        NON_CLONE_CLASSES.add(String.class);
        NON_CLONE_CLASSES.add(Integer.class);
        NON_CLONE_CLASSES.add(Double.class);
        NON_CLONE_CLASSES.add(Float.class);
        NON_CLONE_CLASSES.add(SourceHint.class);

        CLASS_LIST.add(Aggregate9Symbol.class);
        CLASS_LIST.add(AliasSymbol.class);
        CLASS_LIST.add(Alter.class);
        CLASS_LIST.add(ArrayTable.class);
        CLASS_LIST.add(AssignmentStatement.class);
        CLASS_LIST.add(BetweenCriteria.class);
        CLASS_LIST.add(Block.class);
        CLASS_LIST.add(BranchingStatement.class);
        CLASS_LIST.add(CaseExpression.class);
        CLASS_LIST.add(Command.class);
        CLASS_LIST.add(CommandStatement.class);
        CLASS_LIST.add(CompareCriteria.class);
        CLASS_LIST.add(CompoundCriteria.class);
        CLASS_LIST.add(Constant.class);
        CLASS_LIST.add(CreateProcedureCommand.class);
        CLASS_LIST.add(CreateUpdateProcedureCommand.class);
        CLASS_LIST.add(Criteria.class);
        CLASS_LIST.add(CriteriaSelector.class);
        CLASS_LIST.add(DeclareStatement.class);
        CLASS_LIST.add(Delete.class);
        CLASS_LIST.add(DerivedColumn.class);
        CLASS_LIST.add(Drop.class);
        CLASS_LIST.add(DynamicCommand.class);
        CLASS_LIST.add(ElementSymbol.class);
        CLASS_LIST.add(ExceptionExpression.class);
        CLASS_LIST.add(ExistsCriteria.class);
        CLASS_LIST.add(ExpressionCriteria.class);
        CLASS_LIST.add(ExpressionSymbol.class);
        CLASS_LIST.add(FromClause.class);
        CLASS_LIST.add(From.class);
        CLASS_LIST.add(Function.class);
        CLASS_LIST.add(GroupBy.class);
        CLASS_LIST.add(GroupSymbol.class);
        CLASS_LIST.add(HasCriteria.class);
        CLASS_LIST.add(IfStatement.class);
        CLASS_LIST.add(Insert.class);
        CLASS_LIST.add(Into.class);
        CLASS_LIST.add(IsNullCriteria.class);
        CLASS_LIST.add(JoinPredicate.class);
        CLASS_LIST.add(JoinType.class);
        CLASS_LIST.add(JSONObject.class);
        CLASS_LIST.add(Limit.class);
        CLASS_LIST.add(LoopStatement.class);
        CLASS_LIST.add(MatchCriteria.class);
        CLASS_LIST.add(MultipleElementSymbol.class);
        CLASS_LIST.add(NamespaceItem.class);
        CLASS_LIST.add(Node.class);
        CLASS_LIST.add(NotCriteria.class);
        CLASS_LIST.add(ObjectColumn.class);
        CLASS_LIST.add(ObjectTable.class);
        CLASS_LIST.add(Option.class);
        CLASS_LIST.add(OrderByItem.class);
        CLASS_LIST.add(OrderBy.class);
        CLASS_LIST.add(ProjectedColumn.class);
        CLASS_LIST.add(QueryCommand.class);
        CLASS_LIST.add(Query.class);
        CLASS_LIST.add(QueryString.class);
        CLASS_LIST.add(RaiseErrorStatement.class);
        CLASS_LIST.add(RaiseStatement.class);
        CLASS_LIST.add(Reference.class);
        CLASS_LIST.add(ReturnStatement.class);
        CLASS_LIST.add(ScalarSubquery.class);
        CLASS_LIST.add(SearchedCaseExpression.class);
        CLASS_LIST.add(Select.class);
        CLASS_LIST.add(SetClause.class);
        CLASS_LIST.add(SetClauseList.class);
        CLASS_LIST.add(SetCriteria.class);
        CLASS_LIST.add(SetQuery.class);
        CLASS_LIST.add(SimpleNode.class);
        CLASS_LIST.add(Statement.class);
        CLASS_LIST.add(StoredProcedure.class);
        CLASS_LIST.add(SubqueryCompareCriteria.class);
        CLASS_LIST.add(SubqueryFromClause.class);
        CLASS_LIST.add(SubquerySetCriteria.class);
        CLASS_LIST.add(Symbol.class);
        CLASS_LIST.add(TextColumn.class);
        CLASS_LIST.add(TextLine.class);
        CLASS_LIST.add(TextTable.class);
        CLASS_LIST.add(TranslateCriteria.class);
        CLASS_LIST.add(TriggerAction.class);
        CLASS_LIST.add(UnaryFromClause.class);
        CLASS_LIST.add(Update.class);
        CLASS_LIST.add(Window8Function.class);
        CLASS_LIST.add(WhileStatement.class);
        CLASS_LIST.add(WindowSpecification.class);
        CLASS_LIST.add(WithQueryCommand.class);
        CLASS_LIST.add(XMLAttributes.class);
        CLASS_LIST.add(XMLColumn.class);
        CLASS_LIST.add(XMLElement.class);
        CLASS_LIST.add(XMLForest.class);
        CLASS_LIST.add(XMLNamespaces.class);
        CLASS_LIST.add(XMLParse.class);
        CLASS_LIST.add(XMLQuery.class);
        CLASS_LIST.add(XMLSerialize.class);
        CLASS_LIST.add(XMLTable.class);
        CLASS_LIST.add(Create.class);
    }

    private void cacheASTClasses() {

        for (Class<?> klazz : CLASS_LIST) {
            String packagePath = klazz.getPackage().getName();
            packagePath = packagePath.replaceAll("\\" + DOT, File.separator);

            StringBuffer fileName = new StringBuffer();
            fileName.append(packagePath);
            fileName.append(File.separator);
            
            if (klazz.getSimpleName().contains("7")) {
                fileName.append("v7");
                fileName.append(File.separator);
            } else if (klazz.getSimpleName().contains("8")) {
                fileName.append("v8");
                fileName.append(File.separator);
            }

            fileName.append(klazz.getSimpleName());
            fileName.append(JAVA);

            File jFile = new File(DIRECTORY, fileName.toString());
            if (! jFile.exists())
                throw new RuntimeException("The class file " + jFile.getAbsolutePath() + " does not exist");

            classCache.put(klazz, jFile);
        }
    }

    /**
     * @param key
     * @return
     */
    private StringBuffer createCloneMethod(Class<?> klazz) {
        StringBuffer  buffer = new StringBuffer(NEWLINE);
        String className = klazz.getSimpleName();

         buffer.append(TAB);
         buffer.append(OVERRIDE);
         buffer.append(NEWLINE);

         buffer.append(TAB);
         buffer.append(PUBLIC);
         buffer.append(SPACE);        
         buffer.append(className);
         buffer.append(SPACE);
         buffer.append(CLONE_METHOD);
         buffer.append(SPACE);
         buffer.append(OPEN_BRACE);
         buffer.append(NEWLINE);

         buffer.append(TAB);
         buffer.append(TAB);
         buffer.append(className);
         buffer.append(SPACE);
         buffer.append(CLONE);
         buffer.append(SPACE);
         buffer.append(EQUALS);
         buffer.append(SPACE);
         buffer.append(NEW);
         buffer.append(SPACE);
         buffer.append(className);
         buffer.append(CONSTRUCTOR_PARAMS);
         buffer.append(SEMI_COLON);
         buffer.append(NEWLINE);
         buffer.append(NEWLINE);
        
        for (Method method : klazz.getMethods()) {
            String methodName = method.getName();
            if (! methodName.startsWith("set"))
                continue;

            Class<?>[] params = method.getParameterTypes();

            String fieldName = methodName.substring(3);
            String getter = GET + fieldName;
            if (isBoolean(params[0]))
                getter = IS + fieldName;

            // Should only be 1 parameter
            boolean hasNonCloneParam = false;
            for (Class<?> nonCloneClass :  NON_CLONE_CLASSES) {
                if (nonCloneClass.getSimpleName().equalsIgnoreCase(params[0].getSimpleName())) {
                    hasNonCloneParam = true;
                    break;
                }
            }

            // check the param is not an enum
            if (! hasNonCloneParam && params[0].isEnum()) {
                hasNonCloneParam = true;
            }

            if (! isBoolean(params[0])) {
                buffer.append(TAB);
                buffer.append(TAB);
                buffer.append(IF);
                buffer.append(OPEN_BRACKET);
                buffer.append(getter);
                buffer.append(OPEN_BRACKET);
                buffer.append(CLOSE_BRACKET);
                buffer.append(SPACE);
                buffer.append(NOT_NULL);
                buffer.append(CLOSE_BRACKET);
                buffer.append(NEWLINE);
                buffer.append(TAB);
            }

            buffer.append(TAB);
            buffer.append(TAB);
            buffer.append(CLONE);
            buffer.append(DOT);
            buffer.append(methodName);
            buffer.append(OPEN_BRACKET);
            buffer.append(getter);
            buffer.append(OPEN_BRACKET);
            buffer.append(CLOSE_BRACKET);
            
            if(! hasNonCloneParam) {
                buffer.append(DOT);
                buffer.append(CLONE_METHOD);
            }

            buffer.append(CLOSE_BRACKET);
            buffer.append(SEMI_COLON);
            buffer.append(NEWLINE);
        }

        // Return statement
        buffer.append(NEWLINE);
        buffer.append(TAB);
        buffer.append(TAB);
        buffer.append(RETURN);
        buffer.append(SPACE);
        buffer.append(CLONE);
        buffer.append(SEMI_COLON);
        buffer.append(NEWLINE);
        
        buffer.append(TAB);
        buffer.append(END_BRACE);
        
        buffer.append(NEWLINE);
        buffer.append(NEWLINE);

        return  buffer;
    }

    private boolean isBoolean(Class<?> klazz) {
        return klazz.getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName());
    }

    private void writeMethod(StringBuffer method, File jFile) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(jFile));
        BufferedWriter writer = null;
        StringBuffer content = new StringBuffer();

        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }

            int offset = content.lastIndexOf(END_BRACE);
            if (offset == -1)
                throw new RuntimeException("File " + jFile.getName() + " does not contain an accept comment!");

            content.insert(offset, method.toString());

            writer = new BufferedWriter(new FileWriter(jFile));
            writer.write(content.toString());

        } finally {
            reader.close();
            if (writer != null)
                writer.close();
        }
    }

    public void generate() throws Exception {
        cacheASTClasses();

        for (Entry<Class<?>, File> entry : classCache.entrySet()) {
            StringBuffer method = createCloneMethod(entry.getKey());
            writeMethod(method, entry.getValue());
        }
    }

    public static void main(String[] args) throws Exception {
        CloneGenerator cg = new CloneGenerator();
        cg.generate();
    }
}
