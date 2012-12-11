/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl;

import java.util.List;
import org.teiid.designer.udf.IFunctionForm;
import org.teiid.query.function.FunctionForm;

/**
 *
 */
public class FunctionFormImpl implements IFunctionForm {

    private final FunctionForm functionForm;

    /**
     * @param functionForm
     */
    public FunctionFormImpl(FunctionForm functionForm) {
        this.functionForm = functionForm;
    }

    @Override
    public String getName() {
        return functionForm.getName();
    }

    @Override
    public String getDisplayString() {
        return functionForm.getDisplayString();
    }

    @Override
    public String getDescription() {
        return functionForm.getDescription();
    }

    @Override
    public String getCategory() {
        return functionForm.getCategory();
    }

    @Override
    public List<String> getArgNames() {
        return functionForm.getArgNames();
    }
}
