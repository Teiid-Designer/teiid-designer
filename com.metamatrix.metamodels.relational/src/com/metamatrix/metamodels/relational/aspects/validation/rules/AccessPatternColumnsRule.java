/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
