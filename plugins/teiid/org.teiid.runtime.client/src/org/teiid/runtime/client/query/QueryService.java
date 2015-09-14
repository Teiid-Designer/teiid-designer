/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.teiid.core.types.JDBCSQLTypeInfo;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryParser;
import org.teiid.designer.query.IQueryResolver;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.ICommandCollectorVisitor;
import org.teiid.designer.query.sql.IElementCollectorVisitor;
import org.teiid.designer.query.sql.IFunctionCollectorVisitor;
import org.teiid.designer.query.sql.IGroupCollectorVisitor;
import org.teiid.designer.query.sql.IGroupsUsedByElementsVisitor;
import org.teiid.designer.query.sql.IPredicateCollectorVisitor;
import org.teiid.designer.query.sql.IReferenceCollectorVisitor;
import org.teiid.designer.query.sql.IResolverVisitor;
import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.ISQLStringVisitorCallback;
import org.teiid.designer.query.sql.IValueIteratorProviderCollectorVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.ISymbol;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.udf.FunctionMethodDescriptor;
import org.teiid.designer.udf.FunctionParameterDescriptor;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.validator.IUpdateValidator;
import org.teiid.designer.validator.IUpdateValidator.TransformUpdateType;
import org.teiid.designer.validator.IValidator;
import org.teiid.designer.xml.IMappingDocumentFactory;
import org.teiid.language.SQLConstants;
import org.teiid.metadata.FunctionMethod;
import org.teiid.metadata.FunctionMethod.Determinism;
import org.teiid.metadata.FunctionParameter;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.function.FunctionTree;
import org.teiid.query.function.SystemFunctionManager;
import org.teiid.query.function.UDFSource;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.resolver.util.ResolverVisitor;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.visitor.CallbackSQLStringVisitor;
import org.teiid.query.sql.visitor.CommandCollectorVisitor;
import org.teiid.query.sql.visitor.ElementCollectorVisitor;
import org.teiid.query.sql.visitor.FunctionCollectorVisitor;
import org.teiid.query.sql.visitor.GroupCollectorVisitor;
import org.teiid.query.sql.visitor.GroupsUsedByElementsVisitor;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import org.teiid.query.sql.visitor.ValueIteratorProviderCollectorVisitor;
import org.teiid.query.validator.PredicateCollectorVisitor;
import org.teiid.query.validator.ReferenceCollectorVisitor;
import org.teiid.query.validator.UpdateValidator;
import org.teiid.query.validator.UpdateValidator.UpdateType;
import org.teiid.query.validator.Validator;
import org.teiid.runtime.client.proc.ProcedureService;
import org.teiid.runtime.client.xml.MappingDocumentFactory;

/**
 *
 */
public class QueryService implements IQueryService {

    private final ITeiidServerVersion teiidVersion;

    private QueryParser queryParser;

    private final SystemFunctionManager systemFunctionManager;

    private SyntaxFactory factory;

    /**
     * @param teiidVersion
     */
    public QueryService(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
        systemFunctionManager = new SystemFunctionManager(teiidVersion, getClass().getClassLoader());
    }

    /**
     * @return a query parser applicable to the given teiid instance version
     */
    @Override
    public IQueryParser getQueryParser() {
        if (queryParser == null) {
            queryParser = new QueryParser(teiidVersion);
        }

        return queryParser;
    }

    @Override
    public boolean isReservedWord(String word) {
        return SQLConstants.isReservedWord(teiidVersion, word);
    }

    @Override
    public boolean isProcedureReservedWord(String word) {
        return ProcedureReservedWords.isProcedureReservedWord(teiidVersion, word);
    }

    @Override
    public Set<String> getReservedWords() {
        return SQLConstants.getReservedWords(teiidVersion);
    }

    @Override
    public Set<String> getNonReservedWords() {
        return SQLConstants.getNonReservedWords(teiidVersion);
    }

    @Override
    public String getJDBCSQLTypeName(int jdbcType) {
        return JDBCSQLTypeInfo.getTypeName(teiidVersion, jdbcType);
    }

    @Override
    public IFunctionLibrary createFunctionLibrary() {
        return new FunctionLibrary(teiidVersion, systemFunctionManager.getSystemFunctions(), new FunctionTree[0]);
    }

