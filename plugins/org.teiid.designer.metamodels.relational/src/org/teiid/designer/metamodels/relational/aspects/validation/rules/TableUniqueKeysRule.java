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
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueKey;


/**
 * TableUniqueKeysRule, unique keys (i.e., unique constraint or primary key) under the same group
 * object should not reference the same elements (independent of the order of the elements). 
 *
 * @since 8.0
 */
public class TableUniqueKeysRule implements ObjectValidationRule {

    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(Table.class, eObject);

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
