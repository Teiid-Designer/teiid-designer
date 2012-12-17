/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql;

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
import org.teiid.designer.udf.FunctionMethodDescriptor;
import org.teiid.designer.udf.FunctionParameterDescriptor;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.designer.validator.IUpdateValidator;
import org.teiid.designer.validator.IUpdateValidator.TransformUpdateType;
import org.teiid.designer.validator.IValidator;
import org.teiid.designer.xml.IMappingDocumentFactory;
import org.teiid.language.SQLConstants;
import org.teiid.metadata.FunctionMethod.Determinism;
import org.teiid.metadata.FunctionParameter;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionTree;
import org.teiid.query.function.UDFSource;
import org.teiid.query.function.metadata.FunctionMethod;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.validator.UpdateValidator.UpdateType;
import org.teiid772.sql.impl.CrossQueryMetadata;
import org.teiid772.sql.impl.FunctionLibraryImpl;
import org.teiid772.sql.impl.QueryParserImpl;
import org.teiid772.sql.impl.SyntaxFactory;
import org.teiid772.sql.impl.validator.QueryResolverImpl;
import org.teiid772.sql.impl.validator.UpdateValidatorImpl;
import org.teiid772.sql.impl.validator.ValidatorImpl;
import org.teiid772.sql.impl.visitor.CallbackSQLStringVisitorImpl;
import org.teiid772.sql.impl.visitor.CommandCollectorVisitorImpl;
import org.teiid772.sql.impl.visitor.ElementCollectorVisitorImpl;
import org.teiid772.sql.impl.visitor.FunctionCollectorVisitorImpl;
import org.teiid772.sql.impl.visitor.GroupCollectorVisitorImpl;
import org.teiid772.sql.impl.visitor.GroupsUsedByElementsVisitorImpl;
import org.teiid772.sql.impl.visitor.PredicateCollectorVisitorImpl;
import org.teiid772.sql.impl.visitor.ReferenceCollectorVisitorImpl;
import org.teiid772.sql.impl.visitor.ResolverVisitorImpl;
import org.teiid772.sql.impl.visitor.SQLStringVisitorImpl;
import org.teiid772.sql.impl.visitor.ValueIteratorProviderCollectorVisitorImpl;
import org.teiid772.sql.impl.xml.MappingDocumentFactory;

/**
 *
 */
public class QueryService implements IQueryService {

    private IQueryParser queryParser;
    
    private final SyntaxFactory factory = new SyntaxFactory();

    @Override
    public IQueryParser getQueryParser() {
        if (queryParser == null) {
            queryParser = new QueryParserImpl();
        }
        
        return queryParser;
    }
    
    @Override
    public boolean isReservedWord(String word) {
        return SQLConstants.isReservedWord(word);
    }

    @Override
    public boolean isProcedureReservedWord(String word) {
        return ProcedureReservedWords.isProcedureReservedWord(word);
    }

    @Override
    public Set<String> getReservedWords() {
        return SQLConstants.getReservedWords();
    }

    @Override
    public Set<String> getNonReservedWords() {
        return SQLConstants.getNonReservedWords();
    }

    @Override
    public String getJDBCSQLTypeName(int jdbcType) {
        return JDBCSQLTypeInfo.getTypeName(jdbcType);
    }

    @Override
    public IFunctionLibrary createFunctionLibrary() {
        return new FunctionLibraryImpl();
    }

    @Override
    public IFunctionLibrary createFunctionLibrary(List<FunctionMethodDescriptor> functionMethodDescriptors) {

        // Dynamically return a function library for each call rather than cache it here.
        Map<String, FunctionTree> functionTrees = new HashMap<String, FunctionTree>();

        for (FunctionMethodDescriptor descriptor : functionMethodDescriptors) {

            List<FunctionParameter> inputParameters = new ArrayList<FunctionParameter>();
            for (FunctionParameterDescriptor paramDescriptor : descriptor.getInputParameters()) {
                inputParameters.add(new FunctionParameter(paramDescriptor.getName(), paramDescriptor.getType()));
            }

            FunctionParameter outputParameter = new FunctionParameter(descriptor.getOutputParameter().getName(),
                                                                      descriptor.getOutputParameter().getType());

            FunctionMethod fMethod = new FunctionMethod(descriptor.getName(), descriptor.getDescription(),
                                                        descriptor.getCategory(), descriptor.getInvocationClass(),
                                                        descriptor.getInvocationMethod(),
                                                        inputParameters.toArray(new FunctionParameter[0]), outputParameter);

            fMethod.setPushDown(descriptor.getPushDownLiteral());
            if (descriptor.isDeterministic()) {
                fMethod.setDeterminism(Determinism.DETERMINISTIC);
            } else {
                fMethod.setDeterminism(Determinism.NONDETERMINISTIC);
            }

            FunctionTree tree = functionTrees.get(descriptor.getSchema());
            if (tree == null) {
                tree = new FunctionTree(descriptor.getSchema(), new UDFSource(Collections.EMPTY_LIST), false);
                functionTrees.put(descriptor.getSchema(), tree);
            }

            FunctionDescriptor fd = tree.addFunction(descriptor.getSchema(), null, fMethod);
            fd.setMetadataID(descriptor.getMetadataID());
        }

        return new FunctionLibraryImpl(functionTrees.values());
    }