    @Override
    public IFunctionLibrary createFunctionLibrary(List<FunctionMethodDescriptor> functionMethodDescriptors) {

        // Dynamically return a function library for each call rather than cache it here.
        Map<String, FunctionTree> functionTrees = new HashMap<String, FunctionTree>();

        for (FunctionMethodDescriptor descriptor : functionMethodDescriptors) {

            List<FunctionParameter> inputParameters = new ArrayList<FunctionParameter>();
            for (FunctionParameterDescriptor paramDescriptor : descriptor.getInputParameters()) {
                inputParameters.add(new FunctionParameter(teiidVersion, paramDescriptor.getName(), paramDescriptor.getType()));
            }

            FunctionParameter outputParameter = new FunctionParameter(teiidVersion, descriptor.getOutputParameter().getName(),
                                                                      descriptor.getOutputParameter().getType());

            FunctionMethod fMethod = new FunctionMethod(descriptor.getName(), descriptor.getDescription(),
                                                        descriptor.getCategory(), descriptor.getInvocationClass(),
                                                        descriptor.getInvocationMethod(),
                                                        inputParameters.toArray(new FunctionParameter[0]), outputParameter);

            fMethod.setPushDown(descriptor.getPushDownLiteral());
            fMethod.setVarArgs(descriptor.isVariableArgs());
            if (descriptor.isDeterministic()) {
                fMethod.setDeterminism(Determinism.DETERMINISTIC);
            } else {
                fMethod.setDeterminism(Determinism.NONDETERMINISTIC);
            }

            FunctionTree tree = functionTrees.get(descriptor.getSchema());
            if (tree == null) {
                tree = new FunctionTree(teiidVersion, descriptor.getSchema(), new UDFSource(Collections.EMPTY_LIST, getClass().getClassLoader()), false);
                functionTrees.put(descriptor.getSchema(), tree);
            }

            FunctionDescriptor fd = tree.addFunction(descriptor.getSchema(), null, fMethod, false);
            fd.setMetadataID(descriptor.getMetadataID());
        }

        return new FunctionLibrary(teiidVersion, systemFunctionManager.getSystemFunctions(),
                                   functionTrees.values().toArray(new FunctionTree[0]));
    }

    @Override
    public IQueryFactory createQueryFactory() {
        if (factory == null)
            factory = new SyntaxFactory(((QueryParser)getQueryParser()).getTeiidParser());

        return factory;
    }

    @Override
    public IMappingDocumentFactory getMappingDocumentFactory() {
        getQueryParser();
        return new MappingDocumentFactory(queryParser.getTeiidParser());
    }

    @Override
    public String getSymbolName(IExpression expression) {
        if (expression instanceof ISymbol) {
            return ((ISymbol)expression).getName();
        }

        return "expr"; //$NON-NLS-1$
    }

    @Override
    public String getSymbolShortName(String name) {
        int index = name.lastIndexOf(ISymbol.SEPARATOR);
        if (index >= 0) {
            return name.substring(index + 1);
        }
        return name;
    }

    @Override
    public String getSymbolShortName(IExpression expression) {
        if (expression instanceof ISymbol) {
            return ((ISymbol)expression).getShortName();
        }
        return "expr"; //$NON-NLS-1$
    }

    @Override
    public SQLStringVisitor getSQLStringVisitor() {
        return new SQLStringVisitor(teiidVersion);
    }

    @Override
    public ISQLStringVisitor getCallbackSQLStringVisitor(ISQLStringVisitorCallback visitorCallback) {
        return new CallbackSQLStringVisitor(teiidVersion, visitorCallback);
    }

    @Override
    public IGroupCollectorVisitor getGroupCollectorVisitor(boolean removeDuplicates) {
        return new GroupCollectorVisitor(teiidVersion, removeDuplicates);
    }

    @Override
    public IGroupsUsedByElementsVisitor getGroupsUsedByElementsVisitor() {
        return new GroupsUsedByElementsVisitor();
    }

    @Override
    public IElementCollectorVisitor getElementCollectorVisitor(boolean removeDuplicates) {
        return new ElementCollectorVisitor(teiidVersion, removeDuplicates);
    }

    @Override
    public ICommandCollectorVisitor getCommandCollectorVisitor() {
        return new CommandCollectorVisitor(teiidVersion);
    }

    @Override
    public IFunctionCollectorVisitor getFunctionCollectorVisitor(boolean removeDuplicates) {
        return new FunctionCollectorVisitor(teiidVersion, removeDuplicates);
    }

    @Override
    public IPredicateCollectorVisitor getPredicateCollectorVisitor() {
        return new PredicateCollectorVisitor(teiidVersion);
    }

    @Override
    public IReferenceCollectorVisitor getReferenceCollectorVisitor() {
        return new ReferenceCollectorVisitor(teiidVersion);
    }

    @Override
    public IValueIteratorProviderCollectorVisitor getValueIteratorProviderCollectorVisitor() {
        return new ValueIteratorProviderCollectorVisitor(teiidVersion);
    }

    @Override
    public IResolverVisitor getResolverVisitor() {
        return new ResolverVisitor(teiidVersion);
    }

    @Override
    public IValidator getValidator() {
        return new Validator();
    }

    @Override
    public IUpdateValidator getUpdateValidator(IQueryMetadataInterface metadata, TransformUpdateType tInsertType, TransformUpdateType tUpdateType, TransformUpdateType tDeleteType) {

        UpdateType insertType = UpdateType.valueOf(tInsertType.name());
        UpdateType updateType = UpdateType.valueOf(tUpdateType.name());
        UpdateType deleteType = UpdateType.valueOf(tDeleteType.name());

        return new UpdateValidator(metadata, insertType, updateType, deleteType);
    }

    @Override
    public void resolveGroup(IGroupSymbol groupSymbol, IQueryMetadataInterface metadata) throws Exception {
        ResolverUtil.resolveGroup((GroupSymbol)groupSymbol, metadata);
    }

    @Override
    public void fullyQualifyElements(ICommand command) {
        ResolverUtil.fullyQualifyElements((Command)command);
    }

    @Override
    public IQueryResolver getQueryResolver() {
        getQueryParser();
        return new QueryResolver((QueryParser)getQueryParser());
    }

    @Override
    public ProcedureService getProcedureService() {
        return new ProcedureService(teiidVersion);
    }
}
