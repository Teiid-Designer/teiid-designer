/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.relational.aspects.validation.rules;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ColumnSet;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * AccessPatternColumnsRule
 */
public class AccessPatternColumnsRule implements ObjectValidationRule {

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(final EObject eObject, final ValidationContext context) {
        ArgCheck.isInstanceOf(AccessPattern.class, eObject);

        final AccessPattern accessPattern = (AccessPattern) eObject;

        // get the columns for the accessPattern
        final EList apColumns = accessPattern.getColumns();        

        // get the table for the accessPattern
        final Table apTable = accessPattern.getTable();
        Assertion.isNotNull(apTable);

        final Iterator colIter = apColumns.iterator();
        while(colIter.hasNext()) {
            final Column column = (Column) colIter.next();
            final ColumnSet colTable = (ColumnSet) column.eContainer();
            Assertion.isNotNull(colTable);
            
            if(!apTable.equals(colTable)) {
                final ValidationResult result = new ValidationResultImpl(eObject);
                // create validation problem and add it to the result
                final String msg = RelationalPlugin.Util.getString("AccessPatternColumnsRule.AccessPattern_{0}_may_not_reference_column_{1}_from_a_different_table_1", accessPattern.getName(), column.getName()); //$NON-NLS-1$
                result.addProblem( new ValidationProblemImpl(0, IStatus.ERROR ,msg) );
                context.addResult(result);
                return;
            }
        }
    }

}
