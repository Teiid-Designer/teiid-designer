/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation.rules;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ColumnSet;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.ValidationPreferences;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * ColumnIntegerDatatypeRule
 */
public class IndexReferenceOneTableRule implements ObjectValidationRule {
    
    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(Index.class, eObject);

        // See what the preference is ...
        int severity = IStatus.WARNING;

        if (context.hasPreferences()) {        
            severity = context.getPreferenceStatus(ValidationPreferences.RELATIONAL_INDEXES_WITH_COLUMNS_FROM_MULTIPLE_TABLES, severity);

            if (severity == IStatus.OK) {
                return;
            }
        }

        // See whether more than one table are involved ...
        final Index index = (Index) eObject;
        final List columns = index.getColumns();
        final Set referencedTables = new HashSet();
        final Iterator iter = columns.iterator();
        while (iter.hasNext()) {
            final Column column = (Column)iter.next();
            final ColumnSet table = column.getOwner();
            if ( table instanceof Table ) {
                referencedTables.add(table);
            }
        }
        
        // See if there are any duplicates ...
        if ( referencedTables.size() > 1 ) {
            ValidationResult result = new ValidationResultImpl(eObject);
            // create validation problem and add it to the result
            ValidationProblem problem  = new ValidationProblemImpl(0, severity, getValidationMsg(0, new Object[] {eObject}));
            result.addProblem(problem);
            context.addResult(result);
        }
    }
    
    /**
     * Gets the appropriate validation error message for the specified error code. 
     * @param theCode the error code whose validation message is being requested
     * @param theParams the parameters to be used within the message or <code>null</code> if not used
     * @return the validation message
     * @since 4.2
     */
    protected String getValidationMsg(int theCode,
                                      Object[] theParams) {
        return RelationalPlugin.Util.getString("IndexReferenceOneTableRule.Index_references_columns_from_more_than_one_table"); //$NON-NLS-1$
    }

}
