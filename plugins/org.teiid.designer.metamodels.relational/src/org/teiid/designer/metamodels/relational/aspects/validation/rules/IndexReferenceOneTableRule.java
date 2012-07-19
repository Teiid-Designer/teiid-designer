/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation.rules;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ValidationPreferences;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ColumnSet;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.Table;


/**
 * ColumnIntegerDatatypeRule
 *
 * @since 8.0
 */
public class IndexReferenceOneTableRule implements ObjectValidationRule {
    
    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
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
