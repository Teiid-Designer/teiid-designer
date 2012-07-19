/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation.rules;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.Table;


/**
 * TableChildrenUpdatabilityRule
 *
 * @since 8.0
 */
public class TableChildrenUpdatabilityRule implements ObjectValidationRule {

    /* (non-Javadoc)
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(Table.class, eObject);

        Table table = (Table) eObject;

        // First, skip this rule if the table does NOT support update
        if(!table.isSupportsUpdate()) {
            // Don't care what the Columns are set to if the table does not support update
            return;    
        }

        // Rule: If a table/group supports updates then there should be a warning
        // if it does not contain any column that supports updates
        final List columns = table.getColumns();
        if ( !columns.isEmpty() ) {
            Iterator columnIter = columns.iterator();
            boolean hasUpdatableColumn = false;
            while(columnIter.hasNext()) {
                Column column = (Column) columnIter.next();
                if(column.isUpdateable()) {
    				hasUpdatableColumn = true;
                }
            }
    
    		if(!hasUpdatableColumn) {
    			// the table does not have even one updatable column
    			// create validation problem and add it to the result
    			ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.WARNING, getProblemMessage(table));
    			ValidationResult result = new ValidationResultImpl(eObject);
    			result.addProblem(problem);
    	        // add the problem result to the validation context
                context.addResult(result);
    		}
        }
    }

    /**
     * Construct a message indicating that the table does not have a single updatable column. 
     * @param tableObject <code>The EObject</code> for the table
     * @return The error message.
     */
    protected String getProblemMessage(Table tableObject) {
        return RelationalPlugin.Util.getString("TableChildrenUpdatabilityRule.The_table_{0},_is_updatable_but_does_not_have_any_updatable_columns._1", tableObject.getName()); //$NON-NLS-1$;
    }

}