    @Override
    public IQueryFactory createQueryFactory() {
        return new SyntaxFactory();
    }
    
    @Override
    public IMappingDocumentFactory getMappingDocumentFactory() {
        return new MappingDocumentFactory();
    }

    @Override
    public String getSymbolName(IExpression expression) {
        if (expression instanceof ISymbol) {
            return ((ISymbol) expression).getName();
        }
        
        return "expr"; //$NON-NLS-1$
    }

    @Override
    public String getSymbolShortName(String name) {
        int index = name.lastIndexOf(ISymbol.SEPARATOR);
        if(index >= 0) { 
            return name.substring(index+1);
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
    public ISQLStringVisitor getSQLStringVisitor() {
        return new SQLStringVisitorImpl();
    }

    @Override
    public ISQLStringVisitor getCallbackSQLStringVisitor(ISQLStringVisitorCallback visitorCallback) {
        return new CallbackSQLStringVisitorImpl(visitorCallback);
    }

    @Override
    public IGroupCollectorVisitor getGroupCollectorVisitor() {
        return new GroupCollectorVisitorImpl();
    }

    @Override
    public IGroupsUsedByElementsVisitor getGroupsUsedByElementsVisitor() {
        return new GroupsUsedByElementsVisitorImpl();
    }

    @Override
    public IElementCollectorVisitor getElementCollectorVisitor() {
        return new ElementCollectorVisitorImpl();
    }

    @Override
    public ICommandCollectorVisitor getCommandCollectorVisitor() {
        return new CommandCollectorVisitorImpl();
    }

    @Override
    public IFunctionCollectorVisitor getFunctionCollectorVisitor() {
        return new FunctionCollectorVisitorImpl();
    }

    @Override
    public IPredicateCollectorVisitor getPredicateCollectorVisitor() {
        return new PredicateCollectorVisitorImpl();
    }

    @Override
    public IReferenceCollectorVisitor getReferenceCollectorVisitor() {
        return new ReferenceCollectorVisitorImpl();
    }

    @Override
    public IValueIteratorProviderCollectorVisitor getValueIteratorProviderCollectorVisitor() {
        return new ValueIteratorProviderCollectorVisitorImpl();
    }

    @Override
    public IResolverVisitor getResolverVisitor() {
        return new ResolverVisitorImpl();
    }

    @Override
    public IValidator getValidator() {
        return new ValidatorImpl();
    }

    @Override
    public IUpdateValidator getUpdateValidator(IQueryMetadataInterface metadata,
                                               TransformUpdateType tInsertType,
                                               TransformUpdateType tUpdateType,
                                               TransformUpdateType tDeleteType) {
        
        CrossQueryMetadata crossMetadata = new CrossQueryMetadata(metadata);
        UpdateType insertType = UpdateType.valueOf(tInsertType.name());
        UpdateType updateType = UpdateType.valueOf(tUpdateType.name());
        UpdateType deleteType = UpdateType.valueOf(tDeleteType.name());
        
        return new UpdateValidatorImpl(crossMetadata, insertType, updateType, deleteType);
    }

    @Override
    public void resolveGroup(IGroupSymbol groupSymbol,
                             IQueryMetadataInterface metadata) throws Exception {
        GroupSymbol groupSymbolImpl = factory.convert(groupSymbol);
        CrossQueryMetadata crossMetadata = new CrossQueryMetadata(metadata);
        
        ResolverUtil.resolveGroup(groupSymbolImpl, crossMetadata);
    }

    @Override
    public void fullyQualifyElements(ICommand command) {
        Command dCommand = factory.convert(command);
        ResolverUtil.fullyQualifyElements(dCommand);
    }

    @Override
    public IQueryResolver getQueryResolver() {
        return new QueryResolverImpl();
    }

}
