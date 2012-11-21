/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid8.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.teiid.core.types.JDBCSQLTypeInfo;
import org.teiid.designer.sql.IQueryService;
import org.teiid.designer.udf.FunctionMethodDescriptor;
import org.teiid.designer.udf.FunctionParameterDescriptor;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.language.SQLConstants;
import org.teiid.metadata.FunctionMethod;
import org.teiid.metadata.FunctionMethod.Determinism;
import org.teiid.metadata.FunctionParameter;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionTree;
import org.teiid.query.function.UDFSource;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid8.sql.impl.FunctionLibraryImpl;

/**
 *
 */
public class QueryService implements IQueryService {

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

            FunctionDescriptor fd = tree.addFunction(descriptor.getSchema(), null, fMethod, false);
            fd.setMetadataID(descriptor.getMetadataID());
        }

        return new FunctionLibraryImpl(functionTrees.values());
    }

}
