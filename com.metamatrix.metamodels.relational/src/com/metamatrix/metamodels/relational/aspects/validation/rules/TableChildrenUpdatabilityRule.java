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
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * TableChildrenUpdatabilityRule
 */
public class TableChildrenUpdatabilityRule implements ObjectValidationRule {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(Table.class, eObject);

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
