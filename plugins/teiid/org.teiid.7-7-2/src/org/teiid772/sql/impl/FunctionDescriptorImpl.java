/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl;

import org.teiid.designer.udf.IFunctionDescriptor;
import org.teiid.query.function.FunctionDescriptor;

/**
 *
 */
public class FunctionDescriptorImpl implements IFunctionDescriptor {

    private final FunctionDescriptor functionDescriptor;

    /**
     * @param functionDescriptor 
     */
    public FunctionDescriptorImpl(FunctionDescriptor functionDescriptor) {
        this.functionDescriptor = functionDescriptor;
    }
    
    /**
     * @return underlying delegate
     */
    public FunctionDescriptor getDelegate() {
        return functionDescriptor;
    }

    @Override
    public String getName() {
        return functionDescriptor.getName();
    }

    @Override
    public Object getMetadataID() {
        return functionDescriptor.getMetadataID();
    }
}
