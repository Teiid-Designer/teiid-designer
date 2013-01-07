/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.teiid.designer.udf.IFunctionDescriptor;
import org.teiid.designer.udf.IFunctionForm;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.query.function.FunctionDescriptor;
import org.teiid.query.function.FunctionForm;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.query.function.FunctionTree;
import org.teiid.query.function.SystemFunctionManager;

/**
 *
 */
public class FunctionLibraryImpl implements IFunctionLibrary {

    private final SystemFunctionManager systemFunctionManager = new SystemFunctionManager();
    
    private final FunctionLibrary functionLibrary;
    
    private SyntaxFactory factory = new SyntaxFactory();
    
    /**
     * Create new default instance
     */
    public FunctionLibraryImpl() {
        /*
         * System function manager needs this classloader since it uses reflection to instantiate classes, 
         * such as FunctionMethods. The default classloader is taken from the thread, which in turn takes
         * a random plugin. Since no plugin depends on this plugin, ClassNotFound exceptions result.
         * 
         * So set the class loader to the one belonging to this plugin.
         */
        systemFunctionManager.setClassloader(getClass().getClassLoader());
        functionLibrary = new FunctionLibrary(systemFunctionManager.getSystemFunctions(), new FunctionTree[0]);
    }

    /**
     * Create instance with custom functions
     * 
     * @param functionTrees
     */
    public FunctionLibraryImpl(Collection<FunctionTree> functionTrees) {
        /*
         * System function manager needs this classloader since it uses reflection to instantiate classes, 
         * such as FunctionMethods. The default classloader is taken from the thread, which in turn takes
         * a random plugin. Since no plugin depends on this plugin, ClassNotFound exceptions result.
         * 
         * So set the class loader to the one belonging to this plugin.
         */
        systemFunctionManager.setClassloader(getClass().getClassLoader());
        functionLibrary = new FunctionLibrary(systemFunctionManager.getSystemFunctions(), functionTrees.toArray(new FunctionTree[0]));
    }
    
    private String getStaticValue(FunctionName functionName) {
        for (Field field : FunctionLibrary.class.getDeclaredFields()) {
            if (field.getName().equals(functionName.name())) {
                field.setAccessible(true);
                try {
                    return (String) field.get(null);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        
        throw new RuntimeException();
    }
    
    public FunctionLibrary getDelegate() {
        return functionLibrary;
    }
    
    @Override
    public String getFunctionName(FunctionName functionName) {
        return getStaticValue(functionName);
    }
    
    @Override
    public List<String> getFunctionCategories() {
        return functionLibrary.getFunctionCategories();
    }
    
    @Override
    public List<IFunctionForm> getFunctionForms(String category) {
        List<IFunctionForm> functionForms = new ArrayList<IFunctionForm>();
        
        for (FunctionForm functionForm : functionLibrary.getFunctionForms(category)) {
            functionForms.add(factory.createFunctionForm(functionForm));
        }
        
        return functionForms;
    }

    @Override
    public IFunctionForm findFunctionForm(String name, int length) {
        FunctionForm functionForm = functionLibrary.findFunctionForm(name, length);
        return factory.createFunctionForm(functionForm);
    }

    @Override
    public IFunctionDescriptor findFunction(String name, Class[] types) {
        FunctionDescriptor functionDescriptor = functionLibrary.findFunction(name, types);
        return factory.createFunctionDescriptor(functionDescriptor);
    }
}
