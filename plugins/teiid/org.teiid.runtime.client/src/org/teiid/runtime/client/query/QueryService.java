/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.query;

import java.util.Set;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryParser;
import org.teiid.designer.query.IQueryResolver;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.symbol.ISymbol;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.xml.IMappingDocumentFactory;
import org.teiid.language.SQLConstants;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import org.teiid.runtime.client.proc.ProcedureService;
import org.teiid.runtime.client.xml.MappingDocumentFactory;

/**
 *
 */
public class QueryService implements IQueryService {

    private final ITeiidServerVersion teiidVersion;

    private QueryParser queryParser;

    //    private final SystemFunctionManager systemFunctionManager = new SystemFunctionManager();

    private SyntaxFactory factory;

    /**
     * @param teiidVersion
     */
    public QueryService(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
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

    //
    //    @Override
    //    public String getJDBCSQLTypeName(int jdbcType) {
    //        return JDBCSQLTypeInfo.getTypeName(jdbcType);
    //    }
    //
    //    @Override
    //    public IFunctionLibrary createFunctionLibrary() {
    //        /*
    //         * System function manager needs this classloader since it uses reflection to instantiate classes, 
    //         * such as FunctionMethods. The default classloader is taken from the thread, which in turn takes
    //         * a random plugin. Since no plugin depends on this plugin, ClassNotFound exceptions result.
    //         * 
    //         * So set the class loader to the one belonging to this plugin.
    //         */
    //        systemFunctionManager.setClassloader(getClass().getClassLoader());
    //        return new FunctionLibrary(systemFunctionManager.getSystemFunctions(), new FunctionTree[0]);
    //    }
    //
    //    @Override
    //    public IFunctionLibrary createFunctionLibrary(List<FunctionMethodDescriptor> functionMethodDescriptors) {
    //
    //        // Dynamically return a function library for each call rather than cache it here.
    //        Map<String, FunctionTree> functionTrees = new HashMap<String, FunctionTree>();
    //
    //        for (FunctionMethodDescriptor descriptor : functionMethodDescriptors) {
    //
    //            List<FunctionParameter> inputParameters = new ArrayList<FunctionParameter>();
    //            for (FunctionParameterDescriptor paramDescriptor : descriptor.getInputParameters()) {
    //                inputParameters.add(new FunctionParameter(paramDescriptor.getName(), paramDescriptor.getType()));
    //            }
    //
    //            FunctionParameter outputParameter = new FunctionParameter(descriptor.getOutputParameter().getName(),
    //                                                                      descriptor.getOutputParameter().getType());
    //
    //            FunctionMethod fMethod = new FunctionMethod(descriptor.getName(), descriptor.getDescription(),
    //                                                        descriptor.getCategory(), descriptor.getInvocationClass(),
    //                                                        descriptor.getInvocationMethod(),
    //                                                        inputParameters.toArray(new FunctionParameter[0]), outputParameter);
    //
    //            fMethod.setPushDown(descriptor.getPushDownLiteral());
    //            fMethod.setVarArgs(descriptor.isVariableArgs());
    //            if (descriptor.isDeterministic()) {
    //                fMethod.setDeterminism(Determinism.DETERMINISTIC);
    //            } else {
    //                fMethod.setDeterminism(Determinism.NONDETERMINISTIC);
    //            }
    //
    //            FunctionTree tree = functionTrees.get(descriptor.getSchema());
    //            if (tree == null) {
    //                tree = new FunctionTree(descriptor.getSchema(), new UDFSource(Collections.EMPTY_LIST), false);
    //                functionTrees.put(descriptor.getSchema(), tree);
    //            }
    //
    //            FunctionDescriptor fd = tree.addFunction(descriptor.getSchema(), null, fMethod, false);
    //            fd.setMetadataID(descriptor.getMetadataID());
    //        }
    //
    //        /*
    //         * System function manager needs this classloader since it uses reflection to instantiate classes, 
    //         * such as FunctionMethods. The default classloader is taken from the thread, which in turn takes
    //         * a random plugin. Since no plugin depends on this plugin, ClassNotFound exceptions result.
    //         * 
    //         * So set the class loader to the one belonging to this plugin.
    //         */
    //        systemFunctionManager.setClassloader(getClass().getClassLoader());
    //        return new FunctionLibrary(systemFunctionManager.getSystemFunctions(), 
    //                                                      functionTrees.values().toArray(new FunctionTree[0]));
    //    }
    //
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

    //
    //    @Override
    //    public ISQLStringVisitor getCallbackSQLStringVisitor(ISQLStringVisitorCallback visitorCallback) {
    //        return new CallbackSQLStringVisitor(visitorCallback);
    //    }
    //
    //    @Override
    //    public IGroupCollectorVisitor getGroupCollectorVisitor(boolean removeDuplicates) {
    //        return new GroupCollectorVisitor(removeDuplicates);
    //    }
    //
    //    @Override
    //    public IGroupsUsedByElementsVisitor getGroupsUsedByElementsVisitor() {
    //        return new GroupsUsedByElementsVisitor();
    //    }
    //
    //    @Override
    //    public IElementCollectorVisitor getElementCollectorVisitor(boolean removeDuplicates) {
    //        return new ElementCollectorVisitor(removeDuplicates);
    //    }
    //
    //    @Override
    //    public ICommandCollectorVisitor getCommandCollectorVisitor() {
    //        return new CommandCollectorVisitor();
    //    }
    //
    //    @Override
    //    public IFunctionCollectorVisitor getFunctionCollectorVisitor(boolean removeDuplicates) {
    //        return new FunctionCollectorVisitor(removeDuplicates);
    //    }
    //
    //    @Override
    //    public IPredicateCollectorVisitor getPredicateCollectorVisitor() {
    //        return new PredicateCollectorVisitor();
    //    }
    //
    //    @Override
    //    public IReferenceCollectorVisitor getReferenceCollectorVisitor() {
    //        return new ReferenceCollectorVisitor();
    //    }
    //
    //    @Override
    //    public IValueIteratorProviderCollectorVisitor getValueIteratorProviderCollectorVisitor() {
    //        return new ValueIteratorProviderCollectorVisitor();
    //    }
    //
    //    @Override
    //    public IResolverVisitor getResolverVisitor() {
    //        return new WrappedResolverVisitor();
    //    }
    //
    //    @Override
    //    public IValidator getValidator() {
    //        return new WrappedValidator();
    //    }
    //
    //    @Override
    //    public IUpdateValidator getUpdateValidator(QueryMetadataInterface metadata,
    //                                               TransformUpdateType tInsertType,
    //                                               TransformUpdateType tUpdateType,
    //                                               TransformUpdateType tDeleteType) {
    //        
    //        CrossQueryMetadata crossMetadata = new CrossQueryMetadata(metadata);
    //        UpdateType insertType = UpdateType.valueOf(tInsertType.name());
    //        UpdateType updateType = UpdateType.valueOf(tUpdateType.name());
    //        UpdateType deleteType = UpdateType.valueOf(tDeleteType.name());
    //        
    //        return new WrappedUpdateValidator(crossMetadata, insertType, updateType, deleteType);
    //    }
    //
    //    @Override
    //    public void resolveGroup(IGroupSymbol groupSymbol,
    //                             QueryMetadataInterface metadata) throws Exception {
    //        CrossQueryMetadata crossMetadata = new CrossQueryMetadata(metadata);
    //        ResolverUtil.resolveGroup((GroupSymbol) groupSymbol, crossMetadata);
    //    }
    //
    //    @Override
    //    public void fullyQualifyElements(ICommand command) {
    //        ResolverUtil.fullyQualifyElements((Command) command);
    //    }
    //
    @Override
    public IQueryResolver getQueryResolver() {
        return new QueryResolver();
    }

    @Override
    public ProcedureService getProcedureService() {
        return new ProcedureService(teiidVersion);
    }
}
