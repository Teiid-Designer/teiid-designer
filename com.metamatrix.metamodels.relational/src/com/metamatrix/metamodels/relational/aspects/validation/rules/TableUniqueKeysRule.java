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
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * TableUniqueKeysRule, unique keys (i.e., unique constraint or primary key) under the same group
 * object should not reference the same elements (independent of the order of the elements). 
 */
public class TableUniqueKeysRule implements ObjectValidationRule {

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(Table.class, eObject);

        Table table = (Table) eObject;

        Iterator columnIter = table.getColumns().iterator();

        ValidationResult result = new ValidationResultImpl(eObject);

        // compare unique keys are columns on this tables reference
        while(columnIter.hasNext()) {
            Column column = (Column) columnIter.next();
            List uniqueKeys = column.getUniqueKeys();
            if(uniqueKeys.size() > 1) {
                compareUniqueKeys(uniqueKeys, result);
                if(result.hasProblems()) {
                    context.addResult(result);
                    return;
                }                
            }
        }
    }

    /**
     * Compare uniquekeys and add problems to the validationResult, if keys reference same set of
     * columns.
     */
    private void compareUniqueKeys(List uniqueKeys, ValidationResult result) {
        for(int i=0; i < uniqueKeys.size(); i++) {
            UniqueKey key1 = (UniqueKey) uniqueKeys.get(i);
            for(int j=i+1; j < uniqueKeys.size(); j++) {
                UniqueKey key2 = (UniqueKey) uniqueKeys.get(j);
                List columns1 = key1.getColumns();
                List columns2 = key2.getColumns();
                if(compareColumns(columns1, columns2)) {
                    // create validation problem and add it to the result
                    final String msg = RelationalPlugin.Util.getString("TableUniqueKeysRule.UniqueKeys__1")+key1.getName()+RelationalPlugin.Util.getString("TableUniqueKeysRule.,__2")+key2.getName()+RelationalPlugin.Util.getString("TableUniqueKeysRule._reference_same_set_of_columns_on_the_table__3")+key1.getTable().getName(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,msg);
                    result.addProblem(problem);
                    return;
                }
            }
        }
    }

    /**
     * Compare the columns in the given collections, return true if the collections are the same
     * even though the order of columns in the collections are in differrent orders else return false. 
     * @return true if the collections have same columns else false
     */
    private boolean compareColumns(List columns1, List columns2) {
        if(columns1.size() == columns2.size() && columns1.size() > 0) {
            for(int i=0; i < columns1.size(); i++) {
                Column column1 = (Column) columns1.get(i);
                boolean foundMatch = false;
                for(int j=0; j < columns2.size(); j++) {
                    Column column2 = (Column) columns2.get(j);
                    if(column1.equals(column2)) {
                        foundMatch = true;
                        break;
                    }
                }
                // the columns in the first list is not found
                // in the second list    
                if(foundMatch == false) {
                    return false;    
                }
            }
            return true;            
        }
        return false;
    }

}
